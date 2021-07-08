/*    */ package org.jgap.gp.impl;
/*    */ 
/*    */ import org.jgap.gp.GPFitnessFunction;
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
/*    */ public class StaticGPFitnessFunction
/*    */   extends GPFitnessFunction
/*    */   implements Comparable
/*    */ {
/*    */   private static final String CVS_REVISION = "$Revision: 1.1 $";
/*    */   private double m_staticFitnessValue;
/*    */   
/*    */   public StaticGPFitnessFunction(double a_staticFitnessValue) {
/* 39 */     this.m_staticFitnessValue = a_staticFitnessValue;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected double evaluate(IGPProgram a_subject) {
/* 50 */     double result = this.m_staticFitnessValue;
/* 51 */     return result;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public double getStaticFitnessValue() {
/* 61 */     return this.m_staticFitnessValue;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void setStaticFitnessValue(double a_staticFitnessValue) {
/* 72 */     this.m_staticFitnessValue = a_staticFitnessValue;
/*    */   }
/*    */   
/*    */   public int hashCode() {
/* 76 */     int result = (new Double(this.m_staticFitnessValue)).hashCode();
/* 77 */     return result;
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
/* 88 */     StaticGPFitnessFunction other = (StaticGPFitnessFunction)a_other;
/* 89 */     if (this.m_staticFitnessValue > other.m_staticFitnessValue) {
/* 90 */       return 1;
/*    */     }
/* 92 */     if (this.m_staticFitnessValue < other.m_staticFitnessValue) {
/* 93 */       return -1;
/*    */     }
/*    */     
/* 96 */     return 0;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\gp\impl\StaticGPFitnessFunction.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */