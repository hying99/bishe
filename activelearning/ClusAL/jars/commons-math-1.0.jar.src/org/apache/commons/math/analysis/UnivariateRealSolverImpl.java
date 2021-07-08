/*     */ package org.apache.commons.math.analysis;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import org.apache.commons.math.ConvergenceException;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class UnivariateRealSolverImpl
/*     */   implements UnivariateRealSolver, Serializable
/*     */ {
/*     */   static final long serialVersionUID = 1112491292565386596L;
/*     */   protected double absoluteAccuracy;
/*     */   protected double relativeAccuracy;
/*     */   protected double functionValueAccuracy;
/*     */   protected int maximalIterationCount;
/*     */   protected double defaultAbsoluteAccuracy;
/*     */   protected double defaultRelativeAccuracy;
/*     */   protected double defaultFunctionValueAccuracy;
/*     */   protected int defaultMaximalIterationCount;
/*     */   protected boolean resultComputed = false;
/*     */   protected double result;
/*     */   protected int iterationCount;
/*     */   protected UnivariateRealFunction f;
/*     */   
/*     */   protected UnivariateRealSolverImpl(UnivariateRealFunction f, int defaultMaximalIterationCount, double defaultAbsoluteAccuracy) {
/*  88 */     if (f == null) {
/*  89 */       throw new IllegalArgumentException("function can not be null.");
/*     */     }
/*     */     
/*  92 */     this.f = f;
/*  93 */     this.defaultAbsoluteAccuracy = defaultAbsoluteAccuracy;
/*  94 */     this.defaultRelativeAccuracy = 1.0E-14D;
/*  95 */     this.defaultFunctionValueAccuracy = 1.0E-15D;
/*  96 */     this.absoluteAccuracy = defaultAbsoluteAccuracy;
/*  97 */     this.relativeAccuracy = this.defaultRelativeAccuracy;
/*  98 */     this.functionValueAccuracy = this.defaultFunctionValueAccuracy;
/*  99 */     this.defaultMaximalIterationCount = defaultMaximalIterationCount;
/* 100 */     this.maximalIterationCount = defaultMaximalIterationCount;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getResult() {
/* 110 */     if (this.resultComputed) {
/* 111 */       return this.result;
/*     */     }
/* 113 */     throw new IllegalStateException("No result available");
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
/*     */   public int getIterationCount() {
/* 125 */     if (this.resultComputed) {
/* 126 */       return this.iterationCount;
/*     */     }
/* 128 */     throw new IllegalStateException("No result available");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final void setResult(double result, int iterationCount) {
/* 139 */     this.result = result;
/* 140 */     this.iterationCount = iterationCount;
/* 141 */     this.resultComputed = true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final void clearResult() {
/* 148 */     this.resultComputed = false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setAbsoluteAccuracy(double accuracy) {
/* 159 */     this.absoluteAccuracy = accuracy;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getAbsoluteAccuracy() {
/* 168 */     return this.absoluteAccuracy;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void resetAbsoluteAccuracy() {
/* 175 */     this.absoluteAccuracy = this.defaultAbsoluteAccuracy;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setMaximalIterationCount(int count) {
/* 184 */     this.maximalIterationCount = count;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getMaximalIterationCount() {
/* 193 */     return this.maximalIterationCount;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void resetMaximalIterationCount() {
/* 200 */     this.maximalIterationCount = this.defaultMaximalIterationCount;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setRelativeAccuracy(double accuracy) {
/* 211 */     this.relativeAccuracy = accuracy;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getRelativeAccuracy() {
/* 219 */     return this.relativeAccuracy;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void resetRelativeAccuracy() {
/* 226 */     this.relativeAccuracy = this.defaultRelativeAccuracy;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setFunctionValueAccuracy(double accuracy) {
/* 237 */     this.functionValueAccuracy = accuracy;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getFunctionValueAccuracy() {
/* 245 */     return this.functionValueAccuracy;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void resetFunctionValueAccuracy() {
/* 252 */     this.functionValueAccuracy = this.defaultFunctionValueAccuracy;
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
/*     */   protected boolean isBracketing(double lower, double upper, UnivariateRealFunction f) throws FunctionEvaluationException {
/* 268 */     return (f.value(lower) * f.value(upper) < 0.0D);
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
/*     */   protected boolean isSequence(double start, double mid, double end) {
/* 280 */     return (start < mid && mid < end);
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
/*     */   protected void verifyInterval(double lower, double upper) {
/* 292 */     if (lower >= upper) {
/* 293 */       throw new IllegalArgumentException("Endpoints do not specify an interval: [" + lower + "," + upper + "]");
/*     */     }
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
/*     */   protected void verifySequence(double lower, double initial, double upper) {
/* 309 */     if (!isSequence(lower, initial, upper)) {
/* 310 */       throw new IllegalArgumentException("Invalid interval, initial value parameters:  lower=" + lower + " initial=" + initial + " upper=" + upper);
/*     */     }
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
/*     */   protected void verifyBracketing(double lower, double upper, UnivariateRealFunction f) throws FunctionEvaluationException {
/* 330 */     verifyInterval(lower, upper);
/* 331 */     if (!isBracketing(lower, upper, f))
/* 332 */       throw new IllegalArgumentException("Function values at endpoints do not have different signs.  Endpoints: [" + lower + "," + upper + "]" + "  Values: [" + f.value(lower) + "," + f.value(upper) + "]"); 
/*     */   }
/*     */   
/*     */   public abstract double solve(double paramDouble1, double paramDouble2, double paramDouble3) throws ConvergenceException, FunctionEvaluationException;
/*     */   
/*     */   public abstract double solve(double paramDouble1, double paramDouble2) throws ConvergenceException, FunctionEvaluationException;
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\commons-math-1.0.jar!\org\apache\commons\math\analysis\UnivariateRealSolverImpl.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */