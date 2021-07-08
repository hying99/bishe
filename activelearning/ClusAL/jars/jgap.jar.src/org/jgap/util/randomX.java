/*     */ package org.jgap.util;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class randomX
/*     */ {
/*  27 */   private int nbits = 0;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean iset = false;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private double gset;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private byte b;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setSeed() {
/*  50 */     this.nbits = 0;
/*  51 */     this.iset = false;
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
/*     */   public abstract byte nextByte();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int nextInt() {
/*  74 */     return nextShort() << 16 | nextShort() & 0xFFFF;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long nextLong() {
/*  81 */     return nextInt() << 32L | nextInt() & 0xFFFFFFFFL;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float nextFloat() {
/*  88 */     return (float)((nextInt() & Integer.MAX_VALUE) / 2.147483647E9D);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double nextDouble() {
/*  95 */     return (nextLong() & Long.MAX_VALUE) / 9.223372036854776E18D;
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
/*     */   public double nextGaussian() {
/* 108 */     if (!this.iset)
/*     */       while (true) {
/* 110 */         double v1 = 2.0D * nextDouble() - 1.0D;
/* 111 */         double v2 = 2.0D * nextDouble() - 1.0D;
/* 112 */         double rsq = v1 * v1 + v2 * v2;
/* 113 */         if (rsq <= 1.0D && rsq != 0.0D) {
/* 114 */           double fac = Math.sqrt(-2.0D * Math.log(rsq) / rsq);
/* 115 */           this.gset = v1 * fac;
/* 116 */           this.iset = true;
/* 117 */           return v2 * fac;
/*     */         } 
/* 119 */       }   this.iset = false;
/* 120 */     return this.gset;
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
/*     */   public boolean nextBit() {
/* 133 */     if (this.nbits <= 0) {
/* 134 */       this.b = nextByte();
/* 135 */       this.nbits = 8;
/*     */     } 
/* 137 */     boolean bit = ((this.b & 0x80) != 0);
/* 138 */     this.b = (byte)(this.b << 1);
/* 139 */     this.nbits--;
/* 140 */     return bit;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void nextByte(byte[] buf, int buflen) {
/* 151 */     int i = 0;
/*     */     
/* 153 */     while (buflen-- > 0) {
/* 154 */       buf[i++] = nextByte();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void nextByte(byte[] buf) {
/* 164 */     nextByte(buf, buf.length);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public short nextShort() {
/* 171 */     return (short)((short)nextByte() << 8 | (short)(nextByte() & 0xFF));
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jga\\util\randomX.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */