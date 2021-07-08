/*     */ package org.jgap.impl.job;
/*     */ 
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Vector;
/*     */ import org.jgap.Chromosome;
/*     */ import org.jgap.Configuration;
/*     */ import org.jgap.Gene;
/*     */ import org.jgap.Genotype;
/*     */ import org.jgap.IChromosome;
/*     */ import org.jgap.distr.IPopulationMerger;
/*     */ import org.jgap.impl.BooleanGene;
/*     */ import org.jgap.impl.DefaultConfiguration;
/*     */ import org.jgap.impl.FittestPopulationMerger;
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
/*     */ public class SimpleJobConsumer
/*     */ {
/*     */   private static final String CVS_REVISION = "$Revision: 1.4 $";
/*  30 */   private List m_jobs = new Vector();
/*  31 */   private List m_results = new Vector();
/*     */   
/*     */   public void pushJobToGrid(IJob a_job) {
/*  34 */     this.m_jobs.add(a_job);
/*     */     
/*  36 */     (new Thread(a_job)).start();
/*     */   }
/*     */ 
/*     */   
/*     */   public void waitForAllJobs() {
/*  41 */     while (this.m_jobs.size() >= 1) {
/*     */ 
/*     */       
/*     */       try {
/*  45 */         Thread.sleep(50L);
/*  46 */       } catch (InterruptedException iex) {
/*  47 */         iex.printStackTrace();
/*     */         break;
/*     */       } 
/*  50 */       Iterator<IJob> it = this.m_jobs.iterator();
/*  51 */       while (it.hasNext()) {
/*  52 */         IJob job = it.next();
/*  53 */         if (job.isFinished()) {
/*  54 */           System.out.println("Another job finished!");
/*  55 */           this.m_results.add(job.getResult());
/*  56 */           it.remove();
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public EvolveResult[] getResults() {
/*  63 */     int size = this.m_results.size();
/*  64 */     if (size < 1) {
/*  65 */       throw new IllegalStateException("No results found!");
/*     */     }
/*  67 */     EvolveResult[] results = new EvolveResult[size];
/*  68 */     for (int i = 0; i < size; i++) {
/*  69 */       results[i] = this.m_results.get(i);
/*     */     }
/*  71 */     return results;
/*     */   }
/*     */ 
/*     */   
/*     */   public void init() throws Exception {
/*  76 */     DefaultConfiguration defaultConfiguration = new DefaultConfiguration();
/*  77 */     defaultConfiguration.setPreservFittestIndividual(true);
/*  78 */     defaultConfiguration.setKeepPopulationSizeConstant(false);
/*     */     
/*  80 */     SimpleJobConsumer gridClient = new SimpleJobConsumer();
/*     */     
/*  82 */     Chromosome chromosome = new Chromosome((Configuration)defaultConfiguration, (Gene)new BooleanGene((Configuration)defaultConfiguration), 16);
/*     */     
/*  84 */     defaultConfiguration.setSampleChromosome((IChromosome)chromosome);
/*  85 */     defaultConfiguration.setPopulationSize(20);
/*  86 */     defaultConfiguration.setFitnessFunction(new MaxFunction());
/*     */     
/*  88 */     Genotype genotype = Genotype.randomInitialGenotype((Configuration)defaultConfiguration);
/*     */     
/*  90 */     IPopulationSplitter popSplitter = new SimplePopulationSplitter(3);
/*  91 */     for (int i = 0; i < 50; i++) {
/*     */       
/*  93 */       List evolves = genotype.getEvolves(popSplitter);
/*  94 */       Iterator<IEvolveJob> it = evolves.iterator();
/*  95 */       while (it.hasNext()) {
/*  96 */         IEvolveJob evolve = it.next();
/*  97 */         gridClient.pushJobToGrid(evolve);
/*     */       } 
/*     */       
/* 100 */       gridClient.waitForAllJobs();
/*     */       
/* 102 */       FittestPopulationMerger fittestPopulationMerger = new FittestPopulationMerger();
/* 103 */       genotype.mergeResults((IPopulationMerger)fittestPopulationMerger, gridClient.getResults());
/*     */     } 
/*     */     
/* 106 */     IChromosome fittest = genotype.getFittestChromosome();
/* 107 */     System.out.println("Best solution: " + fittest.toString());
/*     */   }
/*     */   
/*     */   public static void main(String[] args) throws Exception {
/* 111 */     (new SimpleJobConsumer()).init();
/* 112 */     System.exit(0);
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\impl\job\SimpleJobConsumer.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */