/*    */ package clus.ext.beamsearch;
/*    */ 
/*    */ import clus.algo.tdidt.ClusNode;
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
/*    */ public class ClusBeamHeuristicMEstimate
/*    */   extends ClusBeamHeuristic
/*    */ {
/*    */   protected double m_Prior;
/*    */   protected double m_MValue;
/*    */   
/*    */   public ClusBeamHeuristicMEstimate(ClusStatistic stat, double mvalue) {
/* 38 */     super(stat);
/* 39 */     this.m_MValue = mvalue;
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
/* 50 */     if (missing.m_SumWeight <= 1.0E-9D) {
/* 51 */       double d1 = c_pstat.getError();
/* 52 */       double d2 = c_tstat.getErrorDiff(c_pstat);
/* 53 */       return this.m_TreeOffset - (d1 + d2) / this.m_NbTrain - 2.0D * Settings.SIZE_PENALTY;
/*    */     } 
/* 55 */     double pos_freq = n_pos / n_tot;
/* 56 */     this.m_Pos.copy(c_pstat);
/* 57 */     this.m_Neg.copy(c_tstat);
/* 58 */     this.m_Neg.subtractFromThis(c_pstat);
/* 59 */     this.m_Pos.addScaled(pos_freq, missing);
/* 60 */     this.m_Neg.addScaled(1.0D - pos_freq, missing);
/* 61 */     double pos_error = this.m_Pos.getError();
/* 62 */     double neg_error = this.m_Neg.getError();
/* 63 */     return this.m_TreeOffset - (pos_error + neg_error) / this.m_NbTrain - 2.0D * Settings.SIZE_PENALTY;
/*    */   }
/*    */ 
/*    */   
/*    */   public double estimateBeamMeasure(ClusNode tree) {
/* 68 */     if (tree.atBottomLevel()) {
/* 69 */       ClusStatistic total = tree.getClusteringStat();
/* 70 */       return -total.getError() / this.m_NbTrain - Settings.SIZE_PENALTY;
/*    */     } 
/* 72 */     double result = 0.0D;
/* 73 */     for (int i = 0; i < tree.getNbChildren(); i++) {
/* 74 */       ClusNode child = (ClusNode)tree.getChild(i);
/* 75 */       result += estimateBeamMeasure(child);
/*    */     } 
/* 77 */     return result - Settings.SIZE_PENALTY;
/*    */   }
/*    */ 
/*    */   
/*    */   public double computeLeafAdd(ClusNode leaf) {
/* 82 */     return -leaf.getClusteringStat().getError() / this.m_NbTrain;
/*    */   }
/*    */   
/*    */   public void setRootStatistic(ClusStatistic stat) {
/* 86 */     this.m_Prior = (stat.getTotalWeight() - stat.getError()) / stat.getTotalWeight();
/* 87 */     System.out.println("Setting prior: " + this.m_Prior);
/*    */   }
/*    */   
/*    */   public String getName() {
/* 91 */     return "Beam Heuristic (MEstimate = " + this.m_MValue + ")" + getAttrHeuristicString();
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\ext\beamsearch\ClusBeamHeuristicMEstimate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */