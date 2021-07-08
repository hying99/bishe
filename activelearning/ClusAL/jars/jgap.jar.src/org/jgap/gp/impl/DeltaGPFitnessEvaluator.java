/*    */ package org.jgap.gp.impl;
/*    */ 
/*    */ import org.jgap.gp.IGPFitnessEvaluator;
/*    */ import org.jgap.gp.IGPProgram;
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
/*    */ public class DeltaGPFitnessEvaluator
/*    */   implements IGPFitnessEvaluator
/*    */ {
/*    */   private static final String CVS_REVISION = "$Revision: 1.2 $";
/*    */   
/*    */   public boolean isFitter(double a_fitness_value1, double a_fitness_value2) {
/* 38 */     if (!Double.isNaN(a_fitness_value1) && !Double.isNaN(a_fitness_value2))
/*    */     {
/* 40 */       return (a_fitness_value1 < a_fitness_value2);
/*    */     }
/* 42 */     if (Double.isNaN(a_fitness_value1)) {
/* 43 */       return false;
/*    */     }
/* 45 */     return true;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean isFitter(IGPProgram a_prog1, IGPProgram a_prog2) {
/* 53 */     return isFitter(a_prog1.getFitnessValue(), a_prog2.getFitnessValue());
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean equals(Object a_object) {
/* 61 */     DeltaGPFitnessEvaluator eval = (DeltaGPFitnessEvaluator)a_object;
/* 62 */     return true;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public int compareTo(Object a_object) {
/* 70 */     DeltaGPFitnessEvaluator eval = (DeltaGPFitnessEvaluator)a_object;
/* 71 */     return 0;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\gp\impl\DeltaGPFitnessEvaluator.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */