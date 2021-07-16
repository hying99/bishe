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
/*     */ 
/*     */ 
/*     */ 
/*     */ public class OneColumnLayout
/*     */   implements LayoutManager
/*     */ {
/*     */   private int gap;
/*     */   private boolean border;
/*     */   
/*     */   public OneColumnLayout(int gap, boolean border) {
/*  50 */     this.gap = gap;
/*  51 */     this.border = border;
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
/*  63 */     return parent.getComponentCount();
/*     */   }
/*     */ 
/*     */   
/*     */   public Dimension preferredLayoutSize(Container parent) {
/*  68 */     Dimension dim = new Dimension(0, 0);
/*     */ 
/*     */     
/*  71 */     Insets insets = parent.getInsets();
/*     */     
/*  73 */     int nComps = getComponentCount(parent);
/*  74 */     for (int i = 0; i < nComps; i++) {
/*  75 */       Component c = parent.getComponent(i);
/*  76 */       if (c.isVisible()) {
/*  77 */         Dimension d = c.getPreferredSize();
/*  78 */         dim.width = Math.max(dim.width, d.width);
/*  79 */         dim.height += d.height;
/*     */       } 
/*     */     } 
/*     */     
/*  83 */     dim.width += insets.left + insets.right + (nComps - 1) * this.gap;
/*  84 */     dim.height += insets.top + insets.bottom + (nComps - 1) * this.gap;
/*     */     
/*  86 */     if (this.border) {
/*  87 */       dim.width += 2 * this.gap;
/*  88 */       dim.height += 2 * this.gap;
/*     */     } 
/*     */     
/*  91 */     return dim;
/*     */   }
/*     */ 
/*     */   
/*     */   public Dimension minimumLayoutSize(Container parent) {
/*  96 */     return preferredLayoutSize(parent);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void layoutContainer(Container parent) {
/* 106 */     Insets insets = parent.getInsets();
/* 107 */     int maxWidth = (parent.getSize()).width - insets.left + insets.right;
/*     */     
/* 109 */     if (this.border) maxWidth -= 2 * this.gap;
/*     */     
/* 111 */     int ypos = insets.top;
/* 112 */     int xpos = insets.left;
/* 113 */     int nComps = getComponentCount(parent);
/* 114 */     for (int i = 0; i < nComps; i++) {
/* 115 */       Component c = parent.getComponent(i);
/* 116 */       if (c.isVisible()) {
/*     */         
/* 118 */         Dimension d = c.getPreferredSize();
/* 119 */         c.setBounds(xpos, ypos, maxWidth, d.height);
/* 120 */         ypos += d.height + this.gap;
/*     */       } 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\jeans\graph\OneColumnLayout.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */