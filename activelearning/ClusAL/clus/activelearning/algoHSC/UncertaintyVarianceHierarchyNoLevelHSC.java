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
/*     */ public class UncertaintyVarianceHierarchyNoLevelHSC
/*     */   extends ClusActiveLearningAlgorithmHSC
/*     */ {
/*     */   public UncertaintyVarianceHierarchyNoLevelHSC(Clus clus, String name) throws ClusException, Exception {
/*  34 */     super(clus, name);
/*     */   }
/*     */ 
/*     */   
/*     */   public RowData sampleLabelBasedHSC(ClusModel[] models) throws ClusException {
/*  39 */     double[][] probs = (double[][])null;
/*  40 */     double[][] variance = (double[][])null;
/*     */     
/*     */     try {
/*  43 */       probs = getPredictionProbabilities(models, getUnlabeledData());
/*  44 */       variance = getVariance(models, getUnlabeledData());
/*  45 */       probs = getLabelUncertainty(probs);
/*  46 */     } catch (IOException|ClassNotFoundException ex) {
/*  47 */       Logger.getLogger(UncertaintySamplingHSC.class.getName()).log(Level.SEVERE, (String)null, ex);
/*     */     } 
/*     */     
/*  50 */     double[][] uncertainty = getFinalUncertainty(variance, probs);
/*  51 */     LinkedList<LabelIndex> labelIndexer = buildLabelMinIndexer(uncertainty);
/*  52 */     return getPartialLabelledDataset(labelIndexer);
/*     */   }
/*     */   
/*     */   private double[][] getFinalUncertainty(double[][] variance, double[][] uncertainty) throws ClusException {
/*  56 */     String[] labels = getLabels();
/*  57 */     double[][] finalVariance = new double[variance.length][];
/*     */     
/*  59 */     HashMap<Integer, Boolean[]> ascendants = (HashMap)new HashMap<>();
/*  60 */     HashMap<Integer, Boolean[]> descendants = (HashMap)new HashMap<>();
/*  61 */     int[] levels = new int[(getLabels()).length];
/*  62 */     for (int i = 0; i < levels.length; i++) {
/*  63 */       levels[i] = (getLabels()[i].split("/")).length - 1;
/*     */     }
/*     */     
/*  66 */     ClassHierarchy h = getHierarchy();
/*  67 */     for (int j = 0; j < h.getTotal(); j++) {
/*  68 */       ClassTerm ct = h.getTermAt(j);
/*  69 */       Boolean[] ids = new Boolean[h.getTotal()];
/*  70 */       for (int m = 0; m < ids.length; ) { ids[m] = Boolean.valueOf(false); m++; }
/*  71 */        ct.fillBoolArrayNodeAndAncestors(ids);
/*  72 */       ascendants.put(Integer.valueOf(ct.getIndex()), ids);
/*  73 */       Boolean[] ids2 = new Boolean[h.getTotal()];
/*  74 */       for (int n = 0; n < ids.length; ) { ids2[n] = Boolean.valueOf(false); n++; }
/*  75 */        ct.fillBoolDescendants(ids2);
/*  76 */       descendants.put(Integer.valueOf(ct.getIndex()), ids2);
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
/*  92 */     for (int k = 0; k < variance.length; k++) {
/*  93 */       OracleAnswer answer = getUnlabeledData().getTuple(k).getOracleAnswer();
/*  94 */       finalVariance[k] = new double[(variance[k]).length];
/*     */       
/*  96 */       for (int m = 0; m < (variance[0]).length; m++) {
/*  97 */         double ancestorsVariance = 0.0D;
/*  98 */         double descendantsVariance = 0.0D;
/*  99 */         double sameLevel = 0.0D;
/* 100 */         double countLevel = 1.0D;
/* 101 */         double countAscestors = 1.0D;
/* 102 */         double countDescendants = 1.0D;
/* 103 */         for (int n = 0; n < labels.length; n++) {
/*     */           
/* 105 */           if (m != n) {
/* 106 */             if (((Boolean[])ascendants.get(Integer.valueOf(m)))[n].booleanValue()) {
/* 107 */               ancestorsVariance += variance[k][n];
/* 108 */               countAscestors++;
/* 109 */             } else if (((Boolean[])descendants.get(Integer.valueOf(m)))[n].booleanValue()) {
/* 110 */               descendantsVariance += variance[k][n];
/* 111 */               countDescendants++;
/*     */             } 
/*     */           }
/*     */         } 
/*     */         
/* 116 */         finalVariance[k][m] = uncertainty[k][m] - ancestorsVariance / countAscestors - descendantsVariance / countDescendants;
/*     */       } 
/*     */     } 
/*     */     
/* 120 */     return finalVariance;
/*     */   }
/*     */ 
/*     */   
/*     */   public RowData sampleInstanceBasedHSC(ClusModel[] models) throws ClusException {
/* 125 */     double[][] probs = (double[][])null;
/*     */     try {
/* 127 */       probs = getPredictionProbabilities(models, getUnlabeledData());
/* 128 */     } catch (IOException|ClassNotFoundException ex) {
/* 129 */       Logger.getLogger(UncertaintySamplingHSC.class.getName()).log(Level.SEVERE, (String)null, ex);
/*     */     } 
/* 131 */     double[] uncertainty = getTuplesEntropyUncertainty(probs);
/* 132 */     LinkedList<TupleIndex> indexer = buildTupleMaxIndexer(uncertainty);
/* 133 */     return getLabeledDataset(indexer);
/*     */   }
/*     */   
/*     */   public double[] getTuplesEntropyUncertainty(double[][] predictionProbabilities) {
/* 137 */     int nbRows = predictionProbabilities.length;
/* 138 */     double[] uncertainty = new double[predictionProbabilities.length];
/* 139 */     for (int i = 0; i < nbRows; i++) {
/* 140 */       double[] probs = predictionProbabilities[i]; double[] arrayOfDouble1; int j; byte b;
/* 141 */       for (arrayOfDouble1 = probs, j = arrayOfDouble1.length, b = 0; b < j; ) { Double prob = Double.valueOf(arrayOfDouble1[b]);
/* 142 */         double logProbability = Math.log(prob.doubleValue()) / Math.log(2.0D);
/* 143 */         if (Double.isInfinite(logProbability)) {
/* 144 */           uncertainty[i] = uncertainty[i] + 0.0D;
/*     */         } else {
/* 146 */           uncertainty[i] = uncertainty[i] + prob.doubleValue() * logProbability;
/*     */         }  b++; }
/*     */       
/* 149 */       uncertainty[i] = -uncertainty[i];
/*     */     } 
/* 151 */     return uncertainty;
/*     */   }
/*     */ 
/*     */   
/*     */   public RowData sampleSemiPartialLabelling(ClusModel[] models) throws ClusException {
/* 156 */     double[][] probs = (double[][])null;
/* 157 */     LinkedList<LabelIndex> labelIndexer = null;
/*     */     try {
/* 159 */       probs = getPredictionProbabilities(models, getUnlabeledData());
/* 160 */       double[] entropyUncertainty = getTuplesEntropyUncertainty(probs);
/* 161 */       RowData rowData = getTuplesByHighestMeasure(entropyUncertainty);
/* 162 */       probs = getPredictionProbabilities(models, rowData);
/* 163 */       double[][] uncertainty = getLabelUncertainty(probs);
/* 164 */       labelIndexer = buildLabelMinIndexerSemi(rowData, uncertainty);
/*     */     }
/* 166 */     catch (IOException|ClassNotFoundException ex) {
/* 167 */       Logger.getLogger(UncertaintySamplingHSC.class.getName()).log(Level.SEVERE, (String)null, ex);
/*     */     } 
/* 169 */     return getPartialLabelledDataset(labelIndexer);
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\activelearning\algoHSC\UncertaintyVarianceHierarchyNoLevelHSC.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */