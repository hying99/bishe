/*     */ package jeans.graph;
/*     */ 
/*     */ import java.awt.Canvas;
/*     */ import java.awt.Color;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Font;
/*     */ import java.awt.FontMetrics;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Image;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.Toolkit;
/*     */ import java.io.IOException;
/*     */ import java.io.Reader;
/*     */ import java.util.Vector;
/*     */ import jeans.graph.drawable.Drawable;
/*     */ import jeans.graph.drawable.DrawableImage;
/*     */ import jeans.graph.drawable.DrawableLine;
/*     */ import jeans.graph.drawable.DrawableProvider;
/*     */ import jeans.resource.MediaCache;
/*     */ import jeans.util.MStreamTokenizer;
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
/*     */ public class TextScroller
/*     */   extends BufferCanvas
/*     */   implements DrawableProvider
/*     */ {
/*     */   public static final long serialVersionUID = 1L;
/*  40 */   private int diff = 2;
/*  41 */   private int border = 2;
/*  42 */   private int yofs = 0;
/*  43 */   private int init_delay = 3000;
/*  44 */   private int next_delay = 50;
/*  45 */   private int incr_y = 1;
/*  46 */   private int max_y = 0;
/*  47 */   Dimension d = null;
/*     */   private boolean running = false;
/*  49 */   private Vector drawables = new Vector();
/*  50 */   private Color cr_color = Color.green;
/*  51 */   private Font cr_font = new Font("Times", 0, 12);
/*  52 */   private Toolkit toolkit = Toolkit.getDefaultToolkit();
/*     */   
/*     */   public TextScroller(int wd, int hi) {
/*  55 */     super(wd, hi);
/*     */   }
/*     */   
/*     */   public void paintIt(Graphics g, Dimension d) {
/*  59 */     this.d = d;
/*  60 */     updateRegion(this.bufGrp, this.border, d.height - this.border, d.width - 2 * this.border);
/*     */   }
/*     */   
/*     */   public void updateRegion(Graphics g, int y0, int y1, int w) {
/*  64 */     Color c = getBackground();
/*  65 */     int hi = y1 - y0 + 1;
/*  66 */     g.setColor(c);
/*  67 */     g.fillRect(this.border, y0, w - 2 * this.border, hi);
/*  68 */     int size = this.drawables.size();
/*  69 */     if (size == 0)
/*  70 */       return;  int cr_drawable = 0;
/*  71 */     Drawable drawable = this.drawables.elementAt(cr_drawable);
/*     */ 
/*     */     
/*  74 */     while (cr_drawable < size - 1 && drawable.isInY(y1 + this.yofs) != -1) {
/*  75 */       drawable.draw(this, 0, this.yofs);
/*  76 */       drawable = this.drawables.elementAt(++cr_drawable);
/*     */     } 
/*  78 */     if (drawable.isInY(y1 + this.yofs) != -1)
/*  79 */       drawable.draw(this, 0, this.yofs); 
/*     */   }
/*     */   
/*     */   public Graphics getDGraphics() {
/*  83 */     return this.bufGrp;
/*     */   }
/*     */   
/*     */   public Canvas getDCanvas() {
/*  87 */     return this;
/*     */   }
/*     */   
/*     */   public FontMetrics getDMetrics(Font font) {
/*  91 */     return this.toolkit.getFontMetrics(font);
/*     */   }
/*     */   
/*     */   public void startScrolling() {
/*  95 */     this.running = true;
/*  96 */     Thread thread = new ScrollThread();
/*  97 */     thread.start();
/*     */   }
/*     */   
/*     */   public void stopScrolling() {
/* 101 */     this.running = false;
/*     */   }
/*     */   
/*     */   public void setScrollText(Reader reader) throws IOException {
/* 105 */     String token = null;
/* 106 */     MStreamTokenizer tokens = new MStreamTokenizer(reader);
/* 107 */     int xp = 5;
/* 108 */     int yp = 4;
/*     */     while (true) {
/* 110 */       if (tokens.isNextToken(":")) {
/* 111 */         token = tokens.readToken();
/* 112 */         if (token.equals("COLOR")) setColor(tokens); 
/* 113 */         if (token.equals("FONT")) setFont(tokens); 
/* 114 */         if (token.equals("XTAB")) xp += setTab(tokens); 
/* 115 */         if (token.equals("YTAB")) yp += setTab(tokens); 
/* 116 */         if (token.equals("IMAGE")) {
/* 117 */           DrawableImage image = setImage(tokens);
/* 118 */           image.setPosition(xp, yp);
/* 119 */           Rectangle rect = image.getBoundRect(this);
/* 120 */           yp += rect.height + this.diff;
/* 121 */           this.drawables.addElement(image);
/*     */         } 
/*     */       } else {
/* 124 */         token = tokens.readTillEol();
/* 125 */         if (token != null) {
/* 126 */           DrawableLine line = new DrawableLine(token, this.cr_font, false);
/* 127 */           line.setPosition(xp, yp);
/* 128 */           line.setColor(this.cr_color);
/* 129 */           Rectangle rect = line.getBoundRect(this);
/* 130 */           yp += rect.height + this.diff;
/* 131 */           this.drawables.addElement(line);
/*     */         } 
/*     */       } 
/* 134 */       if (token == null) {
/* 135 */         this.max_y = yp;
/*     */         return;
/*     */       } 
/*     */     }  } private void setColor(MStreamTokenizer tokens) throws IOException {
/* 139 */     String red = tokens.readToken();
/* 140 */     String green = tokens.readToken();
/* 141 */     String blue = tokens.readToken();
/* 142 */     int cred = 0;
/* 143 */     int cgreen = 0;
/* 144 */     int cblue = 0;
/*     */     try {
/* 146 */       cred = Integer.parseInt(red);
/* 147 */       cgreen = Integer.parseInt(green);
/* 148 */       cblue = Integer.parseInt(blue);
/* 149 */     } catch (NumberFormatException numberFormatException) {}
/* 150 */     this.cr_color = new Color(cred, cgreen, cblue);
/*     */   }
/*     */   
/*     */   private void setFont(MStreamTokenizer tokens) throws IOException {
/* 154 */     String name = tokens.readToken();
/* 155 */     String type = tokens.readToken();
/* 156 */     String size = tokens.readToken();
/* 157 */     int ftype = 0;
/* 158 */     int fsize = 12;
/* 159 */     if (type.equals("BOLD")) ftype = 1; 
/* 160 */     if (type.equals("ITALIC")) ftype = 2; 
/*     */     try {
/* 162 */       fsize = Integer.parseInt(size);
/* 163 */     } catch (NumberFormatException numberFormatException) {}
/* 164 */     this.cr_font = new Font(name, ftype, fsize);
/*     */   }
/*     */   
/*     */   private int setTab(MStreamTokenizer tokens) throws IOException {
/* 168 */     String tab = tokens.readToken();
/* 169 */     int ftab = 0;
/*     */     try {
/* 171 */       ftab = Integer.parseInt(tab);
/* 172 */     } catch (NumberFormatException numberFormatException) {}
/* 173 */     return ftab;
/*     */   }
/*     */   
/*     */   private DrawableImage setImage(MStreamTokenizer tokens) throws IOException {
/* 177 */     String image = tokens.readToken();
/* 178 */     Image fimage = MediaCache.getInstance().getImage(image);
/* 179 */     return new DrawableImage(fimage);
/*     */   }
/*     */   
/*     */   private class ScrollThread extends Thread { private ScrollThread() {}
/*     */     
/*     */     public void run() {
/*     */       try {
/* 186 */         Thread.sleep(TextScroller.this.init_delay);
/* 187 */         while (TextScroller.this.running) {
/*     */           
/* 189 */           TextScroller.this.bufGrp.setClip(TextScroller.this.border, TextScroller.this.border, TextScroller.this.d.width - 2 * TextScroller.this.border, TextScroller.this.d.height - 2 * TextScroller.this.border);
/* 190 */           TextScroller.this.bufGrp.copyArea(TextScroller.this.border, TextScroller.this.border + TextScroller.this.incr_y, TextScroller.this.d.width - 2 * TextScroller.this.border, TextScroller.this.d.height - TextScroller.this.incr_y - TextScroller.this.border, 0, -TextScroller.this.incr_y);
/* 191 */           TextScroller.this.bufGrp.setClip(TextScroller.this.border, TextScroller.this.d.height - TextScroller.this.incr_y - TextScroller.this.border, TextScroller.this.d.width - 2 * TextScroller.this.border, TextScroller.this.incr_y);
/* 192 */           TextScroller.this.updateRegion(TextScroller.this.bufGrp, TextScroller.this.d.height - TextScroller.this.incr_y - TextScroller.this.border, TextScroller.this.d.height - TextScroller.this.border, TextScroller.this.d.width);
/*     */           
/* 194 */           TextScroller.this.repaint();
/*     */           
/* 196 */           TextScroller.this.yofs = TextScroller.this.yofs + TextScroller.this.incr_y;
/* 197 */           if (TextScroller.this.yofs > TextScroller.this.max_y - TextScroller.this.d.height * 70 / 100) {
/* 198 */             TextScroller.this.yofs = -TextScroller.this.d.height;
/*     */           }
/* 200 */           Thread.sleep(TextScroller.this.next_delay);
/*     */         } 
/* 202 */       } catch (InterruptedException interruptedException) {}
/*     */     } }
/*     */ 
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jeans\graph\TextScroller.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */