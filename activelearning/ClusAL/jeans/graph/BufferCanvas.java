/*     */ package jeans.graph;
/*     */ 
/*     */ import java.awt.Canvas;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Font;
/*     */ import java.awt.FontMetrics;
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
/*     */ public abstract class BufferCanvas
/*     */   extends Canvas
/*     */ {
/*     */   Dimension minSize;
/*  68 */   public static final Font[] fonts = createFonts();
/*     */   
/*     */   protected Image bufImg;
/*     */   protected Graphics bufGrp;
/*     */   protected Dimension bufSiz;
/*     */   
/*     */   public BufferCanvas(int minx, int miny) {
/*  75 */     this.minSize = new Dimension(minx, miny);
/*     */   }
/*     */   
/*     */   public Dimension getPreferredSize() {
/*  79 */     return getMinimumSize();
/*     */   }
/*     */   
/*     */   public Dimension getMinimumSize() {
/*  83 */     return this.minSize;
/*     */   }
/*     */   
/*     */   public void setFont(Graphics g, int size, String mys) {
/*  87 */     int font_no = 4;
/*  88 */     FontMetrics fm = g.getFontMetrics(fonts[font_no]);
/*  89 */     while (fm.stringWidth(mys) > size && font_no > 0) {
/*  90 */       font_no--;
/*  91 */       fm = g.getFontMetrics(fonts[font_no]);
/*     */     } 
/*  93 */     g.setFont(fonts[font_no]);
/*     */   }
/*     */   
/*     */   public void reSize(Dimension d) {
/*  97 */     if (this.bufGrp == null || d.width != this.bufSiz.width || d.height != this.bufSiz.height) {
/*  98 */       if (d.width == 0 || d.height == 0)
/*  99 */         return;  this.bufSiz = d;
/* 100 */       this.bufImg = createImage(d.width, d.height);
/* 101 */       this.bufGrp = this.bufImg.getGraphics();
/* 102 */       paintIt(this.bufGrp, this.bufSiz);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void paint(Graphics g) {
/* 107 */     update(g);
/*     */   }
/*     */   
/*     */   public void update(Graphics g) {
/* 111 */     reSize(getSize());
/* 112 */     g.drawImage(this.bufImg, 0, 0, this);
/*     */   }
/*     */   
/*     */   public void redraw() {
/* 116 */     if (this.bufGrp != null)
/* 117 */       paintIt(this.bufGrp, this.bufSiz); 
/* 118 */     repaint();
/*     */   }
/*     */   
/*     */   public Dimension getBufferSize() {
/* 122 */     return this.bufSiz;
/*     */   }
/*     */   
/*     */   public Graphics getBufferGraphics() {
/* 126 */     return this.bufGrp;
/*     */   }
/*     */   
/*     */   public abstract void paintIt(Graphics paramGraphics, Dimension paramDimension);
/*     */   
/*     */   public static Font[] createFonts() {
/* 132 */     Font[] fonts = new Font[5];
/* 133 */     for (int ctr = 0; ctr < 5; ctr++)
/* 134 */       fonts[ctr] = new Font("Courrier", 0, 8 + ctr * 4); 
/* 135 */     return fonts;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jeans\graph\BufferCanvas.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */