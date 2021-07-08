/*     */ package org.jgap;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import org.jgap.util.CloneException;
/*     */ import org.jgap.util.ICloneable;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class FitnessFunction
/*     */   implements Serializable, ICloneable
/*     */ {
/*     */   private static final String CVS_REVISION = "$Revision: 1.22 $";
/*     */   public static final double NO_FITNESS_VALUE = -1.0D;
/*     */   public static final double DELTA = 1.0E-7D;
/*  47 */   private double m_lastComputedFitnessValue = -1.0D;
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
/*     */   public double getFitnessValue(IChromosome a_subject) {
/*  66 */     double fitnessValue = evaluate(a_subject);
/*  67 */     if (fitnessValue < 0.0D) {
/*  68 */       throw new RuntimeException("Fitness values must be positive! Received value: " + fitnessValue);
/*     */     }
/*     */ 
/*     */     
/*  72 */     this.m_lastComputedFitnessValue = fitnessValue;
/*  73 */     return fitnessValue;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getLastComputedFitnessValue() {
/*  85 */     return this.m_lastComputedFitnessValue;
/*     */   }
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
/*     */   protected abstract double evaluate(IChromosome paramIChromosome);
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
/*     */   public Object clone() {
/*     */     try {
/* 115 */       return super.clone();
/* 116 */     } catch (CloneNotSupportedException cex) {
/* 117 */       throw new CloneException(cex);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\FitnessFunction.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */