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
/*    */ public class EvaluateException
/*    */   extends Exception
/*    */ {
/*    */   public static final long serialVersionUID = 1L;
/*    */   int pos;
/*    */   
/*    */   public EvaluateException(String msg, int pos) {
/* 32 */     super(msg);
/* 33 */     this.pos = pos;
/*    */   }
/*    */   
/*    */   public String toString() {
/* 37 */     return getMessage() + " - " + this.pos;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jeans\math\evaluate\EvaluateException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */