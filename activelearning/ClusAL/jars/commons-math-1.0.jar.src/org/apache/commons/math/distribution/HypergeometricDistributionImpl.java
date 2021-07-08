/*     */ package org.apache.commons.math.distribution;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import org.apache.commons.math.MathException;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class HypergeometricDistributionImpl
/*     */   extends AbstractIntegerDistribution
/*     */   implements HypergeometricDistribution, Serializable
/*     */ {
/*     */   static final long serialVersionUID = -436928820673516179L;
/*     */   private int numberOfSuccesses;
/*     */   private int populationSize;
/*     */   private int sampleSize;
/*     */   
/*     */   public HypergeometricDistributionImpl(int populationSize, int numberOfSuccesses, int sampleSize) {
/*  55 */     if (numberOfSuccesses > populationSize) {
/*  56 */       throw new IllegalArgumentException("number of successes must be less than or equal to population size");
/*     */     }
/*     */     
/*  59 */     if (sampleSize > populationSize) {
/*  60 */       throw new IllegalArgumentException("sample size must be less than or equal to population size");
/*     */     }
/*     */     
/*  63 */     setPopulationSize(populationSize);
/*  64 */     setSampleSize(sampleSize);
/*  65 */     setNumberOfSuccesses(numberOfSuccesses);
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
/*     */   public double cumulativeProbability(int x) throws MathException {
/*     */     double ret;
/*  78 */     int n = getPopulationSize();
/*  79 */     int m = getNumberOfSuccesses();
/*  80 */     int k = getSampleSize();
/*     */     
/*  82 */     int[] domain = getDomain(n, m, k);
/*  83 */     if (x < domain[0]) {
/*  84 */       ret = 0.0D;
/*  85 */     } else if (x >= domain[1]) {
/*  86 */       ret = 1.0D;
/*     */     } else {
/*  88 */       ret = 0.0D;
/*  89 */       for (int i = domain[0]; i <= x; i++) {
/*  90 */         ret += probability(n, m, k, i);
/*     */       }
/*     */     } 
/*     */     
/*  94 */     return ret;
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
/*     */   private int[] getDomain(int n, int m, int k) {
/* 106 */     return new int[] { getLowerDomain(n, m, k), getUpperDomain(m, k) };
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
/*     */   protected int getDomainLowerBound(double p) {
/* 121 */     return getLowerDomain(getPopulationSize(), getNumberOfSuccesses(), getSampleSize());
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
/*     */   protected int getDomainUpperBound(double p) {
/* 134 */     return getUpperDomain(getSampleSize(), getNumberOfSuccesses());
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
/*     */   private int getLowerDomain(int n, int m, int k) {
/* 146 */     return Math.max(0, m - n - k);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getNumberOfSuccesses() {
/* 154 */     return this.numberOfSuccesses;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getPopulationSize() {
/* 162 */     return this.populationSize;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getSampleSize() {
/* 170 */     return this.sampleSize;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private int getUpperDomain(int m, int k) {
/* 181 */     return Math.min(k, m);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double probability(int x) {
/*     */     double ret;
/* 193 */     int n = getPopulationSize();
/* 194 */     int m = getNumberOfSuccesses();
/* 195 */     int k = getSampleSize();
/*     */     
/* 197 */     int[] domain = getDomain(n, m, k);
/* 198 */     if (x < domain[0] || x > domain[1]) {
/* 199 */       ret = 0.0D;
/*     */     } else {
/* 201 */       ret = probability(n, m, k, x);
/*     */     } 
/*     */     
/* 204 */     return ret;
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
/*     */   private double probability(int n, int m, int k, int x) {
/* 218 */     return Math.exp(MathUtils.binomialCoefficientLog(m, x) + MathUtils.binomialCoefficientLog(n - m, k - x) - MathUtils.binomialCoefficientLog(n, k));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setNumberOfSuccesses(int num) {
/* 229 */     if (num < 0) {
/* 230 */       throw new IllegalArgumentException("number of successes must be non-negative.");
/*     */     }
/*     */     
/* 233 */     this.numberOfSuccesses = num;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setPopulationSize(int size) {
/* 242 */     if (size <= 0) {
/* 243 */       throw new IllegalArgumentException("population size must be positive.");
/*     */     }
/*     */     
/* 246 */     this.populationSize = size;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setSampleSize(int size) {
/* 255 */     if (size < 0) {
/* 256 */       throw new IllegalArgumentException("sample size must be non-negative.");
/*     */     }
/*     */     
/* 259 */     this.sampleSize = size;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\commons-math-1.0.jar!\org\apache\commons\math\distribution\HypergeometricDistributionImpl.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */