/*     */ package org.apache.commons.math.stat.descriptive;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import org.apache.commons.math.util.MathUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class StatisticalSummaryValues
/*     */   implements Serializable, StatisticalSummary
/*     */ {
/*     */   static final long serialVersionUID = -5108854841843722536L;
/*     */   private final double mean;
/*     */   private final double variance;
/*     */   private final long n;
/*     */   private final double max;
/*     */   private final double min;
/*     */   private final double sum;
/*     */   
/*     */   public StatisticalSummaryValues(double mean, double variance, long n, double max, double min, double sum) {
/*  63 */     this.mean = mean;
/*  64 */     this.variance = variance;
/*  65 */     this.n = n;
/*  66 */     this.max = max;
/*  67 */     this.min = min;
/*  68 */     this.sum = sum;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getMax() {
/*  75 */     return this.max;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getMean() {
/*  82 */     return this.mean;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getMin() {
/*  89 */     return this.min;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getN() {
/*  96 */     return this.n;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getSum() {
/* 103 */     return this.sum;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getStandardDeviation() {
/* 110 */     return Math.sqrt(this.variance);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getVariance() {
/* 117 */     return this.variance;
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
/*     */   public boolean equals(Object object) {
/* 129 */     if (object == this) {
/* 130 */       return true;
/*     */     }
/* 132 */     if (!(object instanceof StatisticalSummaryValues)) {
/* 133 */       return false;
/*     */     }
/* 135 */     StatisticalSummaryValues stat = (StatisticalSummaryValues)object;
/* 136 */     return (MathUtils.equals(stat.getMax(), getMax()) && MathUtils.equals(stat.getMean(), getMean()) && MathUtils.equals(stat.getMin(), getMin()) && MathUtils.equals(stat.getN(), getN()) && MathUtils.equals(stat.getSum(), getSum()) && MathUtils.equals(stat.getVariance(), getVariance()));
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
/*     */   public int hashCode() {
/* 150 */     int result = 31 + MathUtils.hash(getMax());
/* 151 */     result = result * 31 + MathUtils.hash(getMean());
/* 152 */     result = result * 31 + MathUtils.hash(getMin());
/* 153 */     result = result * 31 + MathUtils.hash(getN());
/* 154 */     result = result * 31 + MathUtils.hash(getSum());
/* 155 */     result = result * 31 + MathUtils.hash(getVariance());
/* 156 */     return result;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\commons-math-1.0.jar!\org\apache\commons\math\stat\descriptive\StatisticalSummaryValues.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */