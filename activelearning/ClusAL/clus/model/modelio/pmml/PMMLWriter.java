/*    */ package clus.model.modelio.pmml;
/*    */ 
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
/*    */ 
/*    */ public class PMMLWriter
/*    */ {
/*    */   protected String pmmlcode;
/*    */   
/*    */   public PMMLWriter(String pmml) {
/* 32 */     this.pmmlcode = pmml;
/*    */   }
/*    */   
/*    */   public void write(String fname) throws IOException {
/* 36 */     PrintWriter writer = new PrintWriter(new OutputStreamWriter(System.out));
/*    */     
/* 38 */     writer.flush();
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\model\modelio\pmml\PMMLWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */