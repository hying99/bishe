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
/*    */ 
/*    */ public class ClusBeamHeuristicError
/*    */   extends ClusBeamHeuristic
/*    */ {
/*    */   public ClusBeamHeuristicError(ClusStatistic stat) {
/* 36 */     super(stat);
/*    */   }
/*    */   
/*    */   public double calcHeuristic(ClusStatistic c_tstat, ClusStatistic c_pstat, ClusStatistic missing) {
/* 40 */     double n_tot = c_tstat.m_SumWeight;
/* 41 */     double n_pos = c_pstat.m_SumWeight;
/* 42 */     double n_neg = n_tot - n_pos;
/*    */     
/* 44 */     if (n_pos < Settings.MINIMAL_WEIGHT || n_neg < Settings.MINIMAL_WEIGHT) {
/* 45 */       return Double.NEGATIVE_INFINITY;
/*    */     }
/* 47 */     if (missing.m_SumWeight <= 1.0E-9D) {
/* 48 */       double d1 = c_pstat.getError();
/* 49 */       double d2 = c_tstat.getErrorDiff(c_pstat);
/* 50 */       return this.m_TreeOffset - (d1 + d2) / this.m_NbTrain - 2.0D * Settings.SIZE_PENALTY;
/*    */     } 
/* 52 */     double pos_freq = n_pos / n_tot;
/* 53 */     this.m_Pos.copy(c_pstat);
/* 54 */     this.m_Neg.copy(c_tstat);
/* 55 */     this.m_Neg.subtractFromThis(c_pstat);
/* 56 */     this.m_Pos.addScaled(pos_freq, missing);
/* 57 */     this.m_Neg.addScaled(1.0D - pos_freq, missing);
/* 58 */     double pos_error = this.m_Pos.getError();
/* 59 */     double neg_error = this.m_Neg.getError();
/* 60 */     return this.m_TreeOffset - (pos_error + neg_error) / this.m_NbTrain - 2.0D * Settings.SIZE_PENALTY;
/*    */   }
/*    */ 
/*    */   
/*    */   public double estimateBeamMeasure(ClusNode tree) {
/* 65 */     if (tree.atBottomLevel()) {
/* 66 */       ClusStatistic total = tree.getClusteringStat();
/* 67 */       return -total.getError() / this.m_NbTrain - Settings.SIZE_PENALTY;
/*    */     } 
/* 69 */     double result = 0.0D;
/* 70 */     for (int i = 0; i < tree.getNbChildren(); i++) {
/* 71 */       ClusNode child = (ClusNode)tree.getChild(i);
/* 72 */       result += estimateBeamMeasure(child);
/*    */     } 
/* 74 */     return result - Settings.SIZE_PENALTY;
/*    */   }
/*    */ 
/*    */   
/*    */   public double computeLeafAdd(ClusNode leaf) {
/* 79 */     return -leaf.getClusteringStat().getError() / this.m_NbTrain;
/*    */   }
/*    */   
/*    */   public String getName() {
/* 83 */     return "Beam Heuristic (Reduced Error)" + getAttrHeuristicString();
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\ext\beamsearch\ClusBeamHeuristicError.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */