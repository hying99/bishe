/*     */ package jeans.graph.swing.drawable;
/*     */ 
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.Color;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.event.AdjustmentEvent;
/*     */ import java.awt.event.AdjustmentListener;
/*     */ import java.awt.event.ComponentAdapter;
/*     */ import java.awt.event.ComponentEvent;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JScrollBar;
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
/*     */ public class DrawableScrollableCanvas
/*     */   extends JPanel
/*     */ {
/*     */   public static final long serialVersionUID = 1L;
/*     */   protected Drawable m_ScrollTo;
/*     */   protected MyDrawableCnv m_hCanvas;
/*     */   protected JScrollBar m_hVert;
/*     */   protected JScrollBar m_hHorz;
/*  36 */   protected int m_AutoGap = 10;
/*     */   
/*     */   public DrawableScrollableCanvas() {
/*  39 */     setLayout(new BorderLayout(0, 0));
/*  40 */     add(this.m_hCanvas = new MyDrawableCnv(), "Center");
/*  41 */     add(this.m_hVert = new JScrollBar(1, 0, 1, 0, 10000), "East");
/*  42 */     this.m_hVert.addAdjustmentListener(new MyVertListener());
/*  43 */     this.m_hVert.setVisible(false);
/*  44 */     add(this.m_hHorz = new JScrollBar(0, 0, 1, 0, 10000), "South");
/*  45 */     this.m_hHorz.addAdjustmentListener(new MyHorzListener());
/*  46 */     this.m_hHorz.setVisible(false);
/*  47 */     this.m_hCanvas.addComponentListener(new MyResizeListener());
/*     */   }
/*     */   
/*     */   public void setScrollTo(Drawable drawable) {
/*  51 */     this.m_ScrollTo = drawable;
/*     */   }
/*     */   
/*     */   public DrawableCanvas getCanvas() {
/*  55 */     return this.m_hCanvas;
/*     */   }
/*     */   
/*     */   public void paintAndScroll() {
/*  59 */     this.m_hCanvas.autoScrollTo();
/*  60 */     this.m_hCanvas.repaint();
/*     */   }
/*     */   
/*     */   public void setBackground(Color col) {
/*  64 */     super.setBackground(col);
/*  65 */     System.out.println("Setting background: " + this.m_hCanvas);
/*  66 */     if (this.m_hCanvas != null) this.m_hCanvas.setBackground(col); 
/*     */   }
/*     */   
/*     */   private class MyDrawableCnv extends DrawableCanvas { public static final long serialVersionUID = 1L;
/*     */     
/*     */     private MyDrawableCnv() {}
/*     */     
/*     */     public void updateScrollBars() {
/*  74 */       Dimension picSize = getRenderSize();
/*  75 */       Dimension scrSize = getSize();
/*  76 */       if (picSize == null || scrSize == null)
/*  77 */         return;  if (picSize.width > scrSize.width) {
/*  78 */         DrawableScrollableCanvas.this.m_hHorz.setMaximum(picSize.width);
/*  79 */         DrawableScrollableCanvas.this.m_hHorz.setVisibleAmount(scrSize.width);
/*  80 */         DrawableScrollableCanvas.this.m_hHorz.setVisible(true);
/*     */       } else {
/*  82 */         DrawableScrollableCanvas.this.m_hHorz.setVisible(false);
/*  83 */         if (getXOrig() != 0) {
/*  84 */           DrawableScrollableCanvas.this.m_hHorz.setValue(0);
/*  85 */           setXOrig(0);
/*  86 */           repaint();
/*     */         } 
/*     */       } 
/*  89 */       if (picSize.height > scrSize.height) {
/*  90 */         DrawableScrollableCanvas.this.m_hVert.setMaximum(picSize.height);
/*  91 */         DrawableScrollableCanvas.this.m_hVert.setVisibleAmount(scrSize.height);
/*  92 */         DrawableScrollableCanvas.this.m_hVert.setVisible(true);
/*     */       } else {
/*  94 */         DrawableScrollableCanvas.this.m_hVert.setVisible(false);
/*  95 */         if (getYOrig() != 0) {
/*  96 */           DrawableScrollableCanvas.this.m_hVert.setValue(0);
/*  97 */           setYOrig(0);
/*  98 */           repaint();
/*     */         } 
/*     */       } 
/*     */     }
/*     */     
/*     */     public void render(Graphics2D g) {
/* 104 */       super.render(g);
/* 105 */       updateScrollBars();
/* 106 */       autoScrollTo();
/*     */     }
/*     */     
/*     */     public void autoScrollTo() {
/* 110 */       if (DrawableScrollableCanvas.this.m_ScrollTo != null) {
/* 111 */         int deltax = 0;
/* 112 */         int deltay = 0;
/* 113 */         Dimension size = getSize();
/* 114 */         int top = DrawableScrollableCanvas.this.m_ScrollTo.getY() - DrawableScrollableCanvas.this.m_AutoGap;
/* 115 */         int left = DrawableScrollableCanvas.this.m_ScrollTo.getX() - DrawableScrollableCanvas.this.m_AutoGap;
/* 116 */         int right = DrawableScrollableCanvas.this.m_ScrollTo.getRight() + DrawableScrollableCanvas.this.m_AutoGap;
/* 117 */         int bottom = DrawableScrollableCanvas.this.m_ScrollTo.getYBottom() + DrawableScrollableCanvas.this.m_AutoGap;
/* 118 */         int scr_right = getXOrig() + size.width;
/* 119 */         int scr_bottom = getYOrig() + size.height;
/* 120 */         if (left < getXOrig()) deltax = Math.min(deltax, left - getXOrig()); 
/* 121 */         if (right > scr_right) deltax = Math.max(deltax, right - scr_right); 
/* 122 */         if (top < getYOrig()) deltay = Math.min(deltay, top - getYOrig()); 
/* 123 */         if (bottom > scr_bottom) deltay = Math.max(deltay, bottom - scr_bottom); 
/* 124 */         if (deltax != 0) {
/* 125 */           DrawableScrollableCanvas.this.m_hCanvas.addXOrig(deltax);
/* 126 */           DrawableScrollableCanvas.this.m_hHorz.setValue(getXOrig());
/*     */         } 
/* 128 */         if (deltay != 0) {
/* 129 */           DrawableScrollableCanvas.this.m_hCanvas.addYOrig(deltay);
/* 130 */           DrawableScrollableCanvas.this.m_hVert.setValue(getYOrig());
/*     */         } 
/* 132 */         DrawableScrollableCanvas.this.m_ScrollTo = null;
/* 133 */         if (deltax != 0 || deltay != 0) DrawableScrollableCanvas.this.m_hCanvas.repaint(); 
/*     */       } 
/*     */     } }
/*     */   
/*     */   private class MyResizeListener extends ComponentAdapter {
/*     */     private MyResizeListener() {}
/*     */     
/*     */     public void componentResized(ComponentEvent e) {
/* 141 */       DrawableScrollableCanvas.this.m_hCanvas.updateScrollBars();
/*     */     }
/*     */   }
/*     */   
/*     */   public class MyHorzListener
/*     */     implements AdjustmentListener {
/*     */     public void adjustmentValueChanged(AdjustmentEvent e) {
/* 148 */       DrawableScrollableCanvas.this.m_hCanvas.setXOrig(DrawableScrollableCanvas.this.m_hHorz.getValue());
/* 149 */       DrawableScrollableCanvas.this.m_hCanvas.repaint();
/*     */     }
/*     */   }
/*     */   
/*     */   public class MyVertListener
/*     */     implements AdjustmentListener {
/*     */     public void adjustmentValueChanged(AdjustmentEvent e) {
/* 156 */       DrawableScrollableCanvas.this.m_hCanvas.setYOrig(DrawableScrollableCanvas.this.m_hVert.getValue());
/* 157 */       DrawableScrollableCanvas.this.m_hCanvas.repaint();
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\jeans\graph\swing\drawable\DrawableScrollableCanvas.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */