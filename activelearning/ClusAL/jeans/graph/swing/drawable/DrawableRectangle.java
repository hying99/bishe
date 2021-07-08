/*    */ package jeans.graph.swing.drawable;
/*    */ 
/*    */ import java.awt.Color;
/*    */ import java.awt.Graphics2D;
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
/*    */ public class DrawableRectangle
/*    */   extends Drawable
/*    */ {
/*    */   private Color color;
/*    */   
/*    */   public DrawableRectangle(int xp, int yp, int wd, int hi, Color color) {
/* 32 */     this.xp = xp;
/* 33 */     this.yp = yp;
/* 34 */     this.wd = wd;
/* 35 */     this.hi = hi;
/* 36 */     this.color = color;
/*    */   }
/*    */   
/*    */   public void draw(Graphics2D g, DrawableCanvas canvas, int xofs, int yofs) {
/* 40 */     g.setColor(this.color);
/* 41 */     g.fillRect(this.xp - xofs, this.yp - yofs, this.wd, this.hi);
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jeans\graph\swing\drawable\DrawableRectangle.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */