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
/*    */ public class Cosine
/*    */   extends MathCommand
/*    */ {
/*    */   private static final String CVS_REVISION = "$Revision: 1.6 $";
/*    */   
/*    */   public Cosine(GPConfiguration a_conf, Class type) throws InvalidConfigurationException {
/* 29 */     super(a_conf, 1, type);
/*    */   }
/*    */   
/*    */   public String toString() {
/* 33 */     return "cosine &1";
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getName() {
/* 43 */     return "Cosine";
/*    */   }
/*    */   
/*    */   public float execute_float(ProgramChromosome c, int n, Object[] args) {
/* 47 */     float f = c.execute_float(n, 0, args);
/*    */     
/* 49 */     return (float)Math.cos(Math.max(-10000.0F, Math.min(f, 10000.0F)));
/*    */   }
/*    */   
/*    */   public double execute_double(ProgramChromosome c, int n, Object[] args) {
/* 53 */     double d = c.execute_double(n, 0, args);
/*    */     
/* 55 */     return Math.cos(Math.max(-10000.0D, Math.min(d, 10000.0D)));
/*    */   }
/*    */   
/*    */   public Object execute_object(ProgramChromosome c, int n, Object[] args) {
/* 59 */     return ((Compatible)c.execute_object(n, 0, args)).execute_cosine();
/*    */   }
/*    */   
/*    */   protected static interface Compatible {
/*    */     Object execute_cosine();
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\gp\function\Cosine.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */