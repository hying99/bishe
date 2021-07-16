/*    */ package jeans.io;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.InputStreamReader;
/*    */ import java.io.LineNumberReader;
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
/*    */ public class ConsoleInput
/*    */ {
/* 33 */   protected LineNumberReader m_reader = new LineNumberReader(new InputStreamReader(System.in));
/*    */   protected static ConsoleInput m_instance;
/*    */   
/*    */   public void close() {
/*    */     try {
/* 38 */       this.m_reader.close();
/* 39 */     } catch (IOException iOException) {}
/*    */   }
/*    */   
/*    */   public String readLine() {
/* 43 */     String res = "";
/*    */     try {
/* 45 */       res = this.m_reader.readLine();
/* 46 */     } catch (IOException iOException) {}
/* 47 */     return res;
/*    */   }
/*    */   
/*    */   public static ConsoleInput getInstance() {
/* 51 */     if (m_instance == null) m_instance = new ConsoleInput(); 
/* 52 */     return m_instance;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\jeans\io\ConsoleInput.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */