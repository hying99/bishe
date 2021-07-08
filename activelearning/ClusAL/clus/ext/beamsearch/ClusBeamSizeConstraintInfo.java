/*    */ package clus.ext.beamsearch;
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
/*    */ public class ClusBeamSizeConstraintInfo
/*    */ {
/*    */   public Object visitor;
/*    */   public double[] realcost;
/*    */   public double[] lowcost;
/*    */   public double[] bound;
/*    */   public boolean[] computed;
/*    */   boolean marked;
/*    */   
/*    */   public ClusBeamSizeConstraintInfo(int size) {
/* 38 */     this.realcost = new double[size + 1];
/* 39 */     this.lowcost = new double[size + 1];
/* 40 */     this.bound = new double[size + 1];
/* 41 */     this.computed = new boolean[size + 1];
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\ext\beamsearch\ClusBeamSizeConstraintInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */