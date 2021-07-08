/*      */ package clus.algo.tdidt;
/*      */ 
/*      */ import clus.algo.split.CurrentBestTestAndHeuristic;
/*      */ import clus.data.attweights.ClusAttributeWeights;
/*      */ import clus.data.rows.DataTuple;
/*      */ import clus.data.rows.RowData;
/*      */ import clus.error.multiscore.MultiScore;
/*      */ import clus.error.multiscore.MultiScoreStat;
/*      */ import clus.main.ClusRun;
/*      */ import clus.main.ClusStatManager;
/*      */ import clus.main.Global;
/*      */ import clus.model.ClusModel;
/*      */ import clus.model.ClusModelInfo;
/*      */ import clus.model.processor.ClusModelProcessor;
/*      */ import clus.model.test.NodeTest;
/*      */ import clus.statistic.ClusStatistic;
/*      */ import clus.statistic.StatisticPrintInfo;
/*      */ import clus.util.ClusException;
/*      */ import clus.util.ClusUtil;
/*      */ import java.io.IOException;
/*      */ import java.io.OutputStreamWriter;
/*      */ import java.io.PrintWriter;
/*      */ import java.util.ArrayList;
/*      */ import java.util.HashMap;
/*      */ import jeans.tree.MyNode;
/*      */ import jeans.tree.Node;
/*      */ import jeans.util.MyArray;
/*      */ import jeans.util.StringUtils;
/*      */ import jeans.util.compound.IntObject;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class ClusNode
/*      */   extends MyNode
/*      */   implements ClusModel
/*      */ {
/*      */   public static final long serialVersionUID = 1L;
/*      */   public static final int YES = 0;
/*      */   public static final int NO = 1;
/*      */   public static final int UNK = 2;
/*      */   public int m_ID;
/*      */   public NodeTest m_Test;
/*      */   public ClusStatistic m_ClusteringStat;
/*      */   public ClusStatistic m_TargetStat;
/*      */   public transient Object m_Visitor;
/*      */   public long m_Time;
/*      */   public String[] m_Alternatives;
/*      */   
/*      */   public MyNode cloneNode() {
/*   66 */     ClusNode clone = new ClusNode();
/*   67 */     clone.m_Test = this.m_Test;
/*   68 */     clone.m_ClusteringStat = this.m_ClusteringStat;
/*   69 */     clone.m_TargetStat = this.m_TargetStat;
/*   70 */     clone.m_Alternatives = this.m_Alternatives;
/*   71 */     return clone;
/*      */   }
/*      */   
/*      */   public ClusNode cloneNodeWithVisitor() {
/*   75 */     ClusNode clone = (ClusNode)cloneNode();
/*   76 */     clone.setVisitor(getVisitor());
/*   77 */     return clone;
/*      */   }
/*      */   
/*      */   public final ClusNode cloneTreeWithVisitors(ClusNode n1, ClusNode n2) {
/*   81 */     if (n1 == this) {
/*   82 */       return n2;
/*      */     }
/*   84 */     ClusNode clone = (ClusNode)cloneNode();
/*   85 */     clone.setVisitor(getVisitor());
/*   86 */     int arity = getNbChildren();
/*   87 */     clone.setNbChildren(arity);
/*   88 */     for (int i = 0; i < arity; i++) {
/*   89 */       ClusNode node = (ClusNode)getChild(i);
/*   90 */       clone.setChild((Node)node.cloneTreeWithVisitors(n1, n2), i);
/*      */     } 
/*   92 */     return clone;
/*      */   }
/*      */ 
/*      */   
/*      */   public final ClusNode cloneTreeWithVisitors() {
/*   97 */     ClusNode clone = (ClusNode)cloneNode();
/*   98 */     clone.setVisitor(getVisitor());
/*   99 */     int arity = getNbChildren();
/*  100 */     clone.setNbChildren(arity);
/*  101 */     for (int i = 0; i < arity; i++) {
/*  102 */       ClusNode node = (ClusNode)getChild(i);
/*  103 */       clone.setChild((Node)node.cloneTreeWithVisitors(), i);
/*      */     } 
/*  105 */     return clone;
/*      */   }
/*      */   
/*      */   public void inverseTests() {
/*  109 */     if (getNbChildren() == 2) {
/*  110 */       setTest(getTest().getBranchTest(1));
/*  111 */       ClusNode ch1 = (ClusNode)getChild(0);
/*  112 */       ClusNode ch2 = (ClusNode)getChild(1);
/*  113 */       ch1.inverseTests();
/*  114 */       ch2.inverseTests();
/*  115 */       setChild((Node)ch2, 0);
/*  116 */       setChild((Node)ch1, 1);
/*      */     } else {
/*  118 */       for (int i = 0; i < getNbChildren(); i++) {
/*  119 */         ClusNode node = (ClusNode)getChild(i);
/*  120 */         node.inverseTests();
/*      */       } 
/*      */     } 
/*      */   }
/*      */   
/*      */   public ClusNode[] getChildren() {
/*  126 */     ClusNode[] temp = new ClusNode[this.m_Children.size()];
/*  127 */     for (int i = 0; i < this.m_Children.size(); i++) {
/*  128 */       temp[i] = (ClusNode)getChild(i);
/*      */     }
/*  130 */     return temp;
/*      */   }
/*      */   
/*      */   public double checkTotalWeight() {
/*  134 */     if (atBottomLevel()) {
/*  135 */       return getClusteringStat().getTotalWeight();
/*      */     }
/*  137 */     double sum = 0.0D;
/*  138 */     for (int i = 0; i < getNbChildren(); i++) {
/*  139 */       ClusNode child = (ClusNode)getChild(i);
/*  140 */       sum += child.checkTotalWeight();
/*      */     } 
/*  142 */     if (Math.abs(getClusteringStat().getTotalWeight() - sum) > 1.0E-6D) {
/*  143 */       System.err.println("ClusNode::checkTotalWeight() error: " + getClusteringStat().getTotalWeight() + " <> " + sum);
/*      */     }
/*  145 */     return sum;
/*      */   }
/*      */ 
/*      */   
/*      */   public final void setVisitor(Object visitor) {
/*  150 */     this.m_Visitor = visitor;
/*      */   }
/*      */   
/*      */   public final Object getVisitor() {
/*  154 */     return this.m_Visitor;
/*      */   }
/*      */   
/*      */   public final void clearVisitors() {
/*  158 */     this.m_Visitor = null;
/*  159 */     int arity = getNbChildren();
/*  160 */     for (int i = 0; i < arity; i++) {
/*  161 */       ClusNode child = (ClusNode)getChild(i);
/*  162 */       child.clearVisitors();
/*      */     } 
/*      */   }
/*      */   
/*      */   public final int getID() {
/*  167 */     return this.m_ID;
/*      */   }
/*      */   
/*      */   public boolean equals(Object other) {
/*  171 */     ClusNode o = (ClusNode)other;
/*  172 */     if (this.m_Test != null && o.m_Test != null) {
/*  173 */       if (!this.m_Test.equals(o.m_Test)) {
/*  174 */         return false;
/*      */       }
/*      */     }
/*  177 */     else if (this.m_Test != null || o.m_Test != null) {
/*  178 */       return false;
/*      */     } 
/*      */     
/*  181 */     int nb_c = getNbChildren();
/*  182 */     for (int i = 0; i < nb_c; i++) {
/*  183 */       if (!getChild(i).equals(o.getChild(i))) {
/*  184 */         return false;
/*      */       }
/*      */     } 
/*  187 */     return true;
/*      */   }
/*      */   
/*      */   public int hashCode() {
/*  191 */     int hashCode = 1234;
/*  192 */     if (this.m_Test != null) {
/*  193 */       hashCode += this.m_Test.hashCode();
/*      */     } else {
/*  195 */       hashCode += 4567;
/*      */     } 
/*  197 */     int nb_c = getNbChildren();
/*  198 */     for (int i = 0; i < nb_c; i++) {
/*  199 */       hashCode += getChild(i).hashCode();
/*      */     }
/*  201 */     return hashCode;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final boolean hasBestTest() {
/*  210 */     return (this.m_Test != null);
/*      */   }
/*      */   
/*      */   public final NodeTest getTest() {
/*  214 */     return this.m_Test;
/*      */   }
/*      */   
/*      */   public final void setTest(NodeTest test) {
/*  218 */     this.m_Test = test;
/*      */   }
/*      */   
/*      */   public final String getTestString() {
/*  222 */     return (this.m_Test != null) ? this.m_Test.getString() : "None";
/*      */   }
/*      */   
/*      */   public final void testToNode(CurrentBestTestAndHeuristic best) {
/*  226 */     setTest(best.updateTest());
/*      */   }
/*      */   
/*      */   public int getModelSize() {
/*  230 */     return getNbNodes();
/*      */   }
/*      */   
/*      */   public String getModelInfo() {
/*  234 */     return "Nodes = " + getNbNodes() + " (Leaves: " + getNbLeaves() + ")";
/*      */   }
/*      */   
/*      */   public final boolean hasUnknownBranch() {
/*  238 */     return this.m_Test.hasUnknownBranch();
/*      */   }
/*      */   
/*      */   public String[] getAlternatives() {
/*  242 */     return this.m_Alternatives;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final ClusStatistic getClusteringStat() {
/*  251 */     return this.m_ClusteringStat;
/*      */   }
/*      */   
/*      */   public final ClusStatistic getTargetStat() {
/*  255 */     return this.m_TargetStat;
/*      */   }
/*      */   
/*      */   public final double getTotWeight() {
/*  259 */     return this.m_ClusteringStat.m_SumWeight;
/*      */   }
/*      */ 
/*      */   
/*      */   public final double getUnknownFreq() {
/*  264 */     return this.m_Test.getUnknownFreq();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final void setClusteringStat(ClusStatistic stat) {
/*  273 */     this.m_ClusteringStat = stat;
/*      */   }
/*      */   
/*      */   public final void setTargetStat(ClusStatistic stat) {
/*  277 */     this.m_TargetStat = stat;
/*      */   }
/*      */   
/*      */   public final void computePrediction() {
/*  281 */     if (getClusteringStat() != null) {
/*  282 */       getClusteringStat().calcMean();
/*      */     }
/*  284 */     if (getTargetStat() != null) {
/*  285 */       getTargetStat().calcMean();
/*      */     }
/*      */   }
/*      */   
/*      */   public final int updateArity() {
/*  290 */     int arity = this.m_Test.updateArity();
/*  291 */     setNbChildren(arity);
/*  292 */     return arity;
/*      */   }
/*      */   
/*      */   public final ClusNode postProc(MultiScore score) {
/*  296 */     updateTree();
/*  297 */     safePrune();
/*  298 */     return this;
/*      */   }
/*      */   
/*      */   public final void cleanup() {
/*  302 */     if (this.m_ClusteringStat != null) {
/*  303 */       this.m_ClusteringStat.setSDataSize(0);
/*      */     }
/*  305 */     if (this.m_TargetStat != null) {
/*  306 */       this.m_TargetStat.setSDataSize(0);
/*      */     }
/*      */   }
/*      */   
/*      */   public void makeLeaf() {
/*  311 */     this.m_Test = null;
/*  312 */     cleanup();
/*  313 */     removeAllChildren();
/*      */   }
/*      */   
/*      */   public final void updateTree() {
/*  317 */     cleanup();
/*  318 */     computePrediction();
/*  319 */     int nb_c = getNbChildren();
/*  320 */     for (int i = 0; i < nb_c; i++) {
/*  321 */       ClusNode info = (ClusNode)getChild(i);
/*  322 */       info.updateTree();
/*      */     } 
/*      */   }
/*      */   
/*      */   public void setAlternatives(ArrayList<E> alt) {
/*  327 */     this.m_Alternatives = new String[alt.size()];
/*  328 */     for (int i = 0; i < alt.size(); i++) {
/*  329 */       this.m_Alternatives[i] = alt.get(i).toString();
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final boolean samePrediction(ClusNode other) {
/*  340 */     return this.m_TargetStat.samePrediction(other.m_TargetStat);
/*      */   }
/*      */ 
/*      */   
/*      */   public final boolean allSameLeaves() {
/*  345 */     int nb_c = getNbChildren();
/*  346 */     if (nb_c == 0) {
/*  347 */       return false;
/*      */     }
/*  349 */     ClusNode cr = (ClusNode)getChild(0);
/*  350 */     if (!cr.atBottomLevel()) {
/*  351 */       return false;
/*      */     }
/*  353 */     for (int i = 1; i < nb_c; i++) {
/*  354 */       ClusNode info = (ClusNode)getChild(i);
/*  355 */       if (!info.atBottomLevel()) {
/*  356 */         return false;
/*      */       }
/*  358 */       if (!info.samePrediction(cr)) {
/*  359 */         return false;
/*      */       }
/*      */     } 
/*  362 */     return true;
/*      */   }
/*      */   
/*      */   public void pruneByTrainErr(ClusAttributeWeights scale) {
/*  366 */     if (!atBottomLevel()) {
/*  367 */       double errorsOfSubtree = estimateErrorAbsolute(scale);
/*  368 */       double errorsOfLeaf = getTargetStat().getError(scale);
/*  369 */       if (errorsOfSubtree >= errorsOfLeaf - 0.001D) {
/*  370 */         makeLeaf();
/*      */       } else {
/*  372 */         for (int i = 0; i < getNbChildren(); i++) {
/*  373 */           ClusNode child = (ClusNode)getChild(i);
/*  374 */           child.pruneByTrainErr(scale);
/*      */         } 
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public final void safePrune() {
/*  382 */     int nb_c = getNbChildren();
/*  383 */     for (int i = 0; i < nb_c; i++) {
/*  384 */       ClusNode info = (ClusNode)getChild(i);
/*  385 */       info.safePrune();
/*      */     } 
/*  387 */     if (allSameLeaves())
/*      */     {
/*  389 */       makeLeaf();
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean allInvalidLeaves() {
/*  394 */     int nb_c = getNbChildren();
/*  395 */     if (nb_c == 0) {
/*  396 */       return false;
/*      */     }
/*  398 */     for (int i = 0; i < nb_c; i++) {
/*  399 */       ClusNode info = (ClusNode)getChild(i);
/*  400 */       if (!info.atBottomLevel()) {
/*  401 */         return false;
/*      */       }
/*  403 */       if (info.getTargetStat().isValidPrediction()) {
/*  404 */         return false;
/*      */       }
/*      */     } 
/*  407 */     return true;
/*      */   }
/*      */   
/*      */   public final void pruneInvalid() {
/*  411 */     int nb_c = getNbChildren();
/*  412 */     for (int i = 0; i < nb_c; i++) {
/*  413 */       ClusNode info = (ClusNode)getChild(i);
/*  414 */       info.pruneInvalid();
/*      */     } 
/*  416 */     if (allInvalidLeaves()) {
/*  417 */       makeLeaf();
/*      */     }
/*      */   }
/*      */   
/*      */   public ClusModel prune(int prunetype) {
/*  422 */     if (prunetype == 0) {
/*  423 */       ClusNode pruned = (ClusNode)cloneTree();
/*  424 */       pruned.pruneInvalid();
/*  425 */       return pruned;
/*      */     } 
/*  427 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final void multiScore(MultiScore score) {
/*  436 */     this.m_ClusteringStat = (ClusStatistic)new MultiScoreStat(this.m_ClusteringStat, score);
/*  437 */     int nb_c = getNbChildren();
/*  438 */     for (int i = 0; i < nb_c; i++) {
/*  439 */       ClusNode info = (ClusNode)getChild(i);
/*  440 */       info.multiScore(score);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final void attachModel(HashMap table) throws ClusException {
/*  450 */     int nb_c = getNbChildren();
/*  451 */     if (nb_c > 0) {
/*  452 */       this.m_Test.attachModel(table);
/*      */     }
/*  454 */     for (int i = 0; i < nb_c; i++) {
/*  455 */       ClusNode info = (ClusNode)getChild(i);
/*  456 */       info.attachModel(table);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ClusStatistic predictWeighted(DataTuple tuple) {
/*  466 */     if (atBottomLevel())
/*      */     {
/*      */       
/*  469 */       return getTargetStat();
/*      */     }
/*      */     
/*  472 */     int n_idx = this.m_Test.predictWeighted(tuple);
/*      */     
/*  474 */     n_idx = -1;
/*  475 */     if (n_idx != -1) {
/*  476 */       ClusNode info = (ClusNode)getChild(n_idx);
/*  477 */       return info.predictWeighted(tuple);
/*      */     } 
/*  479 */     int nb_c = getNbChildren();
/*  480 */     ClusNode ch_0 = (ClusNode)getChild(0);
/*  481 */     ClusStatistic ch_0s = ch_0.predictWeighted(tuple);
/*  482 */     ClusStatistic stat = ch_0s.cloneSimple();
/*  483 */     stat.addPrediction(ch_0s, this.m_Test.getProportion(0));
/*  484 */     for (int i = 1; i < nb_c; i++) {
/*  485 */       ClusNode ch_i = (ClusNode)getChild(i);
/*  486 */       ClusStatistic ch_is = ch_i.predictWeighted(tuple);
/*  487 */       stat.addPrediction(ch_is, this.m_Test.getProportion(i));
/*      */     } 
/*  489 */     stat.computePrediction();
/*  490 */     return stat;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public ClusStatistic clusterWeighted(DataTuple tuple) {
/*  496 */     if (atBottomLevel()) {
/*  497 */       return getClusteringStat();
/*      */     }
/*  499 */     int n_idx = this.m_Test.predictWeighted(tuple);
/*  500 */     if (n_idx != -1) {
/*  501 */       ClusNode info = (ClusNode)getChild(n_idx);
/*  502 */       return info.clusterWeighted(tuple);
/*      */     } 
/*  504 */     int nb_c = getNbChildren();
/*  505 */     ClusStatistic stat = getClusteringStat().cloneSimple();
/*  506 */     for (int i = 0; i < nb_c; i++) {
/*  507 */       ClusNode node = (ClusNode)getChild(i);
/*  508 */       ClusStatistic nodes = node.clusterWeighted(tuple);
/*  509 */       stat.addPrediction(nodes, this.m_Test.getProportion(i));
/*      */     } 
/*  511 */     stat.computePrediction();
/*  512 */     return stat;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public final void applyModelProcessor(DataTuple tuple, ClusModelProcessor proc) throws IOException {
/*  518 */     int nb_c = getNbChildren();
/*  519 */     if (nb_c == 0 || proc.needsInternalNodes()) {
/*  520 */       proc.modelUpdate(tuple, this);
/*      */     }
/*  522 */     if (nb_c != 0) {
/*  523 */       int n_idx = this.m_Test.predictWeighted(tuple);
/*  524 */       if (n_idx != -1) {
/*  525 */         ClusNode info = (ClusNode)getChild(n_idx);
/*  526 */         info.applyModelProcessor(tuple, proc);
/*      */       } else {
/*  528 */         for (int i = 0; i < nb_c; i++) {
/*  529 */           ClusNode node = (ClusNode)getChild(i);
/*  530 */           double prop = this.m_Test.getProportion(i);
/*  531 */           node.applyModelProcessor(tuple.multiplyWeight(prop), proc);
/*      */         } 
/*      */       } 
/*      */     } 
/*      */   }
/*      */   
/*      */   public final void applyModelProcessors(DataTuple tuple, MyArray mproc) throws IOException {
/*  538 */     int nb_c = getNbChildren();
/*  539 */     for (int i = 0; i < mproc.size(); i++) {
/*  540 */       ClusModelProcessor proc = (ClusModelProcessor)mproc.elementAt(i);
/*  541 */       if (nb_c == 0 || proc.needsInternalNodes()) {
/*  542 */         proc.modelUpdate(tuple, this);
/*      */       }
/*      */     } 
/*  545 */     if (nb_c != 0) {
/*  546 */       int n_idx = this.m_Test.predictWeighted(tuple);
/*  547 */       if (n_idx != -1) {
/*  548 */         ClusNode info = (ClusNode)getChild(n_idx);
/*  549 */         info.applyModelProcessors(tuple, mproc);
/*      */       } else {
/*  551 */         for (int j = 0; j < nb_c; j++) {
/*  552 */           ClusNode node = (ClusNode)getChild(j);
/*  553 */           double prop = this.m_Test.getProportion(j);
/*  554 */           node.applyModelProcessors(tuple.multiplyWeight(prop), mproc);
/*      */         } 
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final void initTargetStat(ClusStatManager smgr, RowData subset) {
/*  566 */     this.m_TargetStat = smgr.createTargetStat();
/*  567 */     subset.calcTotalStatBitVector(this.m_TargetStat);
/*      */   }
/*      */   
/*      */   public final void initClusteringStat(ClusStatManager smgr, RowData subset) {
/*  571 */     this.m_ClusteringStat = smgr.createClusteringStat();
/*  572 */     subset.calcTotalStatBitVector(this.m_ClusteringStat);
/*      */   }
/*      */   
/*      */   public final void initTargetStat(ClusStatManager smgr, ClusStatistic train, RowData subset) {
/*  576 */     this.m_TargetStat = smgr.createTargetStat();
/*  577 */     this.m_TargetStat.setTrainingStat(train);
/*  578 */     subset.calcTotalStatBitVector(this.m_TargetStat);
/*      */   }
/*      */   
/*      */   public final void initClusteringStat(ClusStatManager smgr, ClusStatistic train, RowData subset) {
/*  582 */     this.m_ClusteringStat = smgr.createClusteringStat();
/*  583 */     this.m_ClusteringStat.setTrainingStat(train);
/*  584 */     subset.calcTotalStatBitVector(this.m_ClusteringStat);
/*      */   }
/*      */   
/*      */   public final void reInitTargetStat(RowData subset) {
/*  588 */     if (this.m_TargetStat != null) {
/*  589 */       this.m_TargetStat.reset();
/*  590 */       subset.calcTotalStatBitVector(this.m_TargetStat);
/*      */     } 
/*      */   }
/*      */   
/*      */   public final void reInitClusteringStat(RowData subset) {
/*  595 */     if (this.m_ClusteringStat != null) {
/*  596 */       this.m_ClusteringStat.reset();
/*  597 */       subset.calcTotalStatBitVector(this.m_ClusteringStat);
/*      */     } 
/*      */   }
/*      */   
/*      */   public final void initTotStats(ClusStatistic stat) {
/*  602 */     this.m_ClusteringStat = stat.cloneStat();
/*  603 */     int nb_c = getNbChildren();
/*  604 */     for (int i = 0; i < nb_c; i++) {
/*  605 */       ClusNode node = (ClusNode)getChild(i);
/*  606 */       node.initTotStats(stat);
/*      */     } 
/*      */   }
/*      */   
/*      */   public final void numberTree() {
/*  611 */     numberTree(new IntObject(1, null));
/*      */   }
/*      */   
/*      */   public final void numberTree(IntObject count) {
/*  615 */     int arity = getNbChildren();
/*  616 */     if (arity > 0) {
/*  617 */       this.m_ID = 0;
/*  618 */       for (int i = 0; i < arity; i++) {
/*  619 */         ClusNode child = (ClusNode)getChild(i);
/*  620 */         child.numberTree(count);
/*      */       } 
/*      */     } else {
/*  623 */       this.m_ID = count.getValue();
/*  624 */       count.incValue();
/*      */     } 
/*      */   }
/*      */   
/*      */   public final void numberCompleteTree() {
/*  629 */     numberCompleteTree(new IntObject(1, null));
/*      */   }
/*      */   
/*      */   public final void numberCompleteTree(IntObject count) {
/*  633 */     this.m_ID = count.getValue();
/*  634 */     count.incValue();
/*  635 */     int arity = getNbChildren();
/*  636 */     for (int i = 0; i < arity; i++) {
/*  637 */       ClusNode child = (ClusNode)getChild(i);
/*  638 */       child.numberCompleteTree(count);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final int getTotalTreeSize() {
/*  646 */     int childrensize = 0;
/*  647 */     int arity = getNbChildren();
/*  648 */     for (int i = 0; i < arity; i++) {
/*  649 */       ClusNode child = (ClusNode)getChild(i);
/*  650 */       childrensize += child.getTotalTreeSize();
/*      */     } 
/*  652 */     return childrensize + 1;
/*      */   }
/*      */   
/*      */   public final void addChildStats() {
/*  656 */     int nb_c = getNbChildren();
/*  657 */     if (nb_c > 0) {
/*  658 */       ClusNode ch0 = (ClusNode)getChild(0);
/*  659 */       ch0.addChildStats();
/*  660 */       ClusStatistic stat = ch0.getClusteringStat();
/*  661 */       ClusStatistic root = stat.cloneSimple();
/*  662 */       root.addPrediction(stat, 1.0D);
/*  663 */       for (int i = 1; i < nb_c; i++) {
/*  664 */         ClusNode node = (ClusNode)getChild(i);
/*  665 */         node.addChildStats();
/*  666 */         root.addPrediction(node.getClusteringStat(), 1.0D);
/*      */       } 
/*  668 */       root.calcMean();
/*  669 */       setClusteringStat(root);
/*      */     } 
/*      */   }
/*      */   
/*      */   public double estimateErrorAbsolute(ClusAttributeWeights scale) {
/*  674 */     return estimateErrorRecursive(this, scale);
/*      */   }
/*      */   
/*      */   public double estimateError(ClusAttributeWeights scale) {
/*  678 */     return estimateErrorRecursive(this, scale) / getTargetStat().getTotalWeight();
/*      */   }
/*      */   
/*      */   public double estimateClusteringSS(ClusAttributeWeights scale) {
/*  682 */     return estimateClusteringSSRecursive(this, scale);
/*      */   }
/*      */   
/*      */   public double estimateClusteringVariance(ClusAttributeWeights scale) {
/*  686 */     return estimateClusteringSSRecursive(this, scale) / getClusteringStat().getTotalWeight();
/*      */   }
/*      */   
/*      */   public static double estimateClusteringSSRecursive(ClusNode tree, ClusAttributeWeights scale) {
/*  690 */     if (tree.atBottomLevel()) {
/*  691 */       ClusStatistic total = tree.getClusteringStat();
/*  692 */       return total.getSVarS(scale);
/*      */     } 
/*  694 */     double result = 0.0D;
/*  695 */     for (int i = 0; i < tree.getNbChildren(); i++) {
/*  696 */       ClusNode child = (ClusNode)tree.getChild(i);
/*  697 */       result += estimateClusteringSSRecursive(child, scale);
/*      */     } 
/*  699 */     return result;
/*      */   }
/*      */ 
/*      */   
/*      */   public static double estimateErrorRecursive(ClusNode tree, ClusAttributeWeights scale) {
/*  704 */     if (tree.atBottomLevel()) {
/*  705 */       ClusStatistic total = tree.getTargetStat();
/*  706 */       return total.getError(scale);
/*      */     } 
/*  708 */     double result = 0.0D;
/*  709 */     for (int i = 0; i < tree.getNbChildren(); i++) {
/*  710 */       ClusNode child = (ClusNode)tree.getChild(i);
/*  711 */       result += estimateErrorRecursive(child, scale);
/*      */     } 
/*  713 */     return result;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static double estimateErrorRecursive(ClusNode tree) {
/*  720 */     if (tree.atBottomLevel()) {
/*  721 */       ClusStatistic total = tree.getTargetStat();
/*      */       
/*  723 */       return total.getError();
/*      */     } 
/*  725 */     double result = 0.0D;
/*  726 */     for (int i = 0; i < tree.getNbChildren(); i++) {
/*  727 */       ClusNode child = (ClusNode)tree.getChild(i);
/*  728 */       result += estimateErrorRecursive(child);
/*      */     } 
/*  730 */     return result;
/*      */   }
/*      */ 
/*      */   
/*      */   public int getNbLeaf() {
/*  735 */     int nbleaf = 0;
/*  736 */     if (atBottomLevel()) {
/*  737 */       nbleaf++;
/*      */     } else {
/*  739 */       for (int i = 0; i < getNbChildren(); i++) {
/*  740 */         ClusNode child = (ClusNode)getChild(i);
/*  741 */         nbleaf += child.getNbLeaf();
/*      */       } 
/*      */     } 
/*  744 */     return nbleaf;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void printModel(PrintWriter wrt) {
/*  754 */     printTree(wrt, StatisticPrintInfo.getInstance(), "");
/*      */   }
/*      */   
/*      */   public void printModel(PrintWriter wrt, StatisticPrintInfo info) {
/*  758 */     printTree(wrt, info, "");
/*      */   }
/*      */   
/*      */   public void printModelAndExamples(PrintWriter wrt, StatisticPrintInfo info, RowData examples) {
/*  762 */     printTree(wrt, info, "", examples);
/*      */   }
/*      */   
/*      */   public void printModelToPythonScript(PrintWriter wrt) {
/*  766 */     printTreeToPythonScript(wrt, "\t");
/*      */   }
/*      */   
/*      */   public void printModelToQuery(PrintWriter wrt, ClusRun cr, int starttree, int startitem, boolean exhaustive) {
/*  770 */     int lastmodel = cr.getNbModels() - 1;
/*  771 */     System.out.println("The number of models to print is:" + lastmodel);
/*  772 */     String[][] tabitem = new String[lastmodel + 1][10000];
/*  773 */     int[][] tabexist = new int[lastmodel + 1][10000];
/*  774 */     Global.set_treecpt(starttree);
/*  775 */     Global.set_itemsetcpt(startitem);
/*  776 */     ClusModelInfo m = cr.getModelInfo(0);
/*      */     
/*  778 */     if (exhaustive) {
/*  779 */       for (int i = 0; i < cr.getNbModels(); i++) {
/*  780 */         ClusModelInfo mod = cr.getModelInfo(i);
/*  781 */         ClusNode tree = (ClusNode)cr.getModel(i);
/*  782 */         if (tree.getNbChildren() != 0) {
/*  783 */           tree.printTreeInDatabase(wrt, tabitem[i], tabexist[i], 0, "all_trees");
/*      */         }
/*      */         
/*  786 */         if (tree.getNbNodes() <= 1) {
/*  787 */           double error_rate = tree.m_ClusteringStat.getErrorRel();
/*  788 */           wrt.println("#" + tree.m_ClusteringStat.getPredictedClassName(0));
/*  789 */           wrt.println(mod.getModelSize() + ", " + error_rate + ", " + (1.0D - error_rate));
/*      */         } else {
/*      */           
/*  792 */           wrt.println(mod.getModelSize() + ", " + mod.m_TrainErr.getErrorClassif() + ", " + mod.m_TrainErr.getErrorAccuracy());
/*      */         } 
/*  794 */         Global.inc_treecpt();
/*      */       } 
/*      */     } else {
/*      */       
/*  798 */       ClusModelInfo mod = cr.getModelInfo(lastmodel);
/*  799 */       ClusNode tree = (ClusNode)cr.getModel(lastmodel);
/*  800 */       tabitem[lastmodel][0] = "null";
/*  801 */       tabexist[lastmodel][0] = 1;
/*  802 */       wrt.println("INSERT INTO trees_sets VALUES(" + Global.get_itemsetcpt() + ", '" + tabitem[lastmodel][0] + "', " + tabexist[lastmodel][0] + ")");
/*  803 */       wrt.println("INSERT INTO greedy_trees VALUES(" + Global.get_treecpt() + ", " + Global.get_itemsetcpt() + ",1)");
/*  804 */       Global.inc_itemsetcpt();
/*  805 */       if (tree.getNbChildren() != 0) {
/*  806 */         printTreeInDatabase(wrt, tabitem[lastmodel], tabexist[lastmodel], 1, "greedy_trees");
/*      */       }
/*  808 */       wrt.println("INSERT INTO trees_charac VALUES(" + Global.get_treecpt() + ", " + mod.getModelSize() + ", " + mod.m_TrainErr.getErrorClassif() + ", " + mod.m_TrainErr.getErrorAccuracy() + ", NULL)");
/*  809 */       Global.inc_treecpt();
/*      */     } 
/*      */   }
/*      */   
/*      */   public final void printTree() {
/*  814 */     PrintWriter wrt = new PrintWriter(new OutputStreamWriter(System.out));
/*  815 */     printTree(wrt, StatisticPrintInfo.getInstance(), "");
/*  816 */     wrt.flush();
/*      */   }
/*      */   
/*      */   public final void writeDistributionForInternalNode(PrintWriter writer, StatisticPrintInfo info) {
/*  820 */     if (info.INTERNAL_DISTR && 
/*  821 */       this.m_TargetStat != null) {
/*  822 */       writer.print(": " + this.m_TargetStat.getString(info));
/*      */     }
/*      */     
/*  825 */     writer.println();
/*      */   }
/*      */   
/*      */   public final void printTree(PrintWriter writer, StatisticPrintInfo info, String prefix) {
/*  829 */     printTree(writer, info, prefix, (RowData)null);
/*      */   }
/*      */   
/*      */   public final void printTree(PrintWriter writer, StatisticPrintInfo info, String prefix, RowData examples) {
/*  833 */     int arity = getNbChildren();
/*  834 */     if (arity > 0) {
/*  835 */       int delta = hasUnknownBranch() ? 1 : 0;
/*  836 */       if (arity - delta == 2) {
/*  837 */         writer.print(this.m_Test.getTestString());
/*      */         
/*  839 */         RowData examples0 = null;
/*  840 */         RowData examples1 = null;
/*  841 */         if (examples != null) {
/*  842 */           examples0 = examples.apply(this.m_Test, 0);
/*  843 */           examples1 = examples.apply(this.m_Test, 1);
/*      */         } 
/*  845 */         showAlternatives(writer);
/*  846 */         writeDistributionForInternalNode(writer, info);
/*  847 */         writer.print(prefix + "+--yes: ");
/*  848 */         ((ClusNode)getChild(0)).printTree(writer, info, prefix + "|       ", examples0);
/*  849 */         writer.print(prefix + "+--no:  ");
/*  850 */         if (hasUnknownBranch()) {
/*  851 */           ((ClusNode)getChild(1)).printTree(writer, info, prefix + "|       ", examples1);
/*  852 */           writer.print(prefix + "+--unk: ");
/*  853 */           ((ClusNode)getChild(2)).printTree(writer, info, prefix + "        ", examples0);
/*      */         } else {
/*  855 */           ((ClusNode)getChild(1)).printTree(writer, info, prefix + "        ", examples1);
/*      */         } 
/*      */       } else {
/*  858 */         writer.println(this.m_Test.getTestString());
/*  859 */         for (int i = 0; i < arity; i++) {
/*  860 */           ClusNode child = (ClusNode)getChild(i);
/*  861 */           String branchlabel = this.m_Test.getBranchLabel(i);
/*  862 */           RowData examplesi = null;
/*  863 */           if (examples != null) {
/*  864 */             examples.apply(this.m_Test, i);
/*      */           }
/*  866 */           writer.print(prefix + "+--" + branchlabel + ": ");
/*  867 */           String suffix = StringUtils.makeString(' ', branchlabel.length() + 4);
/*  868 */           if (i != arity - 1) {
/*  869 */             child.printTree(writer, info, prefix + "|" + suffix, examplesi);
/*      */           } else {
/*  871 */             child.printTree(writer, info, prefix + " " + suffix, examplesi);
/*      */           } 
/*      */         } 
/*      */       } 
/*      */     } else {
/*  876 */       if (this.m_TargetStat == null) {
/*  877 */         writer.print("?");
/*      */       } else {
/*  879 */         writer.print(this.m_TargetStat.getString(info));
/*      */       } 
/*  881 */       if (getID() != 0 && info.SHOW_INDEX) {
/*  882 */         writer.println(" (" + getID() + ")");
/*      */       } else {
/*  884 */         writer.println();
/*      */       } 
/*  886 */       if (examples != null && examples.getNbRows() > 0) {
/*  887 */         writer.println(examples.toString(prefix));
/*  888 */         writer.println(prefix + "Summary:");
/*  889 */         writer.println(examples.getSummary(prefix));
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final void printTreeInDatabase(PrintWriter writer, String[] tabitem, int[] tabexist, int cpt, String typetree) {
/*  998 */     int arity = getNbChildren();
/*  999 */     if (arity > 0) {
/* 1000 */       int delta = hasUnknownBranch() ? 1 : 0;
/* 1001 */       if (arity - delta == 2) {
/*      */         
/* 1003 */         tabitem[cpt] = this.m_Test.getTestString();
/* 1004 */         tabexist[cpt] = 1;
/* 1005 */         cpt++;
/* 1006 */         ((ClusNode)getChild(0)).printTreeInDatabase(writer, tabitem, tabexist, cpt, typetree);
/* 1007 */         cpt--;
/*      */         
/* 1009 */         tabitem[cpt] = this.m_Test.getTestString();
/*      */         
/* 1011 */         tabexist[cpt] = 0;
/* 1012 */         cpt++;
/* 1013 */         if (hasUnknownBranch()) {
/* 1014 */           ((ClusNode)getChild(1)).printTreeInDatabase(writer, tabitem, tabexist, cpt, typetree);
/*      */           
/* 1016 */           ((ClusNode)getChild(2)).printTreeInDatabase(writer, tabitem, tabexist, cpt, typetree);
/*      */         } else {
/* 1018 */           ((ClusNode)getChild(1)).printTreeInDatabase(writer, tabitem, tabexist, cpt, typetree);
/*      */         }
/*      */       
/*      */       } else {
/*      */         
/* 1023 */         writer.println("arity-delta different 2");
/* 1024 */         for (int i = 0; i < arity; i++) {
/* 1025 */           ClusNode child = (ClusNode)getChild(i);
/* 1026 */           String branchlabel = this.m_Test.getBranchLabel(i);
/* 1027 */           writer.print("+--" + branchlabel + ": ");
/* 1028 */           if (i != arity - 1) {
/* 1029 */             child.printTreeInDatabase(writer, tabitem, tabexist, cpt, typetree);
/*      */           } else {
/* 1031 */             child.printTreeInDatabase(writer, tabitem, tabexist, cpt, typetree);
/*      */           }
/*      */         
/*      */         }
/*      */       
/*      */       } 
/* 1037 */     } else if (this.m_TargetStat == null) {
/* 1038 */       writer.print("?");
/*      */     } else {
/* 1040 */       tabitem[cpt] = this.m_TargetStat.getPredictedClassName(0);
/* 1041 */       tabexist[cpt] = 1;
/* 1042 */       writer.print("#");
/* 1043 */       for (int i = 0; i <= cpt - 1; i++) {
/* 1044 */         writer.print(printTestNode(tabitem[i], tabexist[i]) + ", ");
/*      */       }
/* 1046 */       writer.println(printTestNode(tabitem[cpt], tabexist[cpt]));
/* 1047 */       cpt++;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public String printTestNode(String a, int pres) {
/* 1054 */     if (pres == 1) {
/* 1055 */       return a;
/*      */     }
/* 1057 */     return "not(" + a + ")";
/*      */   }
/*      */ 
/*      */   
/*      */   public final void printTreeToPythonScript(PrintWriter writer, String prefix) {
/* 1062 */     int arity = getNbChildren();
/* 1063 */     if (arity > 0) {
/* 1064 */       int delta = hasUnknownBranch() ? 1 : 0;
/* 1065 */       if (arity - delta == 2) {
/* 1066 */         writer.println(prefix + "if " + this.m_Test.getTestString() + ":");
/* 1067 */         ((ClusNode)getChild(0)).printTreeToPythonScript(writer, prefix + "\t");
/* 1068 */         writer.println(prefix + "else: ");
/* 1069 */         if (!hasUnknownBranch())
/*      */         {
/*      */           
/* 1072 */           ((ClusNode)getChild(1)).printTreeToPythonScript(writer, prefix + "\t");
/*      */         
/*      */         }
/*      */       }
/*      */     
/*      */     }
/* 1078 */     else if (this.m_TargetStat != null) {
/* 1079 */       writer.println(prefix + "return " + this.m_TargetStat.getArrayOfStatistic());
/* 1080 */       System.out.println(this.m_TargetStat.getClass());
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public final void showAlternatives(PrintWriter writer) {
/* 1086 */     if (this.m_Alternatives == null) {
/*      */       return;
/*      */     }
/* 1089 */     for (int i = 0; i < this.m_Alternatives.length; i++) {
/* 1090 */       writer.print(" and " + this.m_Alternatives[i]);
/*      */     }
/*      */   }
/*      */   
/*      */   public String toString() {
/*      */     try {
/* 1096 */       if (hasBestTest()) {
/* 1097 */         return getTestString();
/*      */       }
/* 1099 */       return this.m_TargetStat.getSimpleString();
/*      */     }
/* 1101 */     catch (Exception e) {
/* 1102 */       return "null clusnode ";
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ClusStatistic predictWeightedLeaf(DataTuple tuple) {
/* 1110 */     return getTargetStat();
/*      */   }
/*      */   
/*      */   public void retrieveStatistics(ArrayList<ClusStatistic> list) {
/* 1114 */     if (this.m_ClusteringStat != null) {
/* 1115 */       list.add(this.m_ClusteringStat);
/*      */     }
/* 1117 */     if (this.m_TargetStat != null) {
/* 1118 */       list.add(this.m_TargetStat);
/*      */     }
/* 1120 */     int arity = getNbChildren();
/* 1121 */     for (int i = 0; i < arity; i++) {
/* 1122 */       ClusNode child = (ClusNode)getChild(i);
/* 1123 */       child.retrieveStatistics(list);
/*      */     } 
/*      */   }
/*      */   
/*      */   public int getLargestBranchIndex() {
/* 1128 */     double max = 0.0D;
/* 1129 */     int max_idx = -1;
/* 1130 */     for (int i = 0; i < getNbChildren(); i++) {
/* 1131 */       ClusNode child = (ClusNode)getChild(i);
/* 1132 */       double child_w = child.getTotWeight();
/* 1133 */       if (ClusUtil.grOrEq(child_w, max)) {
/* 1134 */         max = child_w;
/* 1135 */         max_idx = i;
/*      */       } 
/*      */     } 
/* 1138 */     return max_idx;
/*      */   }
/*      */ 
/*      */   
/*      */   public void adaptToData(RowData data) {
/* 1143 */     NodeTest tst = getTest();
/* 1144 */     for (int i = 0; i < getNbChildren(); i++) {
/* 1145 */       ClusNode child = (ClusNode)getChild(i);
/* 1146 */       RowData subset = data.applyWeighted(tst, i);
/* 1147 */       child.adaptToData(subset);
/*      */     } 
/*      */     
/* 1150 */     reInitTargetStat(data);
/* 1151 */     reInitClusteringStat(data);
/*      */   }
/*      */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\algo\tdidt\ClusNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */