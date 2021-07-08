/*    */ package clus.ext.beamsearch;
/*    */ 
/*    */ import clus.data.rows.RowData;
/*    */ import clus.model.test.NodeTest;
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
/*    */ public class ClusBeamAttrSelector
/*    */ {
/*    */   public RowData data;
/*    */   public boolean stopcrit;
/*    */   public NodeTest[] besttests;
/*    */   
/*    */   public final boolean hasEvaluations() {
/* 38 */     return (this.besttests != null);
/*    */   }
/*    */   
/*    */   public final NodeTest[] getBestTests() {
/* 42 */     return this.besttests;
/*    */   }
/*    */   
/*    */   public final void setData(RowData data) {
/* 46 */     this.data = data;
/*    */   }
/*    */   
/*    */   public final RowData getData() {
/* 50 */     return this.data;
/*    */   }
/*    */   
/*    */   public final void setStopCrit(boolean stopcrit) {
/* 54 */     this.stopcrit = stopcrit;
/*    */   }
/*    */   
/*    */   public final boolean isStopCrit() {
/* 58 */     return this.stopcrit;
/*    */   }
/*    */   
/*    */   public final void newEvaluations(int nb) {
/* 62 */     this.besttests = new NodeTest[nb];
/*    */   }
/*    */   
/*    */   public final void setBestTest(int i, NodeTest test) {
/* 66 */     this.besttests[i] = test;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\ext\beamsearch\ClusBeamAttrSelector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */