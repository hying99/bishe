/*    */ package jeans.graph.swing.drawable;
/*    */ 
/*    */ import java.awt.Color;
/*    */ import java.awt.Graphics2D;
/*    */ import java.awt.Stroke;
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
/*    */ public class DrawableEmptyRect
/*    */   extends Drawable
/*    */ {
/*    */   private Color color;
/*    */   private Stroke stroke;
/*    */   
/*    */   public DrawableEmptyRect(int xp, int yp, int wd, int hi, Color color) {
/* 33 */     this.xp = xp;
/* 34 */     this.yp = yp;
/* 35 */     this.wd = wd;
/* 36 */     this.hi = hi;
/* 37 */     this.color = color;
/*    */   }
/*    */   
/*    */   public void setStroke(Stroke stroke) {
/* 41 */     this.stroke = stroke;
/*    */   }
/*    */   
/*    */   public void draw(Graphics2D g, DrawableCanvas canvas, int xofs, int yofs) {
/* 45 */     g.setColor(this.color);
/* 46 */     g.setStroke(this.stroke);
/* 47 */     g.drawRect(this.xp - xofs, this.yp - yofs, this.wd, this.hi);
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\jeans\graph\swing\drawable\DrawableEmptyRect.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */