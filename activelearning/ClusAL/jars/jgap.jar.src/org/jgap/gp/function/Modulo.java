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
/*    */ 
/*    */ public class Modulo
/*    */   extends MathCommand
/*    */ {
/*    */   private static final String CVS_REVISION = "$Revision: 1.7 $";
/*    */   
/*    */   public Modulo(GPConfiguration a_conf, Class a_type) throws InvalidConfigurationException {
/* 30 */     super(a_conf, 2, a_type);
/*    */   }
/*    */   
/*    */   public String toString() {
/* 34 */     return "&1 % &2";
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getName() {
/* 44 */     return "Modulo";
/*    */   }
/*    */   
/*    */   public int execute_int(ProgramChromosome c, int n, Object[] args) {
/* 48 */     int v1 = c.execute_int(n, 0, args);
/* 49 */     int v2 = c.execute_int(n, 1, args);
/* 50 */     if (v2 == 0) {
/* 51 */       return 0;
/*    */     }
/* 53 */     return v1 % v2;
/*    */   }
/*    */   
/*    */   public long execute_long(ProgramChromosome c, int n, Object[] args) {
/* 57 */     long v1 = c.execute_long(n, 0, args);
/* 58 */     long v2 = c.execute_long(n, 1, args);
/* 59 */     if (v2 == 0L) {
/* 60 */       return 0L;
/*    */     }
/* 62 */     return v1 % v2;
/*    */   }
/*    */   
/*    */   public float execute_float(ProgramChromosome c, int n, Object[] args) {
/* 66 */     float v1 = c.execute_float(n, 0, args);
/* 67 */     float v2 = c.execute_float(n, 1, args);
/* 68 */     if (Math.abs(v2) < 1.0E-7D) {
/* 69 */       return 0.0F;
/*    */     }
/* 71 */     return v1 % v2;
/*    */   }
/*    */   
/*    */   public double execute_double(ProgramChromosome c, int n, Object[] args) {
/* 75 */     double v1 = c.execute_double(n, 0, args);
/* 76 */     double v2 = c.execute_double(n, 1, args);
/* 77 */     if (Math.abs(v2) < 1.0E-7D) {
/* 78 */       return 0.0D;
/*    */     }
/* 80 */     return v1 % v2;
/*    */   }
/*    */   
/*    */   public Object execute_object(ProgramChromosome c, int n, Object[] args) {
/*    */     try {
/* 85 */       return ((Compatible)c.execute_object(n, 0, args)).execute_mod(c.execute_object(n, 1, args));
/*    */     }
/* 87 */     catch (ArithmeticException aex) {
/* 88 */       throw new IllegalStateException("mod with illegal arguments");
/*    */     } 
/*    */   }
/*    */   
/*    */   protected static interface Compatible {
/*    */     Object execute_mod(Object param1Object);
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\gp\function\Modulo.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */