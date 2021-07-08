/*     */ package org.apache.commons.math.util;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class MathUtils
/*     */ {
/*     */   private static final byte ZB = 0;
/*     */   private static final byte NB = -1;
/*     */   private static final byte PB = 1;
/*     */   private static final short ZS = 0;
/*     */   private static final short NS = -1;
/*     */   private static final short PS = 1;
/*     */   
/*     */   public static double sign(double x) {
/*  64 */     if (Double.isNaN(x)) {
/*  65 */       return Double.NaN;
/*     */     }
/*  67 */     return (x == 0.0D) ? 0.0D : ((x > 0.0D) ? 1.0D : -1.0D);
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
/*     */   public static float sign(float x) {
/*  83 */     if (Float.isNaN(x)) {
/*  84 */       return Float.NaN;
/*     */     }
/*  86 */     return (x == 0.0F) ? 0.0F : ((x > 0.0F) ? 1.0F : -1.0F);
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
/*     */   public static byte sign(byte x) {
/* 101 */     return (x == 0) ? 0 : ((x > 0) ? 1 : -1);
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
/*     */   public static short sign(short x) {
/* 117 */     return (x == 0) ? 0 : ((x > 0) ? 1 : -1);
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
/*     */   public static int sign(int x) {
/* 132 */     return (x == 0) ? 0 : ((x > 0) ? 1 : -1);
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
/*     */   public static long sign(long x) {
/* 147 */     return (x == 0L) ? 0L : ((x > 0L) ? 1L : -1L);
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
/*     */   public static double indicator(double x) {
/* 159 */     if (Double.isNaN(x)) {
/* 160 */       return Double.NaN;
/*     */     }
/* 162 */     return (x >= 0.0D) ? 1.0D : -1.0D;
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
/*     */   public static float indicator(float x) {
/* 174 */     if (Float.isNaN(x)) {
/* 175 */       return Float.NaN;
/*     */     }
/* 177 */     return (x >= 0.0F) ? 1.0F : -1.0F;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static byte indicator(byte x) {
/* 188 */     return (x >= 0) ? 1 : -1;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static short indicator(short x) {
/* 199 */     return (x >= 0) ? 1 : -1;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int indicator(int x) {
/* 210 */     return (x >= 0) ? 1 : -1;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static long indicator(long x) {
/* 221 */     return (x >= 0L) ? 1L : -1L;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static long binomialCoefficient(int n, int k) {
/* 249 */     if (n < k) {
/* 250 */       throw new IllegalArgumentException("must have n >= k for binomial coefficient (n,k)");
/*     */     }
/*     */     
/* 253 */     if (n < 0) {
/* 254 */       throw new IllegalArgumentException("must have n >= 0 for binomial coefficient (n,k)");
/*     */     }
/*     */     
/* 257 */     if (n == k || k == 0) {
/* 258 */       return 1L;
/*     */     }
/* 260 */     if (k == 1 || k == n - 1) {
/* 261 */       return n;
/*     */     }
/*     */     
/* 264 */     long result = Math.round(binomialCoefficientDouble(n, k));
/* 265 */     if (result == Long.MAX_VALUE) {
/* 266 */       throw new ArithmeticException("result too large to represent in a long integer");
/*     */     }
/*     */     
/* 269 */     return result;
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
/*     */ 
/*     */ 
/*     */   
/*     */   public static double binomialCoefficientDouble(int n, int k) {
/* 294 */     return Math.floor(Math.exp(binomialCoefficientLog(n, k)) + 0.5D);
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
/*     */   public static double binomialCoefficientLog(int n, int k) {
/* 315 */     if (n < k) {
/* 316 */       throw new IllegalArgumentException("must have n >= k for binomial coefficient (n,k)");
/*     */     }
/*     */     
/* 319 */     if (n < 0) {
/* 320 */       throw new IllegalArgumentException("must have n >= 0 for binomial coefficient (n,k)");
/*     */     }
/*     */     
/* 323 */     if (n == k || k == 0) {
/* 324 */       return 0.0D;
/*     */     }
/* 326 */     if (k == 1 || k == n - 1) {
/* 327 */       return Math.log(n);
/*     */     }
/* 329 */     double logSum = 0.0D;
/*     */     
/*     */     int i;
/* 332 */     for (i = k + 1; i <= n; i++) {
/* 333 */       logSum += Math.log(i);
/*     */     }
/*     */ 
/*     */     
/* 337 */     for (i = 2; i <= n - k; i++) {
/* 338 */       logSum -= Math.log(i);
/*     */     }
/*     */     
/* 341 */     return logSum;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static long factorial(int n) {
/* 368 */     long result = Math.round(factorialDouble(n));
/* 369 */     if (result == Long.MAX_VALUE) {
/* 370 */       throw new ArithmeticException("result too large to represent in a long integer");
/*     */     }
/*     */     
/* 373 */     return result;
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
/*     */ 
/*     */ 
/*     */   
/*     */   public static double factorialDouble(int n) {
/* 398 */     if (n < 0) {
/* 399 */       throw new IllegalArgumentException("must have n >= 0 for n!");
/*     */     }
/* 401 */     return Math.floor(Math.exp(factorialLog(n)) + 0.5D);
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
/*     */   public static double factorialLog(int n) {
/* 417 */     if (n < 0) {
/* 418 */       throw new IllegalArgumentException("must have n > 0 for n!");
/*     */     }
/* 420 */     double logSum = 0.0D;
/* 421 */     for (int i = 2; i <= n; i++) {
/* 422 */       logSum += Math.log(i);
/*     */     }
/* 424 */     return logSum;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static double cosh(double x) {
/* 435 */     return (Math.exp(x) + Math.exp(-x)) / 2.0D;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static double sinh(double x) {
/* 446 */     return (Math.exp(x) - Math.exp(-x)) / 2.0D;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int hash(double value) {
/* 456 */     long bits = Double.doubleToLongBits(value);
/* 457 */     return (int)(bits ^ bits >>> 32L);
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
/*     */   public static boolean equals(double x, double y) {
/* 469 */     return ((Double.isNaN(x) && Double.isNaN(y)) || x == y);
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\commons-math-1.0.jar!\org\apache\commons\mat\\util\MathUtils.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */