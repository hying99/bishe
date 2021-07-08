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
/*     */ public class ReadTerminalIndexed
/*     */   extends CommandGene
/*     */ {
/*     */   private static final String CVS_REVISION = "$Revision: 1.1 $";
/*     */   private int m_index;
/*     */   
/*     */   public ReadTerminalIndexed(GPConfiguration a_conf, Class a_type, int a_index) throws InvalidConfigurationException {
/*  36 */     this(a_conf, a_type, a_index, 0);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public ReadTerminalIndexed(GPConfiguration a_conf, Class a_type, int a_index, int a_subReturnType) throws InvalidConfigurationException {
/*  42 */     super(a_conf, 0, a_type, a_subReturnType, null);
/*  43 */     if (a_index < 0 || a_index > getGPConfiguration().getMemorySize()) {
/*  44 */       throw new IllegalArgumentException("Memory index invalid!");
/*     */     }
/*  46 */     this.m_index = a_index;
/*     */   }
/*     */   
/*     */   public String toString() {
/*  50 */     return "read_from_index(" + this.m_index + ")";
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getName() {
/*  60 */     return "Read Terminal Indexed";
/*     */   }
/*     */   
/*     */   public int execute_int(ProgramChromosome c, int n, Object[] args) {
/*  64 */     check(c);
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/*  69 */       return ((Integer)getGPConfiguration().readIndexedMemory(this.m_index)).intValue();
/*     */     }
/*  71 */     catch (NullPointerException nex) {
/*  72 */       throw new IllegalArgumentException();
/*     */     }
/*  74 */     catch (IllegalArgumentException iex) {
/*  75 */       throw new IllegalStateException("ReadTerminalIndexed without preceeding StoreTerminalIndexed");
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public long execute_long(ProgramChromosome c, int n, Object[] args) {
/*  81 */     check(c);
/*     */     
/*     */     try {
/*  84 */       return ((Long)getGPConfiguration().readIndexedMemory(this.m_index)).longValue();
/*     */     }
/*  86 */     catch (NullPointerException nex) {
/*  87 */       throw new IllegalArgumentException();
/*     */     }
/*  89 */     catch (IllegalArgumentException iex) {
/*  90 */       throw new IllegalStateException("ReadTerminalIndexed without preceeding StoreTerminalIndexed");
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public double execute_double(ProgramChromosome c, int n, Object[] args) {
/*  96 */     check(c);
/*     */     
/*     */     try {
/*  99 */       return ((Double)getGPConfiguration().readIndexedMemory(this.m_index)).doubleValue();
/*     */     }
/* 101 */     catch (NullPointerException nex) {
/* 102 */       throw new IllegalArgumentException();
/*     */     }
/* 104 */     catch (IllegalArgumentException iex) {
/* 105 */       throw new IllegalStateException("ReadTerminalIndexed without preceeding StoreTerminalIndexed");
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public float execute_float(ProgramChromosome c, int n, Object[] args) {
/* 111 */     check(c);
/*     */     
/*     */     try {
/* 114 */       return ((Float)getGPConfiguration().readIndexedMemory(this.m_index)).floatValue();
/*     */     }
/* 116 */     catch (NullPointerException nex) {
/* 117 */       throw new IllegalArgumentException();
/*     */     }
/* 119 */     catch (IllegalArgumentException iex) {
/* 120 */       throw new IllegalStateException("ReadTerminalIndexed without preceeding StoreTerminalIndexed");
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public Object execute_object(ProgramChromosome c, int n, Object[] args) {
/* 126 */     check(c);
/*     */     
/*     */     try {
/* 129 */       return getGPConfiguration().readIndexedMemory(this.m_index);
/* 130 */     } catch (NullPointerException nex) {
/* 131 */       throw new IllegalArgumentException();
/*     */     }
/* 133 */     catch (IllegalArgumentException iex) {
/* 134 */       throw new IllegalStateException("ReadTerminalIndexed without preceeding StoreTerminalIndexed");
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
/*     */   public int compareTo(Object a_other) {
/* 149 */     if (a_other == null) {
/* 150 */       return 1;
/*     */     }
/*     */     
/* 153 */     ReadTerminalIndexed other = (ReadTerminalIndexed)a_other;
/* 154 */     return (new CompareToBuilder()).append(this.m_index, other.m_index).toComparison();
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
/* 170 */     if (a_other == null) {
/* 171 */       return false;
/*     */     }
/*     */     
/*     */     try {
/* 175 */       ReadTerminalIndexed other = (ReadTerminalIndexed)a_other;
/* 176 */       return (new EqualsBuilder()).append(this.m_index, other.m_index).isEquals();
/*     */     
/*     */     }
/* 179 */     catch (ClassCastException cex) {
/* 180 */       return false;
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\gp\function\ReadTerminalIndexed.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */