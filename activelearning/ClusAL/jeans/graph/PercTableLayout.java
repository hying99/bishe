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
/*     */ public class PercTableLayout
/*     */   implements LayoutManager
/*     */ {
/*     */   private int b_size;
/*     */   private int m_cols;
/*     */   private int[] sizes;
/*     */   private int[] modes;
/*     */   private int[] heights;
/*     */   private int num;
/*     */   private int nbDummies;
/*     */   private int preferredWidth;
/*     */   private int preferredHeight;
/*     */   private boolean sizeUnknown = true;
/*     */   public static final int PERCENT = 1;
/*     */   public static final int ABSOLUTE = 2;
/*     */   public static final int PREF = 4;
/*     */   public static final int DUMMY = 8;
/*     */   
/*     */   public PercTableLayout(String descr, int gap) {
/*  48 */     this.b_size = gap;
/*  49 */     StringTokenizer tokens = new StringTokenizer(descr);
/*  50 */     this.num = tokens.countTokens();
/*  51 */     this.modes = new int[this.num];
/*  52 */     this.sizes = new int[this.num];
/*  53 */     for (int ctr = 0; ctr < this.num; ctr++) {
/*  54 */       this.modes[ctr] = 0;
/*  55 */       this.sizes[ctr] = 0;
/*  56 */       String token = tokens.nextToken();
/*  57 */       int pos = token.indexOf('d');
/*  58 */       if (pos != -1) {
/*  59 */         this.nbDummies++;
/*  60 */         this.modes[ctr] = this.modes[ctr] | 0x8;
/*  61 */         token = token.substring(0, pos);
/*     */       } 
/*  63 */       pos = token.indexOf('%');
/*  64 */       if (pos != -1) {
/*  65 */         this.modes[ctr] = this.modes[ctr] | 0x1;
/*  66 */         token = token.substring(0, pos);
/*     */       } 
/*  68 */       pos = token.indexOf('p');
/*  69 */       if (pos != -1) {
/*  70 */         this.modes[ctr] = this.modes[ctr] | 0x4;
/*     */       } else {
/*     */         try {
/*  73 */           this.sizes[ctr] = Integer.parseInt(token);
/*  74 */         } catch (NumberFormatException numberFormatException) {}
/*     */       } 
/*  76 */       if ((this.modes[ctr] & 0x5) == 0) this.modes[ctr] = this.modes[ctr] | 0x2; 
/*     */     } 
/*  78 */     this.m_cols = this.num - this.nbDummies;
/*     */   }
/*     */   
/*     */   public void addLayoutComponent(String name, Component comp) {}
/*     */   
/*     */   public void removeLayoutComponent(Component comp) {}
/*     */   
/*     */   public int getComponentCount(Container parent) {
/*  86 */     return parent.getComponentCount() + this.nbDummies;
/*     */   }
/*     */   
/*     */   private void calcSizes(Container parent) {
/*  90 */     int nComps = getComponentCount(parent);
/*  91 */     int nRows = nComps / this.m_cols;
/*  92 */     this.heights = new int[nRows];
/*  93 */     for (int i = 0; i < this.num; i++) {
/*  94 */       if ((this.modes[i] & 0x4) != 0) {
/*  95 */         this.sizes[i] = 0;
/*     */       }
/*     */     } 
/*  98 */     for (int row = 0; row < nRows; row++) {
/*  99 */       this.heights[row] = 0;
/* 100 */       int col = 0;
/* 101 */       for (int j = 0; j < this.num; j++) {
/* 102 */         if ((this.modes[j] & 0x8) == 0) {
/* 103 */           Component c = parent.getComponent(col + row * this.m_cols);
/* 104 */           if (c.isVisible()) {
/* 105 */             Dimension d = c.getPreferredSize();
/* 106 */             this.heights[row] = Math.max(this.heights[row], d.height);
/* 107 */             if ((this.modes[j] & 0x4) != 0) {
/* 108 */               this.sizes[j] = Math.max(this.sizes[j], d.width);
/*     */             }
/*     */           } 
/* 111 */           col++;
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private int getColumnWidth(Container parent, int i, int col) {
/* 118 */     int wd = 0;
/* 119 */     if ((this.modes[i] & 0x2) != 0) return this.sizes[i]; 
/* 120 */     if ((this.modes[i] & 0x4) != 0) return this.sizes[i]; 
/* 121 */     int nComps = getComponentCount(parent);
/* 122 */     int nRows = nComps / this.m_cols;
/* 123 */     for (int row = 0; row < nRows; row++) {
/* 124 */       Component c = parent.getComponent(col + row * this.m_cols);
/* 125 */       if (c.isVisible()) {
/* 126 */         Dimension d = c.getPreferredSize();
/* 127 */         wd = Math.max(wd, d.width);
/*     */       } 
/*     */     } 
/* 130 */     return wd;
/*     */   }
/*     */   
/*     */   private void setSizes(Container parent) {
/* 134 */     calcSizes(parent);
/* 135 */     int dim_vert = 0;
/* 136 */     int dim_horz = 0;
/* 137 */     int cr_col = 0;
/* 138 */     int nComps = getComponentCount(parent);
/* 139 */     int nRows = nComps / this.m_cols;
/* 140 */     for (int row = 0; row < nRows; row++) {
/* 141 */       dim_vert += this.heights[row];
/*     */     }
/* 143 */     dim_vert += (nRows - 1) * this.b_size;
/* 144 */     for (int i = 0; i < this.num; i++) {
/* 145 */       if ((this.modes[i] & 0x8) != 0) {
/* 146 */         if ((this.modes[i] & 0x2) != 0)
/* 147 */           dim_horz += this.sizes[i]; 
/*     */       } else {
/* 149 */         if ((this.modes[i] & 0x2) != 0) {
/* 150 */           dim_horz += this.sizes[i];
/*     */         } else {
/* 152 */           dim_horz += getColumnWidth(parent, i, cr_col);
/* 153 */         }  cr_col++;
/*     */       } 
/*     */     } 
/* 156 */     dim_horz += (this.num - 1) * this.b_size;
/* 157 */     this.preferredWidth = dim_horz;
/* 158 */     this.preferredHeight = dim_vert;
/* 159 */     this.sizeUnknown = false;
/*     */   }
/*     */   
/*     */   public Dimension preferredLayoutSize(Container parent) {
/* 163 */     Dimension dim = new Dimension(0, 0);
/* 164 */     setSizes(parent);
/* 165 */     Insets insets = parent.getInsets();
/* 166 */     dim.width = this.preferredWidth + insets.left + insets.right;
/* 167 */     dim.height = this.preferredHeight + insets.top + insets.bottom;
/* 168 */     return dim;
/*     */   }
/*     */   
/*     */   public Dimension minimumLayoutSize(Container parent) {
/* 172 */     return preferredLayoutSize(parent);
/*     */   }
/*     */   
/*     */   public void layoutContainer(Container parent) {
/* 176 */     Insets insets = parent.getInsets();
/* 177 */     int maxWidth = (parent.getSize()).width - insets.left + insets.right;
/* 178 */     int nComps = getComponentCount(parent);
/* 179 */     int nRows = nComps / this.m_cols;
/* 180 */     if (this.sizeUnknown) setSizes(parent); 
/* 181 */     int locksize = 0, cr_col = 0; int i;
/* 182 */     for (i = 0; i < nComps && i < this.num; i++) {
/* 183 */       if ((this.modes[i] & 0x2) != 0) locksize += this.sizes[i]; 
/* 184 */       if ((this.modes[i] & 0x4) != 0) {
/* 185 */         locksize += this.sizes[i];
/*     */       }
/* 187 */       if ((this.modes[i] & 0x8) == 0) cr_col++; 
/*     */     } 
/* 189 */     int horz_ofs = 0;
/* 190 */     int rem_size = maxWidth - locksize - (this.num - 1) * this.b_size;
/* 191 */     cr_col = 0;
/* 192 */     for (i = 0; i < this.num; i++) {
/* 193 */       int vert_ofs = 0;
/* 194 */       int mysize = 0;
/* 195 */       if ((this.modes[i] & 0x2) != 0) mysize = this.sizes[i] * 100; 
/* 196 */       if ((this.modes[i] & 0x1) != 0) mysize = rem_size * this.sizes[i]; 
/* 197 */       if ((this.modes[i] & 0x8) == 0) {
/* 198 */         if ((this.modes[i] & 0x4) != 0) {
/* 199 */           mysize = this.sizes[i] * 100;
/*     */         }
/* 201 */         int xp = (horz_ofs + 50) / 100;
/* 202 */         int wd = (mysize + 50) / 100;
/* 203 */         for (int row = 0; row < nRows; row++) {
/* 204 */           Component c = parent.getComponent(cr_col + row * this.m_cols);
/* 205 */           c.setBounds(xp, vert_ofs, wd, this.heights[row]);
/* 206 */           vert_ofs += this.heights[row] + this.b_size;
/*     */         } 
/* 208 */         cr_col++;
/*     */       } 
/* 210 */       horz_ofs += mysize + this.b_size * 100;
/*     */     } 
/*     */   }
/*     */   
/*     */   public String toString() {
/* 215 */     return getClass().getName() + " [Gap: " + this.b_size + "]";
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jeans\graph\PercTableLayout.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */