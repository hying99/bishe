/*     */ package jeans.graph;
/*     */ 
/*     */ import java.awt.Component;
/*     */ import java.awt.Container;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Insets;
/*     */ import java.awt.LayoutManager;
/*     */ import java.util.StringTokenizer;
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
/*     */ public class PercentLayout
/*     */   implements LayoutManager
/*     */ {
/*     */   private int b_size;
/*     */   private int m_sides;
/*     */   private boolean b_vert;
/*     */   private int[] sizes;
/*     */   private int[] modes;
/*     */   private int num;
/*     */   private int nbDummies;
/*     */   private int preferredWidth;
/*     */   private int preferredHeight;
/*     */   private boolean sizeUnknown = true;
/*     */   public static final int PERCENT = 1;
/*     */   public static final int ABSOLUTE = 2;
/*     */   public static final int PREF = 4;
/*     */   public static final int MIN = 8;
/*     */   public static final int DUMMY = 16;
/*     */   public static final int NORTH = 1;
/*     */   public static final int EAST = 2;
/*     */   public static final int SOUTH = 4;
/*     */   public static final int WEST = 8;
/*     */   public static final int ALL = 15;
/*     */   
/*     */   public PercentLayout(String descr, int gap, int sides, boolean vert) {
/*  72 */     this.b_size = gap;
/*  73 */     this.m_sides = sides;
/*  74 */     this.b_vert = vert;
/*  75 */     StringTokenizer tokens = new StringTokenizer(descr);
/*  76 */     this.num = tokens.countTokens();
/*  77 */     this.modes = new int[this.num];
/*  78 */     this.sizes = new int[this.num];
/*  79 */     for (int ctr = 0; ctr < this.num; ctr++) {
/*  80 */       this.modes[ctr] = 0;
/*  81 */       this.sizes[ctr] = 0;
/*  82 */       String token = tokens.nextToken();
/*  83 */       int pos = token.indexOf('d');
/*  84 */       if (pos != -1) {
/*  85 */         this.nbDummies++;
/*  86 */         this.modes[ctr] = this.modes[ctr] | 0x10;
/*  87 */         token = token.substring(0, pos);
/*     */       } 
/*  89 */       pos = token.indexOf('%');
/*  90 */       if (pos != -1) {
/*  91 */         this.modes[ctr] = this.modes[ctr] | 0x1;
/*  92 */         token = token.substring(0, pos);
/*     */       } 
/*  94 */       if (token.indexOf('p') != -1)
/*  95 */         this.modes[ctr] = this.modes[ctr] | 0x4; 
/*  96 */       if (token.indexOf('m') != -1) {
/*  97 */         this.modes[ctr] = this.modes[ctr] | 0x8;
/*     */       } else {
/*     */         try {
/* 100 */           this.sizes[ctr] = Integer.parseInt(token);
/* 101 */         } catch (NumberFormatException numberFormatException) {}
/*     */       } 
/* 103 */       if ((this.modes[ctr] & 0x5) == 0) this.modes[ctr] = this.modes[ctr] | 0x2;
/*     */     
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void addLayoutComponent(String name, Component comp) {}
/*     */ 
/*     */   
/*     */   public void removeLayoutComponent(Component comp) {}
/*     */ 
/*     */   
/*     */   public int getComponentCount(Container parent) {
/* 116 */     return parent.getComponentCount() + this.nbDummies;
/*     */   }
/*     */   
/*     */   private void setSizes(Container parent) {
/* 120 */     int nComps = getComponentCount(parent);
/* 121 */     Dimension d = null;
/*     */ 
/*     */ 
/*     */     
/* 125 */     int dim_one = 0;
/* 126 */     int dim_mult = 0;
/* 127 */     int idx = 0;
/*     */     
/* 129 */     for (int i = 0; i < nComps && i < this.num; i++) {
/* 130 */       if ((this.modes[i] & 0x10) != 0) {
/* 131 */         if ((this.modes[i] & 0x2) != 0)
/* 132 */           dim_mult += this.sizes[i]; 
/*     */       } else {
/* 134 */         Component c = parent.getComponent(idx++);
/* 135 */         if (c.isVisible()) {
/* 136 */           int my_one, my_mult; d = ((this.modes[i] & 0x4) != 0) ? c.getPreferredSize() : c.getMinimumSize();
/* 137 */           if (this.b_vert) {
/* 138 */             my_one = d.width;
/* 139 */             my_mult = d.height;
/*     */           } else {
/* 141 */             my_one = d.height;
/* 142 */             my_mult = d.width;
/*     */           } 
/* 144 */           if ((this.modes[i] & 0x2) != 0) {
/* 145 */             dim_mult += this.sizes[i];
/*     */           } else {
/* 147 */             dim_mult += my_mult;
/* 148 */           }  dim_one = Math.max(my_one, dim_one);
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/* 153 */     dim_mult += (this.num - 1) * this.b_size;
/*     */     
/* 155 */     if (this.b_vert) {
/* 156 */       this.preferredWidth = dim_one;
/* 157 */       this.preferredHeight = dim_mult;
/*     */     } else {
/* 159 */       this.preferredWidth = dim_mult;
/* 160 */       this.preferredHeight = dim_one;
/*     */     } 
/*     */ 
/*     */     
/* 164 */     if ((this.m_sides & 0x8) != 0) this.preferredWidth += this.b_size;
/*     */     
/* 166 */     if ((this.m_sides & 0x2) != 0) this.preferredWidth += this.b_size;
/*     */     
/* 168 */     if ((this.m_sides & 0x1) != 0) this.preferredHeight += this.b_size;
/*     */     
/* 170 */     if ((this.m_sides & 0x4) != 0) this.preferredHeight += this.b_size;
/*     */ 
/*     */     
/* 173 */     this.sizeUnknown = false;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Dimension preferredLayoutSize(Container parent) {
/* 179 */     Dimension dim = new Dimension(0, 0);
/*     */     
/* 181 */     setSizes(parent);
/*     */ 
/*     */     
/* 184 */     Insets insets = parent.getInsets();
/* 185 */     dim.width = this.preferredWidth + insets.left + insets.right;
/* 186 */     dim.height = this.preferredHeight + insets.top + insets.bottom;
/*     */     
/* 188 */     return dim;
/*     */   }
/*     */ 
/*     */   
/*     */   public Dimension minimumLayoutSize(Container parent) {
/* 193 */     return preferredLayoutSize(parent);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void layoutContainer(Container parent) {
/*     */     int rem_size;
/* 203 */     Insets insets = parent.getInsets();
/* 204 */     int maxWidth = (parent.getSize()).width - insets.left + insets.right;
/*     */     
/* 206 */     int maxHeight = (parent.getSize()).height - insets.top + insets.bottom;
/*     */ 
/*     */     
/* 209 */     if ((this.m_sides & 0x8) != 0) maxWidth -= this.b_size;
/*     */     
/* 211 */     if ((this.m_sides & 0x2) != 0) maxWidth -= this.b_size;
/*     */     
/* 213 */     if ((this.m_sides & 0x1) != 0) maxHeight -= this.b_size;
/*     */     
/* 215 */     if ((this.m_sides & 0x4) != 0) maxHeight -= this.b_size;
/*     */     
/* 217 */     int nComps = getComponentCount(parent);
/*     */ 
/*     */ 
/*     */     
/* 221 */     if (this.sizeUnknown) {
/* 222 */       setSizes(parent);
/*     */     }
/*     */     
/* 225 */     int locksize = 0, idx = 0;
/*     */     
/*     */     int i;
/* 228 */     for (i = 0; i < nComps && i < this.num; i++) {
/* 229 */       if ((this.modes[i] & 0x2) != 0) locksize += this.sizes[i]; 
/* 230 */       if ((this.modes[i] & 0x4) != 0 || (this.modes[i] & 0x8) != 0) {
/* 231 */         Component c = parent.getComponent(idx);
/* 232 */         if (c.isVisible()) {
/* 233 */           Dimension d = ((this.modes[i] & 0x4) != 0) ? c.getPreferredSize() : c.getMinimumSize();
/* 234 */           if (this.b_vert) {
/* 235 */             locksize += d.height;
/*     */           } else {
/* 237 */             locksize += d.width;
/*     */           } 
/*     */         } 
/* 240 */       }  if ((this.modes[i] & 0x10) == 0) idx++;
/*     */     
/*     */     } 
/*     */     
/* 244 */     int mult_ofs = 0, one_ofs = 0;
/* 245 */     if (this.b_vert) {
/* 246 */       rem_size = maxHeight - locksize - (this.num - 1) * this.b_size;
/* 247 */       if ((this.m_sides & 0x1) != 0) mult_ofs = this.b_size * 100; 
/* 248 */       if ((this.m_sides & 0x8) != 0) one_ofs = this.b_size; 
/*     */     } else {
/* 250 */       rem_size = maxWidth - locksize - (this.num - 1) * this.b_size;
/* 251 */       if ((this.m_sides & 0x8) != 0) mult_ofs = this.b_size * 100; 
/* 252 */       if ((this.m_sides & 0x1) != 0) one_ofs = this.b_size;
/*     */     
/*     */     } 
/* 255 */     idx = 0;
/* 256 */     for (i = 0; i < nComps && i < this.num; i++) {
/* 257 */       int mysize = 0;
/* 258 */       if ((this.modes[i] & 0x2) != 0) mysize = this.sizes[i] * 100; 
/* 259 */       if ((this.modes[i] & 0x1) != 0) mysize = rem_size * this.sizes[i]; 
/* 260 */       if ((this.modes[i] & 0x10) == 0) {
/* 261 */         Component c = parent.getComponent(idx++);
/* 262 */         if (c.isVisible()) {
/*     */           
/* 264 */           if ((this.modes[i] & 0x4) != 0 || (this.modes[i] & 0x8) != 0) {
/* 265 */             Dimension d = ((this.modes[i] & 0x4) != 0) ? c.getPreferredSize() : c.getMinimumSize();
/* 266 */             if (this.b_vert) {
/* 267 */               mysize = d.height * 100;
/*     */             } else {
/* 269 */               mysize = d.width * 100;
/*     */             } 
/* 271 */           }  if (this.b_vert) {
/* 272 */             c.setBounds(one_ofs, (mult_ofs + 50) / 100, maxWidth, (mysize + 50) / 100);
/*     */           } else {
/* 274 */             c.setBounds((mult_ofs + 50) / 100, one_ofs, (mysize + 50) / 100, maxHeight);
/*     */           } 
/*     */         } 
/*     */       } 
/* 278 */       mult_ofs += mysize + this.b_size * 100;
/*     */     } 
/*     */   }
/*     */   
/*     */   public String toString() {
/* 283 */     return getClass().getName() + " [Gap: " + this.b_size + "]";
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\jeans\graph\PercentLayout.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */