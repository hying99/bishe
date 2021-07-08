/*     */ package org.apache.commons.math.stat.descriptive.summary;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SumOfLogs
/*     */   extends AbstractStorelessUnivariateStatistic
/*     */   implements Serializable
/*     */ {
/*  60 */   private double value = 0.0D;
/*  61 */   private int n = 0;
/*     */ 
/*     */   
/*     */   static final long serialVersionUID = -370076995648386763L;
/*     */ 
/*     */   
/*     */   public void increment(double d) {
/*  68 */     this.value += Math.log(d);
/*  69 */     this.n++;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getResult() {
/*  76 */     if (this.n > 0) {
/*  77 */       return this.value;
/*     */     }
/*  79 */     return Double.NaN;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getN() {
/*  87 */     return this.n;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void clear() {
/*  94 */     this.value = 0.0D;
/*  95 */     this.n = 0;
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
/* 116 */     double sumLog = Double.NaN;
/* 117 */     if (test(values, begin, length)) {
/* 118 */       sumLog = 0.0D;
/* 119 */       for (int i = begin; i < begin + length; i++) {
/* 120 */         sumLog += Math.log(values[i]);
/*     */       }
/*     */     } 
/* 123 */     return sumLog;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\commons-math-1.0.jar!\org\apache\commons\math\stat\descriptive\summary\SumOfLogs.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */