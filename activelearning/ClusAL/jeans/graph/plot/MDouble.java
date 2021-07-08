/*     */ package jeans.graph.plot;
/*     */ 
/*     */ import java.awt.FontMetrics;
/*     */ import java.awt.Graphics;
/*     */ import jeans.util.StringUtils;
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
/*     */ public class MDouble
/*     */ {
/*     */   public static final int RND_FLOOR = 0;
/*     */   public static final int RND_CEIL = 1;
/*     */   public static final int RND_ROUND = 2;
/*  35 */   protected static final double[] vb = new double[] { 12.0D, 1.2D, 3.0D, 21.0D, 0.01D, 1500.0D, 1350.0D, 0.2654D };
/*     */ 
/*     */   
/*  38 */   protected int m_iNbDigits = 2;
/*     */   
/*     */   protected double m_fReal;
/*     */   
/*     */   public void setRounding(int nb) {
/*  43 */     this.m_iNbDigits = nb;
/*     */   }
/*     */   protected double m_fDigit; protected int m_iPower;
/*     */   public void setFloorValue(double value) {
/*  47 */     setValue(value, 0);
/*     */   }
/*     */   
/*     */   public void setCeilValue(double value) {
/*  51 */     setValue(value, 1);
/*     */   }
/*     */   
/*     */   public void setRoundValue(double value) {
/*  55 */     setValue(value, 2);
/*     */   }
/*     */   
/*     */   public int getWidth(FontMetrics fm) {
/*  59 */     String digit = double2String(this.m_fDigit);
/*  60 */     int wd = fm.stringWidth(digit);
/*  61 */     if (this.m_iPower != 0) {
/*  62 */       wd += fm.stringWidth(".10");
/*  63 */       wd += fm.stringWidth(String.valueOf(this.m_iPower));
/*     */     } 
/*  65 */     return wd;
/*     */   }
/*     */   
/*     */   public void draw(FontMetrics fm, Graphics g, int x, int y) {
/*  69 */     String digit = double2String(this.m_fDigit);
/*  70 */     int ypos = y + fm.getMaxAscent();
/*  71 */     g.drawString(digit, x, ypos);
/*  72 */     if (this.m_iPower != 0) {
/*  73 */       x += fm.stringWidth(digit);
/*  74 */       g.drawString(".", x, ypos - 3);
/*  75 */       x += fm.charWidth('.');
/*  76 */       g.drawString("10", x, ypos);
/*  77 */       x += fm.stringWidth("10");
/*  78 */       String pow = String.valueOf(this.m_iPower);
/*  79 */       g.drawString(pow, x, ypos - 4);
/*     */     } 
/*     */   }
/*     */   
/*     */   public static void killZeros(StringBuffer buffer) {
/*  84 */     int len = buffer.length();
/*  85 */     int pos = len - 1;
/*  86 */     for (; pos > 0 && buffer.charAt(pos) == '0'; pos--);
/*  87 */     if (pos != len - 1) {
/*  88 */       if (pos != 0 && buffer.charAt(pos) != '.') pos++; 
/*  89 */       buffer.delete(pos, len);
/*     */     } 
/*     */   }
/*     */   
/*     */   public String double2String(double value) {
/*  94 */     return StringUtils.roundDouble(this.m_fReal, 2);
/*     */   }
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
/*     */   public void setValue(double value, int mode) {
/* 140 */     this.m_fReal = value;
/*     */   }
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
/*     */   public static double round(double value, int mode) {
/* 181 */     switch (mode) {
/*     */       case 0:
/* 183 */         return Math.floor(value);
/*     */       case 1:
/* 185 */         return Math.ceil(value);
/*     */       case 2:
/* 187 */         return Math.round(value);
/*     */     } 
/* 189 */     return 0.0D;
/*     */   }
/*     */   
/*     */   public static double pow10(int nb) {
/* 193 */     if (nb > 0) {
/* 194 */       double res = 10.0D;
/* 195 */       while (nb > 1) {
/* 196 */         res *= 10.0D;
/* 197 */         nb--;
/*     */       } 
/* 199 */       return res;
/* 200 */     }  if (nb < 0) {
/* 201 */       double res = 0.1D;
/* 202 */       while (nb < -1) {
/* 203 */         res /= 10.0D;
/* 204 */         nb++;
/*     */       } 
/* 206 */       return res;
/*     */     } 
/* 208 */     return 1.0D;
/*     */   }
/*     */ 
/*     */   
/*     */   public static int getNbDigits(double number) {
/* 213 */     double val = Math.abs(number);
/* 214 */     if (val == 0.0D) return 0; 
/* 215 */     if (val >= 1.0D) return (int)Math.floor(Math.log(val) / Math.log(10.0D) + 1.0E-7D) + 1; 
/* 216 */     return -((int)Math.ceil(-Math.log(val) / Math.log(10.0D) - 1.0E-7D));
/*     */   }
/*     */   
/*     */   public float getFloat() {
/* 220 */     return (float)this.m_fReal;
/*     */   }
/*     */   
/*     */   public double getDigit() {
/* 224 */     return this.m_fDigit;
/*     */   }
/*     */   
/*     */   public int getPower() {
/* 228 */     return this.m_iPower;
/*     */   }
/*     */   
/*     */   public static void main(String[] args) {
/* 232 */     MDouble fl = new MDouble();
/* 233 */     for (int i = 0; i < vb.length; i++)
/* 234 */       System.out.println("double " + vb[i] + " " + fl.double2String(vb[i])); 
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jeans\graph\plot\MDouble.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */