/*    */ package org.jgap.gp;
/*    */ 
/*    */ import java.io.Serializable;
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
/*    */ public abstract class GPFitnessFunction
/*    */   implements Serializable
/*    */ {
/*    */   private static final String CVS_REVISION = "$Revision: 1.8 $";
/*    */   public static final double NO_FITNESS_VALUE = -1.0D;
/*    */   public static final double MAX_FITNESS_VALUE = 8.988465674311579E307D;
/* 30 */   private double m_lastComputedFitnessValue = -1.0D;
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
/*    */   public void GPFitnessFunction() {}
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
/*    */   public final double getFitnessValue(IGPProgram a_program) {
/* 58 */     double fitnessValue = evaluate(a_program);
/* 59 */     if (fitnessValue < 0.0D) {
/* 60 */       throw new RuntimeException("Fitness values must be positive! Received value: " + fitnessValue);
/*    */     }
/*    */ 
/*    */     
/* 64 */     this.m_lastComputedFitnessValue = fitnessValue;
/* 65 */     return fitnessValue;
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
/*    */   public double getLastComputedFitnessValue() {
/* 77 */     return this.m_lastComputedFitnessValue;
/*    */   }
/*    */   
/*    */   protected abstract double evaluate(IGPProgram paramIGPProgram);
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\gp\GPFitnessFunction.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */