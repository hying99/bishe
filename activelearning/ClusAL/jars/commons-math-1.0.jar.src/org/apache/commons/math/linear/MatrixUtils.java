/*     */ package org.apache.commons.math.linear;
/*     */ 
/*     */ import java.math.BigDecimal;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class MatrixUtils
/*     */ {
/*     */   public static RealMatrix createRealMatrix(double[][] data) {
/*  46 */     return new RealMatrixImpl(data);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static BigMatrix createBigMatrix(double[][] data) {
/*  60 */     return new BigMatrixImpl(data);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static BigMatrix createBigMatrix(BigDecimal[][] data) {
/*  74 */     return new BigMatrixImpl(data);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static BigMatrix createBigMatrix(String[][] data) {
/*  88 */     return new BigMatrixImpl(data);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static RealMatrix createRowRealMatrix(double[] rowData) {
/* 101 */     int nCols = rowData.length;
/* 102 */     double[][] data = new double[1][nCols];
/* 103 */     System.arraycopy(rowData, 0, data[0], 0, nCols);
/* 104 */     return new RealMatrixImpl(data);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static BigMatrix createRowBigMatrix(double[] rowData) {
/* 117 */     int nCols = rowData.length;
/* 118 */     double[][] data = new double[1][nCols];
/* 119 */     System.arraycopy(rowData, 0, data[0], 0, nCols);
/* 120 */     return new BigMatrixImpl(data);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static BigMatrix createRowBigMatrix(BigDecimal[] rowData) {
/* 133 */     int nCols = rowData.length;
/* 134 */     BigDecimal[][] data = new BigDecimal[1][nCols];
/* 135 */     System.arraycopy(rowData, 0, data[0], 0, nCols);
/* 136 */     return new BigMatrixImpl(data);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static BigMatrix createRowBigMatrix(String[] rowData) {
/* 149 */     int nCols = rowData.length;
/* 150 */     String[][] data = new String[1][nCols];
/* 151 */     System.arraycopy(rowData, 0, data[0], 0, nCols);
/* 152 */     return new BigMatrixImpl(data);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static RealMatrix createColumnRealMatrix(double[] columnData) {
/* 165 */     int nRows = columnData.length;
/* 166 */     double[][] data = new double[nRows][1];
/* 167 */     for (int row = 0; row < nRows; row++) {
/* 168 */       data[row][0] = columnData[row];
/*     */     }
/* 170 */     return new RealMatrixImpl(data);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static BigMatrix createColumnBigMatrix(double[] columnData) {
/* 183 */     int nRows = columnData.length;
/* 184 */     double[][] data = new double[nRows][1];
/* 185 */     for (int row = 0; row < nRows; row++) {
/* 186 */       data[row][0] = columnData[row];
/*     */     }
/* 188 */     return new BigMatrixImpl(data);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static BigMatrix createColumnBigMatrix(BigDecimal[] columnData) {
/* 201 */     int nRows = columnData.length;
/* 202 */     BigDecimal[][] data = new BigDecimal[nRows][1];
/* 203 */     for (int row = 0; row < nRows; row++) {
/* 204 */       data[row][0] = columnData[row];
/*     */     }
/* 206 */     return new BigMatrixImpl(data);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static BigMatrix createColumnBigMatrix(String[] columnData) {
/* 219 */     int nRows = columnData.length;
/* 220 */     String[][] data = new String[nRows][1];
/* 221 */     for (int row = 0; row < nRows; row++) {
/* 222 */       data[row][0] = columnData[row];
/*     */     }
/* 224 */     return new BigMatrixImpl(data);
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\commons-math-1.0.jar!\org\apache\commons\math\linear\MatrixUtils.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */