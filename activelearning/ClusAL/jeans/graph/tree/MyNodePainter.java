/*    */ package jeans.graph.tree;
/*    */ 
/*    */ import java.awt.Color;
/*    */ import java.awt.Graphics2D;
/*    */ import jeans.graph.swing.drawable.Drawable;
/*    */ import jeans.graph.swing.drawable.DrawableCanvas;
/*    */ import jeans.graph.swing.drawable.DrawableExpandButton;
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
/*    */ public abstract class MyNodePainter
/*    */   extends Drawable
/*    */ {
/*    */   protected MyDrawableNode m_Node;
/*    */   protected int m_Zoom;
/*    */   
/*    */   public MyNodePainter(MyDrawableNode node) {
/* 35 */     this.m_Node = node;
/*    */   }
/*    */   
/*    */   public MyDrawableNode getNode() {
/* 39 */     return this.m_Node;
/*    */   }
/*    */   
/*    */   public Color getNodeColor() {
/* 43 */     if (this.m_Node.atBottomLevel()) return (this.m_Node.getPaintSettings()).LEAF_COLOR; 
/* 44 */     return (this.m_Node.getPaintSettings()).NODE_COLOR;
/*    */   }
/*    */   
/*    */   public Color getBorderColor() {
/* 48 */     if (this.m_Node.atBottomLevel()) return (this.m_Node.getPaintSettings()).LEAF_BORDER_COLOR; 
/* 49 */     return (this.m_Node.getPaintSettings()).NODE_BORDER_COLOR;
/*    */   }
/*    */   
/*    */   public abstract MyNodePainter createPainter(MyDrawableNode paramMyDrawableNode);
/*    */   
/*    */   public DrawableExpandButton getExpandButton() {
/* 55 */     return null;
/*    */   }
/*    */   
/*    */   public Drawable getLabel() {
/* 59 */     return null;
/*    */   }
/*    */   
/*    */   public int getZoom() {
/* 63 */     return this.m_Zoom;
/*    */   }
/*    */   
/*    */   public void setZoom(int zoom) {
/* 67 */     this.m_Zoom = zoom;
/*    */   }
/*    */ 
/*    */   
/*    */   public void onFakeLeaf(boolean fake) {}
/*    */   
/*    */   public void draw(Graphics2D g, DrawableCanvas canvas, int xofs, int yofs) {
/* 74 */     g.setColor(getNodeColor());
/* 75 */     g.fillRect(this.xp - xofs, this.yp - yofs, this.wd, this.hi);
/* 76 */     g.setColor(getBorderColor());
/* 77 */     g.drawRect(this.xp - xofs, this.yp - yofs, this.wd, this.hi);
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jeans\graph\tree\MyNodePainter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */