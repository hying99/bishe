/*    */ package clus.ext.hierarchical;
/*    */ 
/*    */ import clus.data.attweights.ClusAttributeWeights;
/*    */ import clus.heuristic.ClusHeuristic;
/*    */ import clus.main.ClusStatManager;
/*    */ import clus.main.Settings;
/*    */ import clus.statistic.ClusStatistic;
/*    */ 
/*    */ public class ClusRuleHeuristicHierarchical
/*    */   extends ClusHeuristic
/*    */ {
/*    */   protected ClusAttributeWeights m_TargetWeights;
/*    */   protected ClusStatManager m_StatManager;
/*    */   
/*    */   public ClusRuleHeuristicHierarchical(ClusStatManager stat_mgr, ClusAttributeWeights prod) {
/* 16 */     this.m_StatManager = stat_mgr;
/* 17 */     this.m_TargetWeights = prod;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public double calcHeuristic(ClusStatistic c_tstat, ClusStatistic c_pstat, ClusStatistic missing) {
/* 27 */     double n_pos = c_pstat.m_SumWeight;
/* 28 */     if (n_pos - Settings.MINIMAL_WEIGHT < 1.0E-6D) {
/* 29 */       return Double.NEGATIVE_INFINITY;
/*    */     }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */     
/* 39 */     double totalValue = getTrainDataHeurValue();
/*    */     
/* 41 */     double ruleValue = c_pstat.getSVarS(this.m_TargetWeights);
/* 42 */     double value = totalValue - ruleValue;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */     
/* 48 */     double train_sum_w = this.m_StatManager.getTrainSetStat(2).getTotalWeight();
/* 49 */     double coverage = n_pos / train_sum_w;
/* 50 */     double cov_par = this.m_StatManager.getSettings().getHeurCoveragePar();
/* 51 */     coverage = Math.pow(coverage, cov_par);
/* 52 */     value *= coverage;
/*    */ 
/*    */     
/* 55 */     return value;
/*    */   }
/*    */   
/*    */   public String getName() {
/* 59 */     return "RuleHeuristicHierarchical";
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\ext\hierarchical\ClusRuleHeuristicHierarchical.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */