/*    */ package clus.data.cols.attribute;
/*    */ 
/*    */ import clus.data.cols.ColTarget;
/*    */ import clus.data.io.ClusReader;
/*    */ import clus.data.type.TimeSeriesAttrType;
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
/*    */ public class TimeSeriesTarget
/*    */   extends TimeSeriesAttrBase
/*    */ {
/*    */   protected ColTarget m_Target;
/*    */   protected int m_Index;
/*    */   
/*    */   public TimeSeriesTarget(ColTarget target, TimeSeriesAttrType type, int index) {
/* 37 */     super(type);
/* 38 */     this.m_Target = target;
/* 39 */     this.m_Index = index;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean read(ClusReader data, int row) throws IOException {
/* 44 */     return true;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\data\cols\attribute\TimeSeriesTarget.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */