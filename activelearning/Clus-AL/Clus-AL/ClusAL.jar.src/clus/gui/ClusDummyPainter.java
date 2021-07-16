/*    */ package clus.gui;
/*    */ 
/*    */ import clus.algo.tdidt.ClusNode;
/*    */ import clus.model.test.NodeTest;
/*    */ import java.awt.FontMetrics;
/*    */ import java.awt.Graphics2D;
/*    */ import java.awt.SystemColor;
/*    */ import jeans.graph.swing.drawable.Drawable;
/*    */ import jeans.graph.swing.drawable.DrawableCanvas;
/*    */ import jeans.graph.swing.drawable.DrawableLines;
/*    */ import jeans.graph.tree.MyDrawableNode;
/*    */ import jeans.graph.tree.MyNodePainter;
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
/*    */ public class ClusDummyPainter
/*    */   extends MyNodePainter
/*    */ {
/*    */   protected Drawable m_Label;
/*    */   
/*    */   public ClusDummyPainter(ClusNode node, int idx) {
/* 37 */     super(null);
/* 38 */     this.m_Label = createLabel(node, idx);
/*    */   }
/*    */   
/*    */   public MyNodePainter createPainter(MyDrawableNode node) {
/* 42 */     return null;
/*    */   }
/*    */   
/*    */   public Drawable getLabel() {
/* 46 */     return this.m_Label;
/*    */   }
/*    */   
/*    */   public void calcSize(Graphics2D g, FontMetrics fm, DrawableCanvas cnv) {
/* 50 */     if (this.m_Label != null) this.m_Label.calcSize(g, fm, cnv); 
/* 51 */     this.wd = 20;
/* 52 */     this.hi = 0;
/*    */   }
/*    */ 
/*    */   
/*    */   public void draw(Graphics2D g, DrawableCanvas cnv, int xofs, int yofs) {}
/*    */   
/*    */   private Drawable createLabel(ClusNode parent, int idx) {
/* 59 */     NodeTest test = parent.getTest();
/* 60 */     if (test.hasBranchLabels()) {
/* 61 */       String label = test.getBranchLabel(idx);
/* 62 */       DrawableLines res = new DrawableLines(label);
/* 63 */       res.setBackground(SystemColor.control);
/* 64 */       return (Drawable)res;
/*    */     } 
/* 66 */     return null;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\gui\ClusDummyPainter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */