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
/*    */ public class MathConfigurationException
/*    */   extends MathException
/*    */   implements Serializable
/*    */ {
/*    */   static final long serialVersionUID = -7958299004965931723L;
/*    */   
/*    */   public MathConfigurationException() {
/* 33 */     this(null, null);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public MathConfigurationException(String message) {
/* 41 */     this(message, null);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public MathConfigurationException(String message, Throwable throwable) {
/* 52 */     super(message, throwable);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public MathConfigurationException(Throwable throwable) {
/* 60 */     this(null, throwable);
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\commons-math-1.0.jar!\org\apache\commons\math\MathConfigurationException.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */