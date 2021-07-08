/*     */ package org.apache.commons.math.distribution;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import org.apache.commons.math.MathException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class AbstractIntegerDistribution
/*     */   extends AbstractDistribution
/*     */   implements IntegerDistribution, Serializable
/*     */ {
/*     */   static final long serialVersionUID = -1146319659338487221L;
/*     */   
/*     */   public double cumulativeProbability(double x) throws MathException {
/*  59 */     return cumulativeProbability((int)Math.floor(x));
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
/*     */   public abstract double cumulativeProbability(int paramInt) throws MathException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double probability(double x) {
/*  86 */     double fl = Math.floor(x);
/*  87 */     if (fl == x) {
/*  88 */       return super.probability((int)x);
/*     */     }
/*  90 */     return 0.0D;
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
/*     */   public double cumulativeProbability(int x0, int x1) throws MathException {
/* 106 */     if (x0 > x1) {
/* 107 */       throw new IllegalArgumentException("lower endpoint must be less than or equal to upper endpoint");
/*     */     }
/*     */     
/* 110 */     return cumulativeProbability(x1) - cumulativeProbability(x0 - 1);
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
/*     */   public int inverseCumulativeProbability(double p) throws MathException {
/* 125 */     if (p < 0.0D || p > 1.0D) {
/* 126 */       throw new IllegalArgumentException("p must be between 0 and 1.0 (inclusive)");
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 132 */     int x0 = getDomainLowerBound(p);
/* 133 */     int x1 = getDomainUpperBound(p);
/*     */     
/* 135 */     while (x0 < x1) {
/* 136 */       int xm = x0 + (x1 - x0) / 2;
/* 137 */       double d = cumulativeProbability(xm);
/* 138 */       if (d > p) {
/*     */         
/* 140 */         if (xm == x1) {
/*     */ 
/*     */           
/* 143 */           x1--;
/*     */           continue;
/*     */         } 
/* 146 */         x1 = xm;
/*     */         
/*     */         continue;
/*     */       } 
/* 150 */       if (xm == x0) {
/*     */ 
/*     */         
/* 153 */         x0++;
/*     */         continue;
/*     */       } 
/* 156 */       x0 = xm;
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 162 */     double pm = cumulativeProbability(x0);
/* 163 */     while (pm > p) {
/* 164 */       x0--;
/* 165 */       pm = cumulativeProbability(x0);
/*     */     } 
/*     */     
/* 168 */     return x0;
/*     */   }
/*     */   
/*     */   protected abstract int getDomainLowerBound(double paramDouble);
/*     */   
/*     */   protected abstract int getDomainUpperBound(double paramDouble);
/*     */   
/*     */   public abstract double probability(int paramInt);
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\commons-math-1.0.jar!\org\apache\commons\math\distribution\AbstractIntegerDistribution.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */