/*     */ package org.apache.commons.math.stat.descriptive.rank;
/*     */ 
/*     */ import org.apache.commons.math.stat.descriptive.AbstractStorelessUnivariateStatistic;
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
/*     */ public class Max
/*     */   extends AbstractStorelessUnivariateStatistic
/*     */ {
/*     */   static final long serialVersionUID = -5593383832225844641L;
/*  52 */   private long n = 0L;
/*  53 */   private double value = Double.NaN;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void increment(double d) {
/*  60 */     if (d > this.value || Double.isNaN(this.value)) {
/*  61 */       this.value = d;
/*     */     }
/*  63 */     this.n++;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void clear() {
/*  70 */     this.value = Double.NaN;
/*  71 */     this.n = 0L;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getResult() {
/*  78 */     return this.value;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getN() {
/*  85 */     return this.n;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double evaluate(double[] values, int begin, int length) {
/* 111 */     double max = Double.NaN;
/* 112 */     if (test(values, begin, length)) {
/* 113 */       max = values[begin];
/* 114 */       for (int i = begin; i < begin + length; i++) {
/* 115 */         max = (max > values[i]) ? max : values[i];
/*     */       }
/*     */     } 
/* 118 */     return max;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\commons-math-1.0.jar!\org\apache\commons\math\stat\descriptive\rank\Max.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */