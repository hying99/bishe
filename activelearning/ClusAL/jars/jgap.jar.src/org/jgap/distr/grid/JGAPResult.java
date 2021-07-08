/*    */ package org.jgap.distr.grid;
/*    */ 
/*    */ import org.homedns.dade.jcgrid.WorkResult;
/*    */ import org.jgap.IChromosome;
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
/*    */ public class JGAPResult
/*    */   extends WorkResult
/*    */ {
/*    */   private static final String CVS_REVISION = "$Revision: 1.2 $";
/*    */   private IChromosome m_fittest;
/*    */   private Population m_pop;
/*    */   private long m_unitDone;
/*    */   
/*    */   public JGAPResult(String name, int id, IChromosome a_fittestChrom, long a_unitdone) {
/* 41 */     super(name, id);
/* 42 */     this.m_fittest = a_fittestChrom;
/* 43 */     this.m_unitDone = a_unitdone;
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
/*    */ 
/*    */ 
/*    */   
/*    */   public JGAPResult(String name, int id, Population a_chromosomes, long a_unitdone) {
/* 59 */     super(name, id);
/* 60 */     this.m_fittest = null;
/* 61 */     this.m_pop = a_chromosomes;
/* 62 */     this.m_unitDone = a_unitdone;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public IChromosome getFittest() {
/* 70 */     return this.m_fittest;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Population getPopulation() {
/* 80 */     return this.m_pop;
/*    */   }
/*    */   
/*    */   public long getUnitDone() {
/* 84 */     return this.m_unitDone;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\distr\grid\JGAPResult.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */