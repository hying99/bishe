/*     */ package jeans.graph.swing;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.FontMetrics;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Insets;
/*     */ import java.util.Vector;
/*     */ import javax.swing.JComponent;
/*     */ import jeans.graph.awt.MessageWrapper;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class MessageComponent
/*     */   extends JComponent
/*     */ {
/*     */   public static final long serialVersionUID = 1L;
/*     */   public static final int ALIGN_TOP = 0;
/*     */   public static final int ALIGN_BOTTOM = 1;
/*  38 */   protected Vector messages = new Vector();
/*     */   protected int m_offs;
/*  40 */   protected int m_limit = 50;
/*     */   protected int m_wd;
/*  42 */   protected int m_align = 0; protected int m_hi;
/*     */   
/*     */   public MessageComponent(int wd, int hi) {
/*  45 */     this.m_wd = wd;
/*  46 */     this.m_hi = hi;
/*     */   }
/*     */   
/*     */   public void setAlign(int align) {
/*  50 */     this.m_align = align;
/*     */   }
/*     */   
/*     */   public Dimension getPreferredSize() {
/*  54 */     FontMetrics fm = getGraphics().getFontMetrics();
/*  55 */     Insets insets = getInsets();
/*  56 */     int hi = insets.top + insets.bottom;
/*  57 */     int wd = insets.left + insets.right;
/*  58 */     return new Dimension(this.m_wd * fm.getMaxAdvance() + wd, this.m_hi * fm.getHeight() + hi);
/*     */   }
/*     */   
/*     */   public void addMessage(MessageWrapper wrapper) {
/*  62 */     this.messages.insertElementAt(wrapper, 0);
/*  63 */     int size = this.messages.size();
/*  64 */     for (int ctr = size - 1; ctr >= this.m_limit; ctr--)
/*  65 */       this.messages.removeElementAt(ctr); 
/*  66 */     repaint();
/*     */   }
/*     */   
/*     */   public void removeLast() {
/*  70 */     int size = this.messages.size();
/*  71 */     if (size > 0) this.messages.removeElementAt(0); 
/*     */   }
/*     */   
/*     */   public void removeAll() {
/*  75 */     this.messages.removeAllElements();
/*  76 */     this.m_offs = 0;
/*     */   }
/*     */   
/*     */   public void setLimit(int limit) {
/*  80 */     this.m_limit = limit;
/*     */   }
/*     */   
/*     */   public int getNbMessages() {
/*  84 */     return this.messages.size();
/*     */   }
/*     */   
/*     */   public void setOffs(int offs) {
/*  88 */     this.m_offs = offs;
/*     */   }
/*     */   
/*     */   public MessageWrapper getMessage(int which) {
/*  92 */     return this.messages.elementAt(which);
/*     */   }
/*     */   
/*     */   public int paintMessageBottom(MessageWrapper msg, Graphics g, FontMetrics fm, int xp, int yp, int wd) {
/*  96 */     int mwd = wd - xp - 5;
/*  97 */     msg.mayBeWrap(fm, g, mwd);
/*  98 */     int hi = msg.getHeight();
/*  99 */     g.setColor(Color.black);
/* 100 */     msg.drawWrapped(g, xp + 3, yp - hi);
/* 101 */     return hi;
/*     */   }
/*     */   
/*     */   public int paintMessageTop(MessageWrapper msg, Graphics g, FontMetrics fm, int xp, int yp, int wd) {
/* 105 */     int mwd = wd - xp - 5;
/* 106 */     msg.mayBeWrap(fm, g, mwd);
/* 107 */     int hi = msg.getHeight();
/* 108 */     g.setColor(Color.black);
/* 109 */     msg.drawWrapped(g, xp + 3, yp);
/* 110 */     return hi;
/*     */   }
/*     */   
/*     */   public void paintComponent(Graphics g) {
/* 114 */     super.paintComponent(g);
/* 115 */     Insets insets = getInsets();
/* 116 */     int wd = getWidth() - insets.left - insets.right;
/* 117 */     int hi = getHeight() - insets.top - insets.bottom;
/* 118 */     int idx = this.m_offs;
/* 119 */     int nb = this.messages.size();
/* 120 */     FontMetrics fm = g.getFontMetrics();
/* 121 */     if (this.m_align == 1) {
/* 122 */       int yp = hi + fm.getMaxAscent() - 3 + insets.top;
/* 123 */       while (yp > 0 && idx < nb) {
/* 124 */         MessageWrapper msg = this.messages.elementAt(idx++);
/* 125 */         yp -= paintMessageBottom(msg, g, fm, 3 + insets.left, yp, wd);
/*     */       } 
/*     */     } else {
/* 128 */       int yp = fm.getMaxAscent() + 3 + insets.top;
/* 129 */       while (yp > 0 && idx >= 0 && idx < nb) {
/* 130 */         MessageWrapper msg = this.messages.elementAt(idx--);
/* 131 */         yp += paintMessageTop(msg, g, fm, 3 + insets.left, yp, wd);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jeans\graph\swing\MessageComponent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */