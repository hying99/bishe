/*    */ package jeans.util;
/*    */ 
/*    */ import java.io.FileReader;
/*    */ import java.io.IOException;
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
/*    */ public class NSTokenTester
/*    */ {
/*    */   public static void main(String[] args) {
/* 31 */     if (args.length != 1) {
/* 32 */       System.out.println("Illegal number of arguments.");
/*    */       return;
/*    */     } 
/* 35 */     System.out.println("Reading file: " + args[0]);
/*    */     try {
/* 37 */       NStreamTokenizer tokens = new NStreamTokenizer(new FileReader(args[0]));
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */       
/* 43 */       char ch = tokens.readChar();
/* 44 */       while (ch != '\000') {
/* 45 */         System.out.print(ch);
/* 46 */         ch = tokens.readChar();
/*    */       } 
/* 48 */       System.out.println();
/* 49 */       tokens.close();
/* 50 */     } catch (IOException e) {
/* 51 */       System.out.println("Error: " + e);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jean\\util\NSTokenTester.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */