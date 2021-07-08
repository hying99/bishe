/*    */ package org.apache.commons.math.stat.descriptive.moment;
/*    */ 
/*    */ import java.io.Serializable;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ThirdMoment
/*    */   extends SecondMoment
/*    */   implements Serializable
/*    */ {
/*    */   static final long serialVersionUID = -7818711964045118679L;
/* 65 */   protected double m3 = Double.NaN;
/* 66 */   protected double nDevSq = Double.NaN;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void increment(double d) {
/* 73 */     if (this.n < 1L) {
/* 74 */       this.m3 = this.m2 = this.m1 = 0.0D;
/*    */     }
/*    */     
/* 77 */     double prevM2 = this.m2;
/* 78 */     super.increment(d);
/* 79 */     this.nDevSq = this.nDev * this.nDev;
/* 80 */     double n0 = this.n;
/* 81 */     this.m3 = this.m3 - 3.0D * this.nDev * prevM2 + (n0 - 1.0D) * (n0 - 2.0D) * this.nDevSq * this.dev;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public double getResult() {
/* 88 */     return this.m3;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void clear() {
/* 95 */     super.clear();
/* 96 */     this.m3 = Double.NaN;
/* 97 */     this.nDevSq = Double.NaN;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\commons-math-1.0.jar!\org\apache\commons\math\stat\descriptive\moment\ThirdMoment.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */