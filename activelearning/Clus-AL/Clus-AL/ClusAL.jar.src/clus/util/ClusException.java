/*    */ package clus.util;
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
/*    */ public class ClusException
/*    */   extends Exception
/*    */ {
/*    */   public static final long serialVersionUID = 1L;
/*    */   
/*    */   public ClusException(String msg) {
/* 32 */     super(msg);
/*    */   }
/*    */   
/*    */   public String toString() {
/* 36 */     return getMessage();
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clu\\util\ClusException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */