/*    */ package org.jgap.gp;
/*    */ 
/*    */ import org.jgap.JGAPTestCase;
/*    */ import org.jgap.RandomGenerator;
/*    */ import org.jgap.gp.function.Add;
/*    */ import org.jgap.gp.function.ForLoop;
/*    */ import org.jgap.gp.function.ForXLoop;
/*    */ import org.jgap.gp.function.SubProgram;
/*    */ import org.jgap.gp.impl.GPConfiguration;
/*    */ import org.jgap.gp.terminal.Constant;
/*    */ import org.jgap.gp.terminal.NOP;
/*    */ import org.jgap.impl.RandomGeneratorForTesting;
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
/*    */ public abstract class GPTestCase
/*    */   extends JGAPTestCase
/*    */ {
/*    */   protected GPConfiguration m_gpconf;
/*    */   protected RandomGeneratorForTesting rn;
/*    */   protected Constant CMD_CONST0;
/*    */   protected Constant CMD_CONST1;
/*    */   protected Constant CMD_CONST2;
/*    */   protected Constant CMD_CONST3;
/*    */   protected Constant CMD_CONST4;
/*    */   protected Add CMD_ADD;
/*    */   protected ForLoop CMD_FOR;
/*    */   protected ForXLoop CMD_FORX;
/*    */   protected SubProgram CMD_SUB_V_I;
/*    */   protected SubProgram CMD_SUB_V_V_V;
/*    */   protected SubProgram CMD_SUB_V_V;
/*    */   protected NOP CMD_NOP;
/*    */   private static final String CVS_REVISION = "$Revision: 1.5 $";
/*    */   
/*    */   public void setUp() {
/* 44 */     super.setUp();
/*    */     try {
/* 46 */       GPConfiguration.reset();
/* 47 */       this.m_gpconf = new GPConfiguration();
/* 48 */       this.rn = new RandomGeneratorForTesting(3);
/* 49 */       this.m_gpconf.setRandomGenerator((RandomGenerator)this.rn);
/* 50 */       this.m_gpconf.setPopulationSize(10);
/*    */       
/* 52 */       this.CMD_CONST0 = new Constant(this.m_gpconf, CommandGene.IntegerClass, new Integer(0));
/*    */       
/* 54 */       this.CMD_CONST1 = new Constant(this.m_gpconf, CommandGene.IntegerClass, new Integer(1));
/*    */       
/* 56 */       this.CMD_CONST2 = new Constant(this.m_gpconf, CommandGene.IntegerClass, new Integer(2));
/*    */       
/* 58 */       this.CMD_CONST3 = new Constant(this.m_gpconf, CommandGene.IntegerClass, new Integer(3));
/*    */       
/* 60 */       this.CMD_CONST4 = new Constant(this.m_gpconf, CommandGene.IntegerClass, new Integer(4));
/*    */       
/* 62 */       this.CMD_NOP = new NOP(this.m_gpconf);
/* 63 */       this.CMD_FOR = new ForLoop(this.m_gpconf, CommandGene.IntegerClass, 15);
/* 64 */       this.CMD_FORX = new ForXLoop(this.m_gpconf, CommandGene.IntegerClass);
/* 65 */       this.CMD_SUB_V_I = new SubProgram(this.m_gpconf, new Class[] { CommandGene.VoidClass, CommandGene.IntegerClass });
/*    */ 
/*    */       
/* 68 */       this.CMD_SUB_V_V = new SubProgram(this.m_gpconf, new Class[] { CommandGene.VoidClass, CommandGene.VoidClass });
/*    */ 
/*    */       
/* 71 */       this.CMD_SUB_V_V_V = new SubProgram(this.m_gpconf, new Class[] { CommandGene.VoidClass, CommandGene.VoidClass, CommandGene.VoidClass });
/*    */ 
/*    */ 
/*    */       
/* 75 */       this.CMD_ADD = new Add(this.m_gpconf, CommandGene.IntegerClass);
/*    */     }
/* 77 */     catch (Exception ex) {
/* 78 */       throw new RuntimeException(ex);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\gp\GPTestCase.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */