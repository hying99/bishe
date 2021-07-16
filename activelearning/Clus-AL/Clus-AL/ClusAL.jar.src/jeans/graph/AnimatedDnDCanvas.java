/*     */ package jeans.graph;
/*     */ 
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Image;
/*     */ import jeans.util.ThreadLock;
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
/*     */ 
/*     */ 
/*     */ public abstract class AnimatedDnDCanvas
/*     */   extends DragNDropCanvas
/*     */ {
/*     */   public static final int ANIMATE_MODE_THREAD = 0;
/*     */   public static final int ANIMATE_MODE_EVENT = 1;
/*     */   public static final int HINT_NONE = 0;
/*     */   public static final int HINT_ALL = 1;
/*     */   public static final int HINT_ANIMATED = 2;
/*  39 */   private Image m_back_img = null;
/*  40 */   private Graphics m_back_grp = null;
/*     */   private boolean m_back_update = false;
/*  42 */   private Animator m_animator = null;
/*     */   private boolean m_animate_enabled = false;
/*     */   private boolean m_paused = false;
/*  45 */   private int m_animate_mode = 0;
/*  46 */   private int m_draw_hint = 1;
/*  47 */   private int m_count = 0;
/*     */   
/*     */   public AnimatedDnDCanvas(int wd, int hi) {
/*  50 */     super(wd, hi);
/*     */   }
/*     */   
/*     */   public boolean isAnimated() {
/*  54 */     return this.m_animate_enabled;
/*     */   }
/*     */   
/*     */   public synchronized void setPaused(boolean paused) {
/*  58 */     if (paused == this.m_paused)
/*  59 */       return;  if (isAnimated() && this.m_animate_mode == 0) {
/*  60 */       setAnimatorState(!paused);
/*     */     }
/*  62 */     this.m_paused = paused;
/*     */   }
/*     */   
/*     */   public synchronized void setAnimateMode(int mode) {
/*  66 */     if (this.m_animate_mode != mode) {
/*  67 */       if (this.m_animate_mode == 0 && this.m_animator != null) {
/*  68 */         this.m_animator.setEnabled(true);
/*  69 */         this.m_animator = null;
/*     */       } 
/*  71 */       this.m_animate_mode = mode;
/*     */     } 
/*     */   }
/*     */   
/*     */   public synchronized void startStopAnimator(boolean start) {
/*  76 */     if (this.m_animate_enabled == start)
/*  77 */       return;  if (!this.m_paused && this.m_animate_mode == 0) setAnimatorState(start); 
/*  78 */     this.m_animate_enabled = start;
/*     */   }
/*     */   
/*     */   public synchronized void update(Graphics g) {
/*  82 */     reSize(getSize());
/*  83 */     if (isAnimated())
/*  84 */     { if (this.m_draw_hint == 1 || this.m_back_update) {
/*  85 */         this.m_back_update = false;
/*  86 */         if (this.m_back_img == null) {
/*  87 */           this.m_back_img = createImage(this.bufSiz.width, this.bufSiz.height);
/*  88 */           this.m_back_grp = this.m_back_img.getGraphics();
/*     */         } 
/*  90 */         paintBackground(this.m_back_grp, this.bufSiz);
/*     */       } 
/*  92 */       if (this.m_draw_hint == 1 || this.m_draw_hint == 2) {
/*  93 */         this.bufGrp.drawImage(this.m_back_img, 0, 0, this);
/*  94 */         paintAnimated(this.bufGrp, this.bufSiz);
/*     */       }
/*     */        }
/*  97 */     else if (this.m_draw_hint == 1) { paintBackground(this.bufGrp, this.bufSiz); }
/*     */     
/*  99 */     toScreen(g, this.bufSiz);
/* 100 */     this.m_draw_hint = 0;
/*     */   }
/*     */   
/*     */   public synchronized void redraw() {
/* 104 */     this.m_draw_hint = 1;
/* 105 */     this.m_back_update = true;
/* 106 */     repaint();
/*     */   }
/*     */   
/*     */   public synchronized void redrawAnimated() {
/* 110 */     this.m_draw_hint = 2;
/* 111 */     repaint();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void clockTick() {
/* 119 */     redrawAnimated();
/* 120 */     this.m_count++;
/*     */   }
/*     */   
/*     */   public synchronized int getAnimateCount() {
/* 124 */     if (this.m_paused) {
/* 125 */       return 0;
/*     */     }
/* 127 */     return this.m_count;
/*     */   }
/*     */ 
/*     */   
/*     */   public void reSize(Dimension d) {
/* 132 */     if (this.bufGrp == null || d.width != this.bufSiz.width || d.height != this.bufSiz.height) {
/* 133 */       if (d.width == 0 || d.height == 0)
/* 134 */         return;  this.bufSiz = d;
/* 135 */       this.bufImg = createImage(d.width, d.height);
/* 136 */       this.bufGrp = this.bufImg.getGraphics();
/* 137 */       if (isAnimated()) {
/* 138 */         this.m_back_img = createImage(d.width, d.height);
/* 139 */         this.m_back_grp = this.m_back_img.getGraphics();
/*     */       } else {
/* 141 */         this.m_back_img = null;
/* 142 */         this.m_back_update = true;
/*     */       } 
/* 144 */       this.m_draw_hint = 1;
/*     */     } 
/*     */   }
/*     */   
/*     */   public void paintIt(Graphics g, Dimension d) {}
/*     */   
/*     */   public abstract void paintBackground(Graphics paramGraphics, Dimension paramDimension);
/*     */   
/*     */   private void setAnimatorState(boolean start) {
/* 153 */     if (this.m_animator != null) {
/* 154 */       this.m_animator.setEnabled(start);
/*     */     }
/* 156 */     else if (start) {
/* 157 */       this.m_animator = new Animator();
/* 158 */       this.m_animator.start();
/*     */     } 
/*     */   }
/*     */   
/*     */   public abstract void paintAnimated(Graphics paramGraphics, Dimension paramDimension);
/*     */   
/*     */   private class Animator extends Thread {
/* 165 */     private ThreadLock m_enable = new ThreadLock();
/*     */     
/*     */     public boolean isEnabled() {
/* 168 */       return !this.m_enable.getState();
/*     */     }
/*     */     
/*     */     public void setEnabled(boolean enable) {
/* 172 */       this.m_enable.setState(!enable);
/*     */     }
/*     */     
/*     */     public void run() {
/*     */       try {
/* 177 */         while (AnimatedDnDCanvas.this.m_animator != null) {
/* 178 */           Thread.sleep(500L);
/* 179 */           this.m_enable.testLock();
/* 180 */           AnimatedDnDCanvas.this.clockTick();
/*     */         } 
/* 182 */       } catch (InterruptedException interruptedException) {}
/* 183 */       AnimatedDnDCanvas.this.m_animator = null;
/*     */     }
/*     */     
/*     */     private Animator() {}
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\jeans\graph\AnimatedDnDCanvas.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */