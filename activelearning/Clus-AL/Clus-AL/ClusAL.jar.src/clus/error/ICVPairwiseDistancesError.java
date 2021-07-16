/*     */ package clus.error;
/*     */ 
/*     */ import clus.algo.rules.ClusRule;
/*     */ import clus.algo.rules.ClusRuleSet;
/*     */ import clus.algo.tdidt.ClusNode;
/*     */ import clus.data.rows.DataTuple;
/*     */ import clus.data.rows.RowData;
/*     */ import clus.data.type.ClusSchema;
/*     */ import clus.model.ClusModel;
/*     */ import clus.model.test.NodeTest;
/*     */ import clus.statistic.ClusDistance;
/*     */ import java.io.PrintWriter;
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
/*     */ public class ICVPairwiseDistancesError
/*     */   extends ClusError
/*     */ {
/*     */   public static final long serialVersionUID = 1L;
/*     */   protected double m_Value;
/*     */   protected double m_ValueWithDefault;
/*     */   protected ClusDistance m_Dist;
/*     */   
/*     */   public ICVPairwiseDistancesError(ClusErrorList par, ClusDistance dist) {
/*  46 */     super(par);
/*  47 */     this.m_Dist = dist;
/*     */   }
/*     */   
/*     */   public static double computeICVPairwiseDistances(ClusDistance dist, RowData data) {
/*  51 */     double sum = 0.0D;
/*  52 */     double sumWiDiag = 0.0D;
/*  53 */     double sumWiTria = 0.0D;
/*  54 */     int nb = data.getNbRows();
/*  55 */     for (int j = 0; j < nb; j++) {
/*  56 */       DataTuple t1 = data.getTuple(j);
/*  57 */       double w1 = t1.getWeight();
/*  58 */       for (int i = 0; i < j; i++) {
/*  59 */         DataTuple t2 = data.getTuple(i);
/*  60 */         double wi = w1 * t2.getWeight();
/*  61 */         double d = dist.calcDistance(t1, t2);
/*  62 */         sum += wi * d;
/*  63 */         sumWiTria += wi;
/*     */       } 
/*  65 */       sumWiDiag += w1 * w1;
/*     */     } 
/*  67 */     return sum / (2.0D * sumWiTria + sumWiDiag);
/*     */   }
/*     */   
/*     */   public void computeRecursive(ClusNode node, RowData data) {
/*  71 */     int nb = node.getNbChildren();
/*  72 */     if (nb == 0) {
/*  73 */       double variance = computeICVPairwiseDistances(this.m_Dist, data);
/*  74 */       double sumweight = data.getSumWeights();
/*  75 */       this.m_Value += sumweight * variance;
/*     */     } else {
/*  77 */       NodeTest tst = node.getTest();
/*  78 */       for (int i = 0; i < node.getNbChildren(); i++) {
/*  79 */         ClusNode child = (ClusNode)node.getChild(i);
/*  80 */         RowData subset = data.applyWeighted(tst, i);
/*  81 */         computeRecursive(child, subset);
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public void computeForRule(ClusRule rule, ClusSchema schema) {
/*  87 */     RowData covered = new RowData(rule.getData(), schema);
/*  88 */     this.m_Value = computeICVPairwiseDistances(this.m_Dist, covered);
/*     */   }
/*     */   
/*     */   public void computeForRuleSet(ClusRuleSet set, ClusSchema schema) {
/*  92 */     double sumWeight = 0.0D;
/*  93 */     for (int i = 0; i < set.getModelSize(); i++) {
/*  94 */       RowData covered = new RowData(set.getRule(i).getData(), schema);
/*  95 */       double weight = covered.getSumWeights();
/*  96 */       this.m_Value += weight * computeICVPairwiseDistances(this.m_Dist, covered);
/*  97 */       sumWeight += weight;
/*     */     } 
/*  99 */     this.m_ValueWithDefault = this.m_Value;
/* 100 */     this.m_Value /= sumWeight;
/* 101 */     RowData defaultData = new RowData(set.getDefaultData(), schema);
/* 102 */     double defWeight = defaultData.getSumWeights();
/* 103 */     this.m_ValueWithDefault += defWeight * computeICVPairwiseDistances(this.m_Dist, defaultData);
/* 104 */     sumWeight += defWeight;
/* 105 */     this.m_ValueWithDefault /= sumWeight;
/*     */   }
/*     */   
/*     */   public void compute(RowData data, ClusModel model) {
/* 109 */     if (model instanceof ClusNode) {
/* 110 */       ClusNode tree = (ClusNode)model;
/* 111 */       computeRecursive(tree, data);
/* 112 */       this.m_Value /= data.getSumWeights();
/* 113 */     } else if (model instanceof ClusRuleSet) {
/* 114 */       computeForRuleSet((ClusRuleSet)model, data.getSchema());
/* 115 */     } else if (model instanceof ClusRule) {
/* 116 */       computeForRule((ClusRule)model, data.getSchema());
/*     */     } 
/*     */   }
/*     */   
/*     */   public void showModelError(PrintWriter wrt, int detail) {
/* 121 */     StringBuffer res = new StringBuffer();
/* 122 */     res.append(String.valueOf(this.m_Value));
/* 123 */     if (this.m_ValueWithDefault != 0.0D) {
/* 124 */       res.append(" (with default: " + this.m_ValueWithDefault + ")");
/*     */     }
/* 126 */     wrt.println(res.toString());
/*     */   }
/*     */   
/*     */   public ClusError getErrorClone(ClusErrorList par) {
/* 130 */     return new ICVPairwiseDistancesError(getParent(), this.m_Dist);
/*     */   }
/*     */   
/*     */   public String getName() {
/* 134 */     return "ICV-Pairwise-Distances";
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\error\ICVPairwiseDistancesError.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */