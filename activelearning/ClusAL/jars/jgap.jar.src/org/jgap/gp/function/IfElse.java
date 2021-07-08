/*    */ package org.jgap.gp.function;
/*    */ 
/*    */ import org.jgap.InvalidConfigurationException;
/*    */ import org.jgap.gp.CommandGene;
/*    */ import org.jgap.gp.IGPProgram;
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
/*    */ public class IfElse
/*    */   extends CommandGene
/*    */ {
/*    */   private static final String CVS_REVISION = "$Revision: 1.10 $";
/*    */   private Class m_type;
/*    */   
/*    */   public IfElse(GPConfiguration a_conf, Class a_type) throws InvalidConfigurationException {
/* 31 */     this(a_conf, a_type, 0, null);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public IfElse(GPConfiguration a_conf, Class a_type, int a_subReturnType, int[] a_subChildTypes) throws InvalidConfigurationException {
/* 37 */     super(a_conf, 3, CommandGene.VoidClass, a_subReturnType, a_subChildTypes);
/* 38 */     this.m_type = a_type;
/*    */   }
/*    */   
/*    */   public String toString() {
/* 42 */     return "if(&1) then (&2) else(&3)";
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getName() {
/* 52 */     return "IfElse";
/*    */   }
/*    */   public void execute_void(ProgramChromosome c, int n, Object[] args) {
/*    */     boolean condition;
/* 56 */     check(c);
/*    */     
/* 58 */     if (this.m_type == CommandGene.IntegerClass) {
/* 59 */       condition = (c.execute_int(n, 0, args) > 0);
/*    */     }
/* 61 */     else if (this.m_type == CommandGene.BooleanClass) {
/* 62 */       condition = c.execute_boolean(n, 0, args);
/*    */     }
/* 64 */     else if (this.m_type == CommandGene.LongClass) {
/* 65 */       condition = (c.execute_long(n, 0, args) > 0L);
/*    */     }
/* 67 */     else if (this.m_type == CommandGene.DoubleClass) {
/* 68 */       condition = (c.execute_double(n, 0, args) > 0.0D);
/*    */     }
/* 70 */     else if (this.m_type == CommandGene.FloatClass) {
/* 71 */       condition = (c.execute_float(n, 0, args) > 0.0F);
/*    */     } else {
/*    */       
/* 74 */       throw new IllegalStateException("IfElse: cannot process type " + this.m_type);
/*    */     } 
/* 76 */     if (condition) {
/* 77 */       c.execute_void(n, 1, args);
/*    */     } else {
/*    */       
/* 80 */       c.execute_void(n, 2, args);
/*    */     } 
/*    */   }
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
/*    */   public Class getChildType(IGPProgram a_ind, int a_chromNum) {
/* 95 */     if (a_chromNum == 0) {
/* 96 */       return this.m_type;
/*    */     }
/* 98 */     return CommandGene.VoidClass;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\gp\function\IfElse.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */