/*    */ package jeans.tree;
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
/*    */ public class SimpleNode
/*    */   implements Node
/*    */ {
/* 29 */   protected Vector nodes = new Vector();
/*    */   protected Node parent;
/*    */   
/*    */   public SimpleNode() {
/* 33 */     this(null);
/*    */   }
/*    */ 
/*    */   
/*    */   public SimpleNode(Node parent) {
/* 38 */     setParent(parent);
/*    */   }
/*    */   
/*    */   public int getLevel() {
/* 42 */     return 0;
/*    */   }
/*    */   
/*    */   public void addChild(Node node) {
/* 46 */     node.setParent(this);
/* 47 */     this.nodes.addElement(node);
/*    */   }
/*    */   
/*    */   public void removeChild(Node node) {
/* 51 */     node.setParent(null);
/* 52 */     this.nodes.removeElement(node);
/*    */   }
/*    */   
/*    */   public Node getParent() {
/* 56 */     return this.parent;
/*    */   }
/*    */   
/*    */   public void setParent(Node parent) {
/* 60 */     this.parent = parent;
/*    */   }
/*    */   
/*    */   public Node getChild(int idx) {
/* 64 */     return this.nodes.elementAt(idx);
/*    */   }
/*    */   
/*    */   public int getNbChildren() {
/* 68 */     return this.nodes.size();
/*    */   }
/*    */   
/*    */   public boolean atTopLevel() {
/* 72 */     return (getParent() == null);
/*    */   }
/*    */   
/*    */   public boolean atBottomLevel() {
/* 76 */     return (getNbChildren() == 0);
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jeans\tree\SimpleNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */