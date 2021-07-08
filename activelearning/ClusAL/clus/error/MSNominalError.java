/*     */ package clus.error;
/*     */ 
/*     */ import clus.data.attweights.ClusAttributeWeights;
/*     */ import clus.data.rows.DataTuple;
/*     */ import clus.data.type.ClusAttrType;
/*     */ import clus.data.type.NominalAttrType;
/*     */ import clus.statistic.ClassificationStat;
/*     */ import clus.statistic.ClusStatistic;
/*     */ import java.io.PrintWriter;
/*     */ import java.text.NumberFormat;
/*     */ 
/*     */ public class MSNominalError
/*     */   extends ClusNominalError
/*     */ {
/*     */   public static final long serialVersionUID = 1L;
/*     */   protected ClusAttributeWeights m_Weights;
/*     */   protected double[] m_SumErr;
/*     */   protected double[] m_SumSqErr;
/*     */   protected boolean m_PrintAllComps = true;
/*     */   
/*     */   public MSNominalError(ClusErrorList par, NominalAttrType[] nom, ClusAttributeWeights weights) {
/*  22 */     super(par, nom);
/*  23 */     this.m_Weights = weights;
/*  24 */     this.m_SumErr = new double[this.m_Dim];
/*  25 */     this.m_SumSqErr = new double[this.m_Dim];
/*     */   }
/*     */   
/*     */   public ClusError getErrorClone(ClusErrorList par) {
/*  29 */     return new MSNominalError(par, this.m_Attrs, this.m_Weights);
/*     */   }
/*     */   
/*     */   public String getName() {
/*  33 */     if (this.m_Weights == null) return "Mean squared error (MSE) for Nominal Attributes"; 
/*  34 */     return "Weighted mean squared error (MSE) for Nominal Attributes (" + this.m_Weights.getName((ClusAttrType[])this.m_Attrs) + ")";
/*     */   }
/*     */   
/*     */   public double getModelErrorComponent(int i) {
/*  38 */     int nb = getNbExamples();
/*  39 */     double err = (nb != 0.0D) ? (this.m_SumErr[i] / nb) : 0.0D;
/*  40 */     if (this.m_Weights != null) err *= this.m_Weights.getWeight((ClusAttrType)getAttr(i)); 
/*  41 */     return err;
/*     */   }
/*     */   
/*     */   public double getModelError() {
/*  45 */     double ss_tree = 0.0D;
/*  46 */     int nb = getNbExamples();
/*  47 */     if (this.m_Weights != null) {
/*  48 */       for (int j = 0; j < this.m_Dim; j++) {
/*  49 */         ss_tree += this.m_SumErr[j] * this.m_Weights.getWeight((ClusAttrType)getAttr(j));
/*     */       }
/*  51 */       return (nb != 0.0D) ? (ss_tree / nb / this.m_Dim) : 0.0D;
/*     */     } 
/*  53 */     for (int i = 0; i < this.m_Dim; i++) {
/*  54 */       ss_tree += this.m_SumErr[i];
/*     */     }
/*  56 */     return (nb != 0.0D) ? (ss_tree / nb / this.m_Dim) : 0.0D;
/*     */   }
/*     */ 
/*     */   
/*     */   public double getModelErrorStandardError() {
/*  61 */     double sum_err = 0.0D;
/*  62 */     double sum_sq_err = 0.0D;
/*  63 */     for (int i = 0; i < this.m_Dim; i++) {
/*  64 */       if (this.m_Weights != null) {
/*  65 */         sum_err += this.m_SumErr[i];
/*  66 */         sum_sq_err += this.m_SumSqErr[i];
/*     */       } else {
/*  68 */         sum_err += this.m_SumErr[i] * this.m_Weights.getWeight((ClusAttrType)getAttr(i));
/*  69 */         sum_sq_err += this.m_SumSqErr[i] * sqr(this.m_Weights.getWeight((ClusAttrType)getAttr(i)));
/*     */       } 
/*     */     } 
/*  72 */     double n = (getNbExamples() * this.m_Dim);
/*  73 */     if (n <= 1.0D) {
/*  74 */       return Double.POSITIVE_INFINITY;
/*     */     }
/*  76 */     double ss_x = (n * sum_sq_err - sqr(sum_err)) / n * (n - 1.0D);
/*  77 */     return Math.sqrt(ss_x / n);
/*     */   }
/*     */ 
/*     */   
/*     */   public static final double sqr(double value) {
/*  82 */     return value * value;
/*     */   }
/*     */   
/*     */   public void addExample(DataTuple tuple, ClusStatistic pred) {
/*  86 */     ClassificationStat stat = pred.getClassificationStat();
/*  87 */     for (int i = 0; i < this.m_Dim; i++) {
/*  88 */       NominalAttrType type = this.m_Attrs[i];
/*  89 */       int value = type.getNominal(tuple);
/*  90 */       for (int j = 0; j < type.getNbValues(); j++) {
/*  91 */         double zeroOne = (value == j) ? 1.0D : 0.0D;
/*  92 */         double prop = stat.getProportion(i, j);
/*  93 */         double err = sqr(zeroOne - prop);
/*     */         
/*  95 */         this.m_SumErr[i] = this.m_SumErr[i] + err;
/*  96 */         this.m_SumSqErr[i] = this.m_SumSqErr[i] + sqr(err);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void addInvalid(DataTuple tuple) {}
/*     */   
/*     */   public void reset() {
/* 105 */     for (int i = 0; i < this.m_Dim; i++) {
/* 106 */       this.m_SumErr[i] = 0.0D;
/* 107 */       this.m_SumSqErr[i] = 0.0D;
/*     */     } 
/*     */   }
/*     */   
/*     */   public void add(ClusError other) {
/* 112 */     MSNominalError oe = (MSNominalError)other;
/* 113 */     for (int i = 0; i < this.m_Dim; i++) {
/* 114 */       this.m_SumErr[i] = this.m_SumErr[i] + oe.m_SumErr[i];
/* 115 */       this.m_SumSqErr[i] = this.m_SumSqErr[i] + oe.m_SumSqErr[i];
/*     */     } 
/*     */   }
/*     */   
/*     */   public void showModelError(PrintWriter out, int detail) {
/* 120 */     NumberFormat fr = getFormat();
/* 121 */     StringBuffer buf = new StringBuffer();
/* 122 */     if (this.m_PrintAllComps) {
/* 123 */       buf.append("[");
/* 124 */       for (int i = 0; i < this.m_Dim; i++) {
/* 125 */         if (i != 0) buf.append(","); 
/* 126 */         buf.append(fr.format(getModelErrorComponent(i)));
/*     */       } 
/* 128 */       if (this.m_Dim > 1) { buf.append("]: "); }
/* 129 */       else { buf.append("]"); }
/*     */     
/* 131 */     }  if (this.m_Dim > 1 || !this.m_PrintAllComps) {
/* 132 */       buf.append(fr.format(getModelError()));
/*     */     }
/* 134 */     out.println(buf.toString());
/*     */   }
/*     */   
/*     */   public void showSummaryError(PrintWriter out, boolean detail) {
/* 138 */     NumberFormat fr = getFormat();
/* 139 */     out.println(getPrefix() + "Mean over components MSE: " + fr.format(getModelError()));
/*     */   }
/*     */   
/*     */   public double computeLeafError(ClusStatistic stat) {
/* 143 */     ClassificationStat cstat = (ClassificationStat)stat;
/* 144 */     return cstat.getSVarS(this.m_Weights) * cstat.getNbAttributes();
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\error\MSNominalError.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */