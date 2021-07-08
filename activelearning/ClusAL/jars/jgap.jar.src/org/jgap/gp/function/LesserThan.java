/*    */ package org.jgap.gp.function;
/*    */ 
/*    */ import org.jgap.InvalidConfigurationException;
/*    */ import org.jgap.gp.CommandGene;
/*    */ import org.jgap.gp.IGPProgram;
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
/*    */ public class LesserThan
/*    */   extends MathCommand
/*    */ {
/*    */   private static final String CVS_REVISION = "$Revision: 1.1 $";
/*    */   private Class m_type;
/*    */   
/*    */   public LesserThan(GPConfiguration a_conf, Class a_type) throws InvalidConfigurationException {
/* 31 */     this(a_conf, a_type, 0, null);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public LesserThan(GPConfiguration a_conf, Class a_type, int a_subReturnType, int[] a_subChildTypes) throws InvalidConfigurationException {
/* 38 */     super(a_conf, 2, CommandGene.BooleanClass, a_subReturnType, a_subChildTypes);
/* 39 */     this.m_type = a_type;
/*    */   }
/*    */   
/*    */   public String toString() {
/* 43 */     return "&1 < &2";
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getName() {
/* 53 */     return "LesserThan";
/*    */   }
/*    */   
/*    */   public boolean execute_boolean(ProgramChromosome c, int n, Object[] args) {
/* 57 */     if (this.m_type == CommandGene.BooleanClass) {
/* 58 */       return (!c.execute_boolean(n, 0, args) && c.execute_boolean(n, 1, args));
/*    */     }
/* 60 */     if (this.m_type == CommandGene.IntegerClass) {
/* 61 */       return (c.execute_int(n, 0, args) < c.execute_int(n, 1, args));
/*    */     }
/* 63 */     if (this.m_type == CommandGene.LongClass) {
/* 64 */       return (c.execute_long(n, 0, args) < c.execute_long(n, 1, args));
/*    */     }
/* 66 */     if (this.m_type == CommandGene.DoubleClass) {
/* 67 */       return (c.execute_double(n, 0, args) < c.execute_double(n, 1, args));
/*    */     }
/* 69 */     if (this.m_type == CommandGene.FloatClass) {
/* 70 */       return (c.execute_float(n, 0, args) < c.execute_float(n, 1, args));
/*    */     }
/*    */ 
/*    */ 
/*    */ 
/*    */     
/* 76 */     throw new UnsupportedOperationException("Unsupported type " + this.m_type + " for LesserThan-command!");
/*    */   }
/*    */ 
/*    */   
/*    */   public Class getChildType(IGPProgram a_ind, int a_index) {
/* 81 */     return this.m_type;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\gp\function\LesserThan.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */