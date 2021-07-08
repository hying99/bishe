/*     */ package org.apache.commons.math.distribution;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import org.apache.commons.math.MathException;
/*     */ import org.apache.commons.math.special.Erf;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class NormalDistributionImpl
/*     */   extends AbstractContinuousDistribution
/*     */   implements NormalDistribution, Serializable
/*     */ {
/*     */   static final long serialVersionUID = 8589540077390120676L;
/*  37 */   private double mean = 0.0D;
/*     */ 
/*     */   
/*  40 */   private double standardDeviation = 1.0D;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public NormalDistributionImpl(double mean, double sd) {
/*  49 */     setMean(mean);
/*  50 */     setStandardDeviation(sd);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public NormalDistributionImpl() {
/*  58 */     this(0.0D, 1.0D);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getMean() {
/*  66 */     return this.mean;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setMean(double mean) {
/*  74 */     this.mean = mean;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getStandardDeviation() {
/*  82 */     return this.standardDeviation;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setStandardDeviation(double sd) {
/*  91 */     if (sd <= 0.0D) {
/*  92 */       throw new IllegalArgumentException("Standard deviation must be positive.");
/*     */     }
/*     */     
/*  95 */     this.standardDeviation = sd;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double cumulativeProbability(double x) throws MathException {
/* 105 */     return 0.5D * (1.0D + Erf.erf((x - this.mean) / this.standardDeviation * Math.sqrt(2.0D)));
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
/*     */   public double inverseCumulativeProbability(double p) throws MathException {
/* 125 */     if (p == 0.0D) {
/* 126 */       return Double.NEGATIVE_INFINITY;
/*     */     }
/* 128 */     if (p == 1.0D) {
/* 129 */       return Double.POSITIVE_INFINITY;
/*     */     }
/* 131 */     return super.inverseCumulativeProbability(p);
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
/*     */   protected double getDomainLowerBound(double p) {
/*     */     double ret;
/* 146 */     if (p < 0.5D) {
/* 147 */       ret = -1.7976931348623157E308D;
/*     */     } else {
/* 149 */       ret = getMean();
/*     */     } 
/*     */     
/* 152 */     return ret;
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
/*     */   protected double getDomainUpperBound(double p) {
/*     */     double ret;
/* 167 */     if (p < 0.5D) {
/* 168 */       ret = getMean();
/*     */     } else {
/* 170 */       ret = Double.MAX_VALUE;
/*     */     } 
/*     */     
/* 173 */     return ret;
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
/*     */   protected double getInitialDomain(double p) {
/*     */     double ret;
/* 187 */     if (p < 0.5D) {
/* 188 */       ret = getMean() - getStandardDeviation();
/* 189 */     } else if (p > 0.5D) {
/* 190 */       ret = getMean() + getStandardDeviation();
/*     */     } else {
/* 192 */       ret = getMean();
/*     */     } 
/*     */     
/* 195 */     return ret;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\commons-math-1.0.jar!\org\apache\commons\math\distribution\NormalDistributionImpl.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */