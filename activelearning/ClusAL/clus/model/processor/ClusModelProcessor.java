/*    */ package clus.model.processor;
/*    */ 
/*    */ import clus.data.rows.DataTuple;
/*    */ import clus.data.type.ClusSchema;
/*    */ import clus.model.ClusModel;
/*    */ import clus.model.ClusModelInfo;
/*    */ import clus.statistic.ClusStatistic;
/*    */ import clus.util.ClusException;
/*    */ import java.io.IOException;
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
/*    */ public abstract class ClusModelProcessor
/*    */ {
/*    */   public boolean shouldProcessModel(ClusModelInfo info) {
/* 37 */     return true;
/*    */   }
/*    */ 
/*    */   
/*    */   public void addModelInfo(ClusModelInfo info) {}
/*    */ 
/*    */   
/*    */   public void initialize(ClusModel model, ClusSchema schema) throws IOException, ClusException {}
/*    */ 
/*    */   
/*    */   public void initializeAll(ClusSchema schema) throws IOException, ClusException {}
/*    */ 
/*    */   
/*    */   public void terminate(ClusModel model) throws IOException {}
/*    */ 
/*    */   
/*    */   public void terminateAll() throws IOException {}
/*    */ 
/*    */   
/*    */   public void exampleUpdate(DataTuple tuple) throws IOException {}
/*    */ 
/*    */   
/*    */   public void exampleDone() throws IOException {}
/*    */ 
/*    */   
/*    */   public void exampleUpdate(DataTuple tuple, ClusStatistic distr) throws IOException {}
/*    */ 
/*    */   
/*    */   public void modelUpdate(DataTuple tuple, ClusModel model) throws IOException {}
/*    */ 
/*    */   
/*    */   public void modelDone() throws IOException {}
/*    */   
/*    */   public boolean needsModelUpdate() {
/* 71 */     return false;
/*    */   }
/*    */   
/*    */   public boolean needsInternalNodes() {
/* 75 */     return false;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\model\processor\ClusModelProcessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */