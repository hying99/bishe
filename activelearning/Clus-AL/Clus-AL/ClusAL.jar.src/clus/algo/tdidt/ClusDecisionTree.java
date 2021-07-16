/*     */ package clus.algo.tdidt;
/*     */ 
/*     */ import clus.Clus;
/*     */ import clus.algo.ClusInductionAlgorithm;
/*     */ import clus.algo.ClusInductionAlgorithmType;
/*     */ import clus.algo.rules.ClusRuleSet;
/*     */ import clus.algo.rules.ClusRulesFromTree;
/*     */ import clus.data.rows.RowData;
/*     */ import clus.data.type.ClusSchema;
/*     */ import clus.ext.bestfirst.BestFirstInduce;
/*     */ import clus.ext.ilevelc.ILevelCInduce;
/*     */ import clus.main.ClusRun;
/*     */ import clus.main.Settings;
/*     */ import clus.model.ClusModel;
/*     */ import clus.model.ClusModelInfo;
/*     */ import clus.pruning.PruneTree;
/*     */ import clus.util.ClusException;
/*     */ import java.io.IOException;
/*     */ import jeans.util.cmdline.CMDLineArgs;
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
/*     */ public class ClusDecisionTree
/*     */   extends ClusInductionAlgorithmType
/*     */ {
/*     */   public static final int LEVEL_WISE = 0;
/*     */   public static final int DEPTH_FIRST = 1;
/*     */   
/*     */   public ClusDecisionTree(Clus clus) {
/*  48 */     super(clus);
/*     */   }
/*     */   
/*     */   public void printInfo() {
/*  52 */     System.out.println("TDIDT");
/*  53 */     System.out.println("Heuristic: " + getStatManager().getHeuristicName());
/*     */   }
/*     */ 
/*     */   
/*     */   public ClusInductionAlgorithm createInduce(ClusSchema schema, Settings sett, CMDLineArgs cargs) throws ClusException, IOException {
/*  58 */     if (sett.hasConstraintFile()) {
/*  59 */       boolean fillin = cargs.hasOption("fillin");
/*  60 */       return new ConstraintDFInduce(schema, sett, fillin);
/*  61 */     }  if (sett.isSectionILevelCEnabled())
/*  62 */       return (ClusInductionAlgorithm)new ILevelCInduce(schema, sett); 
/*  63 */     if (schema.isSparse())
/*  64 */       return new DepthFirstInduceSparse(schema, sett); 
/*  65 */     if (sett.checkInductionOrder("DepthFirst")) {
/*  66 */       return new DepthFirstInduce(schema, sett);
/*     */     }
/*  68 */     return (ClusInductionAlgorithm)new BestFirstInduce(schema, sett);
/*     */   }
/*     */ 
/*     */   
/*     */   public static final ClusNode pruneToRoot(ClusNode orig) {
/*  73 */     ClusNode pruned = (ClusNode)orig.cloneNode();
/*  74 */     pruned.makeLeaf();
/*  75 */     return pruned;
/*     */   }
/*     */   
/*     */   public static ClusModel induceDefault(ClusRun cr) {
/*  79 */     ClusNode node = new ClusNode();
/*  80 */     RowData data = (RowData)cr.getTrainingSet();
/*  81 */     node.initTargetStat(cr.getStatManager(), data);
/*  82 */     node.computePrediction();
/*  83 */     node.makeLeaf();
/*  84 */     return node;
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
/*     */   public void convertToRules(ClusRun cr, ClusModelInfo model) throws ClusException, IOException {
/*  97 */     ClusNode tree_root = (ClusNode)model.getModel();
/*  98 */     ClusRulesFromTree rft = new ClusRulesFromTree(true, getSettings().rulesFromTree());
/*  99 */     ClusRuleSet rule_set = null;
/* 100 */     boolean compDis = getSettings().computeDispersion();
/*     */     
/* 102 */     rule_set = rft.constructRules(cr, tree_root, getStatManager(), compDis, getSettings().getRulePredictionMethod());
/* 103 */     rule_set.addDataToRules((RowData)cr.getTrainingSet());
/*     */     
/* 105 */     ClusModelInfo rules_info = cr.addModelInfo("Rules-" + model.getName());
/* 106 */     rules_info.setModel((ClusModel)rule_set);
/*     */   }
/*     */   
/*     */   public void pruneAll(ClusRun cr) throws ClusException, IOException {
/* 110 */     ClusNode orig = (ClusNode)cr.getModel(1);
/* 111 */     orig.numberTree();
/* 112 */     PruneTree pruner = getStatManager().getTreePruner(cr.getPruneSet());
/* 113 */     pruner.setTrainingData((RowData)cr.getTrainingSet());
/* 114 */     int nb = pruner.getNbResults();
/* 115 */     for (int i = 0; i < nb; i++) {
/* 116 */       ClusModelInfo pruned_info = pruner.getPrunedModelInfo(i, orig);
/* 117 */       cr.addModelInfo(pruned_info);
/*     */     } 
/*     */   }
/*     */   
/*     */   public final ClusModel pruneSingle(ClusModel orig, ClusRun cr) throws ClusException {
/* 122 */     ClusNode pruned = (ClusNode)((ClusNode)orig).cloneTree();
/* 123 */     PruneTree pruner = getStatManager().getTreePruner(cr.getPruneSet());
/* 124 */     pruner.setTrainingData((RowData)cr.getTrainingSet());
/* 125 */     pruner.prune(pruned);
/* 126 */     return pruned;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void postProcess(ClusRun cr) throws ClusException, IOException {
/* 134 */     ClusNode orig = (ClusNode)cr.getModel(1);
/* 135 */     ClusModelInfo orig_info = cr.getModelInfo(1);
/* 136 */     ClusNode defmod = pruneToRoot(orig);
/* 137 */     ClusModelInfo def_info = cr.addModelInfo(0);
/* 138 */     def_info.setModel(defmod);
/* 139 */     if (getSettings().rulesFromTree() != 0) {
/* 140 */       ClusModelInfo model = cr.getModelInfoFallback(2, 1);
/* 141 */       convertToRules(cr, model);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\algo\tdidt\ClusDecisionTree.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */