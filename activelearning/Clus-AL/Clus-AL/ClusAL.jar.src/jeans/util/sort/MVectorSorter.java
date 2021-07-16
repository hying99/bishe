/*    */ package jeans.util.sort;
/*    */ 
/*    */ import java.util.Vector;
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
/*    */ public class MVectorSorter
/*    */ {
/*    */   public static void quickSort(Vector vec, MComparator comp) {
/* 30 */     quickSort(vec, 0, vec.size() - 1, comp);
/*    */   }
/*    */   
/*    */   public static void quickSort(Vector vec, int low, int high, MComparator comp) {
/* 34 */     if (low < high) {
/* 35 */       int mid = split(vec, low, high, comp);
/* 36 */       quickSort(vec, low, mid - 1, comp);
/* 37 */       quickSort(vec, mid + 1, high, comp);
/*    */     } 
/*    */   }
/*    */   
/*    */   public static int split(Vector vec, int low, int high, MComparator comp) {
/* 42 */     int left = low;
/* 43 */     int right = high;
/* 44 */     while (left < right) {
/* 45 */       while (compareObjects(vec, right, low, comp) <= -1)
/* 46 */         right--; 
/* 47 */       while (left < right && compareObjects(vec, left, low, comp) >= 0)
/* 48 */         left++; 
/* 49 */       if (left < right) swapObjects(vec, left, right); 
/*    */     } 
/* 51 */     int mid = right;
/* 52 */     swapObjects(vec, mid, low);
/* 53 */     return mid;
/*    */   }
/*    */   
/*    */   public static int compareObjects(Vector vec, int idx1, int idx2, MComparator comp) {
/* 57 */     Object el1 = vec.elementAt(idx1);
/* 58 */     Object el2 = vec.elementAt(idx2);
/* 59 */     return comp.compare(el1, el2);
/*    */   }
/*    */   
/*    */   public static void swapObjects(Vector<Object> vec, int idx1, int idx2) {
/* 63 */     Object el = vec.elementAt(idx1);
/* 64 */     vec.setElementAt(vec.elementAt(idx2), idx1);
/* 65 */     vec.setElementAt(el, idx2);
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\jean\\util\sort\MVectorSorter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */