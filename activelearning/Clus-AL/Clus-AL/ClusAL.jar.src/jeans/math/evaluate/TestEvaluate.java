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
/*    */ 
/*    */ public class TestEvaluate
/*    */ {
/*    */   public static void main(String[] args) {
/* 28 */     if (args.length != 1) {
/* 29 */       System.out.println("Usage: TestEvaluate [expression]");
/*    */     } else {
/* 31 */       String strg = args[0];
/* 32 */       System.out.println("Expression: " + strg);
/* 33 */       Evaluator evaluator = EvaluatorBuilder.getEvaluator();
/*    */       try {
/* 35 */         Expression expr = evaluator.evaluate(strg);
/* 36 */         System.out.println("Evaluated to: " + expr);
/* 37 */         System.out.println("Value: " + expr.getValue());
/* 38 */         System.out.println("Double: " + expr.getValue().getDouble());
/* 39 */       } catch (EvaluateException exp) {
/* 40 */         System.out.println("Exception: " + exp);
/*    */       } 
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\jeans\math\evaluate\TestEvaluate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */