/*    */ package org.jgap.impl;
/*    */ 
/*    */ import org.jgap.Gene;
/*    */ import org.jgap.IChromosome;
/*    */ import org.jgap.IChromosomePool;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ChromosomePool
/*    */   implements IChromosomePool
/*    */ {
/*    */   private static final String CVS_REVISION = "$Revision: 1.12 $";
/* 40 */   private Pool m_chromosomePool = new Pool();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public synchronized IChromosome acquireChromosome() {
/* 55 */     return (IChromosome)this.m_chromosomePool.acquirePooledObject();
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
/*    */   public synchronized void releaseChromosome(IChromosome a_chromosome) {
/* 70 */     if (a_chromosome == null) {
/* 71 */       throw new IllegalArgumentException("Chromosome instance must not be null!");
/*    */     }
/*    */ 
/*    */ 
/*    */ 
/*    */     
/* 77 */     Gene[] genes = a_chromosome.getGenes();
/* 78 */     int size = a_chromosome.size();
/* 79 */     for (int i = 0; i < size; i++) {
/* 80 */       genes[i].cleanup();
/*    */     }
/*    */ 
/*    */     
/* 84 */     this.m_chromosomePool.releaseObject(a_chromosome);
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\impl\ChromosomePool.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */