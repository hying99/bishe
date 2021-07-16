/*    */ package clus.model.pmml.tildepmml;
/*    */ 
/*    */ import java.io.PrintWriter;
/*    */ import java.util.Vector;
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
/*    */ public class DataFieldCategorical
/*    */   extends DataField
/*    */ {
/*    */   private Vector values;
/*    */   
/*    */   public DataFieldCategorical(String $name, Vector $values) {
/* 33 */     this.name = $name;
/* 34 */     this.opType = "categorical";
/* 35 */     this.values = $values;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void addValue(String value) {
/* 41 */     this.values.add(value);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public int getNbOfValues() {
/* 47 */     return this.values.size();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void print(PrintWriter outStream) {
/* 54 */     int pointer = 0;
/*    */     
/* 56 */     outStream.write("<DataField name=\"" + this.name + "\" optype=\"categorical\"/>\n");
/*    */     
/* 58 */     int counter = this.values.size();
/*    */     
/* 60 */     while (counter > 0) {
/*    */       
/* 62 */       outStream.write(" <Value value=\"" + this.values.elementAt(pointer) + "\"/>\n");
/*    */       
/* 64 */       pointer++;
/* 65 */       counter--;
/*    */     } 
/*    */     
/* 68 */     outStream.write("</DataField>");
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\model\pmml\tildepmml\DataFieldCategorical.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */