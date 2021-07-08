/*     */ package org.jgap.impl.fitness;
/*     */ 
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.jgap.Configuration;
/*     */ import org.jgap.FitnessFunction;
/*     */ import org.jgap.Genotype;
/*     */ import org.jgap.IChromosome;
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
/*     */ public abstract class TruthTableFitnessFunction
/*     */   extends FitnessFunction
/*     */ {
/*     */   private static final String CVS_REVISION = "$Revision: 1.5 $";
/*     */   private Map m_truthTable;
/*     */   public static final int MAX_FITNESS = 9999999;
/*     */   private static final int RELATION_FITNESS = 100;
/*     */   public static final int WORST = 99999;
/*     */   private Configuration m_conf;
/*     */   
/*     */   public TruthTableFitnessFunction() {
/*  52 */     this(Genotype.getStaticConfiguration());
/*     */   }
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
/*     */   public TruthTableFitnessFunction(Configuration a_conf) {
/*  66 */     this.m_conf = a_conf;
/*     */   }
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
/*     */   public TruthTableFitnessFunction(Configuration a_conf, Map a_truthTable) {
/*  82 */     this(a_conf);
/*  83 */     setTruthTable(a_truthTable);
/*     */   }
/*     */   
/*     */   public void setTruthTable(Map a_truthTable) {
/*  87 */     this.m_truthTable = a_truthTable;
/*     */   }
/*     */   
/*     */   public Map getTruthTable() {
/*  91 */     return this.m_truthTable;
/*     */   }
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
/*     */   protected abstract double evaluate(IChromosome paramIChromosome);
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
/*     */   public double calcFitness(Map a_actualInputOutput) {
/* 119 */     double diffAbs = 0.0D;
/*     */ 
/*     */     
/* 122 */     Set keySet = getTruthTable().keySet();
/* 123 */     Iterator<Double> keys = keySet.iterator();
/* 124 */     while (keys.hasNext()) {
/* 125 */       double deltaAbs; Double inputValueWanted = keys.next();
/* 126 */       double outputValueWanted = ((Double)getTruthTable().get(inputValueWanted)).doubleValue();
/*     */       
/* 128 */       Double output = (Double)a_actualInputOutput.get(inputValueWanted);
/* 129 */       if (output != null) {
/* 130 */         double outputValueGiven = output.doubleValue();
/*     */         
/* 132 */         if (Double.isNaN(outputValueWanted)) {
/* 133 */           return Double.NaN;
/*     */         }
/* 135 */         double delta = outputValueGiven - outputValueWanted;
/* 136 */         deltaAbs = (float)Math.abs(delta);
/*     */       } else {
/*     */         
/* 139 */         deltaAbs = Math.abs(outputValueWanted);
/*     */       } 
/* 141 */       diffAbs += deltaAbs;
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 146 */     return diffAbs;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Configuration getConfiguration() {
/* 156 */     return this.m_conf;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\impl\fitness\TruthTableFitnessFunction.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */