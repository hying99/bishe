/*     */ package org.jgap.impl;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.util.Random;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ public class GaussianRandomGenerator
/*     */   implements RandomGenerator
/*     */ {
/*     */   private static final String CVS_REVISION = "$Revision: 1.21 $";
/*     */   private static final double DELTA = 1.0E-7D;
/*     */   private Random m_rn;
/*     */   private double m_standardDeviation;
/*     */   
/*     */   public GaussianRandomGenerator() {
/*  38 */     this(1.0D);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public GaussianRandomGenerator(double a_standardDeviation) {
/*  48 */     init();
/*  49 */     setGaussianStdDeviation(a_standardDeviation);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void init() {
/*  56 */     this.m_rn = new Random();
/*     */   }
/*     */   
/*     */   public void setGaussianStdDeviation(double a_standardDeviation) {
/*  60 */     if (a_standardDeviation <= 1.0E-7D) {
/*  61 */       throw new IllegalArgumentException("Standard deviation must be greater 0!");
/*     */     }
/*     */     
/*  64 */     this.m_standardDeviation = a_standardDeviation;
/*     */   }
/*     */   
/*     */   public double getGaussianStdDeviation() {
/*  68 */     return this.m_standardDeviation;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int nextInt() {
/*  75 */     return Math.abs(Math.min(2147483646, (int)Math.round(nextGaussian() * 2.147483647E9D)));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int nextInt(int a_ceiling) {
/*  85 */     return Math.min(a_ceiling - 1, (int)Math.round(nextGaussian() * a_ceiling / 11.6D));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long nextLong() {
/*  93 */     long result = Math.min(Long.MAX_VALUE, (long)(nextGaussian() * 9.223372036854776E18D / 11.6D));
/*     */     
/*  95 */     return result;
/*     */   }
/*     */   
/*     */   public double nextDouble() {
/*  99 */     return nextGaussian();
/*     */   }
/*     */   
/*     */   public float nextFloat() {
/* 103 */     return (float)nextGaussian();
/*     */   }
/*     */   
/*     */   public boolean nextBoolean() {
/* 107 */     return (nextGaussian() >= 0.5D);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private double nextGaussian() {
/* 116 */     double r = (this.m_rn.nextGaussian() + 5.8D) / 11.6D;
/* 117 */     return r;
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
/*     */ 
/*     */   
/*     */   private void readObject(ObjectInputStream a_inputStream) throws IOException, ClassNotFoundException {
/* 135 */     a_inputStream.defaultReadObject();
/* 136 */     this.m_rn.setSeed(System.currentTimeMillis());
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\impl\GaussianRandomGenerator.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */