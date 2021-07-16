/*    */ package clus.algo.optimizer;
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
/*    */ public abstract class Population
/*    */ {
/*    */   private int m_PopulationSize;
/*    */   private Solution[] m_Solutions;
/*    */   
/*    */   public Population(int populationSize) {
/* 19 */     this.m_PopulationSize = populationSize;
/*    */   }
/*    */ 
/*    */   
/*    */   public abstract Population getNewPopulation();
/*    */ 
/*    */   
/*    */   public abstract void initializePopulation();
/*    */ 
/*    */   
/*    */   public int getPopulationSize() {
/* 30 */     return this.m_PopulationSize;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void setPopulationSize(int m_PopulationSize) {
/* 37 */     this.m_PopulationSize = m_PopulationSize;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Solution[] getSolutions() {
/* 44 */     return this.m_Solutions;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void setSolutions(Solution[] m_Solutions) {
/* 51 */     this.m_Solutions = m_Solutions;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\algo\optimizer\Population.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */