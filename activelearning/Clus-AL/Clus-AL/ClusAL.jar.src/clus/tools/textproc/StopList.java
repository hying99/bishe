/*    */ package clus.tools.textproc;
/*    */ 
/*    */ import java.io.FileReader;
/*    */ import java.io.IOException;
/*    */ import java.io.LineNumberReader;
/*    */ import java.util.Hashtable;
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
/*    */ public class StopList
/*    */   extends Hashtable
/*    */ {
/*    */   public static final long serialVersionUID = 1L;
/*    */   
/*    */   public StopList() {}
/*    */   
/*    */   public StopList(String filename) throws IOException {
/* 39 */     String dummy = "";
/* 40 */     LineNumberReader reader = new LineNumberReader(new FileReader(filename));
/*    */     while (true) {
/* 42 */       String word = reader.readLine();
/* 43 */       if (word == null)
/* 44 */         break;  put((K)word, (V)dummy);
/*    */     } 
/* 46 */     reader.close();
/*    */   }
/*    */   
/*    */   public boolean hasWord(String word) {
/* 50 */     return containsKey(word);
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\tools\textproc\StopList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */