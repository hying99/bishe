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
/*     */ public class ForLoop
/*     */   extends CommandGene
/*     */ {
/*     */   private static final String CVS_REVISION = "$Revision: 1.16 $";
/*  29 */   private static String INTERNAL_COUNTER_STORAGE = "FORLOOPSTORAGE_INT";
/*     */ 
/*     */ 
/*     */   
/*     */   private Class m_typeVar;
/*     */ 
/*     */ 
/*     */   
/*     */   private int m_startIndex;
/*     */ 
/*     */ 
/*     */   
/*     */   private int m_endIndex;
/*     */ 
/*     */   
/*     */   private int m_increment;
/*     */ 
/*     */   
/*     */   private int m_maxLoop;
/*     */ 
/*     */   
/*     */   private String m_memory_name_int;
/*     */ 
/*     */   
/*     */   private String m_varName;
/*     */ 
/*     */ 
/*     */   
/*     */   public ForLoop(GPConfiguration a_conf, Class a_typeVar, int a_maxLoop) throws InvalidConfigurationException {
/*  58 */     this(a_conf, a_typeVar, 0, a_maxLoop);
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
/*     */   public ForLoop(GPConfiguration a_conf, Class a_typeVar, int a_startIndex, int a_maxLoop) throws InvalidConfigurationException {
/*  76 */     this(a_conf, a_typeVar, a_startIndex, a_maxLoop, "i");
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public ForLoop(GPConfiguration a_conf, Class a_typeVar, int a_startIndex, int a_maxLoop, String a_varName) throws InvalidConfigurationException {
/*  82 */     super(a_conf, 2, CommandGene.VoidClass);
/*  83 */     this.m_typeVar = a_typeVar;
/*  84 */     this.m_maxLoop = a_maxLoop;
/*  85 */     this.m_startIndex = a_startIndex;
/*  86 */     this.m_endIndex = -1;
/*  87 */     this.m_increment = 1;
/*  88 */     this.m_varName = a_varName;
/*  89 */     init();
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
/*     */   public ForLoop(GPConfiguration a_conf, Class a_typeVar, int a_startIndex, int a_endIndex, int a_increment, String a_varName) throws InvalidConfigurationException {
/* 111 */     this(a_conf, a_typeVar, a_startIndex, a_endIndex, a_increment, a_varName, 0, 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ForLoop(GPConfiguration a_conf, Class a_typeVar, int a_startIndex, int a_endIndex, int a_increment, String a_varName, int a_subReturnType, int a_subChildType) throws InvalidConfigurationException {
/* 119 */     super(a_conf, 1, CommandGene.VoidClass, a_subReturnType, a_subChildType);
/* 120 */     this.m_typeVar = a_typeVar;
/* 121 */     this.m_increment = a_increment;
/* 122 */     this.m_startIndex = a_startIndex;
/* 123 */     this.m_endIndex = a_endIndex;
/* 124 */     this.m_varName = a_varName;
/* 125 */     init();
/*     */   }
/*     */   
/*     */   protected void init() {
/* 129 */     super.init();
/*     */ 
/*     */     
/* 132 */     this.m_memory_name_int = INTERNAL_COUNTER_STORAGE;
/* 133 */     this.m_memory_name_int += this.m_varName;
/* 134 */     this.m_memory_name_int += getGPConfiguration().getRandomGenerator().nextDouble();
/*     */   }
/*     */   public String toString() {
/*     */     String incrString;
/* 138 */     if (this.m_endIndex == -1) {
/* 139 */       return "for(int i=" + this.m_startIndex + ";i<&1;i++) { &2 }";
/*     */     }
/*     */ 
/*     */     
/* 143 */     if (this.m_increment == 1) {
/* 144 */       incrString = this.m_varName + "++";
/*     */     } else {
/*     */       
/* 147 */       incrString = this.m_varName + "=" + this.m_varName + "+1";
/*     */     } 
/* 149 */     return "for(int " + this.m_varName + "=" + this.m_startIndex + ";" + this.m_varName + "<" + this.m_endIndex + ";" + incrString + ") { &1 }";
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
/* 162 */     return "ForLoop";
/*     */   }
/*     */   
/*     */   public Object execute_object(ProgramChromosome c, int n, Object[] args) {
/* 166 */     StringBuffer value = new StringBuffer();
/* 167 */     value = value.append("for(int " + this.m_varName + "=" + this.m_startIndex + ";" + this.m_varName + "<" + this.m_endIndex + ";" + this.m_varName + "++) {");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 176 */     for (int i = 0; i < size(); i++) {
/* 177 */       value = value.append((StringBuffer)c.execute_object(n, i, args));
/*     */     }
/* 179 */     value = value.append("}");
/* 180 */     return value;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void execute_void(ProgramChromosome c, int n, Object[] args) {
/* 186 */     if (this.m_endIndex == -1) {
/* 187 */       int x; if (this.m_typeVar == CommandGene.IntegerClass) {
/* 188 */         x = c.execute_int(n, 0, args);
/*     */       }
/* 190 */       else if (this.m_typeVar == CommandGene.LongClass) {
/* 191 */         x = (int)c.execute_long(n, 0, args);
/*     */       }
/* 193 */       else if (this.m_typeVar == CommandGene.DoubleClass) {
/* 194 */         x = (int)Math.round(c.execute_double(n, 0, args));
/*     */       }
/* 196 */       else if (this.m_typeVar == CommandGene.FloatClass) {
/* 197 */         x = Math.round(c.execute_float(n, 0, args));
/*     */       } else {
/*     */         
/* 200 */         throw new RuntimeException("Type " + this.m_typeVar + " not supported by ForLoop");
/*     */       } 
/*     */ 
/*     */       
/* 204 */       if (x > this.m_maxLoop) {
/* 205 */         x = this.m_maxLoop;
/*     */       }
/*     */ 
/*     */       
/* 209 */       for (int i = this.m_startIndex; i < x; i++) {
/* 210 */         c.execute_void(n, 1, args);
/*     */       }
/*     */     } else {
/*     */       int i;
/*     */ 
/*     */       
/* 216 */       for (i = this.m_startIndex; i < this.m_endIndex; i += this.m_increment) {
/*     */ 
/*     */         
/* 219 */         getGPConfiguration().storeInMemory(INTERNAL_COUNTER_STORAGE, new Integer(i));
/*     */         
/* 221 */         c.execute_void(n, 0, args);
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public boolean isValid(ProgramChromosome a_program) {
/* 227 */     return true;
/*     */   }
/*     */   
/*     */   public Class getChildType(IGPProgram a_ind, int a_chromNum) {
/* 231 */     if (this.m_endIndex == -1) {
/*     */       
/* 233 */       if (a_chromNum == 0)
/*     */       {
/*     */         
/* 236 */         return this.m_typeVar;
/*     */       }
/*     */ 
/*     */ 
/*     */       
/* 241 */       return CommandGene.VoidClass;
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 246 */     return CommandGene.VoidClass;
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
/*     */   public int compareTo(Object a_other) {
/* 260 */     if (a_other == null) {
/* 261 */       return 1;
/*     */     }
/*     */     
/* 264 */     ForLoop other = (ForLoop)a_other;
/* 265 */     return (new CompareToBuilder()).append(this.m_typeVar, other.m_typeVar).append(this.m_maxLoop, other.m_maxLoop).toComparison();
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
/* 282 */     if (a_other == null) {
/* 283 */       return false;
/*     */     }
/*     */     
/*     */     try {
/* 287 */       ForLoop other = (ForLoop)a_other;
/* 288 */       return (new EqualsBuilder()).append(this.m_typeVar, other.m_typeVar).append(this.m_maxLoop, other.m_maxLoop).isEquals();
/*     */ 
/*     */     
/*     */     }
/* 292 */     catch (ClassCastException cex) {
/* 293 */       return false;
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
/*     */   public String getCounterMemoryName() {
/* 306 */     return this.m_memory_name_int;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\gp\function\ForLoop.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */