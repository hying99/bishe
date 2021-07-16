/*     */ package jeans.graph.image;
/*     */ 
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class BitFile
/*     */ {
/*     */   OutputStream output_;
/*     */   byte[] buffer_;
/*     */   int index_;
/*     */   int bitsLeft_;
/*     */   
/*     */   public BitFile(OutputStream output) {
/* 228 */     this.output_ = output;
/* 229 */     this.buffer_ = new byte[256];
/* 230 */     this.index_ = 0;
/* 231 */     this.bitsLeft_ = 8;
/*     */   }
/*     */   
/*     */   public void Flush() throws IOException {
/* 235 */     int numBytes = this.index_ + ((this.bitsLeft_ == 8) ? 0 : 1);
/* 236 */     if (numBytes > 0) {
/* 237 */       this.output_.write(numBytes);
/* 238 */       this.output_.write(this.buffer_, 0, numBytes);
/* 239 */       this.buffer_[0] = 0;
/* 240 */       this.index_ = 0;
/* 241 */       this.bitsLeft_ = 8;
/*     */     } 
/*     */   }
/*     */   
/*     */   public void WriteBits(int bits, int numbits) throws IOException {
/* 246 */     int bitsWritten = 0;
/* 247 */     int numBytes = 255;
/*     */     do {
/* 249 */       if ((this.index_ == 254 && this.bitsLeft_ == 0) || this.index_ > 254) {
/* 250 */         this.output_.write(numBytes);
/* 251 */         this.output_.write(this.buffer_, 0, numBytes);
/*     */         
/* 253 */         this.buffer_[0] = 0;
/* 254 */         this.index_ = 0;
/* 255 */         this.bitsLeft_ = 8;
/*     */       } 
/*     */       
/* 258 */       if (numbits <= this.bitsLeft_) {
/* 259 */         this.buffer_[this.index_] = (byte)(this.buffer_[this.index_] | (bits & (1 << numbits) - 1) << 8 - this.bitsLeft_);
/*     */         
/* 261 */         bitsWritten += numbits;
/* 262 */         this.bitsLeft_ -= numbits;
/* 263 */         numbits = 0;
/*     */       } else {
/* 265 */         this.buffer_[this.index_] = (byte)(this.buffer_[this.index_] | (bits & (1 << this.bitsLeft_) - 1) << 8 - this.bitsLeft_);
/*     */         
/* 267 */         bitsWritten += this.bitsLeft_;
/* 268 */         bits >>= this.bitsLeft_;
/* 269 */         numbits -= this.bitsLeft_;
/* 270 */         this.buffer_[++this.index_] = 0;
/* 271 */         this.bitsLeft_ = 8;
/*     */       } 
/* 273 */     } while (numbits != 0);
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\jeans\graph\image\BitFile.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */