/*    */ package org.jgap.gp.impl;
/*    */ 
/*    */ import org.jgap.InvalidConfigurationException;
/*    */ import org.jgap.gp.CommandGene;
/*    */ import org.jgap.gp.IGPProgram;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DefaultProgramCreator
/*    */   implements IProgramCreator
/*    */ {
/*    */   private static final String CVS_REVISION = "$Revision: 1.3 $";
/*    */   
/*    */   public IGPProgram create(GPConfiguration a_conf, int a_programIndex, Class[] a_types, Class[][] a_argTypes, CommandGene[][] a_nodeSets, int[] a_minDepths, int[] a_maxDepths, int a_maxNodes, int a_depth, boolean a_grow, int a_tries, boolean[] a_fullModeAllowed) throws InvalidConfigurationException {
/* 33 */     GPProgram program = new GPProgram(a_conf, a_types, a_argTypes, a_nodeSets, a_minDepths, a_maxDepths, a_maxNodes);
/*    */     
/* 35 */     program.growOrFull(a_depth, a_grow, a_maxNodes, a_fullModeAllowed, a_tries);
/*    */     
/* 37 */     return (IGPProgram)program;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\gp\impl\DefaultProgramCreator.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */