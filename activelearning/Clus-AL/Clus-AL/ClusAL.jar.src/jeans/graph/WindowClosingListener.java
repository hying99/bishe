/*    */ package jeans.graph;
/*    */ 
/*    */ import java.awt.Window;
/*    */ import java.awt.event.ActionEvent;
/*    */ import java.awt.event.ActionListener;
/*    */ import java.awt.event.WindowAdapter;
/*    */ import java.awt.event.WindowEvent;
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
/*    */ public class WindowClosingListener
/*    */   extends WindowAdapter
/*    */   implements ActionListener
/*    */ {
/*    */   public static final int TYPE_EXIT = 0;
/*    */   public static final int TYPE_DISPOSE = 1;
/*    */   public static final int TYPE_INVISIBLE = 2;
/*    */   private int type;
/*    */   private Window frame;
/*    */   
/*    */   public WindowClosingListener() {
/* 38 */     this.type = 0;
/*    */   }
/*    */   
/*    */   public WindowClosingListener(Window frame, boolean hide) {
/* 42 */     this.type = hide ? 2 : 1;
/* 43 */     this.frame = frame;
/*    */   }
/*    */   
/*    */   public WindowClosingListener(Window frame, int type) {
/* 47 */     this.type = type;
/* 48 */     this.frame = frame;
/*    */   }
/*    */   
/*    */   public void doClose() {
/* 52 */     if (this.type == 0) { System.exit(0); }
/* 53 */     else if (this.type == 1) { this.frame.dispose(); }
/* 54 */     else if (this.type == 2) { this.frame.setVisible(false); }
/*    */   
/*    */   }
/*    */   public void windowClosing(WindowEvent e) {
/* 58 */     doClose();
/*    */   }
/*    */   
/*    */   public void actionPerformed(ActionEvent e) {
/* 62 */     doClose();
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\jeans\graph\WindowClosingListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */