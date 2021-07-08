/*     */ package clus.model.test;
/*     */ 
/*     */ import clus.data.rows.DataTuple;
/*     */ import clus.data.type.ClusAttrType;
/*     */ import clus.data.type.NominalAttrType;
/*     */ import clus.util.ClusRandom;
/*     */ import jeans.util.sort.DoubleIndexSorter;
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
/*     */ public class NominalTest
/*     */   extends NodeTest
/*     */ {
/*     */   public static final long serialVersionUID = 1L;
/*     */   protected NominalAttrType m_Type;
/*     */   protected double[] m_Sorted;
/*     */   protected int[] m_Index;
/*     */   
/*     */   public NominalTest(NominalAttrType type, double[] freq) {
/*  41 */     this.m_Type = type;
/*  42 */     setProportion(freq);
/*     */   }
/*     */   
/*     */   public ClusAttrType getType() {
/*  46 */     return (ClusAttrType)this.m_Type;
/*     */   }
/*     */   
/*     */   public void setType(ClusAttrType type) {
/*  50 */     this.m_Type = (NominalAttrType)type;
/*     */   }
/*     */   
/*     */   public String getString() {
/*  54 */     if (this.m_Type.getNbValues() > 2) {
/*  55 */       return this.m_Type.getName();
/*     */     }
/*  57 */     String val = this.m_Type.getValue(0);
/*  58 */     if (hasBranchLabels()) {
/*  59 */       return this.m_Type.getName();
/*     */     }
/*  61 */     return this.m_Type.getName() + " = " + val;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(NodeTest test) {
/*  67 */     return (this.m_Type == test.getType());
/*     */   }
/*     */   
/*     */   public int hashCode() {
/*  71 */     return this.m_Type.getIndex();
/*     */   }
/*     */   
/*     */   public void preprocess(int mode) {
/*  75 */     DoubleIndexSorter sorter = DoubleIndexSorter.getInstance();
/*  76 */     sorter.setData(this.m_Sorted = DoubleIndexSorter.arrayclone(this.m_BranchFreq));
/*  77 */     sorter.sort();
/*  78 */     this.m_Index = sorter.getIndex();
/*     */   }
/*     */   
/*     */   public int predictWeighted(DataTuple tuple) {
/*  82 */     int val = this.m_Type.getNominal(tuple);
/*  83 */     return nominalPredictWeighted(val);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int nominalPredict(int value) {
/*  93 */     int arity = getNbChildren();
/*  94 */     if (value == arity) {
/*  95 */       double cumul = 0.0D;
/*  96 */       double val = ClusRandom.nextDouble(0);
/*  97 */       for (int i = 0; i < arity - 1; i++) {
/*  98 */         cumul += this.m_Sorted[i];
/*  99 */         if (val < cumul) {
/* 100 */           return this.m_Index[i];
/*     */         }
/*     */       } 
/* 103 */       return this.m_Index[arity - 1];
/*     */     } 
/* 105 */     return value;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int nominalPredictWeighted(int value) {
/* 111 */     if (value == getNbChildren())
/*     */     {
/* 113 */       return hasUnknownBranch() ? 2 : -1;
/*     */     }
/* 115 */     return value;
/*     */   }
/*     */   
/*     */   public boolean hasBranchLabels() {
/* 119 */     return true;
/*     */   }
/*     */   
/*     */   public String getBranchLabel(int i) {
/* 123 */     return this.m_Type.getValue(i);
/*     */   }
/*     */   
/*     */   public String getBranchString(int i) {
/* 127 */     return this.m_Type.getName() + " = " + this.m_Type.getValue(i);
/*     */   }
/*     */   
/*     */   public NodeTest getBranchTest(int i) {
/* 131 */     SubsetTest test = new SubsetTest(this.m_Type, 1);
/* 132 */     test.setValue(0, i);
/* 133 */     test.setPosFreq(getProportion(i));
/* 134 */     return test;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\model\test\NominalTest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */