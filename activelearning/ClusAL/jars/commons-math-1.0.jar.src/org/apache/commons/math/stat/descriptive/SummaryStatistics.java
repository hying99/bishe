/*     */ package org.apache.commons.math.stat.descriptive;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import org.apache.commons.discovery.tools.DiscoverClass;
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
/*     */ public abstract class SummaryStatistics
/*     */   implements StatisticalSummary, Serializable
/*     */ {
/*     */   static final long serialVersionUID = -6400596334135654825L;
/*     */   
/*     */   public static SummaryStatistics newInstance(Class cls) throws InstantiationException, IllegalAccessException {
/*  46 */     return cls.newInstance();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static SummaryStatistics newInstance() {
/*  55 */     SummaryStatistics instance = null;
/*     */     try {
/*  57 */       DiscoverClass dc = new DiscoverClass();
/*  58 */       instance = (SummaryStatistics)dc.newInstance(SummaryStatistics.class, "org.apache.commons.math.stat.descriptive.SummaryStatisticsImpl");
/*     */     
/*     */     }
/*  61 */     catch (Throwable t) {
/*  62 */       return new SummaryStatisticsImpl();
/*     */     } 
/*  64 */     return instance;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public StatisticalSummary getSummary() {
/*  75 */     return new StatisticalSummaryValues(getMean(), getVariance(), getN(), getMax(), getMin(), getSum());
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
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
/* 157 */     if (object == this) {
/* 158 */       return true;
/*     */     }
/* 160 */     if (!(object instanceof SummaryStatistics)) {
/* 161 */       return false;
/*     */     }
/* 163 */     SummaryStatistics stat = (SummaryStatistics)object;
/* 164 */     return (MathUtils.equals(stat.getGeometricMean(), getGeometricMean()) && MathUtils.equals(stat.getMax(), getMax()) && MathUtils.equals(stat.getMean(), getMean()) && MathUtils.equals(stat.getMin(), getMin()) && MathUtils.equals(stat.getN(), getN()) && MathUtils.equals(stat.getSum(), getSum()) && MathUtils.equals(stat.getSumsq(), getSumsq()) && MathUtils.equals(stat.getVariance(), getVariance()));
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
/*     */   public int hashCode() {
/* 181 */     int result = 31 + MathUtils.hash(getGeometricMean());
/* 182 */     result = result * 31 + MathUtils.hash(getGeometricMean());
/* 183 */     result = result * 31 + MathUtils.hash(getMax());
/* 184 */     result = result * 31 + MathUtils.hash(getMean());
/* 185 */     result = result * 31 + MathUtils.hash(getMin());
/* 186 */     result = result * 31 + MathUtils.hash(getN());
/* 187 */     result = result * 31 + MathUtils.hash(getSum());
/* 188 */     result = result * 31 + MathUtils.hash(getSumsq());
/* 189 */     result = result * 31 + MathUtils.hash(getVariance());
/* 190 */     return result;
/*     */   }
/*     */   
/*     */   public abstract void addValue(double paramDouble);
/*     */   
/*     */   public abstract double getMean();
/*     */   
/*     */   public abstract double getGeometricMean();
/*     */   
/*     */   public abstract double getVariance();
/*     */   
/*     */   public abstract double getStandardDeviation();
/*     */   
/*     */   public abstract double getMax();
/*     */   
/*     */   public abstract double getMin();
/*     */   
/*     */   public abstract long getN();
/*     */   
/*     */   public abstract double getSum();
/*     */   
/*     */   public abstract double getSumsq();
/*     */   
/*     */   public abstract void clear();
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\commons-math-1.0.jar!\org\apache\commons\math\stat\descriptive\SummaryStatistics.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */