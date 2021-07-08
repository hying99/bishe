/*    */ package jeans.graph.drawable;
/*    */ 
/*    */ import java.awt.Rectangle;
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
/*    */ public abstract class Drawable
/*    */ {
/* 33 */   protected int xp = 0;
/* 34 */   protected int yp = 0;
/* 35 */   protected int wd = -1;
/* 36 */   protected int hi = -1;
/*    */ 
/*    */   
/*    */   public void setPosition(int xp, int yp) {
/* 40 */     this.xp = xp;
/* 41 */     this.yp = yp;
/*    */   }
/*    */   
/*    */   public int isInY(int ypos) {
/* 45 */     if (ypos < this.yp) return -1; 
/* 46 */     if (ypos > this.yp + this.hi) return 1; 
/* 47 */     return 0;
/*    */   }
/*    */   
/*    */   public abstract Rectangle getBoundRect(DrawableProvider paramDrawableProvider);
/*    */   
/*    */   public abstract void draw(DrawableProvider paramDrawableProvider, int paramInt1, int paramInt2);
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jeans\graph\drawable\Drawable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */