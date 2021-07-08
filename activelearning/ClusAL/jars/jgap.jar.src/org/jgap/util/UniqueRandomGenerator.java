/*    */ package org.jgap.util;
/*    */ 
/*    */ import java.util.List;
/*    */ import java.util.Vector;
/*    */ import org.jgap.RandomGenerator;
/*    */ 
/*    */ public class UniqueRandomGenerator
/*    */ {
/*    */   private int m_upper;
/*    */   private List m_resultSet;
/*    */   private RandomGenerator m_generator;
/*    */   
/*    */   public UniqueRandomGenerator(int a_upperBoundary, RandomGenerator a_generator) {
/* 14 */     this.m_upper = a_upperBoundary;
/* 15 */     this.m_resultSet = new Vector();
/* 16 */     this.m_generator = a_generator;
/* 17 */     for (int i = 0; i < this.m_upper; i++) {
/* 18 */       this.m_resultSet.add(new Integer(i));
/*    */     }
/*    */   }
/*    */ 
/*    */   
/*    */   public int nextInt() throws IllegalStateException {
/* 24 */     int size = this.m_resultSet.size();
/* 25 */     if (size < 1) {
/* 26 */       throw new IllegalStateException("No more numbers left");
/*    */     }
/* 28 */     int index = this.m_generator.nextInt(size);
/* 29 */     Integer result = this.m_resultSet.get(index);
/* 30 */     this.m_resultSet.remove(index);
/* 31 */     return result.intValue();
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jga\\util\UniqueRandomGenerator.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */