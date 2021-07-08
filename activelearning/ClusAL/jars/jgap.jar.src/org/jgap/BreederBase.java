/*     */ package org.jgap;
/*     */ 
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class BreederBase
/*     */   implements IBreeder
/*     */ {
/*     */   private static final String CVS_REVISION = "$Revision: 1.6 $";
/*     */   
/*     */   protected Population applyNaturalSelectors(Configuration a_config, Population a_pop, boolean a_processBeforeGeneticOperators) {
/*     */     try {
/*  48 */       int selectorSize = a_config.getNaturalSelectorsSize(a_processBeforeGeneticOperators);
/*     */       
/*  50 */       if (selectorSize > 0) {
/*  51 */         int population_size = a_config.getPopulationSize();
/*     */ 
/*     */         
/*  54 */         population_size = (int)Math.round(population_size * a_config.getSelectFromPrevGen());
/*     */ 
/*     */         
/*  57 */         Population new_population = new Population(a_config, population_size);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/*  63 */         for (int i = 0; i < selectorSize; i++) {
/*  64 */           int single_selection_size; NaturalSelector selector = a_config.getNaturalSelector(a_processBeforeGeneticOperators, i);
/*     */           
/*  66 */           if (i == selectorSize - 1 && i > 0) {
/*     */ 
/*     */             
/*  69 */             single_selection_size = population_size - a_pop.size();
/*     */           } else {
/*     */             
/*  72 */             single_selection_size = population_size / selectorSize;
/*     */           } 
/*     */ 
/*     */ 
/*     */           
/*  77 */           selector.select(single_selection_size, a_pop, new_population);
/*     */ 
/*     */           
/*  80 */           selector.empty();
/*     */         } 
/*  82 */         return new_population;
/*     */       } 
/*     */       
/*  85 */       return a_pop;
/*     */     }
/*  87 */     catch (InvalidConfigurationException iex) {
/*     */ 
/*     */       
/*  90 */       throw new IllegalStateException(iex);
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
/*     */   protected void applyGeneticOperators(Configuration a_config, Population a_pop) {
/* 104 */     List geneticOperators = a_config.getGeneticOperators();
/* 105 */     Iterator<GeneticOperator> operatorIterator = geneticOperators.iterator();
/* 106 */     while (operatorIterator.hasNext()) {
/* 107 */       GeneticOperator operator = operatorIterator.next();
/*     */       
/* 109 */       operator.operate(a_pop, a_pop.getChromosomes());
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
/*     */   public abstract Object clone();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int compareTo(Object a_other) {
/* 129 */     if (a_other == null) {
/* 130 */       return 1;
/*     */     }
/* 132 */     return getClass().getName().compareTo(a_other.getClass().getName());
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\BreederBase.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */