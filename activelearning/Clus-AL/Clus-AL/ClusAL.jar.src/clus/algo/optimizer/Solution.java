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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class Solution
/*    */ {
/*    */   private int m_SolutionSize;
/*    */   private Item[] m_Items;
/*    */   
/*    */   public abstract Solution getNewSolution();
/*    */   
/*    */   public abstract double[] getSolutionFitness();
/*    */   
/*    */   public int getSolutionSize() {
/* 30 */     return this.m_SolutionSize;
/*    */   }
/*    */   public int getAmountActive() {
/* 33 */     int activeAmount = 0;
/* 34 */     for (int i = 0; i < getSolutionSize(); i++) {
/* 35 */       if (this.m_Items[i].isActive().booleanValue()) {
/* 36 */         activeAmount++;
/*    */       }
/*    */     } 
/* 39 */     return activeAmount;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void setSolutionSize(int m_SolutionSize) {
/* 45 */     this.m_SolutionSize = m_SolutionSize;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Item[] getItems() {
/* 52 */     return this.m_Items;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void setItems(Item[] m_Items) {
/* 59 */     this.m_Items = m_Items;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 64 */     String output = "";
/* 65 */     for (int i = 0; i < getSolutionSize(); i++) {
/* 66 */       output = output + getItems()[i].isActive() + "\t";
/*    */     }
/* 68 */     output = output + "\n";
/*    */     
/* 70 */     return output;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\algo\optimizer\Solution.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */