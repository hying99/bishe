/*    */ package clus.data.type;
/*    */ 
/*    */ import clus.data.io.ClusReader;
/*    */ import clus.data.rows.DataTuple;
/*    */ import clus.io.ClusSerializable;
/*    */ import clus.util.ClusException;
/*    */ import java.io.IOException;
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
/*    */ public class StringAttrType
/*    */   extends ClusAttrType
/*    */ {
/*    */   public static final long serialVersionUID = 1L;
/*    */   public static final int THIS_TYPE = 3;
/*    */   public static final String THIS_TYPE_NAME = "String";
/*    */   
/*    */   public StringAttrType(String name) {
/* 41 */     super(name);
/*    */   }
/*    */   
/*    */   public ClusAttrType cloneType() {
/* 45 */     StringAttrType at = new StringAttrType(this.m_Name);
/* 46 */     cloneType(at);
/* 47 */     return at;
/*    */   }
/*    */   
/*    */   public int getTypeIndex() {
/* 51 */     return 3;
/*    */   }
/*    */   
/*    */   public String getTypeName() {
/* 55 */     return "String";
/*    */   }
/*    */   
/*    */   public int getValueType() {
/* 59 */     return 2;
/*    */   }
/*    */   
/*    */   public String getString(DataTuple tuple) {
/* 63 */     return (String)tuple.m_Objects[this.m_ArrayIndex];
/*    */   }
/*    */   
/*    */   public int compareValue(DataTuple t1, DataTuple t2) {
/* 67 */     String s1 = (String)t1.m_Objects[this.m_ArrayIndex];
/* 68 */     String s2 = (String)t2.m_Objects[this.m_ArrayIndex];
/* 69 */     return s1.equals(s2) ? 0 : 1;
/*    */   }
/*    */   
/*    */   public void writeARFFType(PrintWriter wrt) throws ClusException {
/* 73 */     wrt.print("string");
/*    */   }
/*    */   
/*    */   public ClusSerializable createRowSerializable() throws ClusException {
/* 77 */     return new MySerializable();
/*    */   }
/*    */   
/*    */   public class MySerializable
/*    */     extends ClusSerializable {
/*    */     public boolean read(ClusReader data, DataTuple tuple) throws IOException {
/* 83 */       String value = data.readString();
/* 84 */       if (value == null) return false; 
/* 85 */       tuple.setObjectVal(value, StringAttrType.this.getArrayIndex());
/* 86 */       return true;
/*    */     }
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\data\type\StringAttrType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */