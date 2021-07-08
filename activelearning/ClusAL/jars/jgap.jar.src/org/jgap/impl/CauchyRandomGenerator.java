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
/*     */ 
/*     */ 
/*     */ 
/*     */ public class CauchyRandomGenerator
/*     */   implements RandomGenerator
/*     */ {
/*     */   private static final String CVS_REVISION = "$Revision: 1.15 $";
/*     */   private double m_scale;
/*     */   private double m_location;
/*     */   private Random m_rn;
/*     */   
/*     */   public CauchyRandomGenerator() {
/*  41 */     this(0.0D, 1.0D);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CauchyRandomGenerator(double a_location, double a_scale) {
/*  52 */     this.m_location = a_location;
/*  53 */     this.m_scale = a_scale;
/*  54 */     this.m_rn = new Random();
/*     */   }
/*     */   
/*     */   public int nextInt() {
/*  58 */     return Math.min(2147483646, (int)Math.round(nextCauchy() * 2.147483647E9D));
/*     */   }
/*     */ 
/*     */   
/*     */   public int nextInt(int a_ceiling) {
/*  63 */     return Math.min(a_ceiling - 1, (int)Math.round(nextCauchy() * a_ceiling));
/*     */   }
/*     */ 
/*     */   
/*     */   public long nextLong() {
/*  68 */     return Math.min(9223372036854775806L, Math.round(nextCauchy() * 9.223372036854776E18D));
/*     */   }
/*     */ 
/*     */   
/*     */   public double nextDouble() {
/*  73 */     return nextCauchy();
/*     */   }
/*     */   
/*     */   public float nextFloat() {
/*  77 */     return Math.min(Float.MAX_VALUE, (float)(nextCauchy() * 3.4028234663852886E38D));
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean nextBoolean() {
/*  82 */     return (nextCauchy() >= 0.5D);
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
/*     */   public double nextCauchy() {
/*  94 */     return 0.5D + Math.atan((this.m_rn.nextDouble() - this.m_location) / this.m_scale) / Math.PI;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getCauchyStandardDeviation() {
/* 105 */     return this.m_scale;
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
/* 123 */     a_inputStream.defaultReadObject();
/* 124 */     this.m_rn.setSeed(System.currentTimeMillis());
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\impl\CauchyRandomGenerator.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */