/*    */ package org.jgap.gp.function;
/*    */ 
/*    */ import org.jgap.InvalidConfigurationException;
/*    */ import org.jgap.gp.CommandGene;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ public class Xor
/*    */   extends MathCommand
/*    */ {
/*    */   private static final String CVS_REVISION = "$Revision: 1.5 $";
/*    */   
/*    */   public Xor(GPConfiguration a_conf) throws InvalidConfigurationException {
/* 29 */     super(a_conf, 2, CommandGene.BooleanClass);
/*    */   }
/*    */ 
/*    */   
/*    */   public CommandGene applyMutation(int index, double a_percentage) throws InvalidConfigurationException {
/*    */     Or or;
/* 35 */     if (a_percentage < 0.5D) {
/* 36 */       And and = new And(getGPConfiguration());
/*    */     } else {
/*    */       
/* 39 */       or = new Or(getGPConfiguration());
/*    */     } 
/* 41 */     return (CommandGene)or;
/*    */   }
/*    */   
/*    */   public String toString() {
/* 45 */     return "&1 ^ &2";
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getName() {
/* 55 */     return "Xor";
/*    */   }
/*    */   
/*    */   public boolean execute_boolean(ProgramChromosome c, int n, Object[] args) {
/* 59 */     return c.execute_boolean(n, 0, args) ^ c.execute_boolean(n, 1, args);
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\gp\function\Xor.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */