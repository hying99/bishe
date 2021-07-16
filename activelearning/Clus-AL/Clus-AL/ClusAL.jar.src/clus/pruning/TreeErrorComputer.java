/*     */ package clus.pruning;
/*     */ 
/*     */ import clus.algo.tdidt.ClusNode;
/*     */ import clus.data.rows.DataTuple;
/*     */ import clus.data.rows.RowData;
/*     */ import clus.error.ClusError;
/*     */ import clus.error.ClusErrorList;
/*     */ import clus.model.ClusModel;
/*     */ import clus.model.processor.ClusModelProcessor;
/*     */ import clus.statistic.ClusStatistic;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class TreeErrorComputer
/*     */   extends ClusModelProcessor
/*     */ {
/*     */   public static void recursiveInitialize(ClusNode node, ErrorVisitor visitor) {
/*  41 */     node.setVisitor(visitor.createInstance());
/*     */     
/*  43 */     for (int i = 0; i < node.getNbChildren(); i++) {
/*  44 */       ClusNode child = (ClusNode)node.getChild(i);
/*  45 */       recursiveInitialize(child, visitor);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void modelUpdate(DataTuple tuple, ClusModel model) throws IOException {
/*  50 */     ClusNode tree = (ClusNode)model;
/*  51 */     ErrorVisitor visitor = (ErrorVisitor)tree.getVisitor();
/*  52 */     visitor.testerr.addExample(tuple, tree.getTargetStat());
/*     */   }
/*     */   
/*     */   public boolean needsModelUpdate() {
/*  56 */     return true;
/*     */   }
/*     */   
/*     */   public boolean needsInternalNodes() {
/*  60 */     return true;
/*     */   }
/*     */   
/*     */   public static ClusError computeErrorOptimized(ClusNode tree, RowData test, ClusErrorList error, boolean miss) {
/*  64 */     error.reset();
/*  65 */     error.setNbExamples(test.getNbRows());
/*  66 */     ClusError child_err = error.getFirstError().getErrorClone();
/*  67 */     computeErrorOptimized(tree, test, child_err, miss);
/*  68 */     return child_err;
/*     */   }
/*     */ 
/*     */   
/*     */   public static void computeErrorOptimized(ClusNode tree, RowData test, ClusError error, boolean miss) {
/*  73 */     computeErrorStandard(tree, test, error);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ClusError computeClusteringErrorStandard(ClusNode tree, RowData test, ClusErrorList error) {
/*  84 */     error.reset();
/*  85 */     error.setNbExamples(test.getNbRows());
/*  86 */     ClusError child_err = error.getFirstError().getErrorClone();
/*  87 */     computeClusteringErrorStandard(tree, test, child_err);
/*  88 */     return child_err;
/*     */   }
/*     */   
/*     */   public static void computeClusteringErrorStandard(ClusNode tree, RowData test, ClusError error) {
/*  92 */     for (int i = 0; i < test.getNbRows(); i++) {
/*  93 */       DataTuple tuple = test.getTuple(i);
/*  94 */       ClusStatistic pred = tree.clusterWeighted(tuple);
/*  95 */       error.addExample(tuple, pred);
/*     */     } 
/*     */   }
/*     */   
/*     */   public static void computeErrorStandard(ClusNode tree, RowData test, ClusError error) {
/* 100 */     for (int i = 0; i < test.getNbRows(); i++) {
/* 101 */       DataTuple tuple = test.getTuple(i);
/* 102 */       ClusStatistic pred = tree.predictWeighted(tuple);
/* 103 */       error.addExample(tuple, pred);
/*     */     } 
/*     */   }
/*     */   
/*     */   public static void computeErrorNode(ClusNode node, RowData test, ClusError error) {
/* 108 */     ClusStatistic pred = node.getTargetStat();
/* 109 */     for (int i = 0; i < test.getNbRows(); i++) {
/* 110 */       DataTuple tuple = test.getTuple(i);
/* 111 */       error.addExample(tuple, pred);
/*     */     } 
/*     */   }
/*     */   
/*     */   public static void initializeTestErrorsData(ClusNode tree, RowData test, ClusError error) throws IOException {
/* 116 */     TreeErrorComputer comp = new TreeErrorComputer();
/* 117 */     initializeTestErrors(tree, error);
/* 118 */     for (int i = 0; i < test.getNbRows(); i++) {
/* 119 */       DataTuple tuple = test.getTuple(i);
/* 120 */       tree.applyModelProcessor(tuple, comp);
/*     */     } 
/*     */   }
/*     */   
/*     */   public static void initializeTestErrors(ClusNode node, ClusError error) {
/* 125 */     ErrorVisitor visitor = (ErrorVisitor)node.getVisitor();
/* 126 */     visitor.testerr = error.getErrorClone(error.getParent());
/* 127 */     for (int i = 0; i < node.getNbChildren(); i++) {
/* 128 */       ClusNode child = (ClusNode)node.getChild(i);
/* 129 */       initializeTestErrors(child, error);
/*     */     } 
/*     */   }
/*     */   
/*     */   public static void computeErrorSimple(ClusNode node, ClusError sum) {
/* 134 */     if (node.atBottomLevel()) {
/* 135 */       ErrorVisitor visitor = (ErrorVisitor)node.getVisitor();
/* 136 */       sum.add(visitor.testerr);
/*     */     } else {
/* 138 */       for (int i = 0; i < node.getNbChildren(); i++) {
/* 139 */         ClusNode child = (ClusNode)node.getChild(i);
/* 140 */         computeErrorSimple(child, sum);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\pruning\TreeErrorComputer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */