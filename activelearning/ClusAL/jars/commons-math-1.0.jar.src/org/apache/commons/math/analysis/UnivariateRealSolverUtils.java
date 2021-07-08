/*     */ package org.apache.commons.math.analysis;
/*     */ 
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
/*     */ public class UnivariateRealSolverUtils
/*     */ {
/*  35 */   private static UnivariateRealSolverFactory factory = null;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static double solve(UnivariateRealFunction f, double x0, double x1) throws ConvergenceException, FunctionEvaluationException {
/*  53 */     setup(f);
/*  54 */     return factory.newDefaultSolver(f).solve(x0, x1);
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
/*     */   public static double solve(UnivariateRealFunction f, double x0, double x1, double absoluteAccuracy) throws ConvergenceException, FunctionEvaluationException {
/*  77 */     setup(f);
/*  78 */     UnivariateRealSolver solver = factory.newDefaultSolver(f);
/*  79 */     solver.setAbsoluteAccuracy(absoluteAccuracy);
/*  80 */     return solver.solve(x0, x1);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static double[] bracket(UnivariateRealFunction function, double initial, double lowerBound, double upperBound) throws ConvergenceException, FunctionEvaluationException {
/* 127 */     return bracket(function, initial, lowerBound, upperBound, 2147483647);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static double[] bracket(UnivariateRealFunction function, double initial, double lowerBound, double upperBound, int maximumIterations) throws ConvergenceException, FunctionEvaluationException {
/*     */     double fa, fb;
/* 170 */     if (function == null) {
/* 171 */       throw new IllegalArgumentException("function is null.");
/*     */     }
/* 173 */     if (maximumIterations <= 0) {
/* 174 */       throw new IllegalArgumentException("bad value for maximumIterations: " + maximumIterations);
/*     */     }
/*     */     
/* 177 */     if (initial < lowerBound || initial > upperBound || lowerBound >= upperBound) {
/* 178 */       throw new IllegalArgumentException("Invalid endpoint parameters:  lowerBound=" + lowerBound + " initial=" + initial + " upperBound=" + upperBound);
/*     */     }
/*     */ 
/*     */     
/* 182 */     double a = initial;
/* 183 */     double b = initial;
/*     */ 
/*     */     
/* 186 */     int numIterations = 0;
/*     */     
/*     */     do {
/* 189 */       a = Math.max(a - 1.0D, lowerBound);
/* 190 */       b = Math.min(b + 1.0D, upperBound);
/* 191 */       fa = function.value(a);
/*     */       
/* 193 */       fb = function.value(b);
/* 194 */       numIterations++;
/* 195 */     } while (fa * fb > 0.0D && numIterations < maximumIterations && (a > lowerBound || b < upperBound));
/*     */ 
/*     */     
/* 198 */     if (fa * fb >= 0.0D) {
/* 199 */       throw new ConvergenceException("Number of iterations= " + numIterations + " maximum iterations= " + maximumIterations + " initial= " + initial + " lowerBound=" + lowerBound + " upperBound=" + upperBound + " final a value=" + a + " final b value=" + b + " f(a)=" + fa + " f(b)=" + fb);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 207 */     return new double[] { a, b };
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static double midpoint(double a, double b) {
/* 218 */     return (a + b) * 0.5D;
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
/*     */   private static void setup(UnivariateRealFunction f) {
/* 230 */     if (f == null) {
/* 231 */       throw new IllegalArgumentException("function can not be null.");
/*     */     }
/*     */     
/* 234 */     if (factory == null)
/* 235 */       factory = UnivariateRealSolverFactory.newInstance(); 
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\commons-math-1.0.jar!\org\apache\commons\math\analysis\UnivariateRealSolverUtils.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */