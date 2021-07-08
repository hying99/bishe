/*    */ package org.jgap.gp.impl;
/*    */ 
/*    */ import org.jgap.gp.CommandGene;
/*    */ import org.jgap.gp.INodeValidator;
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
/*    */ public class NodeValidatorForTesting
/*    */   implements INodeValidator
/*    */ {
/*    */   public boolean validate(ProgramChromosome a_chrom, CommandGene a_node, CommandGene a_rootNode, int a_tries, int a_num, int a_recurseLevel, Class a_type, CommandGene[] a_functionSet, int a_depth, boolean a_grow, int a_childIndex, boolean a_fullProgram) {
/* 20 */     return true;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\gp\impl\NodeValidatorForTesting.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */