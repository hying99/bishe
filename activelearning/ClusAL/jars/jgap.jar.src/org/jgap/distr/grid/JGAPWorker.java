/*    */ package org.jgap.distr.grid;
/*    */ 
/*    */ import org.homedns.dade.jcgrid.WorkRequest;
/*    */ import org.homedns.dade.jcgrid.WorkResult;
/*    */ import org.homedns.dade.jcgrid.worker.Worker;
/*    */ import org.jgap.Configuration;
/*    */ import org.jgap.Genotype;
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
/*    */ public class JGAPWorker
/*    */   implements Worker
/*    */ {
/*    */   private static final String CVS_REVISION = "$Revision: 1.7 $";
/*    */   
/*    */   public WorkResult doWork(WorkRequest work, String workDir) throws Exception {
/* 43 */     JGAPRequest req = (JGAPRequest)work;
/*    */ 
/*    */ 
/*    */ 
/*    */     
/* 48 */     Configuration conf = req.getConfiguration();
/* 49 */     conf = conf.newInstance(conf.getId() + "_1", conf.getName() + "_1");
/*    */ 
/*    */     
/* 52 */     req.setConfiguration(conf);
/* 53 */     Genotype gen = null;
/*    */ 
/*    */     
/* 56 */     if (req.getGenotypeInitializer() != null) {
/*    */ 
/*    */       
/* 59 */       Population initialPop = req.getPopulation();
/* 60 */       gen = req.getGenotypeInitializer().setupGenotype(req, initialPop);
/* 61 */       if (req.getWorkerEvolveStrategy() != null)
/*    */       {
/*    */         
/* 64 */         req.getWorkerEvolveStrategy().evolve(gen);
/*    */       }
/*    */     } 
/*    */ 
/*    */     
/* 69 */     WorkResult res = req.getWorkerReturnStrategy().assembleResult(req, gen);
/* 70 */     return res;
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
/* 86 */     new JGAPWorkers(args);
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\distr\grid\JGAPWorker.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */