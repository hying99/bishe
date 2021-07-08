/*     */ package jeans.graph;
/*     */ 
/*     */ import java.awt.Canvas;
/*     */ import java.awt.Color;
/*     */ import java.awt.Dimension;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DigitalCanvas
/*     */   extends Canvas
/*     */ {
/*     */   public static final long serialVersionUID = 1L;
/*  74 */   public int number = 0;
/*  75 */   public int digits = 4;
/*  76 */   public int sep = -1;
/*  77 */   public char sepchar = '.';
/*     */   
/*  79 */   public Color bkg_col = Color.black;
/*  80 */   public Color dig_on_col = Color.red;
/*  81 */   public Color dig_off_col = Color.black;
/*     */   
/*     */   Image bufImg;
/*     */   Graphics bufGrp;
/*     */   Dimension bufSiz;
/*     */   private int[] xpts;
/*     */   private int[] ypts;
/*  88 */   private static final int[] segs = new int[] { 63, 3, 109, 103, 83, 118, 126, 35, 127, 119 };
/*     */ 
/*     */ 
/*     */   
/*     */   public DigitalCanvas() {
/*  93 */     this.xpts = new int[6];
/*  94 */     this.ypts = new int[6];
/*     */   }
/*     */   
/*     */   public void settings(int dig, int s, char sc) {
/*  98 */     this.digits = dig; this.sep = s; this.sepchar = sc;
/*     */   }
/*     */   
/*     */   public void reSize(Dimension d) {
/* 102 */     if (this.bufGrp == null || d.width != this.bufSiz.width || d.height != this.bufSiz.height) {
/*     */       
/* 104 */       if (d.width == 0 || d.height == 0)
/* 105 */         return;  this.bufSiz = d;
/* 106 */       this.bufImg = createImage(d.width, d.height);
/* 107 */       this.bufGrp = this.bufImg.getGraphics();
/*     */     } 
/* 109 */     this.bufGrp.setColor(this.bkg_col);
/* 110 */     this.bufGrp.fillRect(0, 0, d.width, d.height);
/*     */   }
/*     */   
/*     */   public void drawSeparator(int x, int y, int h, int d) {
/* 114 */     this.bufGrp.setColor(this.dig_on_col);
/* 115 */     if (this.sepchar == ':') {
/* 116 */       this.bufGrp.fillRect(x, y + h / 5, d, d);
/* 117 */       this.bufGrp.fillRect(x, y + 4 * h / 5 - d, d, d);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void drawDigit(int x, int y, int w, int h, int d, int n) {
/* 122 */     int seg = segs[n];
/*     */     
/* 124 */     if ((seg & 0x20) != 0) { this.bufGrp.setColor(this.dig_on_col); }
/* 125 */     else { this.bufGrp.setColor(this.dig_off_col); }
/* 126 */      this.xpts[0] = x + 2 * d / 3; this.ypts[0] = y + d / 2;
/* 127 */     this.xpts[1] = x + d; this.ypts[1] = y + d / 6;
/* 128 */     this.xpts[2] = x + d + w; this.ypts[2] = y + d / 6;
/* 129 */     this.xpts[3] = x + d + w + d / 3; this.ypts[3] = y + d / 2;
/* 130 */     this.xpts[4] = x + d + w; this.ypts[4] = y + 5 * d / 6;
/* 131 */     this.xpts[5] = x + d; this.ypts[5] = y + 5 * d / 6;
/* 132 */     this.bufGrp.fillPolygon(this.xpts, this.ypts, 6);
/*     */     
/* 134 */     if ((seg & 0x40) != 0) { this.bufGrp.setColor(this.dig_on_col); }
/* 135 */     else { this.bufGrp.setColor(this.dig_off_col); }
/* 136 */      this.xpts[0] = x + 2 * d / 3; this.ypts[0] = y + d / 2 + d + h;
/* 137 */     this.xpts[1] = x + d; this.ypts[1] = y + d / 6 + d + h;
/* 138 */     this.xpts[2] = x + d + w; this.ypts[2] = y + d / 6 + d + h;
/* 139 */     this.xpts[3] = x + d + w + d / 3; this.ypts[3] = y + d / 2 + d + h;
/* 140 */     this.xpts[4] = x + d + w; this.ypts[4] = y + 5 * d / 6 + d + h;
/* 141 */     this.xpts[5] = x + d; this.ypts[5] = y + 5 * d / 6 + d + h;
/* 142 */     this.bufGrp.fillPolygon(this.xpts, this.ypts, 6);
/*     */     
/* 144 */     if ((seg & 0x4) != 0) { this.bufGrp.setColor(this.dig_on_col); }
/* 145 */     else { this.bufGrp.setColor(this.dig_off_col); }
/* 146 */      this.xpts[0] = x + 2 * d / 3; this.ypts[0] = y + d / 2 + 2 * (d + h);
/* 147 */     this.xpts[1] = x + d; this.ypts[1] = y + d / 6 + 2 * (d + h);
/* 148 */     this.xpts[2] = x + d + w; this.ypts[2] = y + d / 6 + 2 * (d + h);
/* 149 */     this.xpts[3] = x + d + w + d / 3; this.ypts[3] = y + d / 2 + 2 * (d + h);
/* 150 */     this.xpts[4] = x + d + w; this.ypts[4] = y + 5 * d / 6 + 2 * (d + h);
/* 151 */     this.xpts[5] = x + d; this.ypts[5] = y + 5 * d / 6 + 2 * (d + h);
/* 152 */     this.bufGrp.fillPolygon(this.xpts, this.ypts, 6);
/*     */     
/* 154 */     if ((seg & 0x10) != 0) { this.bufGrp.setColor(this.dig_on_col); }
/* 155 */     else { this.bufGrp.setColor(this.dig_off_col); }
/* 156 */      this.xpts[0] = x + d / 2; this.ypts[0] = y + 2 * d / 3;
/* 157 */     this.xpts[1] = x + 5 * d / 6; this.ypts[1] = y + d;
/* 158 */     this.xpts[2] = x + 5 * d / 6; this.ypts[2] = y + d + h;
/* 159 */     this.xpts[3] = x + d / 2; this.ypts[3] = y + d + h + d / 3;
/* 160 */     this.xpts[4] = x + d / 6; this.ypts[4] = y + d + h;
/* 161 */     this.xpts[5] = x + d / 6; this.ypts[5] = y + d;
/* 162 */     this.bufGrp.fillPolygon(this.xpts, this.ypts, 6);
/*     */     
/* 164 */     if ((seg & 0x1) != 0) { this.bufGrp.setColor(this.dig_on_col); }
/* 165 */     else { this.bufGrp.setColor(this.dig_off_col); }
/* 166 */      this.xpts[0] = x + d / 2 + d + w; this.ypts[0] = y + 2 * d / 3;
/* 167 */     this.xpts[1] = x + 5 * d / 6 + d + w; this.ypts[1] = y + d;
/* 168 */     this.xpts[2] = x + 5 * d / 6 + d + w; this.ypts[2] = y + d + h;
/* 169 */     this.xpts[3] = x + d / 2 + d + w; this.ypts[3] = y + d + h + d / 3;
/* 170 */     this.xpts[4] = x + d / 6 + d + w; this.ypts[4] = y + d + h;
/* 171 */     this.xpts[5] = x + d / 6 + d + w; this.ypts[5] = y + d;
/* 172 */     this.bufGrp.fillPolygon(this.xpts, this.ypts, 6);
/*     */     
/* 174 */     if ((seg & 0x8) != 0) { this.bufGrp.setColor(this.dig_on_col); }
/* 175 */     else { this.bufGrp.setColor(this.dig_off_col); }
/* 176 */      this.xpts[0] = x + d / 2; this.ypts[0] = y + 2 * d / 3 + d + h;
/* 177 */     this.xpts[1] = x + 5 * d / 6; this.ypts[1] = y + d + d + h;
/* 178 */     this.xpts[2] = x + 5 * d / 6; this.ypts[2] = y + d + h + d + h;
/* 179 */     this.xpts[3] = x + d / 2; this.ypts[3] = y + d + h + d / 3 + d + h;
/* 180 */     this.xpts[4] = x + d / 6; this.ypts[4] = y + d + h + d + h;
/* 181 */     this.xpts[5] = x + d / 6; this.ypts[5] = y + d + d + h;
/* 182 */     this.bufGrp.fillPolygon(this.xpts, this.ypts, 6);
/*     */     
/* 184 */     if ((seg & 0x2) != 0) { this.bufGrp.setColor(this.dig_on_col); }
/* 185 */     else { this.bufGrp.setColor(this.dig_off_col); }
/* 186 */      this.xpts[0] = x + d / 2 + d + w; this.ypts[0] = y + 2 * d / 3 + d + h;
/* 187 */     this.xpts[1] = x + 5 * d / 6 + d + w; this.ypts[1] = y + d + d + h;
/* 188 */     this.xpts[2] = x + 5 * d / 6 + d + w; this.ypts[2] = y + d + h + d + h;
/* 189 */     this.xpts[3] = x + d / 2 + d + w; this.ypts[3] = y + d + h + d / 3 + d + h;
/* 190 */     this.xpts[4] = x + d / 6 + d + w; this.ypts[4] = y + d + h + d + h;
/* 191 */     this.xpts[5] = x + d / 6 + d + w; this.ypts[5] = y + d + d + h;
/* 192 */     this.bufGrp.fillPolygon(this.xpts, this.ypts, 6);
/*     */   }
/*     */   
/*     */   public void toScreen(Graphics g) {
/* 196 */     g.drawImage(this.bufImg, 0, 0, this);
/*     */   }
/*     */   
/*     */   public void paint(Graphics g) {
/* 200 */     update(g);
/*     */   }
/*     */   
/*     */   public void update(Graphics g) {
/* 204 */     Dimension d = getSize();
/* 205 */     reSize(d);
/* 206 */     if (this.bufGrp != null) {
/* 207 */       drawNumber();
/* 208 */       toScreen(g);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void drawNumber() {
/* 214 */     int d = this.bufSiz.width * 7 / 120;
/* 215 */     int h = (this.bufSiz.height * 90 / 100 - 3 * d) / 2;
/* 216 */     int y = this.bufSiz.height * 5 / 100;
/* 217 */     int wd = this.bufSiz.width * 90 / 100;
/* 218 */     int num = this.number;
/* 219 */     if (this.sep == -1) {
/* 220 */       int w = (wd - 3 * d * this.digits + d + this.digits / 2) / this.digits;
/* 221 */       int x = (this.bufSiz.width - wd) / 2;
/* 222 */       for (int ctr = this.digits - 1; ctr >= 0; ctr--) {
/* 223 */         drawDigit(x + ctr * (w + 3 * d), y, w, h, d, num % 10);
/* 224 */         num /= 10;
/*     */       } 
/*     */     } else {
/* 227 */       int w = (wd - 3 * d * this.digits + d - 2 * d + this.digits / 2) / this.digits;
/* 228 */       int x = (this.bufSiz.width - wd) / 2; int ctr;
/* 229 */       for (ctr = this.digits - 1; ctr >= this.sep; ctr--) {
/* 230 */         drawDigit(x + 2 * d + ctr * (w + 3 * d), y, w, h, d, num % 10);
/* 231 */         num /= 10;
/*     */       } 
/* 233 */       drawSeparator(x + this.sep * (w + 3 * d), y, this.bufSiz.height * 90 / 100, d);
/* 234 */       for (ctr = this.sep - 1; ctr >= 0; ctr--) {
/* 235 */         drawDigit(x + ctr * (w + 3 * d), y, w, h, d, num % 10);
/* 236 */         num /= 10;
/*     */       } 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jeans\graph\DigitalCanvas.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */