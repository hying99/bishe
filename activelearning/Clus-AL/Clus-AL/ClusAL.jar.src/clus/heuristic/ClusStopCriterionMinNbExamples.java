/*    */ package clus.heuristic;
/*    */ 
/*    */ import clus.statistic.ClusStatistic;
/*    */ 
/*    */ public class ClusStopCriterionMinNbExamples
/*    */   implements ClusStopCriterion {
/*    */   protected int m_MinExamples;
/*    */   
/*    */   public ClusStopCriterionMinNbExamples(int minExamples) {
/* 10 */     this.m_MinExamples = minExamples;
/*    */   }
/*    */   
/*    */   public boolean stopCriterion(ClusStatistic tstat, ClusStatistic pstat, ClusStatistic missing) {
/* 14 */     int n_pos = pstat.getNbExamples();
/* 15 */     int n_neg = tstat.getNbExamples() - n_pos;
/*    */ 
/*    */ 
/*    */     
/* 19 */     return (n_pos < this.m_MinExamples || n_neg < this.m_MinExamples);
/*    */   }
/*    */   
/*    */   public boolean stopCriterion(ClusStatistic tstat, ClusStatistic[] pstat, int nbsplit) {
/* 23 */     for (int i = 0; i < nbsplit; i++) {
/* 24 */       if (pstat[i].getNbExamples() < this.m_MinExamples) {
/* 25 */         return true;
/*    */       }
/*    */     } 
/* 28 */     return false;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\heuristic\ClusStopCriterionMinNbExamples.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */