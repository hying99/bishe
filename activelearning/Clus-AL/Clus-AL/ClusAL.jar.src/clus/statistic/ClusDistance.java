/*    */ package clus.statistic;
/*    */ 
/*    */ import clus.data.rows.DataTuple;
/*    */ import java.io.Serializable;
/*    */ 
/*    */ 
/*    */ public class ClusDistance
/*    */   implements Serializable
/*    */ {
/*    */   public static final long serialVersionUID = 1L;
/*    */   
/*    */   public double calcDistance(DataTuple t1, DataTuple t2) {
/* 13 */     return Double.POSITIVE_INFINITY;
/*    */   }
/*    */   
/*    */   public double calcDistanceToCentroid(DataTuple t1, ClusStatistic s2) {
/* 17 */     return Double.POSITIVE_INFINITY;
/*    */   }
/*    */   
/*    */   public ClusDistance getBasicDistance() {
/* 21 */     return this;
/*    */   }
/*    */   
/*    */   public String getDistanceName() {
/* 25 */     return "UnknownDistance";
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\statistic\ClusDistance.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */