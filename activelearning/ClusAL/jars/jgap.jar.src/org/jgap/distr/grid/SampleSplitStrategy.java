/*    */ package org.jgap.distr.grid;
/*    */ 
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
/*    */ 
/*    */ 
/*    */ 
/*    */ public class SampleSplitStrategy
/*    */   implements IRequestSplitStrategy
/*    */ {
/*    */   private static final String CVS_REVISION = "$Revision: 1.1 $";
/*    */   private Configuration m_config;
/*    */   
/*    */   public SampleSplitStrategy(Configuration a_config) {
/* 31 */     this.m_config = a_config;
/*    */   }
/*    */   
/*    */   public Configuration getConfiguration() {
/* 35 */     return this.m_config;
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
/*    */   public JGAPRequest[] split(JGAPRequest a_request) throws Exception {
/* 51 */     int runs = 20;
/* 52 */     JGAPRequest[] result = new JGAPRequest[20];
/* 53 */     for (int i = 0; i < 20; i++) {
/*    */ 
/*    */       
/* 56 */       Configuration config = getConfiguration().newInstance(i + "", "config " + i);
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */       
/* 65 */       result[i] = a_request.newInstance("JGAP-Grid Request " + i, i);
/*    */     } 
/*    */     
/* 68 */     return result;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\distr\grid\SampleSplitStrategy.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */