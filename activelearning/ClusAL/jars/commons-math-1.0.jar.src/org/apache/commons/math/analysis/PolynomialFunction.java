/*     */ package org.apache.commons.math.analysis;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PolynomialFunction
/*     */   implements DifferentiableUnivariateRealFunction, Serializable
/*     */ {
/*     */   static final long serialVersionUID = 3322454535052136809L;
/*     */   private double[] coefficients;
/*     */   
/*     */   public PolynomialFunction(double[] c) {
/*  55 */     if (c.length < 1) {
/*  56 */       throw new IllegalArgumentException("Polynomial coefficient array must have postive length.");
/*     */     }
/*  58 */     this.coefficients = new double[c.length];
/*  59 */     System.arraycopy(c, 0, this.coefficients, 0, c.length);
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
/*     */   public double value(double x) {
/*  73 */     return evaluate(this.coefficients, x);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int degree() {
/*  83 */     return this.coefficients.length - 1;
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
/*     */   public double[] getCoefficients() {
/*  95 */     double[] out = new double[this.coefficients.length];
/*  96 */     System.arraycopy(this.coefficients, 0, out, 0, this.coefficients.length);
/*  97 */     return out;
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
/*     */   protected static double evaluate(double[] coefficients, double argument) {
/* 111 */     int n = coefficients.length;
/* 112 */     if (n < 1) {
/* 113 */       throw new IllegalArgumentException("Coefficient array must have positive length for evaluation");
/*     */     }
/* 115 */     double result = coefficients[n - 1];
/* 116 */     for (int j = n - 2; j >= 0; j--) {
/* 117 */       result = argument * result + coefficients[j];
/*     */     }
/* 119 */     return result;
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
/*     */   protected static double[] differentiate(double[] coefficients) {
/* 131 */     int n = coefficients.length;
/* 132 */     if (n < 1) {
/* 133 */       throw new IllegalArgumentException("Coefficient array must have positive length for differentiation");
/*     */     }
/* 135 */     if (n == 1) {
/* 136 */       return new double[] { 0.0D };
/*     */     }
/* 138 */     double[] result = new double[n - 1];
/* 139 */     for (int i = n - 1; i > 0; i--) {
/* 140 */       result[i - 1] = i * coefficients[i];
/*     */     }
/* 142 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PolynomialFunction polynomialDerivative() {
/* 151 */     return new PolynomialFunction(differentiate(this.coefficients));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public UnivariateRealFunction derivative() {
/* 160 */     return polynomialDerivative();
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\commons-math-1.0.jar!\org\apache\commons\math\analysis\PolynomialFunction.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */