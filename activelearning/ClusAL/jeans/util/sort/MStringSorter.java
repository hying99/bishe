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
/*    */ public class MStringSorter
/*    */   implements MComparator
/*    */ {
/*    */   protected static MStringSorter m_hInstance;
/*    */   
/*    */   public static MStringSorter getInstance() {
/* 30 */     if (m_hInstance == null) m_hInstance = new MStringSorter(); 
/* 31 */     return m_hInstance;
/*    */   }
/*    */   
/*    */   public int compare(Object obj1, Object obj2) {
/* 35 */     String n1 = obj1.toString();
/* 36 */     String n2 = obj2.toString();
/* 37 */     return n2.compareTo(n1);
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jean\\util\sort\MStringSorter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */