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
/*     */ 
/*     */ public class SwappingMutationOperator
/*     */   extends MutationOperator
/*     */ {
/*     */   private static final String CVS_REVISION = "$Revision: 1.19 $";
/*  36 */   private int m_startOffset = 1;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SwappingMutationOperator() throws InvalidConfigurationException {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SwappingMutationOperator(Configuration a_config) throws InvalidConfigurationException {
/*  61 */     super(a_config);
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
/*     */   public SwappingMutationOperator(Configuration a_config, IUniversalRateCalculator a_mutationRateCalculator) throws InvalidConfigurationException {
/*  81 */     super(a_config, a_mutationRateCalculator);
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
/*     */   public SwappingMutationOperator(Configuration a_config, int a_desiredMutationRate) throws InvalidConfigurationException {
/* 101 */     super(a_config, a_desiredMutationRate);
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
/* 120 */     IUniversalRateCalculator m_mutationRateCalc = getMutationRateCalc();
/*     */ 
/*     */ 
/*     */     
/* 124 */     if (getMutationRate() == 0 && m_mutationRateCalc == null) {
/*     */       return;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 132 */     if (m_mutationRateCalc != null) {
/* 133 */       currentRate = m_mutationRateCalc.calculateCurrentRate();
/*     */     } else {
/*     */       
/* 136 */       currentRate = getMutationRate();
/*     */     } 
/* 138 */     RandomGenerator generator = getConfiguration().getRandomGenerator();
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 143 */     int size = a_population.size();
/* 144 */     for (int i = 0; i < size; i++) {
/* 145 */       IChromosome x = a_population.getChromosome(i);
/*     */       
/* 147 */       IChromosome xm = operate(x, currentRate, generator);
/* 148 */       if (xm != null) {
/* 149 */         a_candidateChromosomes.add(xm);
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
/*     */   protected IChromosome operate(IChromosome a_x, int a_rate, RandomGenerator a_generator) {
/* 167 */     IChromosome chromosome = null;
/*     */     
/* 169 */     for (int j = this.m_startOffset; j < a_x.size(); j++) {
/*     */ 
/*     */       
/* 172 */       if (a_generator.nextInt(a_rate) == 0) {
/* 173 */         if (chromosome == null) {
/* 174 */           chromosome = (IChromosome)a_x.clone();
/*     */         }
/* 176 */         Gene[] genes = chromosome.getGenes();
/* 177 */         Gene[] mutated = operate(a_generator, j, genes);
/*     */ 
/*     */ 
/*     */         
/*     */         try {
/* 182 */           chromosome.setGenes(mutated);
/*     */         }
/* 184 */         catch (InvalidConfigurationException cex) {
/* 185 */           throw new Error("Gene type not allowed by constraint checker", cex);
/*     */         } 
/*     */       } 
/*     */     } 
/* 189 */     return chromosome;
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
/* 211 */     int other = this.m_startOffset + a_generator.nextInt(a_genes.length - this.m_startOffset);
/*     */     
/* 213 */     Gene t = a_genes[a_target_gene];
/* 214 */     a_genes[a_target_gene] = a_genes[other];
/* 215 */     a_genes[other] = t;
/* 216 */     return a_genes;
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
/* 231 */     this.m_startOffset = a_offset;
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
/* 246 */     return this.m_startOffset;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\impl\SwappingMutationOperator.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */