/*     */ package clus.weka;
/*     */ 
/*     */ import clus.algo.tdidt.ClusNode;
/*     */ import clus.data.rows.RowData;
/*     */ import clus.model.test.NodeTest;
/*     */ import clus.model.test.NumericTest;
/*     */ import weka.classifiers.trees.j48.BinC45ModelSelection;
/*     */ import weka.classifiers.trees.j48.BinC45Split;
/*     */ import weka.classifiers.trees.j48.C45PruneableClassifierTree;
/*     */ import weka.classifiers.trees.j48.ClassifierSplitModel;
/*     */ import weka.classifiers.trees.j48.ClassifierTree;
/*     */ import weka.classifiers.trees.j48.Distribution;
/*     */ import weka.classifiers.trees.j48.ModelSelection;
/*     */ import weka.core.Instance;
/*     */ import weka.core.Instances;
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
/*     */ public class TestC45PruningRuleNode
/*     */   extends C45PruneableClassifierTree
/*     */ {
/*     */   public static final long serialVersionUID = 1L;
/*     */   
/*     */   public TestC45PruningRuleNode(Instances all) throws Exception {
/*  37 */     super((ModelSelection)new BinC45ModelSelection(2, all), true, 0.25F, false, true);
/*     */   }
/*     */   
/*     */   public TestC45PruningRuleNode(ModelSelection sel) throws Exception {
/*  41 */     super(sel, true, 0.25F, false, true);
/*     */   }
/*     */   
/*     */   public static void createC45RuleNodeRecursive(TestC45PruningRuleNode result, ClusNode node, RowData data, ClusToWekaData cnv) throws Exception {
/*  45 */     result.m_train = cnv.convertData(data);
/*  46 */     result.m_isEmpty = (data.getNbRows() == 0);
/*  47 */     result.m_isLeaf = node.atBottomLevel();
/*  48 */     NodeTest tst = node.getTest();
/*  49 */     int idx = (tst == null) ? -1 : cnv.getIndex(tst.getType().getName());
/*  50 */     MyBinC45Split split = new MyBinC45Split(idx, 2, result.m_train.sumOfWeights());
/*  51 */     result.m_localModel = (ClassifierSplitModel)split;
/*  52 */     Distribution distr = null;
/*  53 */     if (tst != null) {
/*  54 */       distr = new Distribution(2, result.m_train.numClasses());
/*  55 */       if (tst instanceof NumericTest) split.setSplitPoint(((NumericTest)tst).getBound()); 
/*     */     } else {
/*  57 */       distr = new Distribution(result.m_train);
/*     */     } 
/*  59 */     split.setDistribution(distr);
/*  60 */     if (node.getNbChildren() != 0) {
/*  61 */       result.m_sons = new ClassifierTree[2];
/*  62 */       for (int i = 0; i < 2; i++) {
/*  63 */         TestC45PruningRuleNode child = new TestC45PruningRuleNode(result.m_toSelectModel);
/*  64 */         RowData subset = data.applyWeighted(tst, i);
/*  65 */         Instances subset_inst = cnv.convertData(subset);
/*  66 */         for (int j = 0; j < subset_inst.numInstances(); j++) {
/*  67 */           Instance inst = subset_inst.instance(j);
/*  68 */           distr.add(i, inst);
/*     */         } 
/*  70 */         createC45RuleNodeRecursive(child, (ClusNode)node.getChild(i), subset, cnv);
/*  71 */         result.m_sons[i] = (ClassifierTree)child;
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public static TestC45PruningRuleNode createC45RuleNode(ClusNode node, RowData data, ClusToWekaData cnv) throws Exception {
/*  77 */     Instances all = cnv.convertData(data);
/*  78 */     TestC45PruningRuleNode result = new TestC45PruningRuleNode(all);
/*  79 */     createC45RuleNodeRecursive(result, node, data, cnv);
/*  80 */     return result;
/*     */   }
/*     */   
/*     */   public static void performTest(ClusNode original, ClusNode pruned, RowData data) {
/*  84 */     ClusToWekaData cnv = new ClusToWekaData(data.getSchema());
/*     */     try {
/*  86 */       TestC45PruningRuleNode tree = createC45RuleNode(original, data, cnv);
/*  87 */       System.out.println("Original tree:");
/*  88 */       System.out.println(tree.toString());
/*  89 */       System.out.println("***");
/*  90 */       tree.collapse();
/*  91 */       tree.prune();
/*  92 */       System.out.println("Resulting tree:");
/*  93 */       System.out.println(tree.toString());
/*  94 */       System.out.println("***");
/*  95 */     } catch (Exception e) {
/*  96 */       System.err.println("Exception: " + e.getMessage());
/*  97 */       e.printStackTrace();
/*     */     } 
/*     */   }
/*     */   
/*     */   public static class MyBinC45Split
/*     */     extends BinC45Split {
/*     */     public static final long serialVersionUID = 1L;
/*     */     
/*     */     public MyBinC45Split(int attIndex, int minNoObj, double sumOfWeights) {
/* 106 */       super(attIndex, minNoObj, sumOfWeights);
/*     */     }
/*     */     
/*     */     public void setDistribution(Distribution d) {
/* 110 */       this.m_distribution = d;
/*     */     }
/*     */     
/*     */     public void setSplitPoint(double v) {}
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\weka\TestC45PruningRuleNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */