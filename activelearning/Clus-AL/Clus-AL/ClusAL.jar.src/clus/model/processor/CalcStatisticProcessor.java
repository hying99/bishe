/*    */ package clus.model.processor;
/*    */ 
/*    */ import clus.algo.tdidt.ClusNode;
/*    */ import clus.data.rows.DataTuple;
/*    */ import clus.data.type.ClusSchema;
/*    */ import clus.model.ClusModel;
/*    */ import clus.statistic.ClusStatistic;
/*    */ import java.io.IOException;
/*    */ import jeans.tree.CompleteTreeIterator;
/*    */ import jeans.tree.Node;
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
/*    */ public class CalcStatisticProcessor
/*    */   extends ClusModelProcessor
/*    */ {
/*    */   ClusStatistic m_Clone;
/*    */   
/*    */   public CalcStatisticProcessor(ClusStatistic clone) {
/* 40 */     this.m_Clone = clone;
/*    */   }
/*    */   
/*    */   public boolean needsModelUpdate() {
/* 44 */     return true;
/*    */   }
/*    */   
/*    */   public boolean needsInternalNodes() {
/* 48 */     return true;
/*    */   }
/*    */   
/*    */   public void initialize(ClusModel model, ClusSchema schema) {
/* 52 */     CompleteTreeIterator iter = new CompleteTreeIterator((Node)model);
/* 53 */     while (iter.hasMoreNodes()) {
/* 54 */       ClusNode node = (ClusNode)iter.getNextNode();
/* 55 */       ClusStatistic stat = this.m_Clone.cloneStat();
/* 56 */       node.setClusteringStat(stat);
/* 57 */       stat.setSDataSize(1);
/*    */     } 
/*    */   }
/*    */   
/*    */   public void terminate(ClusModel model) throws IOException {
/* 62 */     CompleteTreeIterator iter = new CompleteTreeIterator((Node)model);
/* 63 */     while (iter.hasMoreNodes()) {
/* 64 */       ClusNode node = (ClusNode)iter.getNextNode();
/* 65 */       node.getClusteringStat().calcMean();
/*    */     } 
/*    */   }
/*    */   
/*    */   public void modelUpdate(DataTuple tuple, ClusModel model) {
/* 70 */     ClusNode node = (ClusNode)model;
/* 71 */     node.getClusteringStat().updateWeighted(tuple, 0);
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\model\processor\CalcStatisticProcessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */