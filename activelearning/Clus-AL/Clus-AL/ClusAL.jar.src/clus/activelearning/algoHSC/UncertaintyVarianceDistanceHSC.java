/*     */ package clus.activelearning.algoHSC;
/*     */ 
/*     */ import clus.Clus;
/*     */ import clus.activelearning.indexing.LabelIndex;
/*     */ import clus.activelearning.indexing.TupleIndex;
/*     */ import clus.activelearning.matrix.MatrixUtils;
/*     */ import clus.algo.kNN.EuclidianDistance;
/*     */ import clus.algo.kNN.KNNStatistics;
/*     */ import clus.data.rows.DataTuple;
/*     */ import clus.data.rows.RowData;
/*     */ import clus.data.type.ClusAttrType;
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
/*     */ public class UncertaintyVarianceDistanceHSC
/*     */   extends BatchRankHSC
/*     */ {
/*     */   public UncertaintyVarianceDistanceHSC(Clus clus, String name, double alpha, double beta, double sigma, int maxIterations) throws ClusException, Exception, Exception {
/*  32 */     super(clus, name, alpha, beta, sigma, maxIterations);
/*     */   }
/*     */ 
/*     */   
/*     */   public RowData sampleLabelBasedHSC(ClusModel[] models) throws ClusException {
/*  37 */     double[][] probs = (double[][])null;
/*  38 */     double[][] variance = (double[][])null;
/*  39 */     LinkedList<LabelIndex> labelIndexer = null;
/*  40 */     RowData rowData = getUnlabeledData();
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
/*     */     try {
/*  54 */       probs = getPredictionProbabilities(models, rowData);
/*  55 */       double[][] uncertainty = getLabelUncertainty(probs);
/*  56 */       double[] distances = getMeanDistance();
/*  57 */       variance = getVariance(models, rowData);
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*  62 */       uncertainty = MatrixUtils.subtractMatrixVectorElementWise(uncertainty, distances);
/*  63 */       uncertainty = MatrixUtils.subtractMatrix(uncertainty, variance);
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
/*  74 */       labelIndexer = buildLabelMinIndexer(uncertainty);
/*  75 */     } catch (IOException|ClassNotFoundException ex) {
/*  76 */       Logger.getLogger(UncertaintySamplingHSC.class.getName()).log(Level.SEVERE, (String)null, ex);
/*     */     } 
/*  78 */     return getPartialLabelledDataset(labelIndexer);
/*     */   }
/*     */ 
/*     */   
/*     */   public RowData sampleInstanceBasedHSC(ClusModel[] models) throws ClusException {
/*  83 */     LinkedList<TupleIndex> ti = null;
/*  84 */     double[][] probs = (double[][])null;
/*  85 */     double[][] variance = (double[][])null;
/*     */     try {
/*  87 */       probs = getPredictionProbabilities(models, getUnlabeledData());
/*  88 */       variance = getVariance(models, getUnlabeledData());
/*     */     }
/*  90 */     catch (IOException|ClassNotFoundException ex) {
/*  91 */       Logger.getLogger(UncertaintyVarianceHSC.class.getName()).log(Level.SEVERE, (String)null, ex);
/*     */     } 
/*  93 */     double[] entropyUncertainty = getTuplesEntropyUncertainty(probs);
/*     */     
/*  95 */     double[] variancePerRow = MatrixUtils.sumRows(variance);
/*     */     
/*  97 */     double[] uncertaintyVariance = MatrixUtils.addVector(entropyUncertainty, variancePerRow);
/*  98 */     double[] distances = getMeanDistance();
/*  99 */     distances = MatrixUtils.addVector(uncertaintyVariance, distances);
/*     */     
/* 101 */     ti = buildTupleMaxIndexer(distances);
/* 102 */     return getLabeledDataset(ti);
/*     */   }
/*     */ 
/*     */   
/*     */   public RowData sampleSemiPartialLabelling(ClusModel[] models) throws ClusException {
/* 107 */     double[][] probs = (double[][])null;
/* 108 */     double[][] variance = (double[][])null;
/* 109 */     LinkedList<LabelIndex> labelIndexer = null;
/*     */     try {
/* 111 */       probs = getPredictionProbabilities(models, getUnlabeledData());
/* 112 */       variance = getVariance(models, getUnlabeledData());
/* 113 */       double[] entropyUncertainty = getTuplesEntropyUncertainty(probs);
/* 114 */       double[] variancePerRow = MatrixUtils.sumRows(variance);
/* 115 */       double[] uncertaintyVariance = MatrixUtils.addVector(entropyUncertainty, variancePerRow);
/* 116 */       double[] weights = getWeights();
/* 117 */       uncertaintyVariance = MatrixUtils.multiplyVector(uncertaintyVariance, weights);
/* 118 */       double[] distances = getMeanDistance();
/* 119 */       distances = MatrixUtils.addVector(uncertaintyVariance, distances);
/*     */       
/* 121 */       RowData rowData = getTuplesByHighestMeasure(distances);
/* 122 */       variance = getVariance(models, rowData);
/* 123 */       probs = getPredictionProbabilities(models, rowData);
/* 124 */       double[][] uncertainty = getLabelUncertainty(probs);
/* 125 */       double[][] labelFrequencyCost = getLabelFrequencyCost(rowData);
/* 126 */       double[][] uncVarSemi = MatrixUtils.subtractMatrix(variance, uncertainty);
/*     */ 
/*     */ 
/*     */       
/* 130 */       uncVarSemi = MatrixUtils.addMatrix(uncVarSemi, labelFrequencyCost);
/*     */ 
/*     */       
/* 133 */       labelIndexer = buildLabelMaxIndexerSemi(rowData, uncVarSemi);
/*     */     }
/* 135 */     catch (IOException|ClassNotFoundException ex) {
/* 136 */       Logger.getLogger(UncertaintySamplingHSC.class.getName()).log(Level.SEVERE, (String)null, ex);
/*     */     } 
/* 138 */     return getPartialLabelledDataset(labelIndexer);
/*     */   }
/*     */ 
/*     */   
/*     */   private double[] getMeanDistance() {
/* 143 */     RowData rowData = getUnlabeledData();
/* 144 */     int nbRows = rowData.getNbRows();
/* 145 */     RowData labeled = getLabeledData();
/*     */     
/* 147 */     double[] distancesRows = new double[nbRows];
/* 148 */     for (int i = 0; i < nbRows; i++) {
/* 149 */       DataTuple dataTuple = rowData.getTuple(i);
/* 150 */       double distance = 0.0D;
/* 151 */       int rows = 1;
/* 152 */       for (int j = 0; j < (this.m_Distances[dataTuple.m_ActiveIndex - 1]).length; j++) {
/* 153 */         if (i != j && !getRemovedIndex()[j] && !rowData.getTuple(j).getOracleAnswer().queriedBefore()) {
/*     */ 
/*     */           
/* 156 */           distance += this.m_Distances[dataTuple.m_ActiveIndex - 1][j];
/* 157 */           rows++;
/*     */         } 
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 169 */       distancesRows[i] = distance / rows;
/*     */     } 
/*     */     
/* 172 */     return distancesRows;
/*     */   }
/*     */   
/*     */   private double[][] calcDistance() {
/* 176 */     RowData labeled = getLabeledData();
/* 177 */     RowData unlabeled = getUnlabeledData();
/* 178 */     initializeDistances(labeled.getSchema());
/* 179 */     initializeDistances(unlabeled.getSchema());
/*     */ 
/*     */     
/* 182 */     int nbRowsLabeled = labeled.getNbRows();
/* 183 */     int nbRowsUnlabeled = unlabeled.getNbRows();
/*     */     
/* 185 */     double[][] distance = new double[nbRowsLabeled + nbRowsUnlabeled][nbRowsLabeled + nbRowsUnlabeled];
/* 186 */     KNNStatistics stats = new KNNStatistics(labeled);
/*     */     
/* 188 */     ClusAttrType[] attrs = labeled.getSchema().getDescriptiveAttributes();
/*     */ 
/*     */     
/* 191 */     double[] weights = new double[attrs.length];
/* 192 */     for (int j = 0; j < weights.length; j++) {
/* 193 */       weights[j] = 1.0D;
/*     */     }
/* 195 */     EuclidianDistance euclidianDistance = new EuclidianDistance(attrs, weights); int i;
/* 196 */     for (i = 0; i < nbRowsLabeled; i++) {
/* 197 */       DataTuple curTup = labeled.getTuple(i);
/* 198 */       for (int k = 0; k < i; k++) {
/* 199 */         DataTuple tuple = labeled.getTuple(k);
/* 200 */         double dist = euclidianDistance.getDistance(tuple, curTup);
/* 201 */         distance[i][k] = gaussianKernelDistance(dist);
/* 202 */         distance[k][i] = gaussianKernelDistance(dist);
/*     */       } 
/*     */     } 
/* 205 */     for (i = 0; i < nbRowsLabeled + nbRowsUnlabeled; i++) {
/* 206 */       DataTuple curTup = unlabeled.getTuple(i);
/* 207 */       for (int k = i; k < i; k++) {
/* 208 */         DataTuple tuple = unlabeled.getTuple(k);
/* 209 */         double dist = euclidianDistance.getDistance(tuple, curTup);
/* 210 */         distance[i][k] = gaussianKernelDistance(dist);
/* 211 */         distance[k][i] = gaussianKernelDistance(dist);
/*     */       } 
/*     */     } 
/* 214 */     return distance;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\activelearning\algoHSC\UncertaintyVarianceDistanceHSC.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */