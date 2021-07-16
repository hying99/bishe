/*     */ package clus.activelearning.labelinfering;
/*     */ 
/*     */ import clus.Clus;
/*     */ import clus.activelearning.algo.ClusActiveLearningAlgorithm;
/*     */ import clus.activelearning.algo.ClusActiveLearningAlgorithmHSC;
/*     */ import clus.activelearning.algo.ClusLabelInferingAlgorithm;
/*     */ import clus.activelearning.algo.ClusLabelPairFindingAlgorithm;
/*     */ import clus.activelearning.data.OracleAnswer;
/*     */ import clus.activelearning.indexing.LabelIndex;
/*     */ import clus.activelearning.iteration.ClusLabelInferingIteration;
/*     */ import clus.algo.kNN.BasicDistance;
/*     */ import clus.algo.kNN.KNNClassifier;
/*     */ import clus.algo.kNN.KNNModel;
/*     */ import clus.algo.kNN.NominalBasicDistance;
/*     */ import clus.algo.kNN.NumericalBasicDistance;
/*     */ import clus.data.rows.DataTuple;
/*     */ import clus.data.rows.RowData;
/*     */ import clus.data.type.ClusAttrType;
/*     */ import clus.data.type.ClusSchema;
/*     */ import clus.ext.hierarchical.ClassHierarchy;
/*     */ import clus.ext.hierarchical.ClassesTuple;
/*     */ import clus.ext.hierarchical.WHTDStatistic;
/*     */ import clus.main.ClusRun;
/*     */ import clus.model.ClusModel;
/*     */ import clus.util.ClusException;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
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
/*     */ public class VSSMAL
/*     */   extends ClusLabelInferingAlgorithm
/*     */ {
/*     */   private double[][] m_Correlation;
/*     */   private Clus m_Clus;
/*     */   
/*     */   public VSSMAL(Clus clus, ClusLabelPairFindingAlgorithm lc, ClusActiveLearningAlgorithm al) {
/*  50 */     super(lc, al);
/*  51 */     this.m_Clus = clus;
/*     */   }
/*     */   
/*     */   public RowData infer(RowData unlabeledDataset, Clus clus, ClusRun clusRun) {
/*  55 */     ClusLabelPairFindingAlgorithm lc = getLabelCorrelation();
/*  56 */     LinkedList<LabelIndex> labels = null;
/*     */     
/*  58 */     HashMap<Integer, DataTuple> infered = new HashMap<>();
/*     */     try {
/*  60 */       labels = lc.getLabelPairs(clusRun);
/*  61 */     } catch (ClusException ex) {
/*  62 */       Logger.getLogger(SSMAL.class.getName()).log(Level.SEVERE, (String)null, (Throwable)ex);
/*     */     } 
/*  64 */     for (LabelIndex instance : labels) {
/*  65 */       DataTuple dataTuple = unlabeledDataset.getTupleByActiveIndex(instance.getIndex());
/*  66 */       infered.put(Integer.valueOf(instance.getIndex()), dataTuple);
/*     */       try {
/*  68 */         inferLabelsHMC(dataTuple, clus, clusRun, instance.getLabel());
/*  69 */       } catch (ClusException ex) {
/*  70 */         Logger.getLogger(SSMAL.class.getName()).log(Level.SEVERE, (String)null, (Throwable)ex);
/*     */       } 
/*     */     } 
/*  73 */     ArrayList<DataTuple> inferedDataset = new ArrayList<>(infered.values());
/*  74 */     return getInferedDataset(inferedDataset, unlabeledDataset);
/*     */   }
/*     */ 
/*     */   
/*     */   private void inferLabelsHMC(DataTuple dataTuple, Clus clus, ClusRun cr, String labelToBeInfered) throws ClusException {
/*  79 */     ClusActiveLearningAlgorithm al = getActiveLearning();
/*  80 */     LinkedList<String> path = (LinkedList<String>)al.getPaths().get(labelToBeInfered);
/*  81 */     ClassHierarchy h = al.getHierarchy();
/*     */     
/*  83 */     ClusLabelInferingIteration iteration = getIteration();
/*  84 */     int batchSize = getLabelCorrelation().getLabelInferingBatchSize();
/*  85 */     for (String pathLabel : path) {
/*  86 */       OracleAnswer oracleAnswer = dataTuple.getOracleAnswer();
/*  87 */       if (iteration.fitBatchSize(batchSize) && !oracleAnswer.queriedBefore(pathLabel)) {
/*  88 */         int labelIndex = getLabelIndex(pathLabel);
/*  89 */         boolean uncertaintyVote = getUncertaintyVote(dataTuple, cr, labelIndex);
/*  90 */         boolean knnVote = getKNNVote(labelIndex, pathLabel, dataTuple, clus, cr);
/*  91 */         boolean positiveOrNegativeAnswer = false;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 101 */         iteration.increaseSelectedAmount();
/* 102 */         if (positiveOrNegativeAnswer) {
/* 103 */           oracleAnswer.addNewPositiveLabel(pathLabel); continue;
/*     */         } 
/* 105 */         oracleAnswer.addNewNegativeLabel(pathLabel, h);
/*     */         return;
/*     */       } 
/*     */       return;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void inferLabelsHSC(DataTuple dataTuple, Clus clus, ClusRun cr, ClusModel[] models, String labelToBeInfered) throws ClusException, ClassNotFoundException, IOException {
/* 116 */     ClusActiveLearningAlgorithmHSC al = (ClusActiveLearningAlgorithmHSC)getActiveLearning();
/* 117 */     LinkedList<String> path = (LinkedList<String>)al.getPaths().get(labelToBeInfered);
/* 118 */     ClassHierarchy h = al.getHierarchy();
/*     */     
/* 120 */     ClusLabelInferingIteration iteration = getIteration();
/* 121 */     int batchSize = getLabelCorrelation().getLabelInferingBatchSize();
/* 122 */     double[] frequencyProbs = countFrequency((RowData)cr.getTrainingSet(), h);
/*     */     
/* 124 */     double[][] probs = al.getPredictionProbabilities(models, (RowData)cr.getTrainingSet());
/* 125 */     double[] threshold = getThresholdPerClass(frequencyProbs, probs);
/*     */     
/* 127 */     for (String pathLabel : path) {
/* 128 */       OracleAnswer oracleAnswer = dataTuple.getOracleAnswer();
/* 129 */       if (iteration.fitBatchSize(batchSize)) {
/* 130 */         if (!oracleAnswer.queriedBefore(pathLabel)) {
/* 131 */           int labelIndex = getLabelIndex(pathLabel);
/* 132 */           ArrayList<DataTuple> tuples = new ArrayList<>();
/* 133 */           tuples.add(dataTuple);
/* 134 */           RowData data = new RowData(tuples, clus.getSchema());
/* 135 */           double[][] probsTuple = al.getPredictionProbabilities(models, data);
/*     */           
/* 137 */           boolean uncertaintyVote = getUncertaintyVoteHSC(probsTuple, labelIndex, threshold);
/* 138 */           boolean knnVote = getKNNVote(labelIndex, pathLabel, dataTuple, clus, cr);
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
/* 149 */           iteration.increaseSelectedAmount();
/* 150 */           if (uncertaintyVote && knnVote) {
/* 151 */             oracleAnswer.addNewPositiveLabel(pathLabel);
/* 152 */             oracleAnswer.addPositiveInferedLabel(pathLabel); continue;
/*     */           } 
/* 154 */           oracleAnswer.addNewNegativeLabel(pathLabel, h);
/* 155 */           oracleAnswer.addNegativeInferedLabel(pathLabel, h);
/*     */           return;
/*     */         } 
/*     */         continue;
/*     */       } 
/*     */       return;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean votingCommittee(boolean uncertaintyVote, boolean correlationVote, boolean knnVote) {
/* 167 */     int positiveCounter = 0;
/* 168 */     int negativeCounter = 0;
/* 169 */     if (uncertaintyVote) {
/* 170 */       positiveCounter++;
/*     */     } else {
/* 172 */       negativeCounter++;
/*     */     } 
/* 174 */     if (correlationVote) {
/* 175 */       positiveCounter++;
/*     */     } else {
/* 177 */       negativeCounter++;
/*     */     } 
/* 179 */     if (knnVote) {
/* 180 */       positiveCounter++;
/* 181 */       negativeCounter++;
/*     */     } 
/* 183 */     return (positiveCounter > negativeCounter);
/*     */   }
/*     */ 
/*     */   
/*     */   private boolean getUncertaintyVote(DataTuple dataTuple, ClusRun cr, int labelIndex) {
/* 188 */     WHTDStatistic wthd = (WHTDStatistic)cr.getModel(1).predictWeighted(dataTuple);
/* 189 */     double[] probs = wthd.getNumericPred();
/* 190 */     return (probs[labelIndex] >= 0.5D);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean getUncertaintyVoteHSC(double[][] probs, int labelIndex, double[] thresholds) {
/* 196 */     return (probs[0][labelIndex] >= thresholds[labelIndex]);
/*     */   }
/*     */   
/*     */   private boolean getKNNVote(int labelIndex, String label, DataTuple dataTuple, Clus clus, ClusRun clusRun) {
/* 200 */     initializeDistances(clus.getSchema());
/* 201 */     KNNClassifier knn = new KNNClassifier(clus);
/* 202 */     KNNModel clusmodel = (KNNModel)knn.induceSingle(clusRun);
/* 203 */     DataTuple inferedTuple = clusmodel.infer(dataTuple, label);
/* 204 */     if (inferedTuple.getOracleAnswer() == null) {
/*     */       
/* 206 */       double[] probs = clusmodel.predictWeighted(dataTuple).getNumericPred();
/* 207 */       return (probs[labelIndex] == 1.0D);
/*     */     } 
/*     */     
/* 210 */     OracleAnswer inferedAnswer = inferedTuple.getOracleAnswer();
/* 211 */     return inferedAnswer.getPositiveAnswers().contains(label);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void initializeDistances(ClusSchema schema) {
/* 217 */     NominalBasicDistance nomDist = new NominalBasicDistance();
/* 218 */     NumericalBasicDistance numDist = new NumericalBasicDistance();
/* 219 */     ClusAttrType[] attrs = schema.getDescriptiveAttributes();
/* 220 */     for (int i = 0; i < attrs.length; i++) {
/* 221 */       if (attrs[i].getTypeIndex() == 0) {
/* 222 */         attrs[i].setBasicDistance((BasicDistance)nomDist);
/*     */       } else {
/* 224 */         attrs[i].setBasicDistance((BasicDistance)numDist);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public RowData inferHMCMode(ClusRun clusRun) {
/* 232 */     Clus clus = getM_Clus();
/* 233 */     RowData unlabeledDataset = getActiveLearning().getUnlabeledData();
/*     */     
/* 235 */     ClusLabelPairFindingAlgorithm lc = getLabelCorrelation();
/* 236 */     LinkedList<LabelIndex> labels = null;
/*     */     
/* 238 */     HashMap<Integer, DataTuple> infered = new HashMap<>();
/*     */     try {
/* 240 */       labels = lc.getLabelPairs(clusRun);
/* 241 */     } catch (ClusException ex) {
/* 242 */       Logger.getLogger(SSMAL.class.getName()).log(Level.SEVERE, (String)null, (Throwable)ex);
/*     */     } 
/* 244 */     for (LabelIndex instance : labels) {
/* 245 */       DataTuple dataTuple = unlabeledDataset.getTupleByActiveIndex(instance.getIndex());
/* 246 */       infered.put(Integer.valueOf(instance.getIndex()), dataTuple);
/*     */       try {
/* 248 */         inferLabelsHMC(dataTuple, clus, clusRun, instance.getLabel());
/* 249 */       } catch (ClusException ex) {
/* 250 */         Logger.getLogger(SSMAL.class.getName()).log(Level.SEVERE, (String)null, (Throwable)ex);
/*     */       } 
/*     */     } 
/* 253 */     ArrayList<DataTuple> inferedDataset = new ArrayList<>(infered.values());
/* 254 */     return getInferedDataset(inferedDataset, unlabeledDataset);
/*     */   }
/*     */ 
/*     */   
/*     */   public RowData inferHSCMode(ClusModel[] models, ClusRun clusRun) {
/* 259 */     Clus clus = getM_Clus();
/* 260 */     RowData unlabeledDataset = getActiveLearning().getUnlabeledData();
/*     */     
/* 262 */     ClusLabelPairFindingAlgorithm lc = getLabelCorrelation();
/* 263 */     LinkedList<LabelIndex> labels = null;
/*     */     
/* 265 */     HashMap<Integer, DataTuple> infered = new HashMap<>();
/*     */     
/*     */     try {
/* 268 */       labels = lc.getLabelPairsHSC(models);
/* 269 */     } catch (ClusException ex) {
/* 270 */       Logger.getLogger(SSMAL.class.getName()).log(Level.SEVERE, (String)null, (Throwable)ex);
/*     */     } 
/* 272 */     for (LabelIndex instance : labels) {
/* 273 */       DataTuple dataTuple = unlabeledDataset.getTupleByActiveIndex(instance.getIndex());
/* 274 */       infered.put(Integer.valueOf(instance.getIndex()), dataTuple);
/*     */       try {
/* 276 */         inferLabelsHSC(dataTuple, clus, clusRun, models, instance.getLabel());
/* 277 */       } catch (ClusException|ClassNotFoundException|IOException ex) {
/* 278 */         Logger.getLogger(SSMAL.class.getName()).log(Level.SEVERE, (String)null, ex);
/*     */       } 
/*     */     } 
/* 281 */     ArrayList<DataTuple> inferedDataset = new ArrayList<>(infered.values());
/* 282 */     return getInferedDataset(inferedDataset, unlabeledDataset);
/*     */   }
/*     */   
/*     */   public double[] countFrequency(RowData rowData, ClassHierarchy h) {
/* 286 */     int nbRows = rowData.getNbRows();
/* 287 */     double[] frequency = new double[h.getTotal()];
/*     */     
/*     */     int index;
/*     */     
/* 291 */     for (index = 0; index < nbRows; index++) {
/* 292 */       DataTuple dataTuple = rowData.getTuple(index);
/* 293 */       ClassesTuple ct = (ClassesTuple)dataTuple.getObjVal(0);
/* 294 */       boolean[] vectorBooleanNodeAndAncestors = ct.getVectorBooleanNodeAndAncestors(h);
/* 295 */       for (int j = 0; j < vectorBooleanNodeAndAncestors.length; j++) {
/* 296 */         if (vectorBooleanNodeAndAncestors[j]) {
/* 297 */           frequency[j] = frequency[j] + 1.0D;
/*     */         }
/*     */       } 
/*     */     } 
/* 301 */     for (index = 0; index < h.getTotal(); index++) {
/* 302 */       frequency[index] = frequency[index] / nbRows;
/*     */     }
/* 304 */     return frequency;
/*     */   }
/*     */   
/*     */   private double[] getThresholdPerClass(double[] frequency, double[][] probs) {
/* 308 */     double[] thresholds = new double[frequency.length];
/* 309 */     double[] columnProbs = new double[probs.length];
/* 310 */     for (int i = 0; i < frequency.length; i++) {
/* 311 */       for (int j = 0; j < probs.length; j++) {
/* 312 */         columnProbs[j] = probs[j][i];
/*     */       }
/*     */       
/* 315 */       Double index = Double.valueOf(Math.ceil(frequency[i] * columnProbs.length));
/* 316 */       Arrays.sort(columnProbs);
/* 317 */       thresholds[i] = columnProbs[index.intValue()];
/*     */     } 
/*     */     
/* 320 */     return thresholds;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Clus getM_Clus() {
/* 327 */     return this.m_Clus;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setM_Clus(Clus m_Clus) {
/* 334 */     this.m_Clus = m_Clus;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\activelearning\labelinfering\VSSMAL.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */