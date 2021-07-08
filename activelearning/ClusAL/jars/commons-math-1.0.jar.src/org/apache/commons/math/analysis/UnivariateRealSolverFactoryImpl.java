/*    */ package org.apache.commons.math.analysis;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class UnivariateRealSolverFactoryImpl
/*    */   extends UnivariateRealSolverFactory
/*    */ {
/*    */   public UnivariateRealSolver newDefaultSolver(UnivariateRealFunction f) {
/* 44 */     return newBrentSolver(f);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public UnivariateRealSolver newBisectionSolver(UnivariateRealFunction f) {
/* 54 */     return new BisectionSolver(f);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public UnivariateRealSolver newBrentSolver(UnivariateRealFunction f) {
/* 64 */     return new BrentSolver(f);
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
/*    */   public UnivariateRealSolver newNewtonSolver(DifferentiableUnivariateRealFunction f) {
/* 76 */     return new NewtonSolver(f);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public UnivariateRealSolver newSecantSolver(UnivariateRealFunction f) {
/* 86 */     return new SecantSolver(f);
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\commons-math-1.0.jar!\org\apache\commons\math\analysis\UnivariateRealSolverFactoryImpl.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */