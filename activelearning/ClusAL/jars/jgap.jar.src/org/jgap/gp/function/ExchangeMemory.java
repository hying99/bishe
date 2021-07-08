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
/*     */ public class ExchangeMemory
/*     */   extends CommandGene
/*     */ {
/*     */   private static final String CVS_REVISION = "$Revision: 1.3 $";
/*     */   private String m_sourceStorageName;
/*     */   private String m_targetStorageName;
/*     */   
/*     */   public ExchangeMemory(GPConfiguration a_conf, String a_firstStorageName, String a_secondStorageName) throws InvalidConfigurationException {
/*  40 */     super(a_conf, 0, CommandGene.VoidClass);
/*  41 */     if (a_firstStorageName == null || a_firstStorageName.length() < 1) {
/*  42 */       throw new IllegalArgumentException("First memory name must not be empty!");
/*     */     }
/*     */     
/*  45 */     if (a_secondStorageName == null || a_secondStorageName.length() < 1) {
/*  46 */       throw new IllegalArgumentException("Second memory name must not be empty!");
/*     */     }
/*     */     
/*  49 */     if (a_firstStorageName.equals(a_secondStorageName)) {
/*  50 */       throw new IllegalArgumentException("First and second memory name must be different!");
/*     */     }
/*     */     
/*  53 */     this.m_sourceStorageName = a_firstStorageName;
/*  54 */     this.m_targetStorageName = a_secondStorageName;
/*     */   }
/*     */   
/*     */   public String toString() {
/*  58 */     return "exchange(" + this.m_sourceStorageName + ", " + this.m_targetStorageName + ")";
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
/*     */   public String getName() {
/*  72 */     return "Exchange Memory";
/*     */   }
/*     */   
/*     */   public void execute_void(ProgramChromosome c, int n, Object[] args) {
/*  76 */     check(c);
/*     */ 
/*     */     
/*     */     try {
/*  80 */       Object value1 = getGPConfiguration().readFromMemory(this.m_sourceStorageName);
/*     */       
/*  82 */       Object value2 = getGPConfiguration().readFromMemory(this.m_targetStorageName);
/*     */ 
/*     */ 
/*     */       
/*  86 */       getGPConfiguration().storeInMemory(this.m_sourceStorageName, value2);
/*  87 */       getGPConfiguration().storeInMemory(this.m_targetStorageName, value1);
/*  88 */     } catch (IllegalArgumentException iex) {
/*  89 */       throw new IllegalStateException("ExchangeMemory without preceeding StoreTerminal");
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isAffectGlobalState() {
/*  96 */     return true;
/*     */   }
/*     */   
/*     */   public boolean isValid(ProgramChromosome a_program) {
/* 100 */     return true;
/*     */   }
/*     */   
/*     */   public Class getChildType(IGPProgram a_ind, int a_chromNum) {
/* 104 */     return null;
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
/* 117 */     if (a_other == null) {
/* 118 */       return 1;
/*     */     }
/*     */     
/* 121 */     ExchangeMemory other = (ExchangeMemory)a_other;
/* 122 */     return (new CompareToBuilder()).append(this.m_sourceStorageName, other.m_sourceStorageName).append(this.m_targetStorageName, other.m_targetStorageName).toComparison();
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
/* 139 */     if (a_other == null) {
/* 140 */       return false;
/*     */     }
/*     */     
/*     */     try {
/* 144 */       ExchangeMemory other = (ExchangeMemory)a_other;
/* 145 */       return (new EqualsBuilder()).append(this.m_sourceStorageName, other.m_sourceStorageName).append(this.m_targetStorageName, other.m_targetStorageName).isEquals();
/*     */ 
/*     */     
/*     */     }
/* 149 */     catch (ClassCastException cex) {
/* 150 */       return false;
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\gp\function\ExchangeMemory.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */