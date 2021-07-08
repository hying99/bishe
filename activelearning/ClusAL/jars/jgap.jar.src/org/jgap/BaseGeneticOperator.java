/*    */ package org.jgap;
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
/*    */ public abstract class BaseGeneticOperator
/*    */   implements GeneticOperator, Comparable
/*    */ {
/*    */   private static final String CVS_REVISION = "$Revision: 1.6 $";
/*    */   private Configuration m_configuration;
/*    */   
/*    */   public BaseGeneticOperator(Configuration a_configuration) throws InvalidConfigurationException {
/* 34 */     if (a_configuration == null) {
/* 35 */       throw new InvalidConfigurationException("Configuration must not be null");
/*    */     }
/* 37 */     this.m_configuration = a_configuration;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Configuration getConfiguration() {
/* 48 */     return this.m_configuration;
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
/*    */   
/*    */   public boolean equals(Object a_other) {
/*    */     try {
/* 65 */       return (compareTo((T)a_other) == 0);
/*    */     }
/* 67 */     catch (ClassCastException cex) {
/* 68 */       return false;
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\BaseGeneticOperator.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */