/*    */ package clus.model.test;
/*    */ 
/*    */ import clus.data.cols.attribute.ClusAttribute;
/*    */ import clus.data.rows.DataTuple;
/*    */ import clus.data.rows.RowData;
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
/*    */ public abstract class SoftTest
/*    */   extends NodeTest
/*    */ {
/*    */   public abstract int softPredictNb(DataTuple paramDataTuple, int paramInt);
/*    */   
/*    */   public abstract int softPredict(RowData paramRowData, DataTuple paramDataTuple, int paramInt1, int paramInt2);
/*    */   
/*    */   public abstract int softPredictNb2(DataTuple paramDataTuple, int paramInt);
/*    */   
/*    */   public abstract int softPredict2(RowData paramRowData, DataTuple paramDataTuple, int paramInt1, int paramInt2);
/*    */   
/*    */   public int predict(ClusAttribute attr, int idx) {
/* 40 */     return 1;
/*    */   }
/*    */   
/*    */   public int predictWeighted(DataTuple tuple) {
/* 44 */     return 1;
/*    */   }
/*    */   
/*    */   public boolean equals(NodeTest test) {
/* 48 */     return false;
/*    */   }
/*    */   
/*    */   public boolean isSoft() {
/* 52 */     return true;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\model\test\SoftTest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */