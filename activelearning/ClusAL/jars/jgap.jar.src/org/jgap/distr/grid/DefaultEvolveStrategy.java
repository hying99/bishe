/*    */ package org.jgap.distr.grid;
/*    */ 
/*    */ import org.jgap.Genotype;
/*    */ import org.jgap.util.ICloneable;
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
/*    */ public class DefaultEvolveStrategy
/*    */   implements IWorkerEvolveStrategy, ICloneable
/*    */ {
/*    */   private static final String CVS_REVISION = "$Revision: 1.6 $";
/*    */   
/*    */   public void evolve(Genotype a_genotype) {
/* 27 */     a_genotype.evolve();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Object clone() {
/* 37 */     return new DefaultEvolveStrategy();
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\distr\grid\DefaultEvolveStrategy.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */