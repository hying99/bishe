/*     */ package org.jgap.gp.function;
/*     */ 
/*     */ import org.apache.commons.lang.builder.CompareToBuilder;
/*     */ import org.apache.commons.lang.builder.EqualsBuilder;
/*     */ import org.jgap.InvalidConfigurationException;
/*     */ import org.jgap.gp.CommandGene;
/*     */ import org.jgap.gp.IGPProgram;
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
/*     */ public class StoreTerminalIndexed
/*     */   extends CommandGene
/*     */ {
/*     */   private static final String CVS_REVISION = "$Revision: 1.1 $";
/*     */   private int m_index;
/*     */   private Class m_type;
/*     */   
/*     */   public StoreTerminalIndexed(GPConfiguration a_conf, int a_index, Class a_childType) throws InvalidConfigurationException {
/*  36 */     this(a_conf, a_index, a_childType, 0, 0);
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
/*     */   public StoreTerminalIndexed(GPConfiguration a_conf, int a_index, Class a_childType, int a_subReturnType, int a_subChildType) throws InvalidConfigurationException {
/*  58 */     super(a_conf, 1, CommandGene.VoidClass, a_subReturnType, new int[] { a_subChildType });
/*     */     
/*  60 */     this.m_type = a_childType;
/*  61 */     this.m_index = a_index;
/*     */   }
/*     */   
/*     */   public String toString() {
/*  65 */     return "store(" + this.m_index + ", &1)";
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getName() {
/*  75 */     return "Store Terminal(" + this.m_index + ")";
/*     */   }
/*     */   
/*     */   public void execute_void(ProgramChromosome c, int n, Object[] args) {
/*  79 */     check(c);
/*  80 */     Object value = null;
/*  81 */     if (this.m_type == CommandGene.IntegerClass) {
/*  82 */       value = new Integer(c.execute_int(n, 0, args));
/*     */     }
/*  84 */     else if (this.m_type == CommandGene.LongClass) {
/*  85 */       value = new Long(c.execute_long(n, 0, args));
/*     */     }
/*  87 */     else if (this.m_type == CommandGene.DoubleClass) {
/*  88 */       value = new Double(c.execute_double(n, 0, args));
/*     */     }
/*  90 */     else if (this.m_type == CommandGene.FloatClass) {
/*  91 */       value = new Float(c.execute_float(n, 0, args));
/*     */     } else {
/*     */       
/*  94 */       value = c.execute(n, 0, args);
/*     */     } 
/*     */ 
/*     */     
/*  98 */     getGPConfiguration().storeIndexedMemory(this.m_index, value);
/*     */   }
/*     */   
/*     */   public int execute_int(ProgramChromosome c, int n, Object[] args) {
/* 102 */     check(c);
/* 103 */     int value = c.execute_int(n, 0, args);
/*     */ 
/*     */     
/* 106 */     getGPConfiguration().storeIndexedMemory(this.m_index, new Integer(value));
/* 107 */     return value;
/*     */   }
/*     */   
/*     */   public long execute_long(ProgramChromosome c, int n, Object[] args) {
/* 111 */     check(c);
/* 112 */     long value = c.execute_long(n, 0, args);
/* 113 */     getGPConfiguration().storeIndexedMemory(this.m_index, new Long(value));
/* 114 */     return value;
/*     */   }
/*     */   
/*     */   public double execute_double(ProgramChromosome c, int n, Object[] args) {
/* 118 */     check(c);
/* 119 */     double value = c.execute_double(n, 0, args);
/* 120 */     getGPConfiguration().storeIndexedMemory(this.m_index, new Double(value));
/* 121 */     return value;
/*     */   }
/*     */   
/*     */   public float execute_float(ProgramChromosome c, int n, Object[] args) {
/* 125 */     check(c);
/* 126 */     float value = c.execute_float(n, 0, args);
/* 127 */     getGPConfiguration().storeIndexedMemory(this.m_index, new Float(value));
/* 128 */     return value;
/*     */   }
/*     */   
/*     */   public Object execute_object(ProgramChromosome c, int n, Object[] args) {
/* 132 */     check(c);
/* 133 */     Object value = c.execute_object(n, 0, args);
/* 134 */     getGPConfiguration().storeIndexedMemory(this.m_index, value);
/* 135 */     return value;
/*     */   }
/*     */   
/*     */   public boolean isAffectGlobalState() {
/* 139 */     return true;
/*     */   }
/*     */   
/*     */   public boolean isValid(ProgramChromosome a_program) {
/* 143 */     return (a_program.getIndividual().getCommandOfClass(0, ReadTerminal.class) > 0);
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
/*     */   public Class getChildType(IGPProgram a_ind, int a_chromNum) {
/* 158 */     return this.m_type;
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
/* 171 */     if (a_other == null) {
/* 172 */       return 1;
/*     */     }
/*     */     
/* 175 */     StoreTerminalIndexed other = (StoreTerminalIndexed)a_other;
/* 176 */     return (new CompareToBuilder()).append(this.m_index, other.m_index).append(this.m_type, other.m_type).toComparison();
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
/* 193 */     if (a_other == null) {
/* 194 */       return false;
/*     */     }
/*     */     
/*     */     try {
/* 198 */       StoreTerminalIndexed other = (StoreTerminalIndexed)a_other;
/* 199 */       return (new EqualsBuilder()).append(this.m_index, other.m_index).append(this.m_type, other.m_type).isEquals();
/*     */ 
/*     */     
/*     */     }
/* 203 */     catch (ClassCastException cex) {
/* 204 */       return false;
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\gp\function\StoreTerminalIndexed.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */