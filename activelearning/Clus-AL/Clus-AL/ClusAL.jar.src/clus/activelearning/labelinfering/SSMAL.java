/*     */ package clus.activelearning.labelinfering;
/*     */ 
/*     */ import clus.Clus;
/*     */ import clus.activelearning.algo.ClusActiveLearningAlgorithm;
/*     */ import clus.activelearning.algo.ClusActiveLearningAlgorithmHSC;
/*     */ import clus.activelearning.algo.ClusLabelInferingAlgorithm;
/*     */ import clus.activelearning.algo.ClusLabelPairFindingAlgorithm;
/*     */ import clus.activelearning.data.ClassCounterCorrelation;
/*     */ import clus.activelearning.data.OracleAnswer;
/*     */ import clus.activelearning.indexing.LabelIndex;
/*     */ import clus.activelearning.iteration.ClusLabelInferingIteration;
/*     */ import clus.algo.kNN.BasicDistance;
/*     */ import clus.algo.kNN.KNNClassifier;
/*     */ import clus.algo.kNN.KNNModel;
/*     */ import clus.algo.kNN.NominalBasicDistance;
/*     */ import clus.algo.kNN.NumericalBasicDistance;
/*     */ import clus.data.rows.DataTuple;
/*     */ import clus.data.rows.RowData;
/*     */ import clus.data.type.ClusAttrType;
/*     */ import clus.data.type.ClusSchema;
/*     */ import clus.ext.hierarchical.ClassHierarchy;
/*     */ import clus.ext.hierarchical.WHTDStatistic;
/*     */ import clus.main.ClusRun;
/*     */ import clus.model.ClusModel;
/*     */ import clus.util.ClusException;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
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
/*     */ public class SSMAL
/*     */   extends ClusLabelInferingAlgorithm
/*     */ {
/*     */   private double[][] m_Correlation;
/*     */   private Clus m_Clus;
/*     */   private boolean m_PredictNegative;
/*     */   private boolean m_PredictPositive;
/*     */   
/*     */   public SSMAL(Clus clus, ClusLabelPairFindingAlgorithm lc, ClusActiveLearningAlgorithm al, boolean positive, boolean negative) {
/*  51 */     super(lc, al);
/*  52 */     this.m_Clus = clus;
/*  53 */     this.m_PredictNegative = negative;
/*  54 */     this.m_PredictPositive = positive;
/*     */   }
/*     */   
/*     */   public RowData infer(RowData unlabeledDataset, Clus clus, ClusRun clusRun) {
/*  58 */     ClusLabelPairFindingAlgorithm lc = getLabelCorrelation();
/*  59 */     LinkedList<LabelIndex> labels = null;
/*     */     
/*  61 */     HashMap<Integer, DataTuple> infered = new HashMap<>();
/*  62 */     this.m_Correlation = getLabelCorrelation((RowData)clusRun.getTrainingSet());
/*     */     try {
/*  64 */       labels = lc.getLabelPairs(clusRun);
/*  65 */     } catch (ClusException ex) {
/*  66 */       Logger.getLogger(SSMAL.class.getName()).log(Level.SEVERE, (String)null, (Throwable)ex);
/*     */     } 
/*  68 */     for (LabelIndex instance : labels) {
/*  69 */       DataTuple dataTuple = unlabeledDataset.getTupleByActiveIndex(instance.getIndex());
/*  70 */       infered.put(Integer.valueOf(instance.getIndex()), dataTuple);
/*     */       try {
/*  72 */         inferLabelsHMC(dataTuple, clus, clusRun, instance.getLabel());
/*  73 */       } catch (ClusException ex) {
/*  74 */         Logger.getLogger(SSMAL.class.getName()).log(Level.SEVERE, (String)null, (Throwable)ex);
/*     */       } 
/*     */     } 
/*  77 */     ArrayList<DataTuple> inferedDataset = new ArrayList<>(infered.values());
/*  78 */     return getInferedDataset(inferedDataset, unlabeledDataset);
/*     */   }
/*     */ 
/*     */   
/*     */   private void inferLabelsHMC(DataTuple dataTuple, Clus clus, ClusRun cr, String labelToBeInfered) throws ClusException {
/*  83 */     ClusActiveLearningAlgorithm al = getActiveLearning();
/*  84 */     LinkedList<String> path = (LinkedList<String>)al.getPaths().get(labelToBeInfered);
/*  85 */     ClassHierarchy h = al.getHierarchy();
/*     */     
/*  87 */     ClusLabelInferingIteration iteration = getIteration();
/*  88 */     int batchSize = getLabelCorrelation().getLabelInferingBatchSize();
/*  89 */     for (String pathLabel : path) {
/*  90 */       OracleAnswer oracleAnswer = dataTuple.getOracleAnswer();
/*  91 */       if (iteration.fitBatchSize(batchSize) && !oracleAnswer.queriedBefore(pathLabel)) {
/*  92 */         int labelIndex = getLabelIndex(pathLabel);
/*  93 */         boolean uncertaintyVote = getUncertaintyVote(dataTuple, cr, labelIndex);
/*  94 */         int correlationVote = getCorrelationVote(labelIndex, oracleAnswer);
/*  95 */         boolean knnVote = getKNNVote(labelIndex, pathLabel, dataTuple, clus, cr);
/*  96 */         boolean positiveOrNegativeAnswer = false;
/*  97 */         if (correlationVote == 0) {
/*  98 */           if (uncertaintyVote == knnVote) {
/*  99 */             positiveOrNegativeAnswer = uncertaintyVote;
/*     */           }
/*     */         } else {
/* 102 */           boolean correlationBooleanVote = (correlationVote == 1);
/* 103 */           boolean votingCommitteeAnswer = votingCommittee(uncertaintyVote, correlationBooleanVote, knnVote);
/* 104 */           positiveOrNegativeAnswer = votingCommitteeAnswer;
/*     */         } 
/* 106 */         iteration.increaseSelectedAmount();
/* 107 */         if (positiveOrNegativeAnswer) {
/* 108 */           if (this.m_PredictPositive)
/* 109 */             oracleAnswer.addNewPositiveLabel(pathLabel);  continue;
/*     */         } 
/* 111 */         if (this.m_PredictNegative) {
/* 112 */           oracleAnswer.addNewNegativeLabel(pathLabel, h);
/*     */           return;
/*     */         } 
/*     */         continue;
/*     */       } 
/*     */       return;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void inferLabelsHSC(DataTuple dataTuple, Clus clus, ClusRun cr, ClusModel[] models, String labelToBeInfered) throws ClusException, ClassNotFoundException, IOException {
/* 123 */     ClusActiveLearningAlgorithmHSC al = (ClusActiveLearningAlgorithmHSC)getActiveLearning();
/* 124 */     LinkedList<String> path = (LinkedList<String>)al.getPaths().get(labelToBeInfered);
/* 125 */     ClassHierarchy h = al.getHierarchy();
/*     */     
/* 127 */     ClusLabelInferingIteration iteration = getIteration();
/* 128 */     int batchSize = getLabelCorrelation().getLabelInferingBatchSize();
/* 129 */     for (String pathLabel : path) {
/* 130 */       OracleAnswer oracleAnswer = dataTuple.getOracleAnswer();
/* 131 */       if (iteration.fitBatchSize(batchSize)) {
/* 132 */         if (!oracleAnswer.queriedBefore(pathLabel)) {
/* 133 */           int labelIndex = getLabelIndex(pathLabel);
/* 134 */           ArrayList<DataTuple> tuples = new ArrayList<>();
/* 135 */           tuples.add(dataTuple);
/* 136 */           RowData data = new RowData(tuples, clus.getSchema());
/* 137 */           double[][] probs = al.getPredictionProbabilities(models, data);
/*     */           
/* 139 */           boolean uncertaintyVote = getUncertaintyVoteHSC(probs, labelIndex);
/* 140 */           int correlationVote = getCorrelationVote(labelIndex, oracleAnswer);
/* 141 */           boolean knnVote = getKNNVote(labelIndex, pathLabel, dataTuple, clus, cr);
/* 142 */           boolean positiveOrNegativeAnswer = false;
/* 143 */           if (correlationVote == 0) {
/* 144 */             if (uncertaintyVote == knnVote) {
/* 145 */               positiveOrNegativeAnswer = uncertaintyVote;
/*     */             }
/*     */           } else {
/* 148 */             boolean correlationBooleanVote = (correlationVote == 1);
/* 149 */             boolean votingCommitteeAnswer = votingCommittee(uncertaintyVote, correlationBooleanVote, knnVote);
/* 150 */             positiveOrNegativeAnswer = votingCommitteeAnswer;
/*     */           } 
/* 152 */           iteration.increaseSelectedAmount();
/* 153 */           if (positiveOrNegativeAnswer) {
/* 154 */             oracleAnswer.addNewPositiveLabel(pathLabel);
/* 155 */             oracleAnswer.addPositiveInferedLabel(pathLabel);
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
/*     */ 
/*     */   
/*     */   private boolean votingCommittee(boolean uncertaintyVote, boolean correlationVote, boolean knnVote) {
/* 171 */     int positiveCounter = 0;
/* 172 */     int negativeCounter = 0;
/* 173 */     if (uncertaintyVote) {
/* 174 */       positiveCounter++;
/*     */     } else {
/* 176 */       negativeCounter++;
/*     */     } 
/* 178 */     if (correlationVote) {
/* 179 */       positiveCounter++;
/*     */     } else {
/* 181 */       negativeCounter++;
/*     */     } 
/* 183 */     if (knnVote) {
/* 184 */       positiveCounter++;
/* 185 */       negativeCounter++;
/*     */     } 
/* 187 */     return (positiveCounter > negativeCounter);
/*     */   }
/*     */ 
/*     */   
/*     */   private boolean getUncertaintyVote(DataTuple dataTuple, ClusRun cr, int labelIndex) {
/* 192 */     WHTDStatistic wthd = (WHTDStatistic)cr.getModel(1).predictWeighted(dataTuple);
/* 193 */     double[] probs = wthd.getNumericPred();
/* 194 */     return (probs[labelIndex] >= 0.5D);
/*     */   }
/*     */   
/*     */   private boolean getUncertaintyVoteHSC(double[][] probs, int labelIndex) {
/* 198 */     return (probs[0][labelIndex] >= 0.5D);
/*     */   }
/*     */   
/*     */   private boolean getKNNVote(int labelIndex, String label, DataTuple dataTuple, Clus clus, ClusRun clusRun) {
/* 202 */     initializeDistances(clus.getSchema());
/* 203 */     KNNClassifier knn = new KNNClassifier(clus);
/* 204 */     KNNModel clusmodel = (KNNModel)knn.induceSingle(clusRun);
/* 205 */     DataTuple inferedTuple = clusmodel.infer(dataTuple, label);
/* 206 */     if (inferedTuple.getOracleAnswer() == null) {
/*     */       
/* 208 */       double[] probs = clusmodel.predictWeighted(dataTuple).getNumericPred();
/* 209 */       return (probs[labelIndex] == 1.0D);
/*     */     } 
/*     */     
/* 212 */     OracleAnswer inferedAnswer = inferedTuple.getOracleAnswer();
/* 213 */     return inferedAnswer.getPositiveAnswers().contains(label);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void initializeDistances(ClusSchema schema) {
/* 219 */     NominalBasicDistance nomDist = new NominalBasicDistance();
/* 220 */     NumericalBasicDistance numDist = new NumericalBasicDistance();
/* 221 */     ClusAttrType[] attrs = schema.getDescriptiveAttributes();
/* 222 */     for (int i = 0; i < attrs.length; i++) {
/* 223 */       if (attrs[i].getTypeIndex() == 0) {
/* 224 */         attrs[i].setBasicDistance((BasicDistance)nomDist);
/*     */       } else {
/* 226 */         attrs[i].setBasicDistance((BasicDistance)numDist);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private int getCorrelationVote(int labelIndex, OracleAnswer oracleAnswer) {
/* 234 */     double[] correlatedRow = this.m_Correlation[labelIndex];
/* 235 */     double maxValue = Math.abs(correlatedRow[0]);
/* 236 */     int maxIndex = 0;
/* 237 */     boolean positiveOrNegative = (correlatedRow[0] > 0.0D);
/* 238 */     for (int i = 1; i < correlatedRow.length; i++) {
/* 239 */       if (correlatedRow[i] > maxValue) {
/* 240 */         maxValue = Math.abs(correlatedRow[i]);
/* 241 */         maxIndex = i;
/* 242 */         positiveOrNegative = (correlatedRow[i] > 0.0D);
/*     */       } 
/*     */     } 
/*     */     
/* 246 */     String mostCorrelatedLabel = getActiveLearning().getLabels()[maxIndex];
/*     */     
/* 248 */     int vote = oracleAnswer.getSSMALvote(mostCorrelatedLabel, positiveOrNegative);
/* 249 */     return vote;
/*     */   }
/*     */   
/*     */   private double[][] getLabelCorrelation(RowData labeledData) {
/* 253 */     ClusActiveLearningAlgorithm al = getActiveLearning();
/* 254 */     ClassHierarchy h = al.getHierarchy();
/*     */     
/* 256 */     int hierarchySize = h.getTotal();
/* 257 */     double[][] correlation = new double[h.getTotal()][h.getTotal()];
/*     */ 
/*     */ 
/*     */     
/* 261 */     for (int i = 1; i < hierarchySize; i++) {
/* 262 */       for (int j = 0; j <= i; j++) {
/* 263 */         if (i == j) {
/* 264 */           correlation[i][j] = 0.0D;
/* 265 */           correlation[j][i] = 0.0D;
/*     */         } else {
/* 267 */           double correlationValue = measureCorrelationValue(labeledData, h, i, j);
/* 268 */           if (!Double.isNaN(correlationValue)) {
/* 269 */             correlation[i][j] = correlationValue;
/* 270 */             correlation[j][i] = correlationValue;
/*     */           } else {
/* 272 */             correlation[i][j] = 0.0D;
/* 273 */             correlation[j][i] = 0.0D;
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } 
/* 278 */     return correlation;
/*     */   }
/*     */ 
/*     */   
/*     */   private double measureCorrelationValue(RowData rowData, ClassHierarchy h, int i, int j) {
/* 283 */     ClassCounterCorrelation cc = new ClassCounterCorrelation(i, j);
/* 284 */     cc.count(rowData, h, i, i);
/* 285 */     double A = cc.getCounterA();
/* 286 */     double B = cc.getCounterB();
/* 287 */     double C = cc.getCounterC();
/* 288 */     double D = cc.getCounterD();
/* 289 */     double correlation = (A * D - B * C) / Math.sqrt((A + B) * (A + C) * (C + D) * (B + D));
/* 290 */     return correlation;
/*     */   }
/*     */ 
/*     */   
/*     */   public RowData inferHMCMode(ClusRun clusRun) {
/* 295 */     Clus clus = getM_Clus();
/* 296 */     RowData unlabeledDataset = getActiveLearning().getUnlabeledData();
/*     */     
/* 298 */     ClusLabelPairFindingAlgorithm lc = getLabelCorrelation();
/* 299 */     LinkedList<LabelIndex> labels = null;
/*     */     
/* 301 */     HashMap<Integer, DataTuple> infered = new HashMap<>();
/* 302 */     this.m_Correlation = getLabelCorrelation((RowData)clusRun.getTrainingSet());
/*     */     try {
/* 304 */       labels = lc.getLabelPairs(clusRun);
/* 305 */     } catch (ClusException ex) {
/* 306 */       Logger.getLogger(SSMAL.class.getName()).log(Level.SEVERE, (String)null, (Throwable)ex);
/*     */     } 
/* 308 */     for (LabelIndex instance : labels) {
/* 309 */       DataTuple dataTuple = unlabeledDataset.getTupleByActiveIndex(instance.getIndex());
/* 310 */       infered.put(Integer.valueOf(instance.getIndex()), dataTuple);
/*     */       try {
/* 312 */         inferLabelsHMC(dataTuple, clus, clusRun, instance.getLabel());
/* 313 */       } catch (ClusException ex) {
/* 314 */         Logger.getLogger(SSMAL.class.getName()).log(Level.SEVERE, (String)null, (Throwable)ex);
/*     */       } 
/*     */     } 
/* 317 */     ArrayList<DataTuple> inferedDataset = new ArrayList<>(infered.values());
/* 318 */     return getInferedDataset(inferedDataset, unlabeledDataset);
/*     */   }
/*     */ 
/*     */   
/*     */   public RowData inferHSCMode(ClusModel[] models, ClusRun clusRun) {
/* 323 */     Clus clus = getM_Clus();
/* 324 */     RowData unlabeledDataset = getActiveLearning().getUnlabeledData();
/*     */     
/* 326 */     ClusLabelPairFindingAlgorithm lc = getLabelCorrelation();
/* 327 */     LinkedList<LabelIndex> labels = null;
/*     */     
/* 329 */     HashMap<Integer, DataTuple> infered = new HashMap<>();
/* 330 */     this.m_Correlation = getLabelCorrelation((RowData)clusRun.getTrainingSet());
/*     */     try {
/* 332 */       labels = lc.getLabelPairsHSC(models);
/* 333 */     } catch (ClusException ex) {
/* 334 */       Logger.getLogger(SSMAL.class.getName()).log(Level.SEVERE, (String)null, (Throwable)ex);
/*     */     } 
/* 336 */     for (LabelIndex instance : labels) {
/* 337 */       DataTuple dataTuple = unlabeledDataset.getTupleByActiveIndex(instance.getIndex());
/* 338 */       infered.put(Integer.valueOf(instance.getIndex()), dataTuple);
/*     */       try {
/* 340 */         inferLabelsHSC(dataTuple, clus, clusRun, models, instance.getLabel());
/* 341 */       } catch (ClusException|ClassNotFoundException|IOException ex) {
/* 342 */         Logger.getLogger(SSMAL.class.getName()).log(Level.SEVERE, (String)null, ex);
/*     */       } 
/*     */     } 
/* 345 */     ArrayList<DataTuple> inferedDataset = new ArrayList<>(infered.values());
/* 346 */     return getInferedDataset(inferedDataset, unlabeledDataset);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Clus getM_Clus() {
/* 353 */     return this.m_Clus;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setM_Clus(Clus m_Clus) {
/* 360 */     this.m_Clus = m_Clus;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\activelearning\labelinfering\SSMAL.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */