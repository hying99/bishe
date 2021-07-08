/*    */ package clus.algo.kNN;
/*    */ 
/*    */ import clus.data.rows.DataTuple;
/*    */ import clus.data.type.ClusAttrType;
/*    */ import clus.data.type.NumericAttrType;
/*    */ import clus.main.Settings;
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
/*    */ 
/*    */ 
/*    */ public class NumericalBasicDistance
/*    */   extends BasicDistance
/*    */ {
/*    */   public double getDistance(ClusAttrType type, DataTuple t1, DataTuple t2) {
/* 44 */     NumericAttrType at = (NumericAttrType)type;
/* 45 */     double x = at.getNumeric(t1);
/*    */     
/* 47 */     if (x == Double.NaN || Double.isInfinite(x))
/*    */     {
/*    */       
/* 50 */       x = at.getStatistic().mean();
/*    */     }
/* 52 */     double y = at.getNumeric(t2);
/*    */ 
/*    */     
/* 55 */     if (y == Double.NaN || Double.isInfinite(y)) {
/* 56 */       y = at.getStatistic().mean();
/*    */     }
/*    */ 
/*    */ 
/*    */     
/* 61 */     if (Settings.kNN_normalized.getValue()) {
/* 62 */       double dif; NumericStatistic numStat = at.getStatistic();
/*    */       
/* 64 */       double min = numStat.min();
/* 65 */       double max = numStat.max();
/*    */       
/* 67 */       if (max != min) {
/* 68 */         dif = max - min;
/*    */       } else {
/* 70 */         dif = 1.0D;
/*    */       } 
/* 72 */       x = (x - min) / dif;
/* 73 */       y = (y - min) / dif;
/*    */     } 
/*    */ 
/*    */     
/* 77 */     return Math.abs(x - y);
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\algo\kNN\NumericalBasicDistance.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */