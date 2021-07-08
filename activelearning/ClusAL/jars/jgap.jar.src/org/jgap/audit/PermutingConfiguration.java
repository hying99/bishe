/*     */ package org.jgap.audit;
/*     */ 
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Vector;
/*     */ import org.jgap.Configuration;
/*     */ import org.jgap.FitnessFunction;
/*     */ import org.jgap.GeneticOperator;
/*     */ import org.jgap.InvalidConfigurationException;
/*     */ import org.jgap.NaturalSelector;
/*     */ import org.jgap.RandomGenerator;
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
/*     */ public class PermutingConfiguration
/*     */   extends Configuration
/*     */ {
/*     */   private static final String CVS_REVISION = "$Revision: 1.8 $";
/*     */   private List m_randomGeneratorSlots;
/*     */   private int m_randomGeneratorIndex;
/*     */   private List m_naturalSelectorSlots;
/*     */   private int m_naturalSelectorIndex;
/*     */   private List m_geneticOperatorSlots;
/*     */   private int m_geneticOperatorIndex;
/*     */   private List m_fitnessFunctionSlots;
/*     */   private int m_fitnessFunctionIndex;
/*     */   private int m_componentIndex;
/*     */   private Configuration m_configuration;
/*     */   
/*     */   public PermutingConfiguration() {
/*  52 */     init();
/*     */   }
/*     */   
/*     */   public void init() {
/*  56 */     this.m_randomGeneratorSlots = new Vector();
/*  57 */     this.m_naturalSelectorSlots = new Vector();
/*  58 */     this.m_geneticOperatorSlots = new Vector();
/*  59 */     this.m_fitnessFunctionSlots = new Vector();
/*  60 */     this.m_randomGeneratorIndex = 0;
/*  61 */     this.m_naturalSelectorIndex = 0;
/*  62 */     this.m_geneticOperatorIndex = 0;
/*  63 */     this.m_fitnessFunctionIndex = 0;
/*  64 */     this.m_componentIndex = 0;
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
/*     */   public PermutingConfiguration(Configuration a_conf) throws InvalidConfigurationException {
/*  78 */     this();
/*  79 */     setEventManager(a_conf.getEventManager());
/*  80 */     setFitnessEvaluator(a_conf.getFitnessEvaluator());
/*  81 */     if (a_conf.getFitnessFunction() != null) {
/*  82 */       setFitnessFunction(a_conf.getFitnessFunction());
/*     */     }
/*  84 */     setMinimumPopSizePercent(a_conf.getMinimumPopSizePercent());
/*  85 */     if (a_conf.getPopulationSize() > 0) {
/*  86 */       setPopulationSize(a_conf.getPopulationSize());
/*     */     }
/*  88 */     if (a_conf.getSampleChromosome() != null) {
/*  89 */       setSampleChromosome(a_conf.getSampleChromosome());
/*     */     }
/*  91 */     setRandomGenerator(a_conf.getRandomGenerator());
/*  92 */     if (a_conf.getChromosomePool() != null) {
/*  93 */       setChromosomePool(a_conf.getChromosomePool());
/*     */     }
/*     */   }
/*     */   
/*     */   public void addRandomGeneratorSlot(RandomGenerator a_randomGenerator) {
/*  98 */     this.m_randomGeneratorSlots.add(a_randomGenerator);
/*     */   }
/*     */   
/*     */   public void addNaturalSelector(NaturalSelector a_naturalSel, boolean a_egal) {
/* 102 */     throw new UnsupportedOperationException("Use addNaturalSelectorSlot instead!");
/*     */   }
/*     */ 
/*     */   
/*     */   public void addNaturalSelectorSlot(NaturalSelector a_naturalSelector) {
/* 107 */     this.m_naturalSelectorSlots.add(a_naturalSelector);
/*     */   }
/*     */   
/*     */   public synchronized void addGeneticOperator(GeneticOperator a_geneticOp) {
/* 111 */     throw new UnsupportedOperationException("Use addGeneticOperatorSlot instead!");
/*     */   }
/*     */ 
/*     */   
/*     */   public void addGeneticOperatorSlot(GeneticOperator a_geneticOperator) {
/* 116 */     this.m_geneticOperatorSlots.add(a_geneticOperator);
/*     */   }
/*     */   
/*     */   public void addFitnessFunctionSlot(FitnessFunction a_fitnessFunction) {
/* 120 */     this.m_fitnessFunctionSlots.add(a_fitnessFunction);
/*     */   }
/*     */ 
/*     */   
/*     */   public Configuration next() throws InvalidConfigurationException {
/* 125 */     this.m_configuration = new Configuration();
/* 126 */     this.m_configuration.setEventManager(getEventManager());
/* 127 */     this.m_configuration.setFitnessEvaluator(getFitnessEvaluator());
/* 128 */     if (getFitnessFunction() != null) {
/* 129 */       Configuration.resetProperty("JGAPFITFUNCINST");
/* 130 */       setFitnessFunction(getFitnessFunction());
/*     */     } 
/* 132 */     this.m_configuration.setMinimumPopSizePercent(getMinimumPopSizePercent());
/* 133 */     if (getPopulationSize() > 0) {
/* 134 */       this.m_configuration.setPopulationSize(getPopulationSize());
/*     */     }
/* 136 */     if (getSampleChromosome() != null) {
/* 137 */       this.m_configuration.setSampleChromosome(getSampleChromosome());
/*     */     }
/* 139 */     this.m_configuration.setRandomGenerator(getRandomGenerator());
/* 140 */     if (getChromosomePool() != null) {
/* 141 */       this.m_configuration.setChromosomePool(getChromosomePool());
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 152 */     if (this.m_geneticOperatorIndex >= Math.pow(2.0D, this.m_geneticOperatorSlots.size()) - 1.0D) {
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 157 */       this.m_geneticOperatorIndex = 0;
/* 158 */       this.m_naturalSelectorIndex++;
/*     */     } 
/*     */ 
/*     */     
/* 162 */     List<GeneticOperator> list = nextList(this.m_geneticOperatorIndex++, this.m_geneticOperatorSlots);
/* 163 */     Iterator<GeneticOperator> it = list.iterator();
/*     */     
/* 165 */     while (it.hasNext()) {
/* 166 */       GeneticOperator op = it.next();
/* 167 */       this.m_configuration.addGeneticOperator(op);
/*     */     } 
/*     */ 
/*     */     
/* 171 */     if (this.m_naturalSelectorIndex >= Math.pow(2.0D, this.m_naturalSelectorSlots.size()) - 1.0D) {
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 176 */       this.m_naturalSelectorIndex = 0;
/* 177 */       this.m_randomGeneratorIndex++;
/*     */     } 
/*     */     
/* 180 */     list = nextList(this.m_naturalSelectorIndex, this.m_naturalSelectorSlots);
/* 181 */     it = list.iterator();
/*     */     
/* 183 */     while (it.hasNext()) {
/* 184 */       NaturalSelector ns = (NaturalSelector)it.next();
/* 185 */       this.m_configuration.addNaturalSelector(ns, true);
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 191 */     this.m_randomGeneratorIndex++;
/* 192 */     if (this.m_randomGeneratorIndex >= this.m_randomGeneratorSlots.size()) {
/* 193 */       this.m_randomGeneratorIndex = 0;
/* 194 */       this.m_fitnessFunctionIndex++;
/*     */     } 
/*     */     
/* 197 */     RandomGenerator rg = this.m_randomGeneratorSlots.get(this.m_randomGeneratorIndex);
/*     */     
/* 199 */     this.m_configuration.setRandomGenerator(rg);
/*     */ 
/*     */ 
/*     */     
/* 203 */     this.m_fitnessFunctionIndex++;
/* 204 */     if (this.m_fitnessFunctionIndex >= this.m_fitnessFunctionSlots.size()) {
/* 205 */       this.m_fitnessFunctionIndex = 0;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 211 */     FitnessFunction ff = this.m_fitnessFunctionSlots.get(this.m_fitnessFunctionIndex);
/*     */     
/* 213 */     Configuration.reset();
/* 214 */     this.m_configuration.setFitnessFunction(ff);
/* 215 */     this.m_componentIndex++;
/* 216 */     return this.m_configuration;
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
/*     */   private List nextList(int index, List list) {
/* 228 */     if (index <= 0) {
/* 229 */       index = 1;
/*     */     } else {
/*     */       
/* 232 */       index++;
/*     */     } 
/* 234 */     List newList = new Vector();
/* 235 */     for (int i = 0; i < list.size(); i++) {
/* 236 */       if ((index & (int)Math.pow(2.0D, i)) > 0) {
/* 237 */         newList.add(list.get(i));
/*     */       }
/*     */     } 
/* 240 */     return newList;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean hasNext() {
/* 248 */     double r = (this.m_randomGeneratorSlots.size() * this.m_fitnessFunctionSlots.size()) * (Math.pow(2.0D, this.m_naturalSelectorSlots.size()) - 1.0D) * (Math.pow(2.0D, this.m_geneticOperatorSlots.size()) - 1.0D);
/*     */ 
/*     */ 
/*     */     
/* 252 */     return (this.m_componentIndex < r);
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\audit\PermutingConfiguration.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */