/*     */ package clus.ext.beamsearch;
/*     */ 
/*     */ import clus.algo.tdidt.ClusNode;
/*     */ import clus.main.Settings;
/*     */ import clus.statistic.ClusStatistic;
/*     */ import clus.statistic.RegressionStat;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ClusBeamHeuristicMorishita
/*     */   extends ClusBeamHeuristic
/*     */ {
/*     */   public ClusBeamHeuristicMorishita(ClusStatistic stat) {
/*  37 */     super(stat);
/*     */   }
/*     */   
/*     */   public double computeMorishitaStat(ClusStatistic stat, ClusStatistic tstat) {
/*  41 */     RegressionStat stat_set = (RegressionStat)stat;
/*  42 */     RegressionStat stat_all = (RegressionStat)tstat;
/*     */     
/*  44 */     double result = 0.0D;
/*  45 */     for (int i = 0; i < stat_set.getNbAttributes(); i++) {
/*  46 */       double term_i = stat_set.getMean(i) - stat_all.getMean(i);
/*  47 */       result += term_i * term_i;
/*     */     } 
/*  49 */     return result * stat_set.getTotalWeight();
/*     */   }
/*     */   
/*     */   public double calcHeuristic(ClusStatistic c_tstat, ClusStatistic c_pstat, ClusStatistic missing) {
/*  53 */     double n_tot = c_tstat.m_SumWeight;
/*  54 */     double n_pos = c_pstat.m_SumWeight;
/*  55 */     double n_neg = n_tot - n_pos;
/*     */     
/*  57 */     if (n_pos < Settings.MINIMAL_WEIGHT || n_neg < Settings.MINIMAL_WEIGHT) {
/*  58 */       return Double.NEGATIVE_INFINITY;
/*     */     }
/*  60 */     this.m_Neg.copy(c_tstat);
/*  61 */     this.m_Neg.subtractFromThis(c_pstat);
/*     */     
/*  63 */     return computeMorishitaStat(c_pstat, c_tstat) + computeMorishitaStat(this.m_Neg, c_tstat);
/*     */   }
/*     */   
/*     */   public double estimateBeamMeasure(ClusNode tree, ClusNode parent) {
/*  67 */     if (tree.atBottomLevel()) {
/*  68 */       return computeMorishitaStat(tree.getClusteringStat(), parent.getClusteringStat());
/*     */     }
/*  70 */     double result = 0.0D;
/*  71 */     for (int i = 0; i < tree.getNbChildren(); i++) {
/*  72 */       ClusNode child = (ClusNode)tree.getChild(i);
/*  73 */       result += estimateBeamMeasure(child);
/*     */     } 
/*  75 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   public double estimateBeamMeasure(ClusNode tree) {
/*  80 */     if (tree.atBottomLevel()) {
/*  81 */       return 0.0D;
/*     */     }
/*  83 */     double result = 0.0D;
/*  84 */     for (int i = 0; i < tree.getNbChildren(); i++) {
/*  85 */       ClusNode child = (ClusNode)tree.getChild(i);
/*  86 */       result += estimateBeamMeasure(child, tree);
/*     */     } 
/*  88 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   public double computeLeafAdd(ClusNode leaf) {
/*  93 */     return 0.0D;
/*     */   }
/*     */   
/*     */   public String getName() {
/*  97 */     return "Beam Heuristic (Morishita)";
/*     */   }
/*     */   
/*     */   public void setRootStatistic(ClusStatistic stat) {
/* 101 */     super.setRootStatistic(stat);
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\ext\beamsearch\ClusBeamHeuristicMorishita.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */