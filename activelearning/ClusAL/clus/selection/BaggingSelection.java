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
/*    */ public class BaggingSelection
/*    */   extends ClusSelection
/*    */ {
/*    */   protected int[] m_Counts;
/*    */   protected int m_NbSel;
/*    */   
/*    */   public BaggingSelection(int nbrows, int nbselected) {
/* 51 */     super(nbrows);
/* 52 */     this.m_Counts = new int[nbrows];
/* 53 */     if (nbselected == 0)
/* 54 */       nbselected = nbrows; 
/*    */     int i;
/* 56 */     for (i = 0; i < nbselected; i++) {
/* 57 */       this.m_Counts[ClusRandom.nextInt(1, nbrows)] = this.m_Counts[ClusRandom.nextInt(1, nbrows)] + 1;
/*    */     }
/* 59 */     for (i = 0; i < nbrows; i++) {
/* 60 */       if (this.m_Counts[i] != 0) {
/* 61 */         this.m_NbSel++;
/*    */       }
/*    */     } 
/*    */   }
/*    */   
/*    */   public boolean changesDistribution() {
/* 67 */     return true;
/*    */   }
/*    */   
/*    */   public double getWeight(int row) {
/* 71 */     return this.m_Counts[row];
/*    */   }
/*    */   
/*    */   public int getNbSelected() {
/* 75 */     return this.m_NbSel;
/*    */   }
/*    */   
/*    */   public boolean isSelected(int row) {
/* 79 */     return (this.m_Counts[row] != 0);
/*    */   }
/*    */   
/*    */   public final int getCount(int row) {
/* 83 */     return this.m_Counts[row];
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\selection\BaggingSelection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */