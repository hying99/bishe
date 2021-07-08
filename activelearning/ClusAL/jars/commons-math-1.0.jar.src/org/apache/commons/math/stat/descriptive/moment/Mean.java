/*     */ package org.apache.commons.math.stat.descriptive.moment;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import org.apache.commons.math.stat.descriptive.AbstractStorelessUnivariateStatistic;
/*     */ import org.apache.commons.math.stat.descriptive.summary.Sum;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Mean
/*     */   extends AbstractStorelessUnivariateStatistic
/*     */   implements Serializable
/*     */ {
/*     */   static final long serialVersionUID = -1296043746617791564L;
/*     */   protected FirstMoment moment;
/*     */   protected boolean incMoment;
/*     */   
/*     */   public Mean() {
/*  68 */     this.incMoment = true;
/*  69 */     this.moment = new FirstMoment();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Mean(FirstMoment m1) {
/*  78 */     this.moment = m1;
/*  79 */     this.incMoment = false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void increment(double d) {
/*  86 */     if (this.incMoment) {
/*  87 */       this.moment.increment(d);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void clear() {
/*  95 */     if (this.incMoment) {
/*  96 */       this.moment.clear();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getResult() {
/* 104 */     return this.moment.m1;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getN() {
/* 111 */     return this.moment.getN();
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
/*     */   public double evaluate(double[] values, int begin, int length) {
/* 131 */     if (test(values, begin, length)) {
/* 132 */       Sum sum = new Sum();
/* 133 */       return sum.evaluate(values, begin, length) / length;
/*     */     } 
/* 135 */     return Double.NaN;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\commons-math-1.0.jar!\org\apache\commons\math\stat\descriptive\moment\Mean.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */