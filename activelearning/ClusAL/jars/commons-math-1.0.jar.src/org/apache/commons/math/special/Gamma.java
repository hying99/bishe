/*     */ package org.apache.commons.math.special;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import org.apache.commons.math.ConvergenceException;
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
/*     */ public class Gamma
/*     */   implements Serializable
/*     */ {
/*     */   private static final double DEFAULT_EPSILON = 1.0E-8D;
/*  36 */   private static double[] lanczos = new double[] { 0.9999999999999971D, 57.15623566586292D, -59.59796035547549D, 14.136097974741746D, -0.4919138160976202D, 3.399464998481189E-5D, 4.652362892704858E-5D, -9.837447530487956E-5D, 1.580887032249125E-4D, -2.1026444172410488E-4D, 2.1743961811521265E-4D, -1.643181065367639E-4D, 8.441822398385275E-5D, -2.6190838401581408E-5D, 3.6899182659531625E-6D };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  56 */   private static final double HALF_LOG_2_PI = 0.5D * Math.log(6.283185307179586D);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static double logGamma(double x) {
/*     */     double ret;
/*  86 */     if (Double.isNaN(x) || x <= 0.0D) {
/*  87 */       ret = Double.NaN;
/*     */     } else {
/*  89 */       double g = 4.7421875D;
/*     */       
/*  91 */       double sum = 0.0D;
/*  92 */       for (int i = lanczos.length - 1; i > 0; i--) {
/*  93 */         sum += lanczos[i] / (x + i);
/*     */       }
/*  95 */       sum += lanczos[0];
/*     */       
/*  97 */       double tmp = x + g + 0.5D;
/*  98 */       ret = (x + 0.5D) * Math.log(tmp) - tmp + HALF_LOG_2_PI + Math.log(sum / x);
/*     */     } 
/*     */ 
/*     */     
/* 102 */     return ret;
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
/*     */   public static double regularizedGammaP(double a, double x) throws MathException {
/* 116 */     return regularizedGammaP(a, x, 1.0E-8D, 2147483647);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static double regularizedGammaP(double a, double x, double epsilon, int maxIterations) throws MathException {
/*     */     double ret;
/* 154 */     if (Double.isNaN(a) || Double.isNaN(x) || a <= 0.0D || x < 0.0D) {
/* 155 */       ret = Double.NaN;
/* 156 */     } else if (x == 0.0D) {
/* 157 */       ret = 0.0D;
/* 158 */     } else if (a > 1.0D && x > a) {
/*     */ 
/*     */       
/* 161 */       ret = 1.0D - regularizedGammaQ(a, x, epsilon, maxIterations);
/*     */     } else {
/*     */       
/* 164 */       double n = 0.0D;
/* 165 */       double an = 1.0D / a;
/* 166 */       double sum = an;
/* 167 */       while (Math.abs(an) > epsilon && n < maxIterations) {
/*     */         
/* 169 */         n++;
/* 170 */         an *= x / (a + n);
/*     */ 
/*     */         
/* 173 */         sum += an;
/*     */       } 
/* 175 */       if (n >= maxIterations) {
/* 176 */         throw new ConvergenceException("maximum number of iterations reached");
/*     */       }
/*     */       
/* 179 */       ret = Math.exp(-x + a * Math.log(x) - logGamma(a)) * sum;
/*     */     } 
/*     */ 
/*     */     
/* 183 */     return ret;
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
/*     */   public static double regularizedGammaQ(double a, double x) throws MathException {
/* 197 */     return regularizedGammaQ(a, x, 1.0E-8D, 2147483647);
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
/*     */ 
/*     */   
/*     */   public static double regularizedGammaQ(double a, double x, double epsilon, int maxIterations) throws MathException {
/*     */     double ret;
/* 230 */     if (Double.isNaN(a) || Double.isNaN(x) || a <= 0.0D || x < 0.0D) {
/* 231 */       ret = Double.NaN;
/* 232 */     } else if (x == 0.0D) {
/* 233 */       ret = 1.0D;
/* 234 */     } else if (x < a || a <= 1.0D) {
/*     */ 
/*     */       
/* 237 */       ret = 1.0D - regularizedGammaP(a, x, epsilon, maxIterations);
/*     */     } else {
/*     */       
/* 240 */       ContinuedFraction cf = new ContinuedFraction(a) {
/*     */           protected double getA(int n, double x) {
/* 242 */             return 2.0D * n + 1.0D - this.val$a + x;
/*     */           }
/*     */           private final double val$a;
/*     */           protected double getB(int n, double x) {
/* 246 */             return n * (this.val$a - n);
/*     */           }
/*     */         };
/*     */       
/* 250 */       ret = 1.0D / cf.evaluate(x, epsilon, maxIterations);
/* 251 */       ret = Math.exp(-x + a * Math.log(x) - logGamma(a)) * ret;
/*     */     } 
/*     */     
/* 254 */     return ret;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\commons-math-1.0.jar!\org\apache\commons\math\special\Gamma.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */