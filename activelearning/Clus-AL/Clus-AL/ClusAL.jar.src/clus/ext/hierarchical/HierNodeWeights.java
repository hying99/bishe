/*     */ package clus.ext.hierarchical;
/*     */ 
/*     */ import java.util.ArrayList;
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
/*     */ public class HierNodeWeights
/*     */ {
/*     */   double[] m_Weights;
/*     */   String m_Name;
/*     */   
/*     */   public final double getWeight(int nodeidx) {
/*  33 */     return this.m_Weights[nodeidx];
/*     */   }
/*     */   
/*     */   public final double[] getWeights() {
/*  37 */     return this.m_Weights;
/*     */   }
/*     */   
/*     */   public final String getName() {
/*  41 */     return this.m_Name;
/*     */   }
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
/*     */   public boolean allParentsOk(ClassTerm term, boolean[] computed) {
/*  55 */     for (int j = 0; j < term.getNbParents(); j++) {
/*  56 */       ClassTerm parent = term.getParent(j);
/*  57 */       if (parent.getIndex() != -1 && !computed[parent.getIndex()]) return false; 
/*     */     } 
/*  59 */     return true;
/*     */   }
/*     */   
/*     */   public void initExponentialDepthWeightsDAG(ClassHierarchy hier, int wtype, double w0) {
/*  63 */     boolean[] weight_computed = new boolean[hier.getTotal()];
/*  64 */     ArrayList<ClassTerm> todo = new ArrayList();
/*  65 */     for (int i = 0; i < hier.getTotal(); i++) {
/*  66 */       ClassTerm term = hier.getTermAt(i);
/*  67 */       todo.add(term);
/*     */     } 
/*  69 */     int nb_done = 0;
/*  70 */     while (nb_done < hier.getTotal()) {
/*  71 */       for (int j = todo.size() - 1; j >= 0; j--) {
/*  72 */         ClassTerm term = todo.get(j);
/*  73 */         if (allParentsOk(term, weight_computed)) {
/*  74 */           double agg_wi; int maxDepth = 0;
/*  75 */           int minDepth = Integer.MAX_VALUE;
/*  76 */           for (int k = 0; k < term.getNbParents(); k++) {
/*  77 */             ClassTerm parent = term.getParent(k);
/*  78 */             maxDepth = Math.max(maxDepth, parent.getMaxDepth() + 1);
/*  79 */             minDepth = Math.min(minDepth, parent.getMinDepth() + 1);
/*     */           } 
/*  81 */           term.setMinDepth(minDepth);
/*  82 */           term.setMaxDepth(maxDepth);
/*     */           
/*  84 */           if (wtype == 2) {
/*  85 */             agg_wi = Double.MAX_VALUE;
/*  86 */             for (int m = 0; m < term.getNbParents(); m++) {
/*  87 */               ClassTerm parent = term.getParent(m);
/*  88 */               if (parent.getIndex() == -1) { agg_wi = Math.min(agg_wi, 1.0D); }
/*  89 */               else { agg_wi = Math.min(agg_wi, this.m_Weights[parent.getIndex()]); }
/*     */ 
/*     */             
/*     */             } 
/*  93 */           } else if (wtype == 3) {
/*  94 */             agg_wi = Double.MIN_VALUE;
/*  95 */             for (int m = 0; m < term.getNbParents(); m++) {
/*  96 */               ClassTerm parent = term.getParent(m);
/*  97 */               if (parent.getIndex() == -1) { agg_wi = Math.max(agg_wi, 1.0D); }
/*  98 */               else { agg_wi = Math.max(agg_wi, this.m_Weights[parent.getIndex()]); }
/*     */             
/*     */             } 
/*     */           } else {
/* 102 */             agg_wi = 0.0D;
/* 103 */             for (int m = 0; m < term.getNbParents(); m++) {
/* 104 */               ClassTerm parent = term.getParent(m);
/* 105 */               if (parent.getIndex() == -1) { agg_wi++; }
/* 106 */               else { agg_wi += this.m_Weights[parent.getIndex()]; }
/*     */             
/* 108 */             }  if (wtype == 1) {
/* 109 */               agg_wi /= term.getNbParents();
/*     */             }
/*     */           } 
/*     */           
/* 113 */           this.m_Weights[term.getIndex()] = w0 * agg_wi;
/* 114 */           weight_computed[term.getIndex()] = true;
/* 115 */           todo.remove(j);
/* 116 */           nb_done++;
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public void initExponentialDepthWeightsRec(ClassTerm node, int depth, double w0) {
/* 123 */     for (int i = 0; i < node.getNbChildren(); i++) {
/* 124 */       ClassTerm child = (ClassTerm)node.getChild(i);
/* 125 */       child.setMinDepth(depth);
/* 126 */       child.setMaxDepth(depth);
/* 127 */       this.m_Weights[child.getIndex()] = calcExponentialDepthWeight(depth, w0);
/* 128 */       initExponentialDepthWeightsRec(child, depth + 1, w0);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void initNoWeights(ClassHierarchy hier) {
/* 134 */     boolean[] weight_computed = new boolean[hier.getTotal()];
/* 135 */     ArrayList<ClassTerm> todo = new ArrayList();
/* 136 */     for (int i = 0; i < hier.getTotal(); i++) {
/* 137 */       ClassTerm term = hier.getTermAt(i);
/* 138 */       todo.add(term);
/*     */     } 
/* 140 */     int nb_done = 0;
/* 141 */     while (nb_done < hier.getTotal()) {
/* 142 */       for (int j = todo.size() - 1; j >= 0; j--) {
/* 143 */         ClassTerm term = todo.get(j);
/* 144 */         if (allParentsOk(term, weight_computed)) {
/* 145 */           int maxDepth = 0;
/* 146 */           int minDepth = Integer.MAX_VALUE;
/* 147 */           for (int k = 0; k < term.getNbParents(); k++) {
/* 148 */             ClassTerm parent = term.getParent(k);
/* 149 */             maxDepth = Math.max(maxDepth, parent.getMaxDepth() + 1);
/* 150 */             minDepth = Math.min(minDepth, parent.getMinDepth() + 1);
/*     */           } 
/* 152 */           term.setMinDepth(minDepth);
/* 153 */           term.setMaxDepth(maxDepth);
/* 154 */           this.m_Weights[term.getIndex()] = 1.0D;
/* 155 */           weight_computed[term.getIndex()] = true;
/* 156 */           todo.remove(j);
/* 157 */           nb_done++;
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void initExponentialDepthWeights(ClassHierarchy hier, int wtype, double w0) {
/* 166 */     this.m_Weights = new double[hier.getTotal()];
/* 167 */     if (wtype == 4) {
/* 168 */       initNoWeights(hier);
/*     */     } else {
/*     */       
/* 171 */       ClassTerm root = hier.getRoot();
/* 172 */       if (hier.isTree()) {
/* 173 */         initExponentialDepthWeightsRec(root, 0, w0);
/* 174 */         this.m_Name = "Exponential depth weights (tree) " + w0;
/*     */       } else {
/* 176 */         root.setMinDepth(-1);
/* 177 */         root.setMaxDepth(-1);
/* 178 */         initExponentialDepthWeightsDAG(hier, wtype, w0);
/* 179 */         this.m_Name = "Exponential depth weights (DAG) " + w0;
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private static final double calcExponentialDepthWeight(int depth, double w0) {
/* 185 */     return Math.pow(w0, depth);
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\ext\hierarchical\HierNodeWeights.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */