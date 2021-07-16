/*    */ package clus.pruning;
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
/*    */ public class SizeConstraintVisitor
/*    */   extends ErrorVisitor
/*    */ {
/*    */   public double[] cost;
/*    */   public int[] left;
/*    */   public boolean[] computed;
/*    */   public double error;
/*    */   
/*    */   public SizeConstraintVisitor(int size) {
/* 36 */     this.cost = new double[size + 1];
/* 37 */     this.left = new int[size + 1];
/* 38 */     this.computed = new boolean[size + 1];
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\pruning\SizeConstraintVisitor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */