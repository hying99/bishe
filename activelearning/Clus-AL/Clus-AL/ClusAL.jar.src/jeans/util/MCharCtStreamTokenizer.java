/*    */ package jeans.util;
/*    */ 
/*    */ import java.io.Reader;
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
/*    */ public class MCharCtStreamTokenizer
/*    */   extends MStreamTokenizer
/*    */ {
/* 29 */   protected long m_charNo = 0L; protected long m_charModulo = 1L;
/* 30 */   protected CallBackFunction m_callback = null;
/*    */   
/*    */   public MCharCtStreamTokenizer(Reader myreader) {
/* 33 */     super(myreader);
/*    */   }
/*    */   
/*    */   public void setCallbackFunction(long modulo, CallBackFunction callback) {
/* 37 */     this.m_charModulo = modulo;
/* 38 */     this.m_callback = callback;
/*    */   }
/*    */   
/*    */   public long getCharNo() {
/* 42 */     return this.m_charNo;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\jean\\util\MCharCtStreamTokenizer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */