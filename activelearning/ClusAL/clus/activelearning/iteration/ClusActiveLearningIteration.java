/*     */ package clus.activelearning.iteration;
/*     */ 
/*     */ import clus.activelearning.indexing.Indexer;
/*     */ import clus.activelearning.indexing.LabelIndex;
/*     */ import clus.util.ClusException;
/*     */ import java.util.HashMap;
/*     */ import java.util.LinkedList;
/*     */ import jeans.resource.ResourceInfo;
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
/*     */ public class ClusActiveLearningIteration
/*     */   extends Iteration
/*     */ {
/*     */   private double m_BudgetSpent;
/*     */   private double m_Budget;
/*     */   private int m_Iteration;
/*     */   private double m_FP;
/*     */   private double m_FN;
/*     */   private double m_TP;
/*     */   private long m_QueryBuildingTime;
/*     */   private LinkedList<LabelIndex> m_Query;
/*     */   private Indexer m_PositiveIndexer;
/*     */   private Indexer m_NegativeIndexer;
/*     */   private double[][] m_PositiveMeasure;
/*     */   private double[][] m_NegativeMeasure;
/*     */   private double[][] m_Measure;
/*     */   private double[][] m_FinalMeasure;
/*     */   
/*     */   public ClusActiveLearningIteration(HashMap<String, Integer> labelCounter, int iterationCounter, double availableBudget, int availableLabels, Indexer indexerPositive, Indexer indexerNegative) {
/*  42 */     super(labelCounter, availableLabels);
/*  43 */     setIteration(iterationCounter);
/*  44 */     setBudget(availableBudget);
/*  45 */     setBudgetSpent(0.0D);
/*  46 */     setQueryBuildingTime(ResourceInfo.getTime());
/*  47 */     setPositiveIndexer(indexerPositive);
/*  48 */     setNegativeIndexer(indexerNegative);
/*     */   }
/*     */ 
/*     */   
/*     */   public void printIteration() {
/*  53 */     System.out.println("ITERATION" + getIteration());
/*  54 */     System.out.println("LABELS SELECTED" + getLabelsSelected());
/*  55 */     System.out.println("BUDGET AVAILABLE" + getBudget());
/*  56 */     System.out.println("BUDGET SPENT" + getBudgetSpent());
/*  57 */     System.out.println("ANSWERED" + getLabelsQueried());
/*     */   }
/*     */   
/*     */   public boolean isLabelAffordable(double cost) {
/*  61 */     return (getBudgetSpent() + cost <= getBudget());
/*     */   }
/*     */   
/*     */   public void updateBudget(double cost) {
/*  65 */     setBudget(getBudget());
/*  66 */     setBudgetSpent(getBudgetSpent() + cost);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isAffordable(double cost) throws ClusException {
/*  73 */     return (getBudget() >= cost);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getIteration() {
/*  80 */     return this.m_Iteration;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void setIteration(int m_Iteration) {
/*  87 */     this.m_Iteration = m_Iteration;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getBudgetSpent() {
/*  94 */     return this.m_BudgetSpent;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void setBudgetSpent(double m_BudgetSpent) {
/* 101 */     this.m_BudgetSpent = m_BudgetSpent;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getBudget() {
/* 108 */     return this.m_Budget;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void setBudget(double m_Budget) {
/* 115 */     this.m_Budget = m_Budget;
/*     */   }
/*     */   
/*     */   public void addFP(double fp) {
/* 119 */     setFP(getFP() + fp);
/*     */   }
/*     */   
/*     */   public void addFN(double fn) {
/* 123 */     setFN(getFN() + fn);
/*     */   }
/*     */   
/*     */   public void addTP(double tp) {
/* 127 */     setTP(getTP() + tp);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getFP() {
/* 134 */     return this.m_FP;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setFP(double m_FP) {
/* 141 */     this.m_FP = m_FP;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getFN() {
/* 148 */     return this.m_FN;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setFN(double m_FN) {
/* 155 */     this.m_FN = m_FN;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getTP() {
/* 162 */     return this.m_TP;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setTP(double m_TP) {
/* 169 */     this.m_TP = m_TP;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getQueryBuildingTime() {
/* 176 */     return this.m_QueryBuildingTime;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setQueryBuildingTime(long m_QueryBuildingType) {
/* 183 */     this.m_QueryBuildingTime = m_QueryBuildingType;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Indexer getPositiveIndexer() {
/* 190 */     return this.m_PositiveIndexer;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setPositiveIndexer(Indexer m_Indexer) {
/* 197 */     this.m_PositiveIndexer = m_Indexer;
/*     */   }
/*     */   
/*     */   public Indexer getNegativeIndexer() {
/* 201 */     return this.m_NegativeIndexer;
/*     */   }
/*     */   
/*     */   public void setNegativeIndexer(Indexer m_Indexer) {
/* 205 */     this.m_NegativeIndexer = m_Indexer;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public LinkedList<LabelIndex> getM_Query() {
/* 212 */     return this.m_Query;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setM_Query(LinkedList<LabelIndex> m_Query) {
/* 219 */     this.m_Query = m_Query;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double[][] getFinalMeasure() {
/* 226 */     return this.m_FinalMeasure;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setFinalMeasure(double[][] m_Measure) {
/* 233 */     this.m_FinalMeasure = m_Measure;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public double[][] getMeasure() {
/* 239 */     return this.m_Measure;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setMeasure(double[][] m_Measure) {
/* 246 */     this.m_Measure = m_Measure;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double[][] getM_PositiveMeasure() {
/* 253 */     return this.m_PositiveMeasure;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setM_PositiveMeasure(double[][] m_PositiveMeasure) {
/* 260 */     this.m_PositiveMeasure = m_PositiveMeasure;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double[][] getM_NegativeMeasure() {
/* 267 */     return this.m_NegativeMeasure;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setM_NegativeMeasure(double[][] m_NegativeMeasure) {
/* 274 */     this.m_NegativeMeasure = m_NegativeMeasure;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\activelearning\iteration\ClusActiveLearningIteration.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */