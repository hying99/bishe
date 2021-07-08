/*     */ package clus.pruning;
/*     */ 
/*     */ import clus.algo.tdidt.ClusNode;
/*     */ import clus.data.attweights.ClusAttributeWeights;
/*     */ import clus.data.rows.RowData;
/*     */ import clus.error.ClusErrorList;
/*     */ import clus.model.ClusModel;
/*     */ import clus.model.ClusModelInfo;
/*     */ import clus.util.ClusException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PruneTree
/*     */ {
/*     */   protected ClusNode m_CrTree;
/*     */   protected ClusNode m_OrigTree;
/*     */   
/*     */   public void prune(ClusNode node) throws ClusException {}
/*     */   
/*     */   public void setTrainingData(RowData data) {}
/*     */   
/*     */   public int getNbResults() {
/*  44 */     return 0;
/*     */   }
/*     */   
/*     */   public String getPrunedName(int i) {
/*  48 */     if (getNbResults() == 1) {
/*  49 */       return "Pruned";
/*     */     }
/*  51 */     return "P" + (i + 1);
/*     */   }
/*     */ 
/*     */   
/*     */   public void updatePrunedModelInfo(ClusModelInfo info) {}
/*     */ 
/*     */   
/*     */   public ClusModelInfo getPrunedModelInfo(int i, ClusNode orig) throws ClusException {
/*  59 */     ClusNode pruned = (ClusNode)orig.cloneTree();
/*  60 */     prune(i, pruned);
/*  61 */     pruned.numberTree();
/*  62 */     ClusModelInfo pruned_info = new ClusModelInfo(getPrunedName(i));
/*  63 */     pruned_info.setModel((ClusModel)pruned);
/*  64 */     updatePrunedModelInfo(pruned_info);
/*  65 */     return pruned_info;
/*     */   }
/*     */   
/*     */   public void prune(int result, ClusNode node) throws ClusException {
/*  69 */     prune(node);
/*     */   }
/*     */ 
/*     */   
/*     */   public void sequenceInitialize(ClusNode node) {}
/*     */ 
/*     */   
/*     */   public void sequenceReset() {}
/*     */ 
/*     */   
/*     */   public ClusNode sequenceNext() {
/*  80 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public void sequenceToElemK(ClusNode node, int k) {}
/*     */   
/*     */   public ClusErrorList createErrorMeasure(RowData data, ClusAttributeWeights weights) {
/*  87 */     return null;
/*     */   }
/*     */   
/*     */   public ClusNode getCurrentTree() {
/*  91 */     return this.m_CrTree;
/*     */   }
/*     */   
/*     */   public void setCurrentTree(ClusNode node) {
/*  95 */     this.m_CrTree = node;
/*     */   }
/*     */   
/*     */   public ClusNode getOriginalTree() {
/*  99 */     return this.m_OrigTree;
/*     */   }
/*     */   
/*     */   public void setOriginalTree(ClusNode node) {
/* 103 */     this.m_OrigTree = node;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\pruning\PruneTree.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */