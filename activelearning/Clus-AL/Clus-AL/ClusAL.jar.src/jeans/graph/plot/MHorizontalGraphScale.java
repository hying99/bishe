/*    */ package jeans.graph.plot;
/*    */ 
/*    */ import java.awt.Color;
/*    */ import java.awt.Component;
/*    */ import java.awt.FontMetrics;
/*    */ import java.awt.Graphics;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class MHorizontalGraphScale
/*    */   extends MGraphScale
/*    */ {
/*    */   public static final int LABEL_GAPX = 20;
/*    */   
/*    */   public int getHorzHeight(FontMetrics fm) {
/* 36 */     int hi = 7 + fm.getHeight();
/* 37 */     if (this.m_sLabel != null) hi += fm.getHeight() + 2; 
/* 38 */     return hi;
/*    */   }
/*    */   
/*    */   public int getHorzLeftBound(FontMetrics fm) {
/* 42 */     return this.m_MinValue.getWidth(fm) / 2;
/*    */   }
/*    */   
/*    */   public int getHorzRightBound(FontMetrics fm) {
/* 46 */     return this.m_MaxValue.getWidth(fm) / 2;
/*    */   }
/*    */   
/*    */   public void draw(Component cnv, Graphics g, int x, int y, int wd, int hi) {
/* 50 */     FontMetrics fm = g.getFontMetrics();
/* 51 */     g.drawLine(x, y, x + wd, y);
/* 52 */     g.drawLine(x, y, x, y + 3);
/* 53 */     g.drawLine(x + wd, y, x + wd, y + 3);
/* 54 */     int wmin = this.m_MinValue.getWidth(fm);
/* 55 */     int wmax = this.m_MaxValue.getWidth(fm);
/* 56 */     this.m_MinValue.draw(fm, g, x - wmin / 2, y + 3 + 3);
/* 57 */     this.m_MaxValue.draw(fm, g, x + wd - wmax / 2, y + 3 + 3);
/* 58 */     int nx1 = x + wmin / 2;
/* 59 */     int nx2 = x + wd - wmax / 2;
/* 60 */     drawXRecursive(g, fm, x, y, wd, this.m_MinValue.getFloat(), this.m_MaxValue.getFloat(), nx1, nx2);
/* 61 */     if (this.m_sLabel != null) {
/* 62 */       Color old = g.getColor();
/* 63 */       g.setColor(this.m_LabelColor);
/* 64 */       int lwd = fm.stringWidth(this.m_sLabel);
/* 65 */       g.drawString(this.m_sLabel, x + wd / 2 - lwd / 2, y + 3 + 3 + fm.getAscent() + fm.getHeight());
/* 66 */       g.setColor(old);
/*    */     } 
/*    */   }
/*    */   
/*    */   public void drawXRecursive(Graphics g, FontMetrics fm, int x, int y, int wd, float f1, float f2, int x1, int x2) {
/* 71 */     MDouble calc = new MDouble();
/* 72 */     calc.setRoundValue(((f1 + f2) / 2.0F));
/* 73 */     int pos = scaleX(x, wd, calc.getFloat());
/* 74 */     int vwd = calc.getWidth(fm);
/* 75 */     if (pos - vwd / 2 - this.m_iGap > x1 && pos + vwd / 2 + this.m_iGap < x2) {
/* 76 */       g.drawLine(pos, y, pos, y + 3);
/* 77 */       calc.draw(fm, g, pos - vwd / 2, y + 3 + 3);
/* 78 */       drawXRecursive(g, fm, x, y, wd, f1, calc.getFloat(), x1, pos - vwd / 2);
/* 79 */       drawXRecursive(g, fm, x, y, wd, calc.getFloat(), f2, pos + vwd / 2, x2);
/*    */     } 
/*    */   }
/*    */   
/*    */   public int scaleX(int x, int wd, float f) {
/* 84 */     float f1 = this.m_MinValue.getFloat();
/* 85 */     float f2 = this.m_MaxValue.getFloat();
/* 86 */     float scale = (f - f1) / (f2 - f1) * wd;
/* 87 */     return Math.round(scale) + x;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\jeans\graph\plot\MHorizontalGraphScale.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */