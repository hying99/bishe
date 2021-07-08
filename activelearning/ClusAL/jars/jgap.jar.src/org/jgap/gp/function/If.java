/*    */ package org.jgap.gp.function;
/*    */ 
/*    */ import org.jgap.InvalidConfigurationException;
/*    */ import org.jgap.gp.CommandGene;
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
/*    */ public class If
/*    */   extends CommandGene
/*    */ {
/*    */   private static final String CVS_REVISION = "$Revision: 1.5 $";
/*    */   
/*    */   public If(GPConfiguration a_conf, Class type) throws InvalidConfigurationException {
/* 29 */     super(a_conf, 2, type);
/*    */   }
/*    */   
/*    */   public String toString() {
/* 33 */     return "if(&1) then (&2)";
/*    */   }
/*    */   
/*    */   public boolean execute_boolean(ProgramChromosome c, int n, Object[] args) {
/* 37 */     boolean x = c.execute_boolean(n, 0, args);
/* 38 */     boolean value = false;
/* 39 */     if (x) {
/* 40 */       value = c.execute_boolean(n, 1, args);
/*    */     }
/* 42 */     return value;
/*    */   }
/*    */   
/*    */   public int execute_int(ProgramChromosome c, int n, Object[] args) {
/* 46 */     int x = c.execute_int(n, 0, args);
/* 47 */     int value = 0;
/* 48 */     if (x >= 0) {
/* 49 */       value = c.execute_int(n, 1, args);
/*    */     }
/* 51 */     return value;
/*    */   }
/*    */   
/*    */   public long execute_long(ProgramChromosome c, int n, Object[] args) {
/* 55 */     long x = c.execute_long(n, 0, args);
/* 56 */     long value = 0L;
/* 57 */     if (x >= 0L) {
/* 58 */       value = c.execute_long(n, 1, args);
/*    */     }
/* 60 */     return value;
/*    */   }
/*    */   
/*    */   public float execute_float(ProgramChromosome c, int n, Object[] args) {
/* 64 */     float x = c.execute_float(n, 0, args);
/* 65 */     float value = 0.0F;
/* 66 */     if (x >= 0.0F) {
/* 67 */       value = c.execute_float(n, 1, args);
/*    */     }
/* 69 */     return value;
/*    */   }
/*    */   
/*    */   public double execute_double(ProgramChromosome c, int n, Object[] args) {
/* 73 */     double x = c.execute_double(n, 0, args);
/* 74 */     double value = 0.0D;
/* 75 */     if (x >= 0.0D) {
/* 76 */       value = c.execute_double(n, 1, args);
/*    */     }
/* 78 */     return value;
/*    */   }
/*    */   
/*    */   public void execute_void(ProgramChromosome c, int n, Object[] args) {
/* 82 */     int x = c.execute_int(n, 0, args);
/* 83 */     if (x >= 0)
/* 84 */       c.execute_void(n, 1, args); 
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\gp\function\If.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */