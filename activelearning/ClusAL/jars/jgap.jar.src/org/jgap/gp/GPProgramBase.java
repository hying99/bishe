/*     */ package org.jgap.gp;
/*     */ 
/*     */ import org.jgap.InvalidConfigurationException;
/*     */ import org.jgap.gp.impl.GPConfiguration;
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
/*     */ public abstract class GPProgramBase
/*     */   implements IGPProgram
/*     */ {
/*     */   private static final String CVS_REVISION = "$Revision: 1.11 $";
/*  27 */   private double m_fitnessValue = -1.0D;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private GPConfiguration m_conf;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Class[] m_types;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Class[][] m_argTypes;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private CommandGene[][] m_nodeSets;
/*     */ 
/*     */ 
/*     */   
/*     */   private int[] m_minDepths;
/*     */ 
/*     */ 
/*     */   
/*     */   private int[] m_maxDepths;
/*     */ 
/*     */ 
/*     */   
/*     */   private int m_maxNodes;
/*     */ 
/*     */ 
/*     */   
/*     */   private Object m_applicationData;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public GPProgramBase(GPConfiguration a_conf) throws InvalidConfigurationException {
/*  69 */     if (a_conf == null) {
/*  70 */       throw new InvalidConfigurationException("Configuration must not be null!");
/*     */     }
/*  72 */     this.m_conf = a_conf;
/*     */   }
/*     */   
/*     */   public GPProgramBase(IGPProgram a_prog) throws InvalidConfigurationException {
/*  76 */     this(a_prog.getGPConfiguration());
/*  77 */     this.m_types = a_prog.getTypes();
/*  78 */     this.m_argTypes = a_prog.getArgTypes();
/*  79 */     this.m_nodeSets = a_prog.getNodeSets();
/*  80 */     this.m_maxDepths = a_prog.getMaxDepths();
/*  81 */     this.m_minDepths = a_prog.getMinDepths();
/*  82 */     this.m_maxNodes = a_prog.getMaxNodes();
/*     */   }
/*     */   
/*     */   public GPConfiguration getGPConfiguration() {
/*  86 */     return this.m_conf;
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
/* 100 */       return (compareTo((T)a_other) == 0);
/* 101 */     } catch (ClassCastException cex) {
/* 102 */       return false;
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
/*     */   public double calcFitnessValue() {
/* 114 */     GPFitnessFunction normalFitnessFunction = getGPConfiguration().getGPFitnessFunction();
/*     */     
/* 116 */     if (normalFitnessFunction != null)
/*     */     {
/*     */ 
/*     */       
/* 120 */       this.m_fitnessValue = normalFitnessFunction.getFitnessValue(this);
/*     */     }
/* 122 */     if (Double.isInfinite(this.m_fitnessValue)) {
/* 123 */       return -1.0D;
/*     */     }
/*     */     
/* 126 */     return this.m_fitnessValue;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getFitnessValue() {
/* 137 */     if (this.m_fitnessValue >= 0.0D) {
/* 138 */       return this.m_fitnessValue;
/*     */     }
/*     */     
/* 141 */     return calcFitnessValue();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getFitnessValueDirectly() {
/* 152 */     return this.m_fitnessValue;
/*     */   }
/*     */   
/*     */   public void setFitnessValue(double a_fitness) {
/* 156 */     this.m_fitnessValue = a_fitness;
/*     */   }
/*     */   
/*     */   public void setTypes(Class[] a_types) {
/* 160 */     this.m_types = a_types;
/*     */   }
/*     */   
/*     */   public Class[] getTypes() {
/* 164 */     return this.m_types;
/*     */   }
/*     */   
/*     */   public Class getType(int a_index) {
/* 168 */     return this.m_types[a_index];
/*     */   }
/*     */   
/*     */   public void setArgTypes(Class[][] a_argTypes) {
/* 172 */     this.m_argTypes = a_argTypes;
/*     */   }
/*     */   
/*     */   public Class[][] getArgTypes() {
/* 176 */     return this.m_argTypes;
/*     */   }
/*     */   
/*     */   public Class[] getArgType(int a_index) {
/* 180 */     return this.m_argTypes[a_index];
/*     */   }
/*     */   
/*     */   public void setNodeSets(CommandGene[][] a_nodeSets) {
/* 184 */     this.m_nodeSets = a_nodeSets;
/*     */   }
/*     */   
/*     */   public CommandGene[][] getNodeSets() {
/* 188 */     return this.m_nodeSets;
/*     */   }
/*     */   
/*     */   public CommandGene[] getNodeSet(int a_index) {
/* 192 */     return this.m_nodeSets[a_index];
/*     */   }
/*     */   
/*     */   public void setMaxDepths(int[] a_maxDepths) {
/* 196 */     this.m_maxDepths = a_maxDepths;
/*     */   }
/*     */   
/*     */   public int[] getMaxDepths() {
/* 200 */     return this.m_maxDepths;
/*     */   }
/*     */   
/*     */   public void setMinDepths(int[] a_minDepths) {
/* 204 */     this.m_minDepths = a_minDepths;
/*     */   }
/*     */   
/*     */   public int[] getMinDepths() {
/* 208 */     return this.m_minDepths;
/*     */   }
/*     */   
/*     */   public void setMaxNodes(int a_maxNodes) {
/* 212 */     this.m_maxNodes = a_maxNodes;
/*     */   }
/*     */   
/*     */   public int getMaxNodes() {
/* 216 */     return this.m_maxNodes;
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
/*     */   public void setApplicationData(Object a_data) {
/* 228 */     this.m_applicationData = a_data;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object getApplicationData() {
/* 238 */     return this.m_applicationData;
/*     */   }
/*     */   
/*     */   public abstract Object clone();
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\gp\GPProgramBase.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */