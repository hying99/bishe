/*      */ package org.jgap;
/*      */ 
/*      */ import java.io.IOException;
/*      */ import java.io.ObjectInputStream;
/*      */ import java.io.Serializable;
/*      */ import java.util.List;
/*      */ import java.util.Vector;
/*      */ import org.apache.commons.lang.builder.CompareToBuilder;
/*      */ import org.jgap.data.config.ConfigException;
/*      */ import org.jgap.data.config.ConfigFileReader;
/*      */ import org.jgap.data.config.Configurable;
/*      */ import org.jgap.data.config.ConfigurationHandler;
/*      */ import org.jgap.data.config.RootConfigurationHandler;
/*      */ import org.jgap.event.IEventManager;
/*      */ import org.jgap.impl.ChainOfSelectors;
/*      */ import org.jgap.impl.GABreeder;
/*      */ import org.jgap.impl.JGAPFactory;
/*      */ import org.jgap.util.CloneException;
/*      */ import org.jgap.util.ICloneable;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class Configuration
/*      */   implements Configurable, Serializable, ICloneable, Comparable
/*      */ {
/*      */   private static final String CVS_REVISION = "$Revision: 1.93 $";
/*      */   public static final String PROPERTY_JGAPFACTORY_CLASS = "JGAPFACTORYCLASS";
/*      */   public static final String PROPERTY_FITFUNC_INST = "JGAPFITFUNCINST";
/*      */   public static final String PROPERTY_BFITFNC_INST = "JGAPBFITFNCINST";
/*      */   public static final String PROPERTY_FITEVAL_INST = "JGAPFITEVALINST";
/*      */   public static final String PROPERTY_SAMPLE_CHROM_INST = "JGAPSAMPLECHRMINST";
/*      */   public static final String PROPERTY_EVENT_MGR_INST = "JGAPEVNTMGRINST";
/*      */   public static final String S_CONFIGURATION = "Configuration";
/*      */   public static final String S_CONFIGURATION_NAME = "Configuration name";
/*      */   public static final String S_POPULATION_SIZE = "Population size";
/*      */   public static final String S_MINPOPSIZE = "Minimum pop. size [%]";
/*      */   public static final String S_CHROMOSOME_SIZE = "Chromosome size";
/*      */   public static final String S_SAMPLE_CHROM = "Sample Chromosome";
/*      */   public static final String S_SIZE = "Size";
/*      */   public static final String S_TOSTRING = "toString";
/*      */   public static final String S_RANDOM_GENERATOR = "Random generator";
/*      */   public static final String S_EVENT_MANAGER = "Event manager";
/*      */   public static final String S_NONE = "none";
/*      */   public static final String S_CONFIGURATION_HANDLER = "Configuration handler";
/*      */   public static final String S_FITNESS_FUNCTION = "Fitness function";
/*      */   public static final String S_FITNESS_EVALUATOR = "Fitness evaluator";
/*      */   public static final String S_GENETIC_OPERATORS = "Genetic operators";
/*      */   public static final String S_NATURAL_SELECTORS = "Natural Selectors";
/*      */   public static final String S_PRE = "pre";
/*      */   public static final String S_POST = "post";
/*  115 */   private ConfigurationConfigurable m_config = new ConfigurationConfigurable();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private FitnessFunction m_objectiveFunction;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private FitnessEvaluator m_fitnessEvaluator;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private IBreeder m_breeder;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private int m_minPercentageSizePopulation;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private BulkFitnessFunction m_bulkObjectiveFunction;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private IChromosome m_sampleChromosome;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private RandomGenerator m_randomGenerator;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private IEventManager m_eventManager;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private transient IChromosomePool m_chromosomePool;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private List m_geneticOperators;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private int m_chromosomeSize;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean m_settingsLocked;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private ChainOfSelectors m_preSelectors;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private ChainOfSelectors m_postSelectors;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean m_preserveFittestIndividual;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private double m_selectFromPrevGen;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private int m_generationNr;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private transient RootConfigurationHandler m_conHandler;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private String m_name;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean m_keepPopulationSizeConstant;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private IJGAPFactory m_factory;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean m_alwaysCalculateFitness;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private transient String threadKey;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private String m_id;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Configuration() {
/*  326 */     this("", (String)null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Configuration(String a_id, String a_name) {
/*  341 */     this.m_id = a_id;
/*  342 */     setName(a_name);
/*  343 */     makeThreadKey();
/*  344 */     this.m_preSelectors = new ChainOfSelectors(this);
/*  345 */     this.m_postSelectors = new ChainOfSelectors(this);
/*  346 */     this.m_selectFromPrevGen = 1.0D;
/*      */ 
/*      */     
/*  349 */     this.m_geneticOperators = new Vector();
/*  350 */     this.m_conHandler = new RootConfigurationHandler();
/*  351 */     this.m_conHandler.setConfigurable(this);
/*  352 */     this.m_keepPopulationSizeConstant = true;
/*  353 */     this.m_alwaysCalculateFitness = false;
/*      */ 
/*      */ 
/*      */     
/*  357 */     String clazz = System.getProperty("JGAPFACTORYCLASS");
/*  358 */     if (clazz != null && clazz.length() > 0) {
/*      */       try {
/*  360 */         this.m_factory = (IJGAPFactory)Class.forName(clazz).newInstance();
/*  361 */       } catch (Throwable ex) {
/*  362 */         throw new RuntimeException("Class " + clazz + " could not be instantiated" + " as type IJGAPFactory", ex);
/*      */       }
/*      */     
/*      */     }
/*      */     else {
/*      */       
/*  368 */       this.m_factory = (IJGAPFactory)new JGAPFactory(false);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Configuration(String a_name) {
/*  382 */     this();
/*  383 */     setName(a_name);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Configuration(String a_configFileName, boolean a_ignore) throws ConfigException, InvalidConfigurationException {
/*  401 */     this();
/*  402 */     ConfigFileReader.instance().setFileName(a_configFileName);
/*      */ 
/*      */ 
/*      */     
/*  406 */     Genotype.setStaticConfiguration(this);
/*      */ 
/*      */ 
/*      */     
/*  410 */     getConfigurationHandler().readConfig();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void reset() {
/*  424 */     reset("");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void reset(String a_id) {
/*  438 */     String threadKey = getThreadKey(Thread.currentThread(), a_id);
/*  439 */     System.setProperty(threadKey + "JGAPFITFUNCINST", "");
/*  440 */     System.setProperty(threadKey + "JGAPBFITFNCINST", "");
/*  441 */     System.setProperty(threadKey + "JGAPFITEVALINST", "");
/*  442 */     System.setProperty(threadKey + "JGAPSAMPLECHRMINST", "");
/*  443 */     System.setProperty(threadKey + "JGAPEVNTMGRINST", "");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void resetProperty(String a_propName) {
/*  454 */     resetProperty(a_propName, "");
/*      */   }
/*      */   
/*      */   public static void resetProperty(String a_propName, String a_id) {
/*  458 */     String threadKey = getThreadKey(Thread.currentThread(), a_id);
/*  459 */     System.setProperty(threadKey + a_propName, "");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setName(String a_name) {
/*  469 */     this.m_name = a_name;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getName() {
/*  479 */     return this.m_name;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setFitnessFunction(FitnessFunction a_functionToSet) throws InvalidConfigurationException {
/*  506 */     verifyChangesAllowed();
/*      */ 
/*      */     
/*  509 */     if (a_functionToSet == null) {
/*  510 */       throw new InvalidConfigurationException("The FitnessFunction instance may not be null.");
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*  515 */     if (this.m_bulkObjectiveFunction != null) {
/*  516 */       throw new InvalidConfigurationException("The bulk fitness function and normal fitness function may not both be set.");
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  523 */     checkProperty("JGAPFITFUNCINST", a_functionToSet, this.m_objectiveFunction, "Fitness function has already been set differently.");
/*      */     
/*  525 */     this.m_objectiveFunction = a_functionToSet;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void checkProperty(String a_propname, Object a_obj, Object a_oldObj, String a_errmsg) {
/*  543 */     String instanceHash = System.getProperty(this.threadKey + a_propname, null);
/*  544 */     String key = makeKey(a_obj);
/*  545 */     if (instanceHash == null || instanceHash.length() < 1) {
/*  546 */       System.setProperty(this.threadKey + a_propname, key);
/*      */     }
/*  548 */     else if (!instanceHash.equals(key)) {
/*  549 */       throw new RuntimeException(a_errmsg + "\nIf you want to set or construct" + " a configuration multiple times, please call" + " static method Configuration.reset() before" + " each setting!");
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected String makeKey(Object a_obj) {
/*  567 */     String key = String.valueOf(a_obj.hashCode()) + a_obj.getClass().getName();
/*      */     
/*  569 */     return key;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized FitnessFunction getFitnessFunction() {
/*  582 */     return this.m_objectiveFunction;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setBulkFitnessFunction(BulkFitnessFunction a_functionToSet) throws InvalidConfigurationException {
/*  610 */     verifyChangesAllowed();
/*      */ 
/*      */ 
/*      */     
/*  614 */     if (a_functionToSet == null) {
/*  615 */       throw new InvalidConfigurationException("The BulkFitnessFunction instance may not be null.");
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*  620 */     if (this.m_objectiveFunction != null) {
/*  621 */       throw new InvalidConfigurationException("The bulk fitness function and normal fitness function may not both be set.");
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  628 */     checkProperty("JGAPBFITFNCINST", a_functionToSet, this.m_bulkObjectiveFunction, "Bulk fitness function has already been set differently.");
/*      */     
/*  630 */     this.m_bulkObjectiveFunction = a_functionToSet;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized BulkFitnessFunction getBulkFitnessFunction() {
/*  643 */     return this.m_bulkObjectiveFunction;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setSampleChromosome(IChromosome a_sampleChromosomeToSet) throws InvalidConfigurationException {
/*  663 */     verifyChangesAllowed();
/*      */ 
/*      */ 
/*      */     
/*  667 */     if (a_sampleChromosomeToSet == null) {
/*  668 */       throw new InvalidConfigurationException("The sample chromosome instance may not be null.");
/*      */     }
/*      */     
/*  671 */     if (a_sampleChromosomeToSet.getConfiguration() == null) {
/*  672 */       throw new InvalidConfigurationException("The sample chromosome's configuration may not be null.");
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  678 */     checkProperty("JGAPSAMPLECHRMINST", a_sampleChromosomeToSet, this.m_sampleChromosome, "Sample chromosome has already been set differently.");
/*      */     
/*  680 */     this.m_sampleChromosome = a_sampleChromosomeToSet;
/*  681 */     this.m_chromosomeSize = this.m_sampleChromosome.size();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public IChromosome getSampleChromosome() {
/*  694 */     return this.m_sampleChromosome;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getChromosomeSize() {
/*  708 */     return this.m_chromosomeSize;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setNaturalSelector(NaturalSelector a_selectorToSet) throws InvalidConfigurationException {
/*  730 */     addNaturalSelector(a_selectorToSet, false);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized NaturalSelector getNaturalSelector() {
/*  745 */     if (getNaturalSelectors(false).size() < 1) {
/*  746 */       return null;
/*      */     }
/*  748 */     return getNaturalSelectors(false).get(0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized NaturalSelector getNaturalSelector(boolean a_processBeforeGeneticOperators, int a_index) {
/*  763 */     if (a_processBeforeGeneticOperators) {
/*  764 */       return this.m_preSelectors.get(a_index);
/*      */     }
/*      */     
/*  767 */     return this.m_postSelectors.get(a_index);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ChainOfSelectors getNaturalSelectors(boolean a_processBeforeGeneticOperators) {
/*  785 */     if (a_processBeforeGeneticOperators) {
/*  786 */       return this.m_preSelectors;
/*      */     }
/*      */     
/*  789 */     return this.m_postSelectors;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getNaturalSelectorsSize(boolean a_processBeforeGeneticOperators) {
/*  804 */     if (a_processBeforeGeneticOperators) {
/*  805 */       return this.m_preSelectors.size();
/*      */     }
/*      */     
/*  808 */     return this.m_postSelectors.size();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void removeNaturalSelectors(boolean a_processBeforeGeneticOperators) {
/*  824 */     if (a_processBeforeGeneticOperators) {
/*  825 */       getNaturalSelectors(true).clear();
/*      */     } else {
/*      */       
/*  828 */       getNaturalSelectors(false).clear();
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setRandomGenerator(RandomGenerator a_generatorToSet) throws InvalidConfigurationException {
/*  849 */     verifyChangesAllowed();
/*      */ 
/*      */     
/*  852 */     if (a_generatorToSet == null) {
/*  853 */       throw new InvalidConfigurationException("The RandomGenerator instance may not be null.");
/*      */     }
/*      */     
/*  856 */     this.m_randomGenerator = a_generatorToSet;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized RandomGenerator getRandomGenerator() {
/*  868 */     return this.m_randomGenerator;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void addGeneticOperator(GeneticOperator a_operatorToAdd) throws InvalidConfigurationException {
/*  890 */     verifyChangesAllowed();
/*      */ 
/*      */     
/*  893 */     if (a_operatorToAdd == null) {
/*  894 */       throw new InvalidConfigurationException("The GeneticOperator instance may not be null.");
/*      */     }
/*      */     
/*  897 */     this.m_geneticOperators.add(a_operatorToAdd);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public List getGeneticOperators() {
/*  912 */     return this.m_geneticOperators;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void setPopulationSize(int a_sizeOfPopulation) throws InvalidConfigurationException {
/*  931 */     verifyChangesAllowed();
/*      */ 
/*      */     
/*  934 */     if (a_sizeOfPopulation < 1) {
/*  935 */       throw new InvalidConfigurationException("The population size must be positive.");
/*      */     }
/*      */     
/*  938 */     this.m_config.m_populationSize = a_sizeOfPopulation;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized int getPopulationSize() {
/*  947 */     return this.m_config.m_populationSize;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setEventManager(IEventManager a_eventManagerToSet) throws InvalidConfigurationException {
/*  966 */     verifyChangesAllowed();
/*      */ 
/*      */     
/*  969 */     if (a_eventManagerToSet == null) {
/*  970 */       throw new InvalidConfigurationException("The event manager instance may not be null.");
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  976 */     checkProperty("JGAPEVNTMGRINST", a_eventManagerToSet, this.m_eventManager, "Event manager has already been set differently.");
/*      */     
/*  978 */     this.m_eventManager = a_eventManagerToSet;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public IEventManager getEventManager() {
/*  991 */     return this.m_eventManager;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setChromosomePool(IChromosomePool a_chromosomePoolToSet) throws InvalidConfigurationException {
/* 1010 */     verifyChangesAllowed();
/* 1011 */     this.m_chromosomePool = a_chromosomePoolToSet;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public IChromosomePool getChromosomePool() {
/* 1029 */     return this.m_chromosomePool;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void lockSettings() throws InvalidConfigurationException {
/* 1056 */     if (!this.m_settingsLocked) {
/* 1057 */       verifyStateIsValid();
/*      */ 
/*      */ 
/*      */       
/* 1061 */       this.m_settingsLocked = true;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isLocked() {
/* 1075 */     return this.m_settingsLocked;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void verifyStateIsValid() throws InvalidConfigurationException {
/* 1096 */     if (this.m_objectiveFunction == null && this.m_bulkObjectiveFunction == null) {
/* 1097 */       throw new InvalidConfigurationException("A desired fitness function or bulk fitness function must be specified in the active configuration.");
/*      */     }
/*      */ 
/*      */     
/* 1101 */     if (this.m_sampleChromosome == null) {
/* 1102 */       throw new InvalidConfigurationException("A sample instance of the desired Chromosome setup must be specified in the active configuration.");
/*      */     }
/*      */ 
/*      */     
/* 1106 */     if (this.m_preSelectors.size() == 0 && this.m_postSelectors.size() == 0) {
/* 1107 */       throw new InvalidConfigurationException("At least one desired natural selector must be specified in the active configuration.");
/*      */     }
/*      */ 
/*      */     
/* 1111 */     if (this.m_randomGenerator == null) {
/* 1112 */       throw new InvalidConfigurationException("A desired random number generator must be specified in the active configuration.");
/*      */     }
/*      */ 
/*      */     
/* 1116 */     if (this.m_eventManager == null) {
/* 1117 */       throw new InvalidConfigurationException("A desired event manager must be specified in the active configuration.");
/*      */     }
/*      */ 
/*      */     
/* 1121 */     if (this.m_geneticOperators.isEmpty()) {
/* 1122 */       throw new InvalidConfigurationException("At least one genetic operator must be specified in the configuration.");
/*      */     }
/*      */ 
/*      */     
/* 1126 */     if (this.m_chromosomeSize <= 0) {
/* 1127 */       throw new InvalidConfigurationException("A chromosome size greater than zero must be specified in the active configuration.");
/*      */     }
/*      */ 
/*      */     
/* 1131 */     if (this.m_config.m_populationSize <= 0) {
/* 1132 */       throw new InvalidConfigurationException("A population size greater than zero must be specified in the active configuration.");
/*      */     }
/*      */ 
/*      */     
/* 1136 */     if (this.m_fitnessEvaluator == null) {
/* 1137 */       throw new IllegalArgumentException("The fitness evaluator may not be null.");
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1146 */     Gene[] sampleGenes = this.m_sampleChromosome.getGenes();
/* 1147 */     for (int i = 0; i < sampleGenes.length; i++) {
/* 1148 */       Gene sampleCopy = sampleGenes[i].newGene();
/* 1149 */       sampleCopy.setAllele(sampleGenes[i].getAllele());
/* 1150 */       if (!sampleCopy.equals(sampleGenes[i])) {
/* 1151 */         throw new InvalidConfigurationException("The sample Gene at gene position (locus) " + i + " does not appear to have a working equals() or compareTo()" + " method.\n" + "It could also be that you forgot to implement method" + " newGene() in your Gene implementation.\n" + "When tested, the method returned false when comparing " + "the sample gene with a gene of the same type and " + "possessing the same value (allele).");
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void verifyChangesAllowed() throws InvalidConfigurationException {
/* 1180 */     if (this.m_settingsLocked) {
/* 1181 */       throw new InvalidConfigurationException("This Configuration object is locked. Settings may not be altered.");
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void addNaturalSelector(NaturalSelector a_selector, boolean a_processBeforeGeneticOperators) throws InvalidConfigurationException {
/* 1202 */     verifyChangesAllowed();
/* 1203 */     if (a_processBeforeGeneticOperators) {
/* 1204 */       this.m_preSelectors.addNaturalSelector(a_selector);
/*      */     } else {
/*      */       
/* 1207 */       this.m_postSelectors.addNaturalSelector(a_selector);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setMinimumPopSizePercent(int a_minimumSizeGuaranteedPercent) {
/* 1221 */     this.m_minPercentageSizePopulation = a_minimumSizeGuaranteedPercent;
/*      */   }
/*      */   
/*      */   public int getMinimumPopSizePercent() {
/* 1225 */     return this.m_minPercentageSizePopulation;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public FitnessEvaluator getFitnessEvaluator() {
/* 1235 */     return this.m_fitnessEvaluator;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setFitnessEvaluator(FitnessEvaluator a_fitnessEvaluator) {
/* 1247 */     if (a_fitnessEvaluator == null) {
/* 1248 */       throw new IllegalStateException("The fitness evaluator object must not be null!");
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1254 */     checkProperty("JGAPFITEVALINST", a_fitnessEvaluator, this.m_fitnessEvaluator, "Fitness evaluator has already been set differently.");
/*      */     
/* 1256 */     this.m_fitnessEvaluator = a_fitnessEvaluator;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isPreserveFittestIndividual() {
/* 1267 */     return this.m_preserveFittestIndividual;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setPreservFittestIndividual(boolean a_preserveFittest) {
/* 1279 */     this.m_preserveFittestIndividual = a_preserveFittest;
/*      */   }
/*      */   
/*      */   public void incrementGenerationNr() {
/* 1283 */     this.m_generationNr++;
/*      */   }
/*      */   
/*      */   public int getGenerationNr() {
/* 1287 */     return this.m_generationNr;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ConfigurationHandler getConfigurationHandler() {
/* 1298 */     return (ConfigurationHandler)this.m_conHandler;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String toString() {
/* 1309 */     String result = "Configuration:";
/*      */ 
/*      */     
/* 1312 */     result = result + "\n Configuration name: " + getName();
/* 1313 */     result = result + "\n Population size: " + getPopulationSize();
/* 1314 */     result = result + "\n Minimum pop. size [%]: " + getMinimumPopSizePercent();
/* 1315 */     result = result + "\n Chromosome size: " + getChromosomeSize();
/*      */ 
/*      */     
/* 1318 */     result = result + "\n Sample Chromosome:\n";
/* 1319 */     if (getSampleChromosome() == null) {
/* 1320 */       result = result + "\n null";
/*      */     } else {
/*      */       
/* 1323 */       result = result + "\n    Size: " + getSampleChromosome().size();
/* 1324 */       result = result + "\n    toString: " + getSampleChromosome().toString();
/*      */     } 
/*      */ 
/*      */     
/* 1328 */     result = result + "\n  Random generator: ";
/* 1329 */     if (getRandomGenerator() != null) {
/* 1330 */       result = result + getRandomGenerator().getClass().getName();
/*      */     } else {
/*      */       
/* 1333 */       result = result + "none";
/*      */     } 
/* 1335 */     result = result + "\n  Event manager: ";
/*      */ 
/*      */     
/* 1338 */     if (getEventManager() == null) {
/* 1339 */       result = result + "none";
/*      */     } else {
/*      */       
/* 1342 */       result = result + getEventManager().getClass().getName();
/*      */     } 
/*      */ 
/*      */     
/* 1346 */     result = result + "\n Configuration handler: ";
/* 1347 */     if (getConfigurationHandler() == null) {
/* 1348 */       result = result + "null";
/*      */     } else {
/*      */       
/* 1351 */       result = result + getConfigurationHandler().getName();
/*      */     } 
/*      */ 
/*      */     
/* 1355 */     result = result + "\n Fitness function: ";
/* 1356 */     if (getFitnessFunction() == null) {
/* 1357 */       result = result + "null";
/*      */     } else {
/*      */       
/* 1360 */       result = result + getFitnessFunction().getClass().getName();
/*      */     } 
/*      */ 
/*      */     
/* 1364 */     result = result + "\n Fitness evaluator: ";
/* 1365 */     if (getFitnessEvaluator() == null) {
/* 1366 */       result = result + "null";
/*      */     } else {
/*      */       
/* 1369 */       result = result + getFitnessEvaluator().getClass().getName();
/*      */     } 
/*      */ 
/*      */     
/* 1373 */     result = result + "\n  Genetic operators: ";
/* 1374 */     if (getGeneticOperators() == null) {
/* 1375 */       result = result + "null";
/*      */     } else {
/*      */       
/* 1378 */       int gensize = getGeneticOperators().size();
/* 1379 */       if (gensize < 1) {
/* 1380 */         result = result + "none";
/*      */       } else {
/*      */         
/* 1383 */         for (int i = 0; i < gensize; i++) {
/* 1384 */           if (i > 0) {
/* 1385 */             result = result + "; ";
/*      */           }
/* 1387 */           result = result + " " + getGeneticOperators().get(i).getClass().getName();
/*      */         } 
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/* 1393 */     int natsize = getNaturalSelectors(true).size();
/* 1394 */     result = result + "\n  Natural Selectors(pre): ";
/* 1395 */     if (natsize < 1) {
/* 1396 */       result = result + "none";
/*      */     } else {
/*      */       
/* 1399 */       for (int i = 0; i < natsize; i++) {
/* 1400 */         if (i > 0) {
/* 1401 */           result = result + "; ";
/*      */         }
/* 1403 */         result = result + " " + getNaturalSelectors(true).get(i).getClass().getName();
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/* 1408 */     natsize = getNaturalSelectors(false).size();
/* 1409 */     result = result + "\n  Natural Selectors(post): ";
/* 1410 */     if (natsize < 1) {
/* 1411 */       result = result + "none";
/*      */     } else {
/*      */       
/* 1414 */       for (int i = 0; i < natsize; i++) {
/* 1415 */         if (i > 0) {
/* 1416 */           result = result + "; ";
/*      */         }
/* 1418 */         result = result + " " + getNaturalSelectors(false).get(i).getClass().getName();
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1430 */     return result;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isKeepPopulationSizeConstant() {
/* 1442 */     return this.m_keepPopulationSizeConstant;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setKeepPopulationSizeConstant(boolean a_keepPopSizeConstant) {
/* 1460 */     this.m_keepPopulationSizeConstant = a_keepPopSizeConstant;
/*      */   }
/*      */   
/*      */   public void setSelectFromPrevGen(double a_percentage) {
/* 1464 */     if (a_percentage < 0.0D || a_percentage > 1.0D) {
/* 1465 */       throw new IllegalArgumentException("Argument must be between 0 and 1");
/*      */     }
/* 1467 */     this.m_selectFromPrevGen = a_percentage;
/*      */   }
/*      */   
/*      */   public double getSelectFromPrevGen() {
/* 1471 */     return this.m_selectFromPrevGen;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public IJGAPFactory getJGAPFactory() {
/* 1480 */     return this.m_factory;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   class ConfigurationConfigurable
/*      */     implements Serializable
/*      */   {
/*      */     int m_populationSize;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected static String getThreadKey(Thread current, String a_id) {
/* 1501 */     return current.toString() + "|" + a_id + "|";
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setJGAPFactory(IJGAPFactory a_factory) {
/* 1511 */     this.m_factory = a_factory;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setBreeder(IBreeder a_breeder) {
/* 1521 */     assert a_breeder != null;
/* 1522 */     this.m_breeder = a_breeder;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public IBreeder getBreeder() {
/* 1533 */     if (this.m_breeder == null) {
/* 1534 */       this.m_breeder = (IBreeder)new GABreeder();
/*      */     }
/* 1536 */     return this.m_breeder;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setAlwaysCaculateFitness(boolean a_alwaysCalculate) {
/* 1582 */     this.m_alwaysCalculateFitness = a_alwaysCalculate;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isAlwaysCalculateFitness() {
/* 1594 */     return this.m_alwaysCalculateFitness;
/*      */   }
/*      */ 
/*      */   
/*      */   protected String makeThreadKey() {
/* 1599 */     Thread current = Thread.currentThread();
/* 1600 */     this.threadKey = getThreadKey(current, this.m_id);
/* 1601 */     return this.threadKey;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void readObject(ObjectInputStream a_inputStream) throws ClassNotFoundException, IOException {
/* 1650 */     a_inputStream.defaultReadObject();
/* 1651 */     makeThreadKey();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getId() {
/* 1661 */     return this.m_id;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void setId(String a_id) {
/* 1673 */     this.m_id = a_id;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Object clone() {
/* 1683 */     return newInstance(this.m_id, this.m_name);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Configuration newInstance(String a_id, String a_name) {
/*      */     try {
/* 1699 */       Configuration result = new Configuration(this.m_name);
/*      */ 
/*      */       
/* 1702 */       if (this.m_factory instanceof ICloneable) {
/* 1703 */         result.m_factory = (IJGAPFactory)((ICloneable)this.m_factory).clone();
/*      */       
/*      */       }
/*      */       else {
/*      */         
/* 1708 */         this.m_factory = (IJGAPFactory)new JGAPFactory(false);
/* 1709 */         result.m_factory = (IJGAPFactory)((JGAPFactory)this.m_factory).clone();
/*      */       } 
/* 1711 */       if (this.m_breeder != null) {
/* 1712 */         result.m_breeder = (IBreeder)doClone(this.m_breeder);
/*      */       }
/* 1714 */       if (this.m_bulkObjectiveFunction != null) {
/* 1715 */         result.m_bulkObjectiveFunction = this.m_bulkObjectiveFunction;
/*      */       }
/* 1717 */       result.m_chromosomeSize = this.m_chromosomeSize;
/*      */ 
/*      */       
/* 1720 */       result.m_eventManager = (IEventManager)doClone(this.m_eventManager);
/* 1721 */       result.m_fitnessEvaluator = (FitnessEvaluator)doClone(this.m_fitnessEvaluator);
/* 1722 */       result.m_generationNr = 0;
/* 1723 */       result.m_geneticOperators = (List)doClone(this.m_geneticOperators);
/* 1724 */       result.m_keepPopulationSizeConstant = this.m_keepPopulationSizeConstant;
/* 1725 */       result.m_minPercentageSizePopulation = this.m_minPercentageSizePopulation;
/* 1726 */       result.m_selectFromPrevGen = this.m_selectFromPrevGen;
/* 1727 */       result.m_objectiveFunction = (FitnessFunction)doClone(this.m_objectiveFunction);
/*      */       
/* 1729 */       result.m_postSelectors = (ChainOfSelectors)doClone(this.m_postSelectors);
/* 1730 */       result.m_preSelectors = (ChainOfSelectors)doClone(this.m_preSelectors);
/* 1731 */       result.m_preserveFittestIndividual = this.m_preserveFittestIndividual;
/* 1732 */       result.m_randomGenerator = (RandomGenerator)doClone(this.m_randomGenerator);
/* 1733 */       result.m_sampleChromosome = (IChromosome)this.m_sampleChromosome.clone();
/* 1734 */       result.m_alwaysCalculateFitness = this.m_alwaysCalculateFitness;
/* 1735 */       result.m_settingsLocked = this.m_settingsLocked;
/*      */ 
/*      */ 
/*      */       
/* 1739 */       result.m_config = new ConfigurationConfigurable();
/* 1740 */       result.m_config.m_populationSize = this.m_config.m_populationSize;
/*      */ 
/*      */       
/* 1743 */       result.m_name = a_name;
/* 1744 */       result.m_id = a_id;
/* 1745 */       result.makeThreadKey();
/* 1746 */       return result;
/* 1747 */     } catch (Throwable t) {
/* 1748 */       throw new CloneException(t);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected Object doClone(Object a_objToClone) throws Exception {
/* 1763 */     if (a_objToClone != null) {
/* 1764 */       ICloneHandler handler = getJGAPFactory().getCloneHandlerFor(a_objToClone, null);
/*      */       
/* 1766 */       if (handler != null) {
/* 1767 */         return handler.perform(a_objToClone, null, null);
/*      */       }
/*      */     } 
/* 1770 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean equals(Object a_other) {
/* 1783 */     return (compareTo(a_other) == 0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int compareTo(Object a_other) {
/* 1796 */     if (a_other == null) {
/* 1797 */       return 1;
/*      */     }
/*      */     
/* 1800 */     Configuration other = (Configuration)a_other;
/*      */     try {
/* 1802 */       return (new CompareToBuilder()).append(this.m_config.m_populationSize, other.m_config.m_populationSize).append(this.m_factory, other.m_factory).append(this.m_breeder, other.m_breeder).append(this.m_objectiveFunction, other.m_objectiveFunction).append(this.m_fitnessEvaluator, other.m_fitnessEvaluator).append(this.m_bulkObjectiveFunction, other.m_bulkObjectiveFunction).append(this.m_sampleChromosome, other.m_sampleChromosome).append(this.m_randomGenerator, other.m_randomGenerator).append(this.m_geneticOperators.toArray(), other.m_geneticOperators.toArray()).append(this.m_chromosomeSize, other.m_chromosomeSize).append(this.m_preSelectors, other.m_preSelectors).append(this.m_postSelectors, other.m_postSelectors).append(this.m_preserveFittestIndividual, other.m_preserveFittestIndividual).append(this.threadKey, other.threadKey).append(this.m_keepPopulationSizeConstant, other.m_keepPopulationSizeConstant).append(this.m_alwaysCalculateFitness, other.m_alwaysCalculateFitness).append(this.m_minPercentageSizePopulation, other.m_minPercentageSizePopulation).append(this.m_selectFromPrevGen, other.m_selectFromPrevGen).append(this.m_generationNr, other.m_generationNr).append(this.m_name, other.m_name).append(this.m_settingsLocked, other.m_settingsLocked).toComparison();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     }
/* 1833 */     catch (ClassCastException cex) {
/* 1834 */       throw new RuntimeException("Cannot compare all objects within org.jgap.Configuration, because at least one does not implement interface java.lang.Comparable!");
/*      */     } 
/*      */   }
/*      */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\Configuration.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */