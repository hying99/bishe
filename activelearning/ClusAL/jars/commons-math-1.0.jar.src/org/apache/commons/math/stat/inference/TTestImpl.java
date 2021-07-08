/*      */ package org.apache.commons.math.stat.inference;
/*      */ 
/*      */ import org.apache.commons.math.MathException;
/*      */ import org.apache.commons.math.distribution.DistributionFactory;
/*      */ import org.apache.commons.math.distribution.TDistribution;
/*      */ import org.apache.commons.math.stat.StatUtils;
/*      */ import org.apache.commons.math.stat.descriptive.StatisticalSummary;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class TTestImpl
/*      */   implements TTest
/*      */ {
/*   35 */   private DistributionFactory distributionFactory = null;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public double pairedT(double[] sample1, double[] sample2) throws IllegalArgumentException, MathException {
/*   66 */     if (sample1 == null || sample2 == null || Math.min(sample1.length, sample2.length) < 2)
/*      */     {
/*   68 */       throw new IllegalArgumentException("insufficient data for t statistic");
/*      */     }
/*   70 */     double meanDifference = StatUtils.meanDifference(sample1, sample2);
/*   71 */     return t(meanDifference, 0.0D, StatUtils.varianceDifference(sample1, sample2, meanDifference), sample1.length);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public double pairedTTest(double[] sample1, double[] sample2) throws IllegalArgumentException, MathException {
/*  111 */     double meanDifference = StatUtils.meanDifference(sample1, sample2);
/*  112 */     return tTest(meanDifference, 0.0D, StatUtils.varianceDifference(sample1, sample2, meanDifference), sample1.length);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean pairedTTest(double[] sample1, double[] sample2, double alpha) throws IllegalArgumentException, MathException {
/*  151 */     if (alpha <= 0.0D || alpha > 0.5D) {
/*  152 */       throw new IllegalArgumentException("bad significance level: " + alpha);
/*      */     }
/*  154 */     return (pairedTTest(sample1, sample2) < alpha);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public double t(double mu, double[] observed) throws IllegalArgumentException {
/*  174 */     if (observed == null || observed.length < 2) {
/*  175 */       throw new IllegalArgumentException("insufficient data for t statistic");
/*      */     }
/*  177 */     return t(StatUtils.mean(observed), mu, StatUtils.variance(observed), observed.length);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public double t(double mu, StatisticalSummary sampleStats) throws IllegalArgumentException {
/*  199 */     if (sampleStats == null || sampleStats.getN() < 2L) {
/*  200 */       throw new IllegalArgumentException("insufficient data for t statistic");
/*      */     }
/*  202 */     return t(sampleStats.getMean(), mu, sampleStats.getVariance(), sampleStats.getN());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public double homoscedasticT(double[] sample1, double[] sample2) throws IllegalArgumentException {
/*  241 */     if (sample1 == null || sample2 == null || Math.min(sample1.length, sample2.length) < 2)
/*      */     {
/*  243 */       throw new IllegalArgumentException("insufficient data for t statistic");
/*      */     }
/*  245 */     return homoscedasticT(StatUtils.mean(sample1), StatUtils.mean(sample2), StatUtils.variance(sample1), StatUtils.variance(sample2), sample1.length, sample2.length);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public double t(double[] sample1, double[] sample2) throws IllegalArgumentException {
/*  280 */     if (sample1 == null || sample2 == null || Math.min(sample1.length, sample2.length) < 2)
/*      */     {
/*  282 */       throw new IllegalArgumentException("insufficient data for t statistic");
/*      */     }
/*  284 */     return t(StatUtils.mean(sample1), StatUtils.mean(sample2), StatUtils.variance(sample1), StatUtils.variance(sample2), sample1.length, sample2.length);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public double t(StatisticalSummary sampleStats1, StatisticalSummary sampleStats2) throws IllegalArgumentException {
/*  323 */     if (sampleStats1 == null || sampleStats2 == null || Math.min(sampleStats1.getN(), sampleStats2.getN()) < 2L)
/*      */     {
/*      */       
/*  326 */       throw new IllegalArgumentException("insufficient data for t statistic");
/*      */     }
/*  328 */     return t(sampleStats1.getMean(), sampleStats2.getMean(), sampleStats1.getVariance(), sampleStats2.getVariance(), sampleStats1.getN(), sampleStats2.getN());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public double homoscedasticT(StatisticalSummary sampleStats1, StatisticalSummary sampleStats2) throws IllegalArgumentException {
/*  371 */     if (sampleStats1 == null || sampleStats2 == null || Math.min(sampleStats1.getN(), sampleStats2.getN()) < 2L)
/*      */     {
/*      */       
/*  374 */       throw new IllegalArgumentException("insufficient data for t statistic");
/*      */     }
/*  376 */     return homoscedasticT(sampleStats1.getMean(), sampleStats2.getMean(), sampleStats1.getVariance(), sampleStats2.getVariance(), sampleStats1.getN(), sampleStats2.getN());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public double tTest(double mu, double[] sample) throws IllegalArgumentException, MathException {
/*  409 */     if (sample == null || sample.length < 2) {
/*  410 */       throw new IllegalArgumentException("insufficient data for t statistic");
/*      */     }
/*  412 */     return tTest(StatUtils.mean(sample), mu, StatUtils.variance(sample), sample.length);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean tTest(double mu, double[] sample, double alpha) throws IllegalArgumentException, MathException {
/*  453 */     if (alpha <= 0.0D || alpha > 0.5D) {
/*  454 */       throw new IllegalArgumentException("bad significance level: " + alpha);
/*      */     }
/*  456 */     return (tTest(mu, sample) < alpha);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public double tTest(double mu, StatisticalSummary sampleStats) throws IllegalArgumentException, MathException {
/*  489 */     if (sampleStats == null || sampleStats.getN() < 2L) {
/*  490 */       throw new IllegalArgumentException("insufficient data for t statistic");
/*      */     }
/*  492 */     return tTest(sampleStats.getMean(), mu, sampleStats.getVariance(), sampleStats.getN());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean tTest(double mu, StatisticalSummary sampleStats, double alpha) throws IllegalArgumentException, MathException {
/*  535 */     if (alpha <= 0.0D || alpha > 0.5D) {
/*  536 */       throw new IllegalArgumentException("bad significance level: " + alpha);
/*      */     }
/*  538 */     return (tTest(mu, sampleStats) < alpha);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public double tTest(double[] sample1, double[] sample2) throws IllegalArgumentException, MathException {
/*  579 */     if (sample1 == null || sample2 == null || Math.min(sample1.length, sample2.length) < 2)
/*      */     {
/*  581 */       throw new IllegalArgumentException("insufficient data");
/*      */     }
/*  583 */     return tTest(StatUtils.mean(sample1), StatUtils.mean(sample2), StatUtils.variance(sample1), StatUtils.variance(sample2), sample1.length, sample2.length);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public double homoscedasticTTest(double[] sample1, double[] sample2) throws IllegalArgumentException, MathException {
/*  623 */     if (sample1 == null || sample2 == null || Math.min(sample1.length, sample2.length) < 2)
/*      */     {
/*  625 */       throw new IllegalArgumentException("insufficient data");
/*      */     }
/*  627 */     return homoscedasticTTest(StatUtils.mean(sample1), StatUtils.mean(sample2), StatUtils.variance(sample1), StatUtils.variance(sample2), sample1.length, sample2.length);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean tTest(double[] sample1, double[] sample2, double alpha) throws IllegalArgumentException, MathException {
/*  688 */     if (alpha <= 0.0D || alpha > 0.5D) {
/*  689 */       throw new IllegalArgumentException("bad significance level: " + alpha);
/*      */     }
/*  691 */     return (tTest(sample1, sample2) < alpha);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean homoscedasticTTest(double[] sample1, double[] sample2, double alpha) throws IllegalArgumentException, MathException {
/*  748 */     if (alpha <= 0.0D || alpha > 0.5D) {
/*  749 */       throw new IllegalArgumentException("bad significance level: " + alpha);
/*      */     }
/*  751 */     return (homoscedasticTTest(sample1, sample2) < alpha);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public double tTest(StatisticalSummary sampleStats1, StatisticalSummary sampleStats2) throws IllegalArgumentException, MathException {
/*  790 */     if (sampleStats1 == null || sampleStats2 == null || Math.min(sampleStats1.getN(), sampleStats2.getN()) < 2L)
/*      */     {
/*  792 */       throw new IllegalArgumentException("insufficient data for t statistic");
/*      */     }
/*  794 */     return tTest(sampleStats1.getMean(), sampleStats2.getMean(), sampleStats1.getVariance(), sampleStats2.getVariance(), sampleStats1.getN(), sampleStats2.getN());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public double homoscedasticTTest(StatisticalSummary sampleStats1, StatisticalSummary sampleStats2) throws IllegalArgumentException, MathException {
/*  835 */     if (sampleStats1 == null || sampleStats2 == null || Math.min(sampleStats1.getN(), sampleStats2.getN()) < 2L)
/*      */     {
/*  837 */       throw new IllegalArgumentException("insufficient data for t statistic");
/*      */     }
/*  839 */     return homoscedasticTTest(sampleStats1.getMean(), sampleStats2.getMean(), sampleStats1.getVariance(), sampleStats2.getVariance(), sampleStats1.getN(), sampleStats2.getN());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean tTest(StatisticalSummary sampleStats1, StatisticalSummary sampleStats2, double alpha) throws IllegalArgumentException, MathException {
/*  901 */     if (alpha <= 0.0D || alpha > 0.5D) {
/*  902 */       throw new IllegalArgumentException("bad significance level: " + alpha);
/*      */     }
/*  904 */     return (tTest(sampleStats1, sampleStats2) < alpha);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected DistributionFactory getDistributionFactory() {
/*  914 */     if (this.distributionFactory == null) {
/*  915 */       this.distributionFactory = DistributionFactory.newInstance();
/*      */     }
/*  917 */     return this.distributionFactory;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected double df(double v1, double v2, double n1, double n2) {
/*  930 */     return (v1 / n1 + v2 / n2) * (v1 / n1 + v2 / n2) / (v1 * v1 / n1 * n1 * (n1 - 1.0D) + v2 * v2 / n2 * n2 * (n2 - 1.0D));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected double t(double m, double mu, double v, double n) {
/*  945 */     return (m - mu) / Math.sqrt(v / n);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected double t(double m1, double m2, double v1, double v2, double n1, double n2) {
/*  963 */     return (m1 - m2) / Math.sqrt(v1 / n1 + v2 / n2);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected double homoscedasticT(double m1, double m2, double v1, double v2, double n1, double n2) {
/*  980 */     double pooledVariance = ((n1 - 1.0D) * v1 + (n2 - 1.0D) * v2) / (n1 + n2 - 2.0D);
/*  981 */     return (m1 - m2) / Math.sqrt(pooledVariance * (1.0D / n1 + 1.0D / n2));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected double tTest(double m, double mu, double v, double n) throws MathException {
/*  996 */     double t = Math.abs(t(m, mu, v, n));
/*  997 */     TDistribution tDistribution = getDistributionFactory().createTDistribution(n - 1.0D);
/*      */     
/*  999 */     return 1.0D - tDistribution.cumulativeProbability(-t, t);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected double tTest(double m1, double m2, double v1, double v2, double n1, double n2) throws MathException {
/* 1020 */     double t = Math.abs(t(m1, m2, v1, v2, n1, n2));
/* 1021 */     double degreesOfFreedom = 0.0D;
/* 1022 */     degreesOfFreedom = df(v1, v2, n1, n2);
/* 1023 */     TDistribution tDistribution = getDistributionFactory().createTDistribution(degreesOfFreedom);
/*      */     
/* 1025 */     return 1.0D - tDistribution.cumulativeProbability(-t, t);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected double homoscedasticTTest(double m1, double m2, double v1, double v2, double n1, double n2) throws MathException {
/* 1046 */     double t = Math.abs(t(m1, m2, v1, v2, n1, n2));
/* 1047 */     double degreesOfFreedom = 0.0D;
/* 1048 */     degreesOfFreedom = n1 + n2 - 2.0D;
/* 1049 */     TDistribution tDistribution = getDistributionFactory().createTDistribution(degreesOfFreedom);
/*      */     
/* 1051 */     return 1.0D - tDistribution.cumulativeProbability(-t, t);
/*      */   }
/*      */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\commons-math-1.0.jar!\org\apache\commons\math\stat\inference\TTestImpl.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */