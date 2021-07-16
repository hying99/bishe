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
/*    */ public class OOBSelection
/*    */   extends ClusSelection
/*    */ {
/*    */   protected int[] m_OOBCounts;
/*    */   protected int m_OOBNbSel;
/*    */   
/*    */   public OOBSelection(BaggingSelection bsel) {
/* 30 */     super(bsel.getNbRows());
/* 31 */     this.m_OOBCounts = new int[bsel.getNbRows()];
/* 32 */     this.m_OOBNbSel = 0;
/* 33 */     for (int i = 0; i < bsel.getNbRows(); i++) {
/* 34 */       if (bsel.getWeight(i) == 0.0D) {
/* 35 */         this.m_OOBCounts[i] = 1;
/* 36 */         this.m_OOBNbSel++;
/*    */       } 
/*    */     } 
/* 39 */     if (this.m_OOBNbSel != bsel.getNbRows() - bsel.getNbSelected()) {
/* 40 */       System.err.println(getClass().getName() + ": Error while creating the OOB");
/*    */     }
/*    */   }
/*    */   
/*    */   public OOBSelection(int nbRows) {
/* 45 */     super(nbRows);
/* 46 */     this.m_OOBCounts = new int[nbRows];
/* 47 */     this.m_OOBNbSel = 0;
/* 48 */     for (int i = 0; i < nbRows; i++) {
/* 49 */       this.m_OOBCounts[i] = 0;
/* 50 */       this.m_OOBNbSel++;
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean changesDistribution() {
/* 57 */     return true;
/*    */   }
/*    */   
/*    */   public double getWeight(int row) {
/* 61 */     return this.m_OOBCounts[row];
/*    */   }
/*    */   
/*    */   public int getNbSelected() {
/* 65 */     return this.m_OOBNbSel;
/*    */   }
/*    */   
/*    */   public boolean isSelected(int row) {
/* 69 */     return (this.m_OOBCounts[row] != 0);
/*    */   }
/*    */   
/*    */   public final int getCount(int row) {
/* 73 */     return this.m_OOBCounts[row];
/*    */   }
/*    */   
/*    */   public void addToThis(OOBSelection other) {
/* 77 */     for (int i = 0; i < this.m_OOBCounts.length; i++) {
/* 78 */       if (getWeight(i) == 0.0D && other.getWeight(i) == 1.0D) {
/* 79 */         this.m_OOBCounts[i] = 1;
/* 80 */         this.m_OOBNbSel++;
/*    */       } 
/*    */     } 
/*    */   }
/*    */   
/*    */   public void addToOther(OOBSelection other) {
/* 86 */     for (int i = 0; i < this.m_OOBCounts.length; i++) {
/* 87 */       if (getWeight(i) == 1.0D && other.getWeight(i) == 0.0D) {
/* 88 */         other.m_OOBCounts[i] = 1;
/* 89 */         other.m_OOBNbSel++;
/*    */       } 
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\selection\OOBSelection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */