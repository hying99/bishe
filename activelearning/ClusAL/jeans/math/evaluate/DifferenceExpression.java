/*    */ package jeans.math.evaluate;
/*    */ 
/*    */ import jeans.math.MNumber;
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
/*    */ public class DifferenceExpression
/*    */   extends BinaryExpression
/*    */ {
/*    */   public Expression createSimilarExpression() {
/* 30 */     return new DifferenceExpression();
/*    */   }
/*    */   
/*    */   public MNumber getValue() {
/* 34 */     return this.leftexpr.getValue().substract(this.rightexpr.getValue());
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jeans\math\evaluate\DifferenceExpression.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */