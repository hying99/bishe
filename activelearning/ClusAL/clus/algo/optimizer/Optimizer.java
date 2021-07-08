/*    */ package clus.algo.optimizer;
/*    */ 
/*    */ import clus.activelearning.algo.ClusActiveLearningAlgorithm;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class Optimizer
/*    */ {
/*    */   private ClusActiveLearningAlgorithm m_ActiveLearningAlgorithm;
/*    */   private Population m_Population;
/*    */   
/*    */   public Optimizer(ClusActiveLearningAlgorithm al) {
/* 19 */     this.m_ActiveLearningAlgorithm = al;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public ClusActiveLearningAlgorithm getActiveLearningAlgorithm() {
/* 26 */     return this.m_ActiveLearningAlgorithm;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void setActiveLearningAlgorithm(ClusActiveLearningAlgorithm m_ActiveLearningAlgorithm) {
/* 33 */     this.m_ActiveLearningAlgorithm = m_ActiveLearningAlgorithm;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Population getPopulation() {
/* 40 */     return this.m_Population;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void setPopulation(Population m_Population) {
/* 47 */     this.m_Population = m_Population;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\algo\optimizer\Optimizer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */