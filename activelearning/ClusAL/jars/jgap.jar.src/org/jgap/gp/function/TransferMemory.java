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
/*     */ public class TransferMemory
/*     */   extends CommandGene
/*     */ {
/*     */   private static final String CVS_REVISION = "$Revision: 1.9 $";
/*     */   private String m_sourceStorageName;
/*     */   private String m_targetStorageName;
/*     */   
/*     */   public TransferMemory(GPConfiguration a_conf, String a_sourceStorageName, String a_targetStorageName) throws InvalidConfigurationException {
/*  40 */     super(a_conf, 0, CommandGene.VoidClass);
/*  41 */     if (a_sourceStorageName == null || a_sourceStorageName.length() < 1) {
/*  42 */       throw new IllegalArgumentException("Source memory name must not be empty!");
/*     */     }
/*     */     
/*  45 */     if (a_targetStorageName == null || a_targetStorageName.length() < 1) {
/*  46 */       throw new IllegalArgumentException("Target memory name must not be empty!");
/*     */     }
/*     */     
/*  49 */     if (a_sourceStorageName.equals(a_targetStorageName)) {
/*  50 */       throw new IllegalArgumentException("Source and target memory name must be different!");
/*     */     }
/*     */     
/*  53 */     this.m_sourceStorageName = a_sourceStorageName;
/*  54 */     this.m_targetStorageName = a_targetStorageName;
/*     */   }
/*     */   
/*     */   public String toString() {
/*  58 */     return "transfer(" + this.m_sourceStorageName + " -> " + this.m_targetStorageName + ")";
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
/*  72 */     return "Transfer Memory";
/*     */   }
/*     */   
/*     */   public void execute_void(ProgramChromosome c, int n, Object[] args) {
/*  76 */     check(c);
/*     */ 
/*     */     
/*     */     try {
/*  80 */       Object value = getGPConfiguration().readFromMemory(this.m_sourceStorageName);
/*     */ 
/*     */       
/*  83 */       getGPConfiguration().storeInMemory(this.m_targetStorageName, value);
/*  84 */     } catch (IllegalArgumentException iex) {
/*  85 */       throw new IllegalStateException("TransferMemory without preceeding StoreTerminal");
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isAffectGlobalState() {
/*  92 */     return true;
/*     */   }
/*     */   
/*     */   public boolean isValid(ProgramChromosome a_program) {
/*  96 */     return true;
/*     */   }
/*     */   
/*     */   public Class getChildType(IGPProgram a_ind, int a_chromNum) {
/* 100 */     return null;
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
/* 113 */     if (a_other == null) {
/* 114 */       return 1;
/*     */     }
/*     */     
/* 117 */     TransferMemory other = (TransferMemory)a_other;
/* 118 */     return (new CompareToBuilder()).append(this.m_sourceStorageName, other.m_sourceStorageName).append(this.m_targetStorageName, other.m_targetStorageName).toComparison();
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
/* 135 */     if (a_other == null) {
/* 136 */       return false;
/*     */     }
/*     */     
/*     */     try {
/* 140 */       TransferMemory other = (TransferMemory)a_other;
/* 141 */       return (new EqualsBuilder()).append(this.m_sourceStorageName, other.m_sourceStorageName).append(this.m_targetStorageName, other.m_targetStorageName).isEquals();
/*     */ 
/*     */     
/*     */     }
/* 145 */     catch (ClassCastException cex) {
/* 146 */       return false;
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\gp\function\TransferMemory.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */