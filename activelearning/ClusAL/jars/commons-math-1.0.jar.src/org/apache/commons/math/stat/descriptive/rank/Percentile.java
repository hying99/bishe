/*     */ package org.apache.commons.math.stat.descriptive.rank;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.util.Arrays;
/*     */ import org.apache.commons.math.stat.descriptive.AbstractUnivariateStatistic;
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
/*     */ public class Percentile
/*     */   extends AbstractUnivariateStatistic
/*     */   implements Serializable
/*     */ {
/*     */   static final long serialVersionUID = -8091216485095130416L;
/*  72 */   private double quantile = 0.0D;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Percentile() {
/*  79 */     this(50.0D);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Percentile(double p) {
/*  89 */     setQuantile(p);
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double evaluate(double[] values, double p) {
/* 119 */     test(values, 0, 0);
/* 120 */     return evaluate(values, 0, values.length, p);
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
/*     */ 
/*     */   
/*     */   public double evaluate(double[] values, int start, int length) {
/* 148 */     return evaluate(values, start, length, this.quantile);
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
/*     */   public double evaluate(double[] values, int begin, int length, double p) {
/* 184 */     test(values, begin, length);
/*     */     
/* 186 */     if (p > 100.0D || p <= 0.0D) {
/* 187 */       throw new IllegalArgumentException("invalid quantile value: " + p);
/*     */     }
/* 189 */     double n = length;
/* 190 */     if (n == 0.0D) {
/* 191 */       return Double.NaN;
/*     */     }
/* 193 */     if (n == 1.0D) {
/* 194 */       return values[begin];
/*     */     }
/* 196 */     double pos = p * (n + 1.0D) / 100.0D;
/* 197 */     double fpos = Math.floor(pos);
/* 198 */     int intPos = (int)fpos;
/* 199 */     double dif = pos - fpos;
/* 200 */     double[] sorted = new double[length];
/* 201 */     System.arraycopy(values, begin, sorted, 0, length);
/* 202 */     Arrays.sort(sorted);
/*     */     
/* 204 */     if (pos < 1.0D) {
/* 205 */       return sorted[0];
/*     */     }
/* 207 */     if (pos >= n) {
/* 208 */       return sorted[length - 1];
/*     */     }
/* 210 */     double lower = sorted[intPos - 1];
/* 211 */     double upper = sorted[intPos];
/* 212 */     return lower + dif * (upper - lower);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getQuantile() {
/* 222 */     return this.quantile;
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
/*     */   public void setQuantile(double p) {
/* 234 */     if (p <= 0.0D || p > 100.0D) {
/* 235 */       throw new IllegalArgumentException("Illegal quantile value: " + p);
/*     */     }
/* 237 */     this.quantile = p;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\commons-math-1.0.jar!\org\apache\commons\math\stat\descriptive\rank\Percentile.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */