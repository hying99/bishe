/*     */ package jeans.graph.image;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class LZWStringTable
/*     */ {
/*     */   private static final int RES_CODES = 2;
/*     */   private static final short HASH_FREE = -1;
/*     */   private static final short NEXT_FIRST = -1;
/*     */   private static final int MAXBITS = 12;
/*     */   private static final int MAXSTR = 4096;
/*     */   private static final short HASHSIZE = 9973;
/*     */   private static final short HASHSTEP = 2039;
/* 293 */   byte[] strChr_ = new byte[4096];
/* 294 */   short[] strNxt_ = new short[4096];
/* 295 */   short[] strHsh_ = new short[9973];
/*     */   
/*     */   short numStrings_;
/*     */ 
/*     */   
/*     */   public int AddCharString(short index, byte b) {
/* 301 */     if (this.numStrings_ >= 4096) {
/* 302 */       return 65535;
/*     */     }
/* 304 */     int hshidx = Hash(index, b);
/* 305 */     while (this.strHsh_[hshidx] != -1) {
/* 306 */       hshidx = (hshidx + 2039) % 9973;
/*     */     }
/* 308 */     this.strHsh_[hshidx] = this.numStrings_;
/* 309 */     this.strChr_[this.numStrings_] = b;
/* 310 */     this.strNxt_[this.numStrings_] = (index != -1) ? index : -1;
/*     */     
/* 312 */     this.numStrings_ = (short)(this.numStrings_ + 1); return this.numStrings_;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public short FindCharString(short index, byte b) {
/* 318 */     if (index == -1) {
/* 319 */       return (short)b;
/*     */     }
/* 321 */     int hshidx = Hash(index, b); int nxtidx;
/* 322 */     while ((nxtidx = this.strHsh_[hshidx]) != -1) {
/* 323 */       if (this.strNxt_[nxtidx] == index && this.strChr_[nxtidx] == b)
/* 324 */         return (short)nxtidx; 
/* 325 */       hshidx = (hshidx + 2039) % 9973;
/*     */     } 
/*     */     
/* 328 */     return -1;
/*     */   }
/*     */   
/*     */   public void ClearTable(int codesize) {
/* 332 */     this.numStrings_ = 0;
/*     */     
/* 334 */     for (int q = 0; q < 9973; q++) {
/* 335 */       this.strHsh_[q] = -1;
/*     */     }
/*     */     
/* 338 */     int w = (1 << codesize) + 2;
/* 339 */     for (int i = 0; i < w; i++)
/* 340 */       AddCharString((short)-1, (byte)i); 
/*     */   }
/*     */   
/*     */   public static int Hash(short index, byte lastbyte) {
/* 344 */     return (((short)(lastbyte << 8) ^ index) & 0xFFFF) % 9973;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\jeans\graph\image\LZWStringTable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */