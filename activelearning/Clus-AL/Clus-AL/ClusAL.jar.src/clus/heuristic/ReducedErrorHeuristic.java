/*    */ package clus.heuristic;
/*    */ 
/*    */ import clus.main.Settings;
/*    */ import clus.statistic.ClusStatistic;
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
/*    */ public class ReducedErrorHeuristic
/*    */   extends ClusHeuristic
/*    */ {
/*    */   private double m_NbTrain;
/*    */   ClusStatistic m_Pos;
/*    */   ClusStatistic m_Neg;
/*    */   
/*    */   public ReducedErrorHeuristic(ClusStatistic stat) {
/* 39 */     this.m_Pos = stat;
/* 40 */     this.m_Neg = stat.cloneStat();
/*    */   }
/*    */   
/*    */   public double calcHeuristic(ClusStatistic c_tstat, ClusStatistic c_pstat, ClusStatistic missing) {
/* 44 */     double n_tot = c_tstat.m_SumWeight;
/* 45 */     double n_pos = c_pstat.m_SumWeight;
/* 46 */     double n_neg = n_tot - n_pos;
/*    */     
/* 48 */     if (n_pos < Settings.MINIMAL_WEIGHT || n_neg < Settings.MINIMAL_WEIGHT) {
/* 49 */       return Double.NEGATIVE_INFINITY;
/*    */     }
/* 51 */     if (missing.m_SumWeight <= 1.0E-9D) {
/* 52 */       double d1 = c_pstat.getError();
/* 53 */       double d2 = c_tstat.getErrorDiff(c_pstat);
/* 54 */       return -(d1 + d2) / this.m_NbTrain;
/*    */     } 
/* 56 */     double pos_freq = n_pos / n_tot;
/* 57 */     this.m_Pos.copy(c_pstat);
/* 58 */     this.m_Neg.copy(c_tstat);
/* 59 */     this.m_Neg.subtractFromThis(c_pstat);
/* 60 */     this.m_Pos.addScaled(pos_freq, missing);
/* 61 */     this.m_Neg.addScaled(1.0D - pos_freq, missing);
/* 62 */     double pos_error = this.m_Pos.getError();
/* 63 */     double neg_error = this.m_Neg.getError();
/* 64 */     return -(pos_error + neg_error) / this.m_NbTrain;
/*    */   }
/*    */ 
/*    */   
/*    */   public double calcHeuristic(ClusStatistic c_tstat, ClusStatistic[] c_pstat, int nbsplit) {
/* 69 */     return Double.NEGATIVE_INFINITY;
/*    */   }
/*    */   
/*    */   public void setRootStatistic(ClusStatistic stat) {
/* 73 */     this.m_NbTrain = stat.m_SumWeight;
/*    */   }
/*    */   
/*    */   public String getName() {
/* 77 */     return "Reduced Error Heuristic";
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\heuristic\ReducedErrorHeuristic.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */