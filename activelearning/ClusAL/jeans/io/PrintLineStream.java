/*    */ package jeans.io;
/*    */ 
/*    */ import java.io.FileNotFoundException;
/*    */ import java.io.FileOutputStream;
/*    */ import java.io.OutputStream;
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
/*    */ public class PrintLineStream
/*    */   implements LineStream
/*    */ {
/*    */   PrintWriter m_print;
/*    */   
/*    */   public PrintLineStream(String fname) throws FileNotFoundException {
/* 32 */     this(new FileOutputStream(fname));
/*    */   }
/*    */   
/*    */   public PrintLineStream(OutputStream output) {
/* 36 */     this(new PrintWriter(output));
/*    */   }
/*    */   
/*    */   public PrintLineStream(PrintWriter print) {
/* 40 */     this.m_print = print;
/*    */   }
/*    */   
/*    */   public void flush() {
/* 44 */     this.m_print.flush();
/*    */   }
/*    */   
/*    */   public void addLine(String line) {
/* 48 */     this.m_print.println(line);
/*    */   }
/*    */   
/*    */   public void close() {
/* 52 */     this.m_print.close();
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jeans\io\PrintLineStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */