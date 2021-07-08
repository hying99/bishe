/*     */ package org.apache.commons.math.distribution;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import org.apache.commons.math.MathException;
/*     */ import org.apache.commons.math.special.Gamma;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class GammaDistributionImpl
/*     */   extends AbstractContinuousDistribution
/*     */   implements GammaDistribution, Serializable
/*     */ {
/*     */   static final long serialVersionUID = -3239549463135430361L;
/*     */   private double alpha;
/*     */   private double beta;
/*     */   
/*     */   public GammaDistributionImpl(double alpha, double beta) {
/*  47 */     setAlpha(alpha);
/*  48 */     setBeta(beta);
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
/*     */   public double cumulativeProbability(double x) throws MathException {
/*     */     double ret;
/*  71 */     if (x <= 0.0D) {
/*  72 */       ret = 0.0D;
/*     */     } else {
/*  74 */       ret = Gamma.regularizedGammaP(getAlpha(), x / getBeta());
/*     */     } 
/*     */     
/*  77 */     return ret;
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
/*     */   public double inverseCumulativeProbability(double p) throws MathException {
/*  95 */     if (p == 0.0D) {
/*  96 */       return 0.0D;
/*     */     }
/*  98 */     if (p == 1.0D) {
/*  99 */       return Double.POSITIVE_INFINITY;
/*     */     }
/* 101 */     return super.inverseCumulativeProbability(p);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setAlpha(double alpha) {
/* 110 */     if (alpha <= 0.0D) {
/* 111 */       throw new IllegalArgumentException("alpha must be positive");
/*     */     }
/* 113 */     this.alpha = alpha;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getAlpha() {
/* 121 */     return this.alpha;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setBeta(double beta) {
/* 130 */     if (beta <= 0.0D) {
/* 131 */       throw new IllegalArgumentException("beta must be positive");
/*     */     }
/* 133 */     this.beta = beta;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getBeta() {
/* 141 */     return this.beta;
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
/* 155 */     return Double.MIN_VALUE;
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
/*     */   protected double getDomainUpperBound(double p) {
/*     */     double ret;
/* 174 */     if (p < 0.5D) {
/*     */       
/* 176 */       ret = getAlpha() * getBeta();
/*     */     } else {
/*     */       
/* 179 */       ret = Double.MAX_VALUE;
/*     */     } 
/*     */     
/* 182 */     return ret;
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
/*     */   protected double getInitialDomain(double p) {
/*     */     double ret;
/* 199 */     if (p < 0.5D) {
/*     */       
/* 201 */       ret = getAlpha() * getBeta() * 0.5D;
/*     */     } else {
/*     */       
/* 204 */       ret = getAlpha() * getBeta();
/*     */     } 
/*     */     
/* 207 */     return ret;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\commons-math-1.0.jar!\org\apache\commons\math\distribution\GammaDistributionImpl.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */