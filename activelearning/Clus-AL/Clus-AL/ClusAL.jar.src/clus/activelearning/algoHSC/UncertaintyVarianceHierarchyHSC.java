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
/*     */ public class UncertaintyVarianceHierarchyHSC
/*     */   extends ClusActiveLearningAlgorithmHSC
/*     */ {
/*     */   public UncertaintyVarianceHierarchyHSC(Clus clus, String name) throws ClusException, Exception {
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
/*  97 */         float level = levels[m];
/*     */         
/*  99 */         double ancestorsVariance = 0.0D;
/* 100 */         double descendantsVariance = 0.0D;
/* 101 */         double sameLevel = 0.0D;
/* 102 */         double countLevel = 1.0D;
/* 103 */         double countAscestors = 1.0D;
/* 104 */         double countDescendants = 1.0D;
/* 105 */         for (int n = 0; n < labels.length; n++) {
/*     */           
/* 107 */           if (m != n) {
/* 108 */             if (((Boolean[])ascendants.get(Integer.valueOf(m)))[n].booleanValue()) {
/* 109 */               ancestorsVariance += variance[k][n];
/* 110 */               countAscestors++;
/* 111 */             } else if (((Boolean[])descendants.get(Integer.valueOf(m)))[n].booleanValue()) {
/* 112 */               descendantsVariance += variance[k][n];
/* 113 */               countDescendants++;
/* 114 */             } else if (level == levels[n]) {
/* 115 */               sameLevel += variance[k][n];
/* 116 */               countLevel++;
/*     */             } 
/*     */           }
/*     */         } 
/*     */         
/* 121 */         finalVariance[k][m] = uncertainty[k][m] - ancestorsVariance / countAscestors - descendantsVariance / countDescendants - sameLevel / countLevel;
/*     */       } 
/*     */     } 
/*     */     
/* 125 */     return finalVariance;
/*     */   }
/*     */ 
/*     */   
/*     */   public RowData sampleInstanceBasedHSC(ClusModel[] models) throws ClusException {
/* 130 */     double[][] probs = (double[][])null;
/*     */     try {
/* 132 */       probs = getPredictionProbabilities(models, getUnlabeledData());
/* 133 */     } catch (IOException|ClassNotFoundException ex) {
/* 134 */       Logger.getLogger(UncertaintySamplingHSC.class.getName()).log(Level.SEVERE, (String)null, ex);
/*     */     } 
/* 136 */     double[] uncertainty = getTuplesEntropyUncertainty(probs);
/* 137 */     LinkedList<TupleIndex> indexer = buildTupleMaxIndexer(uncertainty);
/* 138 */     return getLabeledDataset(indexer);
/*     */   }
/*     */   
/*     */   public double[] getTuplesEntropyUncertainty(double[][] predictionProbabilities) {
/* 142 */     int nbRows = predictionProbabilities.length;
/* 143 */     double[] uncertainty = new double[predictionProbabilities.length];
/* 144 */     for (int i = 0; i < nbRows; i++) {
/* 145 */       double[] probs = predictionProbabilities[i]; double[] arrayOfDouble1; int j; byte b;
/* 146 */       for (arrayOfDouble1 = probs, j = arrayOfDouble1.length, b = 0; b < j; ) { Double prob = Double.valueOf(arrayOfDouble1[b]);
/* 147 */         double logProbability = Math.log(prob.doubleValue()) / Math.log(2.0D);
/* 148 */         if (Double.isInfinite(logProbability)) {
/* 149 */           uncertainty[i] = uncertainty[i] + 0.0D;
/*     */         } else {
/* 151 */           uncertainty[i] = uncertainty[i] + prob.doubleValue() * logProbability;
/*     */         }  b++; }
/*     */       
/* 154 */       uncertainty[i] = -uncertainty[i];
/*     */     } 
/* 156 */     return uncertainty;
/*     */   }
/*     */ 
/*     */   
/*     */   public RowData sampleSemiPartialLabelling(ClusModel[] models) throws ClusException {
/* 161 */     double[][] probs = (double[][])null;
/* 162 */     LinkedList<LabelIndex> labelIndexer = null;
/*     */     try {
/* 164 */       probs = getPredictionProbabilities(models, getUnlabeledData());
/* 165 */       double[] entropyUncertainty = getTuplesEntropyUncertainty(probs);
/* 166 */       RowData rowData = getTuplesByHighestMeasure(entropyUncertainty);
/* 167 */       probs = getPredictionProbabilities(models, rowData);
/* 168 */       double[][] uncertainty = getLabelUncertainty(probs);
/* 169 */       labelIndexer = buildLabelMinIndexerSemi(rowData, uncertainty);
/*     */     }
/* 171 */     catch (IOException|ClassNotFoundException ex) {
/* 172 */       Logger.getLogger(UncertaintySamplingHSC.class.getName()).log(Level.SEVERE, (String)null, ex);
/*     */     } 
/* 174 */     return getPartialLabelledDataset(labelIndexer);
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\activelearning\algoHSC\UncertaintyVarianceHierarchyHSC.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */