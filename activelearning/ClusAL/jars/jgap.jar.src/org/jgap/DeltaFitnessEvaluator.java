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
/*    */ public class DeltaFitnessEvaluator
/*    */   implements FitnessEvaluator, ICloneable, Comparable
/*    */ {
/*    */   private static final String CVS_REVISION = "$Revision: 1.11 $";
/*    */   
/*    */   public boolean isFitter(double a_fitness_value1, double a_fitness_value2) {
/* 37 */     if (Double.isNaN(a_fitness_value1)) {
/* 38 */       return false;
/*    */     }
/* 40 */     if (!Double.isNaN(a_fitness_value1) && !Double.isNaN(a_fitness_value2)) {
/*    */       
/* 42 */       if (a_fitness_value1 < 0.0D) {
/* 43 */         return false;
/*    */       }
/* 45 */       if (a_fitness_value2 < 0.0D) {
/* 46 */         return true;
/*    */       }
/* 48 */       return (a_fitness_value1 < a_fitness_value2);
/*    */     } 
/* 50 */     return true;
/*    */   }
/*    */   
/*    */   public boolean isFitter(IChromosome a_chrom1, IChromosome a_chrom2) {
/* 54 */     return isFitter(a_chrom1.getFitnessValue(), a_chrom2.getFitnessValue());
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Object clone() {
/* 64 */     return new DeltaFitnessEvaluator();
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
/* 75 */     if (a_other.getClass().equals(getClass())) {
/* 76 */       return 0;
/*    */     }
/*    */     
/* 79 */     return getClass().getName().compareTo(a_other.getClass().getName());
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\DeltaFitnessEvaluator.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */