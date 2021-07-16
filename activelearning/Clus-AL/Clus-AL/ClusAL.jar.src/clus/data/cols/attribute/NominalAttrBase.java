/*    */ package clus.data.cols.attribute;
/*    */ 
/*    */ import clus.data.type.ClusAttrType;
/*    */ import clus.data.type.NominalAttrType;
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
/*    */ public abstract class NominalAttrBase
/*    */   extends ClusAttribute
/*    */ {
/*    */   protected NominalAttrType m_Type;
/*    */   
/*    */   public NominalAttrBase(NominalAttrType type) {
/* 32 */     this.m_Type = type;
/*    */   }
/*    */   
/*    */   public NominalAttrType getNominalType() {
/* 36 */     return this.m_Type;
/*    */   }
/*    */   
/*    */   public ClusAttrType getType() {
/* 40 */     return (ClusAttrType)this.m_Type;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\data\cols\attribute\NominalAttrBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */