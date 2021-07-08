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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class randomLEcuyer
/*     */   extends randomX
/*     */ {
/*     */   static final long mul1 = 40014L;
/*     */   static final long mod1 = 2147483563L;
/*     */   static final long mul2 = 40692L;
/*     */   static final long mod2 = 2147483399L;
/*     */   static final int shuffleSize = 32;
/*     */   static final int warmup = 19;
/*     */   int gen1;
/*     */   int gen2;
/*     */   int state;
/*     */   int[] shuffle;
/*     */   
/*     */   public randomLEcuyer() {
/*  52 */     this.shuffle = new int[32];
/*  53 */     setSeed(System.currentTimeMillis());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public randomLEcuyer(long seed) throws IllegalArgumentException {
/*  63 */     this.shuffle = new int[32];
/*  64 */     setSeed(seed);
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
/*     */   public void setSeed(long seed) throws IllegalArgumentException {
/*  78 */     if (seed == 0L) {
/*  79 */       throw new IllegalArgumentException("seed must be nonzero");
/*     */     }
/*  81 */     this.gen1 = this.gen2 = (int)(seed & 0x7FFFFFFFFL);
/*     */ 
/*     */     
/*     */     int i;
/*     */     
/*  86 */     for (i = 0; i < 19; i++) {
/*  87 */       this.gen1 = (int)(this.gen1 * 40014L % 2147483563L);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*  92 */     for (i = 0; i < 32; i++) {
/*  93 */       this.gen1 = (int)(this.gen1 * 40014L % 2147483563L);
/*  94 */       this.shuffle[31 - i] = this.gen1;
/*     */     } 
/*  96 */     this.state = this.shuffle[0];
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte nextByte() {
/* 107 */     this.gen1 = (int)(this.gen1 * 40014L % 2147483563L);
/* 108 */     this.gen2 = (int)(this.gen2 * 40692L % 2147483399L);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 113 */     int i = this.state / 67108862;
/*     */ 
/*     */ 
/*     */     
/* 117 */     this.state = (int)((this.shuffle[i] + this.gen2) % 2147483563L);
/*     */ 
/*     */ 
/*     */     
/* 121 */     this.shuffle[i] = this.gen1;
/*     */     
/* 123 */     return (byte)(this.state / 8388608);
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jga\\util\randomLEcuyer.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */