/*    */ package jeans.io;
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
/*    */ public class MyFile
/*    */ {
/*    */   protected PrintWriter m_Writer;
/*    */   protected boolean m_Enable;
/*    */   
/*    */   public MyFile(String fname) {
/* 33 */     this(fname, true);
/*    */   }
/*    */   
/*    */   public MyFile(String fname, boolean enable) {
/* 37 */     if (!enable)
/*    */       return;  try {
/* 39 */       this.m_Writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(fname)));
/* 40 */       this.m_Enable = true;
/* 41 */     } catch (IOException e) {
/* 42 */       System.err.println("Error creating file: " + fname);
/*    */     } 
/*    */   }
/*    */   
/*    */   public boolean isEnabled() {
/* 47 */     return this.m_Enable;
/*    */   }
/*    */   
/*    */   public void setEnabled(boolean enable) {
/* 51 */     this.m_Enable = enable;
/*    */   }
/*    */   
/*    */   public PrintWriter getWriter() {
/* 55 */     return this.m_Writer;
/*    */   }
/*    */   
/*    */   public void log(String strg) {
/* 59 */     if (this.m_Enable) this.m_Writer.println(strg); 
/*    */   }
/*    */   
/*    */   public void log() {
/* 63 */     if (this.m_Enable) this.m_Writer.println(); 
/*    */   }
/*    */   
/*    */   public void close() {
/* 67 */     if (this.m_Writer != null) {
/* 68 */       this.m_Writer.close();
/* 69 */       this.m_Enable = false;
/* 70 */       this.m_Writer = null;
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\jeans\io\MyFile.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */