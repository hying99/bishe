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
/*     */ 
/*     */ public class SSMALHSC
/*     */   extends ClusActiveLearningAlgorithmHSC
/*     */ {
/*     */   double m_Alpha;
/*     */   
/*     */   public SSMALHSC(Clus clus, String name, double alpha) throws ClusException, Exception {
/*  32 */     super(clus, name);
/*  33 */     this.m_Alpha = alpha;
/*     */   }
/*     */ 
/*     */   
/*     */   public RowData sampleLabelBasedHSC(ClusModel[] models) throws ClusException {
/*  38 */     double[][] probs = (double[][])null;
/*     */     try {
/*  40 */       probs = getPredictionProbabilities(models, getUnlabeledData());
/*     */ 
/*     */     
/*     */     }
/*  44 */     catch (IOException|ClassNotFoundException ex) {
/*  45 */       Logger.getLogger(UncertaintySamplingHSC.class.getName()).log(Level.SEVERE, (String)null, ex);
/*     */     } 
/*  47 */     double[][] labelCorrelation = getLabelCorrelation(getLabeledData());
/*  48 */     double[][] finalCorrelation = getFinalCorrelation(labelCorrelation);
/*  49 */     double[][] uncertainty = measureFinalCorrelation(probs, finalCorrelation);
/*  50 */     LinkedList<LabelIndex> labelIndexer = buildLabelMinIndexer(uncertainty);
/*  51 */     return getPartialLabelledDataset(labelIndexer);
/*     */   }
/*     */   
/*     */   private double[][] getFinalCorrelation(double[][] labelsCorrelation) {
/*  55 */     RowData unlabeledData = getUnlabeledData();
/*  56 */     int nbRows = unlabeledData.getNbRows();
/*  57 */     double[][] finalCorrelation = new double[nbRows][labelsCorrelation.length];
/*  58 */     for (int i = 0; i < nbRows; i++) {
/*     */       
/*  60 */       for (int j = 0; j < labelsCorrelation.length; j++) {
/*  61 */         double unanswered = unlabeledData.getTuple(i).getOracleAnswer().getUnansweredAmount(getHierarchy().getTotal());
/*  62 */         double sum = 0.0D;
/*     */         
/*  64 */         if (unanswered == 1.0D) {
/*  65 */           finalCorrelation[i][j] = 0.0D;
/*     */         } else {
/*  67 */           for (int k = 0; k < (labelsCorrelation[j]).length; k++) {
/*     */             
/*  69 */             if (!unlabeledData.getTuple(i).getOracleAnswer().queriedBefore(getLabels()[k])) {
/*  70 */               sum += Math.abs(labelsCorrelation[j][k]);
/*     */             }
/*     */           } 
/*     */ 
/*     */           
/*  75 */           finalCorrelation[i][j] = sum / unanswered;
/*     */         } 
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  84 */     return finalCorrelation;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double[][] measureFinalCorrelation(double[][] predictionProbabilities, double[][] labelsCorrelation) {
/*  91 */     double[][] uncertainty = new double[predictionProbabilities.length][];
/*  92 */     int nbRows = predictionProbabilities.length;
/*  93 */     for (int i = 0; i < nbRows; i++) {
/*  94 */       double[] probs = predictionProbabilities[i];
/*  95 */       for (int j = 0; j < probs.length; j++) {
/*  96 */         probs[j] = Math.abs(0.5D - probs[j]) * this.m_Alpha - labelsCorrelation[i][j] * (1.0D - this.m_Alpha);
/*     */       }
/*  98 */       uncertainty[i] = probs;
/*     */     } 
/* 100 */     return uncertainty;
/*     */   }
/*     */ 
/*     */   
/*     */   public RowData sampleInstanceBasedHSC(ClusModel[] models) throws ClusException {
/* 105 */     double[][] probs = (double[][])null;
/*     */     try {
/* 107 */       probs = getPredictionProbabilities(models, getUnlabeledData());
/* 108 */     } catch (IOException|ClassNotFoundException ex) {
/* 109 */       Logger.getLogger(UncertaintySamplingHSC.class.getName()).log(Level.SEVERE, (String)null, ex);
/*     */     } 
/* 111 */     double[] uncertainty = getTuplesEntropyUncertainty(probs);
/* 112 */     LinkedList<TupleIndex> indexer = buildTupleMaxIndexer(uncertainty);
/* 113 */     return getLabeledDataset(indexer);
/*     */   }
/*     */   
/*     */   public double[] getTuplesEntropyUncertainty(double[][] predictionProbabilities) {
/* 117 */     int nbRows = predictionProbabilities.length;
/* 118 */     double[] uncertainty = new double[predictionProbabilities.length];
/* 119 */     for (int i = 0; i < nbRows; i++) {
/* 120 */       double[] probs = predictionProbabilities[i]; double[] arrayOfDouble1; int j; byte b;
/* 121 */       for (arrayOfDouble1 = probs, j = arrayOfDouble1.length, b = 0; b < j; ) { Double prob = Double.valueOf(arrayOfDouble1[b]);
/* 122 */         double logProbability = Math.log(prob.doubleValue()) / Math.log(2.0D);
/* 123 */         if (Double.isInfinite(logProbability)) {
/* 124 */           uncertainty[i] = uncertainty[i] + 0.0D;
/*     */         } else {
/* 126 */           uncertainty[i] = uncertainty[i] + prob.doubleValue() * logProbability;
/*     */         }  b++; }
/*     */       
/* 129 */       uncertainty[i] = -uncertainty[i];
/*     */     } 
/* 131 */     return uncertainty;
/*     */   }
/*     */ 
/*     */   
/*     */   public RowData sampleSemiPartialLabelling(ClusModel[] models) throws ClusException {
/* 136 */     double[][] probs = (double[][])null;
/* 137 */     LinkedList<LabelIndex> labelIndexer = null;
/*     */     try {
/* 139 */       probs = getPredictionProbabilities(models, getUnlabeledData());
/* 140 */       double[] entropyUncertainty = getTuplesEntropyUncertainty(probs);
/* 141 */       RowData rowData = getTuplesByHighestMeasure(entropyUncertainty);
/* 142 */       probs = getPredictionProbabilities(models, rowData);
/* 143 */       double[][] uncertainty = getLabelUncertainty(probs);
/* 144 */       labelIndexer = buildLabelMinIndexerSemi(rowData, uncertainty);
/*     */     }
/* 146 */     catch (IOException|ClassNotFoundException ex) {
/* 147 */       Logger.getLogger(UncertaintySamplingHSC.class.getName()).log(Level.SEVERE, (String)null, ex);
/*     */     } 
/* 149 */     return getPartialLabelledDataset(labelIndexer);
/*     */   }
/*     */   
/*     */   private double[][] getLabelCorrelation(RowData labeledData) {
/* 153 */     ClassHierarchy h = getHierarchy();
/*     */     
/* 155 */     int hierarchySize = h.getTotal();
/* 156 */     double[][] correlation = new double[h.getTotal()][h.getTotal()];
/*     */ 
/*     */ 
/*     */     
/* 160 */     for (int i = 1; i < hierarchySize; i++) {
/* 161 */       for (int j = 0; j <= i; j++) {
/* 162 */         if (i == j) {
/* 163 */           correlation[i][j] = 0.0D;
/* 164 */           correlation[j][i] = 0.0D;
/*     */         } else {
/* 166 */           double correlationValue = measureCorrelationValue(labeledData, h, i, j);
/* 167 */           if (!Double.isNaN(correlationValue)) {
/* 168 */             correlation[i][j] = correlationValue;
/* 169 */             correlation[j][i] = correlationValue;
/*     */           } else {
/* 171 */             correlation[i][j] = 0.0D;
/* 172 */             correlation[j][i] = 0.0D;
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } 
/* 177 */     return correlation;
/*     */   }
/*     */ 
/*     */   
/*     */   private double measureCorrelationValue(RowData rowData, ClassHierarchy h, int i, int j) {
/* 182 */     ClassCounterCorrelation cc = new ClassCounterCorrelation(i, j);
/* 183 */     cc.count(rowData, h, i, i);
/* 184 */     double A = cc.getCounterA();
/* 185 */     double B = cc.getCounterB();
/* 186 */     double C = cc.getCounterC();
/* 187 */     double D = cc.getCounterD();
/* 188 */     double correlation = (A * D - B * C) / Math.sqrt((A + B) * (A + C) * (C + D) * (B + D));
/* 189 */     return correlation;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\activelearning\algoHSC\SSMALHSC.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */