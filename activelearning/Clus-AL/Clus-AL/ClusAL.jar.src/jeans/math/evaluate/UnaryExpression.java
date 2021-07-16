/*    */ package jeans.math.evaluate;
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
/*    */ public abstract class UnaryExpression
/*    */   extends Expression
/*    */ {
/*    */   protected Expression expr;
/*    */   
/*    */   public void setExpression(Expression expr) {
/* 30 */     this.expr = expr;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\jeans\math\evaluate\UnaryExpression.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */