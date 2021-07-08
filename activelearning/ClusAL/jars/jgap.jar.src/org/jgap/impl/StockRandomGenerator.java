/*    */ package org.jgap.impl;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.ObjectInputStream;
/*    */ import java.util.Random;
/*    */ import org.jgap.RandomGenerator;
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
/*    */ public class StockRandomGenerator
/*    */   extends Random
/*    */   implements RandomGenerator, ICloneable, Comparable
/*    */ {
/*    */   private static final String CVS_REVISION = "$Revision: 1.8 $";
/*    */   
/*    */   private void readObject(ObjectInputStream a_inputStream) throws IOException, ClassNotFoundException {
/* 48 */     a_inputStream.defaultReadObject();
/* 49 */     setSeed(System.currentTimeMillis());
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Object clone() {
/* 59 */     StockRandomGenerator result = new StockRandomGenerator();
/* 60 */     result.setSeed(System.currentTimeMillis());
/* 61 */     return result;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public int compareTo(Object a_other) {
/* 72 */     if (a_other.getClass().equals(getClass())) {
/* 73 */       return 0;
/*    */     }
/*    */     
/* 76 */     return getClass().getName().compareTo(a_other.getClass().getName());
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\impl\StockRandomGenerator.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */