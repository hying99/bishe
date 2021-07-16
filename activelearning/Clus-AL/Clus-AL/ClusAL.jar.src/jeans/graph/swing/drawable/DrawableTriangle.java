/*    */ package jeans.graph.swing.drawable;
/*    */ 
/*    */ import java.awt.Color;
/*    */ import java.awt.Graphics2D;
/*    */ import java.awt.Polygon;
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
/*    */ public class DrawableTriangle
/*    */   extends Drawable
/*    */ {
/*    */   private Color color;
/*    */   private Stroke stroke;
/*    */   
/*    */   public DrawableTriangle(int xp, int yp, int wd, int hi, Color color) {
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
/* 45 */     drawTriangle(g, this.xp - xofs, this.yp - yofs, this.wd, this.hi);
/*    */   }
/*    */   
/*    */   public void drawTriangle(Graphics2D g, int x, int y, int w, int h) {
/* 49 */     Polygon poly = new Polygon();
/* 50 */     poly.addPoint(x, y + h);
/* 51 */     poly.addPoint(x + w / 2, y);
/* 52 */     poly.addPoint(x + w, y + h);
/* 53 */     g.setColor(this.color);
/* 54 */     g.setStroke(this.stroke);
/* 55 */     g.draw(poly);
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\jeans\graph\swing\drawable\DrawableTriangle.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */