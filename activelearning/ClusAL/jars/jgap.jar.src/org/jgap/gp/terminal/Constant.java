/*    */ package org.jgap.gp.terminal;
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
/*    */ public class Constant
/*    */   extends MathCommand
/*    */ {
/*    */   private static final String CVS_REVISION = "$Revision: 1.7 $";
/*    */   private Object m_value;
/*    */   
/*    */   public Constant(GPConfiguration a_conf, Class a_type, Object a_value) throws InvalidConfigurationException {
/* 31 */     this(a_conf, a_type, a_value, 0);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public Constant(GPConfiguration a_conf, Class a_type, Object a_value, int a_subReturnType) throws InvalidConfigurationException {
/* 37 */     super(a_conf, 0, a_type, a_subReturnType, null);
/* 38 */     this.m_value = a_value;
/*    */   }
/*    */   
/*    */   public String toString() {
/* 42 */     return this.m_value.toString();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getName() {
/* 52 */     return "Constant";
/*    */   }
/*    */   
/*    */   public int execute_int(ProgramChromosome c, int n, Object[] args) {
/* 56 */     return ((Integer)this.m_value).intValue();
/*    */   }
/*    */   
/*    */   public long execute_long(ProgramChromosome c, int n, Object[] args) {
/* 60 */     return ((Long)this.m_value).intValue();
/*    */   }
/*    */   
/*    */   public float execute_float(ProgramChromosome c, int n, Object[] args) {
/* 64 */     return ((Float)this.m_value).floatValue();
/*    */   }
/*    */   
/*    */   public double execute_double(ProgramChromosome c, int n, Object[] args) {
/* 68 */     return ((Double)this.m_value).doubleValue();
/*    */   }
/*    */   
/*    */   public Object execute_object(ProgramChromosome c, int n, Object[] args) {
/* 72 */     return this.m_value;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\gp\terminal\Constant.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */