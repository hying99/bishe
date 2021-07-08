/*    */ package org.jgap;
/*    */ 
/*    */ import junitx.util.PrivateAccessor;
/*    */ import org.jgap.event.EventManager;
/*    */ import org.jgap.event.IEventManager;
/*    */ import org.jgap.impl.BestChromosomesSelector;
/*    */ import org.jgap.impl.BooleanGene;
/*    */ import org.jgap.impl.DefaultMutationRateCalculator;
/*    */ import org.jgap.impl.IntegerGene;
/*    */ import org.jgap.impl.MutationOperator;
/*    */ import org.jgap.impl.StaticFitnessFunction;
/*    */ import org.jgap.impl.StockRandomGenerator;
/*    */ import org.jgap.impl.StringGene;
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
/*    */ public class ConfigurationForTesting
/*    */   extends Configuration
/*    */ {
/*    */   private static final String CVS_REVISION = "$Revision: 1.1 $";
/*    */   public static final double STATIC_FITNESS_VALUE = 2.3D;
/*    */   
/*    */   public ConfigurationForTesting() throws InvalidConfigurationException {
/* 40 */     setPopulationSize(5);
/* 41 */     reset();
/* 42 */     setFitnessFunction((FitnessFunction)new StaticFitnessFunction(2.3D));
/* 43 */     setEventManager((IEventManager)new EventManager());
/* 44 */     setFitnessEvaluator(new DefaultFitnessEvaluator());
/* 45 */     addNaturalSelector((NaturalSelector)new BestChromosomesSelector(this), true);
/* 46 */     addGeneticOperator((GeneticOperator)new MutationOperator(this, (IUniversalRateCalculator)new DefaultMutationRateCalculator(this)));
/* 47 */     setRandomGenerator((RandomGenerator)new StockRandomGenerator());
/* 48 */     Gene[] genes = new Gene[3];
/* 49 */     BooleanGene booleanGene = new BooleanGene(this);
/* 50 */     genes[0] = (Gene)booleanGene;
/* 51 */     StringGene stringGene = new StringGene(this, 1, 10, "abcdefghijklmnopqrstuvwxyz");
/* 52 */     genes[1] = (Gene)stringGene;
/* 53 */     IntegerGene integerGene = new IntegerGene(this, 100, 300);
/* 54 */     genes[2] = (Gene)integerGene;
/* 55 */     Chromosome chrom = new Chromosome(this, genes);
/* 56 */     setSampleChromosome(chrom);
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
/*    */   public synchronized void setRandomGenerator(RandomGenerator a_generatorToSet) throws InvalidConfigurationException {
/*    */     try {
/* 71 */       PrivateAccessor.setField(this, "m_randomGenerator", a_generatorToSet);
/*    */     
/*    */     }
/* 74 */     catch (NoSuchFieldException nex) {
/* 75 */       throw new InvalidConfigurationException(nex.getMessage());
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\ConfigurationForTesting.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */