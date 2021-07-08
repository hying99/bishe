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
/*    */ public abstract class BaseRateCalculator
/*    */   implements IUniversalRateCalculator
/*    */ {
/*    */   private static final String CVS_REVISION = "$Revision: 1.3 $";
/*    */   private transient Configuration m_config;
/*    */   
/*    */   public BaseRateCalculator(Configuration a_config) throws InvalidConfigurationException {
/* 34 */     if (a_config == null) {
/* 35 */       throw new InvalidConfigurationException("Configuration must not be null!");
/*    */     }
/* 37 */     this.m_config = a_config;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Configuration getConfiguration() {
/* 47 */     return this.m_config;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\BaseRateCalculator.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */