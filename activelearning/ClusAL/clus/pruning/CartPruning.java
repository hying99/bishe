/*     */ package clus.pruning;
/*     */ 
/*     */ import clus.algo.tdidt.ClusNode;
/*     */ import clus.data.attweights.ClusAttributeWeights;
/*     */ import clus.data.rows.RowData;
/*     */ import clus.data.type.ClusSchema;
/*     */ import clus.data.type.NominalAttrType;
/*     */ import clus.data.type.NumericAttrType;
/*     */ import clus.error.ClusError;
/*     */ import clus.error.ClusErrorList;
/*     */ import clus.error.ClusSumError;
/*     */ import clus.error.MSError;
/*     */ import clus.error.MSNominalError;
/*     */ import clus.error.MisclassificationError;
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
/*     */ public class CartPruning
/*     */   extends PruneTree
/*     */ {
/*     */   protected int[] m_MaxSize;
/*     */   protected ClusAttributeWeights m_Weights;
/*     */   protected double m_U1;
/*     */   protected double m_U2;
/*     */   protected boolean m_IsMSENominal;
/*     */   protected ClusError m_ErrorMeasure;
/*     */   
/*     */   public CartPruning(ClusAttributeWeights weights, boolean isMSENominal) {
/*  49 */     this.m_Weights = weights;
/*  50 */     this.m_IsMSENominal = isMSENominal;
/*     */   }
/*     */   
/*     */   public CartPruning(int[] maxsize, ClusAttributeWeights weights) {
/*  54 */     this.m_Weights = weights;
/*  55 */     this.m_MaxSize = maxsize;
/*     */   }
/*     */   
/*     */   public int getNbResults() {
/*  59 */     return Math.max(1, this.m_MaxSize.length);
/*     */   }
/*     */   
/*     */   public void prune(int result, ClusNode node) throws ClusException {
/*  63 */     int size = this.m_MaxSize[result];
/*  64 */     TreeErrorComputer.recursiveInitialize(node, new CartVisitor());
/*  65 */     internalInitialize(node);
/*  66 */     while (node.getNbNodes() > size) {
/*  67 */       internalSequenceNext(node);
/*     */     }
/*     */   }
/*     */   
/*     */   public ClusErrorList createErrorMeasure(RowData data, ClusAttributeWeights weights) {
/*  72 */     ClusSchema schema = data.getSchema();
/*  73 */     ClusErrorList parent = new ClusErrorList();
/*  74 */     NumericAttrType[] num = schema.getNumericAttrUse(2);
/*  75 */     NominalAttrType[] nom = schema.getNominalAttrUse(2);
/*  76 */     if (nom.length != 0 && num.length != 0) {
/*  77 */       MSError numErr = new MSError(parent, num, weights);
/*  78 */       MSNominalError nomErr = new MSNominalError(parent, nom, weights);
/*  79 */       ClusSumError error = new ClusSumError(parent);
/*  80 */       error.addComponent((ClusError)numErr);
/*  81 */       error.addComponent((ClusError)nomErr);
/*  82 */       parent.addError(this.m_ErrorMeasure = (ClusError)error);
/*     */     } else {
/*  84 */       if (nom.length != 0)
/*  85 */         if (this.m_IsMSENominal) { parent.addError(this.m_ErrorMeasure = (ClusError)new MSNominalError(parent, nom, weights)); }
/*  86 */         else { parent.addError(this.m_ErrorMeasure = (ClusError)new MisclassificationError(parent, nom)); }
/*     */          
/*  88 */       if (num.length != 0) {
/*  89 */         parent.addError(this.m_ErrorMeasure = (ClusError)new MSError(parent, num, weights));
/*     */       }
/*     */     } 
/*  92 */     parent.setWeights(weights);
/*  93 */     return parent;
/*     */   }
/*     */   
/*     */   public void sequenceInitialize(ClusNode node) {
/*  97 */     TreeErrorComputer.recursiveInitialize(node, new CartVisitor());
/*  98 */     setOriginalTree(node);
/*     */   }
/*     */   
/*     */   public void sequenceReset() {
/* 102 */     setCurrentTree(null);
/*     */   }
/*     */   
/*     */   public ClusNode sequenceNext() {
/* 106 */     ClusNode result = getCurrentTree();
/* 107 */     if (result == null) {
/* 108 */       result = getOriginalTree().cloneTreeWithVisitors();
/* 109 */       internalInitialize(result);
/*     */     } else {
/* 111 */       if (result.atBottomLevel()) {
/* 112 */         return null;
/*     */       }
/* 114 */       internalSequenceNext(result);
/*     */     } 
/*     */     
/* 117 */     setCurrentTree(result);
/* 118 */     return result;
/*     */   }
/*     */   
/*     */   public void sequenceToElemK(ClusNode node, int k) {
/* 122 */     internalInitialize(node);
/* 123 */     for (int i = 0; i < k; i++) {
/* 124 */       internalSequenceNext(node);
/*     */     }
/*     */   }
/*     */   
/*     */   public void initU(ClusNode node) {
/* 129 */     CartVisitor cart = (CartVisitor)node.getVisitor();
/* 130 */     this.m_U1 = 1.0D + cart.delta_u1;
/* 131 */     this.m_U2 = this.m_ErrorMeasure.computeLeafError(node.getClusteringStat()) + cart.delta_u2;
/*     */   }
/*     */ 
/*     */   
/*     */   public static final double getLambda(ClusNode node) {
/* 136 */     CartVisitor cart = (CartVisitor)node.getVisitor();
/* 137 */     return cart.lambda;
/*     */   }
/*     */   
/*     */   public static final double getLambdaMin(ClusNode node) {
/* 141 */     CartVisitor cart = (CartVisitor)node.getVisitor();
/* 142 */     return cart.lambda_min;
/*     */   }
/*     */   
/*     */   public static final void updateLambdaMin(ClusNode node) {
/* 146 */     CartVisitor cart = (CartVisitor)node.getVisitor();
/* 147 */     cart.lambda_min = cart.lambda;
/* 148 */     for (int i = 0; i < node.getNbChildren(); i++) {
/* 149 */       ClusNode ch = (ClusNode)node.getChild(i);
/* 150 */       cart.lambda_min = Math.min(cart.lambda_min, getLambdaMin(ch));
/*     */     } 
/*     */   }
/*     */   
/*     */   public static final void updateLambda(ClusNode node) {
/* 155 */     CartVisitor cart = (CartVisitor)node.getVisitor();
/* 156 */     cart.lambda = -cart.delta_u2 / cart.delta_u1;
/*     */   }
/*     */   
/*     */   public static final void subtractDeltaU(ClusNode node, double d_u1, double d_u2) {
/* 160 */     CartVisitor cart = (CartVisitor)node.getVisitor();
/* 161 */     cart.delta_u1 -= d_u1;
/* 162 */     cart.delta_u2 -= d_u2;
/*     */   }
/*     */   
/*     */   public void internalSequenceNext(ClusNode node) {
/* 166 */     ClusNode cr_node_t = node;
/* 167 */     double lambda_min_t0 = getLambdaMin(node);
/*     */     
/* 169 */     while (getLambda(cr_node_t) > lambda_min_t0) {
/* 170 */       ClusNode ch1 = (ClusNode)cr_node_t.getChild(0);
/* 171 */       ClusNode ch2 = (ClusNode)cr_node_t.getChild(1);
/* 172 */       if (getLambdaMin(ch1) == lambda_min_t0) {
/* 173 */         cr_node_t = ch1; continue;
/*     */       } 
/* 175 */       cr_node_t = ch2;
/*     */     } 
/*     */ 
/*     */     
/* 179 */     cr_node_t.makeLeaf();
/* 180 */     CartVisitor cart_t = (CartVisitor)cr_node_t.getVisitor();
/* 181 */     double delta_u1 = cart_t.delta_u1;
/* 182 */     double delta_u2 = cart_t.delta_u2;
/* 183 */     cart_t.lambda_min = Double.POSITIVE_INFINITY;
/*     */     
/* 185 */     while (!cr_node_t.atTopLevel()) {
/* 186 */       cr_node_t = (ClusNode)cr_node_t.getParent();
/* 187 */       subtractDeltaU(cr_node_t, delta_u1, delta_u2);
/* 188 */       updateLambda(cr_node_t);
/* 189 */       updateLambdaMin(cr_node_t);
/*     */     } 
/* 191 */     this.m_U1 -= delta_u1; this.m_U2 -= delta_u2;
/*     */   }
/*     */ 
/*     */   
/*     */   public void internalInitialize(ClusNode node) {
/* 196 */     internalRecursiveInitialize(node);
/* 197 */     initU(node);
/*     */   }
/*     */   
/*     */   public void internalRecursiveInitialize(ClusNode node) {
/* 201 */     int nb_c = node.getNbChildren();
/* 202 */     for (int i = 0; i < nb_c; i++) {
/* 203 */       internalRecursiveInitialize((ClusNode)node.getChild(i));
/*     */     }
/* 205 */     CartVisitor cart = (CartVisitor)node.getVisitor();
/* 206 */     if (nb_c == 0) {
/* 207 */       cart.delta_u1 = 0.0D;
/* 208 */       cart.delta_u2 = 0.0D;
/* 209 */       cart.lambda_min = Double.POSITIVE_INFINITY;
/*     */     } else {
/* 211 */       cart.delta_u1 = (node.getNbLeaves() - 1);
/* 212 */       double leaf_err = this.m_ErrorMeasure.computeLeafError(node.getClusteringStat());
/* 213 */       double tree_err = this.m_ErrorMeasure.computeTreeErrorClusteringAbsolute(node);
/* 214 */       cart.delta_u2 = tree_err - leaf_err;
/* 215 */       updateLambda(node);
/* 216 */       updateLambdaMin(node);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\pruning\CartPruning.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */