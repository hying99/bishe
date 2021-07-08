/*      */ package org.jgap.gp.impl;
/*      */ 
/*      */ import java.io.Serializable;
/*      */ import java.util.Arrays;
/*      */ import java.util.Comparator;
/*      */ import java.util.Hashtable;
/*      */ import java.util.Iterator;
/*      */ import java.util.Map;
/*      */ import org.apache.log4j.Logger;
/*      */ import org.jgap.ICloneHandler;
/*      */ import org.jgap.IInitializer;
/*      */ import org.jgap.InvalidConfigurationException;
/*      */ import org.jgap.RandomGenerator;
/*      */ import org.jgap.event.GeneticEvent;
/*      */ import org.jgap.gp.CommandGene;
/*      */ import org.jgap.gp.IGPFitnessEvaluator;
/*      */ import org.jgap.gp.IGPProgram;
/*      */ import org.jgap.gp.IPopulationCreator;
/*      */ import org.jgap.gp.terminal.Variable;
/*      */ import org.jgap.util.SystemKit;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class GPGenotype
/*      */   implements Runnable, Serializable, Comparable
/*      */ {
/*      */   private static final String CVS_REVISION = "$Revision: 1.45 $";
/*   33 */   private static transient Logger LOGGER = Logger.getLogger(GPGenotype.class);
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private GPPopulation m_population;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private GPConfiguration m_configuration;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static transient GPConfiguration m_staticConfiguration;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private double m_bestFitness;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private double m_totalFitness;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private IGPProgram m_allTimeBest;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private double m_allTimeBestFitness;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean[] m_fullModeAllowed;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private Class[] m_types;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private Class[][] m_argTypes;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private CommandGene[][] m_nodeSets;
/*      */ 
/*      */ 
/*      */   
/*      */   private int[] m_minDepths;
/*      */ 
/*      */ 
/*      */   
/*      */   private int[] m_maxDepths;
/*      */ 
/*      */ 
/*      */   
/*      */   private int m_maxNodes;
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean m_verbose;
/*      */ 
/*      */ 
/*      */   
/*      */   private Map m_variables;
/*      */ 
/*      */ 
/*      */   
/*      */   private IGPProgram m_fittestToAdd;
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean m_cloneWarningGPProgramShown;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public GPGenotype() throws InvalidConfigurationException {
/*  122 */     init();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public GPGenotype(GPConfiguration a_configuration, GPPopulation a_population, Class[] a_types, Class[][] a_argTypes, CommandGene[][] a_nodeSets, int[] a_minDepths, int[] a_maxDepths, int a_maxNodes) throws InvalidConfigurationException {
/*  157 */     if (a_configuration == null) {
/*  158 */       throw new IllegalArgumentException("The configuration instance may not be null.");
/*      */     }
/*      */     
/*  161 */     if (a_population == null) {
/*  162 */       throw new IllegalArgumentException("The population may not be null.");
/*      */     }
/*      */     
/*  165 */     for (int i = 0; i < a_population.size(); i++) {
/*  166 */       if (a_population.getGPProgram(i) == null) {
/*  167 */         throw new IllegalArgumentException("The GPProgram instance at index " + i + " in population" + " is null, which is forbidden in general.");
/*      */       }
/*      */     } 
/*      */ 
/*      */     
/*  172 */     init();
/*  173 */     this.m_types = a_types;
/*  174 */     this.m_argTypes = a_argTypes;
/*  175 */     this.m_nodeSets = a_nodeSets;
/*  176 */     this.m_maxDepths = a_maxDepths;
/*  177 */     this.m_minDepths = a_minDepths;
/*  178 */     this.m_maxNodes = a_maxNodes;
/*  179 */     setGPPopulation(a_population);
/*  180 */     setGPConfiguration(a_configuration);
/*  181 */     this.m_variables = new Hashtable<Object, Object>();
/*  182 */     this.m_allTimeBestFitness = -1.0D;
/*      */ 
/*      */ 
/*      */     
/*  186 */     getGPConfiguration().lockSettings();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void init() {}
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static GPGenotype randomInitialGenotype(GPConfiguration a_conf, Class[] a_types, Class[][] a_argTypes, CommandGene[][] a_nodeSets, int a_maxNodes, boolean a_verboseOutput) throws InvalidConfigurationException {
/*  226 */     int[] minDepths = null;
/*  227 */     int[] maxDepths = null;
/*  228 */     return randomInitialGenotype(a_conf, a_types, a_argTypes, a_nodeSets, minDepths, maxDepths, a_maxNodes, a_verboseOutput);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static GPGenotype randomInitialGenotype(GPConfiguration a_conf, Class[] a_types, Class[][] a_argTypes, CommandGene[][] a_nodeSets, int[] a_minDepths, int[] a_maxDepths, int a_maxNodes, boolean a_verboseOutput) throws InvalidConfigurationException {
/*  272 */     boolean[] fullModeAllowed = new boolean[a_types.length];
/*  273 */     for (int i = 0; i < a_types.length; i++) {
/*  274 */       fullModeAllowed[i] = true;
/*      */     }
/*  276 */     return randomInitialGenotype(a_conf, a_types, a_argTypes, a_nodeSets, a_minDepths, a_maxDepths, a_maxNodes, fullModeAllowed, a_verboseOutput);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static GPGenotype randomInitialGenotype(GPConfiguration a_conf, Class[] a_types, Class[][] a_argTypes, CommandGene[][] a_nodeSets, int[] a_minDepths, int[] a_maxDepths, int a_maxNodes, boolean[] a_fullModeAllowed, boolean a_verboseOutput) throws InvalidConfigurationException {
/*  319 */     return randomInitialGenotype(a_conf, a_types, a_argTypes, a_nodeSets, a_minDepths, a_maxDepths, a_maxNodes, a_fullModeAllowed, a_verboseOutput, new DefaultPopulationCreator());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static GPGenotype randomInitialGenotype(GPConfiguration a_conf, Class[] a_types, Class[][] a_argTypes, CommandGene[][] a_nodeSets, int[] a_minDepths, int[] a_maxDepths, int a_maxNodes, boolean[] a_fullModeAllowed, boolean a_verboseOutput, IPopulationCreator a_popCreator) throws InvalidConfigurationException {
/*  367 */     if (a_argTypes.length != a_fullModeAllowed.length || (a_minDepths != null && a_argTypes.length != a_minDepths.length) || (a_maxDepths != null && a_argTypes.length != a_maxDepths.length) || a_argTypes.length != a_types.length)
/*      */     {
/*      */ 
/*      */       
/*  371 */       throw new IllegalArgumentException("a_argTypes must have same length as a_types, a_minDepths, a_maxDepths and a_fullModeAllowed");
/*      */     }
/*      */ 
/*      */     
/*  375 */     if (a_conf.getPopulationSize() < 1) {
/*  376 */       throw new IllegalArgumentException("Set the population size in the configuration!");
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*  381 */     System.gc();
/*  382 */     if (a_verboseOutput) {
/*  383 */       LOGGER.info("Creating initial population");
/*  384 */       LOGGER.info("Mem free: " + SystemKit.niceMemory(SystemKit.getTotalMemoryMB()) + " MB");
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/*  389 */     GPPopulation pop = new GPPopulation(a_conf, a_conf.getPopulationSize());
/*      */     try {
/*  391 */       a_popCreator.initialize(pop, a_types, a_argTypes, a_nodeSets, a_minDepths, a_maxDepths, a_maxNodes, a_fullModeAllowed);
/*      */     }
/*  393 */     catch (Exception ex) {
/*  394 */       throw new InvalidConfigurationException(ex);
/*      */     } 
/*  396 */     System.gc();
/*  397 */     if (a_verboseOutput) {
/*  398 */       LOGGER.info("Mem free after creating population: " + SystemKit.niceMemory(SystemKit.getTotalMemoryMB()) + " MB");
/*      */     }
/*      */     
/*  401 */     GPGenotype gp = new GPGenotype(a_conf, pop, a_types, a_argTypes, a_nodeSets, a_minDepths, a_maxDepths, a_maxNodes);
/*      */     
/*  403 */     gp.m_fullModeAllowed = a_fullModeAllowed;
/*      */ 
/*      */     
/*  406 */     Iterator<String> it = gp.m_variables.keySet().iterator();
/*  407 */     while (it.hasNext()) {
/*      */       
/*  409 */       String varName = it.next();
/*  410 */       Variable var = (Variable)gp.m_variables.get(varName);
/*  411 */       a_conf.putVariable(var);
/*      */     } 
/*  413 */     return gp;
/*      */   }
/*      */   
/*      */   public GPConfiguration getGPConfiguration() {
/*  417 */     return this.m_configuration;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static GPConfiguration getStaticGPConfiguration() {
/*  427 */     return m_staticConfiguration;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void setStaticGPConfiguration(GPConfiguration a_configuration) {
/*  439 */     m_staticConfiguration = a_configuration;
/*      */   }
/*      */   
/*      */   static class GPFitnessComparator
/*      */     implements Comparator {
/*      */     public int compare(Object o1, Object o2) {
/*  445 */       if (!(o1 instanceof IGPProgram) || !(o2 instanceof IGPProgram))
/*      */       {
/*  447 */         throw new ClassCastException("FitnessComparator must operate on IGPProgram instances");
/*      */       }
/*  449 */       double f1 = ((IGPProgram)o1).getFitnessValue();
/*  450 */       double f2 = ((IGPProgram)o2).getFitnessValue();
/*  451 */       if (f1 > f2) {
/*  452 */         return 1;
/*      */       }
/*  454 */       if (Math.abs(f1 - f2) < 1.0E-6D) {
/*  455 */         return 0;
/*      */       }
/*      */       
/*  458 */       return -1;
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void evolve(int a_evolutions) {
/*  472 */     int evolutions, offset = getGPConfiguration().getGenerationNr();
/*      */     
/*  474 */     if (a_evolutions < 0) {
/*  475 */       evolutions = Integer.MAX_VALUE;
/*      */     } else {
/*      */       
/*  478 */       evolutions = a_evolutions;
/*      */     } 
/*      */     
/*  481 */     for (int i = 0; i < evolutions; i++) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  487 */       if (this.m_verbose && 
/*  488 */         i % 25 == 0) {
/*  489 */         String freeMB = SystemKit.niceMemory(SystemKit.getFreeMemoryMB());
/*  490 */         LOGGER.info("Evolving generation " + (i + offset) + ", memory free: " + freeMB + " MB");
/*      */       } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  497 */       evolve();
/*  498 */       calcFitness();
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void calcFitness() {
/*  510 */     double totalFitness = 0.0D;
/*  511 */     GPPopulation pop = getGPPopulation();
/*  512 */     IGPProgram best = null;
/*  513 */     IGPFitnessEvaluator evaluator = getGPConfiguration().getGPFitnessEvaluator();
/*  514 */     this.m_bestFitness = -1.0D;
/*  515 */     for (int i = 0; i < pop.size() && pop.getGPProgram(i) != null; i++) {
/*  516 */       double d; IGPProgram program = pop.getGPProgram(i);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       try {
/*  523 */         d = program.getFitnessValue();
/*      */       }
/*  525 */       catch (IllegalStateException iex) {
/*  526 */         d = Double.NaN;
/*      */       } 
/*      */ 
/*      */       
/*  530 */       if (!Double.isInfinite(d) && !Double.isNaN(d)) {
/*      */ 
/*      */         
/*  533 */         if (best == null || evaluator.isFitter(d, this.m_bestFitness)) {
/*  534 */           best = program;
/*  535 */           this.m_bestFitness = d;
/*      */         } 
/*      */         
/*  538 */         totalFitness += d;
/*      */       } 
/*  540 */     }  this.m_totalFitness = totalFitness;
/*      */ 
/*      */ 
/*      */     
/*  544 */     if (this.m_allTimeBest == null || evaluator.isFitter(this.m_bestFitness, this.m_allTimeBestFitness)) {
/*      */       
/*  546 */       pop.setChanged(true);
/*      */       try {
/*  548 */         ICloneHandler cloner = getGPConfiguration().getJGAPFactory().getCloneHandlerFor(best, null);
/*      */         
/*  550 */         if (cloner == null) {
/*  551 */           this.m_allTimeBest = best;
/*  552 */           if (!this.m_cloneWarningGPProgramShown) {
/*  553 */             LOGGER.info("Warning: cannot clone instance of " + best.getClass());
/*      */             
/*  555 */             this.m_cloneWarningGPProgramShown = true;
/*      */           } 
/*      */         } else {
/*      */           
/*  559 */           this.m_allTimeBest = (IGPProgram)cloner.perform(best, null, null);
/*      */         }
/*      */       
/*  562 */       } catch (Exception ex) {
/*  563 */         this.m_allTimeBest = best;
/*  564 */         ex.printStackTrace();
/*      */       } 
/*  566 */       this.m_allTimeBestFitness = this.m_bestFitness;
/*      */ 
/*      */ 
/*      */       
/*      */       try {
/*  571 */         getGPConfiguration().getEventManager().fireGeneticEvent(new GeneticEvent("gpgenotype_best_solution", this));
/*      */       }
/*  573 */       catch (IllegalArgumentException iex) {}
/*      */ 
/*      */       
/*  576 */       if (this.m_verbose)
/*      */       {
/*      */         
/*  579 */         outputSolution(this.m_allTimeBest);
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public IGPProgram getAllTimeBest() {
/*  591 */     return this.m_allTimeBest;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void outputSolution(IGPProgram a_best) {
/*  603 */     if (a_best == null) {
/*  604 */       LOGGER.info("No best solution (null");
/*      */       return;
/*      */     } 
/*  607 */     double bestValue = a_best.getFitnessValue();
/*  608 */     if (Double.isInfinite(bestValue)) {
/*  609 */       LOGGER.info("No best solution (infinite)");
/*      */       return;
/*      */     } 
/*  612 */     LOGGER.info("Best solution fitness: " + bestValue);
/*  613 */     LOGGER.info("Best solution: " + a_best.toStringNorm(0));
/*  614 */     String depths = "";
/*  615 */     int size = a_best.size();
/*  616 */     for (int i = 0; i < size; i++) {
/*  617 */       if (i > 0) {
/*  618 */         depths = depths + " / ";
/*      */       }
/*  620 */       depths = depths + a_best.getChromosome(i).getDepth(0);
/*      */     } 
/*  622 */     if (size == 1) {
/*  623 */       LOGGER.info("Depth of chrom: " + depths);
/*      */     } else {
/*      */       
/*  626 */       LOGGER.info("Depths of chroms: " + depths);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void evolve() {
/*      */     try {
/*  640 */       int popSize = getGPConfiguration().getPopulationSize();
/*  641 */       GPPopulation oldPop = getGPPopulation();
/*  642 */       GPPopulation newPopulation = new GPPopulation(oldPop, false);
/*  643 */       if (this.m_fittestToAdd != null) {
/*  644 */         newPopulation.addFittestProgram(this.m_fittestToAdd);
/*  645 */         this.m_fittestToAdd = null;
/*      */       } 
/*      */       
/*  648 */       RandomGenerator random = getGPConfiguration().getRandomGenerator();
/*  649 */       GPConfiguration conf = getGPConfiguration();
/*      */ 
/*      */       
/*  652 */       int popSize1 = (int)Math.round(popSize * (1.0D - conf.getNewChromsPercent()));
/*  653 */       double crossProb = conf.getCrossoverProb() / (conf.getCrossoverProb() + conf.getReproductionProb());
/*      */       
/*  655 */       int crossover = 0;
/*  656 */       int reproduction = 0;
/*  657 */       int creation = 0; int i;
/*  658 */       for (i = 0; i < popSize1; i++) {
/*      */ 
/*      */         
/*  661 */         getGPConfiguration().clearStack();
/*  662 */         float val = random.nextFloat();
/*      */ 
/*      */ 
/*      */         
/*  666 */         if (i < popSize - 1 && val < crossProb) {
/*  667 */           crossover++;
/*      */ 
/*      */           
/*  670 */           IGPProgram i1 = conf.getSelectionMethod().select(this);
/*      */           
/*  672 */           IGPProgram i2 = conf.getSelectionMethod().select(this);
/*      */           
/*  674 */           int tries = 0;
/*      */           while (true) {
/*      */             try {
/*  677 */               IGPProgram[] newIndividuals = conf.getCrossMethod().operate(i1, i2);
/*      */               
/*  679 */               newPopulation.setGPProgram(i, newIndividuals[0]);
/*  680 */               newPopulation.setGPProgram(i + 1, newIndividuals[1]);
/*  681 */               i++;
/*      */               
/*      */               break;
/*  684 */             } catch (IllegalStateException iex) {
/*  685 */               tries++;
/*  686 */               if (tries >= getGPConfiguration().getProgramCreationMaxtries() && 
/*  687 */                 !getGPConfiguration().isMaxNodeWarningPrinted()) {
/*  688 */                 LOGGER.error("Warning: Maximum number of nodes allowed may be too small");
/*      */                 
/*  690 */                 getGPConfiguration().flagMaxNodeWarningPrinted();
/*      */ 
/*      */                 
/*  693 */                 IGPProgram program = cloneProgram(getGPConfiguration().getPrototypeProgram());
/*      */                 
/*  695 */                 if (program != null) {
/*  696 */                   newPopulation.setGPProgram(i++, program);
/*  697 */                   program = cloneProgram(getGPConfiguration().getPrototypeProgram());
/*      */                   
/*  699 */                   newPopulation.setGPProgram(i, program);
/*      */                   
/*      */                   break;
/*      */                 } 
/*  703 */                 throw new IllegalStateException(iex.getMessage());
/*      */               
/*      */               }
/*      */             
/*      */             }
/*      */           
/*      */           }
/*      */         
/*      */         }
/*      */         else {
/*      */           
/*  714 */           reproduction++;
/*  715 */           newPopulation.setGPProgram(i, conf.getSelectionMethod().select(this));
/*      */         } 
/*      */       } 
/*      */ 
/*      */       
/*  720 */       for (i = popSize1; i < popSize; i++) {
/*  721 */         creation++;
/*      */ 
/*      */         
/*  724 */         int depth = conf.getMinInitDepth() + random.nextInt(conf.getMaxInitDepth() - conf.getMinInitDepth() + 1);
/*      */ 
/*      */         
/*  727 */         int tries = 0;
/*      */ 
/*      */ 
/*      */         
/*      */         while (true) {
/*      */           try {
/*  733 */             IGPProgram program = newPopulation.create(i, this.m_types, this.m_argTypes, this.m_nodeSets, this.m_minDepths, this.m_maxDepths, depth, (i % 2 == 0), this.m_maxNodes, this.m_fullModeAllowed, tries);
/*      */ 
/*      */             
/*  736 */             newPopulation.setGPProgram(i, program);
/*  737 */             LOGGER.debug("Added new GP program (depth " + depth + ", " + tries + " tries)");
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */             
/*      */             break;
/*  744 */           } catch (IllegalStateException iex) {
/*      */ 
/*      */ 
/*      */ 
/*      */             
/*  749 */             tries++;
/*  750 */             if (tries > getGPConfiguration().getProgramCreationMaxtries()) {
/*  751 */               LOGGER.debug("Creating random GP program failed (depth " + depth + ", " + tries + " tries), will use prototype");
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */               
/*  759 */               IGPProgram program = cloneProgram(getGPConfiguration().getPrototypeProgram());
/*      */               
/*  761 */               if (program != null) {
/*      */ 
/*      */                 
/*  764 */                 newPopulation.setGPProgram(i, program);
/*      */                 
/*      */                 break;
/*      */               } 
/*  768 */               throw new IllegalStateException(iex.getMessage());
/*      */             } 
/*      */           } 
/*      */         } 
/*      */       } 
/*      */ 
/*      */       
/*  775 */       LOGGER.debug("Did " + crossover + " x-overs, " + reproduction + " reproductions, " + creation + " creations");
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  781 */       setGPPopulation(newPopulation);
/*      */ 
/*      */       
/*  784 */       conf.incrementGenerationNr();
/*      */ 
/*      */       
/*  787 */       conf.getEventManager().fireGeneticEvent(new GeneticEvent("gpgenotype_evolved_event", this));
/*      */     
/*      */     }
/*  790 */     catch (InvalidConfigurationException iex) {
/*      */ 
/*      */       
/*  793 */       throw new IllegalStateException(iex.getMessage());
/*      */     } 
/*      */   }
/*      */   
/*      */   public GPPopulation getGPPopulation() {
/*  798 */     return this.m_population;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public double getTotalFitness() {
/*  808 */     return this.m_totalFitness;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void run() {
/*      */     try {
/*      */       while (true) {
/*  819 */         Thread.currentThread(); if (!Thread.interrupted()) {
/*  820 */           evolve();
/*  821 */           calcFitness();
/*      */ 
/*      */           
/*  824 */           Thread.sleep(10L); continue;
/*      */         }  break;
/*      */       } 
/*  827 */     } catch (Exception ex) {
/*  828 */       ex.printStackTrace();
/*  829 */       System.exit(1);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized IGPProgram getFittestProgram() {
/*      */     double fittest;
/*  845 */     if (this.m_allTimeBest != null) {
/*  846 */       fittest = this.m_allTimeBest.getFitnessValue();
/*      */     } else {
/*      */       
/*  849 */       fittest = -1.0D;
/*      */     } 
/*  851 */     IGPProgram fittestPop = getGPPopulation().determineFittestProgram();
/*  852 */     if (fittestPop == null) {
/*  853 */       return this.m_allTimeBest;
/*      */     }
/*  855 */     if (getGPConfiguration().getGPFitnessEvaluator().isFitter(fittest, fittestPop.getFitnessValue()))
/*      */     {
/*  857 */       return this.m_allTimeBest;
/*      */     }
/*      */ 
/*      */     
/*  861 */     return fittestPop;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized IGPProgram getFittestProgramComputed() {
/*  877 */     return getGPPopulation().determineFittestProgramComputed();
/*      */   }
/*      */   
/*      */   protected void setGPPopulation(GPPopulation a_pop) {
/*  881 */     this.m_population = a_pop;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setGPConfiguration(GPConfiguration a_configuration) {
/*  892 */     this.m_configuration = a_configuration;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean equals(Object a_other) {
/*      */     try {
/*  906 */       return (compareTo(a_other) == 0);
/*      */     }
/*  908 */     catch (ClassCastException cex) {
/*  909 */       return false;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int compareTo(Object a_other) {
/*      */     try {
/*  933 */       if (a_other == null) {
/*  934 */         return 1;
/*      */       }
/*  936 */       GPGenotype otherGenotype = (GPGenotype)a_other;
/*      */ 
/*      */ 
/*      */       
/*  940 */       int size1 = getGPPopulation().size();
/*  941 */       int size2 = otherGenotype.getGPPopulation().size();
/*  942 */       if (size1 != size2) {
/*  943 */         if (size1 > size2) {
/*  944 */           return 1;
/*      */         }
/*      */         
/*  947 */         return -1;
/*      */       } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  957 */       Arrays.sort((Object[])getGPPopulation().getGPPrograms());
/*  958 */       Arrays.sort((Object[])otherGenotype.getGPPopulation().getGPPrograms());
/*  959 */       for (int i = 0; i < getGPPopulation().size(); i++) {
/*  960 */         int result = getGPPopulation().getGPProgram(i).compareTo(otherGenotype.getGPPopulation().getGPProgram(i));
/*      */         
/*  962 */         if (result != 0) {
/*  963 */           return result;
/*      */         }
/*      */       } 
/*  966 */       return 0;
/*      */     }
/*  968 */     catch (ClassCastException e) {
/*  969 */       return -1;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int hashCode() {
/*  992 */     int size = getGPPopulation().size();
/*      */     
/*  994 */     int twopower = 1;
/*      */ 
/*      */ 
/*      */     
/*  998 */     int localHashCode = -573;
/*  999 */     for (int i = 0; i < size; i++, twopower = 2 * twopower) {
/* 1000 */       IGPProgram prog = getGPPopulation().getGPProgram(i);
/* 1001 */       localHashCode = 31 * localHashCode + prog.hashCode();
/*      */     } 
/* 1003 */     return localHashCode;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setVerboseOutput(boolean a_verbose) {
/* 1013 */     this.m_verbose = a_verbose;
/*      */   }
/*      */   
/*      */   private IGPProgram cloneProgram(IGPProgram a_original) {
/* 1017 */     IGPProgram validProgram = getGPConfiguration().getPrototypeProgram();
/*      */     
/* 1019 */     ICloneHandler cloner = getGPConfiguration().getJGAPFactory().getCloneHandlerFor(validProgram, null);
/*      */     
/* 1021 */     if (cloner != null) {
/*      */       try {
/* 1023 */         IGPProgram program = (IGPProgram)cloner.perform(validProgram, null, null);
/*      */         
/* 1025 */         return program;
/*      */       }
/* 1027 */       catch (Exception ex) {
/* 1028 */         LOGGER.error(ex.getMessage(), ex);
/* 1029 */         return null;
/*      */       } 
/*      */     }
/* 1032 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void putVariable(Variable a_var) {
/* 1044 */     this.m_variables.put(a_var.getName(), a_var);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Variable getVariable(String a_varName) {
/* 1055 */     return (Variable)this.m_variables.get(a_varName);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void addFittestProgram(IGPProgram a_toAdd) {
/* 1068 */     if (a_toAdd != null) {
/* 1069 */       this.m_fittestToAdd = a_toAdd;
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void fillPopulation(int a_num) throws InvalidConfigurationException {
/* 1084 */     IGPProgram sampleProg = getGPConfiguration().getPrototypeProgram();
/* 1085 */     if (sampleProg == null);
/*      */ 
/*      */     
/* 1088 */     Class<?> sampleClass = sampleProg.getClass();
/* 1089 */     IInitializer chromIniter = getGPConfiguration().getJGAPFactory().getInitializerFor(sampleProg, sampleClass);
/*      */     
/* 1091 */     if (chromIniter == null) {
/* 1092 */       throw new InvalidConfigurationException("No initializer found for class " + sampleClass);
/*      */     }
/*      */     
/*      */     try {
/* 1096 */       for (int i = 0; i < a_num; i++);
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     }
/* 1102 */     catch (Exception ex) {
/* 1103 */       throw new IllegalStateException(ex);
/*      */     } 
/*      */   }
/*      */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\gp\impl\GPGenotype.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */