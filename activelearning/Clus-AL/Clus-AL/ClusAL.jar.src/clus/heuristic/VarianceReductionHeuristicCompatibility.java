/*    */ package clus.heuristic;
/*    */ 
/*    */ import clus.data.attweights.ClusAttributeWeights;
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
/*    */ public class VarianceReductionHeuristicCompatibility
/*    */   extends ClusHeuristic
/*    */ {
/*    */   protected String m_BasicDist;
/*    */   protected ClusAttributeWeights m_TargetWeights;
/*    */   
/*    */   public VarianceReductionHeuristicCompatibility(String basicdist, ClusStatistic negstat, ClusAttributeWeights targetweights) {
/* 36 */     this.m_BasicDist = basicdist;
/* 37 */     this.m_TargetWeights = targetweights;
/*    */   }
/*    */   
/*    */   public VarianceReductionHeuristicCompatibility(ClusStatistic negstat, ClusAttributeWeights targetweights) {
/* 41 */     this.m_BasicDist = negstat.getDistanceName();
/* 42 */     this.m_TargetWeights = targetweights;
/*    */   }
/*    */   
/*    */   public double calcHeuristic(ClusStatistic tstat, ClusStatistic pstat, ClusStatistic missing) {
/* 46 */     if (stopCriterion(tstat, pstat, missing)) {
/* 47 */       return Double.NEGATIVE_INFINITY;
/*    */     }
/*    */     
/* 50 */     double ss_tot = tstat.getSVarS(this.m_TargetWeights);
/* 51 */     double ss_pos = pstat.getSVarS(this.m_TargetWeights);
/* 52 */     double ss_neg = tstat.getSVarSDiff(this.m_TargetWeights, pstat);
/* 53 */     double value = FTest.calcVarianceReductionHeuristic(tstat.getTotalWeight(), ss_tot, ss_pos + ss_neg);
/* 54 */     if (Settings.VERBOSE >= 10) {
/* 55 */       System.out.println("TOT: " + tstat.getDebugString());
/* 56 */       System.out.println("POS: " + pstat.getDebugString());
/* 57 */       System.out.println("-> (" + ss_tot + ", " + ss_pos + ", " + ss_neg + ") " + value);
/*    */     } 
/*    */     
/* 60 */     if (value < 1.0E-6D) return Double.NEGATIVE_INFINITY; 
/* 61 */     return value;
/*    */   }
/*    */   
/*    */   public String getName() {
/* 65 */     return "Variance Reduction with Distance '" + this.m_BasicDist + "', (" + this.m_TargetWeights.getName() + ") (FTest = " + FTest.getSettingSig() + ")";
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\heuristic\VarianceReductionHeuristicCompatibility.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */