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
/*     */ public class UncertaintySamplingHierarchy
/*     */   extends ClusActiveLearningAlgorithmHSC
/*     */ {
/*     */   public UncertaintySamplingHierarchy(Clus clus, String name) throws ClusException, Exception {
/*  34 */     super(clus, name);
/*     */   }
/*     */ 
/*     */   
/*     */   public RowData sampleLabelBasedHSC(ClusModel[] models) throws ClusException {
/*  39 */     double[][] probs = (double[][])null;
/*     */     try {
/*  41 */       probs = getPredictionProbabilities(models, getUnlabeledData());
/*  42 */       probs = getLabelUncertainty(probs);
/*     */     }
/*  44 */     catch (IOException|ClassNotFoundException ex) {
/*  45 */       Logger.getLogger(UncertaintySamplingHSC.class.getName()).log(Level.SEVERE, (String)null, ex);
/*     */     } 
/*  47 */     double[][] uncertainty = getFinalUncertainty(probs);
/*  48 */     LinkedList<LabelIndex> labelIndexer = buildLabelMinIndexer(uncertainty);
/*  49 */     return getPartialLabelledDataset(labelIndexer);
/*     */   }
/*     */   
/*     */   private double[][] getFinalUncertainty(double[][] variance) {
/*  53 */     String[] labels = getLabels();
/*  54 */     double[][] finalVariance = new double[variance.length][];
/*     */     
/*  56 */     HashMap<Integer, Boolean[]> ascendants = (HashMap)new HashMap<>();
/*  57 */     HashMap<Integer, Boolean[]> descendants = (HashMap)new HashMap<>();
/*  58 */     int[] levels = new int[(getLabels()).length];
/*  59 */     for (int i = 0; i < levels.length; i++) {
/*  60 */       levels[i] = (getLabels()[i].split("/")).length - 1;
/*     */     }
/*     */     
/*  63 */     ClassHierarchy h = getHierarchy();
/*  64 */     for (int j = 0; j < h.getTotal(); j++) {
/*  65 */       ClassTerm ct = h.getTermAt(j);
/*  66 */       Boolean[] ids = new Boolean[h.getTotal()];
/*  67 */       for (int m = 0; m < ids.length; ) { ids[m] = Boolean.valueOf(false); m++; }
/*  68 */        ct.fillBoolArrayNodeAndAncestors(ids);
/*  69 */       ascendants.put(Integer.valueOf(ct.getIndex()), ids);
/*  70 */       Boolean[] ids2 = new Boolean[h.getTotal()];
/*  71 */       for (int n = 0; n < ids.length; ) { ids2[n] = Boolean.valueOf(false); n++; }
/*  72 */        ct.fillBoolDescendants(ids2);
/*  73 */       descendants.put(Integer.valueOf(ct.getIndex()), ids2);
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
/*  89 */     for (int k = 0; k < variance.length; k++) {
/*  90 */       OracleAnswer answer = getUnlabeledData().getTuple(k).getOracleAnswer();
/*  91 */       finalVariance[k] = new double[(variance[k]).length];
/*     */       
/*  93 */       for (int m = 0; m < (variance[0]).length; m++) {
/*  94 */         float level = levels[m];
/*     */         
/*  96 */         double ancestorsVariance = 0.0D;
/*  97 */         double descendantsVariance = 0.0D;
/*  98 */         double sameLevel = 0.0D;
/*  99 */         double countLevel = 1.0D;
/* 100 */         double countAscestors = 1.0D;
/* 101 */         double countDescendants = 1.0D;
/* 102 */         for (int n = 0; n < labels.length; n++) {
/*     */           
/* 104 */           if (m != n) {
/* 105 */             if (((Boolean[])ascendants.get(Integer.valueOf(m)))[n].booleanValue()) {
/* 106 */               ancestorsVariance += variance[k][n];
/* 107 */               countAscestors++;
/* 108 */             } else if (((Boolean[])descendants.get(Integer.valueOf(m)))[n].booleanValue()) {
/* 109 */               descendantsVariance += variance[k][n];
/* 110 */               countDescendants++;
/* 111 */             } else if (level == levels[n]) {
/* 112 */               sameLevel += variance[k][n];
/* 113 */               countLevel++;
/*     */             } 
/*     */           }
/*     */         } 
/*     */         
/* 118 */         finalVariance[k][m] = -variance[k][m] - ancestorsVariance / countAscestors - descendantsVariance / countDescendants - sameLevel / countLevel;
/*     */       } 
/*     */     } 
/*     */     
/* 122 */     return finalVariance;
/*     */   }
/*     */ 
/*     */   
/*     */   public RowData sampleInstanceBasedHSC(ClusModel[] models) throws ClusException {
/* 127 */     double[][] probs = (double[][])null;
/*     */     try {
/* 129 */       probs = getPredictionProbabilities(models, getUnlabeledData());
/* 130 */     } catch (IOException|ClassNotFoundException ex) {
/* 131 */       Logger.getLogger(UncertaintySamplingHSC.class.getName()).log(Level.SEVERE, (String)null, ex);
/*     */     } 
/* 133 */     double[] uncertainty = getTuplesEntropyUncertainty(probs);
/* 134 */     LinkedList<TupleIndex> indexer = buildTupleMaxIndexer(uncertainty);
/* 135 */     return getLabeledDataset(indexer);
/*     */   }
/*     */   
/*     */   public double[] getTuplesEntropyUncertainty(double[][] predictionProbabilities) {
/* 139 */     int nbRows = predictionProbabilities.length;
/* 140 */     double[] uncertainty = new double[predictionProbabilities.length];
/* 141 */     for (int i = 0; i < nbRows; i++) {
/* 142 */       double[] probs = predictionProbabilities[i]; double[] arrayOfDouble1; int j; byte b;
/* 143 */       for (arrayOfDouble1 = probs, j = arrayOfDouble1.length, b = 0; b < j; ) { Double prob = Double.valueOf(arrayOfDouble1[b]);
/* 144 */         double logProbability = Math.log(prob.doubleValue()) / Math.log(2.0D);
/* 145 */         if (Double.isInfinite(logProbability)) {
/* 146 */           uncertainty[i] = uncertainty[i] + 0.0D;
/*     */         } else {
/* 148 */           uncertainty[i] = uncertainty[i] + prob.doubleValue() * logProbability;
/*     */         }  b++; }
/*     */       
/* 151 */       uncertainty[i] = -uncertainty[i];
/*     */     } 
/* 153 */     return uncertainty;
/*     */   }
/*     */ 
/*     */   
/*     */   public RowData sampleSemiPartialLabelling(ClusModel[] models) throws ClusException {
/* 158 */     double[][] probs = (double[][])null;
/* 159 */     LinkedList<LabelIndex> labelIndexer = null;
/*     */     try {
/* 161 */       probs = getPredictionProbabilities(models, getUnlabeledData());
/* 162 */       double[] entropyUncertainty = getTuplesEntropyUncertainty(probs);
/* 163 */       RowData rowData = getTuplesByHighestMeasure(entropyUncertainty);
/* 164 */       probs = getPredictionProbabilities(models, rowData);
/* 165 */       double[][] uncertainty = getLabelUncertainty(probs);
/* 166 */       labelIndexer = buildLabelMinIndexerSemi(rowData, uncertainty);
/*     */     }
/* 168 */     catch (IOException|ClassNotFoundException ex) {
/* 169 */       Logger.getLogger(UncertaintySamplingHSC.class.getName()).log(Level.SEVERE, (String)null, ex);
/*     */     } 
/* 171 */     return getPartialLabelledDataset(labelIndexer);
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\activelearning\algoHSC\UncertaintySamplingHierarchy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */