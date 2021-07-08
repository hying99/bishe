/*    */ package org.jgap.gp.terminal;
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
/*    */ public class Argument
/*    */   extends CommandGene
/*    */ {
/*    */   private static final String CVS_REVISION = "$Revision: 1.8 $";
/*    */   private int m_index;
/*    */   
/*    */   public Argument(GPConfiguration a_conf, int a_index, Class type) throws InvalidConfigurationException {
/* 31 */     super(a_conf, 0, type);
/* 32 */     this.m_index = a_index;
/*    */   }
/*    */   
/*    */   public String toString() {
/* 36 */     return "Arg(" + this.m_index + ")";
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getName() {
/* 46 */     return "ADF Argument";
/*    */   }
/*    */   
/*    */   public int execute_int(ProgramChromosome c, int n, Object[] args) {
/* 50 */     return ((Integer)args[this.m_index]).intValue();
/*    */   }
/*    */   
/*    */   public long execute_long(ProgramChromosome c, int n, Object[] args) {
/* 54 */     return ((Long)args[this.m_index]).longValue();
/*    */   }
/*    */   
/*    */   public float execute_float(ProgramChromosome c, int n, Object[] args) {
/* 58 */     return ((Float)args[this.m_index]).floatValue();
/*    */   }
/*    */   
/*    */   public double execute_double(ProgramChromosome c, int n, Object[] args) {
/* 62 */     return ((Double)args[this.m_index]).doubleValue();
/*    */   }
/*    */   
/*    */   public Object execute_object(ProgramChromosome c, int n, Object[] args) {
/* 66 */     return args[this.m_index];
/*    */   }
/*    */   
/*    */   public Class getChildType(IGPProgram a_ind, int a_chromNum) {
/* 70 */     return null;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\gp\terminal\Argument.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */