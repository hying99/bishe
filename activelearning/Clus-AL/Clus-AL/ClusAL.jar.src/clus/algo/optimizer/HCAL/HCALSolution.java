/*     */ package clus.algo.optimizer.HCAL;
/*     */ 
/*     */ import clus.algo.optimizer.Item;
/*     */ import clus.algo.optimizer.Solution;
/*     */ import clus.data.rows.RowData;
/*     */ import java.util.Random;
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
/*     */ public class HCALSolution
/*     */   extends Solution
/*     */ {
/*     */   private double m_Budget;
/*     */   private String[] m_Labels;
/*     */   private int m_PopulationSize;
/*     */   
/*     */   public HCALSolution(RowData unlabeledData, String[] labels, double[][] cost, double[][] informativeness, double budget, int populationSize) {
/*  25 */     this.m_Budget = budget;
/*  26 */     this.m_Labels = labels;
/*  27 */     this.m_PopulationSize = populationSize;
/*  28 */     setSolutionSize(unlabeledData.getNbRows() * labels.length);
/*  29 */     HCALItem[] items = new HCALItem[getSolutionSize()];
/*  30 */     for (int i = 0; i < unlabeledData.getNbRows(); i++) {
/*  31 */       for (int j = 0; j < labels.length; j++) {
/*  32 */         items[i * (cost[0]).length + j] = new HCALItem(false, unlabeledData.getTuple(i).getOracleAnswer(), (unlabeledData.getTuple(i)).m_ActiveIndex, cost[i][j], informativeness[i][j], labels[j]);
/*     */       }
/*     */     } 
/*     */     
/*  36 */     setItems((Item[])items);
/*     */   }
/*     */   
/*     */   public HCALSolution(HCALSolution solution) {
/*  40 */     this.m_Budget = solution.getBudget();
/*  41 */     this.m_Labels = solution.getLabels();
/*  42 */     setSolutionSize(solution.getSolutionSize());
/*     */   }
/*     */ 
/*     */   
/*     */   public Solution getNewSolution() {
/*  47 */     Solution newSolution = new HCALSolution(this);
/*  48 */     Random r = new Random();
/*  49 */     r.setSeed(ResourceInfo.getTime());
/*  50 */     HCALItem[] items = (HCALItem[])getItems();
/*  51 */     HCALItem[] newItems = new HCALItem[newSolution.getSolutionSize()];
/*  52 */     for (int i = 0; i < newSolution.getSolutionSize(); i++) {
/*  53 */       newItems[i] = new HCALItem(items[i]);
/*  54 */       double chance = r.nextDouble();
/*     */       
/*  56 */       if (chance > 1.0D / newSolution.getSolutionSize()) {
/*  57 */         newItems[i].setActive(Boolean.valueOf(!items[i].isActive().booleanValue()));
/*     */       }
/*     */     } 
/*  60 */     newSolution.setItems((Item[])newItems);
/*  61 */     return newSolution;
/*     */   }
/*     */ 
/*     */   
/*     */   public double[] getSolutionFitness() {
/*  66 */     HCALItem item = null;
/*  67 */     boolean allZeros = true;
/*  68 */     double[] fitness = new double[2];
/*  69 */     for (int i = 0; i < getSolutionSize(); i++) {
/*  70 */       item = (HCALItem)getItems()[i];
/*  71 */       if (item.isActive().booleanValue() && !item.getOracleAnswer().queriedBefore(item.getLabel())) {
/*  72 */         allZeros = false;
/*  73 */         fitness[0] = fitness[0] + item.getCost();
/*  74 */         fitness[1] = fitness[1] + item.getInformativeness();
/*     */       } 
/*     */     } 
/*     */     
/*  78 */     if (allZeros) {
/*  79 */       return new double[] { Double.MAX_VALUE, Double.MAX_VALUE };
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  86 */     return fitness;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getBudget() {
/*  93 */     return this.m_Budget;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setBudget(double m_Budget) {
/* 100 */     this.m_Budget = m_Budget;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String[] getLabels() {
/* 107 */     return this.m_Labels;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setLabels(String[] m_Labels) {
/* 114 */     this.m_Labels = m_Labels;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\algo\optimizer\HCAL\HCALSolution.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */