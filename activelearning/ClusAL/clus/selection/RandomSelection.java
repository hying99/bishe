/*    */ package clus.selection;
/*    */ 
/*    */ import java.util.Random;
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
/*    */ public class RandomSelection
/*    */   extends ClusSelection
/*    */ {
/*    */   protected int m_NbSelected;
/*    */   protected boolean[] m_Selection;
/*    */   
/*    */   public RandomSelection(int nbrows, double sel) {
/* 33 */     super(nbrows);
/* 34 */     makeSelection(nbrows, (int)Math.round(sel * nbrows));
/*    */   }
/*    */   
/*    */   public RandomSelection(int nbrows, int nbsel) {
/* 38 */     super(nbrows);
/* 39 */     makeSelection(nbrows, nbsel);
/*    */   }
/*    */   
/*    */   public int getNbSelected() {
/* 43 */     return this.m_NbSelected;
/*    */   }
/*    */   
/*    */   public boolean isSelected(int row) {
/* 47 */     return this.m_Selection[row];
/*    */   }
/*    */   
/*    */   private final void makeSelection(int nbrows, int nbsel) {
/* 51 */     this.m_NbSelected = nbsel;
/* 52 */     this.m_Selection = new boolean[nbrows];
/* 53 */     Random rnd = new Random(0L);
/* 54 */     for (int i = 0; i < this.m_NbSelected; i++) {
/* 55 */       int j = 0;
/* 56 */       int p = rnd.nextInt(nbrows - i) + 1;
/*    */       
/* 58 */       while (p > 0 && j < nbrows) {
/*    */         
/* 60 */         p--;
/* 61 */         if (!this.m_Selection[j] && p == 0) this.m_Selection[j] = true;
/*    */         
/* 63 */         j++;
/*    */       } 
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\selection\RandomSelection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */