/*    */ package org.jgap.gp.impl;
/*    */ 
/*    */ import java.io.Serializable;
/*    */ import org.jgap.RandomGenerator;
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
/*    */ public class TournamentSelector
/*    */   implements INaturalGPSelector, Serializable, Cloneable
/*    */ {
/*    */   private static final String CVS_REVISION = "$Revision: 1.3 $";
/*    */   private int m_tournament_size;
/*    */   
/*    */   public TournamentSelector() {
/* 36 */     this(5);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public TournamentSelector(int a_tournament_size) {
/* 47 */     this.m_tournament_size = a_tournament_size;
/*    */   }
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
/*    */   public IGPProgram select(GPGenotype a_genotype) {
/* 61 */     GPPopulation pop = a_genotype.getGPPopulation();
/* 62 */     IGPProgram bestProgram = null;
/* 63 */     int index = 0;
/* 64 */     RandomGenerator random = a_genotype.getGPConfiguration().getRandomGenerator();
/* 65 */     IGPFitnessEvaluator evaluator = a_genotype.getGPConfiguration().getGPFitnessEvaluator();
/*    */     
/* 67 */     for (int i = 0; i < this.m_tournament_size; i++) {
/* 68 */       index = (int)(random.nextDouble() * pop.getPopSize());
/* 69 */       if (bestProgram == null) {
/* 70 */         bestProgram = pop.getGPProgram(index);
/*    */       
/*    */       }
/* 73 */       else if (evaluator.isFitter(pop.getGPProgram(index), bestProgram)) {
/* 74 */         bestProgram = pop.getGPProgram(index);
/*    */       } 
/*    */     } 
/*    */     
/* 78 */     return bestProgram;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Object clone() {
/* 88 */     TournamentSelector sel = new TournamentSelector(this.m_tournament_size);
/* 89 */     return sel;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\gp\impl\TournamentSelector.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */