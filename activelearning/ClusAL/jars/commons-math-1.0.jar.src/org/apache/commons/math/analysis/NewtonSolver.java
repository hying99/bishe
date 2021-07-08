/*    */ package org.apache.commons.math.analysis;
/*    */ 
/*    */ import org.apache.commons.math.ConvergenceException;
/*    */ import org.apache.commons.math.FunctionEvaluationException;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class NewtonSolver
/*    */   extends UnivariateRealSolverImpl
/*    */ {
/*    */   static final long serialVersionUID = 2606474895443431607L;
/*    */   private UnivariateRealFunction derivative;
/*    */   
/*    */   public NewtonSolver(DifferentiableUnivariateRealFunction f) {
/* 43 */     super(f, 100, 1.0E-6D);
/* 44 */     this.derivative = f.derivative();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public double solve(double min, double max) throws ConvergenceException, FunctionEvaluationException {
/* 60 */     return solve(min, max, UnivariateRealSolverUtils.midpoint(min, max));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public double solve(double min, double max, double startValue) throws ConvergenceException, FunctionEvaluationException {
/* 78 */     clearResult();
/* 79 */     verifySequence(min, startValue, max);
/*    */     
/* 81 */     double x0 = startValue;
/*    */ 
/*    */     
/* 84 */     int i = 0;
/* 85 */     while (i < this.maximalIterationCount) {
/* 86 */       double x1 = x0 - this.f.value(x0) / this.derivative.value(x0);
/* 87 */       if (Math.abs(x1 - x0) <= this.absoluteAccuracy) {
/*    */         
/* 89 */         setResult(x1, i);
/* 90 */         return x1;
/*    */       } 
/*    */       
/* 93 */       x0 = x1;
/* 94 */       i++;
/*    */     } 
/*    */     
/* 97 */     throw new ConvergenceException("Maximum number of iterations exceeded " + i);
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\commons-math-1.0.jar!\org\apache\commons\math\analysis\NewtonSolver.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */