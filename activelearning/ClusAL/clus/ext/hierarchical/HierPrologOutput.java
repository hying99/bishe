/*    */ package clus.ext.hierarchical;
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
/*    */ public class HierPrologOutput
/*    */ {
/*    */   public static String termToIndexList(ClassTerm term) {
/* 29 */     String res = String.valueOf(term.getIndex()) + "]";
/* 30 */     ClassTerm cr = term.getCTParent();
/* 31 */     while (cr != null) {
/* 32 */       if (cr.getCTParent() != null) res = cr.getIndex() + "," + res; 
/* 33 */       cr = cr.getCTParent();
/*    */     } 
/* 35 */     return "[" + res;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\ext\hierarchical\HierPrologOutput.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */