/*    */ package clus.ext.beamsearch;
/*    */ 
/*    */ import clus.algo.tdidt.ClusNode;
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
/*    */ 
/*    */ 
/*    */ public class ClusBeamHeuristicSS
/*    */   extends ClusBeamHeuristic
/*    */ {
/*    */   private ClusAttributeWeights m_TargetWeights;
/*    */   
/*    */   public ClusBeamHeuristicSS(ClusStatistic stat, ClusAttributeWeights prod) {
/* 38 */     super(stat);
/* 39 */     this.m_TargetWeights = prod;
/*    */   }
/*    */   
/*    */   public double calcHeuristic(ClusStatistic c_tstat, ClusStatistic c_pstat, ClusStatistic missing) {
/* 43 */     double n_tot = c_tstat.m_SumWeight;
/* 44 */     double n_pos = c_pstat.m_SumWeight;
/* 45 */     double n_neg = n_tot - n_pos;
/*    */     
/* 47 */     if (n_pos < Settings.MINIMAL_WEIGHT || n_neg < Settings.MINIMAL_WEIGHT) {
/* 48 */       return Double.NEGATIVE_INFINITY;
/*    */     }
/* 50 */     if (missing.m_SumWeight == 0.0D) {
/* 51 */       this.m_Neg.copy(c_tstat);
/* 52 */       this.m_Neg.subtractFromThis(c_pstat);
/* 53 */       double d1 = this.m_Pos.getSVarS(this.m_TargetWeights);
/* 54 */       double d2 = this.m_Neg.getSVarS(this.m_TargetWeights);
/* 55 */       return this.m_TreeOffset - (d1 + d2) / this.m_NbTrain - 2.0D * Settings.SIZE_PENALTY;
/*    */     } 
/* 57 */     double pos_freq = n_pos / n_tot;
/* 58 */     this.m_Pos.copy(c_pstat);
/* 59 */     this.m_Neg.copy(c_tstat);
/* 60 */     this.m_Neg.subtractFromThis(c_pstat);
/* 61 */     this.m_Pos.addScaled(pos_freq, missing);
/* 62 */     this.m_Neg.addScaled(1.0D - pos_freq, missing);
/* 63 */     double pos_error = this.m_Pos.getSVarS(this.m_TargetWeights);
/* 64 */     double neg_error = this.m_Neg.getSVarS(this.m_TargetWeights);
/* 65 */     return this.m_TreeOffset - (pos_error + neg_error) / this.m_NbTrain - 2.0D * Settings.SIZE_PENALTY;
/*    */   }
/*    */ 
/*    */   
/*    */   public double estimateBeamMeasure(ClusNode tree) {
/* 70 */     if (tree.atBottomLevel()) {
/* 71 */       ClusStatistic total = tree.getClusteringStat();
/* 72 */       return -total.getSVarS(this.m_TargetWeights) / this.m_NbTrain - Settings.SIZE_PENALTY;
/*    */     } 
/* 74 */     double result = 0.0D;
/* 75 */     for (int i = 0; i < tree.getNbChildren(); i++) {
/* 76 */       ClusNode child = (ClusNode)tree.getChild(i);
/* 77 */       result += estimateBeamMeasure(child);
/*    */     } 
/* 79 */     return result - Settings.SIZE_PENALTY;
/*    */   }
/*    */ 
/*    */   
/*    */   public double computeLeafAdd(ClusNode leaf) {
/* 84 */     return -leaf.getClusteringStat().getSVarS(this.m_TargetWeights) / this.m_NbTrain;
/*    */   }
/*    */   
/*    */   public String getName() {
/* 88 */     return "Beam Heuristic (Reduced Variance)" + getAttrHeuristicString() + " with " + this.m_TargetWeights.getName();
/*    */   }
/*    */   
/*    */   public void setRootStatistic(ClusStatistic stat) {
/* 92 */     super.setRootStatistic(stat);
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\ext\beamsearch\ClusBeamHeuristicSS.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */