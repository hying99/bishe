/*     */ package jeans.graph.awt;
/*     */ 
/*     */ import java.awt.FontMetrics;
/*     */ import java.awt.Graphics;
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
/*     */ 
/*     */ public class MessageWrapper
/*     */ {
/*     */   protected static final int GROW = 2;
/*     */   protected String m_msg;
/*     */   protected int m_hi;
/*     */   protected int m_wd;
/*     */   protected int[] m_wrap;
/*     */   protected int m_nb;
/*     */   
/*     */   public MessageWrapper(String msg) {
/*  37 */     this.m_msg = msg;
/*  38 */     this.m_wrap = new int[2];
/*     */   }
/*     */   
/*     */   public void doWrap(FontMetrics fm, Graphics g, int wmax) {
/*  42 */     int size = this.m_msg.length();
/*  43 */     boolean done = false;
/*  44 */     int pos = 0;
/*  45 */     int argpos = 0;
/*  46 */     this.m_hi = 0;
/*  47 */     this.m_wd = wmax;
/*  48 */     removeAllElements();
/*  49 */     addInteger(0);
/*  50 */     while (!done) {
/*  51 */       int prevpos = pos;
/*  52 */       for (; pos < size && this.m_msg.charAt(pos) != ' '; pos++);
/*  53 */       if (pos >= size) done = true; 
/*  54 */       String strg = this.m_msg.substring(argpos, pos);
/*  55 */       if (fm.stringWidth(strg) > wmax) {
/*  56 */         if (argpos == prevpos) {
/*  57 */           addInteger(pos);
/*  58 */           for (; pos < size && this.m_msg.charAt(pos) == ' '; pos++);
/*  59 */           addInteger(pos);
/*  60 */           argpos = pos;
/*     */         } else {
/*  62 */           addInteger(prevpos);
/*  63 */           for (; prevpos < size && this.m_msg.charAt(prevpos) == ' '; prevpos++);
/*  64 */           addInteger(prevpos);
/*  65 */           pos = argpos = prevpos;
/*     */         } 
/*  67 */         this.m_hi += fm.getHeight() + 1; continue;
/*     */       } 
/*  69 */       pos++;
/*     */     } 
/*     */     
/*  72 */     if (argpos < size) {
/*  73 */       addInteger(size + 1);
/*  74 */       this.m_hi += fm.getHeight() + 1;
/*     */     } 
/*  76 */     shrink();
/*     */   }
/*     */   
/*     */   public int getLoBoundary(int ctr) {
/*  80 */     return this.m_wrap[ctr * 2];
/*     */   }
/*     */   
/*     */   public int getHiBoundary(int ctr) {
/*  84 */     return this.m_wrap[ctr * 2 + 1] - 1;
/*     */   }
/*     */   
/*     */   public int getNbLines() {
/*  88 */     return this.m_nb / 2;
/*     */   }
/*     */   
/*     */   public String getString() {
/*  92 */     return this.m_msg;
/*     */   }
/*     */   
/*     */   public String getLine(int ctr) {
/*  96 */     return this.m_msg.substring(getLoBoundary(ctr), getHiBoundary(ctr));
/*     */   }
/*     */   
/*     */   public void mayBeWrap(FontMetrics fm, Graphics g, int wmax) {
/* 100 */     if (this.m_wd != wmax) doWrap(fm, g, wmax); 
/*     */   }
/*     */   
/*     */   public void drawWrapped(Graphics g, int xpos, int ypos) {
/* 104 */     int nb = getNbLines();
/* 105 */     if (nb != 0) {
/* 106 */       int txtHi = getHeight() / nb;
/* 107 */       for (int ctr = 0; ctr < nb; ctr++) {
/* 108 */         String strg = getLine(ctr);
/* 109 */         g.drawString(strg, xpos, ypos);
/* 110 */         ypos += txtHi;
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public int getHeight() {
/* 116 */     return this.m_hi;
/*     */   }
/*     */   
/*     */   protected void grow() {
/* 120 */     int size = this.m_wrap.length;
/* 121 */     if (this.m_nb + 1 > size) {
/* 122 */       int[] newWrap = new int[size + 2];
/* 123 */       for (int ctr = 0; ctr < size; ctr++)
/* 124 */         System.arraycopy(this.m_wrap, 0, newWrap, 0, size); 
/* 125 */       this.m_wrap = newWrap;
/*     */     } 
/*     */   }
/*     */   
/*     */   protected void shrink() {
/* 130 */     if (this.m_nb < this.m_wrap.length) {
/* 131 */       int[] newWrap = new int[this.m_nb];
/* 132 */       System.arraycopy(this.m_wrap, 0, newWrap, 0, this.m_nb);
/* 133 */       this.m_wrap = newWrap;
/*     */     } 
/*     */   }
/*     */   
/*     */   protected void addInteger(int value) {
/* 138 */     grow();
/* 139 */     this.m_wrap[this.m_nb++] = value;
/*     */   }
/*     */   
/*     */   protected void removeAllElements() {
/* 143 */     this.m_nb = 0;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\jeans\graph\awt\MessageWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */