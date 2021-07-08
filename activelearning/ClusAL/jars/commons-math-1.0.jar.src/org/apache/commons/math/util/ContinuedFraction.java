/*     */ package org.apache.commons.math.util;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import org.apache.commons.math.ConvergenceException;
/*     */ import org.apache.commons.math.MathException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class ContinuedFraction
/*     */   implements Serializable
/*     */ {
/*     */   static final long serialVersionUID = 1768555336266158242L;
/*     */   private static final double DEFAULT_EPSILON = 1.0E-8D;
/*     */   
/*     */   protected abstract double getA(int paramInt, double paramDouble);
/*     */   
/*     */   protected abstract double getB(int paramInt, double paramDouble);
/*     */   
/*     */   public double evaluate(double x) throws MathException {
/*  77 */     return evaluate(x, 1.0E-8D, 2147483647);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double evaluate(double x, double epsilon) throws MathException {
/*  88 */     return evaluate(x, epsilon, 2147483647);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double evaluate(double x, int maxIterations) throws MathException {
/*  99 */     return evaluate(x, 1.0E-8D, maxIterations);
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
/*     */   public double evaluate(double x, double epsilon, int maxIterations) throws MathException {
/* 122 */     double[][] f = new double[2][2];
/* 123 */     double[][] a = new double[2][2];
/* 124 */     double[][] an = new double[2][2];
/*     */     
/* 126 */     a[0][0] = getA(0, x);
/* 127 */     a[0][1] = 1.0D;
/* 128 */     a[1][0] = 1.0D;
/* 129 */     a[1][1] = 0.0D;
/*     */     
/* 131 */     return evaluate(1, x, a, an, f, epsilon, maxIterations);
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
/*     */   private double evaluate(int n, double x, double[][] a, double[][] an, double[][] f, double epsilon, int maxIterations) throws MathException {
/*     */     double ret;
/* 160 */     an[0][0] = getA(n, x);
/* 161 */     an[0][1] = 1.0D;
/* 162 */     an[1][0] = getB(n, x);
/* 163 */     an[1][1] = 0.0D;
/*     */ 
/*     */     
/* 166 */     f[0][0] = a[0][0] * an[0][0] + a[0][1] * an[1][0];
/* 167 */     f[0][1] = a[0][0] * an[0][1] + a[0][1] * an[1][1];
/* 168 */     f[1][0] = a[1][0] * an[0][0] + a[1][1] * an[1][0];
/* 169 */     f[1][1] = a[1][0] * an[0][1] + a[1][1] * an[1][1];
/*     */ 
/*     */     
/* 172 */     if (Math.abs(f[0][0] * f[1][1] - f[1][0] * f[0][1]) < Math.abs(epsilon * f[1][0] * f[1][1])) {
/*     */ 
/*     */       
/* 175 */       ret = f[0][0] / f[1][0];
/*     */     } else {
/* 177 */       if (n >= maxIterations) {
/* 178 */         throw new ConvergenceException("Continued fraction convergents failed to converge.");
/*     */       }
/*     */ 
/*     */       
/* 182 */       ret = evaluate(n + 1, x, f, an, a, epsilon, maxIterations);
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 188 */     return ret;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\commons-math-1.0.jar!\org\apache\commons\mat\\util\ContinuedFraction.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */