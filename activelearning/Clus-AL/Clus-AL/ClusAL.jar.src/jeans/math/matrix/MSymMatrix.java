/*     */ package jeans.math.matrix;
/*     */ 
/*     */ import java.io.Serializable;
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
/*     */ public class MSymMatrix
/*     */   extends MMatrix
/*     */   implements Serializable
/*     */ {
/*     */   protected int m_Size;
/*     */   protected double[][] m_Data;
/*     */   
/*     */   public MSymMatrix(int size) {
/*  33 */     this.m_Size = size;
/*  34 */     this.m_Data = createSymmetricData(size);
/*     */   }
/*     */   
/*     */   public int getSize() {
/*  38 */     return this.m_Size;
/*     */   }
/*     */   
/*     */   public final double xtAx(double[] x) {
/*  42 */     double res = 0.0D;
/*  43 */     for (int i = 0; i < this.m_Size; i++) {
/*  44 */       double xi = x[i];
/*  45 */       double[] ai = this.m_Data[i];
/*  46 */       res += this.m_Data[i][i] * xi * xi;
/*  47 */       for (int j = 0; j < i; j++)
/*  48 */         res += 2.0D * ai[j] * xi * x[j]; 
/*     */     } 
/*  50 */     return res;
/*     */   }
/*     */   
/*     */   public final double xtAx_delta(double[] x1, double[] x2) {
/*  54 */     double res = 0.0D;
/*  55 */     for (int i = 0; i < this.m_Size; i++) {
/*  56 */       double xi = x1[i] - x2[i];
/*  57 */       double[] ai = this.m_Data[i];
/*  58 */       res += this.m_Data[i][i] * xi * xi;
/*  59 */       for (int j = 0; j < i; j++) {
/*  60 */         double xj = x1[j] - x2[j];
/*  61 */         res += 2.0D * ai[j] * xi * xj;
/*     */       } 
/*     */     } 
/*  64 */     return res;
/*     */   }
/*     */   
/*     */   public final double xtAx(MySparseVector x) {
/*  68 */     double res = 0.0D;
/*  69 */     int nb = x.getNbNonZero();
/*  70 */     for (int vi = 0; vi < nb; vi++) {
/*  71 */       int i = x.getPosition(vi);
/*  72 */       double xi = x.getValue(vi);
/*  73 */       res += this.m_Data[i][i] * xi * xi;
/*  74 */       for (int vj = 0; vj < vi; vj++) {
/*  75 */         int j = x.getPosition(vj);
/*  76 */         double xj = x.getValue(vj);
/*  77 */         res += 2.0D * get(i, j) * xi * xj;
/*     */       } 
/*     */     } 
/*  80 */     return res;
/*     */   }
/*     */   
/*     */   public final void addRowWeighted(double[] x, int i, double w) {
/*  84 */     double[] ai = this.m_Data[i]; int j;
/*  85 */     for (j = 0; j <= i; j++) {
/*  86 */       x[j] = x[j] + w * ai[j];
/*     */     }
/*  88 */     for (j = i + 1; j < this.m_Size; j++) {
/*  89 */       x[j] = x[j] + w * this.m_Data[j][i];
/*     */     }
/*     */   }
/*     */   
/*     */   public final double get(int r, int c) {
/*  94 */     if (c > r) return this.m_Data[c][r]; 
/*  95 */     return this.m_Data[r][c];
/*     */   }
/*     */   
/*     */   public final double get_fast(int r, int c) {
/*  99 */     return this.m_Data[r][c];
/*     */   }
/*     */   
/*     */   public final double[] getRow(int r) {
/* 103 */     return this.m_Data[r];
/*     */   }
/*     */   
/*     */   public final void set(int r, int c, double val) {
/* 107 */     this.m_Data[r][c] = val;
/*     */   }
/*     */   
/*     */   public final void set_sym(int r, int c, double val) {
/* 111 */     if (c > r) { this.m_Data[c][r] = val; }
/* 112 */     else { this.m_Data[r][c] = val; }
/*     */   
/*     */   }
/*     */   public final void add_sym(int r, int c, double val) {
/* 116 */     if (c > r) { this.m_Data[c][r] = this.m_Data[c][r] + val; }
/* 117 */     else { this.m_Data[r][c] = this.m_Data[r][c] + val; }
/*     */   
/*     */   }
/*     */   public final int getRows() {
/* 121 */     return this.m_Size;
/*     */   }
/*     */   
/*     */   public final int getCols() {
/* 125 */     return this.m_Size;
/*     */   }
/*     */   
/*     */   private static final double[][] createSymmetricData(int size) {
/* 129 */     double[][] data = new double[size][];
/* 130 */     for (int i = 0; i < size; i++)
/* 131 */       data[i] = new double[i + 1]; 
/* 132 */     return data;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 137 */     String output = "";
/* 138 */     for (int i = 0; i < this.m_Size; i++) {
/* 139 */       output = output + toString(i);
/*     */     }
/* 141 */     return output;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString(int row) {
/* 147 */     String output = "";
/*     */     int j;
/* 149 */     for (j = 0; j <= row; j++)
/*     */     {
/* 151 */       output = output + this.m_Data[row][j] + " ";
/*     */     }
/* 153 */     for (j = row + 1; j < this.m_Size; j++)
/*     */     {
/* 155 */       output = output + this.m_Data[j][row] + " ";
/*     */     }
/* 157 */     output = output + "\n";
/* 158 */     return output;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\jeans\math\matrix\MSymMatrix.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */