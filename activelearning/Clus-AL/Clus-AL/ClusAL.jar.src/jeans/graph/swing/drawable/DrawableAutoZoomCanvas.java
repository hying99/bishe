/*    */ package jeans.graph.swing.drawable;
/*    */ 
/*    */ import java.awt.event.ComponentAdapter;
/*    */ import java.awt.event.ComponentEvent;
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
/*    */ public class DrawableAutoZoomCanvas
/*    */   extends DrawableCanvas
/*    */ {
/*    */   public static final long serialVersionUID = 1L;
/*    */   
/*    */   public DrawableAutoZoomCanvas() {
/* 32 */     addComponentListener(new MyResizeListener());
/*    */   }
/*    */   
/*    */   private class MyResizeListener
/*    */     extends ComponentAdapter {
/*    */     public void componentResized(ComponentEvent e) {
/* 38 */       DrawableAutoZoomCanvas.this.setRenderState(1);
/*    */     }
/*    */     
/*    */     private MyResizeListener() {}
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\jeans\graph\swing\drawable\DrawableAutoZoomCanvas.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */