/*    */ package org.apache.commons.math.analysis;
/*    */ 
/*    */ import org.apache.commons.discovery.tools.DiscoverClass;
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
/*    */ public abstract class UnivariateRealSolverFactory
/*    */ {
/*    */   public static UnivariateRealSolverFactory newInstance() {
/* 58 */     UnivariateRealSolverFactory factory = null;
/*    */     try {
/* 60 */       DiscoverClass dc = new DiscoverClass();
/* 61 */       factory = (UnivariateRealSolverFactory)dc.newInstance(UnivariateRealSolverFactory.class, "org.apache.commons.math.analysis.UnivariateRealSolverFactoryImpl");
/*    */     
/*    */     }
/* 64 */     catch (Throwable t) {
/* 65 */       return new UnivariateRealSolverFactoryImpl();
/*    */     } 
/* 67 */     return factory;
/*    */   }
/*    */   
/*    */   public abstract UnivariateRealSolver newDefaultSolver(UnivariateRealFunction paramUnivariateRealFunction);
/*    */   
/*    */   public abstract UnivariateRealSolver newBisectionSolver(UnivariateRealFunction paramUnivariateRealFunction);
/*    */   
/*    */   public abstract UnivariateRealSolver newBrentSolver(UnivariateRealFunction paramUnivariateRealFunction);
/*    */   
/*    */   public abstract UnivariateRealSolver newNewtonSolver(DifferentiableUnivariateRealFunction paramDifferentiableUnivariateRealFunction);
/*    */   
/*    */   public abstract UnivariateRealSolver newSecantSolver(UnivariateRealFunction paramUnivariateRealFunction);
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\commons-math-1.0.jar!\org\apache\commons\math\analysis\UnivariateRealSolverFactory.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */