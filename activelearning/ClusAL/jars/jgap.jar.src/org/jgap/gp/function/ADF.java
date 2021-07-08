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
/*     */ public class ADF
/*     */   extends CommandGene
/*     */ {
/*     */   private static final String CVS_REVISION = "$Revision: 1.13 $";
/*     */   private int m_chromosomeNum;
/*     */   
/*     */   public ADF(GPConfiguration a_conf, int a_chromosomeNum, int a_arity) throws InvalidConfigurationException {
/*  53 */     super(a_conf, a_arity, null);
/*  54 */     this.m_chromosomeNum = a_chromosomeNum;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getChromosomeNum() {
/*  64 */     return this.m_chromosomeNum;
/*     */   }
/*     */   
/*     */   public String toString() {
/*  68 */     return "ADF" + this.m_chromosomeNum + "(&1,&2,&3)";
/*     */   }
/*     */   
/*     */   public int getArity(IGPProgram a_individual) {
/*  72 */     if (a_individual.size() <= this.m_chromosomeNum) {
/*  73 */       return 0;
/*     */     }
/*  75 */     return a_individual.getChromosome(this.m_chromosomeNum).getArity();
/*     */   }
/*     */   
/*     */   public int execute_int(ProgramChromosome c, int n, Object[] args) {
/*  79 */     check(c);
/*  80 */     int numargs = c.getIndividual().getChromosome(this.m_chromosomeNum).getArity();
/*  81 */     Object[] vals = new Object[numargs];
/*  82 */     for (int i = 0; i < numargs; i++) {
/*  83 */       vals[i] = new Integer(c.execute_int(n, i, args));
/*     */     }
/*     */ 
/*     */     
/*  87 */     return c.getIndividual().execute_int(this.m_chromosomeNum, vals);
/*     */   }
/*     */   
/*     */   public boolean execute_boolean(ProgramChromosome c, int n, Object[] args) {
/*  91 */     check(c);
/*  92 */     int numargs = c.getIndividual().getChromosome(this.m_chromosomeNum).getArity();
/*  93 */     Object[] vals = new Object[numargs];
/*  94 */     for (int i = 0; i < numargs; i++) {
/*  95 */       vals[i] = new Boolean(c.execute_boolean(n, i, args));
/*     */     }
/*  97 */     return c.getIndividual().execute_boolean(this.m_chromosomeNum, vals);
/*     */   }
/*     */   
/*     */   public float execute_float(ProgramChromosome c, int n, Object[] args) {
/* 101 */     check(c);
/* 102 */     int numargs = c.getIndividual().getChromosome(this.m_chromosomeNum).getArity();
/* 103 */     Object[] vals = new Object[numargs];
/* 104 */     for (int i = 0; i < numargs; i++) {
/* 105 */       vals[i] = new Float(c.execute_float(n, i, args));
/*     */     }
/* 107 */     return c.getIndividual().execute_float(this.m_chromosomeNum, vals);
/*     */   }
/*     */   
/*     */   public double execute_double(ProgramChromosome c, int n, Object[] args) {
/* 111 */     check(c);
/* 112 */     int numargs = c.getIndividual().getChromosome(this.m_chromosomeNum).getArity();
/* 113 */     Object[] vals = new Object[numargs];
/* 114 */     for (int i = 0; i < numargs; i++) {
/* 115 */       vals[i] = new Double(c.execute_double(n, i, args));
/*     */     }
/* 117 */     return c.getIndividual().execute_double(this.m_chromosomeNum, vals);
/*     */   }
/*     */   
/*     */   public Object execute_object(ProgramChromosome c, int n, Object[] args) {
/* 121 */     check(c);
/* 122 */     int numargs = c.getIndividual().getChromosome(this.m_chromosomeNum).getArity();
/* 123 */     Object[] vals = new Object[numargs];
/* 124 */     for (int i = 0; i < numargs; i++) {
/* 125 */       vals[i] = c.execute(n, i, args);
/*     */     }
/* 127 */     return c.getIndividual().execute_object(this.m_chromosomeNum, vals);
/*     */   }
/*     */   
/*     */   public Class getChildType(IGPProgram a_ind, int i) {
/* 131 */     return a_ind.getChromosome(this.m_chromosomeNum).getArgTypes()[i];
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isValid(ProgramChromosome a_chrom) {
/* 137 */     StackTraceElement[] stack = (new Exception()).getStackTrace();
/* 138 */     if (stack.length > 60) {
/* 139 */       return false;
/*     */     }
/* 141 */     return true;
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
/* 154 */     if (a_other == null) {
/* 155 */       return 1;
/*     */     }
/*     */     
/* 158 */     ADF other = (ADF)a_other;
/* 159 */     return (new CompareToBuilder()).append(this.m_chromosomeNum, other.m_chromosomeNum).toComparison();
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
/* 175 */     if (a_other == null) {
/* 176 */       return false;
/*     */     }
/*     */     
/*     */     try {
/* 180 */       ADF other = (ADF)a_other;
/* 181 */       return (new EqualsBuilder()).append(this.m_chromosomeNum, other.m_chromosomeNum).isEquals();
/*     */     
/*     */     }
/* 184 */     catch (ClassCastException cex) {
/* 185 */       return false;
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\gp\function\ADF.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */