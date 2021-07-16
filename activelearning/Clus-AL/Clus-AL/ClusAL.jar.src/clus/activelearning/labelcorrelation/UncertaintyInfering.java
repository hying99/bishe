/*    */ package clus.activelearning.labelcorrelation;
/*    */ 
/*    */ import clus.activelearning.algo.ClusActiveLearningAlgorithm;
/*    */ import clus.activelearning.algo.ClusActiveLearningAlgorithmHSC;
/*    */ import clus.activelearning.algo.ClusLabelPairFindingAlgorithm;
/*    */ import clus.activelearning.indexing.LabelIndex;
/*    */ import clus.data.rows.RowData;
/*    */ import clus.ext.hierarchical.WHTDStatistic;
/*    */ import clus.main.ClusRun;
/*    */ import clus.model.ClusModel;
/*    */ import clus.util.ClusException;
/*    */ import java.io.IOException;
/*    */ import java.util.LinkedList;
/*    */ import java.util.logging.Level;
/*    */ import java.util.logging.Logger;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class UncertaintyInfering
/*    */   extends ClusLabelPairFindingAlgorithm
/*    */ {
/*    */   public UncertaintyInfering(ClusActiveLearningAlgorithm al, int batchSize) {
/* 30 */     super(al, batchSize);
/*    */   }
/*    */ 
/*    */   
/*    */   public LinkedList<LabelIndex> getLabelPairs(ClusRun cr) throws ClusException {
/* 35 */     ClusActiveLearningAlgorithm al = getActiveLearningAlgorithm();
/* 36 */     RowData rowData = al.getUnlabeledData();
/* 37 */     int nbRows = rowData.getNbRows();
/* 38 */     double[][] uncertainty = new double[rowData.getNbRows()][];
/* 39 */     for (int i = 0; i < nbRows; i++) {
/* 40 */       WHTDStatistic wthd = (WHTDStatistic)cr.getModel(1).predictWeighted(rowData.getTuple(i));
/* 41 */       double[] probs = wthd.getNumericPred();
/* 42 */       for (int j = 0; j < probs.length; j++) {
/* 43 */         probs[j] = Math.abs(0.5D - probs[j]);
/*    */       }
/* 45 */       uncertainty[i] = probs;
/*    */     } 
/* 47 */     return buildLabelMinIndexer(uncertainty);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public LinkedList<LabelIndex> getLabelPairsHSC(ClusModel[] models) throws ClusException {
/* 55 */     ClusActiveLearningAlgorithmHSC al = (ClusActiveLearningAlgorithmHSC)getActiveLearningAlgorithm();
/* 56 */     RowData rowData = al.getUnlabeledData();
/* 57 */     double[][] uncertainty = (double[][])null;
/*    */     try {
/* 59 */       uncertainty = al.getLabelUncertainty(al.getPredictionProbabilities(models, rowData));
/* 60 */     } catch (IOException|ClassNotFoundException ex) {
/* 61 */       Logger.getLogger(UncertaintyInfering.class.getName()).log(Level.SEVERE, (String)null, ex);
/*    */     } 
/* 63 */     return buildLabelMinIndexer(uncertainty);
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\activelearning\labelcorrelation\UncertaintyInfering.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */