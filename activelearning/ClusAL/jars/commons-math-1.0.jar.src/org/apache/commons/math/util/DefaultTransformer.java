/*    */ package org.apache.commons.math.util;
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
/*    */ public class DefaultTransformer
/*    */   implements NumberTransformer, Serializable
/*    */ {
/*    */   static final long serialVersionUID = 4019938025047800455L;
/*    */   
/*    */   public double transform(Object o) throws MathException {
/* 45 */     if (o == null) {
/* 46 */       throw new MathException("Conversion Exception in Transformation, Object is null");
/*    */     }
/*    */     
/* 49 */     if (o instanceof Number) {
/* 50 */       return ((Number)o).doubleValue();
/*    */     }
/*    */     
/*    */     try {
/* 54 */       return (new Double(o.toString())).doubleValue();
/* 55 */     } catch (Exception e) {
/* 56 */       throw new MathException("Conversion Exception in Transformation: " + e.getMessage(), e);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\commons-math-1.0.jar!\org\apache\commons\mat\\util\DefaultTransformer.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */