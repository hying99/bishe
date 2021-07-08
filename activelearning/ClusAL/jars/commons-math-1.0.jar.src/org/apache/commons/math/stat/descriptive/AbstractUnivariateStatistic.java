/*    */ package org.apache.commons.math.stat.descriptive;
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
/*    */ public abstract class AbstractUnivariateStatistic
/*    */   implements UnivariateStatistic, Serializable
/*    */ {
/*    */   static final long serialVersionUID = -8007759382851708045L;
/*    */   
/*    */   public double evaluate(double[] values) {
/* 43 */     test(values, 0, 0);
/* 44 */     return evaluate(values, 0, values.length);
/*    */   }
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
/*    */   public abstract double evaluate(double[] paramArrayOfdouble, int paramInt1, int paramInt2);
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
/*    */   protected boolean test(double[] values, int begin, int length) {
/* 76 */     if (values == null) {
/* 77 */       throw new IllegalArgumentException("input value array is null");
/*    */     }
/*    */     
/* 80 */     if (begin < 0) {
/* 81 */       throw new IllegalArgumentException("start position cannot be negative");
/*    */     }
/*    */     
/* 84 */     if (length < 0) {
/* 85 */       throw new IllegalArgumentException("length cannot be negative");
/*    */     }
/*    */     
/* 88 */     if (begin + length > values.length) {
/* 89 */       throw new IllegalArgumentException("begin + length > values.length");
/*    */     }
/*    */ 
/*    */     
/* 93 */     if (length == 0) {
/* 94 */       return false;
/*    */     }
/*    */     
/* 97 */     return true;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\commons-math-1.0.jar!\org\apache\commons\math\stat\descriptive\AbstractUnivariateStatistic.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */