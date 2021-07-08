/*     */ package org.jgap.impl;
/*     */ 
/*     */ import java.util.List;
/*     */ import java.util.Vector;
/*     */ import org.jgap.BaseGeneticOperator;
/*     */ import org.jgap.Configuration;
/*     */ import org.jgap.Gene;
/*     */ import org.jgap.GeneticOperator;
/*     */ import org.jgap.Genotype;
/*     */ import org.jgap.IChromosome;
/*     */ import org.jgap.ICompositeGene;
/*     */ import org.jgap.IGeneticOperatorConstraint;
/*     */ import org.jgap.IUniversalRateCalculator;
/*     */ import org.jgap.InvalidConfigurationException;
/*     */ import org.jgap.Population;
/*     */ import org.jgap.RandomGenerator;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class CrossoverOperator
/*     */   extends BaseGeneticOperator
/*     */   implements Comparable
/*     */ {
/*     */   private static final String CVS_REVISION = "$Revision: 1.42 $";
/*     */   private int m_crossoverRate;
/*     */   private double m_crossoverRatePercent;
/*     */   private IUniversalRateCalculator m_crossoverRateCalc;
/*     */   private boolean m_allowFullCrossOver;
/*     */   private boolean m_xoverNewAge;
/*     */   
/*     */   public CrossoverOperator() throws InvalidConfigurationException {
/*  86 */     super(Genotype.getStaticConfiguration());
/*  87 */     init();
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
/*     */   public CrossoverOperator(Configuration a_configuration) throws InvalidConfigurationException {
/* 103 */     super(a_configuration);
/* 104 */     init();
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
/*     */   protected void init() {
/* 116 */     this.m_crossoverRate = 2;
/* 117 */     this.m_crossoverRatePercent = -1.0D;
/* 118 */     setCrossoverRateCalc(null);
/* 119 */     setAllowFullCrossOver(true);
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
/*     */   public CrossoverOperator(Configuration a_configuration, IUniversalRateCalculator a_crossoverRateCalculator) throws InvalidConfigurationException {
/* 140 */     this(a_configuration, a_crossoverRateCalculator, true);
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
/*     */   public CrossoverOperator(Configuration a_configuration, IUniversalRateCalculator a_crossoverRateCalculator, boolean a_allowFullCrossOver) throws InvalidConfigurationException {
/* 163 */     super(a_configuration);
/* 164 */     setCrossoverRateCalc(a_crossoverRateCalculator);
/* 165 */     setAllowFullCrossOver(a_allowFullCrossOver);
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
/*     */   public CrossoverOperator(Configuration a_configuration, int a_desiredCrossoverRate) throws InvalidConfigurationException {
/* 183 */     this(a_configuration, a_desiredCrossoverRate, true);
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
/*     */   public CrossoverOperator(Configuration a_configuration, int a_desiredCrossoverRate, boolean a_allowFullCrossOver) throws InvalidConfigurationException {
/* 203 */     this(a_configuration, a_desiredCrossoverRate, a_allowFullCrossOver, false);
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
/*     */   public CrossoverOperator(Configuration a_configuration, int a_desiredCrossoverRate, boolean a_allowFullCrossOver, boolean a_xoverNewAge) throws InvalidConfigurationException {
/* 226 */     super(a_configuration);
/* 227 */     if (a_desiredCrossoverRate < 1) {
/* 228 */       throw new IllegalArgumentException("Crossover rate must be greater zero");
/*     */     }
/* 230 */     this.m_crossoverRate = a_desiredCrossoverRate;
/* 231 */     this.m_crossoverRatePercent = -1.0D;
/* 232 */     setCrossoverRateCalc(null);
/* 233 */     setAllowFullCrossOver(a_allowFullCrossOver);
/* 234 */     setXoverNewAge(a_xoverNewAge);
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
/*     */   public CrossoverOperator(Configuration a_configuration, double a_crossoverRatePercentage) throws InvalidConfigurationException {
/* 253 */     this(a_configuration, a_crossoverRatePercentage, true);
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
/*     */   public CrossoverOperator(Configuration a_configuration, double a_crossoverRatePercentage, boolean a_allowFullCrossOver) throws InvalidConfigurationException {
/* 274 */     this(a_configuration, a_crossoverRatePercentage, a_allowFullCrossOver, false);
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
/*     */   public CrossoverOperator(Configuration a_configuration, double a_crossoverRatePercentage, boolean a_allowFullCrossOver, boolean a_xoverNewAge) throws InvalidConfigurationException {
/* 298 */     super(a_configuration);
/* 299 */     if (a_crossoverRatePercentage <= 0.0D) {
/* 300 */       throw new IllegalArgumentException("Crossover rate must be greater zero");
/*     */     }
/* 302 */     this.m_crossoverRatePercent = a_crossoverRatePercentage;
/* 303 */     this.m_crossoverRate = -1;
/* 304 */     setCrossoverRateCalc(null);
/* 305 */     setAllowFullCrossOver(a_allowFullCrossOver);
/* 306 */     setXoverNewAge(a_xoverNewAge);
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
/*     */   public void operate(Population a_population, List a_candidateChromosomes) {
/* 325 */     int size = Math.min(getConfiguration().getPopulationSize(), a_population.size());
/*     */     
/* 327 */     int numCrossovers = 0;
/* 328 */     if (this.m_crossoverRate >= 0) {
/* 329 */       numCrossovers = size / this.m_crossoverRate;
/*     */     }
/* 331 */     else if (this.m_crossoverRateCalc != null) {
/* 332 */       numCrossovers = size / this.m_crossoverRateCalc.calculateCurrentRate();
/*     */     } else {
/*     */       
/* 335 */       numCrossovers = (int)(size * this.m_crossoverRatePercent);
/*     */     } 
/* 337 */     RandomGenerator generator = getConfiguration().getRandomGenerator();
/* 338 */     IGeneticOperatorConstraint constraint = getConfiguration().getJGAPFactory().getGeneticOperatorConstraint();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 346 */     for (int i = 0; i < numCrossovers; i++) {
/* 347 */       int index1 = generator.nextInt(size);
/* 348 */       int index2 = generator.nextInt(size);
/* 349 */       IChromosome chrom1 = a_population.getChromosome(index1);
/* 350 */       IChromosome chrom2 = a_population.getChromosome(index2);
/*     */ 
/*     */       
/* 353 */       if (!isXoverNewAge() && chrom1.getAge() < 1 && chrom2.getAge() < 1) {
/*     */         continue;
/*     */       }
/*     */ 
/*     */ 
/*     */       
/* 359 */       if (constraint != null) {
/* 360 */         List<IChromosome> v = new Vector();
/* 361 */         v.add(chrom1);
/* 362 */         v.add(chrom2);
/* 363 */         if (!constraint.isValid(a_population, v, (GeneticOperator)this)) {
/*     */           continue;
/*     */         }
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 371 */       IChromosome firstMate = (IChromosome)chrom1.clone();
/* 372 */       IChromosome secondMate = (IChromosome)chrom2.clone();
/*     */ 
/*     */       
/* 375 */       doCrossover(firstMate, secondMate, a_candidateChromosomes, generator);
/*     */       continue;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void doCrossover(IChromosome firstMate, IChromosome secondMate, List<IChromosome> a_candidateChromosomes, RandomGenerator generator) {
/* 382 */     Gene[] firstGenes = firstMate.getGenes();
/* 383 */     Gene[] secondGenes = secondMate.getGenes();
/* 384 */     int locus = generator.nextInt(firstGenes.length);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 390 */     for (int j = locus; j < firstGenes.length; j++) {
/*     */       Gene gene1, gene2;
/*     */       
/* 393 */       if (firstGenes[j] instanceof ICompositeGene) {
/*     */ 
/*     */         
/* 396 */         int index1 = generator.nextInt(firstGenes[j].size());
/* 397 */         gene1 = ((ICompositeGene)firstGenes[j]).geneAt(index1);
/*     */       } else {
/*     */         
/* 400 */         gene1 = firstGenes[j];
/*     */       } 
/*     */ 
/*     */       
/* 404 */       if (secondGenes[j] instanceof ICompositeGene) {
/*     */ 
/*     */         
/* 407 */         int index2 = generator.nextInt(secondGenes[j].size());
/* 408 */         gene2 = ((ICompositeGene)secondGenes[j]).geneAt(index2);
/*     */       } else {
/*     */         
/* 411 */         gene2 = secondGenes[j];
/*     */       } 
/* 413 */       Object firstAllele = gene1.getAllele();
/* 414 */       gene1.setAllele(gene2.getAllele());
/* 415 */       gene2.setAllele(firstAllele);
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 421 */     a_candidateChromosomes.add(firstMate);
/* 422 */     a_candidateChromosomes.add(secondMate);
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
/*     */   private void setCrossoverRateCalc(IUniversalRateCalculator a_crossoverRateCalculator) {
/* 435 */     this.m_crossoverRateCalc = a_crossoverRateCalculator;
/* 436 */     if (a_crossoverRateCalculator != null) {
/* 437 */       this.m_crossoverRate = -1;
/* 438 */       this.m_crossoverRatePercent = -1.0D;
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
/*     */   public int compareTo(Object a_other) {
/* 455 */     if (a_other == null) {
/* 456 */       return 1;
/*     */     }
/* 458 */     CrossoverOperator op = (CrossoverOperator)a_other;
/* 459 */     if (this.m_crossoverRateCalc == null) {
/* 460 */       if (op.m_crossoverRateCalc != null) {
/* 461 */         return -1;
/*     */       
/*     */       }
/*     */     }
/* 465 */     else if (op.m_crossoverRateCalc == null) {
/* 466 */       return 1;
/*     */     } 
/*     */     
/* 469 */     if (this.m_crossoverRate != op.m_crossoverRate) {
/* 470 */       if (this.m_crossoverRate > op.m_crossoverRate) {
/* 471 */         return 1;
/*     */       }
/*     */       
/* 474 */       return -1;
/*     */     } 
/*     */     
/* 477 */     if (this.m_allowFullCrossOver != op.m_allowFullCrossOver) {
/* 478 */       if (this.m_allowFullCrossOver) {
/* 479 */         return 1;
/*     */       }
/*     */       
/* 482 */       return -1;
/*     */     } 
/*     */     
/* 485 */     if (this.m_xoverNewAge != op.m_xoverNewAge) {
/* 486 */       if (this.m_xoverNewAge) {
/* 487 */         return 1;
/*     */       }
/*     */       
/* 490 */       return -1;
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 495 */     return 0;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setAllowFullCrossOver(boolean a_allowFullXOver) {
/* 505 */     this.m_allowFullCrossOver = a_allowFullXOver;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isAllowFullCrossOver() {
/* 515 */     return this.m_allowFullCrossOver;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getCrossOverRate() {
/* 525 */     return this.m_crossoverRate;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getCrossOverRatePercent() {
/* 535 */     return this.m_crossoverRatePercent;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setXoverNewAge(boolean a_xoverNewAge) {
/* 546 */     this.m_xoverNewAge = a_xoverNewAge;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isXoverNewAge() {
/* 557 */     return this.m_xoverNewAge;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\impl\CrossoverOperator.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */