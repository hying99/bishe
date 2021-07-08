/*    */ package clus.activelearning.algoHSC;
/*    */ 
/*    */ import clus.Clus;
/*    */ import clus.activelearning.algo.ClusActiveLearningAlgorithmHSC;
/*    */ import clus.activelearning.indexing.LabelIndex;
/*    */ import clus.activelearning.indexing.TupleIndex;
/*    */ import clus.data.rows.RowData;
/*    */ import clus.model.ClusModel;
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
/*    */ public class RandomSamplingHSC
/*    */   extends ClusActiveLearningAlgorithmHSC
/*    */ {
/*    */   public RandomSamplingHSC(Clus clus, String name) throws ClusException, Exception {
/* 24 */     super(clus, name);
/*    */   }
/*    */ 
/*    */   
/*    */   public RowData sampleInstanceBasedHSC(ClusModel[] models) throws ClusException {
/* 29 */     LinkedList<TupleIndex> indexes = buildTupleRandomIndexer();
/* 30 */     return getLabeledDataset(indexes);
/*    */   }
/*    */ 
/*    */   
/*    */   public RowData sampleLabelBasedHSC(ClusModel[] models) throws ClusException {
/* 35 */     LinkedList<LabelIndex> indexes = buildLabelRandomIndexer();
/* 36 */     return getPartialLabelledDataset(indexes);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public RowData sampleSemiPartialLabelling(ClusModel[] models) throws ClusException {
/* 42 */     throw new UnsupportedOperationException("Not supported yet.");
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\activelearning\algoHSC\RandomSamplingHSC.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */