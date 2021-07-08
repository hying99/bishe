/*     */ package org.jgap.impl.salesman;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import org.jgap.Chromosome;
/*     */ import org.jgap.Configuration;
/*     */ import org.jgap.DefaultFitnessEvaluator;
/*     */ import org.jgap.FitnessEvaluator;
/*     */ import org.jgap.FitnessFunction;
/*     */ import org.jgap.Gene;
/*     */ import org.jgap.GeneticOperator;
/*     */ import org.jgap.Genotype;
/*     */ import org.jgap.IChromosome;
/*     */ import org.jgap.IChromosomePool;
/*     */ import org.jgap.InvalidConfigurationException;
/*     */ import org.jgap.NaturalSelector;
/*     */ import org.jgap.Population;
/*     */ import org.jgap.RandomGenerator;
/*     */ import org.jgap.event.EventManager;
/*     */ import org.jgap.event.IEventManager;
/*     */ import org.jgap.impl.BestChromosomesSelector;
/*     */ import org.jgap.impl.ChromosomePool;
/*     */ import org.jgap.impl.GreedyCrossover;
/*     */ import org.jgap.impl.StockRandomGenerator;
/*     */ import org.jgap.impl.SwappingMutationOperator;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class Salesman
/*     */   implements Serializable
/*     */ {
/*     */   private static final String CVS_REVISION = "$Revision: 1.20 $";
/*     */   private Configuration m_config;
/*  52 */   private int m_maxEvolution = 128;
/*     */   
/*  54 */   private int m_populationSize = 512;
/*     */   
/*  56 */   private int m_acceptableCost = -1;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract double distance(Gene paramGene1, Gene paramGene2);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract IChromosome createSampleChromosome(Object paramObject);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public FitnessFunction createFitnessFunction(Object a_initial_data) {
/* 103 */     return new SalesmanFitnessFunction(this);
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
/*     */   public Configuration createConfiguration(Object a_initial_data) throws InvalidConfigurationException {
/* 124 */     Configuration config = new Configuration();
/* 125 */     BestChromosomesSelector bestChromsSelector = new BestChromosomesSelector(config, 1.0D);
/*     */     
/* 127 */     bestChromsSelector.setDoubletteChromosomesAllowed(false);
/* 128 */     config.addNaturalSelector((NaturalSelector)bestChromsSelector, true);
/* 129 */     config.setRandomGenerator((RandomGenerator)new StockRandomGenerator());
/* 130 */     config.setMinimumPopSizePercent(0);
/* 131 */     config.setEventManager((IEventManager)new EventManager());
/* 132 */     config.setFitnessEvaluator((FitnessEvaluator)new DefaultFitnessEvaluator());
/* 133 */     config.setChromosomePool((IChromosomePool)new ChromosomePool());
/*     */ 
/*     */     
/* 136 */     config.addGeneticOperator((GeneticOperator)new GreedyCrossover(config));
/* 137 */     config.addGeneticOperator((GeneticOperator)new SwappingMutationOperator(config, 20));
/* 138 */     return config;
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
/*     */   public int getAcceptableCost() {
/* 153 */     return this.m_acceptableCost;
/*     */   }
/*     */   
/*     */   public void setAcceptableCost(int a_acceptableCost) {
/* 157 */     this.m_acceptableCost = a_acceptableCost;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getMaxEvolution() {
/* 167 */     return this.m_maxEvolution;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setMaxEvolution(int a_maxEvolution) {
/* 178 */     this.m_maxEvolution = a_maxEvolution;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getPopulationSize() {
/* 187 */     return this.m_populationSize;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setPopulationSize(int a_populationSize) {
/* 198 */     this.m_populationSize = a_populationSize;
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
/*     */   public IChromosome findOptimalPath(Object a_initial_data) throws Exception {
/* 218 */     this.m_config = createConfiguration(a_initial_data);
/* 219 */     FitnessFunction myFunc = createFitnessFunction(a_initial_data);
/* 220 */     this.m_config.setFitnessFunction(myFunc);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 226 */     IChromosome sampleChromosome = createSampleChromosome(a_initial_data);
/* 227 */     this.m_config.setSampleChromosome(sampleChromosome);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 235 */     this.m_config.setPopulationSize(getPopulationSize());
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 242 */     IChromosome[] chromosomes = new IChromosome[this.m_config.getPopulationSize()];
/*     */     
/* 244 */     Gene[] samplegenes = sampleChromosome.getGenes();
/* 245 */     for (int i = 0; i < chromosomes.length; i++) {
/* 246 */       Gene[] genes = new Gene[samplegenes.length];
/* 247 */       for (int k = 0; k < genes.length; k++) {
/* 248 */         genes[k] = samplegenes[k].newGene();
/* 249 */         genes[k].setAllele(samplegenes[k].getAllele());
/*     */       } 
/* 251 */       shuffle(genes);
/* 252 */       chromosomes[i] = (IChromosome)new Chromosome(this.m_config, genes);
/*     */     } 
/* 254 */     Genotype population = new Genotype(this.m_config, new Population(this.m_config, chromosomes));
/*     */     
/* 256 */     IChromosome best = null;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 261 */     for (int j = 0; j < getMaxEvolution(); j++) {
/* 262 */       population.evolve();
/* 263 */       best = population.getFittestChromosome();
/* 264 */       if (best.getFitnessValue() >= getAcceptableCost()) {
/*     */         break;
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 270 */     return best;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected void shuffle(Gene[] a_genes) {
/* 276 */     for (int r = 0; r < 10 * a_genes.length; r++) {
/* 277 */       for (int i = this.m_startOffset; i < a_genes.length; i++) {
/* 278 */         int p = this.m_startOffset + this.m_config.getRandomGenerator().nextInt(a_genes.length - this.m_startOffset);
/*     */ 
/*     */         
/* 281 */         Gene t = a_genes[i];
/* 282 */         a_genes[i] = a_genes[p];
/* 283 */         a_genes[p] = t;
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/* 288 */   private int m_startOffset = 1;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setStartOffset(int a_offset) {
/* 301 */     this.m_startOffset = a_offset;
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
/*     */   public int getStartOffset() {
/* 315 */     return this.m_startOffset;
/*     */   }
/*     */   
/*     */   public Configuration getConfiguration() {
/* 319 */     return this.m_config;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\impl\salesman\Salesman.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */