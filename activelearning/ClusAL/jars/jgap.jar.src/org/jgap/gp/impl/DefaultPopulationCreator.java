/*    */ package org.jgap.gp.impl;
/*    */ 
/*    */ import org.jgap.InvalidConfigurationException;
/*    */ import org.jgap.gp.CommandGene;
/*    */ import org.jgap.gp.IPopulationCreator;
/*    */ import org.jgap.gp.IProgramCreator;
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
/*    */ public class DefaultPopulationCreator
/*    */   implements IPopulationCreator
/*    */ {
/*    */   private static final String CVS_REVISION = "$Revision: 1.4 $";
/*    */   private IProgramCreator m_programCreator;
/*    */   
/*    */   public DefaultPopulationCreator() {
/* 30 */     this(new DefaultProgramCreator());
/*    */   }
/*    */   
/*    */   public DefaultPopulationCreator(IProgramCreator a_programCreator) {
/* 34 */     this.m_programCreator = a_programCreator;
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
/*    */   public void initialize(GPPopulation a_pop, Class[] a_types, Class[][] a_argTypes, CommandGene[][] a_nodeSets, int[] a_minDepths, int[] a_maxDepths, int a_maxNodes, boolean[] a_fullModeAllowed) throws InvalidConfigurationException {
/* 60 */     a_pop.create(a_types, a_argTypes, a_nodeSets, a_minDepths, a_maxDepths, a_maxNodes, a_fullModeAllowed, this.m_programCreator);
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\gp\impl\DefaultPopulationCreator.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */