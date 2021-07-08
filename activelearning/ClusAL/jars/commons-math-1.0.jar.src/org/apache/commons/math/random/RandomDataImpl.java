/*     */ package org.apache.commons.math.random;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.security.MessageDigest;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.security.NoSuchProviderException;
/*     */ import java.security.SecureRandom;
/*     */ import java.util.Collection;
/*     */ import java.util.Random;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class RandomDataImpl
/*     */   implements RandomData, Serializable
/*     */ {
/*     */   static final long serialVersionUID = -626730818244969716L;
/*  82 */   private Random rand = null;
/*     */ 
/*     */   
/*  85 */   private SecureRandom secRand = null;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String nextHexString(int len) {
/* 104 */     if (len <= 0) {
/* 105 */       throw new IllegalArgumentException("length must be positive");
/*     */     }
/*     */ 
/*     */     
/* 109 */     Random ran = getRan();
/*     */ 
/*     */     
/* 112 */     StringBuffer outBuffer = new StringBuffer();
/*     */ 
/*     */     
/* 115 */     byte[] randomBytes = new byte[len / 2 + 1];
/* 116 */     ran.nextBytes(randomBytes);
/*     */ 
/*     */     
/* 119 */     for (int i = 0; i < randomBytes.length; i++) {
/* 120 */       Integer c = new Integer(randomBytes[i]);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 127 */       String hex = Integer.toHexString(c.intValue() + 128);
/*     */ 
/*     */       
/* 130 */       if (hex.length() == 1) {
/* 131 */         hex = "0" + hex;
/*     */       }
/* 133 */       outBuffer.append(hex);
/*     */     } 
/* 135 */     return outBuffer.toString().substring(0, len);
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
/*     */   public int nextInt(int lower, int upper) {
/* 147 */     if (lower >= upper) {
/* 148 */       throw new IllegalArgumentException("upper bound must be > lower bound");
/*     */     }
/*     */     
/* 151 */     Random rand = getRan();
/* 152 */     return lower + (int)(rand.nextDouble() * (upper - lower + 1));
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
/*     */   public long nextLong(long lower, long upper) {
/* 164 */     if (lower >= upper) {
/* 165 */       throw new IllegalArgumentException("upper bound must be > lower bound");
/*     */     }
/*     */     
/* 168 */     Random rand = getRan();
/* 169 */     return lower + (long)(rand.nextDouble() * (upper - lower + 1L));
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
/*     */   public String nextSecureHexString(int len) {
/* 187 */     if (len <= 0) {
/* 188 */       throw new IllegalArgumentException("length must be positive");
/*     */     }
/*     */ 
/*     */     
/* 192 */     SecureRandom secRan = getSecRan();
/* 193 */     MessageDigest alg = null;
/*     */     try {
/* 195 */       alg = MessageDigest.getInstance("SHA-1");
/* 196 */     } catch (NoSuchAlgorithmException ex) {
/* 197 */       return null;
/*     */     } 
/* 199 */     alg.reset();
/*     */ 
/*     */     
/* 202 */     int numIter = len / 40 + 1;
/*     */     
/* 204 */     StringBuffer outBuffer = new StringBuffer();
/* 205 */     for (int iter = 1; iter < numIter + 1; iter++) {
/* 206 */       byte[] randomBytes = new byte[40];
/* 207 */       secRan.nextBytes(randomBytes);
/* 208 */       alg.update(randomBytes);
/*     */ 
/*     */       
/* 211 */       byte[] hash = alg.digest();
/*     */ 
/*     */       
/* 214 */       for (int i = 0; i < hash.length; i++) {
/* 215 */         Integer c = new Integer(hash[i]);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 222 */         String hex = Integer.toHexString(c.intValue() + 128);
/*     */ 
/*     */         
/* 225 */         if (hex.length() == 1) {
/* 226 */           hex = "0" + hex;
/*     */         }
/* 228 */         outBuffer.append(hex);
/*     */       } 
/*     */     } 
/* 231 */     return outBuffer.toString().substring(0, len);
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
/*     */   public int nextSecureInt(int lower, int upper) {
/* 244 */     if (lower >= upper) {
/* 245 */       throw new IllegalArgumentException("lower bound must be < upper bound");
/*     */     }
/*     */     
/* 248 */     SecureRandom sec = getSecRan();
/* 249 */     return lower + (int)(sec.nextDouble() * (upper - lower + 1));
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
/*     */   public long nextSecureLong(long lower, long upper) {
/* 262 */     if (lower >= upper) {
/* 263 */       throw new IllegalArgumentException("lower bound must be < upper bound");
/*     */     }
/*     */     
/* 266 */     SecureRandom sec = getSecRan();
/* 267 */     return lower + (long)(sec.nextDouble() * (upper - lower + 1L));
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
/*     */   public long nextPoisson(double mean) {
/* 287 */     if (mean <= 0.0D) {
/* 288 */       throw new IllegalArgumentException("Poisson mean must be > 0");
/*     */     }
/* 290 */     double p = Math.exp(-mean);
/* 291 */     long n = 0L;
/* 292 */     double r = 1.0D;
/* 293 */     double rnd = 1.0D;
/* 294 */     Random rand = getRan();
/* 295 */     while (n < 1000.0D * mean) {
/* 296 */       rnd = rand.nextDouble();
/* 297 */       r *= rnd;
/* 298 */       if (r >= p) {
/* 299 */         n++; continue;
/*     */       } 
/* 301 */       return n;
/*     */     } 
/*     */     
/* 304 */     return n;
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
/*     */   public double nextGaussian(double mu, double sigma) {
/* 317 */     if (sigma <= 0.0D) {
/* 318 */       throw new IllegalArgumentException("Gaussian std dev must be > 0");
/*     */     }
/* 320 */     Random rand = getRan();
/* 321 */     return sigma * rand.nextGaussian() + mu;
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
/*     */   public double nextExponential(double mean) {
/* 337 */     if (mean < 0.0D) {
/* 338 */       throw new IllegalArgumentException("Exponential mean must be >= 0");
/*     */     }
/*     */     
/* 341 */     Random rand = getRan();
/* 342 */     double unif = rand.nextDouble();
/* 343 */     while (unif == 0.0D) {
/* 344 */       unif = rand.nextDouble();
/*     */     }
/* 346 */     return -mean * Math.log(unif);
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
/*     */   public double nextUniform(double lower, double upper) {
/* 360 */     if (lower >= upper) {
/* 361 */       throw new IllegalArgumentException("lower bound must be <= upper bound");
/*     */     }
/*     */     
/* 364 */     Random rand = getRan();
/*     */ 
/*     */     
/* 367 */     double u = rand.nextDouble();
/* 368 */     while (u <= 0.0D) {
/* 369 */       u = rand.nextDouble();
/*     */     }
/*     */     
/* 372 */     return lower + u * (upper - lower);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Random getRan() {
/* 383 */     if (this.rand == null) {
/* 384 */       this.rand = new Random();
/* 385 */       this.rand.setSeed(System.currentTimeMillis());
/*     */     } 
/* 387 */     return this.rand;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private SecureRandom getSecRan() {
/* 398 */     if (this.secRand == null) {
/* 399 */       this.secRand = new SecureRandom();
/* 400 */       this.secRand.setSeed(System.currentTimeMillis());
/*     */     } 
/* 402 */     return this.secRand;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void reSeed(long seed) {
/* 413 */     if (this.rand == null) {
/* 414 */       this.rand = new Random();
/*     */     }
/* 416 */     this.rand.setSeed(seed);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void reSeedSecure() {
/* 426 */     if (this.secRand == null) {
/* 427 */       this.secRand = new SecureRandom();
/*     */     }
/* 429 */     this.secRand.setSeed(System.currentTimeMillis());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void reSeedSecure(long seed) {
/* 440 */     if (this.secRand == null) {
/* 441 */       this.secRand = new SecureRandom();
/*     */     }
/* 443 */     this.secRand.setSeed(seed);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void reSeed() {
/* 451 */     if (this.rand == null) {
/* 452 */       this.rand = new Random();
/*     */     }
/* 454 */     this.rand.setSeed(System.currentTimeMillis());
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
/*     */   
/*     */   public void setSecureAlgorithm(String algorithm, String provider) throws NoSuchAlgorithmException, NoSuchProviderException {
/* 476 */     this.secRand = SecureRandom.getInstance(algorithm, provider);
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
/*     */   public int[] nextPermutation(int n, int k) {
/* 489 */     if (k > n) {
/* 490 */       throw new IllegalArgumentException("permutation k exceeds n");
/*     */     }
/*     */     
/* 493 */     if (k == 0) {
/* 494 */       throw new IllegalArgumentException("permutation k must be > 0");
/*     */     }
/*     */ 
/*     */     
/* 498 */     int[] index = getNatural(n);
/* 499 */     shuffle(index, n - k);
/* 500 */     int[] result = new int[k];
/* 501 */     for (int i = 0; i < k; i++) {
/* 502 */       result[i] = index[n - i - 1];
/*     */     }
/*     */     
/* 505 */     return result;
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
/*     */   public Object[] nextSample(Collection c, int k) {
/* 522 */     int len = c.size();
/* 523 */     if (k > len) {
/* 524 */       throw new IllegalArgumentException("sample size exceeds collection size");
/*     */     }
/*     */     
/* 527 */     if (k == 0) {
/* 528 */       throw new IllegalArgumentException("sample size must be > 0");
/*     */     }
/*     */ 
/*     */     
/* 532 */     Object[] objects = c.toArray();
/* 533 */     int[] index = nextPermutation(len, k);
/* 534 */     Object[] result = new Object[k];
/* 535 */     for (int i = 0; i < k; i++) {
/* 536 */       result[i] = objects[index[i]];
/*     */     }
/* 538 */     return result;
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
/*     */   private void shuffle(int[] list, int end) {
/* 551 */     int target = 0;
/* 552 */     for (int i = list.length - 1; i >= end; i--) {
/* 553 */       if (i == 0) {
/* 554 */         target = 0;
/*     */       } else {
/* 556 */         target = nextInt(0, i);
/*     */       } 
/* 558 */       int temp = list[target];
/* 559 */       list[target] = list[i];
/* 560 */       list[i] = temp;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private int[] getNatural(int n) {
/* 571 */     int[] natural = new int[n];
/* 572 */     for (int i = 0; i < n; i++) {
/* 573 */       natural[i] = i;
/*     */     }
/* 575 */     return natural;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\commons-math-1.0.jar!\org\apache\commons\math\random\RandomDataImpl.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */