/*     */ package org.jgap.impl;
/*     */ 
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.List;
/*     */ import java.util.Vector;
/*     */ import org.jgap.Configuration;
/*     */ import org.jgap.Genotype;
/*     */ import org.jgap.IChromosome;
/*     */ import org.jgap.NaturalSelector;
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
/*     */ public class TournamentSelector
/*     */   extends NaturalSelector
/*     */ {
/*     */   private static final String CVS_REVISION = "$Revision: 1.22 $";
/*  30 */   private TournamentSelectorConfigurable m_config = new TournamentSelectorConfigurable();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private List m_chromosomes;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private FitnessValueComparator m_fitnessValueComparator;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TournamentSelector() {
/*  49 */     super(Genotype.getStaticConfiguration());
/*  50 */     init();
/*     */   }
/*     */   
/*     */   private void init() {
/*  54 */     this.m_chromosomes = new Vector();
/*  55 */     this.m_fitnessValueComparator = new FitnessValueComparator();
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
/*     */   public TournamentSelector(Configuration a_config, int a_tournament_size, double a_probability) {
/*  69 */     super(a_config);
/*  70 */     init();
/*  71 */     if (a_tournament_size < 1) {
/*  72 */       throw new IllegalArgumentException("Tournament size must be at least 1!");
/*     */     }
/*  74 */     if (a_probability <= 0.0D || a_probability > 1.0D) {
/*  75 */       throw new IllegalArgumentException("Probability must be greater 0.0 and less or equal than 1.0!");
/*     */     }
/*     */     
/*  78 */     this.m_config.m_tournament_size = a_tournament_size;
/*  79 */     this.m_config.m_probability = a_probability;
/*     */   }
/*     */   
/*     */   public void setTournamentSize(int a_tournament_size) {
/*  83 */     if (a_tournament_size < 1) {
/*  84 */       throw new IllegalArgumentException("Tournament size must be at least 1!");
/*     */     }
/*  86 */     this.m_config.m_tournament_size = a_tournament_size;
/*     */   }
/*     */   
/*     */   public int getTournamentSize() {
/*  90 */     return this.m_config.m_tournament_size;
/*     */   }
/*     */   
/*     */   public double getProbability() {
/*  94 */     return this.m_config.m_probability;
/*     */   }
/*     */   
/*     */   public void setProbability(double a_probability) {
/*  98 */     if (a_probability <= 0.0D || a_probability > 1.0D) {
/*  99 */       throw new IllegalArgumentException("Probability must be greater 0.0 and less or equal than 1.0!");
/*     */     }
/*     */     
/* 102 */     this.m_config.m_probability = a_probability;
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
/*     */   public void select(int a_howManyToSelect, Population a_from_pop, Population a_to_pop) {
/* 119 */     if (a_from_pop != null) {
/* 120 */       for (int j = 0; j < a_from_pop.size(); j++) {
/* 121 */         add(a_from_pop.getChromosome(j));
/*     */       }
/*     */     }
/* 124 */     List<?> tournament = new Vector();
/* 125 */     RandomGenerator rn = getConfiguration().getRandomGenerator();
/* 126 */     int size = this.m_chromosomes.size();
/* 127 */     if (size == 0) {
/*     */       return;
/*     */     }
/*     */     
/* 131 */     for (int i = 0; i < a_howManyToSelect; i++) {
/*     */       
/* 133 */       tournament.clear();
/* 134 */       for (int j = 0; j < this.m_config.m_tournament_size; j++) {
/* 135 */         int k = rn.nextInt(size);
/* 136 */         tournament.add(this.m_chromosomes.get(k));
/*     */       } 
/* 138 */       Collections.sort(tournament, this.m_fitnessValueComparator);
/* 139 */       double prob = rn.nextDouble();
/* 140 */       double probAccumulated = this.m_config.m_probability;
/* 141 */       int index = 0;
/*     */ 
/*     */       
/* 144 */       if (this.m_config.m_tournament_size > 1)
/*     */       {
/* 146 */         while (prob > probAccumulated) {
/*     */ 
/*     */ 
/*     */           
/* 150 */           probAccumulated += probAccumulated * (1.0D - this.m_config.m_probability);
/* 151 */           index++;
/*     */ 
/*     */           
/* 154 */           if (index >= this.m_config.m_tournament_size - 1)
/*     */             break; 
/* 156 */         }  }  a_to_pop.addChromosome((IChromosome)tournament.get(index));
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean returnsUniqueChromosomes() {
/* 167 */     return false;
/*     */   }
/*     */   
/*     */   public void empty() {
/* 171 */     this.m_chromosomes.clear();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void add(IChromosome a_chromosomeToAdd) {
/* 182 */     this.m_chromosomes.add(a_chromosomeToAdd);
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
/*     */   private class FitnessValueComparator
/*     */     implements Comparator
/*     */   {
/*     */     public int compare(Object a_first, Object a_second) {
/* 199 */       IChromosome chrom1 = (IChromosome)a_first;
/* 200 */       IChromosome chrom2 = (IChromosome)a_second;
/* 201 */       if (TournamentSelector.this.getConfiguration().getFitnessEvaluator().isFitter(chrom2.getFitnessValue(), chrom1.getFitnessValue()))
/*     */       {
/* 203 */         return 1;
/*     */       }
/* 205 */       if (TournamentSelector.this.getConfiguration().getFitnessEvaluator().isFitter(chrom1.getFitnessValue(), chrom2.getFitnessValue()))
/*     */       {
/* 207 */         return -1;
/*     */       }
/*     */       
/* 210 */       return 0;
/*     */     }
/*     */   }
/*     */   
/*     */   class TournamentSelectorConfigurable {
/*     */     public double m_probability;
/*     */     public int m_tournament_size;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\impl\TournamentSelector.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */