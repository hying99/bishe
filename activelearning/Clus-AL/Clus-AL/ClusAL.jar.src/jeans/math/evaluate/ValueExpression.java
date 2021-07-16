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
/*    */ 
/*    */ public class ValueExpression
/*    */   extends Expression
/*    */ {
/*    */   MNumber value;
/*    */   
/*    */   public ValueExpression() {}
/*    */   
/*    */   public ValueExpression(MNumber value) {
/* 35 */     setValue(value);
/*    */   }
/*    */   
/*    */   public Expression createSimilarExpression() {
/* 39 */     ValueExpression result = new ValueExpression();
/* 40 */     result.setValue(getValue());
/* 41 */     return result;
/*    */   }
/*    */   
/*    */   public void setValue(MNumber value) {
/* 45 */     this.value = value;
/*    */   }
/*    */   
/*    */   public MNumber getValue() {
/* 49 */     return this.value;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\jeans\math\evaluate\ValueExpression.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */