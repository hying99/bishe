/*    */ package clus.data.type;
/*    */ 
/*    */ import clus.data.io.ClusReader;
/*    */ import clus.data.rows.DataTuple;
/*    */ import clus.io.ClusSerializable;
/*    */ import clus.util.ClusException;
/*    */ import java.io.IOException;
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
/*    */ 
/*    */ public class IntegerAttrType
/*    */   extends ClusAttrType
/*    */ {
/*    */   public static final long serialVersionUID = 1L;
/*    */   public static final int THIS_TYPE = 4;
/*    */   public static final String THIS_TYPE_NAME = "Integer";
/*    */   
/*    */   public IntegerAttrType(String name) {
/* 41 */     super(name);
/*    */   }
/*    */   
/*    */   public ClusAttrType cloneType() {
/* 45 */     IntegerAttrType at = new IntegerAttrType(this.m_Name);
/* 46 */     cloneType(at);
/* 47 */     return at;
/*    */   }
/*    */   
/*    */   public int getTypeIndex() {
/* 51 */     return 4;
/*    */   }
/*    */   
/*    */   public String getTypeName() {
/* 55 */     return "Integer";
/*    */   }
/*    */   
/*    */   public int getValueType() {
/* 59 */     return 0;
/*    */   }
/*    */   
/*    */   public String getString(DataTuple tuple) {
/* 63 */     return String.valueOf(tuple.m_Ints[this.m_ArrayIndex]);
/*    */   }
/*    */   
/*    */   public int compareValue(DataTuple t1, DataTuple t2) {
/* 67 */     int s1 = t1.m_Ints[this.m_ArrayIndex];
/* 68 */     int s2 = t2.m_Ints[this.m_ArrayIndex];
/* 69 */     return (s1 == s2) ? 0 : 1;
/*    */   }
/*    */   
/*    */   public ClusSerializable createRowSerializable() throws ClusException {
/* 73 */     return new MySerializable();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public class MySerializable
/*    */     extends ClusSerializable
/*    */   {
/*    */     protected int m_Index;
/*    */ 
/*    */ 
/*    */ 
/*    */     
/*    */     public boolean read(ClusReader data, DataTuple tuple) throws IOException {
/* 88 */       tuple.setIntVal(this.m_Index++, IntegerAttrType.this.getArrayIndex());
/* 89 */       return true;
/*    */     }
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\data\type\IntegerAttrType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */