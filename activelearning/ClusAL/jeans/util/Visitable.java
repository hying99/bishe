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
/*    */ public class Visitable
/*    */ {
/* 27 */   protected static int nbVisitors = 0;
/*    */   protected Object[] visitors;
/*    */   
/*    */   public static int addVisitorType() {
/* 31 */     return nbVisitors++;
/*    */   }
/*    */   
/*    */   public Visitable() {
/* 35 */     this.visitors = new Object[nbVisitors];
/*    */   }
/*    */   
/*    */   public void setVisitor(Object visitor, int type) {
/* 39 */     this.visitors[type] = visitor;
/*    */   }
/*    */   
/*    */   public Object getVisitor(int type) {
/* 43 */     return this.visitors[type];
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jean\\util\Visitable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */