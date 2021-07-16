/*    */ package jeans.graph;
/*    */ 
/*    */ import java.awt.Color;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class SyntaxHighlighter
/*    */ {
/*    */   public abstract Color getColor(String paramString);
/*    */   
/*    */   public abstract void parseString(String paramString);
/*    */   
/*    */   public abstract String getColorToken();
/*    */   
/*    */   public abstract Color getColor();
/*    */   
/*    */   public void drawLighted(String strg, Graphics g, int xpos, int ypos) {
/* 38 */     boolean done = false;
/* 39 */     parseString(strg);
/* 40 */     FontMetrics fm = g.getFontMetrics();
/* 41 */     while (!done) {
/* 42 */       String token = getColorToken();
/* 43 */       if (token == null) {
/* 44 */         done = true; continue;
/*    */       } 
/* 46 */       g.setColor(getColor());
/* 47 */       g.drawString(token, xpos, ypos);
/* 48 */       xpos += fm.stringWidth(token);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\jeans\graph\SyntaxHighlighter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */