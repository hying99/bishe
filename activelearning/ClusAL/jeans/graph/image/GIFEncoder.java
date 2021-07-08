/*     */ package jeans.graph.image;
/*     */ 
/*     */ import java.awt.AWTException;
/*     */ import java.awt.Image;
/*     */ import java.awt.image.PixelGrabber;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class GIFEncoder
/*     */ {
/*     */   short width_;
/*     */   short height_;
/*     */   int numColors_;
/*     */   byte[] pixels_;
/*     */   byte[] colors_;
/*     */   ScreenDescriptor sd_;
/*     */   ImageDescriptor id_;
/*     */   
/*     */   public GIFEncoder(Image image) throws AWTException {
/*  97 */     this.width_ = (short)image.getWidth(null);
/*  98 */     this.height_ = (short)image.getHeight(null);
/*     */     
/* 100 */     int[] values = new int[this.width_ * this.height_];
/* 101 */     PixelGrabber grabber = new PixelGrabber(image, 0, 0, this.width_, this.height_, values, 0, this.width_);
/*     */ 
/*     */     
/*     */     try {
/* 105 */       if (grabber.grabPixels() != true) {
/* 106 */         throw new AWTException("Grabber returned false: " + grabber
/* 107 */             .status());
/*     */       }
/* 109 */     } catch (InterruptedException interruptedException) {}
/*     */     
/* 111 */     byte[][] r = new byte[this.width_][this.height_];
/* 112 */     byte[][] g = new byte[this.width_][this.height_];
/* 113 */     byte[][] b = new byte[this.width_][this.height_];
/*     */     
/* 115 */     int index = 0;
/*     */     
/* 117 */     for (int y = 0; y < this.height_; y++) {
/* 118 */       for (int x = 0; x < this.width_; x++) {
/* 119 */         r[x][y] = (byte)(values[index] >> 16 & 0xFF);
/* 120 */         g[x][y] = (byte)(values[index] >> 8 & 0xFF);
/* 121 */         b[x][y] = (byte)(values[index] & 0xFF);
/* 122 */         index++;
/*     */       } 
/* 124 */     }  ToIndexedColor(r, g, b);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public GIFEncoder(byte[][] r, byte[][] g, byte[][] b) throws AWTException {
/* 145 */     this.width_ = (short)r.length;
/* 146 */     this.height_ = (short)(r[0]).length;
/*     */     
/* 148 */     ToIndexedColor(r, g, b);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void Write(OutputStream output) throws IOException {
/* 163 */     BitUtils.WriteString(output, "GIF87a");
/*     */     
/* 165 */     ScreenDescriptor sd = new ScreenDescriptor(this.width_, this.height_, this.numColors_);
/*     */     
/* 167 */     sd.Write(output);
/*     */     
/* 169 */     output.write(this.colors_, 0, this.colors_.length);
/* 170 */     ImageDescriptor id = new ImageDescriptor(this.width_, this.height_, ',');
/* 171 */     id.Write(output);
/*     */     
/* 173 */     byte codesize = BitUtils.BitsNeeded(this.numColors_);
/* 174 */     if (codesize == 1)
/* 175 */       codesize = (byte)(codesize + 1); 
/* 176 */     output.write(codesize);
/*     */     
/* 178 */     LZWCompressor.LZWCompress(output, codesize, this.pixels_);
/* 179 */     output.write(0);
/*     */     
/* 181 */     id = new ImageDescriptor((short)0, (short)0, ';');
/* 182 */     id.Write(output);
/* 183 */     output.flush();
/*     */   }
/*     */ 
/*     */   
/*     */   void ToIndexedColor(byte[][] r, byte[][] g, byte[][] b) throws AWTException {
/* 188 */     this.pixels_ = new byte[this.width_ * this.height_];
/* 189 */     this.colors_ = new byte[768];
/* 190 */     int colornum = 0;
/* 191 */     for (int x = 0; x < this.width_; x++) {
/* 192 */       for (int y = 0; y < this.height_; y++) {
/*     */         int search;
/* 194 */         for (search = 0; search < colornum && (
/* 195 */           this.colors_[search * 3] != r[x][y] || this.colors_[search * 3 + 1] != g[x][y] || this.colors_[search * 3 + 2] != b[x][y]); search++);
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 200 */         if (search > 255) {
/* 201 */           throw new AWTException("Too many colors.");
/*     */         }
/* 203 */         this.pixels_[y * this.width_ + x] = (byte)search;
/*     */         
/* 205 */         if (search == colornum) {
/* 206 */           this.colors_[search * 3] = r[x][y];
/* 207 */           this.colors_[search * 3 + 1] = g[x][y];
/* 208 */           this.colors_[search * 3 + 2] = b[x][y];
/* 209 */           colornum++;
/*     */         } 
/*     */       } 
/*     */     } 
/* 213 */     this.numColors_ = 1 << BitUtils.BitsNeeded(colornum);
/* 214 */     byte[] copy = new byte[this.numColors_ * 3];
/* 215 */     System.arraycopy(this.colors_, 0, copy, 0, this.numColors_ * 3);
/* 216 */     this.colors_ = copy;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jeans\graph\image\GIFEncoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */