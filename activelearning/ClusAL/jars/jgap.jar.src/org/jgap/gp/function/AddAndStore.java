/*     */ package org.jgap.gp.function;
/*     */ 
/*     */ import org.apache.commons.lang.builder.CompareToBuilder;
/*     */ import org.apache.commons.lang.builder.EqualsBuilder;
/*     */ import org.jgap.InvalidConfigurationException;
/*     */ import org.jgap.gp.CommandGene;
/*     */ import org.jgap.gp.IGPProgram;
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
/*     */ public class AddAndStore
/*     */   extends MathCommand
/*     */ {
/*     */   private static final String CVS_REVISION = "$Revision: 1.10 $";
/*     */   private String m_storageName;
/*     */   private Class m_type;
/*     */   
/*     */   public AddAndStore(GPConfiguration a_conf, Class a_type, String a_storageName) throws InvalidConfigurationException {
/*  40 */     super(a_conf, 2, CommandGene.VoidClass);
/*  41 */     this.m_type = a_type;
/*  42 */     this.m_storageName = a_storageName;
/*     */   }
/*     */   
/*     */   public String toString() {
/*  46 */     return "Store(" + this.m_storageName + ", &1 + &2)";
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getName() {
/*  56 */     return "AddAndStore";
/*     */   }
/*     */   
/*     */   public void execute_void(ProgramChromosome c, int n, Object[] args) {
/*  60 */     Object value = null;
/*  61 */     if (this.m_type == CommandGene.IntegerClass) {
/*  62 */       value = new Integer(c.execute_int(n, 0, args) + c.execute_int(n, 1, args));
/*     */     }
/*  64 */     else if (this.m_type == CommandGene.LongClass) {
/*  65 */       value = new Long(c.execute_long(n, 0, args) + c.execute_long(n, 1, args));
/*     */     }
/*  67 */     else if (this.m_type == CommandGene.DoubleClass) {
/*  68 */       value = new Double(c.execute_double(n, 0, args) + c.execute_double(n, 1, args));
/*     */     
/*     */     }
/*  71 */     else if (this.m_type == CommandGene.FloatClass) {
/*  72 */       value = new Float(c.execute_float(n, 0, args) + c.execute_float(n, 1, args));
/*     */     }
/*     */     else {
/*     */       
/*  76 */       throw new RuntimeException("Type " + this.m_type + " not supported by AddAndStore");
/*     */     } 
/*     */     
/*  79 */     getGPConfiguration().storeInMemory(this.m_storageName, value);
/*     */   }
/*     */   
/*     */   public Class getChildType(IGPProgram a_ind, int a_chromNum) {
/*  83 */     return this.m_type;
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
/*  96 */     if (a_other == null) {
/*  97 */       return 1;
/*     */     }
/*     */     
/* 100 */     AddAndStore other = (AddAndStore)a_other;
/* 101 */     return (new CompareToBuilder()).append(this.m_type, other.m_type).append(this.m_storageName, other.m_storageName).toComparison();
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
/* 117 */     if (a_other == null) {
/* 118 */       return false;
/*     */     }
/*     */     
/*     */     try {
/* 122 */       AddAndStore other = (AddAndStore)a_other;
/* 123 */       return (new EqualsBuilder()).append(this.m_type, other.m_type).append(this.m_storageName, other.m_storageName).isEquals();
/*     */ 
/*     */     
/*     */     }
/* 127 */     catch (ClassCastException cex) {
/* 128 */       return false;
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\gp\function\AddAndStore.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */