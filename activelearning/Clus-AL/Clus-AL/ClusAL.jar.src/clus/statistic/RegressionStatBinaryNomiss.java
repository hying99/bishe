/*     */ package clus.statistic;
/*     */ 
/*     */ import clus.data.attweights.ClusAttributeWeights;
/*     */ import clus.data.type.ClusAttrType;
/*     */ import clus.data.type.NumericAttrType;
/*     */ import clus.util.ClusFormat;
/*     */ import java.text.NumberFormat;
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
/*     */ public class RegressionStatBinaryNomiss
/*     */   extends RegressionStatBase
/*     */ {
/*     */   public static final long serialVersionUID = 1L;
/*     */   public double[] m_SumValues;
/*     */   
/*     */   public RegressionStatBinaryNomiss(NumericAttrType[] attrs) {
/*  52 */     this(attrs, false);
/*     */   }
/*     */   
/*     */   public RegressionStatBinaryNomiss(NumericAttrType[] attrs, boolean onlymean) {
/*  56 */     super(attrs, onlymean);
/*  57 */     if (!onlymean) {
/*  58 */       this.m_SumValues = new double[this.m_NbAttrs];
/*     */     }
/*     */   }
/*     */   
/*     */   public ClusStatistic cloneStat() {
/*  63 */     return new RegressionStatBinaryNomiss(this.m_Attrs, false);
/*     */   }
/*     */   
/*     */   public ClusStatistic cloneSimple() {
/*  67 */     return new RegressionStatBinaryNomiss(this.m_Attrs, true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ClusStatistic copyNormalizedWeighted(double weight) {
/*  74 */     RegressionStatBinaryNomiss newStat = normalizedCopy();
/*  75 */     for (int iTarget = 0; iTarget < newStat.getNbAttributes(); iTarget++) {
/*  76 */       newStat.m_Means[iTarget] = weight * newStat.m_Means[iTarget];
/*     */     }
/*  78 */     return newStat;
/*     */   }
/*     */   
/*     */   public void reset() {
/*  82 */     this.m_SumWeight = 0.0D;
/*  83 */     this.m_NbExamples = 0;
/*  84 */     Arrays.fill(this.m_SumValues, 0.0D);
/*     */   }
/*     */   
/*     */   public void copy(ClusStatistic other) {
/*  88 */     RegressionStatBinaryNomiss or = (RegressionStatBinaryNomiss)other;
/*  89 */     this.m_SumWeight = or.m_SumWeight;
/*  90 */     this.m_NbExamples = or.m_NbExamples;
/*  91 */     System.arraycopy(or.m_SumValues, 0, this.m_SumValues, 0, this.m_NbAttrs);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public RegressionStatBinaryNomiss normalizedCopy() {
/*  98 */     RegressionStatBinaryNomiss copy = (RegressionStatBinaryNomiss)cloneSimple();
/*  99 */     copy.m_NbExamples = 0;
/* 100 */     copy.m_SumWeight = 1.0D;
/* 101 */     calcMean(copy.m_Means);
/* 102 */     return copy;
/*     */   }
/*     */   
/*     */   public void add(ClusStatistic other) {
/* 106 */     RegressionStatBinaryNomiss or = (RegressionStatBinaryNomiss)other;
/* 107 */     this.m_SumWeight += or.m_SumWeight;
/* 108 */     this.m_NbExamples += or.m_NbExamples;
/* 109 */     for (int i = 0; i < this.m_NbAttrs; i++) {
/* 110 */       this.m_SumValues[i] = this.m_SumValues[i] + or.m_SumValues[i];
/*     */     }
/*     */   }
/*     */   
/*     */   public void addScaled(double scale, ClusStatistic other) {
/* 115 */     RegressionStatBinaryNomiss or = (RegressionStatBinaryNomiss)other;
/* 116 */     this.m_SumWeight += scale * or.m_SumWeight;
/* 117 */     this.m_NbExamples += or.m_NbExamples;
/* 118 */     for (int i = 0; i < this.m_NbAttrs; i++) {
/* 119 */       this.m_SumValues[i] = this.m_SumValues[i] + scale * or.m_SumValues[i];
/*     */     }
/*     */   }
/*     */   
/*     */   public void subtractFromThis(ClusStatistic other) {
/* 124 */     RegressionStatBinaryNomiss or = (RegressionStatBinaryNomiss)other;
/* 125 */     this.m_SumWeight -= or.m_SumWeight;
/* 126 */     this.m_NbExamples -= or.m_NbExamples;
/* 127 */     for (int i = 0; i < this.m_NbAttrs; i++) {
/* 128 */       this.m_SumValues[i] = this.m_SumValues[i] - or.m_SumValues[i];
/*     */     }
/*     */   }
/*     */   
/*     */   public void subtractFromOther(ClusStatistic other) {
/* 133 */     RegressionStatBinaryNomiss or = (RegressionStatBinaryNomiss)other;
/* 134 */     or.m_SumWeight -= this.m_SumWeight;
/* 135 */     or.m_NbExamples -= this.m_NbExamples;
/* 136 */     for (int i = 0; i < this.m_NbAttrs; i++) {
/* 137 */       this.m_SumValues[i] = or.m_SumValues[i] - this.m_SumValues[i];
/*     */     }
/*     */   }
/*     */   
/*     */   public void calcMean(double[] means) {
/* 142 */     for (int i = 0; i < this.m_NbAttrs; i++) {
/* 143 */       means[i] = getMean(i);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public double getMean(int i) {
/* 149 */     return (this.m_SumWeight != 0.0D) ? (this.m_SumValues[i] / this.m_SumWeight) : 0.0D;
/*     */   }
/*     */   
/*     */   public double getSVarS(int i) {
/* 153 */     double n_tot = this.m_SumWeight;
/* 154 */     double sv_tot = this.m_SumValues[i];
/* 155 */     return sv_tot - sv_tot * sv_tot / n_tot;
/*     */   }
/*     */   
/*     */   public double getSVarS(ClusAttributeWeights scale) {
/* 159 */     double result = 0.0D;
/* 160 */     for (int i = 0; i < this.m_NbAttrs; i++) {
/* 161 */       double n_tot = this.m_SumWeight;
/* 162 */       double sv_tot = this.m_SumValues[i];
/* 163 */       result += (sv_tot - sv_tot * sv_tot / n_tot) * scale.getWeight((ClusAttrType)this.m_Attrs[i]);
/*     */     } 
/* 165 */     return result / this.m_NbAttrs;
/*     */   }
/*     */   
/*     */   public double getSVarSDiff(ClusAttributeWeights scale, ClusStatistic other) {
/* 169 */     double result = 0.0D;
/* 170 */     RegressionStatBinaryNomiss or = (RegressionStatBinaryNomiss)other;
/* 171 */     for (int i = 0; i < this.m_NbAttrs; i++) {
/* 172 */       double n_tot = this.m_SumWeight - or.m_SumWeight;
/* 173 */       double sv_tot = this.m_SumValues[i] - or.m_SumValues[i];
/* 174 */       result += (sv_tot - sv_tot * sv_tot / n_tot) * scale.getWeight((ClusAttrType)this.m_Attrs[i]);
/*     */     } 
/* 176 */     return result / this.m_NbAttrs;
/*     */   }
/*     */   
/*     */   public String getString(StatisticPrintInfo info) {
/* 180 */     NumberFormat fr = ClusFormat.SIX_AFTER_DOT;
/* 181 */     StringBuffer buf = new StringBuffer();
/* 182 */     buf.append("[");
/* 183 */     for (int i = 0; i < this.m_NbAttrs; i++) {
/* 184 */       if (i != 0) buf.append(","); 
/* 185 */       buf.append(fr.format(getMean(i)));
/*     */     } 
/* 187 */     buf.append("]");
/* 188 */     if (info.SHOW_EXAMPLE_COUNT) {
/* 189 */       buf.append(": ");
/* 190 */       buf.append(fr.format(this.m_SumWeight));
/*     */     } 
/* 192 */     return buf.toString();
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\statistic\RegressionStatBinaryNomiss.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */