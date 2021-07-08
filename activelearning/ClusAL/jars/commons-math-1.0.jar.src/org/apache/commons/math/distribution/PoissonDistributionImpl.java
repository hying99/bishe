/*     */ package org.apache.commons.math.distribution;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import org.apache.commons.math.MathException;
/*     */ import org.apache.commons.math.special.Gamma;
/*     */ import org.apache.commons.math.util.MathUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PoissonDistributionImpl
/*     */   extends AbstractIntegerDistribution
/*     */   implements PoissonDistribution, Serializable
/*     */ {
/*     */   static final long serialVersionUID = -3349935121172596109L;
/*     */   private double mean;
/*     */   
/*     */   public PoissonDistributionImpl(double p) {
/*  50 */     setMean(p);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getMean() {
/*  59 */     return this.mean;
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
/*     */   public void setMean(double p) {
/*  71 */     if (p <= 0.0D) {
/*  72 */       throw new IllegalArgumentException("The Poisson mean must be positive");
/*     */     }
/*     */     
/*  75 */     this.mean = p;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double probability(int x) {
/*  85 */     if (x < 0 || x == Integer.MAX_VALUE) {
/*  86 */       return 0.0D;
/*     */     }
/*  88 */     return Math.pow(getMean(), x) / MathUtils.factorialDouble(x) * Math.exp(-this.mean);
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
/*     */   public double cumulativeProbability(int x) throws MathException {
/* 101 */     if (x < 0) {
/* 102 */       return 0.0D;
/*     */     }
/* 104 */     if (x == Integer.MAX_VALUE) {
/* 105 */       return 1.0D;
/*     */     }
/* 107 */     return Gamma.regularizedGammaQ(x + 1.0D, this.mean, 1.0E-12D, 2147483647);
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
/*     */   public double normalApproximateProbability(int x) throws MathException {
/* 124 */     NormalDistribution normal = DistributionFactory.newInstance().createNormalDistribution(getMean(), Math.sqrt(getMean()));
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 129 */     return normal.cumulativeProbability(x + 0.5D);
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
/*     */   protected int getDomainLowerBound(double p) {
/* 141 */     return 0;
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
/*     */   protected int getDomainUpperBound(double p) {
/* 153 */     return Integer.MAX_VALUE;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\commons-math-1.0.jar!\org\apache\commons\math\distribution\PoissonDistributionImpl.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */