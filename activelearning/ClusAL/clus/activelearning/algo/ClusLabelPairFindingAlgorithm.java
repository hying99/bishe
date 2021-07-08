/*     */ package clus.activelearning.algo;
/*     */ 
/*     */ import clus.activelearning.data.OracleAnswer;
/*     */ import clus.activelearning.indexing.LabelIndex;
/*     */ import clus.activelearning.indexing.LabelIndexer;
/*     */ import clus.data.rows.DataTuple;
/*     */ import clus.main.ClusRun;
/*     */ import clus.model.ClusModel;
/*     */ import clus.util.ClusException;
/*     */ import java.util.HashSet;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class ClusLabelPairFindingAlgorithm
/*     */ {
/*     */   private int m_LabelInferingBatchSize;
/*     */   private ClusActiveLearningAlgorithm m_ActiveLearningAlgorithm;
/*     */   
/*     */   public ClusLabelPairFindingAlgorithm(ClusActiveLearningAlgorithm al, int batchSize) {
/*  31 */     this.m_LabelInferingBatchSize = batchSize;
/*  32 */     this.m_ActiveLearningAlgorithm = al;
/*     */   }
/*     */   
/*     */   public abstract LinkedList<LabelIndex> getLabelPairs(ClusRun paramClusRun) throws ClusException;
/*     */   
/*     */   public abstract LinkedList<LabelIndex> getLabelPairsHSC(ClusModel[] paramArrayOfClusModel) throws ClusException;
/*     */   
/*     */   protected LinkedList<LabelIndex> buildLabelMaxIndexer(double[][] labelsVariance) throws ClusException {
/*  40 */     LabelIndexer labelIndexer = new LabelIndexer(getLabelInferingBatchSize());
/*  41 */     ClusActiveLearningAlgorithm al = getActiveLearningAlgorithm();
/*     */ 
/*     */ 
/*     */     
/*  45 */     for (int i = 0; i < labelsVariance.length; i++) {
/*  46 */       DataTuple tuple = al.getUnlabeledData().getTuple(i);
/*  47 */       OracleAnswer answer = tuple.getOracleAnswer();
/*  48 */       for (int j = 0; j < (labelsVariance[i]).length; j++) {
/*  49 */         String label = al.getLabels()[j];
/*  50 */         if (!answer.queriedBefore(label)) {
/*  51 */           labelIndexer.addMax(tuple.m_ActiveIndex, label, labelsVariance[i][j]);
/*     */         }
/*     */       } 
/*     */     } 
/*  55 */     return labelIndexer.getIndexer();
/*     */   }
/*     */   
/*     */   protected LinkedList<LabelIndex> buildLabelMinIndexer(double[][] labelsVariance) throws ClusException {
/*  59 */     LabelIndexer labelIndexer = new LabelIndexer(getLabelInferingBatchSize());
/*  60 */     ClusActiveLearningAlgorithm al = getActiveLearningAlgorithm();
/*     */ 
/*     */ 
/*     */     
/*  64 */     for (int i = 0; i < labelsVariance.length; i++) {
/*  65 */       DataTuple tuple = al.getUnlabeledData().getTuple(i);
/*  66 */       OracleAnswer answer = tuple.getOracleAnswer();
/*  67 */       for (int j = 0; j < (labelsVariance[i]).length; j++) {
/*  68 */         String label = al.getLabels()[j];
/*  69 */         if (!answer.queriedBefore(label)) {
/*  70 */           labelIndexer.addMin(tuple.m_ActiveIndex, label, labelsVariance[i][j]);
/*     */         }
/*     */       } 
/*     */     } 
/*  74 */     return labelIndexer.getIndexer();
/*     */   }
/*     */   
/*     */   protected LinkedList<LabelIndex> buildHierarchyOrientedLabelMinIndexer(double[][] labelsVariance) throws ClusException {
/*  78 */     LabelIndexer labelIndexer = new LabelIndexer(getLabelInferingBatchSize());
/*  79 */     ClusActiveLearningAlgorithm al = getActiveLearningAlgorithm();
/*     */ 
/*     */ 
/*     */     
/*  83 */     for (int i = 0; i < labelsVariance.length; i++) {
/*  84 */       DataTuple tuple = al.getUnlabeledData().getTuple(i);
/*  85 */       OracleAnswer answer = tuple.getOracleAnswer();
/*  86 */       HashSet<String> children = answer.getPositiveChildren(al.getHierarchy());
/*     */       
/*  88 */       for (int j = 0; j < (labelsVariance[i]).length; j++) {
/*  89 */         String label = al.getLabels()[j];
/*  90 */         if (!answer.queriedBefore(label) && children.contains(label))
/*     */         {
/*     */ 
/*     */           
/*  94 */           labelIndexer.addMin(tuple.m_ActiveIndex, label, labelsVariance[i][j]);
/*     */         }
/*     */       } 
/*     */     } 
/*     */     
/*  99 */     return labelIndexer.getIndexer();
/*     */   }
/*     */   
/*     */   protected LinkedList<LabelIndex> buildHierarchyOrientedLabelMaxIndexer(double[][] labelsVariance) throws ClusException {
/* 103 */     LabelIndexer labelIndexer = new LabelIndexer(getLabelInferingBatchSize());
/* 104 */     ClusActiveLearningAlgorithm al = getActiveLearningAlgorithm();
/*     */ 
/*     */ 
/*     */     
/* 108 */     for (int i = 0; i < labelsVariance.length; i++) {
/* 109 */       DataTuple tuple = al.getUnlabeledData().getTuple(i);
/* 110 */       OracleAnswer answer = tuple.getOracleAnswer();
/* 111 */       HashSet<String> children = answer.getPositiveChildren(al.getHierarchy());
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 116 */       for (int j = 0; j < (labelsVariance[i]).length; j++) {
/* 117 */         String label = al.getLabels()[j];
/* 118 */         if (!answer.queriedBefore(label) && children.contains(label)) {
/* 119 */           labelIndexer.addMax(tuple.m_ActiveIndex, label, labelsVariance[i][j]);
/*     */         }
/*     */       } 
/*     */     } 
/*     */     
/* 124 */     return labelIndexer.getIndexer();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getLabelInferingBatchSize() {
/* 131 */     return this.m_LabelInferingBatchSize;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setLabelInferingBatchSize(int m_LabelInferingBatchSize) {
/* 138 */     this.m_LabelInferingBatchSize = m_LabelInferingBatchSize;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ClusActiveLearningAlgorithm getActiveLearningAlgorithm() {
/* 145 */     return this.m_ActiveLearningAlgorithm;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setActiveLearningAlgorithm(ClusActiveLearningAlgorithm m_ActiveLearningAlgorithm) {
/* 152 */     this.m_ActiveLearningAlgorithm = m_ActiveLearningAlgorithm;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\activelearning\algo\ClusLabelPairFindingAlgorithm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */