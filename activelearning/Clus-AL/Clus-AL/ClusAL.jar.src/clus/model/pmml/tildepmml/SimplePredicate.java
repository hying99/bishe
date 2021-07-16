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
/*    */ 
/*    */ public class SimplePredicate
/*    */ {
/*    */   private String field;
/*    */   private String operator;
/*    */   private String value;
/*    */   
/*    */   public SimplePredicate(String $field, String $operator, String $value) {
/* 35 */     this.field = $field;
/* 36 */     this.operator = $operator;
/* 37 */     this.value = $value;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getField() {
/* 44 */     return this.field;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getOperator() {
/* 50 */     return this.operator;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getValue() {
/* 56 */     return this.value;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void print(PrintWriter outStream) {
/* 62 */     outStream.write("<SimplePredicate field=\"" + this.field + "\" operator=\"" + this.operator + "\" value=\"" + this.value + "\"/>\n");
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\model\pmml\tildepmml\SimplePredicate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */