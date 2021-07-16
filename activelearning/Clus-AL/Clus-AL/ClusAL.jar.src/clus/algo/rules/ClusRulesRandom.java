/*    */ package clus.algo.rules;
/*    */ 
/*    */ import clus.data.rows.RowData;
/*    */ import clus.main.ClusRun;
/*    */ import clus.util.ClusException;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ClusRulesRandom
/*    */ {
/*    */   public ClusRulesRandom(ClusRun cr) {}
/*    */   
/*    */   public ClusRuleSet constructRules(ClusRun run) throws IOException, ClusException {
/* 82 */     return new ClusRuleSet(run.getStatManager());
/*    */   }
/*    */   
/*    */   public void constructRandomly(ClusRuleSet rset, RowData data) {}
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\algo\rules\ClusRulesRandom.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */