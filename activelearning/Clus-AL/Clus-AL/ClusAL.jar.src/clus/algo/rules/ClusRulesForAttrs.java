/*    */ package clus.algo.rules;
/*    */ 
/*    */ import clus.data.rows.RowData;
/*    */ import clus.data.type.ClusSchema;
/*    */ import clus.data.type.NominalAttrType;
/*    */ import clus.ext.hierarchical.WHTDStatistic;
/*    */ import clus.main.ClusRun;
/*    */ import clus.main.ClusStatManager;
/*    */ import clus.model.test.NodeTest;
/*    */ import clus.model.test.SubsetTest;
/*    */ import clus.statistic.ClusStatistic;
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
/*    */ public class ClusRulesForAttrs
/*    */ {
/* 43 */   double m_SigLevel = 0.05D;
/*    */   
/*    */   public ClusRuleSet constructRules(ClusRun cr) throws IOException, ClusException {
/* 46 */     ClusStatManager mgr = cr.getStatManager();
/* 47 */     ClusRuleSet res = new ClusRuleSet(mgr);
/* 48 */     RowData train = (RowData)cr.getTrainingSet();
/* 49 */     RowData valid = (RowData)cr.getPruneSet();
/* 50 */     WHTDStatistic global_valid = (WHTDStatistic)mgr.createStatistic(3);
/* 51 */     valid.calcTotalStatBitVector((ClusStatistic)global_valid);
/* 52 */     global_valid.calcMean();
/* 53 */     ClusSchema schema = train.getSchema();
/* 54 */     NominalAttrType[] descr = schema.getNominalAttrUse(1);
/* 55 */     for (int i = 0; i < descr.length; i++) {
/* 56 */       NominalAttrType attr = descr[i];
/* 57 */       for (int j = 0; j < attr.getNbValues(); j++) {
/* 58 */         boolean[] isin = new boolean[attr.getNbValues()];
/* 59 */         isin[j] = true;
/* 60 */         ClusRule rule = new ClusRule(mgr);
/* 61 */         rule.addTest((NodeTest)new SubsetTest(attr, 1, isin, 0.0D));
/* 62 */         WHTDStatistic stat = (WHTDStatistic)mgr.createStatistic(3);
/* 63 */         rule.computeCoverStat(train, (ClusStatistic)stat);
/* 64 */         WHTDStatistic valid_stat = (WHTDStatistic)mgr.createStatistic(3);
/* 65 */         rule.computeCoverStat(valid, (ClusStatistic)valid_stat);
/* 66 */         valid_stat.calcMean();
/* 67 */         stat.setValidationStat(valid_stat);
/* 68 */         stat.setGlobalStat(global_valid);
/* 69 */         stat.setSigLevel(this.m_SigLevel);
/* 70 */         stat.calcMean();
/* 71 */         if (stat.isValidPrediction()) {
/* 72 */           rule.setTargetStat((ClusStatistic)stat);
/* 73 */           res.add(rule);
/*    */         } 
/*    */       } 
/*    */     } 
/* 77 */     res.removeEmptyRules();
/* 78 */     res.simplifyRules();
/* 79 */     return res;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\algo\rules\ClusRulesForAttrs.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */