/*    */ package org.jgap.impl;
/*    */ 
/*    */ import org.jgap.Configuration;
/*    */ import org.jgap.DefaultFitnessEvaluator;
/*    */ import org.jgap.FitnessEvaluator;
/*    */ import org.jgap.GeneticOperator;
/*    */ import org.jgap.IBreeder;
/*    */ import org.jgap.InvalidConfigurationException;
/*    */ import org.jgap.event.EventManager;
/*    */ import org.jgap.event.IEventManager;
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
/*    */ public class DefaultConfiguration
/*    */   extends Configuration
/*    */   implements ICloneable
/*    */ {
/*    */   private static final String CVS_REVISION = "$Revision: 1.25 $";
/*    */   
/*    */   public DefaultConfiguration() {
/* 34 */     this("", "");
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
/*    */ 
/*    */   
/*    */   public DefaultConfiguration(String a_id, String a_name) {
/* 51 */     super(a_id, a_name);
/*    */     try {
/* 53 */       setBreeder((IBreeder)new GABreeder());
/* 54 */       setRandomGenerator(new StockRandomGenerator());
/* 55 */       setEventManager((IEventManager)new EventManager());
/* 56 */       BestChromosomesSelector bestChromsSelector = new BestChromosomesSelector(this, 0.95D);
/*    */       
/* 58 */       bestChromsSelector.setDoubletteChromosomesAllowed(true);
/* 59 */       addNaturalSelector(bestChromsSelector, true);
/* 60 */       setMinimumPopSizePercent(0);
/*    */       
/* 62 */       setSelectFromPrevGen(1.0D);
/* 63 */       setKeepPopulationSizeConstant(true);
/* 64 */       setFitnessEvaluator((FitnessEvaluator)new DefaultFitnessEvaluator());
/* 65 */       setChromosomePool(new ChromosomePool());
/* 66 */       addGeneticOperator((GeneticOperator)new CrossoverOperator(this));
/* 67 */       addGeneticOperator((GeneticOperator)new MutationOperator(this, 15));
/*    */     }
/* 69 */     catch (InvalidConfigurationException e) {
/* 70 */       throw new RuntimeException("Fatal error: DefaultConfiguration class could not use its own stock configuration values. This should never happen. Please report this as a bug to the JGAP team.");
/*    */     } 
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
/*    */   public Object clone() {
/* 84 */     return super.clone();
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\impl\DefaultConfiguration.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */