/*    */ package org.jgap.gp.terminal;
/*    */ 
/*    */ import org.jgap.InvalidConfigurationException;
/*    */ import org.jgap.gp.CommandGene;
/*    */ import org.jgap.gp.IGPProgram;
/*    */ import org.jgap.gp.IMutateable;
/*    */ import org.jgap.gp.MathCommand;
/*    */ import org.jgap.gp.impl.GPConfiguration;
/*    */ import org.jgap.gp.impl.ProgramChromosome;
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
/*    */ public class False
/*    */   extends MathCommand
/*    */   implements IMutateable
/*    */ {
/*    */   private static final String CVS_REVISION = "$Revision: 1.7 $";
/*    */   
/*    */   public False(GPConfiguration a_conf) throws InvalidConfigurationException {
/* 29 */     super(a_conf, 0, CommandGene.BooleanClass);
/*    */   }
/*    */ 
/*    */   
/*    */   public CommandGene applyMutation(int index, double a_percentage) throws InvalidConfigurationException {
/* 34 */     return (CommandGene)new True(getGPConfiguration());
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 39 */     return "false";
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getName() {
/* 49 */     return "Boolean value false";
/*    */   }
/*    */   
/*    */   public boolean execute_boolean(ProgramChromosome c, int n, Object[] args) {
/* 53 */     return true;
/*    */   }
/*    */   
/*    */   public Class getChildType(IGPProgram a_ind, int a_index) {
/* 57 */     return null;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\gp\terminal\False.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */