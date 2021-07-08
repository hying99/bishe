/*     */ package org.apache.commons.math.stat.descriptive.moment;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Kurtosis
/*     */   extends AbstractStorelessUnivariateStatistic
/*     */ {
/*     */   static final long serialVersionUID = 2784465764798260919L;
/*     */   protected FourthMoment moment;
/*     */   protected boolean incMoment;
/*     */   
/*     */   public Kurtosis() {
/*  60 */     this.incMoment = true;
/*  61 */     this.moment = new FourthMoment();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Kurtosis(FourthMoment m4) {
/*  70 */     this.incMoment = false;
/*  71 */     this.moment = m4;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void increment(double d) {
/*  78 */     if (this.incMoment) {
/*  79 */       this.moment.increment(d);
/*     */     } else {
/*  81 */       throw new IllegalStateException("Statistics constructed from external moments cannot be incremented");
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getResult() {
/*  90 */     double kurtosis = Double.NaN;
/*  91 */     if (this.moment.getN() > 3L) {
/*  92 */       double variance = this.moment.m2 / (this.moment.n - 1L);
/*  93 */       if (this.moment.n <= 3L || variance < 1.0E-19D) {
/*  94 */         kurtosis = 0.0D;
/*     */       } else {
/*  96 */         double n = this.moment.n;
/*  97 */         kurtosis = (n * (n + 1.0D) * this.moment.m4 - 3.0D * this.moment.m2 * this.moment.m2 * (n - 1.0D)) / (n - 1.0D) * (n - 2.0D) * (n - 3.0D) * variance * variance;
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 103 */     return kurtosis;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void clear() {
/* 110 */     if (this.incMoment) {
/* 111 */       this.moment.clear();
/*     */     } else {
/* 113 */       throw new IllegalStateException("Statistics constructed from external moments cannot be cleared");
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getN() {
/* 122 */     return this.moment.getN();
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
/* 145 */     double kurt = Double.NaN;
/*     */     
/* 147 */     if (test(values, begin, length) && length > 3) {
/*     */ 
/*     */       
/* 150 */       Variance variance = new Variance();
/* 151 */       variance.incrementAll(values, begin, length);
/* 152 */       double mean = variance.moment.m1;
/* 153 */       double stdDev = Math.sqrt(variance.getResult());
/*     */ 
/*     */ 
/*     */       
/* 157 */       double accum3 = 0.0D;
/* 158 */       for (int i = begin; i < begin + length; i++) {
/* 159 */         accum3 += Math.pow(values[i] - mean, 4.0D);
/*     */       }
/* 161 */       accum3 /= Math.pow(stdDev, 4.0D);
/*     */ 
/*     */       
/* 164 */       double n0 = length;
/*     */       
/* 166 */       double coefficientOne = n0 * (n0 + 1.0D) / (n0 - 1.0D) * (n0 - 2.0D) * (n0 - 3.0D);
/*     */       
/* 168 */       double termTwo = 3.0D * Math.pow(n0 - 1.0D, 2.0D) / (n0 - 2.0D) * (n0 - 3.0D);
/*     */ 
/*     */ 
/*     */       
/* 172 */       kurt = coefficientOne * accum3 - termTwo;
/*     */     } 
/* 174 */     return kurt;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\commons-math-1.0.jar!\org\apache\commons\math\stat\descriptive\moment\Kurtosis.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */