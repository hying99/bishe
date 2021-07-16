/*    */ package clus.gui;
/*    */ 
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
/*    */ 
/*    */ public class ShowTree
/*    */ {
/*    */   public static void main(String[] args) {
/*    */     try {
/* 32 */       SimpleTreeFrame.showTree(args[0]);
/* 33 */     } catch (IOException e) {
/* 34 */       System.out.println("IO Error: " + e.getMessage());
/* 35 */     } catch (ClassNotFoundException e) {
/* 36 */       System.out.println("Class Not Found: " + e.getMessage());
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\gui\ShowTree.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */