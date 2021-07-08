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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class FDistributionImpl
/*     */   extends AbstractContinuousDistribution
/*     */   implements FDistribution, Serializable
/*     */ {
/*     */   static final long serialVersionUID = -8516354193418641566L;
/*     */   private double numeratorDegreesOfFreedom;
/*     */   private double denominatorDegreesOfFreedom;
/*     */   
/*     */   public FDistributionImpl(double numeratorDegreesOfFreedom, double denominatorDegreesOfFreedom) {
/*  50 */     setNumeratorDegreesOfFreedom(numeratorDegreesOfFreedom);
/*  51 */     setDenominatorDegreesOfFreedom(denominatorDegreesOfFreedom);
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
/*  71 */     if (x <= 0.0D) {
/*  72 */       ret = 0.0D;
/*     */     } else {
/*  74 */       double n = getNumeratorDegreesOfFreedom();
/*  75 */       double m = getDenominatorDegreesOfFreedom();
/*     */       
/*  77 */       ret = Beta.regularizedBeta(n * x / (m + n * x), 0.5D * n, 0.5D * m);
/*     */     } 
/*     */ 
/*     */     
/*  81 */     return ret;
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
/*  99 */     if (p == 0.0D) {
/* 100 */       return 0.0D;
/*     */     }
/* 102 */     if (p == 1.0D) {
/* 103 */       return Double.POSITIVE_INFINITY;
/*     */     }
/* 105 */     return super.inverseCumulativeProbability(p);
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
/* 118 */     return 0.0D;
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
/* 131 */     return Double.MAX_VALUE;
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
/* 143 */     return getDenominatorDegreesOfFreedom() / (getDenominatorDegreesOfFreedom() - 2.0D);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setNumeratorDegreesOfFreedom(double degreesOfFreedom) {
/* 154 */     if (degreesOfFreedom <= 0.0D) {
/* 155 */       throw new IllegalArgumentException("degrees of freedom must be positive.");
/*     */     }
/*     */     
/* 158 */     this.numeratorDegreesOfFreedom = degreesOfFreedom;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getNumeratorDegreesOfFreedom() {
/* 166 */     return this.numeratorDegreesOfFreedom;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setDenominatorDegreesOfFreedom(double degreesOfFreedom) {
/* 176 */     if (degreesOfFreedom <= 0.0D) {
/* 177 */       throw new IllegalArgumentException("degrees of freedom must be positive.");
/*     */     }
/*     */     
/* 180 */     this.denominatorDegreesOfFreedom = degreesOfFreedom;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getDenominatorDegreesOfFreedom() {
/* 188 */     return this.denominatorDegreesOfFreedom;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\commons-math-1.0.jar!\org\apache\commons\math\distribution\FDistributionImpl.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */