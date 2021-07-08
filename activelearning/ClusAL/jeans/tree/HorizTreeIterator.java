/*     */ package jeans.tree;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class HorizTreeIterator
/*     */ {
/*     */   private int[] childps;
/*     */   private Node[] stack;
/*     */   private int pos;
/*     */   private int level;
/*     */   private int rootdepth;
/*     */   private boolean tonext = true;
/*     */   private boolean done = false;
/*     */   
/*     */   public HorizTreeIterator(Node tree, int depth, int level) {
/*  35 */     this.level = level;
/*  36 */     this.childps = new int[level + 1];
/*  37 */     this.stack = new Node[level + 1];
/*  38 */     this.rootdepth = depth;
/*  39 */     this.pos = 0;
/*  40 */     push(0, tree);
/*  41 */     climbTillLevel();
/*     */   }
/*     */   
/*     */   public int getRootDepth() {
/*  45 */     return this.rootdepth;
/*     */   }
/*     */   
/*     */   public Node getNextNode() {
/*  49 */     if (!this.tonext) gotoNextNode(); 
/*  50 */     this.tonext = false;
/*  51 */     return prevNode();
/*     */   }
/*     */   
/*     */   public boolean isDone() {
/*  55 */     if (!this.tonext) gotoNextNode(); 
/*  56 */     return this.done;
/*     */   }
/*     */   
/*     */   public Node getParent() {
/*  60 */     if (this.pos < 2) return null; 
/*  61 */     return this.stack[this.pos - 2];
/*     */   }
/*     */ 
/*     */   
/*     */   public Node getNodeAtLevel(int level) {
/*  66 */     int mylevel = level - getRootDepth();
/*  67 */     if (mylevel < 0) return null; 
/*  68 */     return this.stack[mylevel];
/*     */   }
/*     */ 
/*     */   
/*     */   public int getChildAtLevel(int level) {
/*  73 */     return this.childps[level - getRootDepth()];
/*     */   }
/*     */   
/*     */   private void gotoNextNode() {
/*  77 */     if (backTrackTillChild())
/*  78 */       climbTillLevel(); 
/*  79 */     this.tonext = true;
/*     */   }
/*     */   
/*     */   private boolean backTrackTillChild() {
/*  83 */     boolean res = true;
/*  84 */     pop();
/*  85 */     if (isEmpty()) {
/*  86 */       res = false;
/*     */     } else {
/*  88 */       Node node = prevNode();
/*  89 */       int nbchild = node.getNbChildren();
/*  90 */       int child = prevNum() + 1;
/*  91 */       pop();
/*  92 */       while (!isEmpty() && child >= nbchild) {
/*  93 */         node = prevNode();
/*  94 */         nbchild = node.getNbChildren();
/*  95 */         child = prevNum() + 1;
/*  96 */         pop();
/*     */       } 
/*  98 */       push(child, node);
/*  99 */       if (child < nbchild) { push(0, node.getChild(child)); }
/* 100 */       else { res = false; }
/*     */     
/* 102 */     }  if (!res) this.done = true; 
/* 103 */     return res;
/*     */   }
/*     */   
/*     */   private void climbTillLevel() {
/* 107 */     Node parent = prevNode();
/* 108 */     while (this.pos <= this.level && parent != null) {
/* 109 */       if (!parent.atBottomLevel()) {
/* 110 */         parent = parent.getChild(0);
/* 111 */         push(0, parent); continue;
/*     */       } 
/* 113 */       parent = null;
/*     */     } 
/*     */     
/* 116 */     if (parent == null) gotoNextNode(); 
/*     */   }
/*     */   
/*     */   private boolean isEmpty() {
/* 120 */     return (this.pos == 0);
/*     */   }
/*     */   
/*     */   private void push(int num, Node node) {
/* 124 */     this.childps[this.pos] = num;
/* 125 */     this.stack[this.pos] = node;
/* 126 */     this.pos++;
/*     */   }
/*     */   
/*     */   private Node prevNode() {
/* 130 */     return this.stack[this.pos - 1];
/*     */   }
/*     */   
/*     */   private int prevNum() {
/* 134 */     return this.childps[this.pos - 1];
/*     */   }
/*     */   
/*     */   private void pop() {
/* 138 */     this.pos--;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jeans\tree\HorizTreeIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */