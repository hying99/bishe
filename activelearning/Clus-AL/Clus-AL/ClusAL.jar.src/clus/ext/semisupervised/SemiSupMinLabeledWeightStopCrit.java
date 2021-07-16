/*    */ package clus.ext.semisupervised;
/*    */ 
/*    */ import clus.heuristic.ClusStopCriterion;
/*    */ import clus.statistic.ClusStatistic;
/*    */ import clus.statistic.CombStat;
/*    */ 
/*    */ public class SemiSupMinLabeledWeightStopCrit
/*    */   implements ClusStopCriterion {
/*    */   protected double m_MinWeight;
/*    */   
/*    */   public SemiSupMinLabeledWeightStopCrit(double minWeight) {
/* 12 */     this.m_MinWeight = minWeight;
/*    */   }
/*    */   
/*    */   public boolean stopCriterion(ClusStatistic tstat, ClusStatistic pstat, ClusStatistic missing) {
/* 16 */     CombStat ctstat = (CombStat)tstat;
/* 17 */     CombStat cpstat = (CombStat)pstat;
/* 18 */     int lastClass = ctstat.getNbNominalAttributes() - 1;
/* 19 */     double w_pos = cpstat.getClassificationStat().getSumWeight(lastClass);
/* 20 */     double w_neg = ctstat.getClassificationStat().getSumWeight(lastClass) - w_pos;
/* 21 */     return (w_pos < this.m_MinWeight || w_neg < this.m_MinWeight);
/*    */   }
/*    */   
/*    */   public boolean stopCriterion(ClusStatistic tstat, ClusStatistic[] pstat, int nbsplit) {
/* 25 */     CombStat ctstat = (CombStat)tstat;
/* 26 */     int lastClass = ctstat.getNbNominalAttributes() - 1;
/* 27 */     for (int i = 0; i < nbsplit; i++) {
/* 28 */       CombStat cstat = (CombStat)pstat[i];
/* 29 */       if (cstat.getClassificationStat().getSumWeight(lastClass) < this.m_MinWeight) {
/* 30 */         return true;
/*    */       }
/*    */     } 
/* 33 */     return false;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\ext\semisupervised\SemiSupMinLabeledWeightStopCrit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */