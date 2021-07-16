/*     */ package clus.activelearning.algo;
/*     */ 
/*     */ import clus.Clus;
/*     */ import clus.activelearning.matrix.MatrixUtils;
/*     */ import clus.data.rows.DataTuple;
/*     */ import clus.data.rows.RowData;
/*     */ import clus.ext.ensembles.ClusForest;
/*     */ import clus.ext.hierarchical.ClassHierarchy;
/*     */ import clus.ext.hierarchical.ClassTerm;
/*     */ import clus.ext.hierarchical.ClassesTuple;
/*     */ import clus.model.ClusModel;
/*     */ import clus.statistic.ClusStatistic;
/*     */ import clus.util.ClusException;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.LinkedList;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class ClusActiveLearningAlgorithmHSC
/*     */   extends ClusActiveLearningAlgorithm
/*     */ {
/*     */   boolean m_OOBSelection;
/*     */   
/*     */   public ClusActiveLearningAlgorithmHSC(Clus clus, String name) throws ClusException, Exception {
/*  34 */     super(clus, name);
/*  35 */     this.m_OOBSelection = clus.getSettings().getWriteOOBError();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public RowData sample(ClusModel[] models) throws ClusException {
/*     */     RowData rowData;
/*  42 */     startIteration();
/*  43 */     if (isPartialLabelling()) {
/*  44 */       rowData = sampleLabelBasedHSC(models);
/*  45 */     } else if (isSemiPartialLabelling()) {
/*  46 */       rowData = sampleSemiPartialLabelling(models);
/*     */     } else {
/*  48 */       rowData = sampleInstanceBasedHSC(models);
/*     */     } 
/*     */     
/*  51 */     finishIteration();
/*  52 */     return rowData;
/*     */   }
/*     */   
/*     */   public abstract RowData sampleLabelBasedHSC(ClusModel[] paramArrayOfClusModel) throws ClusException;
/*     */   
/*     */   public abstract RowData sampleInstanceBasedHSC(ClusModel[] paramArrayOfClusModel) throws ClusException;
/*     */   
/*     */   public abstract RowData sampleSemiPartialLabelling(ClusModel[] paramArrayOfClusModel) throws ClusException;
/*     */   
/*     */   public double[][] getPredictionProbabilities(ClusModel[] models, RowData rowData) throws ClusException, IOException, ClassNotFoundException {
/*  62 */     return processModels(models, rowData);
/*     */   }
/*     */   
/*     */   public double[][] getVariance(ClusModel[] models, RowData rowData) throws ClusException, IOException, ClassNotFoundException {
/*  66 */     ClusForest fr = (ClusForest)models[0];
/*     */     
/*  68 */     int forestSize = fr.getNbModels();
/*     */     
/*  70 */     double[][][] modelsPredictions = new double[forestSize][][];
/*  71 */     ClassHierarchy h = getHierarchy();
/*  72 */     int nbRows = rowData.getNbRows();
/*     */     
/*  74 */     double[][] predictionsSum = new double[nbRows][h.getTotal()];
/*  75 */     if (rowData.getNbRows() == 0) {
/*  76 */       return predictionsSum;
/*     */     }
/*  78 */     for (int treeIndex = 0; treeIndex < forestSize; treeIndex++) {
/*  79 */       double[][] probs = processForest(rowData, models, treeIndex);
/*  80 */       modelsPredictions[treeIndex] = probs;
/*  81 */       predictionsSum = MatrixUtils.addMatrix(predictionsSum, probs);
/*     */     } 
/*  83 */     double[][] mean = MatrixUtils.mean(predictionsSum, forestSize);
/*     */     
/*  85 */     double[][] variance = getVariance(modelsPredictions, mean, forestSize);
/*  86 */     return variance;
/*     */   }
/*     */   
/*     */   private double[][] getVariance(double[][][] predictions, double[][] mean, int forestSize) {
/*  90 */     double forestSizef = forestSize - 1.0D;
/*  91 */     double[][] variance = new double[mean.length][(mean[0]).length];
/*     */     
/*  93 */     for (double[][] prediction : predictions) {
/*  94 */       double[][] distance = MatrixUtils.squaredDistance(prediction, mean);
/*  95 */       variance = MatrixUtils.addMatrix(variance, distance);
/*     */     } 
/*  97 */     for (double[] variance1 : variance) {
/*  98 */       for (int j = 0; j < variance1.length; j++)
/*     */       {
/* 100 */         variance1[j] = variance1[j] / (forestSizef - 1.0D);
/*     */       }
/*     */     } 
/*     */     
/* 104 */     return variance;
/*     */   }
/*     */   
/*     */   private boolean allParentsOk(ClassTerm term, boolean[] computed) {
/* 108 */     for (int j = 0; j < term.getNbParents(); j++) {
/* 109 */       ClassTerm parent = term.getParent(j);
/* 110 */       if (parent.getIndex() != -1 && !computed[parent.getIndex()]) {
/* 111 */         return false;
/*     */       }
/*     */     } 
/* 114 */     return true;
/*     */   }
/*     */   
/*     */   private double[][] processForest(RowData rowData, ClusModel[] models, int treeIndex) throws ClusException, IOException, ClassNotFoundException {
/* 118 */     double[][] probs = resetProbs(rowData);
/* 119 */     ClassHierarchy hier = getHierarchy();
/* 120 */     boolean[] prob_computed = new boolean[hier.getTotal()];
/* 121 */     ArrayList<ClassTerm> todo = new ArrayList();
/* 122 */     for (int i = 0; i < hier.getTotal(); i++) {
/* 123 */       ClassTerm term = hier.getTermAt(i);
/* 124 */       todo.add(term);
/*     */     } 
/* 126 */     int nb_done = 0;
/* 127 */     while (nb_done < hier.getTotal()) {
/* 128 */       for (int j = todo.size() - 1; j >= 0; j--) {
/* 129 */         ClassTerm term = todo.get(j);
/* 130 */         if (allParentsOk(term, prob_computed)) {
/*     */           
/* 132 */           doOneClassForest(rowData, term, models, treeIndex, probs);
/*     */           
/* 134 */           prob_computed[term.getIndex()] = true;
/* 135 */           todo.remove(j);
/* 136 */           nb_done++;
/*     */         } 
/*     */       } 
/*     */     } 
/* 140 */     return probs;
/*     */   }
/*     */ 
/*     */   
/*     */   private void doOneClassForest(RowData rowData, ClassTerm term, ClusModel[] models, int treeIndex, double[][] probs) throws IOException, ClassNotFoundException, ClusException {
/* 145 */     for (int j = 0; j < term.getNbParents(); j++) {
/* 146 */       ClassTerm parent = term.getParent(j);
/* 147 */       for (int i = 0; i < rowData.getNbRows(); i++) {
/* 148 */         updatePredictionForest(rowData, i, models, parent, term, treeIndex, probs);
/*     */       }
/*     */     } 
/*     */     
/* 152 */     int child_idx = term.getIndex();
/* 153 */     for (int exid = 0; exid < rowData.getNbRows(); exid++) {
/* 154 */       probs[exid][child_idx] = probs[exid][child_idx] / term.getNbParents();
/*     */     }
/*     */   }
/*     */   
/*     */   private void updatePredictionForest(RowData data, int exid, ClusModel[] models, ClassTerm parent, ClassTerm term, int treeIndex, double[][] probs) {
/* 159 */     DataTuple tuple = data.getTuple(exid);
/* 160 */     int parent_idx = parent.getIndex();
/* 161 */     int child_idx = term.getIndex();
/* 162 */     ClusForest forest = (ClusForest)models[child_idx];
/* 163 */     ClusModel model = forest.getModelByIndex(treeIndex);
/* 164 */     ClusStatistic prediction = model.predictWeighted(tuple);
/* 165 */     double[] predicted_distr = prediction.getNumericPred();
/* 166 */     double predicted_prob = predicted_distr[0];
/* 167 */     double parent_prob = (parent_idx == -1) ? 1.0D : probs[exid][parent_idx];
/* 168 */     double child_prob = parent_prob * predicted_prob;
/* 169 */     if (child_prob < probs[exid][child_idx]) {
/* 170 */       probs[exid][child_idx] = child_prob;
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   double[][] processModels(ClusModel[] models, RowData rowData) throws ClusException, IOException, ClassNotFoundException {
/* 176 */     double[][] probs = resetProbs(rowData);
/* 177 */     ClassHierarchy hier = getHierarchy();
/* 178 */     boolean[] prob_computed = new boolean[hier.getTotal()];
/*     */     
/* 180 */     ArrayList<ClassTerm> todo = new ArrayList();
/* 181 */     for (int i = 0; i < hier.getTotal(); i++) {
/* 182 */       ClassTerm term = hier.getTermAt(i);
/* 183 */       todo.add(term);
/*     */     } 
/* 185 */     int nb_done = 0;
/* 186 */     while (nb_done < hier.getTotal()) {
/* 187 */       for (int j = todo.size() - 1; j >= 0; j--) {
/* 188 */         ClassTerm term = todo.get(j);
/* 189 */         if (allParentsOk(term, prob_computed)) {
/*     */           
/* 191 */           doOneClass(term, models, rowData, probs);
/*     */           
/* 193 */           prob_computed[term.getIndex()] = true;
/* 194 */           todo.remove(j);
/* 195 */           nb_done++;
/*     */         } 
/*     */       } 
/*     */     } 
/* 199 */     return probs;
/*     */   }
/*     */ 
/*     */   
/*     */   private void doOneClass(ClassTerm term, ClusModel[] models, RowData rowData, double[][] probs) throws IOException, ClassNotFoundException, ClusException {
/* 204 */     for (int j = 0; j < term.getNbParents(); j++) {
/* 205 */       ClassTerm parent = term.getParent(j);
/* 206 */       for (int i = 0; i < rowData.getNbRows(); i++) {
/* 207 */         updatePrediction(rowData, i, models, parent, term, probs);
/*     */       }
/*     */     } 
/*     */     
/* 211 */     int child_idx = term.getIndex();
/* 212 */     for (int exid = 0; exid < rowData.getNbRows(); exid++) {
/* 213 */       probs[exid][child_idx] = probs[exid][child_idx] / term.getNbParents();
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private void updatePrediction(RowData data, int exid, ClusModel[] models, ClassTerm parent, ClassTerm term, double[][] probs) {
/* 219 */     DataTuple tuple = data.getTuple(exid);
/* 220 */     int parent_idx = parent.getIndex();
/* 221 */     int child_idx = term.getIndex();
/* 222 */     ClusModel model = models[child_idx];
/* 223 */     ClusStatistic prediction = model.predictWeighted(tuple);
/* 224 */     double[] predicted_distr = prediction.getNumericPred();
/* 225 */     double predicted_prob = predicted_distr[0];
/* 226 */     double parent_prob = (parent_idx == -1) ? 1.0D : probs[exid][parent_idx];
/* 227 */     double child_prob = parent_prob * predicted_prob;
/* 228 */     if (child_prob < probs[exid][child_idx]) {
/* 229 */       probs[exid][child_idx] = child_prob;
/*     */     }
/*     */   }
/*     */   
/*     */   private double[][] resetProbs(RowData rowData) throws ClusException, IOException {
/* 234 */     ClassHierarchy h = getHierarchy();
/*     */     
/* 236 */     int nbRows = rowData.getNbRows();
/*     */     
/* 238 */     double[][] probs = new double[nbRows][h.getTotal()];
/* 239 */     for (int k = 0; k < nbRows; k++) {
/* 240 */       Arrays.fill(probs[k], Double.MAX_VALUE);
/*     */     }
/* 242 */     return probs;
/*     */   }
/*     */   
/*     */   public double[][] getLabelUncertainty2(double[][] predictionProbabilities, double[][] labeledProbs) {
/* 246 */     double[][] uncertainty = new double[predictionProbabilities.length][];
/* 247 */     int nbRows = predictionProbabilities.length;
/* 248 */     double[] frequencyProbs = countFrequency(getLabeledData(), getHierarchy());
/*     */     
/* 250 */     double[] threshold = getThresholdPerClass(frequencyProbs, labeledProbs, getLabeledData());
/*     */     
/* 252 */     for (int i = 0; i < nbRows; i++) {
/* 253 */       double[] probs = predictionProbabilities[i];
/* 254 */       for (int j = 0; j < probs.length; j++) {
/* 255 */         probs[j] = Math.abs(threshold[j] - probs[j]);
/*     */       }
/* 257 */       uncertainty[i] = probs;
/*     */     } 
/* 259 */     return uncertainty;
/*     */   }
/*     */   
/*     */   public double[][] getLabelUncertainty(double[][] predictionProbabilities) {
/* 263 */     double[][] uncertainty = new double[predictionProbabilities.length][predictionProbabilities.length];
/* 264 */     int nbRows = predictionProbabilities.length;
/*     */     
/* 266 */     for (int i = 0; i < nbRows; i++) {
/* 267 */       double[] probs = predictionProbabilities[i];
/* 268 */       for (int j = 0; j < probs.length; j++) {
/* 269 */         probs[j] = Math.abs(0.5D - probs[j]);
/*     */       }
/* 271 */       uncertainty[i] = probs;
/*     */     } 
/* 273 */     return uncertainty;
/*     */   }
/*     */   
/*     */   public double[][] getLabelUncertaintyHCAL(double[][] predictionProbabilities) {
/* 277 */     double[][] uncertainty = new double[predictionProbabilities.length][];
/* 278 */     int nbRows = predictionProbabilities.length;
/*     */     
/* 280 */     for (int i = 0; i < nbRows; i++) {
/* 281 */       double[] probs = predictionProbabilities[i];
/* 282 */       for (int j = 0; j < probs.length; j++) {
/* 283 */         probs[j] = Math.abs(probs[j] - 0.5D);
/* 284 */         if (probs[j] == 0.0D) {
/* 285 */           probs[j] = 0.0D;
/*     */         } else {
/* 287 */           probs[j] = 1.0D / probs[j];
/*     */         } 
/*     */       } 
/* 290 */       uncertainty[i] = probs;
/*     */     } 
/* 292 */     return uncertainty;
/*     */   }
/*     */   
/*     */   public double[] countFrequency(RowData rowData, ClassHierarchy h) {
/* 296 */     int nbRows = rowData.getNbRows();
/* 297 */     double[] frequency = new double[h.getTotal()];
/*     */     
/*     */     int index;
/*     */     
/* 301 */     for (index = 0; index < nbRows; index++) {
/* 302 */       DataTuple dataTuple = rowData.getTuple(index);
/* 303 */       ClassesTuple ct = (ClassesTuple)dataTuple.getObjVal(0);
/* 304 */       boolean[] vectorBooleanNodeAndAncestors = ct.getVectorBooleanNodeAndAncestors(h);
/* 305 */       for (int j = 0; j < vectorBooleanNodeAndAncestors.length; j++) {
/* 306 */         if (vectorBooleanNodeAndAncestors[j]) {
/* 307 */           frequency[j] = frequency[j] + 1.0D;
/*     */         }
/*     */       } 
/*     */     } 
/* 311 */     for (index = 0; index < h.getTotal(); index++) {
/* 312 */       frequency[index] = frequency[index] / nbRows;
/*     */     }
/*     */     
/* 315 */     return frequency;
/*     */   }
/*     */   
/*     */   private double[] getThresholdPerClass(double[] frequency, double[][] probs, RowData trainingData) {
/* 319 */     double[] thresholds = new double[frequency.length];
/* 320 */     for (int i = 0; i < frequency.length; i++) {
/* 321 */       LinkedList<Double> p = new LinkedList<>();
/* 322 */       String label = getLabels()[i];
/* 323 */       for (int j = 0; j < probs.length; j++) {
/* 324 */         DataTuple tuple = trainingData.getTuple(j);
/* 325 */         if (tuple.getOracleAnswer() == null) {
/* 326 */           ClassesTuple ct = (ClassesTuple)tuple.m_Objects[0];
/* 327 */           String[] labels = ct.toString().split("@");
/* 328 */           for (String tupleLabel : labels) {
/* 329 */             String[] labels2 = tupleLabel.split("/");
/* 330 */             String tupleLabel2 = "";
/* 331 */             for (String tupleLabel3 : labels2) {
/* 332 */               tupleLabel2 = tupleLabel2 + tupleLabel3;
/* 333 */               if (tupleLabel2.equals(label)) {
/* 334 */                 p.add(Double.valueOf(probs[j][i]));
/*     */                 break;
/*     */               } 
/* 337 */               tupleLabel2 = tupleLabel2 + "/";
/*     */             }
/*     */           
/*     */           }
/*     */         
/* 342 */         } else if (tuple.getOracleAnswer().getPositiveAnswers().contains(label)) {
/* 343 */           p.add(Double.valueOf(probs[j][i]));
/*     */         } 
/*     */       } 
/*     */       
/* 347 */       if (p.size() > 0) {
/* 348 */         Double index = Double.valueOf(Math.floor(frequency[i] * (p.size() - 1)));
/* 349 */         Collections.sort(p);
/* 350 */         thresholds[i] = ((Double)p.get(index.intValue())).doubleValue();
/*     */       }
/*     */       else {
/*     */         
/* 354 */         thresholds[i] = Double.MAX_VALUE;
/*     */       } 
/*     */     } 
/* 357 */     return thresholds;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\activelearning\algo\ClusActiveLearningAlgorithmHSC.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */