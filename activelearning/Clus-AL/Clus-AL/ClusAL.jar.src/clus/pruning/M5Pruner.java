/*    */ package clus.pruning;
/*    */ 
/*    */ import clus.algo.tdidt.ClusNode;
/*    */ import clus.data.attweights.ClusAttributeWeights;
/*    */ import clus.data.rows.RowData;
/*    */ import clus.statistic.RegressionStat;
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
/*    */ public class M5Pruner
/*    */   extends PruneTree
/*    */ {
/* 37 */   double m_PruningMult = 2.0D;
/*    */   double m_GlobalDeviation;
/*    */   ClusAttributeWeights m_ClusteringWeights;
/*    */   RowData m_TrainingData;
/*    */   
/*    */   public M5Pruner(ClusAttributeWeights prod, double mult) {
/* 43 */     this.m_ClusteringWeights = prod;
/* 44 */     this.m_PruningMult = mult;
/*    */   }
/*    */   
/*    */   public void prune(ClusNode node) {
/* 48 */     RegressionStat stat = (RegressionStat)node.getClusteringStat();
/* 49 */     this.m_GlobalDeviation = Math.sqrt(stat.getSVarS(this.m_ClusteringWeights) / stat.getTotalWeight());
/* 50 */     pruneRecursive(node);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public int getNbResults() {
/* 56 */     return 1;
/*    */   }
/*    */   
/*    */   private double pruningFactor(double num_instances, int num_params) {
/* 60 */     if (num_instances <= num_params) {
/* 61 */       return 10.0D;
/*    */     }
/* 63 */     return (num_instances + this.m_PruningMult * num_params) / (num_instances - num_params);
/*    */   }
/*    */ 
/*    */   
/*    */   public void pruneRecursive(ClusNode node) {
/* 68 */     if (node.atBottomLevel()) {
/*    */       return;
/*    */     }
/* 71 */     for (int i = 0; i < node.getNbChildren(); i++) {
/* 72 */       ClusNode child = (ClusNode)node.getChild(i);
/* 73 */       pruneRecursive(child);
/*    */     } 
/* 75 */     RegressionStat stat = (RegressionStat)node.getClusteringStat();
/* 76 */     double rmsLeaf = stat.getRMSE(this.m_ClusteringWeights);
/* 77 */     double adjustedErrorLeaf = rmsLeaf * pruningFactor(stat.getTotalWeight(), 1);
/* 78 */     double rmsSubTree = Math.sqrt(node.estimateClusteringSS(this.m_ClusteringWeights) / stat.getTotalWeight());
/* 79 */     double adjustedErrorTree = rmsSubTree * pruningFactor(stat.getTotalWeight(), node.getModelSize());
/*    */ 
/*    */     
/* 82 */     if (adjustedErrorLeaf <= adjustedErrorTree || adjustedErrorLeaf < this.m_GlobalDeviation * 1.0E-5D)
/*    */     {
/* 84 */       node.makeLeaf();
/*    */     }
/*    */   }
/*    */   
/*    */   public void setTrainingData(RowData data) {
/* 89 */     this.m_TrainingData = data;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\pruning\M5Pruner.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */