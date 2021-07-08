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
/*     */ public class Loop
/*     */   extends CommandGene
/*     */ {
/*     */   private static final String CVS_REVISION = "$Revision: 1.4 $";
/*     */   private Class m_typeVar;
/*     */   private int m_count;
/*     */   
/*     */   public Loop(GPConfiguration a_conf, Class a_typeVar, int a_count) throws InvalidConfigurationException {
/*  46 */     this(a_conf, a_typeVar, a_count, 0, 0);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Loop(GPConfiguration a_conf, Class a_typeVar, int a_count, int a_subReturnType, int a_subChildType) throws InvalidConfigurationException {
/*  52 */     super(a_conf, 1, CommandGene.VoidClass, a_subReturnType, a_subChildType);
/*  53 */     this.m_typeVar = a_typeVar;
/*  54 */     this.m_count = a_count;
/*     */   }
/*     */   
/*     */   public String toString() {
/*  58 */     return "loop(" + this.m_count + ", &1 }";
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getName() {
/*  68 */     return "Loop";
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void execute_void(ProgramChromosome c, int n, Object[] args) {
/*  74 */     for (int i = 0; i < this.m_count; i++) {
/*  75 */       c.execute_void(n, 0, args);
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean isValid(ProgramChromosome a_program) {
/*  80 */     return true;
/*     */   }
/*     */   
/*     */   public Class getChildType(IGPProgram a_ind, int a_chromNum) {
/*  84 */     return CommandGene.VoidClass;
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
/*  97 */     if (a_other == null) {
/*  98 */       return 1;
/*     */     }
/*     */     
/* 101 */     Loop other = (Loop)a_other;
/* 102 */     return (new CompareToBuilder()).append(this.m_typeVar, other.m_typeVar).append(this.m_count, other.m_count).toComparison();
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
/* 119 */     if (a_other == null) {
/* 120 */       return false;
/*     */     }
/*     */     
/*     */     try {
/* 124 */       Loop other = (Loop)a_other;
/* 125 */       return (new EqualsBuilder()).append(this.m_typeVar, other.m_typeVar).append(this.m_count, other.m_count).isEquals();
/*     */ 
/*     */     
/*     */     }
/* 129 */     catch (ClassCastException cex) {
/* 130 */       return false;
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\gp\function\Loop.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */