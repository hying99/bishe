/*    */ package jeans.util;
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
/*    */ public class MyVisitorParent
/*    */ {
/*    */   protected int m_NbVisitors;
/* 28 */   protected MyArray m_FreeIDs = new MyArray();
/*    */   
/*    */   public int addVisitor() {
/* 31 */     int nbFree = this.m_FreeIDs.size();
/* 32 */     if (nbFree > 0) {
/* 33 */       Integer pos = (Integer)this.m_FreeIDs.elementAt(nbFree - 1);
/* 34 */       this.m_FreeIDs.removeElementAt(nbFree - 1);
/* 35 */       return pos.intValue();
/*    */     } 
/* 37 */     return this.m_NbVisitors++;
/*    */   }
/*    */   
/*    */   public void removeVisitor(int pos) {}
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\jean\\util\MyVisitorParent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */