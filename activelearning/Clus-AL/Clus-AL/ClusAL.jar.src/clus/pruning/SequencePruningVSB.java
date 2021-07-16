/*     */ package clus.pruning;
/*     */ 
/*     */ import clus.algo.tdidt.ClusNode;
/*     */ import clus.data.attweights.ClusAttributeWeights;
/*     */ import clus.data.rows.RowData;
/*     */ import clus.error.ClusError;
/*     */ import clus.error.ClusErrorList;
/*     */ import java.util.ArrayList;
/*     */ import jeans.io.MyFile;
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
/*     */ public class SequencePruningVSB
/*     */   extends PruneTree
/*     */ {
/*     */   protected RowData m_VSB;
/*     */   protected ClusErrorList m_Error;
/*     */   protected ClusAttributeWeights m_Weights;
/*     */   protected PruneTree m_SeqPruner;
/*     */   protected boolean m_1SERule;
/*     */   protected boolean m_HasMissing;
/*     */   protected String m_Output;
/*     */   
/*     */   public SequencePruningVSB(RowData data, ClusAttributeWeights weights) {
/*  45 */     this.m_VSB = data;
/*  46 */     this.m_Weights = weights;
/*  47 */     this.m_HasMissing = true;
/*     */   }
/*     */   
/*     */   public int getNbResults() {
/*  51 */     return 1;
/*     */   }
/*     */   
/*     */   public void setSequencePruner(PruneTree pruner) {
/*  55 */     this.m_SeqPruner = pruner;
/*  56 */     this.m_Error = pruner.createErrorMeasure(this.m_VSB, this.m_Weights);
/*     */   }
/*     */   
/*     */   public PruneTree getSequencePruner() {
/*  60 */     return this.m_SeqPruner;
/*     */   }
/*     */   
/*     */   public static void resize(ArrayList list, int size) {
/*  64 */     if (size > list.size()) {
/*  65 */       list.ensureCapacity(size);
/*  66 */       int nb_add = size - list.size();
/*  67 */       for (int i = 0; i < nb_add; i++) {
/*  68 */         list.add(null);
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   public void setOutputFile(String output) {
/*  74 */     this.m_Output = output;
/*     */   }
/*     */   
/*     */   public void set1SERule(boolean enable) {
/*  78 */     this.m_1SERule = enable;
/*     */   }
/*     */   
/*     */   public void setHasMissing(boolean missing) {
/*  82 */     this.m_HasMissing = missing;
/*     */   }
/*     */   
/*     */   public void prune(ClusNode node) {
/*  86 */     PruneTree pruner = getSequencePruner();
/*  87 */     pruner.sequenceInitialize(node);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  96 */     ArrayList<ClusError> vsb_errors = new ArrayList();
/*  97 */     ArrayList<Double> train_errors = new ArrayList();
/*  98 */     ArrayList<Integer> sizes = new ArrayList();
/*     */     
/* 100 */     int max_idx = -1;
/* 101 */     boolean done = false;
/* 102 */     pruner.sequenceReset();
/* 103 */     while (!done) {
/* 104 */       ClusNode pruned = pruner.sequenceNext();
/* 105 */       if (pruned == null) {
/* 106 */         done = true; continue;
/*     */       } 
/* 108 */       max_idx++;
/* 109 */       resize(vsb_errors, max_idx + 1); resize(train_errors, max_idx + 1); resize(sizes, max_idx + 1);
/* 110 */       ClusError vsb_err = TreeErrorComputer.computeClusteringErrorStandard(pruned, this.m_VSB, this.m_Error);
/* 111 */       vsb_errors.set(max_idx, vsb_err);
/* 112 */       train_errors.set(max_idx, new Double(pruned.estimateClusteringSS(this.m_Weights)));
/* 113 */       sizes.set(max_idx, new Integer(pruned.getNbNodes()));
/*     */     } 
/*     */ 
/*     */     
/* 117 */     int best_idx = 0;
/* 118 */     double min_error = Double.POSITIVE_INFINITY;
/* 119 */     for (int idx = 0; idx <= max_idx; idx++) {
/* 120 */       ClusError error = vsb_errors.get(idx);
/* 121 */       double value = error.getModelError();
/* 122 */       if (value <= min_error) {
/* 123 */         min_error = value;
/* 124 */         best_idx = idx;
/*     */       } 
/*     */     } 
/*     */     
/* 128 */     ClusError best_error = vsb_errors.get(best_idx);
/* 129 */     double se = best_error.getModelErrorStandardError();
/* 130 */     int se1_idx = 0;
/* 131 */     for (int i = 0; i <= max_idx; i++) {
/* 132 */       ClusError error = vsb_errors.get(i);
/* 133 */       double value = error.getModelError();
/* 134 */       if (value <= min_error + se) {
/* 135 */         se1_idx = i;
/*     */       }
/*     */     } 
/* 138 */     if (se1_idx == -1) se1_idx = best_idx; 
/* 139 */     ClusError leaf_error = vsb_errors.get(max_idx);
/*     */ 
/*     */ 
/*     */     
/* 143 */     if (this.m_Output != null) {
/* 144 */       MyFile log = new MyFile(this.m_Output);
/* 145 */       for (int j = 0; j <= max_idx; j++) {
/* 146 */         ClusError error = vsb_errors.get(j);
/* 147 */         double value = error.getModelError();
/* 148 */         log.log(sizes.get(j).toString() + "\t" + train_errors.get(j) + "\t" + String.valueOf(value));
/*     */       } 
/* 150 */       log.close();
/*     */     } 
/*     */     
/* 153 */     if (this.m_1SERule) {
/* 154 */       pruner.sequenceToElemK(node, se1_idx);
/*     */     } else {
/* 156 */       pruner.sequenceToElemK(node, best_idx);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\pruning\SequencePruningVSB.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */