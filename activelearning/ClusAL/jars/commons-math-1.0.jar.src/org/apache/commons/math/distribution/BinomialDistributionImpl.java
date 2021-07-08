/*     */ package org.apache.commons.math.distribution;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import org.apache.commons.math.MathException;
/*     */ import org.apache.commons.math.special.Beta;
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
/*     */ public class BinomialDistributionImpl
/*     */   extends AbstractIntegerDistribution
/*     */   implements BinomialDistribution, Serializable
/*     */ {
/*     */   static final long serialVersionUID = 6751309484392813623L;
/*     */   private int numberOfTrials;
/*     */   private double probabilityOfSuccess;
/*     */   
/*     */   public BinomialDistributionImpl(int trials, double p) {
/*  50 */     setNumberOfTrials(trials);
/*  51 */     setProbabilityOfSuccess(p);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getNumberOfTrials() {
/*  59 */     return this.numberOfTrials;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getProbabilityOfSuccess() {
/*  67 */     return this.probabilityOfSuccess;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setNumberOfTrials(int trials) {
/*  77 */     if (trials < 0) {
/*  78 */       throw new IllegalArgumentException("number of trials must be non-negative.");
/*     */     }
/*  80 */     this.numberOfTrials = trials;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setProbabilityOfSuccess(double p) {
/*  90 */     if (p < 0.0D || p > 1.0D) {
/*  91 */       throw new IllegalArgumentException("probability of success must be between 0.0 and 1.0, inclusive.");
/*     */     }
/*  93 */     this.probabilityOfSuccess = p;
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
/* 105 */     return -1;
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
/* 117 */     return getNumberOfTrials();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double cumulativeProbability(int x) throws MathException {
/*     */     double ret;
/* 129 */     if (x < 0) {
/* 130 */       ret = 0.0D;
/* 131 */     } else if (x >= getNumberOfTrials()) {
/* 132 */       ret = 1.0D;
/*     */     } else {
/* 134 */       ret = 1.0D - Beta.regularizedBeta(getProbabilityOfSuccess(), x + 1.0D, (getNumberOfTrials() - x));
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 140 */     return ret;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double probability(int x) {
/*     */     double ret;
/* 151 */     if (x < 0 || x > getNumberOfTrials()) {
/* 152 */       ret = 0.0D;
/*     */     } else {
/* 154 */       ret = MathUtils.binomialCoefficientDouble(getNumberOfTrials(), x) * Math.pow(getProbabilityOfSuccess(), x) * Math.pow(1.0D - getProbabilityOfSuccess(), (getNumberOfTrials() - x));
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 160 */     return ret;
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
/*     */   public int inverseCumulativeProbability(double p) throws MathException {
/* 178 */     if (p == 0.0D) {
/* 179 */       return -1;
/*     */     }
/* 181 */     if (p == 1.0D) {
/* 182 */       return Integer.MAX_VALUE;
/*     */     }
/*     */ 
/*     */     
/* 186 */     return super.inverseCumulativeProbability(p);
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\commons-math-1.0.jar!\org\apache\commons\math\distribution\BinomialDistributionImpl.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */