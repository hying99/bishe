/*    */ package jeans.graph.awt;
/*    */ 
/*    */ import java.awt.LayoutManager;
/*    */ import java.awt.Panel;
/*    */ import jeans.graph.PercentLayout;
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
/*    */ public class AlignTabPanel
/*    */   extends Panel
/*    */ {
/*    */   public static final long serialVersionUID = 1L;
/*    */   
/*    */   public AlignTabPanel(Panel parent, int size) {
/* 33 */     String strg = String.valueOf(size) + "d 100%";
/* 34 */     setLayout((LayoutManager)new PercentLayout(strg, 0, 0, false));
/* 35 */     parent.add(this);
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\jeans\graph\awt\AlignTabPanel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */