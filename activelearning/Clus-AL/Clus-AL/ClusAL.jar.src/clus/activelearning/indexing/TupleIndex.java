/*    */ package clus.activelearning.indexing;
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
/*    */ public class TupleIndex
/*    */ {
/*    */   private int m_Index;
/*    */   private double m_Measure;
/*    */   
/*    */   public TupleIndex(int index, double var) {
/* 18 */     this.m_Index = index;
/* 19 */     this.m_Measure = var;
/*    */   }
/*    */   
/*    */   public void setValues(int index, double var) {
/* 23 */     setIndex(index);
/* 24 */     setMeasure(var);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public int getIndex() {
/* 35 */     return this.m_Index;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void setIndex(int m_Index) {
/* 42 */     this.m_Index = m_Index;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public double getMeasure() {
/* 49 */     return this.m_Measure;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void setMeasure(double m_Measure) {
/* 56 */     this.m_Measure = m_Measure;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\activelearning\indexing\TupleIndex.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */