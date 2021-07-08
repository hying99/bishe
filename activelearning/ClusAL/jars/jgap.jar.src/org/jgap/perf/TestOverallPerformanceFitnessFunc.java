/*     */ package org.jgap.perf;
/*     */ 
/*     */ import org.jgap.FitnessFunction;
/*     */ import org.jgap.IChromosome;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class TestOverallPerformanceFitnessFunc
/*     */   extends FitnessFunction
/*     */ {
/*     */   private static final String CVS_REVISION = "$Revision: 1.3 $";
/*     */   private final int m_targetAmount;
/*     */   
/*     */   public TestOverallPerformanceFitnessFunc(int a_targetAmount) {
/*  29 */     if (a_targetAmount < 1 || a_targetAmount > 999) {
/*  30 */       throw new IllegalArgumentException("Change amount must be between 1 and 999 cents.");
/*     */     }
/*     */     
/*  33 */     this.m_targetAmount = a_targetAmount;
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
/*     */   public double evaluate(IChromosome a_subject) {
/*  57 */     int changeAmount = amountOfChange(a_subject);
/*  58 */     int totalCoins = getTotalNumberOfCoins(a_subject);
/*  59 */     int changeDifference = Math.abs(this.m_targetAmount - changeAmount);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  68 */     int fitness = 99 - changeDifference;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  73 */     if (changeAmount == this.m_targetAmount) {
/*  74 */       fitness += 100 - 10 * totalCoins;
/*     */     }
/*     */ 
/*     */     
/*  78 */     return Math.max(1, fitness);
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
/*     */   public static int amountOfChange(IChromosome a_potentialSolution) {
/*  92 */     int numQuarters = getNumberOfCoinsAtGene(a_potentialSolution, 0);
/*  93 */     int numDimes = getNumberOfCoinsAtGene(a_potentialSolution, 1);
/*  94 */     int numNickels = getNumberOfCoinsAtGene(a_potentialSolution, 2);
/*  95 */     int numPennies = getNumberOfCoinsAtGene(a_potentialSolution, 3);
/*  96 */     int A = getNumberOfCoinsAtGene(a_potentialSolution, 4);
/*  97 */     int B = getNumberOfCoinsAtGene(a_potentialSolution, 5);
/*  98 */     int C = getNumberOfCoinsAtGene(a_potentialSolution, 6);
/*  99 */     int D = getNumberOfCoinsAtGene(a_potentialSolution, 7);
/* 100 */     int E = getNumberOfCoinsAtGene(a_potentialSolution, 8);
/* 101 */     int F = getNumberOfCoinsAtGene(a_potentialSolution, 9);
/* 102 */     return numQuarters * 25 + numDimes * 10 + numNickels * 5 + numPennies + A * 29 + B * 31 + C * 37 + D * 41 + E * 43 + F * 47;
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
/*     */   public static int getNumberOfCoinsAtGene(IChromosome a_potentialSolution, int a_position) {
/* 118 */     Integer numCoins = (Integer)a_potentialSolution.getGene(a_position).getAllele();
/*     */     
/* 120 */     return numCoins.intValue();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int getTotalNumberOfCoins(IChromosome a_potentialsolution) {
/* 131 */     int totalCoins = 0;
/* 132 */     int numberOfGenes = a_potentialsolution.size();
/* 133 */     for (int i = 0; i < numberOfGenes; i++) {
/* 134 */       totalCoins += getNumberOfCoinsAtGene(a_potentialsolution, i);
/*     */     }
/* 136 */     return totalCoins;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\perf\TestOverallPerformanceFitnessFunc.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */