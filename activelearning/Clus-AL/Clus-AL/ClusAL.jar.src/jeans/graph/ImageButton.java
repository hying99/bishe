/*     */ package jeans.graph;
/*     */ 
/*     */ import java.awt.Canvas;
/*     */ import java.awt.Color;
/*     */ import java.awt.Container;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Font;
/*     */ import java.awt.FontMetrics;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Image;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.MouseAdapter;
/*     */ import java.awt.event.MouseEvent;
/*     */ import java.awt.image.FilteredImageSource;
/*     */ import java.awt.image.ImageFilter;
/*     */ import java.awt.image.ImageProducer;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Vector;
/*     */ import jeans.graph.image.GrayScaleFilter;
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
/*     */ public class ImageButton
/*     */   extends Canvas
/*     */ {
/*     */   public static final long serialVersionUID = 1L;
/*  72 */   protected Image image = null;
/*  73 */   protected Image gray_image = null;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean imageSizeKnown = false;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected String label;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  88 */   protected int shadow = 2;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  93 */   protected int border = 2;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean selected = false;
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean enable = true;
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean resizeImage = true;
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean showLabel = true;
/*     */ 
/*     */ 
/*     */   
/* 114 */   protected int minWidth = 10;
/*     */   
/* 116 */   protected Vector listeners = new Vector();
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
/*     */   public ImageButton(Image image, Image gray, String label, boolean resizeImage, boolean showLabel) {
/* 131 */     this.image = image;
/* 132 */     this.gray_image = gray;
/* 133 */     this.label = label;
/* 134 */     this.resizeImage = resizeImage;
/* 135 */     this.showLabel = showLabel;
/* 136 */     this.shadow = 2;
/* 137 */     this.border = 2;
/* 138 */     this.minWidth = 10;
/* 139 */     if (image == null) this.imageSizeKnown = true; 
/* 140 */     addMouseListener(new CBMouseListener());
/*     */   }
/*     */   
/*     */   public ImageButton(Image image, String label, boolean resizeImage, boolean showLabel) {
/* 144 */     this(image, (Image)null, label, resizeImage, showLabel);
/*     */   }
/*     */   
/*     */   public ImageButton(Image image, Image gray, String label) {
/* 148 */     this(image, gray, label, false, true);
/*     */   }
/*     */   
/*     */   public ImageButton(Image image, String label) {
/* 152 */     this(image, (Image)null, label, false, true);
/*     */   }
/*     */   
/*     */   public ImageButton(Image image, Image gray) {
/* 156 */     this(image, gray, "", false, false);
/*     */   }
/*     */   
/*     */   public ImageButton(Image image) {
/* 160 */     this(image, (Image)null, "", false, false);
/*     */   }
/*     */   
/*     */   public void addActionListener(ActionListener listener) {
/* 164 */     this.listeners.addElement(listener);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getLabel() {
/* 172 */     return this.label;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setLabel(String label) {
/* 181 */     this.label = label;
/* 182 */     layoutParent();
/* 183 */     repaint();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Image getImage() {
/* 191 */     return this.image;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setImage(Image image) {
/* 200 */     this.image = image;
/* 201 */     layoutParent();
/* 202 */     repaint();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean getResizeImage() {
/* 210 */     return this.resizeImage;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setResizeImage(boolean resizeImage) {
/* 219 */     this.resizeImage = resizeImage;
/* 220 */     layoutParent();
/* 221 */     repaint();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean getShowLabel() {
/* 229 */     return this.showLabel;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setShowLabel(boolean showLabel) {
/* 237 */     this.showLabel = showLabel;
/* 238 */     layoutParent();
/* 239 */     repaint();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean imageSizeKnown() {
/* 246 */     return this.imageSizeKnown;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected String paramString() {
/* 253 */     return super.paramString() + ",label=" + this.label;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean imageUpdate(Image img, int flags, int x, int y, int w, int h) {
/* 263 */     boolean ok = (img == this.image);
/* 264 */     if (ok && (flags & 0x3) != 0) {
/*     */       
/* 266 */       this.imageSizeKnown = true;
/* 267 */       layoutParent();
/*     */     } 
/* 269 */     return super.imageUpdate(img, flags, x, y, w, h);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void layoutParent() {
/* 277 */     Container parent = getParent();
/* 278 */     if (parent != null) {
/* 279 */       parent.doLayout();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void paint(Graphics g) {
/* 288 */     Dimension size = getSize();
/* 289 */     if (isVisible()) {
/* 290 */       if (this.enable)
/* 291 */       { if (this.selected) {
/* 292 */           paintSelected(g, size);
/*     */         } else {
/* 294 */           paintUnSelected(g, size);
/*     */         }  }
/* 296 */       else { paintDisabled(g, size); }
/*     */     
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void paintSelected(Graphics g, Dimension size) {
/* 308 */     Color c = getBackground();
/* 309 */     g.setColor(c);
/* 310 */     ImageUtil.draw3DRect(g, 0, 0, size.width, size.height, this.shadow, false);
/* 311 */     drawBody(g, size);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void paintUnSelected(Graphics g, Dimension size) {
/* 321 */     Color c = getBackground();
/* 322 */     g.setColor(c);
/* 323 */     g.fillRect(0, 0, size.width, size.height);
/* 324 */     ImageUtil.draw3DRect(g, 0, 0, size.width, size.height, this.shadow, true);
/* 325 */     drawBody(g, size);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void paintDisabled(Graphics g, Dimension size) {
/* 335 */     Color c = getBackground();
/* 336 */     g.setColor(c);
/* 337 */     g.fillRect(0, 0, size.width, size.height);
/* 338 */     g.setColor(c.darker());
/* 339 */     g.drawRect(0, 0, size.width - 1, size.height - 1);
/*     */     
/* 341 */     drawBody(g, size);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void drawBody(Graphics g, Dimension size) {
/* 351 */     int selOff = this.selected ? 1 : 0;
/* 352 */     int labelX = 0;
/* 353 */     int labelY = 0;
/* 354 */     int labelW = 0;
/* 355 */     int labelH = 0;
/* 356 */     FontMetrics fm = null;
/* 357 */     if (this.image == null || this.showLabel) {
/*     */       
/* 359 */       Font f = getFont();
/* 360 */       fm = getFontMetrics(f);
/* 361 */       labelH = fm.getAscent() + fm.getDescent();
/* 362 */       labelW = fm.stringWidth(this.label) + 3;
/* 363 */       labelX = size.width / 2 - labelW / 2 + selOff;
/* 364 */       labelY = size.height / 2 - labelH / 2 + fm.getAscent() + selOff;
/*     */     } 
/* 366 */     if (this.image != null) {
/*     */       int x; int y; int w;
/*     */       int h;
/* 369 */       if (this.resizeImage) {
/*     */         
/* 371 */         x = this.shadow + this.border + selOff;
/* 372 */         y = this.shadow + this.border + selOff;
/* 373 */         w = size.width - 2 * (this.shadow + this.border) - selOff;
/* 374 */         h = size.height - 2 * (this.shadow + this.border) - selOff;
/* 375 */         if (this.showLabel) {
/* 376 */           h -= labelH + this.border;
/*     */         }
/*     */       } else {
/* 379 */         Dimension d = new Dimension();
/* 380 */         d.width = this.image.getWidth(this);
/* 381 */         d.height = this.image.getHeight(this);
/* 382 */         if (d.width > 0 && d.height > 0)
/* 383 */           this.imageSizeKnown = true; 
/* 384 */         w = d.width - selOff;
/* 385 */         h = d.height - selOff;
/* 386 */         if (this.showLabel) {
/* 387 */           x = this.border + selOff;
/* 388 */           y = size.height / 2 - d.height / 2 + selOff;
/*     */         } else {
/* 390 */           x = size.width / 2 - d.width / 2 + selOff;
/* 391 */           y = size.height / 2 - d.height / 2 + selOff;
/*     */         } 
/*     */       } 
/* 394 */       if (this.enable) {
/* 395 */         g.drawImage(this.image, x, y, w, h, this);
/* 396 */         if (this.showLabel) {
/* 397 */           g.setColor(getForeground());
/* 398 */           labelX = x + w + this.border + selOff;
/* 399 */           g.drawString(this.label, labelX, labelY);
/*     */         } 
/*     */       } else {
/* 402 */         if (this.gray_image == null && this.image != null) {
/* 403 */           this.gray_image = getGrayScale(this.image);
/*     */         }
/* 405 */         g.drawImage(this.gray_image, x, y, w, h, this);
/* 406 */         if (this.showLabel) {
/* 407 */           g.setColor(Color.gray);
/* 408 */           labelX = x + w + this.border;
/* 409 */           g.drawString(this.label, labelX, labelY);
/*     */         } 
/*     */       } 
/*     */     } else {
/*     */       
/* 414 */       if (!this.enable) {
/* 415 */         g.setColor(Color.gray);
/*     */       } else {
/* 417 */         g.setColor(getForeground());
/* 418 */       }  g.drawString(this.label, labelX, labelY);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Dimension getPreferredSize() {
/* 428 */     return getMinimumSize();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized Dimension getMinimumSize() {
/* 437 */     Dimension d = new Dimension();
/* 438 */     Dimension labelDimension = new Dimension();
/* 439 */     if (this.image == null || this.showLabel) {
/*     */       
/* 441 */       FontMetrics fm = getFontMetrics(getFont());
/* 442 */       labelDimension
/* 443 */         .width = Math.max(fm.stringWidth(this.label) + 2 * (this.shadow + this.border), this.minWidth);
/*     */       
/* 445 */       labelDimension.height = fm.getAscent() + fm.getDescent() + 2 * (this.shadow + this.border);
/*     */       
/* 447 */       if (this.image == null)
/* 448 */         d = labelDimension; 
/*     */     } 
/* 450 */     if (this.image != null) {
/*     */ 
/*     */ 
/*     */       
/* 454 */       d.width = this.image.getWidth(this);
/* 455 */       d.height = this.image.getHeight(this);
/* 456 */       if (d.width > 0 && d.height > 0) {
/*     */         
/* 458 */         d.width += 2 * (this.shadow + this.border);
/* 459 */         d.height += 2 * (this.shadow + this.border);
/* 460 */         d.width = Math.max(d.width, this.minWidth);
/* 461 */         if (this.showLabel) {
/*     */           
/* 463 */           if (labelDimension.height > d.height)
/* 464 */             d.height = labelDimension.height; 
/* 465 */           d.width += labelDimension.width - 2 * this.shadow - this.border;
/*     */         } 
/*     */       } 
/*     */     } 
/* 469 */     return d;
/*     */   }
/*     */   
/*     */   public synchronized void setEnabled(boolean enable) {
/* 473 */     if (this.enable != enable) {
/* 474 */       this.enable = enable;
/* 475 */       repaint();
/*     */     } 
/*     */   }
/*     */   
/*     */   public void myNotifyListeners() {
/* 480 */     for (Enumeration<ActionListener> enum1 = this.listeners.elements(); enum1.hasMoreElements(); ) {
/* 481 */       ActionListener listener = enum1.nextElement();
/* 482 */       listener.actionPerformed(new ActionEvent(this, 0, getLabel()));
/*     */     } 
/*     */   }
/*     */   
/*     */   private Image getGrayScale(Image image) {
/* 487 */     GrayScaleFilter grayScaleFilter = new GrayScaleFilter();
/* 488 */     ImageProducer prod = new FilteredImageSource(image.getSource(), (ImageFilter)grayScaleFilter);
/* 489 */     return createImage(prod);
/*     */   }
/*     */   
/*     */   private class CBMouseListener extends MouseAdapter { private CBMouseListener() {}
/*     */     
/*     */     public void mouseExited(MouseEvent e) {
/* 495 */       if (ImageButton.this.selected) {
/*     */         
/* 497 */         ImageButton.this.selected = false;
/* 498 */         ImageButton.this.repaint();
/*     */       } 
/*     */     }
/*     */     
/*     */     public void mousePressed(MouseEvent e) {
/* 503 */       if (ImageButton.this.enable) {
/* 504 */         ImageButton.this.selected = true;
/* 505 */         ImageButton.this.repaint();
/*     */       } 
/*     */     }
/*     */     
/*     */     public void mouseReleased(MouseEvent e) {
/* 510 */       if (ImageButton.this.selected) {
/*     */         
/* 512 */         ImageButton.this.selected = false;
/* 513 */         ImageButton.this.repaint();
/* 514 */         if (ImageButton.this.enable) ImageButton.this.myNotifyListeners(); 
/*     */       } 
/*     */     } }
/*     */ 
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\jeans\graph\ImageButton.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */