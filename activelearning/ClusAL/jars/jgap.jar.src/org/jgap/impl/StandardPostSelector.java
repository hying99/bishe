/*     */ package org.jgap.impl;
/*     */ 
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.Iterator;
/*     */ import org.jgap.Configuration;
/*     */ import org.jgap.Genotype;
/*     */ import org.jgap.IChromosome;
/*     */ import org.jgap.InvalidConfigurationException;
/*     */ import org.jgap.NaturalSelector;
/*     */ import org.jgap.Population;
/*     */ import org.jgap.util.CloneException;
/*     */ import org.jgap.util.ICloneable;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class StandardPostSelector
/*     */   extends NaturalSelector
/*     */   implements ICloneable
/*     */ {
/*     */   private static final String CVS_REVISION = "$Revision: 1.2 $";
/*     */   private Population m_chromosomes;
/*     */   private boolean m_needsSorting;
/*     */   private NaturalSelector.FitnessValueComparator m_fitnessValueComparator;
/*     */   
/*     */   public StandardPostSelector() throws InvalidConfigurationException {
/*  58 */     this(Genotype.getStaticConfiguration());
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
/*     */   public StandardPostSelector(Configuration a_config) throws InvalidConfigurationException {
/*  72 */     super(a_config);
/*  73 */     this.m_chromosomes = new Population(a_config);
/*  74 */     this.m_needsSorting = false;
/*  75 */     this.m_fitnessValueComparator = new NaturalSelector.FitnessValueComparator(this);
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
/*     */   protected void add(IChromosome a_chromosomeToAdd) {
/*  88 */     a_chromosomeToAdd.setIsSelectedForNextGeneration(false);
/*  89 */     this.m_chromosomes.addChromosome(a_chromosomeToAdd);
/*     */ 
/*     */     
/*  92 */     this.m_needsSorting = true;
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
/*     */     int canBeSelected;
/* 110 */     if (a_from_pop != null) {
/* 111 */       int popSize = a_from_pop.size();
/* 112 */       if (popSize < 1) {
/* 113 */         throw new IllegalStateException("Population size must be greater 0");
/*     */       }
/* 115 */       for (int k = 0; k < popSize; k++) {
/* 116 */         add(a_from_pop.getChromosome(k));
/*     */       }
/*     */     } 
/*     */     
/* 120 */     int chromsSize = this.m_chromosomes.size();
/* 121 */     if (chromsSize < 1) {
/* 122 */       throw new IllegalStateException("Number of chromosomes must be greater 0");
/*     */     }
/*     */     
/* 125 */     if (a_howManyToSelect > chromsSize) {
/* 126 */       canBeSelected = chromsSize;
/*     */     } else {
/*     */       
/* 129 */       canBeSelected = a_howManyToSelect;
/*     */     } 
/* 131 */     int neededSize = a_howManyToSelect;
/*     */ 
/*     */     
/* 134 */     Iterator<IChromosome> it = this.m_chromosomes.iterator();
/* 135 */     while (it.hasNext()) {
/* 136 */       IChromosome c = it.next();
/* 137 */       if (Math.abs(c.getFitnessValueDirectly() - -1.0D) < 1.0E-7D) {
/*     */ 
/*     */         
/* 140 */         a_to_pop.addChromosome(c);
/* 141 */         it.remove();
/* 142 */         canBeSelected--;
/* 143 */         if (canBeSelected < 1) {
/*     */           break;
/*     */         }
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 152 */     if (this.m_needsSorting && canBeSelected > 0) {
/* 153 */       Collections.sort(this.m_chromosomes.getChromosomes(), (Comparator<?>)this.m_fitnessValueComparator);
/*     */       
/* 155 */       this.m_needsSorting = false;
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 160 */     for (int i = 0; i < canBeSelected; i++) {
/* 161 */       IChromosome selectedChromosome = this.m_chromosomes.getChromosome(i);
/* 162 */       selectedChromosome.setIsSelectedForNextGeneration(true);
/* 163 */       a_to_pop.addChromosome(selectedChromosome);
/*     */     } 
/*     */     
/* 166 */     int toAdd = neededSize - a_to_pop.size();
/*     */ 
/*     */ 
/*     */     
/* 170 */     for (int j = 0; j < toAdd; j++) {
/* 171 */       IChromosome selectedChromosome = this.m_chromosomes.getChromosome(j % chromsSize);
/* 172 */       selectedChromosome.setIsSelectedForNextGeneration(true);
/* 173 */       a_to_pop.addChromosome(selectedChromosome);
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
/*     */   public void empty() {
/* 186 */     this.m_chromosomes.getChromosomes().clear();
/* 187 */     this.m_needsSorting = false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean returnsUniqueChromosomes() {
/* 197 */     return true;
/*     */   }
/*     */   
/*     */   public boolean equals(Object a_o) {
/* 201 */     if (a_o == null) {
/* 202 */       return false;
/*     */     }
/* 204 */     StandardPostSelector other = (StandardPostSelector)a_o;
/* 205 */     if (!this.m_fitnessValueComparator.getClass().getName().equals(other.m_fitnessValueComparator.getClass().getName()))
/*     */     {
/* 207 */       return false;
/*     */     }
/* 209 */     if (!this.m_chromosomes.equals(other.m_chromosomes)) {
/* 210 */       return false;
/*     */     }
/* 212 */     return true;
/*     */   }
/*     */   
/*     */   public Object clone() {
/*     */     try {
/* 217 */       StandardPostSelector sel = new StandardPostSelector(getConfiguration());
/* 218 */       sel.m_needsSorting = this.m_needsSorting;
/* 219 */       return sel;
/* 220 */     } catch (Throwable t) {
/* 221 */       throw new CloneException(t);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\impl\StandardPostSelector.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */