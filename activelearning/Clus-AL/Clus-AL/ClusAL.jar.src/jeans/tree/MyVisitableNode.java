/*    */ package jeans.tree;
/*    */ 
/*    */ import jeans.util.MyArray;
/*    */ import jeans.util.MyVisitorParent;
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
/*    */ public class MyVisitableNode
/*    */   extends MyNode
/*    */ {
/*    */   public static final long serialVersionUID = 1L;
/*    */   protected MyVisitorParent m_VisParent;
/*    */   protected MyArray m_Visitors;
/*    */   
/*    */   public MyVisitableNode() {
/* 35 */     this.m_VisParent = new MyVisitorParent();
/*    */   }
/*    */   
/*    */   public MyVisitableNode(MyVisitorParent vpar) {
/* 39 */     this.m_VisParent = vpar;
/*    */   }
/*    */   
/*    */   public MyVisitorParent getVisParent() {
/* 43 */     return this.m_VisParent;
/*    */   }
/*    */   
/*    */   public int addVisitor() {
/* 47 */     return this.m_VisParent.addVisitor();
/*    */   }
/*    */   
/*    */   public void setVisitor(Object visitor, int pos) {
/* 51 */     if (this.m_Visitors == null) this.m_Visitors = new MyArray(); 
/* 52 */     if (pos >= this.m_Visitors.size()) this.m_Visitors.setSize(pos + 1); 
/* 53 */     this.m_Visitors.setElementAt(visitor, pos);
/*    */   }
/*    */   
/*    */   public Object getVisitor(int pos) {
/* 57 */     return this.m_Visitors.elementAt(pos);
/*    */   }
/*    */   
/*    */   public void removeVisitor(int pos) {
/* 61 */     this.m_VisParent.removeVisitor(pos);
/* 62 */     recursiveRemoveVisitor(pos);
/*    */   }
/*    */   
/*    */   protected void recursiveRemoveVisitor(int pos) {}
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\jeans\tree\MyVisitableNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */