/*     */ package clus.activelearning.algoHSC;
/*     */ 
/*     */ import clus.Clus;
/*     */ import clus.activelearning.data.ClassCounterCorrelation;
/*     */ import clus.activelearning.data.FrequencyCounter;
/*     */ import clus.activelearning.data.OracleAnswer;
/*     */ import clus.activelearning.indexing.LabelIndex;
/*     */ import clus.activelearning.matrix.MatrixUtils;
/*     */ import clus.data.rows.RowData;
/*     */ import clus.ext.hierarchical.ClassHierarchy;
/*     */ import clus.model.ClusModel;
/*     */ import clus.util.ClusException;
/*     */ import java.io.IOException;
/*     */ import java.util.HashMap;
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
/*     */ 
/*     */ 
/*     */ public class UncertaintyVarianceCostHSC
/*     */   extends UncertaintyVarianceHSC
/*     */ {
/*     */   public UncertaintyVarianceCostHSC(Clus clus, String name, double alpha, double beta) throws ClusException, Exception {
/*  34 */     super(clus, name, alpha, beta);
/*     */   }
/*     */ 
/*     */   
/*     */   public RowData sampleLabelBasedHSC(ClusModel[] models) throws ClusException {
/*  39 */     double[][] labelFrequency = getLabelFrequencyCost(getUnlabeledData());
/*  40 */     double[][] probs = (double[][])null;
/*  41 */     double[][] variance = (double[][])null;
/*     */     try {
/*  43 */       probs = getPredictionProbabilities(models, getUnlabeledData());
/*  44 */       variance = getVariance(models, getUnlabeledData());
/*     */     }
/*  46 */     catch (IOException|ClassNotFoundException ex) {
/*  47 */       Logger.getLogger(UncertaintySamplingHSC.class.getName()).log(Level.SEVERE, (String)null, ex);
/*     */     } 
/*  49 */     double[][] uncertainty = getLabelUncertainty(probs);
/*     */     
/*  51 */     double[][] uncVariance = MatrixUtils.subtractMatrix(variance, uncertainty);
/*  52 */     uncVariance = MatrixUtils.addMatrix(uncVariance, labelFrequency);
/*     */     
/*  54 */     LinkedList<LabelIndex> labelIndexer = buildLabelMaxIndexer(uncVariance);
/*  55 */     return getPartialLabelledDataset(labelIndexer);
/*     */   }
/*     */ 
/*     */   
/*     */   public RowData sampleInstanceBasedHSC(ClusModel[] models) throws ClusException {
/*  60 */     throw new UnsupportedOperationException("Not supported yet.");
/*     */   }
/*     */ 
/*     */   
/*     */   public RowData sampleSemiPartialLabelling(ClusModel[] models) throws ClusException {
/*  65 */     double[][] probs = (double[][])null;
/*  66 */     double[][] variance = (double[][])null;
/*  67 */     LinkedList<LabelIndex> labelIndexer = null;
/*     */     try {
/*  69 */       probs = getPredictionProbabilities(models, getUnlabeledData());
/*  70 */       variance = getVariance(models, getUnlabeledData());
/*  71 */       double[] entropyUncertainty = getTuplesEntropyUncertainty(probs);
/*  72 */       double[] variancePerRow = MatrixUtils.sumRows(variance);
/*  73 */       double[] uncertaintyVariance = MatrixUtils.addVector(entropyUncertainty, variancePerRow);
/*  74 */       double[] weights = getWeights();
/*  75 */       uncertaintyVariance = MatrixUtils.multiplyVector(uncertaintyVariance, weights);
/*  76 */       RowData rowData = getTuplesByHighestMeasure(uncertaintyVariance);
/*  77 */       variance = getVariance(models, rowData);
/*  78 */       probs = getPredictionProbabilities(models, rowData);
/*  79 */       double[][] uncertainty = getLabelUncertainty(probs);
/*  80 */       double[][] labelFrequencyCost = getLabelFrequencyCost(rowData);
/*  81 */       double[][] uncVarSemi = MatrixUtils.subtractMatrix(variance, uncertainty);
/*     */ 
/*     */ 
/*     */       
/*  85 */       uncVarSemi = MatrixUtils.addMatrix(uncVarSemi, labelFrequencyCost);
/*     */ 
/*     */       
/*  88 */       labelIndexer = buildLabelMaxIndexerSemi(rowData, uncVarSemi);
/*     */     }
/*  90 */     catch (IOException|ClassNotFoundException ex) {
/*  91 */       Logger.getLogger(UncertaintySamplingHSC.class.getName()).log(Level.SEVERE, (String)null, ex);
/*     */     } 
/*  93 */     return getPartialLabelledDataset(labelIndexer);
/*     */   }
/*     */   
/*     */   private double[][] getLabelCorrelation(RowData labeledData) {
/*  97 */     ClassHierarchy h = getHierarchy();
/*     */     
/*  99 */     int hierarchySize = h.getTotal();
/* 100 */     double[][] correlation = new double[h.getTotal()][h.getTotal()];
/*     */ 
/*     */ 
/*     */     
/* 104 */     for (int i = 1; i < hierarchySize; i++) {
/* 105 */       for (int j = 0; j <= i; j++) {
/* 106 */         if (i == j) {
/* 107 */           correlation[i][j] = 0.0D;
/* 108 */           correlation[j][i] = 0.0D;
/*     */         } else {
/* 110 */           double correlationValue = measureCorrelationValue(labeledData, h, i, j);
/* 111 */           if (!Double.isNaN(correlationValue)) {
/* 112 */             correlation[i][j] = correlationValue;
/* 113 */             correlation[j][i] = correlationValue;
/*     */           } else {
/* 115 */             correlation[i][j] = 0.0D;
/* 116 */             correlation[j][i] = 0.0D;
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } 
/* 121 */     return correlation;
/*     */   }
/*     */ 
/*     */   
/*     */   private double measureCorrelationValue(RowData rowData, ClassHierarchy h, int i, int j) {
/* 126 */     ClassCounterCorrelation cc = new ClassCounterCorrelation(i, j);
/* 127 */     cc.count(rowData, h, i, j);
/* 128 */     double A = cc.getCounterA();
/* 129 */     double B = cc.getCounterB();
/* 130 */     double C = cc.getCounterC();
/* 131 */     double D = cc.getCounterD();
/* 132 */     double correlation = (A * D - B * C) / Math.sqrt((A + B) * (A + C) * (C + D) * (B + D));
/* 133 */     return correlation;
/*     */   }
/*     */   
/*     */   private double[][] getFinalCorrelation(RowData rowData, double[][] labelCorrelation) {
/* 137 */     int nbRows = rowData.getNbRows();
/* 138 */     String[] labels = getLabels();
/* 139 */     int nbColumns = labels.length;
/*     */     
/* 141 */     int hierarchySize = getHierarchy().getTotal();
/*     */ 
/*     */ 
/*     */     
/* 145 */     double[][] finalCorrelation = new double[nbRows][nbColumns];
/* 146 */     for (int i = 0; i < nbRows; i++) {
/* 147 */       OracleAnswer oracleAnswer = rowData.getTuple(i).getOracleAnswer();
/* 148 */       double percentage = oracleAnswer.getUnansweredAmount(hierarchySize);
/* 149 */       for (int j = 0; j < nbColumns; j++) {
/* 150 */         String label = labels[j];
/* 151 */         double summedCorrelation = 0.0D;
/* 152 */         for (int k = 0; k < nbColumns; k++) {
/* 153 */           if (!oracleAnswer.queriedBefore(label)) {
/* 154 */             summedCorrelation += Math.abs(labelCorrelation[i][j]);
/*     */           }
/*     */         } 
/* 157 */         finalCorrelation[i][j] = summedCorrelation / percentage;
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 163 */     return finalCorrelation;
/*     */   }
/*     */   
/*     */   protected double[] getWeights() {
/* 167 */     RowData rowData = getUnlabeledData();
/* 168 */     int nbRows = rowData.getNbRows();
/* 169 */     double[] weights = new double[nbRows];
/* 170 */     int hierarchySize = getHierarchy().getTotal();
/*     */ 
/*     */     
/* 173 */     for (int i = 0; i < nbRows; i++)
/*     */     {
/* 175 */       weights[i] = rowData.getTuple(i).getOracleAnswer().getUnansweredAmount(hierarchySize);
/*     */     }
/* 177 */     return weights;
/*     */   }
/*     */   
/*     */   protected double[][] getLabelFrequencyCost(RowData rowData) {
/* 181 */     String[] labels = getLabels();
/* 182 */     double[] frequencyCost = new double[labels.length];
/* 183 */     FrequencyCounter fc = new FrequencyCounter(getLabeledData(), getHierarchy());
/* 184 */     HashMap<String, Integer> labelQueryCounter = getLabelQueryCounter();
/*     */     
/* 186 */     for (int i = 0; i < labels.length; i++) {
/* 187 */       frequencyCost[i] = 1.0D / (1.0D + Math.exp(((Integer)labelQueryCounter.get(getLabels()[i])).intValue()));
/*     */     }
/*     */ 
/*     */     
/* 191 */     double[][] labelFrequencyCost = new double[rowData.getNbRows()][labels.length];
/* 192 */     for (int j = 0; j < rowData.getNbRows(); j++) {
/* 193 */       System.arraycopy(frequencyCost, 0, labelFrequencyCost[j], 0, labels.length);
/*     */     }
/* 195 */     return labelFrequencyCost;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\activelearning\algoHSC\UncertaintyVarianceCostHSC.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */