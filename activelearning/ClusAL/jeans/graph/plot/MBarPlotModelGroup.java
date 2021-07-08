/*    */ package jeans.graph.plot;
/*    */ 
/*    */ import java.util.Vector;
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
/*    */ public class MBarPlotModelGroup
/*    */ {
/* 29 */   protected Vector m_hModels = new Vector();
/*    */   protected String m_hTitle;
/*    */   
/*    */   public MBarPlotModelGroup(String title) {
/* 33 */     this.m_hTitle = title;
/*    */   }
/*    */   
/*    */   public int getNbModels() {
/* 37 */     return this.m_hModels.size();
/*    */   }
/*    */   
/*    */   public String getTitle() {
/* 41 */     return this.m_hTitle;
/*    */   }
/*    */   
/*    */   public void addModel(MBarPlotModel model) {
/* 45 */     this.m_hModels.addElement(model);
/*    */   }
/*    */   
/*    */   public MBarPlotModel getModel(int idx) {
/* 49 */     return this.m_hModels.elementAt(idx);
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jeans\graph\plot\MBarPlotModelGroup.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */