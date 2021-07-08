/*    */ package org.jgap.impl.job;
/*    */ 
/*    */ import java.io.Serializable;
/*    */ import org.jgap.Configuration;
/*    */ 
/*    */ 
/*    */ public abstract class JobResult
/*    */   implements Serializable
/*    */ {
/*    */   private Configuration m_config;
/*    */   
/*    */   public Configuration getConfiguration() {
/* 13 */     return this.m_config;
/*    */   }
/*    */   
/*    */   public void setConfiguration(Configuration a_config) {
/* 17 */     this.m_config = a_config;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\impl\job\JobResult.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */