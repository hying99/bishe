/*    */ package org.jgap.util;
/*    */ 
/*    */ import java.io.UnsupportedEncodingException;
/*    */ import org.apache.commons.codec.DecoderException;
/*    */ import org.apache.commons.codec.net.URLCodec;
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
/*    */ public class StringKit
/*    */ {
/*    */   private static final String CVS_REVISION = "$Revision: 1.1 $";
/*    */   
/*    */   public static String encode(String a_string) {
/*    */     try {
/* 37 */       return (new URLCodec()).encode(a_string, "UTF-8");
/* 38 */     } catch (UnsupportedEncodingException uex) {
/* 39 */       throw new Error("UTF-8 encoding should always be supported!");
/*    */     } 
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
/*    */   public static String decode(String a_string) {
/*    */     try {
/* 54 */       return (new URLCodec()).decode(a_string, "UTF-8");
/* 55 */     } catch (UnsupportedEncodingException uex) {
/* 56 */       throw new Error("UTF-8 encoding should always be supported!", uex);
/* 57 */     } catch (DecoderException dex) {
/* 58 */       throw new Error("UTF-8 encoding should always be supported!", dex);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jga\\util\StringKit.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */