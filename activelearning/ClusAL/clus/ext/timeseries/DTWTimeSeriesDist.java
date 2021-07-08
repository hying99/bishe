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
/*    */ public class DTWTimeSeriesDist
/*    */   extends TimeSeriesDist
/*    */ {
/*    */   public static final long serialVersionUID = 1L;
/*    */   
/*    */   public DTWTimeSeriesDist(TimeSeriesAttrType attr) {
/* 33 */     super(attr);
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
/*    */   public double calcDistance(TimeSeries t1, TimeSeries t2, int adjustmentWindow) {
/* 45 */     int m = t1.length();
/* 46 */     int n = t2.length();
/* 47 */     double[][] wrappingPathMatrix = new double[m][n];
/* 48 */     double[] vt1 = t1.getValues();
/* 49 */     double[] vt2 = t2.getValues();
/* 50 */     wrappingPathMatrix[0][0] = Math.abs(vt1[0] - vt2[0]) * 2.0D;
/* 51 */     int aw = Math.min(m, adjustmentWindow);
/*    */     int i;
/* 53 */     for (i = 1; i < aw; i++) {
/* 54 */       wrappingPathMatrix[i][0] = wrappingPathMatrix[i - 1][0] + Math.abs(vt1[i] - vt2[0]);
/*    */     }
/*    */     
/* 57 */     for (i = aw; i < m; i++) {
/* 58 */       wrappingPathMatrix[i][0] = Double.POSITIVE_INFINITY;
/*    */     }
/*    */     
/* 61 */     aw = Math.min(n, adjustmentWindow);
/* 62 */     for (i = 1; i < aw; i++) {
/* 63 */       wrappingPathMatrix[0][i] = wrappingPathMatrix[0][i - 1] + Math.abs(vt1[0] - vt2[i]);
/*    */     }
/*    */     
/* 66 */     for (i = aw; i < n; i++) {
/* 67 */       wrappingPathMatrix[0][i] = Double.POSITIVE_INFINITY;
/*    */     }
/*    */     
/* 70 */     for (int k = 2; k < m + n - 1; k++) {
/* 71 */       for (int j = Math.max(k - n + 1, 1); j < Math.min(k, m); j++) {
/* 72 */         if (Math.abs(2 * j - k) <= adjustmentWindow) {
/* 73 */           double dfk = Math.abs(vt1[j] - vt2[k - j]);
/* 74 */           wrappingPathMatrix[j][k - j] = Math.min(wrappingPathMatrix[j][k - j - 1] + dfk, Math.min(wrappingPathMatrix[j - 1][k - j] + dfk, wrappingPathMatrix[j - 1][k - j - 1] + dfk * 2.0D));
/*    */         } else {
/* 76 */           wrappingPathMatrix[j][k - j] = Double.POSITIVE_INFINITY;
/*    */         } 
/*    */       } 
/*    */     } 
/*    */ 
/*    */ 
/*    */     
/* 83 */     return wrappingPathMatrix[m - 1][n - 1] / (m + n);
/*    */   }
/*    */   
/*    */   public double calcDistance(TimeSeries t1, TimeSeries t2) {
/* 87 */     return calcDistance(t1, t2, Math.max(Math.max(Math.abs(t1.length() - t2.length()) + 1, t1.length() / 2), t2.length() / 2));
/*    */   }
/*    */   
/*    */   public String getDistanceName() {
/* 91 */     return "DTWTimeSeriesDist";
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\ext\timeseries\DTWTimeSeriesDist.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */