/*     */ package org.jgap.perf;
/*     */ 
/*     */ import java.util.Calendar;
/*     */ import java.util.TimeZone;
/*     */ import org.jgap.Chromosome;
/*     */ import org.jgap.Configuration;
/*     */ import org.jgap.FitnessFunction;
/*     */ import org.jgap.Gene;
/*     */ import org.jgap.Genotype;
/*     */ import org.jgap.IChromosome;
/*     */ import org.jgap.RandomGenerator;
/*     */ import org.jgap.impl.DefaultConfiguration;
/*     */ import org.jgap.impl.IntegerGene;
/*     */ import org.jgap.impl.RandomGeneratorForTesting;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class TestOverallPerformance
/*     */ {
/*     */   private static final String CVS_REVISION = "$Revision: 1.6 $";
/*     */   private static final int MAX_ALLOWED_EVOLUTIONS = 1000;
/*     */   
/*     */   public void makeChangeForAmount(int a_targetChangeAmount) throws Exception {
/*  56 */     Configuration.reset();
/*  57 */     DefaultConfiguration defaultConfiguration = new DefaultConfiguration();
/*  58 */     RandomGeneratorForTesting gen = new RandomGeneratorForTesting();
/*  59 */     gen.setNextDouble(0.5D);
/*  60 */     gen.setNextBoolean(true);
/*  61 */     gen.setNextInt(3);
/*  62 */     gen.setNextFloat(0.7F);
/*  63 */     gen.setNextLong(6L);
/*  64 */     defaultConfiguration.setRandomGenerator((RandomGenerator)gen);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  69 */     FitnessFunction myFunc = new TestOverallPerformanceFitnessFunc(a_targetChangeAmount);
/*     */     
/*  71 */     defaultConfiguration.setFitnessFunction(myFunc);
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
/*  85 */     Gene[] sampleGenes = new Gene[10];
/*  86 */     sampleGenes[0] = (Gene)new IntegerGene((Configuration)defaultConfiguration, 0, 3);
/*  87 */     sampleGenes[1] = (Gene)new IntegerGene((Configuration)defaultConfiguration, 0, 2);
/*  88 */     sampleGenes[2] = (Gene)new IntegerGene((Configuration)defaultConfiguration, 0, 1);
/*  89 */     sampleGenes[3] = (Gene)new IntegerGene((Configuration)defaultConfiguration, 0, 4);
/*  90 */     sampleGenes[4] = (Gene)new IntegerGene((Configuration)defaultConfiguration, 0, 3);
/*  91 */     sampleGenes[5] = (Gene)new IntegerGene((Configuration)defaultConfiguration, 0, 1);
/*  92 */     sampleGenes[6] = (Gene)new IntegerGene((Configuration)defaultConfiguration, 0, 1);
/*  93 */     sampleGenes[7] = (Gene)new IntegerGene((Configuration)defaultConfiguration, 0, 2);
/*  94 */     sampleGenes[8] = (Gene)new IntegerGene((Configuration)defaultConfiguration, 0, 3);
/*  95 */     sampleGenes[9] = (Gene)new IntegerGene((Configuration)defaultConfiguration, 0, 1);
/*  96 */     Chromosome sampleChromosome = new Chromosome((Configuration)defaultConfiguration, sampleGenes);
/*  97 */     defaultConfiguration.setSampleChromosome((IChromosome)sampleChromosome);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 106 */     defaultConfiguration.setPopulationSize(10000);
/*     */ 
/*     */     
/* 109 */     Genotype population = Genotype.randomInitialGenotype((Configuration)defaultConfiguration);
/*     */ 
/*     */ 
/*     */     
/* 113 */     for (int i = 0; i < 1000; i++) {
/* 114 */       population.evolve();
/*     */     }
/*     */ 
/*     */     
/* 118 */     population.getFittestChromosome();
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
/*     */   public static void main(String[] args) throws Exception {
/* 132 */     int amount = 287;
/* 133 */     int numRuns = 20;
/*     */     
/* 135 */     System.out.println("Test started.");
/*     */     
/* 137 */     long starttime = getCurrentMilliseconds();
/* 138 */     for (int i = 0; i < 20; i++) {
/* 139 */       TestOverallPerformance runner = new TestOverallPerformance();
/* 140 */       runner.makeChangeForAmount(287);
/*     */     } 
/*     */     
/* 143 */     long timeMillis = getCurrentMilliseconds() - starttime;
/* 144 */     System.out.println("Overall time needed for executing performance test: " + timeMillis + " [millisecs]");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static long getCurrentMilliseconds() {
/* 152 */     Calendar cal = Calendar.getInstance(TimeZone.getDefault());
/* 153 */     return cal.getTimeInMillis();
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\perf\TestOverallPerformance.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */