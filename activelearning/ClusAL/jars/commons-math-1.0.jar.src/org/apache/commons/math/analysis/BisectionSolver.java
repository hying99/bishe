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
/*     */ 
/*     */ public class BisectionSolver
/*     */   extends UnivariateRealSolverImpl
/*     */ {
/*     */   static final long serialVersionUID = 7137520585963699578L;
/*     */   
/*     */   public BisectionSolver(UnivariateRealFunction f) {
/*  40 */     super(f, 100, 1.0E-6D);
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
/*     */   public double solve(double min, double max, double initial) throws ConvergenceException, FunctionEvaluationException {
/*  58 */     return solve(min, max);
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
/*     */   public double solve(double min, double max) throws ConvergenceException, FunctionEvaluationException {
/*  75 */     clearResult();
/*  76 */     verifyInterval(min, max);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  81 */     int i = 0;
/*  82 */     while (i < this.maximalIterationCount) {
/*  83 */       double m = UnivariateRealSolverUtils.midpoint(min, max);
/*  84 */       double fmin = this.f.value(min);
/*  85 */       double fm = this.f.value(m);
/*     */       
/*  87 */       if (fm * fmin > 0.0D) {
/*     */         
/*  89 */         min = m;
/*  90 */         fmin = fm;
/*     */       } else {
/*     */         
/*  93 */         max = m;
/*     */       } 
/*     */       
/*  96 */       if (Math.abs(max - min) <= this.absoluteAccuracy) {
/*  97 */         m = UnivariateRealSolverUtils.midpoint(min, max);
/*  98 */         setResult(m, i);
/*  99 */         return m;
/*     */       } 
/* 101 */       i++;
/*     */     } 
/*     */     
/* 104 */     throw new ConvergenceException("Maximum number of iterations exceeded: " + this.maximalIterationCount);
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\commons-math-1.0.jar!\org\apache\commons\math\analysis\BisectionSolver.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */