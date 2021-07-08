/*     */ package clus.statistic;
/*     */ 
/*     */ import clus.data.attweights.ClusAttributeWeights;
/*     */ import clus.data.cols.ColTarget;
/*     */ import clus.data.rows.DataTuple;
/*     */ import clus.data.rows.RowData;
/*     */ import clus.data.type.ClusSchema;
/*     */ import clus.ext.beamsearch.ClusBeam;
/*     */ import clus.ext.timeseries.TimeSeries;
/*     */ import clus.util.ClusFormat;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintWriter;
/*     */ import java.io.Serializable;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class ClusStatistic
/*     */   implements Serializable
/*     */ {
/*     */   public static final long serialVersionUID = 1L;
/*     */   public double m_SumWeight;
/*     */   public int m_NbExamples;
/*     */   
/*     */   public abstract ClusStatistic cloneStat();
/*     */   
/*     */   public ClusStatistic cloneSimple() {
/*  54 */     return cloneStat();
/*     */   }
/*     */ 
/*     */   
/*     */   public ClusStatistic copyNormalizedWeighted(double weight) {
/*  59 */     System.err.println(getClass().getName() + ": copyNormalizedWeighted(): Not yet implemented");
/*  60 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getNbAttributes() {
/*  68 */     return getNbNominalAttributes() + getNbNumericAttributes();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getNbNominalAttributes() {
/*  75 */     return 0;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getNbNumericAttributes() {
/*  82 */     return 0;
/*     */   }
/*     */ 
/*     */   
/*     */   public void printDebug() {}
/*     */ 
/*     */   
/*     */   public void setSDataSize(int nbex) {}
/*     */ 
/*     */   
/*     */   public void setTrainingStat(ClusStatistic train) {}
/*     */ 
/*     */   
/*     */   public void optimizePreCalc(RowData data) {}
/*     */ 
/*     */   
/*     */   public void showRootInfo() {}
/*     */   
/*     */   public boolean isValidPrediction() {
/* 101 */     return true;
/*     */   }
/*     */   
/*     */   public void update(ColTarget target, int idx) {
/* 105 */     System.err.println(getClass().getName() + ": update(ColTarget target, int idx): Not yet implemented");
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract void updateWeighted(DataTuple paramDataTuple, int paramInt);
/*     */ 
/*     */   
/*     */   public void updateWeighted(DataTuple tuple, double weight) {}
/*     */ 
/*     */   
/*     */   public abstract void calcMean();
/*     */ 
/*     */   
/*     */   public void computePrediction() {
/* 120 */     calcMean();
/*     */   } public abstract String getString(StatisticPrintInfo paramStatisticPrintInfo); public abstract String getPredictedClassName(int paramInt);
/*     */   public abstract String getArrayOfStatistic();
/*     */   public String getString() {
/* 124 */     return getString(StatisticPrintInfo.getInstance());
/*     */   }
/*     */   
/*     */   public String getPredictString() {
/* 128 */     return getString();
/*     */   }
/*     */ 
/*     */   
/*     */   public abstract void reset();
/*     */   
/*     */   public abstract void copy(ClusStatistic paramClusStatistic);
/*     */   
/*     */   public abstract void addPrediction(ClusStatistic paramClusStatistic, double paramDouble);
/*     */   
/*     */   public abstract void add(ClusStatistic paramClusStatistic);
/*     */   
/*     */   public void addData(RowData data) {
/* 141 */     for (int i = 0; i < data.getNbRows(); i++) {
/* 142 */       updateWeighted(data.getTuple(i), 1);
/*     */     }
/*     */   }
/*     */   
/*     */   public void addScaled(double scale, ClusStatistic other) {
/* 147 */     System.err.println(getClass().getName() + ": addScaled(): Not yet implemented");
/*     */   }
/*     */ 
/*     */   
/*     */   public void resetToSimple(double weight) {
/* 152 */     System.err.println(getClass().getName() + ": resetToSimple(): Not yet implemented");
/*     */   }
/*     */   
/*     */   public abstract void subtractFromThis(ClusStatistic paramClusStatistic);
/*     */   
/*     */   public abstract void subtractFromOther(ClusStatistic paramClusStatistic);
/*     */   
/*     */   public double[] getNumericPred() {
/* 160 */     System.err.println(getClass().getName() + ": getNumericPred(): Not yet implemented");
/* 161 */     return null;
/*     */   }
/*     */   
/*     */   public TimeSeries getTimeSeriesPred() {
/* 165 */     System.err.println(getClass().getName() + ": getTimeSeriesPred(): Not yet implemented");
/* 166 */     return null;
/*     */   }
/*     */   
/*     */   public int[] getNominalPred() {
/* 170 */     System.err.println(getClass().getName() + ": getNominalPred(): Not yet implemented");
/* 171 */     return null;
/*     */   }
/*     */   
/*     */   public String getString2() {
/* 175 */     return "";
/*     */   }
/*     */   
/*     */   public String getClassString() {
/* 179 */     return getString();
/*     */   }
/*     */   
/*     */   public String getSimpleString() {
/* 183 */     return ClusFormat.ONE_AFTER_DOT.format(this.m_SumWeight);
/*     */   }
/*     */   
/*     */   public double getTotalWeight() {
/* 187 */     return this.m_SumWeight;
/*     */   }
/*     */   
/*     */   public int getNbExamples() {
/* 191 */     return this.m_NbExamples;
/*     */   }
/*     */   
/*     */   public String getDebugString() {
/* 195 */     return String.valueOf(this.m_SumWeight);
/*     */   }
/*     */   
/*     */   public boolean samePrediction(ClusStatistic other) {
/* 199 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getError() {
/* 209 */     return getError(null);
/*     */   }
/*     */   
/*     */   public double getErrorRel() {
/* 213 */     return getError(null);
/*     */   }
/*     */   
/*     */   public double getErrorDiff(ClusStatistic other) {
/* 217 */     return getErrorDiff(null, other);
/*     */   }
/*     */   
/*     */   public double getError(ClusAttributeWeights scale, RowData data) {
/* 221 */     return getError(scale);
/*     */   }
/*     */ 
/*     */   
/*     */   public double getError(ClusAttributeWeights scale) {
/* 226 */     System.err.println(getClass().getName() + ": getError(): Not yet implemented");
/* 227 */     return Double.POSITIVE_INFINITY;
/*     */   }
/*     */   
/*     */   public double getErrorDiff(ClusAttributeWeights scale, ClusStatistic other) {
/* 231 */     System.err.println(getClass().getName() + ": getErrorDiff(): Not yet implemented");
/* 232 */     return Double.POSITIVE_INFINITY;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getSVarS(ClusAttributeWeights scale) {
/* 241 */     System.err.println(getClass().getName() + ": getSS(): Not yet implemented");
/* 242 */     return Double.POSITIVE_INFINITY;
/*     */   }
/*     */   
/*     */   public double getSVarSDiff(ClusAttributeWeights scale, ClusStatistic other) {
/* 246 */     System.err.println(getClass().getName() + ": getSSDiff(): Not yet implemented");
/* 247 */     return Double.POSITIVE_INFINITY;
/*     */   }
/*     */   
/*     */   public double getSVarS(ClusAttributeWeights scale, RowData data) {
/* 251 */     return getSVarS(scale);
/*     */   }
/*     */   
/*     */   public double getSVarSDiff(ClusAttributeWeights scale, ClusStatistic other, RowData data) {
/* 255 */     return getSVarSDiff(scale, other);
/*     */   }
/*     */   
/*     */   public double getAbsoluteDistance(DataTuple tuple, ClusAttributeWeights weights) {
/* 259 */     return Double.POSITIVE_INFINITY;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getDispersion(ClusAttributeWeights scale, RowData data) {
/* 266 */     System.err.println(getClass().getName() + ": getDispersion(): Not implemented here!");
/* 267 */     return Double.POSITIVE_INFINITY;
/*     */   }
/*     */   
/*     */   public double getSquaredDistance(DataTuple tuple, ClusAttributeWeights weights) {
/* 271 */     return Double.POSITIVE_INFINITY;
/*     */   }
/*     */   
/*     */   public double getSquaredDistance(ClusStatistic other) {
/* 275 */     return Double.POSITIVE_INFINITY;
/*     */   }
/*     */   
/*     */   public static void reset(ClusStatistic[] stat) {
/* 279 */     for (int i = 0; i < stat.length; ) { stat[i].reset(); i++; }
/*     */   
/*     */   }
/*     */   public String toString() {
/* 283 */     return getString();
/*     */   }
/*     */   
/*     */   public String getExtraInfo() {
/* 287 */     return null;
/*     */   }
/*     */   
/*     */   public void printDistribution(PrintWriter wrt) throws IOException {
/* 291 */     wrt.println(getClass().getName() + " does not implement printDistribution()");
/*     */   }
/*     */   
/*     */   public static void calcMeans(ClusStatistic[] stats) {
/* 295 */     for (int i = 0; i < stats.length; i++) {
/* 296 */       stats[i].calcMean();
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void addPredictWriterSchema(String prefix, ClusSchema schema) {}
/*     */   
/*     */   public String getPredictWriterString() {
/* 304 */     return getPredictString();
/*     */   }
/*     */   
/*     */   public String getPredictWriterString(DataTuple tuple) {
/* 308 */     return getPredictWriterString();
/*     */   }
/*     */   
/*     */   public void predictTuple(DataTuple prediction) {
/* 312 */     System.err.println(getClass().getName() + " does not implement predictTuple()");
/*     */   }
/*     */   
/*     */   public RegressionStat getRegressionStat() {
/* 316 */     return null;
/*     */   }
/*     */   
/*     */   public ClassificationStat getClassificationStat() {
/* 320 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public void unionInit() {}
/*     */ 
/*     */   
/*     */   public void unionDone() {}
/*     */ 
/*     */   
/*     */   public void union(ClusStatistic other) {}
/*     */ 
/*     */   
/*     */   public abstract void vote(ArrayList paramArrayList);
/*     */ 
/*     */   
/*     */   public ClusStatistic normalizedCopy() {
/* 337 */     return null;
/*     */   }
/*     */   
/*     */   public ClusDistance getDistance() {
/* 341 */     return null;
/*     */   }
/*     */   
/*     */   public double getCount(int idx, int cls) {
/* 345 */     System.err.println(getClass().getName() + " does not implement predictTuple()");
/* 346 */     return Double.POSITIVE_INFINITY;
/*     */   }
/*     */   
/*     */   public String getDistanceName() {
/* 350 */     return "Unknown Distance";
/*     */   }
/*     */ 
/*     */   
/*     */   public void setBeam(ClusBeam beam) {}
/*     */   
/*     */   public double getSVarS2(ClusAttributeWeights m_ClusteringWeights) {
/* 357 */     throw new UnsupportedOperationException("Not supported yet.");
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\statistic\ClusStatistic.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */