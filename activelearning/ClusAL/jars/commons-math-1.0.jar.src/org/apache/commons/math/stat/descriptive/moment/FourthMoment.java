/*     */ package org.apache.commons.math.stat.descriptive.moment;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class FourthMoment
/*     */   extends ThirdMoment
/*     */   implements Serializable
/*     */ {
/*     */   static final long serialVersionUID = 4763990447117157611L;
/*  65 */   protected double m4 = Double.NaN;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void increment(double d) {
/*  72 */     if (this.n < 1L) {
/*  73 */       this.m4 = 0.0D;
/*  74 */       this.m3 = 0.0D;
/*  75 */       this.m2 = 0.0D;
/*  76 */       this.m1 = 0.0D;
/*     */     } 
/*     */     
/*  79 */     double prevM3 = this.m3;
/*  80 */     double prevM2 = this.m2;
/*     */     
/*  82 */     super.increment(d);
/*     */     
/*  84 */     double n0 = this.n;
/*     */     
/*  86 */     this.m4 = this.m4 - 4.0D * this.nDev * prevM3 + 6.0D * this.nDevSq * prevM2 + (n0 * n0 - 3.0D * (n0 - 1.0D)) * this.nDevSq * this.nDevSq * (n0 - 1.0D) * n0;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getResult() {
/*  94 */     return this.m4;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void clear() {
/* 101 */     super.clear();
/* 102 */     this.m4 = Double.NaN;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\commons-math-1.0.jar!\org\apache\commons\math\stat\descriptive\moment\FourthMoment.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */