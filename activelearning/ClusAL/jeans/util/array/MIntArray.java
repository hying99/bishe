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
/*     */ public class MIntArray
/*     */ {
/*     */   protected int[] m_hArray;
/*     */   protected int m_lSize;
/*     */   protected int m_lLen;
/*     */   
/*     */   public MIntArray(int size) {
/*  36 */     this.m_hArray = new int[size];
/*  37 */     this.m_lSize = size;
/*  38 */     this.m_lLen = 0;
/*     */   }
/*     */   
/*     */   public MIntArray() {
/*  42 */     this.m_lSize = 0;
/*  43 */     this.m_lLen = 0;
/*     */   }
/*     */   
/*     */   public void read(MStreamTokenizer tokens, char st, char ed) throws IOException {
/*  47 */     tokens.readChar(st);
/*  48 */     int idx = 0;
/*  49 */     int ch = tokens.getCharToken();
/*  50 */     if (ch == ed) {
/*  51 */       this.m_lLen = 0;
/*     */       return;
/*     */     } 
/*  54 */     tokens.pushBackChar(ch);
/*     */     
/*     */     while (true) {
/*  57 */       int val = Integer.parseInt(tokens.readToken());
/*  58 */       if (idx >= this.m_lSize) grow(); 
/*  59 */       this.m_hArray[idx++] = val;
/*     */       
/*  61 */       ch = tokens.getCharToken();
/*  62 */       if (ch == ed) {
/*  63 */         this.m_lLen = idx;
/*     */         return;
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public int[] toArray() {
/*  70 */     int[] nArr = new int[this.m_lLen];
/*  71 */     System.arraycopy(this.m_hArray, 0, nArr, 0, this.m_lLen);
/*  72 */     return nArr;
/*     */   }
/*     */   
/*     */   public int[] getArray() {
/*  76 */     return this.m_hArray;
/*     */   }
/*     */   
/*     */   public void clear() {
/*  80 */     this.m_lSize = 0;
/*  81 */     this.m_hArray = null;
/*     */   }
/*     */   
/*     */   public void grow() {
/*  85 */     int nSize = this.m_lSize * 3 / 2 + 10;
/*  86 */     int[] nArr = new int[nSize];
/*  87 */     if (this.m_hArray != null) System.arraycopy(this.m_hArray, 0, nArr, 0, this.m_lSize); 
/*  88 */     this.m_lSize = nSize;
/*  89 */     this.m_hArray = nArr;
/*     */   }
/*     */   
/*     */   public String toString() {
/*  93 */     String str = "[";
/*  94 */     for (int ctr = 0; ctr < this.m_lLen; ctr++) {
/*  95 */       if (ctr != 0) str = str + ", "; 
/*  96 */       str = str + String.valueOf(this.m_hArray[ctr]);
/*     */     } 
/*  98 */     return str + "]";
/*     */   }
/*     */   
/*     */   public static String toString(int[] arr) {
/* 102 */     String str = "[";
/* 103 */     for (int ctr = 0; ctr < arr.length; ctr++) {
/* 104 */       if (ctr != 0) str = str + ", "; 
/* 105 */       str = str + String.valueOf(arr[ctr]);
/*     */     } 
/* 107 */     return str + "]";
/*     */   }
/*     */   
/*     */   public static void fillArray(int[] array, int val) {
/* 111 */     for (int i = 0; i < array.length; i++)
/* 112 */       array[i] = val; 
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jean\\util\array\MIntArray.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */