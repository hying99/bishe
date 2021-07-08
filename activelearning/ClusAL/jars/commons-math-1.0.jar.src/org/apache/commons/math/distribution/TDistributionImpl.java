/*     */ package org.apache.commons.math.distribution;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import org.apache.commons.math.MathException;
/*     */ import org.apache.commons.math.special.Beta;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class TDistributionImpl
/*     */   extends AbstractContinuousDistribution
/*     */   implements TDistribution, Serializable
/*     */ {
/*     */   static final long serialVersionUID = -5852615386664158222L;
/*     */   private double degreesOfFreedom;
/*     */   
/*     */   public TDistributionImpl(double degreesOfFreedom) {
/*  45 */     setDegreesOfFreedom(degreesOfFreedom);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setDegreesOfFreedom(double degreesOfFreedom) {
/*  53 */     if (degreesOfFreedom <= 0.0D) {
/*  54 */       throw new IllegalArgumentException("degrees of freedom must be positive.");
/*     */     }
/*  56 */     this.degreesOfFreedom = degreesOfFreedom;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getDegreesOfFreedom() {
/*  64 */     return this.degreesOfFreedom;
/*     */   }
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
/*  76 */     if (x == 0.0D) {
/*  77 */       ret = 0.5D;
/*     */     } else {
/*  79 */       double t = Beta.regularizedBeta(getDegreesOfFreedom() / (getDegreesOfFreedom() + x * x), 0.5D * getDegreesOfFreedom(), 0.5D);
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*  84 */       if (x < 0.0D) {
/*  85 */         ret = 0.5D * t;
/*     */       } else {
/*  87 */         ret = 1.0D - 0.5D * t;
/*     */       } 
/*     */     } 
/*     */     
/*  91 */     return ret;
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
/*     */   public double inverseCumulativeProbability(double p) throws MathException {
/* 110 */     if (p == 0.0D) {
/* 111 */       return Double.NEGATIVE_INFINITY;
/*     */     }
/* 113 */     if (p == 1.0D) {
/* 114 */       return Double.POSITIVE_INFINITY;
/*     */     }
/* 116 */     return super.inverseCumulativeProbability(p);
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
/*     */   protected double getDomainLowerBound(double p) {
/* 129 */     return -1.7976931348623157E308D;
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
/*     */   protected double getDomainUpperBound(double p) {
/* 142 */     return Double.MAX_VALUE;
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
/*     */   protected double getInitialDomain(double p) {
/* 154 */     return 0.0D;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\commons-math-1.0.jar!\org\apache\commons\math\distribution\TDistributionImpl.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */