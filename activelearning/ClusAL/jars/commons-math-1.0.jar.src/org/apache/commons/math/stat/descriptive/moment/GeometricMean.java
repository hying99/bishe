/*     */ package org.apache.commons.math.stat.descriptive.moment;
/*     */ 
/*     */ import org.apache.commons.math.stat.descriptive.AbstractStorelessUnivariateStatistic;
/*     */ import org.apache.commons.math.stat.descriptive.summary.SumOfLogs;
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
/*     */ public class GeometricMean
/*     */   extends AbstractStorelessUnivariateStatistic
/*     */ {
/*     */   static final long serialVersionUID = -8178734905303459453L;
/*  57 */   private SumOfLogs sumOfLogs = new SumOfLogs();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void increment(double d) {
/*  64 */     this.sumOfLogs.increment(d);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getResult() {
/*  71 */     if (this.sumOfLogs.getN() > 0L) {
/*  72 */       return Math.exp(this.sumOfLogs.getResult() / this.sumOfLogs.getN());
/*     */     }
/*  74 */     return Double.NaN;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void clear() {
/*  82 */     this.sumOfLogs.clear();
/*     */   }
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
/*     */   public double evaluate(double[] values, int begin, int length) {
/* 103 */     return Math.exp(this.sumOfLogs.evaluate(values, begin, length) / length);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getN() {
/* 111 */     return this.sumOfLogs.getN();
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\commons-math-1.0.jar!\org\apache\commons\math\stat\descriptive\moment\GeometricMean.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */