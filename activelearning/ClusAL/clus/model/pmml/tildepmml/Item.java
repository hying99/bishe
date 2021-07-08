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
/*    */ public class Item
/*    */ {
/*    */   private int id;
/*    */   private boolean fieldOrValue;
/*    */   private String field;
/*    */   private String value;
/*    */   
/*    */   public Item(int $id, boolean $fieldOrValue, String $fieldOrValueString) {
/* 36 */     this.id = $id;
/*    */     
/* 38 */     if ($fieldOrValue) { this.field = ""; this.value = $fieldOrValueString; }
/* 39 */     else { this.value = $fieldOrValueString; this.field = ""; }
/*    */   
/*    */   }
/*    */ 
/*    */   
/*    */   public int getId() {
/* 45 */     return this.id;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean isField() {
/* 51 */     return this.fieldOrValue;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getField() {
/* 57 */     return this.field;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getValue() {
/* 63 */     return this.value;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void print(PrintWriter outStream) {
/* 69 */     if (this.fieldOrValue)
/* 70 */     { outStream.write("<Item id=\"" + this.id + "\" field=\"" + this.field + "\"/>\n"); }
/* 71 */     else { outStream.write("<Item id=\"" + this.id + "\" value=\"" + this.value + "\"/>\n"); }
/*    */   
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\model\pmml\tildepmml\Item.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */