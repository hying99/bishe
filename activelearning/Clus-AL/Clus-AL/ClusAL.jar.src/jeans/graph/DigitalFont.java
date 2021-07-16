/*     */ package jeans.graph;
/*     */ 
/*     */ import java.awt.Color;
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
/*     */ public class DigitalFont
/*     */ {
/*  72 */   public Color dig_on_col = Color.green;
/*  73 */   public Color dig_off_col = Color.black;
/*     */   
/*     */   Graphics bufGrp;
/*     */   private int[] xpts;
/*     */   private int[] ypts;
/*  78 */   private static final int[] segs = new int[] { 63, 3, 109, 103, 83, 118, 126, 35, 127, 119 };
/*     */ 
/*     */ 
/*     */   
/*     */   public DigitalFont() {
/*  83 */     this.xpts = new int[6];
/*  84 */     this.ypts = new int[6];
/*     */   }
/*     */   
/*     */   public void setGraphics(Graphics graph) {
/*  88 */     this.bufGrp = graph;
/*     */   }
/*     */   
/*     */   public void drawNumber(int x, int y, int m_wd, int m_hi, int num, int dignum) {
/*  92 */     int d = Math.min(m_hi / 6, m_wd / dignum / 5);
/*  93 */     int w = (m_wd - (dignum - 1) * d * 2 / 3 - dignum * 2 * d) / dignum;
/*  94 */     int h = (m_hi - 3 * d) / 2;
/*  95 */     int t_wd = (w + 2 * d) * dignum + (dignum - 1) * 3 * d / 2;
/*  96 */     int xp = x + t_wd + (m_wd - t_wd) / 2;
/*  97 */     int yp = y;
/*  98 */     int betw = num;
/*  99 */     for (int dig = 0; dig < dignum; dig++) {
/* 100 */       int digit = betw % 10;
/* 101 */       xp -= w + 2 * d;
/* 102 */       drawDigit(xp, yp, w, h, d, digit);
/* 103 */       xp -= 2 * d / 3;
/* 104 */       betw /= 10;
/*     */     } 
/*     */   }
/*     */   
/*     */   public void drawSeparator(int x, int y, int h, int d, char sepchar) {
/* 109 */     this.bufGrp.setColor(this.dig_on_col);
/* 110 */     if (sepchar == ':') {
/* 111 */       this.bufGrp.fillRect(x, y + h / 5, d, d);
/* 112 */       this.bufGrp.fillRect(x, y + 4 * h / 5 - d, d, d);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void drawDigit(int x, int y, int w, int h, int d, int n) {
/* 117 */     int seg = segs[n];
/*     */     
/* 119 */     if ((seg & 0x20) != 0) { this.bufGrp.setColor(this.dig_on_col); }
/* 120 */     else { this.bufGrp.setColor(this.dig_off_col); }
/* 121 */      this.xpts[0] = x + 2 * d / 3; this.ypts[0] = y + d / 2;
/* 122 */     this.xpts[1] = x + d; this.ypts[1] = y + d / 6;
/* 123 */     this.xpts[2] = x + d + w; this.ypts[2] = y + d / 6;
/* 124 */     this.xpts[3] = x + d + w + d / 3; this.ypts[3] = y + d / 2;
/* 125 */     this.xpts[4] = x + d + w; this.ypts[4] = y + 5 * d / 6;
/* 126 */     this.xpts[5] = x + d; this.ypts[5] = y + 5 * d / 6;
/* 127 */     this.bufGrp.fillPolygon(this.xpts, this.ypts, 6);
/*     */     
/* 129 */     if ((seg & 0x40) != 0) { this.bufGrp.setColor(this.dig_on_col); }
/* 130 */     else { this.bufGrp.setColor(this.dig_off_col); }
/* 131 */      this.xpts[0] = x + 2 * d / 3; this.ypts[0] = y + d / 2 + d + h;
/* 132 */     this.xpts[1] = x + d; this.ypts[1] = y + d / 6 + d + h;
/* 133 */     this.xpts[2] = x + d + w; this.ypts[2] = y + d / 6 + d + h;
/* 134 */     this.xpts[3] = x + d + w + d / 3; this.ypts[3] = y + d / 2 + d + h;
/* 135 */     this.xpts[4] = x + d + w; this.ypts[4] = y + 5 * d / 6 + d + h;
/* 136 */     this.xpts[5] = x + d; this.ypts[5] = y + 5 * d / 6 + d + h;
/* 137 */     this.bufGrp.fillPolygon(this.xpts, this.ypts, 6);
/*     */     
/* 139 */     if ((seg & 0x4) != 0) { this.bufGrp.setColor(this.dig_on_col); }
/* 140 */     else { this.bufGrp.setColor(this.dig_off_col); }
/* 141 */      this.xpts[0] = x + 2 * d / 3; this.ypts[0] = y + d / 2 + 2 * (d + h);
/* 142 */     this.xpts[1] = x + d; this.ypts[1] = y + d / 6 + 2 * (d + h);
/* 143 */     this.xpts[2] = x + d + w; this.ypts[2] = y + d / 6 + 2 * (d + h);
/* 144 */     this.xpts[3] = x + d + w + d / 3; this.ypts[3] = y + d / 2 + 2 * (d + h);
/* 145 */     this.xpts[4] = x + d + w; this.ypts[4] = y + 5 * d / 6 + 2 * (d + h);
/* 146 */     this.xpts[5] = x + d; this.ypts[5] = y + 5 * d / 6 + 2 * (d + h);
/* 147 */     this.bufGrp.fillPolygon(this.xpts, this.ypts, 6);
/*     */     
/* 149 */     if ((seg & 0x10) != 0) { this.bufGrp.setColor(this.dig_on_col); }
/* 150 */     else { this.bufGrp.setColor(this.dig_off_col); }
/* 151 */      this.xpts[0] = x + d / 2; this.ypts[0] = y + 2 * d / 3;
/* 152 */     this.xpts[1] = x + 5 * d / 6; this.ypts[1] = y + d;
/* 153 */     this.xpts[2] = x + 5 * d / 6; this.ypts[2] = y + d + h;
/* 154 */     this.xpts[3] = x + d / 2; this.ypts[3] = y + d + h + d / 3;
/* 155 */     this.xpts[4] = x + d / 6; this.ypts[4] = y + d + h;
/* 156 */     this.xpts[5] = x + d / 6; this.ypts[5] = y + d;
/* 157 */     this.bufGrp.fillPolygon(this.xpts, this.ypts, 6);
/*     */     
/* 159 */     if ((seg & 0x1) != 0) { this.bufGrp.setColor(this.dig_on_col); }
/* 160 */     else { this.bufGrp.setColor(this.dig_off_col); }
/* 161 */      this.xpts[0] = x + d / 2 + d + w; this.ypts[0] = y + 2 * d / 3;
/* 162 */     this.xpts[1] = x + 5 * d / 6 + d + w; this.ypts[1] = y + d;
/* 163 */     this.xpts[2] = x + 5 * d / 6 + d + w; this.ypts[2] = y + d + h;
/* 164 */     this.xpts[3] = x + d / 2 + d + w; this.ypts[3] = y + d + h + d / 3;
/* 165 */     this.xpts[4] = x + d / 6 + d + w; this.ypts[4] = y + d + h;
/* 166 */     this.xpts[5] = x + d / 6 + d + w; this.ypts[5] = y + d;
/* 167 */     this.bufGrp.fillPolygon(this.xpts, this.ypts, 6);
/*     */     
/* 169 */     if ((seg & 0x8) != 0) { this.bufGrp.setColor(this.dig_on_col); }
/* 170 */     else { this.bufGrp.setColor(this.dig_off_col); }
/* 171 */      this.xpts[0] = x + d / 2; this.ypts[0] = y + 2 * d / 3 + d + h;
/* 172 */     this.xpts[1] = x + 5 * d / 6; this.ypts[1] = y + d + d + h;
/* 173 */     this.xpts[2] = x + 5 * d / 6; this.ypts[2] = y + d + h + d + h;
/* 174 */     this.xpts[3] = x + d / 2; this.ypts[3] = y + d + h + d / 3 + d + h;
/* 175 */     this.xpts[4] = x + d / 6; this.ypts[4] = y + d + h + d + h;
/* 176 */     this.xpts[5] = x + d / 6; this.ypts[5] = y + d + d + h;
/* 177 */     this.bufGrp.fillPolygon(this.xpts, this.ypts, 6);
/*     */     
/* 179 */     if ((seg & 0x2) != 0) { this.bufGrp.setColor(this.dig_on_col); }
/* 180 */     else { this.bufGrp.setColor(this.dig_off_col); }
/* 181 */      this.xpts[0] = x + d / 2 + d + w; this.ypts[0] = y + 2 * d / 3 + d + h;
/* 182 */     this.xpts[1] = x + 5 * d / 6 + d + w; this.ypts[1] = y + d + d + h;
/* 183 */     this.xpts[2] = x + 5 * d / 6 + d + w; this.ypts[2] = y + d + h + d + h;
/* 184 */     this.xpts[3] = x + d / 2 + d + w; this.ypts[3] = y + d + h + d / 3 + d + h;
/* 185 */     this.xpts[4] = x + d / 6 + d + w; this.ypts[4] = y + d + h + d + h;
/* 186 */     this.xpts[5] = x + d / 6 + d + w; this.ypts[5] = y + d + d + h;
/* 187 */     this.bufGrp.fillPolygon(this.xpts, this.ypts, 6);
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\jeans\graph\DigitalFont.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */