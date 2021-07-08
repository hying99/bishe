/*    */ package clus.heuristic;
/*    */ 
/*    */ import clus.data.attweights.ClusAttributeWeights;
/*    */ import clus.data.rows.RowData;
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
/*    */ public class VarianceReductionHeuristic
/*    */   extends ClusHeuristic
/*    */ {
/*    */   protected RowData m_Data;
/*    */   protected String m_BasicDist;
/*    */   protected ClusStatistic m_NegStat;
/*    */   protected ClusAttributeWeights m_TargetWeights;
/*    */   
/*    */   public VarianceReductionHeuristic(String basicdist, ClusStatistic negstat, ClusAttributeWeights targetweights) {
/* 38 */     this.m_BasicDist = basicdist;
/* 39 */     this.m_NegStat = negstat;
/* 40 */     this.m_TargetWeights = targetweights;
/*    */   }
/*    */   
/*    */   public VarianceReductionHeuristic(ClusStatistic negstat, ClusAttributeWeights targetweights) {
/* 44 */     this.m_BasicDist = negstat.getDistanceName();
/* 45 */     this.m_NegStat = negstat;
/* 46 */     this.m_TargetWeights = targetweights;
/*    */   }
/*    */   
/*    */   public void setData(RowData data) {
/* 50 */     this.m_Data = data;
/*    */   }
/*    */ 
/*    */   
/*    */   public double calcHeuristic(ClusStatistic tstat, ClusStatistic pstat, ClusStatistic missing) {
/* 55 */     if (stopCriterion(tstat, pstat, missing)) {
/* 56 */       return Double.NEGATIVE_INFINITY;
/*    */     }
/*    */     
/* 59 */     double ss_tot = tstat.getSVarS(this.m_TargetWeights, this.m_Data);
/* 60 */     double ss_pos = pstat.getSVarS(this.m_TargetWeights, this.m_Data);
/* 61 */     this.m_NegStat.copy(tstat);
/* 62 */     this.m_NegStat.subtractFromThis(pstat);
/* 63 */     double ss_neg = this.m_NegStat.getSVarS(this.m_TargetWeights, this.m_Data);
/* 64 */     double value = FTest.calcVarianceReductionHeuristic(tstat.getTotalWeight(), ss_tot, ss_pos + ss_neg);
/* 65 */     if (Settings.VERBOSE >= 10) {
/* 66 */       System.out.println("TOT: " + tstat.getDebugString());
/* 67 */       System.out.println("POS: " + pstat.getDebugString());
/* 68 */       System.out.println("NEG: " + this.m_NegStat.getDebugString());
/* 69 */       System.out.println("-> (" + ss_tot + ", " + ss_pos + ", " + ss_neg + ") " + value);
/*    */     } 
/* 71 */     return value;
/*    */   }
/*    */   
/*    */   public String getName() {
/* 75 */     return "Variance Reduction with Distance '" + this.m_BasicDist + "', (" + this.m_TargetWeights.getName() + ") (FTest = " + FTest.getSettingSig() + ")";
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\heuristic\VarianceReductionHeuristic.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */