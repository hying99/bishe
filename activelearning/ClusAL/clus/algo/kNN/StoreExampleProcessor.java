/*    */ package clus.algo.kNN;
/*    */ 
/*    */ import clus.data.rows.DataTuple;
/*    */ import clus.data.type.ClusSchema;
/*    */ import clus.model.ClusModel;
/*    */ import clus.model.processor.ClusModelProcessor;
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
/*    */ public class StoreExampleProcessor
/*    */   extends ClusModelProcessor
/*    */ {
/*    */   public void initialize(ClusModel model, ClusSchema schema) throws IOException {}
/*    */   
/*    */   public void terminate(ClusModel model) throws IOException {}
/*    */   
/*    */   public void modelUpdate(DataTuple tuple, ClusModel model) throws IOException {
/* 54 */     KNNTree tree_node = (KNNTree)model;
/*    */     
/* 56 */     tree_node.addTuple(tuple);
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\algo\kNN\StoreExampleProcessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */