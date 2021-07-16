/*     */ package jeans.graph;
/*     */ 
/*     */ import java.awt.Canvas;
/*     */ import java.awt.Color;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.FontMetrics;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Image;
/*     */ import java.util.Vector;
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
/*     */ public class StringIconPainter
/*     */   implements Painter
/*     */ {
/*     */   BufferCanvas cnv;
/*  31 */   Vector strings = new Vector();
/*     */   Image icon;
/*  33 */   int fillwidth = 0;
/*  34 */   Color background = Color.white;
/*  35 */   Color textcolor = Color.black;
/*  36 */   Color fillcolor = Color.black;
/*     */   
/*     */   public StringIconPainter(Image icon, BufferCanvas cnv) {
/*  39 */     this.cnv = cnv;
/*  40 */     this.icon = icon;
/*     */   }
/*     */   
/*     */   public void setFill(Color color, int width) {
/*  44 */     this.fillcolor = color;
/*  45 */     this.fillwidth = width;
/*     */   }
/*     */   
/*     */   public void addString(String string) {
/*  49 */     this.strings.addElement(string);
/*     */   }
/*     */   
/*     */   public void setBackground(Color color) {
/*  53 */     this.background = color;
/*     */   }
/*     */   
/*     */   public void setTextColor(Color color) {
/*  57 */     this.textcolor = color;
/*     */   }
/*     */   
/*     */   public void paint(Graphics graph, Canvas canvas) {
/*  61 */     Dimension dim = this.cnv.getBufferSize();
/*  62 */     int iw = this.icon.getWidth(canvas);
/*  63 */     int ih = this.icon.getHeight(canvas);
/*  64 */     FontMetrics fm = graph.getFontMetrics();
/*  65 */     int nb_strs = this.strings.size();
/*  66 */     int hi = Math.max(nb_strs * fm.getHeight(), ih + 10) + 10 + this.fillwidth * 2;
/*  67 */     int wd = 0;
/*  68 */     if (nb_strs > 0) {
/*  69 */       for (int i = 0; i < nb_strs; i++) {
/*  70 */         String string = this.strings.elementAt(i);
/*  71 */         wd = Math.max(wd, fm.stringWidth(string));
/*     */       } 
/*  73 */       wd += iw + 35 + this.fillwidth * 2;
/*     */     } else {
/*  75 */       wd += iw + 20 + this.fillwidth * 2;
/*     */     } 
/*  77 */     int x0 = (dim.width - wd) / 2;
/*  78 */     int ylvl = dim.height / 2;
/*  79 */     int y0 = ylvl - hi / 2;
/*     */     
/*  81 */     graph.setColor(this.background);
/*  82 */     graph.fillRect(x0, y0, wd, hi);
/*  83 */     graph.setColor(this.textcolor);
/*  84 */     graph.drawRect(x0, y0, wd, hi);
/*  85 */     if (this.fillwidth != 0) {
/*  86 */       graph.setColor(this.fillcolor);
/*  87 */       graph.fillRect(x0 + 10, y0 + 10, wd - 20, hi - 20);
/*  88 */       x0 += this.fillwidth;
/*  89 */       y0 += this.fillwidth;
/*  90 */       wd -= 2 * this.fillwidth;
/*  91 */       hi -= 2 * this.fillwidth;
/*     */     } 
/*     */     
/*  94 */     graph.setColor(this.background);
/*  95 */     ImageUtil.draw3DRect(graph, x0 + 5, y0 + 5, iw + 10, ih + 10, 2, false);
/*  96 */     graph.drawImage(this.icon, x0 + 10, y0 + 10, canvas);
/*     */     
/*  98 */     graph.setColor(this.textcolor);
/*  99 */     for (int ctr = 0; ctr < nb_strs; ctr++) {
/* 100 */       String string = this.strings.elementAt(ctr);
/* 101 */       graph.drawString(string, x0 + iw + 25, y0 + 5 + ctr * fm.getHeight() + fm.getAscent());
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\jeans\graph\StringIconPainter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */