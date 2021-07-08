/*    */ package clus.activelearning.algoHMC;
/*    */ 
/*    */ import clus.Clus;
/*    */ import clus.activelearning.algo.ClusActiveLearningAlgorithmHMC;
/*    */ import clus.activelearning.indexing.LabelIndex;
/*    */ import clus.activelearning.indexing.TupleIndex;
/*    */ import clus.activelearning.matrix.MatrixUtils;
/*    */ import clus.data.rows.RowData;
/*    */ import clus.ext.ensembles.ClusForest;
/*    */ import clus.main.ClusRun;
/*    */ import clus.util.ClusException;
/*    */ import java.util.LinkedList;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class QueryByCommitteeHMC
/*    */   extends ClusActiveLearningAlgorithmHMC
/*    */ {
/*    */   public QueryByCommitteeHMC(Clus clus, String name) throws ClusException, Exception {
/* 26 */     super(clus, name);
/*    */   }
/*    */ 
/*    */   
/*    */   public RowData sampleWithoutPartialLabelling(ClusRun clusRun) throws ClusException {
/* 31 */     double[] variance = getTuplesVariance(clusRun);
/* 32 */     LinkedList<TupleIndex> ti = buildTupleMaxIndexer(variance);
/* 33 */     return getLabeledDataset(ti);
/*    */   }
/*    */ 
/*    */   
/*    */   public RowData samplePartialLabelling(ClusRun clusRun) throws ClusException {
/* 38 */     double[][] variance = getLabelsVariance(clusRun);
/* 39 */     LinkedList<LabelIndex> labelIndexes = buildLabelMaxIndexer(variance);
/*    */     
/* 41 */     return getPartialLabelledDataset(labelIndexes);
/*    */   }
/*    */   
/*    */   private double[] getTuplesVariance(ClusRun clusRun) {
/* 45 */     int nbRows = getUnlabeledData().getNbRows();
/* 46 */     double[] variance = new double[getUnlabeledData().getNbRows()];
/* 47 */     for (int i = 0; i < nbRows; i++) {
/* 48 */       ClusForest fr = (ClusForest)clusRun.getModel(1);
/* 49 */       double[] labelsVariance = fr.getVarianceAcrossForest(getUnlabeledData().getTuple(i));
/* 50 */       double summedVariance = MatrixUtils.sumVector(labelsVariance);
/* 51 */       variance[i] = summedVariance;
/*    */     } 
/* 53 */     return variance;
/*    */   }
/*    */   
/*    */   private double[][] getLabelsVariance(ClusRun clusRun) {
/* 57 */     int nbRows = getUnlabeledData().getNbRows();
/* 58 */     double[][] variance = new double[getUnlabeledData().getNbRows()][];
/* 59 */     for (int i = 0; i < nbRows; i++) {
/* 60 */       ClusForest fr = (ClusForest)clusRun.getModel(1);
/* 61 */       variance[i] = fr.getVarianceAcrossForest(getUnlabeledData().getTuple(i));
/*    */     } 
/* 63 */     return variance;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\activelearning\algoHMC\QueryByCommitteeHMC.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */