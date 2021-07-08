/*    */ package org.jgap.util;
/*    */ 
/*    */ import java.util.Comparator;
/*    */ import org.jgap.DefaultFitnessEvaluator;
/*    */ import org.jgap.FitnessEvaluator;
/*    */ import org.jgap.IChromosome;
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
/*    */ public class ChromosomeFitnessComparator
/*    */   implements Comparator
/*    */ {
/*    */   private static final String CVS_REVISION = "$Revision: 1.9 $";
/*    */   private FitnessEvaluator m_fitnessEvaluator;
/*    */   
/*    */   public ChromosomeFitnessComparator() {
/* 39 */     this((FitnessEvaluator)new DefaultFitnessEvaluator());
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public ChromosomeFitnessComparator(FitnessEvaluator a_evaluator) {
/* 49 */     if (a_evaluator == null) {
/* 50 */       throw new IllegalArgumentException("Evaluator must not be null");
/*    */     }
/* 52 */     this.m_fitnessEvaluator = a_evaluator;
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
/*    */   public int compare(Object a_chromosome1, Object a_chromosome2) {
/* 66 */     IChromosome chromosomeOne = (IChromosome)a_chromosome1;
/* 67 */     IChromosome chromosomeTwo = (IChromosome)a_chromosome2;
/* 68 */     if (this.m_fitnessEvaluator.isFitter(chromosomeOne, chromosomeTwo)) {
/* 69 */       return -1;
/*    */     }
/* 71 */     if (this.m_fitnessEvaluator.isFitter(chromosomeTwo, chromosomeOne)) {
/* 72 */       return 1;
/*    */     }
/*    */     
/* 75 */     return 0;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jga\\util\ChromosomeFitnessComparator.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */