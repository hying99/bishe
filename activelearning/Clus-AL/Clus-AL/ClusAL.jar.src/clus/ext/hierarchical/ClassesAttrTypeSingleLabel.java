/*    */ package clus.ext.hierarchical;
/*    */ 
/*    */ import clus.data.rows.DataTuple;
/*    */ import clus.data.type.ClusAttrType;
/*    */ import clus.data.type.ClusSchema;
/*    */ import clus.data.type.StringAttrType;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ClassesAttrTypeSingleLabel
/*    */   extends ClassesAttrType
/*    */ {
/*    */   public static final long serialVersionUID = 1L;
/*    */   
/*    */   public ClassesAttrTypeSingleLabel(String name) {
/* 43 */     super(name);
/*    */   }
/*    */   
/*    */   public ClassesAttrTypeSingleLabel(String name, ClassHierarchy hier) {
/* 47 */     super(name, hier);
/*    */   }
/*    */   
/*    */   public ClassesAttrTypeSingleLabel(String name, String atype) {
/* 51 */     super(name, atype);
/*    */   }
/*    */ 
/*    */   
/*    */   public ClusAttrType cloneType() {
/* 56 */     ClassesAttrTypeSingleLabel at = new ClassesAttrTypeSingleLabel(this.m_Name, this.m_Hier);
/* 57 */     cloneType(at);
/* 58 */     return at;
/*    */   }
/*    */   
/*    */   public void updatePredictWriterSchema(ClusSchema schema) {
/* 62 */     String name = getName();
/* 63 */     schema.addAttrType((ClusAttrType)new StringAttrType(name + "-a"));
/*    */   }
/*    */   
/*    */   public String getPredictionWriterString(DataTuple tuple) {
/* 67 */     StringBuffer buf = new StringBuffer();
/* 68 */     buf.append(getString(tuple));
/* 69 */     return buf.toString();
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\ext\hierarchical\ClassesAttrTypeSingleLabel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */