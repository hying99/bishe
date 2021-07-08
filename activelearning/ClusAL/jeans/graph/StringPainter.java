/*    */ package jeans.graph;
/*    */ 
/*    */ import java.awt.Canvas;
/*    */ import java.awt.Color;
/*    */ import java.awt.Dimension;
/*    */ import java.awt.FontMetrics;
/*    */ import java.awt.Graphics;
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
/*    */ public class StringPainter
/*    */   implements Painter
/*    */ {
/*    */   BufferCanvas cnv;
/*    */   String string;
/*    */   
/*    */   public StringPainter(String string, BufferCanvas cnv) {
/* 33 */     this.string = string;
/* 34 */     this.cnv = cnv;
/*    */   }
/*    */   
/*    */   public void paint(Graphics graph, Canvas canvas) {
/* 38 */     Dimension dim = this.cnv.getBufferSize();
/* 39 */     this.cnv.setFont(graph, dim.width, this.string);
/* 40 */     FontMetrics fm = graph.getFontMetrics();
/* 41 */     int hi = fm.getHeight() + 6;
/* 42 */     int wd = fm.stringWidth(this.string) + 10;
/* 43 */     int x0 = (dim.width - wd) / 2;
/* 44 */     int ylvl = dim.height / 2;
/* 45 */     graph.setColor(Color.white);
/* 46 */     graph.fillRect(x0, ylvl - hi, wd, hi);
/* 47 */     graph.setColor(Color.black);
/* 48 */     graph.drawRect(x0, ylvl - hi, wd, hi);
/* 49 */     graph.setColor(Color.red);
/* 50 */     graph.drawString(this.string, x0 + 5, ylvl - fm.getDescent() - 3);
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jeans\graph\StringPainter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */