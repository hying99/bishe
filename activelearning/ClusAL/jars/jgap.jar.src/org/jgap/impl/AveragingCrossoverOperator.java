/*     */ package org.jgap.impl;
/*     */ 
/*     */ import java.util.Hashtable;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.jgap.BaseGeneticOperator;
/*     */ import org.jgap.Configuration;
/*     */ import org.jgap.Gene;
/*     */ import org.jgap.Genotype;
/*     */ import org.jgap.IChromosome;
/*     */ import org.jgap.ICompositeGene;
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
/*     */ public class AveragingCrossoverOperator
/*     */   extends BaseGeneticOperator
/*     */ {
/*     */   private static final String CVS_REVISION = "$Revision: 1.28 $";
/*     */   private RandomGenerator m_crossoverGenerator;
/*     */   private int m_crossoverRate;
/*     */   private Map m_loci;
/*     */   private IUniversalRateCalculator m_crossoverRateCalc;
/*     */   
/*     */   private void init() {
/*  59 */     this.m_loci = new Hashtable<Object, Object>();
/*  60 */     this.m_crossoverRate = 2;
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
/*     */   public AveragingCrossoverOperator() throws InvalidConfigurationException {
/*  76 */     this(Genotype.getStaticConfiguration(), (RandomGenerator)null);
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
/*     */   public AveragingCrossoverOperator(Configuration a_configuration) throws InvalidConfigurationException {
/*  92 */     this(a_configuration, (RandomGenerator)null);
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
/*     */   public AveragingCrossoverOperator(Configuration a_configuration, RandomGenerator a_generatorForAveraging) throws InvalidConfigurationException {
/* 110 */     super(a_configuration);
/* 111 */     init();
/* 112 */     this.m_crossoverGenerator = a_generatorForAveraging;
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
/*     */   public AveragingCrossoverOperator(Configuration a_configuration, IUniversalRateCalculator a_crossoverRateCalculator) throws InvalidConfigurationException {
/* 132 */     super(a_configuration);
/* 133 */     init();
/* 134 */     setCrossoverRateCalc(a_crossoverRateCalculator);
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
/*     */   private void setCrossoverRateCalc(IUniversalRateCalculator a_crossoverRateCalculator) {
/* 146 */     this.m_crossoverRateCalc = a_crossoverRateCalculator;
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
/*     */   public void operate(Population a_population, List<IChromosome> a_candidateChromosomes) {
/* 167 */     int size = Math.min(getConfiguration().getPopulationSize(), a_population.size());
/*     */     
/* 169 */     int numCrossovers = 0;
/* 170 */     if (this.m_crossoverRateCalc == null) {
/* 171 */       numCrossovers = size / this.m_crossoverRate;
/*     */     } else {
/*     */       
/* 174 */       numCrossovers = size / this.m_crossoverRateCalc.calculateCurrentRate();
/*     */     } 
/* 176 */     RandomGenerator generator = getConfiguration().getRandomGenerator();
/* 177 */     if (this.m_crossoverGenerator == null) {
/* 178 */       this.m_crossoverGenerator = generator;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 186 */     for (int i = 0; i < numCrossovers; i++) {
/* 187 */       int index1 = generator.nextInt(size);
/* 188 */       int index2 = generator.nextInt(size);
/* 189 */       IChromosome firstMate = a_population.getChromosome(index1);
/* 190 */       IChromosome secondMate = a_population.getChromosome(index2);
/* 191 */       Gene[] firstGenes = firstMate.getGenes();
/* 192 */       Gene[] secondGenes = secondMate.getGenes();
/* 193 */       int locus = getLocus(this.m_crossoverGenerator, i, firstGenes.length);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 199 */       for (int j = locus; j < firstGenes.length; j++) {
/*     */         Gene gene1, gene2;
/*     */         
/* 202 */         if (firstGenes[j] instanceof ICompositeGene) {
/*     */           
/* 204 */           index1 = generator.nextInt(firstGenes[j].size());
/* 205 */           gene1 = ((ICompositeGene)firstGenes[j]).geneAt(index1);
/*     */         } else {
/*     */           
/* 208 */           gene1 = firstGenes[j];
/*     */         } 
/*     */ 
/*     */         
/* 212 */         if (secondGenes[j] instanceof CompositeGene) {
/*     */           
/* 214 */           index2 = generator.nextInt(secondGenes[j].size());
/* 215 */           gene2 = ((ICompositeGene)secondGenes[j]).geneAt(index2);
/*     */         } else {
/*     */           
/* 218 */           gene2 = secondGenes[j];
/*     */         } 
/* 220 */         Object firstAllele = gene1.getAllele();
/* 221 */         gene1.setAllele(gene2.getAllele());
/* 222 */         gene2.setAllele(firstAllele);
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 228 */       a_candidateChromosomes.add(firstMate);
/* 229 */       a_candidateChromosomes.add(secondMate);
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
/*     */   protected int getLocus(RandomGenerator a_generator, int a_index, int a_max) {
/* 248 */     Integer locus = (Integer)this.m_loci.get(new Integer(a_index));
/* 249 */     if (locus == null) {
/* 250 */       locus = new Integer(a_generator.nextInt(a_max));
/* 251 */       this.m_loci.put(new Integer(a_index), locus);
/*     */     } 
/* 253 */     return locus.intValue();
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
/*     */   public boolean equals(Object a_other) {
/*     */     try {
/* 269 */       return (compareTo(a_other) == 0);
/*     */     }
/* 271 */     catch (ClassCastException cex) {
/* 272 */       return false;
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
/*     */   public int compareTo(Object a_other) {
/* 288 */     if (a_other == null) {
/* 289 */       return 1;
/*     */     }
/* 291 */     AveragingCrossoverOperator op = (AveragingCrossoverOperator)a_other;
/* 292 */     if (this.m_crossoverRateCalc == null) {
/* 293 */       if (op.m_crossoverRateCalc != null) {
/* 294 */         return -1;
/*     */       
/*     */       }
/*     */     }
/* 298 */     else if (op.m_crossoverRateCalc == null) {
/* 299 */       return 1;
/*     */     } 
/*     */     
/* 302 */     if (this.m_crossoverRate != op.m_crossoverRate) {
/* 303 */       if (this.m_crossoverRate > op.m_crossoverRate) {
/* 304 */         return 1;
/*     */       }
/*     */       
/* 307 */       return -1;
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 312 */     return 0;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setCrossoverRate(int a_rate) {
/* 321 */     this.m_crossoverRate = a_rate;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\impl\AveragingCrossoverOperator.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */