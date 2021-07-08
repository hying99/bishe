/*     */ package org.jgap.impl;
/*     */ 
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.List;
/*     */ import org.jgap.Chromosome;
/*     */ import org.jgap.Configuration;
/*     */ import org.jgap.FitnessEvaluator;
/*     */ import org.jgap.IChromosome;
/*     */ import org.jgap.InvalidConfigurationException;
/*     */ import org.jgap.Population;
/*     */ import org.jgap.distr.IPopulationMerger;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class FittestPopulationMerger
/*     */   implements IPopulationMerger
/*     */ {
/*     */   private static final String CVS_REVISION = "$Revision: 1.17 $";
/*     */   
/*     */   public Population mergePopulations(Population a_population1, Population a_population2, int a_new_population_size) {
/*     */     try {
/*  37 */       a_population1.addChromosomes(a_population2);
/*     */ 
/*     */       
/*  40 */       List<?> allChromosomes = a_population1.getChromosomes();
/*  41 */       Collections.sort(allChromosomes, new FitnessChromosomeComparator(a_population1.getConfiguration()));
/*     */ 
/*     */ 
/*     */       
/*  45 */       Chromosome[] chromosomes = allChromosomes.<Chromosome>toArray(new Chromosome[0]);
/*     */       
/*  47 */       Population mergedPopulation = new Population(a_population1.getConfiguration(), a_new_population_size);
/*     */       
/*  49 */       for (int i = 0; i < a_new_population_size && i < chromosomes.length; i++) {
/*  50 */         mergedPopulation.addChromosome((IChromosome)chromosomes[i]);
/*     */       }
/*     */ 
/*     */       
/*  54 */       return mergedPopulation;
/*  55 */     } catch (InvalidConfigurationException iex) {
/*     */       
/*  57 */       throw new IllegalStateException(iex.getMessage());
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private class FitnessChromosomeComparator
/*     */     implements Comparator
/*     */   {
/*     */     private transient Configuration m_config;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private FitnessEvaluator m_fEvaluator;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public FitnessChromosomeComparator(Configuration a_config) {
/*  79 */       this.m_config = a_config;
/*  80 */       this.m_fEvaluator = this.m_config.getFitnessEvaluator();
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
/*     */ 
/*     */     
/*     */     public int compare(Object a_o1, Object a_o2) {
/*  95 */       IChromosome chr1 = (IChromosome)a_o1;
/*  96 */       IChromosome chr2 = (IChromosome)a_o2;
/*     */       
/*  98 */       if (this.m_fEvaluator.isFitter(chr2.getFitnessValue(), chr1.getFitnessValue()))
/*     */       {
/* 100 */         return 1;
/*     */       }
/* 102 */       if (this.m_fEvaluator.isFitter(chr1.getFitnessValue(), chr2.getFitnessValue()))
/*     */       {
/* 104 */         return -1;
/*     */       }
/*     */       
/* 107 */       return 0;
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\impl\FittestPopulationMerger.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */