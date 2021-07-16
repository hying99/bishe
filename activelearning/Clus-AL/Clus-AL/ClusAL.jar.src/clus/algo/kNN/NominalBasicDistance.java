/*    */ package clus.algo.kNN;
/*    */ 
/*    */ import clus.data.rows.DataTuple;
/*    */ import clus.data.type.ClusAttrType;
/*    */ import clus.data.type.NominalAttrType;
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
/*    */ public class NominalBasicDistance
/*    */   extends BasicDistance
/*    */ {
/*    */   public double getDistance(ClusAttrType type, DataTuple t1, DataTuple t2) {
/* 43 */     NominalAttrType at = (NominalAttrType)type;
/* 44 */     int x = at.getNominal(t1);
/*    */ 
/*    */     
/* 47 */     if (x == at.getNbValues())
/*    */     {
/*    */       
/* 50 */       x = at.getStatistic().mean();
/*    */     }
/* 52 */     int y = at.getNominal(t2);
/*    */ 
/*    */ 
/*    */     
/* 56 */     if (y == at.getNbValues())
/*    */     {
/* 58 */       y = at.getStatistic().mean();
/*    */     }
/* 60 */     if (x != y) {
/* 61 */       return 1.0D;
/*    */     }
/* 63 */     return 0.0D;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\algo\kNN\NominalBasicDistance.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */