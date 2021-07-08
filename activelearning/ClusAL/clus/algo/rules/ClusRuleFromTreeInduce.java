/*     */ package clus.algo.rules;
/*     */ 
/*     */ import clus.Clus;
/*     */ import clus.algo.tdidt.ClusNode;
/*     */ import clus.data.rows.RowData;
/*     */ import clus.data.type.ClusSchema;
/*     */ import clus.ext.ensembles.ClusEnsembleInduce;
/*     */ import clus.ext.ensembles.ClusForest;
/*     */ import clus.main.ClusRun;
/*     */ import clus.main.Settings;
/*     */ import clus.model.ClusModel;
/*     */ import clus.model.ClusModelInfo;
/*     */ import clus.statistic.ClusStatistic;
/*     */ import clus.util.ClusException;
/*     */ import java.io.IOException;
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
/*     */ public class ClusRuleFromTreeInduce
/*     */   extends ClusRuleInduce
/*     */ {
/*     */   protected Clus m_Clus;
/*     */   
/*     */   public ClusRuleFromTreeInduce(ClusSchema schema, Settings sett, Clus clus) throws ClusException, IOException {
/*  42 */     super(schema, sett);
/*  43 */     this.m_Clus = clus;
/*  44 */     sett.setSectionEnsembleEnabled(true);
/*  45 */     getSettings().setEnsembleMode(true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ClusModel induceSingleUnpruned(ClusRun cr) throws ClusException, IOException {
/*     */     ClusStatistic left_over;
/*  57 */     getSettings().disableRuleInduceParams();
/*     */ 
/*     */     
/*  60 */     ClusEnsembleInduce ensemble = new ClusEnsembleInduce(this, this.m_Clus);
/*     */     
/*  62 */     ensemble.induceAll(cr);
/*  63 */     getSettings().returnRuleInduceParams();
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
/*  76 */     ClusForest forestModel = (ClusForest)cr.getModel(1);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  81 */     ClusRulesFromTree treeTransform = new ClusRulesFromTree(true, getSettings().rulesFromTree());
/*  82 */     ClusRuleSet ruleSet = new ClusRuleSet(getStatManager());
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  87 */     int numberOfUniqueRules = 0;
/*     */     
/*  89 */     for (int iTree = 0; iTree < forestModel.getNbModels(); iTree++) {
/*     */ 
/*     */       
/*  92 */       ClusNode treeRootNode = (ClusNode)forestModel.getModel(iTree);
/*     */ 
/*     */       
/*  95 */       numberOfUniqueRules += ruleSet
/*  96 */         .addRuleSet(treeTransform.constructRules(treeRootNode, 
/*  97 */             getStatManager()));
/*     */     } 
/*     */     
/* 100 */     if (Settings.VERBOSE > 0) {
/* 101 */       System.out.println("Transformed " + forestModel.getNbModels() + " trees in ensemble into rules.\n\tCreated " + ruleSet
/* 102 */           .getModelSize() + " rules. (" + numberOfUniqueRules + " of them are unique.)");
/*     */     }
/* 104 */     RowData trainingData = (RowData)cr.getTrainingSet();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 111 */     if (trainingData.getNbRows() > 0) {
/* 112 */       left_over = createTotalTargetStat(trainingData);
/* 113 */       left_over.calcMean();
/*     */     } else {
/* 115 */       if (Settings.VERBOSE > 0) System.out.println("All training examples covered - default rule on entire training set!"); 
/* 116 */       ruleSet.m_Comment = new String(" (on entire training set)");
/* 117 */       left_over = getStatManager().getTrainSetStat(3).cloneStat();
/* 118 */       left_over.copy(getStatManager().getTrainSetStat(3));
/* 119 */       left_over.calcMean();
/*     */       
/* 121 */       System.err.println(left_over.toString());
/*     */     } 
/* 123 */     System.out.println("Left Over: " + left_over);
/* 124 */     ruleSet.setTargetStat(left_over);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 133 */     ruleSet.postProc();
/*     */ 
/*     */ 
/*     */     
/* 137 */     if (getSettings().isRulePredictionOptimized()) {
/* 138 */       ruleSet = optimizeRuleSet(ruleSet, (RowData)cr.getTrainingSet());
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 144 */     if (getSettings().computeDispersion()) {
/* 145 */       ruleSet.computeDispersion(0);
/* 146 */       ruleSet.removeDataFromRules();
/* 147 */       if (cr.getTestIter() != null) {
/* 148 */         RowData testdata = cr.getTestSet();
/* 149 */         ruleSet.addDataToRules(testdata);
/* 150 */         ruleSet.computeDispersion(1);
/* 151 */         ruleSet.removeDataFromRules();
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 156 */     ruleSet.numberRules();
/* 157 */     return ruleSet;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void induceAll(ClusRun cr) throws ClusException, IOException {
/* 165 */     RowData trainData = (RowData)cr.getTrainingSet();
/* 166 */     getStatManager().getHeuristic().setTrainData(trainData);
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
/* 184 */     ClusModel model = induceSingleUnpruned(cr);
/* 185 */     ClusModelInfo rules_model = cr.addModelInfo(2);
/* 186 */     rules_model.setModel(model);
/* 187 */     rules_model.setName("Rules");
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\algo\rules\ClusRuleFromTreeInduce.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */