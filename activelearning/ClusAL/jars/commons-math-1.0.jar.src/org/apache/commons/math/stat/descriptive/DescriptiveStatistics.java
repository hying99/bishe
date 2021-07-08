/*     */ package org.apache.commons.math.stat.descriptive;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.util.Arrays;
/*     */ import org.apache.commons.discovery.tools.DiscoverClass;
/*     */ import org.apache.commons.math.stat.descriptive.moment.GeometricMean;
/*     */ import org.apache.commons.math.stat.descriptive.moment.Kurtosis;
/*     */ import org.apache.commons.math.stat.descriptive.moment.Mean;
/*     */ import org.apache.commons.math.stat.descriptive.moment.Skewness;
/*     */ import org.apache.commons.math.stat.descriptive.moment.Variance;
/*     */ import org.apache.commons.math.stat.descriptive.rank.Max;
/*     */ import org.apache.commons.math.stat.descriptive.rank.Min;
/*     */ import org.apache.commons.math.stat.descriptive.rank.Percentile;
/*     */ import org.apache.commons.math.stat.descriptive.summary.Sum;
/*     */ import org.apache.commons.math.stat.descriptive.summary.SumOfSquares;
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
/*     */ 
/*     */ public abstract class DescriptiveStatistics
/*     */   implements StatisticalSummary, Serializable
/*     */ {
/*     */   static final long serialVersionUID = 5188298269533339922L;
/*     */   public static final int INFINITE_WINDOW = -1;
/*     */   
/*     */   public static DescriptiveStatistics newInstance(Class cls) throws InstantiationException, IllegalAccessException {
/*  55 */     return cls.newInstance();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static DescriptiveStatistics newInstance() {
/*  63 */     DescriptiveStatistics factory = null;
/*     */     try {
/*  65 */       DiscoverClass dc = new DiscoverClass();
/*  66 */       factory = (DescriptiveStatistics)dc.newInstance(DescriptiveStatistics.class, "org.apache.commons.math.stat.descriptive.DescriptiveStatisticsImpl");
/*     */     
/*     */     }
/*  69 */     catch (Throwable t) {
/*  70 */       return new DescriptiveStatisticsImpl();
/*     */     } 
/*  72 */     return factory;
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
/*     */   public double getMean() {
/*  95 */     return apply((UnivariateStatistic)new Mean());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getGeometricMean() {
/* 105 */     return apply((UnivariateStatistic)new GeometricMean());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getVariance() {
/* 114 */     return apply((UnivariateStatistic)new Variance());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getStandardDeviation() {
/* 123 */     double stdDev = Double.NaN;
/* 124 */     if (getN() > 0L) {
/* 125 */       if (getN() > 1L) {
/* 126 */         stdDev = Math.sqrt(getVariance());
/*     */       } else {
/* 128 */         stdDev = 0.0D;
/*     */       } 
/*     */     }
/* 131 */     return stdDev;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getSkewness() {
/* 141 */     return apply((UnivariateStatistic)new Skewness());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getKurtosis() {
/* 151 */     return apply((UnivariateStatistic)new Kurtosis());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getMax() {
/* 159 */     return apply((UnivariateStatistic)new Max());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getMin() {
/* 167 */     return apply((UnivariateStatistic)new Min());
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
/*     */   public double getSum() {
/* 181 */     return apply((UnivariateStatistic)new Sum());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getSumsq() {
/* 190 */     return apply((UnivariateStatistic)new SumOfSquares());
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
/*     */   public double[] getSortedValues() {
/* 237 */     double[] sort = getValues();
/* 238 */     Arrays.sort(sort);
/* 239 */     return sort;
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
/*     */   public double getPercentile(double p) {
/* 267 */     return apply((UnivariateStatistic)new Percentile(p));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 278 */     StringBuffer outBuffer = new StringBuffer();
/* 279 */     outBuffer.append("DescriptiveStatistics:\n");
/* 280 */     outBuffer.append("n: " + getN() + "\n");
/* 281 */     outBuffer.append("min: " + getMin() + "\n");
/* 282 */     outBuffer.append("max: " + getMax() + "\n");
/* 283 */     outBuffer.append("mean: " + getMean() + "\n");
/* 284 */     outBuffer.append("std dev: " + getStandardDeviation() + "\n");
/* 285 */     outBuffer.append("median: " + getPercentile(50.0D) + "\n");
/* 286 */     outBuffer.append("skewness: " + getSkewness() + "\n");
/* 287 */     outBuffer.append("kurtosis: " + getKurtosis() + "\n");
/* 288 */     return outBuffer.toString();
/*     */   }
/*     */   
/*     */   public abstract void addValue(double paramDouble);
/*     */   
/*     */   public abstract long getN();
/*     */   
/*     */   public abstract void clear();
/*     */   
/*     */   public abstract int getWindowSize();
/*     */   
/*     */   public abstract void setWindowSize(int paramInt);
/*     */   
/*     */   public abstract double[] getValues();
/*     */   
/*     */   public abstract double getElement(int paramInt);
/*     */   
/*     */   public abstract double apply(UnivariateStatistic paramUnivariateStatistic);
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\commons-math-1.0.jar!\org\apache\commons\math\stat\descriptive\DescriptiveStatistics.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */