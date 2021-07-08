/*     */ package clus.error.multiscore;
/*     */ 
/*     */ import clus.data.cols.ColTarget;
/*     */ import clus.data.rows.DataTuple;
/*     */ import clus.data.rows.SparseDataTuple;
/*     */ import clus.statistic.ClusStatistic;
/*     */ import clus.statistic.StatisticPrintInfo;
/*     */ import clus.util.ClusFormat;
/*     */ import java.text.NumberFormat;
/*     */ import java.util.ArrayList;
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
/*     */ public class MultiScoreStat
/*     */   extends ClusStatistic
/*     */ {
/*     */   public static final long serialVersionUID = 1L;
/*     */   protected int m_NbTarget;
/*     */   protected int[] m_Score;
/*     */   protected double[] m_MeanValues;
/*     */   
/*     */   public MultiScoreStat(ClusStatistic stat, MultiScore score) {
/*  43 */     this.m_MeanValues = stat.getNumericPred();
/*  44 */     this.m_NbTarget = this.m_MeanValues.length;
/*  45 */     this.m_Score = score.multiScore(this.m_MeanValues);
/*     */   }
/*     */ 
/*     */   
/*     */   public String getArrayOfStatistic() {
/*  50 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getString(StatisticPrintInfo info) {
/*  55 */     NumberFormat fr = ClusFormat.SIX_AFTER_DOT;
/*  56 */     StringBuffer buf = new StringBuffer();
/*  57 */     buf.append("["); int i;
/*  58 */     for (i = 0; i < this.m_NbTarget; i++) {
/*  59 */       if (i != 0) buf.append(","); 
/*  60 */       buf.append(1 - this.m_Score[i]);
/*     */     } 
/*  62 */     buf.append("] : [");
/*  63 */     for (i = 0; i < this.m_NbTarget; i++) {
/*  64 */       if (i != 0) buf.append(",");
/*     */       
/*  66 */       buf.append(fr.format(this.m_MeanValues[i]));
/*     */     } 
/*  68 */     buf.append("]");
/*  69 */     return buf.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getPredictedClassName(int idx) {
/*  74 */     return "";
/*     */   }
/*     */   
/*     */   public double[] getNumericPred() {
/*  78 */     return this.m_MeanValues;
/*     */   }
/*     */   
/*     */   public int[] getNominalPred() {
/*  82 */     return this.m_Score;
/*     */   }
/*     */   
/*     */   public boolean samePrediction(ClusStatistic other) {
/*  86 */     MultiScoreStat or = (MultiScoreStat)other;
/*  87 */     for (int i = 0; i < this.m_NbTarget; i++) {
/*  88 */       if (this.m_Score[i] != or.m_Score[i]) return false; 
/*  89 */     }  return true;
/*     */   }
/*     */   
/*     */   public ClusStatistic cloneStat() {
/*  93 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public void update(ColTarget target, int idx) {}
/*     */ 
/*     */   
/*     */   public void updateWeighted(DataTuple tuple, int idx) {}
/*     */ 
/*     */   
/*     */   public void calcMean() {}
/*     */ 
/*     */   
/*     */   public void reset() {}
/*     */ 
/*     */   
/*     */   public void copy(ClusStatistic other) {}
/*     */ 
/*     */   
/*     */   public void addPrediction(ClusStatistic other, double weight) {}
/*     */ 
/*     */   
/*     */   public void add(ClusStatistic other) {}
/*     */ 
/*     */   
/*     */   public void addScaled(double scale, ClusStatistic other) {}
/*     */ 
/*     */   
/*     */   public void subtractFromThis(ClusStatistic other) {}
/*     */ 
/*     */   
/*     */   public void subtractFromOther(ClusStatistic other) {}
/*     */   
/*     */   public void vote(ArrayList votes) {
/* 127 */     System.err.println(getClass().getName() + "vote (): Not implemented");
/*     */   }
/*     */   
/*     */   public void updateWeighted(SparseDataTuple tuple, int idx) {}
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\error\multiscore\MultiScoreStat.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */