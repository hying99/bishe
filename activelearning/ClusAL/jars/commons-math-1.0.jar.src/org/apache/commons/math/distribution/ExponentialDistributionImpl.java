/*     */ package org.apache.commons.math.distribution;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import org.apache.commons.math.MathException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ExponentialDistributionImpl
/*     */   extends AbstractContinuousDistribution
/*     */   implements ExponentialDistribution, Serializable
/*     */ {
/*     */   static final long serialVersionUID = 2401296428283614780L;
/*     */   private double mean;
/*     */   
/*     */   public ExponentialDistributionImpl(double mean) {
/*  42 */     setMean(mean);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setMean(double mean) {
/*  51 */     if (mean <= 0.0D) {
/*  52 */       throw new IllegalArgumentException("mean must be positive.");
/*     */     }
/*  54 */     this.mean = mean;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getMean() {
/*  62 */     return this.mean;
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
/*     */   public double cumulativeProbability(double x) throws MathException {
/*     */     double ret;
/*  82 */     if (x <= 0.0D) {
/*  83 */       ret = 0.0D;
/*     */     } else {
/*  85 */       ret = 1.0D - Math.exp(-x / getMean());
/*     */     } 
/*  87 */     return ret;
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
/*     */   public double inverseCumulativeProbability(double p) throws MathException {
/*     */     double ret;
/* 105 */     if (p < 0.0D || p > 1.0D) {
/* 106 */       throw new IllegalArgumentException("probability argument must be between 0 and 1 (inclusive)");
/*     */     }
/* 108 */     if (p == 1.0D) {
/* 109 */       ret = Double.POSITIVE_INFINITY;
/*     */     } else {
/* 111 */       ret = -getMean() * Math.log(1.0D - p);
/*     */     } 
/*     */     
/* 114 */     return ret;
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
/*     */   protected double getDomainLowerBound(double p) {
/* 126 */     return 0.0D;
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
/*     */   protected double getDomainUpperBound(double p) {
/* 141 */     if (p < 0.5D)
/*     */     {
/* 143 */       return getMean();
/*     */     }
/*     */     
/* 146 */     return Double.MAX_VALUE;
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
/*     */   protected double getInitialDomain(double p) {
/* 160 */     if (p < 0.5D)
/*     */     {
/* 162 */       return getMean() * 0.5D;
/*     */     }
/*     */     
/* 165 */     return getMean();
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\commons-math-1.0.jar!\org\apache\commons\math\distribution\ExponentialDistributionImpl.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */