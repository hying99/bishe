/*     */ package clus.activelearning.iteration;
/*     */ 
/*     */ import clus.ext.hierarchical.ClassHierarchy;
/*     */ import clus.util.ClusException;
/*     */ import java.util.HashMap;
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
/*     */ public class Iteration
/*     */ {
/*     */   private int m_LabelsQueried;
/*     */   private int m_LabelsAnswered;
/*     */   private HashSet<String> m_NewLabels;
/*     */   private int m_LabelsSelected;
/*     */   private int m_LabelsAvailable;
/*     */   private int m_PositiveAnswers;
/*     */   private int m_NegativeAnswer;
/*     */   private int m_NegativeAnswerNoHierarchy;
/*     */   private boolean m_IsDone;
/*     */   private HashMap<String, Integer> m_LabelCounter;
/*     */   
/*     */   public Iteration(HashMap<String, Integer> labelCounter, int labelsAvailable) {
/*  34 */     setNewLabels(new HashSet<>());
/*  35 */     setLabelsQueried(0);
/*  36 */     setLabelsSelected(0);
/*  37 */     setPositiveAnswers(0);
/*  38 */     setNegativeAnswer(0);
/*  39 */     setNegativeAnswerNoHierarchy(0);
/*  40 */     setLabelsAvailable(labelsAvailable);
/*  41 */     setLabelCounter(labelCounter);
/*     */   }
/*     */ 
/*     */   
/*     */   public void increasePositiveLabelCounter(String label) {
/*  46 */     getLabelCounter().put(label, Integer.valueOf(((Integer)getLabelCounter().get(label)).intValue() + 1));
/*     */   }
/*     */   
/*     */   public void increaseNegativeLabelCounter(String label, ClassHierarchy h) throws ClusException {
/*  50 */     LinkedList<String> descendants = h.getDescendantsString(label);
/*  51 */     descendants.forEach(d -> getLabelCounter().put(d, Integer.valueOf(((Integer)getLabelCounter().get(d)).intValue() + 1)));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void updateTotalLabels(int answeredAmount) {
/*  57 */     setLabelsAvailable(getLabelsAvailable() - answeredAmount);
/*  58 */     setLabelsQueried(getLabelsQueried() + answeredAmount);
/*     */   }
/*     */   
/*     */   public void updateAnsweredLabels(int answeredAmount) {
/*  62 */     setLabelsAnswered(getLabelsAnswered() + answeredAmount);
/*     */   }
/*     */   
/*     */   public void updatePositiveAmount(int positiveAmount) {
/*  66 */     setPositiveAnswers(getPositiveAnswers() + positiveAmount);
/*     */   }
/*     */   
/*     */   public void updateNegativeAmount(int negativeAmount) {
/*  70 */     setNegativeAnswer(getNegativeAnswer() + negativeAmount);
/*     */   }
/*     */   
/*     */   public void updateNegativeAmountHierarchy(int negativeAmountNoHierarchy) {
/*  74 */     setNegativeAnswerNoHierarchy(getNegativeAnswerNoHierarchy() + negativeAmountNoHierarchy);
/*     */   }
/*     */   
/*     */   public boolean fitBatchSize(int batchSize) {
/*  78 */     return (getLabelsSelected() < batchSize);
/*     */   }
/*     */   
/*     */   public boolean fitBatchSizeHCAL(int batchSize, int depth) {
/*  82 */     return (getLabelsSelected() + depth < batchSize);
/*     */   }
/*     */   
/*     */   public void addNewLabels(HashSet<String> newLabels) {
/*  86 */     getNewLabels().addAll(newLabels);
/*     */   }
/*     */   
/*     */   public void increaseSelectedAmount() {
/*  90 */     setLabelsSelected(getLabelsSelected() + 1);
/*     */   }
/*     */   public void increaseSelectedAmountHCAL(int depth) {
/*  93 */     setLabelsSelected(getLabelsSelected() + depth);
/*     */   }
/*     */   public boolean isDone() {
/*  96 */     return this.m_IsDone;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getLabelsQueried() {
/* 104 */     return this.m_LabelsQueried;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final void setLabelsQueried(int m_LabelsQueried) {
/* 111 */     this.m_LabelsQueried = m_LabelsQueried;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HashSet<String> getNewLabels() {
/* 118 */     return this.m_NewLabels;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void setNewLabels(HashSet<String> m_NewLabels) {
/* 125 */     this.m_NewLabels = m_NewLabels;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getLabelsSelected() {
/* 132 */     return this.m_LabelsSelected;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void setLabelsSelected(int m_LabelsSelected) {
/* 139 */     this.m_LabelsSelected = m_LabelsSelected;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getLabelsAvailable() {
/* 146 */     return this.m_LabelsAvailable;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final void setLabelsAvailable(int m_LabelsAvailable) {
/* 153 */     this.m_LabelsAvailable = m_LabelsAvailable;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getPositiveAnswers() {
/* 160 */     return this.m_PositiveAnswers;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final void setPositiveAnswers(int m_PositiveAnswers) {
/* 167 */     this.m_PositiveAnswers = m_PositiveAnswers;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getNegativeAnswer() {
/* 174 */     return this.m_NegativeAnswer;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final void setNegativeAnswer(int m_NegativeAnswer) {
/* 181 */     this.m_NegativeAnswer = m_NegativeAnswer;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setIsDone(boolean m_IsDone) {
/* 188 */     this.m_IsDone = m_IsDone;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HashMap<String, Integer> getLabelCounter() {
/* 195 */     return this.m_LabelCounter;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final void setLabelCounter(HashMap<String, Integer> m_LabelCounter) {
/* 202 */     this.m_LabelCounter = m_LabelCounter;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getNegativeAnswerNoHierarchy() {
/* 209 */     return this.m_NegativeAnswerNoHierarchy;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setNegativeAnswerNoHierarchy(int m_NegativeAnswerNoHierarchy) {
/* 216 */     this.m_NegativeAnswerNoHierarchy = m_NegativeAnswerNoHierarchy;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getLabelsAnswered() {
/* 223 */     return this.m_LabelsAnswered;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setLabelsAnswered(int m_LabelsAnswered) {
/* 230 */     this.m_LabelsAnswered = m_LabelsAnswered;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\activelearning\iteration\Iteration.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */