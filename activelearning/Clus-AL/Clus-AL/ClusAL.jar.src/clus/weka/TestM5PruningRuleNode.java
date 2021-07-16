/*      */ package clus.weka;
/*      */ 
/*      */ import clus.algo.tdidt.ClusNode;
/*      */ import clus.data.attweights.ClusAttributeWeights;
/*      */ import clus.data.rows.RowData;
/*      */ import clus.model.test.NodeTest;
/*      */ import clus.model.test.NumericTest;
/*      */ import weka.classifiers.Classifier;
/*      */ import weka.classifiers.Evaluation;
/*      */ import weka.classifiers.functions.LinearRegression;
/*      */ import weka.classifiers.trees.m5.PreConstructedLinearModel;
/*      */ import weka.classifiers.trees.m5.SplitEvaluate;
/*      */ import weka.classifiers.trees.m5.YongSplitInfo;
/*      */ import weka.core.FastVector;
/*      */ import weka.core.Instance;
/*      */ import weka.core.Instances;
/*      */ import weka.core.Utils;
/*      */ import weka.filters.Filter;
/*      */ import weka.filters.unsupervised.attribute.Remove;
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
/*      */ public class TestM5PruningRuleNode
/*      */   extends Classifier
/*      */ {
/*      */   public static final long serialVersionUID = 1L;
/*      */   private Instances m_instances;
/*      */   private int m_classIndex;
/*      */   protected int m_numInstances;
/*      */   private int m_numAttributes;
/*      */   private boolean m_isLeaf;
/*      */   private int m_splitAtt;
/*      */   private double m_splitValue;
/*      */   private PreConstructedLinearModel m_nodeModel;
/*      */   public int m_numParameters;
/*      */   private double m_rootMeanSquaredError;
/*      */   protected TestM5PruningRuleNode m_left;
/*      */   protected TestM5PruningRuleNode m_right;
/*      */   private TestM5PruningRuleNode m_parent;
/*  138 */   private double m_splitNum = 4.0D;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  144 */   private double m_devFraction = 0.05D;
/*  145 */   private double m_pruningMultiplier = 2.0D;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private int m_leafModelNum;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private double m_globalDeviation;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private double m_globalAbsDeviation;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private int[] m_indices;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static final double SMOOTHING_CONSTANT = 15.0D;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private int m_id;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean m_saveInstances = false;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean m_regressionTree;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void createM5RuleNodeRecursive(TestM5PruningRuleNode result, ClusNode node, RowData data, ClusToWekaData cnv) {
/*  193 */     result.m_classIndex = cnv.getClassIndex();
/*  194 */     result.m_instances = cnv.convertData(data);
/*  195 */     result.m_isLeaf = node.atBottomLevel();
/*  196 */     result.m_numInstances = data.getNbRows();
/*  197 */     result.m_numAttributes = result.m_instances.numAttributes();
/*  198 */     result.m_regressionTree = true;
/*  199 */     result.m_indices = new int[1];
/*  200 */     result.m_indices[0] = cnv.getClassIndex();
/*  201 */     result.m_numParameters = 1;
/*  202 */     NodeTest tst = node.getTest();
/*  203 */     if (tst != null) {
/*  204 */       result.m_splitAtt = cnv.getIndex(tst.getType().getName());
/*  205 */       if (tst instanceof NumericTest) result.m_splitValue = ((NumericTest)tst).getBound(); 
/*      */     } 
/*  207 */     if (node.getNbChildren() != 0)
/*  208 */       for (int i = 0; i < 2; i++) {
/*  209 */         TestM5PruningRuleNode child = new TestM5PruningRuleNode(result.m_globalDeviation, result.m_globalAbsDeviation, result);
/*  210 */         RowData subset = data.applyWeighted(tst, i);
/*  211 */         createM5RuleNodeRecursive(child, (ClusNode)node.getChild(i), subset, cnv);
/*  212 */         if (i == 0) { result.m_left = child; }
/*  213 */         else { result.m_right = child; }
/*      */       
/*      */       }  
/*      */   }
/*      */   
/*      */   public static TestM5PruningRuleNode createM5RuleNode(ClusNode node, RowData data, ClusToWekaData cnv) {
/*  219 */     Instances inst = cnv.convertData(data);
/*  220 */     double gstddev = stdDev(cnv.getClassIndex(), inst);
/*  221 */     double gabsdev = absDev(cnv.getClassIndex(), inst);
/*  222 */     TestM5PruningRuleNode result = new TestM5PruningRuleNode(gstddev, gabsdev, null);
/*  223 */     createM5RuleNodeRecursive(result, node, data, cnv);
/*  224 */     return result;
/*      */   }
/*      */   
/*      */   public static void performTest(ClusNode original, ClusNode pruned, double globaldev, ClusAttributeWeights tweights, RowData data) {
/*  228 */     ClusToWekaData cnv = new ClusToWekaData(data.getSchema());
/*  229 */     cnv.setTargetWeights(tweights);
/*  230 */     TestM5PruningRuleNode tree = createM5RuleNode(original, data, cnv);
/*  231 */     if (Math.abs(tree.m_globalDeviation - globaldev) > 1.0E-6D) {
/*  232 */       System.out.println("Global deviation computed by Clus: " + globaldev);
/*  233 */       System.out.println("Global deviation computed by Weka: " + tree.m_globalDeviation);
/*      */     } 
/*      */ 
/*      */     
/*      */     try {
/*  238 */       tree.prune();
/*  239 */       tree.numLeaves(0);
/*  240 */       System.out.println(tree.treeToString(0));
/*  241 */     } catch (Exception e) {
/*  242 */       System.err.println("Exception: " + e.getMessage());
/*  243 */       e.printStackTrace();
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
/*      */   public TestM5PruningRuleNode(double globalDev, double globalAbsDev, TestM5PruningRuleNode parent) {
/*  255 */     this.m_nodeModel = null;
/*  256 */     this.m_right = null;
/*  257 */     this.m_left = null;
/*  258 */     this.m_parent = parent;
/*  259 */     this.m_globalDeviation = globalDev;
/*  260 */     this.m_globalAbsDeviation = globalAbsDev;
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
/*      */   public void buildClassifier(Instances data) throws Exception {
/*  272 */     this.m_rootMeanSquaredError = Double.MAX_VALUE;
/*      */     
/*  274 */     this.m_instances = data;
/*  275 */     this.m_classIndex = this.m_instances.classIndex();
/*  276 */     this.m_numInstances = this.m_instances.numInstances();
/*  277 */     this.m_numAttributes = this.m_instances.numAttributes();
/*  278 */     this.m_nodeModel = null;
/*  279 */     this.m_right = null;
/*  280 */     this.m_left = null;
/*      */     
/*  282 */     if (this.m_numInstances < this.m_splitNum || 
/*  283 */       stdDev(this.m_classIndex, this.m_instances) < this.m_globalDeviation * this.m_devFraction) {
/*      */       
/*  285 */       this.m_isLeaf = true;
/*      */     } else {
/*  287 */       this.m_isLeaf = false;
/*      */     } 
/*      */     
/*  290 */     split();
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
/*      */   public double classifyInstance(Instance inst) throws Exception {
/*  302 */     if (this.m_isLeaf) {
/*  303 */       if (this.m_nodeModel == null) {
/*  304 */         throw new Exception("Classifier has not been built correctly.");
/*      */       }
/*      */       
/*  307 */       return this.m_nodeModel.classifyInstance(inst);
/*      */     } 
/*      */     
/*  310 */     if (inst.value(this.m_splitAtt) > this.m_splitValue) {
/*  311 */       return this.m_left.classifyInstance(inst);
/*      */     }
/*  313 */     return this.m_right.classifyInstance(inst);
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
/*      */   protected static double smoothingOriginal(double n, double pred, double supportPred) throws Exception {
/*  332 */     double smoothed = (n * pred + 15.0D * supportPred) / (n + 15.0D);
/*      */ 
/*      */ 
/*      */     
/*  336 */     return smoothed;
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
/*      */   public void split() throws Exception {
/*  351 */     if (!this.m_isLeaf) {
/*      */       SplitEvaluate splitEvaluate;
/*  353 */       YongSplitInfo yongSplitInfo1 = new YongSplitInfo(0, this.m_numInstances - 1, -1);
/*  354 */       YongSplitInfo yongSplitInfo2 = new YongSplitInfo(0, this.m_numInstances - 1, -1);
/*      */       
/*      */       int i;
/*  357 */       for (i = 0; i < this.m_numAttributes; i++) {
/*  358 */         if (i != this.m_classIndex) {
/*      */ 
/*      */           
/*  361 */           this.m_instances.sort(i);
/*  362 */           yongSplitInfo2.attrSplit(i, this.m_instances);
/*      */           
/*  364 */           if (Math.abs(yongSplitInfo2.maxImpurity() - yongSplitInfo1
/*  365 */               .maxImpurity()) > 1.0E-6D && yongSplitInfo2
/*  366 */             .maxImpurity() > yongSplitInfo1
/*  367 */             .maxImpurity() + 1.0E-6D) {
/*  368 */             splitEvaluate = yongSplitInfo2.copy();
/*      */           }
/*      */         } 
/*      */       } 
/*      */ 
/*      */       
/*  374 */       if (splitEvaluate.splitAttr() < 0 || splitEvaluate.position() < 1 || splitEvaluate
/*  375 */         .position() > this.m_numInstances - 1) {
/*  376 */         this.m_isLeaf = true;
/*      */       } else {
/*  378 */         this.m_splitAtt = splitEvaluate.splitAttr();
/*  379 */         this.m_splitValue = splitEvaluate.splitValue();
/*  380 */         Instances leftSubset = new Instances(this.m_instances, this.m_numInstances);
/*  381 */         Instances rightSubset = new Instances(this.m_instances, this.m_numInstances);
/*      */         
/*  383 */         for (i = 0; i < this.m_numInstances; i++) {
/*  384 */           if (this.m_instances.instance(i).value(this.m_splitAtt) <= this.m_splitValue) {
/*  385 */             leftSubset.add(this.m_instances.instance(i));
/*      */           } else {
/*  387 */             rightSubset.add(this.m_instances.instance(i));
/*      */           } 
/*      */         } 
/*      */         
/*  391 */         leftSubset.compactify();
/*  392 */         rightSubset.compactify();
/*      */ 
/*      */         
/*  395 */         this.m_left = new TestM5PruningRuleNode(this.m_globalDeviation, this.m_globalAbsDeviation, this);
/*  396 */         this.m_left.setMinNumInstances(this.m_splitNum);
/*  397 */         this.m_left.setRegressionTree(this.m_regressionTree);
/*  398 */         this.m_left.setSaveInstances(this.m_saveInstances);
/*  399 */         this.m_left.buildClassifier(leftSubset);
/*      */         
/*  401 */         this.m_right = new TestM5PruningRuleNode(this.m_globalDeviation, this.m_globalAbsDeviation, this);
/*  402 */         this.m_right.setMinNumInstances(this.m_splitNum);
/*  403 */         this.m_right.setRegressionTree(this.m_regressionTree);
/*  404 */         this.m_right.setSaveInstances(this.m_saveInstances);
/*  405 */         this.m_right.buildClassifier(rightSubset);
/*      */ 
/*      */ 
/*      */         
/*  409 */         if (!this.m_regressionTree) {
/*  410 */           boolean[] attsBelow = attsTestedBelow();
/*  411 */           attsBelow[this.m_classIndex] = true;
/*  412 */           int count = 0;
/*      */           int j;
/*  414 */           for (j = 0; j < this.m_numAttributes; j++) {
/*  415 */             if (attsBelow[j]) {
/*  416 */               count++;
/*      */             }
/*      */           } 
/*      */           
/*  420 */           int[] indices = new int[count];
/*      */           
/*  422 */           count = 0;
/*      */           
/*  424 */           for (j = 0; j < this.m_numAttributes; j++) {
/*  425 */             if (attsBelow[j] && j != this.m_classIndex) {
/*  426 */               indices[count++] = j;
/*      */             }
/*      */           } 
/*      */           
/*  430 */           indices[count] = this.m_classIndex;
/*  431 */           this.m_indices = indices;
/*      */         } else {
/*  433 */           this.m_indices = new int[1];
/*  434 */           this.m_indices[0] = this.m_classIndex;
/*  435 */           this.m_numParameters = 1;
/*      */         } 
/*      */       } 
/*      */     } 
/*      */     
/*  440 */     if (this.m_isLeaf) {
/*  441 */       int[] indices = new int[1];
/*  442 */       indices[0] = this.m_classIndex;
/*  443 */       this.m_indices = indices;
/*  444 */       this.m_numParameters = 1;
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
/*      */   private void buildLinearModel(int[] indices) throws Exception {
/*  461 */     Instances reducedInst = new Instances(this.m_instances);
/*  462 */     Remove attributeFilter = new Remove();
/*      */     
/*  464 */     attributeFilter.setInvertSelection(true);
/*  465 */     attributeFilter.setAttributeIndicesArray(indices);
/*  466 */     attributeFilter.setInputFormat(reducedInst);
/*      */     
/*  468 */     reducedInst = Filter.useFilter(reducedInst, (Filter)attributeFilter);
/*      */ 
/*      */ 
/*      */     
/*  472 */     LinearRegression temp = new LinearRegression();
/*  473 */     temp.buildClassifier(reducedInst);
/*      */     
/*  475 */     double[] lmCoeffs = temp.coefficients();
/*  476 */     double[] coeffs = new double[this.m_instances.numAttributes()];
/*      */     
/*  478 */     for (int i = 0; i < lmCoeffs.length - 1; i++) {
/*  479 */       if (indices[i] != this.m_classIndex) {
/*  480 */         coeffs[indices[i]] = lmCoeffs[i];
/*      */       }
/*      */     } 
/*  483 */     this.m_nodeModel = new PreConstructedLinearModel(coeffs, lmCoeffs[lmCoeffs.length - 1]);
/*  484 */     this.m_nodeModel.buildClassifier(this.m_instances);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean[] attsTestedAbove() {
/*  494 */     boolean[] atts = new boolean[this.m_numAttributes];
/*  495 */     boolean[] attsAbove = null;
/*      */     
/*  497 */     if (this.m_parent != null) {
/*  498 */       attsAbove = this.m_parent.attsTestedAbove();
/*      */     }
/*      */     
/*  501 */     if (attsAbove != null) {
/*  502 */       for (int i = 0; i < this.m_numAttributes; i++) {
/*  503 */         atts[i] = attsAbove[i];
/*      */       }
/*      */     }
/*      */     
/*  507 */     atts[this.m_splitAtt] = true;
/*  508 */     return atts;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean[] attsTestedBelow() {
/*  518 */     boolean[] attsBelow = new boolean[this.m_numAttributes];
/*  519 */     boolean[] attsBelowLeft = null;
/*  520 */     boolean[] attsBelowRight = null;
/*      */     
/*  522 */     if (this.m_right != null) {
/*  523 */       attsBelowRight = this.m_right.attsTestedBelow();
/*      */     }
/*      */     
/*  526 */     if (this.m_left != null) {
/*  527 */       attsBelowLeft = this.m_left.attsTestedBelow();
/*      */     }
/*      */     
/*  530 */     for (int i = 0; i < this.m_numAttributes; i++) {
/*  531 */       if (attsBelowLeft != null) {
/*  532 */         attsBelow[i] = (attsBelow[i] || attsBelowLeft[i]);
/*      */       }
/*      */       
/*  535 */       if (attsBelowRight != null) {
/*  536 */         attsBelow[i] = (attsBelow[i] || attsBelowRight[i]);
/*      */       }
/*      */     } 
/*      */     
/*  540 */     if (!this.m_isLeaf) {
/*  541 */       attsBelow[this.m_splitAtt] = true;
/*      */     }
/*  543 */     return attsBelow;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int numLeaves(int leafCounter) {
/*  553 */     if (!this.m_isLeaf) {
/*      */       
/*  555 */       this.m_leafModelNum = 0;
/*      */       
/*  557 */       if (this.m_left != null) {
/*  558 */         leafCounter = this.m_left.numLeaves(leafCounter);
/*      */       }
/*      */       
/*  561 */       if (this.m_right != null) {
/*  562 */         leafCounter = this.m_right.numLeaves(leafCounter);
/*      */       }
/*      */     }
/*      */     else {
/*      */       
/*  567 */       this.m_leafModelNum = ++leafCounter;
/*      */     } 
/*  569 */     return leafCounter;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String toString() {
/*  576 */     return printNodeLinearModel();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String printNodeLinearModel() {
/*  583 */     return this.m_nodeModel.toString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String printLeafModels() {
/*  590 */     StringBuffer text = new StringBuffer();
/*      */     
/*  592 */     if (this.m_isLeaf) {
/*  593 */       text.append("\nLM num: " + this.m_leafModelNum);
/*  594 */       text.append(this.m_nodeModel.toString());
/*  595 */       text.append("\n");
/*      */     } else {
/*  597 */       text.append(this.m_left.printLeafModels());
/*  598 */       text.append(this.m_right.printLeafModels());
/*      */     } 
/*  600 */     return text.toString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String nodeToString() {
/*  609 */     StringBuffer text = new StringBuffer();
/*      */     
/*  611 */     System.out.println("In to string");
/*  612 */     text.append("Node:\n\tnum inst: " + this.m_numInstances);
/*      */     
/*  614 */     if (this.m_isLeaf) {
/*  615 */       text.append("\n\tleaf");
/*      */     } else {
/*  617 */       text.append("\tnode");
/*      */     } 
/*      */     
/*  620 */     text.append("\n\tSplit att: " + this.m_instances.attribute(this.m_splitAtt).name());
/*  621 */     text.append("\n\tSplit val: " + Utils.doubleToString(this.m_splitValue, 1, 3));
/*  622 */     text.append("\n\tLM num: " + this.m_leafModelNum);
/*  623 */     text.append("\n\tLinear model\n" + this.m_nodeModel.toString());
/*  624 */     text.append("\n\n");
/*      */     
/*  626 */     if (this.m_left != null) {
/*  627 */       text.append(this.m_left.nodeToString());
/*      */     }
/*      */     
/*  630 */     if (this.m_right != null) {
/*  631 */       text.append(this.m_right.nodeToString());
/*      */     }
/*      */     
/*  634 */     return text.toString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String treeToString(int level) {
/*  645 */     StringBuffer text = new StringBuffer();
/*      */     
/*  647 */     if (!this.m_isLeaf) {
/*  648 */       text.append("\n");
/*      */       int i;
/*  650 */       for (i = 1; i <= level; i++) {
/*  651 */         text.append("|   ");
/*      */       }
/*      */       
/*  654 */       if (this.m_instances.attribute(this.m_splitAtt).name().charAt(0) != '[') {
/*  655 */         text.append(this.m_instances.attribute(this.m_splitAtt).name() + " <= " + 
/*  656 */             Utils.doubleToString(this.m_splitValue, 1, 3) + " : ");
/*      */       } else {
/*  658 */         text.append(this.m_instances.attribute(this.m_splitAtt).name() + " false : ");
/*      */       } 
/*      */       
/*  661 */       if (this.m_left != null) {
/*  662 */         text.append(this.m_left.treeToString(level + 1));
/*      */       } else {
/*  664 */         text.append("NULL\n");
/*      */       } 
/*      */       
/*  667 */       for (i = 1; i <= level; i++) {
/*  668 */         text.append("|   ");
/*      */       }
/*      */       
/*  671 */       if (this.m_instances.attribute(this.m_splitAtt).name().charAt(0) != '[') {
/*  672 */         text.append(this.m_instances.attribute(this.m_splitAtt).name() + " >  " + 
/*  673 */             Utils.doubleToString(this.m_splitValue, 1, 3) + " : ");
/*      */       } else {
/*  675 */         text.append(this.m_instances.attribute(this.m_splitAtt).name() + " true : ");
/*      */       } 
/*      */       
/*  678 */       if (this.m_right != null) {
/*  679 */         text.append(this.m_right.treeToString(level + 1));
/*      */       } else {
/*  681 */         text.append("NULL\n");
/*      */       } 
/*      */     } else {
/*  684 */       text.append("LM" + this.m_leafModelNum);
/*      */       
/*  686 */       if (this.m_globalDeviation > 0.0D) {
/*  687 */         text
/*  688 */           .append(" (" + this.m_numInstances + "/" + 
/*  689 */             Utils.doubleToString(100.0D * this.m_rootMeanSquaredError / this.m_globalAbsDeviation, 1, 3) + "%)\n");
/*      */       }
/*      */       else {
/*      */         
/*  693 */         text.append(" (" + this.m_numInstances + ")\n");
/*      */       } 
/*      */     } 
/*  696 */     return text.toString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void installLinearModels() throws Exception {
/*  707 */     if (this.m_isLeaf) {
/*  708 */       buildLinearModel(this.m_indices);
/*      */     } else {
/*  710 */       if (this.m_left != null) {
/*  711 */         this.m_left.installLinearModels();
/*      */       }
/*      */       
/*  714 */       if (this.m_right != null) {
/*  715 */         this.m_right.installLinearModels();
/*      */       }
/*  717 */       buildLinearModel(this.m_indices);
/*      */     } 
/*  719 */     Evaluation nodeModelEval = new Evaluation(this.m_instances);
/*  720 */     nodeModelEval.evaluateModel((Classifier)this.m_nodeModel, this.m_instances);
/*  721 */     this.m_rootMeanSquaredError = nodeModelEval.rootMeanSquaredError();
/*      */     
/*  723 */     if (!this.m_saveInstances) {
/*  724 */       this.m_instances = new Instances(this.m_instances, 0);
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   public void installSmoothedModels() throws Exception {
/*  730 */     if (this.m_isLeaf) {
/*  731 */       double[] coefficients = new double[this.m_numAttributes];
/*      */       
/*  733 */       double[] coeffsUsedByLinearModel = this.m_nodeModel.coefficients();
/*  734 */       TestM5PruningRuleNode current = this;
/*      */ 
/*      */       
/*  737 */       for (int i = 0; i < coeffsUsedByLinearModel.length; i++) {
/*  738 */         if (i != this.m_classIndex) {
/*  739 */           coefficients[i] = coeffsUsedByLinearModel[i];
/*      */         }
/*      */       } 
/*      */       
/*  743 */       double intercept = this.m_nodeModel.intercept();
/*      */       
/*      */       while (true) {
/*  746 */         if (current.m_parent != null) {
/*  747 */           double n = current.m_numInstances;
/*      */           int j;
/*  749 */           for (j = 0; j < coefficients.length; j++) {
/*  750 */             coefficients[j] = coefficients[j] * n / (n + 15.0D);
/*      */           }
/*  752 */           intercept = intercept * n / (n + 15.0D);
/*      */ 
/*      */           
/*  755 */           coeffsUsedByLinearModel = current.m_parent.getModel().coefficients();
/*  756 */           for (j = 0; j < coeffsUsedByLinearModel.length; j++) {
/*  757 */             if (j != this.m_classIndex)
/*      */             {
/*  759 */               coefficients[j] = coefficients[j] + 15.0D * coeffsUsedByLinearModel[j] / (n + 15.0D);
/*      */             }
/*      */           } 
/*      */ 
/*      */ 
/*      */           
/*  765 */           intercept += 15.0D * current.m_parent
/*      */             
/*  767 */             .getModel().intercept() / (n + 15.0D);
/*      */           
/*  769 */           current = current.m_parent;
/*      */         } 
/*  771 */         if (current.m_parent == null)
/*  772 */         { this.m_nodeModel = new PreConstructedLinearModel(coefficients, intercept);
/*      */           
/*  774 */           this.m_nodeModel.buildClassifier(this.m_instances); break; } 
/*      */       } 
/*  776 */     }  if (this.m_left != null) {
/*  777 */       this.m_left.installSmoothedModels();
/*      */     }
/*  779 */     if (this.m_right != null) {
/*  780 */       this.m_right.installSmoothedModels();
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void prune() throws Exception {
/*  790 */     Evaluation nodeModelEval = null;
/*      */     
/*  792 */     if (this.m_isLeaf) {
/*  793 */       buildLinearModel(this.m_indices);
/*  794 */       nodeModelEval = new Evaluation(this.m_instances);
/*      */ 
/*      */ 
/*      */       
/*  798 */       nodeModelEval.evaluateModel((Classifier)this.m_nodeModel, this.m_instances);
/*      */       
/*  800 */       this.m_rootMeanSquaredError = nodeModelEval.rootMeanSquaredError();
/*      */     }
/*      */     else {
/*      */       
/*  804 */       if (this.m_left != null) {
/*  805 */         this.m_left.prune();
/*      */       }
/*      */       
/*  808 */       if (this.m_right != null) {
/*  809 */         this.m_right.prune();
/*      */       }
/*      */       
/*  812 */       buildLinearModel(this.m_indices);
/*  813 */       nodeModelEval = new Evaluation(this.m_instances);
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  818 */       nodeModelEval.evaluateModel((Classifier)this.m_nodeModel, this.m_instances);
/*      */       
/*  820 */       double rmsModel = nodeModelEval.rootMeanSquaredError();
/*      */       
/*  822 */       double adjustedErrorModel = rmsModel * pruningFactor(this.m_numInstances, this.m_nodeModel
/*  823 */           .numParameters() + 1);
/*      */ 
/*      */       
/*  826 */       Evaluation nodeEval = new Evaluation(this.m_instances);
/*      */ 
/*      */       
/*  829 */       int l_params = 0, r_params = 0;
/*      */       
/*  831 */       nodeEval.evaluateModel(this, this.m_instances);
/*      */       
/*  833 */       double rmsSubTree = nodeEval.rootMeanSquaredError();
/*      */       
/*  835 */       if (this.m_left != null) {
/*  836 */         l_params = this.m_left.numParameters();
/*      */       }
/*      */       
/*  839 */       if (this.m_right != null) {
/*  840 */         r_params = this.m_right.numParameters();
/*      */       }
/*      */ 
/*      */       
/*  844 */       double adjustedErrorNode = rmsSubTree * pruningFactor(this.m_numInstances, l_params + r_params + 1);
/*      */ 
/*      */       
/*  847 */       System.out.println("mode: " + rmsModel + " tree: " + rmsSubTree);
/*  848 */       System.out.println("modeadj: " + adjustedErrorModel + " treeadj: " + adjustedErrorNode);
/*      */       
/*  850 */       if (adjustedErrorModel <= adjustedErrorNode || adjustedErrorModel < this.m_globalDeviation * 1.0E-5D) {
/*      */ 
/*      */ 
/*      */         
/*  854 */         this.m_isLeaf = true;
/*  855 */         this.m_right = null;
/*  856 */         this.m_left = null;
/*  857 */         this.m_numParameters = this.m_nodeModel.numParameters() + 1;
/*  858 */         this.m_rootMeanSquaredError = rmsModel;
/*      */       } else {
/*  860 */         this.m_numParameters = l_params + r_params + 1;
/*  861 */         this.m_rootMeanSquaredError = rmsSubTree;
/*      */       } 
/*      */     } 
/*      */     
/*  865 */     if (!this.m_saveInstances) {
/*  866 */       this.m_instances = new Instances(this.m_instances, 0);
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
/*      */   private double pruningFactor(int num_instances, int num_params) {
/*  879 */     if (num_instances <= num_params) {
/*  880 */       return 10.0D;
/*      */     }
/*      */     
/*  883 */     return (num_instances + this.m_pruningMultiplier * num_params) / (num_instances - num_params);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void findBestLeaf(double[] maxCoverage, TestM5PruningRuleNode[] bestLeaf) {
/*  894 */     if (!this.m_isLeaf) {
/*  895 */       if (this.m_left != null) {
/*  896 */         this.m_left.findBestLeaf(maxCoverage, bestLeaf);
/*      */       }
/*      */       
/*  899 */       if (this.m_right != null) {
/*  900 */         this.m_right.findBestLeaf(maxCoverage, bestLeaf);
/*      */       }
/*      */     }
/*  903 */     else if (this.m_numInstances > maxCoverage[0]) {
/*  904 */       maxCoverage[0] = this.m_numInstances;
/*  905 */       bestLeaf[0] = this;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void returnLeaves(FastVector[] v) {
/*  916 */     if (this.m_isLeaf) {
/*  917 */       v[0].addElement(this);
/*      */     } else {
/*  919 */       if (this.m_left != null) {
/*  920 */         this.m_left.returnLeaves(v);
/*      */       }
/*      */       
/*  923 */       if (this.m_right != null) {
/*  924 */         this.m_right.returnLeaves(v);
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public TestM5PruningRuleNode parentNode() {
/*  935 */     return this.m_parent;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public TestM5PruningRuleNode leftNode() {
/*  944 */     return this.m_left;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public TestM5PruningRuleNode rightNode() {
/*  953 */     return this.m_right;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int splitAtt() {
/*  962 */     return this.m_splitAtt;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public double splitVal() {
/*  971 */     return this.m_splitValue;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int numberOfLinearModels() {
/*  980 */     if (this.m_isLeaf) {
/*  981 */       return 1;
/*      */     }
/*  983 */     return this.m_left.numberOfLinearModels() + this.m_right.numberOfLinearModels();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isLeaf() {
/*  993 */     return this.m_isLeaf;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected double rootMeanSquaredError() {
/* 1002 */     return this.m_rootMeanSquaredError;
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
/*      */   public PreConstructedLinearModel getModel() {
/* 1014 */     return this.m_nodeModel;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getNumInstances() {
/* 1023 */     return this.m_numInstances;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private int numParameters() {
/* 1032 */     return this.m_numParameters;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getRegressionTree() {
/* 1042 */     return this.m_regressionTree;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setMinNumInstances(double minNum) {
/* 1051 */     this.m_splitNum = minNum;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public double getMinNumInstances() {
/* 1060 */     return this.m_splitNum;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setRegressionTree(boolean newregressionTree) {
/* 1070 */     this.m_regressionTree = newregressionTree;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void printAllModels() {
/* 1077 */     if (this.m_isLeaf) {
/* 1078 */       System.out.println(this.m_nodeModel.toString());
/*      */     } else {
/* 1080 */       System.out.println(this.m_nodeModel.toString());
/* 1081 */       this.m_left.printAllModels();
/* 1082 */       this.m_right.printAllModels();
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected int assignIDs(int lastID) {
/* 1093 */     int currLastID = lastID + 1;
/* 1094 */     this.m_id = currLastID;
/*      */     
/* 1096 */     if (this.m_left != null) {
/* 1097 */       currLastID = this.m_left.assignIDs(currLastID);
/*      */     }
/*      */     
/* 1100 */     if (this.m_right != null) {
/* 1101 */       currLastID = this.m_right.assignIDs(currLastID);
/*      */     }
/* 1103 */     return currLastID;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void graph(StringBuffer text) {
/* 1113 */     assignIDs(-1);
/* 1114 */     graphTree(text);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void graphTree(StringBuffer text) {
/* 1123 */     text.append("N" + this.m_id + (this.m_isLeaf ? (" [label=\"LM " + this.m_leafModelNum) : (" [label=\"" + this.m_instances
/*      */ 
/*      */         
/* 1126 */         .attribute(this.m_splitAtt).name())) + (this.m_isLeaf ? (" (" + ((this.m_globalDeviation > 0.0D) ? (this.m_numInstances + "/" + 
/*      */ 
/*      */ 
/*      */         
/* 1130 */         Utils.doubleToString(100.0D * this.m_rootMeanSquaredError / this.m_globalAbsDeviation, 1, 3) + "%)") : (this.m_numInstances + ")")) + "\" shape=box style=filled ") : "\"") + (this.m_saveInstances ? ("data=\n" + this.m_instances + "\n,\n") : "") + "]\n");
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
/* 1143 */     if (this.m_left != null) {
/* 1144 */       text.append("N" + this.m_id + "->N" + this.m_left.m_id + " [label=\"<=" + 
/* 1145 */           Utils.doubleToString(this.m_splitValue, 1, 3) + "\"]\n");
/*      */       
/* 1147 */       this.m_left.graphTree(text);
/*      */     } 
/*      */     
/* 1150 */     if (this.m_right != null) {
/* 1151 */       text.append("N" + this.m_id + "->N" + this.m_right.m_id + " [label=\">" + 
/* 1152 */           Utils.doubleToString(this.m_splitValue, 1, 3) + "\"]\n");
/*      */       
/* 1154 */       this.m_right.graphTree(text);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void setSaveInstances(boolean save) {
/* 1165 */     this.m_saveInstances = save;
/*      */   }
/*      */   protected static final double stdDev(int attr, Instances inst) {
/*      */     double sd;
/* 1169 */     int count = 0;
/* 1170 */     double sum = 0.0D, sqrSum = 0.0D;
/*      */     
/* 1172 */     for (int i = 0; i <= inst.numInstances() - 1; i++) {
/* 1173 */       count++;
/* 1174 */       double value = inst.instance(i).value(attr);
/* 1175 */       sum += value;
/* 1176 */       sqrSum += value * value;
/*      */     } 
/*      */     
/* 1179 */     if (count > 1) {
/* 1180 */       double va = (sqrSum - sum * sum / count) / count;
/* 1181 */       va = Math.abs(va);
/* 1182 */       sd = Math.sqrt(va);
/*      */     } else {
/* 1184 */       sd = 0.0D;
/*      */     } 
/*      */     
/* 1187 */     return sd;
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
/*      */   protected static final double absDev(int attr, Instances inst) {
/* 1199 */     double absDev, average = 0.0D, absdiff = 0.0D;
/*      */     int i;
/* 1201 */     for (i = 0; i <= inst.numInstances() - 1; i++) {
/* 1202 */       average += inst.instance(i).value(attr);
/*      */     }
/* 1204 */     if (inst.numInstances() > 1) {
/* 1205 */       average /= inst.numInstances();
/* 1206 */       for (i = 0; i <= inst.numInstances() - 1; i++) {
/* 1207 */         absdiff += Math.abs(inst.instance(i).value(attr) - average);
/*      */       }
/* 1209 */       absDev = absdiff / inst.numInstances();
/*      */     } else {
/* 1211 */       absDev = 0.0D;
/*      */     } 
/*      */     
/* 1214 */     return absDev;
/*      */   }
/*      */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\weka\TestM5PruningRuleNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */