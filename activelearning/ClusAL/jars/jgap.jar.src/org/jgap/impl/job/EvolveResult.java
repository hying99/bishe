/*    */ package org.jgap.impl.job;
/*    */ 
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
/*    */ public class EvolveResult
/*    */   extends JobResult
/*    */ {
/*    */   private static final String CVS_REVISION = "$Revision: 1.3 $";
/*    */   private Population m_pop;
/*    */   
/*    */   public Population getPopulation() {
/* 31 */     return this.m_pop;
/*    */   }
/*    */   
/*    */   public void setPopulation(Population a_pop) {
/* 35 */     this.m_pop = a_pop;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\impl\job\EvolveResult.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */