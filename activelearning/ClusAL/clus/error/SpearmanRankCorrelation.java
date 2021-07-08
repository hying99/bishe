/*     */ package clus.error;
/*     */ 
/*     */ import clus.data.rows.DataTuple;
/*     */ import clus.data.type.NumericAttrType;
/*     */ import java.util.ArrayList;
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
/*     */ public class SpearmanRankCorrelation
/*     */   extends ClusNumericError
/*     */ {
/*  22 */   protected ArrayList<Double> RankCorrelations = new ArrayList<>();
/*     */   
/*     */   public SpearmanRankCorrelation(ClusErrorList par, NumericAttrType[] num) {
/*  25 */     super(par, num);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addExample(double[] real, double[] predicted) {
/*  32 */     double rank = getSpearmanRankCorrelation(real, predicted);
/*     */     
/*  34 */     this.RankCorrelations.add(Double.valueOf(rank));
/*     */   }
/*     */   
/*     */   public void addExample(DataTuple real, DataTuple pred) {
/*  38 */     double[] double_real = new double[this.m_Dim];
/*  39 */     double[] double_pred = new double[this.m_Dim];
/*  40 */     for (int i = 0; i < this.m_Dim; i++) {
/*  41 */       double_real[i] = getAttr(i).getNumeric(real);
/*  42 */       double_pred[i] = getAttr(i).getNumeric(pred);
/*     */     } 
/*     */     
/*  45 */     addExample(double_real, double_pred);
/*     */   }
/*     */ 
/*     */   
/*     */   public double getModelErrorComponent(int i) {
/*  50 */     System.err.println("SpearmanRankCorrelation does not have multiple components (it's a measure over all dimensions)");
/*  51 */     return 0.0D;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getAvgRankCorr() {
/*  60 */     double total = 0.0D;
/*  61 */     for (int i = 0; i < this.RankCorrelations.size(); i++) {
/*  62 */       total += ((Double)this.RankCorrelations.get(i)).doubleValue();
/*     */     }
/*  64 */     return total / this.RankCorrelations.size();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getHarmonicAvgRankCorr() {
/*  72 */     double total = 0.0D;
/*  73 */     for (int i = 0; i < this.RankCorrelations.size(); i++) {
/*  74 */       total += 1.0D / ((Double)this.RankCorrelations.get(i)).doubleValue();
/*     */     }
/*  76 */     return this.RankCorrelations.size() / total;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getRankCorrVariance() {
/*  84 */     double avg = getAvgRankCorr();
/*  85 */     double total = 0.0D;
/*  86 */     for (int i = 0; i < this.RankCorrelations.size(); i++) {
/*  87 */       total += (((Double)this.RankCorrelations.get(i)).doubleValue() - avg) * (((Double)this.RankCorrelations.get(i)).doubleValue() - avg);
/*     */     }
/*  89 */     return total / this.RankCorrelations.size();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getHarmonicRankCorrVariance() {
/*  97 */     double avg = getHarmonicAvgRankCorr();
/*  98 */     double total = 0.0D;
/*  99 */     for (int i = 0; i < this.RankCorrelations.size(); i++) {
/* 100 */       total += (((Double)this.RankCorrelations.get(i)).doubleValue() - avg) * (((Double)this.RankCorrelations.get(i)).doubleValue() - avg);
/*     */     }
/* 102 */     return total / this.RankCorrelations.size();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String getName() {
/* 108 */     return "Spearman Rank Correlation";
/*     */   }
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
/*     */   private double[] getRanks(double[] values) {
/* 121 */     double[] result = new double[values.length];
/*     */     
/* 123 */     int rank = values.length;
/*     */     
/* 125 */     for (int v = 0; v < values.length; v++) {
/* 126 */       for (int i = 0; i < values.length; i++) {
/* 127 */         if (values[i] < values[v]) {
/* 128 */           rank--;
/*     */         }
/*     */       } 
/* 131 */       result[v] = rank;
/* 132 */       rank = values.length;
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 137 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public double getSpearmanRankCorrelation(double[] a, double[] b) {
/* 143 */     int n = a.length;
/*     */     
/* 145 */     double[] ra = getRanks(a);
/* 146 */     double[] rb = getRanks(b);
/*     */     
/* 148 */     double[] d = new double[n];
/* 149 */     for (int i = 0; i < n; i++) {
/* 150 */       d[i] = ra[i] - rb[i];
/*     */     }
/*     */ 
/*     */     
/* 154 */     double sum_ds = 0.0D;
/* 155 */     for (int j = 0; j < n; j++) {
/* 156 */       sum_ds += d[j] * d[j];
/*     */     }
/*     */     
/* 159 */     double rank = 1.0D - 6.0D * sum_ds / (n * (n * n - 1));
/*     */     
/* 161 */     return rank;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public ClusError getErrorClone(ClusErrorList par) {
/* 167 */     return null;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\error\SpearmanRankCorrelation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */