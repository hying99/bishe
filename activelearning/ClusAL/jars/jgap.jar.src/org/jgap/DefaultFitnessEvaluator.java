/*    */ package org.jgap;
/*    */ 
/*    */ import org.jgap.util.ICloneable;
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
/*    */ public class DefaultFitnessEvaluator
/*    */   implements FitnessEvaluator, ICloneable, Comparable
/*    */ {
/*    */   private static final String CVS_REVISION = "$Revision: 1.13 $";
/*    */   
/*    */   public boolean isFitter(double a_fitness_value1, double a_fitness_value2) {
/* 38 */     return (a_fitness_value1 > a_fitness_value2);
/*    */   }
/*    */   
/*    */   public boolean isFitter(IChromosome a_chrom1, IChromosome a_chrom2) {
/* 42 */     return isFitter(a_chrom1.getFitnessValue(), a_chrom2.getFitnessValue());
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Object clone() {
/* 52 */     return new DefaultFitnessEvaluator();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public int compareTo(Object a_other) {
/* 63 */     if (a_other.getClass().equals(getClass())) {
/* 64 */       return 0;
/*    */     }
/*    */     
/* 67 */     return getClass().getName().compareTo(a_other.getClass().getName());
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\DefaultFitnessEvaluator.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */