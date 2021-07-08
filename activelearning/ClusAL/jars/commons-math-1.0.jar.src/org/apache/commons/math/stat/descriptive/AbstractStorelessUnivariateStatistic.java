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
/*     */ public abstract class AbstractStorelessUnivariateStatistic
/*     */   extends AbstractUnivariateStatistic
/*     */   implements StorelessUnivariateStatistic, Serializable
/*     */ {
/*     */   static final long serialVersionUID = -44915725420072521L;
/*     */   
/*     */   public double evaluate(double[] values) {
/*  56 */     if (values == null) {
/*  57 */       throw new IllegalArgumentException("input value array is null");
/*     */     }
/*  59 */     return evaluate(values, 0, values.length);
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
/*     */   public double evaluate(double[] values, int begin, int length) {
/*  80 */     if (test(values, begin, length)) {
/*  81 */       clear();
/*  82 */       incrementAll(values, begin, length);
/*     */     } 
/*  84 */     return getResult();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract void clear();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract double getResult();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract void increment(double paramDouble);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void incrementAll(double[] values) {
/* 113 */     if (values == null) {
/* 114 */       throw new IllegalArgumentException("input values array is null");
/*     */     }
/* 116 */     incrementAll(values, 0, values.length);
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
/*     */   public void incrementAll(double[] values, int begin, int length) {
/* 132 */     if (test(values, begin, length)) {
/* 133 */       int k = begin + length;
/* 134 */       for (int i = begin; i < k; i++) {
/* 135 */         increment(values[i]);
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(Object object) {
/* 148 */     if (object == this) {
/* 149 */       return true;
/*     */     }
/* 151 */     if (!(object instanceof AbstractStorelessUnivariateStatistic)) {
/* 152 */       return false;
/*     */     }
/* 154 */     AbstractStorelessUnivariateStatistic stat = (AbstractStorelessUnivariateStatistic)object;
/* 155 */     return (MathUtils.equals(stat.getResult(), getResult()) && MathUtils.equals(stat.getN(), super.getN()));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 165 */     return 31 * (31 + MathUtils.hash(getResult())) + MathUtils.hash(super.getN());
/*     */   }
/*     */   
/*     */   public abstract long getN();
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\commons-math-1.0.jar!\org\apache\commons\math\stat\descriptive\AbstractStorelessUnivariateStatistic.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */