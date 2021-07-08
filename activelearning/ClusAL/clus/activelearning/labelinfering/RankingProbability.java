/*     */ package clus.activelearning.labelinfering;
/*     */ 
/*     */ import clus.activelearning.algo.ClusActiveLearningAlgorithm;
/*     */ import clus.activelearning.algo.ClusActiveLearningAlgorithmHSC;
/*     */ import clus.activelearning.algo.ClusLabelInferingAlgorithm;
/*     */ import clus.activelearning.algo.ClusLabelPairFindingAlgorithm;
/*     */ import clus.activelearning.data.OracleAnswer;
/*     */ import clus.activelearning.indexing.LabelIndex;
/*     */ import clus.activelearning.iteration.ClusLabelInferingIteration;
/*     */ import clus.data.rows.DataTuple;
/*     */ import clus.data.rows.RowData;
/*     */ import clus.ext.hierarchical.ClassHierarchy;
/*     */ import clus.ext.hierarchical.ClassesTuple;
/*     */ import clus.ext.hierarchical.WHTDStatistic;
/*     */ import clus.main.ClusRun;
/*     */ import clus.model.ClusModel;
/*     */ import clus.util.ClusException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
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
/*     */ public class RankingProbability
/*     */   extends ClusLabelInferingAlgorithm
/*     */ {
/*     */   private final boolean m_PredictNegative;
/*     */   private final boolean m_PredictPositive;
/*     */   
/*     */   public RankingProbability(ClusLabelPairFindingAlgorithm lc, ClusActiveLearningAlgorithm al, boolean positive, boolean negative) {
/*  42 */     super(lc, al);
/*  43 */     this.m_PredictNegative = negative;
/*  44 */     this.m_PredictPositive = positive;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final double[] countLabels(RowData rowData, ClassHierarchy h) {
/*  53 */     int nbRows = rowData.getNbRows();
/*     */ 
/*     */ 
/*     */     
/*  57 */     double[] totalLabels = new double[2];
/*  58 */     for (int index = 0; index < nbRows; index++) {
/*  59 */       double labelsCount = 0.0D;
/*     */       
/*  61 */       DataTuple dataTuple = rowData.getTuple(index);
/*  62 */       ClassesTuple ct = (ClassesTuple)dataTuple.getObjVal(0);
/*  63 */       boolean[] vectorBooleanNodeAndAncestors = ct.getVectorBooleanNodeAndAncestors(h);
/*  64 */       for (int j = 0; j < vectorBooleanNodeAndAncestors.length; j++) {
/*  65 */         if (vectorBooleanNodeAndAncestors[j]) {
/*  66 */           labelsCount++;
/*     */         }
/*     */       } 
/*  69 */       totalLabels[0] = totalLabels[0] + labelsCount;
/*  70 */       totalLabels[1] = totalLabels[1] + labelsCount / h.getTotal();
/*     */     } 
/*     */ 
/*     */     
/*  74 */     return totalLabels;
/*     */   }
/*     */   
/*     */   private void inferLabels(double[] probs, String labelToBeInfered, double[] thresholds, OracleAnswer oracleAnswer) throws ClusException {
/*  78 */     ClusActiveLearningAlgorithm al = getActiveLearning();
/*  79 */     LinkedList<String> path = (LinkedList<String>)getActiveLearning().getPaths().get(labelToBeInfered);
/*  80 */     ClassHierarchy h = al.getHierarchy();
/*  81 */     ClusLabelInferingIteration iteration = getIteration();
/*  82 */     int batchSize = getLabelCorrelation().getLabelInferingBatchSize();
/*  83 */     for (String pathLabel : path) {
/*  84 */       if (iteration.fitBatchSize(batchSize)) {
/*  85 */         if (!oracleAnswer.queriedBefore(pathLabel)) {
/*  86 */           iteration.increaseSelectedAmount();
/*  87 */           int labelIndex = getLabelIndex(pathLabel);
/*     */ 
/*     */           
/*  90 */           if (this.m_PredictPositive) {
/*  91 */             if (probs[labelIndex] > thresholds[labelIndex]) {
/*  92 */               oracleAnswer.addNewPositiveLabel(pathLabel);
/*  93 */               oracleAnswer.addPositiveInferedLabel(pathLabel);
/*     */             }  continue;
/*     */           } 
/*  96 */           if (this.m_PredictNegative && 
/*  97 */             probs[labelIndex] <= thresholds[labelIndex]) {
/*  98 */             oracleAnswer.addNewNegativeLabel(pathLabel, h);
/*  99 */             oracleAnswer.addNegativeInferedLabel(pathLabel, h);
/*     */             return;
/*     */           } 
/*     */         } 
/*     */         continue;
/*     */       } 
/*     */       return;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public RowData inferHMCMode(ClusRun clusRun) {
/*     */     double[] frequencyProbs;
/* 115 */     ClusActiveLearningAlgorithm al = getActiveLearning();
/* 116 */     RowData unlabeledDataset = al.getUnlabeledData();
/* 117 */     int nbRows = unlabeledDataset.getNbRows();
/* 118 */     ClassHierarchy h = al.getHierarchy();
/* 119 */     int numClasses = h.getTotal();
/*     */     
/* 121 */     if (unlabeledDataset.getNbRows() > 0) {
/* 122 */       frequencyProbs = countFrequency((RowData)clusRun.getTrainingSet(), h);
/*     */     } else {
/* 124 */       return new RowData(clusRun.getStatManager().getSchema());
/*     */     } 
/*     */ 
/*     */     
/* 128 */     double[][] probs = new double[nbRows][numClasses];
/* 129 */     ClusModel predictionModel = clusRun.getModel(1);
/*     */     
/* 131 */     for (int i = 0; i < nbRows; i++) {
/* 132 */       DataTuple dataTuple = unlabeledDataset.getTuple(i);
/* 133 */       WHTDStatistic prob = (WHTDStatistic)predictionModel.predictWeighted(dataTuple);
/* 134 */       probs[i] = prob.getNumericPred();
/*     */     } 
/*     */     
/* 137 */     double[] threshold = getThresholdPerClass(frequencyProbs, probs, (RowData)clusRun.getTrainingSet());
/* 138 */     ClusLabelPairFindingAlgorithm lc = getLabelCorrelation();
/*     */ 
/*     */     
/* 141 */     HashMap<Integer, DataTuple> infered = new HashMap<>();
/*     */ 
/*     */     
/*     */     try {
/* 145 */       LinkedList<LabelIndex> indexer = lc.getLabelPairs(clusRun);
/* 146 */       for (LabelIndex instance : indexer) {
/* 147 */         DataTuple dataTuple = unlabeledDataset.getTupleByActiveIndex(instance.getIndex());
/* 148 */         OracleAnswer oracleAnswer = dataTuple.getOracleAnswer();
/* 149 */         infered.put(Integer.valueOf(instance.getIndex()), dataTuple);
/* 150 */         WHTDStatistic wthd = (WHTDStatistic)predictionModel.predictWeighted(dataTuple);
/* 151 */         double[] predictionProbs = wthd.getNumericPred();
/* 152 */         inferLabels(predictionProbs, instance.getLabel(), threshold, oracleAnswer);
/*     */       }
/*     */     
/* 155 */     } catch (ClusException ex) {
/* 156 */       Logger.getLogger(RankingProbability.class
/* 157 */           .getName()).log(Level.SEVERE, (String)null, (Throwable)ex);
/*     */     } 
/* 159 */     ArrayList<DataTuple> inferedDataset = new ArrayList<>(infered.values());
/* 160 */     return getInferedDataset(inferedDataset, unlabeledDataset);
/*     */   }
/*     */ 
/*     */   
/*     */   public RowData inferHSCMode(ClusModel[] models, ClusRun clusRun) {
/*     */     double[] frequencyProbs;
/* 166 */     ClusActiveLearningAlgorithmHSC al = (ClusActiveLearningAlgorithmHSC)getActiveLearning();
/*     */     
/* 168 */     RowData unlabeledDataset = al.getUnlabeledData();
/* 169 */     ClassHierarchy h = al.getHierarchy();
/*     */ 
/*     */     
/* 172 */     if (unlabeledDataset.getNbRows() > 0) {
/* 173 */       frequencyProbs = countFrequency((RowData)clusRun.getTrainingSet(), h);
/*     */     } else {
/* 175 */       return new RowData(clusRun.getStatManager().getSchema());
/*     */     } 
/*     */     
/* 178 */     ClusLabelPairFindingAlgorithm lc = getLabelCorrelation();
/*     */ 
/*     */     
/* 181 */     HashMap<Integer, DataTuple> infered = new HashMap<>();
/*     */     
/*     */     try {
/* 184 */       LinkedList<LabelIndex> indexer = lc.getLabelPairsHSC(models);
/*     */       
/* 186 */       double[][] probs = al.getPredictionProbabilities(models, (RowData)clusRun.getTrainingSet());
/* 187 */       double[] threshold = getThresholdPerClass(frequencyProbs, probs, (RowData)clusRun.getTrainingSet());
/*     */       
/* 189 */       for (LabelIndex instance : indexer) {
/* 190 */         DataTuple dataTuple = unlabeledDataset.getTupleByActiveIndex(instance.getIndex());
/* 191 */         OracleAnswer oracleAnswer = dataTuple.getOracleAnswer();
/* 192 */         infered.put(Integer.valueOf(instance.getIndex()), dataTuple);
/* 193 */         ArrayList<DataTuple> tuples = new ArrayList<>();
/* 194 */         tuples.add(dataTuple);
/* 195 */         RowData data = new RowData(tuples, clusRun.getStatManager().getSchema());
/*     */         
/* 197 */         double[] predictionProbs = al.getPredictionProbabilities(models, data)[0];
/*     */         
/* 199 */         inferLabels(predictionProbs, instance.getLabel(), threshold, oracleAnswer);
/*     */       }
/*     */     
/* 202 */     } catch (ClusException|java.io.IOException|ClassNotFoundException ex) {
/* 203 */       Logger.getLogger(RankingProbability.class
/* 204 */           .getName()).log(Level.SEVERE, (String)null, ex);
/*     */     } 
/* 206 */     ArrayList<DataTuple> inferedDataset = new ArrayList<>(infered.values());
/* 207 */     return getInferedDataset(inferedDataset, unlabeledDataset);
/*     */   }
/*     */   
/*     */   public double[] countFrequency(RowData rowData, ClassHierarchy h) {
/* 211 */     int nbRows = rowData.getNbRows();
/* 212 */     double[] frequency = new double[h.getTotal()];
/*     */     
/*     */     int index;
/*     */     
/* 216 */     for (index = 0; index < nbRows; index++) {
/* 217 */       DataTuple dataTuple = rowData.getTuple(index);
/* 218 */       ClassesTuple ct = (ClassesTuple)dataTuple.getObjVal(0);
/* 219 */       boolean[] vectorBooleanNodeAndAncestors = ct.getVectorBooleanNodeAndAncestors(h);
/* 220 */       for (int j = 0; j < vectorBooleanNodeAndAncestors.length; j++) {
/* 221 */         if (vectorBooleanNodeAndAncestors[j]) {
/* 222 */           frequency[j] = frequency[j] + 1.0D;
/*     */         }
/*     */       } 
/*     */     } 
/* 226 */     for (index = 0; index < h.getTotal(); index++) {
/* 227 */       frequency[index] = frequency[index] / nbRows;
/*     */     }
/*     */     
/* 230 */     return frequency;
/*     */   }
/*     */ 
/*     */   
/*     */   private double[] getThresholdPerClass(double[] frequency, double[][] probs, RowData trainingData) {
/* 235 */     double[] thresholds = new double[frequency.length];
/* 236 */     for (int i = 0; i < frequency.length; i++) {
/* 237 */       LinkedList<Double> p = new LinkedList<>();
/* 238 */       String label = getActiveLearning().getLabels()[i];
/* 239 */       for (int j = 0; j < probs.length; j++) {
/* 240 */         DataTuple tuple = trainingData.getTuple(j);
/* 241 */         if (tuple.getOracleAnswer() == null) {
/* 242 */           ClassesTuple ct = (ClassesTuple)tuple.m_Objects[0];
/* 243 */           String[] labels = ct.toString().split("@");
/* 244 */           for (String tupleLabel : labels) {
/* 245 */             String[] labels2 = tupleLabel.split("/");
/* 246 */             String tupleLabel2 = "";
/* 247 */             for (String tupleLabel3 : labels2) {
/* 248 */               tupleLabel2 = tupleLabel2 + tupleLabel3;
/* 249 */               if (tupleLabel2.equals(label)) {
/* 250 */                 p.add(Double.valueOf(probs[j][i]));
/*     */                 break;
/*     */               } 
/* 253 */               tupleLabel2 = tupleLabel2 + "/";
/*     */             }
/*     */           
/*     */           }
/*     */         
/* 258 */         } else if (tuple.getOracleAnswer().getPositiveAnswers().contains(label)) {
/* 259 */           p.add(Double.valueOf(probs[j][i]));
/*     */         } 
/*     */       } 
/*     */       
/* 263 */       if (p.size() > 0) {
/* 264 */         Double index = Double.valueOf(Math.floor(frequency[i] * p.size()));
/* 265 */         Collections.sort(p);
/* 266 */         thresholds[i] = ((Double)p.get(index.intValue())).doubleValue();
/*     */       } else {
/* 268 */         thresholds[i] = Double.MAX_VALUE;
/*     */       } 
/*     */     } 
/* 271 */     return thresholds;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\activelearning\labelinfering\RankingProbability.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */