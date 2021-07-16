/*    */ package clus.ext.ilevelc;
/*    */ 
/*    */ import clus.data.rows.DataTuple;
/*    */ import clus.data.rows.RowData;
/*    */ import clus.data.type.NumericAttrType;
/*    */ import clus.statistic.ClusStatistic;
/*    */ import clus.statistic.RegressionStat;
/*    */ import clus.statistic.StatisticPrintInfo;
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
/*    */ public class ILevelCStatistic
/*    */   extends RegressionStat
/*    */ {
/*    */   public static final long serialVersionUID = 1L;
/*    */   protected NumericAttrType[] m_Numeric;
/* 36 */   protected int m_ClusterID = -1;
/*    */   
/*    */   public ILevelCStatistic(NumericAttrType[] num) {
/* 39 */     super(num);
/* 40 */     this.m_Numeric = num;
/*    */   }
/*    */   
/*    */   public void setClusterID(int id) {
/* 44 */     this.m_ClusterID = id;
/*    */   }
/*    */   
/*    */   public int getClusterID() {
/* 48 */     return this.m_ClusterID;
/*    */   }
/*    */   
/*    */   public ClusStatistic cloneStat() {
/* 52 */     return (ClusStatistic)new ILevelCStatistic(this.m_Numeric);
/*    */   }
/*    */   
/*    */   public String getString(StatisticPrintInfo info) {
/* 56 */     String res = super.getString(info);
/* 57 */     return res + " L=" + getClusterID();
/*    */   }
/*    */   
/*    */   public String getPredictWriterString(DataTuple tuple) {
/* 61 */     return "";
/*    */   }
/*    */   
/*    */   public void assignInstances(RowData data, int[] clusters) {
/* 65 */     for (int i = 0; i < data.getNbRows(); i++) {
/* 66 */       DataTuple tuple = data.getTuple(i);
/* 67 */       clusters[tuple.getIndex()] = getClusterID();
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\ext\ilevelc\ILevelCStatistic.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */