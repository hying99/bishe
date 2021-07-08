/*    */ package org.jgap;
/*    */ 
/*    */ import java.io.Serializable;
/*    */ import org.jgap.util.CloneException;
/*    */ import org.jgap.util.ICloneable;
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
/*    */ public abstract class BulkFitnessFunction
/*    */   implements Serializable, ICloneable
/*    */ {
/*    */   private static final String CVS_REVISION = "$Revision: 1.9 $";
/*    */   
/*    */   public abstract void evaluate(Population paramPopulation);
/*    */   
/*    */   public Object clone() {
/*    */     try {
/* 58 */       return super.clone();
/* 59 */     } catch (CloneNotSupportedException cex) {
/* 60 */       throw new CloneException(cex);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\BulkFitnessFunction.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */