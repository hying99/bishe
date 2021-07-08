/*    */ package org.jgap.eval;
/*    */ 
/*    */ import java.util.List;
/*    */ import java.util.Vector;
/*    */ import org.jgap.Population;
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
/*    */ public class PopulationHistory
/*    */ {
/*    */   private static final String CVS_REVISION = "$Revision: 1.4 $";
/* 38 */   private List m_populations = new Vector(); public PopulationHistory(int a_maxSize) {
/* 39 */     if (a_maxSize < 0) {
/* 40 */       throw new IllegalArgumentException("Maximum size must be greater or equal to zero!");
/*    */     }
/*    */     
/* 43 */     this.m_maxSize = a_maxSize;
/*    */   }
/*    */   private int m_maxSize;
/*    */   public Population getPopulation(int a_count) {
/* 47 */     if (a_count >= this.m_populations.size()) {
/* 48 */       return null;
/*    */     }
/*    */     
/* 51 */     return this.m_populations.get(a_count);
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
/*    */   public void addPopulation(Population a_population) {
/* 64 */     this.m_populations.add(0, a_population);
/* 65 */     int popSize = this.m_populations.size();
/* 66 */     if (this.m_maxSize != 0 && popSize > this.m_maxSize) {
/* 67 */       this.m_populations.remove(popSize - 1);
/*    */     }
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void removeAllPopulations() {
/* 76 */     this.m_populations.removeAll(this.m_populations);
/*    */   }
/*    */   
/*    */   public int size() {
/* 80 */     return this.m_populations.size();
/*    */   }
/*    */   
/*    */   public List getPopulations() {
/* 84 */     return this.m_populations;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void setPopulations(List a_populations) {
/* 95 */     this.m_populations = a_populations;
/* 96 */     int popSize = this.m_populations.size();
/* 97 */     if (this.m_maxSize != 0 && popSize > this.m_maxSize)
/* 98 */       for (int i = this.m_maxSize; i < popSize; i++)
/* 99 */         this.m_populations.remove(this.m_maxSize);  
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\eval\PopulationHistory.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */