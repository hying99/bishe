/*     */ package org.jgap.impl;
/*     */ 
/*     */ import java.io.Serializable;
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
/*     */ import org.jgap.data.config.Configurable;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class MutationOperator
/*     */   extends BaseGeneticOperator
/*     */   implements Configurable
/*     */ {
/*     */   private static final String CVS_REVISION = "$Revision: 1.44 $";
/*     */   private IUniversalRateCalculator m_mutationRateCalc;
/*  45 */   private MutationOperatorConfigurable m_config = new MutationOperatorConfigurable();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MutationOperator() throws InvalidConfigurationException {
/*  65 */     this(Genotype.getStaticConfiguration());
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
/*     */   public MutationOperator(Configuration a_conf) throws InvalidConfigurationException {
/*  77 */     super(a_conf);
/*  78 */     setMutationRateCalc((IUniversalRateCalculator)new DefaultMutationRateCalculator(a_conf));
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
/*     */   public MutationOperator(Configuration a_config, IUniversalRateCalculator a_mutationRateCalculator) throws InvalidConfigurationException {
/*  97 */     super(a_config);
/*  98 */     setMutationRateCalc(a_mutationRateCalculator);
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
/*     */   public MutationOperator(Configuration a_config, int a_desiredMutationRate) throws InvalidConfigurationException {
/* 118 */     super(a_config);
/* 119 */     this.m_config.m_mutationRate = a_desiredMutationRate;
/* 120 */     setMutationRateCalc(null);
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
/*     */   public void operate(Population a_population, List<IChromosome> a_candidateChromosomes) {
/* 138 */     if (a_population == null || a_candidateChromosomes == null) {
/*     */       return;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 144 */     if (this.m_config.m_mutationRate == 0 && this.m_mutationRateCalc == null) {
/*     */       return;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 154 */     boolean mutate = false;
/* 155 */     RandomGenerator generator = getConfiguration().getRandomGenerator();
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 160 */     int size = Math.min(getConfiguration().getPopulationSize(), a_population.size());
/*     */     
/* 162 */     IGeneticOperatorConstraint constraint = getConfiguration().getJGAPFactory().getGeneticOperatorConstraint();
/*     */     
/* 164 */     for (int i = 0; i < size; i++) {
/* 165 */       IChromosome chrom = a_population.getChromosome(i);
/* 166 */       Gene[] genes = chrom.getGenes();
/* 167 */       IChromosome copyOfChromosome = null;
/*     */ 
/*     */       
/* 170 */       for (int j = 0; j < genes.length; j++) {
/* 171 */         if (this.m_mutationRateCalc != null) {
/*     */ 
/*     */ 
/*     */           
/* 175 */           mutate = this.m_mutationRateCalc.toBePermutated(chrom, j);
/*     */         
/*     */         }
/*     */         else {
/*     */ 
/*     */           
/* 181 */           mutate = (generator.nextInt(this.m_config.m_mutationRate) == 0);
/*     */         } 
/* 183 */         if (mutate) {
/*     */ 
/*     */ 
/*     */           
/* 187 */           if (constraint != null) {
/* 188 */             List<IChromosome> v = new Vector();
/* 189 */             v.add(chrom);
/* 190 */             if (!constraint.isValid(a_population, v, (GeneticOperator)this)) {
/*     */               continue;
/*     */             }
/*     */           } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 202 */           if (copyOfChromosome == null) {
/*     */ 
/*     */             
/* 205 */             copyOfChromosome = (IChromosome)chrom.clone();
/*     */ 
/*     */             
/* 208 */             a_candidateChromosomes.add(copyOfChromosome);
/*     */ 
/*     */             
/* 211 */             genes = copyOfChromosome.getGenes();
/*     */           } 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 217 */           if (genes[j] instanceof ICompositeGene) {
/* 218 */             ICompositeGene compositeGene = (ICompositeGene)genes[j];
/* 219 */             for (int k = 0; k < compositeGene.size(); k++) {
/* 220 */               mutateGene(compositeGene.geneAt(k), generator);
/*     */             }
/*     */           } else {
/*     */             
/* 224 */             mutateGene(genes[j], generator);
/*     */           } 
/*     */         } 
/*     */         continue;
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
/*     */   private void mutateGene(Gene a_gene, RandomGenerator a_generator) {
/* 241 */     for (int k = 0; k < a_gene.size(); k++) {
/*     */ 
/*     */ 
/*     */       
/* 245 */       double percentage = -1.0D + a_generator.nextDouble() * 2.0D;
/*     */ 
/*     */       
/* 248 */       a_gene.applyMutation(k, percentage);
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
/* 259 */     return this.m_mutationRateCalc;
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
/*     */   public void setMutationRateCalc(IUniversalRateCalculator a_mutationRateCalc) {
/* 273 */     this.m_mutationRateCalc = a_mutationRateCalc;
/* 274 */     if (this.m_mutationRateCalc != null) {
/* 275 */       this.m_config.m_mutationRate = 0;
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
/*     */   public void setMutationRate(int a_mutationRate) {
/* 290 */     this.m_config.m_mutationRate = a_mutationRate;
/* 291 */     setMutationRateCalc(null);
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
/* 307 */       return (compareTo(a_other) == 0);
/* 308 */     } catch (ClassCastException cex) {
/* 309 */       return false;
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
/* 325 */     if (a_other == null) {
/* 326 */       return 1;
/*     */     }
/* 328 */     MutationOperator op = (MutationOperator)a_other;
/* 329 */     if (this.m_mutationRateCalc == null) {
/* 330 */       if (op.m_mutationRateCalc != null) {
/* 331 */         return -1;
/*     */       
/*     */       }
/*     */     }
/* 335 */     else if (op.m_mutationRateCalc == null) {
/* 336 */       return 1;
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 341 */     if (this.m_config.m_mutationRate != op.m_config.m_mutationRate) {
/* 342 */       if (this.m_config.m_mutationRate > op.m_config.m_mutationRate) {
/* 343 */         return 1;
/*     */       }
/*     */       
/* 346 */       return -1;
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 351 */     return 0;
/*     */   }
/*     */   
/*     */   public int getMutationRate() {
/* 355 */     return this.m_config.m_mutationRate;
/*     */   }
/*     */   
/*     */   class MutationOperatorConfigurable implements Serializable {
/*     */     public int m_mutationRate;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\impl\MutationOperator.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */