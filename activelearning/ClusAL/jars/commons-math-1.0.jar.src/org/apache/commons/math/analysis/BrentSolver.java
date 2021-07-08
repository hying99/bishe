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
/*     */ 
/*     */ public class BrentSolver
/*     */   extends UnivariateRealSolverImpl
/*     */ {
/*     */   static final long serialVersionUID = 3350616277306882875L;
/*     */   
/*     */   public BrentSolver(UnivariateRealFunction f) {
/*  41 */     super(f, 100, 1.0E-6D);
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
/*     */   public double solve(double min, double max, double initial) throws ConvergenceException, FunctionEvaluationException {
/*  62 */     return solve(min, max);
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
/*     */   public double solve(double min, double max) throws ConvergenceException, FunctionEvaluationException {
/*  84 */     clearResult();
/*  85 */     verifyBracketing(min, max, this.f);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  90 */     double x0 = min;
/*  91 */     double x1 = max;
/*     */ 
/*     */     
/*  94 */     double y0 = this.f.value(x0);
/*  95 */     double y1 = this.f.value(x1);
/*     */     
/*  97 */     double x2 = x0;
/*  98 */     double y2 = y0;
/*  99 */     double delta = x1 - x0;
/* 100 */     double oldDelta = delta;
/*     */     
/* 102 */     int i = 0;
/* 103 */     while (i < this.maximalIterationCount) {
/* 104 */       if (Math.abs(y2) < Math.abs(y1)) {
/* 105 */         x0 = x1;
/* 106 */         x1 = x2;
/* 107 */         x2 = x0;
/* 108 */         y0 = y1;
/* 109 */         y1 = y2;
/* 110 */         y2 = y0;
/*     */       } 
/* 112 */       if (Math.abs(y1) <= this.functionValueAccuracy) {
/*     */ 
/*     */ 
/*     */         
/* 116 */         setResult(x1, i);
/* 117 */         return this.result;
/*     */       } 
/* 119 */       double dx = x2 - x1;
/* 120 */       double tolerance = Math.max(this.relativeAccuracy * Math.abs(x1), this.absoluteAccuracy);
/*     */       
/* 122 */       if (Math.abs(dx) <= tolerance) {
/* 123 */         setResult(x1, i);
/* 124 */         return this.result;
/*     */       } 
/* 126 */       if (Math.abs(oldDelta) < tolerance || Math.abs(y0) <= Math.abs(y1)) {
/*     */ 
/*     */         
/* 129 */         delta = 0.5D * dx;
/* 130 */         oldDelta = delta;
/*     */       } else {
/* 132 */         double p, p1, r3 = y1 / y0;
/*     */ 
/*     */         
/* 135 */         if (x0 == x2) {
/*     */           
/* 137 */           p = dx * r3;
/* 138 */           p1 = 1.0D - r3;
/*     */         } else {
/*     */           
/* 141 */           double r1 = y0 / y2;
/* 142 */           double r2 = y1 / y2;
/* 143 */           p = r3 * (dx * r1 * (r1 - r2) - (x1 - x0) * (r2 - 1.0D));
/* 144 */           p1 = (r1 - 1.0D) * (r2 - 1.0D) * (r3 - 1.0D);
/*     */         } 
/* 146 */         if (p > 0.0D) {
/* 147 */           p1 = -p1;
/*     */         } else {
/* 149 */           p = -p;
/*     */         } 
/* 151 */         if (2.0D * p >= 1.5D * dx * p1 - Math.abs(tolerance * p1) || p >= Math.abs(0.5D * oldDelta * p1)) {
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 156 */           delta = 0.5D * dx;
/* 157 */           oldDelta = delta;
/*     */         } else {
/* 159 */           oldDelta = delta;
/* 160 */           delta = p / p1;
/*     */         } 
/*     */       } 
/*     */       
/* 164 */       x0 = x1;
/* 165 */       y0 = y1;
/*     */       
/* 167 */       if (Math.abs(delta) > tolerance) {
/* 168 */         x1 += delta;
/* 169 */       } else if (dx > 0.0D) {
/* 170 */         x1 += 0.5D * tolerance;
/* 171 */       } else if (dx <= 0.0D) {
/* 172 */         x1 -= 0.5D * tolerance;
/*     */       } 
/* 174 */       y1 = this.f.value(x1);
/* 175 */       if (((y1 > 0.0D) ? true : false) == ((y2 > 0.0D) ? true : false)) {
/* 176 */         x2 = x0;
/* 177 */         y2 = y0;
/* 178 */         delta = x1 - x0;
/* 179 */         oldDelta = delta;
/*     */       } 
/* 181 */       i++;
/*     */     } 
/* 183 */     throw new ConvergenceException("Maximum number of iterations exceeded.");
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\commons-math-1.0.jar!\org\apache\commons\math\analysis\BrentSolver.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */