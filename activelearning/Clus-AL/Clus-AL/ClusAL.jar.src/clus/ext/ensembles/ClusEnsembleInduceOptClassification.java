/*     */ package clus.ext.ensembles;
/*     */ 
/*     */ import clus.data.rows.DataTuple;
/*     */ import clus.data.rows.TupleIterator;
/*     */ import clus.main.Settings;
/*     */ import clus.model.ClusModel;
/*     */ import clus.statistic.ClassificationStat;
/*     */ import clus.statistic.ClusStatistic;
/*     */ import clus.util.ClusException;
/*     */ import java.io.IOException;
/*     */ 
/*     */ public class ClusEnsembleInduceOptClassification
/*     */   extends ClusEnsembleInduceOptimization
/*     */ {
/*     */   static double[][][] m_AvgPredictions;
/*     */   
/*     */   public ClusEnsembleInduceOptClassification(TupleIterator train, TupleIterator test, int nb_tuples) throws IOException, ClusException {
/*  18 */     super(train, test, nb_tuples);
/*     */   }
/*     */   
/*     */   public ClusEnsembleInduceOptClassification(TupleIterator train, TupleIterator test) throws IOException, ClusException {
/*  22 */     super(train, test);
/*     */   }
/*     */   
/*     */   public void initPredictions(ClusStatistic stat) {
/*  26 */     ClassificationStat nstat = (ClassificationStat)stat;
/*  27 */     m_AvgPredictions = new double[m_HashCodeTuple.length][nstat.getNbAttributes()][];
/*  28 */     for (int i = 0; i < nstat.getNbAttributes(); i++) {
/*  29 */       m_AvgPredictions[m_HashCodeTuple.length - 1][i] = new double[nstat.getNbClasses(i)];
/*     */     }
/*     */   }
/*     */   
/*     */   public void initModelPredictionForTuples(ClusModel model, TupleIterator train, TupleIterator test) throws IOException, ClusException {
/*  34 */     if (train != null) {
/*  35 */       train.init();
/*  36 */       DataTuple train_tuple = train.readTuple();
/*  37 */       while (train_tuple != null) {
/*  38 */         int position = locateTuple(train_tuple);
/*  39 */         ClassificationStat stat = (ClassificationStat)model.predictWeighted(train_tuple);
/*  40 */         double[][] counts = (double[][])stat.getClassCounts().clone();
/*  41 */         switch (Settings.m_ClassificationVoteType.getValue()) { case 0:
/*  42 */             counts = transformToMajority(counts); break;
/*  43 */           case 1: counts = transformToProbabilityDistribution(counts); break;
/*  44 */           default: counts = transformToMajority(counts); break; }
/*     */         
/*  46 */         m_AvgPredictions[position] = counts;
/*     */ 
/*     */ 
/*     */         
/*  50 */         train_tuple = train.readTuple();
/*     */       } 
/*  52 */       train.init();
/*     */     } 
/*  54 */     if (test != null) {
/*  55 */       test.init();
/*  56 */       DataTuple test_tuple = test.readTuple();
/*  57 */       while (test_tuple != null) {
/*  58 */         int position = locateTuple(test_tuple);
/*  59 */         ClassificationStat stat = (ClassificationStat)model.predictWeighted(test_tuple);
/*  60 */         double[][] counts = (double[][])stat.getClassCounts().clone();
/*  61 */         switch (Settings.m_ClassificationVoteType.getValue()) { case 0:
/*  62 */             counts = transformToMajority(counts); break;
/*  63 */           case 1: counts = transformToProbabilityDistribution(counts); break;
/*  64 */           default: counts = transformToMajority(counts); break; }
/*     */         
/*  66 */         m_AvgPredictions[position] = counts;
/*     */ 
/*     */ 
/*     */         
/*  70 */         test_tuple = test.readTuple();
/*     */       } 
/*  72 */       test.init();
/*     */     } 
/*     */   }
/*     */   
/*     */   public void addModelPredictionForTuples(ClusModel model, TupleIterator train, TupleIterator test, int nb_models) throws IOException, ClusException {
/*  77 */     if (train != null) {
/*  78 */       train.init();
/*  79 */       DataTuple train_tuple = train.readTuple();
/*  80 */       while (train_tuple != null) {
/*  81 */         int position = locateTuple(train_tuple);
/*  82 */         ClassificationStat stat = (ClassificationStat)model.predictWeighted(train_tuple);
/*  83 */         double[][] counts = (double[][])stat.getClassCounts().clone();
/*  84 */         switch (Settings.m_ClassificationVoteType.getValue()) { case 0:
/*  85 */             counts = transformToMajority(counts); break;
/*  86 */           case 1: counts = transformToProbabilityDistribution(counts); break;
/*  87 */           default: counts = transformToMajority(counts); break; }
/*     */         
/*  89 */         m_AvgPredictions[position] = incrementPredictions(m_AvgPredictions[position], counts, nb_models);
/*     */ 
/*     */ 
/*     */ 
/*     */         
/*  94 */         train_tuple = train.readTuple();
/*     */       } 
/*  96 */       train.init();
/*     */     } 
/*  98 */     if (test != null) {
/*  99 */       test.init();
/* 100 */       DataTuple test_tuple = test.readTuple();
/* 101 */       while (test_tuple != null) {
/* 102 */         int position = locateTuple(test_tuple);
/* 103 */         ClassificationStat stat = (ClassificationStat)model.predictWeighted(test_tuple);
/* 104 */         double[][] counts = (double[][])stat.getClassCounts().clone();
/* 105 */         switch (Settings.m_ClassificationVoteType.getValue()) { case 0:
/* 106 */             counts = transformToMajority(counts); break;
/* 107 */           case 1: counts = transformToProbabilityDistribution(counts); break;
/* 108 */           default: counts = transformToMajority(counts); break; }
/*     */         
/* 110 */         m_AvgPredictions[position] = incrementPredictions(m_AvgPredictions[position], counts, nb_models);
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 115 */         test_tuple = test.readTuple();
/*     */       } 
/* 117 */       test.init();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public static int getPredictionLength(int tuple) {
/* 123 */     return (m_AvgPredictions[tuple]).length;
/*     */   }
/*     */   
/*     */   public static double[] getPredictionValueClassification(int tuple, int attribute) {
/* 127 */     return m_AvgPredictions[tuple][attribute];
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\ext\ensembles\ClusEnsembleInduceOptClassification.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */