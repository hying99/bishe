/*     */ package org.jgap;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.util.Collections;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Vector;
/*     */ import org.jgap.distr.IPopulationMerger;
/*     */ import org.jgap.impl.job.EvolveData;
/*     */ import org.jgap.impl.job.EvolveJob;
/*     */ import org.jgap.impl.job.EvolveResult;
/*     */ import org.jgap.impl.job.IPopulationSplitter;
/*     */ import org.jgap.impl.job.JobData;
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
/*     */ public class Genotype
/*     */   implements Serializable, Runnable
/*     */ {
/*     */   private static final String CVS_REVISION = "$Revision: 1.103 $";
/*     */   private Configuration m_activeConfiguration;
/*     */   private static transient Configuration m_staticConfiguration;
/*     */   private Population m_population;
/*     */   
/*     */   public Genotype(Configuration a_configuration, IChromosome[] a_initialChromosomes) throws InvalidConfigurationException {
/*  71 */     this(a_configuration, new Population(a_configuration, a_initialChromosomes));
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
/*     */ 
/*     */ 
/*     */   
/*     */   public Genotype(Configuration a_configuration, Population a_population) throws InvalidConfigurationException {
/*  95 */     if (a_configuration == null) {
/*  96 */       throw new IllegalArgumentException("The Configuration instance may not be null.");
/*     */     }
/*     */     
/*  99 */     if (a_population == null) {
/* 100 */       throw new IllegalArgumentException("The Population may not be null.");
/*     */     }
/*     */     
/* 103 */     for (int i = 0; i < a_population.size(); i++) {
/* 104 */       if (a_population.getChromosome(i) == null) {
/* 105 */         throw new IllegalArgumentException("The Chromosome instance at index " + i + " of the array of " + "Chromosomes is null. No Chromosomes instance in this array " + "may be null.");
/*     */       }
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 111 */     this.m_population = a_population;
/*     */ 
/*     */ 
/*     */     
/* 115 */     a_configuration.lockSettings();
/* 116 */     this.m_activeConfiguration = a_configuration;
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
/*     */   public Genotype(Configuration a_configuration) throws InvalidConfigurationException {}
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
/*     */   public synchronized IChromosome[] getChromosomes() {
/* 144 */     Iterator<IChromosome> it = getPopulation().iterator();
/* 145 */     Chromosome[] arrayOfChromosome = new Chromosome[getPopulation().size()];
/* 146 */     int i = 0;
/* 147 */     while (it.hasNext()) {
/* 148 */       arrayOfChromosome[i++] = (Chromosome)it.next();
/*     */     }
/* 150 */     return (IChromosome[])arrayOfChromosome;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Population getPopulation() {
/* 160 */     return this.m_population;
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
/*     */   public synchronized IChromosome getFittestChromosome() {
/* 175 */     return getPopulation().determineFittestChromosome();
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
/*     */   public synchronized IChromosome getFittestChromosome(int a_startIndex, int a_endIndex) {
/* 192 */     return getPopulation().determineFittestChromosome(a_startIndex, a_endIndex);
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
/*     */   public synchronized List getFittestChromosomes(int a_numberOfChromosomes) {
/* 207 */     return getPopulation().determineFittestChromosomes(a_numberOfChromosomes);
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
/*     */   public synchronized void evolve() {
/* 224 */     IBreeder breeder = getConfiguration().getBreeder();
/* 225 */     Population newPop = breeder.evolve(getPopulation(), getConfiguration());
/* 226 */     setPopulation(newPop);
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
/*     */   public void evolve(int a_numberOfEvolutions) {
/* 241 */     for (int i = 0; i < a_numberOfEvolutions; i++) {
/* 242 */       evolve();
/*     */     }
/* 244 */     if (this.m_activeConfiguration.isKeepPopulationSizeConstant()) {
/* 245 */       keepPopSizeConstant(getPopulation(), this.m_activeConfiguration.getPopulationSize());
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
/*     */   public String toString() {
/* 258 */     StringBuffer buffer = new StringBuffer();
/* 259 */     for (int i = 0; i < getPopulation().size(); i++) {
/* 260 */       buffer.append(getPopulation().getChromosome(i).toString());
/* 261 */       buffer.append(" [");
/* 262 */       buffer.append(getPopulation().getChromosome(i).getFitnessValueDirectly());
/* 263 */       buffer.append("]\n");
/*     */     } 
/* 265 */     return buffer.toString();
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
/*     */ 
/*     */ 
/*     */   
/*     */   public static Genotype randomInitialGenotype(Configuration a_configuration) throws InvalidConfigurationException {
/* 289 */     if (a_configuration == null) {
/* 290 */       throw new IllegalArgumentException("The Configuration instance may not be null.");
/*     */     }
/*     */     
/* 293 */     a_configuration.lockSettings();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 301 */     int populationSize = a_configuration.getPopulationSize();
/* 302 */     Population pop = new Population(a_configuration, populationSize);
/*     */ 
/*     */     
/* 305 */     Genotype result = new Genotype(a_configuration, pop);
/* 306 */     result.fillPopulation(populationSize);
/* 307 */     return result;
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
/*     */   public void fillPopulation(int a_num) throws InvalidConfigurationException {
/* 321 */     IChromosome sampleChrom = getConfiguration().getSampleChromosome();
/* 322 */     Class<?> sampleClass = sampleChrom.getClass();
/* 323 */     IInitializer chromIniter = getConfiguration().getJGAPFactory().getInitializerFor(sampleChrom, sampleClass);
/*     */     
/* 325 */     if (chromIniter == null) {
/* 326 */       throw new InvalidConfigurationException("No initializer found for class " + sampleClass);
/*     */     }
/*     */     
/*     */     try {
/* 330 */       for (int i = 0; i < a_num; i++) {
/* 331 */         getPopulation().addChromosome((IChromosome)chromIniter.perform(sampleChrom, sampleClass, null));
/*     */       
/*     */       }
/*     */     }
/* 335 */     catch (Exception ex) {
/*     */ 
/*     */       
/* 338 */       if (ex.getCause() != null) {
/* 339 */         throw new IllegalStateException(ex.getCause().toString());
/*     */       }
/*     */       
/* 342 */       throw new IllegalStateException(ex.getMessage());
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
/*     */   
/*     */   public boolean equals(Object a_other) {
/*     */     try {
/* 366 */       if (a_other == null) {
/* 367 */         return false;
/*     */       }
/* 369 */       Genotype otherGenotype = (Genotype)a_other;
/*     */ 
/*     */ 
/*     */       
/* 373 */       if (getPopulation().size() != otherGenotype.getPopulation().size()) {
/* 374 */         return false;
/*     */       }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 383 */       Collections.sort(getPopulation().getChromosomes());
/* 384 */       Collections.sort(otherGenotype.getPopulation().getChromosomes());
/* 385 */       for (int i = 0; i < getPopulation().size(); i++) {
/* 386 */         if (!getPopulation().getChromosome(i).equals(otherGenotype.getPopulation().getChromosome(i)))
/*     */         {
/* 388 */           return false;
/*     */         }
/*     */       } 
/* 391 */       return true;
/* 392 */     } catch (ClassCastException e) {
/* 393 */       return false;
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
/*     */   protected void applyNaturalSelectors(boolean a_processBeforeGeneticOperators) {
/*     */     try {
/* 414 */       int selectorSize = this.m_activeConfiguration.getNaturalSelectorsSize(a_processBeforeGeneticOperators);
/*     */       
/* 416 */       if (selectorSize > 0) {
/* 417 */         int population_size = this.m_activeConfiguration.getPopulationSize();
/* 418 */         if (a_processBeforeGeneticOperators)
/*     */         {
/*     */           
/* 421 */           population_size = (int)Math.round(population_size * getConfiguration().getSelectFromPrevGen());
/*     */         }
/*     */ 
/*     */ 
/*     */         
/* 426 */         Population new_population = new Population(this.m_activeConfiguration, population_size);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 432 */         for (int i = 0; i < selectorSize; i++) {
/* 433 */           int single_selection_size; NaturalSelector selector = this.m_activeConfiguration.getNaturalSelector(a_processBeforeGeneticOperators, i);
/*     */           
/* 435 */           if (i == selectorSize - 1 && i > 0) {
/*     */ 
/*     */             
/* 438 */             single_selection_size = population_size - getPopulation().size();
/*     */           } else {
/*     */             
/* 441 */             single_selection_size = population_size / selectorSize;
/*     */           } 
/*     */ 
/*     */           
/* 445 */           selector.select(single_selection_size, getPopulation(), new_population);
/*     */ 
/*     */ 
/*     */           
/* 449 */           selector.empty();
/*     */         } 
/* 451 */         setPopulation(new Population(this.m_activeConfiguration));
/* 452 */         getPopulation().addChromosomes(new_population);
/*     */       } 
/* 454 */     } catch (InvalidConfigurationException iex) {
/*     */       
/* 456 */       throw new IllegalStateException(iex.getMessage());
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void applyGeneticOperators() {
/* 467 */     List geneticOperators = this.m_activeConfiguration.getGeneticOperators();
/* 468 */     Iterator<GeneticOperator> operatorIterator = geneticOperators.iterator();
/* 469 */     while (operatorIterator.hasNext()) {
/* 470 */       GeneticOperator operator = operatorIterator.next();
/* 471 */       applyGeneticOperator(operator, getPopulation(), getPopulation().getChromosomes());
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
/*     */   public static Configuration getStaticConfiguration() {
/* 486 */     return m_staticConfiguration;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void setStaticConfiguration(Configuration a_configuration) {
/* 497 */     m_staticConfiguration = a_configuration;
/*     */   }
/*     */   
/*     */   public Configuration getConfiguration() {
/* 501 */     return this.m_activeConfiguration;
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
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 524 */     int size = getPopulation().size();
/*     */     
/* 526 */     int twopower = 1;
/*     */ 
/*     */ 
/*     */     
/* 530 */     int localHashCode = -573;
/* 531 */     for (int i = 0; i < size; i++, twopower = 2 * twopower) {
/* 532 */       IChromosome s = getPopulation().getChromosome(i);
/* 533 */       localHashCode = 31 * localHashCode + s.hashCode();
/*     */     } 
/* 535 */     return localHashCode;
/*     */   }
/*     */   
/*     */   protected void setPopulation(Population a_pop) {
/* 539 */     this.m_population = a_pop;
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
/*     */   protected void applyGeneticOperator(GeneticOperator a_operator, Population a_population, List a_chromosomes) {
/* 558 */     a_operator.operate(a_population, a_chromosomes);
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
/*     */   protected void keepPopSizeConstant(Population a_pop, int a_maxSize) {
/* 572 */     int popSize = a_pop.size();
/*     */ 
/*     */     
/* 575 */     while (popSize > a_maxSize) {
/*     */ 
/*     */       
/* 578 */       a_pop.removeChromosome(0);
/* 579 */       popSize--;
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
/*     */   public void run() {
/*     */     while (true) {
/* 593 */       Thread.currentThread(); if (!Thread.interrupted()) {
/* 594 */         evolve();
/*     */         continue;
/*     */       } 
/*     */       break;
/*     */     } 
/*     */   }
/*     */   
/*     */   public List getEvolves(IPopulationSplitter a_splitter) throws Exception {
/* 602 */     List<EvolveJob> result = new Vector();
/* 603 */     Population[] pops = a_splitter.split(getPopulation());
/*     */ 
/*     */     
/* 606 */     for (int i = 0; i < pops.length; i++) {
/* 607 */       Configuration newConf = (Configuration)getConfiguration().clone();
/* 608 */       EvolveData data = new EvolveData(newConf);
/* 609 */       if (pops[i] == null) {
/* 610 */         throw new IllegalStateException("Population must no be null (Index: " + i + ", Splitter: " + a_splitter.getClass().getName() + ")");
/*     */       }
/*     */ 
/*     */ 
/*     */       
/* 615 */       data.setPopulation(pops[i]);
/* 616 */       data.setBreeder(newConf.getBreeder());
/* 617 */       EvolveJob evolveJob = new EvolveJob((JobData)data);
/* 618 */       result.add(evolveJob);
/*     */     } 
/* 620 */     getPopulation().clear();
/* 621 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   public void mergeResults(IPopulationMerger a_merger, EvolveResult[] a_results) throws Exception {
/* 626 */     int size = a_results.length;
/* 627 */     Population target = new Population(getConfiguration());
/* 628 */     for (int i = 0; i < size; i++) {
/* 629 */       EvolveResult singleResult = a_results[i];
/* 630 */       if (singleResult == null) {
/* 631 */         throw new IllegalStateException("Single result is null!");
/*     */       }
/* 633 */       Population pop = singleResult.getPopulation();
/*     */ 
/*     */       
/* 636 */       List<IChromosome> goodOnes = pop.determineFittestChromosomes(3);
/* 637 */       for (int j = 0; j < goodOnes.size(); j++) {
/* 638 */         IChromosome goodOne = goodOnes.get(j);
/* 639 */         target.addChromosome(goodOne);
/*     */       } 
/*     */     } 
/* 642 */     setPopulation(target);
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\Genotype.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */