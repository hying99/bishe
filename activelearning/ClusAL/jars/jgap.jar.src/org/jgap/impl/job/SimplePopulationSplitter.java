/*    */ package org.jgap.impl.job;
/*    */ 
/*    */ import org.jgap.Population;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class SimplePopulationSplitter
/*    */   implements IPopulationSplitter
/*    */ {
/*    */   private static final String CVS_REVISION = "$Revision: 1.3 $";
/*    */   private int m_count;
/*    */   
/*    */   public SimplePopulationSplitter(int a_count) {
/* 25 */     if (a_count < 1) {
/* 26 */       throw new IllegalArgumentException("Count must be greater than zero!");
/*    */     }
/* 28 */     this.m_count = a_count;
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
/*    */   public Population[] split(Population a_pop) throws Exception {
/* 40 */     int popSize = a_pop.size();
/* 41 */     int count = this.m_count;
/* 42 */     int chunkSize = popSize / count;
/* 43 */     if (chunkSize < 1) {
/* 44 */       chunkSize = 1;
/* 45 */       count = 1;
/*    */     } 
/* 47 */     Population[] result = new Population[count];
/* 48 */     int index = 0;
/* 49 */     for (int i = 0; i < count; i++) {
/* 50 */       Population chunk = new Population(a_pop.getConfiguration(), chunkSize);
/*    */ 
/*    */       
/* 53 */       for (int j = 0; j < chunkSize; j++) {
/* 54 */         chunk.addChromosome(a_pop.getChromosome(index));
/* 55 */         index++;
/*    */       } 
/* 57 */       result[i] = chunk;
/* 58 */       popSize -= chunkSize;
/* 59 */       if (popSize < 1) {
/*    */         break;
/*    */       }
/*    */ 
/*    */       
/* 64 */       if (popSize < chunkSize) {
/* 65 */         chunkSize = popSize;
/*    */       }
/*    */     } 
/* 68 */     return result;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\impl\job\SimplePopulationSplitter.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */