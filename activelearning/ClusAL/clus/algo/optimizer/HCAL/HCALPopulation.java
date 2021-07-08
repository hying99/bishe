/*     */ package clus.algo.optimizer.HCAL;
/*     */ 
/*     */ import clus.algo.optimizer.Population;
/*     */ import clus.algo.optimizer.Solution;
/*     */ import clus.data.rows.RowData;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class HCALPopulation
/*     */   extends Population
/*     */ {
/*     */   private double[][] m_Cost;
/*     */   private double[][] m_Informativeness;
/*     */   private RowData m_UnlabeledData;
/*     */   private String[] m_Labels;
/*     */   private double m_Budget;
/*     */   private int m_Index;
/*     */   
/*     */   public HCALPopulation(int populationSize, double budget, RowData unlabeledData, String[] labels, double[][] cost, double[][] informativeness) {
/*  26 */     super(populationSize);
/*  27 */     this.m_Budget = budget;
/*  28 */     this.m_Cost = cost;
/*  29 */     this.m_Informativeness = informativeness;
/*  30 */     this.m_UnlabeledData = unlabeledData;
/*  31 */     this.m_Labels = labels;
/*  32 */     initializePopulation();
/*     */   }
/*     */   
/*     */   public HCALPopulation(HCALPopulation population) {
/*  36 */     super(population.getPopulationSize());
/*  37 */     this.m_Budget = population.getBudget();
/*  38 */     this.m_Cost = population.getCost();
/*  39 */     this.m_Informativeness = population.getInformativeness();
/*  40 */     this.m_UnlabeledData = population.getUnlabeledData();
/*  41 */     this.m_Labels = population.getLabels();
/*  42 */     HCALSolution[] solutions = new HCALSolution[getPopulationSize()];
/*  43 */     for (int i = 0; i < population.getPopulationSize(); i++) {
/*  44 */       solutions[i] = (HCALSolution)population.getSolutions()[i].getNewSolution();
/*     */     }
/*  46 */     setSolutions((Solution[])solutions);
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/*  51 */     HCALSolution[] solutions = (HCALSolution[])getSolutions();
/*  52 */     String output = "";
/*  53 */     for (int i = 0; i < solutions.length; i++) {
/*  54 */       HCALSolution solution = solutions[i];
/*  55 */       for (int j = 0; j < solution.getSolutionSize(); j++) {
/*  56 */         output = output + solution.getItems()[j].isActive() + "\t";
/*     */       }
/*  58 */       output = output + "\n";
/*     */     } 
/*  60 */     return output;
/*     */   }
/*     */ 
/*     */   
/*     */   public final void initializePopulation() {
/*  65 */     HCALSolution[] solutions = new HCALSolution[getPopulationSize()];
/*  66 */     for (int i = 0; i < getPopulationSize(); i++) {
/*  67 */       solutions[i] = new HCALSolution(getUnlabeledData(), getLabels(), getCost(), getInformativeness(), getBudget(), getPopulationSize());
/*     */     }
/*  69 */     setSolutions((Solution[])solutions);
/*     */   }
/*     */ 
/*     */   
/*     */   public Population getNewPopulation() {
/*  74 */     return new HCALPopulation(this);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int findWorstSolution(HCALSolution newSolution) {
/*  81 */     int index = 0;
/*     */     
/*  83 */     double[] newFitness = newSolution.getSolutionFitness();
/*  84 */     double[][] currentFitness = getSolutionsFitness();
/*     */ 
/*     */ 
/*     */     
/*  88 */     double worstCost = currentFitness[0][0];
/*  89 */     double worstInformativeness = currentFitness[0][1];
/*  90 */     for (int i = 1; i < currentFitness.length; i++) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*  96 */       if (currentFitness[i][0] > worstCost && currentFitness[i][1] > worstInformativeness) {
/*  97 */         worstCost = currentFitness[i][0];
/*     */         
/*  99 */         worstInformativeness = currentFitness[i][1];
/* 100 */         index = i;
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 109 */     if (newFitness[0] < worstCost && newFitness[1] < worstInformativeness)
/*     */     {
/* 111 */       return index;
/*     */     }
/*     */     
/* 114 */     return -1;
/*     */   }
/*     */   
/*     */   public double[][] getSolutionsFitness() {
/* 118 */     double[][] currentFitness = new double[(getSolutions()).length][2];
/* 119 */     for (int i = 0; i < (getSolutions()).length; i++) {
/* 120 */       currentFitness[i] = getSolutions()[i].getSolutionFitness();
/*     */     }
/* 122 */     return currentFitness;
/*     */   }
/*     */   
/*     */   public void setSolutionByIndex(Solution solution, int index) {
/* 126 */     getSolutions()[index] = solution;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double[][] getCost() {
/* 133 */     return this.m_Cost;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setCost(double[][] m_Cost) {
/* 140 */     this.m_Cost = m_Cost;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double[][] getInformativeness() {
/* 147 */     return this.m_Informativeness;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setInformativeness(double[][] m_Informativeness) {
/* 154 */     this.m_Informativeness = m_Informativeness;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public RowData getUnlabeledData() {
/* 161 */     return this.m_UnlabeledData;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setUnlabeledData(RowData m_UnlabeledData) {
/* 168 */     this.m_UnlabeledData = m_UnlabeledData;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String[] getLabels() {
/* 175 */     return this.m_Labels;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setLabels(String[] m_Labels) {
/* 182 */     this.m_Labels = m_Labels;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getBudget() {
/* 189 */     return this.m_Budget;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setBudget(double m_Budget) {
/* 196 */     this.m_Budget = m_Budget;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getIndex() {
/* 203 */     return this.m_Index;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setIndex(int m_Index) {
/* 210 */     this.m_Index = m_Index;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\algo\optimizer\HCAL\HCALPopulation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */