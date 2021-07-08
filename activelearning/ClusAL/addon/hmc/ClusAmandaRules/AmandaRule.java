/*    */ package addon.hmc.ClusAmandaRules;
/*    */ 
/*    */ import clus.algo.rules.ClusRule;
/*    */ import clus.data.rows.DataTuple;
/*    */ import clus.main.ClusStatManager;
/*    */ import clus.model.test.NodeTest;
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
/*    */ public class AmandaRule
/*    */   extends ClusRule
/*    */ {
/*    */   public static final long serialVersionUID = 1L;
/*    */   
/*    */   public AmandaRule(ClusStatManager statManager) {
/* 40 */     super(statManager);
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
/*    */   public boolean doTest(NodeTest test, DataTuple tuple) {
/* 60 */     if (test.predictWeighted(tuple) != 0) return false;
/*    */     
/* 62 */     return true;
/*    */   }
/*    */   
/*    */   public boolean covers(DataTuple tuple) {
/* 66 */     for (int i = 0; i < getModelSize(); i++) {
/* 67 */       NodeTest test = getTest(i);
/* 68 */       if (!doTest(test, tuple)) return false; 
/*    */     } 
/* 70 */     return true;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\addon\hmc\ClusAmandaRules\AmandaRule.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */