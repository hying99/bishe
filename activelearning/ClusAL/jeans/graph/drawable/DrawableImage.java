/*    */ package jeans.graph.drawable;
/*    */ 
/*    */ import java.awt.Graphics;
/*    */ import java.awt.Image;
/*    */ import java.awt.Rectangle;
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
/*    */ public class DrawableImage
/*    */   extends Drawable
/*    */ {
/*    */   Image image;
/*    */   
/*    */   public DrawableImage(Image image) {
/* 33 */     this.image = image;
/*    */   }
/*    */   
/*    */   public Rectangle getBoundRect(DrawableProvider prov) {
/* 37 */     if (this.wd == -1 || this.hi == -1) {
/* 38 */       this.wd = this.image.getWidth(prov.getDCanvas());
/* 39 */       this.hi = this.image.getWidth(prov.getDCanvas());
/*    */     } 
/* 41 */     return new Rectangle(this.xp, this.yp, this.wd, this.hi);
/*    */   }
/*    */   
/*    */   public void draw(DrawableProvider prov, int xofs, int yofs) {
/* 45 */     Graphics g = prov.getDGraphics();
/* 46 */     int xpos = this.xp - xofs;
/* 47 */     int ypos = this.yp - yofs;
/* 48 */     g.drawImage(this.image, xpos, ypos, prov.getDCanvas());
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jeans\graph\drawable\DrawableImage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */