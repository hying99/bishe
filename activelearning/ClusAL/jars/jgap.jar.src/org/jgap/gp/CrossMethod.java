/*    */ package org.jgap.gp;
/*    */ 
/*    */ import java.io.Serializable;
/*    */ import org.jgap.gp.impl.GPConfiguration;
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
/*    */ public abstract class CrossMethod
/*    */   implements Serializable
/*    */ {
/*    */   private static final String CVS_REVISION = "$Revision: 1.7 $";
/*    */   private GPConfiguration m_configuration;
/*    */   
/*    */   public CrossMethod(GPConfiguration a_configuration) {
/* 29 */     this.m_configuration = a_configuration;
/*    */   }
/*    */   
/*    */   public GPConfiguration getConfiguration() {
/* 33 */     return this.m_configuration;
/*    */   }
/*    */   
/*    */   public abstract IGPProgram[] operate(IGPProgram paramIGPProgram1, IGPProgram paramIGPProgram2);
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\gp\CrossMethod.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */