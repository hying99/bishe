/*     */ package org.jgap.audit;
/*     */ 
/*     */ import java.util.Hashtable;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Vector;
/*     */ import org.jgap.Configuration;
/*     */ import org.jgap.Genotype;
/*     */ import org.jgap.IChromosome;
/*     */ import org.jgap.InvalidConfigurationException;
/*     */ import org.jgap.Population;
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
/*     */ public class Evaluator
/*     */ {
/*     */   private static final String CVS_REVISION = "$Revision: 1.11 $";
/*     */   private Map m_permutationData;
/*     */   private Map m_permutationRuns;
/*     */   private KeyedValues2D m_data;
/*     */   private PermutingConfiguration m_permConf;
/*     */   private Map m_genotypeData;
/*     */   private List m_genotypeDataAvg;
/*     */   
/*     */   public Evaluator(PermutingConfiguration a_conf) {
/*  57 */     if (a_conf == null) {
/*  58 */       throw new IllegalArgumentException("Configuration must not be null!");
/*     */     }
/*  60 */     this.m_permConf = a_conf;
/*  61 */     this.m_data = new KeyedValues2D();
/*  62 */     this.m_permutationData = new Hashtable<Object, Object>();
/*  63 */     this.m_permutationRuns = new Hashtable<Object, Object>();
/*  64 */     this.m_genotypeData = new Hashtable<Object, Object>();
/*  65 */     this.m_genotypeDataAvg = new Vector();
/*     */   }
/*     */   
/*     */   public boolean hasNext() {
/*  69 */     return this.m_permConf.hasNext();
/*     */   }
/*     */ 
/*     */   
/*     */   public Configuration next() throws InvalidConfigurationException {
/*  74 */     return this.m_permConf.next();
/*     */   }
/*     */ 
/*     */   
/*     */   public void setValue(double a_value, Comparable a_rowKey, Comparable a_columnKey) {
/*  79 */     this.m_data.setValue(new Double(a_value), a_rowKey, a_columnKey);
/*     */   }
/*     */ 
/*     */   
/*     */   public Number getValue(Comparable rowKey, Comparable columnKey) {
/*  84 */     return this.m_data.getValue(rowKey, columnKey);
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
/*     */   public void setValue(int a_permutation, int a_run, double a_value, Comparable a_rowKey, Comparable a_columnKey) {
/* 100 */     Object key = createKey(a_permutation, a_run);
/* 101 */     KeyedValues2D a_data = (KeyedValues2D)this.m_permutationData.get(key);
/*     */     
/* 103 */     if (a_data == null) {
/* 104 */       a_data = new KeyedValues2D();
/* 105 */       this.m_permutationData.put(key, a_data);
/*     */     } 
/*     */ 
/*     */     
/* 109 */     addRunNumber(a_permutation, a_run);
/* 110 */     a_data.setValue(new Double(a_value), a_rowKey, a_columnKey);
/*     */   }
/*     */   
/*     */   protected void addRunNumber(int a_permutation, int a_run) {
/* 114 */     Map<Object, Object> v = (Map)this.m_permutationRuns.get(new Integer(a_permutation));
/* 115 */     if (v == null) {
/* 116 */       v = new Hashtable<Object, Object>();
/*     */     }
/* 118 */     v.put(new Integer(a_run), new Integer(a_run));
/* 119 */     this.m_permutationRuns.put(new Integer(a_permutation), v);
/*     */   }
/*     */ 
/*     */   
/*     */   public Number getValue(int a_permutation, int a_run, Comparable rowKey, Comparable columnKey) {
/* 124 */     KeyedValues2D a_data = (KeyedValues2D)this.m_permutationData.get(createKey(a_permutation, a_run));
/*     */     
/* 126 */     if (a_data == null) {
/* 127 */       return null;
/*     */     }
/* 129 */     return a_data.getValue(rowKey, columnKey);
/*     */   }
/*     */   
/*     */   public KeyedValues2D getData() {
/* 133 */     return this.m_data;
/*     */   }
/*     */   
/*     */   protected Object createKey(int a_permutation, int a_run) {
/* 137 */     return a_permutation + "_" + a_run;
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
/*     */   public KeyedValues2D calcAvgFitness(int a_permutation) {
/* 152 */     if (a_permutation == -1) {
/* 153 */       Iterator<Integer> it = this.m_permutationRuns.keySet().iterator();
/*     */ 
/*     */       
/* 156 */       KeyedValues2D result = new KeyedValues2D();
/* 157 */       while (it.hasNext()) {
/* 158 */         Integer permNumberI = it.next();
/* 159 */         int permNumber = permNumberI.intValue();
/* 160 */         calcAvgFitnessHelper(permNumber, result);
/*     */       } 
/* 162 */       return result;
/*     */     } 
/*     */     
/* 165 */     KeyedValues2D a_data = new KeyedValues2D();
/* 166 */     calcAvgFitnessHelper(a_permutation, a_data);
/* 167 */     return a_data;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void calcAvgFitnessHelper(int a_permutation, KeyedValues2D result) {
/* 175 */     Map runNumbers = (Map)this.m_permutationRuns.get(new Integer(a_permutation));
/* 176 */     if (runNumbers == null) {
/*     */       return;
/*     */     }
/*     */ 
/*     */     
/* 181 */     Iterator<Integer> it = runNumbers.keySet().iterator();
/* 182 */     int numRuns = runNumbers.keySet().size();
/*     */     
/* 184 */     while (it.hasNext()) {
/* 185 */       Integer runI = it.next();
/*     */ 
/*     */       
/* 188 */       KeyedValues2D a_data = (KeyedValues2D)this.m_permutationData.get(createKey(a_permutation, runI.intValue()));
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 193 */       for (int col = 0; col < a_data.getColumnCount(); col++) {
/* 194 */         for (int row = 0; row < a_data.getRowCount(); row++) {
/*     */           double newValue;
/*     */           
/* 197 */           Double d = (Double)result.getValue(a_data.getRowKey(row), a_data.getColumnKey(col));
/*     */ 
/*     */           
/* 200 */           if (d == null) {
/* 201 */             newValue = 0.0D;
/*     */           } else {
/*     */             
/* 204 */             newValue = d.doubleValue();
/*     */           } 
/*     */ 
/*     */ 
/*     */           
/* 209 */           newValue += a_data.getValue(a_data.getRowKey(row), a_data.getColumnKey(col)).doubleValue() / numRuns;
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 214 */           result.setValue(new Double(newValue), a_data.getRowKey(row), a_data.getColumnKey(col));
/*     */         } 
/*     */       } 
/*     */     } 
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
/*     */   public KeyedValues2D calcAvgFitnessImpr(int a_permutation) {
/* 243 */     Map runNumbers = (Map)this.m_permutationRuns.get(new Integer(a_permutation));
/* 244 */     if (runNumbers == null) {
/* 245 */       return null;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 250 */     Iterator<Integer> it = runNumbers.keySet().iterator();
/*     */ 
/*     */     
/* 253 */     while (it.hasNext()) {
/* 254 */       Integer runI = it.next();
/*     */ 
/*     */       
/* 257 */       KeyedValues2D a_data = (KeyedValues2D)this.m_permutationData.get(createKey(a_permutation, runI.intValue()));
/*     */       
/* 259 */       for (int col = 0; col < a_data.getColumnCount(); col++) {
/* 260 */         for (int row = 0; row < a_data.getRowCount(); row++);
/*     */       }
/*     */     } 
/*     */     
/* 264 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getNumberOfRuns(int a_permutation) {
/* 273 */     Map runNumbers = (Map)this.m_permutationRuns.get(new Integer(a_permutation));
/* 274 */     if (runNumbers == null) {
/* 275 */       return 0;
/*     */     }
/*     */     
/* 278 */     return runNumbers.keySet().size();
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
/*     */   
/*     */   public void storeGenotype(int a_permutation, int a_run, Genotype a_genotype) {
/* 295 */     GenotypeData data = new GenotypeData();
/* 296 */     int generation = a_genotype.getConfiguration().getGenerationNr();
/* 297 */     data.generation = generation;
/* 298 */     Population pop = a_genotype.getPopulation();
/* 299 */     data.hashCode = a_genotype.hashCode();
/* 300 */     int popSize = pop.size();
/* 301 */     data.chromosomeData = new ChromosomeData[popSize];
/* 302 */     data.size = popSize;
/*     */ 
/*     */ 
/*     */     
/* 306 */     for (int i = 0; i < popSize; i++) {
/* 307 */       IChromosome chrom = pop.getChromosome(i);
/* 308 */       ChromosomeData chromData = new ChromosomeData();
/* 309 */       chromData.fitnessValue = chrom.getFitnessValue();
/* 310 */       chromData.size = chrom.size();
/* 311 */       chromData.index = i;
/* 312 */       data.chromosomeData[i] = chromData;
/*     */     } 
/* 314 */     String key = a_permutation + "_" + a_run;
/* 315 */     this.m_genotypeData.put(key, data);
/* 316 */     addRunNumber(a_permutation, a_run);
/*     */   }
/*     */   
/*     */   public GenotypeData retrieveGenotype(int a_permutation, int a_run) {
/* 320 */     return (GenotypeData)this.m_genotypeData.get(a_permutation + "_" + a_run);
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
/*     */   public GenotypeDataAvg calcPerformance(int a_permutation) {
/* 335 */     int numRuns = getNumberOfRuns(a_permutation);
/*     */     
/* 337 */     GenotypeDataAvg dataAvg = new GenotypeDataAvg();
/* 338 */     dataAvg.permutation = a_permutation;
/* 339 */     double sizeAvg = 0.0D;
/* 340 */     double fitnessAvg = 0.0D;
/* 341 */     double fitnessBest = -1.0D;
/* 342 */     double fitnessBestOld = -1.0D;
/*     */     
/* 344 */     int fitnessBestGen = -1;
/*     */     
/* 346 */     double fitnessDiversityChromsOld = -1.0D;
/* 347 */     double fitnessBestDeltaAvg = 0.0D;
/*     */     
/* 349 */     double fitnessDiversityAvg = 0.0D;
/*     */ 
/*     */     
/* 352 */     for (int i = 0; i < numRuns; i++) {
/* 353 */       GenotypeData data = retrieveGenotype(a_permutation, i);
/*     */       
/* 355 */       if (i == 0) {
/* 356 */         dataAvg.generation = data.generation;
/*     */       }
/*     */       
/* 359 */       sizeAvg += data.size / numRuns;
/* 360 */       int size = data.size;
/* 361 */       double fitnessAvgChroms = 0.0D;
/* 362 */       double fitnessDiversity = 0.0D;
/* 363 */       double fitnessBestLocal = -1.0D;
/* 364 */       for (int j = 0; j < size; j++) {
/* 365 */         ChromosomeData chrom = data.chromosomeData[j];
/* 366 */         double fitness = chrom.fitnessValue;
/*     */         
/* 368 */         if (j > 0) {
/* 369 */           fitnessDiversity += Math.abs(fitness - fitnessDiversityChromsOld) / (size - 1);
/*     */         }
/*     */         
/* 372 */         fitnessDiversityChromsOld = fitness;
/*     */         
/* 374 */         fitnessAvgChroms += fitness / size;
/*     */         
/* 376 */         if (fitnessBest < fitness) {
/* 377 */           fitnessBest = fitness;
/*     */           
/* 379 */           fitnessBestGen = data.generation;
/*     */         } 
/*     */         
/* 382 */         if (fitnessBestLocal < fitness) {
/* 383 */           fitnessBestLocal = fitness;
/*     */         }
/*     */       } 
/*     */       
/* 387 */       fitnessAvg += fitnessAvgChroms / numRuns;
/*     */       
/* 389 */       fitnessDiversityAvg += fitnessDiversity / numRuns;
/*     */       
/* 391 */       if (i > 0) {
/* 392 */         fitnessBestDeltaAvg += Math.abs(fitnessBestLocal - fitnessBestOld) / (numRuns - 1);
/*     */       }
/*     */       
/* 395 */       fitnessBestOld = fitnessBestLocal;
/*     */     } 
/* 397 */     dataAvg.sizeAvg = sizeAvg;
/* 398 */     dataAvg.avgFitnessValue = fitnessAvg;
/* 399 */     dataAvg.bestFitnessValue = fitnessBest;
/* 400 */     dataAvg.bestFitnessValueGeneration = fitnessBestGen;
/* 401 */     dataAvg.avgDiversityFitnessValue = fitnessDiversityAvg;
/* 402 */     dataAvg.avgBestDeltaFitnessValue = fitnessBestDeltaAvg;
/*     */     
/* 404 */     this.m_genotypeDataAvg.add(dataAvg);
/* 405 */     return dataAvg;
/*     */   }
/*     */   
/*     */   public class GenotypeDataAvg {
/*     */     public int permutation;
/*     */     public int generation;
/*     */     public double sizeAvg;
/*     */     public double bestFitnessValue;
/*     */     public double avgFitnessValue;
/*     */     public int bestFitnessValueGeneration;
/*     */     public double avgDiversityFitnessValue;
/*     */     public double avgBestDeltaFitnessValue;
/*     */   }
/*     */   
/*     */   public class GenotypeData {
/*     */     public int generation;
/*     */     public int hashCode;
/*     */     public int size;
/*     */     public Evaluator.ChromosomeData[] chromosomeData;
/*     */   }
/*     */   
/*     */   public class ChromosomeData {
/*     */     public int index;
/*     */     public int size;
/*     */     public double fitnessValue;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\audit\Evaluator.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */