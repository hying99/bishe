/*    */ package org.jgap.impl;
/*    */ 
/*    */ import org.jgap.BaseRateCalculator;
/*    */ import org.jgap.Configuration;
/*    */ import org.jgap.IChromosome;
/*    */ import org.jgap.InvalidConfigurationException;
/*    */ import org.jgap.RandomGenerator;
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
/*    */ public class DefaultMutationRateCalculator
/*    */   extends BaseRateCalculator
/*    */ {
/*    */   private static final String CVS_REVISION = "$Revision: 1.17 $";
/*    */   
/*    */   public DefaultMutationRateCalculator(Configuration a_config) throws InvalidConfigurationException {
/* 34 */     super(a_config);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public int calculateCurrentRate() {
/* 46 */     int size = getConfiguration().getChromosomeSize();
/* 47 */     if (size < 1) {
/* 48 */       size = 1;
/*    */     }
/* 50 */     return size;
/*    */   }
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
/*    */   public boolean toBePermutated(IChromosome a_chrom, int a_geneIndex) {
/* 66 */     RandomGenerator generator = getConfiguration().getRandomGenerator();
/* 67 */     return (generator.nextInt(calculateCurrentRate()) == 0);
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\impl\DefaultMutationRateCalculator.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */