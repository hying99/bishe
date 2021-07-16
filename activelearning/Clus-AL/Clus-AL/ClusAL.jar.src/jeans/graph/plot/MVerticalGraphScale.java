/*     */ package jeans.graph.plot;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.awt.Component;
/*     */ import java.awt.FontMetrics;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Image;
/*     */ import java.awt.image.FilteredImageSource;
/*     */ import java.awt.image.ImageFilter;
/*     */ import java.awt.image.ImageProducer;
/*     */ import jeans.graph.image.TransformFilter;
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
/*     */ public class MVerticalGraphScale
/*     */   extends MGraphScale
/*     */ {
/*     */   public static final int LABEL_GAPY = 60;
/*     */   protected Image m_VertLabel;
/*     */   
/*     */   public int getVertWidth(Component cnv, FontMetrics fm, int y, int hi) {
/*  40 */     int wd = this.m_MinValue.getWidth(fm);
/*  41 */     wd = Math.max(wd, this.m_MaxValue.getWidth(fm));
/*  42 */     int charh = fm.getHeight();
/*  43 */     int ny1 = y + charh / 2;
/*  44 */     int ny2 = y + hi - charh / 2;
/*  45 */     wd = calcVWRecursive(fm, y, hi, this.m_MinValue.getFloat(), this.m_MaxValue.getFloat(), ny1, ny2, wd);
/*  46 */     wd += 7;
/*  47 */     if (this.m_sLabel != null) {
/*  48 */       if (this.m_VertLabel == null) createVertLabel(cnv, fm); 
/*  49 */       wd += this.m_VertLabel.getWidth(cnv) + 6;
/*     */     } 
/*  51 */     return wd;
/*     */   }
/*     */   
/*     */   public int getVertUpperBound(FontMetrics fm) {
/*  55 */     return fm.getHeight() / 2 + 6;
/*     */   }
/*     */   
/*     */   public int getVertLowerBound(FontMetrics fm) {
/*  59 */     return fm.getHeight() / 2 + 3;
/*     */   }
/*     */   
/*     */   public void createVertLabel(Component cnv, FontMetrics fm) {
/*  63 */     int wd = fm.stringWidth(this.m_sLabel);
/*  64 */     int hi = fm.getHeight();
/*     */     
/*  66 */     Image img = cnv.createImage(wd, hi);
/*  67 */     Graphics g = img.getGraphics();
/*  68 */     g.setColor(Color.black);
/*  69 */     g.fillRect(0, 0, wd, hi);
/*  70 */     g.setColor(this.m_LabelColor);
/*  71 */     g.drawString(this.m_sLabel, 0, fm.getAscent());
/*     */     
/*  73 */     TransformFilter transformFilter = new TransformFilter(0);
/*  74 */     ImageProducer prod = new FilteredImageSource(img.getSource(), (ImageFilter)transformFilter);
/*  75 */     this.m_VertLabel = cnv.createImage(prod);
/*     */   }
/*     */   
/*     */   public void draw(Component cnv, Graphics g, int x, int y, int wd, int hi) {
/*  79 */     FontMetrics fm = g.getFontMetrics();
/*  80 */     g.drawLine(x, y, x, y + hi);
/*  81 */     g.drawLine(x - 3, y, x, y);
/*  82 */     g.drawLine(x - 3, y + hi, x, y + hi);
/*  83 */     int charh = fm.getHeight();
/*  84 */     int delta = -fm.getAscent() + charh / 2;
/*  85 */     int wmin = this.m_MinValue.getWidth(fm);
/*  86 */     int wmax = this.m_MaxValue.getWidth(fm);
/*  87 */     this.m_MaxValue.draw(fm, g, x - 4 - 3 - wmax, y + delta);
/*  88 */     this.m_MinValue.draw(fm, g, x - 4 - 3 - wmin, y + hi + delta);
/*  89 */     int ny1 = y + charh / 2;
/*  90 */     int ny2 = y + hi - charh / 2;
/*  91 */     drawYRecursive(g, fm, x, y, hi, this.m_MinValue.getFloat(), this.m_MaxValue.getFloat(), ny1, ny2);
/*  92 */     if (this.m_sLabel != null) {
/*  93 */       if (this.m_VertLabel == null) createVertLabel(cnv, fm); 
/*  94 */       g.drawImage(this.m_VertLabel, 2, y + hi / 2 - this.m_VertLabel.getHeight(cnv) / 2, cnv);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void drawYRecursive(Graphics g, FontMetrics fm, int x, int y, int hi, float f1, float f2, int y1, int y2) {
/*  99 */     MDouble calc = new MDouble();
/* 100 */     calc.setRoundValue(((f1 + f2) / 2.0F));
/* 101 */     int pos = scaleY(y, hi, calc.getFloat());
/* 102 */     int charh = fm.getHeight();
/* 103 */     if (pos - charh / 2 - this.m_iGap > y1 && pos + charh / 2 + this.m_iGap < y2) {
/* 104 */       int delta = -fm.getAscent() + charh / 2;
/* 105 */       int wd = calc.getWidth(fm);
/* 106 */       g.drawLine(x - 3, pos, x, pos);
/* 107 */       calc.draw(fm, g, x - 4 - 3 - wd, pos + delta);
/* 108 */       drawYRecursive(g, fm, x, y, hi, f1, calc.getFloat(), pos + charh / 2, y2);
/* 109 */       drawYRecursive(g, fm, x, y, hi, calc.getFloat(), f2, y1, pos - charh / 2);
/*     */     } 
/*     */   }
/*     */   
/*     */   public int calcVWRecursive(FontMetrics fm, int y, int hi, float f1, float f2, int y1, int y2, int mwd) {
/* 114 */     MDouble calc = new MDouble();
/* 115 */     calc.setRoundValue(((f1 + f2) / 2.0F));
/* 116 */     int pos = scaleY(y, hi, calc.getFloat());
/* 117 */     int charh = fm.getHeight();
/* 118 */     if (pos - charh / 2 - this.m_iGap > y1 && pos + charh / 2 + this.m_iGap < y2) {
/* 119 */       mwd = Math.max(mwd, calc.getWidth(fm));
/* 120 */       mwd = calcVWRecursive(fm, y, hi, f1, calc.getFloat(), pos + charh / 2, y2, mwd);
/* 121 */       mwd = calcVWRecursive(fm, y, hi, calc.getFloat(), f2, y1, pos - charh / 2, mwd);
/*     */     } 
/* 123 */     return mwd;
/*     */   }
/*     */   
/*     */   public int scaleY(int y, int hi, float f) {
/* 127 */     float f1 = this.m_MinValue.getFloat();
/* 128 */     float f2 = this.m_MaxValue.getFloat();
/* 129 */     float scale = (f - f1) / (f2 - f1) * hi;
/* 130 */     return hi - Math.round(scale) + y;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\jeans\graph\plot\MVerticalGraphScale.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */