/*     */ package org.jgap.impl;
/*     */ 
/*     */ import gnu.trove.THashMap;
/*     */ import java.io.Serializable;
/*     */ import java.math.BigDecimal;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.jgap.Chromosome;
/*     */ import org.jgap.Configuration;
/*     */ import org.jgap.FitnessEvaluator;
/*     */ import org.jgap.Genotype;
/*     */ import org.jgap.IChromosome;
/*     */ import org.jgap.ICloneHandler;
/*     */ import org.jgap.NaturalSelector;
/*     */ import org.jgap.Population;
/*     */ import org.jgap.RandomGenerator;
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
/*     */ public class WeightedRouletteSelector
/*     */   extends NaturalSelector
/*     */   implements ICloneable
/*     */ {
/*     */   private static final String CVS_REVISION = "$Revision: 1.39 $";
/*     */   private static final double DELTA = 1.0E-6D;
/*  41 */   private static final BigDecimal ZERO_BIG_DECIMAL = new BigDecimal(0.0D);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  48 */   private THashMap m_wheel = new THashMap();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private double m_totalNumberOfUsedSlots;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private transient Pool m_counterPool;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  64 */   private WeightedRouletteSelConfig m_config = new WeightedRouletteSelConfig();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public WeightedRouletteSelector() {
/*  76 */     this(Genotype.getStaticConfiguration());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public WeightedRouletteSelector(Configuration a_config) {
/*  86 */     super(a_config);
/*  87 */     this.m_counterPool = new Pool();
/*  88 */     this.m_config.m_doublettesAllowed = false;
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
/*     */   protected synchronized void add(IChromosome a_chromosomeToAdd) {
/* 109 */     SlotCounter counter = (SlotCounter)this.m_wheel.get(a_chromosomeToAdd);
/* 110 */     if (counter != null) {
/*     */ 
/*     */       
/* 113 */       counter.increment();
/*     */ 
/*     */ 
/*     */     
/*     */     }
/*     */     else {
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 123 */       a_chromosomeToAdd.setIsSelectedForNextGeneration(false);
/*     */ 
/*     */ 
/*     */       
/* 127 */       counter = (SlotCounter)this.m_counterPool.acquirePooledObject();
/* 128 */       if (counter == null) {
/* 129 */         counter = new SlotCounter();
/*     */       }
/* 131 */       counter.reset(a_chromosomeToAdd.getFitnessValue());
/* 132 */       this.m_wheel.put(a_chromosomeToAdd, counter);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized void select(int a_howManyToSelect, Population a_from_pop, Population a_to_pop) {
/* 156 */     if (a_from_pop != null) {
/* 157 */       int size = a_from_pop.size();
/* 158 */       for (int k = 0; k < size; k++) {
/* 159 */         add(a_from_pop.getChromosome(k));
/*     */       }
/*     */     } 
/* 162 */     RandomGenerator generator = getConfiguration().getRandomGenerator();
/* 163 */     scaleFitnessValues();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 172 */     Set entries = this.m_wheel.entrySet();
/* 173 */     int numberOfEntries = entries.size();
/* 174 */     double[] fitnessValues = new double[numberOfEntries];
/* 175 */     double[] counterValues = new double[numberOfEntries];
/* 176 */     Chromosome[] arrayOfChromosome = new Chromosome[numberOfEntries];
/* 177 */     this.m_totalNumberOfUsedSlots = 0.0D;
/* 178 */     Iterator<Map.Entry> entryIterator = entries.iterator();
/* 179 */     for (int i = 0; i < numberOfEntries; i++) {
/* 180 */       Map.Entry chromosomeEntry = entryIterator.next();
/* 181 */       IChromosome currentChromosome = (IChromosome)chromosomeEntry.getKey();
/*     */       
/* 183 */       SlotCounter currentCounter = (SlotCounter)chromosomeEntry.getValue();
/*     */       
/* 185 */       fitnessValues[i] = currentCounter.getFitnessValue();
/* 186 */       counterValues[i] = fitnessValues[i] * currentCounter.getCounterValue();
/*     */       
/* 188 */       arrayOfChromosome[i] = (Chromosome)currentChromosome;
/*     */ 
/*     */ 
/*     */       
/* 192 */       this.m_totalNumberOfUsedSlots += counterValues[i];
/*     */     } 
/* 194 */     if (a_howManyToSelect > numberOfEntries && !getDoubletteChromosomesAllowed())
/*     */     {
/* 196 */       a_howManyToSelect = numberOfEntries;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 202 */     for (int j = 0; j < a_howManyToSelect; j++) {
/* 203 */       IChromosome selectedChromosome = spinWheel(generator, fitnessValues, counterValues, (IChromosome[])arrayOfChromosome);
/*     */       
/* 205 */       selectedChromosome.setIsSelectedForNextGeneration(true);
/* 206 */       if (a_to_pop.contains(selectedChromosome)) {
/* 207 */         ICloneHandler cloner = getConfiguration().getJGAPFactory().getCloneHandlerFor(selectedChromosome, null);
/*     */         
/* 209 */         if (cloner != null) {
/*     */           try {
/* 211 */             a_to_pop.addChromosome((IChromosome)cloner.perform(selectedChromosome, null, null));
/*     */           }
/* 213 */           catch (Exception ex) {
/* 214 */             ex.printStackTrace();
/* 215 */             a_to_pop.addChromosome(selectedChromosome);
/*     */           } 
/*     */         } else {
/*     */           
/* 219 */           a_to_pop.addChromosome(selectedChromosome);
/*     */         }
/*     */       
/*     */       }
/*     */       else {
/*     */         
/* 225 */         a_to_pop.addChromosome(selectedChromosome);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private IChromosome spinWheel(RandomGenerator a_generator, double[] a_fitnessValues, double[] a_counterValues, IChromosome[] a_chromosomes) {
/* 255 */     double selectedSlot = a_generator.nextDouble() * this.m_totalNumberOfUsedSlots;
/*     */     
/* 257 */     if (selectedSlot > this.m_totalNumberOfUsedSlots) {
/* 258 */       selectedSlot = this.m_totalNumberOfUsedSlots;
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
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 276 */     double currentSlot = 0.0D;
/* 277 */     FitnessEvaluator evaluator = getConfiguration().getFitnessEvaluator();
/* 278 */     boolean isFitter2_1 = evaluator.isFitter(2.0D, 1.0D);
/* 279 */     for (int i = 0; i < a_counterValues.length; i++) {
/*     */       boolean found;
/*     */ 
/*     */ 
/*     */       
/* 284 */       if (isFitter2_1) {
/*     */         
/* 286 */         found = (selectedSlot - currentSlot <= 1.0E-6D);
/*     */       }
/*     */       else {
/*     */         
/* 290 */         found = (Math.abs(currentSlot - selectedSlot) <= 1.0E-6D);
/*     */       } 
/* 292 */       if (found) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 298 */         if (!getDoubletteChromosomesAllowed()) {
/* 299 */           this.m_totalNumberOfUsedSlots -= a_counterValues[i];
/* 300 */           a_counterValues[i] = 0.0D;
/*     */         } else {
/*     */           
/* 303 */           a_counterValues[i] = a_counterValues[i] - a_fitnessValues[i];
/* 304 */           this.m_totalNumberOfUsedSlots -= a_fitnessValues[i];
/*     */         } 
/*     */         
/* 307 */         if (Math.abs(this.m_totalNumberOfUsedSlots) < 1.0E-6D) {
/* 308 */           this.m_totalNumberOfUsedSlots = 0.0D;
/*     */         }
/*     */ 
/*     */         
/* 312 */         return a_chromosomes[i];
/*     */       } 
/*     */       
/* 315 */       currentSlot += a_counterValues[i];
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 321 */     return a_chromosomes[a_counterValues.length - 1];
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
/*     */   public synchronized void empty() {
/* 334 */     this.m_counterPool.releaseAllObjects(this.m_wheel.values());
/*     */ 
/*     */     
/* 337 */     this.m_wheel.clear();
/* 338 */     this.m_totalNumberOfUsedSlots = 0.0D;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void scaleFitnessValues() {
/* 345 */     double largestFitnessValue = 0.0D;
/* 346 */     BigDecimal totalFitness = ZERO_BIG_DECIMAL;
/* 347 */     Iterator<SlotCounter> counterIterator = this.m_wheel.values().iterator();
/* 348 */     while (counterIterator.hasNext()) {
/* 349 */       SlotCounter counter = counterIterator.next();
/* 350 */       if (counter.getFitnessValue() > largestFitnessValue) {
/* 351 */         largestFitnessValue = counter.getFitnessValue();
/*     */       }
/* 353 */       BigDecimal counterFitness = new BigDecimal(counter.getFitnessValue());
/* 354 */       totalFitness = totalFitness.add(counterFitness.multiply(new BigDecimal(counter.getCounterValue())));
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 360 */     if (largestFitnessValue > 0.0D && totalFitness.floatValue() > 1.0E-7D) {
/*     */       
/* 362 */       double scalingFactor = totalFitness.divide(new BigDecimal(largestFitnessValue), 4).doubleValue();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 368 */       counterIterator = this.m_wheel.values().iterator();
/* 369 */       while (counterIterator.hasNext()) {
/* 370 */         SlotCounter counter = counterIterator.next();
/* 371 */         counter.scaleFitnessValue(scalingFactor);
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
/*     */   public boolean returnsUniqueChromosomes() {
/* 383 */     return false;
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
/*     */   public void setDoubletteChromosomesAllowed(boolean a_doublettesAllowed) {
/* 396 */     this.m_config.m_doublettesAllowed = a_doublettesAllowed;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean getDoubletteChromosomesAllowed() {
/* 406 */     return this.m_config.m_doublettesAllowed;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object clone() {
/* 416 */     WeightedRouletteSelector result = new WeightedRouletteSelector(getConfiguration());
/*     */     
/* 418 */     result.m_wheel = this.m_wheel.clone();
/* 419 */     result.m_config = new WeightedRouletteSelConfig();
/* 420 */     result.m_config.m_doublettesAllowed = this.m_config.m_doublettesAllowed;
/* 421 */     return result;
/*     */   }
/*     */   
/*     */   public boolean equals(Object o) {
/* 425 */     WeightedRouletteSelector other = (WeightedRouletteSelector)o;
/* 426 */     if (other == null) {
/* 427 */       return false;
/*     */     }
/* 429 */     if (this.m_totalNumberOfUsedSlots != other.m_totalNumberOfUsedSlots) {
/* 430 */       return false;
/*     */     }
/* 432 */     if (other.m_config == null) {
/* 433 */       return false;
/*     */     }
/* 435 */     if (this.m_config.m_doublettesAllowed != other.m_config.m_doublettesAllowed) {
/* 436 */       return false;
/*     */     }
/* 438 */     if (other.m_counterPool == null) {
/* 439 */       return false;
/*     */     }
/* 441 */     if (!this.m_wheel.equals(other.m_wheel)) {
/* 442 */       return false;
/*     */     }
/* 444 */     return true;
/*     */   }
/*     */   
/*     */   class WeightedRouletteSelConfig implements Serializable {
/*     */     public boolean m_doublettesAllowed;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\impl\WeightedRouletteSelector.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */