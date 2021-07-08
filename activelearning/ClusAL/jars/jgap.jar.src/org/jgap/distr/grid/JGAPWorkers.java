/*     */ package org.jgap.distr.grid;
/*     */ 
/*     */ import org.apache.commons.cli.CommandLine;
/*     */ import org.apache.commons.cli.Options;
/*     */ import org.apache.log4j.Logger;
/*     */ import org.homedns.dade.jcgrid.GridNodeConfig;
/*     */ import org.homedns.dade.jcgrid.GridNodeGenericConfig;
/*     */ import org.homedns.dade.jcgrid.cmd.MainCmd;
/*     */ import org.homedns.dade.jcgrid.worker.GridNodeWorkerConfig;
/*     */ import org.homedns.dade.jcgrid.worker.GridWorker;
/*     */ import org.homedns.dade.jcgrid.worker.Worker;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class JGAPWorkers
/*     */ {
/*     */   private static final String CVS_REVISION = "$Revision: 1.4 $";
/*     */   private Class m_workerClass;
/*     */   private Class m_workerFeedbackClaas;
/*  33 */   private static final String className = JGAPWorkers.class.getName();
/*     */   
/*  35 */   private static Logger log = Logger.getLogger(className);
/*     */ 
/*     */   
/*     */   public JGAPWorkers(String[] args) throws Exception {
/*  39 */     MainCmd.setUpLog4J("worker", true);
/*  40 */     GridNodeWorkerConfig config = new GridNodeWorkerConfig();
/*  41 */     Options options = new Options();
/*  42 */     CommandLine cmd = MainCmd.parseCommonOptions(options, (GridNodeConfig)config, args);
/*  43 */     if (config.getSessionName().equals("none")) {
/*  44 */       config.setSessionName("MyGAWorker_session");
/*     */     }
/*  46 */     getNeededFiles(config);
/*  47 */     startWork(config);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void getNeededFiles(GridNodeWorkerConfig a_config) {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JGAPWorkers(GridNodeWorkerConfig a_config) throws Exception {
/*  68 */     startWork(a_config);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void startWork(GridNodeWorkerConfig a_config) throws Exception {
/*  75 */     GridWorker[] gw = new GridWorker[a_config.getWorkerCount()]; int i;
/*  76 */     for (i = 0; i < a_config.getWorkerCount(); i++) {
/*     */ 
/*     */       
/*  79 */       gw[i] = new GridWorker();
/*  80 */       gw[i].setNodeConfig((GridNodeGenericConfig)a_config.clone());
/*  81 */       ((GridNodeGenericConfig)gw[i].getNodeConfig()).setSessionName(a_config.getSessionName() + "_" + i);
/*     */       
/*  83 */       ((GridNodeGenericConfig)gw[i].getNodeConfig()).setWorkingDir(a_config.getWorkingDir() + "_" + i);
/*     */       
/*  85 */       Worker myWorker = new JGAPWorker();
/*     */ 
/*     */       
/*  88 */       gw[i].setWorker(myWorker);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*  94 */       gw[i].start();
/*     */     } 
/*     */ 
/*     */     
/*  98 */     for (i = 0; i < a_config.getWorkerCount(); i++) {
/*  99 */       gw[i].waitShutdown();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void main(String[] args) throws Exception {
/* 121 */     MainCmd.setUpLog4J("worker", true);
/* 122 */     GridNodeWorkerConfig config = new GridNodeWorkerConfig();
/* 123 */     Options options = new Options();
/* 124 */     CommandLine cmd = MainCmd.parseCommonOptions(options, (GridNodeConfig)config, args);
/*     */ 
/*     */     
/* 127 */     new JGAPWorkers(config);
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\distr\grid\JGAPWorkers.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */