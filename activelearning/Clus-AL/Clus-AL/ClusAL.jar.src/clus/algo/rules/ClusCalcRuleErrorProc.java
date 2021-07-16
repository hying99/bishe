/*    */ package clus.algo.rules;
/*    */ 
/*    */ import clus.data.rows.DataTuple;
/*    */ import clus.error.ClusErrorList;
/*    */ import clus.model.ClusModel;
/*    */ import clus.model.processor.ClusModelProcessor;
/*    */ import java.io.IOException;
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
/*    */ public class ClusCalcRuleErrorProc
/*    */   extends ClusModelProcessor
/*    */ {
/*    */   protected int m_Subset;
/*    */   protected ClusErrorList m_Global;
/*    */   
/*    */   public ClusCalcRuleErrorProc(int subset, ClusErrorList global) {
/* 41 */     this.m_Subset = subset;
/* 42 */     this.m_Global = global;
/*    */   }
/*    */   
/*    */   public void modelUpdate(DataTuple tuple, ClusModel model) throws IOException {
/* 46 */     ClusRule rule = (ClusRule)model;
/* 47 */     ClusErrorList error = rule.getError(this.m_Subset);
/* 48 */     error.addExample(tuple, rule.getTargetStat());
/*    */   }
/*    */   
/*    */   public void terminate(ClusModel model) throws IOException {
/* 52 */     ClusRuleSet set = (ClusRuleSet)model;
/* 53 */     for (int i = 0; i < set.getModelSize(); i++) {
/* 54 */       ClusRule rule = set.getRule(i);
/* 55 */       rule.getError(this.m_Subset).updateFromGlobalMeasure(this.m_Global);
/*    */     } 
/*    */   }
/*    */   
/*    */   public boolean needsModelUpdate() {
/* 60 */     return true;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\algo\rules\ClusCalcRuleErrorProc.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */