/*     */ package org.apache.commons.math.analysis;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SplineInterpolator
/*     */   implements UnivariateRealInterpolator
/*     */ {
/*     */   public UnivariateRealFunction interpolate(double[] x, double[] y) {
/*  52 */     if (x.length != y.length) {
/*  53 */       throw new IllegalArgumentException("Dataset arrays must have same length.");
/*     */     }
/*     */     
/*  56 */     if (x.length < 3) {
/*  57 */       throw new IllegalArgumentException("At least 3 datapoints are required to compute a spline interpolant");
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*  62 */     int n = x.length - 1;
/*     */     
/*  64 */     for (int i = 0; i < n; i++) {
/*  65 */       if (x[i] >= x[i + 1]) {
/*  66 */         throw new IllegalArgumentException("Dataset x values must be strictly increasing.");
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/*  71 */     double[] h = new double[n];
/*  72 */     for (int k = 0; k < n; k++) {
/*  73 */       h[k] = x[k + 1] - x[k];
/*     */     }
/*     */     
/*  76 */     double[] mu = new double[n];
/*  77 */     double[] z = new double[n + 1];
/*  78 */     mu[0] = 0.0D;
/*  79 */     z[0] = 0.0D;
/*  80 */     double g = 0.0D;
/*  81 */     for (int m = 1; m < n; m++) {
/*  82 */       g = 2.0D * (x[m + 1] - x[m - 1]) - h[m - 1] * mu[m - 1];
/*  83 */       mu[m] = h[m] / g;
/*  84 */       z[m] = (3.0D * (y[m + 1] * h[m - 1] - y[m] * (x[m + 1] - x[m - 1]) + y[m - 1] * h[m]) / h[m - 1] * h[m] - h[m - 1] * z[m - 1]) / g;
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/*  89 */     double[] b = new double[n];
/*  90 */     double[] c = new double[n + 1];
/*  91 */     double[] d = new double[n];
/*     */     
/*  93 */     z[n] = 0.0D;
/*  94 */     c[n] = 0.0D;
/*     */     
/*  96 */     for (int j = n - 1; j >= 0; j--) {
/*  97 */       c[j] = z[j] - mu[j] * c[j + 1];
/*  98 */       b[j] = (y[j + 1] - y[j]) / h[j] - h[j] * (c[j + 1] + 2.0D * c[j]) / 3.0D;
/*  99 */       d[j] = (c[j + 1] - c[j]) / 3.0D * h[j];
/*     */     } 
/*     */     
/* 102 */     PolynomialFunction[] polynomials = new PolynomialFunction[n];
/* 103 */     double[] coefficients = new double[4];
/* 104 */     for (int i1 = 0; i1 < n; i1++) {
/* 105 */       coefficients[0] = y[i1];
/* 106 */       coefficients[1] = b[i1];
/* 107 */       coefficients[2] = c[i1];
/* 108 */       coefficients[3] = d[i1];
/* 109 */       polynomials[i1] = new PolynomialFunction(coefficients);
/*     */     } 
/*     */     
/* 112 */     return new PolynomialSplineFunction(x, polynomials);
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\commons-math-1.0.jar!\org\apache\commons\math\analysis\SplineInterpolator.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */