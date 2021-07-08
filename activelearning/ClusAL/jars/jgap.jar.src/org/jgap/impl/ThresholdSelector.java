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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ThresholdSelector
/*     */   extends NaturalSelector
/*     */ {
/*     */   private static final String CVS_REVISION = "$Revision: 1.18 $";
/*     */   private List m_chromosomes;
/*     */   private boolean m_needsSorting;
/*     */   private FitnessValueComparator m_fitnessValueComparator;
/*  42 */   private ThresholdSelectorConfigurable m_config = new ThresholdSelectorConfigurable();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ThresholdSelector() {
/*  54 */     this(Genotype.getStaticConfiguration(), 0.3D);
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
/*     */   public ThresholdSelector(Configuration a_config, double a_bestChromosomes_Percentage) {
/*  69 */     super(a_config);
/*  70 */     if (a_bestChromosomes_Percentage < 0.0D || a_bestChromosomes_Percentage > 1.0D)
/*     */     {
/*  72 */       throw new IllegalArgumentException("Percentage must be between 0.0 and 1.0 !");
/*     */     }
/*     */     
/*  75 */     this.m_config.m_bestChroms_Percentage = a_bestChromosomes_Percentage;
/*  76 */     this.m_chromosomes = new Vector();
/*  77 */     this.m_needsSorting = false;
/*  78 */     this.m_fitnessValueComparator = new FitnessValueComparator();
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
/*     */   public void select(int a_howManyToSelect, Population a_from_pop, Population a_to_pop) {
/*     */     int canBeSelected;
/*  95 */     if (a_from_pop != null) {
/*  96 */       int k = a_from_pop.size();
/*  97 */       for (int m = 0; m < k; m++) {
/*  98 */         add(a_from_pop.getChromosome(m));
/*     */       }
/*     */     } 
/*     */     
/* 102 */     if (a_howManyToSelect > this.m_chromosomes.size()) {
/* 103 */       canBeSelected = this.m_chromosomes.size();
/*     */     } else {
/*     */       
/* 106 */       canBeSelected = a_howManyToSelect;
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 111 */     if (this.m_needsSorting) {
/* 112 */       Collections.sort(this.m_chromosomes, this.m_fitnessValueComparator);
/* 113 */       this.m_needsSorting = false;
/*     */     } 
/*     */     
/* 116 */     int bestToBeSelected = (int)Math.round(canBeSelected * this.m_config.m_bestChroms_Percentage);
/*     */     
/* 118 */     for (int i = 0; i < bestToBeSelected; i++) {
/* 119 */       a_to_pop.addChromosome(this.m_chromosomes.get(i));
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 124 */     int missing = a_howManyToSelect - bestToBeSelected;
/* 125 */     RandomGenerator rn = getConfiguration().getRandomGenerator();
/*     */     
/* 127 */     int size = this.m_chromosomes.size();
/* 128 */     for (int j = 0; j < missing; j++) {
/* 129 */       int index = rn.nextInt(size);
/* 130 */       IChromosome chrom = this.m_chromosomes.get(index);
/* 131 */       a_to_pop.addChromosome(chrom);
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
/* 142 */     return false;
/*     */   }
/*     */   
/*     */   public void empty() {
/* 146 */     this.m_chromosomes.clear();
/* 147 */     this.m_needsSorting = false;
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
/* 158 */     this.m_chromosomes.add(a_chromosomeToAdd);
/* 159 */     this.m_needsSorting = true;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private class FitnessValueComparator
/*     */     implements Comparator
/*     */   {
/*     */     private FitnessValueComparator() {}
/*     */ 
/*     */ 
/*     */     
/*     */     public int compare(Object a_first, Object a_second) {
/* 172 */       IChromosome chrom1 = (IChromosome)a_first;
/* 173 */       IChromosome chrom2 = (IChromosome)a_second;
/* 174 */       if (ThresholdSelector.this.getConfiguration().getFitnessEvaluator().isFitter(chrom2.getFitnessValue(), chrom1.getFitnessValue()))
/*     */       {
/* 176 */         return 1;
/*     */       }
/* 178 */       if (ThresholdSelector.this.getConfiguration().getFitnessEvaluator().isFitter(chrom1.getFitnessValue(), chrom2.getFitnessValue()))
/*     */       {
/* 180 */         return -1;
/*     */       }
/*     */       
/* 183 */       return 0;
/*     */     }
/*     */   }
/*     */   
/*     */   class ThresholdSelectorConfigurable {
/*     */     public double m_bestChroms_Percentage;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\impl\ThresholdSelector.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */