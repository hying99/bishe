/*     */ package clus.pruning;
/*     */ 
/*     */ import clus.algo.tdidt.ClusNode;
/*     */ import clus.data.rows.RowData;
/*     */ import clus.model.test.NodeTest;
/*     */ import clus.statistic.ClassificationStat;
/*     */ import clus.statistic.ClusStatistic;
/*     */ import clus.util.ClusException;
/*     */ import clus.util.ClusUtil;
/*     */ import org.apache.commons.math.MathException;
/*     */ import org.apache.commons.math.distribution.DistributionFactory;
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
/*     */ public class C45Pruner
/*     */   extends PruneTree
/*     */ {
/*     */   RowData m_TrainingData;
/*     */   boolean m_SubTreeRaising = true;
/*  40 */   double m_ConfidenceFactor = 0.25D;
/*  41 */   double m_ZScore = 0.0D;
/*     */   
/*     */   public void prune(ClusNode node) throws ClusException {
/*  44 */     this.m_ZScore = computeZScore();
/*  45 */     node.safePrune();
/*     */     
/*  47 */     node.pruneByTrainErr(null);
/*  48 */     pruneC45Recursive(node, this.m_TrainingData);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int getNbResults() {
/*  54 */     return 1;
/*     */   }
/*     */   
/*     */   public void pruneC45Recursive(ClusNode node, RowData data) throws ClusException {
/*  58 */     if (!node.atBottomLevel()) {
/*     */       
/*  60 */       NodeTest tst = node.getTest();
/*  61 */       for (int i = 0; i < node.getNbChildren(); i++) {
/*  62 */         ClusNode child = (ClusNode)node.getChild(i);
/*  63 */         RowData subset = data.applyWeighted(tst, i);
/*  64 */         pruneC45Recursive(child, subset);
/*     */       } 
/*     */       
/*  67 */       double errorsLargestBranch = 0.0D;
/*  68 */       int indexOfLargestBranch = node.getLargestBranchIndex();
/*  69 */       if (this.m_SubTreeRaising) {
/*  70 */         ClusNode largest = (ClusNode)node.getChild(indexOfLargestBranch);
/*  71 */         errorsLargestBranch = getEstimatedErrorsForBranch(largest, data);
/*     */       } else {
/*  73 */         errorsLargestBranch = Double.MAX_VALUE;
/*     */       } 
/*     */       
/*  76 */       double errorsLeaf = getEstimatedErrorsForDistribution((ClassificationStat)node.getTargetStat());
/*     */       
/*  78 */       double errorsTree = getEstimatedErrors(node);
/*     */       
/*  80 */       if (ClusUtil.smOrEq(errorsLeaf, errorsTree + 0.1D) && 
/*  81 */         ClusUtil.smOrEq(errorsLeaf, errorsLargestBranch + 0.1D)) {
/*  82 */         node.makeLeaf();
/*     */         
/*     */         return;
/*     */       } 
/*  86 */       if (ClusUtil.smOrEq(errorsLargestBranch, errorsTree + 0.1D)) {
/*  87 */         ClusNode largest = (ClusNode)node.getChild(indexOfLargestBranch);
/*  88 */         node.makeLeaf();
/*  89 */         node.setTest(largest.getTest());
/*  90 */         node.setNbChildren(largest.getNbChildren());
/*  91 */         for (int j = 0; j < largest.getNbChildren(); j++) {
/*  92 */           node.setChild(largest.getChild(j), j);
/*     */         }
/*  94 */         node.adaptToData(data);
/*  95 */         pruneC45Recursive(node, data);
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public double getEstimatedErrorsForDistribution(ClassificationStat stat) {
/* 101 */     if (ClusUtil.eq(stat.getTotalWeight(), 0.0D)) {
/* 102 */       return 0.0D;
/*     */     }
/* 104 */     double nb_incorrect = stat.getError();
/* 105 */     return nb_incorrect + addErrs(stat.getTotalWeight(), nb_incorrect, this.m_ConfidenceFactor);
/*     */   }
/*     */ 
/*     */   
/*     */   public double getEstimatedErrorsForBranch(ClusNode node, RowData data) {
/* 110 */     if (node.atBottomLevel()) {
/* 111 */       ClassificationStat stat = (ClassificationStat)node.getTargetStat().cloneStat();
/* 112 */       data.calcTotalStatBitVector((ClusStatistic)stat);
/* 113 */       return getEstimatedErrorsForDistribution(stat);
/*     */     } 
/* 115 */     double sum = 0.0D;
/* 116 */     NodeTest tst = node.getTest();
/* 117 */     for (int i = 0; i < node.getNbChildren(); i++) {
/* 118 */       ClusNode child = (ClusNode)node.getChild(i);
/* 119 */       RowData subset = data.applyWeighted(tst, i);
/* 120 */       sum += getEstimatedErrorsForBranch(child, subset);
/*     */     } 
/* 122 */     return sum;
/*     */   }
/*     */ 
/*     */   
/*     */   public double getEstimatedErrors(ClusNode node) {
/* 127 */     if (node.atBottomLevel()) {
/* 128 */       return getEstimatedErrorsForDistribution((ClassificationStat)node.getTargetStat());
/*     */     }
/* 130 */     double sum = 0.0D;
/* 131 */     for (int i = 0; i < node.getNbChildren(); i++) {
/* 132 */       ClusNode child = (ClusNode)node.getChild(i);
/* 133 */       sum += getEstimatedErrors(child);
/*     */     } 
/* 135 */     return sum;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double addErrs(double N, double e, double CF) {
/* 144 */     if (CF > 0.5D) {
/* 145 */       return 0.0D;
/*     */     }
/*     */ 
/*     */     
/* 149 */     if (e < 1.0D) {
/*     */ 
/*     */       
/* 152 */       double base = N * (1.0D - Math.pow(CF, 1.0D / N));
/* 153 */       if (e == 0.0D) {
/* 154 */         return base;
/*     */       }
/*     */       
/* 157 */       return base + e * (addErrs(N, 1.0D, CF) - base);
/*     */     } 
/*     */ 
/*     */     
/* 161 */     if (e + 0.5D >= N)
/*     */     {
/* 163 */       return Math.max(N - e, 0.0D);
/*     */     }
/*     */     
/* 166 */     double z = this.m_ZScore;
/* 167 */     double f = (e + 0.5D) / N;
/* 168 */     double r = (f + z * z / 2.0D * N + z * Math.sqrt(f / N - f * f / N + z * z / 4.0D * N * N)) / (1.0D + z * z / N);
/* 169 */     return r * N - e;
/*     */   }
/*     */   
/*     */   public void setTrainingData(RowData data) {
/* 173 */     this.m_TrainingData = data;
/*     */   }
/*     */   
/*     */   public double computeZScore() throws ClusException {
/*     */     try {
/* 178 */       DistributionFactory distributionFactory = DistributionFactory.newInstance();
/* 179 */       return distributionFactory.createNormalDistribution().inverseCumulativeProbability(1.0D - this.m_ConfidenceFactor);
/* 180 */     } catch (MathException e) {
/* 181 */       throw new ClusException(e.getMessage());
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\pruning\C45Pruner.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */