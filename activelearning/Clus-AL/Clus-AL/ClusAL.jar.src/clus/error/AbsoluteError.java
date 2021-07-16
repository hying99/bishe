/*     */ package clus.error;
/*     */ 
/*     */ import clus.data.rows.DataTuple;
/*     */ import clus.data.type.NumericAttrType;
/*     */ import clus.statistic.ClusStatistic;
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
/*     */ public class AbsoluteError
/*     */   extends ClusNumericError
/*     */ {
/*     */   public static final long serialVersionUID = 1L;
/*     */   protected double[] m_AbsError;
/*     */   protected double[] m_AbsDefError;
/*     */   
/*     */   public AbsoluteError(ClusErrorList par, NumericAttrType[] num) {
/*  41 */     super(par, num);
/*  42 */     this.m_AbsError = new double[this.m_Dim];
/*  43 */     this.m_AbsDefError = new double[this.m_Dim];
/*     */   }
/*     */   
/*     */   public double getModelError() {
/*  47 */     double avg_abs = 0.0D;
/*  48 */     for (int i = 0; i < this.m_Dim; ) { avg_abs += this.m_AbsError[i]; i++; }
/*  49 */      return avg_abs / this.m_Dim;
/*     */   }
/*     */   
/*     */   public void addExample(double[] real, double[] predicted) {
/*  53 */     for (int i = 0; i < this.m_Dim; i++) {
/*  54 */       double err = real[i] - predicted[i];
/*  55 */       this.m_AbsError[i] = this.m_AbsError[i] + Math.abs(err);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void addInvalid(DataTuple tuple) {}
/*     */   
/*     */   public void addExample(DataTuple tuple, ClusStatistic pred) {
/*  63 */     double[] predicted = pred.getNumericPred();
/*  64 */     for (int i = 0; i < this.m_Dim; i++) {
/*  65 */       double err = this.m_Attrs[i].getNumeric(tuple) - predicted[i];
/*  66 */       this.m_AbsError[i] = this.m_AbsError[i] + Math.abs(err);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void add(ClusError other) {
/*  71 */     AbsoluteError oe = (AbsoluteError)other;
/*  72 */     for (int i = 0; i < this.m_Dim; i++) {
/*  73 */       this.m_AbsError[i] = this.m_AbsError[i] + oe.m_AbsError[i];
/*  74 */       this.m_AbsDefError[i] = this.m_AbsDefError[i] + oe.m_AbsDefError[i];
/*     */     } 
/*     */   }
/*     */   
/*     */   public void showDefaultError(PrintWriter out, boolean detail) {
/*  79 */     out.println(getPrefix() + "Default error: " + " " + showDoubleArray(this.m_AbsDefError));
/*     */   }
/*     */   
/*     */   public void showModelError(PrintWriter out, int detail) {
/*  83 */     NumberFormat fr = getFormat();
/*  84 */     StringBuffer buf = new StringBuffer();
/*  85 */     buf.append(showDoubleArray(this.m_AbsError, getNbExamples()));
/*  86 */     if (this.m_Dim > 1) {
/*  87 */       buf.append(": " + fr.format(getModelError() / getNbExamples()));
/*     */     }
/*  89 */     out.println(buf.toString());
/*     */   }
/*     */   
/*     */   public void showRelativeError(PrintWriter out, boolean detail) {
/*  93 */     out.println(getPrefix() + "Relative error: " + "" + showDoubleArray(this.m_AbsError, this.m_AbsDefError));
/*     */   }
/*     */   
/*     */   public void showSummaryError(PrintWriter out, boolean detail) {
/*  97 */     NumberFormat fr = getFormat();
/*  98 */     double ss_def = 0.0D;
/*  99 */     double ss_tree = 0.0D;
/* 100 */     for (int i = 0; i < this.m_Dim; i++) {
/* 101 */       ss_tree += this.m_AbsError[i];
/* 102 */       ss_def += this.m_AbsDefError[i];
/*     */     } 
/* 104 */     double re = (ss_def != 0.0D) ? (ss_tree / ss_def) : 0.0D;
/* 105 */     out.println(getPrefix() + "Sum over components RE: " + fr.format(re) + " = " + fr.format(ss_tree) + " / " + fr.format(ss_def));
/*     */   }
/*     */   
/*     */   public double getSummaryError() {
/* 109 */     double ss_tree = 0.0D;
/* 110 */     for (int i = 0; i < this.m_Dim; i++) {
/* 111 */       ss_tree += this.m_AbsError[i];
/*     */     }
/* 113 */     return ss_tree;
/*     */   }
/*     */   
/*     */   public String getName() {
/* 117 */     return "Mean absolute error (MAE)";
/*     */   }
/*     */   
/*     */   public ClusError getErrorClone(ClusErrorList par) {
/* 121 */     return new AbsoluteError(par, this.m_Attrs);
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\error\AbsoluteError.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */