/*    */ package jeans.graph;
/*    */ 
/*    */ import java.awt.Color;
/*    */ import java.awt.Component;
/*    */ import java.awt.Dimension;
/*    */ import java.awt.Graphics;
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
/*    */ public class JColorShower
/*    */   extends Component
/*    */ {
/*    */   public static final long serialVersionUID = 1L;
/*    */   protected Dimension dim;
/*    */   protected Color color;
/*    */   
/*    */   public JColorShower(Color color, int wd, int hi) {
/* 72 */     this.dim = new Dimension(wd, hi);
/* 73 */     this.color = color;
/*    */   }
/*    */   
/*    */   public void paint(Graphics g) {
/* 77 */     Dimension size = getSize();
/* 78 */     int xpos = size.width / 2 - this.dim.width / 2;
/* 79 */     int ypos = size.height / 2 - this.dim.height / 2;
/* 80 */     g.setColor(this.color);
/* 81 */     g.fillRect(xpos, ypos, this.dim.width - 1, this.dim.height - 1);
/* 82 */     g.setColor(Color.black);
/* 83 */     g.drawRect(xpos, ypos, this.dim.width - 1, this.dim.height - 1);
/*    */   }
/*    */   
/*    */   public Dimension getPreferredSize() {
/* 87 */     return this.dim;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jeans\graph\JColorShower.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */