/*    */ package jeans.math.matrix;
/*    */ 
/*    */ import java.io.PrintWriter;
/*    */ import java.text.NumberFormat;
/*    */ import jeans.util.StringUtils;
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
/*    */ public abstract class MMatrix
/*    */ {
/*    */   public abstract double get(int paramInt1, int paramInt2);
/*    */   
/*    */   public abstract int getRows();
/*    */   
/*    */   public abstract int getCols();
/*    */   
/*    */   public static final double dot(double[] x, double[] y) {
/* 39 */     double res = 0.0D;
/* 40 */     for (int i = 0; i < x.length; i++)
/* 41 */       res += x[i] * y[i]; 
/* 42 */     return res;
/*    */   }
/*    */   
/*    */   public static final double dot_delta(double[] x1, double[] x2, double[] y1, double[] y2) {
/* 46 */     double res = 0.0D;
/* 47 */     for (int i = 0; i < x1.length; i++)
/* 48 */       res += (x1[i] - x2[i]) * (y1[i] - y2[i]); 
/* 49 */     return res;
/*    */   }
/*    */   
/*    */   public static final double dot(MySparseVector x, double[] y) {
/* 53 */     double res = 0.0D;
/* 54 */     int nb = x.getNbNonZero();
/* 55 */     for (int vi = 0; vi < nb; vi++) {
/* 56 */       int i = x.getPosition(vi);
/* 57 */       res += x.getValue(vi) * y[i];
/*    */     } 
/* 59 */     return res;
/*    */   }
/*    */   
/*    */   public final double[][] toCPArray() {
/* 63 */     int rows = getRows();
/* 64 */     int cols = getCols();
/* 65 */     double[][] data = new double[rows][cols];
/* 66 */     for (int i = 0; i < rows; i++) {
/* 67 */       for (int j = 0; j < cols; j++) {
/* 68 */         data[i][j] = get(i, j);
/*    */       }
/*    */     } 
/* 71 */     return data;
/*    */   }
/*    */   
/*    */   public final void print(PrintWriter wrt, NumberFormat format, int size) {
/* 75 */     if (getRows() == 1) {
/* 76 */       printRow(0, wrt, format, size);
/* 77 */       wrt.println();
/*    */     } else {
/* 79 */       for (int mi = 0; mi < getRows(); mi++) {
/* 80 */         if (mi == 0) { wrt.print("["); }
/* 81 */         else { wrt.print(" "); }
/* 82 */          printRow(mi, wrt, format, size);
/* 83 */         if (mi == getRows() - 1) wrt.print("]"); 
/* 84 */         wrt.println();
/*    */       } 
/*    */     } 
/*    */   }
/*    */   
/*    */   private final void printRow(int mi, PrintWriter wrt, NumberFormat format, int size) {
/* 90 */     wrt.print("[");
/* 91 */     for (int mj = 0; mj < getCols(); mj++) {
/* 92 */       if (mj != 0) wrt.print(";"); 
/* 93 */       String strg = format.format(get(mi, mj));
/* 94 */       wrt.print(StringUtils.printStr(strg, size));
/*    */     } 
/* 96 */     wrt.print("]");
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jeans\math\matrix\MMatrix.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */