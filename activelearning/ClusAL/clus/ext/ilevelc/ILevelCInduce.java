/*     */ package clus.ext.ilevelc;
/*     */ 
/*     */ import clus.algo.tdidt.ClusNode;
/*     */ import clus.algo.tdidt.DepthFirstInduce;
/*     */ import clus.data.attweights.ClusAttributeWeights;
/*     */ import clus.data.attweights.ClusNormalizedAttributeWeights;
/*     */ import clus.data.rows.DataTuple;
/*     */ import clus.data.rows.RowData;
/*     */ import clus.data.rows.RowDataSortHelper;
/*     */ import clus.data.type.ClusAttrType;
/*     */ import clus.data.type.ClusSchema;
/*     */ import clus.data.type.NominalAttrType;
/*     */ import clus.data.type.NumericAttrType;
/*     */ import clus.main.ClusRun;
/*     */ import clus.main.Settings;
/*     */ import clus.model.ClusModel;
/*     */ import clus.model.test.NodeTest;
/*     */ import clus.model.test.NumericTest;
/*     */ import clus.util.ClusException;
/*     */ import clus.util.ClusRandom;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import jeans.tree.Node;
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
/*     */ public class ILevelCInduce
/*     */   extends DepthFirstInduce
/*     */ {
/*     */   protected NodeTest m_BestTest;
/*     */   protected ClusNode m_BestLeaf;
/*  41 */   protected RowDataSortHelper m_SortHelper = new RowDataSortHelper();
/*  42 */   protected double m_BestHeur = Double.POSITIVE_INFINITY;
/*  43 */   protected int m_NbClasses = 1;
/*  44 */   protected int m_MaxNbClasses = 2;
/*     */   protected double m_MinLeafWeight;
/*     */   protected int m_NbTrain;
/*     */   protected double m_GlobalSS;
/*     */   protected ArrayList m_Constraints;
/*     */   protected int[][] m_ConstraintsIndex;
/*     */   protected ClusNormalizedAttributeWeights m_Scale;
/*     */   
/*     */   public ILevelCInduce(ClusSchema schema, Settings sett) throws ClusException, IOException {
/*  53 */     super(schema, sett);
/*     */   }
/*     */ 
/*     */   
/*     */   public int[][] createConstraintsIndex() {
/*  58 */     ArrayList[] crIndex = new ArrayList[this.m_NbTrain];
/*  59 */     for (int i = 0; i < this.m_Constraints.size(); i++) {
/*  60 */       ILevelConstraint ic = this.m_Constraints.get(i);
/*  61 */       int t1 = ic.getT1().getIndex();
/*  62 */       int t2 = ic.getT2().getIndex();
/*  63 */       if (crIndex[t1] == null) crIndex[t1] = new ArrayList(); 
/*  64 */       if (crIndex[t2] == null) crIndex[t2] = new ArrayList(); 
/*  65 */       crIndex[t1].add(new Integer(i));
/*  66 */       crIndex[t2].add(new Integer(i));
/*     */     } 
/*     */     
/*  69 */     int[][] index = new int[this.m_NbTrain][];
/*  70 */     for (int j = 0; j < this.m_NbTrain; j++) {
/*  71 */       if (crIndex[j] != null) {
/*  72 */         int nb = crIndex[j].size();
/*  73 */         index[j] = new int[nb];
/*  74 */         for (int k = 0; k < nb; k++) {
/*  75 */           Integer value = crIndex[j].get(k);
/*  76 */           index[j][k] = value.intValue();
/*     */         } 
/*     */       } 
/*     */     } 
/*  80 */     return index;
/*     */   }
/*     */   
/*     */   public ILevelConstraint[] getSubsetConstraints(RowData data) {
/*  84 */     int count = 0;
/*  85 */     boolean[] constr = new boolean[this.m_Constraints.size()];
/*  86 */     for (int i = 0; i < data.getNbRows(); i++) {
/*  87 */       DataTuple tuple = data.getTuple(i);
/*  88 */       int[] index = this.m_ConstraintsIndex[tuple.getIndex()];
/*  89 */       if (index != null) {
/*  90 */         for (int k = 0; k < index.length; k++) {
/*  91 */           int cid = index[k];
/*  92 */           if (!constr[cid]) {
/*  93 */             constr[cid] = true;
/*  94 */             count++;
/*     */           } 
/*     */         } 
/*     */       }
/*     */     } 
/*  99 */     int pos = 0;
/* 100 */     ILevelConstraint[] result = new ILevelConstraint[count];
/* 101 */     for (int j = 0; j < this.m_Constraints.size(); j++) {
/* 102 */       if (constr[j]) result[pos++] = this.m_Constraints.get(j); 
/*     */     } 
/* 104 */     return result;
/*     */   }
/*     */   
/*     */   public int[] createIE(RowData data) {
/* 108 */     int[] ie = new int[this.m_NbTrain];
/* 109 */     for (int i = 0; i < data.getNbRows(); i++) {
/* 110 */       ie[data.getTuple(i).getIndex()] = 2;
/*     */     }
/* 112 */     return ie;
/*     */   }
/*     */   
/*     */   public double computeHeuristic(int violated, double ss) {
/* 116 */     double ss_norm = ss / this.m_GlobalSS;
/* 117 */     double violated_norm = 1.0D * violated / this.m_Constraints.size();
/* 118 */     double alpha = getSettings().getILevelCAlpha();
/* 119 */     double heur = (1.0D - alpha) * ss_norm + alpha * violated_norm;
/*     */     
/* 121 */     return heur;
/*     */   }
/*     */   
/*     */   public double computeHeuristic(ILevelCHeurStat pos, ILevelCHeurStat neg, ILevelCStatistic ps, boolean use_p_lab, double ss_offset, int nb_violated) {
/* 125 */     if (pos.getTotalWeight() < this.m_MinLeafWeight) return Double.POSITIVE_INFINITY; 
/* 126 */     if (neg.getTotalWeight() < this.m_MinLeafWeight) return Double.POSITIVE_INFINITY; 
/* 127 */     int pLabel = -1;
/* 128 */     int nbLabels = this.m_NbClasses;
/* 129 */     if (use_p_lab) {
/*     */       
/* 131 */       nbLabels = this.m_NbClasses - 1;
/* 132 */       pLabel = ps.getClusterID();
/*     */     } 
/* 134 */     int v1 = tryLabel(pos, neg, pLabel, nbLabels);
/* 135 */     int v2 = tryLabel(neg, pos, pLabel, nbLabels);
/* 136 */     if (v1 == -1)
/* 137 */     { if (v2 != -1) { nb_violated += v2; }
/* 138 */       else { return Double.POSITIVE_INFINITY; }  }
/* 139 */     else if (v2 == -1)
/* 140 */     { if (v1 != -1) { nb_violated += v1; }
/* 141 */       else { return Double.POSITIVE_INFINITY; }
/*     */        }
/* 143 */     else { nb_violated += Math.min(v1, v2); }
/*     */     
/* 145 */     double ss_pos = pos.getSVarS((ClusAttributeWeights)this.m_Scale);
/* 146 */     double ss_neg = neg.getSVarS((ClusAttributeWeights)this.m_Scale);
/* 147 */     double ss = ss_offset + ss_pos + ss_neg;
/* 148 */     return computeHeuristic(nb_violated, ss);
/*     */   }
/*     */   
/*     */   public void findNumericConstraints(NumericAttrType at, ClusNode leaf, boolean use_p_lab, double ss_offset, int violated_offset, int violated_leaf, int[] clusters) throws ClusException {
/* 152 */     RowData data = (RowData)leaf.getVisitor();
/* 153 */     ILevelCStatistic tot = (ILevelCStatistic)leaf.getClusteringStat();
/*     */     
/* 155 */     int idx = at.getArrayIndex();
/* 156 */     if (at.isSparse()) {
/* 157 */       data.sortSparse(at, this.m_SortHelper);
/*     */     } else {
/* 159 */       data.sort(at);
/*     */     } 
/* 161 */     int nb_rows = data.getNbRows();
/* 162 */     if (at.hasMissing()) {
/* 163 */       throw new ClusException("Does not support attributes with missing values: " + at.getName());
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 169 */     ILevelCStatistic cs = (ILevelCStatistic)leaf.getClusteringStat();
/* 170 */     ILevelCHeurStat pos = new ILevelCHeurStat(cs, this.m_NbClasses);
/* 171 */     ILevelCHeurStat neg = new ILevelCHeurStat(cs, this.m_NbClasses);
/*     */     
/* 173 */     int[] ie = createIE(data);
/*     */     
/* 175 */     pos.setIndices(this.m_ConstraintsIndex, this.m_Constraints, ie, clusters);
/* 176 */     neg.setIndices(this.m_ConstraintsIndex, this.m_Constraints, ie, clusters);
/*     */     
/* 178 */     for (int i = 0; i < nb_rows; i++) {
/* 179 */       DataTuple tuple = data.getTuple(i);
/* 180 */       neg.updateWeighted(tuple, tuple.getWeight());
/*     */     } 
/* 182 */     int nb_violated = violated_leaf;
/*     */     
/* 184 */     double prev = Double.NaN;
/* 185 */     for (int j = 0; j < nb_rows; j++) {
/* 186 */       DataTuple tuple = data.getTuple(j);
/* 187 */       double value = tuple.getDoubleVal(idx);
/* 188 */       if (value != prev && prev != Double.NaN) {
/*     */         
/* 190 */         double heuristic = computeHeuristic(pos, neg, tot, use_p_lab, ss_offset, violated_offset + nb_violated);
/* 191 */         if (heuristic < this.m_BestHeur) {
/* 192 */           this.m_BestHeur = heuristic;
/* 193 */           this.m_BestLeaf = leaf;
/* 194 */           double pos_freq = pos.getTotalWeight() / tot.getTotalWeight();
/* 195 */           double splitpoint = (value + prev) / 2.0D;
/* 196 */           this.m_BestTest = (NodeTest)new NumericTest((ClusAttrType)at, splitpoint, pos_freq);
/*     */         } 
/* 198 */         prev = value;
/*     */       } 
/* 200 */       pos.updateWeighted(tuple, tuple.getWeight());
/* 201 */       neg.removeWeighted(tuple, tuple.getWeight());
/* 202 */       int tidx = tuple.getIndex();
/* 203 */       int[] cidx = this.m_ConstraintsIndex[tidx];
/* 204 */       if (cidx != null)
/* 205 */         for (int k = 0; k < cidx.length; k++) {
/* 206 */           ILevelConstraint cons = this.m_Constraints.get(cidx[k]);
/* 207 */           int otidx = cons.getOtherTupleIdx(tuple);
/* 208 */           if (ie[otidx] != 0) {
/* 209 */             boolean was_violated = false;
/* 210 */             if (cons.getType() == 0)
/* 211 */             { if (ie[tidx] != ie[otidx]) was_violated = true;
/*     */                }
/* 213 */             else if (ie[tidx] == ie[otidx]) { was_violated = true; }
/*     */             
/* 215 */             if (was_violated) { nb_violated--; }
/* 216 */             else { nb_violated++; }
/*     */           
/*     */           } 
/*     */         }  
/* 220 */       ie[tidx] = 1;
/*     */     } 
/*     */   }
/*     */   
/*     */   public void tryGivenLeaf(ClusNode leaf, boolean use_p_lab, int violated, double ss, int[] clusters) throws ClusException {
/* 225 */     ILevelCStatistic stat = (ILevelCStatistic)leaf.getClusteringStat();
/* 226 */     if (stat.getTotalWeight() <= this.m_MinLeafWeight) {
/*     */       return;
/*     */     }
/*     */     
/* 230 */     RowData leaf_data = (RowData)leaf.getVisitor();
/* 231 */     double ss_leaf = stat.getSVarS((ClusAttributeWeights)this.m_Scale);
/* 232 */     double ss_offset = ss - ss_leaf;
/* 233 */     int[] v_info = countViolatedConstaints(leaf_data, clusters);
/* 234 */     int violated_offset = violated - v_info[0];
/* 235 */     System.out.println("Violated by leaf: " + v_info[0] + " internal ML: " + v_info[1] + " (of " + violated + " total)");
/* 236 */     ClusSchema schema = getSchema();
/* 237 */     ClusAttrType[] attrs = schema.getDescriptiveAttributes();
/* 238 */     for (int i = 0; i < attrs.length; i++) {
/* 239 */       ClusAttrType at = attrs[i];
/* 240 */       if (at instanceof NumericAttrType) { findNumericConstraints((NumericAttrType)at, leaf, use_p_lab, ss_offset, violated_offset, v_info[1], clusters); }
/* 241 */       else { throw new ClusException("Unsupported descriptive attribute type: " + at.getName()); }
/*     */     
/*     */     } 
/*     */   }
/*     */   public void tryEachLeaf(ClusNode tree, ClusNode root, int violated, double ss, int[] clusters) throws ClusException {
/* 246 */     int nb_c = tree.getNbChildren();
/* 247 */     if (nb_c == 0) {
/* 248 */       ILevelCStatistic ps = (ILevelCStatistic)tree.getClusteringStat();
/* 249 */       int nbParLabel = countLabel(root, ps.getClusterID());
/* 250 */       tryGivenLeaf(tree, (nbParLabel <= 1), violated, ss, clusters);
/*     */     } else {
/* 252 */       for (int i = 0; i < nb_c; i++) {
/* 253 */         ClusNode child = (ClusNode)tree.getChild(i);
/* 254 */         tryEachLeaf(child, root, violated, ss, clusters);
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public ILevelCHeurStat computeCHeurStat(ClusNode leaf, ClusNode par, int[] ie, int[] clusters) {
/* 260 */     RowData data = (RowData)leaf.getVisitor();
/* 261 */     ILevelCStatistic cs = (ILevelCStatistic)leaf.getClusteringStat();
/* 262 */     ILevelCHeurStat lstat = new ILevelCHeurStat(cs, this.m_NbClasses);
/* 263 */     lstat.setIndices(this.m_ConstraintsIndex, this.m_Constraints, ie, clusters);
/* 264 */     for (int i = 0; i < data.getNbRows(); i++) {
/* 265 */       DataTuple tuple = data.getTuple(i);
/* 266 */       lstat.updateWeighted(tuple, tuple.getWeight());
/*     */     } 
/* 268 */     return lstat;
/*     */   }
/*     */   
/*     */   public int tryLabel(ILevelCHeurStat a, ILevelCHeurStat b, int parlabel, int nb) {
/* 272 */     int v1 = a.computeMinimumExtViolated(parlabel, -1, (nb < this.m_MaxNbClasses));
/* 273 */     int label_a = a.getClusterID();
/* 274 */     if (label_a == -1) nb++; 
/* 275 */     int v2 = b.computeMinimumExtViolated(parlabel, label_a, (nb < this.m_MaxNbClasses));
/* 276 */     return (v1 == -1 || v2 == -1) ? -1 : (v1 + v2);
/*     */   }
/*     */   
/*     */   public void assignLabels(ILevelCHeurStat a, ILevelCHeurStat b, ClusNode root, int parlabel, int nb) throws ClusException {
/* 280 */     int v1 = a.computeMinimumExtViolated(parlabel, -1, (nb < this.m_MaxNbClasses));
/* 281 */     int label_a = a.getClusterID();
/* 282 */     if (label_a == -1) {
/* 283 */       nb++;
/* 284 */       label_a = freeLabel(root, -1);
/* 285 */       this.m_NbClasses = Math.max(label_a + 1, this.m_NbClasses);
/* 286 */       a.setClusterID(label_a);
/*     */     } 
/* 288 */     int v2 = b.computeMinimumExtViolated(parlabel, label_a, (nb < this.m_MaxNbClasses));
/* 289 */     if (b.getClusterID() == -1) {
/* 290 */       int label_b = freeLabel(root, label_a);
/* 291 */       this.m_NbClasses = Math.max(label_b + 1, this.m_NbClasses);
/* 292 */       b.setClusterID(label_b);
/*     */     } 
/* 294 */     if (v1 == -1 || v2 == -1) {
/* 295 */       throw new ClusException("Error: can't assign labels: v1 = " + v1 + " v2 = " + v2);
/*     */     }
/*     */   }
/*     */   
/*     */   public void storeLabels(ClusNode leaf, ILevelCHeurStat stat) {
/* 300 */     ILevelCStatistic cs = (ILevelCStatistic)leaf.getClusteringStat();
/* 301 */     ILevelCStatistic ts = (ILevelCStatistic)leaf.getTargetStat();
/* 302 */     cs.setClusterID(stat.getClusterID());
/* 303 */     ts.setClusterID(stat.getClusterID());
/*     */   }
/*     */   
/*     */   public void enterBestTest(ClusNode tree, ClusNode root, int[] clusters) throws ClusException {
/* 307 */     int nb_c = tree.getNbChildren();
/* 308 */     if (nb_c == 0) {
/* 309 */       if (tree == this.m_BestLeaf) {
/* 310 */         RowData data = (RowData)tree.getVisitor();
/* 311 */         tree.setTest(this.m_BestTest);
/* 312 */         int arity = tree.updateArity();
/* 313 */         NodeTest test = tree.getTest();
/* 314 */         for (int j = 0; j < arity; j++) {
/* 315 */           ClusNode child = new ClusNode();
/* 316 */           tree.setChild((Node)child, j);
/* 317 */           RowData subset = data.applyWeighted(test, j);
/* 318 */           child.initClusteringStat(getStatManager(), subset);
/* 319 */           child.initTargetStat(getStatManager(), subset);
/* 320 */           child.getTargetStat().calcMean();
/* 321 */           child.setVisitor(subset);
/*     */         } 
/* 323 */         int[] ie = createIE(data);
/* 324 */         ILevelCStatistic ps = (ILevelCStatistic)tree.getClusteringStat();
/* 325 */         ILevelCHeurStat left = computeCHeurStat((ClusNode)tree.getChild(0), tree, ie, clusters);
/* 326 */         ILevelCHeurStat right = computeCHeurStat((ClusNode)tree.getChild(1), tree, ie, clusters);
/* 327 */         int nbParLabel = countLabel(root, ps.getClusterID());
/* 328 */         int pLabel = -1;
/* 329 */         int nbLabels = this.m_NbClasses;
/* 330 */         if (nbParLabel <= 0) {
/*     */           
/* 332 */           nbLabels = this.m_NbClasses - 1;
/* 333 */           pLabel = ps.getClusterID();
/*     */         } 
/* 335 */         int v1 = tryLabel(left, right, pLabel, nbLabels);
/* 336 */         int v2 = tryLabel(right, left, pLabel, nbLabels);
/* 337 */         if (v1 == -1 || v2 == -1) {
/* 338 */           v1 = tryLabel(left, right, pLabel, nbLabels);
/* 339 */           v2 = tryLabel(right, left, pLabel, nbLabels);
/*     */         } 
/* 341 */         if ((v1 <= v2 && v1 != -1) || v2 == -1) {
/* 342 */           assignLabels(left, right, root, pLabel, nbLabels);
/*     */         } else {
/* 344 */           assignLabels(right, left, root, pLabel, nbLabels);
/*     */         } 
/* 346 */         storeLabels((ClusNode)tree.getChild(0), left);
/* 347 */         storeLabels((ClusNode)tree.getChild(1), right);
/* 348 */         tree.setVisitor(null);
/*     */       } 
/*     */     } else {
/* 351 */       for (int i = 0; i < nb_c; i++) {
/* 352 */         ClusNode child = (ClusNode)tree.getChild(i);
/* 353 */         enterBestTest(child, root, clusters);
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public int freeLabel(ClusNode tree, int ignore) {
/* 359 */     boolean[] set = new boolean[this.m_NbClasses];
/* 360 */     labelSet(tree, set);
/* 361 */     for (int i = 0; i < set.length; i++) {
/* 362 */       if (!set[i] && i != ignore) return i; 
/*     */     } 
/* 364 */     if (this.m_NbClasses < this.m_MaxNbClasses) return this.m_NbClasses; 
/* 365 */     return -1;
/*     */   }
/*     */   
/*     */   public void labelSet(ClusNode tree, boolean[] set) {
/* 369 */     int nb_c = tree.getNbChildren();
/* 370 */     if (nb_c == 0) {
/* 371 */       ILevelCStatistic cs = (ILevelCStatistic)tree.getClusteringStat();
/* 372 */       if (cs.getClusterID() != -1) set[cs.getClusterID()] = true; 
/*     */     } else {
/* 374 */       for (int i = 0; i < nb_c; i++) {
/* 375 */         ClusNode child = (ClusNode)tree.getChild(i);
/* 376 */         labelSet(child, set);
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public int countLabel(ClusNode tree, int label) {
/* 382 */     int nb_c = tree.getNbChildren();
/* 383 */     if (nb_c == 0) {
/* 384 */       ILevelCStatistic cs = (ILevelCStatistic)tree.getClusteringStat();
/* 385 */       return (cs.getClusterID() == label) ? 1 : 0;
/*     */     } 
/* 387 */     int count = 0;
/* 388 */     for (int i = 0; i < nb_c; i++) {
/* 389 */       ClusNode child = (ClusNode)tree.getChild(i);
/* 390 */       count += countLabel(child, label);
/*     */     } 
/* 392 */     return count;
/*     */   }
/*     */ 
/*     */   
/*     */   public void iLevelCInduce(ClusNode root) throws ClusException {
/* 397 */     double ss = root.estimateClusteringSS((ClusAttributeWeights)this.m_Scale);
/* 398 */     int[] clusters = assignAllInstances(root);
/* 399 */     int violated = countViolatedConstaints(clusters);
/* 400 */     computeHeuristic(violated, ss);
/* 401 */     this.m_BestHeur = Double.POSITIVE_INFINITY;
/*     */     
/*     */     while (true) {
/* 404 */       this.m_BestTest = null;
/* 405 */       this.m_BestLeaf = null;
/*     */       
/* 407 */       tryEachLeaf(root, root, violated, ss, clusters);
/* 408 */       if (this.m_BestTest == null) {
/*     */         return;
/*     */       }
/*     */ 
/*     */       
/* 413 */       enterBestTest(root, root, clusters);
/* 414 */       System.out.println("Tree:");
/* 415 */       root.printTree();
/* 416 */       ss = root.estimateClusteringSS((ClusAttributeWeights)this.m_Scale);
/* 417 */       clusters = assignAllInstances(root);
/* 418 */       violated = countViolatedConstaints(clusters);
/* 419 */       double heur = computeHeuristic(violated, ss);
/* 420 */       if (Math.abs(heur - this.m_BestHeur) > 1.0E-6D) {
/* 421 */         throw new ClusException("Error: heuristic " + heur + " <> " + this.m_BestHeur);
/*     */       }
/* 423 */       System.out.println("CHECK heuristic " + heur + " == " + this.m_BestHeur + " [OK]");
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void assignAllInstances(ClusNode tree, int[] clusters) {
/* 429 */     int nb_c = tree.getNbChildren();
/* 430 */     if (nb_c == 0) {
/* 431 */       ILevelCStatistic stat = (ILevelCStatistic)tree.getClusteringStat();
/* 432 */       stat.assignInstances((RowData)tree.getVisitor(), clusters);
/*     */     } 
/* 434 */     for (int i = 0; i < nb_c; i++) {
/* 435 */       ClusNode child = (ClusNode)tree.getChild(i);
/* 436 */       assignAllInstances(child, clusters);
/*     */     } 
/*     */   }
/*     */   
/*     */   public int[] assignAllInstances(ClusNode root) {
/* 441 */     int[] clusters = new int[this.m_NbTrain];
/* 442 */     assignAllInstances(root, clusters);
/* 443 */     return clusters;
/*     */   }
/*     */   
/*     */   public int[] countViolatedConstaints(RowData data, int[] clusters) {
/* 447 */     int violated = 0;
/* 448 */     int violated_internal = 0;
/* 449 */     int[] ie = createIE(data);
/* 450 */     ILevelConstraint[] constr = getSubsetConstraints(data);
/* 451 */     for (int i = 0; i < constr.length; i++) {
/* 452 */       ILevelConstraint ic = constr[i];
/* 453 */       int type = ic.getType();
/* 454 */       int t1 = ic.getT1().getIndex();
/* 455 */       int t2 = ic.getT2().getIndex();
/* 456 */       if (type == 0) {
/* 457 */         if (clusters[t1] != clusters[t2]) violated++;
/*     */       
/* 459 */       } else if (clusters[t1] == clusters[t2]) {
/* 460 */         violated++;
/* 461 */         if (ie[t1] != 0 && ie[t2] != 0) {
/* 462 */           violated_internal++;
/*     */         }
/*     */       } 
/*     */     } 
/*     */     
/* 467 */     int[] result = new int[2];
/* 468 */     result[0] = violated;
/* 469 */     result[1] = violated_internal;
/* 470 */     return result;
/*     */   }
/*     */   
/*     */   public int countViolatedConstaints(int[] clusters) {
/* 474 */     int violated = 0;
/* 475 */     for (int i = 0; i < this.m_Constraints.size(); i++) {
/* 476 */       ILevelConstraint ic = this.m_Constraints.get(i);
/* 477 */       int type = ic.getType();
/* 478 */       int t1 = ic.getT1().getIndex();
/* 479 */       int t2 = ic.getT2().getIndex();
/* 480 */       if (type == 0)
/* 481 */       { if (clusters[t1] != clusters[t2]) violated++;
/*     */          }
/* 483 */       else if (clusters[t1] == clusters[t2]) { violated++; }
/*     */     
/*     */     } 
/* 486 */     return violated;
/*     */   }
/*     */   
/*     */   public ArrayList createConstraints(RowData data, int nbRows) {
/* 490 */     ArrayList<ILevelConstraint> constr = new ArrayList();
/* 491 */     ClusAttrType type = getSchema().getAttrType(getSchema().getNbAttributes() - 1);
/* 492 */     if (type.getTypeIndex() == 0) {
/* 493 */       NominalAttrType cls = (NominalAttrType)type;
/* 494 */       this.m_MaxNbClasses = cls.getNbValues();
/* 495 */       int nbConstraints = getSettings().getILevelCNbRandomConstraints();
/* 496 */       for (int i = 0; i < nbConstraints; i++) {
/* 497 */         int t1i = ClusRandom.nextInt(4, nbRows);
/* 498 */         int t2i = ClusRandom.nextInt(4, nbRows);
/* 499 */         DataTuple t1 = data.getTuple(t1i);
/* 500 */         DataTuple t2 = data.getTuple(t2i);
/* 501 */         if (cls.getNominal(t1) == cls.getNominal(t2)) {
/* 502 */           constr.add(new ILevelConstraint(t1, t2, 0));
/*     */         } else {
/* 504 */           constr.add(new ILevelConstraint(t1, t2, 1));
/*     */         } 
/*     */       } 
/*     */     } 
/* 508 */     return constr;
/*     */   }
/*     */   
/*     */   public ClusModel induceSingleUnpruned(ClusRun cr) throws ClusException, IOException {
/* 512 */     this.m_NbClasses = 1;
/* 513 */     ClusRandom.reset(4);
/* 514 */     RowData data = (RowData)cr.getTrainingSet();
/* 515 */     int nbTrain = data.getNbRows();
/*     */     
/* 517 */     RowData test = cr.getTestSet();
/* 518 */     if (test != null) {
/* 519 */       ArrayList allData = new ArrayList();
/* 520 */       data.addTo(allData);
/* 521 */       test.addTo(allData);
/* 522 */       data = new RowData(allData, data.getSchema());
/*     */     } 
/* 524 */     System.out.println("All data: " + data.getNbRows());
/*     */     
/* 526 */     data.addIndices();
/* 527 */     this.m_NbTrain = data.getNbRows();
/* 528 */     this.m_MinLeafWeight = getSettings().getMinimalWeight();
/* 529 */     ArrayList points = data.toArrayList();
/*     */     
/* 531 */     if (getSettings().hasILevelCFile()) {
/* 532 */       String fname = getSettings().getILevelCFile();
/* 533 */       this.m_Constraints = ILevelConstraint.loadConstraints(fname, points);
/*     */     }
/*     */     else {
/*     */       
/* 537 */       this.m_Constraints = createConstraints(data, nbTrain);
/*     */     } 
/* 539 */     if (getSettings().isILevelCCOPKMeans()) {
/* 540 */       COPKMeans km = new COPKMeans(this.m_MaxNbClasses, getStatManager());
/* 541 */       COPKMeansModel model = null;
/* 542 */       int sumIter = 0;
/* 543 */       long t1 = System.currentTimeMillis();
/* 544 */       for (int i = 0; i < 100000; i++) {
/* 545 */         model = (COPKMeansModel)km.induce(data, this.m_Constraints);
/* 546 */         sumIter = Math.max(sumIter, model.getIterations());
/* 547 */         model.setCSets(i + 1);
/* 548 */         model.setAvgIter(sumIter);
/* 549 */         long t2 = System.currentTimeMillis();
/*     */         
/* 551 */         if (!model.isIllegal()) return (ClusModel)model; 
/* 552 */         this.m_Constraints = createConstraints(data, nbTrain);
/*     */       } 
/* 554 */       return (ClusModel)model;
/* 555 */     }  if (getSettings().isILevelCMPCKMeans()) {
/* 556 */       MPCKMeansWrapper wrap = new MPCKMeansWrapper(getStatManager());
/* 557 */       return wrap.induce(data, test, this.m_Constraints, this.m_MaxNbClasses);
/*     */     } 
/*     */     
/* 560 */     DerivedConstraintsComputer comp = new DerivedConstraintsComputer(points, this.m_Constraints);
/* 561 */     comp.compute();
/* 562 */     this.m_ConstraintsIndex = createConstraintsIndex();
/* 563 */     System.out.println("Number of instance level constraints: " + this.m_Constraints.size());
/*     */     
/* 565 */     ClusNode root = new ClusNode();
/* 566 */     root.initClusteringStat(this.m_StatManager, data);
/* 567 */     root.initTargetStat(this.m_StatManager, data);
/* 568 */     root.setVisitor(data);
/*     */     
/* 570 */     ILevelCStatistic ilevels = (ILevelCStatistic)root.getClusteringStat();
/* 571 */     ilevels.setClusterID(0);
/*     */     
/* 573 */     this.m_Scale = (ClusNormalizedAttributeWeights)getStatManager().getClusteringWeights();
/* 574 */     this.m_GlobalSS = ilevels.getSVarS((ClusAttributeWeights)this.m_Scale);
/* 575 */     System.out.println("Global SS: " + this.m_GlobalSS);
/*     */     
/* 577 */     initSelectorAndSplit(root.getClusteringStat());
/* 578 */     iLevelCInduce(root);
/* 579 */     root.postProc(null);
/* 580 */     cleanSplit();
/* 581 */     return (ClusModel)root;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\ext\ilevelc\ILevelCInduce.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */