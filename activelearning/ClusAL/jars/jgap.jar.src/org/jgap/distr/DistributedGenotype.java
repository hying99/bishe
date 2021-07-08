/*    */ package org.jgap.distr;
/*    */ 
/*    */ import org.jgap.Configuration;
/*    */ import org.jgap.Genotype;
/*    */ import org.jgap.InvalidConfigurationException;
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
/*    */ public class DistributedGenotype
/*    */   extends Genotype
/*    */ {
/*    */   private static final String CVS_REVISION = "$Revision: 1.8 $";
/*    */   
/*    */   public DistributedGenotype(Configuration a_activeConfiguration, Population a_population) throws InvalidConfigurationException {
/* 30 */     super(a_activeConfiguration, a_population);
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\distr\DistributedGenotype.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */