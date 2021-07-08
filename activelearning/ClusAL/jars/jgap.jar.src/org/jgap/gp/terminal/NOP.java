/*    */ package org.jgap.gp.terminal;
/*    */ 
/*    */ import org.jgap.InvalidConfigurationException;
/*    */ import org.jgap.gp.CommandGene;
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
/*    */ public class NOP
/*    */   extends MathCommand
/*    */ {
/*    */   private static final String CVS_REVISION = "$Revision: 1.8 $";
/*    */   
/*    */   public NOP(GPConfiguration a_conf) throws InvalidConfigurationException {
/* 30 */     this(a_conf, 0);
/*    */   }
/*    */ 
/*    */   
/*    */   public NOP(GPConfiguration a_conf, int a_subReturnType) throws InvalidConfigurationException {
/* 35 */     super(a_conf, 0, CommandGene.VoidClass, a_subReturnType, null);
/*    */   }
/*    */   
/*    */   public String toString() {
/* 39 */     return "NOP";
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getName() {
/* 49 */     return "NOP (NO Operation)";
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
/*    */   public void execute_void(ProgramChromosome c, int n, Object[] args) {}
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
/*    */   public Object execute_object(ProgramChromosome c, int n, Object[] args) {
/* 73 */     return new StringBuffer(";");
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\gp\terminal\NOP.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */