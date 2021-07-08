/*    */ package org.jgap.impl.job;
/*    */ 
/*    */ import java.io.Serializable;
/*    */ import org.jgap.Configuration;
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
/*    */ public abstract class JobData
/*    */   implements Serializable
/*    */ {
/*    */   private static final String CVS_REVISION = "$Revision: 1.2 $";
/*    */   private Configuration m_config;
/*    */   
/*    */   public JobData(Configuration a_config) {
/* 29 */     this.m_config = a_config;
/*    */   }
/*    */   
/*    */   public Configuration getConfiguration() {
/* 33 */     return this.m_config;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\impl\job\JobData.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */