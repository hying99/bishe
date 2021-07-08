/*     */ package org.apache.commons.math.distribution;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DistributionFactoryImpl
/*     */   extends DistributionFactory
/*     */ {
/*     */   public ChiSquaredDistribution createChiSquareDistribution(double degreesOfFreedom) {
/*  42 */     return new ChiSquaredDistributionImpl(degreesOfFreedom);
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
/*     */   public GammaDistribution createGammaDistribution(double alpha, double beta) {
/*  55 */     return new GammaDistributionImpl(alpha, beta);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TDistribution createTDistribution(double degreesOfFreedom) {
/*  65 */     return new TDistributionImpl(degreesOfFreedom);
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
/*     */   public FDistribution createFDistribution(double numeratorDegreesOfFreedom, double denominatorDegreesOfFreedom) {
/*  78 */     return new FDistributionImpl(numeratorDegreesOfFreedom, denominatorDegreesOfFreedom);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ExponentialDistribution createExponentialDistribution(double mean) {
/*  89 */     return new ExponentialDistributionImpl(mean);
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
/*     */   public BinomialDistribution createBinomialDistribution(int numberOfTrials, double probabilityOfSuccess) {
/* 102 */     return new BinomialDistributionImpl(numberOfTrials, probabilityOfSuccess);
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
/*     */   public HypergeometricDistribution createHypergeometricDistribution(int populationSize, int numberOfSuccesses, int sampleSize) {
/* 117 */     return new HypergeometricDistributionImpl(populationSize, numberOfSuccesses, sampleSize);
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
/*     */   public NormalDistribution createNormalDistribution(double mean, double sd) {
/* 130 */     return new NormalDistributionImpl(mean, sd);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public NormalDistribution createNormalDistribution() {
/* 140 */     return new NormalDistributionImpl();
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
/*     */   public PoissonDistribution createPoissonDistribution(double lambda) {
/* 154 */     return new PoissonDistributionImpl(lambda);
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\commons-math-1.0.jar!\org\apache\commons\math\distribution\DistributionFactoryImpl.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */