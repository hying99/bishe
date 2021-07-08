/*    */ package org.jgap.impl.job;
/*    */ 
/*    */ import org.jgap.FitnessFunction;
/*    */ import org.jgap.IChromosome;
/*    */ import org.jgap.impl.BooleanGene;
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
/*    */ public class MaxFunction
/*    */   extends FitnessFunction
/*    */ {
/*    */   private static final String CVS_REVISION = "$Revision: 1.1 $";
/*    */   
/*    */   public double evaluate(IChromosome a_subject) {
/* 34 */     int total = 0;
/*    */     
/* 36 */     for (int i = 0; i < a_subject.size(); i++) {
/* 37 */       BooleanGene value = (BooleanGene)a_subject.getGene(a_subject.size() - i + 1);
/*    */       
/* 39 */       if (value.booleanValue()) {
/* 40 */         total = (int)(total + Math.pow(2.0D, i));
/*    */       }
/*    */     } 
/*    */     
/* 44 */     return total;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\impl\job\MaxFunction.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */