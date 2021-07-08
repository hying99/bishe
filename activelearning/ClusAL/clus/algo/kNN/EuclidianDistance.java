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
/*    */ public class EuclidianDistance
/*    */   extends VectorDistance
/*    */ {
/*    */   public EuclidianDistance(ClusAttrType[] attrs, double[] weights) {
/* 33 */     super(attrs, weights);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public double getDistance(DataTuple t1, DataTuple t2) {
/* 40 */     double dist = 0.0D;
/*    */     
/* 42 */     for (int i = 0; i < amountAttribs(); i++) {
/*    */       
/* 44 */       double curDist = getAttrib(i).getBasicDistance(t1, t2);
/*    */ 
/*    */       
/* 47 */       dist += getWeight(i) * Math.pow(curDist, 2.0D);
/*    */     } 
/*    */ 
/*    */     
/* 51 */     return Math.sqrt(dist);
/*    */   }
/*    */   
/*    */   public String toString() {
/* 55 */     return "Euclidian";
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\algo\kNN\EuclidianDistance.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */