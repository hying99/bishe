/*     */ package org.apache.commons.math.special;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import org.apache.commons.math.MathException;
/*     */ import org.apache.commons.math.util.ContinuedFraction;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Beta
/*     */   implements Serializable
/*     */ {
/*     */   private static final double DEFAULT_EPSILON = 1.0E-8D;
/*     */   
/*     */   public static double regularizedBeta(double x, double a, double b) throws MathException {
/*  54 */     return regularizedBeta(x, a, b, 1.0E-8D, 2147483647);
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
/*     */   public static double regularizedBeta(double x, double a, double b, double epsilon) throws MathException {
/*  74 */     return regularizedBeta(x, a, b, epsilon, 2147483647);
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
/*     */   public static double regularizedBeta(double x, double a, double b, int maxIterations) throws MathException {
/*  90 */     return regularizedBeta(x, a, b, 1.0E-8D, maxIterations);
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
/*     */ 
/*     */   
/*     */   public static double regularizedBeta(double x, double a, double b, double epsilon, int maxIterations) throws MathException {
/*     */     double ret;
/* 121 */     if (Double.isNaN(x) || Double.isNaN(a) || Double.isNaN(b) || x < 0.0D || x > 1.0D || a <= 0.0D || b <= 0.0D) {
/*     */ 
/*     */       
/* 124 */       ret = Double.NaN;
/* 125 */     } else if (x > (a + 1.0D) / (a + b + 2.0D)) {
/* 126 */       ret = 1.0D - regularizedBeta(1.0D - x, b, a, epsilon, maxIterations);
/*     */     } else {
/* 128 */       ContinuedFraction fraction = new ContinuedFraction(b, a) { private final double val$b;
/*     */           
/*     */           protected double getB(int n, double x) {
/*     */             double ret;
/* 132 */             if (n % 2 == 0) {
/* 133 */               double m = n / 2.0D;
/* 134 */               ret = m * (this.val$b - m) * x / (this.val$a + 2.0D * m - 1.0D) * (this.val$a + 2.0D * m);
/*     */             } else {
/*     */               
/* 137 */               double m = (n - 1.0D) / 2.0D;
/* 138 */               ret = -((this.val$a + m) * (this.val$a + this.val$b + m) * x) / (this.val$a + 2.0D * m) * (this.val$a + 2.0D * m + 1.0D);
/*     */             } 
/*     */             
/* 141 */             return ret;
/*     */           }
/*     */           private final double val$a;
/*     */           protected double getA(int n, double x) {
/* 145 */             return 1.0D;
/*     */           } }
/*     */         ;
/* 148 */       ret = Math.exp(a * Math.log(x) + b * Math.log(1.0D - x) - Math.log(a) - logBeta(a, b, epsilon, maxIterations)) * 1.0D / fraction.evaluate(x, epsilon, maxIterations);
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 153 */     return ret;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static double logBeta(double a, double b) {
/* 164 */     return logBeta(a, b, 1.0E-8D, 2147483647);
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
/*     */   public static double logBeta(double a, double b, double epsilon, int maxIterations) {
/*     */     double ret;
/* 189 */     if (Double.isNaN(a) || Double.isNaN(b) || a <= 0.0D || b <= 0.0D) {
/* 190 */       ret = Double.NaN;
/*     */     } else {
/* 192 */       ret = Gamma.logGamma(a) + Gamma.logGamma(b) - Gamma.logGamma(a + b);
/*     */     } 
/*     */ 
/*     */     
/* 196 */     return ret;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\commons-math-1.0.jar!\org\apache\commons\math\special\Beta.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */