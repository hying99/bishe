/*    */ package clus.activelearning.algoHMC;
/*    */ 
/*    */ import clus.Clus;
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
/*    */ public class UncertaintyVarianceHMC
/*    */   extends UncertaintySamplingHMC
/*    */ {
/*    */   public UncertaintyVarianceHMC(Clus clus, String name) throws ClusException, Exception {
/* 25 */     super(clus, name);
/*    */   }
/*    */ 
/*    */   
/*    */   public RowData samplePartialLabelling(ClusRun clusRun) throws ClusException {
/* 30 */     double[][] uncertainty = getLabelsEntropyUncertainty(clusRun);
/* 31 */     double[][] variance = getLabelsVariance(clusRun);
/* 32 */     double[][] uncvar = MatrixUtils.subtractMatrix(variance, uncertainty);
/* 33 */     LinkedList<LabelIndex> labelIndexer = buildLabelMaxIndexer(uncvar);
/* 34 */     return getPartialLabelledDataset(labelIndexer);
/*    */   }
/*    */ 
/*    */   
/*    */   public RowData sampleWithoutPartialLabelling(ClusRun clusRun) throws ClusException {
/* 39 */     double[] uncertainty = getTuplesEntropyUncertainty(clusRun);
/* 40 */     double[] variance = getTuplesVariance(clusRun);
/* 41 */     double[] uncVar = MatrixUtils.addVector(variance, uncertainty);
/* 42 */     LinkedList<TupleIndex> indexer = buildTupleMaxIndexer(uncVar);
/* 43 */     return getLabeledDataset(indexer);
/*    */   }
/*    */   
/*    */   protected double[] getTuplesVariance(ClusRun clusRun) {
/* 47 */     int nbRows = getUnlabeledData().getNbRows();
/* 48 */     double[] variance = new double[getUnlabeledData().getNbRows()];
/* 49 */     for (int i = 0; i < nbRows; i++) {
/* 50 */       ClusForest fr = (ClusForest)clusRun.getModel(1);
/* 51 */       double[] labelsVariance = fr.getVarianceAcrossForest(getUnlabeledData().getTuple(i));
/* 52 */       double summedVariance = MatrixUtils.sumVector(labelsVariance);
/* 53 */       variance[i] = summedVariance;
/*    */     } 
/* 55 */     return variance;
/*    */   }
/*    */   
/*    */   protected double[][] getLabelsVariance(ClusRun clusRun) {
/* 59 */     int nbRows = getUnlabeledData().getNbRows();
/* 60 */     double[][] variance = new double[getUnlabeledData().getNbRows()][];
/* 61 */     for (int i = 0; i < nbRows; i++) {
/* 62 */       ClusForest fr = (ClusForest)clusRun.getModel(1);
/* 63 */       variance[i] = fr.getVarianceAcrossForest(getUnlabeledData().getTuple(i));
/*    */     } 
/* 65 */     return variance;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\activelearning\algoHMC\UncertaintyVarianceHMC.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */