/*    */ package jeans.tree;
/*    */ 
/*    */ import jeans.util.IntegerStack;
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
/*    */ public class TreeIterator
/*    */ {
/*    */   private Node node;
/*    */   private IntegerStack stack;
/*    */   private int index;
/*    */   
/*    */   public TreeIterator(Node node) {
/* 34 */     this.index = 0;
/* 35 */     this.node = node;
/* 36 */     this.stack = new IntegerStack();
/*    */   }
/*    */   
/*    */   public TreeIterator(TreeIterator iter) {
/* 40 */     this.index = iter.index;
/* 41 */     this.node = iter.node;
/* 42 */     this.stack = new IntegerStack(iter.stack);
/*    */   }
/*    */   
/*    */   public void reset() {
/* 46 */     this.stack.clear();
/* 47 */     this.index = 0;
/*    */   }
/*    */   
/*    */   public boolean atBottomLevel() {
/* 51 */     return this.node.atBottomLevel();
/*    */   }
/*    */   
/*    */   public boolean atTopLevel() {
/* 55 */     return this.stack.isEmpty();
/*    */   }
/*    */   
/*    */   public Node getNode() {
/* 59 */     return this.node;
/*    */   }
/*    */   
/*    */   public int getIndex() {
/* 63 */     return this.index;
/*    */   }
/*    */   
/*    */   public int getLevel() {
/* 67 */     return this.stack.getSize();
/*    */   }
/*    */   
/*    */   public void goUp() {
/* 71 */     this.node = this.node.getParent();
/* 72 */     this.index = this.stack.pop();
/*    */   }
/*    */   
/*    */   public void goDown(int child) {
/* 76 */     this.stack.push(this.index);
/* 77 */     this.node = this.node.getChild(child);
/* 78 */     this.index = child;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jeans\tree\TreeIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */