/*     */ package org.apache.commons.math.distribution;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import org.apache.commons.math.ConvergenceException;
/*     */ import org.apache.commons.math.FunctionEvaluationException;
/*     */ import org.apache.commons.math.MathException;
/*     */ import org.apache.commons.math.analysis.UnivariateRealFunction;
/*     */ import org.apache.commons.math.analysis.UnivariateRealSolverUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class AbstractContinuousDistribution
/*     */   extends AbstractDistribution
/*     */   implements ContinuousDistribution, Serializable
/*     */ {
/*     */   static final long serialVersionUID = -38038050983108802L;
/*     */   
/*     */   public double inverseCumulativeProbability(double p) throws MathException {
/*  60 */     if (p < 0.0D || p > 1.0D) {
/*  61 */       throw new IllegalArgumentException("p must be between 0.0 and 1.0, inclusive.");
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*  66 */     UnivariateRealFunction rootFindingFunction = new UnivariateRealFunction(this, p) {
/*     */         private final double val$p;
/*     */         
/*     */         public double value(double x) throws FunctionEvaluationException {
/*     */           try {
/*  71 */             return this.this$0.cumulativeProbability(x) - this.val$p;
/*  72 */           } catch (MathException ex) {
/*  73 */             throw new FunctionEvaluationException(x, "Error computing cdf", ex);
/*     */           } 
/*     */         }
/*     */ 
/*     */         
/*     */         private final AbstractContinuousDistribution this$0;
/*     */       };
/*  80 */     double lowerBound = getDomainLowerBound(p);
/*  81 */     double upperBound = getDomainUpperBound(p);
/*  82 */     double[] bracket = null;
/*     */     try {
/*  84 */       bracket = UnivariateRealSolverUtils.bracket(rootFindingFunction, getInitialDomain(p), lowerBound, upperBound);
/*     */     
/*     */     }
/*  87 */     catch (ConvergenceException ex) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*  96 */       if (Math.abs(rootFindingFunction.value(lowerBound)) < 1.0E-6D) {
/*  97 */         return lowerBound;
/*     */       }
/*  99 */       if (Math.abs(rootFindingFunction.value(upperBound)) < 1.0E-6D) {
/* 100 */         return upperBound;
/*     */       }
/*     */       
/* 103 */       throw new MathException(ex);
/*     */     } 
/*     */ 
/*     */     
/* 107 */     double root = UnivariateRealSolverUtils.solve(rootFindingFunction, bracket[0], bracket[1]);
/*     */     
/* 109 */     return root;
/*     */   }
/*     */   
/*     */   protected abstract double getInitialDomain(double paramDouble);
/*     */   
/*     */   protected abstract double getDomainLowerBound(double paramDouble);
/*     */   
/*     */   protected abstract double getDomainUpperBound(double paramDouble);
/*     */   
/*     */   public abstract double cumulativeProbability(double paramDouble) throws MathException;
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\commons-math-1.0.jar!\org\apache\commons\math\distribution\AbstractContinuousDistribution.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */