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
/*     */ 
/*     */ public class ChiSquaredDistributionImpl
/*     */   extends AbstractContinuousDistribution
/*     */   implements ChiSquaredDistribution, Serializable
/*     */ {
/*     */   static final long serialVersionUID = -8352658048349159782L;
/*     */   private GammaDistribution gamma;
/*     */   
/*     */   public ChiSquaredDistributionImpl(double degreesOfFreedom) {
/*  43 */     setGamma(DistributionFactory.newInstance().createGammaDistribution(degreesOfFreedom / 2.0D, 2.0D));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setDegreesOfFreedom(double degreesOfFreedom) {
/*  52 */     getGamma().setAlpha(degreesOfFreedom / 2.0D);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getDegreesOfFreedom() {
/*  60 */     return getGamma().getAlpha() * 2.0D;
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
/*  71 */     return getGamma().cumulativeProbability(x);
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
/*  89 */     if (p == 0.0D) {
/*  90 */       return 0.0D;
/*     */     }
/*  92 */     if (p == 1.0D) {
/*  93 */       return Double.POSITIVE_INFINITY;
/*     */     }
/*  95 */     return super.inverseCumulativeProbability(p);
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
/* 108 */     return Double.MIN_VALUE * getGamma().getBeta();
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
/*     */   protected double getDomainUpperBound(double p) {
/*     */     double ret;
/* 126 */     if (p < 0.5D) {
/*     */       
/* 128 */       ret = getDegreesOfFreedom();
/*     */     } else {
/*     */       
/* 131 */       ret = Double.MAX_VALUE;
/*     */     } 
/*     */     
/* 134 */     return ret;
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
/* 151 */     if (p < 0.5D) {
/*     */       
/* 153 */       ret = getDegreesOfFreedom() * 0.5D;
/*     */     } else {
/*     */       
/* 156 */       ret = getDegreesOfFreedom();
/*     */     } 
/*     */     
/* 159 */     return ret;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void setGamma(GammaDistribution gamma) {
/* 167 */     this.gamma = gamma;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private GammaDistribution getGamma() {
/* 175 */     return this.gamma;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\commons-math-1.0.jar!\org\apache\commons\math\distribution\ChiSquaredDistributionImpl.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */