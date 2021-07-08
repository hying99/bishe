/*    */ package jeans.util;
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
/*    */ public class VectorUtils
/*    */ {
/*    */   public static String[] toStringArray(Vector<E> vec) {
/* 30 */     int size = vec.size();
/* 31 */     String[] array = new String[size];
/* 32 */     for (int i = 0; i < size; i++)
/* 33 */       array[i] = vec.elementAt(i).toString(); 
/* 34 */     return array;
/*    */   }
/*    */   
/*    */   public static int[] toIntArray(Vector vec) throws NumberFormatException {
/* 38 */     int size = vec.size();
/* 39 */     int[] array = new int[size];
/* 40 */     for (int i = 0; i < size; i++)
/* 41 */       array[i] = Integer.parseInt(vec.elementAt(i).toString()); 
/* 42 */     return array;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jean\\util\VectorUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */