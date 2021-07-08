/*    */ package clus.pruning;
/*    */ 
/*    */ import clus.algo.tdidt.ClusNode;
/*    */ import clus.data.rows.DataTuple;
/*    */ import clus.data.rows.RowData;
/*    */ import clus.error.ClusError;
/*    */ import clus.error.ClusErrorList;
/*    */ import clus.statistic.ClusStatistic;
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
/*    */ public class BottomUpPruningVSB
/*    */   extends PruneTree
/*    */ {
/*    */   protected ClusError m_TreeErr;
/*    */   protected ClusError m_NodeErr;
/*    */   protected RowData m_Data;
/*    */   
/*    */   public BottomUpPruningVSB(ClusErrorList parent, RowData data) {
/* 37 */     this.m_TreeErr = parent.getFirstError();
/* 38 */     this.m_NodeErr = this.m_TreeErr.getErrorClone(parent);
/* 39 */     this.m_Data = data;
/*    */   }
/*    */   
/*    */   public int getNbResults() {
/* 43 */     return 1;
/*    */   }
/*    */   
/*    */   public void prune(ClusNode node) {
/* 47 */     prune(node, this.m_Data);
/*    */   }
/*    */   
/*    */   public void prune(ClusNode node, RowData data) {
/* 51 */     if (!node.atBottomLevel()) {
/* 52 */       int arity = node.getNbChildren();
/* 53 */       for (int i = 0; i < arity; i++) {
/* 54 */         RowData subset = data.applyWeighted(node.getTest(), i);
/* 55 */         prune((ClusNode)node.getChild(i), subset);
/*    */       } 
/* 57 */       int nbrows = data.getNbRows();
/* 58 */       if (nbrows > 0) {
/* 59 */         this.m_TreeErr.reset();
/* 60 */         this.m_NodeErr.reset();
/* 61 */         this.m_TreeErr.getParent().setNbExamples(0);
/* 62 */         for (int j = 0; j < nbrows; j++) {
/* 63 */           DataTuple tuple = data.getTuple(j);
/* 64 */           ClusStatistic pred = node.predictWeighted(tuple);
/* 65 */           this.m_TreeErr.addExample(tuple, pred);
/* 66 */           this.m_NodeErr.addExample(tuple, node.predictWeightedLeaf(tuple));
/*    */         } 
/*    */ 
/*    */         
/* 70 */         this.m_TreeErr.getParent().setNbExamples(nbrows);
/* 71 */         double tree_err = this.m_TreeErr.getModelError();
/* 72 */         double node_err = this.m_NodeErr.getModelError();
/*    */ 
/*    */         
/* 75 */         if (this.m_TreeErr.shouldBeLow())
/* 76 */         { if (tree_err > node_err) node.makeLeaf();
/*    */            }
/* 78 */         else if (tree_err <= node_err) { node.makeLeaf(); }
/*    */       
/*    */       } else {
/* 81 */         node.makeLeaf();
/*    */       } 
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\pruning\BottomUpPruningVSB.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */