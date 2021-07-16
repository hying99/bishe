/*    */ package clus.ext.hierarchical;
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
/*    */ public class FlatClassesAttrType
/*    */   extends ClassesAttrType
/*    */ {
/*    */   public static final long serialVersionUID = 1L;
/*    */   protected ClassesAttrType m_Mimic;
/*    */   
/*    */   public FlatClassesAttrType(String name, ClassesAttrType mimic) {
/* 40 */     super(name);
/* 41 */     this.m_Mimic = mimic;
/*    */   }
/*    */   
/*    */   public ClusSerializable createRowSerializable() throws ClusException {
/* 45 */     return new MySerializable();
/*    */   }
/*    */   
/*    */   public class MySerializable
/*    */     extends ClusSerializable {
/*    */     public boolean read(ClusReader data, DataTuple tuple) throws IOException {
/* 51 */       ClassesTuple other = (ClassesTuple)tuple.getObjVal(FlatClassesAttrType.this.m_Mimic.getArrayIndex());
/* 52 */       tuple.setObjectVal(other.toFlat(FlatClassesAttrType.this.getM_Table()), FlatClassesAttrType.this.getArrayIndex());
/* 53 */       return true;
/*    */     }
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\ext\hierarchical\FlatClassesAttrType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */