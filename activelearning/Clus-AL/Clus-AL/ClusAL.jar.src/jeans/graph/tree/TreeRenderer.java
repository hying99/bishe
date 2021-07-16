/*     */ package jeans.graph.tree;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.FontMetrics;
/*     */ import java.awt.Graphics2D;
/*     */ import java.util.ArrayList;
/*     */ import jeans.graph.swing.drawable.Drawable;
/*     */ import jeans.graph.swing.drawable.DrawableCanvas;
/*     */ import jeans.graph.swing.drawable.DrawableExpandButton;
/*     */ import jeans.graph.swing.drawable.DrawableRectangle;
/*     */ import jeans.graph.swing.drawable.DrawableRenderer;
/*     */ import jeans.tree.IntervalTreeNodeRB;
/*     */ import jeans.tree.IntervalTreeRB;
/*     */ import jeans.util.Executer;
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
/*     */ public class TreeRenderer
/*     */   implements DrawableRenderer
/*     */ {
/*     */   protected MyDrawableNode m_Node;
/*     */   protected Dimension m_Size;
/*     */   protected boolean m_Vert;
/*  37 */   protected ArrayList m_Debug = new ArrayList();
/*     */   protected int m_Levels;
/*     */   
/*     */   public TreeRenderer(MyDrawableNode node) {
/*  41 */     this.m_Node = node;
/*  42 */     this.m_Vert = true;
/*     */   }
/*     */   
/*     */   public void setZoomLevels(int levels) {
/*  46 */     this.m_Levels = levels;
/*     */   }
/*     */   
/*     */   public void setHorzVert(boolean horz) {
/*  50 */     this.m_Vert = !horz;
/*     */   }
/*     */   
/*     */   public void render(Graphics2D g, FontMetrics fm, DrawableCanvas canvas) {
/*  54 */     calcSizes(this.m_Node, g, fm, canvas);
/*  55 */     int depth = this.m_Node.getMaxLeafDepth();
/*  56 */     MyNodePainter paint = this.m_Node.getPainter();
/*  57 */     paint.setXY(0, 0);
/*  58 */     Renderer renderer = new Renderer(depth, true, this.m_Levels);
/*  59 */     renderer.calcHi(this.m_Node, 0);
/*  60 */     NodePainterSettings sett = this.m_Node.getPaintSettings();
/*  61 */     renderer.positionY(this.m_Node, 0, sett.YTOP);
/*  62 */     renderer.render(this.m_Node);
/*  63 */     renderer.moveTop(this.m_Node, sett.XLEFT);
/*  64 */     this.m_Size = canvas.transformDimension(renderer.getSize(this.m_Node));
/*     */   }
/*     */   
/*     */   public class Renderer {
/*     */     int m_MaxHi;
/*     */     int[][] m_Hi;
/*     */     int[][] m_LHi;
/*     */     IntervalTreeRB m_ITree;
/*     */     
/*     */     public Renderer(int depth, boolean calcHi, int levels) {
/*  74 */       if (calcHi) {
/*  75 */         this.m_Hi = new int[depth + 1][levels];
/*  76 */         this.m_LHi = new int[depth + 1][levels];
/*     */       } 
/*  78 */       this.m_ITree = new IntervalTreeRB();
/*  79 */       this.m_MaxHi = Integer.MIN_VALUE;
/*  80 */       TreeRenderer.this.m_Debug.clear();
/*     */     }
/*     */     
/*     */     public Dimension getSize(MyDrawableNode node) {
/*  84 */       NodePainterSettings sett = TreeRenderer.this.m_Node.getPaintSettings();
/*  85 */       int wd = this.m_ITree.findMax(sett.XLEFT);
/*  86 */       if (TreeRenderer.this.m_Vert) {
/*  87 */         return new Dimension(wd, this.m_MaxHi);
/*     */       }
/*  89 */       return new Dimension(this.m_MaxHi, wd);
/*     */     }
/*     */ 
/*     */     
/*     */     public void calcHi(MyDrawableNode node, int depth) {
/*  94 */       MyNodePainter pdr = node.getPainter();
/*  95 */       Drawable label = pdr.getLabel();
/*  96 */       int level = pdr.getZoom();
/*  97 */       if (label != null)
/*  98 */         this.m_LHi[depth][level] = Math.max(this.m_LHi[depth][level], TreeRenderer.this.getHeight(label)); 
/*  99 */       this.m_Hi[depth][level] = Math.max(this.m_Hi[depth][level], TreeRenderer.this.getHeight(pdr));
/* 100 */       for (int i = 0; i < node.getNbFakeChildren(); i++) {
/* 101 */         MyDrawableNode child = (MyDrawableNode)node.getChild(i);
/* 102 */         calcHi(child, depth + 1);
/*     */       } 
/*     */     }
/*     */     
/*     */     public void positionY(MyDrawableNode node, int depth, int ypos) {
/* 107 */       MyNodePainter pdr = node.getPainter();
/* 108 */       TreeRenderer.this.setY(pdr, ypos);
/* 109 */       NodePainterSettings sett = node.getPaintSettings();
/*     */       
/* 111 */       int labelhi = 0;
/* 112 */       for (int i = 0; i < node.getNbFakeChildren(); i++) {
/* 113 */         MyDrawableNode child = (MyDrawableNode)node.getChild(i);
/* 114 */         labelhi = Math.max(labelhi, this.m_LHi[depth + 1][child.getZoom()]);
/*     */       } 
/*     */       
/* 117 */       int maxhi = 0;
/* 118 */       MyDrawableNode parent = (MyDrawableNode)node.getParent();
/* 119 */       if (parent == null) {
/* 120 */         maxhi = this.m_Hi[depth][node.getZoom()];
/*     */       } else {
/* 122 */         for (int k = 0; k < parent.getNbFakeChildren(); k++) {
/* 123 */           MyDrawableNode sibling = (MyDrawableNode)parent.getChild(k);
/* 124 */           maxhi = Math.max(maxhi, this.m_Hi[depth][sibling.getZoom()]);
/*     */         } 
/*     */       } 
/*     */       
/* 128 */       ypos += sett.YGAP + maxhi + labelhi;
/* 129 */       if (ypos > this.m_MaxHi) {
/* 130 */         this.m_MaxHi = ypos;
/*     */       }
/* 132 */       for (int j = 0; j < node.getNbFakeChildren(); j++) {
/* 133 */         MyDrawableNode child = (MyDrawableNode)node.getChild(j);
/* 134 */         positionY(child, depth + 1, ypos);
/*     */       } 
/*     */     }
/*     */     
/*     */     public void render(MyDrawableNode node) {
/* 139 */       if (!node.atFakeBottomLevel()) {
/* 140 */         positionNode(node);
/*     */       }
/*     */       
/* 143 */       for (int i = 0; i < node.getNbFakeChildren(); i++) {
/* 144 */         MyDrawableNode child = (MyDrawableNode)node.getChild(i);
/* 145 */         render(child);
/*     */       } 
/*     */     }
/*     */     
/*     */     private void moveTop(MyDrawableNode node, int gap) {
/* 150 */       MyNodePainter pdr = node.getPainter();
/* 151 */       int xpos = TreeRenderer.this.getX(pdr);
/* 152 */       if (xpos < gap) moveNodeRecursive(node, gap - xpos);
/*     */     
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private void positionNode(MyDrawableNode node) {
/* 164 */       MyNodePainter pdr = node.getPainter();
/*     */       
/* 166 */       int toLeft = 0;
/* 167 */       int nbChildren = node.getNbFakeChildren();
/* 168 */       NodePainterSettings sett = node.getPaintSettings();
/* 169 */       for (int i = 0; i < nbChildren / 2; i++) {
/* 170 */         MyNodePainter cdr = node.getChildPainter(i);
/* 171 */         toLeft += getWidthP(cdr);
/* 172 */         if (i < nbChildren - 1) {
/* 173 */           MyNodePainter ch2dr = node.getChildPainter(i + 1);
/* 174 */           int xgap = Math.max(sett.XGAP[cdr.getZoom()], sett.XGAP[ch2dr.getZoom()]);
/* 175 */           if (i == nbChildren / 2 - 1 && nbChildren % 2 == 0) {
/* 176 */             toLeft += xgap / 2;
/*     */           } else {
/* 178 */             toLeft += xgap;
/*     */           } 
/*     */         } 
/*     */       } 
/* 182 */       if (nbChildren % 2 != 0) {
/* 183 */         MyNodePainter cdr = node.getChildPainter(nbChildren / 2);
/* 184 */         toLeft += getWidthP(cdr) / 2;
/*     */       } 
/* 186 */       int movepar = 0;
/* 187 */       int xps = TreeRenderer.this.getXMid(pdr) - toLeft;
/*     */       
/* 189 */       int ymin = Integer.MAX_VALUE;
/* 190 */       int ymax = Integer.MIN_VALUE;
/* 191 */       for (int j = 0; j < nbChildren; j++) {
/* 192 */         MyDrawableNode chi = (MyDrawableNode)node.getChild(j);
/* 193 */         MyNodePainter cdr = chi.getPainter();
/* 194 */         int ch_y0 = TreeRenderer.this.getY(cdr) - sett.YGAP + 1;
/* 195 */         int ch_y1 = chi.atFakeBottomLevel() ? TreeRenderer.this.getYBottom(cdr) : (TreeRenderer.this.getY(chi.getChildPainter(0)) - sett.YGAP);
/* 196 */         if (ch_y0 < ymin) ymin = ch_y0; 
/* 197 */         if (ch_y1 > ymax) ymax = ch_y1;
/*     */       
/*     */       } 
/* 200 */       int xmax = this.m_ITree.findOverlappingIntervalsMax(ymin, ymax, sett.XLEFT);
/* 201 */       if (xps < xmax) {
/* 202 */         movepar = xmax - xps;
/* 203 */         xps = xmax;
/*     */       } 
/* 205 */       for (int k = 0; k < nbChildren; k++) {
/* 206 */         MyNodePainter cdr = node.getChildPainter(k);
/* 207 */         setXP(cdr, xps);
/* 208 */         xps = getRightP(cdr);
/* 209 */         if (k < nbChildren - 1) {
/* 210 */           MyNodePainter ch2dr = node.getChildPainter(k + 1);
/* 211 */           xps += Math.max(sett.XGAP[cdr.getZoom()], sett.XGAP[ch2dr.getZoom()]);
/*     */         } else {
/* 213 */           xps += sett.XGAP[0];
/*     */         } 
/*     */       } 
/*     */       
/* 217 */       this.m_ITree.addInterval(ymin, ymax, xps);
/* 218 */       if (movepar > 0) repositionParent(node, movepar); 
/*     */     }
/*     */     
/*     */     private void moveNode(MyDrawableNode node, int delta) {
/* 222 */       MyNodePainter paint = node.getPainter();
/* 223 */       if (TreeRenderer.this.m_Vert) {
/* 224 */         paint.translate(delta, 0);
/*     */       } else {
/* 226 */         paint.translate(0, delta);
/*     */       } 
/* 228 */       NodePainterSettings sett = node.getPaintSettings();
/* 229 */       int ymin = TreeRenderer.this.getY(paint) - sett.YGAP + 1;
/* 230 */       int ymax = node.atFakeBottomLevel() ? TreeRenderer.this.getYBottom(paint) : (TreeRenderer.this.getY(node.getChildPainter(0)) - sett.YGAP);
/* 231 */       int xmax = this.m_ITree.findOverlappingIntervalsMax(ymin, ymax, sett.XLEFT);
/* 232 */       this.m_ITree.addInterval(ymin, ymax, Math.max(xmax, getRightP(paint) + sett.XGAP[0]));
/*     */     }
/*     */     
/*     */     private void moveNodeRecursive(MyDrawableNode node, int delta) {
/* 236 */       moveNode(node, delta);
/* 237 */       for (int i = 0; i < node.getNbFakeChildren(); i++) {
/* 238 */         MyDrawableNode child = (MyDrawableNode)node.getChild(i);
/* 239 */         moveNodeRecursive(child, delta);
/*     */       } 
/*     */     }
/*     */     
/*     */     private void repositionParent(MyDrawableNode node, int delta) {
/* 244 */       moveNode(node, delta);
/* 245 */       int idx = node.getIndex();
/* 246 */       node = (MyDrawableNode)node.getParent();
/* 247 */       while (node != null) {
/* 248 */         int nbCh = node.getNbFakeChildren();
/* 249 */         for (int i = idx + 1; i < nbCh; i++) {
/* 250 */           MyDrawableNode child = (MyDrawableNode)node.getChild(i);
/* 251 */           moveNode(child, delta);
/*     */         } 
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 281 */         MyNodePainter paint = node.getPainter();
/* 282 */         if (node.getNbChildren() % 2 == 0) {
/* 283 */           MyNodePainter myNodePainter1 = node.getChildPainter(node.getNbChildren() / 2 - 1);
/* 284 */           MyNodePainter ch2 = node.getChildPainter(node.getNbChildren() / 2);
/* 285 */           delta = (TreeRenderer.this.getXMid(myNodePainter1) + TreeRenderer.this.getXMid(ch2)) / 2 - TreeRenderer.this.getXMid(paint);
/* 286 */           if (delta > 0) {
/* 287 */             moveNode(node, delta);
/* 288 */             idx = node.getIndex();
/* 289 */             node = (MyDrawableNode)node.getParent(); continue;
/*     */           } 
/* 291 */           node = null;
/*     */           continue;
/*     */         } 
/* 294 */         MyNodePainter ch1 = node.getChildPainter(node.getNbChildren() / 2);
/* 295 */         delta = TreeRenderer.this.getXMid(ch1) - TreeRenderer.this.getXMid(paint);
/* 296 */         if (delta > 0) {
/* 297 */           moveNode(node, delta);
/* 298 */           idx = node.getIndex();
/* 299 */           node = (MyDrawableNode)node.getParent(); continue;
/*     */         } 
/* 301 */         node = null;
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     private int getWidthP(MyNodePainter pdr) {
/* 308 */       int wd = TreeRenderer.this.getWidth(pdr);
/* 309 */       Drawable label = pdr.getLabel();
/* 310 */       if (label != null && TreeRenderer.this.getWidth(label) > wd) {
/* 311 */         int delta = (TreeRenderer.this.getWidth(label) - wd) / 2;
/* 312 */         wd += 2 * delta;
/*     */       } 
/* 314 */       return wd;
/*     */     }
/*     */     
/*     */     private int getRightP(MyNodePainter pdr) {
/* 318 */       int wd = TreeRenderer.this.getWidth(pdr);
/* 319 */       Drawable label = pdr.getLabel();
/* 320 */       if (label != null && TreeRenderer.this.getWidth(label) > wd) {
/* 321 */         int delta = (TreeRenderer.this.getWidth(label) - wd) / 2;
/* 322 */         return TreeRenderer.this.getX(pdr) + wd + delta;
/*     */       } 
/* 324 */       return TreeRenderer.this.getX(pdr) + wd;
/*     */     }
/*     */ 
/*     */     
/*     */     private void setXP(MyNodePainter pdr, int x) {
/* 329 */       int wd = TreeRenderer.this.getWidth(pdr);
/* 330 */       Drawable label = pdr.getLabel();
/* 331 */       if (label != null && TreeRenderer.this.getWidth(label) > wd) {
/* 332 */         int delta = (TreeRenderer.this.getWidth(label) - wd) / 2;
/* 333 */         TreeRenderer.this.setX(pdr, x + delta);
/*     */       } else {
/* 335 */         TreeRenderer.this.setX(pdr, x);
/*     */       } 
/*     */     }
/*     */     
/*     */     public void addDebugLines() {
/* 340 */       TreeRenderer.DebugLineAdder adder = new TreeRenderer.DebugLineAdder();
/* 341 */       this.m_ITree.execute(adder);
/*     */     }
/*     */   }
/*     */   
/*     */   protected class DebugLineAdder
/*     */     implements Executer {
/*     */     public void execute(Object param) {
/* 348 */       IntervalTreeNodeRB node = (IntervalTreeNodeRB)param;
/* 349 */       if (TreeRenderer.this.m_Vert) {
/* 350 */         TreeRenderer.this.m_Debug.add(new DrawableRectangle(node.value, node.key, 1, node.high - node.key, Color.red));
/*     */       } else {
/* 352 */         TreeRenderer.this.m_Debug.add(new DrawableRectangle(node.key, node.value, node.high - node.key, 1, Color.red));
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   private int getX(Drawable dr) {
/* 358 */     return this.m_Vert ? dr.getX() : dr.getY();
/*     */   }
/*     */   
/*     */   private int getY(Drawable dr) {
/* 362 */     return this.m_Vert ? dr.getY() : dr.getX();
/*     */   }
/*     */   
/*     */   private int getWidth(Drawable dr) {
/* 366 */     return this.m_Vert ? dr.getWidth() : dr.getHeight();
/*     */   }
/*     */   
/*     */   private int getHeight(Drawable dr) {
/* 370 */     return this.m_Vert ? dr.getHeight() : dr.getWidth();
/*     */   }
/*     */   
/*     */   public int getRight(Drawable dr) {
/* 374 */     return this.m_Vert ? dr.getRight() : dr.getYBottom();
/*     */   }
/*     */   
/*     */   private int getXMid(Drawable dr) {
/* 378 */     return this.m_Vert ? dr.getXMid() : dr.getYMid();
/*     */   }
/*     */   
/*     */   private int getYBottom(Drawable dr) {
/* 382 */     return this.m_Vert ? dr.getYBottom() : dr.getRight();
/*     */   }
/*     */   
/*     */   private void setX(Drawable dr, int pos) {
/* 386 */     if (this.m_Vert) { dr.setX(pos); }
/* 387 */     else { dr.setY(pos); }
/*     */   
/*     */   }
/*     */   private void setY(Drawable dr, int pos) {
/* 391 */     if (this.m_Vert) { dr.setY(pos); }
/* 392 */     else { dr.setX(pos); }
/*     */   
/*     */   }
/*     */   public Dimension getSize() {
/* 396 */     return this.m_Size;
/*     */   }
/*     */ 
/*     */   
/*     */   public void removeAll(DrawableCanvas canvas) {}
/*     */   
/*     */   public void addAll(DrawableCanvas canvas) {
/* 403 */     addLines(this.m_Node, canvas);
/* 404 */     addNodes(this.m_Node, canvas);
/*     */   }
/*     */ 
/*     */   
/*     */   public void addDebug(DrawableCanvas canvas) {
/* 409 */     for (int i = 0; i < this.m_Debug.size(); i++) {
/* 410 */       canvas.addDrawable(this.m_Debug.get(i));
/*     */     }
/*     */   }
/*     */   
/*     */   public void addLines(MyDrawableNode node, DrawableCanvas canvas) {
/* 415 */     NodePainterSettings sett = node.getPaintSettings();
/* 416 */     MyNodePainter pdr = node.getPainter();
/* 417 */     Drawable label = pdr.getLabel();
/* 418 */     if (label != null) {
/* 419 */       label.setY(pdr.getY() - sett.YGAP / 2 - label.getHeight() - 4);
/* 420 */       label.setX(pdr.getXMid() - label.getWidth() / 2);
/* 421 */       canvas.addDrawable(label);
/*     */     } 
/* 423 */     int nbChildren = node.getNbFakeChildren();
/* 424 */     if (nbChildren == 0)
/* 425 */       return;  MyDrawableNode ch1 = (MyDrawableNode)node.getChild(0);
/* 426 */     MyNodePainter p1 = ch1.getPainter();
/* 427 */     MyDrawableNode chN = (MyDrawableNode)node.getChild(nbChildren - 1);
/* 428 */     MyNodePainter pN = chN.getPainter();
/* 429 */     int lwd = 2;
/* 430 */     if (this.m_Vert) {
/* 431 */       int ypos = p1.getY() - sett.YGAP / 2;
/* 432 */       canvas.addDrawable((Drawable)new DrawableRectangle(pdr.getXMid() - 1, pdr.getYBottom() + 1, lwd, ypos - pdr.getYBottom(), Color.black));
/* 433 */       canvas.addDrawable((Drawable)new DrawableRectangle(p1.getXMid() - 1, ypos - 1, pN.getXMid() - p1.getXMid() + 2, lwd, Color.black));
/* 434 */       for (int i = 0; i < nbChildren; i++) {
/* 435 */         MyDrawableNode child = (MyDrawableNode)node.getChild(i);
/* 436 */         MyNodePainter cdr = child.getPainter();
/* 437 */         int xchild = cdr.getXMid();
/* 438 */         canvas.addDrawable((Drawable)new DrawableRectangle(xchild - 1, ypos, lwd, cdr.getY() - ypos + 2, Color.black));
/* 439 */         DrawableExpandButton bt = cdr.getExpandButton();
/* 440 */         if (bt != null) {
/* 441 */           bt.setXY(xchild - 4, ypos - 3);
/* 442 */           canvas.addDrawable((Drawable)bt);
/*     */         } 
/* 444 */         addLines(child, canvas);
/*     */       } 
/*     */     } else {
/* 447 */       int xpos = p1.getX() - sett.YGAP / 2;
/* 448 */       int xpb = pdr.getRight();
/* 449 */       canvas.addDrawable((Drawable)new DrawableRectangle(xpb + 1, pdr.getYMid() - 1, xpos - xpb, lwd, Color.black));
/* 450 */       canvas.addDrawable((Drawable)new DrawableRectangle(xpos, p1.getYMid() - 1, lwd, pN.getYMid() - p1.getYMid() + 2, Color.black));
/* 451 */       for (int i = 0; i < nbChildren; i++) {
/* 452 */         MyDrawableNode child = (MyDrawableNode)node.getChild(i);
/* 453 */         MyNodePainter cdr = child.getPainter();
/* 454 */         int ychild = cdr.getYMid();
/* 455 */         canvas.addDrawable((Drawable)new DrawableRectangle(xpos, ychild - 1, cdr.getX() - xpos + 2, lwd, Color.black));
/* 456 */         DrawableExpandButton bt = cdr.getExpandButton();
/* 457 */         if (bt != null) {
/* 458 */           bt.setXY(xpos - 4, ychild - 4);
/* 459 */           canvas.addDrawable((Drawable)bt);
/*     */         } 
/* 461 */         addLines(child, canvas);
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public static void addNodes(MyDrawableNode node, DrawableCanvas canvas) {
/* 467 */     MyNodePainter pdr = node.getPainter();
/* 468 */     canvas.addDrawable(pdr);
/* 469 */     int nbChildren = node.getNbFakeChildren();
/* 470 */     if (nbChildren == 0)
/* 471 */       return;  for (int i = 0; i < nbChildren; i++) {
/* 472 */       MyDrawableNode child = (MyDrawableNode)node.getChild(i);
/* 473 */       addNodes(child, canvas);
/*     */     } 
/*     */   }
/*     */   
/*     */   public static void calcSizes(MyDrawableNode node, Graphics2D g, FontMetrics fm, DrawableCanvas canvas) {
/* 478 */     MyNodePainter paint = node.getPainter();
/* 479 */     paint.calcSize(g, fm, canvas);
/* 480 */     Drawable label = paint.getLabel();
/* 481 */     if (label != null) label.calcSize(g, fm, canvas); 
/* 482 */     for (int i = 0; i < node.getNbFakeChildren(); i++) {
/* 483 */       MyDrawableNode child = (MyDrawableNode)node.getChild(i);
/* 484 */       calcSizes(child, g, fm, canvas);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\jeans\graph\tree\TreeRenderer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */