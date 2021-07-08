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
/*     */ public class SubProgram
/*     */   extends CommandGene
/*     */   implements ICloneable
/*     */ {
/*     */   private static final String CVS_REVISION = "$Revision: 1.12 $";
/*     */   private int m_subtrees;
/*     */   private Class[] m_types;
/*     */   
/*     */   public SubProgram(GPConfiguration a_conf, Class[] a_types) throws InvalidConfigurationException {
/*  44 */     this(a_conf, a_types, 0, (int[])null);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public SubProgram(GPConfiguration a_conf, Class[] a_types, int a_subReturnType, int[] a_subChildTypes) throws InvalidConfigurationException {
/*  50 */     super(a_conf, a_types.length, a_types[a_types.length - 1], a_subReturnType, a_subChildTypes);
/*     */     
/*  52 */     if (a_types.length < 1) {
/*  53 */       throw new IllegalArgumentException("Number of subtrees must be >= 1");
/*     */     }
/*  55 */     this.m_types = a_types;
/*  56 */     this.m_subtrees = a_types.length;
/*     */   }
/*     */   
/*     */   public String toString() {
/*  60 */     String ret = "sub[";
/*  61 */     for (int i = 1; i < this.m_subtrees; i++) {
/*  62 */       ret = ret + "&" + i + " --> ";
/*     */     }
/*  64 */     ret = ret + "&" + this.m_subtrees + "]";
/*  65 */     return ret;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getName() {
/*  75 */     return "Sub program";
/*     */   }
/*     */   
/*     */   public int execute_int(ProgramChromosome c, int n, Object[] args) {
/*  79 */     check(c);
/*  80 */     int value = -1;
/*  81 */     for (int i = 0; i < this.m_subtrees; i++) {
/*  82 */       if (i < this.m_subtrees - 1) {
/*  83 */         c.execute_void(n, i, args);
/*     */       } else {
/*     */         
/*  86 */         value = c.execute_int(n, i, args);
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  93 */     return value;
/*     */   }
/*     */   
/*     */   public void execute_void(ProgramChromosome c, int n, Object[] args) {
/*  97 */     check(c);
/*  98 */     for (int i = 0; i < this.m_subtrees; i++) {
/*  99 */       c.execute_void(n, i, args);
/*     */     }
/*     */   }
/*     */   
/*     */   public long execute_long(ProgramChromosome c, int n, Object[] args) {
/* 104 */     check(c);
/* 105 */     long value = -1L;
/* 106 */     for (int i = 0; i < this.m_subtrees; i++) {
/* 107 */       value = c.execute_long(n, i, args);
/*     */     }
/* 109 */     return value;
/*     */   }
/*     */   
/*     */   public float execute_float(ProgramChromosome c, int n, Object[] args) {
/* 113 */     check(c);
/* 114 */     float value = -1.0F;
/* 115 */     for (int i = 0; i < this.m_subtrees; i++) {
/* 116 */       value = c.execute_float(n, i, args);
/*     */     }
/* 118 */     return value;
/*     */   }
/*     */   
/*     */   public double execute_double(ProgramChromosome c, int n, Object[] args) {
/* 122 */     check(c);
/* 123 */     double value = -1.0D;
/* 124 */     for (int i = 0; i < this.m_subtrees; i++) {
/* 125 */       value = c.execute_double(n, i, args);
/*     */     }
/* 127 */     return value;
/*     */   }
/*     */   
/*     */   public Object execute_object(ProgramChromosome c, int n, Object[] args) {
/* 131 */     check(c);
/* 132 */     Object value = null;
/* 133 */     for (int i = 0; i < this.m_subtrees; i++) {
/* 134 */       value = c.execute_object(n, i, args);
/*     */     }
/* 136 */     return value;
/*     */   }
/*     */   
/*     */   public boolean isValid(ProgramChromosome a_program) {
/* 140 */     return true;
/*     */   }
/*     */   
/*     */   public Class getChildType(IGPProgram a_ind, int a_chromNum) {
/* 144 */     return this.m_types[a_chromNum];
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
/* 157 */     if (a_other == null) {
/* 158 */       return 1;
/*     */     }
/*     */     
/* 161 */     SubProgram other = (SubProgram)a_other;
/* 162 */     return (new CompareToBuilder()).append((Object[])this.m_types, (Object[])other.m_types).toComparison();
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
/* 178 */     if (a_other == null) {
/* 179 */       return false;
/*     */     }
/*     */     
/*     */     try {
/* 183 */       SubProgram other = (SubProgram)a_other;
/* 184 */       return (new EqualsBuilder()).append((Object[])this.m_types, (Object[])other.m_types).isEquals();
/*     */     
/*     */     }
/* 187 */     catch (ClassCastException cex) {
/* 188 */       return false;
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
/* 201 */       int[] subChildTypes = getSubChildTypes();
/* 202 */       if (subChildTypes != null) {
/* 203 */         subChildTypes = (int[])subChildTypes.clone();
/*     */       }
/* 205 */       SubProgram result = new SubProgram(getGPConfiguration(), this.m_types, getSubReturnType(), subChildTypes);
/*     */       
/* 207 */       result.m_subtrees = this.m_subtrees;
/* 208 */       result.m_types = (Class[])this.m_types.clone();
/* 209 */       return result;
/* 210 */     } catch (Throwable t) {
/* 211 */       throw new CloneException(t);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\gp\function\SubProgram.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */