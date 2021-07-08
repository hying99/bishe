/*    */ package org.jgap.util;
/*    */ 
/*    */ import java.net.InetAddress;
/*    */ import java.net.UnknownHostException;
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
/*    */ public final class NetworkKit
/*    */ {
/*    */   private static final String CVS_REVISION = "$Revision: 1.2 $";
/*    */   
/*    */   public static String getLocalIPAddress() throws UnknownHostException {
/* 41 */     return InetAddress.getLocalHost().getHostAddress();
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
/*    */   public static String getLocalHostName() throws UnknownHostException {
/* 53 */     return InetAddress.getLocalHost().getHostName();
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jga\\util\NetworkKit.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */