/*    */ package clus.heuristic;
/*    */ 
/*    */ import clus.data.rows.RowData;
/*    */ import clus.statistic.ClusStatistic;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class ClusHeuristic
/*    */ {
/*    */   public static final double DELTA = 1.0E-6D;
/*    */   protected RowData m_TrainData;
/*    */   protected double m_TrainDataHeurValue;
/*    */   protected ClusStopCriterion m_StopCrit;
/*    */   
/*    */   public void setData(RowData data) {}
/*    */   
/*    */   public void setInitialData(ClusStatistic stat, RowData data) {}
/*    */   
/*    */   public void setRootStatistic(ClusStatistic stat) {}
/*    */   
/*    */   public abstract double calcHeuristic(ClusStatistic paramClusStatistic1, ClusStatistic paramClusStatistic2, ClusStatistic paramClusStatistic3);
/*    */   
/*    */   public abstract String getName();
/*    */   
/*    */   public double calcHeuristic(ClusStatistic c_tstat, ClusStatistic[] c_pstat, int nbsplit) {
/* 54 */     return Double.NEGATIVE_INFINITY;
/*    */   }
/*    */   
/*    */   public boolean stopCriterion(ClusStatistic tstat, ClusStatistic pstat, ClusStatistic missing) {
/* 58 */     return this.m_StopCrit.stopCriterion(tstat, pstat, missing);
/*    */   }
/*    */   
/*    */   public boolean stopCriterion(ClusStatistic tstat, ClusStatistic[] pstat, int nbsplit) {
/* 62 */     return this.m_StopCrit.stopCriterion(tstat, pstat, nbsplit);
/*    */   }
/*    */   
/*    */   public static double nonZero(double val) {
/* 66 */     if (val < 1.0E-6D) return Double.NEGATIVE_INFINITY; 
/* 67 */     return val;
/*    */   }
/*    */   
/*    */   public RowData getTrainData() {
/* 71 */     return this.m_TrainData;
/*    */   }
/*    */   
/*    */   public void setTrainData(RowData data) {
/* 75 */     this.m_TrainData = data;
/*    */   }
/*    */   
/*    */   public double getTrainDataHeurValue() {
/* 79 */     return this.m_TrainDataHeurValue;
/*    */   }
/*    */   
/*    */   public void setTrainDataHeurValue(double value) {
/* 83 */     this.m_TrainDataHeurValue = value;
/*    */   }
/*    */   
/*    */   public void setStopCriterion(ClusStopCriterion stop) {
/* 87 */     this.m_StopCrit = stop;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean isAcceptable(ClusStatistic c_tstat, ClusStatistic c_pstat, ClusStatistic missing) {
/* 97 */     return true;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\heuristic\ClusHeuristic.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */