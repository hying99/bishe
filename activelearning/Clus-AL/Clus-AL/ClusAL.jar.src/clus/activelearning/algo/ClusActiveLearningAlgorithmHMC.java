/*    */ package clus.activelearning.algo;
/*    */ 
/*    */ import clus.Clus;
/*    */ import clus.data.rows.RowData;
/*    */ import clus.main.ClusRun;
/*    */ import clus.util.ClusException;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class ClusActiveLearningAlgorithmHMC
/*    */   extends ClusActiveLearningAlgorithm
/*    */ {
/*    */   public ClusActiveLearningAlgorithmHMC(Clus clus, String name) throws ClusException, Exception {
/* 20 */     super(clus, name);
/*    */   } public RowData sample(ClusRun clusRun) throws ClusException {
/*    */     RowData queriedData;
/* 23 */     startIteration();
/*    */     
/* 25 */     if (isPartialLabelling()) {
/* 26 */       queriedData = samplePartialLabelling(clusRun);
/*    */     } else {
/* 28 */       queriedData = sampleWithoutPartialLabelling(clusRun);
/*    */     } 
/* 30 */     finishIteration();
/* 31 */     return queriedData;
/*    */   }
/*    */   
/*    */   public abstract RowData samplePartialLabelling(ClusRun paramClusRun) throws ClusException;
/*    */   
/*    */   public abstract RowData sampleWithoutPartialLabelling(ClusRun paramClusRun) throws ClusException;
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\activelearning\algo\ClusActiveLearningAlgorithmHMC.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */