/*     */ package clus.heuristic;
/*     */ 
/*     */ import clus.statistic.ClassificationStat;
/*     */ import clus.statistic.ClusStatistic;
/*     */ import jeans.math.MathUtil;
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
/*     */ public class GainHeuristic
/*     */   extends ClusHeuristic
/*     */ {
/*     */   protected boolean m_GainRatio;
/*     */   
/*     */   public GainHeuristic(boolean gainratio) {
/*  34 */     this.m_GainRatio = gainratio;
/*     */   }
/*     */   
/*     */   public final boolean isGainRatio() {
/*  38 */     return this.m_GainRatio;
/*     */   }
/*     */ 
/*     */   
/*     */   public double calcHeuristic(ClusStatistic c_tstat, ClusStatistic c_pstat, ClusStatistic missing) {
/*  43 */     if (stopCriterion(c_tstat, c_pstat, missing)) {
/*  44 */       return Double.NEGATIVE_INFINITY;
/*     */     }
/*     */     
/*  47 */     ClassificationStat tstat = (ClassificationStat)c_tstat;
/*  48 */     ClassificationStat pstat = (ClassificationStat)c_pstat;
/*     */     
/*  50 */     double n_tot = tstat.getTotalWeight();
/*  51 */     double n_pos = pstat.getTotalWeight();
/*  52 */     double n_neg = n_tot - n_pos;
/*     */     
/*  54 */     double tot_ent = tstat.entropy();
/*  55 */     double pos_ent = pstat.entropy();
/*  56 */     double neg_ent = tstat.entropyDifference(pstat);
/*     */     
/*  58 */     double value = tot_ent - (n_pos * pos_ent + n_neg * neg_ent) / n_tot;
/*  59 */     if (value < 1.0E-6D) return Double.NEGATIVE_INFINITY; 
/*  60 */     if (this.m_GainRatio) {
/*  61 */       double si = ClassificationStat.computeSplitInfo(n_tot, n_pos, n_neg);
/*  62 */       if (si < 1.0E-6D) return Double.NEGATIVE_INFINITY; 
/*  63 */       return value / si;
/*     */     } 
/*  65 */     return value;
/*     */   }
/*     */ 
/*     */   
/*     */   public double calcHeuristic(ClusStatistic c_tstat, ClusStatistic[] c_pstat, int nbsplit) {
/*  70 */     if (stopCriterion(c_tstat, c_pstat, nbsplit)) {
/*  71 */       return Double.NEGATIVE_INFINITY;
/*     */     }
/*     */     
/*  74 */     ClassificationStat tstat = (ClassificationStat)c_tstat;
/*  75 */     double n_tot = tstat.getTotalWeight();
/*  76 */     double value = tstat.entropy();
/*     */     
/*  78 */     for (int i = 0; i < nbsplit; i++) {
/*  79 */       ClassificationStat pstat = (ClassificationStat)c_pstat[i];
/*  80 */       double n_set = pstat.getTotalWeight();
/*  81 */       value -= n_set / n_tot * pstat.entropy();
/*     */     } 
/*  83 */     if (value < 1.0E-6D) {
/*  84 */       return Double.NEGATIVE_INFINITY;
/*     */     }
/*  86 */     if (this.m_GainRatio) {
/*     */       
/*  88 */       double si = 0.0D;
/*  89 */       for (int j = 0; j < nbsplit; j++) {
/*  90 */         double n_set = c_pstat[j].getTotalWeight();
/*  91 */         if (n_set >= 1.0E-6D) {
/*  92 */           double div = n_set / n_tot;
/*  93 */           si -= div * Math.log(div);
/*     */         } 
/*     */       } 
/*  96 */       si /= MathUtil.M_LN2;
/*     */       
/*  98 */       if (si < 1.0E-6D) return Double.NEGATIVE_INFINITY; 
/*  99 */       return value / si;
/*     */     } 
/* 101 */     return value;
/*     */   }
/*     */   
/*     */   public String getName() {
/* 105 */     return this.m_GainRatio ? "Gainratio" : "Gain";
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\heuristic\GainHeuristic.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */