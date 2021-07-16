/*    */ package clus.algo.kNN;
/*    */ 
/*    */ import clus.data.rows.DataTuple;
/*    */ import clus.data.type.ClusAttrType;
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
/*    */ public class ManhattanDistance
/*    */   extends VectorDistance
/*    */ {
/*    */   public ManhattanDistance(ClusAttrType[] attrs, double[] weights) {
/* 35 */     super(attrs, weights);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public double getDistance(DataTuple t1, DataTuple t2) {
/* 42 */     double dist = 0.0D;
/*    */ 
/*    */     
/* 45 */     for (int i = 0; i < amountAttribs(); i++) {
/*    */       
/* 47 */       double curDist = getAttrib(i).getBasicDistance(t1, t2);
/*    */ 
/*    */       
/* 50 */       dist += getWeight(i) * curDist;
/*    */     } 
/*    */ 
/*    */     
/* 54 */     return dist;
/*    */   }
/*    */   public String toString() {
/* 57 */     return "Manhattan";
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\algo\kNN\ManhattanDistance.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */