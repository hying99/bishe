/*     */ package org.jgap.distr.grid.gp;
/*     */ 
/*     */ import org.homedns.dade.jcgrid.client.GridNodeClientConfig;
/*     */ import org.jgap.gp.CommandGene;
/*     */ import org.jgap.gp.impl.GPConfiguration;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class GridConfigurationGPBase
/*     */   implements IGridConfigurationGP
/*     */ {
/*     */   private static final String CVS_REVISION = "$Revision: 1.4 $";
/*     */   private IClientFeedbackGP m_clientFeedback;
/*     */   private IRequestSplitStrategyGP m_splitStrategy;
/*     */   private GPConfiguration m_config;
/*     */   private IClientEvolveStrategyGP m_clientEvolveStrategy;
/*     */   private IWorkerEvolveStrategyGP m_workerEvolveStrategy;
/*     */   private IWorkerReturnStrategyGP m_workerReturnStrategy;
/*     */   private IGenotypeInitializerGP m_genotypeInitializer;
/*     */   private String m_packageName;
/*     */   private Class[] m_types;
/*     */   private Class[][] m_argTypes;
/*     */   private CommandGene[][] m_nodeSets;
/*     */   private int[] m_minDepths;
/*     */   private int[] m_maxDepths;
/*     */   private int m_maxNodes;
/*     */   
/*     */   public String getPackageName() {
/*  62 */     return this.m_packageName;
/*     */   }
/*     */   
/*     */   public IClientFeedbackGP getClientFeedback() {
/*  66 */     return this.m_clientFeedback;
/*     */   }
/*     */   
/*     */   public IClientEvolveStrategyGP getClientEvolveStrategy() {
/*  70 */     return this.m_clientEvolveStrategy;
/*     */   }
/*     */   
/*     */   public IRequestSplitStrategyGP getRequestSplitStrategy() {
/*  74 */     return this.m_splitStrategy;
/*     */   }
/*     */   
/*     */   public GPConfiguration getConfiguration() {
/*  78 */     return this.m_config;
/*     */   }
/*     */   
/*     */   public void setConfiguration(GPConfiguration a_config) {
/*  82 */     this.m_config = a_config;
/*     */   }
/*     */   
/*     */   public IWorkerEvolveStrategyGP getWorkerEvolveStrategy() {
/*  86 */     return this.m_workerEvolveStrategy;
/*     */   }
/*     */   
/*     */   public IWorkerReturnStrategyGP getWorkerReturnStrategy() {
/*  90 */     return this.m_workerReturnStrategy;
/*     */   }
/*     */   
/*     */   public IGenotypeInitializerGP getGenotypeInitializer() {
/*  94 */     return this.m_genotypeInitializer;
/*     */   }
/*     */   
/*     */   public void setGenotypeInitializer(IGenotypeInitializerGP a_initializer) {
/*  98 */     this.m_genotypeInitializer = a_initializer;
/*     */   }
/*     */   
/*     */   public void setWorkerReturnStrategy(IWorkerReturnStrategyGP a_strategy) {
/* 102 */     this.m_workerReturnStrategy = a_strategy;
/*     */   }
/*     */   
/*     */   public void setWorkerEvolveStrategy(IWorkerEvolveStrategyGP a_strategy) {
/* 106 */     this.m_workerEvolveStrategy = a_strategy;
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
/*     */   public void setClientEvolveStrategy(IClientEvolveStrategyGP a_strategy) {
/* 131 */     this.m_clientEvolveStrategy = a_strategy;
/*     */   }
/*     */   
/*     */   public void setClientFeedback(IClientFeedbackGP a_clientFeedback) {
/* 135 */     this.m_clientFeedback = a_clientFeedback;
/*     */   }
/*     */   
/*     */   public void setRequestSplitStrategy(IRequestSplitStrategyGP a_splitStrategy) {
/* 139 */     this.m_splitStrategy = a_splitStrategy;
/*     */   }
/*     */   
/*     */   public void setTypes(Class[] a_types) {
/* 143 */     this.m_types = a_types;
/*     */   }
/*     */   
/*     */   public void setArgTypes(Class[][] a_argTypes) {
/* 147 */     this.m_argTypes = a_argTypes;
/*     */   }
/*     */   
/*     */   public void setNodeSets(CommandGene[][] a_nodeSets) {
/* 151 */     this.m_nodeSets = a_nodeSets;
/*     */   }
/*     */   
/*     */   public void setMinDepths(int[] a_minDepths) {
/* 155 */     this.m_minDepths = a_minDepths;
/*     */   }
/*     */   
/*     */   public void setMaxDepths(int[] a_maxDepths) {
/* 159 */     this.m_maxDepths = a_maxDepths;
/*     */   }
/*     */   
/*     */   public void setMaxNodes(int a_maxNodes) {
/* 163 */     this.m_maxNodes = a_maxNodes;
/*     */   }
/*     */   
/*     */   public Class[] getTypes() {
/* 167 */     return this.m_types;
/*     */   }
/*     */   
/*     */   public Class[][] getArgTypes() {
/* 171 */     return this.m_argTypes;
/*     */   }
/*     */   
/*     */   public CommandGene[][] getNodeSets() {
/* 175 */     return this.m_nodeSets;
/*     */   }
/*     */   
/*     */   public int[] getMinDepths() {
/* 179 */     return this.m_minDepths;
/*     */   }
/*     */   
/*     */   public int[] getMaxDepths() {
/* 183 */     return this.m_maxDepths;
/*     */   }
/*     */   
/*     */   public int getMaxNodes() {
/* 187 */     return this.m_maxNodes;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\distr\grid\gp\GridConfigurationGPBase.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */