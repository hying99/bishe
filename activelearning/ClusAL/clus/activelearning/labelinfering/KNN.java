/*     */ package clus.activelearning.labelinfering;
/*     */ 
/*     */ import clus.Clus;
/*     */ import clus.activelearning.algo.ClusActiveLearningAlgorithm;
/*     */ import clus.activelearning.algo.ClusLabelInferingAlgorithm;
/*     */ import clus.activelearning.algo.ClusLabelPairFindingAlgorithm;
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
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
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
/*     */ public class KNN
/*     */   extends ClusLabelInferingAlgorithm
/*     */ {
/*     */   private final Clus m_Clus;
/*     */   private final boolean m_PredictNegative;
/*     */   private final boolean m_PredictPositive;
/*     */   private int m_K;
/*     */   
/*     */   public KNN(Clus clus, ClusLabelPairFindingAlgorithm lc, ClusActiveLearningAlgorithm al, boolean inferPositive, boolean inferNegative) {
/*  47 */     super(lc, al);
/*  48 */     this.m_Clus = clus;
/*  49 */     this.m_PredictNegative = inferNegative;
/*  50 */     this.m_PredictPositive = inferPositive;
/*     */   }
/*     */   
/*     */   private ClusModel createKNN(ClusRun clusRun) {
/*  54 */     initializeDistances(this.m_Clus.getSchema());
/*  55 */     KNNClassifier knn = new KNNClassifier(this.m_Clus);
/*  56 */     ClusModel clusmodel = knn.induceSingle(clusRun);
/*  57 */     return clusmodel;
/*     */   }
/*     */   
/*     */   private void initializeDistances(ClusSchema schema) {
/*  61 */     NominalBasicDistance nomDist = new NominalBasicDistance();
/*  62 */     NumericalBasicDistance numDist = new NumericalBasicDistance();
/*  63 */     ClusAttrType[] attrs = schema.getDescriptiveAttributes();
/*  64 */     for (ClusAttrType attr : attrs) {
/*  65 */       if (attr.getTypeIndex() == 0) {
/*  66 */         attr.setBasicDistance((BasicDistance)nomDist);
/*     */       } else {
/*  68 */         attr.setBasicDistance((BasicDistance)numDist);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void inferLabels(double[] predictions, String labelToBeInfered, OracleAnswer oracleAnswer) throws ClusException {
/*  75 */     ClusActiveLearningAlgorithm al = getActiveLearning();
/*  76 */     LinkedList<String> path = (LinkedList<String>)al.getPaths().get(labelToBeInfered);
/*  77 */     ClassHierarchy h = al.getHierarchy();
/*  78 */     ClusLabelInferingIteration iteration = getIteration();
/*  79 */     int batchSize = getLabelCorrelation().getLabelInferingBatchSize();
/*  80 */     for (String pathLabel : path) {
/*  81 */       if (iteration.fitBatchSize(batchSize)) {
/*  82 */         int labelIndex = getLabelIndex(pathLabel);
/*  83 */         iteration.increaseSelectedAmount();
/*  84 */         if (this.m_PredictPositive && predictions[labelIndex] == 1.0D) {
/*  85 */           oracleAnswer.addNewPositiveLabel(pathLabel);
/*  86 */           oracleAnswer.addPositiveInferedLabel(pathLabel); continue;
/*  87 */         }  if (this.m_PredictNegative) {
/*  88 */           oracleAnswer.addNewNegativeLabel(pathLabel, h);
/*  89 */           oracleAnswer.addNegativeInferedLabel(pathLabel, h);
/*     */           return;
/*     */         } 
/*     */         continue;
/*     */       } 
/*     */       return;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public RowData inferHMCMode(ClusRun clusRun) {
/* 100 */     RowData unlabeledDataset = getActiveLearning().getUnlabeledData();
/* 101 */     ClusModel clusmodel = createKNN(clusRun);
/* 102 */     ClusLabelPairFindingAlgorithm lc = getLabelCorrelation();
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 107 */     HashMap<Integer, DataTuple> infered = new HashMap<>();
/*     */     try {
/* 109 */       LinkedList<LabelIndex> labels = lc.getLabelPairs(clusRun);
/* 110 */       for (LabelIndex instance : labels) {
/* 111 */         DataTuple dataTuple = unlabeledDataset.getTupleByActiveIndex(instance.getIndex());
/* 112 */         OracleAnswer oracleAnswer = dataTuple.getOracleAnswer();
/* 113 */         infered.put(Integer.valueOf(instance.getIndex()), dataTuple);
/* 114 */         WHTDStatistic wthd = (WHTDStatistic)clusmodel.predictWeighted(dataTuple);
/* 115 */         double[] predictionProbs = wthd.getNumericPred();
/* 116 */         inferLabels(predictionProbs, instance.getLabel(), oracleAnswer);
/*     */       } 
/* 118 */     } catch (ClusException ex) {
/* 119 */       Logger.getLogger(KNN.class.getName()).log(Level.SEVERE, (String)null, (Throwable)ex);
/*     */     } 
/* 121 */     ArrayList<DataTuple> inferedDataset = new ArrayList<>(infered.values());
/* 122 */     return getInferedDataset(inferedDataset, unlabeledDataset);
/*     */   }
/*     */ 
/*     */   
/*     */   public RowData inferHSCMode(ClusModel[] models, ClusRun clusRun) {
/* 127 */     RowData unlabeledDataset = getActiveLearning().getUnlabeledData();
/* 128 */     KNNModel clusmodel = (KNNModel)createKNN(clusRun);
/* 129 */     ClusLabelPairFindingAlgorithm lc = getLabelCorrelation();
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 134 */     HashMap<Integer, DataTuple> infered = new HashMap<>();
/*     */     try {
/* 136 */       LinkedList<LabelIndex> labels = lc.getLabelPairsHSC(models);
/* 137 */       for (LabelIndex instance : labels) {
/* 138 */         DataTuple dataTuple = unlabeledDataset.getTupleByActiveIndex(instance.getIndex());
/* 139 */         OracleAnswer oracleAnswer = dataTuple.getOracleAnswer();
/* 140 */         infered.put(Integer.valueOf(instance.getIndex()), dataTuple);
/* 141 */         DataTuple inferedTuple = clusmodel.infer(dataTuple, instance.getLabel());
/* 142 */         if (inferedTuple.getOracleAnswer() == null) {
/*     */           
/* 144 */           WHTDStatistic wthd = (WHTDStatistic)clusmodel.predictWeighted(dataTuple);
/* 145 */           double[] predictionProbs = wthd.getNumericPred();
/* 146 */           inferLabels(predictionProbs, instance.getLabel(), oracleAnswer);
/*     */           continue;
/*     */         } 
/* 149 */         OracleAnswer inferedAnswer = inferedTuple.getOracleAnswer();
/* 150 */         inferLabels(inferedAnswer, instance.getLabel(), oracleAnswer);
/*     */       }
/*     */     
/* 153 */     } catch (ClusException ex) {
/* 154 */       Logger.getLogger(KNN.class.getName()).log(Level.SEVERE, (String)null, (Throwable)ex);
/*     */     } 
/* 156 */     ArrayList<DataTuple> inferedDataset = new ArrayList<>(infered.values());
/* 157 */     return getInferedDataset(inferedDataset, unlabeledDataset);
/*     */   }
/*     */   
/*     */   private void inferLabels(OracleAnswer inferedAnswer, String labelToBeInfered, OracleAnswer oracleAnswer) throws ClusException {
/* 161 */     ClusActiveLearningAlgorithm al = getActiveLearning();
/* 162 */     LinkedList<String> path = (LinkedList<String>)al.getPaths().get(labelToBeInfered);
/* 163 */     ClassHierarchy h = al.getHierarchy();
/* 164 */     ClusLabelInferingIteration iteration = getIteration();
/* 165 */     int batchSize = getLabelCorrelation().getLabelInferingBatchSize();
/* 166 */     HashSet<String> positiveAnswers = inferedAnswer.getPositiveAnswers();
/*     */     
/* 168 */     for (String pathLabel : path) {
/* 169 */       if (iteration.fitBatchSize(batchSize)) {
/*     */         
/* 171 */         iteration.increaseSelectedAmount();
/* 172 */         if (positiveAnswers.contains(pathLabel) && this.m_PredictPositive) {
/* 173 */           oracleAnswer.addNewPositiveLabel(pathLabel);
/* 174 */           oracleAnswer.addPositiveInferedLabel(pathLabel); continue;
/* 175 */         }  if (this.m_PredictNegative) {
/* 176 */           oracleAnswer.addNewNegativeLabel(pathLabel, h);
/* 177 */           oracleAnswer.addNegativeInferedLabel(pathLabel, h);
/*     */           break;
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getK() {
/* 190 */     return this.m_K;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setK(int m_K) {
/* 197 */     this.m_K = m_K;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\activelearning\labelinfering\KNN.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */