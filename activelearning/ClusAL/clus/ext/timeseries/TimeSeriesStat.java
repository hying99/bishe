/*     */ package clus.ext.timeseries;
/*     */ 
/*     */ import clus.data.attweights.ClusAttributeWeights;
/*     */ import clus.data.rows.DataTuple;
/*     */ import clus.data.rows.RowData;
/*     */ import clus.data.type.ClusAttrType;
/*     */ import clus.data.type.ClusSchema;
/*     */ import clus.data.type.NumericAttrType;
/*     */ import clus.data.type.TimeSeriesAttrType;
/*     */ import clus.statistic.BitVectorStat;
/*     */ import clus.statistic.ClusDistance;
/*     */ import clus.statistic.ClusStatistic;
/*     */ import clus.statistic.StatisticPrintInfo;
/*     */ import clus.statistic.SumPairwiseDistancesStat;
/*     */ import clus.util.ClusFormat;
/*     */ import java.text.NumberFormat;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class TimeSeriesStat
/*     */   extends SumPairwiseDistancesStat
/*     */ {
/*     */   public static final long serialVersionUID = 1L;
/*     */   protected TimeSeriesAttrType m_Attr;
/*  45 */   private ArrayList m_TimeSeriesStack = new ArrayList();
/*  46 */   public TimeSeries m_RepresentativeMean = new TimeSeries("[]");
/*  47 */   public TimeSeries m_RepresentativeMedoid = new TimeSeries("[]");
/*     */ 
/*     */   
/*     */   protected double m_AvgDistances;
/*     */ 
/*     */   
/*     */   public TimeSeriesStat(TimeSeriesAttrType attr, ClusDistance dist, int efflvl) {
/*  54 */     super(dist, efflvl);
/*  55 */     this.m_Attr = attr;
/*     */   }
/*     */   
/*     */   public ClusStatistic cloneStat() {
/*  59 */     TimeSeriesStat stat = new TimeSeriesStat(this.m_Attr, this.m_Distance, this.m_Efficiency);
/*  60 */     stat.cloneFrom((BitVectorStat)this);
/*  61 */     return (ClusStatistic)stat;
/*     */   }
/*     */   
/*     */   public ClusStatistic cloneSimple() {
/*  65 */     TimeSeriesStat stat = new TimeSeriesStat(this.m_Attr, this.m_Distance, this.m_Efficiency);
/*  66 */     stat.m_RepresentativeMean = new TimeSeries(this.m_RepresentativeMean.length());
/*  67 */     stat.m_RepresentativeMedoid = new TimeSeries(this.m_RepresentativeMedoid.length());
/*  68 */     return (ClusStatistic)stat;
/*     */   }
/*     */   
/*     */   public void copy(ClusStatistic other) {
/*  72 */     TimeSeriesStat or = (TimeSeriesStat)other;
/*  73 */     super.copy((ClusStatistic)or);
/*     */ 
/*     */ 
/*     */     
/*  77 */     this.m_TimeSeriesStack.clear();
/*  78 */     this.m_TimeSeriesStack.addAll(or.m_TimeSeriesStack);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TimeSeriesStat normalizedCopy() {
/*  87 */     TimeSeriesStat copy = (TimeSeriesStat)cloneSimple();
/*  88 */     copy.m_NbExamples = 0;
/*  89 */     copy.m_SumWeight = 1.0D;
/*  90 */     copy.m_TimeSeriesStack.add(getTimeSeriesPred());
/*  91 */     copy.m_RepresentativeMean.setValues(this.m_RepresentativeMean.getValues());
/*  92 */     copy.m_RepresentativeMedoid.setValues(this.m_RepresentativeMedoid.getValues());
/*  93 */     return copy;
/*     */   }
/*     */   
/*     */   public void addPrediction(ClusStatistic other, double weight) {
/*  97 */     TimeSeriesStat or = (TimeSeriesStat)other;
/*  98 */     this.m_SumWeight += weight * or.m_SumWeight;
/*  99 */     TimeSeries pred = new TimeSeries(or.getTimeSeriesPred());
/* 100 */     pred.setTSWeight(weight);
/* 101 */     this.m_TimeSeriesStack.add(pred);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void updateWeighted(DataTuple tuple, int idx) {
/* 108 */     super.updateWeighted(tuple, idx);
/* 109 */     TimeSeries newTimeSeries = new TimeSeries((TimeSeries)tuple.m_Objects[0]);
/* 110 */     newTimeSeries.setTSWeight(tuple.getWeight());
/* 111 */     this.m_TimeSeriesStack.add(newTimeSeries);
/*     */   }
/*     */   
/*     */   public double calcDistance(TimeSeries ts1, TimeSeries ts2) {
/* 115 */     TimeSeriesDist dist = (TimeSeriesDist)getDistance();
/* 116 */     return dist.calcDistance(ts1, ts2);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getDispersion(ClusAttributeWeights scale, RowData data) {
/* 123 */     return getSVarS(scale, data);
/*     */   }
/*     */   
/*     */   public double getAbsoluteDistance(DataTuple tuple, ClusAttributeWeights weights) {
/* 127 */     int idx = this.m_Attr.getIndex();
/* 128 */     TimeSeries actual = (TimeSeries)tuple.getObjVal(0);
/* 129 */     return calcDistance(this.m_RepresentativeMean, actual) * weights.getWeight(idx);
/*     */   }
/*     */   
/*     */   public void initNormalizationWeights(ClusAttributeWeights weights, boolean[] shouldNormalize) {
/* 133 */     int idx = this.m_Attr.getIndex();
/* 134 */     if (shouldNormalize[idx]) {
/* 135 */       double var = this.m_SVarS / getTotalWeight();
/* 136 */       double norm = (var > 0.0D) ? (1.0D / var) : 1.0D;
/* 137 */       weights.setWeight((ClusAttrType)this.m_Attr, norm);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void calcSumAndSumSqDistances(TimeSeries prototype) {
/* 142 */     this.m_AvgDistances = 0.0D;
/* 143 */     int count = this.m_TimeSeriesStack.size();
/* 144 */     for (int i = 0; i < count; i++) {
/* 145 */       double dist = calcDistance(prototype, this.m_TimeSeriesStack.get(i));
/* 146 */       this.m_AvgDistances += dist;
/*     */     } 
/* 148 */     this.m_AvgDistances /= count;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void calcMean() {
/* 158 */     this.m_RepresentativeMedoid = null;
/* 159 */     double minDistance = Double.POSITIVE_INFINITY; int i;
/* 160 */     for (i = 0; i < this.m_TimeSeriesStack.size(); i++) {
/* 161 */       double crDistance = 0.0D;
/* 162 */       TimeSeries t1 = this.m_TimeSeriesStack.get(i);
/* 163 */       for (int k = 0; k < this.m_TimeSeriesStack.size(); k++) {
/* 164 */         TimeSeries t2 = this.m_TimeSeriesStack.get(k);
/* 165 */         double dist = calcDistance(t1, t2);
/* 166 */         crDistance += dist * t2.geTSWeight();
/*     */       } 
/* 168 */       if (crDistance < minDistance) {
/* 169 */         this.m_RepresentativeMedoid = this.m_TimeSeriesStack.get(i);
/* 170 */         minDistance = crDistance;
/*     */       } 
/*     */     } 
/* 173 */     calcSumAndSumSqDistances(this.m_RepresentativeMedoid);
/*     */     
/* 175 */     if (this.m_Attr.isEqualLength()) {
/* 176 */       this.m_RepresentativeMean.setSize(this.m_RepresentativeMedoid.length());
/* 177 */       for (i = 0; i < this.m_RepresentativeMean.length(); i++) {
/* 178 */         double sum = 0.0D;
/* 179 */         for (int k = 0; k < this.m_TimeSeriesStack.size(); k++) {
/* 180 */           TimeSeries t1 = this.m_TimeSeriesStack.get(k);
/* 181 */           sum += t1.getValue(i) * t1.geTSWeight();
/*     */         } 
/* 183 */         this.m_RepresentativeMean.setValue(i, sum / this.m_SumWeight);
/*     */       } 
/*     */     } 
/* 186 */     double sumwi = 0.0D;
/* 187 */     for (int j = 0; j < this.m_TimeSeriesStack.size(); j++) {
/* 188 */       TimeSeries t1 = this.m_TimeSeriesStack.get(j);
/* 189 */       sumwi += t1.geTSWeight();
/*     */     } 
/* 191 */     double diff = Math.abs(this.m_SumWeight - sumwi);
/* 192 */     if (diff > 1.0E-6D) {
/* 193 */       System.err.println("Error: Sanity check failed! - " + diff);
/*     */     }
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
/*     */   public void reset() {
/* 228 */     super.reset();
/* 229 */     this.m_TimeSeriesStack.clear();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getString(StatisticPrintInfo info) {
/* 238 */     NumberFormat fr = ClusFormat.SIX_AFTER_DOT;
/* 239 */     StringBuffer buf = new StringBuffer();
/* 240 */     buf.append("Mean: ");
/* 241 */     buf.append(this.m_RepresentativeMean.toString());
/* 242 */     if (info.SHOW_EXAMPLE_COUNT) {
/* 243 */       buf.append(": ");
/* 244 */       buf.append(fr.format(this.m_SumWeight));
/*     */     } 
/* 246 */     buf.append("; ");
/*     */     
/* 248 */     buf.append("Medoid: ");
/* 249 */     buf.append(this.m_RepresentativeMedoid.toString());
/* 250 */     if (info.SHOW_EXAMPLE_COUNT) {
/* 251 */       buf.append(": ");
/* 252 */       buf.append(fr.format(this.m_SumWeight));
/* 253 */       buf.append(", ");
/* 254 */       buf.append(fr.format(this.m_AvgDistances));
/*     */     } 
/* 256 */     buf.append("; ");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 266 */     return buf.toString();
/*     */   }
/*     */   
/*     */   public void addPredictWriterSchema(String prefix, ClusSchema schema) {
/* 270 */     schema.addAttrType((ClusAttrType)new TimeSeriesAttrType(prefix + "-p-TimeSeries"));
/* 271 */     schema.addAttrType((ClusAttrType)new NumericAttrType(prefix + "-p-Distance"));
/* 272 */     schema.addAttrType((ClusAttrType)new NumericAttrType(prefix + "-p-Size"));
/* 273 */     schema.addAttrType((ClusAttrType)new NumericAttrType(prefix + "-p-AvgDist"));
/*     */   }
/*     */   
/*     */   public String getPredictWriterString(DataTuple tuple) {
/* 277 */     StringBuffer buf = new StringBuffer();
/* 278 */     buf.append(this.m_RepresentativeMedoid.toString());
/* 279 */     double dist = calcDistanceToCentroid(tuple);
/* 280 */     buf.append(",");
/* 281 */     buf.append(dist);
/* 282 */     buf.append(",");
/* 283 */     buf.append(getTotalWeight());
/* 284 */     buf.append(",");
/* 285 */     buf.append(this.m_AvgDistances);
/* 286 */     return buf.toString();
/*     */   }
/*     */   
/*     */   public TimeSeries getRepresentativeMean() {
/* 290 */     return this.m_RepresentativeMean;
/*     */   }
/*     */   
/*     */   public TimeSeries getRepresentativeMedoid() {
/* 294 */     return this.m_RepresentativeMedoid;
/*     */   }
/*     */   
/*     */   public TimeSeries getTimeSeriesPred() {
/* 298 */     return this.m_RepresentativeMedoid;
/*     */   }
/*     */   
/*     */   public TimeSeriesAttrType getAttribute() {
/* 302 */     return this.m_Attr;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\ext\timeseries\TimeSeriesStat.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */