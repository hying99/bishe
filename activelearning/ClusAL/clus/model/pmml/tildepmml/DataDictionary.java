/*    */ package clus.model.pmml.tildepmml;
/*    */ 
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
/*    */ 
/*    */ public class DataDictionary
/*    */ {
/*    */   private int nbOfFields;
/*    */   private Vector dataFields;
/*    */   
/*    */   public DataDictionary(Vector $dataFields) {
/* 33 */     this.nbOfFields = $dataFields.size();
/* 34 */     this.dataFields = $dataFields;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public int getNbOfFields() {
/* 41 */     return this.nbOfFields;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void addDataField(DataField $dataField) {
/* 47 */     this.dataFields.add($dataField);
/* 48 */     this.nbOfFields++;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public DataField getDataFieldAt(int index) {
/* 54 */     return this.dataFields.elementAt(index);
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\model\pmml\tildepmml\DataDictionary.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */