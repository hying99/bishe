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
/*    */ public class BitMapSelection
/*    */   extends ClusSelection
/*    */ {
/*    */   protected int m_NbSelected;
/*    */   protected boolean[] m_Selection;
/*    */   
/*    */   public BitMapSelection(int nbrows) {
/* 31 */     super(nbrows);
/* 32 */     this.m_Selection = new boolean[nbrows];
/*    */   }
/*    */   
/*    */   public int getNbSelected() {
/* 36 */     return this.m_NbSelected;
/*    */   }
/*    */   
/*    */   public boolean isSelected(int row) {
/* 40 */     return this.m_Selection[row];
/*    */   }
/*    */   
/*    */   public void select(int row) {
/* 44 */     if (!this.m_Selection[row]) {
/* 45 */       this.m_Selection[row] = true;
/* 46 */       this.m_NbSelected++;
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\selection\BitMapSelection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */