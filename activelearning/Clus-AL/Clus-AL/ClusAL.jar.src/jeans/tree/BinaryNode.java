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
/*     */ public class BinaryNode
/*     */   implements Node
/*     */ {
/*     */   String m_Name;
/*     */   Object m_Cargo;
/*     */   BinaryNode m_Left;
/*     */   BinaryNode m_Right;
/*     */   Node m_Parent;
/*     */   boolean m_bFakeLeaf;
/*     */   
/*     */   public void setName(String name) {
/*  34 */     this.m_Name = name;
/*     */   }
/*     */   
/*     */   public int getLevel() {
/*  38 */     int depth = 0;
/*  39 */     BinaryNode node = this;
/*  40 */     while (!node.atTopLevel()) {
/*  41 */       node = (BinaryNode)node.getParent();
/*  42 */       depth++;
/*     */     } 
/*  44 */     return depth;
/*     */   }
/*     */   
/*     */   public void setFakeLeaf(boolean fake) {
/*  48 */     this.m_bFakeLeaf = fake;
/*     */   }
/*     */   
/*     */   public boolean isFakeLeaf() {
/*  52 */     return this.m_bFakeLeaf;
/*     */   }
/*     */   
/*     */   public String getName() {
/*  56 */     return this.m_Name;
/*     */   }
/*     */   
/*     */   public void setCargo(Object cargo) {
/*  60 */     this.m_Cargo = cargo;
/*     */   }
/*     */   
/*     */   public Object getCargo() {
/*  64 */     return this.m_Cargo;
/*     */   }
/*     */   
/*     */   public void setLeft(BinaryNode node) {
/*  68 */     this.m_Left = node;
/*  69 */     node.setParent(this);
/*     */   }
/*     */   
/*     */   public BinaryNode getLeft() {
/*  73 */     return this.m_Left;
/*     */   }
/*     */   
/*     */   public void setRight(BinaryNode node) {
/*  77 */     this.m_Right = node;
/*  78 */     node.setParent(this);
/*     */   }
/*     */   
/*     */   public BinaryNode getRight() {
/*  82 */     return this.m_Right;
/*     */   }
/*     */   
/*     */   public Node getChild(int idx) {
/*  86 */     if (idx == 0) return this.m_Left; 
/*  87 */     return this.m_Right;
/*     */   }
/*     */   
/*     */   public int getNbChildren() {
/*  91 */     return atBottomLevel() ? 0 : 2;
/*     */   }
/*     */ 
/*     */   
/*     */   public void addChild(Node node) {}
/*     */   
/*     */   public void removeChild(Node node) {
/*  98 */     if (node == this.m_Left) this.m_Left = null; 
/*  99 */     if (node == this.m_Right) this.m_Right = null; 
/*     */   }
/*     */   
/*     */   public void setParent(Node parent) {
/* 103 */     this.m_Parent = parent;
/*     */   }
/*     */   
/*     */   public Node getParent() {
/* 107 */     return this.m_Parent;
/*     */   }
/*     */   
/*     */   public boolean atTopLevel() {
/* 111 */     return (this.m_Parent == null);
/*     */   }
/*     */   
/*     */   public boolean atBottomLevel() {
/* 115 */     return (this.m_bFakeLeaf || atRealBottomLevel());
/*     */   }
/*     */   
/*     */   public boolean atRealBottomLevel() {
/* 119 */     return (this.m_Left == null && this.m_Right == null);
/*     */   }
/*     */   
/*     */   public static int maxHeight(BinaryNode node) {
/* 123 */     if (node.atRealBottomLevel()) {
/* 124 */       return 0;
/*     */     }
/* 126 */     int ld = maxHeight(node.getLeft());
/* 127 */     int rd = maxHeight(node.getRight());
/* 128 */     return Math.max(ld, rd) + 1;
/*     */   }
/*     */ 
/*     */   
/*     */   public static int getHeight(BinaryNode node) {
/* 133 */     if (node.atBottomLevel()) {
/* 134 */       return 0;
/*     */     }
/* 136 */     int ld = getHeight(node.getLeft());
/* 137 */     int rd = getHeight(node.getRight());
/* 138 */     return Math.max(ld, rd) + 1;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\jeans\tree\BinaryNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */