/*    */ package org.jgap.gp.function;
/*    */ 
/*    */ import org.jgap.InvalidConfigurationException;
/*    */ import org.jgap.gp.CommandGene;
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
/*    */ 
/*    */ public class Or
/*    */   extends MathCommand
/*    */   implements IMutateable
/*    */ {
/*    */   private static final String CVS_REVISION = "$Revision: 1.6 $";
/*    */   
/*    */   public Or(GPConfiguration a_conf) throws InvalidConfigurationException {
/* 29 */     super(a_conf, 2, CommandGene.BooleanClass);
/*    */   }
/*    */ 
/*    */   
/*    */   public CommandGene applyMutation(int index, double a_percentage) throws InvalidConfigurationException {
/*    */     And and;
/* 35 */     if (a_percentage < 0.5D) {
/* 36 */       Xor xor = new Xor(getGPConfiguration());
/*    */     } else {
/*    */       
/* 39 */       and = new And(getGPConfiguration());
/*    */     } 
/* 41 */     return (CommandGene)and;
/*    */   }
/*    */   
/*    */   public String toString() {
/* 45 */     return "&1 || &2";
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getName() {
/* 55 */     return "Or";
/*    */   }
/*    */   
/*    */   public boolean execute_boolean(ProgramChromosome c, int n, Object[] args) {
/* 59 */     if (c.execute_boolean(n, 0, args)) {
/* 60 */       return true;
/*    */     }
/* 62 */     if (c.execute_boolean(n, 1, args)) {
/* 63 */       return true;
/*    */     }
/* 65 */     return false;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\gp\function\Or.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */