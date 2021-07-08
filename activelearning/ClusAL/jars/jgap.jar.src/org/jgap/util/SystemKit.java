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
/*    */ public class SystemKit
/*    */ {
/*    */   private static final String CVS_REVISION = "$Revision: 1.5 $";
/*    */   
/*    */   public static double getTotalMemoryMB() {
/* 29 */     return getTotalMemoryKB() / 1024.0D;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static double getTotalMemoryKB() {
/* 39 */     return (Runtime.getRuntime().totalMemory() / 1024L);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static double getFreeMemoryMB() {
/* 49 */     return getFreeMemoryKB() / 1024.0D;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static double getFreeMemoryKB() {
/* 59 */     return (Runtime.getRuntime().freeMemory() / 1024L);
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
/*    */   public static String niceMemory(double a_mem) {
/* 71 */     String freeMB = "" + a_mem;
/* 72 */     int index = freeMB.indexOf('.');
/* 73 */     int len = freeMB.length();
/* 74 */     if (len - index > 2) {
/* 75 */       freeMB = freeMB.substring(0, index + 2);
/*    */     }
/* 77 */     return freeMB;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jga\\util\SystemKit.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */