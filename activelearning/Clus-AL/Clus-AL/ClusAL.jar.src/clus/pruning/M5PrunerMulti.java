/*     */ package clus.pruning;
/*     */ 
/*     */ import clus.algo.tdidt.ClusNode;
/*     */ import clus.data.attweights.ClusAttributeWeights;
/*     */ import clus.data.rows.RowData;
/*     */ import clus.statistic.RegressionStat;
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
/*     */ public class M5PrunerMulti
/*     */   extends PruneTree
/*     */ {
/*  37 */   double m_F = 1.0E-5D;
/*  38 */   double m_PruningMult = 2.0D;
/*     */   double[] m_GlobalRMSE;
/*     */   ClusAttributeWeights m_TargetWeights;
/*     */   RowData m_TrainingData;
/*     */   
/*     */   public M5PrunerMulti(ClusAttributeWeights prod, double mult) {
/*  44 */     this.m_TargetWeights = prod;
/*  45 */     this.m_PruningMult = mult;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void prune(ClusNode node) {
/*  51 */     RegressionStat stat = (RegressionStat)node.getClusteringStat();
/*  52 */     this.m_GlobalRMSE = stat.getRootScaledVariances(this.m_TargetWeights);
/*  53 */     pruneRecursive(node);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int getNbResults() {
/*  59 */     return 1;
/*     */   }
/*     */   
/*     */   private double pruningFactor(double num_instances, int num_params) {
/*  63 */     if (num_instances <= num_params) {
/*  64 */       return 10.0D;
/*     */     }
/*  66 */     return (num_instances + this.m_PruningMult * num_params) / (num_instances - num_params);
/*     */   }
/*     */ 
/*     */   
/*     */   public static double estimateRootScaledVariance(ClusNode tree, int attr, ClusAttributeWeights scale) {
/*  71 */     double totweight = tree.getClusteringStat().getTotalWeight();
/*  72 */     return Math.sqrt(estimateScaledVariance(tree, attr, scale) / totweight);
/*     */   }
/*     */   
/*     */   public static double estimateScaledVariance(ClusNode tree, int attr, ClusAttributeWeights scale) {
/*  76 */     if (tree.atBottomLevel()) {
/*  77 */       RegressionStat stat = (RegressionStat)tree.getClusteringStat();
/*  78 */       return stat.getScaledSS(attr, scale);
/*     */     } 
/*  80 */     double result = 0.0D;
/*  81 */     for (int i = 0; i < tree.getNbChildren(); i++) {
/*  82 */       ClusNode child = (ClusNode)tree.getChild(i);
/*  83 */       result += estimateScaledVariance(child, attr, scale);
/*     */     } 
/*  85 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean allAccurate(RegressionStat stat) {
/*  91 */     for (int i = 0; i < stat.getNbAttributes(); i++) {
/*     */       
/*  93 */       double E_leaf = stat.getRootScaledVariance(i, this.m_TargetWeights) * pruningFactor(stat.getTotalWeight(), 1);
/*  94 */       if (E_leaf >= this.m_GlobalRMSE[i] * this.m_F) return false; 
/*     */     } 
/*  96 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean allBetterThanTree(ClusNode node, RegressionStat stat, int modelsize) {
/* 101 */     for (int i = 0; i < stat.getNbAttributes(); i++) {
/*     */       
/* 103 */       double E_leaf = stat.getRootScaledVariance(i, this.m_TargetWeights) * pruningFactor(stat.getTotalWeight(), 1);
/*     */       
/* 105 */       double E_tree = estimateRootScaledVariance(node, i, this.m_TargetWeights) * pruningFactor(stat.getTotalWeight(), modelsize);
/* 106 */       if (E_leaf > E_tree) {
/* 107 */         return false;
/*     */       }
/*     */     } 
/* 110 */     return true;
/*     */   }
/*     */   
/*     */   public void pruneRecursive(ClusNode node) {
/* 114 */     if (node.atBottomLevel()) {
/*     */       return;
/*     */     }
/* 117 */     for (int i = 0; i < node.getNbChildren(); i++) {
/* 118 */       ClusNode child = (ClusNode)node.getChild(i);
/* 119 */       pruneRecursive(child);
/*     */     } 
/* 121 */     RegressionStat leaf_stat = (RegressionStat)node.getClusteringStat();
/* 122 */     if (allAccurate(leaf_stat)) {
/* 123 */       node.makeLeaf();
/*     */     }
/* 125 */     int modelsize = node.getNbNodes();
/* 126 */     if (allBetterThanTree(node, leaf_stat, modelsize)) {
/* 127 */       node.makeLeaf();
/*     */     }
/*     */   }
/*     */   
/*     */   public void setTrainingData(RowData data) {
/* 132 */     this.m_TrainingData = data;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\pruning\M5PrunerMulti.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */