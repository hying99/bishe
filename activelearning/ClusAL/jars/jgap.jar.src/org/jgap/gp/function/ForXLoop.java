/*     */ package org.jgap.gp.function;
/*     */ 
/*     */ import org.apache.commons.lang.builder.CompareToBuilder;
/*     */ import org.apache.commons.lang.builder.EqualsBuilder;
/*     */ import org.jgap.InvalidConfigurationException;
/*     */ import org.jgap.gp.CommandGene;
/*     */ import org.jgap.gp.IGPProgram;
/*     */ import org.jgap.gp.impl.GPConfiguration;
/*     */ import org.jgap.gp.impl.ProgramChromosome;
/*     */ import org.jgap.gp.terminal.Variable;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ForXLoop
/*     */   extends CommandGene
/*     */ {
/*     */   private static final String CVS_REVISION = "$Revision: 1.9 $";
/*     */   private Class m_type;
/*     */   
/*     */   public ForXLoop(GPConfiguration a_conf, Class a_type) throws InvalidConfigurationException {
/*  34 */     super(a_conf, 1, CommandGene.VoidClass);
/*  35 */     this.m_type = a_type;
/*     */   }
/*     */   
/*     */   public String toString() {
/*  39 */     return "for(int i=0;i<X;i++) { &1 }";
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getName() {
/*  49 */     return "ForXLoop";
/*     */   }
/*     */   public void execute_void(ProgramChromosome c, int n, Object[] args) {
/*     */     int x;
/*  53 */     check(c);
/*  54 */     int index = c.getVariableWithReturnType(0, this.m_type);
/*  55 */     if (index < 0) {
/*  56 */       throw new IllegalStateException("Variable missing for forX");
/*     */     }
/*     */     
/*  59 */     Variable var = (Variable)c.getNode(index);
/*     */ 
/*     */ 
/*     */     
/*  63 */     if (this.m_type == CommandGene.IntegerClass) {
/*  64 */       x = ((Integer)var.getValue()).intValue();
/*     */     }
/*  66 */     else if (this.m_type == CommandGene.LongClass) {
/*  67 */       x = ((Long)var.getValue()).intValue();
/*     */     }
/*  69 */     else if (this.m_type == CommandGene.DoubleClass) {
/*  70 */       x = ((Double)var.getValue()).intValue();
/*     */     }
/*  72 */     else if (this.m_type == CommandGene.FloatClass) {
/*  73 */       x = ((Float)var.getValue()).intValue();
/*     */     } else {
/*     */       
/*  76 */       throw new RuntimeException("Type " + this.m_type + " unknown in ForXCommand");
/*     */     } 
/*  78 */     if (x > 15) {
/*  79 */       x = 15;
/*     */     }
/*  81 */     for (int i = 0; i < x; i++) {
/*  82 */       c.execute_void(n, 0, args);
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean isValid(ProgramChromosome a_program) {
/*  87 */     return (a_program.getVariableWithReturnType(0, this.m_type) >= 0);
/*     */   }
/*     */   
/*     */   public Class getChildType(IGPProgram a_ind, int a_chromNum) {
/*  91 */     return CommandGene.VoidClass;
/*     */   }
/*     */   
/*     */   public Class getReturnType() {
/*  95 */     return super.getReturnType();
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
/*     */   public int compareTo(Object a_other) {
/* 107 */     if (a_other == null) {
/* 108 */       return 1;
/*     */     }
/*     */     
/* 111 */     ForXLoop other = (ForXLoop)a_other;
/* 112 */     return (new CompareToBuilder()).append(this.m_type, other.m_type).toComparison();
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
/*     */   public boolean equals(Object a_other) {
/* 127 */     if (a_other == null) {
/* 128 */       return false;
/*     */     }
/*     */     
/*     */     try {
/* 132 */       ForXLoop other = (ForXLoop)a_other;
/* 133 */       return (new EqualsBuilder()).append(this.m_type, other.m_type).isEquals();
/*     */     
/*     */     }
/* 136 */     catch (ClassCastException cex) {
/* 137 */       return false;
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\gp\function\ForXLoop.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */