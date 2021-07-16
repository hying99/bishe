/*     */ package clus.ext.beamsearch;
/*     */ 
/*     */ import clus.data.attweights.ClusAttributeWeights;
/*     */ import clus.data.rows.DataTuple;
/*     */ import clus.data.rows.SparseDataTuple;
/*     */ import clus.data.type.ClusAttrType;
/*     */ import clus.data.type.NumericAttrType;
/*     */ import clus.main.Settings;
/*     */ import clus.statistic.ClusStatistic;
/*     */ import clus.statistic.RegressionStat;
/*     */ import clus.statistic.StatisticPrintInfo;
/*     */ import java.util.ArrayList;
/*     */ 
/*     */ public class ClusBeamSimRegrStat extends RegressionStat {
/*     */   public static final long serialVersionUID = 1L;
/*     */   public double[] m_SumPredictions;
/*     */   public double[] m_SumSqPredictions;
/*     */   private ClusBeam m_Beam;
/*     */   
/*     */   public ClusBeamSimRegrStat(NumericAttrType[] attrs, ClusBeam beam) {
/*  21 */     super(attrs);
/*  22 */     this.m_SumPredictions = new double[this.m_NbAttrs];
/*  23 */     this.m_SumSqPredictions = new double[this.m_NbAttrs];
/*  24 */     this.m_Beam = beam;
/*     */   }
/*     */   
/*     */   public ClusBeamSimRegrStat(NumericAttrType[] attrs, boolean onlymean, ClusBeam beam) {
/*  28 */     super(attrs, onlymean);
/*  29 */     if (!onlymean) {
/*  30 */       this.m_SumPredictions = new double[this.m_NbAttrs];
/*  31 */       this.m_SumSqPredictions = new double[this.m_NbAttrs];
/*  32 */       this.m_Beam = beam;
/*     */     } 
/*     */   }
/*     */   
/*     */   public void calcMean(double[] means) {
/*  37 */     super.calcMean(means);
/*     */   }
/*     */   
/*     */   public double getMean(int i) {
/*  41 */     return super.getMean(i);
/*     */   }
/*     */   
/*     */   public double getSVarS(int i) {
/*  45 */     return super.getSVarS(i);
/*     */   }
/*     */   
/*     */   public void add(ClusStatistic other) {
/*  49 */     super.add(other);
/*  50 */     ClusBeamSimRegrStat or = (ClusBeamSimRegrStat)other;
/*  51 */     for (int i = 0; i < this.m_NbAttrs; i++) {
/*  52 */       this.m_SumPredictions[i] = this.m_SumPredictions[i] + or.m_SumPredictions[i];
/*  53 */       this.m_SumSqPredictions[i] = this.m_SumSqPredictions[i] + or.m_SumSqPredictions[i];
/*     */     } 
/*     */   }
/*     */   
/*     */   public void updateWeighted(SparseDataTuple tuple, double weight) {
/*  58 */     System.out.println("public void updateWeighted(SparseDataTuple tuple, double weight)");
/*  59 */     System.exit(1);
/*     */   }
/*     */   
/*     */   public void updateWeighted(DataTuple tuple, double weight) {
/*  63 */     super.updateWeighted(tuple, weight);
/*  64 */     ArrayList<ClusBeamModel> models = this.m_Beam.toArray();
/*  65 */     double[] vals = new double[this.m_NbAttrs];
/*  66 */     double[] valssq = new double[this.m_NbAttrs];
/*  67 */     for (int k = 0; k < models.size(); k++) {
/*  68 */       ClusBeamModel cbm = models.get(k);
/*  69 */       RegressionStat rstat = (RegressionStat)cbm.getPredictionForTuple(tuple);
/*  70 */       for (int j = 0; j < this.m_NbAttrs; j++) {
/*  71 */         vals[j] = vals[j] + rstat.getMean(j);
/*  72 */         valssq[j] = valssq[j] + rstat.getMean(j) * rstat.getMean(j);
/*     */       } 
/*     */     } 
/*  75 */     for (int i = 0; i < this.m_NbAttrs; i++) {
/*  76 */       this.m_SumPredictions[i] = this.m_SumPredictions[i] + weight * vals[i];
/*  77 */       this.m_SumSqPredictions[i] = this.m_SumSqPredictions[i] + weight * valssq[i];
/*     */     } 
/*     */   }
/*     */   
/*     */   public double getSVarS(ClusAttributeWeights scale) {
/*  82 */     double result = super.getSVarS(scale);
/*  83 */     double similarity = 0.0D;
/*  84 */     for (int i = 0; i < this.m_NbAttrs; i++) {
/*  85 */       double mean = super.getMean(i);
/*  86 */       double firstterm = this.m_SumWeight * this.m_Beam.getCrWidth() * mean * mean;
/*  87 */       double secondterm = 2.0D * mean * getSumPredictions(i);
/*  88 */       double thirdterm = getSumSqPredictions(i);
/*  89 */       similarity += firstterm - secondterm + thirdterm;
/*  90 */       similarity *= scale.getWeight((ClusAttrType)this.m_Attrs[i]);
/*     */     } 
/*  92 */     similarity /= this.m_NbAttrs;
/*  93 */     similarity /= this.m_Beam.getCrWidth();
/*     */     
/*  95 */     result += Settings.BEAM_SIMILARITY * similarity;
/*  96 */     return result;
/*     */   }
/*     */   
/*     */   public ClusStatistic cloneStat() {
/* 100 */     return (ClusStatistic)new ClusBeamSimRegrStat(this.m_Attrs, false, this.m_Beam);
/*     */   }
/*     */   
/*     */   public ClusStatistic cloneSimple() {
/* 104 */     return (ClusStatistic)new ClusBeamSimRegrStat(this.m_Attrs, true, this.m_Beam);
/*     */   }
/*     */   
/*     */   public void copy(ClusStatistic other) {
/* 108 */     super.copy(other);
/* 109 */     ClusBeamSimRegrStat or = (ClusBeamSimRegrStat)other;
/* 110 */     System.arraycopy(or.m_SumPredictions, 0, this.m_SumPredictions, 0, this.m_NbAttrs);
/* 111 */     System.arraycopy(or.m_SumSqPredictions, 0, this.m_SumSqPredictions, 0, this.m_NbAttrs);
/*     */   }
/*     */   
/*     */   public String getString(StatisticPrintInfo info) {
/* 115 */     return super.getString(info);
/*     */   }
/*     */   
/*     */   public void reset() {
/* 119 */     super.reset();
/* 120 */     for (int i = 0; i < this.m_NbAttrs; i++) {
/* 121 */       this.m_SumPredictions[i] = 0.0D;
/* 122 */       this.m_SumSqPredictions[i] = 0.0D;
/*     */     } 
/*     */   }
/*     */   
/*     */   public void subtractFromOther(ClusStatistic other) {
/* 127 */     super.subtractFromOther(other);
/* 128 */     ClusBeamSimRegrStat or = (ClusBeamSimRegrStat)other;
/* 129 */     for (int i = 0; i < this.m_NbAttrs; i++) {
/* 130 */       this.m_SumPredictions[i] = or.m_SumPredictions[i] - this.m_SumPredictions[i];
/* 131 */       this.m_SumSqPredictions[i] = or.m_SumSqPredictions[i] - this.m_SumSqPredictions[i];
/*     */     } 
/*     */   }
/*     */   
/*     */   public void subtractFromThis(ClusStatistic other) {
/* 136 */     super.subtractFromThis(other);
/* 137 */     ClusBeamSimRegrStat or = (ClusBeamSimRegrStat)other;
/* 138 */     for (int i = 0; i < this.m_NbAttrs; i++) {
/* 139 */       this.m_SumPredictions[i] = this.m_SumPredictions[i] - or.m_SumPredictions[i];
/* 140 */       this.m_SumSqPredictions[i] = this.m_SumSqPredictions[i] - or.m_SumSqPredictions[i];
/*     */     } 
/*     */   }
/*     */   
/*     */   public double getSumPredictions(int i) {
/* 145 */     return this.m_SumPredictions[i];
/*     */   }
/*     */   
/*     */   public double[] getSumPredictions() {
/* 149 */     return this.m_SumPredictions;
/*     */   }
/*     */   
/*     */   public double getSumSqPredictions(int i) {
/* 153 */     return this.m_SumSqPredictions[i];
/*     */   }
/*     */   
/*     */   public double[] getSumSqPredictions() {
/* 157 */     return this.m_SumSqPredictions;
/*     */   }
/*     */   
/*     */   public void setBeam(ClusBeam beam) {
/* 161 */     this.m_Beam = beam;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\ext\beamsearch\ClusBeamSimRegrStat.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */