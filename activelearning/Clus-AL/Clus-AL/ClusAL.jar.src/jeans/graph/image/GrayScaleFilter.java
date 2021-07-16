/*    */ package jeans.graph.image;
/*    */ 
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class GrayScaleFilter
/*    */   extends RGBImageFilter
/*    */ {
/*    */   public int filterRGB(int x, int y, int rgb) {
/* 39 */     int red = rgb >> 16 & 0xFF;
/* 40 */     int green = rgb >> 8 & 0xFF;
/* 41 */     int blue = rgb >> 8 & 0xFF;
/* 42 */     int gray = (red + green + blue) / 3;
/* 43 */     return rgb & 0xFF000000 | gray << 16 | gray << 8 | gray;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\jeans\graph\image\GrayScaleFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */