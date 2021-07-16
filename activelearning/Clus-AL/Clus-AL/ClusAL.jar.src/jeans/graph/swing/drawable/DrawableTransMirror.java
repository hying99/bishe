/*    */ package jeans.graph.swing.drawable;
/*    */ 
/*    */ import java.awt.Dimension;
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
/*    */ public class DrawableTransMirror
/*    */   extends DrawableTransform
/*    */ {
/*    */   public void transform(Drawable drawable) {
/* 30 */     int x = drawable.getX();
/* 31 */     int y = drawable.getY();
/* 32 */     int wd = drawable.getWidth();
/* 33 */     int hi = drawable.getHeight();
/* 34 */     drawable.setXY(y, x);
/* 35 */     drawable.setWidth(hi);
/* 36 */     drawable.setHeight(wd);
/*    */   }
/*    */   
/*    */   public Dimension transformDimension(Dimension dim) {
/* 40 */     return new Dimension(dim.height, dim.width);
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\jeans\graph\swing\drawable\DrawableTransMirror.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */