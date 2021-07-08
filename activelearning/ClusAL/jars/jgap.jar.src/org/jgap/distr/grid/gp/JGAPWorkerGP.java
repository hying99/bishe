/*    */ package org.jgap.distr.grid.gp;
/*    */ 
/*    */ import org.homedns.dade.jcgrid.WorkRequest;
/*    */ import org.homedns.dade.jcgrid.WorkResult;
/*    */ import org.homedns.dade.jcgrid.worker.Worker;
/*    */ import org.jgap.gp.impl.GPConfiguration;
/*    */ import org.jgap.gp.impl.GPGenotype;
/*    */ import org.jgap.gp.impl.GPPopulation;
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
/*    */ public class JGAPWorkerGP
/*    */   implements Worker
/*    */ {
/*    */   private static final String CVS_REVISION = "$Revision: 1.3 $";
/*    */   
/*    */   public WorkResult doWork(WorkRequest work, String workDir) throws Exception {
/* 43 */     JGAPRequestGP req = (JGAPRequestGP)work;
/*    */ 
/*    */ 
/*    */ 
/*    */     
/* 48 */     GPConfiguration conf = req.getConfiguration();
/* 49 */     if (conf.getJGAPFactory() == null) {
/* 50 */       throw new IllegalStateException("JGAPFactory must not be null!");
/*    */     }
/* 52 */     conf = conf.newInstanceGP(conf.getId() + "_1", conf.getName() + "_1");
/*    */ 
/*    */     
/* 55 */     req.setConfiguration(conf);
/* 56 */     GPGenotype gen = null;
/*    */ 
/*    */     
/* 59 */     if (req.getGenotypeInitializer() != null) {
/*    */ 
/*    */       
/* 62 */       GPPopulation initialPop = req.getPopulation();
/* 63 */       gen = req.getGenotypeInitializer().setupGenotype(req, initialPop);
/* 64 */       if (req.getWorkerEvolveStrategy() != null)
/*    */       {
/*    */         
/* 67 */         req.getWorkerEvolveStrategy().evolve(gen);
/*    */       }
/*    */     } 
/*    */ 
/*    */     
/* 72 */     WorkResult res = req.getWorkerReturnStrategy().assembleResult(req, gen);
/* 73 */     return res;
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
/*    */   public static void main(String[] args) throws Exception {
/* 89 */     new JGAPWorkersGP(args);
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\distr\grid\gp\JGAPWorkerGP.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */