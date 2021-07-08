/*     */ package clus.algo.tdidt;
/*     */ 
/*     */ import clus.algo.ClusInductionAlgorithm;
/*     */ import clus.algo.split.CurrentBestTestAndHeuristic;
/*     */ import clus.algo.split.NominalSplit;
/*     */ import clus.data.rows.DataTuple;
/*     */ import clus.data.rows.RowData;
/*     */ import clus.data.type.ClusAttrType;
/*     */ import clus.data.type.ClusSchema;
/*     */ import clus.data.type.NominalAttrType;
/*     */ import clus.data.type.NumericAttrType;
/*     */ import clus.error.multiscore.MultiScore;
/*     */ import clus.ext.constraint.ClusConstraintFile;
/*     */ import clus.main.ClusRun;
/*     */ import clus.main.Settings;
/*     */ import clus.model.ClusModel;
/*     */ import clus.model.test.NodeTest;
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
/*     */ public class ConstraintDFInduce
/*     */   extends DepthFirstInduce
/*     */ {
/*     */   protected boolean m_FillIn;
/*     */   protected String m_ConstrFile;
/*     */   
/*     */   public ConstraintDFInduce(ClusSchema schema, Settings sett, boolean fillin) throws ClusException, IOException {
/*  48 */     super(schema, sett);
/*  49 */     this.m_FillIn = fillin;
/*  50 */     this.m_ConstrFile = sett.getConstraintFile();
/*     */   }
/*     */   
/*     */   public ConstraintDFInduce(ClusInductionAlgorithm other) {
/*  54 */     super(other, (NominalSplit)null);
/*     */   }
/*     */   
/*     */   public void fillInStatsAndTests(ClusNode node, RowData data) {
/*  58 */     NodeTest test = node.getTest();
/*  59 */     if (test == null) {
/*     */       return;
/*     */     }
/*     */     
/*  63 */     if (!test.hasConstants()) {
/*     */       
/*  65 */       if (initSelectorAndStopCrit(node, data)) {
/*  66 */         node.makeLeaf();
/*     */         return;
/*     */       } 
/*  69 */       ClusAttrType at = test.getType();
/*  70 */       if (at instanceof NominalAttrType) { getFindBestTest().findNominal((NominalAttrType)at, data); }
/*  71 */       else { getFindBestTest().findNumeric((NumericAttrType)at, data); }
/*  72 */        CurrentBestTestAndHeuristic best = this.m_FindBestTest.getBestTest();
/*  73 */       if (best.hasBestTest()) {
/*  74 */         node.testToNode(best);
/*  75 */         if (Settings.VERBOSE > 0) System.out.println("Fill in Test: " + node.getTestString() + " -> " + best.getHeuristicValue()); 
/*     */       } else {
/*  77 */         node.makeLeaf();
/*     */         return;
/*     */       } 
/*     */     } else {
/*  81 */       double tot_weight = 0.0D;
/*  82 */       double unk_weight = 0.0D;
/*  83 */       double tot_no_unk = 0.0D;
/*  84 */       double[] branch_weight = new double[test.getNbChildren()]; int i;
/*  85 */       for (i = 0; i < data.getNbRows(); i++) {
/*  86 */         DataTuple tuple = data.getTuple(i);
/*  87 */         int pred = test.predictWeighted(tuple);
/*  88 */         if (pred == -1) {
/*  89 */           unk_weight += tuple.getWeight();
/*     */         } else {
/*  91 */           branch_weight[pred] = branch_weight[pred] + tuple.getWeight();
/*  92 */           tot_no_unk += tuple.getWeight();
/*     */         } 
/*  94 */         tot_weight += tuple.getWeight();
/*     */       } 
/*  96 */       for (i = 0; i < test.getNbChildren(); i++) {
/*  97 */         test.setProportion(i, branch_weight[i] / tot_no_unk);
/*     */       }
/*  99 */       test.setUnknownFreq(unk_weight / tot_weight);
/*     */     } 
/* 101 */     NodeTest best_test = node.getTest();
/* 102 */     for (int j = 0; j < node.getNbChildren(); j++) {
/* 103 */       ClusNode child = (ClusNode)node.getChild(j);
/* 104 */       RowData subset = data.applyWeighted(best_test, j);
/* 105 */       child.initTargetStat(this.m_StatManager, subset);
/* 106 */       child.initClusteringStat(this.m_StatManager, subset);
/* 107 */       fillInStatsAndTests(child, subset);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void induceRecursive(ClusNode node, RowData data) {
/* 112 */     if (node.atBottomLevel()) {
/* 113 */       induce(node, data);
/*     */     } else {
/* 115 */       NodeTest test = node.getTest();
/* 116 */       for (int j = 0; j < node.getNbChildren(); j++) {
/* 117 */         ClusNode child = (ClusNode)node.getChild(j);
/* 118 */         RowData subset = data.applyWeighted(test, j);
/* 119 */         induceRecursive(child, subset);
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public ClusNode createRootNode(RowData data, ClusStatistic cstat, ClusStatistic tstat) {
/* 125 */     ClusConstraintFile file = ClusConstraintFile.getInstance();
/* 126 */     ClusNode root = file.getClone(this.m_ConstrFile);
/* 127 */     root.setClusteringStat(cstat);
/* 128 */     root.setTargetStat(tstat);
/* 129 */     fillInStatsAndTests(root, data);
/* 130 */     return root;
/*     */   }
/*     */   
/*     */   public ClusNode fillInInTree(RowData data, ClusNode tree, ClusStatistic cstat, ClusStatistic tstat) {
/* 134 */     ClusNode root = tree.cloneTreeWithVisitors();
/* 135 */     root.setClusteringStat(cstat);
/* 136 */     root.setTargetStat(tstat);
/* 137 */     fillInStatsAndTests(root, data);
/* 138 */     return root;
/*     */   }
/*     */   
/*     */   public ClusNode fillInInduce(ClusRun cr, ClusNode node, MultiScore score) throws ClusException {
/* 142 */     RowData data = (RowData)cr.getTrainingSet();
/* 143 */     ClusStatistic cstat = createTotalClusteringStat(data);
/* 144 */     ClusStatistic tstat = createTotalTargetStat(data);
/* 145 */     initSelectorAndSplit(cstat);
/* 146 */     ClusNode root = fillInInTree(data, node, cstat, tstat);
/* 147 */     root.postProc(score);
/* 148 */     cleanSplit();
/* 149 */     return root;
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
/*     */   public ClusModel induceSingleUnpruned(ClusRun cr) throws ClusException, IOException {
/* 168 */     RowData data = (RowData)cr.getTrainingSet();
/* 169 */     ClusStatistic cstat = createTotalClusteringStat(data);
/* 170 */     ClusStatistic tstat = createTotalTargetStat(data);
/* 171 */     initSelectorAndSplit(cstat);
/* 172 */     ClusNode root = createRootNode(data, cstat, tstat);
/* 173 */     this.m_Root = root;
/* 174 */     if (!this.m_FillIn)
/*     */     {
/* 176 */       induceRecursive(root, data);
/*     */     }
/* 178 */     root.postProc((MultiScore)null);
/* 179 */     cleanSplit();
/* 180 */     return root;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\algo\tdidt\ConstraintDFInduce.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */