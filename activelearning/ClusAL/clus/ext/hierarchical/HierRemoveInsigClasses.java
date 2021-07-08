/*     */ package clus.ext.hierarchical;
/*     */ 
/*     */ import clus.algo.tdidt.ClusNode;
/*     */ import clus.data.ClusData;
/*     */ import clus.data.rows.DataTuple;
/*     */ import clus.data.rows.RowData;
/*     */ import clus.pruning.PruneTree;
/*     */ import clus.statistic.ClusStatistic;
/*     */ import clus.util.ClusException;
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
/*     */ public class HierRemoveInsigClasses
/*     */   extends PruneTree
/*     */ {
/*     */   PruneTree m_Pruner;
/*     */   ClusData m_PruneSet;
/*     */   ClassHierarchy m_Hier;
/*     */   boolean m_NoRoot;
/*     */   boolean m_UseBonferroni;
/*     */   double m_SigLevel;
/*     */   int m_Bonferroni;
/*     */   
/*     */   public HierRemoveInsigClasses(ClusData pruneset, PruneTree other, boolean bonf, ClassHierarchy hier) {
/*  45 */     this.m_Pruner = other;
/*  46 */     this.m_PruneSet = pruneset;
/*  47 */     this.m_Hier = hier;
/*  48 */     this.m_UseBonferroni = bonf;
/*     */   }
/*     */   
/*     */   public int getNbResults() {
/*  52 */     return 1;
/*     */   }
/*     */   
/*     */   public void setNoRootPreds(boolean noroot) {
/*  56 */     this.m_NoRoot = noroot;
/*     */   }
/*     */   
/*     */   public void setSignificance(double siglevel) {
/*  60 */     this.m_SigLevel = siglevel;
/*     */   }
/*     */   
/*     */   public void prune(ClusNode node) throws ClusException {
/*  64 */     this.m_Pruner.prune(node);
/*  65 */     if (this.m_SigLevel != 0.0D && this.m_PruneSet.getNbRows() != 0) {
/*     */       
/*  67 */       WHTDStatistic global = (WHTDStatistic)node.getTargetStat().cloneStat();
/*  68 */       this.m_PruneSet.calcTotalStat((ClusStatistic)global);
/*  69 */       global.calcMean();
/*  70 */       this.m_Bonferroni = computeNRecursive(node);
/*  71 */       executeRecursive(node, global, (RowData)this.m_PruneSet);
/*     */     } 
/*     */   }
/*     */   
/*     */   public int computeNRecursive(ClusNode node) {
/*  76 */     int result = 0;
/*  77 */     if (node.atBottomLevel()) {
/*  78 */       WHTDStatistic stat = (WHTDStatistic)node.getTargetStat();
/*  79 */       result += stat.getNbPredictedClasses();
/*     */     } 
/*  81 */     for (int i = 0; i < node.getNbChildren(); i++) {
/*  82 */       result += computeNRecursive((ClusNode)node.getChild(i));
/*     */     }
/*  84 */     return result;
/*     */   }
/*     */   
/*     */   public void executeRecursive(ClusNode node, WHTDStatistic global, RowData data) {
/*  88 */     int arity = node.getNbChildren();
/*  89 */     for (int i = 0; i < arity; i++) {
/*  90 */       RowData subset = data.applyWeighted(node.getTest(), i);
/*  91 */       executeRecursive((ClusNode)node.getChild(i), global, subset);
/*     */     } 
/*  93 */     WHTDStatistic orig = (WHTDStatistic)node.getTargetStat();
/*  94 */     WHTDStatistic valid = (WHTDStatistic)orig.cloneStat();
/*  95 */     for (int j = 0; j < data.getNbRows(); j++) {
/*  96 */       DataTuple tuple = data.getTuple(j);
/*  97 */       valid.updateWeighted(tuple, j);
/*     */     } 
/*  99 */     valid.calcMean();
/* 100 */     WHTDStatistic pred = (WHTDStatistic)orig.cloneStat();
/* 101 */     pred.copy((ClusStatistic)orig);
/* 102 */     pred.setValidationStat(valid);
/* 103 */     pred.setGlobalStat(global);
/* 104 */     if (this.m_UseBonferroni) {
/* 105 */       pred.setSigLevel(this.m_SigLevel / this.m_Bonferroni);
/*     */     } else {
/* 107 */       pred.setSigLevel(this.m_SigLevel);
/*     */     } 
/* 109 */     pred.calcMean();
/* 110 */     node.setTargetStat((ClusStatistic)pred);
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\ext\hierarchical\HierRemoveInsigClasses.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */