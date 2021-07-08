/*     */ package org.jgap.distr.grid.gp;
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
/*     */ import org.jgap.gp.impl.GPConfiguration;
/*     */ import org.jgap.gp.impl.GPPopulation;
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
/*     */ public class JGAPClientGP
/*     */   extends Thread
/*     */ {
/*     */   private static final String CVS_REVISION = "$Revision: 1.6 $";
/*  33 */   private transient Logger log = Logger.getLogger(getClass());
/*     */ 
/*     */   
/*     */   protected GridNodeClientConfig m_gridconfig;
/*     */   
/*     */   protected JGAPRequestGP m_workReq;
/*     */   
/*     */   private GridClient m_gc;
/*     */   
/*     */   private IGridConfigurationGP m_gridConfig;
/*     */ 
/*     */   
/*     */   public JGAPClientGP(GridNodeClientConfig a_gridconfig, String a_clientClassName) throws Exception {
/*  46 */     this.m_gridconfig = a_gridconfig;
/*  47 */     Class<?> client = Class.forName(a_clientClassName);
/*  48 */     this.m_gridConfig = client.getConstructor(new Class[0]).newInstance(new Object[0]);
/*     */     
/*  50 */     this.m_gridConfig.initialize(this.m_gridconfig);
/*  51 */     if (this.m_gridConfig.getClientFeedback() == null) {
/*  52 */       this.m_gridConfig.setClientFeedback(new NullClientFeedbackGP());
/*     */     }
/*     */ 
/*     */     
/*  56 */     JGAPRequestGP req = new JGAPRequestGP(this.m_gridconfig.getSessionName(), 0, this.m_gridConfig);
/*     */     
/*  58 */     req.setWorkerReturnStrategy(this.m_gridConfig.getWorkerReturnStrategy());
/*  59 */     req.setGenotypeInitializer(this.m_gridConfig.getGenotypeInitializer());
/*  60 */     req.setEvolveStrategy(this.m_gridConfig.getWorkerEvolveStrategy());
/*     */ 
/*     */     
/*  63 */     req.setEvolveStrategy(null);
/*  64 */     setWorkRequest(req);
/*     */ 
/*     */     
/*  67 */     start();
/*  68 */     join();
/*     */   }
/*     */   
/*     */   public void setWorkRequest(JGAPRequestGP a_request) {
/*  72 */     this.m_workReq = a_request;
/*     */   }
/*     */ 
/*     */   
/*     */   protected GridClient startClient() throws Exception {
/*  77 */     GridClient gc = new GridClient();
/*  78 */     gc.setNodeConfig((GridNodeGenericConfig)this.m_gridconfig);
/*  79 */     gc.start();
/*  80 */     return gc;
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
/*  93 */       this.m_gc = startClient();
/*     */ 
/*     */       
/*     */       try {
/*  97 */         IClientEvolveStrategyGP clientEvolver = this.m_gridConfig.getClientEvolveStrategy();
/*     */         
/*  99 */         if (clientEvolver != null) {
/* 100 */           clientEvolver.initialize(this.m_gc, getConfiguration(), this.m_gridConfig.getClientFeedback());
/*     */         }
/*     */ 
/*     */ 
/*     */         
/* 105 */         evolve(this.m_gc);
/*     */       } finally {
/*     */         try {
/* 108 */           this.m_gc.stop();
/* 109 */         } catch (Exception ex) {}
/*     */       } 
/* 111 */     } catch (Exception ex) {
/* 112 */       ex.printStackTrace();
/* 113 */       this.m_gridConfig.getClientFeedback().error("Error while doing the work", ex);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void sendWorkRequests(JGAPRequestGP[] a_workList) throws Exception {
/* 121 */     for (int i = 0; i < a_workList.length; i++) {
/* 122 */       JGAPRequestGP req = a_workList[i];
/* 123 */       GPPopulation pop = req.getPopulation();
/* 124 */       if (pop == null || pop.isFirstEmpty()) {
/* 125 */         this.log.error("Initial population to send to worker is empty!");
/*     */       }
/* 127 */       this.m_gridConfig.getClientFeedback().sendingFragmentRequest(req);
/* 128 */       this.m_gc.send((GridMessage)new GridMessageWorkRequest(req));
/* 129 */       if (isInterrupted()) {
/*     */         break;
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   protected void receiveWorkResults(JGAPRequestGP[] workList) throws Exception {
/* 137 */     IClientFeedbackGP feedback = this.m_gridConfig.getClientFeedback();
/*     */ 
/*     */     
/* 140 */     int idx = -1;
/* 141 */     for (int i = 0; i < workList.length; i++) {
/* 142 */       feedback.setProgressValue(i + workList.length);
/* 143 */       this.m_gc.getGridMessageChannel();
/* 144 */       GridMessageWorkResult gmwr = (GridMessageWorkResult)this.m_gc.recv(i);
/* 145 */       JGAPResultGP workResult = (JGAPResultGP)gmwr.getWorkResult();
/* 146 */       this.m_gridConfig.getClientEvolveStrategy().resultReceived(workResult);
/* 147 */       idx = workResult.getRID();
/*     */ 
/*     */       
/* 150 */       feedback.receivedFragmentResult(workList[idx], workResult, idx);
/*     */       
/* 152 */       if (isInterrupted()) {
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
/* 168 */     IClientFeedbackGP feedback = this.m_gridConfig.getClientFeedback();
/* 169 */     feedback.beginWork();
/* 170 */     IClientEvolveStrategyGP evolver = this.m_gridConfig.getClientEvolveStrategy();
/* 171 */     IRequestSplitStrategyGP splitter = this.m_gridConfig.getRequestSplitStrategy();
/* 172 */     int evolutionIndex = 0;
/*     */     while (true) {
/* 174 */       this.log.warn("Beginning evolution cycle " + evolutionIndex);
/*     */       
/* 176 */       JGAPRequestGP[] workRequests = evolver.generateWorkRequests(this.m_workReq, splitter, null);
/*     */       
/* 178 */       feedback.setProgressMaximum(0);
/* 179 */       feedback.setProgressMaximum(workRequests.length - 1);
/* 180 */       sendWorkRequests(workRequests);
/* 181 */       if (isInterrupted()) {
/*     */         break;
/*     */       }
/* 184 */       evolver.afterWorkRequestsSent();
/* 185 */       receiveWorkResults(workRequests);
/* 186 */       evolver.evolve();
/*     */ 
/*     */       
/* 189 */       feedback.completeFrame(evolutionIndex);
/* 190 */       evolutionIndex++;
/*     */ 
/*     */       
/* 193 */       if (evolver.isEvolutionFinished(evolutionIndex)) {
/* 194 */         evolver.onFinished();
/*     */         break;
/*     */       } 
/*     */     } 
/* 198 */     this.m_gridConfig.getClientFeedback().endWork();
/*     */   }
/*     */   
/*     */   public void start() {
/*     */     try {
/* 203 */       this.m_gridConfig.validate();
/* 204 */     } catch (Exception ex) {
/* 205 */       throw new RuntimeException(ex);
/*     */     } 
/* 207 */     super.start();
/*     */   }
/*     */   
/*     */   public GPConfiguration getConfiguration() {
/* 211 */     return this.m_gridConfig.getConfiguration();
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
/* 223 */     if (args.length < 1) {
/* 224 */       System.out.println("Please provide a name of the grid configuration class to use");
/*     */       
/* 226 */       System.out.println("An example class would be examples.grid.fitnessDistributed.GridConfiguration");
/*     */       
/* 228 */       System.exit(1);
/*     */     } 
/*     */ 
/*     */     
/*     */     try {
/* 233 */       MainCmd.setUpLog4J("client", true);
/*     */ 
/*     */       
/* 236 */       GridNodeClientConfig config = new GridNodeClientConfig();
/* 237 */       Options options = new Options();
/* 238 */       CommandLine cmd = MainCmd.parseCommonOptions(options, (GridNodeConfig)config, args);
/*     */ 
/*     */       
/* 241 */       new JGAPClientGP(config, args[0]);
/* 242 */     } catch (Exception ex) {
/* 243 */       ex.printStackTrace();
/* 244 */       System.exit(1);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\distr\grid\gp\JGAPClientGP.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */