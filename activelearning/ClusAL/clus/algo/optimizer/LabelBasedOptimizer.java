/*    */ package clus.algo.optimizer;
/*    */ 
/*    */ import clus.activelearning.algo.ClusActiveLearningAlgorithm;
/*    */ import clus.activelearning.indexing.LabelIndex;
/*    */ import java.util.LinkedList;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class LabelBasedOptimizer
/*    */   extends Optimizer
/*    */ {
/*    */   private double m_Budget;
/*    */   private int m_Iterations;
/*    */   
/*    */   public LabelBasedOptimizer(ClusActiveLearningAlgorithm al, double budget, int iterations) {
/* 22 */     super(al);
/* 23 */     this.m_Budget = budget;
/* 24 */     this.m_Iterations = iterations;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public abstract LinkedList<LabelIndex> optimize();
/*    */ 
/*    */   
/*    */   public double getBudget() {
/* 33 */     return this.m_Budget;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void setBudget(double m_Budget) {
/* 40 */     this.m_Budget = m_Budget;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public int getIterations() {
/* 47 */     return this.m_Iterations;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void setIterations(int m_Iterations) {
/* 54 */     this.m_Iterations = m_Iterations;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\algo\optimizer\LabelBasedOptimizer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */