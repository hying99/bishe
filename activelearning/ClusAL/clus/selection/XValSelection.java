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
/*    */ public class XValSelection
/*    */   extends ClusSelection
/*    */ {
/*    */   protected int m_Fold;
/*    */   protected int m_NbSel;
/*    */   protected XValMainSelection m_Sel;
/*    */   
/*    */   public XValSelection(XValMainSelection sel, int fold) {
/* 31 */     super(sel.getNbRows());
/* 32 */     this.m_Sel = sel;
/* 33 */     this.m_Fold = fold;
/* 34 */     this.m_NbSel = sel.getNbSelected(fold);
/*    */   }
/*    */   
/*    */   public int getNbSelected() {
/* 38 */     return this.m_NbSel;
/*    */   }
/*    */   
/*    */   public boolean isSelected(int row) {
/* 42 */     return this.m_Sel.isSelected(row, this.m_Fold);
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\selection\XValSelection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */