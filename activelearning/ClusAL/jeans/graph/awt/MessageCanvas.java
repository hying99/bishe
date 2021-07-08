/*    */ package jeans.graph.awt;
/*    */ 
/*    */ import java.awt.Color;
/*    */ import java.awt.Dimension;
/*    */ import java.awt.FontMetrics;
/*    */ import java.awt.Graphics;
/*    */ import java.awt.SystemColor;
/*    */ import java.util.Vector;
/*    */ import jeans.graph.BufferCanvas;
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
/*    */ public class MessageCanvas
/*    */   extends BufferCanvas
/*    */ {
/*    */   public static final long serialVersionUID = 1L;
/* 34 */   protected Vector messages = new Vector();
/*    */   protected int m_offs;
/* 36 */   protected int m_limit = 50;
/*    */   
/*    */   public MessageCanvas(int wd, int hi) {
/* 39 */     super(wd, hi);
/*    */   }
/*    */   
/*    */   public void addMessage(MessageWrapper wrapper) {
/* 43 */     this.messages.insertElementAt(wrapper, 0);
/* 44 */     int size = this.messages.size();
/* 45 */     for (int ctr = size - 1; ctr >= this.m_limit; ctr--)
/* 46 */       this.messages.removeElementAt(ctr); 
/* 47 */     redraw();
/*    */   }
/*    */   
/*    */   public void removeLast() {
/* 51 */     int size = this.messages.size();
/* 52 */     if (size > 0) this.messages.removeElementAt(0); 
/*    */   }
/*    */   
/*    */   public void removeAll() {
/* 56 */     this.messages.removeAllElements();
/* 57 */     this.m_offs = 0;
/*    */   }
/*    */   
/*    */   public void setLimit(int limit) {
/* 61 */     this.m_limit = limit;
/*    */   }
/*    */   
/*    */   public int getNbMessages() {
/* 65 */     return this.messages.size();
/*    */   }
/*    */   
/*    */   public void setOffs(int offs) {
/* 69 */     this.m_offs = offs;
/*    */   }
/*    */   
/*    */   public MessageWrapper getMessage(int which) {
/* 73 */     return this.messages.elementAt(which);
/*    */   }
/*    */   
/*    */   public int paintMessage(MessageWrapper msg, Graphics g, FontMetrics fm, int yp, int wd) {
/* 77 */     int xp = 3;
/* 78 */     int mwd = wd - xp - 5;
/* 79 */     msg.mayBeWrap(fm, g, mwd);
/* 80 */     int hi = msg.getHeight();
/* 81 */     g.setColor(Color.black);
/* 82 */     msg.drawWrapped(g, xp + 3, yp - hi);
/* 83 */     return hi;
/*    */   }
/*    */   
/*    */   public void paintIt(Graphics g, Dimension d) {
/* 87 */     int wd = d.width;
/* 88 */     int idx = this.m_offs;
/* 89 */     g.setColor(SystemColor.control);
/* 90 */     g.fillRect(0, 0, d.width, d.height);
/* 91 */     int nb = this.messages.size();
/* 92 */     FontMetrics fm = g.getFontMetrics();
/* 93 */     int yp = d.height + fm.getMaxAscent() - 3;
/* 94 */     while (yp > 0 && idx < nb) {
/* 95 */       MessageWrapper msg = this.messages.elementAt(idx++);
/* 96 */       yp -= paintMessage(msg, g, fm, yp, wd);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jeans\graph\awt\MessageCanvas.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */