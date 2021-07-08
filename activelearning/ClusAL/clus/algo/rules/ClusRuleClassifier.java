/*    */ package clus.algo.rules;
/*    */ 
/*    */ import clus.Clus;
/*    */ import clus.algo.ClusInductionAlgorithm;
/*    */ import clus.algo.ClusInductionAlgorithmType;
/*    */ import clus.algo.tdidt.ClusDecisionTree;
/*    */ import clus.data.type.ClusSchema;
/*    */ import clus.data.type.NumericAttrType;
/*    */ import clus.main.ClusRun;
/*    */ import clus.main.Settings;
/*    */ import clus.model.ClusModel;
/*    */ import clus.model.ClusModelInfo;
/*    */ import clus.util.ClusException;
/*    */ import java.io.IOException;
/*    */ import jeans.util.cmdline.CMDLineArgs;
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
/*    */ public class ClusRuleClassifier
/*    */   extends ClusInductionAlgorithmType
/*    */ {
/*    */   public ClusRuleClassifier(Clus clus) {
/* 44 */     super(clus);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public ClusInductionAlgorithm createInduce(ClusSchema schema, Settings sett, CMDLineArgs cargs) throws ClusException, IOException {
/*    */     ClusInductionAlgorithm induce;
/* 52 */     if (sett.isRulePredictionOptimized()) {
/* 53 */       NumericAttrType[] descrNumTypes = schema.getNumericAttrUse(1);
/* 54 */       NumericAttrType[] tarNumTypes = schema.getNumericAttrUse(3);
/* 55 */       RuleNormalization.initialize(Clus.calcStdDevsForTheSet(getClus().getData(), descrNumTypes), 
/* 56 */           Clus.calcStdDevsForTheSet(getClus().getData(), tarNumTypes));
/*    */     } 
/*    */ 
/*    */     
/* 60 */     if (sett.getCoveringMethod() == 9) {
/* 61 */       induce = new ClusRuleFromTreeInduce(schema, sett, getClus());
/*    */     } else {
/* 63 */       if (sett.isSectionILevelCEnabled()) {
/* 64 */         induce = new ClusRuleConstraintInduce(schema, sett);
/*    */       } else {
/* 66 */         induce = new ClusRuleInduce(schema, sett);
/*    */       } 
/* 68 */       induce.getStatManager().setRuleInduceOnly(true);
/*    */     } 
/* 70 */     induce.getStatManager().initRuleSettings();
/* 71 */     return induce;
/*    */   }
/*    */   
/*    */   public void printInfo() {
/* 75 */     if (!getSettings().isRandomRules()) {
/* 76 */       System.out.println("RuleSystem based on CN2");
/* 77 */       System.out.println("Heuristic: " + getStatManager().getHeuristicName());
/*    */     } else {
/* 79 */       System.out.println("RuleSystem generating random rules");
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public void pruneAll(ClusRun cr) throws ClusException, IOException {}
/*    */   
/*    */   public ClusModel pruneSingle(ClusModel model, ClusRun cr) throws ClusException, IOException {
/* 87 */     return model;
/*    */   }
/*    */ 
/*    */   
/*    */   public void postProcess(ClusRun cr) throws ClusException, IOException {
/* 92 */     if (getSettings().getCoveringMethod() != 9) {
/* 93 */       ClusModelInfo def_model = cr.addModelInfo(0);
/* 94 */       def_model.setModel(ClusDecisionTree.induceDefault(cr));
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\algo\rules\ClusRuleClassifier.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */