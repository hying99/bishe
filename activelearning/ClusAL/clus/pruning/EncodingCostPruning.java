/*     */ package clus.pruning;
/*     */ 
/*     */ import clus.algo.tdidt.ClusNode;
/*     */ import clus.data.rows.RowData;
/*     */ import clus.heuristic.EncodingCost;
/*     */ import clus.model.test.NodeTest;
/*     */ import clus.util.ClusException;
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
/*     */ public class EncodingCostPruning
/*     */   extends PruneTree
/*     */ {
/*     */   public double m_Ecc;
/*  37 */   public double m_EccGain = 0.0D;
/*     */   public ClusNode m_BestNodeToPrune;
/*     */   public RowData m_Data;
/*     */   public EncodingCost m_EC;
/*     */   
/*     */   public EncodingCostPruning() {
/*  43 */     this.m_EC = new EncodingCost();
/*     */   }
/*     */   
/*     */   public void setTrainingData(RowData data) {
/*  47 */     this.m_Data = data;
/*  48 */     this.m_EC.setAttributes(this.m_Data.getSchema().getDescriptiveAttributes());
/*     */   }
/*     */   
/*     */   public int getNbResults() {
/*  52 */     return 1;
/*     */   }
/*     */   
/*     */   public void prune(ClusNode node) throws ClusException {
/*  56 */     node.numberCompleteTree();
/*  57 */     int totalNbNodes = node.getTotalTreeSize();
/*  58 */     this.m_EC.initializeLogPMatrix(totalNbNodes);
/*  59 */     doPrune(node);
/*     */   }
/*     */ 
/*     */   
/*     */   public void doPrune(ClusNode node) throws ClusException {
/*  64 */     System.out.println("Pruning!");
/*  65 */     this.m_Ecc = calculateEncodingCost(node, this.m_Data);
/*  66 */     System.out.println(" -> orig ecc = " + this.m_Ecc);
/*  67 */     traverseTreeAndRecordEncodingCostIfLeafChildren(node, node, this.m_Data);
/*  68 */     if (this.m_EccGain > 0.0D && this.m_BestNodeToPrune != null) {
/*  69 */       System.out.println("Pruning node such that ECC drops with " + this.m_EccGain);
/*  70 */       this.m_BestNodeToPrune.makeLeaf();
/*  71 */       this.m_EccGain = 0.0D;
/*  72 */       this.m_BestNodeToPrune = null;
/*  73 */       doPrune(node);
/*     */     } 
/*     */   }
/*     */   
/*     */   public double calculateEncodingCost(ClusNode node, RowData data) {
/*  78 */     ArrayList<RowData> clusters = new ArrayList<>();
/*  79 */     ArrayList<Integer> clusterIds = new ArrayList<>();
/*  80 */     getLeafClusters(node, data, clusters, clusterIds);
/*  81 */     this.m_EC.setClusters(clusters, clusterIds);
/*  82 */     double ecv = this.m_EC.getEncodingCostValue();
/*  83 */     return ecv;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int traverseTreeAndRecordEncodingCostIfLeafChildren(ClusNode node, ClusNode rootNode, RowData rootData) {
/*  91 */     int arity = node.getNbChildren();
/*  92 */     if (arity > 0) {
/*  93 */       int nbLeafChildren = 0;
/*  94 */       for (int i = 0; i < arity; i++) {
/*  95 */         ClusNode child = (ClusNode)node.getChild(i);
/*  96 */         nbLeafChildren += traverseTreeAndRecordEncodingCostIfLeafChildren(child, rootNode, rootData);
/*     */       } 
/*  98 */       if (nbLeafChildren == arity) {
/*     */         
/* 100 */         ClusNode[] children = node.getChildren();
/* 101 */         NodeTest test = node.getTest();
/* 102 */         node.makeLeaf();
/*     */         
/* 104 */         double ecc = calculateEncodingCost(rootNode, rootData);
/* 105 */         System.out.println("new ecc = " + ecc);
/* 106 */         double eccGain = this.m_Ecc - ecc;
/* 107 */         if (eccGain > this.m_EccGain) {
/* 108 */           this.m_EccGain = eccGain;
/* 109 */           this.m_BestNodeToPrune = node;
/* 110 */           System.out.println("better!");
/*     */         } 
/*     */         
/* 113 */         node.setTest(test);
/* 114 */         for (int j = 0; j < children.length; j++) {
/* 115 */           node.addChild((Node)children[j]);
/*     */         }
/*     */       } 
/* 118 */       return 0;
/*     */     } 
/*     */     
/* 121 */     return 1;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void traverseTreeAndRecordEncodingCost(ClusNode node, ClusNode rootNode, RowData rootData) {
/* 128 */     int arity = node.getNbChildren();
/* 129 */     if (arity > 0) {
/* 130 */       for (int i = 0; i < arity; i++) {
/* 131 */         ClusNode child = (ClusNode)node.getChild(i);
/* 132 */         traverseTreeAndRecordEncodingCost(child, rootNode, rootData);
/*     */       } 
/*     */       
/* 135 */       ClusNode[] children = node.getChildren();
/* 136 */       NodeTest test = node.getTest();
/* 137 */       node.makeLeaf();
/*     */       
/* 139 */       double ecc = calculateEncodingCost(rootNode, rootData);
/* 140 */       System.out.println("new ecc = " + ecc);
/* 141 */       double eccGain = this.m_Ecc - ecc;
/* 142 */       if (eccGain > this.m_EccGain) {
/* 143 */         this.m_EccGain = eccGain;
/* 144 */         this.m_BestNodeToPrune = node;
/* 145 */         System.out.println("better!");
/*     */       } 
/*     */       
/* 148 */       node.setTest(test);
/* 149 */       for (int j = 0; j < children.length; j++) {
/* 150 */         node.addChild((Node)children[j]);
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void getLeafClusters(ClusNode node, RowData data, ArrayList<RowData> clusters, ArrayList<Integer> clusterIds) {
/* 158 */     if (!node.atBottomLevel()) {
/* 159 */       int arity = node.getNbChildren();
/* 160 */       for (int i = 0; i < arity; i++) {
/* 161 */         RowData subset = data.applyWeighted(node.getTest(), i);
/* 162 */         getLeafClusters((ClusNode)node.getChild(i), subset, clusters, clusterIds);
/*     */       } 
/*     */     } else {
/*     */       
/* 166 */       clusters.add(data);
/* 167 */       clusterIds.add(new Integer(node.getID() - 1));
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\pruning\EncodingCostPruning.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */