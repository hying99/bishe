/*    */ package jeans.util;
/*    */ 
/*    */ import java.io.File;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStreamReader;
/*    */ import java.io.Reader;
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
/*    */ public class RunProcess
/*    */ {
/*    */   protected Process m_Process;
/*    */   
/*    */   public void run(String pname, String wdir) throws IOException {
/* 32 */     Runtime rt = Runtime.getRuntime();
/* 33 */     File dir = (wdir != null) ? new File(wdir) : null;
/* 34 */     this.m_Process = rt.exec(pname, (String[])null, dir);
/*    */   }
/*    */   
/*    */   public Reader getOutputReader() {
/* 38 */     return new InputStreamReader(this.m_Process.getInputStream());
/*    */   }
/*    */   
/*    */   public MStreamTokenizer getOutputTokenizer() {
/* 42 */     return new MStreamTokenizer(getOutputReader());
/*    */   }
/*    */   
/*    */   public void waitFor() throws InterruptedException {
/* 46 */     this.m_Process.waitFor();
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\jean\\util\RunProcess.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */