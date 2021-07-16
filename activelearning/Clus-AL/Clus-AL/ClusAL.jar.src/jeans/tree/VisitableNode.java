/*    */ package jeans.tree;
/*    */ 
/*    */ import java.util.Vector;
/*    */ import jeans.util.Visitable;
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
/*    */ public class VisitableNode
/*    */   extends Visitable
/*    */   implements Node
/*    */ {
/* 30 */   protected Vector nodes = new Vector();
/*    */   
/*    */   protected Node parent;
/*    */   
/*    */   public VisitableNode(Node parent) {
/* 35 */     this.parent = parent;
/*    */   }
/*    */   
/*    */   public int getLevel() {
/* 39 */     return 0;
/*    */   }
/*    */   
/*    */   public void addChild(Node node) {
/* 43 */     node.setParent(this);
/* 44 */     this.nodes.addElement(node);
/*    */   }
/*    */   
/*    */   public void removeChild(Node node) {
/* 48 */     node.setParent(null);
/* 49 */     this.nodes.removeElement(node);
/*    */   }
/*    */   
/*    */   public void setParent(Node parent) {
/* 53 */     this.parent = parent;
/*    */   }
/*    */   
/*    */   public Node getParent() {
/* 57 */     return this.parent;
/*    */   }
/*    */   
/*    */   public Node getChild(int idx) {
/* 61 */     return this.nodes.elementAt(idx);
/*    */   }
/*    */   
/*    */   public int getNbChildren() {
/* 65 */     return this.nodes.size();
/*    */   }
/*    */   
/*    */   public boolean atTopLevel() {
/* 69 */     return (getParent() == null);
/*    */   }
/*    */   
/*    */   public boolean atBottomLevel() {
/* 73 */     return (getNbChildren() == 0);
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\jeans\tree\VisitableNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */