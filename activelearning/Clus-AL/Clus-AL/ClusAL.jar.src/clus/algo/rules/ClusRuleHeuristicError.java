/*    */ package clus.algo.rules;
/*    */ 
/*    */ import clus.data.attweights.ClusAttributeWeights;
/*    */ import clus.heuristic.ClusHeuristic;
/*    */ import clus.main.ClusStatManager;
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
/*    */ public class ClusRuleHeuristicError
/*    */   extends ClusHeuristic
/*    */ {
/*    */   private ClusAttributeWeights m_TargetWeights;
/* 38 */   private ClusStatManager m_StatManager = null;
/*    */   
/*    */   public ClusRuleHeuristicError(ClusAttributeWeights prod) {
/* 41 */     this.m_TargetWeights = prod;
/*    */   }
/*    */   
/*    */   public ClusRuleHeuristicError(ClusStatManager stat_mgr, ClusAttributeWeights prod) {
/* 45 */     this.m_StatManager = stat_mgr;
/* 46 */     this.m_TargetWeights = prod;
/*    */   }
/*    */   
/*    */   public double calcHeuristic(ClusStatistic c_tstat, ClusStatistic c_pstat, ClusStatistic missing) {
/* 50 */     double n_pos = c_pstat.m_SumWeight;
/*    */ 
/*    */     
/* 53 */     if (n_pos - Settings.MINIMAL_WEIGHT < 1.0E-6D) {
/* 54 */       return Double.NEGATIVE_INFINITY;
/*    */     }
/* 56 */     double pos_error = c_pstat.getError(this.m_TargetWeights);
/*    */     
/* 58 */     double global_sum_w = this.m_StatManager.getTrainSetStat(2).getTotalWeight();
/* 59 */     double heur_par = getSettings().getHeurCoveragePar();
/* 60 */     pos_error *= 1.0D + heur_par * global_sum_w / c_pstat.m_SumWeight;
/*    */     
/* 62 */     return -pos_error;
/*    */   }
/*    */   
/*    */   public String getName() {
/* 66 */     return "Rule Heuristic (Reduced Error)";
/*    */   }
/*    */   
/*    */   public Settings getSettings() {
/* 70 */     return this.m_StatManager.getSettings();
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\algo\rules\ClusRuleHeuristicError.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */