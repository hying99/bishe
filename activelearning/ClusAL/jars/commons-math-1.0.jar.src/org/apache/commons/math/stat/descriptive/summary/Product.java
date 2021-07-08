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
/*     */ public class Product
/*     */   extends AbstractStorelessUnivariateStatistic
/*     */   implements Serializable
/*     */ {
/*     */   static final long serialVersionUID = 2824226005990582538L;
/*  52 */   private long n = 0L;
/*  53 */   private double value = Double.NaN;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void increment(double d) {
/*  60 */     if (this.n == 0L) {
/*  61 */       this.value = d;
/*     */     } else {
/*  63 */       this.value *= d;
/*     */     } 
/*  65 */     this.n++;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getResult() {
/*  72 */     return this.value;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getN() {
/*  79 */     return this.n;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void clear() {
/*  86 */     this.value = Double.NaN;
/*  87 */     this.n = 0L;
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
/*     */   public double evaluate(double[] values, int begin, int length) {
/* 105 */     double product = Double.NaN;
/* 106 */     if (test(values, begin, length)) {
/* 107 */       product = 1.0D;
/* 108 */       for (int i = begin; i < begin + length; i++) {
/* 109 */         product *= values[i];
/*     */       }
/*     */     } 
/* 112 */     return product;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\commons-math-1.0.jar!\org\apache\commons\math\stat\descriptive\summary\Product.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */