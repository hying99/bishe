/*     */ package org.jgap.distr.grid.gp;
/*     */ 
/*     */ import org.homedns.dade.jcgrid.WorkRequest;
/*     */ import org.homedns.dade.jcgrid.worker.GridWorkerFeedback;
/*     */ import org.jgap.gp.impl.GPConfiguration;
/*     */ import org.jgap.gp.impl.GPPopulation;
/*     */ import org.jgap.util.ICloneable;
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
/*     */ public class JGAPRequestGP
/*     */   extends WorkRequest
/*     */   implements ICloneable
/*     */ {
/*     */   private static final String CVS_REVISION = "$Revision: 1.5 $";
/*     */   private IGridConfigurationGP m_config;
/*     */   private GPPopulation m_pop;
/*     */   private IWorkerEvolveStrategyGP m_evolveStrategy;
/*     */   private IWorkerReturnStrategyGP m_returnStrategy;
/*     */   private IGenotypeInitializerGP m_genotypeInitializer;
/*     */   private GridWorkerFeedback m_workerFeedback;
/*     */   
/*     */   public JGAPRequestGP(String a_name, int a_id, IGridConfigurationGP a_config, IWorkerEvolveStrategyGP a_strategy) {
/*  57 */     super(a_name, a_id);
/*  58 */     this.m_config = a_config;
/*  59 */     this.m_evolveStrategy = a_strategy;
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
/*     */   public JGAPRequestGP(String name, int id, IGridConfigurationGP a_config) {
/*  73 */     this(name, id, a_config, new DefaultEvolveStrategyGP());
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
/*     */   public JGAPRequestGP(String a_name, int a_id, IGridConfigurationGP a_config, GPPopulation a_pop, IWorkerEvolveStrategyGP a_strategy) {
/*  91 */     this(a_name, a_id, a_config, a_strategy);
/*  92 */     this.m_pop = a_pop;
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
/*     */   public JGAPRequestGP(String a_name, int a_id, IGridConfigurationGP a_config, GPPopulation a_pop) {
/* 109 */     this(a_name, a_id, a_config, a_pop, new DefaultEvolveStrategyGP());
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
/*     */   public void setEvolveStrategy(IWorkerEvolveStrategyGP a_evolveStrategy) {
/* 122 */     this.m_evolveStrategy = a_evolveStrategy;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public IWorkerEvolveStrategyGP getWorkerEvolveStrategy() {
/* 132 */     return this.m_evolveStrategy;
/*     */   }
/*     */   
/*     */   public void setWorkerReturnStrategy(IWorkerReturnStrategyGP a_strategy) {
/* 136 */     this.m_returnStrategy = a_strategy;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public IWorkerReturnStrategyGP getWorkerReturnStrategy() {
/* 146 */     return this.m_returnStrategy;
/*     */   }
/*     */   
/*     */   public GridWorkerFeedback getWorkerFeedback() {
/* 150 */     return this.m_workerFeedback;
/*     */   }
/*     */   
/*     */   public void setWorkerFeedback(GridWorkerFeedback a_feedback) {
/* 154 */     this.m_workerFeedback = a_feedback;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setGenotypeInitializer(IGenotypeInitializerGP a_initializer) {
/* 164 */     this.m_genotypeInitializer = a_initializer;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public IGenotypeInitializerGP getGenotypeInitializer() {
/* 175 */     return this.m_genotypeInitializer;
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
/*     */   public void setPopulation(GPPopulation a_pop) {
/* 188 */     this.m_pop = a_pop;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public GPConfiguration getConfiguration() {
/* 199 */     return this.m_config.getConfiguration();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public IGridConfigurationGP getGridConfiguration() {
/* 209 */     return this.m_config;
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
/*     */   public void setConfiguration(GPConfiguration a_conf) {
/* 222 */     this.m_config.setConfiguration(a_conf);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public GPPopulation getPopulation() {
/* 233 */     return this.m_pop;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object clone() {
/* 243 */     JGAPRequestGP result = newInstance(getSessionName(), getRID());
/* 244 */     return result;
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
/*     */   public JGAPRequestGP newInstance(String a_name, int a_ID) {
/* 259 */     JGAPRequestGP result = new JGAPRequestGP(a_name, a_ID, this.m_config, getPopulation());
/*     */     
/* 261 */     result.setEvolveStrategy(getWorkerEvolveStrategy());
/* 262 */     result.setGenotypeInitializer(getGenotypeInitializer());
/* 263 */     result.setWorkerReturnStrategy(getWorkerReturnStrategy());
/* 264 */     return result;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\distr\grid\gp\JGAPRequestGP.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */