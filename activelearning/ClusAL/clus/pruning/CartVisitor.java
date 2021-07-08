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
/*    */ public class CartVisitor
/*    */   extends ErrorVisitor
/*    */ {
/*    */   public double delta_u1;
/*    */   public double delta_u2;
/*    */   public double lambda;
/*    */   public double lambda_min;
/*    */   
/*    */   public ErrorVisitor createInstance() {
/* 33 */     return new CartVisitor();
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\pruning\CartVisitor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */