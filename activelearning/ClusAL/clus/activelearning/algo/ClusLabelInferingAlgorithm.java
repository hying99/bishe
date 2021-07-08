/*     */ package clus.activelearning.algo;
/*     */ 
/*     */ import clus.activelearning.data.OracleAnswer;
/*     */ import clus.activelearning.iteration.ClusLabelInferingIteration;
/*     */ import clus.activelearning.iteration.Iteration;
/*     */ import clus.activelearning.labelinfering.KNN;
/*     */ import clus.data.rows.DataTuple;
/*     */ import clus.data.rows.RowData;
/*     */ import clus.ext.hierarchical.ClassHierarchy;
/*     */ import clus.main.ClusRun;
/*     */ import clus.model.ClusModel;
/*     */ import clus.util.ClusException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashSet;
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
/*     */ public abstract class ClusLabelInferingAlgorithm
/*     */ {
/*     */   private ClusLabelPairFindingAlgorithm m_LabelPairFinding;
/*     */   private ClusActiveLearningAlgorithm m_ActiveLearning;
/*     */   private ClusLabelInferingIteration m_Iteration;
/*     */   private int m_InferedLabels;
/*     */   
/*     */   public RowData inferHMC(ClusRun cr) {
/*  35 */     startIteration();
/*  36 */     RowData rowData = inferHMCMode(cr);
/*  37 */     finishIteration();
/*  38 */     return rowData;
/*     */   }
/*     */   
/*     */   public RowData inferHSC(ClusModel[] models, ClusRun cr) {
/*  42 */     startIteration();
/*  43 */     RowData rowData = inferHSCMode(models, cr);
/*  44 */     finishIteration();
/*  45 */     return rowData;
/*     */   }
/*     */   
/*     */   public abstract RowData inferHMCMode(ClusRun paramClusRun);
/*     */   
/*     */   public abstract RowData inferHSCMode(ClusModel[] paramArrayOfClusModel, ClusRun paramClusRun);
/*     */   
/*     */   public ClusLabelInferingAlgorithm(ClusLabelPairFindingAlgorithm lc, ClusActiveLearningAlgorithm al) {
/*  53 */     setLabelCorrelation(lc);
/*  54 */     setActiveLearning(al);
/*  55 */     setInferedLabels(0);
/*  56 */     startIteration();
/*     */   }
/*     */   
/*     */   public boolean isDone() {
/*  60 */     return getIteration().isDone();
/*     */   }
/*     */ 
/*     */   
/*     */   private void startIteration() {
/*  65 */     ClusLabelInferingIteration iteration = new ClusLabelInferingIteration(getActiveLearning().getLabelQueryCounter(), getActiveLearning().getAvaliableLabels());
/*  66 */     setIteration(iteration);
/*     */   }
/*     */   
/*     */   protected RowData getInferedDataset(ArrayList<DataTuple> dataTuples, RowData unlabeledDataset) {
/*  70 */     HashSet<Integer> removableIndexes = null;
/*  71 */     RowData inferedData = null;
/*     */     try {
/*  73 */       addInferedLabels(dataTuples);
/*  74 */       inferedData = new RowData(dataTuples, unlabeledDataset.getSchema());
/*     */       
/*  76 */       removableIndexes = getActiveLearning().finishAnswering(inferedData, (Iteration)getIteration());
/*  77 */     } catch (ClusException ex) {
/*  78 */       Logger.getLogger(KNN.class.getName()).log(Level.SEVERE, (String)null, (Throwable)ex);
/*     */     } 
/*  80 */     unlabeledDataset.removeRows(removableIndexes);
/*  81 */     return inferedData;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void addInferedLabels(ArrayList<DataTuple> dataTuples) throws ClusException {
/*  86 */     ClusActiveLearningAlgorithm al = getActiveLearning();
/*  87 */     ClassHierarchy h = al.getHierarchy();
/*  88 */     for (DataTuple datatuple : dataTuples) {
/*  89 */       OracleAnswer oracleAnswer = datatuple.getOracleAnswer();
/*     */       
/*  91 */       oracleAnswer.labelTuple(datatuple, h);
/*     */ 
/*     */       
/*  94 */       this.m_Iteration.addNewLabels(oracleAnswer.getNewLabels());
/*  95 */       this.m_Iteration.updateNegativeInfered(oracleAnswer.getNegativeInferedSize());
/*  96 */       this.m_Iteration.updatePositiveInfered(oracleAnswer.getPositiveInferedSize());
/*  97 */       this.m_Iteration.updatePositiveCorrectInfered(oracleAnswer.getCorrectPositiveInferedSize());
/*  98 */       this.m_Iteration.updateNegativeCorrectInfered(oracleAnswer.getCorrectNegativeInferedSize());
/*     */     } 
/*     */   }
/*     */   
/*     */   private void finishIteration() {
/* 103 */     ClusLabelInferingIteration iteration = getIteration();
/* 104 */     ClusActiveLearningAlgorithm al = getActiveLearning();
/* 105 */     setInferedLabels(getInferedLabels() + iteration.getLabelsQueried());
/* 106 */     al.setAvailableLabels(iteration.getLabelsAvailable());
/* 107 */     al.setLabelsQueried(al.getLabelsQueried());
/* 108 */     al.setLabelQueryCounter(iteration.getLabelCounter());
/*     */   }
/*     */   
/*     */   protected int getLabelIndex(String label) {
/* 112 */     String[] labels = getActiveLearning().getLabels();
/* 113 */     for (int i = 0; i < labels.length; i++) {
/* 114 */       if (labels[i].equals(label)) {
/* 115 */         return i;
/*     */       }
/*     */     } 
/* 118 */     return -1;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ClusLabelPairFindingAlgorithm getLabelCorrelation() {
/* 125 */     return this.m_LabelPairFinding;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void setLabelCorrelation(ClusLabelPairFindingAlgorithm m_LabelCorrelation) {
/* 132 */     this.m_LabelPairFinding = m_LabelCorrelation;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ClusActiveLearningAlgorithm getActiveLearning() {
/* 139 */     return this.m_ActiveLearning;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final void setActiveLearning(ClusActiveLearningAlgorithm m_ActiveLearning) {
/* 146 */     this.m_ActiveLearning = m_ActiveLearning;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ClusLabelInferingIteration getIteration() {
/* 153 */     return this.m_Iteration;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void setIteration(ClusLabelInferingIteration m_Iteration) {
/* 160 */     this.m_Iteration = m_Iteration;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getInferedLabels() {
/* 167 */     return this.m_InferedLabels;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final void setInferedLabels(int m_InferedLabels) {
/* 174 */     this.m_InferedLabels = m_InferedLabels;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\activelearning\algo\ClusLabelInferingAlgorithm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */