/*    */ package clus.activelearning.algoHMC;
/*    */ 
/*    */ import clus.Clus;
/*    */ import clus.activelearning.algo.ClusActiveLearningAlgorithmHMC;
/*    */ import clus.activelearning.indexing.LabelIndex;
/*    */ import clus.activelearning.indexing.TupleIndex;
/*    */ import clus.data.rows.RowData;
/*    */ import clus.ext.hierarchical.WHTDStatistic;
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
/*    */ public class UncertaintySamplingHMC
/*    */   extends ClusActiveLearningAlgorithmHMC
/*    */ {
/*    */   public UncertaintySamplingHMC(Clus clus, String name) throws ClusException, Exception {
/* 25 */     super(clus, name);
/*    */   }
/*    */ 
/*    */   
/*    */   public RowData samplePartialLabelling(ClusRun clusRun) throws ClusException {
/* 30 */     double[][] uncertainty = getLabelsEntropyUncertainty(clusRun);
/* 31 */     LinkedList<LabelIndex> labelIndexer = buildLabelMinIndexer(uncertainty);
/* 32 */     return getPartialLabelledDataset(labelIndexer);
/*    */   }
/*    */ 
/*    */   
/*    */   public RowData sampleWithoutPartialLabelling(ClusRun clusRun) throws ClusException {
/* 37 */     double[] uncertainty = getTuplesEntropyUncertainty(clusRun);
/* 38 */     LinkedList<TupleIndex> indexer = buildTupleMaxIndexer(uncertainty);
/* 39 */     return getLabeledDataset(indexer);
/*    */   }
/*    */   
/*    */   public double[] getTuplesEntropyUncertainty(ClusRun clusRun) {
/* 43 */     int nbRows = getUnlabeledData().getNbRows();
/* 44 */     double[] uncertainty = new double[getUnlabeledData().getNbRows()];
/* 45 */     for (int i = 0; i < nbRows; i++) {
/* 46 */       WHTDStatistic wthd = (WHTDStatistic)clusRun.getModel(1).predictWeighted(getUnlabeledData().getTuple(i));
/* 47 */       double[] probs = wthd.getNumericPred();
/* 48 */       double maxProb = 0.0D;
/* 49 */       for (int j = 0; j < probs.length; j++) {
/* 50 */         double logProbability = Math.log(probs[j]);
/* 51 */         if (probs[j] > maxProb) {
/* 52 */           maxProb = probs[j];
/*    */         }
/* 54 */         if (Double.isInfinite(logProbability)) {
/* 55 */           uncertainty[i] = uncertainty[i] + 0.0D;
/*    */         } else {
/* 57 */           uncertainty[i] = uncertainty[i] + probs[j] * logProbability;
/*    */         } 
/*    */       } 
/* 60 */       uncertainty[i] = maxProb - uncertainty[i];
/*    */     } 
/* 62 */     return uncertainty;
/*    */   }
/*    */   
/*    */   public double[][] getLabelsEntropyUncertainty(ClusRun clusRun) {
/* 66 */     int nbRows = getUnlabeledData().getNbRows();
/* 67 */     double[][] uncertainty = new double[getUnlabeledData().getNbRows()][];
/* 68 */     for (int i = 0; i < nbRows; i++) {
/* 69 */       WHTDStatistic wthd = (WHTDStatistic)clusRun.getModel(1).predictWeighted(getUnlabeledData().getTuple(i));
/* 70 */       double[] probs = wthd.getNumericPred();
/* 71 */       for (int j = 0; j < probs.length; j++) {
/* 72 */         probs[j] = Math.abs(0.5D - probs[j]);
/*    */       }
/* 74 */       uncertainty[i] = probs;
/*    */     } 
/* 76 */     return uncertainty;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\activelearning\algoHMC\UncertaintySamplingHMC.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */