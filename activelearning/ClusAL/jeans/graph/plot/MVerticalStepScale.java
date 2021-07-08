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
/*     */ public class MVerticalStepScale
/*     */   extends MGraphScale
/*     */ {
/*     */   public static final int LABEL_GAPY = 60;
/*     */   protected Image m_VertLabel;
/*     */   
/*     */   public int getVertWidth(Component cnv, FontMetrics fm, int y, int hi) {
/*  40 */     int wd = this.m_MinValue.getWidth(fm);
/*  41 */     wd = Math.max(wd, this.m_MaxValue.getWidth(fm));
/*  42 */     int charh = fm.getHeight();
/*  43 */     int y1 = y + charh / 2;
/*  44 */     int y2 = y + hi - charh / 2;
/*  45 */     double value = this.m_MinValue.getFloat() + this.m_dRealStep;
/*  46 */     double max = this.m_MaxValue.getFloat();
/*  47 */     MDouble calc = new MDouble();
/*  48 */     while (value < max) {
/*  49 */       calc.setRoundValue(value);
/*  50 */       int pos = scaleY(y, hi, calc.getFloat());
/*  51 */       if (pos - charh / 2 - this.m_iGap < y1)
/*  52 */         break;  if (pos + charh / 2 + this.m_iGap < y2) {
/*  53 */         int swd = calc.getWidth(fm);
/*  54 */         wd = Math.max(wd, swd);
/*  55 */         y2 = pos - charh / 2;
/*     */       } 
/*  57 */       value += this.m_dRealStep;
/*     */     } 
/*  59 */     wd += 7;
/*  60 */     if (this.m_sLabel != null) {
/*  61 */       if (this.m_VertLabel == null) createVertLabel(cnv, fm); 
/*  62 */       wd += this.m_VertLabel.getWidth(cnv) + 6;
/*     */     } 
/*  64 */     return wd;
/*     */   }
/*     */   
/*     */   public boolean tryStep(FontMetrics fm, int y, int hi) {
/*  68 */     int charh = fm.getHeight();
/*  69 */     int y1 = y + charh / 2;
/*  70 */     int y2 = y + hi - charh / 2;
/*  71 */     double value = this.m_MinValue.getFloat() + this.m_dRealStep;
/*  72 */     double max = this.m_MaxValue.getFloat();
/*  73 */     MDouble calc = new MDouble();
/*  74 */     while (value < max) {
/*  75 */       calc.setRoundValue(value);
/*  76 */       int pos = scaleY(y, hi, calc.getFloat());
/*  77 */       if (pos - charh / 2 - this.m_iGap < y1)
/*  78 */         break;  if (pos + charh / 2 + this.m_iGap < y2) {
/*  79 */         y2 = pos - charh / 2;
/*     */       } else {
/*  81 */         return false;
/*     */       } 
/*  83 */       value += this.m_dRealStep;
/*     */     } 
/*  85 */     return true;
/*     */   }
/*     */   
/*     */   public void autoStep(FontMetrics fm, int y, int hi) {
/*  89 */     this.m_dRealStep = this.m_dStep;
/*  90 */     for (; !tryStep(fm, y, hi); this.m_dRealStep *= 3.0D);
/*     */   }
/*     */   
/*     */   public int getVertUpperBound(FontMetrics fm) {
/*  94 */     return fm.getHeight() / 2 + 6;
/*     */   }
/*     */   
/*     */   public int getVertLowerBound(FontMetrics fm) {
/*  98 */     return fm.getHeight() / 2 + 3;
/*     */   }
/*     */   
/*     */   public void createVertLabel(Component cnv, FontMetrics fm) {
/* 102 */     int wd = fm.stringWidth(this.m_sLabel);
/* 103 */     int hi = fm.getHeight();
/*     */     
/* 105 */     Image img = cnv.createImage(wd, hi);
/* 106 */     Graphics g = img.getGraphics();
/* 107 */     g.setColor(Color.black);
/* 108 */     g.fillRect(0, 0, wd, hi);
/* 109 */     g.setColor(this.m_LabelColor);
/* 110 */     g.drawString(this.m_sLabel, 0, fm.getAscent());
/*     */     
/* 112 */     TransformFilter transformFilter = new TransformFilter(0);
/* 113 */     ImageProducer prod = new FilteredImageSource(img.getSource(), (ImageFilter)transformFilter);
/* 114 */     this.m_VertLabel = cnv.createImage(prod);
/*     */   }
/*     */   
/*     */   public void draw(Component cnv, Graphics g, int x, int y, int wd, int hi) {
/* 118 */     FontMetrics fm = g.getFontMetrics();
/* 119 */     g.drawLine(x, y, x, y + hi);
/* 120 */     g.drawLine(x - 3, y, x, y);
/* 121 */     g.drawLine(x - 3, y + hi, x, y + hi);
/* 122 */     int charh = fm.getHeight();
/* 123 */     int delta = -fm.getAscent() + charh / 2;
/* 124 */     int wmin = this.m_MinValue.getWidth(fm);
/* 125 */     int wmax = this.m_MaxValue.getWidth(fm);
/* 126 */     this.m_MaxValue.draw(fm, g, x - 4 - 3 - wmax, y + delta);
/* 127 */     this.m_MinValue.draw(fm, g, x - 4 - 3 - wmin, y + hi + delta);
/* 128 */     int y1 = y + charh / 2;
/* 129 */     int y2 = y + hi - charh / 2;
/* 130 */     double value = this.m_MinValue.getFloat() + this.m_dRealStep;
/* 131 */     double max = this.m_MaxValue.getFloat();
/* 132 */     MDouble calc = new MDouble();
/* 133 */     while (value < max) {
/* 134 */       calc.setRoundValue(value);
/* 135 */       int pos = scaleY(y, hi, calc.getFloat());
/* 136 */       if (pos - charh / 2 - this.m_iGap < y1)
/* 137 */         break;  if (pos + charh / 2 + this.m_iGap < y2) {
/* 138 */         int swd = calc.getWidth(fm);
/* 139 */         g.drawLine(x - 3, pos, x, pos);
/* 140 */         calc.draw(fm, g, x - 4 - 3 - swd, pos + delta);
/* 141 */         y2 = pos - charh / 2;
/*     */       } 
/* 143 */       value += this.m_dRealStep;
/*     */     } 
/* 145 */     if (this.m_sLabel != null) {
/* 146 */       if (this.m_VertLabel == null) createVertLabel(cnv, fm); 
/* 147 */       g.drawImage(this.m_VertLabel, 2, y + hi / 2 - this.m_VertLabel.getHeight(cnv) / 2, cnv);
/*     */     } 
/*     */   }
/*     */   
/*     */   public int scaleY(int y, int hi, float f) {
/* 152 */     float f1 = this.m_MinValue.getFloat();
/* 153 */     float f2 = this.m_MaxValue.getFloat();
/* 154 */     float scale = (f - f1) / (f2 - f1) * hi;
/* 155 */     return hi - Math.round(scale) + y;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jeans\graph\plot\MVerticalStepScale.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */