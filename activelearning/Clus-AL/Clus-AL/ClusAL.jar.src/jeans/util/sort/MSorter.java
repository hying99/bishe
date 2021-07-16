/*    */ package jeans.util.sort;
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
/*    */ 
/*    */ public class MSorter
/*    */ {
/*    */   public static void quickSort(MSortable x, int off, int len) {
/* 30 */     if (len < 7) {
/* 31 */       for (int i = off; i < len + off; i++) {
/* 32 */         for (int j = i; j > off && x.getDouble(j - 1) <= x.getDouble(j); j--) {
/* 33 */           x.swap(j, j - 1);
/*    */         }
/*    */       } 
/*    */       return;
/*    */     } 
/* 38 */     int m = off + len / 2;
/* 39 */     if (len > 7) {
/* 40 */       int l = off;
/* 41 */       int i = off + len - 1;
/* 42 */       if (len > 40) {
/* 43 */         int j = len / 8;
/* 44 */         l = med3(x, l, l + j, l + 2 * j);
/* 45 */         m = med3(x, m - j, m, m + j);
/* 46 */         i = med3(x, i - 2 * j, i - j, i);
/*    */       } 
/* 48 */       m = med3(x, l, m, i);
/*    */     } 
/* 50 */     double v = x.getDouble(m);
/*    */ 
/*    */     
/* 53 */     int a = off, b = a, c = off + len - 1, d = c;
/*    */     while (true) {
/* 55 */       if (b <= c && x.getDouble(b) > v) {
/* 56 */         if (x.getDouble(b) == v)
/* 57 */           x.swap(a++, b); 
/* 58 */         b++; continue;
/*    */       } 
/* 60 */       while (c >= b && x.getDouble(c) < v) {
/* 61 */         if (x.getDouble(c) == v)
/* 62 */           x.swap(c, d--); 
/* 63 */         c--;
/*    */       } 
/* 65 */       if (b > c)
/*    */         break; 
/* 67 */       x.swap(b++, c--);
/*    */     } 
/*    */ 
/*    */     
/* 71 */     int n = off + len;
/* 72 */     int s = Math.min(a - off, b - a); vecswap(x, off, b - s, s);
/* 73 */     s = Math.min(d - c, n - d - 1); vecswap(x, b, n - s, s);
/*    */ 
/*    */     
/* 76 */     if ((s = b - a) > 1)
/* 77 */       quickSort(x, off, s); 
/* 78 */     if ((s = d - c) > 1) {
/* 79 */       quickSort(x, n - s, s);
/*    */     }
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   private static void vecswap(MSortable x, int a, int b, int n) {
/* 86 */     for (int i = 0; i < n; ) { x.swap(a, b); i++; a++; b++; }
/*    */   
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   private static int med3(MSortable x, int a, int b, int c) {
/* 93 */     double da = x.getDouble(a);
/* 94 */     double db = x.getDouble(b);
/* 95 */     double dc = x.getDouble(c);
/* 96 */     return (da >= db) ? ((db >= dc) ? b : ((da >= dc) ? c : a)) : ((db <= dc) ? b : ((da <= dc) ? c : a));
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\jean\\util\sort\MSorter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */