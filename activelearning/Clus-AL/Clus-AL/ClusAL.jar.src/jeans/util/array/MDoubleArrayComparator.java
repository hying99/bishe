/*    */ package jeans.util.array;
/*    */ 
/*    */ import java.util.Comparator;
/*    */ 
/*    */ public class MDoubleArrayComparator
/*    */   implements Comparator
/*    */ {
/*    */   protected int m_Index;
/*    */   
/*    */   public MDoubleArrayComparator(int idx) {
/* 11 */     this.m_Index = idx;
/*    */   }
/*    */   
/*    */   public int compare(Object arg0, Object arg1) {
/* 15 */     double arg0d = ((double[])arg0)[this.m_Index];
/* 16 */     double arg1d = ((double[])arg1)[this.m_Index];
/* 17 */     if (arg0d == arg1d) {
/* 18 */       return 0;
/*    */     }
/* 20 */     return (arg0d > arg1d) ? 1 : -1;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\jean\\util\array\MDoubleArrayComparator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */