/*     */ package clus.algo.rules;
/*     */ 
/*     */ import clus.algo.tdidt.ClusNode;
/*     */ import clus.data.rows.RowData;
/*     */ import clus.main.ClusRun;
/*     */ import clus.main.ClusStatManager;
/*     */ import clus.model.test.NodeTest;
/*     */ import clus.tools.optimization.GDAlg;
/*     */ import clus.tools.optimization.OptAlg;
/*     */ import clus.tools.optimization.OptProbl;
/*     */ import clus.tools.optimization.de.DeAlg;
/*     */ import clus.util.ClusException;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ClusRulesFromTree
/*     */ {
/*     */   protected boolean m_Validated;
/*     */   protected int m_Mode;
/*     */   
/*     */   public ClusRulesFromTree(boolean onlyValidated, int mode) {
/*  64 */     this.m_Validated = onlyValidated;
/*  65 */     this.m_Mode = mode;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ClusRuleSet constructRules(ClusRun cr, ClusNode node, ClusStatManager mgr, boolean computeDispersion, int optimizeRuleWeights) throws ClusException, IOException {
/*  86 */     ClusRuleSet ruleSet = constructRules(node, mgr);
/*     */     
/*  88 */     RowData data = (RowData)cr.getTrainingSet();
/*     */ 
/*     */     
/*  91 */     if (optimizeRuleWeights == 6 || optimizeRuleWeights == 8) {
/*     */       DeAlg deAlg;
/*  93 */       OptAlg optAlg = null;
/*     */       
/*  95 */       OptProbl.OptParam param = ruleSet.giveFormForWeightOptimization(null, data);
/*     */ 
/*     */       
/*  98 */       if (optimizeRuleWeights == 8) {
/*  99 */         GDAlg gDAlg = new GDAlg(mgr, param, ruleSet);
/*     */       } else {
/* 101 */         deAlg = new DeAlg(mgr, param, ruleSet);
/*     */       } 
/*     */       
/* 104 */       ArrayList<Double> weights = deAlg.optimize();
/*     */ 
/*     */       
/* 107 */       System.out.print("The weights for rules from trees:");
/* 108 */       for (int j = 0; j < ruleSet.getModelSize(); j++) {
/* 109 */         ruleSet.getRule(j).setOptWeight(((Double)weights.get(j)).doubleValue());
/* 110 */         System.out.print(((Double)weights.get(j)).doubleValue() + "; ");
/*     */       } 
/* 112 */       System.out.print("\n");
/* 113 */       ruleSet.removeLowWeightRules();
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 118 */     if (computeDispersion) {
/*     */ 
/*     */ 
/*     */       
/* 122 */       ruleSet.addDataToRules(data);
/*     */ 
/*     */       
/* 125 */       ruleSet.computeDispersion(0);
/* 126 */       ruleSet.removeDataFromRules();
/* 127 */       if (cr.getTestIter() != null) {
/* 128 */         RowData testdata = cr.getTestSet();
/* 129 */         ruleSet.addDataToRules(testdata);
/*     */         
/* 131 */         ruleSet.computeDispersion(1);
/* 132 */         ruleSet.removeDataFromRules();
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 137 */     ruleSet.numberRules();
/* 138 */     return ruleSet;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ClusRuleSet constructRules(ClusNode node, ClusStatManager mgr) {
/* 150 */     ClusRuleSet ruleSet = new ClusRuleSet(mgr);
/* 151 */     ClusRule init = new ClusRule(mgr);
/*     */     
/* 153 */     constructRecursive(node, init, ruleSet);
/* 154 */     ruleSet.removeEmptyRules();
/* 155 */     ruleSet.simplifyRules();
/* 156 */     ruleSet.setTargetStat(node.getTargetStat());
/* 157 */     return ruleSet;
/*     */   }
/*     */ 
/*     */   
/*     */   public void constructRecursive(ClusNode node, ClusRule rule, ClusRuleSet set) {
/* 162 */     if ((node.atBottomLevel() || this.m_Mode == 2) && (
/* 163 */       !this.m_Validated || node.getTargetStat().isValidPrediction())) {
/* 164 */       rule.setTargetStat(node.getTargetStat());
/* 165 */       rule.setID(node.getID());
/* 166 */       set.add(rule);
/*     */     } 
/*     */     
/* 169 */     NodeTest test = node.getTest();
/* 170 */     for (int i = 0; i < node.getNbChildren(); i++) {
/* 171 */       ClusNode child = (ClusNode)node.getChild(i);
/* 172 */       NodeTest branchTest = test.getBranchTest(i);
/* 173 */       ClusRule child_rule = rule.cloneRule();
/* 174 */       child_rule.addTest(branchTest);
/* 175 */       constructRecursive(child, child_rule, set);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\algo\rules\ClusRulesFromTree.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */