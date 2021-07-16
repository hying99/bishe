/*    */ package clus.data.cols.attribute;
/*    */ 
/*    */ import clus.data.type.ClusAttrType;
/*    */ import clus.data.type.TimeSeriesAttrType;
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
/*    */ public abstract class TimeSeriesAttrBase
/*    */   extends ClusAttribute
/*    */ {
/*    */   protected TimeSeriesAttrType m_Type;
/*    */   
/*    */   public TimeSeriesAttrBase(TimeSeriesAttrType type) {
/* 33 */     this.m_Type = type;
/*    */   }
/*    */   
/*    */   public ClusAttrType getType() {
/* 37 */     return (ClusAttrType)this.m_Type;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\data\cols\attribute\TimeSeriesAttrBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */