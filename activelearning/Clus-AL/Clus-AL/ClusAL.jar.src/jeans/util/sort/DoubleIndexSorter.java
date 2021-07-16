/*     */ package jeans.util.sort;
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
/*     */ public class DoubleIndexSorter
/*     */ {
/*     */   protected static DoubleIndexSorter m_Instance;
/*     */   protected double[] m_Data;
/*     */   protected int[] m_Index;
/*     */   
/*     */   public static DoubleIndexSorter getInstance() {
/*  34 */     if (m_Instance == null) m_Instance = new DoubleIndexSorter(); 
/*  35 */     return m_Instance;
/*     */   }
/*     */   
/*     */   public final void setData(double[] data) {
/*  39 */     this.m_Data = data;
/*  40 */     int nb = data.length;
/*  41 */     this.m_Index = new int[nb];
/*  42 */     for (int i = 0; i < nb; ) { this.m_Index[i] = i; i++; }
/*     */   
/*     */   }
/*     */   public final void setData(double[] data, int[] index) {
/*  46 */     this.m_Data = data;
/*  47 */     this.m_Index = index;
/*     */   }
/*     */   
/*     */   public static double[] arrayclone(double[] data) {
/*  51 */     double[] res = new double[data.length];
/*  52 */     System.arraycopy(data, 0, res, 0, data.length);
/*  53 */     return res;
/*     */   }
/*     */   
/*     */   public static double[] unsort(double[] data, int[] index) {
/*  57 */     int nb = data.length;
/*  58 */     double[] ndata = new double[nb];
/*  59 */     for (int i = 0; i < nb; i++) {
/*  60 */       ndata[index[i]] = data[i];
/*     */     }
/*  62 */     return ndata;
/*     */   }
/*     */   
/*     */   public final void sort() {
/*  66 */     quickSort(0, this.m_Data.length);
/*     */   }
/*     */   
/*     */   public final int[] getIndex() {
/*  70 */     return this.m_Index;
/*     */   }
/*     */ 
/*     */   
/*     */   private final void quickSort(int off, int len) {
/*  75 */     if (len < 7) {
/*  76 */       for (int i = off; i < len + off; i++) {
/*  77 */         for (int j = i; j > off && this.m_Data[j - 1] <= this.m_Data[j]; j--) {
/*  78 */           swap(j, j - 1);
/*     */         }
/*     */       } 
/*     */       return;
/*     */     } 
/*  83 */     int m = off + len / 2;
/*  84 */     if (len > 7) {
/*  85 */       int l = off;
/*  86 */       int i = off + len - 1;
/*  87 */       if (len > 40) {
/*  88 */         int j = len / 8;
/*  89 */         l = med3(l, l + j, l + 2 * j);
/*  90 */         m = med3(m - j, m, m + j);
/*  91 */         i = med3(i - 2 * j, i - j, i);
/*     */       } 
/*  93 */       m = med3(l, m, i);
/*     */     } 
/*  95 */     double v = this.m_Data[m];
/*     */ 
/*     */     
/*  98 */     int a = off, b = a, c = off + len - 1, d = c;
/*     */     while (true) {
/* 100 */       if (b <= c && this.m_Data[b] > v) {
/* 101 */         if (this.m_Data[b] == v)
/* 102 */           swap(a++, b); 
/* 103 */         b++; continue;
/*     */       } 
/* 105 */       while (c >= b && this.m_Data[c] < v) {
/* 106 */         if (this.m_Data[c] == v)
/* 107 */           swap(c, d--); 
/* 108 */         c--;
/*     */       } 
/* 110 */       if (b > c)
/*     */         break; 
/* 112 */       swap(b++, c--);
/*     */     } 
/*     */ 
/*     */     
/* 116 */     int n = off + len;
/* 117 */     int s = Math.min(a - off, b - a); vecswap(off, b - s, s);
/* 118 */     s = Math.min(d - c, n - d - 1); vecswap(b, n - s, s);
/*     */ 
/*     */     
/* 121 */     if ((s = b - a) > 1)
/* 122 */       quickSort(off, s); 
/* 123 */     if ((s = d - c) > 1) {
/* 124 */       quickSort(n - s, s);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private final void swap(int a, int b) {
/* 131 */     double t = this.m_Data[a];
/* 132 */     this.m_Data[a] = this.m_Data[b];
/* 133 */     this.m_Data[b] = t;
/* 134 */     int i = this.m_Index[a];
/* 135 */     this.m_Index[a] = this.m_Index[b];
/* 136 */     this.m_Index[b] = i;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final void vecswap(int a, int b, int n) {
/* 143 */     for (int i = 0; i < n; ) { swap(a, b); i++; a++; b++; }
/*     */   
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private final int med3(int a, int b, int c) {
/* 150 */     return (this.m_Data[a] >= this.m_Data[b]) ? ((this.m_Data[b] >= this.m_Data[c]) ? b : ((this.m_Data[a] >= this.m_Data[c]) ? c : a)) : ((this.m_Data[b] <= this.m_Data[c]) ? b : ((this.m_Data[a] <= this.m_Data[c]) ? c : a));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static void main(String[] args) {
/* 156 */     int nb = 80;
/* 157 */     double[] arr = new double[nb];
/* 158 */     for (int i = 0; i < nb; i++) {
/* 159 */       arr[i] = Math.random();
/*     */     }
/* 161 */     DoubleIndexSorter sr = getInstance();
/* 162 */     sr.setData(arr);
/* 163 */     sr.sort();
/* 164 */     for (int j = 0; j < 80; j++) {
/* 165 */       System.out.println(arr[j]);
/* 166 */       System.out.println(sr.getIndex()[j]);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\jean\\util\sort\DoubleIndexSorter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */