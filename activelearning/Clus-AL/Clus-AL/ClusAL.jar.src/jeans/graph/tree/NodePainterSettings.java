/*    */ package jeans.graph.tree;
/*    */ 
/*    */ import java.awt.Color;
/*    */ import jeans.util.MyVisitorParent;
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
/*    */ public class NodePainterSettings
/*    */   extends MyVisitorParent
/*    */ {
/* 31 */   public Color NODE_COLOR = Color.white;
/* 32 */   public Color LEAF_COLOR = Color.white;
/* 33 */   public Color NODE_BORDER_COLOR = Color.black;
/* 34 */   public Color LEAF_BORDER_COLOR = Color.black;
/*    */   
/* 36 */   public int YTOP = 10;
/* 37 */   public int XLEFT = 10;
/*    */   
/*    */   public int[] XGAP;
/*    */   
/* 41 */   public int YGAP = 20;
/*    */   
/*    */   protected Object m_Document;
/*    */   
/*    */   public void setDocument(Object doc) {
/* 46 */     this.m_Document = doc;
/*    */   }
/*    */   
/*    */   public Object getDocument() {
/* 50 */     return this.m_Document;
/*    */   }
/*    */   
/*    */   public void print() {
/* 54 */     System.out.println("YTOP  = " + this.YTOP);
/* 55 */     System.out.println("XLEFT = " + this.XLEFT);
/* 56 */     System.out.println("XGAP  = " + this.XGAP);
/* 57 */     System.out.println("YGAP  = " + this.YGAP);
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\jeans\graph\tree\NodePainterSettings.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */