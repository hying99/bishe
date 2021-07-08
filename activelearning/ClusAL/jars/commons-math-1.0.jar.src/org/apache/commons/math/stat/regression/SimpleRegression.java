/*     */ package org.apache.commons.math.stat.regression;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import org.apache.commons.math.MathException;
/*     */ import org.apache.commons.math.distribution.DistributionFactory;
/*     */ import org.apache.commons.math.distribution.TDistribution;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SimpleRegression
/*     */   implements Serializable
/*     */ {
/*     */   static final long serialVersionUID = -3004689053607543335L;
/*  60 */   private double sumX = 0.0D;
/*     */ 
/*     */   
/*  63 */   private double sumXX = 0.0D;
/*     */ 
/*     */   
/*  66 */   private double sumY = 0.0D;
/*     */ 
/*     */   
/*  69 */   private double sumYY = 0.0D;
/*     */ 
/*     */   
/*  72 */   private double sumXY = 0.0D;
/*     */ 
/*     */   
/*  75 */   private long n = 0L;
/*     */ 
/*     */   
/*  78 */   private double xbar = 0.0D;
/*     */ 
/*     */   
/*  81 */   private double ybar = 0.0D;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addData(double x, double y) {
/* 106 */     if (this.n == 0L) {
/* 107 */       this.xbar = x;
/* 108 */       this.ybar = y;
/*     */     } else {
/* 110 */       double dx = x - this.xbar;
/* 111 */       double dy = y - this.ybar;
/* 112 */       this.sumXX += dx * dx * this.n / (this.n + 1.0D);
/* 113 */       this.sumYY += dy * dy * this.n / (this.n + 1.0D);
/* 114 */       this.sumXY += dx * dy * this.n / (this.n + 1.0D);
/* 115 */       this.xbar += dx / (this.n + 1.0D);
/* 116 */       this.ybar += dy / (this.n + 1.0D);
/*     */     } 
/* 118 */     this.sumX += x;
/* 119 */     this.sumY += y;
/* 120 */     this.n++;
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
/*     */   public void addData(double[][] data) {
/* 140 */     for (int i = 0; i < data.length; i++) {
/* 141 */       addData(data[i][0], data[i][1]);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void clear() {
/* 149 */     this.sumX = 0.0D;
/* 150 */     this.sumXX = 0.0D;
/* 151 */     this.sumY = 0.0D;
/* 152 */     this.sumYY = 0.0D;
/* 153 */     this.sumXY = 0.0D;
/* 154 */     this.n = 0L;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getN() {
/* 163 */     return this.n;
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
/*     */   public double predict(double x) {
/* 184 */     double b1 = getSlope();
/* 185 */     return getIntercept(b1) + b1 * x;
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
/*     */   public double getIntercept() {
/* 205 */     return getIntercept(getSlope());
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
/*     */   public double getSlope() {
/* 225 */     if (this.n < 2L) {
/* 226 */       return Double.NaN;
/*     */     }
/* 228 */     if (Math.abs(this.sumXX) < 4.9E-323D) {
/* 229 */       return Double.NaN;
/*     */     }
/* 231 */     return this.sumXY / this.sumXX;
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
/*     */   public double getSumSquaredErrors() {
/* 249 */     return getSumSquaredErrors(getSlope());
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
/*     */   public double getTotalSumSquares() {
/* 263 */     if (this.n < 2L) {
/* 264 */       return Double.NaN;
/*     */     }
/* 266 */     return this.sumYY;
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
/*     */   public double getRegressionSumSquares() {
/* 286 */     return getRegressionSumSquares(getSlope());
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
/*     */   public double getMeanSquareError() {
/* 300 */     if (this.n < 3L) {
/* 301 */       return Double.NaN;
/*     */     }
/* 303 */     return getSumSquaredErrors() / (this.n - 2L);
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
/*     */   public double getR() {
/* 321 */     double b1 = getSlope();
/* 322 */     double result = Math.sqrt(getRSquare(b1));
/* 323 */     if (b1 < 0.0D) {
/* 324 */       result = -result;
/*     */     }
/* 326 */     return result;
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
/*     */   public double getRSquare() {
/* 344 */     return getRSquare(getSlope());
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
/*     */   public double getInterceptStdErr() {
/* 359 */     return Math.sqrt(getMeanSquareError() * (1.0D / this.n + this.xbar * this.xbar / this.sumXX));
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
/*     */   public double getSlopeStdErr() {
/* 374 */     return Math.sqrt(getMeanSquareError() / this.sumXX);
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
/*     */   public double getSlopeConfidenceInterval() throws MathException {
/* 401 */     return getSlopeConfidenceInterval(0.05D);
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
/*     */   public double getSlopeConfidenceInterval(double alpha) throws MathException {
/* 437 */     if (alpha >= 1.0D || alpha <= 0.0D) {
/* 438 */       throw new IllegalArgumentException();
/*     */     }
/* 440 */     return getSlopeStdErr() * getTDistribution().inverseCumulativeProbability(1.0D - alpha / 2.0D);
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
/*     */   public double getSignificance() throws MathException {
/* 466 */     return 2.0D * (1.0D - getTDistribution().cumulativeProbability(Math.abs(getSlope()) / getSlopeStdErr()));
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
/*     */   private double getIntercept(double slope) {
/* 481 */     return (this.sumY - slope * this.sumX) / this.n;
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
/*     */   private double getSumSquaredErrors(double b1) {
/* 494 */     return this.sumYY - this.sumXY * this.sumXY / this.sumXX;
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
/*     */   private double getRSquare(double b1) {
/* 506 */     double ssto = getTotalSumSquares();
/* 507 */     return (ssto - getSumSquaredErrors(b1)) / ssto;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private double getRegressionSumSquares(double slope) {
/* 517 */     return slope * slope * this.sumXX;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private TDistribution getTDistribution() {
/* 527 */     return DistributionFactory.newInstance().createTDistribution((this.n - 2L));
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\commons-math-1.0.jar!\org\apache\commons\math\stat\regression\SimpleRegression.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */