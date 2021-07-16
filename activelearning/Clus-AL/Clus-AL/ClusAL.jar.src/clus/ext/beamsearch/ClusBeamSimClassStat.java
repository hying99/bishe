/*     */ package clus.ext.beamsearch;
/*     */ 
/*     */ import clus.data.attweights.ClusAttributeWeights;
/*     */ import clus.data.rows.DataTuple;
/*     */ import clus.data.rows.SparseDataTuple;
/*     */ import clus.data.type.ClusAttrType;
/*     */ import clus.data.type.NominalAttrType;
/*     */ import clus.main.Settings;
/*     */ import clus.statistic.ClassificationStat;
/*     */ import clus.statistic.ClusStatistic;
/*     */ import java.util.ArrayList;
/*     */ 
/*     */ public class ClusBeamSimClassStat
/*     */   extends ClassificationStat {
/*     */   private static final long serialVersionUID = 1L;
/*     */   public double[][] m_SumPredictions;
/*     */   public double[][] m_SumSqPredictions;
/*     */   private ClusBeam m_Beam;
/*     */   
/*     */   public ClusBeamSimClassStat(NominalAttrType[] nomAtts, ClusBeam beam) {
/*  21 */     super(nomAtts);
/*  22 */     this.m_Beam = beam;
/*  23 */     this.m_SumPredictions = replicateEmpty(this.m_ClassCounts);
/*  24 */     this.m_SumSqPredictions = replicateEmpty(this.m_ClassCounts);
/*     */   }
/*     */   
/*     */   public ClusStatistic cloneStat() {
/*  28 */     return (ClusStatistic)new ClusBeamSimClassStat(this.m_Attrs, this.m_Beam);
/*     */   }
/*     */   
/*     */   public void reset() {
/*  32 */     super.reset();
/*  33 */     for (int i = 0; i < this.m_NbTarget; i++) {
/*  34 */       for (int j = 0; j < (this.m_SumPredictions[i]).length; j++) {
/*  35 */         this.m_SumPredictions[i][j] = 0.0D;
/*  36 */         this.m_SumSqPredictions[i][j] = 0.0D;
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public void copy(ClusStatistic other) {
/*  42 */     super.copy(other);
/*  43 */     ClusBeamSimClassStat or = (ClusBeamSimClassStat)other;
/*     */ 
/*     */     
/*  46 */     for (int i = 0; i < this.m_NbTarget; i++) {
/*  47 */       double[] my = this.m_SumPredictions[i];
/*  48 */       double[] your = or.m_SumPredictions[i];
/*  49 */       System.arraycopy(your, 0, my, 0, my.length);
/*  50 */       my = this.m_SumSqPredictions[i];
/*  51 */       your = or.m_SumSqPredictions[i];
/*  52 */       System.arraycopy(your, 0, my, 0, my.length);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void addPrediction(ClusStatistic other, double weight) {
/*  57 */     super.addPrediction(other, weight);
/*  58 */     ClusBeamSimClassStat or = (ClusBeamSimClassStat)other;
/*  59 */     for (int i = 0; i < this.m_NbTarget; i++) {
/*  60 */       double[] mysum = this.m_SumPredictions[i];
/*  61 */       double[] yoursum = or.m_SumPredictions[i];
/*  62 */       double[] mysumsq = this.m_SumSqPredictions[i];
/*  63 */       double[] yoursumsq = or.m_SumSqPredictions[i];
/*  64 */       for (int j = 0; j < mysum.length; j++) {
/*  65 */         mysum[j] = mysum[j] + weight * yoursum[j];
/*  66 */         mysumsq[j] = mysumsq[j] + weight * yoursumsq[j];
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public void add(ClusStatistic other) {
/*  72 */     super.add(other);
/*  73 */     ClusBeamSimClassStat or = (ClusBeamSimClassStat)other;
/*  74 */     for (int i = 0; i < this.m_NbTarget; i++) {
/*  75 */       double[] mysum = this.m_SumPredictions[i];
/*  76 */       double[] yoursum = or.m_SumPredictions[i];
/*  77 */       double[] mysumsq = this.m_SumSqPredictions[i];
/*  78 */       double[] yoursumsq = or.m_SumSqPredictions[i];
/*  79 */       for (int j = 0; j < mysum.length; j++) {
/*  80 */         mysum[j] = mysum[j] + yoursum[j];
/*  81 */         mysumsq[j] = mysumsq[j] + yoursumsq[j];
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public void subtractFromThis(ClusStatistic other) {
/*  87 */     super.subtractFromThis(other);
/*  88 */     ClusBeamSimClassStat or = (ClusBeamSimClassStat)other;
/*  89 */     for (int i = 0; i < this.m_NbTarget; i++) {
/*  90 */       double[] mysum = this.m_SumPredictions[i];
/*  91 */       double[] yoursum = or.m_SumPredictions[i];
/*  92 */       double[] mysumsq = this.m_SumPredictions[i];
/*  93 */       double[] yoursumsq = or.m_SumPredictions[i];
/*  94 */       for (int j = 0; j < mysum.length; j++) {
/*  95 */         mysum[j] = mysum[j] - yoursum[j];
/*  96 */         mysumsq[j] = mysumsq[j] - yoursumsq[j];
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public void subtractFromOther(ClusStatistic other) {
/* 102 */     super.subtractFromOther(other);
/* 103 */     ClusBeamSimClassStat or = (ClusBeamSimClassStat)other;
/* 104 */     for (int i = 0; i < this.m_NbTarget; i++) {
/* 105 */       double[] mysum = this.m_SumPredictions[i];
/* 106 */       double[] yoursum = or.m_SumPredictions[i];
/* 107 */       double[] mysumsq = this.m_SumPredictions[i];
/* 108 */       double[] yoursumsq = or.m_SumPredictions[i];
/* 109 */       for (int j = 0; j < mysum.length; j++) {
/* 110 */         mysum[j] = yoursum[j] - mysum[j];
/* 111 */         mysumsq[j] = yoursumsq[j] - mysumsq[j];
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public void updateWeighted(DataTuple tuple, int idx) {
/* 117 */     updateWeighted(tuple, tuple.getWeight());
/*     */   }
/*     */   
/*     */   public void updateWeighted(SparseDataTuple tuple, int idx) {
/* 121 */     updateWeighted((DataTuple)tuple, tuple.getWeight());
/*     */   }
/*     */   
/*     */   public void updateWeighted(DataTuple tuple, double weight) {
/* 125 */     super.updateWeighted(tuple, weight);
/* 126 */     ArrayList<ClusBeamModel> models = this.m_Beam.toArray();
/* 127 */     double[][] vals = replicateEmpty(this.m_SumPredictions);
/* 128 */     double[][] valssq = replicateEmpty(this.m_SumSqPredictions);
/* 129 */     for (int k = 0; k < models.size(); k++) {
/* 130 */       ClusBeamModel cbm = models.get(k);
/* 131 */       ClassificationStat cstat = (ClassificationStat)cbm.getPredictionForTuple(tuple);
/* 132 */       double[][] cvals = cstat.getProbabilityPrediction();
/* 133 */       for (int i = 0; i < this.m_NbTarget; i++) {
/* 134 */         for (int j = 0; j < (cvals[i]).length; j++) {
/* 135 */           vals[i][j] = vals[i][j] + cvals[i][j];
/* 136 */           valssq[i][j] = valssq[i][j] + cvals[i][j] * cvals[i][j];
/*     */         } 
/*     */       } 
/*     */     } 
/* 140 */     for (int m = 0; m < this.m_SumPredictions.length; m++) {
/* 141 */       for (int n = 0; n < (this.m_SumPredictions[m]).length; n++) {
/* 142 */         this.m_SumPredictions[m][n] = this.m_SumPredictions[m][n] + weight * vals[m][n];
/* 143 */         this.m_SumSqPredictions[m][n] = this.m_SumSqPredictions[m][n] + weight * valssq[m][n];
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public double getSVarS(ClusAttributeWeights scale) {
/* 149 */     double result = super.getSVarS(scale);
/* 150 */     double similarity = 0.0D;
/* 151 */     double[][] probdistr = getProbabilityPrediction();
/*     */     
/* 153 */     for (int i = 0; i < this.m_NbTarget; i++) {
/* 154 */       double firstterm = 0.0D;
/* 155 */       double secondterm = 0.0D;
/* 156 */       double thirdterm = 0.0D;
/* 157 */       for (int j = 0; j < (this.m_SumPredictions[i]).length; j++) {
/* 158 */         firstterm += probdistr[i][j] * probdistr[i][j];
/* 159 */         secondterm += 2.0D * probdistr[i][j] * getSumPrediction(i, j);
/* 160 */         thirdterm += getSumSqPrediction(i, j);
/*     */       } 
/* 162 */       similarity += firstterm - secondterm + thirdterm;
/* 163 */       similarity *= scale.getWeight((ClusAttrType)this.m_Attrs[i]);
/*     */     } 
/* 165 */     similarity /= this.m_NbTarget;
/* 166 */     similarity /= this.m_Beam.getCrWidth();
/*     */     
/* 168 */     result += Settings.BEAM_SIMILARITY * similarity;
/* 169 */     return result;
/*     */   }
/*     */   
/*     */   public double[][] replicateEmpty(double[][] values) {
/* 173 */     double[][] result = new double[values.length][];
/* 174 */     for (int i = 0; i < values.length; i++)
/* 175 */       result[i] = new double[(values[i]).length]; 
/* 176 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   public double getSumPrediction(int target, int classval) {
/* 181 */     return this.m_SumPredictions[target][classval];
/*     */   }
/*     */   
/*     */   public double[] getSumPrediction(int target) {
/* 185 */     return this.m_SumPredictions[target];
/*     */   }
/*     */   
/*     */   public double getSumSqPrediction(int target, int classval) {
/* 189 */     return this.m_SumSqPredictions[target][classval];
/*     */   }
/*     */   
/*     */   public double[] getSumSqPrediction(int target) {
/* 193 */     return this.m_SumSqPredictions[target];
/*     */   }
/*     */   
/*     */   public void setBeam(ClusBeam beam) {
/* 197 */     this.m_Beam = beam;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\ext\beamsearch\ClusBeamSimClassStat.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */