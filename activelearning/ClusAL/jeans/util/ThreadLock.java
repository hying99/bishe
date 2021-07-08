/*    */ package jeans.util;
/*    */ 
/*    */ import java.io.PrintStream;
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
/*    */ public class ThreadLock
/*    */ {
/*    */   private boolean lock = false;
/*    */   
/*    */   public synchronized boolean getState() {
/* 32 */     return this.lock;
/*    */   }
/*    */   
/*    */   public synchronized void setState(boolean state) {
/* 36 */     if (state) {
/* 37 */       this.lock = true;
/*    */     } else {
/* 39 */       release();
/*    */     } 
/*    */   }
/*    */   
/*    */   public synchronized void lock() throws InterruptedException {
/* 44 */     this.lock = true;
/* 45 */     wait();
/*    */   }
/*    */   
/*    */   public synchronized void lock(long timeout) throws InterruptedException {
/* 49 */     this.lock = true;
/* 50 */     wait(timeout);
/*    */   }
/*    */   
/*    */   public synchronized void release() {
/* 54 */     if (this.lock) notify(); 
/* 55 */     this.lock = false;
/*    */   }
/*    */   
/*    */   public synchronized void entry() throws InterruptedException {
/* 59 */     boolean lockstate = this.lock;
/* 60 */     this.lock = true;
/* 61 */     if (lockstate) wait(); 
/*    */   }
/*    */   
/*    */   public synchronized boolean tryEntry() {
/* 65 */     boolean lockstate = this.lock;
/* 66 */     this.lock = true;
/* 67 */     return !lockstate;
/*    */   }
/*    */   
/*    */   public synchronized boolean testLock() throws InterruptedException {
/* 71 */     if (this.lock) {
/* 72 */       wait();
/* 73 */       return true;
/*    */     } 
/* 75 */     return false;
/*    */   }
/*    */   
/*    */   public synchronized boolean testLockDebug(String strg, PrintStream output) throws InterruptedException {
/* 79 */     if (this.lock) {
/* 80 */       output.println(strg + " waits..");
/* 81 */       wait();
/* 82 */       output.println(strg + " resumes..");
/* 83 */       return true;
/*    */     } 
/* 85 */     return false;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jean\\util\ThreadLock.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */