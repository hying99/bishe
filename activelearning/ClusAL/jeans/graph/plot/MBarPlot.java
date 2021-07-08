/*     */ package jeans.graph.plot;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.awt.FontMetrics;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Insets;
/*     */ import javax.swing.JComponent;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class MBarPlot
/*     */   extends JComponent
/*     */ {
/*     */   public static final long serialVersionUID = 1L;
/*  32 */   Color[] m_hColors = new Color[] { Color.red, Color.yellow, Color.blue };
/*     */   
/*  34 */   MVerticalGraphScale m_hVScale = new MVerticalGraphScale();
/*     */   MBarPlotModel m_hModel;
/*     */   MBarPlotModelGroup m_hGroup;
/*     */   JScrollBar m_hScroll;
/*     */   int m_iModel;
/*     */   int m_iBar;
/*     */   
/*     */   public void setModel(MBarPlotModel model) {
/*  42 */     this.m_hModel = model;
/*  43 */     this.m_hGroup = null;
/*  44 */     updateScrolls();
/*     */   }
/*     */   
/*     */   public void setModel(MBarPlotModelGroup group) {
/*  48 */     this.m_hGroup = group;
/*  49 */     this.m_hModel = null;
/*  50 */     updateScrolls();
/*     */   }
/*     */   
/*     */   public void setScroll(JScrollBar scroll) {
/*  54 */     this.m_hScroll = scroll;
/*     */   }
/*     */   
/*     */   protected int getNbModels() {
/*  58 */     return (this.m_hModel == null) ? this.m_hGroup.getNbModels() : 1;
/*     */   }
/*     */   
/*     */   protected MBarPlotModel getModel(int idx) {
/*  62 */     return (this.m_hModel == null) ? this.m_hGroup.getModel(idx) : this.m_hModel;
/*     */   }
/*     */   
/*     */   protected void updateScrolls() {
/*  66 */     int nbbars = 0;
/*  67 */     if (this.m_hModel != null || this.m_hGroup != null) {
/*  68 */       for (int j = 0; j < getNbModels(); j++) {
/*  69 */         MBarPlotModel model = getModel(j);
/*  70 */         nbbars += model.getNbBars();
/*     */       } 
/*     */     }
/*  73 */     if (this.m_hScroll != null) {
/*  74 */       this.m_hScroll.setMinimum(0);
/*  75 */       this.m_hScroll.setValue(0);
/*  76 */       this.m_hScroll.setMaximum(nbbars);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void reset() {
/*  81 */     this.m_iModel = 0;
/*  82 */     this.m_iBar = 0;
/*     */   }
/*     */   
/*     */   public boolean hasMore() {
/*  86 */     int nb = getNbModels();
/*  87 */     if (this.m_iModel >= nb) return false; 
/*  88 */     if (this.m_iModel == nb - 1) {
/*  89 */       MBarPlotModel model = getModel(this.m_iModel);
/*  90 */       return (this.m_iBar < model.getNbBars());
/*     */     } 
/*  92 */     return true;
/*     */   }
/*     */   
/*     */   public void skip() {
/*  96 */     MBarPlotModel model = getModel(this.m_iModel);
/*  97 */     if (this.m_iBar >= model.getNbBars() - 1) {
/*  98 */       this.m_iBar = 0;
/*  99 */       this.m_iModel++;
/*     */     } else {
/* 101 */       this.m_iBar++;
/*     */     } 
/*     */   }
/*     */   
/*     */   public MBarPlotModel getModel() {
/* 106 */     return getModel(this.m_iModel);
/*     */   }
/*     */   
/*     */   public int getBar() {
/* 110 */     return this.m_iBar;
/*     */   }
/*     */ 
/*     */   
/*     */   public void paintComponent(Graphics g) {
/* 115 */     super.paintComponent(g);
/* 116 */     FontMetrics fm = g.getFontMetrics();
/* 117 */     Insets insets = getInsets();
/* 118 */     int m_wd = getWidth() - insets.left - insets.right;
/* 119 */     int m_hi = getHeight() - insets.top - insets.bottom;
/* 120 */     g.setColor(Color.black);
/* 121 */     g.fillRect(insets.left, insets.top, m_wd, m_hi);
/* 122 */     g.setColor(Color.green);
/* 123 */     if (this.m_hModel == null && this.m_hGroup == null)
/*     */       return; 
/* 125 */     int nb = getNbModels();
/* 126 */     if (nb == 0)
/* 127 */       return;  float ymin = Float.MAX_VALUE, ymax = -3.4028235E38F;
/* 128 */     int mypos = 0;
/* 129 */     for (int j = 0; j < nb; j++) {
/* 130 */       MBarPlotModel model = getModel(j);
/* 131 */       model.setOffset(mypos);
/* 132 */       mypos += 5 + model.getNbBars() * 13;
/* 133 */       for (int i = 0; i < model.getNbBars(); i++) {
/* 134 */         float y = model.getBarValue(i);
/* 135 */         if (y <= ymin) ymin = y; 
/* 136 */         if (y >= ymax) ymax = y; 
/*     */       } 
/*     */     } 
/* 139 */     this.m_hVScale.setMinMax(0.0F, ymax);
/* 140 */     ymin = this.m_hVScale.getRealMin();
/* 141 */     ymax = this.m_hVScale.getRealMax();
/*     */     
/* 143 */     int y_min = Math.max(10 + fm.getHeight(), this.m_hVScale.getVertUpperBound(fm)) + insets.top;
/* 144 */     int y_xas = m_hi + insets.top - 20;
/* 145 */     int v_wd = this.m_hVScale.getVertWidth(this, fm, y_min, y_xas - y_min);
/* 146 */     int x_yas = v_wd + insets.left + 5;
/* 147 */     int x_max = m_wd + insets.left - 5;
/*     */     
/* 149 */     this.m_hVScale.draw(this, g, x_yas, y_min, x_max - x_yas, y_xas - y_min);
/*     */     
/* 151 */     reset();
/* 152 */     int idx = 0;
/* 153 */     int deltax = 0;
/* 154 */     if (this.m_hScroll != null) {
/* 155 */       int pos = this.m_hScroll.getValue();
/* 156 */       while (hasMore() && idx < pos) {
/* 157 */         skip(); idx++;
/*     */       } 
/* 159 */       if (hasMore())
/* 160 */         deltax = getModel().getOffset() + getBar() * 13; 
/*     */     } 
/* 162 */     int nbbars_show = 0;
/* 163 */     int xpos = 0;
/* 164 */     while (hasMore()) {
/* 165 */       MBarPlotModel model = getModel();
/* 166 */       int bar = getBar();
/* 167 */       int xstart = x_yas + 10 + model.getOffset() - deltax;
/* 168 */       xpos = xstart + bar * 13;
/* 169 */       if (xpos >= x_max) {
/*     */         break;
/*     */       }
/* 172 */       float y = model.getBarValue(bar);
/* 173 */       g.setColor(this.m_hColors[bar]);
/* 174 */       int ypos = calc(y, ymin, ymax, y_xas, y_min);
/* 175 */       g.fillRect(xpos, ypos, 10, y_xas - ypos);
/*     */       
/* 177 */       String title = model.getTitle();
/* 178 */       int twd = fm.stringWidth(title);
/* 179 */       int tpos = xstart + 13 * model.getNbBars() / 2 - twd / 2;
/* 180 */       if (tpos < xstart) tpos = xstart; 
/* 181 */       g.setColor(Color.green);
/* 182 */       g.drawString(title, tpos, 5 + fm.getAscent());
/*     */       
/* 184 */       nbbars_show++;
/* 185 */       skip();
/*     */     } 
/* 187 */     if (xpos >= x_max) {
/* 188 */       nbbars_show = Math.max(nbbars_show - 1, 1);
/* 189 */       this.m_hScroll.setVisibleAmount(nbbars_show);
/*     */     } 
/* 191 */     g.setColor(Color.green);
/* 192 */     g.drawLine(x_yas, y_xas, xpos + 13 + 10, y_xas);
/*     */   }
/*     */   
/*     */   public void drawClass(Graphics g, int x, int y, int cl) {
/* 196 */     switch (cl) {
/*     */       case 0:
/* 198 */         g.drawLine(x - 2, y, x + 2, y);
/* 199 */         g.drawLine(x, y - 2, x, y + 2);
/*     */         break;
/*     */       case 1:
/* 202 */         g.drawOval(x - 2, y - 2, 4, 4);
/*     */         break;
/*     */       case 2:
/* 205 */         g.drawLine(x - 2, y - 2, x + 2, y + 2);
/* 206 */         g.drawLine(x + 2, y - 2, x - 2, y + 2);
/*     */         break;
/*     */       case 3:
/* 209 */         g.drawRect(x - 2, y - 2, 4, 4);
/*     */         break;
/*     */       case 4:
/* 212 */         g.drawLine(x - 2, y - 2, x + 2, y - 2);
/* 213 */         g.drawLine(x + 2, y - 2, x, y + 2);
/* 214 */         g.drawLine(x, y + 2, x - 2, y - 2);
/*     */         break;
/*     */       case 5:
/* 217 */         g.drawLine(x - 2, y + 2, x + 2, y + 2);
/* 218 */         g.drawLine(x + 2, y + 2, x, y - 2);
/* 219 */         g.drawLine(x, y - 2, x - 2, y + 2);
/*     */         break;
/*     */     } 
/*     */   }
/*     */   
/*     */   public void drawBarClass(Graphics g, int x, int y, int w, int h, int cl) {
/* 225 */     g.drawRect(x, y, w, h);
/* 226 */     for (int yp = 0; yp < h - 7; yp += 7)
/* 227 */       drawClass(g, x + w / 2, y + h - yp - 5, cl); 
/*     */   }
/*     */   
/*     */   public void printPostScript(Graphics g) {
/* 231 */     int m_wd = 552;
/*     */     
/* 233 */     int m_hi = 200;
/* 234 */     if (this.m_hModel == null && this.m_hGroup == null)
/* 235 */       return;  FontMetrics fm = g.getFontMetrics();
/*     */     
/* 237 */     int nb = getNbModels();
/* 238 */     if (nb == 0)
/* 239 */       return;  float ymin = Float.MAX_VALUE, ymax = -3.4028235E38F;
/* 240 */     int mypos = 0;
/* 241 */     for (int j = 0; j < nb; j++) {
/* 242 */       MBarPlotModel model = getModel(j);
/* 243 */       model.setOffset(mypos);
/* 244 */       mypos += 5 + model.getNbBars() * 13;
/* 245 */       for (int i = 0; i < model.getNbBars(); i++) {
/* 246 */         float y = model.getBarValue(i);
/* 247 */         if (y <= ymin) ymin = y; 
/* 248 */         if (y >= ymax) ymax = y; 
/*     */       } 
/*     */     } 
/* 251 */     this.m_hVScale.setMinMax(0.0F, ymax);
/* 252 */     ymin = this.m_hVScale.getRealMin();
/* 253 */     ymax = this.m_hVScale.getRealMax();
/*     */     
/* 255 */     int y_min = Math.max(10 + fm.getHeight(), this.m_hVScale.getVertUpperBound(fm));
/* 256 */     int y_xas = m_hi - 20;
/* 257 */     int v_wd = this.m_hVScale.getVertWidth(this, fm, y_min, y_xas - y_min);
/* 258 */     int x_yas = v_wd + 10;
/* 259 */     int x_max = m_wd - 10;
/*     */     
/* 261 */     this.m_hVScale.draw(this, g, x_yas, y_min, x_max - x_yas, y_xas - y_min);
/*     */     
/* 263 */     reset();
/* 264 */     int idx = 0;
/* 265 */     int deltax = 0;
/* 266 */     if (this.m_hScroll != null) {
/* 267 */       int pos = this.m_hScroll.getValue();
/* 268 */       while (hasMore() && idx < pos) {
/* 269 */         skip(); idx++;
/*     */       } 
/* 271 */       if (hasMore())
/* 272 */         deltax = getModel().getOffset() + getBar() * 13; 
/*     */     } 
/* 274 */     int nbbars_show = 0;
/* 275 */     int xpos = 0;
/* 276 */     while (hasMore()) {
/* 277 */       MBarPlotModel model = getModel();
/* 278 */       int bar = getBar();
/* 279 */       int xstart = x_yas + 10 + model.getOffset() - deltax;
/* 280 */       xpos = xstart + bar * 13;
/* 281 */       if (xpos >= x_max) {
/*     */         break;
/*     */       }
/* 284 */       float y = model.getBarValue(bar);
/* 285 */       int ypos = calc(y, ymin, ymax, y_xas, y_min);
/* 286 */       drawBarClass(g, xpos, ypos, 10, y_xas - ypos, bar);
/*     */       
/* 288 */       String title = model.getTitle();
/* 289 */       int twd = fm.stringWidth(title);
/* 290 */       int tpos = xstart + 13 * model.getNbBars() / 2 - twd / 2;
/* 291 */       if (tpos < xstart) tpos = xstart; 
/* 292 */       g.drawString(title, tpos, 5 + fm.getAscent());
/*     */       
/* 294 */       nbbars_show++;
/* 295 */       skip();
/*     */     } 
/* 297 */     g.drawLine(x_yas, y_xas, xpos + 13 + 10, y_xas);
/*     */   }
/*     */   
/*     */   public void yLabel(String label) {
/* 301 */     this.m_hVScale.setLabel(label);
/*     */   }
/*     */   
/*     */   public int calc(float val, float min, float max, int imin, int imax) {
/* 305 */     float p = (val - min) / (max - min) * (imax - imin);
/* 306 */     return Math.round(p) + imin;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jeans\graph\plot\MBarPlot.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */