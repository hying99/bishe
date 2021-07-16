/*     */ package clus.ext.ensembles;
/*     */ 
/*     */ import clus.data.rows.DataTuple;
/*     */ import clus.data.rows.TupleIterator;
/*     */ import clus.main.ClusStatManager;
/*     */ import clus.model.ClusModel;
/*     */ import clus.statistic.ClusStatistic;
/*     */ import clus.util.ClusException;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ 
/*     */ 
/*     */ public class ClusEnsembleInduceOptimization
/*     */ {
/*     */   static int[] m_HashCodeTuple;
/*     */   
/*     */   public ClusEnsembleInduceOptimization(TupleIterator train, TupleIterator test, int nb_tuples) throws IOException, ClusException {
/*  18 */     m_HashCodeTuple = new int[nb_tuples];
/*  19 */     int count = 0;
/*  20 */     if (train != null) {
/*  21 */       train.init();
/*  22 */       DataTuple train_tuple = train.readTuple();
/*  23 */       while (train_tuple != null) {
/*  24 */         m_HashCodeTuple[count] = train_tuple.hashCode();
/*  25 */         count++;
/*  26 */         train_tuple = train.readTuple();
/*     */       } 
/*     */     } 
/*  29 */     if (test != null) {
/*  30 */       test.init();
/*  31 */       DataTuple test_tuple = test.readTuple();
/*  32 */       while (test_tuple != null) {
/*  33 */         m_HashCodeTuple[count] = test_tuple.hashCode();
/*  34 */         count++;
/*  35 */         test_tuple = test.readTuple();
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public ClusEnsembleInduceOptimization(TupleIterator train, TupleIterator test) throws IOException, ClusException {
/*  42 */     ArrayList<Integer> tuple_hash = new ArrayList<>();
/*  43 */     if (train != null) {
/*  44 */       train.init();
/*  45 */       DataTuple train_tuple = train.readTuple();
/*  46 */       while (train_tuple != null) {
/*  47 */         tuple_hash.add(Integer.valueOf(train_tuple.hashCode()));
/*  48 */         train_tuple = train.readTuple();
/*     */       } 
/*  50 */       train.init();
/*     */     } 
/*  52 */     if (test != null) {
/*  53 */       test.init();
/*  54 */       DataTuple test_tuple = test.readTuple();
/*  55 */       while (test_tuple != null) {
/*  56 */         tuple_hash.add(Integer.valueOf(test_tuple.hashCode()));
/*  57 */         test_tuple = test.readTuple();
/*     */       } 
/*  59 */       test.init();
/*     */     } 
/*  61 */     int nb_tuples = tuple_hash.size();
/*  62 */     m_HashCodeTuple = new int[nb_tuples];
/*  63 */     for (int k = 0; k < nb_tuples; k++)
/*  64 */       m_HashCodeTuple[k] = ((Integer)tuple_hash.get(k)).intValue(); 
/*     */   }
/*     */   
/*     */   public static int locateTuple(DataTuple tuple) {
/*  68 */     int position = -1;
/*  69 */     boolean found = false;
/*  70 */     int i = 0;
/*     */     
/*  72 */     while (!found && i < m_HashCodeTuple.length) {
/*  73 */       if (m_HashCodeTuple[i] == tuple.hashCode()) {
/*  74 */         position = i;
/*  75 */         found = true;
/*     */       } 
/*  77 */       i++;
/*     */     } 
/*  79 */     return position;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void initPredictions(ClusStatistic stat) {}
/*     */ 
/*     */ 
/*     */   
/*     */   public void initModelPredictionForTuples(ClusModel model, TupleIterator train, TupleIterator test) throws IOException, ClusException {}
/*     */ 
/*     */   
/*     */   public void addModelPredictionForTuples(ClusModel model, TupleIterator train, TupleIterator test, int nb_models) throws IOException, ClusException {}
/*     */ 
/*     */   
/*     */   public static double[] incrementPredictions(double[] avg_predictions, double[] predictions, double nb_models) {
/*  95 */     int plength = avg_predictions.length;
/*  96 */     double[] result = new double[plength];
/*  97 */     for (int i = 0; i < plength; i++)
/*  98 */       result[i] = avg_predictions[i] + (predictions[i] - avg_predictions[i]) / nb_models; 
/*  99 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   public static double[][] incrementPredictions(double[][] sum_predictions, double[][] predictions, int nb_models) {
/* 104 */     double[][] result = new double[sum_predictions.length][];
/* 105 */     for (int i = 0; i < sum_predictions.length; i++) {
/* 106 */       result[i] = new double[(sum_predictions[i]).length];
/* 107 */       for (int j = 0; j < (sum_predictions[i]).length; j++) {
/* 108 */         result[i][j] = sum_predictions[i][j] + (predictions[i][j] - sum_predictions[i][j]) / nb_models;
/*     */       }
/*     */     } 
/* 111 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   public static double[][] incrementPredictions(double[][] sum_predictions, double[][] predictions) {
/* 116 */     double[][] result = new double[sum_predictions.length][];
/* 117 */     for (int i = 0; i < sum_predictions.length; i++) {
/* 118 */       result[i] = new double[(sum_predictions[i]).length];
/* 119 */       for (int j = 0; j < (sum_predictions[i]).length; j++) {
/* 120 */         result[i][j] = sum_predictions[i][j] + predictions[i][j];
/*     */       }
/*     */     } 
/* 123 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   public static double[][] transformToMajority(double[][] m_Counts) {
/* 128 */     int[] maxPerTarget = new int[m_Counts.length];
/* 129 */     for (int i = 0; i < m_Counts.length; i++) {
/* 130 */       maxPerTarget[i] = -1;
/* 131 */       double m_max = Double.NEGATIVE_INFINITY;
/* 132 */       for (int j = 0; j < (m_Counts[i]).length; j++) {
/* 133 */         if (m_Counts[i][j] > m_max) {
/* 134 */           maxPerTarget[i] = j;
/* 135 */           m_max = m_Counts[i][j];
/*     */         } 
/*     */       } 
/*     */     } 
/* 139 */     double[][] result = new double[m_Counts.length][];
/* 140 */     for (int m = 0; m < m_Counts.length; m++) {
/* 141 */       result[m] = new double[(m_Counts[m]).length];
/* 142 */       result[m][maxPerTarget[m]] = result[m][maxPerTarget[m]] + 1.0D;
/*     */     } 
/* 144 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   public static double[][] transformToProbabilityDistribution(double[][] m_Counts) {
/* 149 */     double[] sumPerTarget = new double[m_Counts.length];
/* 150 */     for (int i = 0; i < m_Counts.length; i++) {
/* 151 */       for (int j = 0; j < (m_Counts[i]).length; j++)
/* 152 */         sumPerTarget[i] = sumPerTarget[i] + m_Counts[i][j]; 
/* 153 */     }  double[][] result = new double[m_Counts.length][];
/*     */     
/* 155 */     for (int m = 0; m < m_Counts.length; m++) {
/* 156 */       result[m] = new double[(m_Counts[m]).length];
/* 157 */       for (int n = 0; n < (m_Counts[m]).length; n++) {
/* 158 */         result[m][n] = m_Counts[m][n] / sumPerTarget[m];
/*     */       }
/*     */     } 
/* 161 */     return result;
/*     */   }
/*     */   
/*     */   public static int getPredictionLength(int tuple) {
/* 165 */     if (ClusStatManager.getMode() == 2 || ClusStatManager.getMode() == 1)
/* 166 */       return ClusEnsembleInduceOptRegHMLC.getPredictionLength(tuple); 
/* 167 */     if (ClusStatManager.getMode() == 0)
/* 168 */       return ClusEnsembleInduceOptClassification.getPredictionLength(tuple); 
/* 169 */     return -1;
/*     */   }
/*     */ 
/*     */   
/*     */   public static double getPredictionValue(int tuple, int attribute) {
/* 174 */     if (ClusStatManager.getMode() == 2 || ClusStatManager.getMode() == 1)
/* 175 */       return ClusEnsembleInduceOptRegHMLC.getPredictionValue(tuple, attribute); 
/* 176 */     return -1.0D;
/*     */   }
/*     */   
/*     */   public static double[] getPredictionValueClassification(int tuple, int attribute) {
/* 180 */     if (ClusStatManager.getMode() == 0)
/* 181 */       return ClusEnsembleInduceOptClassification.getPredictionValueClassification(tuple, attribute); 
/* 182 */     return null;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\ext\ensembles\ClusEnsembleInduceOptimization.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */