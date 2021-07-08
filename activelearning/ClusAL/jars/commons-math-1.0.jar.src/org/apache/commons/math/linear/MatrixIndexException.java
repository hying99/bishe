/*    */ package org.apache.commons.math.linear;
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
/*    */ public class MatrixIndexException
/*    */   extends RuntimeException
/*    */ {
/*    */   static final long serialVersionUID = -1341109412864309526L;
/*    */   
/*    */   public MatrixIndexException() {
/* 33 */     this(null);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public MatrixIndexException(String message) {
/* 41 */     super(message);
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\commons-math-1.0.jar!\org\apache\commons\math\linear\MatrixIndexException.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */