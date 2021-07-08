/*     */ package org.jgap.gp.function;
/*     */ 
/*     */ import org.apache.commons.lang.builder.CompareToBuilder;
/*     */ import org.apache.commons.lang.builder.EqualsBuilder;
/*     */ import org.jgap.InvalidConfigurationException;
/*     */ import org.jgap.gp.CommandGene;
/*     */ import org.jgap.gp.impl.GPConfiguration;
/*     */ import org.jgap.gp.impl.ProgramChromosome;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ReadTerminal
/*     */   extends CommandGene
/*     */ {
/*     */   private static final String CVS_REVISION = "$Revision: 1.11 $";
/*     */   private String m_storageName;
/*     */   
/*     */   public ReadTerminal(GPConfiguration a_conf, Class a_type, String a_storageName) throws InvalidConfigurationException {
/*  37 */     this(a_conf, a_type, a_storageName, 0);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public ReadTerminal(GPConfiguration a_conf, Class a_type, String a_storageName, int a_subReturnType) throws InvalidConfigurationException {
/*  43 */     super(a_conf, 0, a_type, a_subReturnType, null);
/*  44 */     if (a_storageName == null || a_storageName.length() < 1) {
/*  45 */       throw new IllegalArgumentException("Memory name must not be empty!");
/*     */     }
/*  47 */     this.m_storageName = a_storageName;
/*     */   }
/*     */   
/*     */   public String toString() {
/*  51 */     return "read_from(" + this.m_storageName + ")";
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getName() {
/*  61 */     return "Read Terminal";
/*     */   }
/*     */   
/*     */   public int execute_int(ProgramChromosome c, int n, Object[] args) {
/*  65 */     check(c);
/*     */ 
/*     */     
/*     */     try {
/*  69 */       return ((Integer)getGPConfiguration().readFromMemory(this.m_storageName)).intValue();
/*     */     }
/*  71 */     catch (IllegalArgumentException iex) {
/*  72 */       throw new IllegalStateException("ReadTerminal without preceeding StoreTerminal");
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public long execute_long(ProgramChromosome c, int n, Object[] args) {
/*  78 */     check(c);
/*     */     try {
/*  80 */       return ((Long)getGPConfiguration().readFromMemory(this.m_storageName)).longValue();
/*     */     }
/*  82 */     catch (IllegalArgumentException iex) {
/*  83 */       throw new IllegalStateException("ReadTerminal without preceeding StoreTerminal");
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public double execute_double(ProgramChromosome c, int n, Object[] args) {
/*  89 */     check(c);
/*     */     try {
/*  91 */       return ((Double)getGPConfiguration().readFromMemory(this.m_storageName)).doubleValue();
/*     */     }
/*  93 */     catch (IllegalArgumentException iex) {
/*  94 */       throw new IllegalStateException("ReadTerminal without preceeding StoreTerminal");
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public float execute_float(ProgramChromosome c, int n, Object[] args) {
/* 100 */     check(c);
/*     */     try {
/* 102 */       return ((Float)getGPConfiguration().readFromMemory(this.m_storageName)).floatValue();
/*     */     }
/* 104 */     catch (IllegalArgumentException iex) {
/* 105 */       throw new IllegalStateException("ReadTerminal without preceeding StoreTerminal");
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public Object execute_object(ProgramChromosome c, int n, Object[] args) {
/* 111 */     check(c);
/*     */     try {
/* 113 */       return getGPConfiguration().readFromMemory(this.m_storageName);
/* 114 */     } catch (IllegalArgumentException iex) {
/* 115 */       throw new IllegalStateException("ReadTerminal without preceeding StoreTerminal");
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isValid(ProgramChromosome a_program) {
/* 121 */     return (a_program.getIndividual().getCommandOfClass(0, StoreTerminal.class) >= 0);
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
/*     */   public int compareTo(Object a_other) {
/* 135 */     if (a_other == null) {
/* 136 */       return 1;
/*     */     }
/*     */     
/* 139 */     ReadTerminal other = (ReadTerminal)a_other;
/* 140 */     return (new CompareToBuilder()).append(this.m_storageName, other.m_storageName).toComparison();
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
/*     */   public boolean equals(Object a_other) {
/* 156 */     if (a_other == null) {
/* 157 */       return false;
/*     */     }
/*     */     
/*     */     try {
/* 161 */       ReadTerminal other = (ReadTerminal)a_other;
/* 162 */       return (new EqualsBuilder()).append(this.m_storageName, other.m_storageName).isEquals();
/*     */     
/*     */     }
/* 165 */     catch (ClassCastException cex) {
/* 166 */       return false;
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\gp\function\ReadTerminal.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */