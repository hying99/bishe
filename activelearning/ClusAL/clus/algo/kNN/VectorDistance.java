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
/*    */ 
/*    */ 
/*    */ public abstract class VectorDistance
/*    */ {
/*    */   private ClusAttrType[] $attrs;
/*    */   private double[] $weights;
/*    */   
/*    */   public VectorDistance(ClusAttrType[] attrs, double[] weights) {
/* 39 */     setAttribs(attrs);
/* 40 */     this.$weights = weights;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public int amountAttribs() {
/* 46 */     return this.$attrs.length;
/*    */   }
/*    */   public void setAttribs(ClusAttrType[] attrs) {
/* 49 */     this.$attrs = attrs;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public ClusAttrType getAttrib(int idx) {
/* 57 */     return this.$attrs[idx];
/*    */   }
/*    */   
/*    */   public double getWeight(int idx) {
/* 61 */     return this.$weights[idx];
/*    */   }
/*    */   
/*    */   public abstract double getDistance(DataTuple paramDataTuple1, DataTuple paramDataTuple2);
/*    */   
/*    */   public abstract String toString();
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\algo\kNN\VectorDistance.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */