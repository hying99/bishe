/*    */ package org.jgap.gp.impl;
/*    */ 
/*    */ import java.io.Serializable;
/*    */ import java.util.Arrays;
/*    */ import org.jgap.gp.IGPFitnessEvaluator;
/*    */ import org.jgap.gp.IGPProgram;
/*    */ import org.jgap.gp.INaturalGPSelector;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class FitnessProportionateSelection
/*    */   implements INaturalGPSelector, Serializable
/*    */ {
/*    */   private static final String CVS_REVISION = "$Revision: 1.6 $";
/*    */   
/*    */   public IGPProgram select(GPGenotype a_genotype) {
/* 45 */     double chosen = a_genotype.getGPConfiguration().getRandomGenerator().nextFloat() * a_genotype.getTotalFitness();
/*    */     
/* 47 */     int num = 0;
/* 48 */     GPPopulation pop = a_genotype.getGPPopulation();
/* 49 */     int popSize = pop.size();
/*    */ 
/*    */     
/* 52 */     IGPFitnessEvaluator evaluator = a_genotype.getGPConfiguration().getGPFitnessEvaluator();
/* 53 */     num = Arrays.binarySearch(pop.getFitnessRanks(), (float)chosen);
/* 54 */     if (num >= 0) {
/* 55 */       return pop.getGPProgram(num);
/*    */     }
/*    */ 
/*    */ 
/*    */     
/* 60 */     if (evaluator.isFitter(2.0D, 1.0D)) {
/* 61 */       boolean deltaMode = false;
/*    */     } else {
/*    */       
/* 64 */       boolean deltaMode = true;
/*    */     } 
/* 66 */     for (num = 1; num < popSize && 
/* 67 */       chosen >= pop.getFitnessRank(num); num++);
/*    */ 
/*    */ 
/*    */     
/* 71 */     num--;
/* 72 */     if (num >= popSize - 1) {
/* 73 */       if (popSize - 1 < 1) {
/* 74 */         num = 1;
/*    */       
/*    */       }
/*    */       else {
/*    */         
/* 79 */         num = a_genotype.getGPConfiguration().getRandomGenerator().nextInt(popSize - 1);
/*    */       } 
/*    */     }
/*    */     
/* 83 */     return pop.getGPProgram(num);
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\gp\impl\FitnessProportionateSelection.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */