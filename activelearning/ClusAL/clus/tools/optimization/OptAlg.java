/*    */ package clus.tools.optimization;
/*    */ 
/*    */ import clus.algo.rules.ClusRuleSet;
/*    */ import clus.main.ClusStatManager;
/*    */ import clus.main.Settings;
/*    */ import java.util.ArrayList;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class OptAlg
/*    */ {
/*    */   private ClusStatManager m_StatMgr;
/*    */   
/*    */   public OptAlg(ClusStatManager stat_mgr) {
/* 49 */     this.m_StatMgr = stat_mgr;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public abstract ArrayList<Double> optimize();
/*    */ 
/*    */ 
/*    */   
/*    */   protected final Settings getSettings() {
/* 60 */     return this.m_StatMgr.getSettings();
/*    */   }
/*    */   
/*    */   public void postProcess(ClusRuleSet rset) {}
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\tools\optimization\OptAlg.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */