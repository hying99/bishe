/*     */ package org.apache.commons.math.stat.descriptive.moment;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import org.apache.commons.math.stat.descriptive.AbstractStorelessUnivariateStatistic;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class StandardDeviation
/*     */   extends AbstractStorelessUnivariateStatistic
/*     */   implements Serializable
/*     */ {
/*     */   static final long serialVersionUID = 5728716329662425188L;
/*  46 */   private Variance variance = null;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public StandardDeviation() {
/*  53 */     this.variance = new Variance();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public StandardDeviation(SecondMoment m2) {
/*  62 */     this.variance = new Variance(m2);
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
/*     */   public StandardDeviation(boolean isBiasCorrected) {
/*  76 */     this.variance = new Variance(isBiasCorrected);
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
/*     */   public StandardDeviation(boolean isBiasCorrected, SecondMoment m2) {
/*  91 */     this.variance = new Variance(isBiasCorrected, m2);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void increment(double d) {
/*  98 */     this.variance.increment(d);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getN() {
/* 105 */     return this.variance.getN();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getResult() {
/* 112 */     return Math.sqrt(this.variance.getResult());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void clear() {
/* 119 */     this.variance.clear();
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
/*     */   public double evaluate(double[] values) {
/* 137 */     return Math.sqrt(this.variance.evaluate(values));
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
/*     */   public double evaluate(double[] values, int begin, int length) {
/* 159 */     return Math.sqrt(this.variance.evaluate(values, begin, length));
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
/*     */   public double evaluate(double[] values, double mean, int begin, int length) {
/* 188 */     return Math.sqrt(this.variance.evaluate(values, mean, begin, length));
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
/*     */   public double evaluate(double[] values, double mean) {
/* 213 */     return Math.sqrt(this.variance.evaluate(values, mean));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isBiasCorrected() {
/* 220 */     return this.variance.isBiasCorrected();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setBiasCorrected(boolean isBiasCorrected) {
/* 227 */     this.variance.setBiasCorrected(isBiasCorrected);
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\commons-math-1.0.jar!\org\apache\commons\math\stat\descriptive\moment\StandardDeviation.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */