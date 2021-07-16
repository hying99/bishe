/*    */ package jeans.graph;
/*    */ 
/*    */ import java.awt.Color;
/*    */ import java.awt.GridLayout;
/*    */ import java.awt.Panel;
/*    */ import jeans.util.CallBackFunction;
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
/*    */ public class ColorSelector
/*    */   extends Panel
/*    */   implements CallBackFunction
/*    */ {
/*    */   public static final long serialVersionUID = 1L;
/* 32 */   CallBackFunction call_back = null;
/*    */   ColorBar red;
/*    */   
/*    */   public ColorSelector(CallBackFunction modified, boolean horiz) {
/* 36 */     this.call_back = modified;
/* 37 */     if (horiz) { setLayout(new GridLayout(0, 1)); }
/* 38 */     else { setLayout(new GridLayout(1, 0)); }
/* 39 */      add(this.red = new ColorBar(this, "R", horiz));
/* 40 */     add(this.green = new ColorBar(this, "G", horiz));
/* 41 */     add(this.blue = new ColorBar(this, "B", horiz));
/* 42 */     this.red.setColor(Color.red);
/* 43 */     this.green.setColor(Color.green);
/* 44 */     this.blue.setColor(Color.blue);
/*    */   }
/*    */   ColorBar green; ColorBar blue;
/*    */   public void setColor(int red, int green, int blue) {
/* 48 */     this.red.setValue(red);
/* 49 */     this.green.setValue(green);
/* 50 */     this.blue.setValue(blue);
/*    */   }
/*    */   
/*    */   public void setColor(Color color) {
/* 54 */     setColor(color.getRed(), color.getGreen(), color.getBlue());
/*    */   }
/*    */   
/*    */   public Color getColor() {
/* 58 */     int red = this.red.getValue();
/* 59 */     int green = this.green.getValue();
/* 60 */     int blue = this.blue.getValue();
/* 61 */     return new Color(red, green, blue);
/*    */   }
/*    */   
/*    */   public void callBackFunction(Object obj) {
/* 65 */     if (this.call_back != null)
/* 66 */       this.call_back.callBackFunction(obj); 
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\jeans\graph\ColorSelector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */