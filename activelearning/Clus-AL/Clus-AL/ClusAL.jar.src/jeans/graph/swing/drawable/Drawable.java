/*     */ package jeans.graph.swing.drawable;
/*     */ 
/*     */ import java.awt.Dimension;
/*     */ import java.awt.FontMetrics;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.event.MouseEvent;
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
/*     */ public abstract class Drawable
/*     */ {
/*     */   protected int xp;
/*     */   protected int yp;
/*  31 */   protected int wd = -1;
/*  32 */   protected int hi = -1;
/*     */   
/*     */   public Drawable() {
/*  35 */     this.xp = 0; this.yp = 0;
/*     */   }
/*     */ 
/*     */   
/*     */   public void calcSize(Graphics2D g, FontMetrics fm, DrawableCanvas canvas) {}
/*     */ 
/*     */   
/*     */   public void undoTransform() {}
/*     */   
/*     */   public abstract void draw(Graphics2D paramGraphics2D, DrawableCanvas paramDrawableCanvas, int paramInt1, int paramInt2);
/*     */   
/*     */   public boolean mousePressed(DrawableCanvas canvas, int x, int y, MouseEvent evt) {
/*  47 */     return false;
/*     */   }
/*     */   
/*     */   public boolean mouseSensitive() {
/*  51 */     return false;
/*     */   }
/*     */   
/*     */   public int getX() {
/*  55 */     return this.xp;
/*     */   }
/*     */   
/*     */   public int getY() {
/*  59 */     return this.yp;
/*     */   }
/*     */   
/*     */   public int getWidth() {
/*  63 */     return this.wd;
/*     */   }
/*     */   
/*     */   public int getHeight() {
/*  67 */     return this.hi;
/*     */   }
/*     */   
/*     */   public int getRight() {
/*  71 */     return this.xp + this.wd;
/*     */   }
/*     */   
/*     */   public int getXMid() {
/*  75 */     return this.xp + this.wd / 2;
/*     */   }
/*     */   
/*     */   public int getYMid() {
/*  79 */     return this.yp + this.hi / 2;
/*     */   }
/*     */   
/*     */   public int getYBottom() {
/*  83 */     return this.yp + this.hi;
/*     */   }
/*     */   
/*     */   public Rectangle getBoundRect() {
/*  87 */     return new Rectangle(this.xp, this.yp, this.wd, this.hi);
/*     */   }
/*     */   
/*     */   public Dimension getSize() {
/*  91 */     return new Dimension(this.wd, this.hi);
/*     */   }
/*     */   
/*     */   public void translate(int dx, int dy) {
/*  95 */     this.xp += dx;
/*  96 */     this.yp += dy;
/*     */   }
/*     */   
/*     */   public void setXY(int xpos, int ypos) {
/* 100 */     this.xp = xpos; this.yp = ypos;
/*     */   }
/*     */   
/*     */   public void setX(int xpos) {
/* 104 */     this.xp = xpos;
/*     */   }
/*     */   
/*     */   public void setY(int ypos) {
/* 108 */     this.yp = ypos;
/*     */   }
/*     */   
/*     */   public void setWidth(int wd) {
/* 112 */     this.wd = wd;
/*     */   }
/*     */   
/*     */   public void setHeight(int hi) {
/* 116 */     this.hi = hi;
/*     */   }
/*     */   
/*     */   public void setSize(int wd, int hi) {
/* 120 */     this.wd = wd;
/* 121 */     this.hi = hi;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\jeans\graph\swing\drawable\Drawable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */