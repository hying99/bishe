/*    */ package clus.activelearning.labelcorrelation;
/*    */ 
/*    */ import clus.activelearning.algo.ClusActiveLearningAlgorithm;
/*    */ import clus.activelearning.algo.ClusActiveLearningAlgorithmHSC;
/*    */ import clus.activelearning.algo.ClusLabelPairFindingAlgorithm;
/*    */ import clus.activelearning.indexing.LabelIndex;
/*    */ import clus.data.rows.RowData;
/*    */ import clus.ext.ensembles.ClusForest;
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
/*    */ public class VarianceInfering
/*    */   extends ClusLabelPairFindingAlgorithm
/*    */ {
/*    */   public VarianceInfering(ClusActiveLearningAlgorithm al, int batchSize) {
/* 31 */     super(al, batchSize);
/*    */   }
/*    */ 
/*    */   
/*    */   public LinkedList<LabelIndex> getLabelPairs(ClusRun cr) throws ClusException {
/* 36 */     ClusActiveLearningAlgorithm al = getActiveLearningAlgorithm();
/* 37 */     RowData rowData = al.getUnlabeledData();
/* 38 */     int nbRows = rowData.getNbRows();
/* 39 */     double[][] variance = new double[rowData.getNbRows()][];
/* 40 */     ClusForest forest = (ClusForest)cr.getModel(1);
/* 41 */     for (int i = 0; i < nbRows; i++) {
/* 42 */       double[] probs = forest.getVarianceAcrossForest(rowData.getTuple(i));
/* 43 */       variance[i] = probs;
/*    */     } 
/* 45 */     return buildLabelMinIndexer(variance);
/*    */   }
/*    */ 
/*    */   
/*    */   public LinkedList<LabelIndex> getLabelPairsHSC(ClusModel[] models) throws ClusException {
/* 50 */     ClusActiveLearningAlgorithmHSC al = (ClusActiveLearningAlgorithmHSC)getActiveLearningAlgorithm();
/* 51 */     RowData rowData = al.getUnlabeledData();
/* 52 */     double[][] variance = (double[][])null;
/*    */     try {
/* 54 */       variance = al.getVariance(models, rowData);
/*    */     }
/* 56 */     catch (IOException|ClassNotFoundException ex) {
/* 57 */       Logger.getLogger(VarianceInfering.class.getName()).log(Level.SEVERE, (String)null, ex);
/*    */     } 
/* 59 */     return buildLabelMinIndexer(variance);
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\activelearning\labelcorrelation\VarianceInfering.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */