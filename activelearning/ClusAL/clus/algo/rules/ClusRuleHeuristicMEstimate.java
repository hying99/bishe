/*    */ package clus.algo.rules;
/*    */ 
/*    */ import clus.heuristic.ClusHeuristic;
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
/*    */ public class ClusRuleHeuristicMEstimate
/*    */   extends ClusHeuristic
/*    */ {
/*    */   double m_MValue;
/*    */   double m_Prior;
/*    */   
/*    */   public ClusRuleHeuristicMEstimate(double m_value) {
/* 38 */     this.m_MValue = m_value;
/*    */   }
/*    */   
/*    */   public double calcHeuristic(ClusStatistic c_tstat, ClusStatistic c_pstat, ClusStatistic missing) {
/* 42 */     double n_pos = c_pstat.m_SumWeight;
/*    */ 
/*    */     
/* 45 */     if (n_pos - Settings.MINIMAL_WEIGHT < 1.0E-6D) {
/* 46 */       return Double.NEGATIVE_INFINITY;
/*    */     }
/* 48 */     double correct = n_pos - c_pstat.getError();
/* 49 */     double m_estimate = (correct + this.m_MValue * this.m_Prior) / (n_pos + this.m_MValue);
/* 50 */     return m_estimate;
/*    */   }
/*    */   
/*    */   public void setRootStatistic(ClusStatistic stat) {
/* 54 */     this.m_Prior = (stat.getTotalWeight() - stat.getError()) / stat.getTotalWeight();
/* 55 */     System.out.println("Setting prior: " + this.m_Prior);
/*    */   }
/*    */   
/*    */   public String getName() {
/* 59 */     return "Rule Heuristic (M-Estimate, M = " + this.m_MValue + ")";
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\algo\rules\ClusRuleHeuristicMEstimate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */