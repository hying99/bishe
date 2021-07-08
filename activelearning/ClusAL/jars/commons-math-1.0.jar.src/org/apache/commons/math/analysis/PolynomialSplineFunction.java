/*     */ package org.apache.commons.math.analysis;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.util.Arrays;
/*     */ import org.apache.commons.math.FunctionEvaluationException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PolynomialSplineFunction
/*     */   implements UnivariateRealFunction, Serializable
/*     */ {
/*     */   static final long serialVersionUID = 7011031166416885789L;
/*     */   private double[] knots;
/*  70 */   private PolynomialFunction[] polynomials = null;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  76 */   private int n = 0;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PolynomialSplineFunction(double[] knots, PolynomialFunction[] polynomials) {
/*  95 */     if (knots.length < 2) {
/*  96 */       throw new IllegalArgumentException("Not enough knot values -- spline partition must have at least 2 points.");
/*     */     }
/*     */     
/*  99 */     if (knots.length - 1 != polynomials.length) {
/* 100 */       throw new IllegalArgumentException("Number of polynomial interpolants must match the number of segments.");
/*     */     }
/*     */     
/* 103 */     if (!isStrictlyIncreasing(knots)) {
/* 104 */       throw new IllegalArgumentException("Knot values must be strictly increasing.");
/*     */     }
/*     */ 
/*     */     
/* 108 */     this.n = knots.length - 1;
/* 109 */     this.knots = new double[this.n + 1];
/* 110 */     System.arraycopy(knots, 0, this.knots, 0, this.n + 1);
/* 111 */     this.polynomials = new PolynomialFunction[this.n];
/* 112 */     System.arraycopy(polynomials, 0, this.polynomials, 0, this.n);
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
/*     */   public double value(double v) throws FunctionEvaluationException {
/* 131 */     if (v < this.knots[0] || v >= this.knots[this.n]) {
/* 132 */       throw new FunctionEvaluationException(v, "Argument outside domain");
/*     */     }
/* 134 */     int i = Arrays.binarySearch(this.knots, v);
/* 135 */     if (i < 0) {
/* 136 */       i = -i - 2;
/*     */     }
/* 138 */     return this.polynomials[i].value(v - this.knots[i]);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public UnivariateRealFunction derivative() {
/* 146 */     return polynomialSplineDerivative();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PolynomialSplineFunction polynomialSplineDerivative() {
/* 155 */     PolynomialFunction[] derivativePolynomials = new PolynomialFunction[this.n];
/* 156 */     for (int i = 0; i < this.n; i++) {
/* 157 */       derivativePolynomials[i] = this.polynomials[i].polynomialDerivative();
/*     */     }
/* 159 */     return new PolynomialSplineFunction(this.knots, derivativePolynomials);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getN() {
/* 169 */     return this.n;
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
/*     */   public PolynomialFunction[] getPolynomials() {
/* 181 */     PolynomialFunction[] p = new PolynomialFunction[this.n];
/* 182 */     System.arraycopy(this.polynomials, 0, p, 0, this.n);
/* 183 */     return p;
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
/*     */   public double[] getKnots() {
/* 195 */     double[] out = new double[this.n + 1];
/* 196 */     System.arraycopy(this.knots, 0, out, 0, this.n + 1);
/* 197 */     return out;
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
/*     */   private static boolean isStrictlyIncreasing(double[] x) {
/* 209 */     for (int i = 1; i < x.length; i++) {
/* 210 */       if (x[i - 1] >= x[i]) {
/* 211 */         return false;
/*     */       }
/*     */     } 
/* 214 */     return true;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\commons-math-1.0.jar!\org\apache\commons\math\analysis\PolynomialSplineFunction.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */