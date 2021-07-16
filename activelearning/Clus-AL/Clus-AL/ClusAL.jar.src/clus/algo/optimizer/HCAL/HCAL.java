/*     */ package clus.algo.optimizer.HCAL;
/*     */ 
/*     */ import clus.activelearning.algo.ClusActiveLearningAlgorithm;
/*     */ import clus.activelearning.data.OracleAnswer;
/*     */ import clus.activelearning.indexing.LabelIndex;
/*     */ import clus.activelearning.indexing.LabelIndexer;
/*     */ import clus.activelearning.iteration.ClusActiveLearningIteration;
/*     */ import clus.algo.optimizer.LabelBasedOptimizer;
/*     */ import clus.algo.optimizer.Solution;
/*     */ import clus.data.rows.RowData;
/*     */ import clus.model.ClusModel;
/*     */ import clus.util.ClusException;
/*     */ import java.util.Collections;
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
/*     */ public class HCAL
/*     */   extends LabelBasedOptimizer
/*     */ {
/*     */   private double[][] m_Cost;
/*     */   private double[][] m_Informativeness;
/*     */   private RowData m_UnlabeledData;
/*     */   private String[] m_Labels;
/*     */   private double m_Budget;
/*     */   private int m_PopulationSize;
/*     */   
/*     */   public HCAL(ClusActiveLearningAlgorithm al, ClusModel[] models, int populationSize, double budget, int iterations, double[][] cost, double[][] informativeness) throws ClusException {
/*  38 */     super(al, budget, iterations);
/*  39 */     this.m_UnlabeledData = al.getUnlabeledData();
/*  40 */     this.m_Labels = al.getLabels();
/*  41 */     this.m_Budget = budget;
/*  42 */     this.m_Cost = cost;
/*  43 */     this.m_Informativeness = informativeness;
/*  44 */     this.m_PopulationSize = populationSize;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public LinkedList<LabelIndex> optimize() {
/*  50 */     HCALPopulation currentPopulation = new HCALPopulation(getPopulationSize(), getBudget(), getUnlabeledData(), getLabels(), getCost(), getInformativeness());
/*  51 */     for (int j = 0; j < getIterations() + 1; j++) {
/*  52 */       HCALPopulation newPopulation = (HCALPopulation)currentPopulation.getNewPopulation();
/*  53 */       for (int i = 0; i < newPopulation.getPopulationSize(); i++) {
/*  54 */         HCALSolution solution = (HCALSolution)newPopulation.getSolutions()[i];
/*  55 */         int index = currentPopulation.findWorstSolution(solution);
/*  56 */         if (index != -1) {
/*  57 */           currentPopulation.setSolutionByIndex(newPopulation.getSolutions()[i], index);
/*     */         }
/*     */       } 
/*     */     } 
/*     */     
/*  62 */     HCALSolution bestSolution = getBestSolution(currentPopulation.getSolutions());
/*     */     try {
/*  64 */       return buildLabelMinIndexer(bestSolution);
/*  65 */     } catch (ClusException ex) {
/*  66 */       Logger.getLogger(HCAL.class.getName()).log(Level.SEVERE, (String)null, (Throwable)ex);
/*     */       
/*  68 */       return null;
/*     */     } 
/*     */   }
/*     */   private LinkedList<LabelIndex> buildLabelMinIndexer(HCALSolution solution) throws ClusException {
/*  72 */     LabelIndexer labelIndexer = new LabelIndexer(getActiveLearningAlgorithm().getBatchsize());
/*     */     
/*  74 */     ClusActiveLearningIteration iteration = getActiveLearningAlgorithm().getIteration();
/*     */     
/*  76 */     HCALItem[] items = (HCALItem[])solution.getItems();
/*     */     
/*  78 */     for (HCALItem item : items) {
/*  79 */       OracleAnswer answer = item.getOracleAnswer();
/*  80 */       if (!answer.queriedBefore(item.getLabel())) {
/*  81 */         double cost = getActiveLearningAlgorithm().getCost(item.getLabel());
/*  82 */         if (iteration.isAffordable(cost)) {
/*  83 */           labelIndexer.addMin(item.getActiveIndex(), item.getLabel(), item.getInformativeness());
/*     */         }
/*     */       } 
/*     */     } 
/*  87 */     Collections.sort(labelIndexer.getIndexer());
/*     */     
/*  89 */     return labelIndexer.getIndexer();
/*     */   }
/*     */   
/*     */   private HCALSolution getBestSolution(Solution[] solutions) {
/*  93 */     HCALSolution bestSolution = (HCALSolution)solutions[0];
/*  94 */     int index = 0;
/*  95 */     for (int i = 1; i < solutions.length; i++) {
/*     */       
/*  97 */       if (solutions[i].getSolutionFitness()[1] < bestSolution.getSolutionFitness()[1]) {
/*  98 */         bestSolution = (HCALSolution)solutions[i];
/*  99 */         index = i;
/*     */       } 
/*     */     } 
/* 102 */     return bestSolution;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double[][] getCost() {
/* 109 */     return this.m_Cost;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setCost(double[][] m_Cost) {
/* 116 */     this.m_Cost = m_Cost;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double[][] getInformativeness() {
/* 123 */     return this.m_Informativeness;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setInformativeness(double[][] m_Informativeness) {
/* 130 */     this.m_Informativeness = m_Informativeness;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public RowData getUnlabeledData() {
/* 137 */     return this.m_UnlabeledData;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setUnlabeledData(RowData m_UnlabeledData) {
/* 144 */     this.m_UnlabeledData = m_UnlabeledData;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String[] getLabels() {
/* 151 */     return this.m_Labels;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setLabels(String[] m_Labels) {
/* 158 */     this.m_Labels = m_Labels;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getBudget() {
/* 165 */     return this.m_Budget;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setBudget(double m_Budget) {
/* 172 */     this.m_Budget = m_Budget;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getPopulationSize() {
/* 179 */     return this.m_PopulationSize;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setPopulationSize(int m_PopulationSize) {
/* 186 */     this.m_PopulationSize = m_PopulationSize;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\algo\optimizer\HCAL\HCAL.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */