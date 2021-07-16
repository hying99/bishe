/*    */ package jeans.math.evaluate;
/*    */ 
/*    */ import jeans.math.MComplex;
/*    */ import jeans.math.MLong;
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
/*    */ public class EvaluatorBuilder
/*    */ {
/*    */   public static Evaluator getEvaluator() {
/* 31 */     Evaluator evaluator = new Evaluator();
/* 32 */     evaluator.addInfixBinaryExpression("+", 0, new SumExpression());
/* 33 */     evaluator.addInfixBinaryExpression("-", 0, new DifferenceExpression());
/* 34 */     evaluator.addInfixBinaryExpression("*", 1, new ProductExpression());
/* 35 */     evaluator.addInfixBinaryExpression("/", 1, new QuotientExpression());
/* 36 */     evaluator.addConstant("i", (MNumber)new MComplex((MNumber)MLong.ZERO, (MNumber)MLong.ONE));
/* 37 */     return evaluator;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\jeans\math\evaluate\EvaluatorBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */