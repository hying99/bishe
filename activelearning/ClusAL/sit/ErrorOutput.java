/*    */ package sit;
/*    */ 
/*    */ import clus.error.MSError;
/*    */ import clus.main.Settings;
/*    */ import java.io.FileNotFoundException;
/*    */ import java.io.IOException;
/*    */ import java.io.PrintWriter;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ErrorOutput
/*    */ {
/*    */   protected PrintWriter m_Writer;
/*    */   protected Settings m_Sett;
/*    */   
/*    */   public ErrorOutput(Settings sett) {
/* 17 */     this.m_Sett = sett;
/*    */     try {
/* 19 */       this.m_Writer = this.m_Sett.getFileAbsoluteWriter(this.m_Sett.getAppName() + ".err");
/* 20 */     } catch (FileNotFoundException e) {
/*    */       
/* 22 */       e.printStackTrace();
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public void writeHeader() throws IOException {
/* 28 */     this.m_Writer.println("@relation experiment");
/* 29 */     this.m_Writer.println("@attribute Dataset {" + this.m_Sett.getDataFile() + "}");
/* 30 */     this.m_Writer.println("@attribute Run numeric");
/* 31 */     this.m_Writer.println("@attribute Fold numeric");
/* 32 */     this.m_Writer.println("@attribute Learner string");
/* 33 */     this.m_Writer.println("@attribute Search string");
/* 34 */     this.m_Writer.println("@attribute MainTarget numeric");
/*    */ 
/*    */     
/* 37 */     this.m_Writer.print("@attribute ");
/* 38 */     String errName = this.m_Sett.getError();
/* 39 */     this.m_Writer.print(errName + " numeric\n");
/*    */ 
/*    */     
/* 42 */     this.m_Writer.println("@attribute SupportTargets string");
/* 43 */     this.m_Writer.println("@attribute Runtime numeric");
/* 44 */     this.m_Writer.println("@data");
/*    */     
/* 46 */     this.m_Writer.flush();
/*    */   }
/*    */   
/*    */   public void addFold(int run, int fold, String learner, String search, String mt, double error, String sts, Long dif) {
/* 50 */     this.m_Writer.println(this.m_Sett.getDataFile() + "," + run + "," + fold + "," + learner + "," + search + "," + mt + "," + error + "," + sts + "," + (dif.longValue() / 1000.0D));
/* 51 */     this.m_Writer.flush();
/*    */   }
/*    */ 
/*    */   
/*    */   public void addFoldAllErrors(int run, int fold, String learner, String search, String mt, MSError error, String sts, Long dif) {
/* 56 */     this.m_Writer.print(this.m_Sett.getDataFile() + "," + run + "," + fold + "," + learner + "," + search + "," + mt + ",");
/*    */     
/* 58 */     for (int i = 0; i < error.getDimension(); i++);
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */     
/* 64 */     this.m_Writer.print(error.getModelError() + ",");
/*    */     
/* 66 */     this.m_Writer.print(sts + "," + (dif.longValue() / 1000.0D));
/* 67 */     this.m_Writer.println();
/* 68 */     this.m_Writer.flush();
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\sit\ErrorOutput.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */