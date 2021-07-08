/*     */ package org.jgap;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ChromosomeForTesting
/*     */   extends Chromosome
/*     */ {
/*     */   private boolean m_isCloned;
/*     */   
/*     */   public ChromosomeForTesting() throws InvalidConfigurationException {
/*  25 */     super(Genotype.getStaticConfiguration());
/*     */   }
/*     */ 
/*     */   
/*     */   public ChromosomeForTesting(Configuration a_config, Gene[] a_initialGenes) throws InvalidConfigurationException {
/*  30 */     super(a_config, a_initialGenes);
/*     */   }
/*     */   
/*     */   public int getComputedTimes() {
/*  34 */     return TestResultHolder.computedTimes;
/*     */   }
/*     */   
/*     */   public void resetComputedTimes() {
/*  38 */     TestResultHolder.computedTimes = 0;
/*     */   }
/*     */   
/*     */   public double getFitnessValue() {
/*  42 */     if (this.m_isCloned && this.m_fitnessValue < 0.0D)
/*     */     {
/*  44 */       TestResultHolder.computedTimes++;
/*     */     }
/*  46 */     return super.getFitnessValue();
/*     */   }
/*     */   
/*     */   public void resetIsCloned() {
/*  50 */     this.m_isCloned = false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public IChromosome randomInitialChromosome2() throws InvalidConfigurationException {
/*  57 */     if (getConfiguration() == null) {
/*  58 */       throw new IllegalArgumentException("Configuration instance must not be null");
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  64 */     getConfiguration().lockSettings();
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  69 */     IChromosomePool pool = getConfiguration().getChromosomePool();
/*  70 */     if (pool != null) {
/*  71 */       IChromosome randomChromosome = pool.acquireChromosome();
/*  72 */       if (randomChromosome != null) {
/*  73 */         Gene[] genes = randomChromosome.getGenes();
/*  74 */         RandomGenerator randomGenerator = getConfiguration().getRandomGenerator();
/*  75 */         for (int j = 0; j < genes.length; j++) {
/*  76 */           genes[j].setToRandomValue(randomGenerator);
/*     */         }
/*  78 */         randomChromosome.setFitnessValueDirectly(-1.0D);
/*     */         
/*  80 */         return randomChromosome;
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  87 */     IChromosome sampleChromosome = getConfiguration().getSampleChromosome();
/*  88 */     Gene[] sampleGenes = sampleChromosome.getGenes();
/*  89 */     Gene[] newGenes = new Gene[sampleGenes.length];
/*  90 */     RandomGenerator generator = getConfiguration().getRandomGenerator();
/*  91 */     for (int i = 0; i < newGenes.length; i++) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*  98 */       newGenes[i] = sampleGenes[i].newGene();
/*     */ 
/*     */       
/* 101 */       newGenes[i].setToRandomValue(generator);
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 106 */     return new ChromosomeForTesting(getConfiguration(), newGenes);
/*     */   }
/*     */   
/*     */   public synchronized Object clone() {
/*     */     try {
/* 111 */       ChromosomeForTesting chrom = new ChromosomeForTesting(getConfiguration(), ((Chromosome)super.clone()).getGenes());
/*     */       
/* 113 */       chrom.m_isCloned = true;
/* 114 */       return chrom;
/*     */     }
/* 116 */     catch (InvalidConfigurationException iex) {
/* 117 */       throw new IllegalStateException(iex.getMessage());
/*     */     } 
/*     */   }
/*     */   
/*     */   public static class TestResultHolder {
/*     */     static int computedTimes; }
/*     */   
/*     */   public boolean isHandlerFor(Object a_obj, Class<?> a_class) {
/* 125 */     if (a_class == getClass()) {
/* 126 */       return true;
/*     */     }
/*     */     
/* 129 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Object perform(Object a_obj, Class a_class, Object a_params) throws Exception {
/* 135 */     return randomInitialChromosome2();
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\ChromosomeForTesting.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */