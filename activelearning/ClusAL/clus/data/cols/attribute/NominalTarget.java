/*    */ package clus.data.cols.attribute;
/*    */ 
/*    */ import clus.data.cols.ColTarget;
/*    */ import clus.data.io.ClusReader;
/*    */ import clus.data.type.NominalAttrType;
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
/*    */ public class NominalTarget
/*    */   extends NominalAttrBase
/*    */ {
/*    */   protected ColTarget m_Target;
/*    */   protected int m_Index;
/*    */   
/*    */   public NominalTarget(ColTarget target, NominalAttrType type, int index) {
/* 37 */     super(type);
/* 38 */     this.m_Target = target;
/* 39 */     this.m_Index = index;
/*    */   }
/*    */   
/*    */   public boolean read(ClusReader data, int row) throws IOException {
/* 43 */     String value = data.readString();
/* 44 */     if (value == null) return false; 
/* 45 */     Integer i = getNominalType().getValueIndex(value);
/* 46 */     if (i != null) {
/* 47 */       this.m_Target.setNominal(this.m_Index, row, i.intValue());
/*    */     } else {
/* 49 */       throw new IOException("Illegal value '" + value + "' for target " + getName() + " at row " + (row + 1));
/*    */     } 
/* 51 */     return true;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\data\cols\attribute\NominalTarget.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */