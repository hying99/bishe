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
/*     */ public class Skewness
/*     */   extends AbstractStorelessUnivariateStatistic
/*     */   implements Serializable
/*     */ {
/*     */   static final long serialVersionUID = 7101857578996691352L;
/*  45 */   protected ThirdMoment moment = null;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean incMoment;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Skewness() {
/*  59 */     this.incMoment = true;
/*  60 */     this.moment = new ThirdMoment();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Skewness(ThirdMoment m3) {
/*  68 */     this.incMoment = false;
/*  69 */     this.moment = m3;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void increment(double d) {
/*  76 */     if (this.incMoment) {
/*  77 */       this.moment.increment(d);
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
/*     */   public double getResult() {
/*  90 */     if (this.moment.n < 3L) {
/*  91 */       return Double.NaN;
/*     */     }
/*  93 */     double variance = this.moment.m2 / (this.moment.n - 1L);
/*  94 */     double skewness = Double.NaN;
/*  95 */     if (variance < 1.0E-19D) {
/*  96 */       skewness = 0.0D;
/*     */     } else {
/*  98 */       double n0 = this.moment.getN();
/*  99 */       skewness = n0 * this.moment.m3 / (n0 - 1.0D) * (n0 - 2.0D) * Math.sqrt(variance) * variance;
/*     */     } 
/*     */     
/* 102 */     return skewness;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getN() {
/* 109 */     return this.moment.getN();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void clear() {
/* 116 */     if (this.incMoment) {
/* 117 */       this.moment.clear();
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
/*     */ 
/*     */ 
/*     */   
/*     */   public double evaluate(double[] values, int begin, int length) {
/* 141 */     double skew = Double.NaN;
/*     */     
/* 143 */     if (test(values, begin, length) && length > 2) {
/* 144 */       Mean mean = new Mean();
/*     */       
/* 146 */       double m = mean.evaluate(values, begin, length);
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 151 */       double accum = 0.0D;
/* 152 */       double accum2 = 0.0D;
/* 153 */       for (int i = begin; i < begin + length; i++) {
/* 154 */         accum += Math.pow(values[i] - m, 2.0D);
/* 155 */         accum2 += values[i] - m;
/*     */       } 
/* 157 */       double stdDev = Math.sqrt((accum - Math.pow(accum2, 2.0D) / length) / (length - 1));
/*     */ 
/*     */       
/* 160 */       double accum3 = 0.0D;
/* 161 */       for (int j = begin; j < begin + length; j++) {
/* 162 */         accum3 += Math.pow(values[j] - m, 3.0D);
/*     */       }
/* 164 */       accum3 /= Math.pow(stdDev, 3.0D);
/*     */ 
/*     */       
/* 167 */       double n0 = length;
/*     */ 
/*     */       
/* 170 */       skew = n0 / (n0 - 1.0D) * (n0 - 2.0D) * accum3;
/*     */     } 
/* 172 */     return skew;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\commons-math-1.0.jar!\org\apache\commons\math\stat\descriptive\moment\Skewness.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */