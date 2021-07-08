/*     */ package clus.ext.optxval;
/*     */ 
/*     */ import clus.algo.tdidt.ClusNode;
/*     */ import jeans.tree.MyNode;
/*     */ import jeans.tree.Node;
/*     */ import jeans.util.MyArray;
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
/*     */ public class OptXValBinTree
/*     */   extends MyNode
/*     */ {
/*     */   public static final long serialVersionUID = 1L;
/*     */   protected int m_NbTests;
/*     */   protected int m_NbFoldTests;
/*     */   protected long m_Time;
/*     */   
/*     */   public double[] getFIs() {
/*  40 */     double[] arr = new double[getMaxLeafDepth()];
/*  41 */     getFIs(arr, 0);
/*  42 */     return arr;
/*     */   }
/*     */   
/*     */   private void getFIs(double[] arr, int depth) {
/*  46 */     if (this.m_NbTests > 0) arr[depth] = arr[depth] + this.m_NbFoldTests / this.m_NbTests; 
/*  47 */     for (int j = 0; j < getNbChildren(); j++) {
/*  48 */       OptXValBinTree tree = (OptXValBinTree)getChild(j);
/*  49 */       tree.getFIs(arr, depth + 1);
/*     */     } 
/*     */   }
/*     */   
/*     */   public double[] getNodes() {
/*  54 */     double[] arr = new double[getMaxLeafDepth()];
/*  55 */     getNodes(arr, 0);
/*  56 */     return arr;
/*     */   }
/*     */   
/*     */   private void getNodes(double[] arr, int depth) {
/*  60 */     if (this.m_NbTests > 0) arr[depth] = arr[depth] + 1.0D; 
/*  61 */     for (int j = 0; j < getNbChildren(); j++) {
/*  62 */       OptXValBinTree tree = (OptXValBinTree)getChild(j);
/*  63 */       tree.getNodes(arr, depth + 1);
/*     */     } 
/*     */   }
/*     */   
/*     */   public double[] getTimes() {
/*  68 */     double[] arr = new double[getMaxLeafDepth()];
/*  69 */     getTimes(arr, 0);
/*  70 */     return arr;
/*     */   }
/*     */   
/*     */   private void getTimes(double[] arr, int depth) {
/*  74 */     arr[depth] = arr[depth] + this.m_Time;
/*  75 */     for (int j = 0; j < getNbChildren(); j++) {
/*  76 */       OptXValBinTree tree = (OptXValBinTree)getChild(j);
/*  77 */       tree.getTimes(arr, depth + 1);
/*     */     } 
/*     */   }
/*     */   
/*     */   public static void processs(MyArray nodes, MyArray left, MyArray right, OptXValBinTree tree) {
/*  82 */     for (int i = 0; i < nodes.size(); i++) {
/*  83 */       Object obj = nodes.elementAt(i);
/*  84 */       if (obj instanceof ClusNode) {
/*  85 */         ClusNode node = (ClusNode)obj;
/*  86 */         tree.m_Time += node.m_Time;
/*  87 */         if (node.getNbChildren() > 0) {
/*  88 */           left.addElement(node.getChild(0));
/*  89 */           right.addElement(node.getChild(1));
/*     */           
/*  91 */           tree.m_NbTests++;
/*  92 */           tree.m_NbFoldTests++;
/*     */         } 
/*     */       } else {
/*  95 */         OptXValNode node = (OptXValNode)obj;
/*  96 */         tree.m_Time += node.m_Time;
/*  97 */         ClusNode[] cnodes = node.getNodes(); int j;
/*  98 */         for (j = 0; j < cnodes.length; j++) {
/*  99 */           tree.m_Time += (cnodes[j]).m_Time;
/* 100 */           if (cnodes[j].getNbChildren() > 0) {
/* 101 */             Object n1 = cnodes[j].getChild(0);
/* 102 */             Object n2 = cnodes[j].getChild(1);
/* 103 */             if (n1 != null) left.addElement(n1); 
/* 104 */             if (n2 != null) right.addElement(n2); 
/*     */           } 
/*     */         } 
/* 107 */         for (j = 0; j < node.getNbChildren(); j++) {
/* 108 */           OptXValSplit split = (OptXValSplit)node.getChild(j);
/* 109 */           if (split.getNbChildren() > 0) {
/* 110 */             left.addElement(split.getChild(0));
/* 111 */             right.addElement(split.getChild(1));
/*     */             
/* 113 */             tree.m_NbTests++;
/* 114 */             tree.m_NbFoldTests += (split.getFolds()).length;
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public static OptXValBinTree convertTree(MyArray crnodes) {
/* 122 */     OptXValBinTree tree = new OptXValBinTree();
/* 123 */     if (crnodes.size() != 0) {
/* 124 */       tree.setNbChildren(2);
/* 125 */       MyArray left = new MyArray();
/* 126 */       MyArray right = new MyArray();
/* 127 */       processs(crnodes, left, right, tree);
/* 128 */       tree.setChild((Node)convertTree(left), 0);
/* 129 */       tree.setChild((Node)convertTree(right), 1);
/*     */     } 
/* 131 */     return tree;
/*     */   }
/*     */   
/*     */   public static OptXValBinTree convertTree(Object object) {
/* 135 */     MyArray arr = new MyArray();
/* 136 */     arr.addElement(object);
/* 137 */     return convertTree(arr);
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\ext\optxval\OptXValBinTree.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */