/*    */ package org.jgap.impl.salesman;
/*    */ 
/*    */ import org.jgap.FitnessFunction;
/*    */ import org.jgap.Gene;
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
/*    */ public class SalesmanFitnessFunction
/*    */   extends FitnessFunction
/*    */ {
/*    */   private static final String CVS_REVISION = "$Revision: 1.9 $";
/*    */   private final Salesman m_salesman;
/*    */   
/*    */   public SalesmanFitnessFunction(Salesman a_salesman) {
/* 30 */     this.m_salesman = a_salesman;
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
/*    */   protected double evaluate(IChromosome a_subject) {
/* 45 */     double s = 0.0D;
/* 46 */     Gene[] genes = a_subject.getGenes();
/* 47 */     for (int i = 0; i < genes.length - 1; i++) {
/* 48 */       s += this.m_salesman.distance(genes[i], genes[i + 1]);
/*    */     }
/*    */     
/* 51 */     s += this.m_salesman.distance(genes[genes.length - 1], genes[0]);
/* 52 */     return 1.073741823E9D - s;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\impl\salesman\SalesmanFitnessFunction.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */