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
/*    */ public class BottomLevelIterator
/*    */ {
/*    */   private TreeIterator iterator;
/*    */   private int cr_level;
/*    */   private boolean busy = true;
/*    */   
/*    */   public BottomLevelIterator(TreeIterator iterator) {
/* 33 */     this.iterator = iterator;
/* 34 */     this.cr_level = 0;
/*    */   }
/*    */   
/*    */   public Node getNextNode() {
/* 38 */     descentTillBottom();
/* 39 */     Node node = this.iterator.getNode();
/* 40 */     climbTillDescision();
/* 41 */     return node;
/*    */   }
/*    */   
/*    */   public boolean hasMoreAreas() {
/* 45 */     return this.busy;
/*    */   }
/*    */   
/*    */   public int getLevel() {
/* 49 */     return this.cr_level;
/*    */   }
/*    */   
/*    */   private void descentTillBottom() {
/* 53 */     while (!this.iterator.atBottomLevel()) {
/* 54 */       this.iterator.goDown(1);
/* 55 */       this.cr_level++;
/*    */     } 
/*    */   }
/*    */   
/*    */   private void climbTillDescision() {
/*    */     while (true) {
/* 61 */       if (this.cr_level <= 0) {
/* 62 */         this.busy = false;
/*    */         return;
/*    */       } 
/* 65 */       int num = this.iterator.getIndex();
/* 66 */       this.iterator.goUp();
/* 67 */       this.cr_level--;
/* 68 */       if (num < this.iterator.getNode().getNbChildren()) {
/* 69 */         this.iterator.goDown(num + 1);
/* 70 */         this.cr_level++;
/*    */         return;
/*    */       } 
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jeans\tree\BottomLevelIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */