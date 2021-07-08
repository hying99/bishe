/*     */ package clus.activelearning.algoHSC;
/*     */ 
/*     */ import clus.Clus;
/*     */ import clus.activelearning.indexing.LabelIndex;
/*     */ import clus.activelearning.indexing.TupleIndex;
/*     */ import clus.activelearning.indexing.TupleIndexer;
/*     */ import clus.activelearning.matrix.MatrixUtils;
/*     */ import clus.algo.kNN.BasicDistance;
/*     */ import clus.algo.kNN.EuclidianDistance;
/*     */ import clus.algo.kNN.KNNStatistics;
/*     */ import clus.algo.kNN.NominalBasicDistance;
/*     */ import clus.algo.kNN.NumericalBasicDistance;
/*     */ import clus.data.rows.DataTuple;
/*     */ import clus.data.rows.RowData;
/*     */ import clus.data.type.ClusAttrType;
/*     */ import clus.data.type.ClusSchema;
/*     */ import clus.model.ClusModel;
/*     */ import clus.util.ClusException;
/*     */ import java.io.IOException;
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
/*     */ public class BatchRankHSC
/*     */   extends UncertaintyVarianceCostHSC
/*     */ {
/*     */   protected double[][] m_Distances;
/*     */   protected double m_Alpha;
/*     */   protected double m_Beta;
/*     */   protected int m_MaxIteration;
/*     */   protected double m_Sigma;
/*     */   
/*     */   public BatchRankHSC(Clus clus, String name, double alpha, double beta, double sigma, int maxIterations) throws ClusException, Exception {
/*  43 */     super(clus, name, alpha, beta);
/*  44 */     this.m_Alpha = alpha;
/*  45 */     this.m_Beta = beta;
/*  46 */     this.m_Sigma = sigma;
/*  47 */     this.m_MaxIteration = maxIterations;
/*  48 */     initializeDistances();
/*     */   }
/*     */   
/*     */   private void initializeDistances() {
/*  52 */     initializeDistances(getUnlabeledData().getSchema());
/*     */     
/*  54 */     calcDistance();
/*     */   }
/*     */   
/*     */   protected void initializeDistances(ClusSchema schema) {
/*  58 */     NominalBasicDistance nomDist = new NominalBasicDistance();
/*  59 */     NumericalBasicDistance numDist = new NumericalBasicDistance();
/*  60 */     ClusAttrType[] attrs = schema.getDescriptiveAttributes();
/*  61 */     for (ClusAttrType attr : attrs) {
/*  62 */       if (attr.getTypeIndex() == 0) {
/*  63 */         attr.setBasicDistance((BasicDistance)nomDist);
/*     */       } else {
/*  65 */         attr.setBasicDistance((BasicDistance)numDist);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void calcDistance() {
/*  72 */     RowData rowData = getUnlabeledData();
/*  73 */     int nbRows = rowData.getNbRows();
/*  74 */     this.m_Distances = new double[nbRows][nbRows];
/*  75 */     KNNStatistics stats = new KNNStatistics(rowData);
/*     */     
/*  77 */     ClusAttrType[] attrs = rowData.getSchema().getDescriptiveAttributes();
/*     */     
/*  79 */     double[] weights = new double[attrs.length];
/*  80 */     for (int j = 0; j < weights.length; j++) {
/*  81 */       weights[j] = 1.0D;
/*     */     }
/*  83 */     EuclidianDistance euclidianDistance = new EuclidianDistance(attrs, weights);
/*  84 */     for (int i = 0; i < nbRows; i++) {
/*  85 */       DataTuple curTup = rowData.getTuple(i);
/*  86 */       for (int k = 0; k < i; k++) {
/*  87 */         DataTuple tuple = rowData.getTuple(k);
/*  88 */         double dist = euclidianDistance.getDistance(tuple, curTup);
/*  89 */         this.m_Distances[i][k] = gaussianKernelDistance(dist);
/*  90 */         this.m_Distances[k][i] = gaussianKernelDistance(dist);
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private void printIndex(int[] solution) {
/*  96 */     for (int i = 0; i < solution.length; i++) {
/*  97 */       if (solution[i] == 1) {
/*  98 */         System.out.println(i);
/*     */       }
/*     */     } 
/* 101 */     System.out.println("-----------");
/*     */   }
/*     */   
/*     */   protected double gaussianKernelDistance(double distance) {
/* 105 */     return Math.exp(-distance / 2.0D * Math.pow(this.m_Sigma, 2.0D));
/*     */   }
/*     */ 
/*     */   
/*     */   public RowData sampleLabelBasedHSC(ClusModel[] models) throws ClusException {
/* 110 */     throw new UnsupportedOperationException("BatchRank Algorithm does not support PartialLabelling, only Semi");
/*     */   }
/*     */ 
/*     */   
/*     */   public RowData sampleInstanceBasedHSC(ClusModel[] models) throws ClusException {
/* 115 */     LinkedList<TupleIndex> ti = null;
/*     */     try {
/* 117 */       ti = optimize(models);
/* 118 */     } catch (IOException|ClassNotFoundException ex) {
/* 119 */       Logger.getLogger(BatchRankHSC.class.getName()).log(Level.SEVERE, (String)null, ex);
/*     */     } 
/* 121 */     return getLabeledDataset(ti);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public RowData sampleSemiPartialLabelling(ClusModel[] models) throws ClusException {
/* 128 */     LinkedList<LabelIndex> labelIndexer = null;
/*     */     try {
/* 130 */       LinkedList<TupleIndex> ti = optimize(models);
/* 131 */       RowData rowData = getTuplesByIndexer(ti);
/* 132 */       double[][] variance = getVariance(models, rowData);
/* 133 */       double[][] probs = getPredictionProbabilities(models, rowData);
/* 134 */       double[][] uncertainty = getLabelUncertainty(probs);
/* 135 */       double[][] labelFrequencyCost = getLabelFrequencyCost(rowData);
/* 136 */       double[][] uncVarSemi = MatrixUtils.subtractMatrix(variance, uncertainty);
/* 137 */       uncVarSemi = MatrixUtils.addMatrix(uncVarSemi, labelFrequencyCost);
/* 138 */       labelIndexer = buildLabelMaxIndexerSemi(rowData, uncVarSemi);
/* 139 */     } catch (IOException|ClassNotFoundException ex) {
/* 140 */       Logger.getLogger(BatchRankHSC.class.getName()).log(Level.SEVERE, (String)null, ex);
/*     */     } 
/* 142 */     return getPartialLabelledDataset(labelIndexer);
/*     */   }
/*     */ 
/*     */   
/*     */   protected LinkedList<TupleIndex> optimize(ClusModel[] models) throws ClusException, IOException, ClassNotFoundException {
/* 147 */     double[][] matrix = getMatrix(models);
/* 148 */     double[] summedColumns = MatrixUtils.sumColumns(matrix);
/* 149 */     LinkedList<TupleIndex> tuples = getTupleIndexerByHighestMeasure(summedColumns);
/* 150 */     int[] solution = getSolution(tuples);
/* 151 */     int iteration = 0;
/* 152 */     while (iteration < this.m_MaxIteration) {
/* 153 */       summedColumns = MatrixUtils.multiplyMatrixVector(matrix, solution);
/* 154 */       tuples = getTupleIndexerByHighestMeasure(summedColumns);
/* 155 */       solution = getSolution(tuples);
/* 156 */       iteration++;
/*     */     } 
/* 158 */     tuples = createTupleIndex(solution);
/* 159 */     return tuples;
/*     */   }
/*     */   
/*     */   LinkedList<TupleIndex> createTupleIndex(int[] solution) {
/* 163 */     TupleIndexer ti = new TupleIndexer(getBatchsize());
/* 164 */     for (int i = 0; i < solution.length; i++) {
/* 165 */       if (solution[i] == 1) {
/* 166 */         ti.add((getUnlabeledData().getTuple(i)).m_ActiveIndex);
/*     */       }
/*     */     } 
/* 169 */     return ti.getIndexer();
/*     */   }
/*     */   
/*     */   private int[] getSolution(LinkedList<TupleIndex> ti) {
/* 173 */     int nbRows = getUnlabeledData().getNbRows();
/* 174 */     int[] highest = new int[nbRows];
/* 175 */     for (int i = 0; i < ti.size(); i++) {
/* 176 */       highest[((TupleIndex)ti.get(i)).getIndex()] = 1;
/*     */     }
/* 178 */     return highest;
/*     */   }
/*     */   
/*     */   private double[][] getMatrix(ClusModel[] models) throws ClusException, IOException, ClassNotFoundException {
/* 182 */     RowData rowData = getUnlabeledData();
/* 183 */     double[][] predictions = getPredictionProbabilities(models, rowData);
/* 184 */     double[] uncertainty = getTuplesEntropyUncertainty(predictions);
/* 185 */     double[][] var = getVariance(models, rowData);
/* 186 */     double[] varRow = MatrixUtils.sumRows(var);
/*     */     
/* 188 */     double[][] distances = getDistances(uncertainty, varRow);
/* 189 */     return distances;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private double[][] getDistances(double[] uncertainty, double[] variance) {
/* 196 */     RowData rowData = getUnlabeledData();
/* 197 */     int nbRows = rowData.getNbRows();
/* 198 */     double[][] distances = new double[nbRows][nbRows];
/* 199 */     for (int i = 0; i < nbRows; i++) {
/* 200 */       DataTuple dataTuple = rowData.getTuple(i);
/* 201 */       for (int j = 0, k = 0; j < (this.m_Distances[dataTuple.m_ActiveIndex - 1]).length; j++) {
/* 202 */         if (!getRemovedIndex()[j]) {
/* 203 */           distances[i][k] = this.m_Distances[dataTuple.m_ActiveIndex - 1][j];
/* 204 */           k++;
/*     */         } 
/*     */       } 
/* 207 */       distances[i][i] = this.m_Alpha * uncertainty[i] + this.m_Beta * variance[i];
/*     */     } 
/* 209 */     return distances;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\activelearning\algoHSC\BatchRankHSC.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */