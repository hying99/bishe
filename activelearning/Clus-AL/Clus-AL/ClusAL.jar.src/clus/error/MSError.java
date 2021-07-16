/*     */ package clus.error;
/*     */ 
/*     */ import clus.data.attweights.ClusAttributeWeights;
/*     */ import clus.data.rows.DataTuple;
/*     */ import clus.data.type.ClusAttrType;
/*     */ import clus.data.type.NumericAttrType;
/*     */ import clus.statistic.ClusStatistic;
/*     */ import clus.statistic.RegressionStat;
/*     */ import java.io.PrintWriter;
/*     */ import java.text.NumberFormat;
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
/*     */ public class MSError
/*     */   extends ClusNumericError
/*     */ {
/*     */   public static final long serialVersionUID = 1L;
/*     */   protected int[] m_nbEx;
/*     */   protected double[] m_SumErr;
/*     */   protected double[] m_SumSqErr;
/*     */   protected ClusAttributeWeights m_Weights;
/*     */   protected boolean m_PrintAllComps;
/*     */   
/*     */   public MSError(ClusErrorList par, NumericAttrType[] num) {
/*  50 */     this(par, num, (ClusAttributeWeights)null, true);
/*     */   }
/*     */   
/*     */   public MSError(ClusErrorList par, NumericAttrType[] num, ClusAttributeWeights weights) {
/*  54 */     this(par, num, weights, true);
/*     */   }
/*     */   
/*     */   public MSError(ClusErrorList par, NumericAttrType[] num, ClusAttributeWeights weights, boolean printall) {
/*  58 */     super(par, num);
/*  59 */     this.m_nbEx = new int[this.m_Dim];
/*  60 */     this.m_SumErr = new double[this.m_Dim];
/*  61 */     this.m_SumSqErr = new double[this.m_Dim];
/*  62 */     this.m_Weights = weights;
/*  63 */     this.m_PrintAllComps = printall;
/*     */   }
/*     */   
/*     */   public void reset() {
/*  67 */     for (int i = 0; i < this.m_Dim; i++) {
/*  68 */       this.m_SumErr[i] = 0.0D;
/*  69 */       this.m_SumSqErr[i] = 0.0D;
/*  70 */       this.m_nbEx[i] = 0;
/*     */     } 
/*     */   }
/*     */   
/*     */   public void setWeights(ClusAttributeWeights weights) {
/*  75 */     this.m_Weights = weights;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public double getModelErrorComponent(int i) {
/*  81 */     int nb = this.m_nbEx[i];
/*     */ 
/*     */     
/*  84 */     double err = (nb != 0.0D) ? (this.m_SumErr[i] / nb) : 0.0D;
/*  85 */     if (this.m_Weights != null) err *= this.m_Weights.getWeight((ClusAttrType)getAttr(i));
/*     */     
/*  87 */     return err;
/*     */   }
/*     */   
/*     */   public double getModelError() {
/*  91 */     double ss_tree = 0.0D;
/*  92 */     int nb = 0; int i;
/*  93 */     for (i = 0; i < this.m_Dim; i++) {
/*  94 */       nb += this.m_nbEx[i];
/*     */     }
/*  96 */     if (this.m_Weights != null) {
/*  97 */       for (i = 0; i < this.m_Dim; i++) {
/*  98 */         ss_tree += this.m_SumErr[i] * this.m_Weights.getWeight((ClusAttrType)getAttr(i));
/*     */       }
/* 100 */       return (nb != 0.0D) ? (ss_tree / nb) : 0.0D;
/*     */     } 
/* 102 */     for (i = 0; i < this.m_Dim; i++) {
/* 103 */       ss_tree += this.m_SumErr[i];
/*     */     }
/* 105 */     return (nb != 0.0D) ? (ss_tree / nb) : 0.0D;
/*     */   }
/*     */ 
/*     */   
/*     */   public double getModelErrorStandardError() {
/* 110 */     double sum_err = 0.0D;
/* 111 */     double sum_sq_err = 0.0D;
/* 112 */     for (int i = 0; i < this.m_Dim; i++) {
/* 113 */       if (this.m_Weights != null) {
/* 114 */         sum_err += this.m_SumErr[i];
/* 115 */         sum_sq_err += this.m_SumSqErr[i];
/*     */       } else {
/* 117 */         sum_err += this.m_SumErr[i] * this.m_Weights.getWeight((ClusAttrType)getAttr(i));
/* 118 */         sum_sq_err += this.m_SumSqErr[i] * sqr(this.m_Weights.getWeight((ClusAttrType)getAttr(i)));
/*     */       } 
/*     */     } 
/*     */     
/* 122 */     double n = 0.0D;
/* 123 */     for (int j = 0; j < this.m_Dim; j++) {
/* 124 */       n += this.m_nbEx[j];
/*     */     }
/*     */     
/* 127 */     if (n <= 1.0D) {
/* 128 */       return Double.POSITIVE_INFINITY;
/*     */     }
/* 130 */     double ss_x = (n * sum_sq_err - sqr(sum_err)) / n * (n - 1.0D);
/* 131 */     return Math.sqrt(ss_x / n);
/*     */   }
/*     */ 
/*     */   
/*     */   public static final double sqr(double value) {
/* 136 */     return value * value;
/*     */   }
/*     */   
/*     */   public void addExample(double[] real, double[] predicted) {
/* 140 */     for (int i = 0; i < this.m_Dim; i++) {
/* 141 */       double err = sqr(real[i] - predicted[i]);
/* 142 */       System.out.println(err);
/* 143 */       if (!Double.isInfinite(err) && !Double.isNaN(err)) {
/* 144 */         this.m_SumErr[i] = this.m_SumErr[i] + err;
/* 145 */         this.m_SumSqErr[i] = this.m_SumSqErr[i] + sqr(err);
/* 146 */         this.m_nbEx[i] = this.m_nbEx[i] + 1;
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public void addExample(double[] real, boolean[] predicted) {
/* 152 */     for (int i = 0; i < this.m_Dim; i++) {
/* 153 */       double predicted_i = predicted[i] ? 1.0D : 0.0D;
/* 154 */       double err = sqr(real[i] - predicted_i);
/* 155 */       System.out.println(err);
/* 156 */       if (!Double.isInfinite(err) && !Double.isNaN(err)) {
/* 157 */         this.m_SumErr[i] = this.m_SumErr[i] + err;
/* 158 */         this.m_SumSqErr[i] = this.m_SumSqErr[i] + sqr(err);
/* 159 */         this.m_nbEx[i] = this.m_nbEx[i] + 1;
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public void addExample(DataTuple tuple, ClusStatistic pred) {
/* 165 */     double[] predicted = pred.getNumericPred();
/* 166 */     for (int i = 0; i < this.m_Dim; i++) {
/* 167 */       double err = sqr(getAttr(i).getNumeric(tuple) - predicted[i]);
/* 168 */       if (!Double.isInfinite(err) && !Double.isNaN(err)) {
/* 169 */         this.m_SumErr[i] = this.m_SumErr[i] + err;
/* 170 */         this.m_SumSqErr[i] = this.m_SumSqErr[i] + sqr(err);
/* 171 */         this.m_nbEx[i] = this.m_nbEx[i] + 1;
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public void addExample(DataTuple real, DataTuple pred) {
/* 177 */     for (int i = 0; i < this.m_Dim; i++) {
/* 178 */       double real_i = getAttr(i).getNumeric(real);
/* 179 */       double predicted_i = getAttr(i).getNumeric(pred);
/* 180 */       double err = sqr(real_i - predicted_i);
/* 181 */       if (!Double.isInfinite(err) && !Double.isNaN(err)) {
/* 182 */         this.m_SumErr[i] = this.m_SumErr[i] + err;
/* 183 */         this.m_SumSqErr[i] = this.m_SumSqErr[i] + sqr(err);
/* 184 */         this.m_nbEx[i] = this.m_nbEx[i] + 1;
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public void addInvalid(DataTuple tuple) {}
/*     */   
/*     */   public void add(ClusError other) {
/* 192 */     MSError oe = (MSError)other;
/* 193 */     for (int i = 0; i < this.m_Dim; i++) {
/* 194 */       this.m_SumErr[i] = this.m_SumErr[i] + oe.m_SumErr[i];
/* 195 */       this.m_SumSqErr[i] = this.m_SumSqErr[i] + oe.m_SumSqErr[i];
/* 196 */       this.m_nbEx[i] = this.m_nbEx[i] + oe.m_nbEx[i];
/*     */     } 
/*     */   }
/*     */   
/*     */   public void showModelError(PrintWriter out, int detail) {
/* 201 */     NumberFormat fr = getFormat();
/* 202 */     StringBuffer buf = new StringBuffer();
/* 203 */     if (this.m_PrintAllComps) {
/* 204 */       buf.append("[");
/* 205 */       for (int i = 0; i < this.m_Dim; i++) {
/* 206 */         if (i != 0) buf.append(","); 
/* 207 */         buf.append(fr.format(getModelErrorComponent(i)));
/*     */       } 
/* 209 */       if (this.m_Dim > 1) { buf.append("]: "); }
/* 210 */       else { buf.append("]"); }
/*     */     
/* 212 */     }  if (this.m_Dim > 1 || !this.m_PrintAllComps) {
/* 213 */       buf.append(fr.format(getModelError()));
/*     */     }
/* 215 */     out.println(buf.toString());
/*     */   }
/*     */   
/*     */   public void showSummaryError(PrintWriter out, boolean detail) {
/* 219 */     NumberFormat fr = getFormat();
/* 220 */     out.println(getPrefix() + "Mean over components MSE: " + fr.format(getModelError()));
/*     */   }
/*     */   
/*     */   public String getName() {
/* 224 */     if (this.m_Weights == null) return "Mean squared error (MSE)"; 
/* 225 */     return "Weighted mean squared error (MSE) (" + this.m_Weights.getName((ClusAttrType[])this.m_Attrs) + ")";
/*     */   }
/*     */   
/*     */   public ClusError getErrorClone(ClusErrorList par) {
/* 229 */     return new MSError(par, this.m_Attrs, this.m_Weights, this.m_PrintAllComps);
/*     */   }
/*     */   
/*     */   public double computeLeafError(ClusStatistic stat) {
/* 233 */     RegressionStat rstat = (RegressionStat)stat;
/* 234 */     return rstat.getSVarS(this.m_Weights) * rstat.getNbAttributes();
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\error\MSError.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */