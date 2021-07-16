/*    */ package clus.ext.timeseries;
/*    */ 
/*    */ import clus.data.type.TimeSeriesAttrType;
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
/*    */ public class TSCTimeSeriesDist
/*    */   extends TimeSeriesDist
/*    */ {
/*    */   public static final long serialVersionUID = 1L;
/*    */   
/*    */   public TSCTimeSeriesDist(TimeSeriesAttrType attr) {
/* 33 */     super(attr);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public double calcDistance(TimeSeries t1, TimeSeries t2) {
/* 39 */     double[] ts1 = t1.getValues();
/* 40 */     double[] ts2 = t2.getValues();
/* 41 */     double mean_ts1 = calculateMean(ts1);
/* 42 */     double mean_ts2 = calculateMean(ts2);
/* 43 */     double cc = 0.0D;
/* 44 */     double sum_ts1_sqr = 0.0D;
/* 45 */     double sum_ts2_sqr = 0.0D;
/* 46 */     double sum_ts1_ts2 = 0.0D;
/* 47 */     if (ts1.length != ts2.length) {
/* 48 */       System.err.println("TimeSeriesCorrelation applies only to Time Series with equal length");
/*    */       
/* 50 */       System.exit(1);
/*    */     } 
/* 52 */     for (int k = 0; k < ts1.length; k++) {
/* 53 */       sum_ts1_ts2 += (ts1[k] - mean_ts1) * (ts2[k] - mean_ts2);
/* 54 */       sum_ts1_sqr += Math.pow(ts1[k] - mean_ts1, 2.0D);
/* 55 */       sum_ts2_sqr += Math.pow(ts2[k] - mean_ts2, 2.0D);
/*    */     } 
/* 57 */     cc = 1.0D - Math.abs(sum_ts1_ts2 / Math.sqrt(sum_ts1_sqr * sum_ts2_sqr));
/* 58 */     return cc;
/*    */   }
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
/*    */   public double calculateMean(double[] ts) {
/* 88 */     double sum = 0.0D;
/* 89 */     for (int k = 0; k < ts.length; ) { sum += ts[k]; k++; }
/* 90 */      sum /= ts.length;
/* 91 */     return sum;
/*    */   }
/*    */   
/*    */   public String getDistanceName() {
/* 95 */     return "TSCTimeSeriesDist";
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\ext\timeseries\TSCTimeSeriesDist.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */