/*     */ package org.jgap.gp.impl;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.util.Arrays;
/*     */ import java.util.Comparator;
/*     */ import java.util.List;
/*     */ import java.util.Random;
/*     */ import org.apache.log4j.Logger;
/*     */ import org.jgap.ICloneHandler;
/*     */ import org.jgap.IJGAPFactory;
/*     */ import org.jgap.InvalidConfigurationException;
/*     */ import org.jgap.gp.CommandGene;
/*     */ import org.jgap.gp.GPProgramBase;
/*     */ import org.jgap.gp.IGPFitnessEvaluator;
/*     */ import org.jgap.gp.IGPProgram;
/*     */ import org.jgap.gp.IProgramCreator;
/*     */ import org.jgap.util.StringKit;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class GPPopulation
/*     */   implements Serializable, Comparable
/*     */ {
/*     */   private static final String CVS_REVISION = "$Revision: 1.31 $";
/*     */   static final String GPPROGRAM_DELIMITER_HEADING = "<";
/*     */   static final String GPPROGRAM_DELIMITER_CLOSING = ">";
/*     */   static final String GPPROGRAM_DELIMITER = "#";
/*  34 */   private transient Logger LOGGER = Logger.getLogger(GPPopulation.class);
/*     */ 
/*     */ 
/*     */   
/*     */   private IGPProgram[] m_programs;
/*     */ 
/*     */ 
/*     */   
/*     */   private transient float[] m_fitnessRank;
/*     */ 
/*     */ 
/*     */   
/*     */   private int m_popSize;
/*     */ 
/*     */ 
/*     */   
/*     */   private transient IGPProgram m_fittestProgram;
/*     */ 
/*     */ 
/*     */   
/*     */   private GPConfiguration m_config;
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean m_changed;
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean m_sorted;
/*     */ 
/*     */   
/*     */   private IGPProgram m_fittestToAdd;
/*     */ 
/*     */ 
/*     */   
/*     */   public GPPopulation(GPConfiguration a_config, int a_size) throws InvalidConfigurationException {
/*  70 */     if (a_config == null) {
/*  71 */       throw new InvalidConfigurationException("Configuration must not be null!");
/*     */     }
/*  73 */     this.m_config = a_config;
/*  74 */     this.m_programs = (IGPProgram[])new GPProgram[a_size];
/*  75 */     this.m_popSize = a_size;
/*  76 */     this.m_fitnessRank = new float[a_size];
/*  77 */     for (int i = 0; i < a_size; i++) {
/*  78 */       this.m_fitnessRank[i] = 0.5F;
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
/*     */   public GPPopulation(GPPopulation a_pop) throws InvalidConfigurationException {
/*  90 */     this(a_pop, false);
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
/*     */   public GPPopulation(GPPopulation a_pop, boolean a_keepPrograms) throws InvalidConfigurationException {
/* 103 */     this.m_config = a_pop.getGPConfiguration();
/* 104 */     this.m_popSize = a_pop.getPopSize();
/* 105 */     this.m_programs = (IGPProgram[])new GPProgram[this.m_popSize];
/* 106 */     this.m_fitnessRank = new float[this.m_popSize];
/* 107 */     if (a_keepPrograms) {
/* 108 */       synchronized (this.m_programs) {
/* 109 */         for (int i = 0; i < this.m_popSize; i++) {
/* 110 */           this.m_programs[i] = a_pop.getGPProgram(i);
/* 111 */           this.m_fitnessRank[i] = a_pop.getFitnessRank(i);
/*     */         } 
/*     */       } 
/* 114 */       this.m_fittestProgram = a_pop.determineFittestProgramComputed();
/* 115 */       if (this.m_fittestProgram != null) {
/* 116 */         this.m_fittestProgram = (IGPProgram)this.m_fittestProgram.clone();
/*     */       }
/* 118 */       setChanged(a_pop.isChanged());
/* 119 */       if (!this.m_changed) {
/* 120 */         this.m_sorted = true;
/*     */       }
/*     */     } else {
/*     */       
/* 124 */       for (int i = 0; i < this.m_popSize; i++) {
/* 125 */         this.m_fitnessRank[i] = 0.5F;
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
/*     */ 
/*     */   
/*     */   public void sort(Comparator<? super IGPProgram> c) {
/* 142 */     Arrays.sort(this.m_programs, c);
/* 143 */     float f = 0.0F;
/* 144 */     for (int i = 0; i < this.m_programs.length; i++) {
/* 145 */       this.m_fitnessRank[i] = f;
/* 146 */       if (this.m_programs[i] != null) {
/* 147 */         f = (float)(f + this.m_programs[i].getFitnessValue());
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void create(Class[] a_types, Class[][] a_argTypes, CommandGene[][] a_nodeSets, int[] a_minDepths, int[] a_maxDepths, int a_maxNodes, boolean[] a_fullModeAllowed) throws InvalidConfigurationException {
/* 180 */     create(a_types, a_argTypes, a_nodeSets, a_minDepths, a_maxDepths, a_maxNodes, a_fullModeAllowed, new DefaultProgramCreator());
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
/*     */ 
/*     */   
/*     */   public void create(Class[] a_types, Class[][] a_argTypes, CommandGene[][] a_nodeSets, int[] a_minDepths, int[] a_maxDepths, int a_maxNodes, boolean[] a_fullModeAllowed, IProgramCreator a_programCreator) throws InvalidConfigurationException {
/*     */     int divisor;
/* 207 */     if (this.m_popSize < 2) {
/* 208 */       divisor = 1;
/*     */     } else {
/*     */       
/* 211 */       divisor = this.m_popSize - 1;
/*     */     } 
/* 213 */     int genNr = getGPConfiguration().getGenerationNr();
/* 214 */     int genI = (new Random()).nextInt(this.m_popSize);
/* 215 */     for (int i = 0; i < this.m_popSize; i++) {
/* 216 */       IGPProgram program = null;
/*     */ 
/*     */       
/* 219 */       int depth = 2 + (getGPConfiguration().getMaxInitDepth() - 1) * i / divisor;
/*     */ 
/*     */ 
/*     */       
/* 223 */       int tries = 0;
/*     */       while (true) {
/*     */         try {
/* 226 */           program = create(i, a_types, a_argTypes, a_nodeSets, a_minDepths, a_maxDepths, depth, (i % 2 == 0), a_maxNodes, a_fullModeAllowed, tries, a_programCreator);
/*     */ 
/*     */ 
/*     */           
/* 230 */           if (i == 0 && getGPConfiguration().getPrototypeProgram() == null) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */             
/* 237 */             getGPConfiguration().setPrototypeProgram(program); break;
/*     */           } 
/* 239 */           if (genNr % 5 == 0 && genNr > 0 && i == genI) {
/*     */             
/* 241 */             double protoFitness = getGPConfiguration().getPrototypeProgram().getFitnessValue();
/*     */             
/* 243 */             if (getGPConfiguration().getGPFitnessEvaluator().isFitter(program.getFitnessValue(), protoFitness))
/*     */             {
/* 245 */               getGPConfiguration().setPrototypeProgram(program);
/*     */             }
/*     */           } 
/*     */           
/*     */           break;
/* 250 */         } catch (IllegalStateException iex) {
/* 251 */           tries++;
/* 252 */           if (tries > getGPConfiguration().getProgramCreationMaxtries()) {
/* 253 */             IGPProgram prototype = getGPConfiguration().getPrototypeProgram();
/* 254 */             if (prototype != null) {
/* 255 */               ICloneHandler cloner = getGPConfiguration().getJGAPFactory().getCloneHandlerFor(prototype, null);
/*     */               
/* 257 */               if (cloner != null) {
/*     */                 try {
/* 259 */                   program = (IGPProgram)cloner.perform(prototype, null, null);
/* 260 */                   this.LOGGER.warn("Prototype program reused because random program did not satisfy constraints");
/*     */ 
/*     */                 
/*     */                 }
/* 264 */                 catch (Exception ex) {
/*     */ 
/*     */                   
/* 267 */                   throw iex;
/*     */                 } 
/*     */                 break;
/*     */               } 
/* 271 */               this.LOGGER.warn("Warning: no clone handler found for prototype program type " + prototype);
/*     */             } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */             
/* 278 */             throw iex;
/*     */           } 
/*     */         } 
/*     */       } 
/*     */       
/* 283 */       setGPProgram(i, program);
/*     */     } 
/* 285 */     setChanged(true);
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public IGPProgram create(Class[] a_types, Class[][] a_argTypes, CommandGene[][] a_nodeSets, int[] a_minDepths, int[] a_maxDepths, int a_depth, boolean a_grow, int a_maxNodes, boolean[] a_fullModeAllowed, int a_tries) throws InvalidConfigurationException {
/* 313 */     return create(0, a_types, a_argTypes, a_nodeSets, a_minDepths, a_maxDepths, a_depth, a_grow, a_maxNodes, a_fullModeAllowed, a_tries);
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public IGPProgram create(int a_programIndex, Class[] a_types, Class[][] a_argTypes, CommandGene[][] a_nodeSets, int[] a_minDepths, int[] a_maxDepths, int a_depth, boolean a_grow, int a_maxNodes, boolean[] a_fullModeAllowed, int a_tries) throws InvalidConfigurationException {
/* 341 */     return create(a_programIndex, a_types, a_argTypes, a_nodeSets, a_minDepths, a_maxDepths, a_depth, a_grow, a_maxNodes, a_fullModeAllowed, a_tries, new DefaultProgramCreator());
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public IGPProgram create(int a_programIndex, Class[] a_types, Class[][] a_argTypes, CommandGene[][] a_nodeSets, int[] a_minDepths, int[] a_maxDepths, int a_depth, boolean a_grow, int a_maxNodes, boolean[] a_fullModeAllowed, int a_tries, IProgramCreator a_programCreator) throws InvalidConfigurationException {
/* 387 */     if (this.m_fittestToAdd != null) {
/*     */       IGPProgram iGPProgram;
/* 389 */       ICloneHandler cloner = getGPConfiguration().getJGAPFactory().getCloneHandlerFor(this.m_fittestToAdd, null);
/*     */       
/* 391 */       if (cloner == null) {
/* 392 */         iGPProgram = this.m_fittestToAdd;
/*     */       } else {
/*     */         
/*     */         try {
/* 396 */           iGPProgram = (IGPProgram)cloner.perform(this.m_fittestToAdd, null, null);
/*     */         }
/* 398 */         catch (Exception ex) {
/* 399 */           ex.printStackTrace();
/* 400 */           iGPProgram = this.m_fittestToAdd;
/*     */         } 
/*     */       } 
/*     */ 
/*     */       
/* 405 */       this.m_fittestToAdd = null;
/* 406 */       return iGPProgram;
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 412 */     IGPProgram program = a_programCreator.create(getGPConfiguration(), a_programIndex, a_types, a_argTypes, a_nodeSets, a_minDepths, a_maxDepths, a_maxNodes, a_depth, a_grow, a_tries, a_fullModeAllowed);
/*     */ 
/*     */ 
/*     */     
/* 416 */     return program;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getPopSize() {
/* 427 */     return this.m_popSize;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public GPConfiguration getGPConfiguration() {
/* 437 */     return this.m_config;
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
/*     */   public void setGPProgram(int a_index, IGPProgram a_program) {
/* 452 */     synchronized (this.m_programs) {
/* 453 */       this.m_programs[a_index] = a_program;
/*     */     } 
/* 455 */     setChanged(true);
/*     */   }
/*     */   
/*     */   public IGPProgram getGPProgram(int a_index) {
/* 459 */     return this.m_programs[a_index];
/*     */   }
/*     */   
/*     */   public IGPProgram[] getGPPrograms() {
/* 463 */     return this.m_programs;
/*     */   }
/*     */   
/*     */   public int size() {
/* 467 */     return this.m_programs.length;
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
/*     */   public IGPProgram determineFittestProgram() {
/* 481 */     if (!this.m_changed && this.m_fittestProgram != null) {
/* 482 */       return this.m_fittestProgram;
/*     */     }
/* 484 */     double bestFitness = -1.0D;
/* 485 */     IGPFitnessEvaluator evaluator = getGPConfiguration().getGPFitnessEvaluator();
/*     */     
/* 487 */     for (int i = 0; i < this.m_programs.length && this.m_programs[i] != null; i++) {
/* 488 */       double d; IGPProgram program = this.m_programs[i];
/*     */       try {
/* 490 */         d = program.getFitnessValue();
/* 491 */       } catch (IllegalStateException iex) {
/* 492 */         d = Double.NaN;
/*     */       } 
/* 494 */       if (!Double.isNaN(d) && (
/* 495 */         this.m_fittestProgram == null || evaluator.isFitter(d, bestFitness))) {
/* 496 */         bestFitness = d;
/* 497 */         this.m_fittestProgram = program;
/*     */       } 
/*     */     } 
/*     */     
/* 501 */     setChanged(false);
/* 502 */     if (this.m_fittestProgram != null) {
/* 503 */       IJGAPFactory factory = getGPConfiguration().getJGAPFactory();
/* 504 */       if (factory == null) {
/* 505 */         throw new IllegalStateException("JGAPFactory must not be null!");
/*     */       }
/* 507 */       ICloneHandler cloner = factory.getCloneHandlerFor(this.m_fittestProgram, null);
/* 508 */       if (cloner != null) {
/*     */         try {
/* 510 */           this.m_fittestProgram = (IGPProgram)cloner.perform(this.m_fittestProgram, null, null);
/*     */         }
/* 512 */         catch (Exception ex) {}
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 517 */     return this.m_fittestProgram;
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
/*     */   public IGPProgram determineFittestProgramComputed() {
/* 530 */     double bestFitness = -1.0D;
/* 531 */     IGPFitnessEvaluator evaluator = getGPConfiguration().getGPFitnessEvaluator();
/*     */     
/* 533 */     IGPProgram fittest = null;
/* 534 */     for (int i = 0; i < this.m_programs.length && this.m_programs[i] != null; i++) {
/* 535 */       double fitness; IGPProgram program = this.m_programs[i];
/* 536 */       if (program instanceof GPProgramBase) {
/* 537 */         GPProgramBase program1 = (GPProgramBase)program;
/* 538 */         fitness = program1.getFitnessValueDirectly();
/*     */       } else {
/*     */         
/* 541 */         fitness = program.getFitnessValue();
/*     */       } 
/* 543 */       if (Math.abs(fitness - -1.0D) > 1.0E-7D)
/*     */       {
/* 545 */         if (fittest == null || evaluator.isFitter(fitness, bestFitness)) {
/* 546 */           fittest = program;
/* 547 */           bestFitness = fitness;
/*     */         } 
/*     */       }
/*     */     } 
/* 551 */     return fittest;
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
/*     */   public List determineFittestChromosomes(int a_numberOfPrograms) {
/* 567 */     int numberOfChromosomes = Math.min(a_numberOfPrograms, this.m_programs.length);
/* 568 */     if (numberOfChromosomes <= 0) {
/* 569 */       return null;
/*     */     }
/* 571 */     if (!this.m_changed && this.m_sorted) {
/* 572 */       return Arrays.<IGPProgram>asList(this.m_programs).subList(0, numberOfChromosomes);
/*     */     }
/*     */     
/* 575 */     sortByFitness();
/*     */     
/* 577 */     return Arrays.<IGPProgram>asList(this.m_programs).subList(0, numberOfChromosomes);
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
/*     */   public void sortByFitness() {
/* 591 */     sort(new GPProgramFitnessComparator(getGPConfiguration().getGPFitnessEvaluator()));
/*     */     
/* 593 */     setChanged(false);
/* 594 */     setSorted(true);
/* 595 */     this.m_fittestProgram = this.m_programs[0];
/*     */   }
/*     */   
/*     */   public float[] getFitnessRanks() {
/* 599 */     return this.m_fitnessRank;
/*     */   }
/*     */   
/*     */   public float getFitnessRank(int a_index) {
/* 603 */     return this.m_fitnessRank[a_index];
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
/*     */   protected void setChanged(boolean a_changed) {
/* 616 */     this.m_changed = a_changed;
/* 617 */     setSorted(false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isChanged() {
/* 627 */     return this.m_changed;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void setSorted(boolean a_sorted) {
/* 638 */     this.m_sorted = a_sorted;
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
/*     */   public int compareTo(Object a_pop) {
/* 655 */     GPPopulation other = (GPPopulation)a_pop;
/* 656 */     if (a_pop == null) {
/* 657 */       return 1;
/*     */     }
/* 659 */     int size1 = size();
/* 660 */     int size2 = other.size();
/* 661 */     if (size1 != size2) {
/* 662 */       if (size1 < size2) {
/* 663 */         return -1;
/*     */       }
/*     */       
/* 666 */       return 1;
/*     */     } 
/*     */     
/* 669 */     IGPProgram[] progs2 = other.getGPPrograms();
/* 670 */     for (int i = 0; i < size1; i++) {
/* 671 */       if (!containedInArray(progs2, this.m_programs[i])) {
/* 672 */         return 1;
/*     */       }
/*     */     } 
/* 675 */     return 0;
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
/*     */   protected boolean containedInArray(IGPProgram[] a_progs, IGPProgram a_prog) {
/* 690 */     for (int i = 0; i < a_progs.length; i++) {
/* 691 */       if (a_progs[i] == null) {
/* 692 */         return false;
/*     */       }
/* 694 */       if (a_progs[i].equals(a_prog)) {
/* 695 */         return true;
/*     */       }
/*     */     } 
/* 698 */     return false;
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
/*     */   public boolean equals(Object a_pop) {
/*     */     try {
/* 712 */       return (compareTo(a_pop) == 0);
/*     */     }
/* 714 */     catch (ClassCastException e) {
/*     */ 
/*     */ 
/*     */       
/* 718 */       return false;
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
/*     */   public void addFittestProgram(IGPProgram a_toAdd) {
/* 732 */     if (a_toAdd != null) {
/* 733 */       this.m_fittestToAdd = a_toAdd;
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
/*     */   public void clear() {
/* 746 */     for (int i = 0; i < this.m_programs.length; i++) {
/* 747 */       this.m_programs[i] = null;
/*     */     }
/* 749 */     this.m_changed = true;
/* 750 */     this.m_sorted = true;
/* 751 */     this.m_fittestProgram = null;
/*     */   }
/*     */   
/*     */   public boolean isFirstEmpty() {
/* 755 */     if (size() < 1) {
/* 756 */       return true;
/*     */     }
/* 758 */     if (this.m_programs[0] == null) {
/* 759 */       return true;
/*     */     }
/* 761 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getPersistentRepresentation() {
/* 772 */     StringBuffer b = new StringBuffer();
/* 773 */     for (IGPProgram program : this.m_programs) {
/* 774 */       b.append("<");
/* 775 */       b.append(encode(program.getClass().getName() + "#" + program.getPersistentRepresentation()));
/*     */ 
/*     */ 
/*     */       
/* 779 */       b.append(">");
/*     */     } 
/* 781 */     return b.toString();
/*     */   }
/*     */   
/*     */   protected String encode(String a_string) {
/* 785 */     return StringKit.encode(a_string);
/*     */   }
/*     */   
/*     */   protected String decode(String a_string) {
/* 789 */     return StringKit.decode(a_string);
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\gp\impl\GPPopulation.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */