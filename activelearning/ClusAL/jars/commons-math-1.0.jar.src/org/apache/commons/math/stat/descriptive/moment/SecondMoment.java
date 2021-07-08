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
/*    */ public class SecondMoment
/*    */   extends FirstMoment
/*    */   implements Serializable
/*    */ {
/*    */   static final long serialVersionUID = 3942403127395076445L;
/* 57 */   protected double m2 = Double.NaN;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void increment(double d) {
/* 64 */     if (this.n < 1L) {
/* 65 */       this.m1 = this.m2 = 0.0D;
/*    */     }
/* 67 */     super.increment(d);
/* 68 */     this.m2 += (this.n - 1.0D) * this.dev * this.nDev;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void clear() {
/* 75 */     super.clear();
/* 76 */     this.m2 = Double.NaN;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public double getResult() {
/* 83 */     return this.m2;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\commons-math-1.0.jar!\org\apache\commons\math\stat\descriptive\moment\SecondMoment.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */