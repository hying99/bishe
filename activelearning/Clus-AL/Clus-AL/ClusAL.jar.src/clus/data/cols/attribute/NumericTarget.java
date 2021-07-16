/*    */ package clus.data.cols.attribute;
/*    */ 
/*    */ import clus.data.cols.ColTarget;
/*    */ import clus.data.io.ClusReader;
/*    */ import clus.data.type.NumericAttrType;
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
/*    */ public class NumericTarget
/*    */   extends NumericAttrBase
/*    */ {
/*    */   protected ColTarget m_Target;
/*    */   protected int m_Index;
/*    */   
/*    */   public NumericTarget(ColTarget target, NumericAttrType type, int index) {
/* 37 */     super(type);
/* 38 */     this.m_Target = target;
/* 39 */     this.m_Index = index;
/*    */   }
/*    */   
/*    */   public boolean read(ClusReader data, int row) throws IOException {
/* 43 */     if (!data.readNoSpace()) return false; 
/* 44 */     this.m_Target.setNumeric(this.m_Index, row, data.getFloat());
/* 45 */     return true;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\data\cols\attribute\NumericTarget.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */