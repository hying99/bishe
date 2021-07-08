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
/*    */ public class Pow
/*    */   extends MathCommand
/*    */ {
/*    */   private static final String CVS_REVISION = "$Revision: 1.5 $";
/*    */   
/*    */   public Pow(GPConfiguration a_conf, Class a_type) throws InvalidConfigurationException {
/* 29 */     super(a_conf, 2, a_type);
/*    */   }
/*    */   
/*    */   public String toString() {
/* 33 */     return "&1 ^ &2";
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getName() {
/* 43 */     return "Power";
/*    */   }
/*    */   
/*    */   public int execute_int(ProgramChromosome c, int n, Object[] args) {
/* 47 */     int i = c.execute_int(n, 0, args);
/* 48 */     int j = c.execute_int(n, 1, args);
/*    */     
/* 50 */     return (int)Math.pow(Math.max(-10000.0F, Math.min(i, 20.0F)), Math.max(-10000.0F, Math.min(j, 20.0F)));
/*    */   }
/*    */ 
/*    */   
/*    */   public float execute_float(ProgramChromosome c, int n, Object[] args) {
/* 55 */     float f = c.execute_float(n, 0, args);
/* 56 */     float g = c.execute_float(n, 1, args);
/*    */     
/* 58 */     return (float)Math.pow(Math.max(-10000.0F, Math.min(f, 20.0F)), Math.max(-10000.0F, Math.min(g, 20.0F)));
/*    */   }
/*    */ 
/*    */   
/*    */   public double execute_double(ProgramChromosome c, int n, Object[] args) {
/* 63 */     double f = c.execute_double(n, 0, args);
/* 64 */     double g = c.execute_double(n, 1, args);
/*    */     
/* 66 */     return Math.pow(Math.max(-10000.0D, Math.min(f, 20.0D)), Math.max(-10000.0D, Math.min(g, 20.0D)));
/*    */   }
/*    */ 
/*    */   
/*    */   public Object execute_object(ProgramChromosome c, int n, Object[] args) {
/* 71 */     return ((Compatible)c.execute_object(n, 0, args)).execute_pow(c.execute_object(n, 1, args));
/*    */   }
/*    */   
/*    */   protected static interface Compatible {
/*    */     Object execute_pow(Object param1Object);
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\gp\function\Pow.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */