/*     */ package org.jgap.gp;
/*     */ 
/*     */ import org.jgap.InvalidConfigurationException;
/*     */ import org.jgap.gp.impl.GPConfiguration;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class BaseGPChromosome
/*     */   implements IGPChromosome
/*     */ {
/*     */   private static final String CVS_REVISION = "$Revision: 1.7 $";
/*     */   private GPConfiguration m_configuration;
/*     */   private IGPProgram m_ind;
/*     */   
/*     */   public BaseGPChromosome(GPConfiguration a_configuration) throws InvalidConfigurationException {
/*  39 */     if (a_configuration == null) {
/*  40 */       throw new InvalidConfigurationException("Configuration to be set must not be null!");
/*     */     }
/*     */ 
/*     */     
/*  44 */     this.m_configuration = a_configuration;
/*     */   }
/*     */ 
/*     */   
/*     */   public BaseGPChromosome(GPConfiguration a_configuration, IGPProgram a_ind) throws InvalidConfigurationException {
/*  49 */     this(a_configuration);
/*  50 */     this.m_ind = a_ind;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public IGPProgram getIndividual() {
/*  60 */     return this.m_ind;
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
/*     */   public void setIndividual(IGPProgram a_ind) {
/*  72 */     if (a_ind == null) {
/*  73 */       throw new IllegalArgumentException("Individual must not be null");
/*     */     }
/*  75 */     this.m_ind = a_ind;
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
/*     */   public int getTerminal(int a_index) {
/*  90 */     CommandGene[] functions = getFunctions();
/*  91 */     int len = functions.length;
/*  92 */     for (int j = 0; j < len && functions[j] != null; j++) {
/*  93 */       if (functions[j].getArity(this.m_ind) == 0 && 
/*  94 */         --a_index < 0) {
/*  95 */         return j;
/*     */       }
/*     */     } 
/*     */     
/*  99 */     return -1;
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
/*     */   public int getFunction(int a_index) {
/* 114 */     CommandGene[] functions = getFunctions();
/* 115 */     int len = functions.length;
/* 116 */     for (int j = 0; j < len && functions[j] != null; j++) {
/* 117 */       if (functions[j].getArity(this.m_ind) != 0 && 
/* 118 */         --a_index < 0) {
/* 119 */         return j;
/*     */       }
/*     */     } 
/*     */     
/* 123 */     return -1;
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
/*     */   public int getTerminal(int a_index, Class a_type, int a_subType) {
/* 141 */     CommandGene[] functions = getFunctions();
/* 142 */     int len = functions.length;
/* 143 */     for (int j = 0; j < len && functions[j] != null; j++) {
/* 144 */       if ((a_subType == 0 || functions[j].getSubReturnType() == a_subType) && functions[j].getReturnType() == a_type && functions[j].getArity(this.m_ind) == 0)
/*     */       {
/*     */         
/* 147 */         if (--a_index < 0) {
/* 148 */           return j;
/*     */         }
/*     */       }
/*     */     } 
/* 152 */     return -1;
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
/*     */   public int getFunction(int a_index, Class a_type, int a_subType) {
/* 170 */     CommandGene[] functions = getFunctions();
/* 171 */     int len = functions.length;
/* 172 */     for (int j = 0; j < len && functions[j] != null; j++) {
/* 173 */       if (functions[j].getReturnType() == a_type && (a_subType == 0 || a_subType == functions[j].getSubReturnType()) && functions[j].getArity(this.m_ind) != 0)
/*     */       {
/*     */         
/* 176 */         if (--a_index < 0) {
/* 177 */           return j;
/*     */         }
/*     */       }
/*     */     } 
/* 181 */     return -1;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int numTerminals() {
/* 191 */     int count = 0;
/* 192 */     CommandGene[] functions = getFunctions();
/* 193 */     int len = functions.length;
/* 194 */     for (int i = 0; i < len && functions[i] != null; i++) {
/* 195 */       if (functions[i].getArity(this.m_ind) == 0) {
/* 196 */         count++;
/*     */       }
/*     */     } 
/* 199 */     return count;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int numFunctions() {
/* 209 */     int count = 0;
/* 210 */     CommandGene[] functions = getFunctions();
/* 211 */     int len = functions.length;
/* 212 */     for (int i = 0; i < len && functions[i] != null; i++) {
/* 213 */       if (functions[i].getArity(this.m_ind) != 0) {
/* 214 */         count++;
/*     */       }
/*     */     } 
/* 217 */     return count;
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
/*     */   public int numTerminals(Class a_type, int a_subType) {
/* 231 */     int count = 0;
/* 232 */     CommandGene[] functions = getFunctions();
/* 233 */     int len = functions.length;
/* 234 */     for (int i = 0; i < len && functions[i] != null; i++) {
/* 235 */       if (functions[i].getArity(this.m_ind) == 0 && functions[i].getReturnType() == a_type && (a_subType == 0 || functions[i].getSubReturnType() == a_subType))
/*     */       {
/*     */         
/* 238 */         count++;
/*     */       }
/*     */     } 
/* 241 */     return count;
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
/*     */   public int numFunctions(Class a_type, int a_subType) {
/* 255 */     int count = 0;
/* 256 */     CommandGene[] functions = getFunctions();
/* 257 */     int len = functions.length;
/* 258 */     for (int i = 0; i < len && functions[i] != null; i++) {
/* 259 */       if (functions[i].getArity(this.m_ind) != 0 && functions[i].getReturnType() == a_type && (a_subType == 0 || functions[i].getSubReturnType() == a_subType))
/*     */       {
/*     */         
/* 262 */         count++;
/*     */       }
/*     */     } 
/* 265 */     return count;
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
/*     */   public CommandGene getNode(int a_index) {
/* 279 */     if (a_index >= (getFunctions()).length || getFunctions()[a_index] == null) {
/* 280 */       return null;
/*     */     }
/* 282 */     return getFunctions()[a_index];
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
/*     */   public int getCommandOfClass(int a_n, Class<?> a_class) {
/* 296 */     CommandGene[] functions = getFunctions();
/* 297 */     int len = functions.length;
/* 298 */     for (int j = 0; j < len && functions[j] != null; j++) {
/* 299 */       if (functions[j].getClass() == a_class && 
/* 300 */         --a_n < 0) {
/* 301 */         return j;
/*     */       }
/*     */     } 
/*     */     
/* 305 */     return -1;
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
/*     */   public int getAssignableFromClass(int a_n, Class a_class) {
/* 319 */     CommandGene[] functions = getFunctions();
/* 320 */     int len = functions.length;
/* 321 */     for (int j = 0; j < len && functions[j] != null; j++) {
/* 322 */       if (a_class.isAssignableFrom(functions[j].getClass()) && 
/* 323 */         --a_n < 0) {
/* 324 */         return j;
/*     */       }
/*     */     } 
/*     */     
/* 328 */     return -1;
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
/*     */   public int getVariableWithReturnType(int a_n, Class a_returnType) {
/* 342 */     CommandGene[] functions = getFunctions();
/* 343 */     int len = functions.length;
/* 344 */     for (int j = 0; j < len && functions[j] != null; j++) {
/* 345 */       if (functions[j].getClass() == Variable.class) {
/* 346 */         Variable v = (Variable)functions[j];
/* 347 */         if (v.getReturnType() == a_returnType && 
/* 348 */           --a_n < 0) {
/* 349 */           return j;
/*     */         }
/*     */       } 
/*     */     } 
/*     */     
/* 354 */     return -1;
/*     */   }
/*     */   
/*     */   public GPConfiguration getGPConfiguration() {
/* 358 */     return this.m_configuration;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\gp\BaseGPChromosome.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */