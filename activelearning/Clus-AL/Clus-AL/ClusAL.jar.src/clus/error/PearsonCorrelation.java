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
/*     */ public class PearsonCorrelation
/*     */   extends ClusNumericError
/*     */ {
/*     */   public static final long serialVersionUID = 1L;
/*     */   protected double[] m_SumPi;
/*     */   protected double[] m_SumSPi;
/*     */   protected double[] m_SumAi;
/*     */   protected double[] m_SumSAi;
/*     */   protected double[] m_SumPiAi;
/*     */   
/*     */   public PearsonCorrelation(ClusErrorList par, NumericAttrType[] num) {
/*  42 */     super(par, num);
/*  43 */     this.m_SumPi = new double[this.m_Dim];
/*  44 */     this.m_SumSPi = new double[this.m_Dim];
/*  45 */     this.m_SumAi = new double[this.m_Dim];
/*  46 */     this.m_SumSAi = new double[this.m_Dim];
/*  47 */     this.m_SumPiAi = new double[this.m_Dim];
/*     */   }
/*     */   
/*     */   public void reset() {
/*  51 */     for (int i = 0; i < this.m_Dim; i++) {
/*  52 */       this.m_SumPi[i] = 0.0D;
/*  53 */       this.m_SumSPi[i] = 0.0D;
/*  54 */       this.m_SumAi[i] = 0.0D;
/*  55 */       this.m_SumSAi[i] = 0.0D;
/*  56 */       this.m_SumPiAi[i] = 0.0D;
/*     */     } 
/*     */   }
/*     */   
/*     */   public boolean shouldBeLow() {
/*  61 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public double getCorrelation(int i) {
/*  66 */     int nb = getNbExamples();
/*     */     
/*  68 */     double Pi_ss = this.m_SumSPi[i] - this.m_SumPi[i] * this.m_SumPi[i] / nb;
/*     */     
/*  70 */     if (Pi_ss == 0.0D)
/*     */     {
/*  72 */       return 0.0D;
/*     */     }
/*  74 */     double Ai_ss = this.m_SumSAi[i] - this.m_SumAi[i] * this.m_SumAi[i] / nb;
/*     */     
/*  76 */     double root = Math.sqrt(Pi_ss * Ai_ss);
/*  77 */     double above = this.m_SumPiAi[i] - this.m_SumPi[i] * this.m_SumAi[i] / nb;
/*     */ 
/*     */     
/*  80 */     return above / root;
/*     */   }
/*     */   
/*     */   public double getModelErrorComponent(int i) {
/*  84 */     return getCorrelation(i);
/*     */   }
/*     */   
/*     */   public double getModelError() {
/*  88 */     double mean = 0.0D;
/*  89 */     for (int i = 0; i < this.m_Dim; i++) {
/*  90 */       mean += getCorrelation(i);
/*     */     }
/*  92 */     return mean / this.m_Dim;
/*     */   }
/*     */   
/*     */   public void addExample(double[] real, double[] predicted) {
/*  96 */     for (int i = 0; i < this.m_Dim; i++) {
/*     */       
/*  98 */       this.m_SumPi[i] = this.m_SumPi[i] + predicted[i];
/*  99 */       this.m_SumSPi[i] = this.m_SumSPi[i] + predicted[i] * predicted[i];
/*     */       
/* 101 */       this.m_SumAi[i] = this.m_SumAi[i] + real[i];
/* 102 */       this.m_SumSAi[i] = this.m_SumSAi[i] + real[i] * real[i];
/*     */       
/* 104 */       this.m_SumPiAi[i] = this.m_SumPiAi[i] + predicted[i] * real[i];
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void addInvalid(DataTuple tuple) {}
/*     */   
/*     */   public void addExample(DataTuple tuple, ClusStatistic pred) {
/* 112 */     double[] predicted = pred.getNumericPred();
/* 113 */     for (int i = 0; i < this.m_Dim; i++) {
/* 114 */       double real_i = getAttr(i).getNumeric(tuple);
/*     */ 
/*     */       
/* 117 */       this.m_SumPi[i] = this.m_SumPi[i] + predicted[i];
/* 118 */       this.m_SumSPi[i] = this.m_SumSPi[i] + predicted[i] * predicted[i];
/*     */       
/* 120 */       this.m_SumAi[i] = this.m_SumAi[i] + real_i;
/* 121 */       this.m_SumSAi[i] = this.m_SumSAi[i] + real_i * real_i;
/*     */       
/* 123 */       this.m_SumPiAi[i] = this.m_SumPiAi[i] + predicted[i] * real_i;
/*     */     } 
/*     */   }
/*     */   
/*     */   public void addExample(DataTuple real, DataTuple pred) {
/* 128 */     for (int i = 0; i < this.m_Dim; i++) {
/* 129 */       double real_i = getAttr(i).getNumeric(real);
/* 130 */       double predicted_i = getAttr(i).getNumeric(pred);
/*     */ 
/*     */       
/* 133 */       this.m_SumPi[i] = this.m_SumPi[i] + predicted_i;
/* 134 */       this.m_SumSPi[i] = this.m_SumSPi[i] + predicted_i * predicted_i;
/*     */       
/* 136 */       this.m_SumAi[i] = this.m_SumAi[i] + real_i;
/* 137 */       this.m_SumSAi[i] = this.m_SumSAi[i] + real_i * real_i;
/*     */       
/* 139 */       this.m_SumPiAi[i] = this.m_SumPiAi[i] + predicted_i * real_i;
/*     */     } 
/*     */   }
/*     */   
/*     */   public void add(ClusError other) {
/* 144 */     PearsonCorrelation oe = (PearsonCorrelation)other;
/* 145 */     for (int i = 0; i < this.m_Dim; i++) {
/*     */       
/* 147 */       this.m_SumPi[i] = this.m_SumPi[i] + oe.m_SumPi[i];
/* 148 */       this.m_SumSPi[i] = this.m_SumSPi[i] + oe.m_SumSPi[i];
/*     */       
/* 150 */       this.m_SumAi[i] = this.m_SumAi[i] + oe.m_SumAi[i];
/* 151 */       this.m_SumSAi[i] = this.m_SumSAi[i] + oe.m_SumSAi[i];
/*     */       
/* 153 */       this.m_SumPiAi[i] = this.m_SumPiAi[i] + oe.m_SumPiAi[i];
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void showModelError(PrintWriter out, int detail) {
/* 161 */     NumberFormat fr = getFormat();
/* 162 */     StringBuffer buf = new StringBuffer();
/* 163 */     buf.append("[");
/* 164 */     int nb = getNbExamples();
/* 165 */     double avg_sq_r = 0.0D;
/* 166 */     for (int i = 0; i < this.m_Dim; i++) {
/* 167 */       double Pi_ss = this.m_SumSPi[i] - this.m_SumPi[i] * this.m_SumPi[i] / nb;
/* 168 */       double Ai_ss = this.m_SumSAi[i] - this.m_SumAi[i] * this.m_SumAi[i] / nb;
/* 169 */       double root = Math.sqrt(Pi_ss * Ai_ss);
/* 170 */       double above = this.m_SumPiAi[i] - this.m_SumPi[i] * this.m_SumAi[i] / nb;
/* 171 */       double el = above / root;
/* 172 */       if (i != 0) buf.append(","); 
/* 173 */       buf.append(fr.format(el));
/* 174 */       avg_sq_r += el * el;
/*     */     } 
/* 176 */     avg_sq_r /= this.m_Dim;
/* 177 */     buf.append("], Avg r^2: " + fr.format(avg_sq_r));
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 187 */     out.println(buf.toString());
/*     */   }
/*     */   
/*     */   public boolean hasSummary() {
/* 191 */     return false;
/*     */   }
/*     */   
/*     */   public String getName() {
/* 195 */     return "Pearson correlation coefficient";
/*     */   }
/*     */   
/*     */   public ClusError getErrorClone(ClusErrorList par) {
/* 199 */     return new PearsonCorrelation(par, this.m_Attrs);
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\error\PearsonCorrelation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */