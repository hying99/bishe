/*    */ package org.jgap.util;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.net.URL;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class randomHotBits
/*    */   extends randomX
/*    */ {
/*    */   long state;
/* 19 */   int nuflen = 256; int buflen = 0;
/*    */   byte[] buffer;
/* 21 */   int bufptr = -1;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public randomHotBits() {
/* 28 */     this.buffer = new byte[this.nuflen];
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private void fillBuffer() throws IOException {
/* 36 */     URL u = new URL("http://www.fourmilab.ch/cgi-bin/uncgi/Hotbits?nbytes=128&fmt=bin");
/* 37 */     InputStream s = u.openStream();
/*    */ 
/*    */     
/* 40 */     this.buflen = 0; int l;
/* 41 */     while ((l = s.read()) != -1) {
/* 42 */       this.buffer[this.buflen++] = (byte)l;
/*    */     }
/* 44 */     s.close();
/* 45 */     this.bufptr = 0;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public byte nextByte() {
/*    */     try {
/* 55 */       synchronized (this.buffer) {
/* 56 */         if (this.bufptr < 0 || this.bufptr >= this.buflen) {
/* 57 */           fillBuffer();
/*    */         }
/* 59 */         return this.buffer[this.bufptr++];
/*    */       } 
/* 61 */     } catch (IOException e) {
/* 62 */       throw new RuntimeException("Cannot obtain HotBits");
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jga\\util\randomHotBits.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */