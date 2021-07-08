/*    */ package org.apache.commons.math.distribution;
/*    */ 
/*    */ import org.apache.commons.discovery.tools.DiscoverClass;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class DistributionFactory
/*    */ {
/*    */   public static DistributionFactory newInstance() {
/* 58 */     DistributionFactory factory = null;
/*    */     try {
/* 60 */       DiscoverClass dc = new DiscoverClass();
/* 61 */       factory = (DistributionFactory)dc.newInstance(DistributionFactory.class, "org.apache.commons.math.distribution.DistributionFactoryImpl");
/*    */     
/*    */     }
/* 64 */     catch (Throwable t) {
/* 65 */       return new DistributionFactoryImpl();
/*    */     } 
/* 67 */     return factory;
/*    */   }
/*    */   
/*    */   public abstract BinomialDistribution createBinomialDistribution(int paramInt, double paramDouble);
/*    */   
/*    */   public abstract ChiSquaredDistribution createChiSquareDistribution(double paramDouble);
/*    */   
/*    */   public abstract ExponentialDistribution createExponentialDistribution(double paramDouble);
/*    */   
/*    */   public abstract FDistribution createFDistribution(double paramDouble1, double paramDouble2);
/*    */   
/*    */   public abstract GammaDistribution createGammaDistribution(double paramDouble1, double paramDouble2);
/*    */   
/*    */   public abstract TDistribution createTDistribution(double paramDouble);
/*    */   
/*    */   public abstract HypergeometricDistribution createHypergeometricDistribution(int paramInt1, int paramInt2, int paramInt3);
/*    */   
/*    */   public abstract NormalDistribution createNormalDistribution(double paramDouble1, double paramDouble2);
/*    */   
/*    */   public abstract NormalDistribution createNormalDistribution();
/*    */   
/*    */   public abstract PoissonDistribution createPoissonDistribution(double paramDouble);
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\commons-math-1.0.jar!\org\apache\commons\math\distribution\DistributionFactory.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */