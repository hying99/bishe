/*     */ package org.jgap.gp.function;
/*     */ 
/*     */ import org.apache.commons.lang.builder.CompareToBuilder;
/*     */ import org.apache.commons.lang.builder.EqualsBuilder;
/*     */ import org.jgap.InvalidConfigurationException;
/*     */ import org.jgap.gp.MathCommand;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class IncrementMemory
/*     */   extends MathCommand
/*     */ {
/*     */   private static final String CVS_REVISION = "$Revision: 1.2 $";
/*     */   private int m_increment;
/*     */   private String m_memoryName;
/*     */   private int m_initialValue;
/*     */   
/*     */   public IncrementMemory(GPConfiguration a_conf, Class a_type, String a_memoryName, int a_initialValue) throws InvalidConfigurationException {
/*  49 */     this(a_conf, a_type, a_memoryName, a_initialValue, 1);
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
/*     */   public IncrementMemory(GPConfiguration a_conf, Class a_type, String a_memoryName, int a_initialValue, int a_increment) throws InvalidConfigurationException {
/*  67 */     this(a_conf, a_type, a_memoryName, a_initialValue, a_increment, 0, 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public IncrementMemory(GPConfiguration a_conf, Class a_type, String a_memoryName, int a_initialValue, int a_increment, int a_subReturnType, int a_subChildType) throws InvalidConfigurationException {
/*  74 */     super(a_conf, 0, a_type, a_subReturnType, a_subChildType);
/*  75 */     this.m_increment = a_increment;
/*  76 */     this.m_memoryName = a_memoryName;
/*  77 */     this.m_initialValue = a_initialValue;
/*     */   }
/*     */   
/*     */   public String toString() {
/*  81 */     if (this.m_increment == 1) {
/*  82 */       return "INCMEM(" + this.m_memoryName + ")";
/*     */     }
/*     */     
/*  85 */     return "INCMEM(" + this.m_memoryName + ", " + this.m_increment + ")";
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getName() {
/*  96 */     return "INCMEM(" + this.m_memoryName + ")";
/*     */   }
/*     */   public int execute_int(ProgramChromosome c, int n, Object[] args) {
/*     */     int valueI;
/* 100 */     Integer value = (Integer)getGPConfiguration().readFromMemoryIfExists(this.m_memoryName);
/*     */ 
/*     */     
/* 103 */     if (value == null) {
/* 104 */       valueI = this.m_initialValue;
/*     */     } else {
/*     */       
/* 107 */       valueI = value.intValue() + 1;
/*     */     } 
/* 109 */     getGPConfiguration().storeInMemory(this.m_memoryName, new Integer(valueI));
/* 110 */     return valueI;
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
/*     */   public int compareTo(Object a_other) {
/* 123 */     if (a_other == null) {
/* 124 */       return 1;
/*     */     }
/*     */     
/* 127 */     IncrementMemory other = (IncrementMemory)a_other;
/* 128 */     return (new CompareToBuilder()).append(this.m_increment, other.m_increment).append(this.m_memoryName, other.m_memoryName).toComparison();
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
/*     */   public boolean equals(Object a_other) {
/* 145 */     if (a_other == null) {
/* 146 */       return false;
/*     */     }
/*     */     
/*     */     try {
/* 150 */       IncrementMemory other = (IncrementMemory)a_other;
/* 151 */       return (new EqualsBuilder()).append(this.m_increment, other.m_increment).append(this.m_memoryName, other.m_memoryName).isEquals();
/*     */ 
/*     */     
/*     */     }
/* 155 */     catch (ClassCastException cex) {
/* 156 */       return false;
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\gp\function\IncrementMemory.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */