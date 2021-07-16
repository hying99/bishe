/*     */ package clus.model.test;
/*     */ 
/*     */ import clus.data.rows.DataTuple;
/*     */ import clus.data.type.ClusAttrType;
/*     */ import clus.data.type.NumericAttrType;
/*     */ import clus.util.ClusRandom;
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
/*     */ public class NumericTest
/*     */   extends NodeTest
/*     */ {
/*     */   protected double m_Bound;
/*     */   protected NumericAttrType m_Type;
/*     */   public static final long serialVersionUID = 1L;
/*     */   
/*     */   public NumericTest(ClusAttrType attr, double bound, double posfreq) {
/*  38 */     this.m_Type = (NumericAttrType)attr;
/*  39 */     this.m_Bound = bound;
/*  40 */     setArity(2);
/*  41 */     setPosFreq(posfreq);
/*     */   }
/*     */   
/*     */   public NumericTest(ClusAttrType attr) {
/*  45 */     this(attr, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY);
/*     */   }
/*     */   
/*     */   public boolean isInverseNumeric() {
/*  49 */     return false;
/*     */   }
/*     */   
/*     */   public final int getAttrIndex() {
/*  53 */     return this.m_Type.getArrayIndex();
/*     */   }
/*     */   
/*     */   public final NumericAttrType getNumType() {
/*  57 */     return this.m_Type;
/*     */   }
/*     */   
/*     */   public final double getBound() {
/*  61 */     return this.m_Bound;
/*     */   }
/*     */   
/*     */   public final void setBound(double bound) {
/*  65 */     this.m_Bound = bound;
/*     */   }
/*     */   
/*     */   public ClusAttrType getType() {
/*  69 */     return (ClusAttrType)this.m_Type;
/*     */   }
/*     */   
/*     */   public void setType(ClusAttrType type) {
/*  73 */     this.m_Type = (NumericAttrType)type;
/*     */   }
/*     */   
/*     */   public String getString() {
/*  77 */     String value = (this.m_Bound != Double.NEGATIVE_INFINITY) ? String.valueOf(this.m_Bound) : "?";
/*  78 */     return this.m_Type.getName() + " > " + value;
/*     */   }
/*     */   
/*     */   public boolean hasConstants() {
/*  82 */     return (this.m_Bound != Double.NEGATIVE_INFINITY);
/*     */   }
/*     */   
/*     */   public boolean equals(NodeTest test) {
/*  86 */     if (this.m_Type != test.getType()) {
/*  87 */       return false;
/*     */     }
/*  89 */     NumericTest ntest = (NumericTest)test;
/*  90 */     if (isInverseNumeric() != ntest.isInverseNumeric()) {
/*  91 */       return false;
/*     */     }
/*  93 */     return (this.m_Bound == ntest.m_Bound);
/*     */   }
/*     */   
/*     */   public int hashCode() {
/*  97 */     long v = Double.doubleToLongBits(this.m_Bound);
/*  98 */     return this.m_Type.getIndex() + (int)(v ^ v >>> 32L);
/*     */   }
/*     */   
/*     */   public int softEquals(NodeTest test) {
/* 102 */     if (this.m_Type != test.getType()) {
/* 103 */       return 0;
/*     */     }
/* 105 */     NumericTest ntest = (NumericTest)test;
/* 106 */     if (this.m_Bound == ntest.m_Bound) {
/* 107 */       return 2;
/*     */     }
/* 109 */     if (Math.abs(getPosFreq() - ntest.getPosFreq()) < 0.1D) {
/* 110 */       return 1;
/*     */     }
/* 112 */     return 0;
/*     */   }
/*     */   
/*     */   public int numericPredict(double value) {
/* 116 */     if (value == Double.POSITIVE_INFINITY) {
/* 117 */       return (ClusRandom.nextDouble(0) < getPosFreq()) ? 0 : 1;
/*     */     }
/*     */     
/* 120 */     return (value > this.m_Bound) ? 0 : 1;
/*     */   }
/*     */   
/*     */   public int numericPredictWeighted(double value) {
/* 124 */     if (value == Double.POSITIVE_INFINITY) {
/* 125 */       return hasUnknownBranch() ? 2 : -1;
/*     */     }
/* 127 */     return (value > this.m_Bound) ? 0 : 1;
/*     */   }
/*     */ 
/*     */   
/*     */   public int predictWeighted(DataTuple tuple) {
/* 132 */     double val = this.m_Type.getNumeric(tuple);
/* 133 */     return numericPredictWeighted(val);
/*     */   }
/*     */   
/*     */   public NodeTest getBranchTest(int i) {
/* 137 */     if (i == 0) {
/* 138 */       return this;
/*     */     }
/* 140 */     return new InverseNumericTest((ClusAttrType)this.m_Type, getBound(), 1.0D - getPosFreq());
/*     */   }
/*     */ 
/*     */   
/*     */   public NodeTest simplifyConjunction(NodeTest other) {
/* 145 */     if (getType().getIndex() != other.getType().getIndex()) {
/* 146 */       return null;
/*     */     }
/* 148 */     NumericTest onum = (NumericTest)other;
/* 149 */     if (isInverseNumeric() != onum.isInverseNumeric()) {
/* 150 */       return null;
/*     */     }
/* 152 */     double pos_freq = Math.min(getPosFreq(), onum.getPosFreq());
/* 153 */     if (isInverseNumeric()) {
/* 154 */       double d = Math.min(getBound(), onum.getBound());
/* 155 */       return new InverseNumericTest((ClusAttrType)this.m_Type, d, pos_freq);
/*     */     } 
/* 157 */     double new_bound = Math.max(getBound(), onum.getBound());
/* 158 */     return new NumericTest((ClusAttrType)this.m_Type, new_bound, pos_freq);
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\model\test\NumericTest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */