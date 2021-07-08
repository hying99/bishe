/*     */ package jeans.graph;
/*     */ 
/*     */ import java.awt.Canvas;
/*     */ import java.awt.Color;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Image;
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
/*     */ public class ImageViewer
/*     */   extends Canvas
/*     */ {
/*     */   public static final long serialVersionUID = 1L;
/*     */   public static final int NOBORDER = 0;
/*     */   public static final int LINEBORDER = 1;
/*     */   public static final int SUNKEN3D = 1;
/*     */   public static final int RAISED3D = 2;
/*     */   private Image image;
/*     */   private Dimension size;
/*     */   private Color fillColor;
/*     */   private int fillWidth;
/*     */   private int borderWidth;
/*     */   private int borderType;
/*     */   
/*     */   public ImageViewer(Image image, int type) {
/*  44 */     this.image = image;
/*  45 */     this.fillColor = Color.black;
/*  46 */     this.fillWidth = 0;
/*  47 */     this.borderWidth = getDefaultSize(type);
/*  48 */     this.borderType = type;
/*  49 */     this.size = calcSize(image.getWidth(this), image.getHeight(this));
/*     */   }
/*     */   
/*     */   public ImageViewer(Image image, int wd, int hi, int bType) {
/*  53 */     this(image, new Dimension(wd, hi), Color.black, 0, getDefaultSize(bType), bType);
/*     */   }
/*     */   
/*     */   public ImageViewer(Image image, int wd, int hi, int bWidth, int bType) {
/*  57 */     this(image, new Dimension(wd, hi), Color.black, 0, bWidth, bType);
/*     */   }
/*     */   
/*     */   public ImageViewer(Image image, Dimension size, Color fColor, int fWidth, int bWidth, int bType) {
/*  61 */     this.image = image;
/*  62 */     this.fillColor = fColor;
/*  63 */     this.fillWidth = fWidth;
/*  64 */     this.borderWidth = bWidth;
/*  65 */     this.borderType = bType;
/*  66 */     this.size = size;
/*     */   }
/*     */   
/*     */   public Dimension getPreferredSize() {
/*  70 */     return this.size;
/*     */   }
/*     */   
/*     */   public void paint(Graphics g) {
/*  74 */     update(g);
/*     */   }
/*     */   
/*     */   public void update(Graphics g) {
/*  78 */     Dimension d = getSize();
/*  79 */     int width = this.image.getWidth(this);
/*  80 */     int height = this.image.getHeight(this);
/*  81 */     if (this.fillWidth != 0) {
/*  82 */       g.setColor(this.fillColor);
/*  83 */       int ofs = 3 * this.borderWidth;
/*  84 */       g.fillRect(ofs, ofs, d.width - 2 * ofs, d.height - 2 * ofs);
/*     */     } 
/*  86 */     g.drawImage(this.image, d.width / 2 - width / 2, d.height / 2 - height / 2, this);
/*  87 */     if (this.borderType == 1) {
/*  88 */       g.setColor(getBackground().darker());
/*  89 */       g.drawRect(0, 0, d.width - 1, d.height - 1);
/*  90 */     } else if (this.borderType == 2) {
/*  91 */       g.setColor(getBackground());
/*  92 */       ImageUtil.draw3DRect(g, 0, 0, d.width, d.height, this.borderWidth, true);
/*  93 */     } else if (this.borderType == 1) {
/*  94 */       g.setColor(getBackground());
/*  95 */       ImageUtil.draw3DRect(g, 0, 0, d.width, d.height, this.borderWidth, false);
/*     */     } 
/*     */   }
/*     */   
/*     */   private Dimension calcSize(int wd, int hi) {
/* 100 */     int cte = this.borderWidth * 6 + this.fillWidth * 2;
/* 101 */     return new Dimension(wd + cte, hi + cte);
/*     */   }
/*     */   
/*     */   private static int getDefaultSize(int type) {
/* 105 */     if (type == 1) return 1; 
/* 106 */     if (type != 0) return 2; 
/* 107 */     return 0;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jeans\graph\ImageViewer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */