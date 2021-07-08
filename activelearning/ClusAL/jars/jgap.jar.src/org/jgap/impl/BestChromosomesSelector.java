/*     */ package org.jgap.impl;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import org.jgap.Configuration;
/*     */ import org.jgap.Genotype;
/*     */ import org.jgap.IChromosome;
/*     */ import org.jgap.ICloneHandler;
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
/*     */ public class BestChromosomesSelector
/*     */   extends NaturalSelector
/*     */   implements ICloneable
/*     */ {
/*     */   private static final String CVS_REVISION = "$Revision: 1.49 $";
/*     */   private Population m_chromosomes;
/*     */   private boolean m_doublettesAllowed;
/*     */   private boolean m_needsSorting;
/*     */   private NaturalSelector.FitnessValueComparator m_fitnessValueComparator;
/*  49 */   private BestChromosomesSelectorConfig m_config = new BestChromosomesSelectorConfig();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BestChromosomesSelector() throws InvalidConfigurationException {
/*  64 */     this(Genotype.getStaticConfiguration());
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
/*     */   public BestChromosomesSelector(Configuration a_config) throws InvalidConfigurationException {
/*  78 */     this(a_config, 1.0D);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public BestChromosomesSelector(Configuration a_config, double a_originalRate) throws InvalidConfigurationException {
/*  84 */     super(a_config);
/*  85 */     this.m_chromosomes = new Population(a_config);
/*  86 */     this.m_needsSorting = false;
/*  87 */     this.m_doublettesAllowed = true;
/*  88 */     setOriginalRate(a_originalRate);
/*  89 */     this.m_fitnessValueComparator = new NaturalSelector.FitnessValueComparator(this);
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
/*     */   protected void add(IChromosome a_chromosomeToAdd) {
/* 103 */     if (!getDoubletteChromosomesAllowed() && this.m_chromosomes.getChromosomes().contains(a_chromosomeToAdd)) {
/*     */       return;
/*     */     }
/*     */ 
/*     */     
/* 108 */     a_chromosomeToAdd.setIsSelectedForNextGeneration(false);
/* 109 */     if (getDoubletteChromosomesAllowed()) {
/* 110 */       ICloneHandler cloner = getConfiguration().getJGAPFactory().getCloneHandlerFor(a_chromosomeToAdd, null);
/*     */       
/* 112 */       if (cloner != null) {
/*     */         try {
/* 114 */           this.m_chromosomes.addChromosome((IChromosome)cloner.perform(a_chromosomeToAdd, null, null));
/*     */         }
/* 116 */         catch (Exception ex) {
/* 117 */           ex.printStackTrace();
/* 118 */           this.m_chromosomes.addChromosome(a_chromosomeToAdd);
/*     */         } 
/*     */       } else {
/*     */         
/* 122 */         this.m_chromosomes.addChromosome(a_chromosomeToAdd);
/*     */       } 
/*     */     } else {
/*     */       
/* 126 */       this.m_chromosomes.addChromosome(a_chromosomeToAdd);
/*     */     } 
/*     */ 
/*     */     
/* 130 */     this.m_needsSorting = true;
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
/* 148 */     if (a_from_pop != null) {
/* 149 */       int popSize = a_from_pop.size();
/* 150 */       if (popSize < 1) {
/* 151 */         throw new IllegalStateException("Population size must be greater 0");
/*     */       }
/* 153 */       for (int j = 0; j < popSize; j++) {
/* 154 */         add(a_from_pop.getChromosome(j));
/*     */       }
/*     */     } 
/*     */     
/* 158 */     int chromsSize = this.m_chromosomes.size();
/* 159 */     if (chromsSize < 1) {
/* 160 */       throw new IllegalStateException("Number of chromosomes must be greater 0");
/*     */     }
/* 162 */     if (a_howManyToSelect > chromsSize) {
/* 163 */       canBeSelected = chromsSize;
/*     */     } else {
/*     */       
/* 166 */       canBeSelected = a_howManyToSelect;
/*     */     } 
/* 168 */     int neededSize = a_howManyToSelect;
/*     */     
/* 170 */     double origRate = this.m_config.m_originalRate;
/* 171 */     if (origRate < 1.0D) {
/* 172 */       canBeSelected = (int)Math.round(canBeSelected * origRate);
/*     */       
/* 174 */       if (canBeSelected < 1) {
/* 175 */         canBeSelected = 1;
/*     */       }
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 181 */     if (this.m_needsSorting) {
/* 182 */       Collections.sort(this.m_chromosomes.getChromosomes(), (Comparator<?>)this.m_fitnessValueComparator);
/*     */       
/* 184 */       this.m_needsSorting = false;
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 189 */     for (int i = 0; i < canBeSelected; i++) {
/* 190 */       IChromosome selectedChromosome = this.m_chromosomes.getChromosome(i);
/* 191 */       selectedChromosome.setIsSelectedForNextGeneration(true);
/* 192 */       a_to_pop.addChromosome(selectedChromosome);
/*     */     } 
/* 194 */     if (getDoubletteChromosomesAllowed()) {
/*     */       
/* 196 */       int toAdd = neededSize - a_to_pop.size();
/*     */ 
/*     */ 
/*     */       
/* 200 */       for (int j = 0; j < toAdd; j++) {
/* 201 */         IChromosome selectedChromosome = this.m_chromosomes.getChromosome(j % chromsSize);
/* 202 */         ICloneHandler cloner = getConfiguration().getJGAPFactory().getCloneHandlerFor(selectedChromosome, null);
/*     */         
/* 204 */         if (cloner != null) {
/*     */           try {
/* 206 */             selectedChromosome = (IChromosome)cloner.perform(selectedChromosome, null, null);
/*     */           }
/* 208 */           catch (Exception ex) {
/* 209 */             ex.printStackTrace();
/*     */           } 
/*     */         }
/* 212 */         selectedChromosome.setIsSelectedForNextGeneration(true);
/* 213 */         a_to_pop.addChromosome(selectedChromosome);
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
/*     */   public void empty() {
/* 227 */     this.m_chromosomes.getChromosomes().clear();
/* 228 */     this.m_needsSorting = false;
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
/*     */   public void setDoubletteChromosomesAllowed(boolean a_doublettesAllowed) {
/* 243 */     this.m_doublettesAllowed = a_doublettesAllowed;
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
/*     */   public boolean getDoubletteChromosomesAllowed() {
/* 255 */     return this.m_doublettesAllowed;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean returnsUniqueChromosomes() {
/* 265 */     return true;
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
/*     */   public void setOriginalRate(double a_originalRate) {
/* 283 */     if (a_originalRate < 0.0D || a_originalRate > 1.0D) {
/* 284 */       throw new IllegalArgumentException("Original rate must be greater than zero and not greater than one!");
/*     */     }
/*     */     
/* 287 */     this.m_config.m_originalRate = a_originalRate;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getOriginalRate() {
/* 298 */     return this.m_config.m_originalRate;
/*     */   }
/*     */ 
/*     */   
/*     */   class BestChromosomesSelectorConfig
/*     */     implements Serializable
/*     */   {
/*     */     public double m_originalRate;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object a_o) {
/* 310 */     if (a_o == null) {
/* 311 */       return false;
/*     */     }
/* 313 */     BestChromosomesSelector other = (BestChromosomesSelector)a_o;
/* 314 */     if (this.m_doublettesAllowed != other.m_doublettesAllowed) {
/* 315 */       return false;
/*     */     }
/* 317 */     if (!this.m_fitnessValueComparator.getClass().getName().equals(other.m_fitnessValueComparator.getClass().getName()))
/*     */     {
/* 319 */       return false;
/*     */     }
/* 321 */     if (Math.abs(this.m_config.m_originalRate - other.m_config.m_originalRate) > 0.001D)
/*     */     {
/* 323 */       return false;
/*     */     }
/* 325 */     if (!this.m_chromosomes.equals(other.m_chromosomes)) {
/* 326 */       return false;
/*     */     }
/* 328 */     return true;
/*     */   }
/*     */   
/*     */   public Object clone() {
/*     */     try {
/* 333 */       BestChromosomesSelector sel = new BestChromosomesSelector(getConfiguration(), this.m_config.m_originalRate);
/*     */       
/* 335 */       sel.m_needsSorting = this.m_needsSorting;
/*     */       
/* 337 */       sel.m_doublettesAllowed = this.m_doublettesAllowed;
/* 338 */       return sel;
/* 339 */     } catch (Throwable t) {
/* 340 */       throw new CloneException(t);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\impl\BestChromosomesSelector.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */