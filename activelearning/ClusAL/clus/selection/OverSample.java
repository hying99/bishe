/*    */ package clus.selection;
/*    */ 
/*    */ import clus.util.ClusRandom;
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
/*    */ public class OverSample
/*    */   extends ClusSelection
/*    */ {
/*    */   protected int m_NbSelected;
/*    */   
/*    */   public OverSample(int nbrows, double sel) {
/* 32 */     super(nbrows);
/* 33 */     this.m_NbSelected = (int)Math.ceil(sel * nbrows);
/*    */   }
/*    */   
/*    */   public boolean supportsReplacement() {
/* 37 */     return true;
/*    */   }
/*    */   
/*    */   public int getIndex(int i) {
/* 41 */     return ClusRandom.nextInt(1, this.m_NbRows);
/*    */   }
/*    */   
/*    */   public int getNbSelected() {
/* 45 */     return this.m_NbSelected;
/*    */   }
/*    */   
/*    */   public boolean isSelected(int row) {
/* 49 */     return false;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\selection\OverSample.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */