/*    */ package org.jgap.distr.grid;
/*    */ 
/*    */ import org.jgap.Configuration;
/*    */ import org.jgap.IChromosome;
/*    */ import org.jgap.event.EventManager;
/*    */ import org.jgap.event.IEventManager;
/*    */ import org.jgap.impl.DefaultConfiguration;
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
/*    */ public class RequestSplitStrategy
/*    */ {
/*    */   private static final String CVS_REVISION = "$Revision: 1.2 $";
/*    */   private Configuration m_config;
/*    */   
/*    */   public RequestSplitStrategy(Configuration a_config) {
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
/*    */   public JGAPRequest[] split(JGAPRequest a_request) throws Exception {
/* 48 */     int runs = 20;
/* 49 */     JGAPRequest[] result = new JGAPRequest[20];
/* 50 */     for (int i = 0; i < 20; i++) {
/*    */       
/* 52 */       DefaultConfiguration defaultConfiguration = new DefaultConfiguration("config " + i, i + "");
/* 53 */       defaultConfiguration.setEventManager((IEventManager)new EventManager());
/* 54 */       defaultConfiguration.setPopulationSize(getConfiguration().getPopulationSize());
/* 55 */       defaultConfiguration.setFitnessFunction(getConfiguration().getFitnessFunction());
/*    */       
/* 57 */       IChromosome sample = (IChromosome)getConfiguration().getSampleChromosome().clone();
/* 58 */       defaultConfiguration.setSampleChromosome(sample);
/* 59 */       result[i] = a_request.newInstance("JGAP-Grid Request " + i, i);
/*    */     } 
/*    */     
/* 62 */     return result;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\distr\grid\RequestSplitStrategy.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */