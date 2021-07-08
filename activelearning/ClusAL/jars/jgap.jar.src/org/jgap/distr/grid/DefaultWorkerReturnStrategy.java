/*    */ package org.jgap.distr.grid;
/*    */ 
/*    */ import org.jgap.Genotype;
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
/*    */ public class DefaultWorkerReturnStrategy
/*    */   implements IWorkerReturnStrategy
/*    */ {
/*    */   private static final String CVS_REVISION = "$Revision: 1.2 $";
/*    */   
/*    */   public JGAPResult assembleResult(JGAPRequest a_req, Genotype a_genotype) {
/*    */     try {
/* 37 */       IChromosome fittest = a_genotype.getFittestChromosome();
/* 38 */       Population pop = new Population(a_req.getConfiguration(), fittest);
/* 39 */       JGAPResult result = new JGAPResult(a_req.getSessionName(), a_req.getRID(), pop, 1L);
/*    */       
/* 41 */       return result;
/* 42 */     } catch (Throwable t) {
/* 43 */       throw new RuntimeException(t);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\distr\grid\DefaultWorkerReturnStrategy.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */