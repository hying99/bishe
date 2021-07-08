/*    */ package org.apache.commons.math.distribution;
/*    */ 
/*    */ import java.io.Serializable;
/*    */ import org.apache.commons.math.MathException;
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
/*    */ public abstract class AbstractDistribution
/*    */   implements Distribution, Serializable
/*    */ {
/*    */   static final long serialVersionUID = -38038050983108802L;
/*    */   
/*    */   public double cumulativeProbability(double x0, double x1) throws MathException {
/* 59 */     if (x0 > x1) {
/* 60 */       throw new IllegalArgumentException("lower endpoint must be less than or equal to upper endpoint");
/*    */     }
/*    */     
/* 63 */     return super.cumulativeProbability(x1) - super.cumulativeProbability(x0);
/*    */   }
/*    */   
/*    */   public abstract double cumulativeProbability(double paramDouble) throws MathException;
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\commons-math-1.0.jar!\org\apache\commons\math\distribution\AbstractDistribution.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */