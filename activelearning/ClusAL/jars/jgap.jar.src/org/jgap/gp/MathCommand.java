/*    */ package org.jgap.gp;
/*    */ 
/*    */ import org.jgap.InvalidConfigurationException;
/*    */ import org.jgap.gp.impl.GPConfiguration;
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
/*    */ 
/*    */ public abstract class MathCommand
/*    */   extends CommandGene
/*    */ {
/*    */   private static final String CVS_REVISION = "$Revision: 1.11 $";
/*    */   
/*    */   public MathCommand(GPConfiguration a_conf, int a_arity, Class a_returnType) throws InvalidConfigurationException {
/* 29 */     super(a_conf, a_arity, a_returnType, 0);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public MathCommand(GPConfiguration a_conf, int a_arity, Class a_returnType, int a_subReturnType) throws InvalidConfigurationException {
/* 35 */     this(a_conf, a_arity, a_returnType, a_subReturnType, (int[])null);
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public MathCommand(GPConfiguration a_conf, int a_arity, Class a_returnType, int a_subReturnType, int[] a_subChildTypes) throws InvalidConfigurationException {
/* 55 */     super(a_conf, a_arity, a_returnType, a_subReturnType, a_subChildTypes);
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public MathCommand(GPConfiguration a_conf, int a_arity, Class a_returnType, int a_subReturnType, int a_subChildType) throws InvalidConfigurationException {
/* 75 */     super(a_conf, a_arity, a_returnType, a_subReturnType, a_subChildType);
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\gp\MathCommand.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */