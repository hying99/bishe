/*    */ package org.jgap.impl;
/*    */ 
/*    */ import org.jgap.BaseRateCalculator;
/*    */ import org.jgap.Configuration;
/*    */ import org.jgap.IChromosome;
/*    */ import org.jgap.InvalidConfigurationException;
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
/*    */ public class DefaultCrossoverRateCalculator
/*    */   extends BaseRateCalculator
/*    */ {
/*    */   private static final String CVS_REVISION = "$Revision: 1.9 $";
/*    */   
/*    */   public DefaultCrossoverRateCalculator(Configuration a_config) throws InvalidConfigurationException {
/* 32 */     super(a_config);
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
/* 66 */     return true;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\impl\DefaultCrossoverRateCalculator.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */