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
/*    */ public class Pop
/*    */   extends MathCommand
/*    */ {
/*    */   private static final String CVS_REVISION = "$Revision: 1.6 $";
/*    */   
/*    */   public Pop(GPConfiguration a_conf, Class a_type) throws InvalidConfigurationException {
/* 29 */     this(a_conf, a_type, 0);
/*    */   }
/*    */ 
/*    */   
/*    */   public Pop(GPConfiguration a_conf, Class a_type, int a_subReturnType) throws InvalidConfigurationException {
/* 34 */     super(a_conf, 0, a_type, a_subReturnType, null);
/*    */   }
/*    */   
/*    */   public String toString() {
/* 38 */     return "pop &1";
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getName() {
/* 48 */     return "Pop";
/*    */   }
/*    */   
/*    */   public int execute_int(ProgramChromosome c, int n, Object[] args) {
/* 52 */     check(c);
/*    */ 
/*    */     
/* 55 */     if (getGPConfiguration().stackSize() < 1) {
/* 56 */       throw new IllegalStateException("pop without push");
/*    */     }
/* 58 */     return ((Integer)getGPConfiguration().popFromStack()).intValue();
/*    */   }
/*    */ 
/*    */   
/*    */   public long execute_long(ProgramChromosome c, int n, Object[] args) {
/* 63 */     check(c);
/* 64 */     if (getGPConfiguration().stackSize() < 1) {
/* 65 */       throw new IllegalStateException("pop without push");
/*    */     }
/* 67 */     return ((Long)getGPConfiguration().popFromStack()).longValue();
/*    */   }
/*    */   
/*    */   public double execute_double(ProgramChromosome c, int n, Object[] args) {
/* 71 */     check(c);
/* 72 */     if (getGPConfiguration().stackSize() < 1) {
/* 73 */       throw new IllegalStateException("pop without push");
/*    */     }
/* 75 */     return ((Double)getGPConfiguration().popFromStack()).doubleValue();
/*    */   }
/*    */   
/*    */   public float execute_float(ProgramChromosome c, int n, Object[] args) {
/* 79 */     check(c);
/* 80 */     if (getGPConfiguration().stackSize() < 1) {
/* 81 */       throw new IllegalStateException("pop without push");
/*    */     }
/* 83 */     return ((Float)getGPConfiguration().popFromStack()).floatValue();
/*    */   }
/*    */   
/*    */   public Object execute_object(ProgramChromosome c, int n, Object[] args) {
/* 87 */     check(c);
/* 88 */     if (getGPConfiguration().stackSize() < 1) {
/* 89 */       throw new IllegalStateException("pop without push");
/*    */     }
/* 91 */     return getGPConfiguration().popFromStack();
/*    */   }
/*    */   
/*    */   public boolean isValid(ProgramChromosome a_program) {
/* 95 */     return (a_program.getCommandOfClass(0, Push.class) >= 0);
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\gp\function\Pop.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */