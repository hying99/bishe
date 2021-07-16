/*    */ package clus.data.cols.attribute;
/*    */ 
/*    */ import clus.data.type.ClusAttrType;
/*    */ import clus.data.type.NumericAttrType;
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
/*    */ public abstract class NumericAttrBase
/*    */   extends ClusAttribute
/*    */ {
/*    */   protected NumericAttrType m_Type;
/*    */   
/*    */   public NumericAttrBase(NumericAttrType type) {
/* 32 */     this.m_Type = type;
/*    */   }
/*    */   
/*    */   public ClusAttrType getType() {
/* 36 */     return (ClusAttrType)this.m_Type;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\data\cols\attribute\NumericAttrBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */