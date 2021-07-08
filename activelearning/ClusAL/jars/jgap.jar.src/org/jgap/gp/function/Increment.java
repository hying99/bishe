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
/*     */ public class Increment
/*     */   extends MathCommand
/*     */ {
/*     */   private static final String CVS_REVISION = "$Revision: 1.9 $";
/*     */   private int m_increment;
/*     */   
/*     */   public Increment(GPConfiguration a_conf, Class a_type) throws InvalidConfigurationException {
/*  44 */     this(a_conf, a_type, 1);
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
/*     */   public Increment(GPConfiguration a_conf, Class a_type, int a_increment) throws InvalidConfigurationException {
/*  60 */     this(a_conf, a_type, a_increment, 0, 0);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Increment(GPConfiguration a_conf, Class a_type, int a_increment, int a_subReturnType, int a_subChildType) throws InvalidConfigurationException {
/*  66 */     super(a_conf, 1, a_type, a_subReturnType, a_subChildType);
/*  67 */     this.m_increment = a_increment;
/*     */   }
/*     */   
/*     */   public String toString() {
/*  71 */     if (this.m_increment == 1) {
/*  72 */       return "INC(&1)";
/*     */     }
/*     */     
/*  75 */     return "INC(" + this.m_increment + ", &1)";
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
/*  86 */     return "INC";
/*     */   }
/*     */   
/*     */   public int execute_int(ProgramChromosome c, int n, Object[] args) {
/*  90 */     return c.execute_int(n, 0, args) + this.m_increment;
/*     */   }
/*     */   
/*     */   public long execute_long(ProgramChromosome c, int n, Object[] args) {
/*  94 */     return c.execute_long(n, 0, args) + this.m_increment;
/*     */   }
/*     */   
/*     */   public float execute_float(ProgramChromosome c, int n, Object[] args) {
/*  98 */     return c.execute_float(n, 0, args) + this.m_increment;
/*     */   }
/*     */   
/*     */   public double execute_double(ProgramChromosome c, int n, Object[] args) {
/* 102 */     return c.execute_double(n, 0, args) + this.m_increment;
/*     */   }
/*     */   
/*     */   public Object execute_object(ProgramChromosome c, int n, Object[] args) {
/* 106 */     return ((Compatible)c.execute_object(n, 0, args)).execute_increment();
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
/*     */   public int compareTo(Object a_other) {
/* 122 */     if (a_other == null) {
/* 123 */       return 1;
/*     */     }
/*     */     
/* 126 */     Increment other = (Increment)a_other;
/* 127 */     return (new CompareToBuilder()).append(this.m_increment, other.m_increment).toComparison();
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
/* 143 */     if (a_other == null) {
/* 144 */       return false;
/*     */     }
/*     */     
/*     */     try {
/* 148 */       Increment other = (Increment)a_other;
/* 149 */       return (new EqualsBuilder()).append(this.m_increment, other.m_increment).isEquals();
/*     */     
/*     */     }
/* 152 */     catch (ClassCastException cex) {
/* 153 */       return false;
/*     */     } 
/*     */   }
/*     */   
/*     */   protected static interface Compatible {
/*     */     Object execute_increment();
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\gp\function\Increment.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */