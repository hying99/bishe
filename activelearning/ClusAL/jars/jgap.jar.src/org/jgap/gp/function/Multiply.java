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
/*    */ public class Multiply
/*    */   extends MathCommand
/*    */   implements IMutateable
/*    */ {
/*    */   private static final String CVS_REVISION = "$Revision: 1.6 $";
/*    */   
/*    */   public Multiply(GPConfiguration a_conf, Class a_type) throws InvalidConfigurationException {
/* 29 */     super(a_conf, 2, a_type);
/*    */   }
/*    */ 
/*    */   
/*    */   public CommandGene applyMutation(int index, double a_percentage) throws InvalidConfigurationException {
/* 34 */     Divide mutant = new Divide(getGPConfiguration(), getReturnType());
/* 35 */     return (CommandGene)mutant;
/*    */   }
/*    */   
/*    */   public String toString() {
/* 39 */     return "&1 * &2";
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getName() {
/* 49 */     return "Multiply";
/*    */   }
/*    */   
/*    */   public int execute_int(ProgramChromosome c, int n, Object[] args) {
/* 53 */     return c.execute_int(n, 0, args) * c.execute_int(n, 1, args);
/*    */   }
/*    */   
/*    */   public long execute_long(ProgramChromosome c, int n, Object[] args) {
/* 57 */     return c.execute_long(n, 0, args) * c.execute_long(n, 1, args);
/*    */   }
/*    */   
/*    */   public float execute_float(ProgramChromosome c, int n, Object[] args) {
/* 61 */     return c.execute_float(n, 0, args) * c.execute_float(n, 1, args);
/*    */   }
/*    */   
/*    */   public double execute_double(ProgramChromosome c, int n, Object[] args) {
/* 65 */     return c.execute_double(n, 0, args) * c.execute_double(n, 1, args);
/*    */   }
/*    */   
/*    */   public Object execute_object(ProgramChromosome c, int n, Object[] args) {
/* 69 */     return ((Compatible)c.execute_object(n, 0, args)).execute_multiply(c.execute_object(n, 1, args));
/*    */   }
/*    */   
/*    */   protected static interface Compatible {
/*    */     Object execute_multiply(Object param1Object);
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\gp\function\Multiply.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */