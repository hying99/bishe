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
/*    */ public class CompleteTreeIterator
/*    */   extends TreeIterator
/*    */ {
/*    */   private boolean busy = true;
/*    */   private boolean advanced = true;
/*    */   
/*    */   public CompleteTreeIterator(Node node) {
/* 32 */     super(node);
/*    */   }
/*    */   
/*    */   public void reset() {
/* 36 */     super.reset();
/* 37 */     this.busy = true;
/* 38 */     this.advanced = true;
/*    */   }
/*    */   
/*    */   public Node getNextNode() {
/* 42 */     if (!this.advanced) gotoNextNode(); 
/* 43 */     this.advanced = false;
/* 44 */     return getNode();
/*    */   }
/*    */   
/*    */   public boolean hasMoreNodes() {
/* 48 */     if (!this.advanced) gotoNextNode(); 
/* 49 */     this.advanced = true;
/* 50 */     return this.busy;
/*    */   }
/*    */   
/*    */   private void gotoNextNode() {
/* 54 */     if (!atBottomLevel()) {
/* 55 */       goDown(0);
/*    */     } else {
/* 57 */       boolean done = false;
/* 58 */       while (!done) {
/* 59 */         if (atTopLevel()) {
/* 60 */           this.busy = false;
/* 61 */           done = true; continue;
/*    */         } 
/* 63 */         int crindex = getIndex();
/* 64 */         goUp();
/* 65 */         int children = getNode().getNbChildren();
/* 66 */         if (crindex + 1 < children) {
/* 67 */           goDown(crindex + 1);
/* 68 */           done = true;
/*    */         } 
/*    */       } 
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\jeans\tree\CompleteTreeIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */