/*     */ package jeans.graph.swing.drawable;
/*     */ 
/*     */ import java.awt.BasicStroke;
/*     */ import java.awt.Color;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.FontMetrics;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.Image;
/*     */ import java.awt.Insets;
/*     */ import java.awt.Point;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.RenderingHints;
/*     */ import java.awt.Stroke;
/*     */ import java.awt.SystemColor;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.MouseAdapter;
/*     */ import java.awt.event.MouseEvent;
/*     */ import java.util.ArrayList;
/*     */ import javax.swing.JComponent;
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
/*     */ public class DrawableCanvas
/*     */   extends JComponent
/*     */ {
/*     */   public static final long serialVersionUID = 1L;
/*     */   public static final int STATE_RENDERERD = 0;
/*     */   public static final int STATE_RENDER_AND_SIZE = 1;
/*     */   public static final int STATE_RENDER = 2;
/*  38 */   public static final Stroke SINGLE_STROKE = new BasicStroke(1.0F);
/*  39 */   public static final Stroke DOUBLE_STROKE = new BasicStroke(2.0F);
/*     */   
/*     */   protected int total_x;
/*     */   protected int total_y;
/*     */   protected int screen_x;
/*  44 */   protected int buf_x = 500; protected int screen_y; protected int net_x; protected int net_y;
/*  45 */   protected int buf_y = 500;
/*  46 */   protected int orig_x = 0;
/*  47 */   protected int orig_y = 0;
/*     */   
/*     */   protected boolean init = false;
/*     */   protected ArrayList[][] net;
/*     */   protected Image back_img;
/*  52 */   protected Color m_BackColor = SystemColor.control;
/*  53 */   protected Color m_FrameColor = Color.black;
/*  54 */   protected int m_FrameSize = 0;
/*     */   
/*     */   protected ActionListener m_Rendered;
/*     */   protected DrawableTransform m_Transform;
/*     */   protected DrawableRenderer renderer;
/*  59 */   protected int renderstate = 0;
/*     */   protected boolean m_KeyFocus;
/*     */   
/*     */   public DrawableCanvas() {
/*  63 */     addMouseListener(new MyMouseListener());
/*     */   }
/*     */   
/*     */   public void setRenderDoneListener(ActionListener listener) {
/*  67 */     this.m_Rendered = listener;
/*     */   }
/*     */   
/*     */   public void setKeyFocusEnabled(boolean focus) {
/*  71 */     this.m_KeyFocus = focus;
/*     */   }
/*     */   
/*     */   public boolean isFocusTraversable() {
/*  75 */     return this.m_KeyFocus;
/*     */   }
/*     */   
/*     */   public void init(int bufwd, int bufhi, int totwd, int tothi) {
/*  79 */     this.buf_x = bufwd; this.buf_y = bufhi;
/*  80 */     init(totwd, tothi);
/*     */   }
/*     */   
/*     */   public void init(int totwd, int tothi) {
/*  84 */     this.total_x = totwd; this.total_y = tothi;
/*  85 */     this.net_x = ceilDiv(totwd, this.buf_x);
/*  86 */     this.net_y = ceilDiv(tothi, this.buf_y);
/*  87 */     createNet(this.net_x, this.net_y);
/*  88 */     this.init = true;
/*     */   }
/*     */   
/*     */   public void setRenderState(int state) {
/*  92 */     this.renderstate = state;
/*     */   }
/*     */   
/*     */   public boolean isRendered() {
/*  96 */     return (this.renderstate == 0);
/*     */   }
/*     */   
/*     */   public void setGridSize(int bufwd, int bufhi) {
/* 100 */     this.buf_x = bufwd;
/* 101 */     this.buf_y = bufhi;
/*     */   }
/*     */   
/*     */   public void resizeNet(int wd, int hi) {
/* 105 */     init(wd, hi);
/*     */   }
/*     */   
/*     */   public void clear() {
/* 109 */     for (int ctr_x = 0; ctr_x < this.net_x; ctr_x++) {
/* 110 */       for (int ctr_y = 0; ctr_y < this.net_y; ctr_y++)
/* 111 */         this.net[ctr_x][ctr_y].clear(); 
/*     */     } 
/*     */   }
/*     */   public void setRenderer(DrawableRenderer renderer) {
/* 115 */     this.renderer = renderer;
/* 116 */     this.renderstate = 1;
/*     */   }
/*     */   
/*     */   public static int ceilDiv(int num, int div) {
/* 120 */     if (num % div == 0) return num / div; 
/* 121 */     return num / div + 1;
/*     */   }
/*     */   
/*     */   public void setOrigin(int x, int y) {
/* 125 */     this.orig_x = x;
/* 126 */     this.orig_y = y;
/*     */   }
/*     */   
/*     */   public void setXOrig(int x) {
/* 130 */     this.orig_x = x;
/*     */   }
/*     */   
/*     */   public void setYOrig(int y) {
/* 134 */     this.orig_y = y;
/*     */   }
/*     */   
/*     */   public void addXOrig(int x) {
/* 138 */     this.orig_x += x;
/*     */   }
/*     */   
/*     */   public void addYOrig(int y) {
/* 142 */     this.orig_y += y;
/*     */   }
/*     */   
/*     */   public int getXOrig() {
/* 146 */     return this.orig_x;
/*     */   }
/*     */   
/*     */   public int getYOrig() {
/* 150 */     return this.orig_y;
/*     */   }
/*     */   
/*     */   public Point getOrigin() {
/* 154 */     return new Point(this.orig_x, this.orig_y);
/*     */   }
/*     */   
/*     */   public Dimension getTotal() {
/* 158 */     return new Dimension(this.total_x, this.total_y);
/*     */   }
/*     */   
/*     */   public void setBackground(Image bkg) {
/* 162 */     if (bkg == null)
/* 163 */       return;  this.back_img = bkg;
/*     */   }
/*     */   
/*     */   public void setBackground(Color col) {
/* 167 */     System.out.println("Background: " + col);
/* 168 */     this.m_BackColor = col;
/*     */   }
/*     */   
/*     */   public void setFrame(int size, Color col) {
/* 172 */     this.m_FrameSize = size;
/* 173 */     this.m_FrameColor = col;
/*     */   }
/*     */   
/*     */   public void initBackground(Graphics g, int dx, int dy, int pos_x, int pos_y, int wd, int hi) {
/* 177 */     g.setClip(0, 0, wd, hi);
/* 178 */     if (this.back_img != null)
/* 179 */     { int xsiz = this.back_img.getWidth(this);
/* 180 */       int ysiz = this.back_img.getHeight(this);
/* 181 */       int xofs = -pos_x % xsiz + dx;
/* 182 */       int yofs = -pos_y % ysiz + dy; int xp;
/* 183 */       for (xp = xofs; xp < wd; xp += xsiz) {
/* 184 */         int yp; for (yp = yofs; yp < hi; yp += ysiz)
/* 185 */           g.drawImage(this.back_img, xp, yp, this); 
/*     */       }  }
/* 187 */     else { g.setColor(this.m_BackColor);
/* 188 */       g.fillRect(0, 0, wd, hi); }
/*     */     
/* 190 */     if (this.m_FrameSize != 0) {
/* 191 */       g.setColor(this.m_FrameColor);
/* 192 */       Insets ins = getInsets();
/* 193 */       Dimension size = getSize();
/* 194 */       int mwd = size.width - ins.left - ins.right;
/* 195 */       int mhi = size.height - ins.top - ins.bottom;
/* 196 */       g.fillRect(ins.left, ins.top, mwd, this.m_FrameSize);
/* 197 */       g.fillRect(ins.left, ins.top + mhi - this.m_FrameSize, mwd, this.m_FrameSize);
/* 198 */       g.fillRect(ins.left, ins.top, this.m_FrameSize, mhi);
/* 199 */       g.fillRect(ins.left + mwd - this.m_FrameSize, ins.top, this.m_FrameSize, mhi);
/*     */     } 
/*     */   }
/*     */   
/*     */   public ArrayList getRectangle(int x, int y) {
/* 204 */     int ctr_x = x / this.buf_x;
/* 205 */     int ctr_y = y / this.buf_y;
/* 206 */     return this.net[ctr_x][ctr_y];
/*     */   }
/*     */   
/*     */   public Point getDisplayedMaxPoint() {
/* 210 */     Dimension dim = getSize();
/* 211 */     int myx = Math.min(this.orig_x + dim.width, this.net_x * this.buf_x);
/* 212 */     int myy = Math.min(this.orig_y + dim.height, this.net_y * this.buf_y);
/* 213 */     return new Point(myx, myy);
/*     */   }
/*     */   
/*     */   public Point getPosition(Drawable drawable, int xp, int yp) {
/* 217 */     int xpos = xp, ypos = yp;
/* 218 */     Point pt = getDisplayedMaxPoint();
/* 219 */     Rectangle rect = drawable.getBoundRect();
/* 220 */     if (xpos + rect.width + 3 > pt.x) xpos = pt.x - rect.width - 3; 
/* 221 */     if (ypos + rect.height + 3 > pt.y) ypos = pt.y - rect.height - 3; 
/* 222 */     xpos = Math.max(5, xpos);
/* 223 */     ypos = Math.max(5, ypos);
/* 224 */     return new Point(xpos, ypos);
/*     */   }
/*     */   
/*     */   public Point getCenterCoords(Rectangle rect) {
/* 228 */     Point pt = getDisplayedMaxPoint();
/* 229 */     int wd = Math.abs(pt.x - this.orig_x);
/* 230 */     int hi = Math.abs(pt.y - this.orig_y);
/* 231 */     return new Point(this.orig_x + wd / 2 - rect.width / 2, this.orig_y + hi / 2 - rect.height / 2);
/*     */   }
/*     */   
/*     */   public void removeDrawable(Drawable drawable) {
/* 235 */     Rectangle rect = drawable.getBoundRect();
/* 236 */     int x2 = rect.x + rect.width;
/* 237 */     int y2 = rect.y + rect.height;
/*     */     
/* 239 */     int rx_1 = rect.x / this.buf_x;
/* 240 */     int rx_2 = x2 / this.buf_x;
/* 241 */     int ry_1 = rect.y / this.buf_y;
/* 242 */     int ry_2 = y2 / this.buf_y;
/* 243 */     for (int ctr_x = rx_1; ctr_x <= rx_2; ctr_x++) {
/* 244 */       for (int ctr_y = ry_1; ctr_y <= ry_2; ctr_y++)
/* 245 */         this.net[ctr_x][ctr_y].remove(drawable); 
/*     */     } 
/*     */   }
/*     */   public void removeDrawableSafe(Drawable drawable) {
/* 249 */     Rectangle rect = drawable.getBoundRect();
/* 250 */     int x2 = rect.x + rect.width;
/* 251 */     int y2 = rect.y + rect.height;
/*     */     
/* 253 */     int rx_1 = rect.x / this.buf_x;
/* 254 */     int rx_2 = x2 / this.buf_x;
/* 255 */     int ry_1 = rect.y / this.buf_y;
/* 256 */     int ry_2 = y2 / this.buf_y;
/* 257 */     if (rx_2 >= this.net_x)
/* 258 */       return;  if (ry_2 >= this.net_y)
/* 259 */       return;  for (int ctr_x = rx_1; ctr_x <= rx_2; ctr_x++) {
/* 260 */       for (int ctr_y = ry_1; ctr_y <= ry_2; ctr_y++) {
/* 261 */         this.net[ctr_x][ctr_y].remove(drawable);
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   public void removeDrawableAll(Drawable drawable) {
/* 267 */     for (int ctr_x = 0; ctr_x < this.net_x; ctr_x++) {
/* 268 */       for (int ctr_y = 0; ctr_y < this.net_y; ctr_y++)
/* 269 */         this.net[ctr_x][ctr_y].remove(drawable); 
/*     */     } 
/*     */   }
/*     */   public void addDrawable(Drawable drawable) {
/* 273 */     if (this.m_Transform != null) this.m_Transform.transform(drawable); 
/* 274 */     Rectangle rect = drawable.getBoundRect();
/* 275 */     int x2 = rect.x + rect.width;
/* 276 */     int y2 = rect.y + rect.height;
/*     */     
/* 278 */     int rx_1 = rect.x / this.buf_x;
/* 279 */     int rx_2 = x2 / this.buf_x;
/* 280 */     int ry_1 = rect.y / this.buf_y;
/* 281 */     int ry_2 = y2 / this.buf_y;
/* 282 */     for (int ctr_x = rx_1; ctr_x <= rx_2; ctr_x++) {
/* 283 */       for (int ctr_y = ry_1; ctr_y <= ry_2; ctr_y++)
/* 284 */         this.net[ctr_x][ctr_y].add(drawable); 
/*     */     } 
/*     */   }
/*     */   public void addDrawableNoTrans(Drawable drawable) {
/* 288 */     Rectangle rect = drawable.getBoundRect();
/* 289 */     int x2 = rect.x + rect.width;
/* 290 */     int y2 = rect.y + rect.height;
/*     */     
/* 292 */     int rx_1 = rect.x / this.buf_x;
/* 293 */     int rx_2 = x2 / this.buf_x;
/* 294 */     int ry_1 = rect.y / this.buf_y;
/* 295 */     int ry_2 = y2 / this.buf_y;
/* 296 */     for (int ctr_x = rx_1; ctr_x <= rx_2; ctr_x++) {
/* 297 */       for (int ctr_y = ry_1; ctr_y <= ry_2; ctr_y++)
/* 298 */         this.net[ctr_x][ctr_y].add(drawable); 
/*     */     } 
/*     */   }
/*     */   public void setTransform(DrawableTransform trans) {
/* 302 */     this.m_Transform = trans;
/*     */   }
/*     */   
/*     */   public Dimension transformDimension(Dimension dim) {
/* 306 */     if (this.m_Transform == null) return dim; 
/* 307 */     return this.m_Transform.transformDimension(dim);
/*     */   }
/*     */   
/*     */   public void drawRectangle(Graphics2D g, int dx, int dy, int rec_x, int rec_y) {
/* 311 */     int delta_x = rec_x * this.buf_x - this.orig_x;
/* 312 */     int delta_y = rec_y * this.buf_y - this.orig_y;
/* 313 */     int clip_x = delta_x;
/* 314 */     int clip_y = delta_y;
/* 315 */     int clip_w = this.buf_x;
/* 316 */     int clip_h = this.buf_y;
/* 317 */     if (delta_x < 0) {
/* 318 */       clip_x = 0;
/* 319 */       clip_w += delta_x;
/*     */     } 
/* 321 */     if (delta_y < 0) {
/* 322 */       clip_y = 0;
/* 323 */       clip_h += delta_y;
/*     */     } 
/* 325 */     clip_w = Math.min(clip_w, this.screen_x - clip_x - dx);
/* 326 */     clip_h = Math.min(clip_h, this.screen_y - clip_y - dy);
/* 327 */     g.setClip(clip_x + dx, clip_y + dx, clip_w, clip_h);
/* 328 */     int m_orig_x = this.orig_x + dx;
/* 329 */     int m_orig_y = this.orig_y + dy;
/* 330 */     ArrayList<Drawable> curr = this.net[rec_x][rec_y];
/* 331 */     for (int i = 0; i < curr.size(); i++) {
/* 332 */       Drawable drawable = curr.get(i);
/* 333 */       drawable.draw(g, this, m_orig_x, m_orig_y);
/*     */     } 
/*     */   }
/*     */   
/*     */   public Dimension getRenderSize() {
/* 338 */     if (this.renderer != null) return this.renderer.getSize(); 
/* 339 */     return new Dimension(0, 0);
/*     */   }
/*     */   
/*     */   public DrawableRenderer getRenderer() {
/* 343 */     return this.renderer;
/*     */   }
/*     */   
/*     */   public void render(Graphics2D g) {
/* 347 */     if (this.renderer != null) {
/* 348 */       FontMetrics fm = g.getFontMetrics();
/* 349 */       this.renderer.render(g, fm, this);
/* 350 */       Dimension size = this.renderer.getSize();
/* 351 */       this.renderer.removeAll(this);
/* 352 */       resizeNet(size.width, size.height);
/* 353 */       this.renderer.addAll(this);
/* 354 */       this.renderstate = 0;
/* 355 */       if (this.m_Rendered != null) this.m_Rendered.actionPerformed(null); 
/*     */     } else {
/* 357 */       clear();
/*     */     } 
/*     */   }
/*     */   
/*     */   public void paintComponent(Graphics g) {
/* 362 */     super.paintComponent(g);
/* 363 */     Graphics2D g2 = (Graphics2D)g;
/* 364 */     g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
/* 365 */     if (this.renderstate != 0) render(g2); 
/* 366 */     Insets insets = getInsets();
/* 367 */     this.screen_x = getWidth() - insets.left - insets.right;
/* 368 */     this.screen_y = getHeight() - insets.top - insets.bottom;
/* 369 */     initBackground(g2, insets.left, insets.top, this.orig_x, this.orig_y, this.screen_x, this.screen_y);
/* 370 */     if (this.init) {
/* 371 */       int rx_1 = Math.min(this.orig_x / this.buf_x, this.net_x - 1);
/* 372 */       int rx_2 = Math.min((this.orig_x + this.screen_x) / this.buf_x, this.net_x - 1);
/* 373 */       int ry_1 = Math.min(this.orig_y / this.buf_y, this.net_y - 1);
/* 374 */       int ry_2 = Math.min((this.orig_y + this.screen_y) / this.buf_y, this.net_y - 1);
/* 375 */       for (int ctr_x = rx_1; ctr_x <= rx_2; ctr_x++) {
/* 376 */         for (int ctr_y = ry_1; ctr_y <= ry_2; ctr_y++) {
/* 377 */           drawRectangle(g2, insets.left, insets.top, ctr_x, ctr_y);
/*     */         }
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void createNet(int net_x, int net_y) {
/* 386 */     this.net = new ArrayList[net_x][net_y];
/* 387 */     for (int ctr_x = 0; ctr_x < net_x; ctr_x++) {
/* 388 */       for (int ctr_y = 0; ctr_y < net_y; ctr_y++)
/* 389 */         this.net[ctr_x][ctr_y] = new ArrayList(); 
/*     */     } 
/*     */   }
/*     */   
/*     */   public class MyMouseListener extends MouseAdapter {
/*     */     public void mousePressed(MouseEvent evt) {
/* 395 */       if (DrawableCanvas.this.m_KeyFocus) DrawableCanvas.this.requestFocus(); 
/* 396 */       if (!DrawableCanvas.this.init)
/* 397 */         return;  int xp = evt.getX() + DrawableCanvas.this.orig_x;
/* 398 */       int yp = evt.getY() + DrawableCanvas.this.orig_y;
/* 399 */       int ctr_x = Math.min(xp / DrawableCanvas.this.buf_x, DrawableCanvas.this.net_x - 1);
/* 400 */       int ctr_y = Math.min(yp / DrawableCanvas.this.buf_y, DrawableCanvas.this.net_y - 1);
/* 401 */       ArrayList<Drawable> field = DrawableCanvas.this.net[ctr_x][ctr_y];
/* 402 */       for (int ctr = field.size() - 1; ctr >= 0; ctr--) {
/* 403 */         Drawable drawable = field.get(ctr);
/* 404 */         Rectangle rect = drawable.getBoundRect();
/* 405 */         if (rect.contains(new Point(xp, yp))) {
/* 406 */           drawable.mousePressed(DrawableCanvas.this, xp, yp, evt);
/*     */           return;
/*     */         } 
/*     */       } 
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\jeans\graph\swing\drawable\DrawableCanvas.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */