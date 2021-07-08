/*    */ package clus.data.cols.attribute;
/*    */ 
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
/*    */ public class TimeSeriesAttribute
/*    */   extends TimeSeriesAttrBase
/*    */ {
/*    */   public double[][] m_Data;
/*    */   
/*    */   public TimeSeriesAttribute(TimeSeriesAttrType type) {
/* 35 */     super(type);
/*    */   }
/*    */   
/*    */   public boolean read(ClusReader data, int row) throws IOException {
/* 39 */     System.err.println("TimeSerriesAttribute:read(ClusReader,int) - not implemented");
/* 40 */     return true;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\data\cols\attribute\TimeSeriesAttribute.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */