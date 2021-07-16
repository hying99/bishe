/*     */ package clus.pruning;
/*     */ 
/*     */ import clus.algo.tdidt.ClusNode;
/*     */ import clus.data.attweights.ClusAttributeWeights;
/*     */ import clus.data.rows.RowData;
/*     */ import clus.error.ClusError;
/*     */ import clus.error.ClusErrorList;
/*     */ import clus.model.test.NodeTest;
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
/*     */ public class SizeConstraintPruning
/*     */   extends PruneTree
/*     */ {
/*     */   public RowData m_Data;
/*     */   public double[] m_MaxError;
/*     */   public ClusErrorList m_ErrorMeasure;
/*     */   public ClusErrorList m_AdditiveError;
/*     */   public int[] m_MaxSize;
/*     */   public ClusAttributeWeights m_TargetWeights;
/*     */   public int m_CrIndex;
/*     */   public int m_MaxIndex;
/*     */   
/*     */   public SizeConstraintPruning(int maxsize, ClusAttributeWeights prod) {
/*  43 */     this.m_MaxSize = new int[1];
/*  44 */     this.m_MaxSize[0] = maxsize;
/*  45 */     this.m_TargetWeights = prod;
/*     */   }
/*     */   
/*     */   public SizeConstraintPruning(int[] maxsize, ClusAttributeWeights prod) {
/*  49 */     this.m_MaxSize = maxsize;
/*  50 */     this.m_TargetWeights = prod;
/*     */   }
/*     */   
/*     */   public int getMaxSize() {
/*  54 */     return this.m_MaxSize[0];
/*     */   }
/*     */   
/*     */   public void setTrainingData(RowData data) {
/*  58 */     this.m_Data = data;
/*     */   }
/*     */   
/*     */   public ClusAttributeWeights getTargetWeights() {
/*  62 */     return this.m_TargetWeights;
/*     */   }
/*     */   
/*     */   public void pruneInitialize(ClusNode node, int size) {
/*  66 */     recursiveInitialize(node, size);
/*     */     
/*  68 */     if (isUsingAdditiveError()) {
/*  69 */       recursiveInitializeError(node, this.m_Data);
/*     */     } else {
/*  71 */       recursiveInitializeErrorFromStatistic(node);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void pruneExecute(ClusNode node, int size) {
/*  76 */     computeCosts(node, size);
/*  77 */     pruneToSizeK(node, size);
/*     */   }
/*     */   
/*     */   public void prune(ClusNode node) throws ClusException {
/*  81 */     prune(0, node);
/*     */   }
/*     */   
/*     */   public int getNbResults() {
/*  85 */     return Math.max(1, this.m_MaxSize.length);
/*     */   }
/*     */   
/*     */   public String getPrunedName(int i) {
/*  89 */     return "S(" + this.m_MaxSize[i] + ")";
/*     */   }
/*     */   
/*     */   public void prune(int result, ClusNode node) throws ClusException {
/*  93 */     if (this.m_MaxError == null) {
/*  94 */       int size = this.m_MaxSize[result];
/*  95 */       int orig = node.getNbNodes();
/*  96 */       System.out.println("Pruning to size (" + orig + "): " + size);
/*  97 */       pruneInitialize(node, size);
/*  98 */       pruneExecute(node, size);
/*     */     }
/* 100 */     else if (this.m_MaxSize.length == 0) {
/* 101 */       pruneMaxError(node, node.getNbNodes());
/*     */     } else {
/* 103 */       pruneMaxError(node, this.m_MaxSize[result]);
/*     */     } 
/*     */     
/* 106 */     node.clearVisitors();
/*     */   }
/*     */   
/*     */   public void sequenceInitialize(ClusNode node) {
/* 110 */     int max_size = node.getNbNodes();
/* 111 */     int abs_max = getMaxSize();
/* 112 */     if (abs_max != -1 && max_size > abs_max) max_size = abs_max; 
/* 113 */     if (max_size % 2 == 0) max_size--; 
/* 114 */     this.m_MaxIndex = max_size;
/* 115 */     this.m_CrIndex = this.m_MaxIndex;
/* 116 */     recursiveInitialize(node, max_size);
/* 117 */     setOriginalTree(node);
/*     */   }
/*     */   
/*     */   public void sequenceReset() {
/* 121 */     this.m_CrIndex = this.m_MaxIndex;
/*     */   }
/*     */   
/*     */   public ClusNode sequenceNext() {
/* 125 */     if (this.m_CrIndex > 0) {
/* 126 */       ClusNode cloned = getOriginalTree().cloneTreeWithVisitors();
/* 127 */       pruneExecute(cloned, this.m_CrIndex);
/* 128 */       this.m_CrIndex -= 2;
/* 129 */       return cloned;
/*     */     } 
/* 131 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public void sequenceToElemK(ClusNode node, int k) {
/* 136 */     pruneExecute(node, this.m_MaxIndex - 2 * k);
/*     */   }
/*     */   
/*     */   public void pruneMaxError(ClusNode node, int maxsize) throws ClusException {
/* 140 */     pruneInitialize(node, maxsize);
/* 141 */     int constr_ok_size = maxsize;
/* 142 */     for (int crsize = 1; crsize <= maxsize; crsize += 2) {
/* 143 */       ClusNode copy = node.cloneTreeWithVisitors();
/* 144 */       pruneExecute(copy, crsize);
/* 145 */       ClusErrorList cr_err = this.m_ErrorMeasure.getErrorClone();
/* 146 */       ClusError err = cr_err.getFirstError();
/*     */       
/* 148 */       TreeErrorComputer.computeErrorStandard(copy, this.m_Data, err);
/* 149 */       cr_err.setNbExamples(this.m_Data.getNbRows());
/* 150 */       if (this.m_MaxError.length == 1) {
/* 151 */         double max_err = this.m_MaxError[0];
/*     */         
/* 153 */         if (err.getModelError() <= max_err) {
/* 154 */           constr_ok_size = crsize;
/*     */           break;
/*     */         } 
/*     */       } else {
/* 158 */         boolean isOK = true;
/* 159 */         for (int i = 0; i < this.m_MaxError.length; i++) {
/* 160 */           double err_i = this.m_MaxError[i];
/* 161 */           if (!Double.isNaN(err_i) && 
/* 162 */             err.getModelErrorComponent(i) > err_i) {
/* 163 */             isOK = false;
/*     */           }
/*     */         } 
/*     */         
/* 167 */         if (isOK) {
/* 168 */           constr_ok_size = crsize;
/*     */           break;
/*     */         } 
/*     */       } 
/*     */     } 
/* 173 */     pruneExecute(node, constr_ok_size);
/*     */   }
/*     */ 
/*     */   
/*     */   private static void recursiveInitialize(ClusNode node, int size) {
/* 178 */     SizeConstraintVisitor visitor = new SizeConstraintVisitor(size);
/* 179 */     node.setVisitor(visitor);
/*     */     
/* 181 */     for (int i = 0; i < node.getNbChildren(); i++) {
/* 182 */       ClusNode child = (ClusNode)node.getChild(i);
/* 183 */       recursiveInitialize(child, size);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void recursiveInitializeError(ClusNode node, RowData data) {
/* 189 */     SizeConstraintVisitor visitor = (SizeConstraintVisitor)node.getVisitor();
/* 190 */     ClusErrorList parent = getAdditiveError();
/* 191 */     ClusError err = parent.getFirstError();
/*     */     
/* 193 */     parent.reset();
/* 194 */     TreeErrorComputer.computeErrorNode(node, data, err);
/* 195 */     parent.setNbExamples(data.getNbRows());
/* 196 */     visitor.error = err.getModelErrorAdditive();
/*     */     
/* 198 */     NodeTest tst = node.getTest();
/* 199 */     for (int i = 0; i < node.getNbChildren(); i++) {
/* 200 */       ClusNode child = (ClusNode)node.getChild(i);
/* 201 */       RowData subset = data.applyWeighted(tst, i);
/* 202 */       recursiveInitializeError(child, subset);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void recursiveInitializeErrorFromStatistic(ClusNode node) {
/* 208 */     SizeConstraintVisitor visitor = (SizeConstraintVisitor)node.getVisitor();
/* 209 */     visitor.error = node.getTargetStat().getError(this.m_TargetWeights);
/*     */     
/* 211 */     for (int i = 0; i < node.getNbChildren(); i++) {
/* 212 */       ClusNode child = (ClusNode)node.getChild(i);
/* 213 */       recursiveInitializeErrorFromStatistic(child);
/*     */     } 
/*     */   }
/*     */   
/*     */   public double computeNodeCost(ClusNode node) {
/* 218 */     SizeConstraintVisitor visitor = (SizeConstraintVisitor)node.getVisitor();
/* 219 */     return visitor.error;
/*     */   }
/*     */   
/*     */   public double computeCosts(ClusNode node, int l) {
/* 223 */     SizeConstraintVisitor visitor = (SizeConstraintVisitor)node.getVisitor();
/* 224 */     if (visitor.computed[l]) return visitor.cost[l]; 
/* 225 */     if (l < 3 || node.atBottomLevel()) {
/* 226 */       visitor.cost[l] = computeNodeCost(node);
/*     */     } else {
/* 228 */       visitor.cost[l] = computeNodeCost(node);
/* 229 */       ClusNode ch1 = (ClusNode)node.getChild(0);
/* 230 */       ClusNode ch2 = (ClusNode)node.getChild(1);
/* 231 */       for (int k1 = 1; k1 <= l - 2; k1++) {
/* 232 */         int k2 = l - k1 - 1;
/*     */         
/* 234 */         double cost = computeCosts(ch1, k1) + computeCosts(ch2, k2);
/*     */         
/* 236 */         if (cost < visitor.cost[l]) {
/* 237 */           visitor.cost[l] = cost;
/* 238 */           visitor.left[l] = k1;
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/* 243 */     visitor.computed[l] = true;
/* 244 */     return visitor.cost[l];
/*     */   }
/*     */   
/*     */   public static void pruneToSizeK(ClusNode node, int l) {
/* 248 */     if (node.atBottomLevel())
/* 249 */       return;  SizeConstraintVisitor visitor = (SizeConstraintVisitor)node.getVisitor();
/* 250 */     if (l < 3 || visitor.left[l] == 0) {
/* 251 */       node.makeLeaf();
/*     */     } else {
/* 253 */       int k1 = visitor.left[l];
/* 254 */       int k2 = l - k1 - 1;
/* 255 */       pruneToSizeK((ClusNode)node.getChild(0), k1);
/* 256 */       pruneToSizeK((ClusNode)node.getChild(1), k2);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void setMaxError(double[] max_err) {
/* 261 */     this.m_MaxError = max_err;
/*     */   }
/*     */   
/*     */   public void setErrorMeasure(ClusErrorList parent) {
/* 265 */     this.m_ErrorMeasure = parent;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void setAdditiveError(ClusErrorList parent) {
/* 271 */     this.m_AdditiveError = parent;
/*     */   }
/*     */   
/*     */   public ClusErrorList getAdditiveError() {
/* 275 */     return this.m_AdditiveError;
/*     */   }
/*     */   
/*     */   public boolean isUsingAdditiveError() {
/* 279 */     return (this.m_AdditiveError != null);
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\pruning\SizeConstraintPruning.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */