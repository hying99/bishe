/*    */ package org.jgap.distr.grid.gp;
/*    */ 
/*    */ import org.homedns.dade.jcgrid.WorkResult;
/*    */ import org.jgap.gp.IGPProgram;
/*    */ import org.jgap.gp.impl.GPPopulation;
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
/*    */ public class JGAPResultGP
/*    */   extends WorkResult
/*    */ {
/*    */   private static final String CVS_REVISION = "$Revision: 1.3 $";
/*    */   private IGPProgram m_fittest;
/*    */   private GPPopulation m_pop;
/*    */   private long m_unitDone;
/*    */   
/*    */   public JGAPResultGP(String a_sessionName, int a_id, IGPProgram a_fittestProg, long a_unitdone) {
/* 45 */     super(a_sessionName, a_id);
/* 46 */     this.m_fittest = a_fittestProg;
/* 47 */     this.m_unitDone = a_unitdone;
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
/*    */   public JGAPResultGP(String a_sessionName, int id, GPPopulation a_programs, long a_unitdone) {
/* 63 */     super(a_sessionName, id);
/* 64 */     this.m_fittest = null;
/* 65 */     this.m_pop = a_programs;
/* 66 */     this.m_unitDone = a_unitdone;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public IGPProgram getFittest() {
/* 74 */     return this.m_fittest;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public GPPopulation getPopulation() {
/* 84 */     return this.m_pop;
/*    */   }
/*    */   
/*    */   public long getUnitDone() {
/* 88 */     return this.m_unitDone;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\distr\grid\gp\JGAPResultGP.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */