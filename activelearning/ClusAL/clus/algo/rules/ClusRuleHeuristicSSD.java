/*    */ package clus.algo.rules;
/*    */ 
/*    */ import clus.data.attweights.ClusAttributeWeights;
/*    */ import clus.data.rows.RowData;
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
/*    */ public class ClusRuleHeuristicSSD
/*    */   extends ClusHeuristic
/*    */ {
/*    */   protected RowData m_Data;
/*    */   protected String m_BasicDist;
/*    */   protected ClusStatistic m_NegStat;
/*    */   protected ClusAttributeWeights m_TargetWeights;
/*    */   protected ClusStatManager m_StatManager;
/*    */   
/*    */   public ClusRuleHeuristicSSD(ClusStatManager statManager, String basicdist, ClusStatistic negstat, ClusAttributeWeights targetweights) {
/* 43 */     this.m_StatManager = statManager;
/* 44 */     this.m_BasicDist = basicdist;
/* 45 */     this.m_NegStat = negstat;
/* 46 */     this.m_TargetWeights = targetweights;
/*    */   }
/*    */ 
/*    */   
/*    */   public void setData(RowData data) {
/* 51 */     this.m_Data = data;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public double calcHeuristic(ClusStatistic tstat, ClusStatistic pstat, ClusStatistic missing) {
/* 57 */     double n_pos = pstat.m_SumWeight;
/*    */     
/* 59 */     if (n_pos < Settings.MINIMAL_WEIGHT) {
/* 60 */       return Double.NEGATIVE_INFINITY;
/*    */     }
/*    */     
/* 63 */     double offset = this.m_StatManager.getSettings().getHeurDispOffset();
/* 64 */     double def_value = getTrainDataHeurValue();
/*    */ 
/*    */     
/* 67 */     double value = pstat.getSVarS(this.m_TargetWeights, this.m_Data);
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */     
/* 73 */     double norm = this.m_StatManager.getSettings().getVarBasedDispNormWeight();
/* 74 */     value = 1.0D / norm * norm * (1.0D - value / def_value) + offset;
/*    */ 
/*    */ 
/*    */     
/* 78 */     double train_sum_w = this.m_StatManager.getTrainSetStat(2).getTotalWeight();
/* 79 */     double cov_par = this.m_StatManager.getSettings().getHeurCoveragePar();
/* 80 */     value *= Math.pow(n_pos / train_sum_w, cov_par);
/*    */     
/* 82 */     if (value < 1.0E-6D) return Double.NEGATIVE_INFINITY; 
/* 83 */     return value;
/*    */   }
/*    */   
/*    */   public String getName() {
/* 87 */     return "SS Reduction for Rules (" + this.m_BasicDist + ", " + this.m_TargetWeights.getName() + ")";
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\algo\rules\ClusRuleHeuristicSSD.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */