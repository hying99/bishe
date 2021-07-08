/*    */ package org.jgap.util;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class randomMCG
/*    */   extends randomX
/*    */ {
/*    */   long state;
/*    */   
/*    */   public randomMCG() {
/* 40 */     setSeed(System.currentTimeMillis());
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public randomMCG(long seed) throws IllegalArgumentException {
/* 50 */     setSeed(seed);
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
/*    */   public void setSeed(long seed) throws IllegalArgumentException {
/* 64 */     if (seed == 0L) {
/* 65 */       throw new IllegalArgumentException("seed must be nonzero");
/*    */     }
/* 67 */     setSeed();
/* 68 */     this.state = seed & 0xFFFFFFFFL;
/* 69 */     for (int i = 0; i < 11; i++) {
/* 70 */       nextByte();
/*    */     }
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public byte nextByte() {
/* 80 */     this.state = this.state * 16807L & 0x7FFFFFFFL;
/* 81 */     return (byte)(int)(this.state >> 11L & 0xFFL);
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jga\\util\randomMCG.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */