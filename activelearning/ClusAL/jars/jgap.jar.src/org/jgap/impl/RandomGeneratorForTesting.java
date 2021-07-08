/*     */ package org.jgap.impl;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import org.jgap.RandomGenerator;
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
/*     */ public class RandomGeneratorForTesting
/*     */   implements RandomGenerator, Serializable
/*     */ {
/*     */   private static final String CVS_REVISION = "$Revision: 1.1 $";
/*     */   private long m_nextLong;
/*     */   private double m_nextDouble;
/*     */   private boolean m_nextBoolean;
/*     */   private int[] m_nextIntSequence;
/*     */   private float[] m_nextFloatSequence;
/*     */   private double m_nextGaussian;
/*     */   private int m_intIndex;
/*     */   private int m_floatIndex;
/*     */   
/*     */   public RandomGeneratorForTesting() {}
/*     */   
/*     */   public RandomGeneratorForTesting(int a_nextInt) {
/*  40 */     this();
/*  41 */     setNextInt(a_nextInt);
/*     */   }
/*     */   
/*     */   public RandomGeneratorForTesting(double a_nextDouble) {
/*  45 */     this();
/*  46 */     setNextDouble(a_nextDouble);
/*     */   }
/*     */   
/*     */   public RandomGeneratorForTesting(float a_nextFloat) {
/*  50 */     this();
/*  51 */     setNextFloat(a_nextFloat);
/*     */   }
/*     */   
/*     */   public RandomGeneratorForTesting(long a_nextLong) {
/*  55 */     this();
/*  56 */     setNextLong(a_nextLong);
/*     */   }
/*     */   
/*     */   public RandomGeneratorForTesting(boolean a_nextBoolean) {
/*  60 */     this();
/*  61 */     setNextBoolean(a_nextBoolean);
/*  62 */     setNextInt(1);
/*     */   }
/*     */   
/*     */   public int nextInt() {
/*  66 */     int result = this.m_nextIntSequence[this.m_intIndex++];
/*  67 */     if (this.m_intIndex >= this.m_nextIntSequence.length) {
/*  68 */       this.m_intIndex = 0;
/*     */     }
/*  70 */     return result;
/*     */   }
/*     */   
/*     */   public int nextInt(int a_ceiling) {
/*  74 */     return nextInt() % a_ceiling;
/*     */   }
/*     */   
/*     */   public long nextLong() {
/*  78 */     return this.m_nextLong;
/*     */   }
/*     */   
/*     */   public double nextDouble() {
/*  82 */     return this.m_nextDouble;
/*     */   }
/*     */   
/*     */   public double nextGaussian() {
/*  86 */     return this.m_nextGaussian;
/*     */   }
/*     */   
/*     */   public float nextFloat() {
/*  90 */     float result = this.m_nextFloatSequence[this.m_floatIndex++];
/*  91 */     if (this.m_floatIndex >= this.m_nextFloatSequence.length) {
/*  92 */       this.m_floatIndex = 0;
/*     */     }
/*  94 */     return result;
/*     */   }
/*     */   
/*     */   public boolean nextBoolean() {
/*  98 */     return this.m_nextBoolean;
/*     */   }
/*     */   
/*     */   public void setNextBoolean(boolean a_nextBoolean) {
/* 102 */     this.m_nextBoolean = a_nextBoolean;
/*     */   }
/*     */   
/*     */   public void setNextDouble(double a_nextDouble) {
/* 106 */     this.m_nextDouble = a_nextDouble % 1.0D;
/*     */   }
/*     */   
/*     */   public void setNextGaussian(double a_nextDouble) {
/* 110 */     this.m_nextGaussian = a_nextDouble;
/*     */   }
/*     */   
/*     */   public void setNextFloat(float a_nextFloat) {
/* 114 */     setNextFloatSequence(new float[] { a_nextFloat % 1.0F });
/*     */   }
/*     */   
/*     */   public void setNextInt(int a_nextInt) {
/* 118 */     setNextIntSequence(new int[] { a_nextInt });
/*     */   }
/*     */   
/*     */   public void setNextLong(long a_nextLong) {
/* 122 */     this.m_nextLong = a_nextLong;
/*     */   }
/*     */   
/*     */   public void setNextFloatSequence(float[] a_sequence) {
/* 126 */     this.m_floatIndex = 0;
/* 127 */     this.m_nextFloatSequence = a_sequence;
/*     */   }
/*     */   
/*     */   public void setNextIntSequence(int[] a_sequence) {
/* 131 */     this.m_intIndex = 0;
/* 132 */     this.m_nextIntSequence = a_sequence;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\impl\RandomGeneratorForTesting.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */