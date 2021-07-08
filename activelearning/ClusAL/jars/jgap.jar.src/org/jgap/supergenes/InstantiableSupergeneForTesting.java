/*    */ package org.jgap.supergenes;
/*    */ 
/*    */ import org.jgap.Configuration;
/*    */ import org.jgap.Gene;
/*    */ import org.jgap.Genotype;
/*    */ import org.jgap.InvalidConfigurationException;
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
/*    */ public class InstantiableSupergeneForTesting
/*    */   extends AbstractSupergene
/*    */ {
/*    */   private static final String CVS_REVISION = "$Revision: 1.1 $";
/*    */   
/*    */   public InstantiableSupergeneForTesting(Configuration a_config, Gene[] a_genes) throws InvalidConfigurationException {
/* 28 */     super(a_config, a_genes);
/*    */   }
/*    */ 
/*    */   
/*    */   public InstantiableSupergeneForTesting(Configuration a_config) throws InvalidConfigurationException {
/* 33 */     super(a_config, new Gene[0]);
/*    */   }
/*    */ 
/*    */   
/*    */   public InstantiableSupergeneForTesting() throws InvalidConfigurationException {
/* 38 */     this(Genotype.getStaticConfiguration());
/*    */   }
/*    */   
/*    */   public boolean isValid(Gene[] a_gene) {
/* 42 */     return true;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\supergenes\InstantiableSupergeneForTesting.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */