/*    */ package jeans.graph.drawable;
/*    */ 
/*    */ import java.awt.Color;
/*    */ import java.awt.Font;
/*    */ import java.awt.FontMetrics;
/*    */ import java.awt.Graphics;
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
/*    */ public class DrawableLine
/*    */   extends Drawable
/*    */ {
/*    */   public static final int LOWER = 0;
/*    */   public static final int CENTER = 1;
/*    */   public static final int UPPER = 2;
/*    */   String line;
/*    */   Font font;
/*    */   boolean spacing;
/* 36 */   Color color = Color.black;
/*    */   int xorient;
/*    */   int yorient;
/*    */   
/*    */   public DrawableLine(String line, Font font, boolean spacing) {
/* 41 */     this.line = line;
/* 42 */     this.font = font;
/* 43 */     this.spacing = spacing;
/*    */   }
/*    */   
/*    */   public void setColor(Color color) {
/* 47 */     this.color = color;
/*    */   }
/*    */   
/*    */   public void setOrientation(int xo, int yo) {
/* 51 */     this.xorient = xo;
/* 52 */     this.yorient = yo;
/*    */   }
/*    */   
/*    */   public Rectangle getBoundRect(DrawableProvider prov) {
/* 56 */     if (this.wd == -1 || this.hi == -1) {
/* 57 */       FontMetrics fm = prov.getDMetrics(this.font);
/* 58 */       this.wd = fm.stringWidth(this.line);
/* 59 */       this.hi = getHeight(fm);
/*    */     } 
/* 61 */     return new Rectangle(this.xp, this.yp, this.wd, this.hi);
/*    */   }
/*    */   
/*    */   public int getHeight(FontMetrics fm) {
/* 65 */     if (this.spacing) return fm.getHeight(); 
/* 66 */     return fm.getAscent();
/*    */   }
/*    */   
/*    */   public void draw(DrawableProvider prov, int xofs, int yofs) {
/* 70 */     Graphics g = prov.getDGraphics();
/* 71 */     g.setColor(this.color);
/* 72 */     g.setFont(this.font);
/* 73 */     FontMetrics fm = prov.getDMetrics(this.font);
/* 74 */     int xpos = this.xp - xofs;
/* 75 */     int ypos = this.yp + fm.getAscent() - yofs;
/* 76 */     switch (this.xorient) { case 1:
/* 77 */         xpos = xpos + this.wd / 2 - fm.stringWidth(this.line) / 2; break;
/* 78 */       case 2: xpos = xpos + this.wd - fm.stringWidth(this.line); break; }
/*    */     
/* 80 */     switch (this.yorient) { case 1:
/* 81 */         ypos = ypos + this.hi / 2 - getHeight(fm) / 2; break;
/* 82 */       case 2: ypos = ypos + this.hi - getHeight(fm); break; }
/*    */     
/* 84 */     g.drawString(this.line, xpos, ypos);
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jeans\graph\drawable\DrawableLine.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */