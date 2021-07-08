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
/*    */ public abstract class Indexer
/*    */ {
/*    */   private int m_Batchsize;
/*    */   private int m_SelectedIndex;
/*    */   
/*    */   protected Indexer(int batchSize) {
/* 18 */     setBatchsize(batchSize);
/* 19 */     setSelectedIndex(-1);
/*    */   }
/*    */   
/*    */   public boolean isIndexSet() {
/* 23 */     return (getSelectedIndex() != -1);
/*    */   }
/*    */   public int getBatchsize() {
/* 26 */     return this.m_Batchsize;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public final void setBatchsize(int m_Batchsize) {
/* 33 */     this.m_Batchsize = m_Batchsize;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public int getSelectedIndex() {
/* 40 */     return this.m_SelectedIndex;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public final void setSelectedIndex(int m_SelectedIndex) {
/* 47 */     this.m_SelectedIndex = m_SelectedIndex;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\activelearning\indexing\Indexer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */