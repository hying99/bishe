/*     */ package clus.activelearning.algoHSC;
/*     */ 
/*     */ import clus.Clus;
/*     */ import clus.activelearning.algo.ClusActiveLearningAlgorithmHSC;
/*     */ import clus.activelearning.data.ClassCounterCorrelation;
/*     */ import clus.activelearning.data.OracleAnswer;
/*     */ import clus.activelearning.indexing.LabelIndex;
/*     */ import clus.activelearning.indexing.TupleIndex;
/*     */ import clus.algo.kNN.BasicDistance;
/*     */ import clus.algo.kNN.EuclidianDistance;
/*     */ import clus.algo.kNN.KNNStatistics;
/*     */ import clus.algo.kNN.NominalBasicDistance;
/*     */ import clus.algo.kNN.NumericalBasicDistance;
/*     */ import clus.data.rows.DataTuple;
/*     */ import clus.data.rows.RowData;
/*     */ import clus.data.type.ClusAttrType;
/*     */ import clus.data.type.ClusSchema;
/*     */ import clus.ext.hierarchical.ClassHierarchy;
/*     */ import clus.ext.hierarchical.ClassTerm;
/*     */ import clus.ext.hierarchical.ClassesTuple;
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
/*     */ public class QueryByCommitteeHierarchyHSC
/*     */   extends ClusActiveLearningAlgorithmHSC
/*     */ {
/*     */   protected double[][] m_Distances;
/*     */   protected double m_Sigma;
/*     */   
/*     */   public QueryByCommitteeHierarchyHSC(Clus clus, String name) throws ClusException, Exception {
/*  49 */     super(clus, name);
/*  50 */     this.m_Sigma = 1.0D;
/*     */     
/*  52 */     initializeDistances();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public RowData sampleLabelBasedHSC(ClusModel[] models) throws ClusException {
/*  58 */     double[][] variance = (double[][])null;
/*  59 */     double[][] uncertainty = (double[][])null;
/*  60 */     double[][] predictionProbabilities = (double[][])null;
/*  61 */     int[] classCount = new int[getHierarchy().getTotal()];
/*  62 */     RowData rowData = getLabeledData();
/*  63 */     int nbRows = rowData.getNbRows();
/*     */     
/*  65 */     ClassesTuple ct2 = null;
/*     */     
/*  67 */     double[] highestPrediction = new double[getHierarchy().getTotal()];
/*  68 */     double[] lowestPrediction = new double[getHierarchy().getTotal()];
/*  69 */     double[] adjustedDecisionBorder = new double[getHierarchy().getTotal()];
/*  70 */     for (int i = 0; i < lowestPrediction[i]; i++) {
/*  71 */       lowestPrediction[i] = 2.0D;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/*  94 */       variance = getVariance(models, getUnlabeledData());
/*     */     }
/*  96 */     catch (IOException|ClassNotFoundException ex) {
/*  97 */       Logger.getLogger(UncertaintySamplingHSC.class.getName()).log(Level.SEVERE, (String)null, ex);
/*     */     } 
/*     */     
/* 100 */     double[][] finalMeasure = getFinalUncertainty(variance, uncertainty);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 106 */     LinkedList<LabelIndex> labelIndexer = buildLabelMinIndexer(finalMeasure);
/* 107 */     return getPartialLabelledDataset(labelIndexer);
/*     */   }
/*     */   
/*     */   public double[][] getLabelAdjustedUncertainty(double[][] predictionProbabilities, double[] adjusted) {
/* 111 */     double[][] uncertainty = new double[predictionProbabilities.length][predictionProbabilities.length];
/* 112 */     int nbRows = predictionProbabilities.length;
/*     */     
/* 114 */     for (int i = 0; i < nbRows; i++) {
/* 115 */       double[] probs = predictionProbabilities[i];
/* 116 */       for (int j = 0; j < probs.length; j++) {
/* 117 */         probs[j] = Math.abs(adjusted[j] - probs[j]);
/*     */       }
/* 119 */       uncertainty[i] = probs;
/*     */     } 
/* 121 */     return uncertainty;
/*     */   }
/*     */   
/*     */   private double[][] getFinalUncertainty(double[][] variance, double[][] uncertainty) throws ClusException {
/* 125 */     String[] labels = getLabels();
/* 126 */     double[][] finalVariance = new double[variance.length][];
/*     */     
/* 128 */     HashMap<Integer, Boolean[]> ascendants = (HashMap)new HashMap<>();
/* 129 */     HashMap<Integer, Boolean[]> descendants = (HashMap)new HashMap<>();
/* 130 */     int[] levels = new int[(getLabels()).length];
/* 131 */     for (int i = 0; i < levels.length; i++) {
/* 132 */       levels[i] = (getLabels()[i].split("/")).length - 1;
/*     */     }
/*     */     
/* 135 */     ClassHierarchy h = getHierarchy();
/* 136 */     for (int j = 0; j < h.getTotal(); j++) {
/* 137 */       ClassTerm ct = h.getTermAt(j);
/* 138 */       Boolean[] ids = new Boolean[h.getTotal()];
/* 139 */       for (int m = 0; m < ids.length; ) { ids[m] = Boolean.valueOf(false); m++; }
/* 140 */        ct.fillBoolArrayNodeAndAncestors(ids);
/* 141 */       ascendants.put(Integer.valueOf(ct.getIndex()), ids);
/* 142 */       Boolean[] ids2 = new Boolean[h.getTotal()];
/* 143 */       for (int n = 0; n < ids.length; ) { ids2[n] = Boolean.valueOf(false); n++; }
/* 144 */        ct.fillBoolDescendants(ids2);
/* 145 */       descendants.put(Integer.valueOf(ct.getIndex()), ids2);
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
/* 161 */     for (int k = 0; k < variance.length; k++) {
/* 162 */       OracleAnswer answer = getUnlabeledData().getTuple(k).getOracleAnswer();
/* 163 */       finalVariance[k] = new double[(variance[k]).length];
/*     */       
/* 165 */       for (int m = 0; m < (variance[0]).length; m++) {
/* 166 */         float level = levels[m];
/*     */         
/* 168 */         double ancestorsVariance = 0.0D;
/* 169 */         double descendantsVariance = 0.0D;
/* 170 */         double sameLevel = 0.0D;
/* 171 */         double countLevel = 1.0D;
/* 172 */         double countAscestors = 1.0D;
/* 173 */         double countDescendants = 1.0D;
/* 174 */         for (int n = 0; n < labels.length; n++) {
/*     */           
/* 176 */           if (m != n) {
/* 177 */             if (((Boolean[])ascendants.get(Integer.valueOf(m)))[n].booleanValue()) {
/* 178 */               ancestorsVariance += variance[k][n];
/* 179 */               countAscestors++;
/* 180 */             } else if (((Boolean[])descendants.get(Integer.valueOf(m)))[n].booleanValue()) {
/* 181 */               descendantsVariance += variance[k][n];
/* 182 */               countDescendants++;
/* 183 */             } else if (level == levels[n]) {
/* 184 */               sameLevel += variance[k][n];
/* 185 */               countLevel++;
/*     */             } 
/*     */           }
/*     */         } 
/*     */         
/* 190 */         finalVariance[k][m] = -variance[k][m] - ancestorsVariance / countAscestors - descendantsVariance / countDescendants - sameLevel / countLevel;
/*     */       } 
/*     */     } 
/*     */     
/* 194 */     return finalVariance;
/*     */   }
/*     */   
/*     */   private void initializeDistances() {
/* 198 */     initializeDistances(getUnlabeledData().getSchema());
/*     */     
/* 200 */     calcDistance();
/*     */   }
/*     */   
/*     */   protected void initializeDistances(ClusSchema schema) {
/* 204 */     NominalBasicDistance nomDist = new NominalBasicDistance();
/* 205 */     NumericalBasicDistance numDist = new NumericalBasicDistance();
/* 206 */     ClusAttrType[] attrs = schema.getDescriptiveAttributes();
/* 207 */     for (ClusAttrType attr : attrs) {
/* 208 */       if (attr.getTypeIndex() == 0) {
/* 209 */         attr.setBasicDistance((BasicDistance)nomDist);
/*     */       } else {
/* 211 */         attr.setBasicDistance((BasicDistance)numDist);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void calcDistance() {
/* 218 */     RowData rowData = getUnlabeledData();
/* 219 */     int nbRows = rowData.getNbRows();
/* 220 */     this.m_Distances = new double[nbRows][nbRows];
/* 221 */     KNNStatistics stats = new KNNStatistics(rowData);
/*     */     
/* 223 */     ClusAttrType[] attrs = rowData.getSchema().getDescriptiveAttributes();
/*     */     
/* 225 */     double[] weights = new double[attrs.length];
/* 226 */     for (int j = 0; j < weights.length; j++) {
/* 227 */       weights[j] = 1.0D;
/*     */     }
/* 229 */     EuclidianDistance euclidianDistance = new EuclidianDistance(attrs, weights);
/* 230 */     for (int i = 0; i < nbRows; i++) {
/* 231 */       DataTuple curTup = rowData.getTuple(i);
/* 232 */       double sum = 0.0D;
/* 233 */       for (int k = 0; k < i; k++) {
/* 234 */         DataTuple tuple = rowData.getTuple(k);
/* 235 */         double dist = euclidianDistance.getDistance(tuple, curTup);
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 240 */         this.m_Distances[i][k] = gaussianKernelDistance(dist);
/* 241 */         this.m_Distances[k][i] = gaussianKernelDistance(dist);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected double gaussianKernelDistance(double distance) {
/* 249 */     return Math.exp(-distance / 2.0D * Math.pow(this.m_Sigma, 2.0D));
/*     */   }
/*     */ 
/*     */   
/*     */   public RowData sampleInstanceBasedHSC(ClusModel[] models) throws ClusException {
/* 254 */     double[][] probs = (double[][])null;
/*     */     try {
/* 256 */       probs = getPredictionProbabilities(models, getUnlabeledData());
/* 257 */     } catch (IOException|ClassNotFoundException ex) {
/* 258 */       Logger.getLogger(UncertaintySamplingHSC.class.getName()).log(Level.SEVERE, (String)null, ex);
/*     */     } 
/* 260 */     double[] uncertainty = getTuplesEntropyUncertainty(probs);
/* 261 */     LinkedList<TupleIndex> indexer = buildTupleMaxIndexer(uncertainty);
/* 262 */     return getLabeledDataset(indexer);
/*     */   }
/*     */   
/*     */   public double[] getTuplesEntropyUncertainty(double[][] predictionProbabilities) {
/* 266 */     int nbRows = predictionProbabilities.length;
/* 267 */     double[] uncertainty = new double[predictionProbabilities.length];
/* 268 */     for (int i = 0; i < nbRows; i++) {
/* 269 */       double[] probs = predictionProbabilities[i]; double[] arrayOfDouble1; int j; byte b;
/* 270 */       for (arrayOfDouble1 = probs, j = arrayOfDouble1.length, b = 0; b < j; ) { Double prob = Double.valueOf(arrayOfDouble1[b]);
/* 271 */         double logProbability = Math.log(prob.doubleValue()) / Math.log(2.0D);
/* 272 */         if (Double.isInfinite(logProbability)) {
/* 273 */           uncertainty[i] = uncertainty[i] + 0.0D;
/*     */         } else {
/* 275 */           uncertainty[i] = uncertainty[i] + prob.doubleValue() * logProbability;
/*     */         }  b++; }
/*     */       
/* 278 */       uncertainty[i] = -uncertainty[i];
/*     */     } 
/* 280 */     return uncertainty;
/*     */   }
/*     */ 
/*     */   
/*     */   public RowData sampleSemiPartialLabelling(ClusModel[] models) throws ClusException {
/* 285 */     double[][] probs = (double[][])null;
/* 286 */     LinkedList<LabelIndex> labelIndexer = null;
/*     */     try {
/* 288 */       probs = getPredictionProbabilities(models, getUnlabeledData());
/* 289 */       double[] entropyUncertainty = getTuplesEntropyUncertainty(probs);
/* 290 */       RowData rowData = getTuplesByHighestMeasure(entropyUncertainty);
/* 291 */       probs = getPredictionProbabilities(models, rowData);
/* 292 */       double[][] uncertainty = getLabelUncertainty(probs);
/* 293 */       labelIndexer = buildLabelMinIndexerSemi(rowData, uncertainty);
/*     */     }
/* 295 */     catch (IOException|ClassNotFoundException ex) {
/* 296 */       Logger.getLogger(UncertaintySamplingHSC.class.getName()).log(Level.SEVERE, (String)null, ex);
/*     */     } 
/* 298 */     return getPartialLabelledDataset(labelIndexer);
/*     */   }
/*     */   
/*     */   private double[][] getFinalCorrelation(double[][] labelsCorrelation) {
/* 302 */     RowData unlabeledData = getUnlabeledData();
/* 303 */     int nbRows = unlabeledData.getNbRows();
/* 304 */     double[][] finalCorrelation = new double[nbRows][labelsCorrelation.length];
/* 305 */     for (int i = 0; i < nbRows; i++) {
/*     */       
/* 307 */       for (int j = 0; j < labelsCorrelation.length; j++) {
/* 308 */         double unanswered = unlabeledData.getTuple(i).getOracleAnswer().getUnansweredAmount(getHierarchy().getTotal());
/* 309 */         double sum = 0.0D;
/*     */         
/* 311 */         if (unanswered == 1.0D) {
/* 312 */           finalCorrelation[i][j] = 0.0D;
/*     */         } else {
/* 314 */           for (int k = 0; k < (labelsCorrelation[j]).length; k++) {
/*     */             
/* 316 */             if (!unlabeledData.getTuple(i).getOracleAnswer().queriedBefore(getLabels()[k])) {
/* 317 */               sum += Math.abs(labelsCorrelation[j][k]);
/*     */             }
/*     */           } 
/*     */ 
/*     */           
/* 322 */           finalCorrelation[i][j] = sum / unanswered;
/*     */         } 
/*     */       } 
/*     */     } 
/* 326 */     return finalCorrelation;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double[][] measureFinalCorrelation(double[][] predictionProbabilities, double[][] labelsCorrelation) {
/* 333 */     double[][] uncertainty = new double[predictionProbabilities.length][];
/* 334 */     int nbRows = predictionProbabilities.length;
/* 335 */     for (int i = 0; i < nbRows; i++) {
/* 336 */       double[] probs = predictionProbabilities[i];
/* 337 */       for (int j = 0; j < probs.length; j++) {
/* 338 */         probs[j] = probs[j] * -labelsCorrelation[i][j];
/*     */       }
/* 340 */       uncertainty[i] = probs;
/*     */     } 
/* 342 */     return uncertainty;
/*     */   }
/*     */   
/*     */   private double[][] getLabelCorrelation(RowData labeledData) {
/* 346 */     ClassHierarchy h = getHierarchy();
/*     */     
/* 348 */     int hierarchySize = h.getTotal();
/* 349 */     double[][] correlation = new double[h.getTotal()][h.getTotal()];
/*     */ 
/*     */ 
/*     */     
/* 353 */     for (int i = 1; i < hierarchySize; i++) {
/* 354 */       for (int j = 0; j <= i; j++) {
/* 355 */         if (i == j) {
/* 356 */           correlation[i][j] = 0.0D;
/* 357 */           correlation[j][i] = 0.0D;
/*     */         } else {
/* 359 */           double correlationValue = measureCorrelationValue(labeledData, h, i, j);
/* 360 */           if (!Double.isNaN(correlationValue)) {
/* 361 */             correlation[i][j] = correlationValue;
/* 362 */             correlation[j][i] = correlationValue;
/*     */           } else {
/* 364 */             correlation[i][j] = 0.0D;
/* 365 */             correlation[j][i] = 0.0D;
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } 
/* 370 */     return correlation;
/*     */   }
/*     */ 
/*     */   
/*     */   private double measureCorrelationValue(RowData rowData, ClassHierarchy h, int i, int j) {
/* 375 */     ClassCounterCorrelation cc = new ClassCounterCorrelation(i, j);
/* 376 */     cc.count(rowData, h, i, i);
/* 377 */     double A = cc.getCounterA();
/* 378 */     double B = cc.getCounterB();
/* 379 */     double C = cc.getCounterC();
/* 380 */     double D = cc.getCounterD();
/* 381 */     double correlation = (A * D - B * C) / Math.sqrt((A + B) * (A + C) * (C + D) * (B + D));
/* 382 */     return correlation;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\activelearning\algoHSC\QueryByCommitteeHierarchyHSC.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */