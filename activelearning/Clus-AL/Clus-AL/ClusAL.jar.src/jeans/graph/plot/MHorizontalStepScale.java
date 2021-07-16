/*     */ package jeans.graph.plot;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.awt.Component;
/*     */ import java.awt.FontMetrics;
/*     */ import java.awt.Graphics;
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
/*     */ public class MHorizontalStepScale
/*     */   extends MGraphScale
/*     */ {
/*     */   public static final int LABEL_GAPX = 20;
/*     */   
/*     */   public boolean tryStep(FontMetrics fm, int x, int wd) {
/*  36 */     int wmin = this.m_MinValue.getWidth(fm);
/*  37 */     int wmax = this.m_MaxValue.getWidth(fm);
/*  38 */     int x1 = x + wmin / 2;
/*  39 */     int x2 = x + wd - wmax / 2;
/*  40 */     double value = this.m_MinValue.getFloat() + this.m_dRealStep;
/*  41 */     double max = this.m_MaxValue.getFloat();
/*  42 */     MDouble calc = new MDouble();
/*  43 */     while (value < max) {
/*  44 */       calc.setRoundValue(value);
/*  45 */       int vwd = calc.getWidth(fm);
/*  46 */       int pos = scaleX(x, wd, calc.getFloat());
/*  47 */       if (pos + vwd / 2 + this.m_iGap > x2)
/*  48 */         break;  if (pos - vwd / 2 - this.m_iGap > x1) {
/*  49 */         x1 = pos + vwd / 2;
/*     */       } else {
/*  51 */         return false;
/*     */       } 
/*  53 */       value += this.m_dRealStep;
/*     */     } 
/*  55 */     return true;
/*     */   }
/*     */   
/*     */   public void autoStep(FontMetrics fm, int x, int wd) {
/*  59 */     this.m_dRealStep = this.m_dStep;
/*  60 */     for (; !tryStep(fm, x, wd); this.m_dRealStep *= 2.0D);
/*     */   }
/*     */   
/*     */   public int getHorzHeight(FontMetrics fm) {
/*  64 */     int hi = 7 + fm.getHeight();
/*  65 */     if (this.m_sLabel != null) hi += fm.getHeight() + 2; 
/*  66 */     return hi;
/*     */   }
/*     */   
/*     */   public int getHorzLeftBound(FontMetrics fm) {
/*  70 */     return this.m_MinValue.getWidth(fm) / 2;
/*     */   }
/*     */   
/*     */   public int getHorzRightBound(FontMetrics fm) {
/*  74 */     return this.m_MaxValue.getWidth(fm) / 2;
/*     */   }
/*     */   
/*     */   public void draw(Component cnv, Graphics g, int x, int y, int wd, int hi) {
/*  78 */     FontMetrics fm = g.getFontMetrics();
/*  79 */     g.drawLine(x, y, x + wd, y);
/*  80 */     g.drawLine(x, y, x, y + 3);
/*  81 */     g.drawLine(x + wd, y, x + wd, y + 3);
/*  82 */     int wmin = this.m_MinValue.getWidth(fm);
/*  83 */     int wmax = this.m_MaxValue.getWidth(fm);
/*  84 */     this.m_MinValue.draw(fm, g, x - wmin / 2, y + 3 + 3);
/*  85 */     this.m_MaxValue.draw(fm, g, x + wd - wmax / 2, y + 3 + 3);
/*  86 */     int x1 = x + wmin / 2;
/*  87 */     int x2 = x + wd - wmax / 2;
/*  88 */     double value = this.m_MinValue.getFloat() + this.m_dRealStep;
/*  89 */     double max = this.m_MaxValue.getFloat();
/*  90 */     MDouble calc = new MDouble();
/*  91 */     while (value < max) {
/*  92 */       calc.setRoundValue(value);
/*  93 */       int vwd = calc.getWidth(fm);
/*  94 */       int pos = scaleX(x, wd, calc.getFloat());
/*  95 */       if (pos + vwd / 2 + this.m_iGap > x2)
/*  96 */         break;  if (pos - vwd / 2 - this.m_iGap > x1) {
/*  97 */         g.drawLine(pos, y, pos, y + 3);
/*  98 */         calc.draw(fm, g, pos - vwd / 2, y + 3 + 3);
/*  99 */         x1 = pos + vwd / 2;
/*     */       } 
/* 101 */       value += this.m_dRealStep;
/*     */     } 
/* 103 */     if (this.m_sLabel != null) {
/* 104 */       Color old = g.getColor();
/* 105 */       g.setColor(this.m_LabelColor);
/* 106 */       int lwd = fm.stringWidth(this.m_sLabel);
/* 107 */       g.drawString(this.m_sLabel, x + wd / 2 - lwd / 2, y + 3 + 3 + fm.getAscent() + fm.getHeight());
/* 108 */       g.setColor(old);
/*     */     } 
/*     */   }
/*     */   
/*     */   public int scaleX(int x, int wd, float f) {
/* 113 */     float f1 = this.m_MinValue.getFloat();
/* 114 */     float f2 = this.m_MaxValue.getFloat();
/* 115 */     float scale = (f - f1) / (f2 - f1) * wd;
/* 116 */     return Math.round(scale) + x;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\jeans\graph\plot\MHorizontalStepScale.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */