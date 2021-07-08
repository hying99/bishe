/*    */ package jeans.graph.swing.drawable;
/*    */ 
/*    */ import java.awt.Color;
/*    */ import java.awt.Font;
/*    */ import java.awt.FontMetrics;
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
/*    */ public class DrawableLines
/*    */   extends Drawable
/*    */ {
/*    */   protected String[] m_hLines;
/*    */   protected Color m_Background;
/*    */   protected Color m_Border;
/* 32 */   protected Color m_Text = Color.black;
/*    */   protected Font m_Font;
/*    */   
/*    */   public DrawableLines(String[] lines) {
/* 36 */     this.m_hLines = lines;
/*    */   }
/*    */   
/*    */   public DrawableLines(String line) {
/* 40 */     this.m_hLines = new String[1];
/* 41 */     this.m_hLines[0] = line;
/*    */   }
/*    */   
/*    */   public void setFont(Font font) {
/* 45 */     this.m_Font = font;
/*    */   }
/*    */   
/*    */   public void setBackground(Color color) {
/* 49 */     this.m_Background = color;
/*    */   }
/*    */   
/*    */   public void setBorder(Color color) {
/* 53 */     this.m_Border = color;
/*    */   }
/*    */   
/*    */   public void calcSize(Graphics2D g, FontMetrics fm, DrawableCanvas canvas) {
/* 57 */     this.wd = 0;
/* 58 */     FontMetrics fm2 = fm;
/* 59 */     if (this.m_Font != null) {
/* 60 */       g.setFont(this.m_Font);
/* 61 */       fm2 = g.getFontMetrics();
/*    */     } 
/* 63 */     this.hi = this.m_hLines.length * fm2.getHeight() + 6;
/* 64 */     for (int i = 0; i < this.m_hLines.length; i++)
/* 65 */       this.wd = Math.max(this.wd, fm2.stringWidth(this.m_hLines[i]) + 6); 
/*    */   }
/*    */   
/*    */   public void draw(Graphics2D g, DrawableCanvas canvas, int xofs, int yofs) {
/* 69 */     if (this.m_Font != null) g.setFont(this.m_Font); 
/* 70 */     FontMetrics fm = g.getFontMetrics();
/* 71 */     int ypos = this.yp - yofs - 1;
/* 72 */     int xpos = this.xp + 3 - xofs;
/* 73 */     if (this.m_Background != null) {
/* 74 */       g.setColor(this.m_Background);
/* 75 */       g.fillRect(xpos, ypos, this.wd, this.hi);
/*    */     } 
/* 77 */     if (this.m_Border != null) {
/* 78 */       g.setColor(this.m_Border);
/* 79 */       g.drawRect(xpos, ypos, this.wd, this.hi);
/*    */     } 
/* 81 */     g.setColor(this.m_Text);
/* 82 */     ypos += fm.getAscent() + 3;
/* 83 */     for (int i = 0; i < this.m_hLines.length; i++) {
/* 84 */       g.drawString(this.m_hLines[i], xpos, ypos);
/* 85 */       ypos += fm.getHeight();
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jeans\graph\swing\drawable\DrawableLines.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */