/*    */ package jeans.math.matrix;
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
/*    */ public class MStoredMatrix
/*    */   extends MMatrix
/*    */ {
/*    */   protected int m_Rows;
/*    */   protected int m_Cols;
/*    */   protected double[][] m_Data;
/*    */   
/*    */   public MStoredMatrix(double[] row) {
/* 31 */     this.m_Rows = 1;
/* 32 */     this.m_Cols = row.length;
/* 33 */     this.m_Data = new double[1][];
/* 34 */     this.m_Data[0] = row;
/*    */   }
/*    */   
/*    */   public final double get(int r, int c) {
/* 38 */     if (c > r) return this.m_Data[c][r]; 
/* 39 */     return this.m_Data[r][c];
/*    */   }
/*    */   
/*    */   public final int getRows() {
/* 43 */     return this.m_Rows;
/*    */   }
/*    */   
/*    */   public final int getCols() {
/* 47 */     return this.m_Cols;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jeans\math\matrix\MStoredMatrix.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */