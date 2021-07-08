/*     */ package org.jgap.impl;
/*     */ 
/*     */ import java.util.List;
/*     */ import org.jgap.BaseGeneticOperator;
/*     */ import org.jgap.Configuration;
/*     */ import org.jgap.Gene;
/*     */ import org.jgap.Genotype;
/*     */ import org.jgap.IChromosome;
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
/*     */ public class InversionOperator
/*     */   extends BaseGeneticOperator
/*     */ {
/*     */   private static final String CVS_REVISION = "$Revision: 1.10 $";
/*     */   
/*     */   public InversionOperator() throws InvalidConfigurationException {
/*  38 */     this(Genotype.getStaticConfiguration());
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
/*     */   public InversionOperator(Configuration a_config) throws InvalidConfigurationException {
/*  51 */     super(a_config);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void operate(Population a_population, List<IChromosome> a_candidateChromosomes) {
/*  62 */     int size = Math.min(getConfiguration().getPopulationSize(), a_population.size());
/*     */     
/*  64 */     RandomGenerator generator = getConfiguration().getRandomGenerator();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  71 */     int index1 = generator.nextInt(size);
/*  72 */     IChromosome chrom1 = a_population.getChromosome(index1);
/*  73 */     IChromosome firstMate = (IChromosome)chrom1.clone();
/*  74 */     Gene[] firstGenes = firstMate.getGenes();
/*  75 */     int locus = generator.nextInt(firstGenes.length);
/*     */ 
/*     */     
/*  78 */     Gene[] invertedGenes = new Gene[firstGenes.length];
/*  79 */     int index = 0;
/*  80 */     int len = firstGenes.length; int j;
/*  81 */     for (j = locus; j < len; j++) {
/*  82 */       invertedGenes[index++] = firstGenes[j];
/*     */     }
/*  84 */     for (j = 0; j < locus; j++) {
/*  85 */       invertedGenes[index++] = firstGenes[j];
/*     */     }
/*     */     try {
/*  88 */       firstMate.setGenes(invertedGenes);
/*     */     }
/*  90 */     catch (InvalidConfigurationException cex) {
/*     */ 
/*     */       
/*  93 */       throw new Error(cex);
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/*  98 */     a_candidateChromosomes.add(firstMate);
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
/* 113 */     if (a_other == null) {
/* 114 */       return 1;
/*     */     }
/* 116 */     InversionOperator op = (InversionOperator)a_other;
/*     */ 
/*     */     
/* 119 */     return 0;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\impl\InversionOperator.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */