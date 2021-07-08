/*    */ package clus.activelearning.labelcorrelation;
/*    */ 
/*    */ import clus.activelearning.algo.ClusActiveLearningAlgorithm;
/*    */ import clus.activelearning.algo.ClusActiveLearningAlgorithmHSC;
/*    */ import clus.activelearning.algo.ClusLabelPairFindingAlgorithm;
/*    */ import clus.activelearning.indexing.LabelIndex;
/*    */ import clus.data.rows.RowData;
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
/*    */ public class HierarchyOrientedInfering
/*    */   extends ClusLabelPairFindingAlgorithm
/*    */ {
/*    */   boolean m_PositiveInfering;
/*    */   boolean m_NegativeInfering;
/*    */   
/*    */   public HierarchyOrientedInfering(ClusActiveLearningAlgorithm al, int batchSize, boolean positive, boolean negative) {
/* 33 */     super(al, batchSize);
/* 34 */     this.m_PositiveInfering = positive;
/* 35 */     this.m_NegativeInfering = negative;
/*    */   }
/*    */ 
/*    */   
/*    */   public LinkedList<LabelIndex> getLabelPairs(ClusRun cr) throws ClusException {
/* 40 */     return null;
/*    */   }
/*    */ 
/*    */   
/*    */   public LinkedList<LabelIndex> getLabelPairsHSC(ClusModel[] models) throws ClusException {
/* 45 */     ClusActiveLearningAlgorithmHSC al = (ClusActiveLearningAlgorithmHSC)getActiveLearningAlgorithm();
/*    */     
/* 47 */     RowData rowData = al.getUnlabeledData();
/* 48 */     double[][] variance = (double[][])null;
/* 49 */     double[][] uncertainty = (double[][])null;
/* 50 */     double[][] uncvar = (double[][])null;
/* 51 */     double[][] probs = (double[][])null;
/*    */     try {
/* 53 */       if (this.m_PositiveInfering && this.m_NegativeInfering) {
/* 54 */         uncertainty = al.getLabelUncertainty(al.getPredictionProbabilities(models, rowData));
/* 55 */         return buildHierarchyOrientedLabelMinIndexer(uncertainty);
/*    */       } 
/* 57 */       probs = al.getPredictionProbabilities(models, rowData);
/* 58 */       if (this.m_PositiveInfering) {
/* 59 */         return buildHierarchyOrientedLabelMaxIndexer(probs);
/*    */       }
/* 61 */       return buildHierarchyOrientedLabelMinIndexer(probs);
/*    */ 
/*    */ 
/*    */     
/*    */     }
/* 66 */     catch (IOException|ClassNotFoundException ex) {
/* 67 */       Logger.getLogger(VarianceInfering.class.getName()).log(Level.SEVERE, (String)null, ex);
/*    */       
/* 69 */       return null;
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\activelearning\labelcorrelation\HierarchyOrientedInfering.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */