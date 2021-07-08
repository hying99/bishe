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
/*    */ public class Add3
/*    */   extends MathCommand
/*    */ {
/*    */   private static final String CVS_REVISION = "$Revision: 1.6 $";
/*    */   
/*    */   public Add3(GPConfiguration a_conf, Class type) throws InvalidConfigurationException {
/* 29 */     super(a_conf, 3, type);
/*    */   }
/*    */   
/*    */   public String toString() {
/* 33 */     return "&1 + &2 + &3";
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getName() {
/* 43 */     return "Add3";
/*    */   }
/*    */   
/*    */   public int execute_int(ProgramChromosome c, int n, Object[] args) {
/* 47 */     return c.execute_int(n, 0, args) + c.execute_int(n, 1, args) + c.execute_int(n, 2, args);
/*    */   }
/*    */ 
/*    */   
/*    */   public long execute_long(ProgramChromosome c, int n, Object[] args) {
/* 52 */     return c.execute_long(n, 0, args) + c.execute_long(n, 1, args) + c.execute_long(n, 2, args);
/*    */   }
/*    */ 
/*    */   
/*    */   public float execute_float(ProgramChromosome c, int n, Object[] args) {
/* 57 */     return c.execute_float(n, 0, args) + c.execute_float(n, 1, args) + c.execute_float(n, 2, args);
/*    */   }
/*    */ 
/*    */   
/*    */   public double execute_double(ProgramChromosome c, int n, Object[] args) {
/* 62 */     return c.execute_double(n, 0, args) + c.execute_double(n, 1, args) + c.execute_double(n, 2, args);
/*    */   }
/*    */ 
/*    */   
/*    */   public Object execute_object(ProgramChromosome c, int n, Object[] args) {
/* 67 */     return ((Compatible)((Compatible)c.execute_object(n, 0, args)).execute_add3(c.execute_object(n, 1, args))).execute_add3(c.execute_object(n, 2, args));
/*    */   }
/*    */   
/*    */   protected static interface Compatible {
/*    */     Object execute_add3(Object param1Object);
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\gp\function\Add3.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */