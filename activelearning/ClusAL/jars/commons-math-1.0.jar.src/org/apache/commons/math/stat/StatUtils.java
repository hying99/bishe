/*     */ package org.apache.commons.math.stat;
/*     */ 
/*     */ import org.apache.commons.math.stat.descriptive.UnivariateStatistic;
/*     */ import org.apache.commons.math.stat.descriptive.moment.GeometricMean;
/*     */ import org.apache.commons.math.stat.descriptive.moment.Mean;
/*     */ import org.apache.commons.math.stat.descriptive.moment.Variance;
/*     */ import org.apache.commons.math.stat.descriptive.rank.Max;
/*     */ import org.apache.commons.math.stat.descriptive.rank.Min;
/*     */ import org.apache.commons.math.stat.descriptive.rank.Percentile;
/*     */ import org.apache.commons.math.stat.descriptive.summary.Product;
/*     */ import org.apache.commons.math.stat.descriptive.summary.Sum;
/*     */ import org.apache.commons.math.stat.descriptive.summary.SumOfLogs;
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
/*     */ public final class StatUtils
/*     */ {
/*  39 */   private static UnivariateStatistic sum = (UnivariateStatistic)new Sum();
/*     */ 
/*     */   
/*  42 */   private static UnivariateStatistic sumSq = (UnivariateStatistic)new SumOfSquares();
/*     */ 
/*     */   
/*  45 */   private static UnivariateStatistic prod = (UnivariateStatistic)new Product();
/*     */ 
/*     */   
/*  48 */   private static UnivariateStatistic sumLog = (UnivariateStatistic)new SumOfLogs();
/*     */ 
/*     */   
/*  51 */   private static UnivariateStatistic min = (UnivariateStatistic)new Min();
/*     */ 
/*     */   
/*  54 */   private static UnivariateStatistic max = (UnivariateStatistic)new Max();
/*     */ 
/*     */   
/*  57 */   private static UnivariateStatistic mean = (UnivariateStatistic)new Mean();
/*     */ 
/*     */   
/*  60 */   private static Variance variance = new Variance();
/*     */ 
/*     */   
/*  63 */   private static Percentile percentile = new Percentile();
/*     */ 
/*     */   
/*  66 */   private static GeometricMean geometricMean = new GeometricMean();
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
/*     */   public static double sum(double[] values) {
/*  87 */     return sum.evaluate(values);
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
/*     */   public static double sum(double[] values, int begin, int length) {
/* 106 */     return sum.evaluate(values, begin, length);
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
/*     */   public static double sumSq(double[] values) {
/* 121 */     return sumSq.evaluate(values);
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
/*     */   public static double sumSq(double[] values, int begin, int length) {
/* 140 */     return sumSq.evaluate(values, begin, length);
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
/*     */   public static double product(double[] values) {
/* 154 */     return prod.evaluate(values);
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
/*     */   public static double product(double[] values, int begin, int length) {
/* 173 */     return prod.evaluate(values, begin, length);
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
/*     */   public static double sumLog(double[] values) {
/* 190 */     return sumLog.evaluate(values);
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
/*     */   public static double sumLog(double[] values, int begin, int length) {
/* 212 */     return sumLog.evaluate(values, begin, length);
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
/*     */   public static double mean(double[] values) {
/* 229 */     return mean.evaluate(values);
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
/*     */   public static double mean(double[] values, int begin, int length) {
/* 251 */     return mean.evaluate(values, begin, length);
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
/*     */   public static double geometricMean(double[] values) {
/* 268 */     return geometricMean.evaluate(values);
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
/*     */   public static double geometricMean(double[] values, int begin, int length) {
/* 290 */     return geometricMean.evaluate(values, begin, length);
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
/*     */   public static double variance(double[] values) {
/* 310 */     return variance.evaluate(values);
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
/*     */   public static double variance(double[] values, int begin, int length) {
/* 335 */     return variance.evaluate(values, begin, length);
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
/*     */   public static double variance(double[] values, double mean, int begin, int length) {
/* 366 */     return variance.evaluate(values, mean, begin, length);
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
/*     */   public static double variance(double[] values, double mean) {
/* 392 */     return variance.evaluate(values, mean);
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
/*     */   public static double max(double[] values) {
/* 413 */     return max.evaluate(values);
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
/*     */   public static double max(double[] values, int begin, int length) {
/* 440 */     return max.evaluate(values, begin, length);
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
/*     */   public static double min(double[] values) {
/* 461 */     return min.evaluate(values);
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
/*     */   public static double min(double[] values, int begin, int length) {
/* 488 */     return min.evaluate(values, begin, length);
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
/*     */   public static double percentile(double[] values, double p) {
/* 515 */     return percentile.evaluate(values, p);
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
/*     */   public static double percentile(double[] values, int begin, int length, double p) {
/* 547 */     return percentile.evaluate(values, begin, length, p);
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
/*     */   public static double sumDifference(double[] sample1, double[] sample2) throws IllegalArgumentException {
/* 562 */     int n = sample1.length;
/* 563 */     if (n != sample2.length || n < 1) {
/* 564 */       throw new IllegalArgumentException("Input arrays must have the same (positive) length.");
/*     */     }
/*     */     
/* 567 */     double result = 0.0D;
/* 568 */     for (int i = 0; i < n; i++) {
/* 569 */       result += sample1[i] - sample2[i];
/*     */     }
/* 571 */     return result;
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
/*     */   public static double meanDifference(double[] sample1, double[] sample2) throws IllegalArgumentException {
/* 586 */     return sumDifference(sample1, sample2) / sample1.length;
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
/*     */   public static double varianceDifference(double[] sample1, double[] sample2, double meanDifference) throws IllegalArgumentException {
/* 603 */     double sum1 = 0.0D;
/* 604 */     double sum2 = 0.0D;
/* 605 */     double diff = 0.0D;
/* 606 */     int n = sample1.length;
/* 607 */     if (n < 2) {
/* 608 */       throw new IllegalArgumentException("Input array lengths must be at least 2.");
/*     */     }
/* 610 */     for (int i = 0; i < n; i++) {
/* 611 */       diff = sample1[i] - sample2[i];
/* 612 */       sum1 += (diff - meanDifference) * (diff - meanDifference);
/* 613 */       sum2 += diff - meanDifference;
/*     */     } 
/* 615 */     return (sum1 - sum2 * sum2 / n) / (n - 1);
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\commons-math-1.0.jar!\org\apache\commons\math\stat\StatUtils.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */