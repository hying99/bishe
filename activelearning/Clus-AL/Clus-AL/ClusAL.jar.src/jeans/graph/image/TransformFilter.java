/*     */ package jeans.graph.image;
/*     */ 
/*     */ import java.awt.image.ColorModel;
/*     */ import java.awt.image.ImageFilter;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class TransformFilter
/*     */   extends ImageFilter
/*     */ {
/*     */   public static final int Rot90 = 0;
/*     */   public static final int Rot180 = 1;
/*     */   public static final int Rot270 = 2;
/*     */   public static final int FlipHorz = 3;
/*     */   public static final int FlipVert = 4;
/*  51 */   protected static ColorModel defaultRGB = ColorModel.getRGBdefault(); protected int[] raster;
/*     */   protected int dstW;
/*     */   protected int dstH;
/*     */   
/*     */   public TransformFilter(int mytype) {
/*  56 */     this.type = mytype;
/*     */   }
/*     */   protected int srcW; protected int srcH; protected int type;
/*     */   public void setDimensions(int width, int height) {
/*  60 */     if (this.type == 0 || this.type == 2) {
/*  61 */       this.srcH = this.dstW = height;
/*  62 */       this.srcW = this.dstH = width;
/*     */     } else {
/*  64 */       this.srcH = this.dstH = height;
/*  65 */       this.srcW = this.dstW = width;
/*     */     } 
/*  67 */     this.raster = new int[this.srcW * this.srcH];
/*  68 */     this.consumer.setDimensions(this.dstW, this.dstH);
/*     */   }
/*     */   
/*     */   public void setColorModel(ColorModel model) {
/*  72 */     this.consumer.setColorModel(defaultRGB);
/*     */   }
/*     */   
/*     */   public void setHints(int hintflags) {
/*  76 */     this.consumer.setHints(0xE | hintflags & 0x10);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setPixels(int x, int y, int w, int h, ColorModel model, byte[] pixels, int off, int scansize) {
/*  84 */     int srcoff = off;
/*  85 */     int dstoff = y * this.srcW + x;
/*  86 */     for (int yc = 0; yc < h; yc++) {
/*  87 */       for (int xc = 0; xc < w; xc++) {
/*  88 */         this.raster[dstoff++] = model.getRGB(pixels[srcoff++] & 0xFF);
/*     */       }
/*  90 */       srcoff += scansize - w;
/*  91 */       dstoff += this.srcW - w;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void setPixels(int x, int y, int w, int h, ColorModel model, int[] pixels, int off, int scansize) {
/*  97 */     int srcoff = off;
/*  98 */     int dstoff = y * this.srcW + x;
/*  99 */     if (model == defaultRGB) {
/* 100 */       for (int yc = 0; yc < h; yc++) {
/* 101 */         System.arraycopy(pixels, srcoff, this.raster, dstoff, w);
/* 102 */         srcoff += scansize;
/* 103 */         dstoff += this.srcW;
/*     */       } 
/*     */     } else {
/* 106 */       for (int yc = 0; yc < h; yc++) {
/* 107 */         for (int xc = 0; xc < w; xc++) {
/* 108 */           this.raster[dstoff++] = model.getRGB(pixels[srcoff++]);
/*     */         }
/* 110 */         srcoff += scansize - w;
/* 111 */         dstoff += this.srcW - w;
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public void rot90deg() {
/* 117 */     int[] pixels = new int[this.dstW];
/*     */     
/* 119 */     int mxaddr = this.srcW * this.srcH;
/* 120 */     for (int dy = 0; dy < this.dstH; dy++) {
/* 121 */       for (int dx = 0; dx < this.dstW; dx++) {
/*     */         
/* 123 */         int addr = dx * this.srcW + this.srcW - dy - 1;
/* 124 */         if (addr >= 0 && addr < mxaddr) {
/* 125 */           pixels[dx] = this.raster[addr];
/*     */         } else {
/* 127 */           pixels[dx] = 0;
/*     */         } 
/*     */       } 
/* 130 */       this.consumer.setPixels(0, dy, this.dstW, 1, defaultRGB, pixels, 0, this.dstW);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void rot180deg() {
/* 135 */     int[] pixels = new int[this.dstW];
/*     */     
/* 137 */     int mxaddr = this.srcW * this.srcH;
/* 138 */     for (int dy = 0; dy < this.dstH; dy++) {
/* 139 */       for (int dx = 0; dx < this.dstW; dx++) {
/* 140 */         int addr = this.dstW - dx + (this.dstH - dy) * this.srcW;
/* 141 */         if (addr >= 0 && addr < mxaddr) {
/* 142 */           pixels[dx] = this.raster[addr];
/*     */         } else {
/* 144 */           pixels[dx] = 0;
/*     */         } 
/*     */       } 
/* 147 */       this.consumer.setPixels(0, dy, this.dstW, 1, defaultRGB, pixels, 0, this.dstW);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void rot270deg() {
/* 152 */     int[] pixels = new int[this.dstW];
/*     */     
/* 154 */     int mxaddr = this.srcW * this.srcH;
/* 155 */     for (int dy = 0; dy < this.dstH; dy++) {
/* 156 */       for (int dx = 0; dx < this.dstW; dx++) {
/* 157 */         int addr = dy + (this.srcH - dx) * this.srcW;
/* 158 */         if (addr >= 0 && addr < mxaddr) {
/* 159 */           pixels[dx] = this.raster[addr];
/*     */         } else {
/* 161 */           pixels[dx] = 0;
/*     */         } 
/*     */       } 
/* 164 */       this.consumer.setPixels(0, dy, this.dstW, 1, defaultRGB, pixels, 0, this.dstW);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void fliphorz() {
/* 169 */     int[] pixels = new int[this.dstW];
/*     */     
/* 171 */     int mxaddr = this.srcW * this.srcH;
/* 172 */     for (int dy = 0; dy < this.dstH; dy++) {
/* 173 */       for (int dx = 0; dx < this.dstW; dx++) {
/* 174 */         int addr = this.dstW - dx - 1 + dy * this.srcW;
/* 175 */         if (addr >= 0 && addr < mxaddr) {
/* 176 */           pixels[dx] = this.raster[addr];
/*     */         } else {
/* 178 */           pixels[dx] = 0;
/*     */         } 
/*     */       } 
/* 181 */       this.consumer.setPixels(0, dy, this.dstW, 1, defaultRGB, pixels, 0, this.dstW);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void flipvert() {
/* 186 */     int[] pixels = new int[this.dstW];
/*     */     
/* 188 */     int mxaddr = this.srcW * this.srcH;
/* 189 */     for (int dy = 0; dy < this.dstH; dy++) {
/* 190 */       for (int dx = 0; dx < this.dstW; dx++) {
/* 191 */         int addr = dx + (this.dstH - dy - 1) * this.srcW;
/* 192 */         if (addr >= 0 && addr < mxaddr) {
/* 193 */           pixels[dx] = this.raster[addr];
/*     */         } else {
/* 195 */           pixels[dx] = 0;
/*     */         } 
/*     */       } 
/* 198 */       this.consumer.setPixels(0, dy, this.dstW, 1, defaultRGB, pixels, 0, this.dstW);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void imageComplete(int status) {
/* 203 */     if (status == 1 || status == 4) {
/* 204 */       this.consumer.imageComplete(status);
/*     */       return;
/*     */     } 
/* 207 */     switch (this.type) { case 0:
/* 208 */         rot90deg(); break;
/* 209 */       case 1: rot180deg(); break;
/* 210 */       case 2: rot270deg(); break;
/* 211 */       case 3: fliphorz(); break;
/* 212 */       case 4: flipvert(); break; }
/*     */     
/* 214 */     this.consumer.imageComplete(status);
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\jeans\graph\image\TransformFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */