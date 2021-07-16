/*    */ package clus.heuristic;
/*    */ 
/*    */ import clus.data.attweights.ClusAttributeWeights;
/*    */ import clus.data.type.ClusAttrType;
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
/*    */ public class VarianceReductionHeuristicEfficient
/*    */   extends ClusHeuristic
/*    */ {
/*    */   private ClusAttributeWeights m_ClusteringWeights;
/*    */   private ClusAttrType[] m_Attrs;
/*    */   
/*    */   public VarianceReductionHeuristicEfficient(ClusAttributeWeights prod, ClusAttrType[] attrs) {
/* 36 */     this.m_ClusteringWeights = prod;
/* 37 */     this.m_Attrs = attrs;
/*    */   }
/*    */ 
/*    */   
/*    */   public double calcHeuristic(ClusStatistic tstat, ClusStatistic pstat, ClusStatistic missing) {
/* 42 */     if (stopCriterion(tstat, pstat, missing))
/*    */     {
/* 44 */       return Double.NEGATIVE_INFINITY;
/*    */     }
/*    */ 
/*    */     
/* 48 */     double ss_tot = tstat.getSVarS(this.m_ClusteringWeights);
/* 49 */     double ss_pos = pstat.getSVarS2(this.m_ClusteringWeights);
/* 50 */     double ss_neg = tstat.getSVarSDiff(this.m_ClusteringWeights, pstat);
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
/* 62 */     return FTest.calcVarianceReductionHeuristic(tstat.getTotalWeight(), ss_tot, ss_pos + ss_neg);
/*    */   }
/*    */ 
/*    */   
/*    */   public double calcHeuristic(ClusStatistic tstat, ClusStatistic[] pstat, int nbsplit) {
/* 67 */     if (stopCriterion(tstat, pstat, nbsplit)) {
/* 68 */       return Double.NEGATIVE_INFINITY;
/*    */     }
/*    */     
/* 71 */     double ss_sum = 0.0D;
/* 72 */     for (int i = 0; i < nbsplit; i++) {
/* 73 */       ss_sum += pstat[i].getSVarS(this.m_ClusteringWeights);
/*    */     }
/* 75 */     double ss_tot = tstat.getSVarS(this.m_ClusteringWeights);
/* 76 */     return FTest.calcVarianceReductionHeuristic(tstat.getTotalWeight(), ss_tot, ss_sum);
/*    */   }
/*    */   
/*    */   public String getName() {
/* 80 */     return "Variance Reduction (FTest = " + Settings.FTEST_VALUE + ", " + this.m_ClusteringWeights.getName(this.m_Attrs) + ")";
/*    */   }
/*    */   
/*    */   public void printInfo(double ss_tot, double ss_pos, double ss_neg, ClusStatistic pstat) {
/* 84 */     pstat.calcMean();
/* 85 */     System.out.println("C-pos: " + pstat);
/* 86 */     System.out.println("SS-pos: " + ss_pos + " SS-neg: " + ss_neg + " -> " + (ss_tot - ss_pos + ss_neg));
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\heuristic\VarianceReductionHeuristicEfficient.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */