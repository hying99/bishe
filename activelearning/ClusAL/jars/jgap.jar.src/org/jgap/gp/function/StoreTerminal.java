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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class StoreTerminal
/*     */   extends CommandGene
/*     */ {
/*     */   private static final String CVS_REVISION = "$Revision: 1.12 $";
/*     */   private String m_storageName;
/*     */   private Class m_type;
/*     */   
/*     */   public StoreTerminal(GPConfiguration a_conf, String a_storageName, Class a_childType) throws InvalidConfigurationException {
/*  40 */     this(a_conf, a_storageName, a_childType, 0, 0);
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
/*     */   public StoreTerminal(GPConfiguration a_conf, String a_storageName, Class a_childType, int a_subReturnType, int a_subChildType) throws InvalidConfigurationException {
/*  60 */     super(a_conf, 1, CommandGene.VoidClass, a_subReturnType, new int[] { a_subChildType });
/*     */     
/*  62 */     this.m_type = a_childType;
/*  63 */     if (a_storageName == null || a_storageName.length() < 1) {
/*  64 */       throw new IllegalArgumentException("Memory name must not be empty!");
/*     */     }
/*  66 */     this.m_storageName = a_storageName;
/*     */   }
/*     */   
/*     */   public String toString() {
/*  70 */     return "store(" + this.m_storageName + ", &1)";
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getName() {
/*  80 */     return "Store Terminal(" + this.m_storageName + ")";
/*     */   }
/*     */   
/*     */   public void execute_void(ProgramChromosome c, int n, Object[] args) {
/*  84 */     check(c);
/*  85 */     Object value = null;
/*  86 */     if (this.m_type == CommandGene.IntegerClass) {
/*  87 */       value = new Integer(c.execute_int(n, 0, args));
/*     */     }
/*  89 */     else if (this.m_type == CommandGene.LongClass) {
/*  90 */       value = new Long(c.execute_long(n, 0, args));
/*     */     }
/*  92 */     else if (this.m_type == CommandGene.DoubleClass) {
/*  93 */       value = new Double(c.execute_double(n, 0, args));
/*     */     }
/*  95 */     else if (this.m_type == CommandGene.FloatClass) {
/*  96 */       value = new Float(c.execute_float(n, 0, args));
/*     */     } else {
/*     */       
/*  99 */       value = c.execute(n, 0, args);
/*     */     } 
/*     */ 
/*     */     
/* 103 */     getGPConfiguration().storeInMemory(this.m_storageName, value);
/*     */   }
/*     */   
/*     */   public int execute_int(ProgramChromosome c, int n, Object[] args) {
/* 107 */     check(c);
/* 108 */     int value = c.execute_int(n, 0, args);
/*     */ 
/*     */     
/* 111 */     getGPConfiguration().storeInMemory(this.m_storageName, new Integer(value));
/* 112 */     return value;
/*     */   }
/*     */   
/*     */   public long execute_long(ProgramChromosome c, int n, Object[] args) {
/* 116 */     check(c);
/* 117 */     long value = c.execute_long(n, 0, args);
/* 118 */     getGPConfiguration().storeInMemory(this.m_storageName, new Long(value));
/* 119 */     return value;
/*     */   }
/*     */   
/*     */   public double execute_double(ProgramChromosome c, int n, Object[] args) {
/* 123 */     check(c);
/* 124 */     double value = c.execute_double(n, 0, args);
/* 125 */     getGPConfiguration().storeInMemory(this.m_storageName, new Double(value));
/* 126 */     return value;
/*     */   }
/*     */   
/*     */   public float execute_float(ProgramChromosome c, int n, Object[] args) {
/* 130 */     check(c);
/* 131 */     float value = c.execute_float(n, 0, args);
/* 132 */     getGPConfiguration().storeInMemory(this.m_storageName, new Float(value));
/* 133 */     return value;
/*     */   }
/*     */   
/*     */   public Object execute_object(ProgramChromosome c, int n, Object[] args) {
/* 137 */     check(c);
/* 138 */     Object value = c.execute_object(n, 0, args);
/* 139 */     getGPConfiguration().storeInMemory(this.m_storageName, value);
/* 140 */     return value;
/*     */   }
/*     */   
/*     */   public boolean isAffectGlobalState() {
/* 144 */     return true;
/*     */   }
/*     */   
/*     */   public boolean isValid(ProgramChromosome a_program) {
/* 148 */     return (a_program.getIndividual().getCommandOfClass(0, ReadTerminal.class) > 0);
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
/* 163 */     return this.m_type;
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
/* 176 */     if (a_other == null) {
/* 177 */       return 1;
/*     */     }
/*     */     
/* 180 */     StoreTerminal other = (StoreTerminal)a_other;
/* 181 */     return (new CompareToBuilder()).append(this.m_storageName, other.m_storageName).append(this.m_type, other.m_type).toComparison();
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
/* 198 */     if (a_other == null) {
/* 199 */       return false;
/*     */     }
/*     */     
/*     */     try {
/* 203 */       StoreTerminal other = (StoreTerminal)a_other;
/* 204 */       return (new EqualsBuilder()).append(this.m_storageName, other.m_storageName).append(this.m_type, other.m_type).isEquals();
/*     */ 
/*     */     
/*     */     }
/* 208 */     catch (ClassCastException cex) {
/* 209 */       return false;
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\gp\function\StoreTerminal.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */