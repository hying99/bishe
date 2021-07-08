/*    */ package clus.data.rows;
/*    */ 
/*    */ import jeans.util.compound.DoubleObject;
/*    */ import jeans.util.sort.MSortable;
/*    */ 
/*    */ public class RowDataSortHelper
/*    */   implements MSortable {
/*  8 */   protected int m_NbRows = 0;
/*    */   public DataTuple[] missing;
/*    */   public DataTuple[] zero;
/*    */   public DoubleObject[] other;
/*    */   
/*    */   public void resize(int nbrows) {
/* 14 */     if (nbrows > this.m_NbRows) {
/* 15 */       this.missing = new DataTuple[nbrows];
/* 16 */       this.zero = new DataTuple[nbrows];
/* 17 */       DoubleObject[] new_other = new DoubleObject[nbrows];
/* 18 */       if (this.m_NbRows > 0) {
/* 19 */         System.arraycopy(this.other, 0, new_other, 0, this.m_NbRows);
/*    */       }
/* 21 */       for (int i = this.m_NbRows; i < nbrows; i++) {
/* 22 */         new_other[i] = new DoubleObject();
/*    */       }
/* 24 */       this.other = new_other;
/* 25 */       this.m_NbRows = nbrows;
/*    */     } 
/*    */   }
/*    */   
/*    */   public double getDouble(int i) {
/* 30 */     return this.other[i].getValue();
/*    */   }
/*    */   
/*    */   public void swap(int i, int j) {
/* 34 */     DoubleObject obj_i = this.other[i];
/* 35 */     this.other[i] = this.other[j];
/* 36 */     this.other[j] = obj_i;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\data\rows\RowDataSortHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */