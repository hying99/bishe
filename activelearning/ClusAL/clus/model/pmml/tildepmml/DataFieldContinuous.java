/*    */ package clus.model.pmml.tildepmml;
/*    */ 
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
/*    */ 
/*    */ public class DataFieldContinuous
/*    */   extends DataField
/*    */ {
/*    */   public DataFieldContinuous(String $name) {
/* 30 */     this.name = $name;
/* 31 */     this.opType = "continuous";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void print(PrintWriter outStream) {
/* 37 */     outStream.write("<DataField name=\"" + this.name + "\" optype=\"continuous\"/>\n");
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\model\pmml\tildepmml\DataFieldContinuous.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */