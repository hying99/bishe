/*     */ package org.jgap.impl.job;
/*     */ 
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import org.jgap.Configuration;
/*     */ import org.jgap.GeneticOperator;
/*     */ import org.jgap.IBreeder;
/*     */ import org.jgap.InvalidConfigurationException;
/*     */ import org.jgap.NaturalSelector;
/*     */ import org.jgap.Population;
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
/*     */ public class EvolveJob
/*     */   extends JobBase
/*     */   implements IEvolveJob
/*     */ {
/*     */   private static final String CVS_REVISION = "$Revision: 1.12 $";
/*     */   
/*     */   public EvolveJob(JobData a_data) {
/*  32 */     super(a_data);
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
/*     */   public JobResult execute(JobData a_data) throws Exception {
/*  48 */     EvolveData data = (EvolveData)a_data;
/*  49 */     return evolve(data);
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
/*     */   public EvolveResult evolve(EvolveData a_evolveData) {
/*  62 */     EvolveResult result = new EvolveResult();
/*  63 */     Configuration config = a_evolveData.getConfiguration();
/*  64 */     result.setConfiguration(config);
/*  65 */     Population pop = a_evolveData.getPopulation();
/*     */ 
/*     */     
/*  68 */     IBreeder breeder = a_evolveData.getBreeder();
/*  69 */     pop = breeder.evolve(pop, config);
/*     */     
/*  71 */     result.setPopulation(pop);
/*  72 */     return result;
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
/*     */   protected Population applyNaturalSelectors(Configuration a_config, Population a_pop, boolean a_processBeforeGeneticOperators) {
/*     */     try {
/*  92 */       int selectorSize = a_config.getNaturalSelectorsSize(a_processBeforeGeneticOperators);
/*     */       
/*  94 */       if (selectorSize > 0) {
/*  95 */         int m_population_size = a_config.getPopulationSize();
/*     */         
/*  97 */         Population new_population = new Population(a_config, m_population_size);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 103 */         for (int i = 0; i < selectorSize; i++) {
/* 104 */           int m_single_selection_size; NaturalSelector selector = a_config.getNaturalSelector(a_processBeforeGeneticOperators, i);
/*     */           
/* 106 */           if (i == selectorSize - 1 && i > 0) {
/*     */ 
/*     */             
/* 109 */             m_single_selection_size = m_population_size - a_pop.size();
/*     */           } else {
/*     */             
/* 112 */             m_single_selection_size = m_population_size / selectorSize;
/*     */           } 
/*     */ 
/*     */ 
/*     */           
/* 117 */           selector.select(m_single_selection_size, a_pop, new_population);
/*     */ 
/*     */           
/* 120 */           selector.empty();
/*     */         } 
/*     */ 
/*     */         
/* 124 */         return new_population;
/*     */       } 
/*     */       
/* 127 */       return a_pop;
/*     */     }
/* 129 */     catch (InvalidConfigurationException iex) {
/*     */       
/* 131 */       throw new IllegalStateException(iex.getMessage());
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void applyGeneticOperators(Configuration a_config, Population a_pop) {
/* 142 */     List geneticOperators = a_config.getGeneticOperators();
/* 143 */     Iterator<GeneticOperator> operatorIterator = geneticOperators.iterator();
/* 144 */     while (operatorIterator.hasNext()) {
/* 145 */       GeneticOperator operator = operatorIterator.next();
/*     */       
/* 147 */       operator.operate(a_pop, a_pop.getChromosomes());
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\impl\job\EvolveJob.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */