/*    */ package org.jgap.util;
/*    */ 
/*    */ import java.util.Random;
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
/*    */ public class randomJava
/*    */   extends randomX
/*    */ {
/*    */   private Random r;
/* 23 */   private int ibytes = 0;
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private int idat;
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public randomJava() {
/* 34 */     this.r = new Random();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public randomJava(long seed) {
/* 44 */     this.r = new Random(seed);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void setSeed(long seed) {
/* 54 */     setSeed();
/* 55 */     this.r.setSeed(seed);
/* 56 */     this.ibytes = 0;
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
/*    */   public byte nextByte() {
/* 72 */     if (this.ibytes <= 0) {
/* 73 */       this.idat = this.r.nextInt();
/* 74 */       this.ibytes = 4;
/*    */     } 
/* 76 */     byte d = (byte)this.idat;
/* 77 */     this.idat >>= 8;
/* 78 */     this.ibytes--;
/* 79 */     return d;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jga\\util\randomJava.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */