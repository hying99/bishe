/*     */ package clus.activelearning.algoHSC;
/*     */ 
/*     */ import clus.Clus;
/*     */ import clus.activelearning.algo.ClusActiveLearningAlgorithmHSC;
/*     */ import clus.activelearning.data.OracleAnswer;
/*     */ import clus.activelearning.indexing.LabelIndex;
/*     */ import clus.activelearning.indexing.TupleIndex;
/*     */ import clus.data.rows.RowData;
/*     */ import clus.ext.hierarchical.ClassHierarchy;
/*     */ import clus.ext.hierarchical.ClassTerm;
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
/*     */ public class QueryByCommitteeHierarchyNoLevelHSC
/*     */   extends ClusActiveLearningAlgorithmHSC
/*     */ {
/*     */   protected double[][] m_Distances;
/*     */   protected double m_Sigma;
/*     */   
/*     */   public QueryByCommitteeHierarchyNoLevelHSC(Clus clus, String name) throws ClusException, Exception {
/*  49 */     super(clus, name);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public RowData sampleLabelBasedHSC(ClusModel[] models) throws ClusException {
/*  55 */     double[][] variance = (double[][])null;
/*  56 */     double[][] uncertainty = (double[][])null;
/*  57 */     RowData rowData = getLabeledData();
/*     */     
/*     */     try {
/*  60 */       variance = getVariance(models, getUnlabeledData());
/*  61 */     } catch (IOException|ClassNotFoundException ex) {
/*  62 */       Logger.getLogger(UncertaintySamplingHSC.class.getName()).log(Level.SEVERE, (String)null, ex);
/*     */     } 
/*  64 */     getIteration().setMeasure(variance);
/*     */     
/*  66 */     double[][] finalMeasure = getFinalUncertainty(variance, uncertainty);
/*  67 */     getIteration().setFinalMeasure(finalMeasure);
/*  68 */     LinkedList<LabelIndex> labelIndexer = buildLabelMaxIndexer(finalMeasure);
/*  69 */     return getPartialLabelledDataset(labelIndexer);
/*     */   }
/*     */ 
/*     */   
/*     */   private double[][] getFinalUncertainty(double[][] variance, double[][] uncertainty) throws ClusException {
/*  74 */     String[] labels = getLabels();
/*  75 */     double[][] finalVariance = new double[variance.length][];
/*     */     
/*  77 */     HashMap<Integer, Boolean[]> ascendants = (HashMap)new HashMap<>();
/*  78 */     HashMap<Integer, Boolean[]> descendants = (HashMap)new HashMap<>();
/*  79 */     int[] levels = new int[(getLabels()).length];
/*  80 */     for (int i = 0; i < levels.length; i++) {
/*  81 */       levels[i] = (getLabels()[i].split("/")).length - 1;
/*     */     }
/*     */     
/*  84 */     ClassHierarchy h = getHierarchy();
/*  85 */     for (int j = 0; j < h.getTotal(); j++) {
/*  86 */       ClassTerm ct = h.getTermAt(j);
/*  87 */       Boolean[] ids = new Boolean[h.getTotal()];
/*  88 */       for (int m = 0; m < ids.length; ) { ids[m] = Boolean.valueOf(false); m++; }
/*  89 */        ct.fillBoolArrayNodeAndAncestors(ids);
/*  90 */       ascendants.put(Integer.valueOf(ct.getIndex()), ids);
/*  91 */       Boolean[] ids2 = new Boolean[h.getTotal()];
/*  92 */       for (int n = 0; n < ids.length; ) { ids2[n] = Boolean.valueOf(false); n++; }
/*  93 */        ct.fillBoolDescendants(ids2);
/*  94 */       descendants.put(Integer.valueOf(ct.getIndex()), ids2);
/*     */     } 
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
/* 110 */     for (int k = 0; k < variance.length; k++) {
/* 111 */       OracleAnswer answer = getUnlabeledData().getTuple(k).getOracleAnswer();
/* 112 */       finalVariance[k] = new double[(variance[k]).length];
/*     */       
/* 114 */       for (int m = 0; m < (variance[0]).length; m++) {
/* 115 */         float level = levels[m];
/*     */         
/* 117 */         double ancestorsVariance = 0.0D;
/* 118 */         double descendantsVariance = 0.0D;
/* 119 */         double sameLevel = 0.0D;
/* 120 */         double countLevel = 1.0D;
/* 121 */         double countAscestors = 1.0D;
/* 122 */         double countDescendants = 1.0D;
/* 123 */         for (int n = 0; n < labels.length; n++) {
/*     */           
/* 125 */           if (m != n) {
/* 126 */             if (((Boolean[])ascendants.get(Integer.valueOf(m)))[n].booleanValue()) {
/* 127 */               ancestorsVariance += variance[k][n];
/* 128 */               countAscestors++;
/* 129 */             } else if (((Boolean[])descendants.get(Integer.valueOf(m)))[n].booleanValue()) {
/* 130 */               descendantsVariance += variance[k][n];
/* 131 */               countDescendants++;
/* 132 */             } else if (level == levels[n]) {
/* 133 */               sameLevel += variance[k][n];
/* 134 */               countLevel++;
/*     */             } 
/*     */           }
/*     */         } 
/*     */         
/* 139 */         finalVariance[k][m] = variance[k][m] + ancestorsVariance / countAscestors + descendantsVariance / countDescendants;
/*     */       } 
/*     */     } 
/*     */     
/* 143 */     return finalVariance;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public RowData sampleInstanceBasedHSC(ClusModel[] models) throws ClusException {
/* 151 */     double[][] probs = (double[][])null;
/*     */     try {
/* 153 */       probs = getPredictionProbabilities(models, getUnlabeledData());
/* 154 */     } catch (IOException|ClassNotFoundException ex) {
/* 155 */       Logger.getLogger(UncertaintySamplingHSC.class.getName()).log(Level.SEVERE, (String)null, ex);
/*     */     } 
/* 157 */     double[] uncertainty = getTuplesEntropyUncertainty(probs);
/* 158 */     LinkedList<TupleIndex> indexer = buildTupleMaxIndexer(uncertainty);
/* 159 */     return getLabeledDataset(indexer);
/*     */   }
/*     */   
/*     */   public double[] getTuplesEntropyUncertainty(double[][] predictionProbabilities) {
/* 163 */     int nbRows = predictionProbabilities.length;
/* 164 */     double[] uncertainty = new double[predictionProbabilities.length];
/* 165 */     for (int i = 0; i < nbRows; i++) {
/* 166 */       double[] probs = predictionProbabilities[i]; double[] arrayOfDouble1; int j; byte b;
/* 167 */       for (arrayOfDouble1 = probs, j = arrayOfDouble1.length, b = 0; b < j; ) { Double prob = Double.valueOf(arrayOfDouble1[b]);
/* 168 */         double logProbability = Math.log(prob.doubleValue()) / Math.log(2.0D);
/* 169 */         if (Double.isInfinite(logProbability)) {
/* 170 */           uncertainty[i] = uncertainty[i] + 0.0D;
/*     */         } else {
/* 172 */           uncertainty[i] = uncertainty[i] + prob.doubleValue() * logProbability;
/*     */         }  b++; }
/*     */       
/* 175 */       uncertainty[i] = -uncertainty[i];
/*     */     } 
/* 177 */     return uncertainty;
/*     */   }
/*     */ 
/*     */   
/*     */   public RowData sampleSemiPartialLabelling(ClusModel[] models) throws ClusException {
/* 182 */     double[][] probs = (double[][])null;
/* 183 */     LinkedList<LabelIndex> labelIndexer = null;
/*     */     try {
/* 185 */       probs = getPredictionProbabilities(models, getUnlabeledData());
/* 186 */       double[] entropyUncertainty = getTuplesEntropyUncertainty(probs);
/* 187 */       RowData rowData = getTuplesByHighestMeasure(entropyUncertainty);
/* 188 */       probs = getPredictionProbabilities(models, rowData);
/* 189 */       double[][] uncertainty = getLabelUncertainty(probs);
/* 190 */       labelIndexer = buildLabelMinIndexerSemi(rowData, uncertainty);
/*     */     }
/* 192 */     catch (IOException|ClassNotFoundException ex) {
/* 193 */       Logger.getLogger(UncertaintySamplingHSC.class.getName()).log(Level.SEVERE, (String)null, ex);
/*     */     } 
/* 195 */     return getPartialLabelledDataset(labelIndexer);
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\activelearning\algoHSC\QueryByCommitteeHierarchyNoLevelHSC.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */