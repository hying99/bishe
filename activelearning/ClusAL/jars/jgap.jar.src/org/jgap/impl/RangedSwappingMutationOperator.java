/*     */ package org.jgap.impl;
/*     */ 
/*     */ import java.util.List;
/*     */ import org.jgap.Configuration;
/*     */ import org.jgap.Gene;
/*     */ import org.jgap.IChromosome;
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
/*     */ public class RangedSwappingMutationOperator
/*     */   extends MutationOperator
/*     */ {
/*     */   private static final String CVS_REVISION = "$Revision: 1.1 $";
/*  35 */   private int m_startOffset = 0;
/*     */   
/*  37 */   private int m_range = 0;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public RangedSwappingMutationOperator() throws InvalidConfigurationException {
/*  52 */     this.m_range = 0;
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
/*     */   public RangedSwappingMutationOperator(Configuration a_config, int a_range) throws InvalidConfigurationException {
/*  68 */     super(a_config);
/*  69 */     this.m_range = a_range;
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
/*     */   public RangedSwappingMutationOperator(Configuration a_config, IUniversalRateCalculator a_mutationRateCalculator, int a_range) throws InvalidConfigurationException {
/*  92 */     super(a_config, a_mutationRateCalculator);
/*  93 */     this.m_range = a_range;
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
/*     */   public RangedSwappingMutationOperator(Configuration a_config, int a_desiredMutationRate, int a_range) throws InvalidConfigurationException {
/* 117 */     super(a_config, a_desiredMutationRate);
/* 118 */     this.m_range = a_range;
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
/*     */     int currentRate;
/* 137 */     IUniversalRateCalculator m_mutationRateCalc = getMutationRateCalc();
/*     */ 
/*     */ 
/*     */     
/* 141 */     if (getMutationRate() == 0 && m_mutationRateCalc == null) {
/*     */       return;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 149 */     if (m_mutationRateCalc != null) {
/* 150 */       currentRate = m_mutationRateCalc.calculateCurrentRate();
/*     */     } else {
/*     */       
/* 153 */       currentRate = getMutationRate();
/*     */     } 
/* 155 */     RandomGenerator generator = getConfiguration().getRandomGenerator();
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 160 */     int size = a_population.size();
/* 161 */     for (int i = 0; i < size; i++) {
/* 162 */       IChromosome x = a_population.getChromosome(i);
/*     */       
/* 164 */       IChromosome xm = operate(x, currentRate, generator);
/* 165 */       if (xm != null) {
/* 166 */         a_candidateChromosomes.add(xm);
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected IChromosome operate(IChromosome a_x, int a_rate, RandomGenerator a_generator) {
/* 185 */     IChromosome chromosome = null;
/*     */     
/* 187 */     for (int j = this.m_startOffset; j < a_x.size(); j++) {
/*     */ 
/*     */       
/* 190 */       if (a_generator.nextInt(a_rate) == 0) {
/* 191 */         if (chromosome == null) {
/* 192 */           chromosome = (IChromosome)a_x.clone();
/*     */         }
/* 194 */         Gene[] genes = chromosome.getGenes();
/* 195 */         if (this.m_range == 0) {
/* 196 */           this.m_range = genes.length;
/*     */         }
/* 198 */         Gene[] mutated = operate(a_generator, j, genes);
/*     */ 
/*     */ 
/*     */         
/*     */         try {
/* 203 */           chromosome.setGenes(mutated);
/* 204 */         } catch (InvalidConfigurationException cex) {
/* 205 */           throw new Error("Gene type not allowed by constraint checker", cex);
/*     */         } 
/*     */       } 
/*     */     } 
/* 209 */     return chromosome;
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
/*     */   protected Gene[] operate(RandomGenerator a_generator, int a_target_gene, Gene[] a_genes) {
/* 231 */     int rand = a_generator.nextInt(2 * this.m_range);
/* 232 */     int other = a_target_gene - this.m_range + rand;
/* 233 */     if (other < 0) {
/* 234 */       other = 0;
/*     */     }
/* 236 */     if (other >= a_genes.length) {
/* 237 */       other = a_genes.length - 1;
/*     */     }
/* 239 */     Gene t = a_genes[a_target_gene];
/* 240 */     a_genes[a_target_gene] = a_genes[other];
/* 241 */     a_genes[other] = t;
/* 242 */     return a_genes;
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
/*     */   public void setStartOffset(int a_offset) {
/* 257 */     this.m_startOffset = a_offset;
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
/*     */   public int getStartOffset() {
/* 272 */     return this.m_startOffset;
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
/*     */   public void setRange(int a_range) {
/* 285 */     this.m_range = a_range;
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
/*     */   public int getRange() {
/* 297 */     return this.m_range;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\impl\RangedSwappingMutationOperator.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */