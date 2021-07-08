/*     */ package org.jgap.gp.impl;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.util.HashMap;
/*     */ import java.util.Hashtable;
/*     */ import java.util.Map;
/*     */ import java.util.Stack;
/*     */ import org.apache.commons.lang.builder.CompareToBuilder;
/*     */ import org.jgap.Configuration;
/*     */ import org.jgap.GeneticOperator;
/*     */ import org.jgap.IJGAPFactory;
/*     */ import org.jgap.InvalidConfigurationException;
/*     */ import org.jgap.RandomGenerator;
/*     */ import org.jgap.distr.Culture;
/*     */ import org.jgap.distr.CultureMemoryCell;
/*     */ import org.jgap.event.EventManager;
/*     */ import org.jgap.event.IEventManager;
/*     */ import org.jgap.gp.CommandGene;
/*     */ import org.jgap.gp.CrossMethod;
/*     */ import org.jgap.gp.GPFitnessFunction;
/*     */ import org.jgap.gp.IGPFitnessEvaluator;
/*     */ import org.jgap.gp.IGPInitStrategy;
/*     */ import org.jgap.gp.IGPProgram;
/*     */ import org.jgap.gp.INaturalGPSelector;
/*     */ import org.jgap.gp.INodeValidator;
/*     */ import org.jgap.gp.terminal.Variable;
/*     */ import org.jgap.impl.JGAPFactory;
/*     */ import org.jgap.impl.StockRandomGenerator;
/*     */ import org.jgap.util.CloneException;
/*     */ import org.jgap.util.ICloneable;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class GPConfiguration
/*     */   extends Configuration
/*     */ {
/*     */   private static final String CVS_REVISION = "$Revision: 1.38 $";
/*     */   private GPFitnessFunction m_objectiveFunction;
/*  46 */   private Stack m_stack = new Stack();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  51 */   private transient Culture m_memory = new Culture(50);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  58 */   private double m_crossoverProb = 0.9D;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  65 */   private double m_reproductionProb = 0.1D;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  70 */   private double m_mutationProb = 0.1D;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  76 */   private double m_newChromsPercent = 0.3D;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  82 */   private double m_functionProb = 0.9D;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  87 */   private int m_maxCrossoverDepth = 17;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  92 */   private int m_maxInitDepth = 7;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  97 */   private int m_minInitDepth = 3;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private INaturalGPSelector m_selectionMethod;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private CrossMethod m_crossMethod;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean m_strictProgramCreation;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 121 */   private int m_programCreationMaxTries = 5;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private IGPFitnessEvaluator m_fitnessEvaluator;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private INodeValidator m_nodeValidator;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private transient boolean m_warningPrinted;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private IGPProgram m_prototypeProgram;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean m_useProgramCache = false;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Map m_variables;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private transient Map m_programCache;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private transient IJGAPFactory m_factory;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private IGPInitStrategy m_initStrategy;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public GPConfiguration() throws InvalidConfigurationException {
/* 179 */     this("", (String)null);
/*     */   }
/*     */ 
/*     */   
/*     */   public GPConfiguration(String a_id, String a_name) throws InvalidConfigurationException {
/* 184 */     super(a_id, a_name);
/* 185 */     init(true);
/* 186 */     this.m_selectionMethod = new TournamentSelector(3);
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
/*     */   public GPConfiguration(String a_name) throws InvalidConfigurationException {
/* 201 */     this();
/* 202 */     setName(a_name);
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
/*     */   public void setGPFitnessEvaluator(IGPFitnessEvaluator a_evaluator) {
/* 215 */     this.m_fitnessEvaluator = a_evaluator;
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
/*     */   protected void init(boolean a_fullInit) throws InvalidConfigurationException {
/* 234 */     String clazz = System.getProperty("JGAPFACTORYCLASS");
/* 235 */     if (clazz != null && clazz.length() > 0) {
/*     */       try {
/* 237 */         this.m_factory = (IJGAPFactory)Class.forName(clazz).newInstance();
/* 238 */       } catch (Throwable ex) {
/* 239 */         throw new RuntimeException("Class " + clazz + " could not be instantiated" + " as type IJGAPFactory");
/*     */       }
/*     */     
/*     */     }
/*     */     else {
/*     */       
/* 245 */       this.m_factory = (IJGAPFactory)new JGAPFactory(false);
/*     */     } 
/* 247 */     if (this.m_factory == null) {
/* 248 */       throw new IllegalStateException("JGAPFactory not registered!");
/*     */     }
/* 250 */     this.m_programCache = new HashMap<Object, Object>(50);
/* 251 */     if (a_fullInit) {
/* 252 */       this.m_variables = new Hashtable<Object, Object>();
/* 253 */       this.m_crossMethod = new BranchTypingCross(this);
/* 254 */       setEventManager((IEventManager)new EventManager());
/* 255 */       setRandomGenerator((RandomGenerator)new StockRandomGenerator());
/* 256 */       setGPFitnessEvaluator(new DefaultGPFitnessEvaluator());
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
/*     */   public GPConfiguration(INaturalGPSelector a_selectionMethod) throws InvalidConfigurationException {
/* 272 */     init(true);
/* 273 */     this.m_selectionMethod = a_selectionMethod;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setSelectionMethod(INaturalGPSelector a_method) {
/* 284 */     if (a_method == null) {
/* 285 */       throw new IllegalArgumentException("Selection method must not be null");
/*     */     }
/* 287 */     this.m_selectionMethod = a_method;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setCrossoverMethod(CrossMethod a_method) {
/* 298 */     if (a_method == null) {
/* 299 */       throw new IllegalArgumentException("Crossover method must not be null");
/*     */     }
/* 301 */     this.m_crossMethod = a_method;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized void verifyStateIsValid() throws InvalidConfigurationException {}
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized void addGeneticOperator(GeneticOperator a_operatorToAdd) throws InvalidConfigurationException {
/* 312 */     throw new UnsupportedOperationException("Use addGeneticOperator(GPGeneticOperator) instead!");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getCrossoverProb() {
/* 322 */     return this.m_crossoverProb;
/*     */   }
/*     */   
/*     */   public void setCrossoverProb(float a_crossoverProb) {
/* 326 */     this.m_crossoverProb = a_crossoverProb;
/*     */   }
/*     */   
/*     */   public double getReproductionProb() {
/* 330 */     return this.m_reproductionProb;
/*     */   }
/*     */   
/*     */   public void setReproductionProb(float a_reproductionProb) {
/* 334 */     this.m_reproductionProb = a_reproductionProb;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getMutationProb() {
/* 344 */     return this.m_mutationProb;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setMutationProb(float a_mutationProb) {
/* 355 */     this.m_mutationProb = a_mutationProb;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setFunctionProb(double a_functionProb) {
/* 366 */     this.m_functionProb = a_functionProb;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getFunctionProb() {
/* 376 */     return this.m_functionProb;
/*     */   }
/*     */   
/*     */   public void setNewChromsPercent(double a_newChromsPercent) {
/* 380 */     this.m_newChromsPercent = a_newChromsPercent;
/*     */   }
/*     */   
/*     */   public double getNewChromsPercent() {
/* 384 */     return this.m_newChromsPercent;
/*     */   }
/*     */   
/*     */   public int getMaxCrossoverDepth() {
/* 388 */     return this.m_maxCrossoverDepth;
/*     */   }
/*     */   
/*     */   public void setMaxCrossoverDepth(int a_maxCrossoverDepth) {
/* 392 */     this.m_maxCrossoverDepth = a_maxCrossoverDepth;
/*     */   }
/*     */   
/*     */   public INaturalGPSelector getSelectionMethod() {
/* 396 */     return this.m_selectionMethod;
/*     */   }
/*     */   
/*     */   public CrossMethod getCrossMethod() {
/* 400 */     return this.m_crossMethod;
/*     */   }
/*     */   
/*     */   public int getMaxInitDepth() {
/* 404 */     return this.m_maxInitDepth;
/*     */   }
/*     */   
/*     */   public void setMaxInitDepth(int a_maxDepth) {
/* 408 */     this.m_maxInitDepth = a_maxDepth;
/*     */   }
/*     */   
/*     */   public int getMinInitDepth() {
/* 412 */     return this.m_minInitDepth;
/*     */   }
/*     */   
/*     */   public void setMinInitDepth(int a_minDepth) {
/* 416 */     this.m_minInitDepth = a_minDepth;
/*     */   }
/*     */   
/*     */   public void pushToStack(Object a_value) {
/* 420 */     this.m_stack.push(a_value);
/*     */   }
/*     */   
/*     */   public Object popFromStack() {
/* 424 */     return this.m_stack.pop();
/*     */   }
/*     */   
/*     */   public Object peekStack() {
/* 428 */     return this.m_stack.peek();
/*     */   }
/*     */   
/*     */   public int stackSize() {
/* 432 */     return this.m_stack.size();
/*     */   }
/*     */   
/*     */   public void clearStack() {
/* 436 */     this.m_stack.clear();
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
/*     */   public void storeInMemory(String a_name, Object a_value) {
/* 449 */     this.m_memory.set(a_name, a_value, -1);
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
/*     */   public CultureMemoryCell storeMatrixMemory(int a_x, int a_y, Object a_value) {
/* 464 */     return this.m_memory.setMatrix(a_x, a_y, a_value);
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
/*     */   public Object readMatrixMemory(int a_x, int a_y) {
/* 478 */     return this.m_memory.getMatrix(a_x, a_y).getCurrentValue();
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
/*     */   public Object readFromMemory(String a_name) {
/* 491 */     return this.m_memory.get(a_name).getCurrentValue();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object readFromMemoryIfExists(String a_name) {
/* 502 */     CultureMemoryCell cell = null;
/*     */     try {
/* 504 */       cell = this.m_memory.get(a_name);
/* 505 */     } catch (IllegalArgumentException iex) {}
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 510 */     if (cell == null) {
/* 511 */       return null;
/*     */     }
/* 513 */     return cell.getCurrentValue();
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
/*     */   public CultureMemoryCell storeIndexedMemory(int a_index, Object a_value) {
/* 527 */     return this.m_memory.set(a_index, a_value, -1, "noname");
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
/*     */   public Object readIndexedMemory(int a_index) {
/* 540 */     CultureMemoryCell cell = this.m_memory.get(a_index);
/* 541 */     if (cell == null) {
/* 542 */       return null;
/*     */     }
/*     */     
/* 545 */     return cell.getCurrentValue();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void clearMemory() {
/* 556 */     this.m_memory.clear();
/*     */   }
/*     */   
/*     */   public GPFitnessFunction getGPFitnessFunction() {
/* 560 */     return this.m_objectiveFunction;
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
/*     */   public synchronized void setFitnessFunction(GPFitnessFunction a_functionToSet) throws InvalidConfigurationException {
/* 581 */     verifyChangesAllowed();
/*     */ 
/*     */     
/* 584 */     if (a_functionToSet == null) {
/* 585 */       throw new InvalidConfigurationException("The FitnessFunction instance may not be null.");
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 591 */     checkProperty("JGAPFITFUNCINST", a_functionToSet, this.m_objectiveFunction, "Fitness function has already been set differently.");
/*     */     
/* 593 */     this.m_objectiveFunction = a_functionToSet;
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
/*     */   public boolean isStrictProgramCreation() {
/* 605 */     return this.m_strictProgramCreation;
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
/*     */   public void setStrictProgramCreation(boolean a_strict) {
/* 617 */     this.m_strictProgramCreation = a_strict;
/*     */   }
/*     */   
/*     */   public int getProgramCreationMaxtries() {
/* 621 */     return this.m_programCreationMaxTries;
/*     */   }
/*     */   
/*     */   public void setProgramCreationMaxTries(int a_maxtries) {
/* 625 */     this.m_programCreationMaxTries = a_maxtries;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public IGPFitnessEvaluator getGPFitnessEvaluator() {
/* 635 */     return this.m_fitnessEvaluator;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean validateNode(ProgramChromosome a_chrom, CommandGene a_node, CommandGene a_rootNode, int a_tries, int a_num, int a_recurseLevel, Class a_type, CommandGene[] a_functionSet, int a_depth, boolean a_grow, int a_childIndex, boolean a_fullProgram) {
/* 670 */     INodeValidator nodeValidator = getNodeValidator();
/* 671 */     if (nodeValidator == null) {
/* 672 */       return true;
/*     */     }
/* 674 */     return nodeValidator.validate(a_chrom, a_node, a_rootNode, a_tries, a_num, a_recurseLevel, a_type, a_functionSet, a_depth, a_grow, a_childIndex, a_fullProgram);
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
/*     */   public void setNodeValidator(INodeValidator a_nodeValidator) {
/* 689 */     this.m_nodeValidator = a_nodeValidator;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public INodeValidator getNodeValidator() {
/* 699 */     return this.m_nodeValidator;
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
/*     */   public boolean equals(Object a_other) {
/*     */     try {
/* 713 */       return (compareTo(a_other) == 0);
/* 714 */     } catch (ClassCastException cex) {
/* 715 */       return false;
/*     */     } 
/*     */   }
/*     */   
/*     */   public int compareTo(Object a_other) {
/* 720 */     if (a_other == null) {
/* 721 */       return 1;
/*     */     }
/*     */     
/* 724 */     GPConfiguration other = (GPConfiguration)a_other;
/* 725 */     return (new CompareToBuilder()).append(this.m_objectiveFunction, other.m_objectiveFunction).append(this.m_crossoverProb, other.m_crossoverProb).append(this.m_reproductionProb, other.m_reproductionProb).append(this.m_newChromsPercent, other.m_newChromsPercent).append(this.m_maxCrossoverDepth, other.m_maxCrossoverDepth).append(this.m_maxInitDepth, other.m_maxInitDepth).append(this.m_selectionMethod.getClass(), other.m_selectionMethod.getClass()).append(this.m_crossMethod.getClass(), other.m_crossMethod.getClass()).append(this.m_programCreationMaxTries, other.m_programCreationMaxTries).append(this.m_strictProgramCreation, other.m_strictProgramCreation).append(this.m_fitnessEvaluator.getClass(), other.m_fitnessEvaluator.getClass()).toComparison();
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
/*     */   public boolean isMaxNodeWarningPrinted() {
/* 751 */     return this.m_warningPrinted;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void flagMaxNodeWarningPrinted() {
/* 761 */     this.m_warningPrinted = true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setPrototypeProgram(IGPProgram a_program) {
/* 772 */     this.m_prototypeProgram = a_program;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public IGPProgram getPrototypeProgram() {
/* 782 */     return this.m_prototypeProgram;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getMemorySize() {
/* 792 */     return this.m_memory.size();
/*     */   }
/*     */   
/*     */   public GPProgramInfo readProgramCache(GPProgram a_prog) {
/* 796 */     GPProgramInfo pci = new GPProgramInfo(a_prog, true);
/* 797 */     pci.setFound(false);
/* 798 */     return (GPProgramInfo)this.m_programCache.get(pci.getToStringNorm());
/*     */   }
/*     */   
/*     */   public GPProgramInfo putToProgramCache(GPProgram a_prog) {
/* 802 */     GPProgramInfo pci = new GPProgramInfo(a_prog, true);
/* 803 */     return this.m_programCache.put(pci.getToStringNorm(), pci);
/*     */   }
/*     */   
/*     */   public boolean isUseProgramCache() {
/* 807 */     return this.m_useProgramCache;
/*     */   }
/*     */   
/*     */   public void setUseProgramCache(boolean a_useCache) {
/* 811 */     this.m_useProgramCache = a_useCache;
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
/*     */   public void putVariable(Variable a_var) {
/* 823 */     this.m_variables.put(a_var.getName(), a_var);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Variable getVariable(String a_varName) {
/* 834 */     return (Variable)this.m_variables.get(a_varName);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object clone() {
/* 844 */     return newInstanceGP(getId(), getName());
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
/*     */   public GPConfiguration newInstanceGP(String a_id, String a_name) {
/*     */     try {
/* 860 */       GPConfiguration result = new GPConfiguration(getName());
/*     */ 
/*     */       
/* 863 */       if (this.m_factory instanceof ICloneable) {
/* 864 */         result.m_factory = (IJGAPFactory)((ICloneable)this.m_factory).clone();
/*     */       
/*     */       }
/*     */       else {
/*     */         
/* 869 */         this.m_factory = (IJGAPFactory)new JGAPFactory(false);
/* 870 */         result.m_factory = (IJGAPFactory)((JGAPFactory)this.m_factory).clone();
/*     */       } 
/* 872 */       if (result.m_factory == null) {
/* 873 */         throw new IllegalStateException("JGAPFactory must not be null!");
/*     */       }
/* 875 */       if (this.m_objectiveFunction != null) {
/* 876 */         result.m_objectiveFunction = this.m_objectiveFunction;
/*     */       }
/* 878 */       result.m_crossoverProb = this.m_crossoverProb;
/* 879 */       result.m_reproductionProb = this.m_reproductionProb;
/* 880 */       result.m_newChromsPercent = this.m_newChromsPercent;
/* 881 */       result.m_functionProb = this.m_functionProb;
/* 882 */       result.m_maxCrossoverDepth = this.m_maxCrossoverDepth;
/* 883 */       result.m_maxInitDepth = this.m_maxInitDepth;
/* 884 */       result.m_minInitDepth = this.m_minInitDepth;
/* 885 */       result.m_strictProgramCreation = this.m_strictProgramCreation;
/* 886 */       result.m_programCreationMaxTries = this.m_programCreationMaxTries;
/* 887 */       result.m_selectionMethod = (INaturalGPSelector)doClone(this.m_selectionMethod);
/* 888 */       result.m_crossMethod = (CrossMethod)doClone(this.m_crossMethod);
/* 889 */       result.m_fitnessEvaluator = (IGPFitnessEvaluator)doClone(this.m_fitnessEvaluator);
/*     */       
/* 891 */       result.m_nodeValidator = (INodeValidator)doClone(this.m_nodeValidator);
/* 892 */       result.m_useProgramCache = this.m_useProgramCache;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 898 */       result.setName(a_name);
/* 899 */       result.setId(a_id);
/* 900 */       result.makeThreadKey();
/* 901 */       return result;
/* 902 */     } catch (Throwable t) {
/* 903 */       throw new CloneException(t);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public IJGAPFactory getJGAPFactory() {
/* 914 */     return this.m_factory;
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
/*     */   private void readObject(ObjectInputStream a_inputStream) throws IOException, ClassNotFoundException {
/* 931 */     a_inputStream.defaultReadObject();
/*     */     try {
/* 933 */       init(false);
/* 934 */     } catch (InvalidConfigurationException iex) {
/* 935 */       iex.printStackTrace();
/* 936 */       throw new IOException(iex.toString());
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
/*     */   public void setInitStrategy(IGPInitStrategy a_strategy) {
/* 948 */     this.m_initStrategy = a_strategy;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public IGPInitStrategy getInitStrategy() {
/* 959 */     return this.m_initStrategy;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\gp\impl\GPConfiguration.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */