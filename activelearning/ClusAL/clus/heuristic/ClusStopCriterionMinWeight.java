/*    */ package clus.heuristic;
/*    */ 
/*    */ import clus.statistic.ClusStatistic;
/*    */ 
/*    */ public class ClusStopCriterionMinWeight
/*    */   implements ClusStopCriterion {
/*    */   protected double m_MinWeight;
/*    */   
/*    */   public ClusStopCriterionMinWeight(double minWeight) {
/* 10 */     this.m_MinWeight = minWeight;
/*    */   }
/*    */   
/*    */   public boolean stopCriterion(ClusStatistic tstat, ClusStatistic pstat, ClusStatistic missing) {
/* 14 */     double w_pos = pstat.getTotalWeight();
/* 15 */     double w_neg = tstat.getTotalWeight() - w_pos;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */     
/* 22 */     return (w_pos < this.m_MinWeight || w_neg < this.m_MinWeight);
/*    */   }
/*    */   
/*    */   public boolean stopCriterion(ClusStatistic tstat, ClusStatistic[] pstat, int nbsplit) {
/* 26 */     for (int i = 0; i < nbsplit; i++) {
/* 27 */       if (pstat[i].getTotalWeight() < this.m_MinWeight) {
/* 28 */         return true;
/*    */       }
/*    */     } 
/* 31 */     return false;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\heuristic\ClusStopCriterionMinWeight.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */