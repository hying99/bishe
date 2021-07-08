/*    */ package jeans.graph.swing.drawable;
/*    */ 
/*    */ import java.awt.Color;
/*    */ import java.awt.Graphics2D;
/*    */ import java.awt.event.ActionEvent;
/*    */ import java.awt.event.ActionListener;
/*    */ import java.awt.event.MouseEvent;
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
/*    */ public class DrawableExpandButton
/*    */   extends Drawable
/*    */ {
/*    */   protected boolean m_bState = true;
/*    */   protected ActionListener m_hListener;
/* 32 */   protected Color m_Background = Color.white;
/*    */   
/*    */   public DrawableExpandButton(int wd, int hi, boolean state) {
/* 35 */     this.wd = wd;
/* 36 */     this.hi = hi;
/* 37 */     this.m_bState = state;
/*    */   }
/*    */   
/*    */   public void setBackground(Color color) {
/* 41 */     this.m_Background = color;
/*    */   }
/*    */   
/*    */   public void draw(Graphics2D g, DrawableCanvas canvas, int xofs, int yofs) {
/* 45 */     g.setStroke(DrawableCanvas.SINGLE_STROKE);
/* 46 */     g.setColor(this.m_Background);
/* 47 */     g.fillRect(this.xp - xofs, this.yp - yofs, this.wd, this.hi);
/* 48 */     g.setColor(Color.black);
/* 49 */     g.drawRect(this.xp - xofs, this.yp - yofs, this.wd, this.hi);
/* 50 */     int ymid = this.yp - yofs + this.hi / 2;
/* 51 */     g.drawLine(this.xp - xofs + 2, ymid, this.xp - xofs + this.wd - 2, ymid);
/* 52 */     if (this.m_bState) {
/* 53 */       int xmid = this.xp - xofs + this.wd / 2;
/* 54 */       g.drawLine(xmid, this.yp - yofs + 2, xmid, this.yp - yofs + this.hi - 2);
/*    */     } 
/*    */   }
/*    */   
/*    */   public boolean getState() {
/* 59 */     return this.m_bState;
/*    */   }
/*    */   
/*    */   public void setState(boolean state) {
/* 63 */     this.m_bState = state;
/*    */   }
/*    */   
/*    */   public void setActionListener(ActionListener listener) {
/* 67 */     this.m_hListener = listener;
/*    */   }
/*    */   
/*    */   public boolean mousePressed(DrawableCanvas canvas, int x, int y, MouseEvent evt) {
/* 71 */     this.m_bState = !this.m_bState;
/* 72 */     if (this.m_hListener != null) this.m_hListener.actionPerformed(new ActionEvent(this, 0, "")); 
/* 73 */     return true;
/*    */   }
/*    */   
/*    */   public boolean mouseSensitive() {
/* 77 */     return true;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jeans\graph\swing\drawable\DrawableExpandButton.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */