/*     */ package clus.activelearning.algoHSC;
/*     */ 
/*     */ import clus.Clus;
/*     */ import clus.activelearning.algo.ClusActiveLearningAlgorithm;
/*     */ import clus.activelearning.algo.ClusActiveLearningAlgorithmHSC;
/*     */ import clus.activelearning.indexing.TupleIndex;
/*     */ import clus.activelearning.matrix.MatrixUtils;
/*     */ import clus.algo.kNN.BasicDistance;
/*     */ import clus.algo.kNN.KNNClassifier;
/*     */ import clus.algo.kNN.KNNModel;
/*     */ import clus.algo.kNN.NominalBasicDistance;
/*     */ import clus.algo.kNN.NumericalBasicDistance;
/*     */ import clus.algo.optimizer.HCAL.HCAL;
/*     */ import clus.data.rows.DataTuple;
/*     */ import clus.data.rows.RowData;
/*     */ import clus.data.type.ClusAttrType;
/*     */ import clus.data.type.ClusSchema;
/*     */ import clus.ext.hierarchical.ClassHierarchy;
/*     */ import clus.ext.hierarchical.ClassTerm;
/*     */ import clus.ext.hierarchical.ClassesTuple;
/*     */ import clus.ext.hierarchical.ClassesValue;
/*     */ import clus.model.ClusModel;
/*     */ import clus.util.ClusException;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.LinkedList;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.Logger;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class UncertaintySamplingHCALHSC
/*     */   extends ClusActiveLearningAlgorithmHSC
/*     */ {
/*     */   private HashMap<Integer, Boolean[]> m_Leafs;
/*     */   private int m_MaxIterations;
/*     */   private Clus m_Clus;
/*     */   private int m_PopulationSize;
/*     */   private int m_NeighboursSize;
/*     */   
/*     */   public UncertaintySamplingHCALHSC(Clus clus, String name, int maxIterations, int populationSize) throws ClusException, IOException, ClassNotFoundException, Exception {
/*  52 */     super(clus, name);
/*  53 */     this.m_Clus = clus;
/*  54 */     this.m_PopulationSize = populationSize;
/*  55 */     initializeLeafs();
/*  56 */     this.m_MaxIterations = maxIterations;
/*  57 */     setNeighbours();
/*     */   }
/*     */ 
/*     */   
/*     */   private void initializeLeafs() throws ClusException {
/*  62 */     this.m_Leafs = (HashMap)new HashMap<>();
/*  63 */     ClassHierarchy h = getHierarchy();
/*  64 */     for (int i = 0; i < h.getTotal(); i++) {
/*  65 */       ClassTerm ct = h.getTermAt(i);
/*  66 */       Boolean[] ids = new Boolean[h.getTotal()];
/*  67 */       for (int j = 0; j < ids.length; ) { ids[j] = Boolean.valueOf(false); j++; }
/*  68 */        ct.getLeavesIds(ids, ct.getIndex());
/*  69 */       this.m_Leafs.put(Integer.valueOf(ct.getIndex()), ids);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public RowData sampleLabelBasedHSC(ClusModel[] models) throws ClusException {
/*  75 */     double[][] probs = (double[][])null;
/*  76 */     RowData unlabeledData = getUnlabeledData();
/*     */     
/*  78 */     double[][] cost = new double[unlabeledData.getNbRows()][getHierarchy().getTotal()];
/*  79 */     for (int i = 0; i < unlabeledData.getNbRows(); i++) {
/*  80 */       for (int k = 0; k < getHierarchy().getTotal(); k++) {
/*  81 */         cost[i][k] = getCost(getLabels()[k]);
/*     */       }
/*     */     } 
/*     */     try {
/*  85 */       probs = getPredictionProbabilities(models, unlabeledData);
/*  86 */     } catch (IOException|ClassNotFoundException ex) {
/*  87 */       Logger.getLogger(UncertaintySamplingHSC.class.getName()).log(Level.SEVERE, (String)null, ex);
/*     */     } 
/*  89 */     double[][] uncertainty = getLabelUncertaintyHCAL(probs);
/*  90 */     double[][] knnPredictions = new double[unlabeledData.getNbRows()][getHierarchy().getTotal()];
/*     */     try {
/*  92 */       for (int k = 0; k < unlabeledData.getNbRows(); k++) {
/*  93 */         knnPredictions[k] = findValidNeighbours(unlabeledData.getTuple(k), models);
/*     */       }
/*  95 */     } catch (IOException|ClassNotFoundException ex) {
/*  96 */       Logger.getLogger(UncertaintySamplingHCALHSC.class.getName()).log(Level.SEVERE, (String)null, ex);
/*     */     } 
/*     */     
/*  99 */     double[][] uncertaintyHCAL = new double[unlabeledData.getNbRows()][getHierarchy().getTotal()];
/* 100 */     for (int j = 0; j < uncertainty.length; j++) {
/* 101 */       for (int k = 0; k < (uncertainty[j]).length; k++) {
/* 102 */         if (knnPredictions[j][k] > 0.0D) {
/* 103 */           uncertaintyHCAL[j][k] = -(knnPredictions[j][k] * uncertainty[j][k] + uncertainty[j][k]);
/*     */         } else {
/* 105 */           uncertaintyHCAL[j][k] = -(knnPredictions[j][k] * sumDescendantsUncertainty(uncertainty[j], k) + uncertainty[j][k]);
/*     */         } 
/*     */       } 
/*     */     } 
/* 109 */     HCAL hcal = new HCAL((ClusActiveLearningAlgorithm)this, models, getPopulationSize(), getBudget(), getMaxIterations(), cost, uncertaintyHCAL);
/* 110 */     return getPartialLabelledDataset(hcal.optimize());
/*     */   }
/*     */   
/*     */   private double[] findValidNeighbours(DataTuple dataTuple, ClusModel[] models) throws ClusException, IOException, ClassNotFoundException {
/* 114 */     double[] votes = new double[getHierarchy().getTotal()];
/* 115 */     LinkedList<DataTuple> sortedNeighbours = dataTuple.getSortedNeighbours();
/* 116 */     for (int i = 0; i < sortedNeighbours.size(); i++) {
/* 117 */       DataTuple data = sortedNeighbours.get(i);
/*     */       
/* 119 */       if (data.getOracleAnswer() == null) {
/*     */         
/* 121 */         votes = MatrixUtils.addVector(votes, countNeighbours(data, getHierarchy()));
/* 122 */       } else if (data.getOracleAnswer().queriedBefore()) {
/*     */         
/* 124 */         ArrayList<DataTuple> dataTuples = new ArrayList<>();
/* 125 */         dataTuples.add(data);
/* 126 */         RowData rowData = new RowData(dataTuples, getLabeledData().getSchema());
/* 127 */         double[][] predictions = getPredictionProbabilities(models, rowData);
/* 128 */         votes = MatrixUtils.addVector(votes, countNeighboursPredicted(predictions[0], data));
/*     */       } 
/* 130 */       if (i > this.m_NeighboursSize) {
/*     */         break;
/*     */       }
/*     */     } 
/* 134 */     return votes;
/*     */   }
/*     */   
/*     */   public double[] countNeighboursPredicted(double[] predictionProbabilities, DataTuple data) {
/* 138 */     double[] votes = new double[getHierarchy().getTotal()];
/* 139 */     for (int i = 0; i < predictionProbabilities.length; i++) {
/* 140 */       String label = getLabels()[i];
/* 141 */       if (data.getOracleAnswer().queriedBefore(label)) {
/* 142 */         if (data.getOracleAnswer().queriedPositively(label)) {
/* 143 */           votes[i] = votes[i] + 1.0D;
/* 144 */         } else if (data.getOracleAnswer().queriedNegatively(label)) {
/* 145 */           votes[i] = votes[i] - 1.0D;
/*     */         } 
/*     */       }
/* 148 */       if (predictionProbabilities[i] > 0.0D) {
/* 149 */         votes[i] = votes[i] + 1.0D;
/*     */       } else {
/* 151 */         votes[i] = votes[i] - 1.0D;
/*     */       } 
/*     */     } 
/*     */     
/* 155 */     return votes;
/*     */   }
/*     */   
/*     */   public double[] countNeighbours(DataTuple tuple, ClassHierarchy h) {
/* 159 */     double[] votes = new double[h.getTotal()];
/* 160 */     int sidx = h.getType().getArrayIndex();
/* 161 */     ClassesTuple tp = (ClassesTuple)tuple.getObjVal(sidx);
/* 162 */     if (tp != null) {
/* 163 */       for (int j = 0; j < tp.getNbClasses(); j++) {
/* 164 */         ClassesValue val = tp.getClass(j);
/* 165 */         int idx = val.getIndex();
/* 166 */         votes[idx] = votes[idx] + 1.0D;
/*     */       } 
/*     */     }
/* 169 */     return votes;
/*     */   }
/*     */   
/*     */   private double sumDescendantsUncertainty(double[] uncertainty, int classIndex) {
/* 173 */     double sum = 0.0D;
/* 174 */     Boolean[] descendants = this.m_Leafs.get(Integer.valueOf(classIndex));
/*     */ 
/*     */     
/* 177 */     for (int j = 0; j < descendants.length; j++) {
/* 178 */       if (descendants[j].booleanValue()) {
/* 179 */         sum += uncertainty[j];
/*     */       }
/*     */     } 
/* 182 */     return sum;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public RowData sampleInstanceBasedHSC(ClusModel[] models) throws ClusException {
/* 188 */     double[][] probs = (double[][])null;
/*     */     try {
/* 190 */       probs = getPredictionProbabilities(models, getUnlabeledData());
/* 191 */     } catch (IOException|ClassNotFoundException ex) {
/* 192 */       Logger.getLogger(UncertaintySamplingHSC.class.getName()).log(Level.SEVERE, (String)null, ex);
/*     */     } 
/* 194 */     double[] uncertainty = getTuplesUncertainty(probs);
/* 195 */     LinkedList<TupleIndex> indexer = buildTupleMaxIndexer(uncertainty);
/* 196 */     return getLabeledDataset(indexer);
/*     */   }
/*     */ 
/*     */   
/*     */   public RowData sampleSemiPartialLabelling(ClusModel[] models) throws ClusException {
/* 201 */     throw new UnsupportedOperationException("Not supported yet.");
/*     */   }
/*     */   
/*     */   public double[] getTuplesUncertainty(double[][] predictionProbabilities) {
/* 205 */     int nbRows = predictionProbabilities.length;
/* 206 */     double[] uncertainty = new double[predictionProbabilities.length];
/* 207 */     for (int i = 0; i < nbRows; i++) {
/* 208 */       double[] probs = predictionProbabilities[i]; double[] arrayOfDouble1; int j; byte b;
/* 209 */       for (arrayOfDouble1 = probs, j = arrayOfDouble1.length, b = 0; b < j; ) { Double prob = Double.valueOf(arrayOfDouble1[b]);
/* 210 */         uncertainty[i] = uncertainty[i] + 1.0D / prob.doubleValue(); b++; }
/*     */       
/* 212 */       uncertainty[i] = -uncertainty[i];
/*     */     } 
/* 214 */     return uncertainty;
/*     */   }
/*     */ 
/*     */   
/*     */   private void setNeighbours() throws ClusException, IOException, ClassNotFoundException {
/* 219 */     ArrayList<DataTuple> labeled = getLabeledData().toArrayList();
/* 220 */     ArrayList<DataTuple> unlabeled = getUnlabeledData().toArrayList();
/* 221 */     labeled.addAll(unlabeled);
/* 222 */     RowData fullData = new RowData(labeled, getLabeledData().getSchema());
/* 223 */     initializeDistances(fullData.getSchema());
/* 224 */     KNNClassifier knn = new KNNClassifier(this.m_Clus);
/* 225 */     KNNModel clusmodel = (KNNModel)knn.induceSingleActiveLearning(fullData, this.m_Clus.getStatManager());
/* 226 */     this.m_NeighboursSize = labeled.size() / 2 + 1;
/*     */     
/* 228 */     for (int i = 0; i < getUnlabeledData().getNbRows(); i++) {
/* 229 */       DataTuple dataTuple = getUnlabeledData().getTuple(i);
/* 230 */       dataTuple.setSortedNeighbours(clusmodel.sortNeighboursByDistance(dataTuple));
/*     */     } 
/*     */   }
/*     */   
/*     */   private void initializeDistances(ClusSchema schema) {
/* 235 */     NominalBasicDistance nomDist = new NominalBasicDistance();
/* 236 */     NumericalBasicDistance numDist = new NumericalBasicDistance();
/* 237 */     ClusAttrType[] attrs = schema.getDescriptiveAttributes();
/* 238 */     for (ClusAttrType attr : attrs) {
/* 239 */       if (attr.getTypeIndex() == 0) {
/* 240 */         attr.setBasicDistance((BasicDistance)nomDist);
/*     */       } else {
/* 242 */         attr.setBasicDistance((BasicDistance)numDist);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HashMap<Integer, Boolean[]> getLeafs() {
/* 252 */     return this.m_Leafs;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getMaxIterations() {
/* 259 */     return this.m_MaxIterations;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setMaxIterations(int m_MaxIterations) {
/* 266 */     this.m_MaxIterations = m_MaxIterations;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Clus getClus() {
/* 273 */     return this.m_Clus;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setClus(Clus m_Clus) {
/* 280 */     this.m_Clus = m_Clus;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getPopulationSize() {
/* 287 */     return this.m_PopulationSize;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setPopulationSize(int m_PopulationSize) {
/* 294 */     this.m_PopulationSize = m_PopulationSize;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\activelearning\algoHSC\UncertaintySamplingHCALHSC.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */