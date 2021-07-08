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
/*     */ public class TwoWayMutationOperator
/*     */   extends BaseGeneticOperator
/*     */ {
/*     */   private static final String CVS_REVISION = "$Revision: 1.7 $";
/*     */   private int m_mutationRate;
/*     */   private IUniversalRateCalculator m_mutationRateCalc;
/*     */   
/*     */   public TwoWayMutationOperator() throws InvalidConfigurationException {
/*  63 */     this(Genotype.getStaticConfiguration(), (IUniversalRateCalculator)new DefaultMutationRateCalculator(Genotype.getStaticConfiguration()));
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
/*     */   public TwoWayMutationOperator(Configuration a_config) throws InvalidConfigurationException {
/*  81 */     this(a_config, (IUniversalRateCalculator)new DefaultMutationRateCalculator(a_config));
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
/*     */   public TwoWayMutationOperator(Configuration a_config, IUniversalRateCalculator a_mutationRateCalculator) throws InvalidConfigurationException {
/* 101 */     super(a_config);
/* 102 */     setMutationRateCalc(a_mutationRateCalculator);
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
/*     */   public TwoWayMutationOperator(Configuration a_config, int a_desiredMutationRate) throws InvalidConfigurationException {
/* 122 */     super(a_config);
/* 123 */     this.m_mutationRate = a_desiredMutationRate;
/* 124 */     setMutationRateCalc(null);
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
/*     */   public void operate(Population a_population, List<IChromosome> a_candidateChromosomes) {
/* 136 */     if (a_population == null || a_candidateChromosomes == null) {
/*     */       return;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 142 */     if (this.m_mutationRate == 0 && this.m_mutationRateCalc == null) {
/*     */       return;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 152 */     boolean mutate = false;
/* 153 */     RandomGenerator generator = getConfiguration().getRandomGenerator();
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 158 */     int size = Math.min(getConfiguration().getPopulationSize(), a_population.size());
/*     */     
/* 160 */     IGeneticOperatorConstraint constraint = getConfiguration().getJGAPFactory().getGeneticOperatorConstraint();
/*     */     
/* 162 */     for (int i = 0; i < size; i++) {
/* 163 */       int geneIndex; IChromosome chrom = a_population.getChromosome(i);
/* 164 */       Gene[] genes = chrom.getGenes();
/* 165 */       IChromosome copyOfChromosome = null;
/*     */ 
/*     */       
/* 168 */       double d = generator.nextDouble();
/*     */ 
/*     */ 
/*     */       
/* 172 */       if (d >= 0.2538D) {
/* 173 */         geneIndex = 3;
/*     */       }
/* 175 */       else if (d >= 0.10454630000000004D) {
/* 176 */         geneIndex = 2;
/*     */       }
/* 178 */       else if (d >= 0.029919440000000033D) {
/* 179 */         geneIndex = 1;
/*     */       } else {
/*     */         
/* 182 */         geneIndex = 0;
/*     */       } 
/* 184 */       if (geneIndex >= genes.length) {
/* 185 */         geneIndex = genes.length - 1;
/*     */       }
/*     */       
/* 188 */       if (this.m_mutationRateCalc != null) {
/*     */ 
/*     */ 
/*     */         
/* 192 */         mutate = this.m_mutationRateCalc.toBePermutated(chrom, geneIndex);
/*     */       
/*     */       }
/*     */       else {
/*     */ 
/*     */         
/* 198 */         mutate = (generator.nextInt(this.m_mutationRate) == 0);
/*     */       } 
/* 200 */       if (mutate) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 209 */         if (constraint != null) {
/* 210 */           List<IChromosome> v = new Vector();
/* 211 */           v.add(chrom);
/* 212 */           if (!constraint.isValid(a_population, v, (GeneticOperator)this)) {
/*     */             continue;
/*     */           }
/*     */         } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 224 */         if (copyOfChromosome == null) {
/*     */ 
/*     */           
/* 227 */           copyOfChromosome = (IChromosome)chrom.clone();
/*     */ 
/*     */           
/* 230 */           a_candidateChromosomes.add(copyOfChromosome);
/*     */ 
/*     */           
/* 233 */           genes = copyOfChromosome.getGenes();
/*     */         } 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 239 */         if (genes[geneIndex] instanceof ICompositeGene) {
/* 240 */           ICompositeGene compositeGene = (ICompositeGene)genes[geneIndex];
/* 241 */           for (int k = 0; k < compositeGene.size(); k++) {
/* 242 */             mutateGene(compositeGene.geneAt(k), generator);
/*     */           }
/*     */         } else {
/*     */           
/* 246 */           mutateGene(genes[geneIndex], generator);
/*     */         } 
/*     */       } 
/*     */       continue;
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
/*     */   private void mutateGene(Gene a_gene, RandomGenerator a_generator) {
/* 261 */     for (int k = 0; k < a_gene.size(); k++) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 269 */       double percentage = -1.0D + a_generator.nextDouble() * 2.0D;
/*     */ 
/*     */       
/* 272 */       a_gene.applyMutation(k, percentage);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public IUniversalRateCalculator getMutationRateCalc() {
/* 283 */     return this.m_mutationRateCalc;
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
/*     */   public void setMutationRateCalc(IUniversalRateCalculator a_mutationRateCalc) {
/* 296 */     this.m_mutationRateCalc = a_mutationRateCalc;
/* 297 */     if (this.m_mutationRateCalc != null) {
/* 298 */       this.m_mutationRate = 0;
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
/*     */   public boolean equals(Object a_other) {
/*     */     try {
/* 315 */       return (compareTo(a_other) == 0);
/*     */     }
/* 317 */     catch (ClassCastException cex) {
/* 318 */       return false;
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
/* 334 */     if (a_other == null) {
/* 335 */       return 1;
/*     */     }
/* 337 */     TwoWayMutationOperator op = (TwoWayMutationOperator)a_other;
/* 338 */     if (this.m_mutationRateCalc == null) {
/* 339 */       if (op.m_mutationRateCalc != null) {
/* 340 */         return -1;
/*     */       
/*     */       }
/*     */     }
/* 344 */     else if (op.m_mutationRateCalc == null) {
/* 345 */       return 1;
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 350 */     if (this.m_mutationRate != op.m_mutationRate) {
/* 351 */       if (this.m_mutationRate > op.m_mutationRate) {
/* 352 */         return 1;
/*     */       }
/*     */       
/* 355 */       return -1;
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 360 */     return 0;
/*     */   }
/*     */   
/*     */   public int getMutationRate() {
/* 364 */     return this.m_mutationRate;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\impl\TwoWayMutationOperator.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */