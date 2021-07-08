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
/*     */ public class SecantSolver
/*     */   extends UnivariateRealSolverImpl
/*     */   implements Serializable
/*     */ {
/*     */   static final long serialVersionUID = 1984971194738974867L;
/*     */   
/*     */   public SecantSolver(UnivariateRealFunction f) {
/*  50 */     super(f, 100, 1.0E-6D);
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
/*     */   public double solve(double min, double max, double initial) throws ConvergenceException, FunctionEvaluationException {
/*  69 */     return solve(min, max);
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
/*  86 */     clearResult();
/*  87 */     verifyBracketing(min, max, this.f);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  94 */     double x0 = min;
/*  95 */     double x1 = max;
/*  96 */     double y0 = this.f.value(x0);
/*  97 */     double y1 = this.f.value(x1);
/*  98 */     double x2 = x0;
/*  99 */     double y2 = y0;
/* 100 */     double oldDelta = x2 - x1;
/* 101 */     int i = 0;
/* 102 */     while (i < this.maximalIterationCount) {
/* 103 */       double delta; if (Math.abs(y2) < Math.abs(y1)) {
/* 104 */         x0 = x1;
/* 105 */         x1 = x2;
/* 106 */         x2 = x0;
/* 107 */         y0 = y1;
/* 108 */         y1 = y2;
/* 109 */         y2 = y0;
/*     */       } 
/* 111 */       if (Math.abs(y1) <= this.functionValueAccuracy) {
/* 112 */         setResult(x1, i);
/* 113 */         return this.result;
/*     */       } 
/* 115 */       if (Math.abs(oldDelta) < Math.max(this.relativeAccuracy * Math.abs(x1), this.absoluteAccuracy)) {
/*     */         
/* 117 */         setResult(x1, i);
/* 118 */         return this.result;
/*     */       } 
/*     */       
/* 121 */       if (Math.abs(y1) > Math.abs(y0)) {
/*     */         
/* 123 */         delta = 0.5D * oldDelta;
/*     */       } else {
/* 125 */         delta = (x0 - x1) / (1.0D - y0 / y1);
/* 126 */         if (delta / oldDelta > 1.0D)
/*     */         {
/*     */           
/* 129 */           delta = 0.5D * oldDelta;
/*     */         }
/*     */       } 
/* 132 */       x0 = x1;
/* 133 */       y0 = y1;
/* 134 */       x1 += delta;
/* 135 */       y1 = this.f.value(x1);
/* 136 */       if (((y1 > 0.0D) ? true : false) == ((y2 > 0.0D) ? true : false)) {
/*     */         
/* 138 */         x2 = x0;
/* 139 */         y2 = y0;
/*     */       } 
/* 141 */       oldDelta = x2 - x1;
/* 142 */       i++;
/*     */     } 
/* 144 */     throw new ConvergenceException("Maximal iteration number exceeded" + i);
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\commons-math-1.0.jar!\org\apache\commons\math\analysis\SecantSolver.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */