/*    */ package org.jgap.gp.impl;
/*    */ 
/*    */ import java.util.Comparator;
/*    */ import org.jgap.gp.IGPFitnessEvaluator;
/*    */ import org.jgap.gp.IGPProgram;
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
/*    */ public class GPProgramFitnessComparator
/*    */   implements Comparator
/*    */ {
/*    */   private static final String CVS_REVISION = "$Revision: 1.4 $";
/*    */   private IGPFitnessEvaluator m_fitnessEvaluator;
/*    */   
/*    */   public GPProgramFitnessComparator() {
/* 42 */     this(new DefaultGPFitnessEvaluator());
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public GPProgramFitnessComparator(IGPFitnessEvaluator a_evaluator) {
/* 52 */     if (a_evaluator == null) {
/* 53 */       throw new IllegalArgumentException("Evaluator must not be null");
/*    */     }
/* 55 */     this.m_fitnessEvaluator = a_evaluator;
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
/*    */   
/*    */   public int compare(Object a_program1, Object a_program2) {
/* 70 */     IGPProgram progOne = (IGPProgram)a_program1;
/* 71 */     IGPProgram progTwo = (IGPProgram)a_program2;
/* 72 */     if (this.m_fitnessEvaluator.isFitter(progOne, progTwo)) {
/* 73 */       return -1;
/*    */     }
/* 75 */     if (this.m_fitnessEvaluator.isFitter(progTwo, progOne)) {
/* 76 */       return 1;
/*    */     }
/*    */     
/* 79 */     return 0;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\gp\impl\GPProgramFitnessComparator.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */