/*     */ package clus.activelearning.algoHSC;
/*     */ 
/*     */ import clus.Clus;
/*     */ import clus.activelearning.algo.ClusActiveLearningAlgorithmHSC;
/*     */ import clus.activelearning.data.ClassCounterCorrelation;
/*     */ import clus.activelearning.indexing.LabelIndex;
/*     */ import clus.activelearning.indexing.TupleIndex;
/*     */ import clus.data.rows.RowData;
/*     */ import clus.ext.hierarchical.ClassHierarchy;
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
/*     */ public class VSSMALHSC
/*     */   extends ClusActiveLearningAlgorithmHSC
/*     */ {
/*     */   public VSSMALHSC(Clus clus, String name) throws ClusException, Exception {
/*  29 */     super(clus, name);
/*     */   }
/*     */ 
/*     */   
/*     */   public RowData sampleLabelBasedHSC(ClusModel[] models) throws ClusException {
/*  34 */     double[][] probs = (double[][])null;
/*  35 */     double[][] variance = (double[][])null;
/*     */     try {
/*  37 */       probs = getPredictionProbabilities(models, getUnlabeledData());
/*     */       
/*  39 */       variance = getVariance(models, getUnlabeledData());
/*     */     }
/*  41 */     catch (IOException|ClassNotFoundException ex) {
/*  42 */       Logger.getLogger(UncertaintySamplingHSC.class.getName()).log(Level.SEVERE, (String)null, ex);
/*     */     } 
/*  44 */     double[][] labelCorrelation = getLabelCorrelation(getLabeledData());
/*  45 */     double[][] finalCorrelation = getFinalCorrelation(labelCorrelation);
/*  46 */     double[][] uncertainty = migue(probs, finalCorrelation, variance);
/*  47 */     LinkedList<LabelIndex> labelIndexer = buildLabelMinIndexer(uncertainty);
/*  48 */     return getPartialLabelledDataset(labelIndexer);
/*     */   }
/*     */   
/*     */   private double[][] getFinalCorrelation(double[][] labelsCorrelation) {
/*  52 */     double[] correlation = new double[labelsCorrelation.length];
/*  53 */     RowData unlabeledData = getUnlabeledData();
/*  54 */     int nbRows = unlabeledData.getNbRows();
/*  55 */     double[][] finalCorrelation = new double[nbRows][labelsCorrelation.length];
/*  56 */     for (int i = 0; i < nbRows; i++) {
/*     */       
/*  58 */       for (int j = 0; j < labelsCorrelation.length; j++) {
/*  59 */         double unanswered = unlabeledData.getTuple(i).getOracleAnswer().getUnansweredAmount(getHierarchy().getTotal());
/*  60 */         double sum = 0.0D;
/*     */         
/*  62 */         if (unanswered == 1.0D) {
/*  63 */           finalCorrelation[i][j] = 0.0D;
/*     */         } else {
/*  65 */           for (int k = 0; k < (labelsCorrelation[j]).length; k++) {
/*     */             
/*  67 */             if (!unlabeledData.getTuple(i).getOracleAnswer().queriedBefore(getLabels()[k])) {
/*  68 */               sum += Math.abs(labelsCorrelation[j][k]);
/*     */             }
/*     */           } 
/*     */ 
/*     */           
/*  73 */           finalCorrelation[i][j] = sum / unanswered;
/*     */         } 
/*     */       } 
/*     */     } 
/*  77 */     return finalCorrelation;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double[][] migue(double[][] predictionProbabilities, double[][] labelsCorrelation, double[][] variance) {
/*  84 */     double[][] uncertainty = new double[predictionProbabilities.length][];
/*  85 */     int nbRows = predictionProbabilities.length;
/*  86 */     for (int i = 0; i < nbRows; i++) {
/*  87 */       double[] probs = predictionProbabilities[i];
/*  88 */       for (int j = 0; j < probs.length; j++) {
/*  89 */         probs[j] = Math.abs(0.5D - probs[j]) - labelsCorrelation[i][j] - variance[i][j];
/*     */       }
/*  91 */       uncertainty[i] = probs;
/*     */     } 
/*  93 */     return uncertainty;
/*     */   }
/*     */ 
/*     */   
/*     */   public RowData sampleInstanceBasedHSC(ClusModel[] models) throws ClusException {
/*  98 */     double[][] probs = (double[][])null;
/*     */     try {
/* 100 */       probs = getPredictionProbabilities(models, getUnlabeledData());
/* 101 */     } catch (IOException|ClassNotFoundException ex) {
/* 102 */       Logger.getLogger(UncertaintySamplingHSC.class.getName()).log(Level.SEVERE, (String)null, ex);
/*     */     } 
/* 104 */     double[] uncertainty = getTuplesEntropyUncertainty(probs);
/* 105 */     LinkedList<TupleIndex> indexer = buildTupleMaxIndexer(uncertainty);
/* 106 */     return getLabeledDataset(indexer);
/*     */   }
/*     */   
/*     */   public double[] getTuplesEntropyUncertainty(double[][] predictionProbabilities) {
/* 110 */     int nbRows = predictionProbabilities.length;
/* 111 */     double[] uncertainty = new double[predictionProbabilities.length];
/* 112 */     for (int i = 0; i < nbRows; i++) {
/* 113 */       double[] probs = predictionProbabilities[i]; double[] arrayOfDouble1; int j; byte b;
/* 114 */       for (arrayOfDouble1 = probs, j = arrayOfDouble1.length, b = 0; b < j; ) { Double prob = Double.valueOf(arrayOfDouble1[b]);
/* 115 */         double logProbability = Math.log(prob.doubleValue()) / Math.log(2.0D);
/* 116 */         if (Double.isInfinite(logProbability)) {
/* 117 */           uncertainty[i] = uncertainty[i] + 0.0D;
/*     */         } else {
/* 119 */           uncertainty[i] = uncertainty[i] + prob.doubleValue() * logProbability;
/*     */         }  b++; }
/*     */       
/* 122 */       uncertainty[i] = -uncertainty[i];
/*     */     } 
/* 124 */     return uncertainty;
/*     */   }
/*     */ 
/*     */   
/*     */   public RowData sampleSemiPartialLabelling(ClusModel[] models) throws ClusException {
/* 129 */     double[][] probs = (double[][])null;
/* 130 */     LinkedList<LabelIndex> labelIndexer = null;
/*     */     try {
/* 132 */       probs = getPredictionProbabilities(models, getUnlabeledData());
/* 133 */       double[] entropyUncertainty = getTuplesEntropyUncertainty(probs);
/* 134 */       RowData rowData = getTuplesByHighestMeasure(entropyUncertainty);
/* 135 */       probs = getPredictionProbabilities(models, rowData);
/* 136 */       double[][] uncertainty = getLabelUncertainty(probs);
/* 137 */       labelIndexer = buildLabelMinIndexerSemi(rowData, uncertainty);
/*     */     }
/* 139 */     catch (IOException|ClassNotFoundException ex) {
/* 140 */       Logger.getLogger(UncertaintySamplingHSC.class.getName()).log(Level.SEVERE, (String)null, ex);
/*     */     } 
/* 142 */     return getPartialLabelledDataset(labelIndexer);
/*     */   }
/*     */   
/*     */   private double[][] getLabelCorrelation(RowData labeledData) {
/* 146 */     ClassHierarchy h = getHierarchy();
/*     */     
/* 148 */     int hierarchySize = h.getTotal();
/* 149 */     double[][] correlation = new double[h.getTotal()][h.getTotal()];
/*     */ 
/*     */ 
/*     */     
/* 153 */     for (int i = 1; i < hierarchySize; i++) {
/* 154 */       for (int j = 0; j <= i; j++) {
/* 155 */         if (i == j) {
/* 156 */           correlation[i][j] = 0.0D;
/* 157 */           correlation[j][i] = 0.0D;
/*     */         } else {
/* 159 */           double correlationValue = measureCorrelationValue(labeledData, h, i, j);
/* 160 */           if (!Double.isNaN(correlationValue)) {
/* 161 */             correlation[i][j] = correlationValue;
/* 162 */             correlation[j][i] = correlationValue;
/*     */           } else {
/* 164 */             correlation[i][j] = 0.0D;
/* 165 */             correlation[j][i] = 0.0D;
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } 
/* 170 */     return correlation;
/*     */   }
/*     */ 
/*     */   
/*     */   private double measureCorrelationValue(RowData rowData, ClassHierarchy h, int i, int j) {
/* 175 */     ClassCounterCorrelation cc = new ClassCounterCorrelation(i, j);
/* 176 */     cc.count(rowData, h, i, i);
/* 177 */     double A = cc.getCounterA();
/* 178 */     double B = cc.getCounterB();
/* 179 */     double C = cc.getCounterC();
/* 180 */     double D = cc.getCounterD();
/* 181 */     double correlation = (A * D - B * C) / Math.sqrt((A + B) * (A + C) * (C + D) * (B + D));
/* 182 */     return correlation;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\activelearning\algoHSC\VSSMALHSC.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */