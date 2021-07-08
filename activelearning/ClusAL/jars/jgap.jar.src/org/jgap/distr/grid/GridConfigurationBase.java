/*     */ package org.jgap.distr.grid;
/*     */ 
/*     */ import org.homedns.dade.jcgrid.client.GridNodeClientConfig;
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
/*     */ public abstract class GridConfigurationBase
/*     */   implements IGridConfiguration
/*     */ {
/*     */   private static final String CVS_REVISION = "$Revision: 1.2 $";
/*     */   private IClientFeedback m_clientFeedback;
/*     */   private IRequestSplitStrategy m_splitStrategy;
/*     */   private Configuration m_config;
/*     */   private IClientEvolveStrategy m_clientEvolveStrategy;
/*     */   private IWorkerEvolveStrategy m_workerEvolveStrategy;
/*     */   private IWorkerReturnStrategy m_workerReturnStrategy;
/*     */   private IGenotypeInitializer m_genotypeInitializer;
/*     */   private String m_packageName;
/*     */   
/*     */   public String getPackageName() {
/*  48 */     return this.m_packageName;
/*     */   }
/*     */   
/*     */   public IClientFeedback getClientFeedback() {
/*  52 */     return this.m_clientFeedback;
/*     */   }
/*     */   
/*     */   public IClientEvolveStrategy getClientEvolveStrategy() {
/*  56 */     return this.m_clientEvolveStrategy;
/*     */   }
/*     */   
/*     */   public IRequestSplitStrategy getRequestSplitStrategy() {
/*  60 */     return this.m_splitStrategy;
/*     */   }
/*     */   
/*     */   public Configuration getConfiguration() {
/*  64 */     return this.m_config;
/*     */   }
/*     */   
/*     */   public void setConfiguration(Configuration a_config) {
/*  68 */     this.m_config = a_config;
/*     */   }
/*     */   
/*     */   public IWorkerEvolveStrategy getWorkerEvolveStrategy() {
/*  72 */     return this.m_workerEvolveStrategy;
/*     */   }
/*     */   
/*     */   public IWorkerReturnStrategy getWorkerReturnStrategy() {
/*  76 */     return this.m_workerReturnStrategy;
/*     */   }
/*     */   
/*     */   public IGenotypeInitializer getGenotypeInitializer() {
/*  80 */     return this.m_genotypeInitializer;
/*     */   }
/*     */   
/*     */   public void setGenotypeInitializer(IGenotypeInitializer a_initializer) {
/*  84 */     this.m_genotypeInitializer = a_initializer;
/*     */   }
/*     */   
/*     */   public void setWorkerReturnStrategy(IWorkerReturnStrategy a_strategy) {
/*  88 */     this.m_workerReturnStrategy = a_strategy;
/*     */   }
/*     */   
/*     */   public void setWorkerEvolveStrategy(IWorkerEvolveStrategy a_strategy) {
/*  92 */     this.m_workerEvolveStrategy = a_strategy;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract void initialize(GridNodeClientConfig paramGridNodeClientConfig) throws Exception;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract void validate() throws Exception;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setClientEvolveStrategy(IClientEvolveStrategy a_strategy) {
/* 117 */     this.m_clientEvolveStrategy = a_strategy;
/*     */   }
/*     */   
/*     */   public void setClientFeedback(IClientFeedback a_clientFeedback) {
/* 121 */     this.m_clientFeedback = a_clientFeedback;
/*     */   }
/*     */   
/*     */   public void setRequestSplitStrategy(IRequestSplitStrategy a_splitStrategy) {
/* 125 */     this.m_splitStrategy = a_splitStrategy;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\distr\grid\GridConfigurationBase.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */