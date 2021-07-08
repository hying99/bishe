/*    */ package clus.util;
/*    */ 
/*    */ import java.io.FileOutputStream;
/*    */ import java.io.IOException;
/*    */ import java.io.OutputStreamWriter;
/*    */ import java.io.PrintWriter;
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
/*    */ public class DebugFile
/*    */ {
/*    */   protected static boolean m_TryCreate;
/*    */   protected static PrintWriter m_Writer;
/*    */   
/*    */   public static void log(String strg) {
/* 33 */     if (!m_TryCreate) makeWriter(); 
/* 34 */     if (m_Writer != null) m_Writer.println(strg); 
/*    */   }
/*    */   
/*    */   public static void close() {
/* 38 */     if (m_Writer != null) m_Writer.close(); 
/*    */   }
/*    */   
/*    */   public static void exit() {
/* 42 */     close();
/* 43 */     System.exit(-1);
/*    */   }
/*    */   
/*    */   protected static PrintWriter makeWriter() {
/* 47 */     m_TryCreate = true;
/*    */     try {
/* 49 */       return new PrintWriter(new OutputStreamWriter(new FileOutputStream("debug.txt")));
/* 50 */     } catch (IOException e) {
/* 51 */       System.err.println("Error creating debug writer");
/* 52 */       return null;
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clu\\util\DebugFile.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */