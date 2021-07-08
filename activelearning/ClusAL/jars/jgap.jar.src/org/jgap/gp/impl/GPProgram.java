/*     */ package org.jgap.gp.impl;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.util.Arrays;
/*     */ import org.jgap.ICloneHandler;
/*     */ import org.jgap.InvalidConfigurationException;
/*     */ import org.jgap.gp.CommandGene;
/*     */ import org.jgap.gp.GPProgramBase;
/*     */ import org.jgap.gp.IGPProgram;
/*     */ import org.jgap.gp.function.ADF;
/*     */ import org.jgap.gp.terminal.Argument;
/*     */ import org.jgap.util.CloneException;
/*     */ import org.jgap.util.ICloneable;
/*     */ import org.jgap.util.StringKit;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class GPProgram
/*     */   extends GPProgramBase
/*     */   implements Serializable, Comparable, ICloneable
/*     */ {
/*     */   private static final String CVS_REVISION = "$Revision: 1.17 $";
/*     */   static final String PROGRAMCHROM_DELIMITER_HEADING = "<";
/*     */   static final String PROGRAMCHROM_DELIMITER_CLOSING = ">";
/*     */   static final String PROGRAMCHROM_DELIMITER = "#";
/*     */   private ProgramChromosome[] m_chromosomes;
/*     */   
/*     */   public GPProgram(GPConfiguration a_conf, Class[] a_types, Class[][] a_argTypes, CommandGene[][] a_nodeSets, int[] a_minDepths, int[] a_maxDepths, int a_maxNodes) throws InvalidConfigurationException {
/*  65 */     super(a_conf);
/*  66 */     this.m_chromosomes = new ProgramChromosome[a_types.length];
/*  67 */     setTypes(a_types);
/*  68 */     setArgTypes(a_argTypes);
/*  69 */     setNodeSets(a_nodeSets);
/*  70 */     setMaxDepths(a_maxDepths);
/*  71 */     setMinDepths(a_minDepths);
/*  72 */     setMaxNodes(a_maxNodes);
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
/*     */   public GPProgram(IGPProgram a_prog) throws InvalidConfigurationException {
/*  86 */     super(a_prog);
/*  87 */     this.m_chromosomes = new ProgramChromosome[(getTypes()).length];
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
/*     */   public GPProgram(GPConfiguration a_conf, int a_numChromosomes) throws InvalidConfigurationException {
/* 102 */     super(a_conf);
/* 103 */     this.m_chromosomes = new ProgramChromosome[a_numChromosomes];
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ProgramChromosome getChromosome(int a_index) {
/* 114 */     IGPProgram ind = this.m_chromosomes[a_index].getIndividual();
/* 115 */     if (this != ind) {
/* 116 */       this.m_chromosomes[a_index].setIndividual((IGPProgram)this);
/*     */     }
/* 118 */     return this.m_chromosomes[a_index];
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
/*     */   public void setChromosome(int a_index, ProgramChromosome a_chrom) {
/* 131 */     this.m_chromosomes[a_index] = a_chrom;
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
/*     */   public void growOrFull(int a_depth, boolean a_grow, int a_maxNodes, boolean[] a_fullModeAllowed, int a_tries) {
/* 150 */     GPConfiguration conf = getGPConfiguration();
/* 151 */     int size = this.m_chromosomes.length;
/* 152 */     for (int i = 0; i < size; i++) {
/*     */ 
/*     */       
/*     */       try {
/* 156 */         this.m_chromosomes[i] = new ProgramChromosome(conf, a_maxNodes, (IGPProgram)this);
/* 157 */       } catch (InvalidConfigurationException iex) {
/* 158 */         throw new RuntimeException(iex);
/*     */       } 
/* 160 */       this.m_chromosomes[i].setArgTypes(getArgTypes()[i]);
/*     */ 
/*     */ 
/*     */       
/* 164 */       int len = (getNodeSets()[i]).length;
/* 165 */       for (int k = 0; k < len; k++) {
/* 166 */         if (getNodeSets()[i][k] instanceof ADF) {
/* 167 */           ((ADF)getNodeSets()[i][k]).setReturnType(getTypes()[((ADF)getNodeSets()[i][k]).getChromosomeNum()]);
/*     */         }
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 173 */     for (int j = 0; j < size; j++) {
/*     */       int depth;
/*     */       
/* 176 */       if (getMaxDepths() != null && a_depth > getMaxDepths()[j]) {
/* 177 */         depth = getMaxDepths()[j];
/*     */       
/*     */       }
/* 180 */       else if (getMinDepths() != null && a_depth < getMinDepths()[j]) {
/* 181 */         depth = getMinDepths()[j];
/*     */       } else {
/*     */         
/* 184 */         depth = a_depth;
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 190 */       if (a_grow || !a_fullModeAllowed[j]) {
/* 191 */         this.m_chromosomes[j].growOrFull(j, depth, getType(j), getArgType(j), getNodeSet(j), true, a_tries);
/*     */       }
/*     */       else {
/*     */         
/* 195 */         this.m_chromosomes[j].growOrFull(j, depth, getType(j), getArgType(j), getNodeSet(j), false, a_tries);
/*     */       } 
/*     */     } 
/*     */     
/* 199 */     if (getGPConfiguration().isUseProgramCache()) {
/*     */ 
/*     */       
/* 202 */       GPProgramInfo pcInfo = getGPConfiguration().readProgramCache(this);
/* 203 */       if (pcInfo == null) {
/*     */         
/* 205 */         pcInfo = getGPConfiguration().putToProgramCache(this);
/*     */       } else {
/*     */         
/* 208 */         setFitnessValue(pcInfo.getFitnessValue());
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
/*     */   public void growOrFull(Class[][] a_argTypes, CommandGene[][] a_nodeSets) {
/* 226 */     int size = this.m_chromosomes.length; int i;
/* 227 */     for (i = 0; i < size; i++) {
/* 228 */       this.m_chromosomes[i].setArgTypes(a_argTypes[i]);
/*     */ 
/*     */ 
/*     */       
/* 232 */       int len = (getNodeSets()[i]).length;
/* 233 */       for (int j = 0; j < len; j++) {
/* 234 */         if (getNodeSets()[i][j] instanceof ADF) {
/* 235 */           ((ADF)getNodeSets()[i][j]).setReturnType(getTypes()[((ADF)getNodeSets()[i][j]).getChromosomeNum()]);
/*     */         }
/*     */       } 
/*     */     } 
/*     */     
/* 240 */     for (i = 0; i < size; i++) {
/* 241 */       ProgramChromosome chrom = this.m_chromosomes[i];
/* 242 */       chrom.setFunctionSet(a_nodeSets[i]);
/* 243 */       CommandGene[] functionSet = chrom.getFunctionSet();
/* 244 */       CommandGene[] newFktSet = new CommandGene[functionSet.length + (a_argTypes[i]).length];
/* 245 */       System.arraycopy(functionSet, 0, newFktSet, 0, functionSet.length);
/*     */ 
/*     */       
/* 248 */       for (int ii = 0; ii < (a_argTypes[i]).length; ii++) {
/*     */         try {
/* 250 */           functionSet[(a_nodeSets[i]).length + ii] = (CommandGene)new Argument(getGPConfiguration(), ii, a_argTypes[i][ii]);
/*     */         }
/* 252 */         catch (InvalidConfigurationException iex) {
/* 253 */           throw new RuntimeException(iex);
/*     */         } 
/*     */       } 
/* 256 */       chrom.redepth();
/*     */     } 
/* 258 */     if (getGPConfiguration().isUseProgramCache()) {
/*     */ 
/*     */       
/* 261 */       GPProgramInfo pcInfo = getGPConfiguration().readProgramCache(this);
/* 262 */       if (pcInfo == null) {
/*     */         
/* 264 */         pcInfo = getGPConfiguration().putToProgramCache(this);
/*     */       } else {
/*     */         
/* 267 */         setFitnessValue(pcInfo.getFitnessValue());
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
/*     */   public int size() {
/* 279 */     return this.m_chromosomes.length;
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
/*     */   public String toString(int a_startNode) {
/* 292 */     if (a_startNode < 0) {
/* 293 */       return "";
/*     */     }
/* 295 */     StringBuffer sb = new StringBuffer();
/* 296 */     for (int i = 0; i < this.m_chromosomes.length; i++) {
/* 297 */       if (i > 0) {
/* 298 */         sb.append(" ==> ");
/*     */       }
/* 300 */       sb.append(this.m_chromosomes[i].toString(a_startNode));
/*     */     } 
/* 302 */     return sb.toString();
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
/*     */   public String toStringNorm(int a_startNode) {
/* 314 */     if (a_startNode < 0) {
/* 315 */       return "";
/*     */     }
/* 317 */     StringBuffer sb = new StringBuffer();
/* 318 */     for (int i = 0; i < this.m_chromosomes.length; i++) {
/* 319 */       if (i > 0) {
/* 320 */         sb.append(" ==> ");
/*     */       }
/* 322 */       this.m_chromosomes[i].setIndividual((IGPProgram)this);
/* 323 */       sb.append(this.m_chromosomes[i].toStringNorm(a_startNode));
/*     */     } 
/* 325 */     return sb.toString();
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
/*     */   public int execute_int(int a_chromosomeNum, Object[] a_args) {
/* 339 */     this.m_chromosomes[a_chromosomeNum].setIndividual((IGPProgram)this);
/* 340 */     return this.m_chromosomes[a_chromosomeNum].execute_int(a_args);
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
/*     */   public float execute_float(int a_chromosomeNum, Object[] a_args) {
/* 354 */     this.m_chromosomes[a_chromosomeNum].setIndividual((IGPProgram)this);
/* 355 */     return this.m_chromosomes[a_chromosomeNum].execute_float(a_args);
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
/*     */   public double execute_double(int a_chromosomeNum, Object[] a_args) {
/* 369 */     this.m_chromosomes[a_chromosomeNum].setIndividual((IGPProgram)this);
/* 370 */     return this.m_chromosomes[a_chromosomeNum].execute_double(a_args);
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
/*     */   public boolean execute_boolean(int a_chromosomeNum, Object[] a_args) {
/* 384 */     this.m_chromosomes[a_chromosomeNum].setIndividual((IGPProgram)this);
/* 385 */     return this.m_chromosomes[a_chromosomeNum].execute_boolean(a_args);
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
/*     */   public Object execute_object(int a_chromosomeNum, Object[] a_args) {
/* 399 */     this.m_chromosomes[a_chromosomeNum].setIndividual((IGPProgram)this);
/* 400 */     return this.m_chromosomes[a_chromosomeNum].execute_object(a_args);
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
/*     */   public void execute_void(int a_chromosomeNum, Object[] a_args) {
/* 413 */     this.m_chromosomes[a_chromosomeNum].setIndividual((IGPProgram)this);
/* 414 */     this.m_chromosomes[a_chromosomeNum].execute_void(a_args);
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
/*     */   public int getCommandOfClass(int a_chromosomeNum, Class a_class) {
/* 428 */     for (int i = a_chromosomeNum; i < this.m_chromosomes.length; i++) {
/* 429 */       int j = this.m_chromosomes[i].getCommandOfClass(0, a_class);
/* 430 */       if (j >= 0) {
/* 431 */         return j;
/*     */       }
/*     */     } 
/* 434 */     return -1;
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
/*     */   public int compareTo(Object a_other) {
/* 452 */     if (a_other == null) {
/* 453 */       return 1;
/*     */     }
/* 455 */     int size = size();
/* 456 */     GPProgram other = (GPProgram)a_other;
/* 457 */     ProgramChromosome[] otherChroms = other.m_chromosomes;
/*     */ 
/*     */ 
/*     */     
/* 461 */     if (other.size() != size) {
/* 462 */       return size() - other.size();
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 468 */     Arrays.sort((Object[])this.m_chromosomes);
/* 469 */     Arrays.sort((Object[])otherChroms);
/* 470 */     for (int i = 0; i < size; i++) {
/* 471 */       int comparison = this.m_chromosomes[i].compareTo(otherChroms[i]);
/* 472 */       if (comparison != 0) {
/* 473 */         return comparison;
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 478 */     return 0;
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
/*     */   public Object clone() {
/*     */     try {
/*     */       int[] minDepthsClone, maxDepthsClone;
/* 492 */       if (getMinDepths() != null) {
/* 493 */         minDepthsClone = (int[])getMinDepths().clone();
/*     */       } else {
/*     */         
/* 496 */         minDepthsClone = null;
/*     */       } 
/*     */       
/* 499 */       if (getMaxDepths() != null) {
/* 500 */         maxDepthsClone = (int[])getMaxDepths().clone();
/*     */       } else {
/*     */         
/* 503 */         maxDepthsClone = null;
/*     */       } 
/* 505 */       GPProgram result = new GPProgram(getGPConfiguration(), (Class[])getTypes().clone(), (Class[][])getArgTypes().clone(), (CommandGene[][])getNodeSets().clone(), minDepthsClone, maxDepthsClone, getMaxNodes());
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 512 */       result.setFitnessValue(getFitnessValueDirectly());
/*     */ 
/*     */       
/* 515 */       Object appData = getApplicationData();
/* 516 */       if (appData != null) {
/* 517 */         ICloneHandler cloner = getGPConfiguration().getJGAPFactory().getCloneHandlerFor(appData, null);
/*     */         
/* 519 */         if (cloner != null) {
/* 520 */           result.setApplicationData(cloner.perform(appData, null, null));
/*     */         } else {
/*     */           
/* 523 */           result.setApplicationData(appData);
/*     */         } 
/*     */       } 
/* 526 */       for (int i = 0; i < this.m_chromosomes.length && 
/* 527 */         this.m_chromosomes[i] != null; i++)
/*     */       {
/*     */         
/* 530 */         result.m_chromosomes[i] = (ProgramChromosome)this.m_chromosomes[i].clone();
/*     */       }
/* 532 */       return result;
/* 533 */     } catch (Exception ex) {
/* 534 */       throw new CloneException(ex);
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
/*     */   public String getPersistentRepresentation() {
/* 546 */     StringBuffer b = new StringBuffer();
/* 547 */     for (ProgramChromosome chrom : this.m_chromosomes) {
/* 548 */       b.append("<");
/* 549 */       b.append(encode(chrom.getClass().getName() + "#" + chrom.getPersistentRepresentation()));
/*     */ 
/*     */ 
/*     */       
/* 553 */       b.append(">");
/*     */     } 
/* 555 */     return b.toString();
/*     */   }
/*     */   
/*     */   protected String encode(String a_string) {
/* 559 */     return StringKit.encode(a_string);
/*     */   }
/*     */   
/*     */   protected String decode(String a_string) {
/* 563 */     return StringKit.decode(a_string);
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\gp\impl\GPProgram.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */