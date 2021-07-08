/*    */ package clus.activelearning.algoHMC;
/*    */ 
/*    */ import clus.Clus;
/*    */ import clus.activelearning.algo.ClusActiveLearningAlgorithmHMC;
/*    */ import clus.activelearning.indexing.LabelIndex;
/*    */ import clus.activelearning.indexing.TupleIndex;
/*    */ import clus.data.rows.RowData;
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
/*    */ public class RandomSamplingHMC
/*    */   extends ClusActiveLearningAlgorithmHMC
/*    */ {
/*    */   public RandomSamplingHMC(Clus clus, String name) throws ClusException, Exception {
/* 24 */     super(clus, name);
/*    */   }
/*    */ 
/*    */   
/*    */   public RowData sampleWithoutPartialLabelling(ClusRun clusRun) throws ClusException {
/* 29 */     LinkedList<TupleIndex> indexes = buildTupleRandomIndexer();
/* 30 */     return getLabeledDataset(indexes);
/*    */   }
/*    */ 
/*    */   
/*    */   public RowData samplePartialLabelling(ClusRun clusRun) throws ClusException {
/* 35 */     LinkedList<LabelIndex> indexes = buildLabelRandomIndexer();
/* 36 */     return getPartialLabelledDataset(indexes);
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\activelearning\algoHMC\RandomSamplingHMC.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */