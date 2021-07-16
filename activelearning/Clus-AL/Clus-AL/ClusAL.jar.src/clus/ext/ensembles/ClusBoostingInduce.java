/*     */ package clus.ext.ensembles;
/*     */ 
/*     */ import clus.algo.ClusInductionAlgorithm;
/*     */ import clus.algo.tdidt.ClusDecisionTree;
/*     */ import clus.algo.tdidt.ClusNode;
/*     */ import clus.algo.tdidt.DepthFirstInduce;
/*     */ import clus.algo.tdidt.DepthFirstInduceSparse;
/*     */ import clus.data.attweights.ClusAttributeWeights;
/*     */ import clus.data.rows.DataTuple;
/*     */ import clus.data.rows.RowData;
/*     */ import clus.data.type.ClusSchema;
/*     */ import clus.main.ClusRun;
/*     */ import clus.main.ClusStatManager;
/*     */ import clus.main.Settings;
/*     */ import clus.model.ClusModel;
/*     */ import clus.model.ClusModelInfo;
/*     */ import clus.statistic.ClusStatistic;
/*     */ import clus.util.ClusException;
/*     */ import java.io.IOException;
/*     */ import java.util.Random;
/*     */ import jeans.util.array.MDoubleArray;
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
/*     */ public class ClusBoostingInduce
/*     */   extends ClusInductionAlgorithm
/*     */ {
/*  48 */   Random m_Random = new Random(0L);
/*     */   
/*     */   public ClusBoostingInduce(ClusSchema schema, Settings sett) throws ClusException, IOException {
/*  51 */     super(schema, sett);
/*     */   }
/*     */   
/*     */   public double[] computeNormalizedLoss(RowData trainData, ClusNode tree) {
/*  55 */     ClusAttributeWeights weights = getStatManager().getClusteringWeights();
/*  56 */     double[] L = new double[trainData.getNbRows()];
/*  57 */     for (int i = 0; i < trainData.getNbRows(); i++) {
/*  58 */       DataTuple tuple = trainData.getTuple(i);
/*  59 */       ClusStatistic prediction = tree.predictWeighted(tuple);
/*  60 */       L[i] = prediction.getSquaredDistance(tuple, weights);
/*     */     } 
/*  62 */     double D = MDoubleArray.max(L);
/*  63 */     MDoubleArray.dotscalar(L, 1.0D / D);
/*  64 */     return L;
/*     */   }
/*     */   
/*     */   public double computeAverageLoss(RowData trainData, double[] L) {
/*  68 */     double avg = 0.0D;
/*  69 */     double tot_w = trainData.getSumWeights();
/*  70 */     for (int i = 0; i < trainData.getNbRows(); i++) {
/*  71 */       DataTuple tuple = trainData.getTuple(i);
/*  72 */       avg += L[i] * tuple.getWeight() / tot_w;
/*     */     } 
/*  74 */     return avg;
/*     */   }
/*     */   
/*     */   public void updateWeights(RowData trainData, double[] L, double beta) {
/*  78 */     for (int i = 0; i < trainData.getNbRows(); i++) {
/*  79 */       DataTuple tuple = trainData.getTuple(i);
/*  80 */       tuple.setWeight(tuple.getWeight() * Math.pow(beta, 1.0D - L[i]));
/*     */     } 
/*     */   }
/*     */   public ClusBoostingForest induceSingleUnprunedBoosting(ClusRun cr) throws ClusException, IOException {
/*     */     DepthFirstInduce tdidt;
/*  85 */     ClusBoostingForest result = new ClusBoostingForest(getStatManager());
/*  86 */     RowData trainData = ((RowData)cr.getTrainingSet()).shallowCloneData();
/*     */     
/*  88 */     if (getSchema().isSparse()) {
/*  89 */       DepthFirstInduceSparse depthFirstInduceSparse = new DepthFirstInduceSparse(this);
/*     */     } else {
/*     */       
/*  92 */       tdidt = new DepthFirstInduce(this);
/*     */     } 
/*  94 */     int[] outputEnsembleAt = getSettings().getNbBaggingSets().getIntVectorSorted();
/*  95 */     int nbTrees = outputEnsembleAt[outputEnsembleAt.length - 1];
/*  96 */     int verbose = Settings.VERBOSE;
/*  97 */     for (int i = 0; i < nbTrees; i++) {
/*  98 */       if (verbose != 0) {
/*  99 */         System.out.println();
/* 100 */         System.out.println("Tree: " + i + " (of max: " + nbTrees + ")");
/*     */       } 
/* 102 */       RowData train = trainData.sampleWeighted(this.m_Random);
/* 103 */       ClusNode tree = tdidt.induceSingleUnpruned(train);
/* 104 */       double[] L = computeNormalizedLoss(trainData, tree);
/* 105 */       double Lbar = computeAverageLoss(trainData, L);
/* 106 */       double beta = Lbar / (1.0D - Lbar);
/* 107 */       if (verbose != 0) {
/* 108 */         System.out.println("Average loss: " + Lbar + " beta: " + beta);
/*     */       }
/* 110 */       if (Lbar >= 0.5D)
/* 111 */         break;  updateWeights(trainData, L, beta);
/* 112 */       result.addModelToForest((ClusModel)tree, beta);
/*     */     } 
/* 114 */     return result;
/*     */   }
/*     */   
/*     */   public ClusModel induceSingleUnpruned(ClusRun cr) throws ClusException, IOException {
/* 118 */     return induceSingleUnprunedBoosting(cr);
/*     */   }
/*     */   
/*     */   public void induceAll(ClusRun cr) throws ClusException, IOException {
/* 122 */     ClusBoostingForest model = induceSingleUnprunedBoosting(cr);
/* 123 */     ClusModelInfo default_model = cr.addModelInfo(0);
/* 124 */     ClusModel def = ClusDecisionTree.induceDefault(cr);
/* 125 */     default_model.setModel(def);
/* 126 */     default_model.setName("Default");
/* 127 */     ClusModelInfo model_info = cr.addModelInfo(1);
/* 128 */     model_info.setName("Original");
/* 129 */     model_info.setModel(model);
/* 130 */     cr.getStatManager(); if (ClusStatManager.getMode() == 2) {
/* 131 */       double[] thresholds = cr.getStatManager().getSettings().getClassificationThresholds().getDoubleVector();
/* 132 */       if (thresholds != null)
/* 133 */         for (int i = 0; i < thresholds.length; i++) {
/* 134 */           ClusModelInfo pruned_info = cr.addModelInfo(2 + i);
/* 135 */           ClusBoostingForest new_forest = model.cloneBoostingForestWithThreshold(thresholds[i]);
/* 136 */           new_forest.setPrintModels(Settings.isPrintEnsembleModels());
/* 137 */           pruned_info.setModel(new_forest);
/* 138 */           pruned_info.setName("T(" + thresholds[i] + ")");
/*     */         }  
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\ext\ensembles\ClusBoostingInduce.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */