/*    */ package org.jgap.distr.grid;
/*    */ 
/*    */ import org.jgap.Configuration;
/*    */ import org.jgap.Genotype;
/*    */ import org.jgap.Population;
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
/*    */ public class DefaultGenotypeInitializer
/*    */   implements IGenotypeInitializer, Comparable
/*    */ {
/*    */   private static final String CVS_REVISION = "$Revision: 1.4 $";
/*    */   
/*    */   public Genotype setupGenotype(JGAPRequest a_req, Population a_initialPop) throws Exception {
/*    */     Genotype gen;
/* 40 */     Configuration conf = a_req.getConfiguration();
/* 41 */     if (a_initialPop == null || a_initialPop.size() < 1) {
/* 42 */       gen = Genotype.randomInitialGenotype(conf);
/*    */     
/*    */     }
/*    */     else {
/*    */       
/* 47 */       gen = new Genotype(conf, a_initialPop);
/*    */ 
/*    */       
/* 50 */       int size = conf.getPopulationSize() - a_initialPop.size();
/* 51 */       gen.fillPopulation(size);
/*    */     } 
/* 53 */     return gen;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public int compareTo(Object a_other) {
/* 64 */     if (a_other.getClass().equals(getClass())) {
/* 65 */       return 0;
/*    */     }
/*    */     
/* 68 */     return getClass().getName().compareTo(a_other.getClass().getName());
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\distr\grid\DefaultGenotypeInitializer.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */