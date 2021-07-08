/*     */ package jeans.graph;
/*     */ 
/*     */ import java.awt.Component;
/*     */ import java.awt.Container;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Insets;
/*     */ import java.awt.LayoutManager;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class MyColumnLayout
/*     */   implements LayoutManager
/*     */ {
/*     */   private int gap;
/*     */   private int cols;
/*     */   private int rows;
/*     */   private int[] wds;
/*     */   private int[] his;
/*     */   private boolean border;
/*     */   private boolean sizeKnown;
/*     */   
/*     */   public MyColumnLayout(int cols, int rows, int gap, boolean border) {
/*  52 */     this.gap = gap;
/*  53 */     this.border = border;
/*  54 */     this.cols = cols;
/*  55 */     this.rows = rows;
/*  56 */     this.wds = new int[cols];
/*  57 */     this.his = new int[rows];
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void addLayoutComponent(String name, Component comp) {}
/*     */ 
/*     */   
/*     */   public void removeLayoutComponent(Component comp) {}
/*     */ 
/*     */   
/*     */   public int getComponentCount(Container parent) {
/*  69 */     return parent.getComponentCount();
/*     */   }
/*     */   
/*     */   public void calcSizes(Container parent, int nComps) {
/*     */     int i;
/*  74 */     for (i = 0; i < this.cols; ) { this.wds[i] = 0; i++; }
/*  75 */      for (i = 0; i < this.rows; ) { this.his[i] = 0; i++; }
/*     */     
/*  77 */     int idx = 0;
/*  78 */     for (int row = 0; row < this.rows && idx < nComps; row++) {
/*  79 */       for (int col = 0; col < this.cols && idx < nComps; col++) {
/*  80 */         Component c = parent.getComponent(idx++);
/*  81 */         if (c.isVisible()) {
/*  82 */           Dimension d = c.getPreferredSize();
/*  83 */           this.wds[col] = Math.max(this.wds[col], d.width);
/*  84 */           this.his[row] = Math.max(this.his[row], d.height);
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/*  89 */     this.sizeKnown = true;
/*     */   }
/*     */ 
/*     */   
/*     */   public Dimension preferredLayoutSize(Container parent) {
/*  94 */     Dimension dim = new Dimension(0, 0);
/*     */ 
/*     */     
/*  97 */     Insets insets = parent.getInsets();
/*     */     
/*  99 */     int nComps = getComponentCount(parent);
/* 100 */     calcSizes(parent, nComps);
/*     */     int i;
/* 102 */     for (i = 0; i < this.cols; ) { dim.width += this.wds[i]; i++; }
/* 103 */      for (i = 0; i < this.rows; ) { dim.height += this.his[i]; i++; }
/*     */     
/* 105 */     dim.width += insets.left + insets.right + (this.cols - 1) * this.gap;
/* 106 */     dim.height += insets.top + insets.bottom + (this.rows - 1) * this.gap;
/*     */     
/* 108 */     if (this.border) {
/* 109 */       dim.width += 2 * this.gap;
/* 110 */       dim.height += 2 * this.gap;
/*     */     } 
/*     */     
/* 113 */     return dim;
/*     */   }
/*     */ 
/*     */   
/*     */   public Dimension minimumLayoutSize(Container parent) {
/* 118 */     return preferredLayoutSize(parent);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void layoutContainer(Container parent) {
/* 128 */     Insets insets = parent.getInsets();
/* 129 */     int maxWidth = (parent.getSize()).width - insets.left + insets.right;
/*     */     
/* 131 */     if (this.border) maxWidth -= 2 * this.gap;
/*     */     
/* 133 */     int ypos = insets.top;
/* 134 */     int xpos = insets.left;
/* 135 */     if (this.border) {
/* 136 */       xpos += this.gap;
/* 137 */       ypos += this.gap;
/*     */     } 
/*     */     
/* 140 */     int nComps = getComponentCount(parent);
/* 141 */     if (!this.sizeKnown) calcSizes(parent, nComps);
/*     */     
/* 143 */     int idx = 0;
/* 144 */     for (int row = 0; row < this.rows && idx < nComps; row++) {
/* 145 */       for (int col = 0; col < this.cols && idx < nComps; col++) {
/* 146 */         Component c = parent.getComponent(idx++);
/* 147 */         if (c.isVisible()) {
/* 148 */           Dimension d = c.getPreferredSize();
/* 149 */           c.setBounds(xpos, ypos, d.width, d.height);
/* 150 */           xpos += this.wds[col] + this.gap;
/*     */         } 
/*     */       } 
/* 153 */       ypos += this.his[row] + this.gap;
/* 154 */       xpos = insets.left;
/* 155 */       if (this.border) xpos += this.gap; 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jeans\graph\MyColumnLayout.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */