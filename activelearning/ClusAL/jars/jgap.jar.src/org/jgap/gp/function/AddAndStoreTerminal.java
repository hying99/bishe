/*     */ package org.jgap.gp.function;
/*     */ 
/*     */ import org.apache.commons.lang.builder.CompareToBuilder;
/*     */ import org.apache.commons.lang.builder.EqualsBuilder;
/*     */ import org.jgap.InvalidConfigurationException;
/*     */ import org.jgap.gp.CommandGene;
/*     */ import org.jgap.gp.IGPProgram;
/*     */ import org.jgap.gp.impl.GPConfiguration;
/*     */ import org.jgap.gp.impl.ProgramChromosome;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class AddAndStoreTerminal
/*     */   extends CommandGene
/*     */   implements ICloneable
/*     */ {
/*     */   private static final String CVS_REVISION = "$Revision: 1.2 $";
/*     */   private String m_storageName;
/*     */   private Class m_type;
/*     */   
/*     */   public AddAndStoreTerminal(GPConfiguration a_conf, String a_storageName, Class a_childType) throws InvalidConfigurationException {
/*  53 */     this(a_conf, a_storageName, a_childType, 0, 0);
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
/*     */   public AddAndStoreTerminal(GPConfiguration a_conf, String a_storageName, Class a_childType, int a_subReturnType, int a_subChildType) throws InvalidConfigurationException {
/*  74 */     super(a_conf, 1, CommandGene.VoidClass, a_subReturnType, new int[] { a_subChildType });
/*     */     
/*  76 */     this.m_type = a_childType;
/*  77 */     if (a_storageName == null || a_storageName.length() < 1) {
/*  78 */       throw new IllegalArgumentException("Memory name must not be empty!");
/*     */     }
/*  80 */     this.m_storageName = a_storageName;
/*     */   }
/*     */   
/*     */   public String toString() {
/*  84 */     return "addstore(" + this.m_storageName + ", &1)";
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getName() {
/*  94 */     return "Add+Store Terminal";
/*     */   }
/*     */   
/*     */   public void execute_void(ProgramChromosome c, int n, Object[] args) {
/*  98 */     check(c);
/*  99 */     if (this.m_type == CommandGene.IntegerClass) {
/* 100 */       int value = c.execute_int(n, 0, args);
/* 101 */       Integer oldValue = (Integer)getGPConfiguration().readFromMemoryIfExists(this.m_storageName);
/*     */       
/* 103 */       if (oldValue != null) {
/* 104 */         value += oldValue.intValue();
/*     */       }
/*     */ 
/*     */       
/* 108 */       getGPConfiguration().storeInMemory(this.m_storageName, new Integer(value));
/*     */     }
/* 110 */     else if (this.m_type == CommandGene.LongClass) {
/* 111 */       long value = c.execute_long(n, 0, args);
/* 112 */       Long oldValue = (Long)getGPConfiguration().readFromMemoryIfExists(this.m_storageName);
/*     */       
/* 114 */       if (oldValue != null) {
/* 115 */         value += oldValue.longValue();
/*     */       }
/* 117 */       getGPConfiguration().storeInMemory(this.m_storageName, new Long(value));
/*     */     }
/* 119 */     else if (this.m_type == CommandGene.DoubleClass) {
/* 120 */       double value = c.execute_double(n, 0, args);
/* 121 */       Double oldValue = (Double)getGPConfiguration().readFromMemoryIfExists(this.m_storageName);
/*     */       
/* 123 */       if (oldValue != null) {
/* 124 */         value += oldValue.doubleValue();
/*     */       }
/* 126 */       getGPConfiguration().storeInMemory(this.m_storageName, new Double(value));
/*     */     }
/* 128 */     else if (this.m_type == CommandGene.FloatClass) {
/* 129 */       float value = c.execute_float(n, 0, args);
/* 130 */       Float oldValue = (Float)getGPConfiguration().readFromMemoryIfExists(this.m_storageName);
/*     */       
/* 132 */       if (oldValue != null) {
/* 133 */         value += oldValue.floatValue();
/*     */       }
/* 135 */       getGPConfiguration().storeInMemory(this.m_storageName, new Float(value));
/*     */     } else {
/*     */       
/* 138 */       throw new IllegalStateException("Type " + this.m_type + " unknown");
/*     */     } 
/*     */   }
/*     */   
/*     */   public boolean isAffectGlobalState() {
/* 143 */     return true;
/*     */   }
/*     */   
/*     */   public boolean isValid(ProgramChromosome a_program) {
/* 147 */     return (a_program.getIndividual().getCommandOfClass(0, ReadTerminal.class) > 0);
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
/* 162 */     return this.m_type;
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
/* 175 */     if (a_other == null) {
/* 176 */       return 1;
/*     */     }
/*     */     
/* 179 */     AddAndStoreTerminal other = (AddAndStoreTerminal)a_other;
/* 180 */     return (new CompareToBuilder()).append(this.m_storageName, other.m_storageName).append(this.m_type, other.m_type).toComparison();
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
/* 197 */     if (a_other == null) {
/* 198 */       return false;
/*     */     }
/*     */     
/*     */     try {
/* 202 */       AddAndStoreTerminal other = (AddAndStoreTerminal)a_other;
/* 203 */       return (new EqualsBuilder()).append(this.m_storageName, other.m_storageName).append(this.m_type, other.m_type).isEquals();
/*     */ 
/*     */     
/*     */     }
/* 207 */     catch (ClassCastException cex) {
/* 208 */       return false;
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
/*     */   public Object clone() {
/*     */     try {
/* 221 */       AddAndStoreTerminal result = new AddAndStoreTerminal(getGPConfiguration(), this.m_storageName, this.m_type, getSubReturnType(), getSubChildType(0));
/*     */ 
/*     */       
/* 224 */       return result;
/* 225 */     } catch (Throwable t) {
/* 226 */       throw new CloneException(t);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\gp\function\AddAndStoreTerminal.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */