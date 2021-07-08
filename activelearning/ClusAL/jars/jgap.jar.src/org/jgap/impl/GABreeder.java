/*     */ package org.jgap.impl;
/*     */ 
/*     */ import org.jgap.BreederBase;
/*     */ import org.jgap.BulkFitnessFunction;
/*     */ import org.jgap.Configuration;
/*     */ import org.jgap.IChromosome;
/*     */ import org.jgap.IInitializer;
/*     */ import org.jgap.InvalidConfigurationException;
/*     */ import org.jgap.Population;
/*     */ import org.jgap.event.GeneticEvent;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class GABreeder
/*     */   extends BreederBase
/*     */ {
/*     */   private static final String CVS_REVISION = "$Revision: 1.8 $";
/*     */   
/*     */   public Population evolve(Population a_pop, Configuration config) {
/*  25 */     Population pop = a_pop;
/*  26 */     int originalPopSize = config.getPopulationSize();
/*  27 */     IChromosome fittest = null;
/*     */ 
/*     */ 
/*     */     
/*  31 */     if (config.getGenerationNr() == 0) {
/*  32 */       int k = pop.size();
/*  33 */       for (int m = 0; m < k; m++) {
/*  34 */         IChromosome chrom = pop.getChromosome(m);
/*  35 */         chrom.increaseAge();
/*     */ 
/*     */       
/*     */       }
/*     */ 
/*     */     
/*     */     }
/*  42 */     else if (config.isPreserveFittestIndividual()) {
/*     */ 
/*     */       
/*  45 */       fittest = pop.determineFittestChromosome(0, pop.size() - 1);
/*     */     } 
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
/*  57 */     if (config.isKeepPopulationSizeConstant()) {
/*     */       try {
/*  59 */         pop.keepPopSizeConstant();
/*  60 */       } catch (InvalidConfigurationException iex) {
/*  61 */         throw new RuntimeException(iex);
/*     */       } 
/*     */     }
/*  64 */     int currentPopSize = pop.size();
/*     */ 
/*     */     
/*  67 */     BulkFitnessFunction bulkFunction = config.getBulkFitnessFunction();
/*  68 */     boolean bulkFitFunc = (bulkFunction != null);
/*  69 */     if (!bulkFitFunc) {
/*  70 */       for (int k = 0; k < currentPopSize; k++) {
/*  71 */         IChromosome chrom = pop.getChromosome(k);
/*  72 */         chrom.getFitnessValue();
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*  77 */     pop = applyNaturalSelectors(config, pop, true);
/*     */ 
/*     */     
/*  80 */     applyGeneticOperators(config, pop);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  87 */     currentPopSize = pop.size();
/*  88 */     for (int i = originalPopSize; i < currentPopSize; i++) {
/*  89 */       IChromosome chrom = pop.getChromosome(i);
/*  90 */       chrom.setFitnessValueDirectly(-1.0D);
/*     */ 
/*     */       
/*  93 */       chrom.resetAge();
/*     */ 
/*     */       
/*  96 */       chrom.increaseOperatedOn();
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 101 */     int size = Math.min(originalPopSize, currentPopSize);
/* 102 */     for (int j = 0; j < size; j++) {
/* 103 */       IChromosome chrom = pop.getChromosome(j);
/* 104 */       chrom.increaseAge();
/*     */ 
/*     */       
/* 107 */       chrom.resetOperatedOn();
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 112 */     pop = applyNaturalSelectors(config, pop, false);
/*     */ 
/*     */     
/* 115 */     if (bulkFunction != null)
/*     */     {
/*     */       
/* 118 */       bulkFunction.evaluate(pop);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 123 */     if (config.getMinimumPopSizePercent() > 0) {
/* 124 */       int sizeWanted = config.getPopulationSize();
/*     */       
/* 126 */       int minSize = (int)Math.round(sizeWanted * config.getMinimumPopSizePercent() / 100.0D);
/*     */ 
/*     */       
/* 129 */       int popSize = pop.size();
/* 130 */       if (popSize < minSize) {
/*     */         
/* 132 */         IChromosome sampleChrom = config.getSampleChromosome();
/* 133 */         Class<?> sampleChromClass = sampleChrom.getClass();
/* 134 */         IInitializer chromIniter = config.getJGAPFactory().getInitializerFor(sampleChrom, sampleChromClass);
/*     */         
/* 136 */         while (pop.size() < minSize) {
/*     */ 
/*     */           
/*     */           try {
/* 140 */             IChromosome newChrom = (IChromosome)chromIniter.perform(sampleChrom, sampleChromClass, null);
/*     */             
/* 142 */             pop.addChromosome(newChrom);
/* 143 */           } catch (Exception ex) {
/* 144 */             throw new RuntimeException(ex);
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 151 */     if (fittest != null && !pop.contains(fittest))
/*     */     {
/*     */       
/* 154 */       pop.addChromosome(fittest);
/*     */     }
/*     */ 
/*     */     
/* 158 */     config.incrementGenerationNr();
/*     */ 
/*     */     
/* 161 */     config.getEventManager().fireGeneticEvent(new GeneticEvent("genotype_evolved_event", this));
/*     */     
/* 163 */     return pop;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object clone() {
/* 173 */     return new GABreeder();
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\impl\GABreeder.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */