/*    */ package org.jgap.impl;
/*    */ 
/*    */ import java.util.Random;
/*    */ import org.jgap.FitnessFunction;
/*    */ import org.jgap.IChromosome;
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
/*    */ public class RandomFitnessFunction
/*    */   extends FitnessFunction
/*    */ {
/*    */   private static final String CVS_REVISION = "$Revision: 1.6 $";
/* 31 */   private Random m_rand = new Random();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public double evaluate(IChromosome a_chrom) {
/* 41 */     double result = this.m_rand.nextDouble();
/* 42 */     return result;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\impl\RandomFitnessFunction.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */