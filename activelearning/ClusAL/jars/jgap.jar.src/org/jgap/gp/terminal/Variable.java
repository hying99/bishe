/*     */ package org.jgap.gp.terminal;
/*     */ 
/*     */ import java.util.Hashtable;
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
/*     */ public class Variable
/*     */   extends CommandGene
/*     */ {
/*     */   private static final String CVS_REVISION = "$Revision: 1.9 $";
/*  28 */   public static Hashtable vars = new Hashtable<Object, Object>();
/*     */ 
/*     */   
/*     */   private String m_name;
/*     */ 
/*     */   
/*     */   private Object m_value;
/*     */ 
/*     */ 
/*     */   
/*     */   public Variable(GPConfiguration a_conf, String a_varName, Class type) throws InvalidConfigurationException {
/*  39 */     super(a_conf, 0, type);
/*  40 */     this.m_name = a_varName;
/*  41 */     vars.put(a_varName, this);
/*     */   }
/*     */   
/*     */   public String toString() {
/*  45 */     return this.m_name;
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
/*     */   public String getName() {
/*  58 */     return this.m_name;
/*     */   }
/*     */   
/*     */   public Class getChildType(IGPProgram a_ind, int a_chromNum) {
/*  62 */     return null;
/*     */   }
/*     */   
/*     */   public boolean execute_boolean(ProgramChromosome c, int n, Object[] args) {
/*  66 */     return ((Boolean)this.m_value).booleanValue();
/*     */   }
/*     */   
/*     */   public int execute_int(ProgramChromosome c, int n, Object[] args) {
/*  70 */     return ((Integer)this.m_value).intValue();
/*     */   }
/*     */   
/*     */   public long execute_long(ProgramChromosome c, int n, Object[] args) {
/*  74 */     return ((Long)this.m_value).longValue();
/*     */   }
/*     */   
/*     */   public float execute_float(ProgramChromosome c, int n, Object[] args) {
/*  78 */     return ((Float)this.m_value).floatValue();
/*     */   }
/*     */   
/*     */   public double execute_double(ProgramChromosome c, int n, Object[] args) {
/*  82 */     return ((Double)this.m_value).doubleValue();
/*     */   }
/*     */   
/*     */   public Object execute_object(ProgramChromosome c, int n, Object[] args) {
/*  86 */     return this.m_value;
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
/*     */   public static Variable getVariable(String name) {
/*  99 */     return (Variable)vars.get(name);
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
/*     */   
/*     */   public static Variable create(GPConfiguration a_conf, String a_name, Class a_type) throws InvalidConfigurationException {
/*     */     Variable var;
/* 122 */     if ((var = getVariable(a_name)) != null) {
/* 123 */       return var;
/*     */     }
/* 125 */     return new Variable(a_conf, a_name, a_type);
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
/*     */   public void set(Object a_value) {
/* 137 */     this.m_value = a_value;
/*     */   }
/*     */   
/*     */   public Object getValue() {
/* 141 */     return this.m_value;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\gp\terminal\Variable.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */