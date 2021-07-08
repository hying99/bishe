/*     */ package org.jgap.distr;
/*     */ 
/*     */ import org.jgap.Chromosome;
/*     */ import org.jgap.FitnessFunction;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Problem
/*     */ {
/*     */   private static final String CVS_REVISION = "$Revision: 1.2 $";
/*     */   private FitnessFunction m_fitFunc;
/*     */   private int m_populationSize;
/*     */   private Object m_ID;
/*     */   private Chromosome[] m_initialChroms;
/*     */   
/*     */   public Problem() {}
/*     */   
/*     */   public Problem(FitnessFunction a_fitFunc, int a_popSize, Chromosome[] a_initialChroms) throws IllegalArgumentException {
/*  47 */     if (a_fitFunc == null) {
/*  48 */       throw new IllegalArgumentException("Fitness function must not be null!");
/*     */     }
/*  50 */     if (a_popSize <= 0) {
/*  51 */       throw new IllegalArgumentException("Population size must be greater zero.");
/*     */     }
/*  53 */     this.m_fitFunc = a_fitFunc;
/*  54 */     this.m_populationSize = a_popSize;
/*  55 */     this.m_initialChroms = a_initialChroms;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setID(Object a_ID) {
/*  65 */     this.m_ID = a_ID;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object getID() {
/*  75 */     return this.m_ID;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getPopulationSize() {
/*  85 */     return this.m_populationSize;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public FitnessFunction getFitnessFunction() {
/*  95 */     return this.m_fitFunc;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Chromosome[] getChromosomes() {
/* 105 */     return this.m_initialChroms;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\distr\Problem.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */