/*    */ package org.apache.commons.math.special;
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
/*    */ public class Erf
/*    */   implements Serializable
/*    */ {
/*    */   public static double erf(double x) throws MathException {
/* 52 */     double ret = Gamma.regularizedGammaP(0.5D, x * x, 1.0E-15D, 10000);
/* 53 */     if (x < 0.0D) {
/* 54 */       ret = -ret;
/*    */     }
/* 56 */     return ret;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\commons-math-1.0.jar!\org\apache\commons\math\special\Erf.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */