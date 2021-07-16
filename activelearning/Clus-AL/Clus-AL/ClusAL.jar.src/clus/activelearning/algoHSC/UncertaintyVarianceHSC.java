/*    */ package clus.activelearning.algoHSC;
/*    */ 
/*    */ import clus.Clus;
/*    */ import clus.activelearning.indexing.LabelIndex;
/*    */ import clus.activelearning.indexing.TupleIndex;
/*    */ import clus.activelearning.matrix.MatrixUtils;
/*    */ import clus.data.rows.RowData;
/*    */ import clus.model.ClusModel;
/*    */ import clus.util.ClusException;
/*    */ import java.io.IOException;
/*    */ import java.util.LinkedList;
/*    */ import java.util.logging.Level;
/*    */ import java.util.logging.Logger;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class UncertaintyVarianceHSC
/*    */   extends UncertaintySamplingHSC
/*    */ {
/*    */   double alpha;
/*    */   double beta;
/*    */   
/*    */   public UncertaintyVarianceHSC(Clus clus, String name, double alpha, double beta) throws ClusException, Exception {
/* 30 */     super(clus, name);
/* 31 */     this.alpha = alpha;
/* 32 */     this.beta = beta;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public RowData sampleLabelBasedHSC(ClusModel[] models) throws ClusException {
/* 38 */     double[][] probs = (double[][])null;
/* 39 */     double[][] variance = (double[][])null;
/*    */     try {
/* 41 */       probs = getPredictionProbabilities(models, getUnlabeledData());
/* 42 */       variance = getVariance(models, getUnlabeledData());
/*    */     }
/* 44 */     catch (IOException|ClassNotFoundException ex) {
/* 45 */       Logger.getLogger(UncertaintySamplingHSC.class.getName()).log(Level.SEVERE, (String)null, ex);
/*    */     } 
/* 47 */     double[][] uncertainty = getLabelUncertainty(probs);
/* 48 */     double[][] uncVariance = MatrixUtils.subtractMatrixWeighted(variance, this.alpha, uncertainty, this.beta);
/*    */ 
/*    */     
/* 51 */     LinkedList<LabelIndex> labelIndexer = buildLabelMaxIndexer(uncVariance);
/* 52 */     return getPartialLabelledDataset(labelIndexer);
/*    */   }
/*    */ 
/*    */   
/*    */   public RowData sampleInstanceBasedHSC(ClusModel[] models) throws ClusException {
/* 57 */     double[][] probs = (double[][])null;
/* 58 */     double[][] variance = (double[][])null;
/*    */     try {
/* 60 */       probs = getPredictionProbabilities(models, getUnlabeledData());
/* 61 */       variance = getVariance(models, getUnlabeledData());
/*    */     }
/* 63 */     catch (IOException|ClassNotFoundException ex) {
/* 64 */       Logger.getLogger(UncertaintyVarianceHSC.class.getName()).log(Level.SEVERE, (String)null, ex);
/*    */     } 
/* 66 */     double[] entropyUncertainty = getTuplesEntropyUncertainty(probs);
/* 67 */     double[] variancePerRow = MatrixUtils.sumRows(variance);
/* 68 */     double[] uncertaintyVariance = MatrixUtils.addWeightedVector(entropyUncertainty, this.alpha, variancePerRow, this.beta);
/* 69 */     LinkedList<TupleIndex> ti = buildTupleMaxIndexer(uncertaintyVariance);
/* 70 */     return getLabeledDataset(ti);
/*    */   }
/*    */ 
/*    */   
/*    */   public RowData sampleSemiPartialLabelling(ClusModel[] models) throws ClusException {
/* 75 */     double[][] probs = (double[][])null;
/* 76 */     double[][] variance = (double[][])null;
/* 77 */     LinkedList<LabelIndex> labelIndexer = null;
/*    */     try {
/* 79 */       probs = getPredictionProbabilities(models, getUnlabeledData());
/* 80 */       variance = getVariance(models, getUnlabeledData());
/* 81 */       double[] entropyUncertainty = getTuplesEntropyUncertainty(probs);
/* 82 */       double[] variancePerRow = MatrixUtils.sumRows(variance);
/* 83 */       double[] uncertaintyVariance = MatrixUtils.addVector(entropyUncertainty, variancePerRow);
/* 84 */       RowData rowData = getTuplesByHighestMeasure(uncertaintyVariance);
/* 85 */       variance = getVariance(models, rowData);
/* 86 */       probs = getPredictionProbabilities(models, rowData);
/* 87 */       double[][] uncertainty = getLabelUncertainty(probs);
/* 88 */       double[][] uncVarSemi = MatrixUtils.subtractMatrix(variance, uncertainty);
/* 89 */       labelIndexer = buildLabelMaxIndexerSemi(rowData, uncVarSemi);
/*    */     }
/* 91 */     catch (IOException|ClassNotFoundException ex) {
/* 92 */       Logger.getLogger(UncertaintySamplingHSC.class.getName()).log(Level.SEVERE, (String)null, ex);
/*    */     } 
/* 94 */     return getPartialLabelledDataset(labelIndexer);
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\activelearning\algoHSC\UncertaintyVarianceHSC.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */