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
/*     */ 
/*     */ 
/*     */ 
/*     */ public class GaussianMutationOperator
/*     */   extends BaseGeneticOperator
/*     */ {
/*     */   private static final String CVS_REVISION = "$Revision: 1.22 $";
/*     */   private double m_deviation;
/*     */   private RandomGenerator m_rg;
/*     */   
/*     */   public GaussianMutationOperator() throws InvalidConfigurationException {
/*  43 */     this(Genotype.getStaticConfiguration());
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
/*     */   public GaussianMutationOperator(Configuration a_config) throws InvalidConfigurationException {
/*  58 */     this(a_config, 0.05D);
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
/*     */   public GaussianMutationOperator(Configuration a_configuration, double a_deviation) throws InvalidConfigurationException {
/*  74 */     super(a_configuration);
/*  75 */     this.m_deviation = a_deviation;
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
/*     */   public void operate(Population a_population, List<IChromosome> a_candidateChromosomes) {
/*  89 */     int size = Math.min(getConfiguration().getPopulationSize(), a_population.size());
/*     */     
/*  91 */     if (this.m_rg == null) {
/*  92 */       RandomGenerator rn = getConfiguration().getRandomGenerator();
/*  93 */       this.m_rg = rn;
/*     */     } 
/*  95 */     for (int i = 0; i < size; i++) {
/*  96 */       Gene[] genes = a_population.getChromosome(i).getGenes();
/*  97 */       IChromosome copyOfChromosome = null;
/*     */ 
/*     */       
/* 100 */       for (int j = 0; j < genes.length; j++) {
/* 101 */         double nextGaussian = this.m_rg.nextDouble();
/* 102 */         double diff = nextGaussian * this.m_deviation;
/*     */ 
/*     */         
/* 105 */         if (copyOfChromosome == null) {
/* 106 */           copyOfChromosome = (IChromosome)a_population.getChromosome(i).clone();
/*     */ 
/*     */           
/* 109 */           a_candidateChromosomes.add(copyOfChromosome);
/*     */           
/* 111 */           genes = copyOfChromosome.getGenes();
/*     */         } 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 117 */         if (genes[j] instanceof CompositeGene) {
/* 118 */           CompositeGene compositeGene = (CompositeGene)genes[j];
/* 119 */           for (int k = 0; k < compositeGene.size(); k++) {
/* 120 */             mutateGene(compositeGene.geneAt(k), diff);
/*     */           }
/*     */         } else {
/*     */           
/* 124 */           mutateGene(genes[j], diff);
/*     */         } 
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
/*     */   private void mutateGene(Gene a_gene, double a_percentage) {
/* 140 */     for (int k = 0; k < a_gene.size(); k++)
/*     */     {
/*     */       
/* 143 */       a_gene.applyMutation(k, a_percentage);
/*     */     }
/*     */   }
/*     */   
/*     */   private void setRandomGenerator(RandomGenerator a_rg) {
/* 148 */     this.m_rg = a_rg;
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
/* 164 */     if (a_other == null) {
/* 165 */       return 1;
/*     */     }
/* 167 */     GaussianMutationOperator op = (GaussianMutationOperator)a_other;
/* 168 */     if (this.m_deviation != op.m_deviation) {
/* 169 */       if (this.m_deviation > op.m_deviation) {
/* 170 */         return 1;
/*     */       }
/*     */       
/* 173 */       return -1;
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 178 */     return 0;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getDeviation() {
/* 188 */     return this.m_deviation;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\impl\GaussianMutationOperator.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */