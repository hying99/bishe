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
/*     */ public class ScaleImageFilter
/*     */   extends ImageFilter
/*     */ {
/*  46 */   protected static ColorModel defaultRGB = ColorModel.getRGBdefault();
/*     */   
/*     */   protected int[] raster;
/*     */   protected int width;
/*     */   
/*     */   public ScaleImageFilter(int zoom) {
/*  52 */     this.zoom = zoom;
/*     */   }
/*     */   protected int height; protected int zoom;
/*     */   public void setDimensions(int width, int height) {
/*  56 */     this.width = width;
/*  57 */     this.height = height;
/*  58 */     this.raster = new int[width * height];
/*  59 */     this.consumer.setDimensions(width * this.zoom / 100, height * this.zoom / 100);
/*     */   }
/*     */   
/*     */   public void setColorModel(ColorModel model) {
/*  63 */     this.consumer.setColorModel(defaultRGB);
/*     */   }
/*     */   
/*     */   public void setHints(int hintflags) {
/*  67 */     this.consumer.setHints(0xE | hintflags & 0x10);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setPixels(int x, int y, int w, int h, ColorModel model, byte[] pixels, int off, int scansize) {
/*  72 */     int srcoff = off;
/*  73 */     int dstoff = y * this.width + x;
/*  74 */     for (int yc = 0; yc < h; yc++) {
/*  75 */       for (int xc = 0; xc < w; xc++) {
/*  76 */         this.raster[dstoff++] = model.getRGB(pixels[srcoff++] & 0xFF);
/*     */       }
/*  78 */       srcoff += scansize - w;
/*  79 */       dstoff += this.width - w;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void setPixels(int x, int y, int w, int h, ColorModel model, int[] pixels, int off, int scansize) {
/*  85 */     int srcoff = off;
/*  86 */     int dstoff = y * this.width + x;
/*  87 */     if (model == defaultRGB) {
/*  88 */       for (int yc = 0; yc < h; yc++) {
/*  89 */         System.arraycopy(pixels, srcoff, this.raster, dstoff, w);
/*  90 */         srcoff += scansize;
/*  91 */         dstoff += this.width;
/*     */       } 
/*     */     } else {
/*  94 */       for (int yc = 0; yc < h; yc++) {
/*  95 */         for (int xc = 0; xc < w; xc++) {
/*  96 */           this.raster[dstoff++] = model.getRGB(pixels[srcoff++]);
/*     */         }
/*  98 */         srcoff += scansize - w;
/*  99 */         dstoff += this.width - w;
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public void imageComplete(int status) {
/* 105 */     if (status == 1 || status == 4) {
/* 106 */       this.consumer.imageComplete(status);
/*     */       return;
/*     */     } 
/* 109 */     int scanwd = this.width * this.zoom / 100;
/* 110 */     int scanhi = this.height * this.zoom / 100;
/* 111 */     int[] pixels = new int[scanwd];
/* 112 */     for (int r = 0; r < scanhi; r++) {
/* 113 */       int srcoffs = r * 100 / this.zoom * this.width;
/* 114 */       for (int c = 0; c < scanwd; c++) {
/* 115 */         pixels[c] = this.raster[srcoffs + c * 100 / this.zoom];
/*     */       }
/* 117 */       this.consumer.setPixels(0, r, scanwd, 1, defaultRGB, pixels, 0, scanwd);
/*     */     } 
/* 119 */     this.consumer.imageComplete(status);
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\jeans\graph\image\ScaleImageFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */