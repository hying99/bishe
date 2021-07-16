/*    */ package clus.data;
/*    */ 
/*    */ import clus.algo.tdidt.ClusNode;
/*    */ import clus.data.rows.DataPreprocs;
/*    */ import clus.error.ClusErrorList;
/*    */ import clus.selection.ClusSelection;
/*    */ import clus.statistic.ClusStatistic;
/*    */ import clus.util.ClusException;
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
/*    */ public abstract class ClusData
/*    */ {
/*    */   protected int m_NbRows;
/*    */   
/*    */   public final int getNbRows() {
/* 37 */     return this.m_NbRows;
/*    */   }
/*    */   
/*    */   public final void setNbRows(int nb) {
/* 41 */     this.m_NbRows = nb;
/*    */   }
/*    */   
/*    */   public ClusData selectFrom(ClusSelection sel) {
/* 45 */     return null;
/*    */   }
/*    */   
/*    */   public abstract ClusData cloneData();
/*    */   
/*    */   public abstract ClusData select(ClusSelection paramClusSelection);
/*    */   
/*    */   public abstract void insert(ClusData paramClusData, ClusSelection paramClusSelection);
/*    */   
/*    */   public abstract void resize(int paramInt);
/*    */   
/*    */   public abstract void attach(ClusNode paramClusNode);
/*    */   
/*    */   public abstract void calcTotalStat(ClusStatistic paramClusStatistic);
/*    */   
/*    */   public abstract void calcError(ClusNode paramClusNode, ClusErrorList paramClusErrorList);
/*    */   
/*    */   public abstract double[] getNumeric(int paramInt);
/*    */   
/*    */   public abstract int[] getNominal(int paramInt);
/*    */   
/*    */   public abstract void preprocess(int paramInt, DataPreprocs paramDataPreprocs) throws ClusException;
/*    */   
/*    */   public void calcTotalStats(ClusStatistic[] stats) {
/* 69 */     for (int i = 0; i < stats.length; i++)
/* 70 */       calcTotalStat(stats[i]); 
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\data\ClusData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */