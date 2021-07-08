/*     */ package org.jgap.distr.grid;
/*     */ 
/*     */ import org.homedns.dade.jcgrid.WorkRequest;
/*     */ import org.homedns.dade.jcgrid.worker.GridWorkerFeedback;
/*     */ import org.jgap.Configuration;
/*     */ import org.jgap.Population;
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
/*     */ public class JGAPRequest
/*     */   extends WorkRequest
/*     */   implements ICloneable
/*     */ {
/*     */   private static final String CVS_REVISION = "$Revision: 1.9 $";
/*     */   private Configuration m_config;
/*     */   private Population m_pop;
/*     */   private IWorkerEvolveStrategy m_evolveStrategy;
/*     */   private IWorkerReturnStrategy m_returnStrategy;
/*     */   private IGenotypeInitializer m_genotypeInitializer;
/*     */   private GridWorkerFeedback m_workerFeedback;
/*     */   
/*     */   public JGAPRequest(String a_name, int a_id, Configuration a_config, IWorkerEvolveStrategy a_strategy) {
/*  54 */     super(a_name, a_id);
/*  55 */     this.m_config = a_config;
/*  56 */     this.m_evolveStrategy = a_strategy;
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
/*     */   public JGAPRequest(String a_name, int a_id, Configuration a_config) {
/*  70 */     this(a_name, a_id, a_config, new DefaultEvolveStrategy());
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
/*     */   public JGAPRequest(String a_name, int a_id, Configuration a_config, Population a_pop, IWorkerEvolveStrategy a_strategy) {
/*  88 */     this(a_name, a_id, a_config, a_strategy);
/*  89 */     this.m_pop = a_pop;
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
/*     */   public JGAPRequest(String a_name, int a_id, Configuration a_config, Population a_pop) {
/* 106 */     this(a_name, a_id, a_config, a_pop, new DefaultEvolveStrategy());
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
/*     */   public void setEvolveStrategy(IWorkerEvolveStrategy a_evolveStrategy) {
/* 119 */     this.m_evolveStrategy = a_evolveStrategy;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public IWorkerEvolveStrategy getWorkerEvolveStrategy() {
/* 129 */     return this.m_evolveStrategy;
/*     */   }
/*     */   
/*     */   public void setWorkerReturnStrategy(IWorkerReturnStrategy a_strategy) {
/* 133 */     this.m_returnStrategy = a_strategy;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public IWorkerReturnStrategy getWorkerReturnStrategy() {
/* 143 */     return this.m_returnStrategy;
/*     */   }
/*     */   
/*     */   public GridWorkerFeedback getWorkerFeedback() {
/* 147 */     return this.m_workerFeedback;
/*     */   }
/*     */   
/*     */   public void setWorkerFeedback(GridWorkerFeedback a_feedback) {
/* 151 */     this.m_workerFeedback = a_feedback;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setGenotypeInitializer(IGenotypeInitializer a_initializer) {
/* 161 */     this.m_genotypeInitializer = a_initializer;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public IGenotypeInitializer getGenotypeInitializer() {
/* 172 */     return this.m_genotypeInitializer;
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
/*     */   public void setPopulation(Population a_pop) {
/* 185 */     this.m_pop = a_pop;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Configuration getConfiguration() {
/* 196 */     return this.m_config;
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
/*     */   public void setConfiguration(Configuration a_conf) {
/* 208 */     this.m_config = a_conf;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Population getPopulation() {
/* 219 */     return this.m_pop;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object clone() {
/* 229 */     JGAPRequest result = newInstance(getSessionName(), getRID());
/* 230 */     return result;
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
/*     */   public JGAPRequest newInstance(String a_name, int a_ID) {
/* 245 */     JGAPRequest result = new JGAPRequest(a_name, a_ID, getConfiguration(), getPopulation());
/*     */     
/* 247 */     result.setEvolveStrategy(getWorkerEvolveStrategy());
/* 248 */     result.setGenotypeInitializer(getGenotypeInitializer());
/* 249 */     result.setWorkerReturnStrategy(getWorkerReturnStrategy());
/* 250 */     return result;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\distr\grid\JGAPRequest.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */