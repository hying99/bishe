/*    */ package jeans.tree;
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
/*    */ public class LeafTreeIterator
/*    */   extends TreeIterator
/*    */ {
/*    */   private boolean busy = true;
/*    */   private boolean advanced = false;
/* 31 */   private int cr_level = 0;
/* 32 */   private Node cr_node = null;
/*    */   
/*    */   public LeafTreeIterator(Node node) {
/* 35 */     super(node);
/*    */   }
/*    */   
/*    */   public LeafTreeIterator(LeafTreeIterator other) {
/* 39 */     super(other);
/* 40 */     this.busy = other.busy;
/* 41 */     this.advanced = other.advanced;
/* 42 */     this.cr_level = other.cr_level;
/* 43 */     this.cr_node = other.cr_node;
/*    */   }
/*    */   
/*    */   public void reset() {
/* 47 */     super.reset();
/* 48 */     this.busy = true;
/* 49 */     this.advanced = false;
/*    */   }
/*    */   
/*    */   public Node getNextNode() {
/* 53 */     if (!this.advanced) gotoNextNode(); 
/* 54 */     this.advanced = false;
/* 55 */     return this.cr_node;
/*    */   }
/*    */   
/*    */   public boolean hasMoreNodes() {
/* 59 */     if (!this.advanced) gotoNextNode(); 
/* 60 */     this.advanced = true;
/* 61 */     return (this.cr_node != null);
/*    */   }
/*    */   
/*    */   public void gotoNextNode() {
/* 65 */     if (this.busy) {
/* 66 */       descentTillBottom();
/* 67 */       this.cr_node = getNode();
/* 68 */       climbTillDescision();
/*    */     } else {
/* 70 */       this.cr_node = null;
/*    */     } 
/*    */   }
/*    */   
/*    */   public int getLevel() {
/* 75 */     return this.cr_level;
/*    */   }
/*    */   
/*    */   private void descentTillBottom() {
/* 79 */     while (!atBottomLevel()) {
/* 80 */       goDown(0);
/* 81 */       this.cr_level++;
/*    */     } 
/*    */   }
/*    */   
/*    */   private void climbTillDescision() {
/*    */     while (true) {
/* 87 */       if (this.cr_level <= 0) {
/* 88 */         this.busy = false;
/*    */         return;
/*    */       } 
/* 91 */       int num = getIndex();
/* 92 */       goUp();
/* 93 */       this.cr_level--;
/* 94 */       if (num < getNode().getNbChildren() - 1) {
/* 95 */         goDown(num + 1);
/* 96 */         this.cr_level++;
/*    */         return;
/*    */       } 
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jeans\tree\LeafTreeIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */