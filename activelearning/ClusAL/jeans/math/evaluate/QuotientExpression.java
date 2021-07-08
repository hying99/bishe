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
/*    */ public class QuotientExpression
/*    */   extends BinaryExpression
/*    */ {
/*    */   public Expression createSimilarExpression() {
/* 30 */     return new QuotientExpression();
/*    */   }
/*    */   
/*    */   public MNumber getValue() {
/* 34 */     return this.leftexpr.getValue().divide(this.rightexpr.getValue());
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jeans\math\evaluate\QuotientExpression.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */