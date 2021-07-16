/*    */ package clus.selection;
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
/*    */ public abstract class ClusSelection
/*    */ {
/*    */   protected int m_NbRows;
/*    */   
/*    */   public ClusSelection(int nbrows) {
/* 30 */     this.m_NbRows = nbrows;
/*    */   }
/*    */   
/*    */   public int getNbRows() {
/* 34 */     return this.m_NbRows;
/*    */   }
/*    */   
/*    */   public boolean supportsReplacement() {
/* 38 */     return false;
/*    */   }
/*    */   
/*    */   public boolean changesDistribution() {
/* 42 */     return false;
/*    */   }
/*    */   
/*    */   public double getWeight(int row) {
/* 46 */     return 1.0D;
/*    */   }
/*    */   
/*    */   public int getIndex(int i) {
/* 50 */     return 0;
/*    */   }
/*    */   
/*    */   public abstract int getNbSelected();
/*    */   
/*    */   public abstract boolean isSelected(int paramInt);
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\selection\ClusSelection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */