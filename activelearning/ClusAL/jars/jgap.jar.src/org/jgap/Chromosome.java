/*      */ package org.jgap;
/*      */ 
/*      */ import java.util.List;
/*      */ import java.util.Vector;
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
/*      */ public class Chromosome
/*      */   extends BaseChromosome
/*      */ {
/*      */   private static final String CVS_REVISION = "$Revision: 1.95 $";
/*      */   private Object m_applicationData;
/*      */   private List m_multiObjective;
/*      */   private boolean m_isSelectedForNextGeneration;
/*  100 */   protected double m_fitnessValue = -1.0D;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean m_compareAppData;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private IGeneConstraintChecker m_geneAlleleChecker;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean m_alwaysCalculate;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Chromosome() throws InvalidConfigurationException {
/*  141 */     this(Genotype.getStaticConfiguration());
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
/*      */   public Chromosome(Configuration a_configuration) throws InvalidConfigurationException {
/*  155 */     super(a_configuration);
/*  156 */     this.m_alwaysCalculate = a_configuration.isAlwaysCalculateFitness();
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
/*      */   public Chromosome(Configuration a_configuration, String a_persistentRepresentatuion) throws InvalidConfigurationException, UnsupportedRepresentationException {
/*  174 */     this(a_configuration);
/*  175 */     setValueFromPersistentRepresentation(a_persistentRepresentatuion);
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
/*      */   public Chromosome(Configuration a_configuration, int a_desiredSize) throws InvalidConfigurationException {
/*  191 */     this(a_configuration);
/*  192 */     if (a_desiredSize <= 0) {
/*  193 */       throw new IllegalArgumentException("Chromosome size must be greater than zero");
/*      */     }
/*      */     
/*  196 */     setGenes(new Gene[a_desiredSize]);
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
/*      */   public Chromosome(Configuration a_configuration, Gene a_sampleGene, int a_desiredSize) throws InvalidConfigurationException {
/*  220 */     this(a_configuration, a_desiredSize);
/*  221 */     initFromGene(a_sampleGene);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Chromosome(Configuration a_configuration, Gene a_sampleGene, int a_desiredSize, IGeneConstraintChecker a_constraintChecker) throws InvalidConfigurationException {
/*  228 */     this(a_configuration, a_desiredSize);
/*  229 */     initFromGene(a_sampleGene);
/*  230 */     setConstraintChecker(a_constraintChecker);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void initFromGene(Gene a_sampleGene) {
/*  237 */     if (a_sampleGene == null) {
/*  238 */       throw new IllegalArgumentException("Sample Gene cannot be null.");
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  244 */     int size = size();
/*  245 */     for (int i = 0; i < size; i++) {
/*  246 */       setGene(i, a_sampleGene.newGene());
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
/*      */   public Chromosome(Configuration a_configuration, Gene[] a_initialGenes) throws InvalidConfigurationException {
/*  264 */     this(a_configuration, (a_initialGenes == null) ? 0 : a_initialGenes.length);
/*  265 */     checkGenes(a_initialGenes);
/*  266 */     setGenes(a_initialGenes);
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
/*      */   public Chromosome(Configuration a_configuration, Gene[] a_initialGenes, IGeneConstraintChecker a_constraintChecker) throws InvalidConfigurationException {
/*  288 */     this(a_configuration, a_initialGenes.length);
/*  289 */     checkGenes(a_initialGenes);
/*  290 */     setGenes(a_initialGenes);
/*  291 */     setConstraintChecker(a_constraintChecker);
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
/*      */   protected void checkGenes(Gene[] a_initialGenes) {
/*  307 */     for (int i = 0; i < a_initialGenes.length; i++) {
/*  308 */       if (a_initialGenes[i] == null) {
/*  309 */         throw new IllegalArgumentException("The gene at index " + i + " in the given array of " + "genes was found to be null. No gene in the array " + "may be null.");
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
/*      */   public synchronized Object clone() {
/*  337 */     if (getConfiguration() == null) {
/*  338 */       throw new IllegalStateException("The active Configuration object must be set on this Chromosome prior to invocation of the clone() method.");
/*      */     }
/*      */ 
/*      */     
/*  342 */     IChromosome copy = null;
/*      */ 
/*      */ 
/*      */     
/*  346 */     IChromosomePool pool = getConfiguration().getChromosomePool();
/*  347 */     if (pool != null) {
/*  348 */       copy = pool.acquireChromosome();
/*  349 */       if (copy != null) {
/*  350 */         Gene[] genes = copy.getGenes();
/*  351 */         for (int i = 0; i < size(); i++) {
/*  352 */           genes[i].setAllele(getGene(i).getAllele());
/*      */         }
/*      */       } 
/*      */     } 
/*      */     try {
/*  357 */       if (copy == null) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  363 */         int size = size();
/*  364 */         if (size > 0) {
/*  365 */           Gene[] copyOfGenes = new Gene[size];
/*  366 */           for (int i = 0; i < copyOfGenes.length; i++) {
/*  367 */             copyOfGenes[i] = getGene(i).newGene();
/*  368 */             copyOfGenes[i].setAllele(getGene(i).getAllele());
/*      */           } 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*  374 */           copy = new Chromosome(getConfiguration(), copyOfGenes);
/*      */         } else {
/*      */           
/*  377 */           copy = new Chromosome(getConfiguration());
/*      */         } 
/*  379 */         copy.setFitnessValue(this.m_fitnessValue);
/*      */       } 
/*      */ 
/*      */       
/*  383 */       copy.setConstraintChecker(getConstraintChecker());
/*      */     }
/*  385 */     catch (InvalidConfigurationException iex) {
/*  386 */       throw new IllegalStateException(iex.getMessage());
/*      */     } 
/*      */ 
/*      */     
/*      */     try {
/*  391 */       copy.setApplicationData(cloneObject(getApplicationData()));
/*      */     }
/*  393 */     catch (Exception ex) {
/*  394 */       throw new IllegalStateException(ex.getMessage());
/*      */     } 
/*  396 */     return copy;
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
/*      */   protected Object cloneObject(Object a_object) throws Exception {
/*  412 */     if (a_object == null) {
/*  413 */       return null;
/*      */     }
/*      */ 
/*      */     
/*  417 */     ICloneHandler cloner = getConfiguration().getJGAPFactory().getCloneHandlerFor(a_object, a_object.getClass());
/*      */     
/*  419 */     if (cloner != null) {
/*  420 */       return cloner.perform(a_object, null, this);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*  425 */     return a_object;
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
/*      */   public double getFitnessValue() {
/*  446 */     if (this.m_fitnessValue >= 0.0D && !this.m_alwaysCalculate) {
/*  447 */       return this.m_fitnessValue;
/*      */     }
/*      */     
/*  450 */     return calcFitnessValue();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public double getFitnessValueDirectly() {
/*  461 */     return this.m_fitnessValue;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected double calcFitnessValue() {
/*  472 */     if (getConfiguration() != null) {
/*  473 */       FitnessFunction normalFitnessFunction = getConfiguration().getFitnessFunction();
/*      */       
/*  475 */       if (normalFitnessFunction != null)
/*      */       {
/*      */ 
/*      */         
/*  479 */         this.m_fitnessValue = normalFitnessFunction.getFitnessValue(this);
/*      */       }
/*      */     } 
/*  482 */     return this.m_fitnessValue;
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
/*      */   public void setFitnessValue(double a_newFitnessValue) {
/*  497 */     if (a_newFitnessValue >= 0.0D && Math.abs(this.m_fitnessValue - a_newFitnessValue) > 1.0E-7D)
/*      */     {
/*  499 */       this.m_fitnessValue = a_newFitnessValue;
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
/*      */   public void setFitnessValueDirectly(double a_newFitnessValue) {
/*  515 */     this.m_fitnessValue = a_newFitnessValue;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String toString() {
/*      */     String appData;
/*  527 */     StringBuffer representation = new StringBuffer();
/*  528 */     representation.append("Size:" + size());
/*      */ 
/*      */ 
/*      */     
/*  532 */     representation.append(", Fitness value:" + this.m_fitnessValue);
/*  533 */     representation.append(", Alleles:");
/*  534 */     representation.append("[");
/*      */ 
/*      */     
/*  537 */     int size = size();
/*  538 */     for (int i = 0; i < size; i++) {
/*  539 */       if (i > 0) {
/*  540 */         representation.append(", ");
/*      */       }
/*  542 */       if (getGene(i) == null) {
/*  543 */         representation.append("null");
/*      */       } else {
/*      */         
/*  546 */         representation.append(getGene(i).toString());
/*      */       } 
/*      */     } 
/*  549 */     representation.append("]");
/*      */     
/*  551 */     if (getApplicationData() != null) {
/*  552 */       appData = getApplicationData().toString();
/*      */     } else {
/*      */       
/*  555 */       appData = "null";
/*      */     } 
/*  557 */     representation.append(", Application data:" + appData);
/*  558 */     return representation.toString();
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
/*      */   public static IChromosome randomInitialChromosome(Configuration a_configuration) throws InvalidConfigurationException {
/*  585 */     if (a_configuration == null) {
/*  586 */       throw new IllegalArgumentException("Configuration instance must not be null");
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  592 */     a_configuration.lockSettings();
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  597 */     IChromosomePool pool = a_configuration.getChromosomePool();
/*  598 */     if (pool != null) {
/*  599 */       IChromosome randomChromosome = pool.acquireChromosome();
/*  600 */       if (randomChromosome != null) {
/*  601 */         Gene[] genes = randomChromosome.getGenes();
/*  602 */         RandomGenerator randomGenerator = a_configuration.getRandomGenerator();
/*  603 */         for (int j = 0; j < genes.length; j++) {
/*  604 */           genes[j].setToRandomValue(randomGenerator);
/*      */         }
/*      */         
/*  607 */         randomChromosome.setFitnessValueDirectly(-1.0D);
/*      */         
/*  609 */         return randomChromosome;
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/*  615 */     IChromosome sampleChromosome = a_configuration.getSampleChromosome();
/*      */     
/*  617 */     sampleChromosome.setFitnessValue(-1.0D);
/*  618 */     Gene[] sampleGenes = sampleChromosome.getGenes();
/*  619 */     Gene[] newGenes = new Gene[sampleGenes.length];
/*  620 */     RandomGenerator generator = a_configuration.getRandomGenerator();
/*  621 */     for (int i = 0; i < newGenes.length; i++) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  628 */       newGenes[i] = sampleGenes[i].newGene();
/*      */ 
/*      */       
/*  631 */       newGenes[i].setToRandomValue(generator);
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  637 */     return new Chromosome(a_configuration, newGenes);
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
/*      */   public boolean equals(Object other) {
/*      */     try {
/*  665 */       return (compareTo(other) == 0);
/*      */     }
/*  667 */     catch (ClassCastException cex) {
/*  668 */       return false;
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
/*      */   public int hashCode() {
/*  686 */     int hashCode = 1;
/*  687 */     if (getGenes() != null) {
/*  688 */       int size = size();
/*  689 */       for (int i = 0; i < size; i++) {
/*  690 */         int geneHashcode; Gene gene = getGene(i);
/*  691 */         if (gene == null) {
/*  692 */           geneHashcode = -55;
/*      */         } else {
/*      */           
/*  695 */           geneHashcode = gene.hashCode();
/*      */         } 
/*  697 */         hashCode = 31 * hashCode + geneHashcode;
/*      */       } 
/*      */     } 
/*  700 */     return hashCode;
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
/*      */   public int compareTo(Object other) {
/*  722 */     if (other == null) {
/*  723 */       return 1;
/*      */     }
/*  725 */     int size = size();
/*  726 */     IChromosome otherChromosome = (IChromosome)other;
/*  727 */     Gene[] otherGenes = otherChromosome.getGenes();
/*      */ 
/*      */ 
/*      */     
/*  731 */     if (otherChromosome.size() != size) {
/*  732 */       return size() - otherChromosome.size();
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  738 */     for (int i = 0; i < size; i++) {
/*  739 */       int comparison = getGene(i).compareTo((T)otherGenes[i]);
/*  740 */       if (comparison != 0) {
/*  741 */         return comparison;
/*      */       }
/*      */     } 
/*      */ 
/*      */     
/*  746 */     if (this.m_fitnessValue != otherChromosome.getFitnessValueDirectly()) {
/*  747 */       FitnessEvaluator eval = getConfiguration().getFitnessEvaluator();
/*  748 */       if (eval != null) {
/*  749 */         if (eval.isFitter(this.m_fitnessValue, otherChromosome.getFitnessValueDirectly()))
/*      */         {
/*  751 */           return 1;
/*      */         }
/*      */         
/*  754 */         return -1;
/*      */       } 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  760 */       return -1;
/*      */     } 
/*      */     
/*  763 */     if (this.m_compareAppData)
/*      */     {
/*      */       
/*  766 */       if (getApplicationData() == null) {
/*  767 */         if (otherChromosome.getApplicationData() != null) {
/*  768 */           return -1;
/*      */         }
/*      */       } else {
/*  771 */         if (otherChromosome.getApplicationData() == null) {
/*  772 */           return 1;
/*      */         }
/*      */         
/*  775 */         if (getApplicationData() instanceof Comparable) {
/*      */           try {
/*  777 */             return ((Comparable<Object>)getApplicationData()).compareTo(otherChromosome.getApplicationData());
/*      */           
/*      */           }
/*  780 */           catch (ClassCastException cex) {
/*      */             
/*  782 */             return -1;
/*      */           } 
/*      */         }
/*      */         
/*  786 */         return getApplicationData().getClass().getName().compareTo(otherChromosome.getApplicationData().getClass().getName());
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  793 */     return 0;
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
/*      */   public void setIsSelectedForNextGeneration(boolean a_isSelected) {
/*  807 */     this.m_isSelectedForNextGeneration = a_isSelected;
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
/*      */   public boolean isSelectedForNextGeneration() {
/*  820 */     return this.m_isSelectedForNextGeneration;
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
/*      */   public void cleanup() {
/*  832 */     if (getConfiguration() == null) {
/*  833 */       throw new IllegalStateException("The active Configuration object must be set on this Chromosome prior to invocation of the cleanup() method.");
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  839 */     getConfiguration().getFitnessFunction(); this.m_fitnessValue = -1.0D;
/*      */     
/*  841 */     this.m_isSelectedForNextGeneration = false;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  849 */     IChromosomePool pool = getConfiguration().getChromosomePool();
/*  850 */     if (pool != null) {
/*      */ 
/*      */ 
/*      */       
/*  854 */       pool.releaseChromosome(this);
/*      */ 
/*      */     
/*      */     }
/*      */     else {
/*      */ 
/*      */       
/*  861 */       for (int i = 0; i < size(); i++) {
/*  862 */         getGene(i).cleanup();
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setApplicationData(Object a_newData) {
/*  881 */     this.m_applicationData = a_newData;
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
/*      */   public Object getApplicationData() {
/*  897 */     return this.m_applicationData;
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
/*      */   public void setGenes(Gene[] a_genes) throws InvalidConfigurationException {
/*  911 */     super.setGenes(a_genes);
/*  912 */     verify(getConstraintChecker());
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
/*      */   public void setCompareApplicationData(boolean a_doCompare) {
/*  926 */     this.m_compareAppData = a_doCompare;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isCompareApplicationData() {
/*  936 */     return this.m_compareAppData;
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
/*      */   public void setConstraintChecker(IGeneConstraintChecker a_constraintChecker) throws InvalidConfigurationException {
/*  951 */     verify(a_constraintChecker);
/*  952 */     this.m_geneAlleleChecker = a_constraintChecker;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public IGeneConstraintChecker getConstraintChecker() {
/*  963 */     return this.m_geneAlleleChecker;
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
/*      */   protected void verify(IGeneConstraintChecker a_constraintChecker) throws InvalidConfigurationException {
/*  978 */     if (a_constraintChecker != null && getGenes() != null) {
/*  979 */       int len = (getGenes()).length;
/*  980 */       for (int i = 0; i < len; i++) {
/*  981 */         Gene gene = getGene(i);
/*  982 */         if (!a_constraintChecker.verify(gene, null, this, i)) {
/*  983 */           throw new InvalidConfigurationException("The gene type " + gene.getClass().getName() + " is not allowed to be used in the chromosome due to the" + " constraint checker used.");
/*      */         }
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
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isHandlerFor(Object a_obj, Class<Chromosome> a_class) {
/*  999 */     if (a_class == Chromosome.class) {
/* 1000 */       return true;
/*      */     }
/*      */     
/* 1003 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Object perform(Object a_obj, Class a_class, Object a_params) throws Exception {
/* 1010 */     return randomInitialChromosome(getConfiguration());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setMultiObjectives(List a_values) {
/* 1017 */     if (this.m_multiObjective == null) {
/* 1018 */       this.m_multiObjective = new Vector();
/*      */     }
/* 1020 */     this.m_multiObjective.clear();
/* 1021 */     this.m_multiObjective.addAll(a_values);
/*      */   }
/*      */   
/*      */   public List getMultiObjectives() {
/* 1025 */     return this.m_multiObjective;
/*      */   }
/*      */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\Chromosome.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */