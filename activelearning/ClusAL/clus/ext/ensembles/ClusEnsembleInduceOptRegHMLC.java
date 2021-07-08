/*    */ package clus.ext.ensembles;
/*    */ 
/*    */ import clus.data.rows.DataTuple;
/*    */ import clus.data.rows.TupleIterator;
/*    */ import clus.model.ClusModel;
/*    */ import clus.statistic.ClusStatistic;
/*    */ import clus.statistic.RegressionStatBase;
/*    */ import clus.util.ClusException;
/*    */ import java.io.IOException;
/*    */ 
/*    */ 
/*    */ public class ClusEnsembleInduceOptRegHMLC
/*    */   extends ClusEnsembleInduceOptimization
/*    */ {
/*    */   static double[][] m_AvgPredictions;
/*    */   
/*    */   public ClusEnsembleInduceOptRegHMLC(TupleIterator train, TupleIterator test, int nb_tuples) throws IOException, ClusException {
/* 18 */     super(train, test, nb_tuples);
/*    */   }
/*    */   
/*    */   public ClusEnsembleInduceOptRegHMLC(TupleIterator train, TupleIterator test) throws IOException, ClusException {
/* 22 */     super(train, test);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void initPredictions(ClusStatistic stat) {
/* 28 */     m_AvgPredictions = new double[m_HashCodeTuple.length][stat.getNbAttributes()];
/*    */   }
/*    */   
/*    */   public void initModelPredictionForTuples(ClusModel model, TupleIterator train, TupleIterator test) throws IOException, ClusException {
/* 32 */     if (train != null) {
/* 33 */       train.init();
/* 34 */       DataTuple train_tuple = train.readTuple();
/* 35 */       while (train_tuple != null) {
/* 36 */         int position = locateTuple(train_tuple);
/* 37 */         RegressionStatBase stat = (RegressionStatBase)model.predictWeighted(train_tuple);
/* 38 */         m_AvgPredictions[position] = stat.getNumericPred();
/* 39 */         train_tuple = train.readTuple();
/*    */       } 
/* 41 */       train.init();
/*    */     } 
/* 43 */     if (test != null) {
/* 44 */       test.init();
/* 45 */       DataTuple test_tuple = test.readTuple();
/* 46 */       while (test_tuple != null) {
/* 47 */         int position = locateTuple(test_tuple);
/* 48 */         RegressionStatBase stat = (RegressionStatBase)model.predictWeighted(test_tuple);
/* 49 */         m_AvgPredictions[position] = stat.getNumericPred();
/* 50 */         test_tuple = test.readTuple();
/*    */       } 
/* 52 */       test.init();
/*    */     } 
/*    */   }
/*    */   
/*    */   public void addModelPredictionForTuples(ClusModel model, TupleIterator train, TupleIterator test, int nb_models) throws IOException, ClusException {
/* 57 */     if (train != null) {
/* 58 */       train.init();
/* 59 */       DataTuple train_tuple = train.readTuple();
/* 60 */       while (train_tuple != null) {
/* 61 */         int position = locateTuple(train_tuple);
/* 62 */         RegressionStatBase stat = (RegressionStatBase)model.predictWeighted(train_tuple);
/* 63 */         m_AvgPredictions[position] = incrementPredictions(m_AvgPredictions[position], stat.getNumericPred(), nb_models);
/* 64 */         train_tuple = train.readTuple();
/*    */       } 
/* 66 */       train.init();
/*    */     } 
/* 68 */     if (test != null) {
/* 69 */       test.init();
/* 70 */       DataTuple test_tuple = test.readTuple();
/* 71 */       while (test_tuple != null) {
/* 72 */         int position = locateTuple(test_tuple);
/* 73 */         ClusStatistic stat = model.predictWeighted(test_tuple);
/* 74 */         m_AvgPredictions[position] = incrementPredictions(m_AvgPredictions[position], stat.getNumericPred(), nb_models);
/* 75 */         test_tuple = test.readTuple();
/*    */       } 
/* 77 */       test.init();
/*    */     } 
/*    */   }
/*    */   
/*    */   public static int getPredictionLength(int tuple) {
/* 82 */     return (m_AvgPredictions[tuple]).length;
/*    */   }
/*    */   
/*    */   public static double getPredictionValue(int tuple, int attribute) {
/* 86 */     return m_AvgPredictions[tuple][attribute];
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\ext\ensembles\ClusEnsembleInduceOptRegHMLC.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */