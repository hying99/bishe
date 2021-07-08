/*    */ package org.jgap.impl;
/*    */ 
/*    */ import org.jgap.RandomGenerator;
/*    */ import org.jgap.util.randomX;
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
/*    */ public class HotBitsRandomGenerator
/*    */   implements RandomGenerator
/*    */ {
/*    */   private static final String CVS_REVISION = "$Revision: 1.3 $";
/*    */   private randomX m_randomGenerator;
/*    */   
/*    */   public HotBitsRandomGenerator(randomX a_randomGenerator) {
/* 30 */     this.m_randomGenerator = a_randomGenerator;
/*    */   }
/*    */   
/*    */   public randomX getRandomGenerator() {
/* 34 */     return this.m_randomGenerator;
/*    */   }
/*    */   
/*    */   public byte nextByte() {
/* 38 */     return this.m_randomGenerator.nextByte();
/*    */   }
/*    */   
/*    */   public int nextInt() {
/* 42 */     return this.m_randomGenerator.nextInt();
/*    */   }
/*    */   
/*    */   public int nextInt(int ceiling) {
/* 46 */     return this.m_randomGenerator.nextInt() % ceiling;
/*    */   }
/*    */   
/*    */   public long nextLong() {
/* 50 */     return this.m_randomGenerator.nextLong();
/*    */   }
/*    */   
/*    */   public double nextDouble() {
/* 54 */     return this.m_randomGenerator.nextDouble();
/*    */   }
/*    */   
/*    */   public float nextFloat() {
/* 58 */     return this.m_randomGenerator.nextFloat();
/*    */   }
/*    */   
/*    */   public boolean nextBoolean() {
/* 62 */     return this.m_randomGenerator.nextBit();
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\impl\HotBitsRandomGenerator.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */