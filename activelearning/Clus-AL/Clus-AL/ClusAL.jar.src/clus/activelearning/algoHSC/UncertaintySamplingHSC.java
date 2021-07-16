/*     */ package clus.activelearning.algoHSC;
/*     */ 
/*     */ import clus.Clus;
/*     */ import clus.activelearning.algo.ClusActiveLearningAlgorithmHSC;
/*     */ import clus.activelearning.indexing.LabelIndex;
/*     */ import clus.activelearning.indexing.TupleIndex;
/*     */ import clus.data.rows.RowData;
/*     */ import clus.model.ClusModel;
/*     */ import clus.util.ClusException;
/*     */ import java.io.IOException;
/*     */ import java.util.LinkedList;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.Logger;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class UncertaintySamplingHSC
/*     */   extends ClusActiveLearningAlgorithmHSC
/*     */ {
/*     */   public UncertaintySamplingHSC(Clus clus, String name) throws ClusException, Exception {
/*  27 */     super(clus, name);
/*     */   }
/*     */ 
/*     */   
/*     */   public RowData sampleLabelBasedHSC(ClusModel[] models) throws ClusException {
/*  32 */     double[][] probs = (double[][])null;
/*     */     try {
/*  34 */       probs = getPredictionProbabilities(models, getUnlabeledData());
/*  35 */     } catch (IOException|ClassNotFoundException ex) {
/*  36 */       Logger.getLogger(UncertaintySamplingHSC.class.getName()).log(Level.SEVERE, (String)null, ex);
/*     */     } 
/*     */     
/*  39 */     double[][] uncertainty = getLabelUncertainty(probs);
/*     */     
/*  41 */     LinkedList<LabelIndex> labelIndexer = buildLabelMinIndexer(uncertainty);
/*  42 */     return getPartialLabelledDataset(labelIndexer);
/*     */   }
/*     */ 
/*     */   
/*     */   public RowData sampleInstanceBasedHSC(ClusModel[] models) throws ClusException {
/*  47 */     double[][] probs = (double[][])null;
/*     */     try {
/*  49 */       probs = getPredictionProbabilities(models, getUnlabeledData());
/*  50 */     } catch (IOException|ClassNotFoundException ex) {
/*  51 */       Logger.getLogger(UncertaintySamplingHSC.class.getName()).log(Level.SEVERE, (String)null, ex);
/*     */     } 
/*  53 */     double[] uncertainty = getTuplesEntropyUncertainty(probs);
/*     */ 
/*     */     
/*  56 */     LinkedList<TupleIndex> indexer = buildTupleMaxIndexer(uncertainty);
/*  57 */     return getLabeledDataset(indexer);
/*     */   }
/*     */   
/*     */   public double[] getMigue(double[][] predictionProbabilities) {
/*  61 */     int nbRows = predictionProbabilities.length;
/*  62 */     double[] uncertainty = new double[predictionProbabilities.length];
/*  63 */     for (int i = 0; i < nbRows; i++) {
/*  64 */       double[] probs = predictionProbabilities[i]; double[] arrayOfDouble1; int j; byte b;
/*  65 */       for (arrayOfDouble1 = probs, j = arrayOfDouble1.length, b = 0; b < j; ) { Double prob = Double.valueOf(arrayOfDouble1[b]);
/*  66 */         uncertainty[i] = Math.abs(0.5D - prob.doubleValue()); b++; }
/*     */       
/*  68 */       uncertainty[i] = -uncertainty[i];
/*     */     } 
/*  70 */     return uncertainty;
/*     */   }
/*     */   
/*     */   public double[] getTuplesEntropyUncertainty(double[][] predictionProbabilities) {
/*  74 */     int nbRows = predictionProbabilities.length;
/*  75 */     double[] uncertainty = new double[predictionProbabilities.length];
/*  76 */     for (int i = 0; i < nbRows; i++) {
/*  77 */       double[] probs = predictionProbabilities[i]; double[] arrayOfDouble1; int j; byte b;
/*  78 */       for (arrayOfDouble1 = probs, j = arrayOfDouble1.length, b = 0; b < j; ) { Double prob = Double.valueOf(arrayOfDouble1[b]);
/*  79 */         double logProbability = Math.log(prob.doubleValue()) / Math.log(2.0D);
/*  80 */         if (Double.isInfinite(logProbability)) {
/*  81 */           uncertainty[i] = uncertainty[i] + 0.0D;
/*     */         } else {
/*  83 */           uncertainty[i] = uncertainty[i] + prob.doubleValue() * logProbability;
/*     */         }  b++; }
/*     */       
/*  86 */       uncertainty[i] = -uncertainty[i];
/*     */     } 
/*  88 */     return uncertainty;
/*     */   }
/*     */ 
/*     */   
/*     */   public RowData sampleSemiPartialLabelling(ClusModel[] models) throws ClusException {
/*  93 */     double[][] probs = (double[][])null;
/*  94 */     LinkedList<LabelIndex> labelIndexer = null;
/*     */     try {
/*  96 */       probs = getPredictionProbabilities(models, getUnlabeledData());
/*  97 */       double[] entropyUncertainty = getTuplesEntropyUncertainty(probs);
/*  98 */       RowData rowData = getTuplesByHighestMeasure(entropyUncertainty);
/*  99 */       probs = getPredictionProbabilities(models, rowData);
/* 100 */       double[][] uncertainty = getLabelUncertainty(probs);
/* 101 */       labelIndexer = buildLabelMinIndexerSemi(rowData, uncertainty);
/*     */     }
/* 103 */     catch (IOException|ClassNotFoundException ex) {
/* 104 */       Logger.getLogger(UncertaintySamplingHSC.class.getName()).log(Level.SEVERE, (String)null, ex);
/*     */     } 
/* 106 */     return getPartialLabelledDataset(labelIndexer);
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\activelearning\algoHSC\UncertaintySamplingHSC.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */