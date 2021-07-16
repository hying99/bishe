/*    */ package jeans.resource;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.io.InputStreamReader;
/*    */ import java.io.LineNumberReader;
/*    */ import java.util.Vector;
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
/*    */ public class MStringTable
/*    */ {
/* 30 */   protected static Vector m_Vector = new Vector();
/*    */   
/*    */   public static String get(int idx) {
/* 33 */     if (idx >= m_Vector.size()) return null; 
/* 34 */     return m_Vector.elementAt(idx);
/*    */   }
/*    */   
/*    */   public static void load(String fname) throws IOException {
/* 38 */     String line = "";
/* 39 */     String store = "";
/* 40 */     boolean start = true;
/* 41 */     InputStream stream = MediaInterface.getInstance().openStream(fname);
/* 42 */     LineNumberReader reader = new LineNumberReader(new InputStreamReader(stream));
/* 43 */     while (line != null) {
/* 44 */       line = reader.readLine();
/* 45 */       if (line != null && line.length() != 0) {
/* 46 */         if (line.charAt(0) == '*') {
/* 47 */           m_Vector.addElement(store);
/* 48 */           start = true;
/* 49 */           store = ""; continue;
/*    */         } 
/* 51 */         if (!start) store = store + '$'; 
/* 52 */         store = store + line;
/* 53 */         start = false;
/*    */       } 
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\jeans\resource\MStringTable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */