/*    */ package sit.searchAlgorithm;
/*    */ import clus.data.type.ClusAttrType;
/*    */ import java.util.Date;
/*    */ import java.util.Iterator;
/*    */ import org.jgap.Chromosome;
/*    */ import org.jgap.Configuration;
/*    */ import org.jgap.FitnessFunction;
/*    */ import org.jgap.Gene;
/*    */ import org.jgap.GeneticOperator;
/*    */ import org.jgap.Genotype;
/*    */ import org.jgap.InvalidConfigurationException;
/*    */ import org.jgap.impl.BooleanGene;
/*    */ import org.jgap.impl.DefaultConfiguration;
/*    */ import sit.TargetSet;
/*    */ 
/*    */ public class GeneticSearch extends SearchAlgorithmImpl {
/* 17 */   protected final int MAX_ALLOWED_EVOLUTIONS = 50;
/*    */ 
/*    */   
/*    */   public TargetSet search(ClusAttrType mainTarget, TargetSet candidates) {
/* 21 */     Configuration.reset();
/* 22 */     DefaultConfiguration defaultConfiguration = new DefaultConfiguration();
/* 23 */     FitnessFunction SITFitness = new SITFitnessFunction(mainTarget, this.learner, candidates);
/* 24 */     Genotype population = null;
/*    */ 
/*    */ 
/*    */ 
/*    */     
/*    */     try {
/* 30 */       Chromosome chromosome = new Chromosome((Configuration)defaultConfiguration, (Gene)new BooleanGene((Configuration)defaultConfiguration), candidates.size());
/* 31 */       defaultConfiguration.setSampleChromosome((IChromosome)chromosome);
/* 32 */       defaultConfiguration.setPopulationSize(20);
/* 33 */       defaultConfiguration.setFitnessFunction(SITFitness);
/* 34 */       defaultConfiguration.setPreservFittestIndividual(true);
/* 35 */       defaultConfiguration.setKeepPopulationSizeConstant(false);
/* 36 */       List l = defaultConfiguration.getGeneticOperators();
/* 37 */       Iterator<GeneticOperator> iterator = l.iterator();
/*    */ 
/*    */       
/* 40 */       while (iterator.hasNext()) {
/* 41 */         GeneticOperator o = iterator.next();
/* 42 */         if (o instanceof MutationOperator) {
/* 43 */           ((MutationOperator)o).setMutationRate(1);
/*    */         }
/*    */       } 
/*    */       
/* 47 */       population = Genotype.randomInitialGenotype((Configuration)defaultConfiguration);
/* 48 */     } catch (InvalidConfigurationException e) {
/* 49 */       e.printStackTrace();
/*    */     } 
/*    */ 
/*    */     
/* 53 */     Chromosome bestSolutionSoFar = null;
/* 54 */     Long d = Long.valueOf((new Date()).getTime());
/* 55 */     for (int i = 0; i < 50; i++) {
/*    */       
/* 57 */       population.evolve();
/* 58 */       bestSolutionSoFar = (Chromosome)population.getFittestChromosome();
/*    */       
/* 60 */       Long new_d = Long.valueOf((new Date()).getTime());
/* 61 */       Long dif = Long.valueOf(new_d.longValue() - d.longValue());
/* 62 */       d = new_d;
/*    */       
/* 64 */       System.out.println("Evolution " + (i + 1) + " completed in " + (dif.longValue() / 1000L) + " sec.");
/* 65 */       System.out.print("Best fitness so far:" + (10.0D - bestSolutionSoFar.getFitnessValue()));
/* 66 */       System.out.println("Best support set:" + getTargetSet(candidates, bestSolutionSoFar));
/*    */     } 
/*    */     
/* 69 */     return getTargetSet(candidates, bestSolutionSoFar);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected static final TargetSet getTargetSet(TargetSet t, Chromosome c) {
/* 76 */     Object[] targets = t.toArray();
/* 77 */     TargetSet result = new TargetSet();
/* 78 */     Gene[] genes = c.getGenes();
/* 79 */     for (int i = 0; i < t.size(); i++) {
/* 80 */       if (((BooleanGene)genes[i]).booleanValue()) {
/* 81 */         result.add(targets[i]);
/*    */       }
/*    */     } 
/*    */     
/* 85 */     return result;
/*    */   }
/*    */   
/*    */   public String getName() {
/* 89 */     return "GeneticSearch";
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\sit\searchAlgorithm\GeneticSearch.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */