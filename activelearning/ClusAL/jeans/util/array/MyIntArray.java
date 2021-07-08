/*     */ package jeans.util.array;
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
/*     */ public class MyIntArray
/*     */ {
/*     */   private int[] m_Ints;
/*     */   private int m_Size;
/*     */   private int m_Mult;
/*     */   private int m_Div;
/*     */   
/*     */   public MyIntArray() {
/*  32 */     this.m_Ints = new int[0];
/*  33 */     this.m_Size = 0;
/*  34 */     this.m_Mult = 2;
/*  35 */     this.m_Div = 1;
/*     */   }
/*     */   
/*     */   public MyIntArray(int cap) {
/*  39 */     this.m_Ints = new int[cap];
/*  40 */     this.m_Size = 0;
/*  41 */     this.m_Mult = 2;
/*  42 */     this.m_Div = 1;
/*     */   }
/*     */   
/*     */   public MyIntArray(int cap, int mult, int div) {
/*  46 */     this.m_Ints = new int[cap];
/*  47 */     this.m_Size = 0;
/*  48 */     this.m_Mult = mult;
/*  49 */     this.m_Div = div;
/*     */   }
/*     */   
/*     */   public final int binarySearch(int key) {
/*  53 */     int low = 0;
/*  54 */     int high = this.m_Size - 1;
/*  55 */     while (low <= high) {
/*  56 */       int mid = (low + high) / 2;
/*  57 */       int midVal = this.m_Ints[mid];
/*  58 */       if (midVal < key) { low = mid + 1; continue; }
/*  59 */        if (midVal > key) { high = mid - 1; continue; }
/*  60 */        return mid;
/*     */     } 
/*  62 */     return -(low + 1);
/*     */   }
/*     */   
/*     */   public static int[] remove(int e, int[] a) {
/*  66 */     int idx = 0;
/*  67 */     int[] r = new int[a.length - 1];
/*  68 */     for (int i = 0; i < a.length; ) { if (a[i] != e) r[idx++] = a[i];  i++; }
/*  69 */      return r;
/*     */   }
/*     */   
/*     */   public static int[] mergeSorted(int[] a1, int[] a2) {
/*  73 */     int idx = 0;
/*  74 */     int a1p = 0;
/*  75 */     int a2p = 0;
/*  76 */     int[] a3 = new int[a1.length + a2.length];
/*  77 */     while (a1p < a1.length && a2p < a2.length) {
/*  78 */       if (a1[a1p] <= a2[a2p]) { a3[idx++] = a1[a1p++]; continue; }
/*  79 */        a3[idx++] = a2[a2p++];
/*     */     }  int i;
/*  81 */     for (i = a1p; i < a1.length; ) { a3[idx++] = a1[i]; i++; }
/*  82 */      for (i = a2p; i < a2.length; ) { a3[idx++] = a2[i]; i++; }
/*  83 */      return a3;
/*     */   }
/*     */   
/*     */   public static int isIntersectSorted(int[] a1, int[] a2) {
/*  87 */     int a1p = 0;
/*  88 */     int a2p = 0;
/*  89 */     while (a1p < a1.length && a2p < a2.length) {
/*  90 */       if (a1[a1p] == a2[a2p])
/*  91 */         return 1; 
/*  92 */       if (a1[a1p] < a2[a2p]) {
/*  93 */         a1p++; continue;
/*     */       } 
/*  95 */       a2p++;
/*     */     } 
/*     */     
/*  98 */     return 0;
/*     */   }
/*     */   
/*     */   public static int[] intersectSorted(int[] a1, int[] a2) {
/* 102 */     int nb = 0;
/* 103 */     int a1p = 0;
/* 104 */     int a2p = 0;
/* 105 */     while (a1p < a1.length && a2p < a2.length) {
/* 106 */       if (a1[a1p] == a2[a2p]) {
/* 107 */         nb++; a1p++; a2p++; continue;
/* 108 */       }  if (a1[a1p] < a2[a2p]) {
/* 109 */         a1p++; continue;
/*     */       } 
/* 111 */       a2p++;
/*     */     } 
/*     */     
/* 114 */     int idx = 0;
/* 115 */     a1p = a2p = 0;
/* 116 */     int[] r = new int[nb];
/* 117 */     if (nb > 0) {
/* 118 */       while (a1p < a1.length && a2p < a2.length) {
/* 119 */         if (a1[a1p] == a2[a2p]) {
/* 120 */           r[idx++] = a1[a1p];
/* 121 */           a1p++; a2p++; continue;
/* 122 */         }  if (a1[a1p] < a2[a2p]) {
/* 123 */           a1p++; continue;
/*     */         } 
/* 125 */         a2p++;
/*     */       } 
/*     */     }
/*     */     
/* 129 */     return r;
/*     */   }
/*     */   
/*     */   public static String print(int[] arr) {
/* 133 */     if (arr.length == 0) return "[]"; 
/* 134 */     StringBuffer buff = new StringBuffer();
/* 135 */     buff.append('[');
/* 136 */     buff.append(arr[0]);
/* 137 */     for (int i = 1; i < arr.length; i++) {
/* 138 */       buff.append(',');
/* 139 */       buff.append(arr[i]);
/*     */     } 
/* 141 */     buff.append(']');
/* 142 */     return buff.toString();
/*     */   }
/*     */   
/*     */   public void removeMinusOnes() {
/* 146 */     int delta = 0;
/* 147 */     for (int i = 0; i < this.m_Size; i++) {
/* 148 */       while (i < this.m_Size && this.m_Ints[i + delta] == -1) {
/* 149 */         delta++;
/* 150 */         this.m_Size--;
/*     */       } 
/* 152 */       if (i < this.m_Size) this.m_Ints[i] = this.m_Ints[i + delta];
/*     */     
/*     */     } 
/*     */   }
/*     */   
/*     */   public final void addElement(int element) {
/* 158 */     if (this.m_Size == this.m_Ints.length) {
/* 159 */       int[] newints = new int[this.m_Mult * this.m_Ints.length / this.m_Div + 1];
/* 160 */       System.arraycopy(this.m_Ints, 0, newints, 0, this.m_Size);
/* 161 */       this.m_Ints = newints;
/*     */     } 
/* 163 */     this.m_Ints[this.m_Size++] = element;
/*     */   }
/*     */   
/*     */   public final int elementAt(int index) {
/* 167 */     return this.m_Ints[index];
/*     */   }
/*     */   
/*     */   public final int[] elements() {
/* 171 */     return this.m_Ints;
/*     */   }
/*     */ 
/*     */   
/*     */   public final void insertElementAt(int element, int index) {
/* 176 */     if (this.m_Size < this.m_Ints.length) {
/* 177 */       System.arraycopy(this.m_Ints, index, this.m_Ints, index + 1, this.m_Size - index);
/* 178 */       this.m_Ints[index] = element;
/*     */     } else {
/* 180 */       int[] nObjs = new int[this.m_Mult * this.m_Ints.length / this.m_Div + 1];
/* 181 */       System.arraycopy(this.m_Ints, 0, nObjs, 0, index);
/* 182 */       nObjs[index] = element;
/* 183 */       System.arraycopy(this.m_Ints, index, nObjs, index + 1, this.m_Size - index);
/* 184 */       this.m_Ints = nObjs;
/*     */     } 
/* 186 */     this.m_Size++;
/*     */   }
/*     */   
/*     */   public final void removeElementAt(int index) {
/* 190 */     System.arraycopy(this.m_Ints, index + 1, this.m_Ints, index, this.m_Size - index - 1);
/*     */   }
/*     */   
/*     */   public final void removeAllElements() {
/* 194 */     this.m_Size = 0;
/*     */   }
/*     */   
/*     */   public final void setElementAt(int element, int index) {
/* 198 */     this.m_Ints[index] = element;
/* 199 */     if (index >= this.m_Size) this.m_Size = index + 1; 
/*     */   }
/*     */   
/*     */   public final int size() {
/* 203 */     return this.m_Size;
/*     */   }
/*     */   
/*     */   public final void setCapacity(int cap) {
/* 207 */     int[] newints = new int[cap];
/* 208 */     System.arraycopy(this.m_Ints, 0, newints, 0, Math.min(cap, this.m_Size));
/* 209 */     this.m_Ints = newints;
/* 210 */     if (this.m_Ints.length < this.m_Size) this.m_Size = this.m_Ints.length; 
/*     */   }
/*     */   
/*     */   public final void swap(int first, int second) {
/* 214 */     int help = this.m_Ints[first];
/* 215 */     this.m_Ints[first] = this.m_Ints[second];
/* 216 */     this.m_Ints[second] = help;
/*     */   }
/*     */   
/*     */   public final void trimToSize() {
/* 220 */     int[] newints = new int[this.m_Size];
/* 221 */     System.arraycopy(this.m_Ints, 0, newints, 0, this.m_Size);
/* 222 */     this.m_Ints = newints;
/*     */   }
/*     */   
/*     */   public String toString() {
/* 226 */     StringBuffer buf = new StringBuffer();
/* 227 */     buf.append("[");
/* 228 */     for (int i = 0; i < this.m_Size; i++) {
/* 229 */       if (i != 0) buf.append(","); 
/* 230 */       buf.append(String.valueOf(this.m_Ints[i]));
/*     */     } 
/* 232 */     buf.append("]");
/* 233 */     return buf.toString();
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jean\\util\array\MyIntArray.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */