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
/*    */ public class randomLCG
/*    */   extends randomX
/*    */ {
/*    */   long state;
/*    */   
/*    */   public randomLCG() {
/* 32 */     setSeed(System.currentTimeMillis());
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public randomLCG(long seed) {
/* 42 */     setSeed(seed);
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
/*    */   public void setSeed(long seed) {
/* 54 */     setSeed();
/* 55 */     this.state = seed & 0xFFFFFFFFL;
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
/*    */   public byte nextByte() {
/* 69 */     this.state = this.state * 1103515245L + 12345L & 0x7FFFFFFFL;
/* 70 */     return (byte)(int)(this.state >> 11L & 0xFFL);
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jga\\util\randomLCG.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */