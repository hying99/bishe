/*    */ package clus.ext.hierarchical;
/*    */ 
/*    */ import clus.algo.tdidt.ClusNode;
/*    */ import clus.model.ClusModelInfo;
/*    */ import clus.pruning.PruneTree;
/*    */ import clus.statistic.ClusStatistic;
/*    */ import clus.util.ClusException;
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
/*    */ public class HierClassTresholdPruner
/*    */   extends PruneTree
/*    */ {
/*    */   protected double[] m_Thresholds;
/*    */   
/*    */   public HierClassTresholdPruner(double[] tresholds) {
/* 39 */     this.m_Thresholds = tresholds;
/*    */   }
/*    */   
/*    */   public void prune(ClusNode node) throws ClusException {
/* 43 */     prune(0, node);
/*    */   }
/*    */   
/*    */   public int getNbResults() {
/* 47 */     return this.m_Thresholds.length;
/*    */   }
/*    */   
/*    */   public String getPrunedName(int i) {
/* 51 */     return "T(" + this.m_Thresholds[i] + ")";
/*    */   }
/*    */   
/*    */   public double getThreshold(int i) {
/* 55 */     return this.m_Thresholds[i];
/*    */   }
/*    */   
/*    */   public void updatePrunedModelInfo(ClusModelInfo info) {
/* 59 */     info.setShouldWritePredictions(false);
/*    */   }
/*    */   
/*    */   public void prune(int result, ClusNode node) throws ClusException {
/* 63 */     pruneRecursive(node, this.m_Thresholds[result]);
/*    */   }
/*    */   
/*    */   public void pruneRecursive(ClusNode node, double threshold) throws ClusException {
/* 67 */     WHTDStatistic stat = (WHTDStatistic)node.getTargetStat();
/* 68 */     WHTDStatistic new_stat = (WHTDStatistic)stat.cloneStat();
/* 69 */     new_stat.copyAll((ClusStatistic)stat);
/* 70 */     new_stat.setThreshold(threshold);
/* 71 */     new_stat.calcMean();
/* 72 */     node.setTargetStat((ClusStatistic)new_stat);
/* 73 */     for (int i = 0; i < node.getNbChildren(); i++)
/* 74 */       pruneRecursive((ClusNode)node.getChild(i), threshold); 
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\ext\hierarchical\HierClassTresholdPruner.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */