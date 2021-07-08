/*    */ package org.jgap.gp.function;
/*    */ 
/*    */ import org.jgap.InvalidConfigurationException;
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
/*    */ 
/*    */ public class Exp
/*    */   extends MathCommand
/*    */ {
/*    */   private static final String CVS_REVISION = "$Revision: 1.7 $";
/*    */   
/*    */   public Exp(GPConfiguration a_conf, Class a_type) throws InvalidConfigurationException {
/* 29 */     super(a_conf, 1, a_type);
/*    */   }
/*    */   
/*    */   public String toString() {
/* 33 */     return "Exp(&1)";
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getName() {
/* 43 */     return "Exp";
/*    */   }
/*    */   
/*    */   public int execute_int(ProgramChromosome c, int n, Object[] args) {
/* 47 */     int i = c.execute_int(n, 0, args);
/*    */     
/* 49 */     return (int)Math.exp(Math.max(-10000.0F, Math.min(i, 20.0F)));
/*    */   }
/*    */   
/*    */   public float execute_float(ProgramChromosome c, int n, Object[] args) {
/* 53 */     float f = c.execute_float(n, 0, args);
/*    */     
/* 55 */     return (float)Math.exp(Math.max(-10000.0F, Math.min(f, 20.0F)));
/*    */   }
/*    */   
/*    */   public double execute_double(ProgramChromosome c, int n, Object[] args) {
/* 59 */     double f = c.execute_double(n, 0, args);
/*    */     
/* 61 */     return Math.exp(Math.max(-10000.0D, Math.min(f, 20.0D)));
/*    */   }
/*    */   
/*    */   public Object execute_object(ProgramChromosome c, int n, Object[] args) {
/* 65 */     return ((Compatible)c.execute_object(n, 0, args)).execute_exp();
/*    */   }
/*    */   
/*    */   protected static interface Compatible {
/*    */     Object execute_exp();
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\gp\function\Exp.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */