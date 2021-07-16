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
/*    */ public abstract class XValMainSelection
/*    */ {
/*    */   protected int m_NbFolds;
/*    */   protected int m_NbRows;
/*    */   
/*    */   public XValMainSelection(int nbfolds, int nbrows) {
/* 31 */     this.m_NbFolds = nbfolds;
/* 32 */     this.m_NbRows = nbrows;
/*    */   }
/*    */   
/*    */   public int getNbFolds() {
/* 36 */     return this.m_NbFolds;
/*    */   }
/*    */   
/*    */   public int getNbRows() {
/* 40 */     return this.m_NbRows;
/*    */   }
/*    */   
/*    */   public int getNbSelected(int fold) {
/* 44 */     int nb = 0;
/* 45 */     for (int i = 0; i < this.m_NbRows; ) { if (getFold(i) == fold) nb++;  i++; }
/* 46 */      return nb;
/*    */   }
/*    */   
/*    */   public boolean isSelected(int row, int fold) {
/* 50 */     return (getFold(row) == fold);
/*    */   }
/*    */   
/*    */   public abstract int getFold(int paramInt);
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\selection\XValMainSelection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */