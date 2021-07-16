/*    */ package jeans.io.filesys;
/*    */ 
/*    */ import jeans.util.sort.MComparator;
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
/*    */ public class MFileSorter
/*    */   implements MComparator
/*    */ {
/*    */   public int compare(Object obj1, Object obj2) {
/* 30 */     String n1 = ((MFileEntry)obj1).getName();
/* 31 */     String n2 = ((MFileEntry)obj2).getName();
/* 32 */     return n2.compareTo(n1);
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\jeans\io\filesys\MFileSorter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */