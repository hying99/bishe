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
/*    */ public class Equals
/*    */   extends MathCommand
/*    */ {
/*    */   private static final String CVS_REVISION = "$Revision: 1.7 $";
/*    */   private Class m_type;
/*    */   
/*    */   public Equals(GPConfiguration a_conf, Class a_type) throws InvalidConfigurationException {
/* 31 */     this(a_conf, a_type, 0, null);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public Equals(GPConfiguration a_conf, Class a_type, int a_subReturnType, int[] a_subChildTypes) throws InvalidConfigurationException {
/* 37 */     super(a_conf, 2, CommandGene.BooleanClass, a_subReturnType, a_subChildTypes);
/* 38 */     this.m_type = a_type;
/*    */   }
/*    */   
/*    */   public String toString() {
/* 42 */     return "Equals(&1, &2)";
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getName() {
/* 52 */     return "Equals";
/*    */   }
/*    */   
/*    */   public boolean execute_boolean(ProgramChromosome c, int n, Object[] args) {
/* 56 */     if (this.m_type == CommandGene.BooleanClass) {
/* 57 */       return (c.execute_boolean(n, 0, args) == c.execute_boolean(n, 1, args));
/*    */     }
/* 59 */     if (this.m_type == CommandGene.IntegerClass) {
/* 60 */       return (c.execute_int(n, 0, args) == c.execute_int(n, 1, args));
/*    */     }
/* 62 */     if (this.m_type == CommandGene.LongClass) {
/* 63 */       return (c.execute_long(n, 0, args) == c.execute_long(n, 1, args));
/*    */     }
/* 65 */     if (this.m_type == CommandGene.DoubleClass) {
/* 66 */       return (Math.abs(c.execute_double(n, 0, args) - c.execute_double(n, 1, args)) < 1.0E-7D);
/*    */     }
/*    */     
/* 69 */     if (this.m_type == CommandGene.FloatClass) {
/* 70 */       return (Math.abs(c.execute_float(n, 0, args) - c.execute_float(n, 1, args)) < 1.0E-7D);
/*    */     }
/*    */     
/* 73 */     if (this.m_type == CommandGene.VoidClass) {
/* 74 */       return c.execute_object(n, 0, args).equals(c.execute_object(n, 1, args));
/*    */     }
/* 76 */     throw new UnsupportedOperationException("Unsupported type " + this.m_type + " for Equals-command!");
/*    */   }
/*    */ 
/*    */   
/*    */   public Class getChildType(IGPProgram a_ind, int a_index) {
/* 81 */     return this.m_type;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\gp\function\Equals.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */