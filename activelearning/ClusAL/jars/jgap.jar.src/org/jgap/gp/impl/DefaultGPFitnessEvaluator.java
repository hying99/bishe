/*     */ package org.jgap.gp.impl;
/*     */ 
/*     */ import org.jgap.gp.IGPFitnessEvaluator;
/*     */ import org.jgap.gp.IGPProgram;
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
/*     */ public class DefaultGPFitnessEvaluator
/*     */   implements IGPFitnessEvaluator, Cloneable
/*     */ {
/*     */   private static final String CVS_REVISION = "$Revision: 1.8 $";
/*     */   
/*     */   public boolean isFitter(double a_fitness_value1, double a_fitness_value2) {
/*  38 */     if (!Double.isNaN(a_fitness_value1) && !Double.isNaN(a_fitness_value2))
/*     */     {
/*  40 */       return (a_fitness_value1 > a_fitness_value2);
/*     */     }
/*  42 */     if (Double.isNaN(a_fitness_value1)) {
/*  43 */       return false;
/*     */     }
/*  45 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isFitter(IGPProgram a_prog1, IGPProgram a_prog2) {
/*     */     double d1;
/*     */     double d2;
/*  53 */     if (a_prog1 == null) {
/*  54 */       return false;
/*     */     }
/*  56 */     if (a_prog2 == null) {
/*  57 */       return true;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/*  65 */       d1 = a_prog1.getFitnessValue();
/*     */     }
/*  67 */     catch (IllegalStateException iex) {
/*     */ 
/*     */       
/*  70 */       d1 = Double.NaN;
/*     */     } 
/*     */     try {
/*  73 */       d2 = a_prog2.getFitnessValue();
/*     */     }
/*  75 */     catch (IllegalStateException iex) {
/*     */ 
/*     */       
/*  78 */       d2 = Double.NaN;
/*     */     } 
/*  80 */     return isFitter(d1, d2);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(Object a_object) {
/*  89 */     DefaultGPFitnessEvaluator eval = (DefaultGPFitnessEvaluator)a_object;
/*  90 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int compareTo(Object a_object) {
/*  98 */     DefaultGPFitnessEvaluator eval = (DefaultGPFitnessEvaluator)a_object;
/*  99 */     return 0;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object clone() {
/* 109 */     DefaultGPFitnessEvaluator result = new DefaultGPFitnessEvaluator();
/* 110 */     return result;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\gp\impl\DefaultGPFitnessEvaluator.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */