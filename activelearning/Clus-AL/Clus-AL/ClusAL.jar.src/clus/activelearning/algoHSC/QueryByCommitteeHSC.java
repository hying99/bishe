/*     */ package clus.activelearning.algoHSC;
/*     */ 
/*     */ import clus.Clus;
/*     */ import clus.activelearning.algo.ClusActiveLearningAlgorithmHSC;
/*     */ import clus.activelearning.indexing.LabelIndex;
/*     */ import clus.activelearning.indexing.TupleIndex;
/*     */ import clus.activelearning.matrix.MatrixUtils;
/*     */ import clus.data.rows.DataTuple;
/*     */ import clus.data.rows.RowData;
/*     */ import clus.ext.ensembles.ClusForest;
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
/*     */ public class QueryByCommitteeHSC
/*     */   extends ClusActiveLearningAlgorithmHSC
/*     */ {
/*     */   public QueryByCommitteeHSC(Clus clus, String name) throws ClusException, Exception {
/*  31 */     super(clus, name);
/*     */   }
/*     */ 
/*     */   
/*     */   public RowData sampleLabelBasedHSC(ClusModel[] models) throws ClusException {
/*  36 */     double[][] variance = (double[][])null;
/*     */     try {
/*  38 */       variance = getVariance(models, getUnlabeledData());
/*  39 */     } catch (IOException|ClassNotFoundException ex) {
/*  40 */       Logger.getLogger(QueryByCommitteeHSC.class.getName()).log(Level.SEVERE, (String)null, ex);
/*     */     } 
/*     */     
/*  43 */     double[][] positive = new double[getUnlabeledData().getNbRows()][getHierarchy().getTotal()];
/*  44 */     double[][] negative = new double[getUnlabeledData().getNbRows()][getHierarchy().getTotal()];
/*     */     
/*  46 */     for (int i = 0; i < getUnlabeledData().getNbRows(); i++) {
/*  47 */       for (int j = 0; j < (getLabels()).length; j++) {
/*  48 */         if (getUnlabeledData().getTuple(i).getOracleAnswer().getPossibleLabels().contains(getLabels()[j])) {
/*  49 */           positive[i][j] = variance[i][j];
/*  50 */           negative[i][j] = -1.0D;
/*     */         } else {
/*  52 */           positive[i][j] = -1.0D;
/*  53 */           negative[i][j] = variance[i][j];
/*     */         } 
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/*  59 */     getIteration().setM_PositiveMeasure(positive);
/*  60 */     getIteration().setM_NegativeMeasure(negative);
/*     */ 
/*     */     
/*  63 */     LinkedList<LabelIndex> instances = buildLabelMaxIndexer(variance);
/*  64 */     return getPartialLabelledDataset(instances);
/*     */   }
/*     */ 
/*     */   
/*     */   public RowData sampleInstanceBasedHSC(ClusModel[] models) throws ClusException {
/*  69 */     double[][] variance = (double[][])null;
/*     */     try {
/*  71 */       variance = getVariance(models, getUnlabeledData());
/*  72 */     } catch (IOException|ClassNotFoundException ex) {
/*  73 */       Logger.getLogger(QueryByCommitteeHSC.class.getName()).log(Level.SEVERE, (String)null, ex);
/*     */     } 
/*  75 */     double[] varianceRow = MatrixUtils.sumRows(variance);
/*  76 */     LinkedList<TupleIndex> indexer = buildTupleMaxIndexer(varianceRow);
/*  77 */     return getLabeledDataset(indexer);
/*     */   }
/*     */ 
/*     */   
/*     */   public RowData sampleSemiPartialLabelling(ClusModel[] models) throws ClusException {
/*  82 */     double[][] variance = (double[][])null;
/*     */     try {
/*  84 */       variance = getVariance(models, getUnlabeledData());
/*  85 */     } catch (IOException|ClassNotFoundException ex) {
/*  86 */       Logger.getLogger(QueryByCommitteeHSC.class.getName()).log(Level.SEVERE, (String)null, ex);
/*     */     } 
/*  88 */     double[] varianceRow = MatrixUtils.sumRows(variance);
/*     */     
/*  90 */     RowData rowData = getTuplesByHighestMeasure(varianceRow);
/*  91 */     double[][] varianceLabels = getLabelsVariance(models, rowData);
/*  92 */     LinkedList<LabelIndex> instances = buildLabelMaxIndexerSemi(rowData, varianceLabels);
/*  93 */     return getPartialLabelledDataset(instances);
/*     */   }
/*     */ 
/*     */   
/*     */   private double[][] getLabelsVariance(ClusModel[] models, RowData data) {
/*  98 */     int nbRows = data.getNbRows();
/*  99 */     int nbModels = models.length;
/*     */     
/* 101 */     double[][] probs = new double[nbRows][nbModels];
/*     */     
/* 103 */     for (int i = 0; i < nbRows; i++) {
/* 104 */       DataTuple dataTuple = data.getTuple(i);
/* 105 */       for (int j = 0; j < nbModels; j++) {
/* 106 */         ClusForest forest = (ClusForest)models[j];
/* 107 */         probs[i][j] = forest.getVarianceAcrossForestHSC(dataTuple);
/*     */       } 
/*     */     } 
/* 110 */     return probs;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\activelearning\algoHSC\QueryByCommitteeHSC.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */