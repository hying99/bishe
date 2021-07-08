/*      */ package org.jgap.gp.impl;
/*      */ 
/*      */ import java.lang.reflect.Constructor;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collections;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.StringTokenizer;
/*      */ import java.util.Vector;
/*      */ import org.jgap.InvalidConfigurationException;
/*      */ import org.jgap.UnsupportedRepresentationException;
/*      */ import org.jgap.gp.BaseGPChromosome;
/*      */ import org.jgap.gp.CommandGene;
/*      */ import org.jgap.gp.IGPChromosome;
/*      */ import org.jgap.gp.IGPInitStrategy;
/*      */ import org.jgap.gp.IGPProgram;
/*      */ import org.jgap.gp.IMutateable;
/*      */ import org.jgap.gp.terminal.Argument;
/*      */ import org.jgap.gp.terminal.NOP;
/*      */ import org.jgap.util.ICloneable;
/*      */ import org.jgap.util.StringKit;
/*      */ import org.jgap.util.UniqueRandomGenerator;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class ProgramChromosome
/*      */   extends BaseGPChromosome
/*      */   implements Comparable, Cloneable
/*      */ {
/*      */   private static final String CVS_REVISION = "$Revision: 1.32 $";
/*      */   static final String PERSISTENT_FIELD_DELIMITER = ":";
/*      */   static final String GENE_DELIMITER_HEADING = "<";
/*      */   static final String GENE_DELIMITER_CLOSING = ">";
/*      */   static final String GENE_DELIMITER = "#";
/*      */   private CommandGene[] m_functionSet;
/*      */   private int[] m_depth;
/*      */   private Class[] argTypes;
/*      */   private transient int m_index;
/*      */   private transient int m_maxDepth;
/*      */   private CommandGene[] m_genes;
/*      */   private Object m_applicationData;
/*      */   private boolean m_compareAppData;
/*      */   
/*      */   public ProgramChromosome(GPConfiguration a_conf, int a_size) throws InvalidConfigurationException {
/*   79 */     super(a_conf);
/*   80 */     if (a_size <= 0) {
/*   81 */       throw new IllegalArgumentException("Chromosome size must be greater than zero");
/*      */     }
/*      */     
/*   84 */     init(a_size);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public ProgramChromosome(GPConfiguration a_conf, int a_size, IGPProgram a_ind) throws InvalidConfigurationException {
/*   90 */     super(a_conf, a_ind);
/*   91 */     if (a_size <= 0) {
/*   92 */       throw new IllegalArgumentException("Chromosome size must be greater than zero");
/*      */     }
/*      */     
/*   95 */     if (a_ind == null) {
/*   96 */       throw new IllegalArgumentException("Individual must not be null");
/*      */     }
/*   98 */     init(a_size);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ProgramChromosome(GPConfiguration a_conf, int a_size, CommandGene[] a_functionSet, Class[] a_argTypes, IGPProgram a_ind) throws InvalidConfigurationException {
/*  106 */     super(a_conf, a_ind);
/*  107 */     if (a_size <= 0) {
/*  108 */       throw new IllegalArgumentException("Chromosome size must be greater than zero");
/*      */     }
/*      */     
/*  111 */     if (a_ind == null) {
/*  112 */       throw new IllegalArgumentException("Individual must not be null");
/*      */     }
/*  114 */     this.m_functionSet = a_functionSet;
/*  115 */     this.argTypes = a_argTypes;
/*  116 */     init(a_size);
/*      */   }
/*      */ 
/*      */   
/*      */   public ProgramChromosome(GPConfiguration a_conf, CommandGene[] a_initialGenes) throws InvalidConfigurationException {
/*  121 */     super(a_conf);
/*  122 */     int i = 0;
/*  123 */     while (i < a_initialGenes.length && a_initialGenes[i] != null) {
/*  124 */       i++;
/*      */     }
/*  126 */     init(a_initialGenes.length);
/*  127 */     for (int k = 0; k < i; k++) {
/*  128 */       this.m_genes[k] = a_initialGenes[k];
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   public ProgramChromosome(GPConfiguration a_conf) throws InvalidConfigurationException {
/*  134 */     super(a_conf);
/*  135 */     init();
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
/*      */   public ProgramChromosome() throws InvalidConfigurationException {
/*  148 */     this(GPGenotype.getStaticGPConfiguration());
/*      */   }
/*      */ 
/*      */   
/*      */   private void init() throws InvalidConfigurationException {
/*  153 */     init(getGPConfiguration().getPopulationSize());
/*      */   }
/*      */ 
/*      */   
/*      */   private void init(int a_size) throws InvalidConfigurationException {
/*  158 */     this.m_depth = new int[a_size];
/*  159 */     this.m_genes = new CommandGene[a_size];
/*      */   }
/*      */ 
/*      */   
/*      */   public void setArgTypes(Class[] a_argTypes) {
/*  164 */     this.argTypes = a_argTypes;
/*      */   }
/*      */   
/*      */   public synchronized Object clone() {
/*      */     try {
/*  169 */       int size = this.m_genes.length;
/*  170 */       CommandGene[] genes = new CommandGene[size];
/*  171 */       for (int i = 0; i < size && 
/*  172 */         this.m_genes[i] != null; i++) {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  177 */         if (ICloneable.class.isAssignableFrom(this.m_genes[i].getClass())) {
/*  178 */           genes[i] = (CommandGene)((ICloneable)this.m_genes[i]).clone();
/*      */         
/*      */         }
/*      */         else {
/*      */           
/*  183 */           genes[i] = this.m_genes[i];
/*      */         } 
/*      */       } 
/*  186 */       ProgramChromosome chrom = new ProgramChromosome(getGPConfiguration(), genes);
/*      */       
/*  188 */       chrom.argTypes = (Class[])this.argTypes.clone();
/*  189 */       if (getFunctionSet() != null) {
/*  190 */         chrom.setFunctionSet((CommandGene[])getFunctionSet().clone());
/*      */       }
/*  192 */       if (this.m_depth != null) {
/*  193 */         chrom.m_depth = (int[])this.m_depth.clone();
/*      */       }
/*  195 */       chrom.setIndividual(getIndividual());
/*  196 */       return chrom;
/*      */     }
/*  198 */     catch (Exception cex) {
/*      */ 
/*      */       
/*  201 */       throw new IllegalStateException(cex);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void cleanup() {
/*  212 */     int len = this.m_genes.length;
/*  213 */     for (int i = 0; i < len && 
/*  214 */       this.m_genes[i] != null; i++)
/*      */     {
/*      */       
/*  217 */       this.m_genes[i].cleanup();
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
/*      */   public void growOrFull(int a_num, int a_depth, Class a_type, Class[] a_argTypes, CommandGene[] a_functionSet, boolean a_grow, int a_tries) {
/*      */     try {
/*      */       CommandGene n;
/*  240 */       this.argTypes = a_argTypes;
/*  241 */       setFunctionSet(new CommandGene[a_functionSet.length + a_argTypes.length]);
/*  242 */       System.arraycopy(a_functionSet, 0, getFunctionSet(), 0, a_functionSet.length);
/*      */       
/*  244 */       for (int i = 0; i < a_argTypes.length; i++) {
/*  245 */         this.m_functionSet[a_functionSet.length + i] = (CommandGene)new Argument(getGPConfiguration(), i, a_argTypes[i]);
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  251 */       IGPInitStrategy programIniter = getGPConfiguration().getInitStrategy();
/*  252 */       if (programIniter == null) {
/*  253 */         n = null;
/*      */       } else {
/*      */         
/*      */         try {
/*  257 */           n = programIniter.init((IGPChromosome)this, a_num);
/*  258 */         } catch (Exception ex) {
/*  259 */           throw new IllegalStateException(ex);
/*      */         } 
/*      */       } 
/*      */ 
/*      */       
/*  264 */       int localDepth = a_depth;
/*  265 */       this.m_index = 0;
/*  266 */       this.m_maxDepth = localDepth;
/*  267 */       growOrFullNode(a_num, localDepth, a_type, 0, this.m_functionSet, n, 0, a_grow, -1, false);
/*      */ 
/*      */ 
/*      */       
/*  271 */       if (!getGPConfiguration().validateNode(this, (CommandGene)null, n, a_tries, a_num, 0, a_type, this.m_functionSet, a_depth, a_grow, -1, true))
/*      */       {
/*      */         
/*  274 */         throw new IllegalStateException("Randomly created program violates configuration constraints (symptom 3).");
/*      */       }
/*      */ 
/*      */       
/*  278 */       redepth();
/*      */     }
/*  280 */     catch (InvalidConfigurationException iex) {
/*  281 */       throw new IllegalStateException(iex.getMessage());
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
/*      */   public String toString(int a_startNode) {
/*  295 */     if (a_startNode < 0) {
/*  296 */       return "";
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*  301 */     String funcName = this.m_genes[a_startNode].toString();
/*  302 */     int j = 1;
/*      */     while (true) {
/*  304 */       String placeHolder = "&" + j;
/*  305 */       int foundIndex = funcName.indexOf(placeHolder);
/*  306 */       if (foundIndex < 0) {
/*      */         break;
/*      */       }
/*  309 */       funcName = funcName.replaceFirst(placeHolder, "");
/*  310 */       j++;
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/*  315 */     if (j > 0) {
/*  316 */       funcName = funcName.trim();
/*      */     }
/*  318 */     IGPProgram ind = getIndividual();
/*  319 */     if (getFunctions()[a_startNode].getArity(ind) == 0) {
/*  320 */       return funcName + " ";
/*      */     }
/*  322 */     String str = "";
/*  323 */     str = str + funcName + " ( ";
/*  324 */     int arity = this.m_genes[a_startNode].getArity(ind);
/*  325 */     for (int i = 0; i < arity; i++) {
/*  326 */       str = str + toString(getChild(a_startNode, i));
/*      */     }
/*  328 */     if (a_startNode == 0) {
/*  329 */       str = str + ")";
/*      */     } else {
/*      */       
/*  332 */       str = str + ") ";
/*      */     } 
/*  334 */     return str;
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
/*      */   public String toStringNorm(int a_startNode) {
/*  346 */     if (a_startNode < 0) {
/*  347 */       return "";
/*      */     }
/*  349 */     IGPProgram ind = getIndividual();
/*  350 */     if (this.m_genes[a_startNode].getArity(ind) == 0) {
/*  351 */       return getFunctions()[a_startNode].toString();
/*      */     }
/*  353 */     String str = "";
/*  354 */     boolean paramOutput = false;
/*  355 */     if (this.m_genes[a_startNode].getArity(ind) > 0 && 
/*  356 */       this.m_genes[a_startNode].toString().indexOf("&1") >= 0) {
/*  357 */       paramOutput = true;
/*      */     }
/*      */     
/*  360 */     if (this.m_genes[a_startNode].getArity(ind) == 1 || paramOutput) {
/*  361 */       str = str + getFunctions()[a_startNode].toString();
/*      */     }
/*  363 */     if (a_startNode > 0) {
/*  364 */       str = "(" + str;
/*      */     }
/*  366 */     for (int i = 0; i < this.m_genes[a_startNode].getArity(ind); i++) {
/*  367 */       String childString = toStringNorm(getChild(a_startNode, i));
/*  368 */       String placeHolder = "&" + (i + 1);
/*  369 */       int placeholderIndex = str.indexOf(placeHolder);
/*  370 */       if (placeholderIndex >= 0) {
/*  371 */         str = str.replaceFirst(placeHolder, childString);
/*      */       } else {
/*      */         
/*  374 */         str = str + childString;
/*      */       } 
/*  376 */       if (i == 0 && this.m_genes[a_startNode].getArity(ind) != 1 && !paramOutput)
/*      */       {
/*  378 */         str = str + " " + this.m_genes[a_startNode].toString() + " ";
/*      */       }
/*      */     } 
/*  381 */     if (a_startNode > 0) {
/*  382 */       str = str + ")";
/*      */     }
/*  384 */     return str;
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
/*      */   public boolean isPossible(Class a_returnType, int a_subReturnType, CommandGene[] a_nodeSet, boolean a_function, boolean a_growing) {
/*  405 */     IGPProgram ind = getIndividual();
/*  406 */     for (int i = 0; i < a_nodeSet.length; i++) {
/*  407 */       if (a_nodeSet[i].getReturnType() == a_returnType && (a_subReturnType == 0 || a_subReturnType == a_nodeSet[i].getSubReturnType())) {
/*      */ 
/*      */         
/*  410 */         if (a_nodeSet[i].getArity(ind) == 0 && (!a_function || a_growing)) {
/*  411 */           return true;
/*      */         }
/*  413 */         if (a_nodeSet[i].getArity(ind) != 0 && a_function) {
/*  414 */           return true;
/*      */         }
/*      */       } 
/*      */     } 
/*  418 */     return false;
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
/*      */   protected CommandGene selectNode(int a_chromIndex, Class<Void> a_returnType, int a_subReturnType, CommandGene[] a_functionSet, boolean a_function, boolean a_growing) {
/*  439 */     if (!isPossible(a_returnType, a_subReturnType, a_functionSet, a_function, a_growing)) {
/*      */       
/*  441 */       if (a_growing && (a_returnType == CommandGene.VoidClass || a_returnType == Void.class)) {
/*      */         
/*      */         try {
/*      */ 
/*      */           
/*  446 */           return (CommandGene)new NOP(getGPConfiguration(), a_subReturnType);
/*      */         }
/*  448 */         catch (InvalidConfigurationException iex) {
/*      */ 
/*      */           
/*  451 */           throw new RuntimeException(iex);
/*      */         } 
/*      */       }
/*  454 */       String errormsg = "Chromosome (index " + a_chromIndex + ") requires a " + (a_function ? ("function" + (a_growing ? " or terminal" : "")) : "terminal") + " of return type " + a_returnType + " (sub return type " + a_subReturnType + ")" + " but there is no such node available";
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  462 */       if (!getGPConfiguration().isStrictProgramCreation())
/*      */       {
/*      */         
/*  465 */         throw new IllegalStateException(errormsg);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*  470 */       throw new RuntimeException(errormsg);
/*      */     } 
/*      */     
/*  473 */     CommandGene n = null;
/*      */ 
/*      */ 
/*      */     
/*  477 */     IGPProgram ind = getIndividual();
/*  478 */     UniqueRandomGenerator randGen = new UniqueRandomGenerator(a_functionSet.length, getGPConfiguration().getRandomGenerator());
/*      */     
/*      */     while (true) {
/*  481 */       int lindex = randGen.nextInt();
/*      */ 
/*      */       
/*  484 */       if (a_functionSet[lindex].getReturnType() == a_returnType && (a_subReturnType == 0 || a_functionSet[lindex].getSubReturnType() == a_subReturnType)) {
/*      */ 
/*      */         
/*  487 */         if (a_functionSet[lindex].getArity(ind) == 0 && (!a_function || a_growing))
/*      */         {
/*  489 */           n = a_functionSet[lindex];
/*      */         }
/*  491 */         if (a_functionSet[lindex].getArity(ind) != 0 && a_function) {
/*  492 */           n = a_functionSet[lindex];
/*      */         }
/*      */       } 
/*      */       
/*  496 */       if (n != null) {
/*  497 */         return n;
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
/*      */   protected void growOrFullNode(int a_num, int a_depth, Class a_returnType, int a_subReturnType, CommandGene[] a_functionSet, CommandGene a_rootNode, int a_recurseLevel, boolean a_grow, int a_childNum, boolean a_validateNode) {
/*  525 */     boolean mutated = false;
/*  526 */     GPConfiguration conf = getGPConfiguration();
/*  527 */     if (a_rootNode == null || a_validateNode) {
/*  528 */       CommandGene node; int tries = 0;
/*  529 */       int evolutionRound = getGPConfiguration().getGenerationNr();
/*      */       
/*  531 */       CommandGene[] localFunctionSet = (CommandGene[])a_functionSet.clone();
/*      */       while (true) {
/*  533 */         node = selectNode(a_num, a_returnType, a_subReturnType, localFunctionSet, (a_depth >= 1), a_grow);
/*      */         
/*  535 */         if (!conf.validateNode(this, node, a_rootNode, tries++, a_num, a_recurseLevel, a_returnType, localFunctionSet, a_depth, a_grow, a_childNum, false))
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*  541 */           if (evolutionRound > 0 || tries <= 10) {
/*      */ 
/*      */             
/*  544 */             localFunctionSet = remove(localFunctionSet, node);
/*  545 */             if (localFunctionSet.length == 0) {
/*  546 */               throw new IllegalStateException("No appropriate function found during program creation!");
/*      */             }
/*      */             
/*      */             continue;
/*      */           } 
/*      */         }
/*      */         break;
/*      */       } 
/*  554 */       if (conf.getRandomGenerator().nextDouble() <= conf.getMutationProb() && 
/*  555 */         IMutateable.class.isAssignableFrom(node.getClass())) {
/*      */         try {
/*  557 */           node = ((IMutateable)node).applyMutation(0, 1.0D);
/*  558 */           mutated = true;
/*  559 */         } catch (InvalidConfigurationException iex) {}
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  565 */       a_rootNode = node;
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  572 */     this.m_depth[this.m_index] = this.m_maxDepth - a_depth;
/*  573 */     if (!mutated && a_rootNode instanceof ICloneable) {
/*  574 */       this.m_genes[this.m_index++] = (CommandGene)((ICloneable)a_rootNode).clone();
/*      */     } else {
/*      */       
/*  577 */       this.m_genes[this.m_index++] = a_rootNode;
/*      */     } 
/*  579 */     if (a_depth >= 1) {
/*  580 */       IGPProgram ind = getIndividual();
/*  581 */       for (int i = 0; i < a_rootNode.getArity(ind); i++)
/*      */       {
/*  583 */         if (this.m_index < this.m_depth.length) {
/*  584 */           growOrFullNode(a_num, a_depth - 1, a_rootNode.getChildType(getIndividual(), i), a_rootNode.getSubChildType(i), a_functionSet, a_rootNode, a_recurseLevel + 1, a_grow, i, true);
/*      */ 
/*      */ 
/*      */         
/*      */         }
/*      */         else {
/*      */ 
/*      */ 
/*      */           
/*  593 */           throw new IllegalStateException("Randomly created program violates configuration constraints (symptom 1). It may be that you specified a too small number of maxNodes to use!");
/*      */         
/*      */         }
/*      */       
/*      */       }
/*      */     
/*      */     }
/*  600 */     else if (a_rootNode.getArity(getIndividual()) > 0) {
/*      */ 
/*      */       
/*  603 */       throw new IllegalStateException("Randomly created program violates configuration constraints (symptom 2)");
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
/*      */   public void redepth() {
/*  617 */     this.m_depth[0] = 0;
/*  618 */     redepth(0);
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
/*      */   protected int redepth(int a_index) {
/*  637 */     int num = a_index + 1;
/*  638 */     CommandGene command = getNode(a_index);
/*  639 */     if (command == null) {
/*  640 */       throw new IllegalStateException("ProgramChromosome invalid at index " + a_index);
/*      */     }
/*      */     
/*  643 */     IGPProgram ind = getIndividual();
/*  644 */     int arity = command.getArity(ind);
/*  645 */     for (int i = 0; i < arity; i++) {
/*  646 */       if (num < this.m_depth.length) {
/*  647 */         this.m_depth[num] = this.m_depth[a_index] + 1;
/*      */         
/*  649 */         num = redepth(num);
/*  650 */         if (num < 0) {
/*      */           break;
/*      */         }
/*      */       } else {
/*      */         
/*  655 */         return -1;
/*      */       } 
/*      */     } 
/*  658 */     return num;
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
/*      */   public int getChild(int a_index, int a_child) {
/*  675 */     int len = (getFunctions()).length;
/*  676 */     for (int i = a_index + 1; i < len; i++) {
/*  677 */       if (this.m_depth[i] <= this.m_depth[a_index]) {
/*  678 */         return -1;
/*      */       }
/*  680 */       if (this.m_depth[i] == this.m_depth[a_index] + 1 && 
/*  681 */         --a_child < 0) {
/*  682 */         return i;
/*      */       }
/*      */     } 
/*      */     
/*  686 */     throw new RuntimeException("Bad child " + a_child + " of node with index = " + a_index);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public CommandGene[] getFunctionSet() {
/*  693 */     return this.m_functionSet;
/*      */   }
/*      */   
/*      */   public void setFunctionSet(CommandGene[] a_functionSet) {
/*  697 */     this.m_functionSet = a_functionSet;
/*      */   }
/*      */   
/*      */   public CommandGene[] getFunctions() {
/*  701 */     return this.m_genes;
/*      */   }
/*      */ 
/*      */   
/*      */   public void setFunctions(CommandGene[] a_functions) throws InvalidConfigurationException {
/*  706 */     this.m_genes = a_functions;
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
/*      */   public int getSize(int a_index) {
/*      */     int i;
/*  721 */     for (i = a_index + 1; i < this.m_genes.length && this.m_genes[i] != null; 
/*  722 */       i++) {
/*  723 */       if (this.m_depth[i] <= this.m_depth[a_index]) {
/*      */         break;
/*      */       }
/*      */     } 
/*  727 */     return i - a_index;
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
/*      */   public int getDepth(int a_index) {
/*  740 */     int maxdepth = this.m_depth[a_index];
/*  741 */     for (int i = a_index + 1; i < this.m_genes.length && this.m_genes[i] != null; 
/*  742 */       i++) {
/*  743 */       if (this.m_depth[i] <= this.m_depth[a_index]) {
/*      */         break;
/*      */       }
/*  746 */       if (this.m_depth[i] > maxdepth) {
/*  747 */         maxdepth = this.m_depth[i];
/*      */       }
/*      */     } 
/*  750 */     return maxdepth - this.m_depth[a_index];
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
/*      */   public int getParentNode(int a_child) {
/*  765 */     if (a_child >= this.m_genes.length || this.m_genes[a_child] == null) {
/*  766 */       return -1;
/*      */     }
/*  768 */     for (int i = a_child - 1; i >= 0; i--) {
/*  769 */       if (this.m_depth[i] == this.m_depth[a_child] - 1) {
/*  770 */         return i;
/*      */       }
/*      */     } 
/*  773 */     return -1;
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
/*      */   public CommandGene getNode(Class a_type, boolean a_exactMatch) {
/*  788 */     return getNode(a_type, a_exactMatch, 0);
/*      */   }
/*      */ 
/*      */   
/*      */   public CommandGene getNode(Class<?> a_type, boolean a_exactMatch, int a_startIndex) {
/*  793 */     int size = this.m_genes.length;
/*  794 */     for (int i = a_startIndex; i < size && 
/*  795 */       this.m_genes[i] != null; i++) {
/*  796 */       if (a_exactMatch) {
/*  797 */         if (this.m_genes[i].getClass() == a_type) {
/*  798 */           (this.m_genes[i]).nodeIndex = i;
/*  799 */           return this.m_genes[i];
/*      */         }
/*      */       
/*      */       }
/*  803 */       else if (a_type.isAssignableFrom(this.m_genes[i].getClass())) {
/*  804 */         (this.m_genes[i]).nodeIndex = i;
/*  805 */         return this.m_genes[i];
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  813 */     return null;
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
/*      */   public boolean execute_boolean(Object[] args) {
/*  828 */     boolean rtn = this.m_genes[0].execute_boolean(this, 0, args);
/*  829 */     cleanup();
/*  830 */     return rtn;
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
/*      */   public boolean execute_boolean(int n, int child, Object[] args) {
/*  847 */     if (child == 0) {
/*  848 */       return this.m_genes[n + 1].execute_boolean(this, n + 1, args);
/*      */     }
/*  850 */     int other = getChild(n, child);
/*  851 */     return this.m_genes[other].execute_boolean(this, other, args);
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
/*      */   public void execute_void(Object[] args) {
/*  864 */     this.m_genes[0].execute_void(this, 0, args);
/*  865 */     cleanup();
/*      */   }
/*      */   
/*      */   public void execute_void(int n, int child, Object[] args) {
/*  869 */     if (child == 0) {
/*  870 */       this.m_genes[n + 1].execute_void(this, n + 1, args);
/*      */     } else {
/*      */       
/*  873 */       int other = getChild(n, child);
/*  874 */       this.m_genes[other].execute_void(this, other, args);
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
/*      */   public int execute_int(Object[] args) {
/*  890 */     int rtn = this.m_genes[0].execute_int(this, 0, args);
/*  891 */     cleanup();
/*  892 */     return rtn;
/*      */   }
/*      */   
/*      */   public int execute_int(int n, int child, Object[] args) {
/*  896 */     if (child == 0) {
/*  897 */       return this.m_genes[n + 1].execute_int(this, n + 1, args);
/*      */     }
/*      */     
/*  900 */     int other = getChild(n, child);
/*  901 */     return this.m_genes[other].execute_int(this, other, args);
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
/*      */   public long execute_long(Object[] args) {
/*  916 */     long rtn = this.m_genes[0].execute_long(this, 0, args);
/*  917 */     cleanup();
/*  918 */     return rtn;
/*      */   }
/*      */   
/*      */   public long execute_long(int n, int child, Object[] args) {
/*  922 */     if (child == 0) {
/*  923 */       return this.m_genes[n + 1].execute_long(this, n + 1, args);
/*      */     }
/*  925 */     int other = getChild(n, child);
/*  926 */     return this.m_genes[other].execute_long(this, other, args);
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
/*      */   public float execute_float(Object[] args) {
/*  940 */     float rtn = this.m_genes[0].execute_float(this, 0, args);
/*  941 */     cleanup();
/*  942 */     return rtn;
/*      */   }
/*      */   
/*      */   public float execute_float(int n, int child, Object[] args) {
/*  946 */     if (child == 0) {
/*  947 */       return this.m_genes[n + 1].execute_float(this, n + 1, args);
/*      */     }
/*  949 */     int other = getChild(n, child);
/*  950 */     return this.m_genes[other].execute_float(this, other, args);
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
/*      */   public double execute_double(Object[] args) {
/*  964 */     double rtn = this.m_genes[0].execute_double(this, 0, args);
/*  965 */     cleanup();
/*  966 */     return rtn;
/*      */   }
/*      */   
/*      */   public double execute_double(int n, int child, Object[] args) {
/*  970 */     if (child == 0) {
/*  971 */       return this.m_genes[n + 1].execute_double(this, n + 1, args);
/*      */     }
/*  973 */     int other = getChild(n, child);
/*  974 */     return this.m_genes[other].execute_double(this, other, args);
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
/*      */   public Object execute_object(Object[] args) {
/*  989 */     Object rtn = this.m_genes[0].execute_object(this, 0, args);
/*  990 */     cleanup();
/*  991 */     return rtn;
/*      */   }
/*      */   
/*      */   public Object execute_object(int n, int child, Object[] args) {
/*  995 */     if (child == 0) {
/*  996 */       return this.m_genes[n + 1].execute_object(this, n + 1, args);
/*      */     }
/*  998 */     int other = getChild(n, child);
/*  999 */     return this.m_genes[other].execute_object(this, other, args);
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
/*      */   public Object execute(Object[] args) {
/* 1013 */     return this.m_genes[0].execute_object(this, 0, args);
/*      */   }
/*      */   
/*      */   public Object execute(int n, int child, Object[] args) {
/* 1017 */     return execute_object(n, child, args);
/*      */   }
/*      */   
/*      */   public void setGene(int index, CommandGene a_gene) {
/* 1021 */     if (a_gene == null) {
/* 1022 */       throw new IllegalArgumentException("Gene may not be null!");
/*      */     }
/* 1024 */     this.m_genes[index] = a_gene;
/*      */   }
/*      */   
/*      */   public Class[] getArgTypes() {
/* 1028 */     return this.argTypes;
/*      */   }
/*      */   
/*      */   public int getArity() {
/* 1032 */     return this.argTypes.length;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int size() {
/* 1042 */     int i = 0;
/* 1043 */     while (i < this.m_genes.length && this.m_genes[i] != null) {
/* 1044 */       i++;
/*      */     }
/* 1046 */     return i;
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
/*      */   public int compareTo(Object a_other) {
/* 1067 */     if (a_other == null) {
/* 1068 */       return 1;
/*      */     }
/* 1070 */     int size = size();
/* 1071 */     ProgramChromosome otherChromosome = (ProgramChromosome)a_other;
/* 1072 */     CommandGene[] otherGenes = otherChromosome.m_genes;
/*      */ 
/*      */ 
/*      */     
/* 1076 */     if (otherChromosome.size() != size) {
/* 1077 */       return size() - otherChromosome.size();
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1083 */     for (int i = 0; i < size; i++) {
/* 1084 */       int comparison = this.m_genes[i].compareTo(otherGenes[i]);
/* 1085 */       if (comparison != 0) {
/* 1086 */         return comparison;
/*      */       }
/*      */     } 
/*      */     
/* 1090 */     if (isCompareApplicationData())
/*      */     {
/*      */       
/* 1093 */       if (getApplicationData() == null) {
/* 1094 */         if (otherChromosome.getApplicationData() != null) {
/* 1095 */           return -1;
/*      */         }
/*      */       } else {
/* 1098 */         if (otherChromosome.getApplicationData() == null) {
/* 1099 */           return 1;
/*      */         }
/*      */         
/* 1102 */         if (getApplicationData() instanceof Comparable) {
/*      */           try {
/* 1104 */             return ((Comparable<Object>)getApplicationData()).compareTo(otherChromosome.getApplicationData());
/*      */           
/*      */           }
/* 1107 */           catch (ClassCastException cex) {
/* 1108 */             return -1;
/*      */           } 
/*      */         }
/*      */         
/* 1112 */         return getApplicationData().getClass().getName().compareTo(otherChromosome.getApplicationData().getClass().getName());
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1119 */     return 0;
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
/*      */     try {
/* 1133 */       return (compareTo(a_other) == 0);
/*      */     }
/* 1135 */     catch (ClassCastException cex) {
/* 1136 */       return false;
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
/*      */   public void setCompareApplicationData(boolean a_doCompare) {
/* 1151 */     this.m_compareAppData = a_doCompare;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isCompareApplicationData() {
/* 1161 */     return this.m_compareAppData;
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
/*      */   public Object getApplicationData() {
/* 1177 */     return this.m_applicationData;
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
/*      */   public synchronized CommandGene getGene(int a_locus) {
/* 1192 */     return this.m_genes[a_locus];
/*      */   }
/*      */   
/*      */   private CommandGene[] remove(CommandGene[] a_functionSet, CommandGene node) {
/* 1196 */     int size = a_functionSet.length;
/* 1197 */     for (int i = 0; i < size; i++) {
/* 1198 */       if (a_functionSet[i] == node) {
/*      */         
/* 1200 */         CommandGene[] result = new CommandGene[size - 1];
/* 1201 */         if (i > 0) {
/* 1202 */           System.arraycopy(a_functionSet, 0, result, 0, i);
/*      */         }
/* 1204 */         if (size - i > 1) {
/* 1205 */           System.arraycopy(a_functionSet, i + 1, result, i, size - i - 1);
/*      */         }
/* 1207 */         return result;
/*      */       } 
/*      */     } 
/* 1210 */     return a_functionSet;
/*      */   }
/*      */   
/*      */   protected String encode(String a_string) {
/* 1214 */     return StringKit.encode(a_string);
/*      */   }
/*      */   
/*      */   protected String decode(String a_string) {
/* 1218 */     return StringKit.decode(a_string);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getPersistentRepresentation() {
/* 1229 */     StringBuffer b = new StringBuffer();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1236 */     for (CommandGene gene : this.m_genes) {
/* 1237 */       if (gene == null) {
/*      */         break;
/*      */       }
/* 1240 */       b.append("<");
/* 1241 */       b.append(encode(gene.getClass().getName() + "#" + gene.getPersistentRepresentation()));
/*      */ 
/*      */ 
/*      */       
/* 1245 */       b.append(">");
/*      */     } 
/* 1247 */     return b.toString();
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
/*      */   public void setValueFromPersistentRepresentation(String a_representation) throws UnsupportedRepresentationException {
/* 1261 */     if (a_representation != null) {
/*      */       try {
/* 1263 */         List r = split(a_representation);
/* 1264 */         Iterator<String> iter = r.iterator();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1270 */         List<CommandGene> genes = new Vector();
/* 1271 */         while (iter.hasNext()) {
/* 1272 */           String g = decode(iter.next());
/* 1273 */           StringTokenizer st = new StringTokenizer(g, "#");
/* 1274 */           if (st.countTokens() != 2) {
/* 1275 */             throw new UnsupportedRepresentationException("In " + g + ", " + "expecting two tokens, separated by " + "#");
/*      */           }
/* 1277 */           String clas = st.nextToken();
/* 1278 */           String representation = st.nextToken();
/* 1279 */           CommandGene gene = createGene(clas, representation);
/* 1280 */           genes.add(gene);
/*      */         } 
/* 1282 */         this.m_genes = genes.<CommandGene>toArray(new CommandGene[0]);
/*      */       }
/* 1284 */       catch (Exception ex) {
/* 1285 */         throw new UnsupportedRepresentationException(ex.toString());
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
/*      */   protected CommandGene createGene(String a_geneClassName, String a_persistentRepresentation) throws Exception {
/* 1306 */     Class<?> geneClass = Class.forName(a_geneClassName);
/* 1307 */     Constructor<?> constr = geneClass.getConstructor(new Class[] { GPConfiguration.class });
/* 1308 */     CommandGene gene = (CommandGene)constr.newInstance(new Object[] { getGPConfiguration() });
/* 1309 */     gene.setValueFromPersistentRepresentation(a_persistentRepresentation);
/* 1310 */     return gene;
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
/*      */   protected static final List split(String a_string) throws UnsupportedRepresentationException {
/* 1326 */     List<?> a = Collections.synchronizedList(new ArrayList());
/* 1327 */     StringTokenizer st = new StringTokenizer(a_string, "<>", true);
/*      */     
/* 1329 */     while (st.hasMoreTokens()) {
/* 1330 */       if (!st.nextToken().equals("<")) {
/* 1331 */         throw new UnsupportedRepresentationException(a_string + " no opening tag");
/*      */       }
/* 1333 */       String n = st.nextToken();
/* 1334 */       if (n.equals(">")) {
/*      */         
/* 1336 */         a.add("");
/*      */         continue;
/*      */       } 
/* 1339 */       a.add(n);
/* 1340 */       if (!st.nextToken().equals(">")) {
/* 1341 */         throw new UnsupportedRepresentationException(a_string + " no closing tag");
/*      */       }
/*      */     } 
/*      */ 
/*      */     
/* 1346 */     return a;
/*      */   }
/*      */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\gp\impl\ProgramChromosome.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */