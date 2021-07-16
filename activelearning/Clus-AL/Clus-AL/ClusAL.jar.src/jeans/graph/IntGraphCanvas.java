/*     */ package jeans.graph;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.awt.Dimension;
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
/*     */ public class IntGraphCanvas
/*     */   extends BufferCanvas
/*     */ {
/*     */   public static final long serialVersionUID = 1L;
/*     */   protected String m_labelx;
/*     */   protected String m_labely;
/*     */   protected int[] m_data;
/*     */   protected int[] m_type;
/*  33 */   protected IntGraphType[] m_datatypes = new IntGraphType[1];
/*     */   
/*     */   public IntGraphCanvas(int wd, int hi) {
/*  36 */     super(wd, hi);
/*     */   }
/*     */   
/*     */   public void setNbData(int size) {
/*  40 */     if (this.m_data == null || this.m_data.length != size) {
/*  41 */       this.m_data = new int[size];
/*  42 */       this.m_type = new int[size];
/*     */     } 
/*     */   }
/*     */   
/*     */   public void setDataItem(int idx, int value, int type) {
/*  47 */     this.m_data[idx] = value;
/*  48 */     this.m_type[idx] = type;
/*     */   }
/*     */   
/*     */   public void addDataType(int type, String name, Color color) {
/*  52 */     if (this.m_datatypes.length <= type) {
/*  53 */       IntGraphType[] newtypes = new IntGraphType[type + 1];
/*  54 */       System.arraycopy(this.m_datatypes, 0, newtypes, 0, this.m_datatypes.length);
/*  55 */       this.m_datatypes = newtypes;
/*     */     } 
/*  57 */     System.out.println("Adding data type: " + type + " " + name + " " + color);
/*  58 */     this.m_datatypes[type] = new IntGraphType(name, color);
/*     */   }
/*     */   
/*     */   public void setLabelX(String label) {
/*  62 */     this.m_labelx = label;
/*     */   }
/*     */   
/*     */   public void setLabelY(String label) {
/*  66 */     this.m_labely = label;
/*     */   }
/*     */   
/*     */   public void paintIt(Graphics g, Dimension d) {
/*  70 */     g.setColor(getBackground());
/*  71 */     g.fillRect(0, 0, d.width, d.height);
/*  72 */     FontMetrics fm = g.getFontMetrics();
/*  73 */     int legwd = 0;
/*  74 */     if (this.m_datatypes.length > 1) {
/*  75 */       for (int i = 0; i < this.m_datatypes.length; i++) {
/*  76 */         IntGraphType t = this.m_datatypes[i];
/*  77 */         legwd = Math.max(legwd, fm.stringWidth(t.m_name));
/*     */       } 
/*  79 */       legwd += 25;
/*     */     } 
/*  81 */     int labelxhi = 0;
/*  82 */     if (this.m_labelx != null) labelxhi = fm.getHeight() + 5; 
/*  83 */     int labelywd = 0;
/*  84 */     if (this.m_labely != null) labelywd = fm.stringWidth(this.m_labely) + 5; 
/*  85 */     int leftwd = d.width - legwd - labelywd;
/*  86 */     int nbdata = this.m_data.length;
/*  87 */     int ygraph = d.height - labelxhi - 3;
/*  88 */     int wd_i = Math.min(leftwd / nbdata, 30);
/*  89 */     int updelta = Math.max(5, wd_i / 2);
/*  90 */     int maxvalue = 0;
/*  91 */     int scale = 100;
/*  92 */     if (nbdata > 0) {
/*  93 */       for (int i = 0; i < this.m_data.length; i++) {
/*  94 */         int value = this.m_data[i];
/*  95 */         maxvalue = Math.max(maxvalue, value);
/*     */       } 
/*  97 */       scale = (ygraph - updelta) * 100 / maxvalue;
/*     */     } 
/*  99 */     int bases = (int)Math.floor(Math.log(maxvalue) / Math.log(10.0D) - 1.0D);
/* 100 */     int factor = (int)Math.round(Math.pow(10.0D, bases));
/* 101 */     int step = factor;
/* 102 */     while (maxvalue / step > 5)
/* 103 */       step *= 5; 
/* 104 */     int crfac = step;
/* 105 */     int scalewd = 0;
/* 106 */     while (crfac <= maxvalue) {
/* 107 */       scalewd = Math.max(scalewd, fm.stringWidth(String.valueOf(crfac)));
/* 108 */       crfac += step;
/*     */     } 
/* 110 */     scalewd += 6;
/* 111 */     int xgstart = 5 + labelywd + scalewd;
/* 112 */     int xp = xgstart + 5;
/* 113 */     leftwd -= scalewd;
/* 114 */     wd_i = Math.min(leftwd / nbdata, 30);
/* 115 */     if (nbdata > 0) {
/* 116 */       for (int i = 0; i < this.m_data.length; i++) {
/* 117 */         int value = this.m_data[i] * scale / 100;
/* 118 */         int type = this.m_type[i];
/* 119 */         if (type < this.m_datatypes.length) g.setColor((this.m_datatypes[type]).m_color); 
/* 120 */         ImageUtil.drawCube(g, xp, ygraph - value, wd_i * 4 / 6, value, wd_i * 3 / 6, 0.5F);
/* 121 */         xp += wd_i;
/*     */       } 
/*     */     }
/* 124 */     g.setColor(Color.black);
/* 125 */     g.drawLine(xgstart, ygraph + 1, xgstart, updelta);
/* 126 */     g.drawRect(xgstart, ygraph + 1, leftwd, 2);
/* 127 */     crfac = step;
/* 128 */     while (crfac <= maxvalue) {
/* 129 */       int pos = ygraph - crfac * scale / 100;
/* 130 */       g.drawLine(xgstart - 2, pos, xgstart, pos);
/* 131 */       String strg = String.valueOf(crfac);
/* 132 */       g.drawString(strg, xgstart - 6 - fm.stringWidth(strg), pos + fm.getMaxAscent() / 2);
/* 133 */       crfac += step;
/*     */     } 
/* 135 */     if (this.m_labelx != null) {
/* 136 */       int lyw = fm.stringWidth(this.m_labelx);
/* 137 */       int xly = xgstart + leftwd / 2 - lyw / 2;
/* 138 */       int yly = d.height - 5;
/* 139 */       g.drawString(this.m_labelx, xly, yly);
/*     */     } 
/* 141 */     if (this.m_labely != null) {
/* 142 */       g.drawString(this.m_labely, 5, 10 + fm.getMaxAscent() / 2);
/*     */     }
/* 144 */     int xleg = d.width - legwd + 5;
/* 145 */     int yleg = 5;
/* 146 */     int ylegdelta = Math.max(fm.getHeight(), 10) + 2;
/* 147 */     if (this.m_datatypes.length > 1)
/* 148 */       for (int i = 0; i < this.m_datatypes.length; i++) {
/* 149 */         IntGraphType t = this.m_datatypes[i];
/* 150 */         g.setColor(t.m_color);
/* 151 */         g.fillRect(xleg, yleg, 10, 10);
/* 152 */         g.setColor(Color.black);
/* 153 */         g.drawRect(xleg, yleg, 10, 10);
/* 154 */         g.drawString(t.m_name, xleg + 10 + 5, yleg + 5 + fm.getMaxAscent() / 2);
/* 155 */         yleg += ylegdelta;
/*     */       }  
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\jeans\graph\IntGraphCanvas.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */