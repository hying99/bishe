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
/*    */ 
/*    */ public class MiningField
/*    */ {
/*    */   private String name;
/*    */   private String usageType;
/*    */   
/*    */   public MiningField(String $name, String $usageType) {
/* 33 */     this.name = $name;
/* 34 */     this.usageType = $usageType;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public MiningField(String $name) {
/* 40 */     this.name = $name;
/* 41 */     this.usageType = "";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getName() {
/* 47 */     return this.name;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getUsageType() {
/* 53 */     return this.usageType;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void print(PrintWriter outStream) {
/* 59 */     if (this.usageType != "") { outStream.write("<MiningField name=\"" + this.name + "\" usageType=\"" + this.usageType + "\"></MiningField>\n"); }
/* 60 */     else { outStream.write("<MiningField name=\"" + this.name + "\"></MiningField>\n"); }
/*    */   
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\model\pmml\tildepmml\MiningField.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */