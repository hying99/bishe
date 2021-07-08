/*     */ package org.jgap.distr.grid;
/*     */ 
/*     */ import org.apache.commons.cli.CommandLine;
/*     */ import org.apache.commons.cli.Options;
/*     */ import org.apache.log4j.Logger;
/*     */ import org.homedns.dade.jcgrid.GridNodeConfig;
/*     */ import org.homedns.dade.jcgrid.GridNodeGenericConfig;
/*     */ import org.homedns.dade.jcgrid.client.GridClient;
/*     */ import org.homedns.dade.jcgrid.client.GridNodeClientConfig;
/*     */ import org.homedns.dade.jcgrid.cmd.MainCmd;
/*     */ import org.homedns.dade.jcgrid.message.GridMessage;
/*     */ import org.homedns.dade.jcgrid.message.GridMessageWorkRequest;
/*     */ import org.homedns.dade.jcgrid.message.GridMessageWorkResult;
/*     */ import org.jgap.Configuration;
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
/*     */ public class JGAPClient
/*     */   extends Thread
/*     */ {
/*     */   private static final String CVS_REVISION = "$Revision: 1.12 $";
/*  31 */   private static final String className = JGAPClient.class.getName();
/*     */   
/*  33 */   private static Logger log = Logger.getLogger(className);
/*     */   
/*     */   protected GridNodeClientConfig m_gridconfig;
/*     */   
/*     */   protected JGAPRequest m_workReq;
/*     */   
/*     */   private GridClient m_gc;
/*     */   
/*     */   private IGridConfiguration m_gridConfig;
/*     */ 
/*     */   
/*     */   public JGAPClient(GridNodeClientConfig a_gridconfig, String a_clientClassName) throws Exception {
/*  45 */     this.m_gridconfig = a_gridconfig;
/*  46 */     Class<?> client = Class.forName(a_clientClassName);
/*  47 */     this.m_gridConfig = client.getConstructor(new Class[0]).newInstance(new Object[0]);
/*     */     
/*  49 */     this.m_gridConfig.initialize(this.m_gridconfig);
/*     */ 
/*     */     
/*  52 */     JGAPRequest req = new JGAPRequest(this.m_gridconfig.getSessionName(), 0, this.m_gridConfig.getConfiguration());
/*     */     
/*  54 */     req.setWorkerReturnStrategy(this.m_gridConfig.getWorkerReturnStrategy());
/*  55 */     req.setGenotypeInitializer(this.m_gridConfig.getGenotypeInitializer());
/*  56 */     req.setEvolveStrategy(this.m_gridConfig.getWorkerEvolveStrategy());
/*     */ 
/*     */     
/*  59 */     req.setEvolveStrategy(null);
/*  60 */     setWorkRequest(req);
/*     */ 
/*     */     
/*  63 */     start();
/*  64 */     join();
/*     */   }
/*     */   
/*     */   public void setWorkRequest(JGAPRequest a_request) {
/*  68 */     this.m_workReq = a_request;
/*     */   }
/*     */ 
/*     */   
/*     */   protected GridClient startClient() throws Exception {
/*  73 */     GridClient gc = new GridClient();
/*  74 */     gc.setNodeConfig((GridNodeGenericConfig)this.m_gridconfig);
/*  75 */     gc.start();
/*  76 */     return gc;
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
/*     */   public void run() {
/*     */     try {
/*  89 */       this.m_gc = startClient();
/*     */ 
/*     */       
/*     */       try {
/*  93 */         IClientEvolveStrategy clientEvolver = this.m_gridConfig.getClientEvolveStrategy();
/*     */         
/*  95 */         if (clientEvolver != null) {
/*  96 */           clientEvolver.initialize(this.m_gc, getConfiguration(), this.m_gridConfig.getClientFeedback());
/*     */         }
/*     */ 
/*     */ 
/*     */         
/* 101 */         evolve(this.m_gc);
/*     */       } finally {
/*     */         try {
/* 104 */           this.m_gc.stop();
/* 105 */         } catch (Exception ex) {}
/*     */       } 
/* 107 */     } catch (Exception ex) {
/* 108 */       ex.printStackTrace();
/* 109 */       if (this.m_gridConfig.getClientFeedback() != null) {
/* 110 */         this.m_gridConfig.getClientFeedback().error("Error while doing the work", ex);
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void sendWorkRequests(JGAPRequest[] a_workList) throws Exception {
/* 119 */     for (int i = 0; i < a_workList.length; i++) {
/* 120 */       JGAPRequest req = a_workList[i];
/* 121 */       this.m_gridConfig.getClientFeedback().sendingFragmentRequest(req);
/* 122 */       this.m_gc.send((GridMessage)new GridMessageWorkRequest(req));
/* 123 */       if (isInterrupted()) {
/*     */         break;
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   protected void receiveWorkResults(JGAPRequest[] workList) throws Exception {
/* 131 */     IClientFeedback feedback = this.m_gridConfig.getClientFeedback();
/*     */ 
/*     */     
/* 134 */     int idx = -1;
/* 135 */     for (int i = 0; i < workList.length; i++) {
/* 136 */       feedback.setProgressValue(i + workList.length);
/* 137 */       this.m_gc.getGridMessageChannel();
/* 138 */       GridMessageWorkResult gmwr = (GridMessageWorkResult)this.m_gc.recv(i);
/* 139 */       JGAPResult workResult = (JGAPResult)gmwr.getWorkResult();
/* 140 */       this.m_gridConfig.getClientEvolveStrategy().resultReceived(workResult);
/* 141 */       idx = workResult.getRID();
/*     */ 
/*     */       
/* 144 */       feedback.receivedFragmentResult(workList[idx], workResult, idx);
/*     */       
/* 146 */       if (isInterrupted()) {
/*     */         break;
/*     */       }
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
/*     */   protected void evolve(GridClient gc) throws Exception {
/* 162 */     IClientFeedback feedback = this.m_gridConfig.getClientFeedback();
/* 163 */     feedback.beginWork();
/* 164 */     IClientEvolveStrategy evolver = this.m_gridConfig.getClientEvolveStrategy();
/* 165 */     IRequestSplitStrategy splitter = this.m_gridConfig.getRequestSplitStrategy();
/* 166 */     int evolutionIndex = 0;
/*     */     
/*     */     while (true) {
/* 169 */       JGAPRequest[] workRequests = evolver.generateWorkRequests(this.m_workReq, splitter, null);
/*     */       
/* 171 */       feedback.setProgressMaximum(0);
/* 172 */       feedback.setProgressMaximum(workRequests.length - 1);
/* 173 */       sendWorkRequests(workRequests);
/* 174 */       if (isInterrupted()) {
/*     */         break;
/*     */       }
/* 177 */       evolver.afterWorkRequestsSent();
/* 178 */       receiveWorkResults(workRequests);
/* 179 */       evolver.evolve();
/*     */ 
/*     */       
/* 182 */       feedback.completeFrame(evolutionIndex);
/* 183 */       evolutionIndex++;
/*     */ 
/*     */       
/* 186 */       if (evolver.isEvolutionFinished(evolutionIndex)) {
/* 187 */         evolver.onFinished();
/*     */         break;
/*     */       } 
/*     */     } 
/* 191 */     this.m_gridConfig.getClientFeedback().endWork();
/*     */   }
/*     */   
/*     */   public void start() {
/*     */     try {
/* 196 */       this.m_gridConfig.validate();
/* 197 */     } catch (Exception ex) {
/* 198 */       throw new RuntimeException(ex);
/*     */     } 
/* 200 */     super.start();
/*     */   }
/*     */   
/*     */   public Configuration getConfiguration() {
/* 204 */     return this.m_gridConfig.getConfiguration();
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
/*     */   public static void main(String[] args) {
/* 216 */     if (args.length < 1) {
/* 217 */       System.out.println("Please provide a name of the grid configuration class to use");
/*     */       
/* 219 */       System.out.println("An example class would be examples.grid.fitnessDistributed.GridConfiguration");
/*     */       
/* 221 */       System.exit(1);
/*     */     } 
/*     */ 
/*     */     
/*     */     try {
/* 226 */       MainCmd.setUpLog4J("client", true);
/*     */ 
/*     */       
/* 229 */       GridNodeClientConfig config = new GridNodeClientConfig();
/* 230 */       Options options = new Options();
/* 231 */       CommandLine cmd = MainCmd.parseCommonOptions(options, (GridNodeConfig)config, args);
/*     */ 
/*     */       
/* 234 */       new JGAPClient(config, args[0]);
/* 235 */     } catch (Exception ex) {
/* 236 */       ex.printStackTrace();
/* 237 */       System.exit(1);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\distr\grid\JGAPClient.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */