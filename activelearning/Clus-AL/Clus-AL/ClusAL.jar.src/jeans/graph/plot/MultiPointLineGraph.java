/*     */ package jeans.graph.plot;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.awt.Font;
/*     */ import java.awt.FontMetrics;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Insets;
/*     */ import javax.swing.JComponent;
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
/*     */ 
/*     */ public class MultiPointLineGraph
/*     */   extends JComponent
/*     */ {
/*     */   public static final long serialVersionUID = 1L;
/*  69 */   public static final Font font = new Font("Courrier", 0, 12);
/*     */   
/*     */   int channels;
/*     */   
/*     */   int width;
/*     */   
/*     */   Color[] colors;
/*     */   float[][] ydata;
/*     */   float[] xdata;
/*     */   String xlabel;
/*     */   String ylabel;
/*  80 */   MHorizontalGraphScale horz = new MHorizontalGraphScale();
/*  81 */   MVerticalGraphScale vert = new MVerticalGraphScale();
/*     */   
/*     */   public MultiPointLineGraph(int num, int width) {
/*  84 */     this.channels = num;
/*  85 */     this.width = width;
/*  86 */     this.colors = new Color[num];
/*  87 */     this.xdata = new float[width];
/*  88 */     this.ydata = new float[width][this.channels];
/*  89 */     for (int ctr = 0; ctr < num; ) { this.colors[ctr] = Color.black; ctr++; }
/*     */   
/*     */   }
/*     */   
/*     */   public void paintComponent(Graphics g) {
/*  94 */     super.paintComponent(g);
/*  95 */     FontMetrics fm = g.getFontMetrics();
/*  96 */     Insets insets = getInsets();
/*  97 */     int m_wd = getWidth() - insets.left - insets.right;
/*  98 */     int m_hi = getHeight() - insets.top - insets.bottom;
/*  99 */     g.setColor(Color.black);
/* 100 */     g.fillRect(insets.left, insets.top, m_wd, m_hi);
/* 101 */     g.setColor(Color.green);
/*     */     
/* 103 */     float xmin = Float.MAX_VALUE, xmax = -3.4028235E38F;
/* 104 */     float ymin = Float.MAX_VALUE, ymax = -3.4028235E38F;
/* 105 */     for (int pt = 0; pt < this.width; pt++) {
/* 106 */       for (int i = 0; i < this.channels; i++) {
/* 107 */         float y = this.ydata[pt][i];
/* 108 */         if (y <= ymin) ymin = y; 
/* 109 */         if (y >= ymax) ymax = y; 
/*     */       } 
/* 111 */       float x = this.xdata[pt];
/* 112 */       if (x <= xmin) xmin = x; 
/* 113 */       if (x >= xmax) xmax = x;
/*     */     
/*     */     } 
/* 116 */     this.horz.setMinMax(xmin, xmax);
/* 117 */     xmin = this.horz.getRealMin();
/* 118 */     xmax = this.horz.getRealMax();
/*     */     
/* 120 */     this.vert.setMinMax(ymin, ymax);
/* 121 */     ymin = this.vert.getRealMin();
/* 122 */     ymax = this.vert.getRealMax();
/*     */     
/* 124 */     int y_min = Math.max(3, this.vert.getVertUpperBound(fm)) + insets.top;
/* 125 */     int y_xas = m_hi + insets.top - this.horz.getHorzHeight(fm);
/* 126 */     int v_wd = this.vert.getVertWidth(this, fm, y_min, y_xas - y_min);
/* 127 */     int x_yas = Math.max(v_wd, this.horz.getHorzLeftBound(fm)) + insets.left;
/* 128 */     int x_max = m_wd + insets.left - 5 - this.horz.getHorzRightBound(fm);
/*     */     
/* 130 */     this.horz.draw(this, g, x_yas, y_xas, x_max - x_yas, y_xas - y_min);
/* 131 */     this.vert.draw(this, g, x_yas, y_min, x_max - x_yas, y_xas - y_min);
/*     */     
/* 133 */     for (int ctr = 0; ctr < this.channels; ctr++) {
/* 134 */       g.setColor(this.colors[ctr]);
/* 135 */       int xprev = calcX(0, xmin, xmax, x_yas, x_max);
/* 136 */       int yprev = calcY(ctr, 0, ymin, ymax, y_min, y_xas);
/* 137 */       g.fillRect(xprev - 2, yprev - 2, 4, 4);
/* 138 */       for (int i = 1; i < this.width; i++) {
/* 139 */         int xp = calcX(i, xmin, xmax, x_yas, x_max);
/* 140 */         int yp = calcY(ctr, i, ymin, ymax, y_min, y_xas);
/* 141 */         g.drawLine(xprev, yprev, xp, yp);
/* 142 */         g.fillRect(xp - 2, yp - 2, 4, 4);
/* 143 */         xprev = xp; yprev = yp;
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public void xLabel(String label) {
/* 149 */     this.horz.setLabel(label);
/*     */   }
/*     */   
/*     */   public void yLabel(String label) {
/* 153 */     this.vert.setLabel(label);
/*     */   }
/*     */   
/*     */   public void addYPoint(int pos, int channel, float value) {
/* 157 */     this.ydata[pos][channel] = value;
/*     */   }
/*     */   
/*     */   public void addXPoint(int pos, float value) {
/* 161 */     this.xdata[pos] = value;
/*     */   }
/*     */   
/*     */   public void color(int which, Color color) {
/* 165 */     this.colors[which] = color;
/*     */   }
/*     */   
/*     */   public int calcX(int num, float xmin, float xmax, int min, int max) {
/* 169 */     float xp = (this.xdata[num] - xmin) / (xmax - xmin) * (max - min);
/* 170 */     return Math.round(xp) + min;
/*     */   }
/*     */   
/*     */   public int calcY(int chan, int num, float ymin, float ymax, int min, int max) {
/* 174 */     float yp = (this.ydata[num][chan] - ymin) / (ymax - ymin);
/* 175 */     yp = (1.0F - yp) * (max - min);
/* 176 */     return (int)yp + min;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\jeans\graph\plot\MultiPointLineGraph.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */