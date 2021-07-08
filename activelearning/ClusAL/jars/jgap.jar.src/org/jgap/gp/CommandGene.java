/*     */ package org.jgap.gp;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import org.jgap.InvalidConfigurationException;
/*     */ import org.jgap.RandomGenerator;
/*     */ import org.jgap.UnsupportedRepresentationException;
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
/*     */ public abstract class CommandGene
/*     */   implements Comparable, Serializable
/*     */ {
/*     */   private static final String CVS_REVISION = "$Revision: 1.26 $";
/*     */   static final String PERSISTENT_FIELD_DELIMITER = ":";
/*     */   static final String EXTENDED_INFO_DELIMITER = "~";
/*     */   public static final double DELTA = 1.0E-7D;
/*  43 */   public static final Class BooleanClass = Boolean.class;
/*     */   
/*  45 */   public static final Class IntegerClass = Integer.class;
/*     */   
/*  47 */   public static final Class LongClass = Long.class;
/*     */   
/*  49 */   public static final Class FloatClass = Float.class;
/*     */   
/*  51 */   public static final Class DoubleClass = Double.class;
/*     */   
/*  53 */   public static final Class VoidClass = Void.class;
/*     */ 
/*     */ 
/*     */   
/*     */   private GPConfiguration m_configuration;
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean m_noValidation;
/*     */ 
/*     */ 
/*     */   
/*     */   private Class m_returnType;
/*     */ 
/*     */ 
/*     */   
/*     */   private int m_arity;
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean m_integerType;
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean m_floatType;
/*     */ 
/*     */ 
/*     */   
/*     */   private double m_energy;
/*     */ 
/*     */ 
/*     */   
/*     */   private Object m_applicationData;
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean m_compareAppData;
/*     */ 
/*     */ 
/*     */   
/*     */   private int m_subReturnType;
/*     */ 
/*     */ 
/*     */   
/*     */   private int[] m_subChildTypes;
/*     */ 
/*     */ 
/*     */   
/*     */   public int nodeIndex;
/*     */ 
/*     */ 
/*     */   
/*     */   protected void init() {}
/*     */ 
/*     */ 
/*     */   
/*     */   public CommandGene(GPConfiguration a_conf, int a_arity, Class<Integer> a_returnType) throws InvalidConfigurationException {
/* 110 */     if (a_conf == null) {
/* 111 */       throw new InvalidConfigurationException("Configuration must not be null!");
/*     */     }
/* 113 */     this.m_configuration = a_conf;
/* 114 */     init();
/* 115 */     this.m_arity = a_arity;
/* 116 */     this.m_returnType = a_returnType;
/* 117 */     if (a_returnType == Integer.class || a_returnType == Long.class) {
/*     */       
/* 119 */       this.m_integerType = true;
/*     */     }
/* 121 */     else if (a_returnType == Double.class || a_returnType == Float.class) {
/*     */       
/* 123 */       this.m_floatType = true;
/*     */     } 
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
/*     */   public CommandGene(GPConfiguration a_conf, int a_arity, Class a_returnType, int a_subReturnType, int[] a_childSubTypes) throws InvalidConfigurationException {
/* 144 */     this(a_conf, a_arity, a_returnType);
/* 145 */     if (a_childSubTypes != null) {
/* 146 */       boolean specialCase = false;
/*     */ 
/*     */       
/* 149 */       if (a_childSubTypes.length == 1 && 
/* 150 */         a_childSubTypes[0] == 0) {
/* 151 */         this.m_subChildTypes = null;
/* 152 */         specialCase = true;
/*     */       } 
/*     */       
/* 155 */       if (!specialCase) {
/* 156 */         if (a_childSubTypes.length != a_arity) {
/* 157 */           throw new IllegalArgumentException("Length of child sub types must equal the given arity (or set the former to null)");
/*     */         
/*     */         }
/*     */       }
/*     */       else {
/*     */         
/* 163 */         this.m_subChildTypes = a_childSubTypes;
/*     */       } 
/*     */     } else {
/*     */       
/* 167 */       this.m_subChildTypes = a_childSubTypes;
/*     */     } 
/* 169 */     this.m_subReturnType = a_subReturnType;
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
/*     */   public CommandGene(GPConfiguration a_conf, int a_arity, Class a_returnType, int a_subReturnType) throws InvalidConfigurationException {
/* 187 */     this(a_conf, a_arity, a_returnType, a_subReturnType, (int[])null);
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
/*     */   public CommandGene(GPConfiguration a_conf, int a_arity, Class a_returnType, int a_subReturnType, int a_childSubType) throws InvalidConfigurationException {
/* 208 */     this(a_conf, a_arity, a_returnType, a_subReturnType, new int[] { a_childSubType });
/*     */   }
/*     */ 
/*     */   
/*     */   public void setAllele(Object a_newValue) {
/* 213 */     throw new UnsupportedOperationException("Method setAllele() not used.");
/*     */   }
/*     */ 
/*     */   
/*     */   public Object getAllele() {
/* 218 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void setToRandomValue(RandomGenerator a_numberGenerator) {}
/*     */ 
/*     */ 
/*     */   
/*     */   public void cleanup() {}
/*     */ 
/*     */ 
/*     */   
/*     */   public int size() {
/* 232 */     return this.m_arity;
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
/*     */   public int getArity(IGPProgram a_indvividual) {
/* 246 */     return this.m_arity;
/*     */   }
/*     */   
/*     */   public int compareTo(Object a_other) {
/* 250 */     CommandGene o2 = (CommandGene)a_other;
/* 251 */     if (size() != o2.size()) {
/* 252 */       if (size() > o2.size()) {
/* 253 */         return 1;
/*     */       }
/*     */       
/* 256 */       return -1;
/*     */     } 
/*     */     
/* 259 */     if (getClass() != o2.getClass())
/*     */     {
/* 261 */       return -1;
/*     */     }
/*     */     
/* 264 */     return 0;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object a_other) {
/* 269 */     if (a_other == null) {
/* 270 */       return false;
/*     */     }
/*     */ 
/*     */     
/*     */     try {
/* 275 */       CommandGene other = (CommandGene)a_other;
/* 276 */       if (getClass() == a_other.getClass()) {
/* 277 */         if (getInternalValue() == null) {
/* 278 */           if (other.getInternalValue() == null) {
/* 279 */             return true;
/*     */           }
/*     */           
/* 282 */           return false;
/*     */         } 
/*     */ 
/*     */         
/* 286 */         if (other.getInternalValue() == null) {
/* 287 */           return false;
/*     */         }
/*     */         
/* 290 */         return true;
/*     */       } 
/*     */ 
/*     */ 
/*     */       
/* 295 */       return false;
/*     */     
/*     */     }
/* 298 */     catch (ClassCastException cex) {
/* 299 */       return false;
/*     */     } 
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
/*     */   public abstract String toString();
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
/*     */   public Object execute(ProgramChromosome c, int n, Object[] args) {
/* 326 */     if (this.m_returnType == BooleanClass) {
/* 327 */       return new Boolean(execute_boolean(c, n, args));
/*     */     }
/* 329 */     if (this.m_returnType == IntegerClass) {
/* 330 */       return new Integer(execute_int(c, n, args));
/*     */     }
/* 332 */     if (this.m_returnType == LongClass) {
/* 333 */       return new Long(execute_long(c, n, args));
/*     */     }
/* 335 */     if (this.m_returnType == FloatClass) {
/* 336 */       return new Float(execute_float(c, n, args));
/*     */     }
/* 338 */     if (this.m_returnType == DoubleClass) {
/* 339 */       return new Double(execute_double(c, n, args));
/*     */     }
/* 341 */     if (this.m_returnType == VoidClass) {
/* 342 */       execute_void(c, n, args);
/*     */     } else {
/*     */       
/* 345 */       return execute_object(c, n, args);
/*     */     } 
/* 347 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Class getReturnType() {
/* 357 */     return this.m_returnType;
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
/*     */   public void setReturnType(Class a_type) {
/* 369 */     this.m_returnType = a_type;
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
/*     */   public boolean execute_boolean(ProgramChromosome c, int n, Object[] args) {
/* 385 */     throw new UnsupportedOperationException(getName() + " cannot return boolean");
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
/*     */   public void execute_void(ProgramChromosome c, int n, Object[] args) {
/* 401 */     throw new UnsupportedOperationException(getName() + " cannot return void");
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
/*     */   public int execute_int(ProgramChromosome c, int n, Object[] args) {
/* 418 */     throw new UnsupportedOperationException(getName() + " cannot return int");
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
/*     */   public long execute_long(ProgramChromosome c, int n, Object[] args) {
/* 435 */     throw new UnsupportedOperationException(getName() + " cannot return long");
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
/*     */   public float execute_float(ProgramChromosome c, int n, Object[] args) {
/* 452 */     throw new UnsupportedOperationException(getName() + " cannot return float");
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
/*     */   public double execute_double(ProgramChromosome c, int n, Object[] args) {
/* 469 */     throw new UnsupportedOperationException(getName() + " cannot return double");
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
/*     */   public Object execute_object(ProgramChromosome c, int n, Object[] args) {
/* 486 */     throw new UnsupportedOperationException(getName() + " cannot return Object");
/*     */   }
/*     */ 
/*     */   
/*     */   public String getName() {
/* 491 */     return toString();
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
/*     */   public Class getChildType(IGPProgram a_ind, int a_chromNum) {
/* 507 */     if (this.m_arity == 0) {
/* 508 */       return null;
/*     */     }
/*     */     
/* 511 */     return getReturnType();
/*     */   }
/*     */ 
/*     */   
/*     */   protected Object getInternalValue() {
/* 516 */     return null;
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
/*     */   public int hashCode() {
/* 533 */     if (getInternalValue() == null) {
/* 534 */       return getClass().getName().hashCode();
/*     */     }
/*     */     
/* 537 */     return getInternalValue().hashCode();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isIntegerType() {
/* 542 */     return this.m_integerType;
/*     */   }
/*     */   
/*     */   public boolean isFloatType() {
/* 546 */     return this.m_floatType;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isAffectGlobalState() {
/* 556 */     return false;
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
/*     */   public boolean isValid(ProgramChromosome a_program) {
/* 570 */     return true;
/*     */   }
/*     */   
/*     */   public boolean isValid(ProgramChromosome a_program, int a_index) {
/* 574 */     return true;
/*     */   }
/*     */   
/*     */   protected void check(ProgramChromosome a_program) {
/* 578 */     if (this.m_noValidation) {
/*     */       return;
/*     */     }
/* 581 */     if (!isValid(a_program)) {
/* 582 */       throw new IllegalStateException("State for GP-command not valid");
/*     */     }
/*     */   }
/*     */   
/*     */   protected void check(ProgramChromosome a_program, int a_index) {
/* 587 */     if (this.m_noValidation) {
/*     */       return;
/*     */     }
/* 590 */     if (!isValid(a_program, a_index)) {
/* 591 */       throw new IllegalStateException("State for GP-command not valid");
/*     */     }
/*     */   }
/*     */   
/*     */   public void setNoValidation(boolean a_noValidation) {
/* 596 */     this.m_noValidation = a_noValidation;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public GPConfiguration getGPConfiguration() {
/* 606 */     return this.m_configuration;
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
/*     */   public void setApplicationData(Object a_newData) {
/* 622 */     this.m_applicationData = a_newData;
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
/*     */   public Object getApplicationData() {
/* 638 */     return this.m_applicationData;
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
/*     */   public void setCompareApplicationData(boolean a_doCompare) {
/* 652 */     this.m_compareAppData = a_doCompare;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isCompareApplicationData() {
/* 662 */     return this.m_compareAppData;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getEnergy() {
/* 672 */     return this.m_energy;
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
/*     */   public void setEnergy(double a_energy) {
/* 684 */     this.m_energy = a_energy;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getSubReturnType() {
/* 694 */     return this.m_subReturnType;
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
/*     */   public int getSubChildType(int a_childNum) {
/* 706 */     if (this.m_subChildTypes == null) {
/* 707 */       return 0;
/*     */     }
/*     */     
/* 710 */     return this.m_subChildTypes[a_childNum];
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected int[] getSubChildTypes() {
/* 721 */     return this.m_subChildTypes;
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
/*     */   public void ensureUniqueness(ProgramChromosome a_program) {
/* 734 */     if (a_program.getCommandOfClass(1, getClass()) >= 0) {
/* 735 */       throw new IllegalStateException("Command must not occur more than once!");
/*     */     }
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
/*     */   public String getPersistentRepresentation() {
/*     */     String s;
/* 750 */     if (this.m_returnType == null) {
/* 751 */       s = "null";
/*     */     } else {
/*     */       
/* 754 */       s = this.m_returnType.getClass().getName();
/*     */     } 
/* 756 */     String result = ":" + this.m_arity + ":" + s + ":" + this.m_subReturnType + ":" + this.m_subChildTypes + "~" + getPersistentRepresentationExt() + "~";
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 762 */     return result;
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
/*     */   protected String getPersistentRepresentationExt() {
/* 775 */     return null;
/*     */   }
/*     */   
/*     */   public void setValueFromPersistentRepresentation(String a_representation) throws UnsupportedRepresentationException {}
/*     */   
/*     */   protected void setValueFromString(int a_index, String a_value) {}
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\gp\CommandGene.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */