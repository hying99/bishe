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
/*    */ public class QDMTimeSeriesDist
/*    */   extends TimeSeriesDist
/*    */ {
/*    */   public static final long serialVersionUID = 1L;
/*    */   
/*    */   public QDMTimeSeriesDist(TimeSeriesAttrType attr) {
/* 33 */     super(attr);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public double calcDistance(TimeSeries t1, TimeSeries t2) {
/* 39 */     double[] vt1 = t1.getValuesNoCopy();
/* 40 */     double[] vt2 = t2.getValuesNoCopy();
/* 41 */     int m = Math.max(vt1.length, vt2.length);
/* 42 */     int n = Math.min(vt1.length, vt2.length);
/* 43 */     double distance = 0.0D;
/* 44 */     for (int i = 0; i < m; i++) {
/* 45 */       for (int j = i + 1; j < m; j++)
/*    */       {
/*    */ 
/*    */         
/* 49 */         distance += Math.abs(diff(vt1[j], vt1[i]) - diff(vt2[j % n], vt2[i % n]));
/*    */       }
/*    */     } 
/* 52 */     distance /= (m * (m - 1));
/* 53 */     return distance;
/*    */   }
/*    */   
/*    */   public static int diff(double a, double b) {
/* 57 */     if (a == 0.0D && b == 0.0D) return 0; 
/* 58 */     if (b != 0.0D && Math.abs(a / b - 1.0D) < 0.02D)
/* 59 */       return 0; 
/* 60 */     if (a < b) {
/* 61 */       return -1;
/*    */     }
/* 63 */     return 1;
/*    */   }
/*    */   
/*    */   public String getDistanceName() {
/* 67 */     return "QDMTimeSeriesDist";
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\ext\timeseries\QDMTimeSeriesDist.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */