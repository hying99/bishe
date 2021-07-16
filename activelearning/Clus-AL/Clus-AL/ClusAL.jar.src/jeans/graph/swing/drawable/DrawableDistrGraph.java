/*    */ package jeans.graph.swing.drawable;
/*    */ 
/*    */ import java.awt.Color;
/*    */ import java.awt.Graphics2D;
/*    */ import jeans.graph.plot.MDistrInfo;
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
/*    */ public class DrawableDistrGraph
/*    */   extends Drawable
/*    */ {
/*    */   protected MDistrInfo m_hDistrInfo;
/*    */   protected float m_fTotal;
/*    */   
/*    */   public DrawableDistrGraph(int wd, int hi, MDistrInfo distInfo, float total) {
/* 35 */     this.wd = wd;
/* 36 */     this.hi = hi;
/* 37 */     this.m_hDistrInfo = distInfo;
/* 38 */     this.m_fTotal = total;
/*    */   }
/*    */   
/*    */   public void draw(Graphics2D g, DrawableCanvas canvas, int xofs, int yofs) {
/* 42 */     int mxp = this.xp - xofs;
/* 43 */     int myp = this.yp - yofs;
/* 44 */     int xprev = mxp;
/* 45 */     float value = 0.0F;
/* 46 */     for (int i = 0; i < this.m_hDistrInfo.getNbBins(); i++) {
/* 47 */       g.setColor(this.m_hDistrInfo.getBinColor(i));
/* 48 */       value += this.m_hDistrInfo.getBinCount(i);
/* 49 */       float delta = value * this.wd / this.m_fTotal;
/* 50 */       int xnext = Math.min(this.wd, Math.round(delta)) + mxp;
/* 51 */       g.fillRect(xprev, myp, xnext - xprev, this.hi);
/* 52 */       g.setColor(Color.black);
/* 53 */       g.drawLine(xprev, myp, xprev, myp + this.hi);
/* 54 */       xprev = xnext;
/*    */     } 
/* 56 */     g.drawLine(xprev, myp, xprev, myp + this.hi);
/* 57 */     g.setColor(Color.black);
/* 58 */     g.drawRect(mxp, myp, this.wd, this.hi);
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\jeans\graph\swing\drawable\DrawableDistrGraph.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */