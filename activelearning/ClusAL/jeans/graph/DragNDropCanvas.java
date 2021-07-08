/*     */ package jeans.graph;
/*     */ 
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Image;
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
/*     */ public abstract class DragNDropCanvas
/*     */   extends BufferCanvas
/*     */ {
/*     */   protected static final int max_dx = 150;
/*     */   protected static final int max_dy = 150;
/*     */   protected Image m_image;
/*     */   protected Image m_buf_img;
/*     */   protected Graphics m_buf_grp;
/*     */   protected boolean is_dragging;
/*     */   protected int drag_x;
/*     */   protected int drag_wd;
/*     */   protected int drag_x0;
/*     */   protected int image_w;
/*     */   protected int drag_y;
/*     */   protected int drag_hi;
/*     */   protected int drag_y0;
/*     */   protected int image_h;
/*     */   
/*     */   public DragNDropCanvas(int wd, int hi) {
/*  40 */     super(wd, hi);
/*  41 */     this.is_dragging = false;
/*  42 */     this.m_image = null;
/*     */   }
/*     */   
/*     */   public void setDragImage(Image image) {
/*  46 */     this.m_image = image;
/*  47 */     this.image_w = this.m_image.getWidth(this);
/*  48 */     this.image_h = this.m_image.getHeight(this);
/*     */   }
/*     */   
/*     */   public Dimension getDragImageSize() {
/*  52 */     return new Dimension(this.image_w, this.image_h);
/*     */   }
/*     */   
/*     */   public boolean isDragging() {
/*  56 */     return this.is_dragging;
/*     */   }
/*     */   
/*     */   public Image makeImage(int wd, int hi) {
/*  60 */     return createImage(wd, hi);
/*     */   }
/*     */   
/*     */   public synchronized void dragTo(int xp, int yp) {
/*  64 */     if (this.is_dragging) {
/*  65 */       this.drag_x0 = Math.min(this.drag_x, xp);
/*  66 */       this.drag_y0 = Math.min(this.drag_y, yp);
/*  67 */       this.drag_wd = Math.abs(this.drag_x - xp) + this.image_w;
/*  68 */       this.drag_hi = Math.abs(this.drag_y - yp) + this.image_h;
/*  69 */       this.drag_x = xp;
/*  70 */       this.drag_y = yp;
/*     */     } else {
/*  72 */       this.is_dragging = true;
/*  73 */       this.drag_x0 = this.drag_x = xp;
/*  74 */       this.drag_y0 = this.drag_y = yp;
/*  75 */       this.drag_wd = this.image_w;
/*  76 */       this.drag_hi = this.image_h;
/*     */     } 
/*  78 */     Graphics g = getGraphics();
/*  79 */     drawDragImage(g);
/*  80 */     if (this.drag_wd > 150 || this.drag_hi > 150) {
/*  81 */       aroundDragImage(g, this.bufSiz);
/*     */     }
/*     */   }
/*     */   
/*     */   public void drop() {
/*  86 */     this.is_dragging = false;
/*     */   }
/*     */   
/*     */   public void update(Graphics g) {
/*  90 */     Dimension dim = getSize();
/*  91 */     reSize(dim);
/*  92 */     if (!this.is_dragging) {
/*  93 */       g.drawImage(this.bufImg, 0, 0, this);
/*     */     } else {
/*  95 */       aroundDragImage(g, dim);
/*  96 */       drawDragImage(g);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void toScreen(Graphics g, Dimension dim) {
/* 101 */     if (!this.is_dragging) {
/* 102 */       g.drawImage(this.bufImg, 0, 0, this);
/*     */     } else {
/* 104 */       aroundDragImage(g, dim);
/* 105 */       drawDragImage(g);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void aroundDragImage(Graphics g, Dimension dim) {
/* 110 */     g.setClip(0, 0, this.drag_x0, dim.height);
/* 111 */     g.drawImage(this.bufImg, 0, 0, this);
/* 112 */     int x_clip = this.drag_x0 + this.drag_wd;
/* 113 */     g.setClip(x_clip, 0, dim.width - x_clip, dim.height);
/* 114 */     g.drawImage(this.bufImg, 0, 0, this);
/* 115 */     g.setClip(this.drag_x0, 0, this.drag_wd, this.drag_y0);
/* 116 */     g.drawImage(this.bufImg, 0, 0, this);
/* 117 */     int y_clip = this.drag_y0 + this.drag_hi;
/* 118 */     g.setClip(this.drag_x0, y_clip, this.drag_wd, dim.height - y_clip);
/* 119 */     g.drawImage(this.bufImg, 0, 0, this);
/*     */   }
/*     */   
/*     */   private void drawDragImage(Graphics g) {
/* 123 */     if (this.m_buf_img == null) makeBufImg(); 
/* 124 */     this.m_buf_grp.setClip(0, 0, this.drag_wd, this.drag_hi);
/* 125 */     this.m_buf_grp.drawImage(this.bufImg, -this.drag_x0, -this.drag_y0, this);
/* 126 */     this.m_buf_grp.drawImage(this.m_image, this.drag_x - this.drag_x0, this.drag_y - this.drag_y0, this);
/* 127 */     g.setClip(this.drag_x0, this.drag_y0, this.drag_wd, this.drag_hi);
/* 128 */     g.drawImage(this.m_buf_img, this.drag_x0, this.drag_y0, this);
/*     */   }
/*     */   
/*     */   private void makeBufImg() {
/* 132 */     this.m_buf_img = createImage(150, 150);
/* 133 */     this.m_buf_grp = this.m_buf_img.getGraphics();
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jeans\graph\DragNDropCanvas.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */