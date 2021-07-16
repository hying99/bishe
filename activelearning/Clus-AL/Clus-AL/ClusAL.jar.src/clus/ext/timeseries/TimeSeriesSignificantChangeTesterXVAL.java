/*     */ package clus.ext.timeseries;
/*     */ 
/*     */ import clus.algo.rules.ClusRule;
/*     */ import clus.data.rows.DataTuple;
/*     */ import clus.data.rows.RowData;
/*     */ import clus.data.type.ClusSchema;
/*     */ import clus.data.type.TimeSeriesAttrType;
/*     */ import clus.error.ClusError;
/*     */ import clus.error.ClusErrorList;
/*     */ import clus.model.ClusModel;
/*     */ import clus.selection.ClusSelection;
/*     */ import clus.selection.XValMainSelection;
/*     */ import clus.selection.XValRandomSelection;
/*     */ import clus.selection.XValSelection;
/*     */ import clus.statistic.ClusStatistic;
/*     */ import clus.util.ClusException;
/*     */ import java.io.PrintWriter;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Random;
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
/*     */ public class TimeSeriesSignificantChangeTesterXVAL
/*     */   extends ClusError
/*     */ {
/*     */   public static final long serialVersionUID = 1L;
/*     */   protected TimeSeriesStat m_Stat;
/*     */   protected ArrayList m_FoldsMean;
/*     */   protected ArrayList m_FoldsMedoid;
/*     */   
/*     */   public TimeSeriesSignificantChangeTesterXVAL(ClusErrorList par, TimeSeriesStat stat) {
/*  48 */     super(par);
/*  49 */     this.m_Stat = stat;
/*     */   }
/*     */   
/*     */   public boolean isComputeForModel(String name) {
/*  53 */     return false;
/*     */   }
/*     */   
/*     */   public double computeMeanValue(TimeSeriesAttrType attr, RowData data) {
/*  57 */     int nb = 0;
/*  58 */     double sum = 0.0D;
/*  59 */     for (int i = 0; i < data.getNbRows(); i++) {
/*  60 */       DataTuple tuple = data.getTuple(i);
/*  61 */       double[] series = attr.getTimeSeries(tuple).getValues();
/*  62 */       for (int j = 0; j < series.length; j++) {
/*  63 */         sum += series[j];
/*  64 */         nb++;
/*     */       } 
/*     */     } 
/*  67 */     return sum / nb;
/*     */   }
/*     */   
/*     */   public int getTimeSeriesLength(TimeSeriesAttrType attr, RowData data) {
/*  71 */     int len = 0;
/*  72 */     for (int i = 0; i < data.getNbRows(); i++) {
/*  73 */       DataTuple tuple = data.getTuple(i);
/*  74 */       double[] series = attr.getTimeSeries(tuple).getValues();
/*  75 */       len = Math.max(len, series.length);
/*     */     } 
/*  77 */     return len;
/*     */   }
/*     */   
/*     */   public double computeError(RowData data, TimeSeries ts) {
/*  81 */     double sum = 0.0D;
/*  82 */     TimeSeriesDist dist = (TimeSeriesDist)this.m_Stat.getDistance();
/*  83 */     TimeSeriesAttrType attr = this.m_Stat.getAttribute();
/*  84 */     for (int i = 0; i < data.getNbRows(); i++) {
/*  85 */       DataTuple tuple = data.getTuple(i);
/*  86 */       TimeSeries series = attr.getTimeSeries(tuple);
/*  87 */       sum += dist.calcDistance(series, ts);
/*     */     } 
/*  89 */     return sum / data.getNbRows();
/*     */   }
/*     */ 
/*     */   
/*     */   public void doOneFold(RowData train, RowData test) {
/*  94 */     TimeSeriesAttrType attr = this.m_Stat.getAttribute();
/*  95 */     double mean = computeMeanValue(attr, train);
/*  96 */     double[] mean_series = new double[getTimeSeriesLength(attr, train)];
/*  97 */     Arrays.fill(mean_series, mean);
/*  98 */     TimeSeries mean_ts = new TimeSeries(mean_series);
/*     */     
/* 100 */     TimeSeriesStat stat = (TimeSeriesStat)this.m_Stat.cloneStat();
/* 101 */     train.calcTotalStatBitVector((ClusStatistic)stat);
/* 102 */     stat.calcMean();
/* 103 */     TimeSeries medoid_ts = stat.getRepresentativeMedoid();
/*     */     
/* 105 */     double mean_err = computeError(test, mean_ts);
/* 106 */     double medoid_err = computeError(test, medoid_ts);
/* 107 */     this.m_FoldsMean.add(new Double(mean_err));
/* 108 */     this.m_FoldsMedoid.add(new Double(medoid_err));
/*     */   }
/*     */ 
/*     */   
/*     */   public void computeSignificantChangePValueXVAL(RowData data) throws ClusException {
/* 113 */     this.m_FoldsMean = new ArrayList();
/* 114 */     this.m_FoldsMedoid = new ArrayList();
/* 115 */     Random random = new Random(0L);
/* 116 */     int nbfolds = 10;
/* 117 */     XValRandomSelection xValRandomSelection = new XValRandomSelection(data.getNbRows(), nbfolds, random);
/* 118 */     for (int i = 0; i < nbfolds; i++) {
/* 119 */       XValSelection msel = new XValSelection((XValMainSelection)xValRandomSelection, i);
/* 120 */       RowData train = (RowData)data.cloneData();
/* 121 */       RowData test = (RowData)train.select((ClusSelection)msel);
/* 122 */       doOneFold(train, test);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void computeForRule(ClusRule rule, ClusSchema schema) throws ClusException {
/* 127 */     RowData covered = new RowData(rule.getData(), schema);
/* 128 */     computeSignificantChangePValueXVAL(covered);
/*     */   }
/*     */   
/*     */   public void compute(RowData data, ClusModel model) throws ClusException {
/* 132 */     if (model instanceof ClusRule) {
/* 133 */       computeForRule((ClusRule)model, data.getSchema());
/*     */     }
/*     */   }
/*     */   
/*     */   public void printArray(ArrayList<Double> arr, StringBuffer res) {
/* 138 */     res.append("[");
/* 139 */     double sum = 0.0D;
/* 140 */     for (int i = 0; i < arr.size(); i++) {
/* 141 */       double v = ((Double)arr.get(i)).doubleValue();
/* 142 */       if (i != 0) res.append(","); 
/* 143 */       res.append(String.valueOf(v));
/* 144 */       sum += v;
/*     */     } 
/* 146 */     res.append("]");
/*     */   }
/*     */   
/*     */   public void showModelError(PrintWriter wrt, int detail) {
/* 150 */     StringBuffer res = new StringBuffer();
/* 151 */     res.append("\n");
/* 152 */     res.append("   Medoid: ");
/* 153 */     printArray(this.m_FoldsMedoid, res);
/* 154 */     res.append("\n");
/* 155 */     res.append("   Mean: ");
/* 156 */     printArray(this.m_FoldsMean, res);
/* 157 */     wrt.println(res.toString());
/*     */   }
/*     */   
/*     */   public ClusError getErrorClone(ClusErrorList par) {
/* 161 */     return new TimeSeriesSignificantChangeTesterXVAL(getParent(), this.m_Stat);
/*     */   }
/*     */   
/*     */   public String getName() {
/* 165 */     return "Significant Time Change XVAL";
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\ext\timeseries\TimeSeriesSignificantChangeTesterXVAL.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */