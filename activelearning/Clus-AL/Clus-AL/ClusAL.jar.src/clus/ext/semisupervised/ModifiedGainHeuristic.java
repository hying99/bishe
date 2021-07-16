/*    */ package clus.ext.semisupervised;
/*    */ 
/*    */ import clus.heuristic.ClusHeuristicImpl;
/*    */ import clus.main.Settings;
/*    */ import clus.statistic.ClassificationStat;
/*    */ import clus.statistic.ClusStatistic;
/*    */ 
/*    */ public class ModifiedGainHeuristic
/*    */   extends ClusHeuristicImpl
/*    */ {
/*    */   public ModifiedGainHeuristic(ClusStatistic stat) {
/* 12 */     super(stat);
/*    */   }
/*    */   
/*    */   public double calcHeuristic(ClusStatistic c_tstat, ClusStatistic c_pstat, ClusStatistic c_nstat, ClusStatistic missing) {
/* 16 */     ClassificationStat tstat = (ClassificationStat)c_tstat;
/* 17 */     ClassificationStat pstat = (ClassificationStat)c_pstat;
/* 18 */     ClassificationStat nstat = (ClassificationStat)c_nstat;
/*    */     
/* 20 */     int nb = tstat.m_NbTarget;
/* 21 */     double n_tot = tstat.m_SumWeight;
/* 22 */     double n_pos = pstat.m_SumWeight;
/* 23 */     double n_neg = nstat.m_SumWeight;
/*    */     
/* 25 */     if (n_pos < Settings.MINIMAL_WEIGHT || n_neg < Settings.MINIMAL_WEIGHT) {
/* 26 */       return Double.NEGATIVE_INFINITY;
/*    */     }
/*    */     
/* 29 */     double pos_ent = 0.0D;
/* 30 */     double neg_ent = 0.0D;
/* 31 */     double tot_ent = 0.0D;
/*    */     
/* 33 */     for (int i = 0; i < nb; i++) {
/* 34 */       pos_ent += pstat.entropy(i);
/* 35 */       neg_ent += nstat.entropy(i);
/* 36 */       tot_ent += tstat.entropy(i);
/*    */     } 
/*    */     
/* 39 */     double gain = tot_ent - (n_pos * pos_ent + n_neg * neg_ent) / n_tot;
/* 40 */     if (gain < 1.0E-6D) return Double.NEGATIVE_INFINITY; 
/* 41 */     return gain;
/*    */   }
/*    */   
/*    */   public String getName() {
/* 45 */     return "Gain modified for semi-supervised learning";
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\ext\semisupervised\ModifiedGainHeuristic.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */