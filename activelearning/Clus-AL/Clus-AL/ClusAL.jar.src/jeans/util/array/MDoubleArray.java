/*     */ package jeans.util.array;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import jeans.util.MStreamTokenizer;
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
/*     */ public class MDoubleArray
/*     */ {
/*     */   protected double[] m_hArray;
/*     */   protected int m_lSize;
/*     */   protected int m_lLen;
/*     */   
/*     */   public static final void add(double[] arr, double[] arr1) {
/*  36 */     for (int i = 0; i < arr.length; i++)
/*  37 */       arr[i] = arr[i] + arr1[i]; 
/*     */   }
/*     */   
/*     */   public static final void add(double[] arr, double[] arr1, double weight) {
/*  41 */     for (int i = 0; i < arr.length; i++)
/*  42 */       arr[i] = arr[i] + weight * arr1[i]; 
/*     */   }
/*     */   
/*     */   public static final void divide(double[] arr, double[] arr1) {
/*  46 */     for (int i = 0; i < arr.length; i++) {
/*  47 */       if (arr1[i] != 0.0D) arr[i] = arr[i] / arr1[i]; 
/*     */     } 
/*     */   }
/*     */   public static final void subtractFromThis(double[] arr, double[] other) {
/*  51 */     for (int i = 0; i < arr.length; i++)
/*  52 */       arr[i] = arr[i] - other[i]; 
/*     */   }
/*     */   
/*     */   public static final void subtractFromOther(double[] arr, double[] other) {
/*  56 */     for (int i = 0; i < arr.length; i++)
/*  57 */       arr[i] = other[i] - arr[i]; 
/*     */   }
/*     */   
/*     */   public static final void dotscalar(double[] arr, double fac) {
/*  61 */     for (int i = 0; i < arr.length; ) { arr[i] = arr[i] * fac; i++; }
/*     */   
/*     */   }
/*     */   public static final double[] clone(double[] arr) {
/*  65 */     double[] clone = new double[arr.length];
/*  66 */     System.arraycopy(arr, 0, clone, 0, arr.length);
/*  67 */     return clone;
/*     */   }
/*     */   
/*     */   public void read(MStreamTokenizer tokens, char st, char ed) throws IOException {
/*  71 */     tokens.readChar(st);
/*  72 */     int idx = 0;
/*  73 */     int ch = tokens.getCharToken();
/*  74 */     if (ch == ed) {
/*  75 */       this.m_lLen = 0;
/*     */       return;
/*     */     } 
/*  78 */     tokens.pushBackChar(ch);
/*     */     
/*     */     while (true) {
/*  81 */       double val = Double.parseDouble(tokens.readToken());
/*  82 */       if (idx >= this.m_lSize) grow(); 
/*  83 */       this.m_hArray[idx++] = val;
/*     */       
/*  85 */       ch = tokens.getCharToken();
/*  86 */       if (ch == ed) {
/*  87 */         this.m_lLen = idx;
/*     */         return;
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public double[] toArray() {
/*  94 */     double[] nArr = new double[this.m_lLen];
/*  95 */     System.arraycopy(this.m_hArray, 0, nArr, 0, this.m_lLen);
/*  96 */     return nArr;
/*     */   }
/*     */   
/*     */   public void clear() {
/* 100 */     this.m_lSize = 0;
/* 101 */     this.m_hArray = null;
/*     */   }
/*     */   
/*     */   public void grow() {
/* 105 */     int nSize = this.m_lSize * 3 / 2 + 10;
/* 106 */     double[] nArr = new double[nSize];
/* 107 */     if (this.m_hArray != null) System.arraycopy(this.m_hArray, 0, nArr, 0, this.m_lSize); 
/* 108 */     this.m_lSize = nSize;
/* 109 */     this.m_hArray = nArr;
/*     */   }
/*     */   
/*     */   public static String toString(double[] arr) {
/* 113 */     String str = "[";
/* 114 */     for (int ctr = 0; ctr < arr.length; ctr++) {
/* 115 */       if (ctr != 0) str = str + ", "; 
/* 116 */       str = str + String.valueOf(arr[ctr]);
/*     */     } 
/* 118 */     return str + "]";
/*     */   }
/*     */   
/*     */   public String toString() {
/* 122 */     String str = "[";
/* 123 */     for (int ctr = 0; ctr < this.m_lLen; ctr++) {
/* 124 */       if (ctr != 0) str = str + ", "; 
/* 125 */       str = str + String.valueOf(this.m_hArray[ctr]);
/*     */     } 
/* 127 */     return str + "]";
/*     */   }
/*     */   
/*     */   public static double max(double[] values) {
/* 131 */     double m = Double.NEGATIVE_INFINITY;
/* 132 */     for (int i = 0; i < values.length; i++) {
/* 133 */       if (values[i] > m) m = values[i]; 
/*     */     } 
/* 135 */     return m;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\jean\\util\array\MDoubleArray.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */