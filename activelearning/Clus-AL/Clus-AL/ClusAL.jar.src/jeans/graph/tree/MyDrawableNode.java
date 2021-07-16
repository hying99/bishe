/*     */ package jeans.graph.tree;
/*     */ 
/*     */ import jeans.tree.MyVisitableNode;
/*     */ import jeans.tree.Node;
/*     */ import jeans.util.MyVisitorParent;
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
/*     */ public class MyDrawableNode
/*     */   extends MyVisitableNode
/*     */ {
/*     */   public static final long serialVersionUID = 1L;
/*     */   protected boolean m_bFakeLeaf;
/*     */   protected MyNodePainter m_Painter;
/*     */   protected int m_Index;
/*     */   
/*     */   public MyDrawableNode(MyVisitorParent vpar) {
/*  37 */     super(vpar);
/*     */   }
/*     */   
/*     */   public static MyDrawableNode createTree(Node node, MyNodePainter paint) {
/*  41 */     NodePainterSettings par = new NodePainterSettings();
/*  42 */     return createTree(node, par, paint);
/*     */   }
/*     */   
/*     */   public static MyDrawableNode createTree(Node node, NodePainterSettings par, MyNodePainter paint) {
/*  46 */     MyDrawableNode root = new MyDrawableNode(par);
/*  47 */     par.addVisitor();
/*  48 */     root.recursiveCreateTree(node, paint);
/*  49 */     return root;
/*     */   }
/*     */   
/*     */   public void deleteChild(int idx) {
/*  53 */     removeChild(idx);
/*  54 */     for (int i = 0; i < getNbChildren(); i++) {
/*  55 */       MyDrawableNode node = (MyDrawableNode)getChild(i);
/*  56 */       node.setIndex(idx);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void swapChildren(int ch1, int ch2) {
/*  61 */     MyDrawableNode child1 = (MyDrawableNode)getChild(ch1);
/*  62 */     MyDrawableNode child2 = (MyDrawableNode)getChild(ch2);
/*  63 */     setChild((Node)child2, ch1);
/*  64 */     setChild((Node)child1, ch2);
/*  65 */     child2.setIndex(ch1);
/*  66 */     child1.setIndex(ch2);
/*     */   }
/*     */   
/*     */   public int getIndex() {
/*  70 */     return this.m_Index;
/*     */   }
/*     */   
/*     */   public void setIndex(int index) {
/*  74 */     this.m_Index = index;
/*     */   }
/*     */   
/*     */   public NodePainterSettings getPaintSettings() {
/*  78 */     return (NodePainterSettings)this.m_VisParent;
/*     */   }
/*     */   
/*     */   public MyNodePainter getPainter() {
/*  82 */     return this.m_Painter;
/*     */   }
/*     */   
/*     */   public MyNodePainter getChildPainter(int i) {
/*  86 */     return ((MyDrawableNode)getChild(i)).getPainter();
/*     */   }
/*     */   
/*     */   public void setPainter(MyNodePainter paint) {
/*  90 */     this.m_Painter = paint;
/*     */   }
/*     */   
/*     */   public void setFakeLeaf(boolean fake) {
/*  94 */     this.m_bFakeLeaf = fake;
/*  95 */     this.m_Painter.onFakeLeaf(fake);
/*     */   }
/*     */   
/*     */   public boolean isFakeLeaf() {
/*  99 */     return this.m_bFakeLeaf;
/*     */   }
/*     */   
/*     */   public void recursiveFakeLeaf(boolean fake) {
/* 103 */     setFakeLeaf(fake);
/* 104 */     for (int i = 0; i < getNbChildren(); i++) {
/* 105 */       MyDrawableNode child = (MyDrawableNode)getChild(i);
/* 106 */       child.recursiveFakeLeaf(fake);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void recursiveZoom(int zoom) {
/* 111 */     setZoom(zoom);
/* 112 */     for (int i = 0; i < getNbChildren(); i++) {
/* 113 */       MyDrawableNode child = (MyDrawableNode)getChild(i);
/* 114 */       child.recursiveZoom(zoom);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void recursiveZoomSubtree(int zoom) {
/* 119 */     for (int i = 0; i < getNbChildren(); i++) {
/* 120 */       MyDrawableNode child = (MyDrawableNode)getChild(i);
/* 121 */       child.recursiveZoom(zoom);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void setZoom(int zoom) {
/* 126 */     if (this.m_Painter != null) this.m_Painter.setZoom(zoom); 
/*     */   }
/*     */   
/*     */   public int getZoom() {
/* 130 */     return (this.m_Painter != null) ? this.m_Painter.getZoom() : 0;
/*     */   }
/*     */   
/*     */   public boolean atFakeBottomLevel() {
/* 134 */     if (isFakeLeaf()) return true; 
/* 135 */     return atBottomLevel();
/*     */   }
/*     */   
/*     */   public int getNbFakeChildren() {
/* 139 */     if (isFakeLeaf()) return 0; 
/* 140 */     return getNbChildren();
/*     */   }
/*     */   
/*     */   public int getFakeDepth() {
/* 144 */     int nb = getNbChildren();
/* 145 */     if (nb == 0 || isFakeLeaf()) {
/* 146 */       return 1;
/*     */     }
/* 148 */     int max = 0;
/* 149 */     for (int i = 0; i < nb; i++) {
/* 150 */       MyDrawableNode node = (MyDrawableNode)getChild(i);
/* 151 */       max = Math.max(max, node.getFakeDepth());
/*     */     } 
/* 153 */     return max + 1;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 158 */     return getVisitor(0).toString();
/*     */   }
/*     */   
/*     */   private void recursiveCreateTree(Node node, MyNodePainter paint) {
/* 162 */     setVisitor(node, 0);
/* 163 */     setPainter(paint.createPainter(this));
/* 164 */     int arity = node.getNbChildren();
/* 165 */     setNbChildren(arity);
/* 166 */     for (int i = 0; i < arity; i++) {
/* 167 */       MyDrawableNode child = new MyDrawableNode(getVisParent());
/* 168 */       setChild((Node)child, i);
/* 169 */       child.setIndex(i);
/* 170 */       child.recursiveCreateTree(node.getChild(i), paint);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\jeans\graph\tree\MyDrawableNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */