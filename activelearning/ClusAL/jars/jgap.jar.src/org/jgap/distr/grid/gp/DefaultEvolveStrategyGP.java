/*    */ package org.jgap.distr.grid.gp;
/*    */ 
/*    */ import org.jgap.gp.impl.GPGenotype;
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
/*    */ 
/*    */ public class DefaultEvolveStrategyGP
/*    */   implements IWorkerEvolveStrategyGP, ICloneable
/*    */ {
/*    */   private static final String CVS_REVISION = "$Revision: 1.2 $";
/*    */   
/*    */   public void evolve(GPGenotype a_genotype) {
/* 28 */     a_genotype.evolve();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Object clone() {
/* 38 */     return new DefaultEvolveStrategyGP();
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\distr\grid\gp\DefaultEvolveStrategyGP.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */