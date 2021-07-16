/*    */ package jeans.graph;
/*    */ 
/*    */ import java.awt.Color;
/*    */ import java.awt.Graphics;
/*    */ import java.awt.Polygon;
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
/*    */ public class ImageUtil
/*    */ {
/*    */   public static void draw3DRect(Graphics g, int x, int y, int width, int height, int shadow, boolean raised) {
/* 30 */     Color c = g.getColor();
/* 31 */     Color brighter = c.brighter();
/* 32 */     Color darker = c.darker();
/*    */ 
/*    */     
/* 35 */     g.setColor(raised ? brighter : darker); int i;
/* 36 */     for (i = 0; i < shadow; i++) {
/* 37 */       g.drawLine(x + i, y + i, x + width - 1 - i, y + i);
/* 38 */       g.drawLine(x + i, y + i, x + i, y + height - 1 - i);
/*    */     } 
/*    */     
/* 41 */     g.setColor(raised ? darker : brighter);
/* 42 */     for (i = 0; i < shadow; i++) {
/* 43 */       g.drawLine(x + i, y + height - 1 - i, x + width - 1 - i, y + height - 1 - i);
/* 44 */       g.drawLine(x + width - 1 - i, y + height - 1 - i, x + width - 1 - i, y + i);
/*    */     } 
/* 46 */     g.setColor(c);
/*    */   }
/*    */   
/*    */   public static void drawThickRect(Graphics g, int x, int y, int w, int h, int d) {
/* 50 */     g.fillRect(x, y, w, d);
/* 51 */     g.fillRect(x, y + d, d, h - 2 * d);
/* 52 */     g.fillRect(x, y + h - d, w, d);
/* 53 */     g.fillRect(x + w - d, y + d, d, h - 2 * d);
/*    */   }
/*    */ 
/*    */   
/*    */   public static Polygon makeParalRect(int x1, int y1, int x2, int y2, int x3, int y3) {
/* 58 */     Polygon p = new Polygon();
/* 59 */     p.addPoint(x1, y1);
/* 60 */     p.addPoint(x2, y2);
/* 61 */     int x4 = x2 + x3 - x1;
/* 62 */     int y4 = y2 + y3 - y1;
/* 63 */     p.addPoint(x4, y4);
/* 64 */     p.addPoint(x3, y3);
/* 65 */     return p;
/*    */   }
/*    */   
/*    */   public static Polygon makeHexagon(int x, int y, int wd) {
/* 69 */     int side = wd * 55 / 100;
/* 70 */     int delta = (wd - side) / 2;
/* 71 */     Polygon p = new Polygon();
/* 72 */     p.addPoint(x, y);
/* 73 */     p.addPoint(x + delta, y - wd / 2);
/* 74 */     p.addPoint(x + wd - delta, y - wd / 2);
/* 75 */     p.addPoint(x + wd, y);
/* 76 */     p.addPoint(x + wd - delta, y + wd / 2);
/* 77 */     p.addPoint(x + delta, y + wd / 2);
/* 78 */     return p;
/*    */   }
/*    */   
/*    */   public static void drawCube(Graphics g, int x, int y, int w, int h, int d, float fac) {
/* 82 */     int dx = (int)(d * fac);
/* 83 */     int dy = (int)(d * (1.0F - fac));
/* 84 */     Color color = g.getColor();
/* 85 */     g.fillRect(x, y, w + 1, h + 1);
/* 86 */     g.setColor(color.darker());
/* 87 */     Polygon p1 = makeParalRect(x + w, y + h, x + w, y, x + w + dx, y + h - dy);
/* 88 */     Polygon p2 = makeParalRect(x, y, x + dx, y - dy, x + w, y);
/* 89 */     g.fillPolygon(p1);
/* 90 */     g.fillPolygon(p2);
/* 91 */     g.setColor(color);
/* 92 */     g.drawPolygon(p1);
/* 93 */     g.drawPolygon(p2);
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\jeans\graph\ImageUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */