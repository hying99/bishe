/*    */ package org.apache.commons.math;
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
/*    */ public class FunctionEvaluationException
/*    */   extends MathException
/*    */ {
/*    */   static final long serialVersionUID = -317289374378977972L;
/* 32 */   private double argument = Double.NaN;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public FunctionEvaluationException(double argument) {
/* 42 */     this(argument, "Evaluation failed for argument = " + argument);
/*    */   }
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
/*    */   public FunctionEvaluationException(double argument, String message) {
/* 55 */     this(argument, message, null);
/*    */   }
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
/*    */   public FunctionEvaluationException(double argument, String message, Throwable cause) {
/* 69 */     super(message + " Evaluation failed for argument=" + argument, cause);
/* 70 */     this.argument = argument;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public double getArgument() {
/* 79 */     return this.argument;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\commons-math-1.0.jar!\org\apache\commons\math\FunctionEvaluationException.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */