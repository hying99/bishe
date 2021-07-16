/*     */ package clus.ext.beamsearch;
/*     */ 
/*     */ import clus.algo.tdidt.ClusNode;
/*     */ import clus.statistic.ClassificationStat;
/*     */ import clus.statistic.ClusStatistic;
/*     */ import java.util.Arrays;
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
/*     */ public class ClusBeamSizeConstraints
/*     */ {
/*     */   protected int m_LeafCount;
/*     */   protected int m_StopCount;
/*     */   protected boolean m_IsModified;
/*     */   protected boolean m_Debug;
/*     */   
/*     */   public void enforce(ClusNode root, int size) {
/*  40 */     reset();
/*  41 */     initVisitors(root, size);
/*  42 */     computeCostUsingConstraints(root, size);
/*  43 */     ClusBeamSizeConstraintInfo info = (ClusBeamSizeConstraintInfo)root.getVisitor();
/*  44 */     pruneUsingConstraints(root, size, info.realcost[size]);
/*  45 */     pruneNonMarkedNodes(root);
/*  46 */     removeVisitors(root);
/*     */   }
/*     */   
/*     */   public boolean isModified() {
/*  50 */     return this.m_IsModified;
/*     */   }
/*     */   
/*     */   public boolean isFinished() {
/*  54 */     return (this.m_StopCount >= this.m_LeafCount);
/*     */   }
/*     */   
/*     */   public void reset() {
/*  58 */     this.m_LeafCount = 0;
/*  59 */     this.m_StopCount = 0;
/*  60 */     this.m_IsModified = false;
/*     */   }
/*     */   
/*     */   public static double computeLowerBound(ClusStatistic stat, int l) {
/*  64 */     ClassificationStat cs = (ClassificationStat)stat;
/*  65 */     double[] clcnts = cs.getClassCounts(0);
/*  66 */     double[] c2 = new double[clcnts.length];
/*  67 */     System.arraycopy(clcnts, 0, c2, 0, c2.length);
/*  68 */     Arrays.sort(c2);
/*  69 */     int s = (l - 1) / 2;
/*  70 */     double result = 0.0D;
/*  71 */     for (int i = s + 2; i <= c2.length; i++) {
/*  72 */       result += c2[c2.length - i];
/*     */     }
/*  74 */     return result;
/*     */   }
/*     */   
/*     */   public static void computeCostUsingConstraints(ClusNode node, int l) {
/*  78 */     ClusBeamSizeConstraintInfo info = (ClusBeamSizeConstraintInfo)node.getVisitor();
/*  79 */     if (info.computed[l]) {
/*     */       return;
/*     */     }
/*  82 */     boolean is_leaf = node.atBottomLevel();
/*     */     
/*  84 */     info.lowcost[l] = node.getClusteringStat().getError(); info.realcost[l] = node.getClusteringStat().getError();
/*  85 */     if (is_leaf) {
/*  86 */       info.realcost[l] = node.getClusteringStat().getError();
/*  87 */       info.lowcost[l] = computeLowerBound(node.getClusteringStat(), l);
/*     */     } else {
/*  89 */       info.lowcost[l] = node.getClusteringStat().getError(); info.realcost[l] = node.getClusteringStat().getError();
/*  90 */       ClusNode ch1 = (ClusNode)node.getChild(0);
/*  91 */       ClusNode ch2 = (ClusNode)node.getChild(1);
/*  92 */       ClusBeamSizeConstraintInfo i1 = (ClusBeamSizeConstraintInfo)ch1.getVisitor();
/*  93 */       ClusBeamSizeConstraintInfo i2 = (ClusBeamSizeConstraintInfo)ch2.getVisitor();
/*  94 */       for (int k1 = 1; k1 <= l - 2; k1++) {
/*  95 */         int k2 = l - k1 - 1;
/*  96 */         computeCostUsingConstraints(ch1, k1);
/*  97 */         computeCostUsingConstraints(ch2, k2);
/*  98 */         double realcost1 = i1.realcost[k1];
/*  99 */         double realcost2 = i2.realcost[k2];
/* 100 */         if (realcost1 + realcost2 < info.realcost[l]) {
/* 101 */           info.realcost[l] = realcost1 + realcost2;
/*     */         }
/* 103 */         double lowcost1 = i1.lowcost[k1];
/* 104 */         double lowcost2 = i2.lowcost[k2];
/* 105 */         if (lowcost1 + lowcost2 < info.lowcost[l]) {
/* 106 */           info.lowcost[l] = lowcost1 + lowcost2;
/*     */         }
/*     */       } 
/*     */     } 
/* 110 */     info.computed[l] = true;
/*     */   }
/*     */   
/*     */   public void pruneUsingConstraints(ClusNode node, int l, double b) {
/* 114 */     ClusBeamSizeConstraintInfo info = (ClusBeamSizeConstraintInfo)node.getVisitor();
/* 115 */     info.marked = true;
/*     */ 
/*     */     
/* 118 */     if (b <= info.bound[l]) {
/*     */       return;
/*     */     }
/*     */     
/* 122 */     for (int i = 1; i <= l; i++) {
/* 123 */       if (b > info.bound[i]) info.bound[i] = b; 
/*     */     } 
/* 125 */     if (info.lowcost[l] > b || Math.abs(info.lowcost[l] - node.getClusteringStat().getError()) < 1.0E-12D) {
/*     */       return;
/*     */     }
/*     */     
/* 129 */     if (l >= 3 && !node.atBottomLevel()) {
/* 130 */       for (int k1 = 1; k1 <= l - 2; k1++) {
/* 131 */         int k2 = l - k1 - 1;
/* 132 */         ClusNode ch1 = (ClusNode)node.getChild(0);
/* 133 */         ClusNode ch2 = (ClusNode)node.getChild(1);
/* 134 */         ClusBeamSizeConstraintInfo i1 = (ClusBeamSizeConstraintInfo)ch1.getVisitor();
/* 135 */         ClusBeamSizeConstraintInfo i2 = (ClusBeamSizeConstraintInfo)ch2.getVisitor();
/* 136 */         if (i1.lowcost[k1] + i2.lowcost[k2] <= b) {
/* 137 */           double b1 = b - i2.lowcost[k2];
/* 138 */           double b2 = b - i1.lowcost[k1];
/* 139 */           pruneUsingConstraints(ch1, k1, b1);
/* 140 */           pruneUsingConstraints(ch2, k2, b2);
/*     */         } 
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   public void pruneNonMarkedNodes(ClusNode node) {
/* 147 */     ClusBeamSizeConstraintInfo info = (ClusBeamSizeConstraintInfo)node.getVisitor();
/* 148 */     if (node.atBottomLevel()) {
/* 149 */       this.m_LeafCount++;
/* 150 */       ClusBeamAttrSelector attrsel = (ClusBeamAttrSelector)info.visitor;
/* 151 */       if (attrsel.isStopCrit()) this.m_StopCount++;
/*     */     
/* 153 */     } else if (!info.marked) {
/* 154 */       ClusBeamAttrSelector attrsel = new ClusBeamAttrSelector();
/* 155 */       info.visitor = attrsel;
/* 156 */       node.makeLeaf();
/* 157 */       attrsel.setStopCrit(true);
/* 158 */       this.m_IsModified = true;
/*     */     } else {
/* 160 */       for (int i = 0; i < node.getNbChildren(); i++) {
/* 161 */         ClusNode child = (ClusNode)node.getChild(i);
/* 162 */         pruneNonMarkedNodes(child);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public static void initVisitors(ClusNode node, int size) {
/* 169 */     ClusBeamSizeConstraintInfo info = new ClusBeamSizeConstraintInfo(size);
/* 170 */     info.visitor = node.getVisitor();
/* 171 */     node.setVisitor(info);
/* 172 */     for (int i = 0; i < node.getNbChildren(); i++) {
/* 173 */       ClusNode child = (ClusNode)node.getChild(i);
/* 174 */       initVisitors(child, size);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public static void removeVisitors(ClusNode node) {
/* 180 */     ClusBeamSizeConstraintInfo info = (ClusBeamSizeConstraintInfo)node.getVisitor();
/* 181 */     node.setVisitor(info.visitor);
/* 182 */     for (int i = 0; i < node.getNbChildren(); i++) {
/* 183 */       ClusNode child = (ClusNode)node.getChild(i);
/* 184 */       removeVisitors(child);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void setDebug(boolean debug) {
/* 189 */     this.m_Debug = debug;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\ext\beamsearch\ClusBeamSizeConstraints.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */