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
/*     */ public class MultiLevelMatrixGraph
/*     */   extends JComponent
/*     */ {
/*     */   public static final long serialVersionUID = 1L;
/*  69 */   public static final Font font = new Font("Courrier", 0, 12);
/*     */   
/*     */   int channels;
/*     */   int cols;
/*     */   int rows;
/*     */   Color[] colors;
/*     */   float[][][] matrix;
/*     */   String xlabel;
/*     */   String ylabel;
/*  78 */   MHorizontalStepScale horz = new MHorizontalStepScale();
/*  79 */   MVerticalStepScale vert = new MVerticalStepScale();
/*     */   
/*     */   public MultiLevelMatrixGraph(int chan, int cols, int rows) {
/*  82 */     this.channels = chan;
/*  83 */     this.cols = cols;
/*  84 */     this.rows = rows;
/*  85 */     this.colors = new Color[chan];
/*  86 */     this.matrix = new float[cols][rows][chan];
/*  87 */     for (int ctr = 0; ctr < chan; ) { this.colors[ctr] = Color.black; ctr++; }
/*     */   
/*     */   }
/*     */   public void setXRange(float xmin, float xmax, float step) {
/*  91 */     this.horz.setStep(step);
/*  92 */     this.horz.setMinMax(xmin, xmax);
/*     */   }
/*     */   
/*     */   public void setYRange(float ymin, float ymax, float step) {
/*  96 */     this.vert.setStep(step);
/*  97 */     this.vert.setMinMax(ymin, ymax);
/*     */   }
/*     */ 
/*     */   
/*     */   public void paintComponent(Graphics g) {
/* 102 */     super.paintComponent(g);
/* 103 */     FontMetrics fm = g.getFontMetrics();
/* 104 */     Insets insets = getInsets();
/* 105 */     int m_wd = getWidth() - insets.left - insets.right;
/* 106 */     int m_hi = getHeight() - insets.top - insets.bottom;
/* 107 */     g.setColor(Color.black);
/* 108 */     g.fillRect(insets.left, insets.top, m_wd, m_hi);
/* 109 */     g.setColor(Color.green);
/*     */     
/* 111 */     float zmin = Float.MAX_VALUE, zmax = -1.4E-45F;
/* 112 */     for (int col = 0; col < this.cols; col++) {
/* 113 */       for (int row = 0; row < this.rows; row++) {
/* 114 */         for (int ch = 0; ch < this.channels; ch++) {
/* 115 */           float z = this.matrix[col][row][ch];
/* 116 */           if (z <= zmin) zmin = z; 
/* 117 */           if (z >= zmax) zmax = z;
/*     */         
/*     */         } 
/*     */       } 
/*     */     } 
/* 122 */     int x_max = m_wd - 5 - this.horz.getHorzRightBound(fm) + insets.left;
/* 123 */     int y_xas = m_hi - this.horz.getHorzHeight(fm) + insets.top;
/* 124 */     int y_min = Math.max(3, this.vert.getVertUpperBound(fm)) + insets.top;
/* 125 */     int rowhi = (y_xas - y_min) / this.rows + 1;
/* 126 */     this.vert.autoStep(fm, y_min + rowhi / 2, y_xas - y_min - rowhi);
/* 127 */     int v_wd = this.vert.getVertWidth(this, fm, y_min + rowhi / 2, y_xas - y_min - rowhi);
/* 128 */     int x_yas = Math.max(v_wd, this.horz.getHorzLeftBound(fm)) + insets.left;
/* 129 */     int colwd = (x_max - x_yas) / this.cols + 1;
/* 130 */     this.horz.autoStep(fm, x_yas + colwd / 2, x_max - x_yas - colwd);
/*     */     
/* 132 */     this.horz.draw(this, g, x_yas + colwd / 2, y_xas, x_max - x_yas - colwd, y_xas - y_min);
/* 133 */     this.vert.draw(this, g, x_yas, y_min + rowhi / 2, x_max - x_yas, y_xas - y_min - rowhi);
/* 134 */     g.drawLine(x_yas, y_xas, x_yas + colwd, y_xas);
/* 135 */     g.drawLine(x_max - colwd, y_xas, x_max, y_xas);
/* 136 */     g.drawLine(x_yas, y_xas, x_yas, y_xas - rowhi);
/* 137 */     g.drawLine(x_yas, y_min, x_yas, y_min + rowhi);
/*     */     
/* 139 */     for (int i = 0; i < this.cols; i++) {
/* 140 */       int xp = (x_max - x_yas) * i / this.cols + x_yas + 1;
/* 141 */       for (int row = 0; row < this.rows; row++) {
/* 142 */         int yp = y_xas - (y_xas - y_min) * (row + 1) / this.rows - 1;
/*     */         
/* 144 */         float above = -3.4028235E38F;
/* 145 */         Color color = Color.black;
/* 146 */         for (int ch = 0; ch < this.channels; ch++) {
/* 147 */           float z = this.matrix[i][row][ch];
/* 148 */           if (z > above) {
/* 149 */             above = z;
/* 150 */             color = this.colors[ch];
/*     */           } 
/*     */         } 
/* 153 */         g.setColor(calcC(color, above, zmin, zmax));
/* 154 */         g.fillRect(xp, yp, colwd, rowhi);
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public void xGap(int gap) {
/* 160 */     this.horz.setGap(gap);
/*     */   }
/*     */   
/*     */   public void yGap(int gap) {
/* 164 */     this.vert.setGap(gap);
/*     */   }
/*     */   
/*     */   public void xLabel(String label) {
/* 168 */     this.horz.setLabel(label);
/*     */   }
/*     */   
/*     */   public void yLabel(String label) {
/* 172 */     this.vert.setLabel(label);
/*     */   }
/*     */   
/*     */   public void color(int which, Color color) {
/* 176 */     this.colors[which] = color;
/*     */   }
/*     */   
/*     */   public void setMatrix(int col, int row, int chan, float value) {
/* 180 */     this.matrix[col][row][chan] = value;
/*     */   }
/*     */   
/*     */   public Color calcC(Color color, float val, float min, float max) {
/* 184 */     float fac = Math.min((val - min) / (max - min) + 0.25F, 1.0F);
/* 185 */     float red = fac * color.getRed();
/* 186 */     float green = fac * color.getGreen();
/* 187 */     float blue = fac * color.getBlue();
/* 188 */     return new Color((int)red, (int)green, (int)blue);
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jeans\graph\plot\MultiLevelMatrixGraph.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */