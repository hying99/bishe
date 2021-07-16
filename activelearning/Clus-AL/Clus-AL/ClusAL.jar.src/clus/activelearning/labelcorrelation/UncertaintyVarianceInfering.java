/*    */ package clus.activelearning.labelcorrelation;
/*    */ 
/*    */ import clus.activelearning.algo.ClusActiveLearningAlgorithm;
/*    */ import clus.activelearning.algoHSC.UncertaintySamplingHSC;
/*    */ import clus.activelearning.indexing.LabelIndex;
/*    */ import clus.activelearning.matrix.MatrixUtils;
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
/*    */ 
/*    */ public class UncertaintyVarianceInfering
/*    */   extends UncertaintyInfering
/*    */ {
/*    */   public UncertaintyVarianceInfering(ClusActiveLearningAlgorithm al, int batchSize) {
/* 31 */     super(al, batchSize);
/*    */   }
/*    */ 
/*    */   
/*    */   public LinkedList<LabelIndex> getLabelPairs(ClusRun cr) throws ClusException {
/* 36 */     ClusActiveLearningAlgorithm al = getActiveLearningAlgorithm();
/* 37 */     RowData rowData = al.getUnlabeledData();
/* 38 */     int nbRows = rowData.getNbRows();
/* 39 */     double[][] uncertainty = new double[rowData.getNbRows()][];
/* 40 */     System.out.println("THIS IS NOT FINISHED, FINISH THIS LATER");
/* 41 */     for (int i = 0; i < nbRows; i++) {
/* 42 */       WHTDStatistic wthd = (WHTDStatistic)cr.getModel(1).predictWeighted(rowData.getTuple(i));
/* 43 */       double[] probs = wthd.getNumericPred();
/* 44 */       for (int j = 0; j < probs.length; j++) {
/* 45 */         probs[j] = Math.abs(0.5D - probs[j]);
/*    */       }
/* 47 */       uncertainty[i] = probs;
/*    */     } 
/* 49 */     return al.buildLabelMinIndexer(uncertainty);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public LinkedList<LabelIndex> getLabelPairsHSC(ClusModel[] models) throws ClusException {
/* 55 */     UncertaintySamplingHSC al = (UncertaintySamplingHSC)getActiveLearningAlgorithm();
/* 56 */     RowData rowData = al.getUnlabeledData();
/* 57 */     double[][] uncertainty = (double[][])null;
/*    */     try {
/* 59 */       uncertainty = al.getLabelUncertainty(al.getPredictionProbabilities(models, rowData));
/* 60 */       double[][] variance = (double[][])null;
/* 61 */       variance = al.getVariance(models, rowData);
/* 62 */       uncertainty = MatrixUtils.subtractMatrix(uncertainty, variance);
/* 63 */     } catch (IOException|ClassNotFoundException ex) {
/* 64 */       Logger.getLogger(VarianceInfering.class.getName()).log(Level.SEVERE, (String)null, ex);
/*    */     } 
/* 66 */     return buildLabelMinIndexer(uncertainty);
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\activelearning\labelcorrelation\UncertaintyVarianceInfering.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */