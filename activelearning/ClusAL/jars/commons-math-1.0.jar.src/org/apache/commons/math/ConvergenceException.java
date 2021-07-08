/*    */ package org.apache.commons.math;
/*    */ 
/*    */ import java.io.Serializable;
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
/*    */ public class ConvergenceException
/*    */   extends MathException
/*    */   implements Serializable
/*    */ {
/*    */   static final long serialVersionUID = -3657394299929217890L;
/*    */   
/*    */   public ConvergenceException() {
/* 35 */     this(null, null);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public ConvergenceException(String message) {
/* 43 */     this(message, null);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public ConvergenceException(String message, Throwable cause) {
/* 52 */     super(message, cause);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public ConvergenceException(Throwable throwable) {
/* 60 */     this(null, throwable);
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\commons-math-1.0.jar!\org\apache\commons\math\ConvergenceException.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */