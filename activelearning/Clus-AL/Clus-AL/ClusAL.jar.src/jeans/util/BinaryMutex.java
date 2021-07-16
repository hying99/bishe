/*    */ package jeans.util;
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
/*    */ public class BinaryMutex
/*    */ {
/*    */   private boolean lock = false;
/*    */   
/*    */   public synchronized void enter() throws InterruptedException {
/* 30 */     if (this.lock) wait(); 
/* 31 */     this.lock = true;
/*    */   }
/*    */   
/*    */   public synchronized void leave() {
/* 35 */     this.lock = false;
/* 36 */     notify();
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\jean\\util\BinaryMutex.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */