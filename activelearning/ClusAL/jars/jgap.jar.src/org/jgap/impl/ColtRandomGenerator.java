/*    */ package org.jgap.impl;
/*    */ 
/*    */ import cern.jet.random.Uniform;
/*    */ import org.jgap.RandomGenerator;
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
/*    */ public class ColtRandomGenerator
/*    */   implements RandomGenerator
/*    */ {
/*    */   public int nextInt() {
/* 35 */     return Uniform.staticNextIntFromTo(-2147483648, 2147483647);
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
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public int nextInt(int ceiling) {
/* 51 */     return Uniform.staticNextIntFromTo(0, ceiling);
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
/*    */   
/*    */   public long nextLong() {
/* 64 */     return Uniform.staticNextLongFromTo(Long.MIN_VALUE, Long.MAX_VALUE);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public double nextDouble() {
/* 75 */     return Uniform.staticNextDouble();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public float nextFloat() {
/* 85 */     return Uniform.staticNextFloatFromTo(0.0F, 1.0F);
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
/*    */   
/*    */   public boolean nextBoolean() {
/* 98 */     return Uniform.staticNextBoolean();
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\impl\ColtRandomGenerator.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */