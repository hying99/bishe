/*    */ package org.jgap;
/*    */ 
/*    */ import java.io.Serializable;
/*    */ import java.util.Comparator;
/*    */ import org.jgap.data.config.Configurable;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class NaturalSelector
/*    */   implements INaturalSelector, Configurable
/*    */ {
/*    */   private static final String CVS_REVISION = "$Revision: 1.25 $";
/*    */   private Configuration m_config;
/*    */   
/*    */   public NaturalSelector(Configuration a_config) {
/* 37 */     this.m_config = a_config;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Configuration getConfiguration() {
/* 47 */     return this.m_config;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected abstract void add(IChromosome paramIChromosome);
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public class FitnessValueComparator
/*    */     implements Comparator, Serializable
/*    */   {
/*    */     public int compare(Object first, Object second) {
/* 70 */       IChromosome chrom1 = (IChromosome)first;
/* 71 */       IChromosome chrom2 = (IChromosome)second;
/* 72 */       if (NaturalSelector.this.getConfiguration().getFitnessEvaluator().isFitter(chrom2, chrom1))
/*    */       {
/* 74 */         return 1;
/*    */       }
/* 76 */       if (NaturalSelector.this.getConfiguration().getFitnessEvaluator().isFitter(chrom1, chrom2))
/*    */       {
/* 78 */         return -1;
/*    */       }
/*    */       
/* 81 */       return 0;
/*    */     }
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\NaturalSelector.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */