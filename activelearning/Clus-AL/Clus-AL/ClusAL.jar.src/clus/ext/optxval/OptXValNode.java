/*     */ package clus.ext.optxval;
/*     */ 
/*     */ import clus.algo.tdidt.ClusNode;
/*     */ import clus.util.ClusFormat;
/*     */ import java.io.PrintWriter;
/*     */ import java.util.Arrays;
/*     */ import jeans.tree.MyNode;
/*     */ import jeans.tree.Node;
/*     */ import jeans.util.array.MyIntArray;
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
/*     */ 
/*     */ 
/*     */ public class OptXValNode
/*     */   extends MyNode
/*     */ {
/*     */   public static final long serialVersionUID = 1L;
/*     */   protected int[] m_Folds;
/*     */   protected ClusNode[] m_Nodes;
/*     */   public long m_Time;
/*     */   
/*     */   public void init(int[] folds) {
/*  45 */     int mnb = folds.length;
/*  46 */     this.m_Nodes = new ClusNode[mnb];
/*  47 */     this.m_Folds = new int[mnb];
/*  48 */     System.arraycopy(folds, 0, this.m_Folds, 0, mnb);
/*     */   }
/*     */ 
/*     */   
/*     */   public void oneFold(int fold, ClusNode onode) {}
/*     */   
/*     */   public ClusNode[] getNodes() {
/*  55 */     return this.m_Nodes;
/*     */   }
/*     */   
/*     */   public final void printTree(PrintWriter writer, String prefix) {
/*  59 */     int lvc = 0;
/*  60 */     for (int i = 0; i < this.m_Folds.length; i++) {
/*  61 */       ClusNode node = this.m_Nodes[i];
/*  62 */       if (!node.hasBestTest()) {
/*  63 */         if (lvc != 0) writer.print(", "); 
/*  64 */         writer.print(this.m_Folds[i] + ": ");
/*  65 */         writer.print(ClusFormat.ONE_AFTER_DOT.format(node.getTotWeight()));
/*  66 */         lvc++;
/*     */       } 
/*     */     } 
/*  69 */     if (lvc > 0) {
/*  70 */       writer.print(" ");
/*  71 */       showPath(getPath(), writer);
/*     */     } 
/*  73 */     int nb = getNbChildren();
/*  74 */     if (nb > 0) {
/*  75 */       if (lvc > 0) {
/*  76 */         writer.println();
/*  77 */         writer.print(prefix);
/*     */       } 
/*     */     } else {
/*  80 */       writer.println();
/*     */     } 
/*  82 */     for (int j = 0; j < nb; j++) {
/*  83 */       OptXValSplit split = (OptXValSplit)getChild(j);
/*  84 */       if (j != 0) {
/*  85 */         writer.println(prefix + "|  ");
/*  86 */         writer.print(prefix);
/*     */       } 
/*  88 */       writer.print("G" + j + " ");
/*  89 */       writer.print(MyIntArray.print(split.getFolds()));
/*  90 */       writer.print(" - ");
/*  91 */       writer.print(split.getTest().getString());
/*  92 */       writer.println();
/*  93 */       int mb = split.getNbChildren();
/*  94 */       String gfix = (j != nb - 1) ? "|  " : "   ";
/*  95 */       for (int k = 0; k < mb; k++) {
/*  96 */         OptXValNode node = (OptXValNode)split.getChild(k);
/*  97 */         String suffix = (k != mb - 1) ? "|      " : "       ";
/*  98 */         if (k == 0) { writer.print(prefix + gfix + "+-yes: "); }
/*     */         else
/* 100 */         { writer.println(prefix + gfix + "|");
/* 101 */           writer.print(prefix + gfix + "+-no:  "); }
/*     */         
/* 103 */         node.printTree(writer, prefix + gfix + suffix);
/*     */       } 
/*     */     } 
/* 106 */     writer.flush();
/*     */   }
/*     */   
/*     */   public final ClusNode getTree(int fold) {
/* 110 */     int idx = Arrays.binarySearch(this.m_Folds, fold);
/* 111 */     ClusNode node = this.m_Nodes[idx];
/* 112 */     if (node.hasBestTest() && node.atBottomLevel()) {
/* 113 */       OptXValSplit split = null;
/* 114 */       int nb = getNbChildren();
/* 115 */       for (int i = 0; i < nb; i++) {
/* 116 */         OptXValSplit msplit = (OptXValSplit)getChild(i);
/* 117 */         if (msplit.contains(fold)) {
/* 118 */           split = msplit;
/*     */           break;
/*     */         } 
/*     */       } 
/* 122 */       int arity = node.updateArity();
/* 123 */       for (int j = 0; j < arity; j++) {
/* 124 */         OptXValNode subnode = (OptXValNode)split.getChild(j);
/* 125 */         node.setChild((Node)subnode.getTree(fold), j);
/*     */       } 
/*     */     } 
/* 128 */     return node;
/*     */   }
/*     */   
/*     */   public final void setNodeIndex(int idx, ClusNode node) {
/* 132 */     this.m_Nodes[idx] = node;
/*     */   }
/*     */   
/*     */   public final void setNode(int fold, ClusNode node) {
/* 136 */     int idx = Arrays.binarySearch(this.m_Folds, fold);
/* 137 */     this.m_Nodes[idx] = node;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\ext\optxval\OptXValNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */