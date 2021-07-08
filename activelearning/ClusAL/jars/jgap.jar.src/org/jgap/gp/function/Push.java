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
/*     */ public class Push
/*     */   extends CommandGene
/*     */ {
/*     */   private static final String CVS_REVISION = "$Revision: 1.9 $";
/*     */   private Class m_type;
/*     */   
/*     */   public Push(GPConfiguration a_conf, Class a_type) throws InvalidConfigurationException {
/*  33 */     this(a_conf, a_type, 0, 0);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Push(GPConfiguration a_conf, Class a_type, int a_subReturnType, int a_subChildType) throws InvalidConfigurationException {
/*  39 */     super(a_conf, 1, CommandGene.VoidClass, a_subReturnType, a_subChildType);
/*  40 */     this.m_type = a_type;
/*     */   }
/*     */   
/*     */   public String toString() {
/*  44 */     return "push &1";
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getName() {
/*  54 */     return "Push";
/*     */   }
/*     */   
/*     */   public void execute_void(ProgramChromosome c, int n, Object[] args) {
/*  58 */     check(c);
/*  59 */     int value = c.execute_int(n, 0, args);
/*     */ 
/*     */     
/*  62 */     pushIt(new Integer(value));
/*     */   }
/*     */   
/*     */   public int execute_int(ProgramChromosome c, int n, Object[] args) {
/*  66 */     check(c);
/*  67 */     int value = c.execute_int(n, 0, args);
/*     */ 
/*     */     
/*  70 */     pushIt(new Integer(value));
/*  71 */     return value;
/*     */   }
/*     */   
/*     */   public long execute_long(ProgramChromosome c, int n, Object[] args) {
/*  75 */     check(c);
/*  76 */     long value = c.execute_long(n, 0, args);
/*     */ 
/*     */     
/*  79 */     pushIt(new Long(value));
/*  80 */     return value;
/*     */   }
/*     */   
/*     */   public double execute_double(ProgramChromosome c, int n, Object[] args) {
/*  84 */     check(c);
/*  85 */     double value = c.execute_double(n, 0, args);
/*  86 */     pushIt(new Double(value));
/*  87 */     return value;
/*     */   }
/*     */   
/*     */   public float execute_float(ProgramChromosome c, int n, Object[] args) {
/*  91 */     check(c);
/*  92 */     float value = c.execute_float(n, 0, args);
/*  93 */     pushIt(new Float(value));
/*  94 */     return value;
/*     */   }
/*     */   
/*     */   public Object execute_object(ProgramChromosome c, int n, Object[] args) {
/*  98 */     check(c);
/*  99 */     Object value = c.execute_object(n, 0, args);
/* 100 */     pushIt(value);
/* 101 */     return value;
/*     */   }
/*     */   
/*     */   public boolean isAffectGlobalState() {
/* 105 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isValid(ProgramChromosome a_program) {
/* 110 */     return (a_program.getCommandOfClass(0, Pop.class) >= 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void pushIt(Object a_value) {
/* 118 */     getGPConfiguration().pushToStack(a_value);
/*     */   }
/*     */   
/*     */   public Class getChildType(IGPProgram a_ind, int a_chromNum) {
/* 122 */     return this.m_type;
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
/* 135 */     if (a_other == null) {
/* 136 */       return 1;
/*     */     }
/*     */     
/* 139 */     Push other = (Push)a_other;
/* 140 */     return (new CompareToBuilder()).append(this.m_type, other.m_type).toComparison();
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
/* 161 */       Push other = (Push)a_other;
/* 162 */       return (new EqualsBuilder()).append(this.m_type, other.m_type).isEquals();
/*     */     
/*     */     }
/* 165 */     catch (ClassCastException cex) {
/* 166 */       return false;
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\gp\function\Push.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */