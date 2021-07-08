/*     */ package org.apache.commons.math.stat.descriptive;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import org.apache.commons.math.stat.descriptive.moment.FirstMoment;
/*     */ import org.apache.commons.math.stat.descriptive.moment.GeometricMean;
/*     */ import org.apache.commons.math.stat.descriptive.moment.Mean;
/*     */ import org.apache.commons.math.stat.descriptive.moment.SecondMoment;
/*     */ import org.apache.commons.math.stat.descriptive.moment.Variance;
/*     */ import org.apache.commons.math.stat.descriptive.rank.Max;
/*     */ import org.apache.commons.math.stat.descriptive.rank.Min;
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
/*     */ public class SummaryStatisticsImpl
/*     */   extends SummaryStatistics
/*     */   implements Serializable
/*     */ {
/*     */   static final long serialVersionUID = 8787174276883311692L;
/*  40 */   protected long n = 0L;
/*     */ 
/*     */   
/*  43 */   protected SecondMoment secondMoment = null;
/*     */ 
/*     */   
/*  46 */   protected Sum sum = null;
/*     */ 
/*     */   
/*  49 */   protected SumOfSquares sumsq = null;
/*     */ 
/*     */   
/*  52 */   protected Min min = null;
/*     */ 
/*     */   
/*  55 */   protected Max max = null;
/*     */ 
/*     */   
/*  58 */   protected SumOfLogs sumLog = null;
/*     */ 
/*     */   
/*  61 */   protected GeometricMean geoMean = null;
/*     */ 
/*     */   
/*  64 */   protected Mean mean = null;
/*     */ 
/*     */   
/*  67 */   protected Variance variance = null;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SummaryStatisticsImpl() {
/*  73 */     this.sum = new Sum();
/*  74 */     this.sumsq = new SumOfSquares();
/*  75 */     this.min = new Min();
/*  76 */     this.max = new Max();
/*  77 */     this.sumLog = new SumOfLogs();
/*  78 */     this.geoMean = new GeometricMean();
/*  79 */     this.secondMoment = new SecondMoment();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addValue(double value) {
/*  88 */     this.sum.increment(value);
/*  89 */     this.sumsq.increment(value);
/*  90 */     this.min.increment(value);
/*  91 */     this.max.increment(value);
/*  92 */     this.sumLog.increment(value);
/*  93 */     this.geoMean.increment(value);
/*  94 */     this.secondMoment.increment(value);
/*  95 */     this.n++;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getN() {
/* 103 */     return this.n;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getSum() {
/* 111 */     return this.sum.getResult();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getSumsq() {
/* 122 */     return this.sumsq.getResult();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getMean() {
/* 133 */     return (new Mean((FirstMoment)this.secondMoment)).getResult();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getStandardDeviation() {
/* 144 */     double stdDev = Double.NaN;
/* 145 */     if (getN() > 0L) {
/* 146 */       if (getN() > 1L) {
/* 147 */         stdDev = Math.sqrt(getVariance());
/*     */       } else {
/* 149 */         stdDev = 0.0D;
/*     */       } 
/*     */     }
/* 152 */     return stdDev;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getVariance() {
/* 163 */     return (new Variance(this.secondMoment)).getResult();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getMax() {
/* 174 */     return this.max.getResult();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getMin() {
/* 185 */     return this.min.getResult();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getGeometricMean() {
/* 196 */     return this.geoMean.getResult();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 206 */     StringBuffer outBuffer = new StringBuffer();
/* 207 */     outBuffer.append("SummaryStatistics:\n");
/* 208 */     outBuffer.append("n: " + getN() + "\n");
/* 209 */     outBuffer.append("min: " + getMin() + "\n");
/* 210 */     outBuffer.append("max: " + getMax() + "\n");
/* 211 */     outBuffer.append("mean: " + getMean() + "\n");
/* 212 */     outBuffer.append("geometric mean: " + getGeometricMean() + "\n");
/* 213 */     outBuffer.append("variance: " + getVariance() + "\n");
/* 214 */     outBuffer.append("sum of squares: " + getSumsq() + "\n");
/* 215 */     outBuffer.append("standard deviation: " + getStandardDeviation() + "\n");
/* 216 */     return outBuffer.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void clear() {
/* 223 */     this.n = 0L;
/* 224 */     this.min.clear();
/* 225 */     this.max.clear();
/* 226 */     this.sum.clear();
/* 227 */     this.sumLog.clear();
/* 228 */     this.sumsq.clear();
/* 229 */     this.geoMean.clear();
/* 230 */     this.secondMoment.clear();
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\commons-math-1.0.jar!\org\apache\commons\math\stat\descriptive\SummaryStatisticsImpl.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */