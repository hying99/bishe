/*    */ package jeans.graph.image;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class RotateFilter
/*    */   extends TransformFilter
/*    */ {
/*    */   private double sin;
/*    */   private double cos;
/*    */   private int o_x;
/*    */   private int o_y;
/* 45 */   private double[] coord = new double[2];
/*    */   
/*    */   public RotateFilter(double angle, int ox, int oy) {
/* 48 */     super(3);
/* 49 */     this.o_x = ox;
/* 50 */     this.o_y = oy;
/* 51 */     this.sin = Math.sin(angle);
/* 52 */     this.cos = Math.cos(angle);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void transform(double x, double y, double[] retcoord) {
/* 60 */     retcoord[0] = this.cos * x + this.sin * y;
/* 61 */     retcoord[1] = this.cos * y - this.sin * x;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void itransform(double x, double y, double[] retcoord) {
/* 69 */     retcoord[0] = this.cos * x - this.sin * y;
/* 70 */     retcoord[1] = this.cos * y + this.sin * x;
/*    */   }
/*    */   
/*    */   public void imageComplete(int status) {
/* 74 */     if (status == 1 || status == 4) {
/* 75 */       this.consumer.imageComplete(status);
/*    */       return;
/*    */     } 
/* 78 */     int[] pixels = new int[this.dstW];
/* 79 */     int mxaddr = this.srcW * this.srcH;
/* 80 */     for (int dy = 0; dy < this.dstH; dy++) {
/* 81 */       for (int dx = 0; dx < this.dstW; dx++) {
/* 82 */         transform((dx - this.o_x), (dy - this.o_y), this.coord);
/* 83 */         int xp = (int)Math.round(this.coord[0] + this.o_x);
/* 84 */         int yp = (int)Math.round(this.coord[1] + this.o_y);
/* 85 */         int addr = yp * this.srcW + xp;
/* 86 */         if (addr >= 0 && addr < mxaddr) {
/* 87 */           pixels[dx] = this.raster[addr];
/*    */         } else {
/* 89 */           pixels[dx] = 0;
/*    */         } 
/*    */       } 
/* 92 */       this.consumer.setPixels(0, dy, this.dstW, 1, defaultRGB, pixels, 0, this.dstW);
/*    */     } 
/* 94 */     this.consumer.imageComplete(status);
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\jeans\graph\image\RotateFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */