/*     */ package org.apache.commons.math.stat.descriptive.moment;
/*     */ 
/*     */ import java.io.Serializable;
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
/*     */ 
/*     */ 
/*     */ public class Variance
/*     */   extends AbstractStorelessUnivariateStatistic
/*     */   implements Serializable
/*     */ {
/*     */   static final long serialVersionUID = -9111962718267217978L;
/*  56 */   protected SecondMoment moment = null;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean incMoment = true;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean isBiasCorrected = true;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Variance() {
/*  77 */     this.moment = new SecondMoment();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Variance(SecondMoment m2) {
/*  87 */     this.incMoment = false;
/*  88 */     this.moment = m2;
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
/*     */   public Variance(boolean isBiasCorrected) {
/* 100 */     this.moment = new SecondMoment();
/* 101 */     this.isBiasCorrected = isBiasCorrected;
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
/*     */   public Variance(boolean isBiasCorrected, SecondMoment m2) {
/* 114 */     this.incMoment = false;
/* 115 */     this.moment = m2;
/* 116 */     this.isBiasCorrected = isBiasCorrected;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void increment(double d) {
/* 123 */     if (this.incMoment) {
/* 124 */       this.moment.increment(d);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getResult() {
/* 132 */     if (this.moment.n == 0L)
/* 133 */       return Double.NaN; 
/* 134 */     if (this.moment.n == 1L) {
/* 135 */       return 0.0D;
/*     */     }
/* 137 */     if (this.isBiasCorrected) {
/* 138 */       return this.moment.m2 / (this.moment.n - 1.0D);
/*     */     }
/* 140 */     return this.moment.m2 / this.moment.n;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getN() {
/* 149 */     return this.moment.getN();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void clear() {
/* 156 */     if (this.incMoment) {
/* 157 */       this.moment.clear();
/*     */     }
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
/*     */   public double evaluate(double[] values) {
/* 178 */     if (values == null) {
/* 179 */       throw new IllegalArgumentException("input values array is null");
/*     */     }
/* 181 */     return evaluate(values, 0, values.length);
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
/*     */   public double evaluate(double[] values, int begin, int length) {
/* 206 */     double var = Double.NaN;
/*     */     
/* 208 */     if (test(values, begin, length)) {
/* 209 */       clear();
/* 210 */       if (length == 1) {
/* 211 */         var = 0.0D;
/* 212 */       } else if (length > 1) {
/* 213 */         Mean mean = new Mean();
/* 214 */         double m = mean.evaluate(values, begin, length);
/* 215 */         var = evaluate(values, m, begin, length);
/*     */       } 
/*     */     } 
/* 218 */     return var;
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
/*     */   public double evaluate(double[] values, double mean, int begin, int length) {
/* 250 */     double var = Double.NaN;
/*     */     
/* 252 */     if (test(values, begin, length)) {
/* 253 */       if (length == 1) {
/* 254 */         var = 0.0D;
/* 255 */       } else if (length > 1) {
/* 256 */         double accum = 0.0D;
/* 257 */         double accum2 = 0.0D;
/* 258 */         for (int i = begin; i < begin + length; i++) {
/* 259 */           accum += Math.pow(values[i] - mean, 2.0D);
/* 260 */           accum2 += values[i] - mean;
/*     */         } 
/* 262 */         if (this.isBiasCorrected) {
/* 263 */           var = (accum - Math.pow(accum2, 2.0D) / length) / (length - 1);
/*     */         } else {
/*     */           
/* 266 */           var = (accum - Math.pow(accum2, 2.0D) / length) / length;
/*     */         } 
/*     */       } 
/*     */     }
/*     */     
/* 271 */     return var;
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
/*     */   public double evaluate(double[] values, double mean) {
/* 300 */     return evaluate(values, mean, 0, values.length);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isBiasCorrected() {
/* 307 */     return this.isBiasCorrected;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setBiasCorrected(boolean isBiasCorrected) {
/* 314 */     this.isBiasCorrected = isBiasCorrected;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\commons-math-1.0.jar!\org\apache\commons\math\stat\descriptive\moment\Variance.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */