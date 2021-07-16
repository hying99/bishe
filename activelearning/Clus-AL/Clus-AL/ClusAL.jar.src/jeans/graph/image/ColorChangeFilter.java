/*    */ package jeans.graph.image;
/*    */ 
/*    */ import java.awt.Color;
/*    */ import java.awt.image.RGBImageFilter;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ColorChangeFilter
/*    */   extends RGBImageFilter
/*    */ {
/*    */   int scolor;
/*    */   int tcolor;
/*    */   
/*    */   public ColorChangeFilter(Color a, Color b) {
/* 37 */     this.scolor = a.getRed() << 16 | a.getGreen() << 8 | a.getBlue() << 0;
/* 38 */     this.tcolor = b.getRed() << 16 | b.getGreen() << 8 | b.getBlue() << 0;
/* 39 */     this.canFilterIndexColorModel = true;
/*    */   }
/*    */   
/*    */   public ColorChangeFilter(String a, String b) {
/* 43 */     this(stringToColor(a), stringToColor(b));
/*    */   }
/*    */   
/*    */   public int filterRGB(int x, int y, int rgb) {
/* 47 */     int lorgb = rgb & 0xFFFFFF;
/* 48 */     if (lorgb == this.scolor) lorgb = this.tcolor; 
/* 49 */     return rgb & 0xFF000000 | lorgb;
/*    */   }
/*    */   
/*    */   public String toString() {
/* 53 */     return Integer.toString(this.scolor, 16) + " -> " + Integer.toString(this.tcolor, 16);
/*    */   }
/*    */ 
/*    */   
/*    */   public static Color stringToColor(String strg) throws NumberFormatException {
/* 58 */     int ofs = 0;
/* 59 */     if (strg.startsWith("0x")) { ofs = 2; }
/* 60 */     else if (strg.startsWith("#")) { ofs = 1; }
/*    */      try {
/* 62 */       int red = Integer.parseInt(strg.substring(ofs, ofs + 2), 16);
/* 63 */       int green = Integer.parseInt(strg.substring(ofs + 2, ofs + 4), 16);
/* 64 */       int blue = Integer.parseInt(strg.substring(ofs + 4, ofs + 6), 16);
/* 65 */       return new Color(red, green, blue);
/* 66 */     } catch (ArrayIndexOutOfBoundsException e) {
/* 67 */       throw new NumberFormatException();
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\jeans\graph\image\ColorChangeFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */