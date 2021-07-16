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
/*    */ public class VarianceReductionHeuristicInclMissingValues
/*    */   extends ClusHeuristic
/*    */ {
/*    */   private ClusAttributeWeights m_TargetWeights;
/*    */   private ClusAttrType[] m_Attrs;
/*    */   protected ClusStatistic m_Pos;
/*    */   protected ClusStatistic m_Neg;
/*    */   protected ClusStatistic m_Tot;
/*    */   
/*    */   public VarianceReductionHeuristicInclMissingValues(ClusAttributeWeights prod, ClusAttrType[] attrs, ClusStatistic stat) {
/* 37 */     this.m_TargetWeights = prod;
/* 38 */     this.m_Attrs = attrs;
/* 39 */     this.m_Pos = stat.cloneStat();
/* 40 */     this.m_Neg = stat.cloneStat();
/* 41 */     this.m_Tot = stat.cloneStat();
/*    */   }
/*    */   
/*    */   public double calcHeuristic(ClusStatistic tstat, ClusStatistic pstat, ClusStatistic missing) {
/* 45 */     double n_tot = tstat.m_SumWeight;
/* 46 */     double n_pos = pstat.m_SumWeight;
/* 47 */     double n_neg = n_tot - n_pos;
/*    */     
/* 49 */     if (n_pos < Settings.MINIMAL_WEIGHT || n_neg < Settings.MINIMAL_WEIGHT) {
/* 50 */       return Double.NEGATIVE_INFINITY;
/*    */     }
/*    */     
/* 53 */     double pos_freq = n_pos / n_tot;
/* 54 */     this.m_Pos.copy(pstat);
/* 55 */     this.m_Neg.copy(tstat);
/* 56 */     this.m_Tot.copy(tstat);
/* 57 */     this.m_Tot.add(missing);
/* 58 */     this.m_Neg.subtractFromThis(pstat);
/* 59 */     this.m_Pos.addScaled(pos_freq, missing);
/* 60 */     this.m_Neg.addScaled(1.0D - pos_freq, missing);
/* 61 */     double s_ss_pos = this.m_Pos.getSVarS(this.m_TargetWeights);
/* 62 */     double s_ss_neg = this.m_Neg.getSVarS(this.m_TargetWeights);
/* 63 */     double s_ss_tot = this.m_Tot.getSVarS(this.m_TargetWeights);
/* 64 */     return FTest.calcVarianceReductionHeuristic(n_tot, s_ss_tot, s_ss_pos + s_ss_neg);
/*    */   }
/*    */   
/*    */   public String getName() {
/* 68 */     return "Variance Reduction Including Missing Values (ftest: " + Settings.FTEST_VALUE + ", " + this.m_TargetWeights.getName(this.m_Attrs) + ")";
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\heuristic\VarianceReductionHeuristicInclMissingValues.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */