/*    */ package sit.mtLearner;
/*    */ 
/*    */ import clus.data.rows.RowData;
/*    */ import java.util.ArrayList;
/*    */ import sit.TargetSet;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ResultsCache
/*    */ {
/* 24 */   protected ArrayList<TargetSet> m_TargetSets = new ArrayList<>();
/* 25 */   protected ArrayList<RowData> m_TestData = new ArrayList<>();
/* 26 */   protected ArrayList<RowData> m_Predictions = new ArrayList<>();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void addResult(TargetSet targetset, RowData[] testpred) {}
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public RowData[] getResult(TargetSet targets, RowData test) {
/* 40 */     for (int i = 0; i < this.m_TargetSets.size(); i++) {
/* 41 */       if (targets.equals(this.m_TargetSets.get(i)))
/*    */       {
/* 43 */         if (test.equals(this.m_TestData.get(i))) {
/*    */           
/* 45 */           RowData[] result = { test, this.m_Predictions.get(i) };
/* 46 */           return result;
/*    */         } 
/*    */       }
/*    */     } 
/*    */ 
/*    */ 
/*    */     
/* 53 */     return null;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\sit\mtLearner\ResultsCache.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */