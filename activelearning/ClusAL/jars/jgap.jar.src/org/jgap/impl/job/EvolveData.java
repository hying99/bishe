/*    */ package org.jgap.impl.job;
/*    */ 
/*    */ import org.jgap.Configuration;
/*    */ import org.jgap.IBreeder;
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
/*    */ public class EvolveData
/*    */   extends JobData
/*    */ {
/*    */   private static final String CVS_REVISION = "$Revision: 1.5 $";
/*    */   private Population m_pop;
/*    */   private IBreeder m_breeder;
/*    */   
/*    */   public EvolveData(Configuration a_config) {
/* 30 */     super(a_config);
/*    */   }
/*    */   
/*    */   public Population getPopulation() {
/* 34 */     return this.m_pop;
/*    */   }
/*    */   
/*    */   public void setPopulation(Population a_pop) {
/* 38 */     this.m_pop = a_pop;
/*    */   }
/*    */   
/*    */   public void setBreeder(IBreeder a_breeder) {
/* 42 */     this.m_breeder = a_breeder;
/*    */   }
/*    */   
/*    */   public IBreeder getBreeder() {
/* 46 */     return this.m_breeder;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\impl\job\EvolveData.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */