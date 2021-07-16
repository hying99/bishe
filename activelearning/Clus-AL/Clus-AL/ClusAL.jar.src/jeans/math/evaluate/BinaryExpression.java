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
/*    */ public abstract class BinaryExpression
/*    */   extends Expression
/*    */ {
/*    */   protected Expression leftexpr;
/*    */   protected Expression rightexpr;
/*    */   
/*    */   public BinaryExpression setLeftExpression(Expression left) {
/* 30 */     this.leftexpr = left;
/* 31 */     return this;
/*    */   }
/*    */   
/*    */   public BinaryExpression setRightExpression(Expression right) {
/* 35 */     this.rightexpr = right;
/* 36 */     return this;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\jeans\math\evaluate\BinaryExpression.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */