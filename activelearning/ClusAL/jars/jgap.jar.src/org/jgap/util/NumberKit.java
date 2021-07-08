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
/*    */ public class NumberKit
/*    */ {
/*    */   private static final String CVS_REVISION = "$Revision: 1.1 $";
/*    */   
/*    */   public static int hexValue(char c) {
/* 32 */     if ('0' <= c && c <= '9') {
/* 33 */       return c - 48;
/*    */     }
/* 35 */     if ('A' <= c && c <= 'F') {
/* 36 */       return c - 65 + 10;
/*    */     }
/* 38 */     if ('a' <= c && c <= 'f') {
/* 39 */       return c - 97 + 10;
/*    */     }
/* 41 */     return -1;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jga\\util\NumberKit.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */