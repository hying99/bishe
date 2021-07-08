/*    */ package clus.algo.tdidt.processor;
/*    */ 
/*    */ import clus.algo.tdidt.ClusNode;
/*    */ import clus.data.rows.DataTuple;
/*    */ import clus.data.type.ClusSchema;
/*    */ import clus.model.ClusModel;
/*    */ import clus.model.processor.ClusModelProcessor;
/*    */ import java.io.IOException;
/*    */ import jeans.util.MyArray;
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
/*    */ public class BasicExampleCollector
/*    */   extends ClusModelProcessor
/*    */ {
/*    */   public boolean needsModelUpdate() {
/* 38 */     return true;
/*    */   }
/*    */   
/*    */   public void initialize(ClusModel model, ClusSchema schema) {
/* 42 */     ClusNode root = (ClusNode)model;
/* 43 */     recursiveInitialize(root);
/*    */   }
/*    */ 
/*    */   
/*    */   public void terminate(ClusModel model) throws IOException {}
/*    */   
/*    */   public void modelUpdate(DataTuple tuple, ClusModel model) {
/* 50 */     ClusNode node = (ClusNode)model;
/* 51 */     MyArray visitor = (MyArray)node.getVisitor();
/* 52 */     visitor.addElement(tuple);
/*    */   }
/*    */   
/*    */   private void recursiveInitialize(ClusNode node) {
/* 56 */     if (node.atBottomLevel()) {
/* 57 */       node.setVisitor(new MyArray());
/*    */     } else {
/* 59 */       for (int i = 0; i < node.getNbChildren(); i++) {
/* 60 */         ClusNode child = (ClusNode)node.getChild(i);
/* 61 */         recursiveInitialize(child);
/*    */       } 
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\algo\tdidt\processor\BasicExampleCollector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */