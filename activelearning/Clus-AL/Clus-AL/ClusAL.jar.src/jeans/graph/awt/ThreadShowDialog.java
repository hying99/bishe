/*    */ package jeans.graph.awt;
/*    */ 
/*    */ import java.awt.Dialog;
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
/*    */ public class ThreadShowDialog
/*    */   extends Thread
/*    */ {
/*    */   protected Dialog m_dialog;
/*    */   
/*    */   public ThreadShowDialog(Dialog dialog) {
/* 32 */     this.m_dialog = dialog;
/*    */   }
/*    */   
/*    */   public void run() {
/* 36 */     this.m_dialog.setVisible(true);
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\jeans\graph\awt\ThreadShowDialog.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */