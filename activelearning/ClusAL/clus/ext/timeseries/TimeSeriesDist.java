/*    */ package clus.ext.timeseries;
/*    */ 
/*    */ import clus.data.rows.DataTuple;
/*    */ import clus.data.type.TimeSeriesAttrType;
/*    */ import clus.statistic.ClusDistance;
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
/*    */ public abstract class TimeSeriesDist
/*    */   extends ClusDistance
/*    */ {
/*    */   public static final long serialVersionUID = 1L;
/*    */   protected TimeSeriesAttrType m_Attr;
/*    */   
/*    */   public TimeSeriesDist(TimeSeriesAttrType attr) {
/* 38 */     this.m_Attr = attr;
/*    */   }
/*    */   
/*    */   public abstract double calcDistance(TimeSeries paramTimeSeries1, TimeSeries paramTimeSeries2);
/*    */   
/*    */   public double calcDistance(DataTuple t1, DataTuple t2) {
/* 44 */     TimeSeries ts1 = this.m_Attr.getTimeSeries(t1);
/* 45 */     TimeSeries ts2 = this.m_Attr.getTimeSeries(t2);
/* 46 */     return calcDistance(ts1, ts2);
/*    */   }
/*    */   
/*    */   public double calcDistanceToCentroid(DataTuple t1, ClusStatistic s2) {
/* 50 */     TimeSeries ts1 = this.m_Attr.getTimeSeries(t1);
/* 51 */     TimeSeriesStat stat = (TimeSeriesStat)s2;
/* 52 */     return calcDistance(ts1, stat.getRepresentativeMedoid());
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\ext\timeseries\TimeSeriesDist.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */