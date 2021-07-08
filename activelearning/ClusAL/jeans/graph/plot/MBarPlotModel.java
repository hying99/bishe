/*    */ package jeans.graph.plot;
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
/*    */ public abstract class MBarPlotModel
/*    */ {
/*    */   protected int m_iOffs;
/*    */   
/*    */   public abstract String getTitle();
/*    */   
/*    */   public abstract int getNbBars();
/*    */   
/*    */   public abstract float getBarValue(int paramInt);
/*    */   
/*    */   public void setScales(float[] scales) {}
/*    */   
/*    */   public void setOffset(int offs) {
/* 39 */     this.m_iOffs = offs;
/*    */   }
/*    */   
/*    */   public int getOffset() {
/* 43 */     return this.m_iOffs;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jeans\graph\plot\MBarPlotModel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */