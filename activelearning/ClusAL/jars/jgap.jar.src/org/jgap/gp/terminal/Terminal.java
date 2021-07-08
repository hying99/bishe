/*     */ package org.jgap.gp.terminal;
/*     */ 
/*     */ import org.jgap.InvalidConfigurationException;
/*     */ import org.jgap.RandomGenerator;
/*     */ import org.jgap.gp.CommandGene;
/*     */ import org.jgap.gp.IGPProgram;
/*     */ import org.jgap.gp.IMutateable;
/*     */ import org.jgap.gp.impl.GPConfiguration;
/*     */ import org.jgap.gp.impl.GPGenotype;
/*     */ import org.jgap.gp.impl.ProgramChromosome;
/*     */ import org.jgap.util.CloneException;
/*     */ import org.jgap.util.ICloneable;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Terminal
/*     */   extends CommandGene
/*     */   implements IMutateable, ICloneable
/*     */ {
/*     */   private static final String CVS_REVISION = "$Revision: 1.14 $";
/*     */   private float m_value_float;
/*     */   private double m_value_double;
/*     */   private int m_value_int;
/*     */   private long m_value_long;
/*     */   private double m_lowerBounds;
/*     */   private double m_upperBounds;
/*     */   private boolean m_wholeNumbers;
/*     */   
/*     */   public Terminal() throws InvalidConfigurationException {
/*  44 */     this(GPGenotype.getStaticGPConfiguration(), CommandGene.IntegerClass);
/*     */   }
/*     */ 
/*     */   
/*     */   public Terminal(GPConfiguration a_conf, Class a_returnType) throws InvalidConfigurationException {
/*  49 */     this(a_conf, a_returnType, 0.0D, 99.0D, false);
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
/*     */   public Terminal(GPConfiguration a_conf, Class a_returnType, double a_minValue, double a_maxValue) throws InvalidConfigurationException {
/*  67 */     this(a_conf, a_returnType, a_minValue, a_maxValue, false);
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
/*     */   public Terminal(GPConfiguration a_conf, Class a_returnType, double a_minValue, double a_maxValue, boolean a_wholeNumbers) throws InvalidConfigurationException {
/*  87 */     this(a_conf, a_returnType, a_minValue, a_maxValue, a_wholeNumbers, 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Terminal(GPConfiguration a_conf, Class a_returnType, double a_minValue, double a_maxValue, boolean a_wholeNumbers, int a_subReturnType) throws InvalidConfigurationException {
/*  94 */     super(a_conf, 0, a_returnType, a_subReturnType, null);
/*  95 */     this.m_lowerBounds = a_minValue;
/*  96 */     this.m_upperBounds = a_maxValue;
/*  97 */     this.m_wholeNumbers = a_wholeNumbers;
/*  98 */     setRandomValue();
/*     */   }
/*     */   
/*     */   protected void setRandomValue(int a_value) {
/* 102 */     RandomGenerator randomGen = getGPConfiguration().getRandomGenerator();
/* 103 */     this.m_value_int = (int)Math.round(randomGen.nextDouble() * (this.m_upperBounds - this.m_lowerBounds) + this.m_lowerBounds);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected void setRandomValue(long a_value) {
/* 109 */     RandomGenerator randomGen = getGPConfiguration().getRandomGenerator();
/* 110 */     this.m_value_long = Math.round(randomGen.nextDouble() * (this.m_upperBounds - this.m_lowerBounds) + this.m_lowerBounds);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected void setRandomValue(double a_value) {
/* 116 */     RandomGenerator randomGen = getGPConfiguration().getRandomGenerator();
/* 117 */     this.m_value_double = randomGen.nextDouble() * (this.m_upperBounds - this.m_lowerBounds) + this.m_lowerBounds;
/*     */     
/* 119 */     if (this.m_wholeNumbers) {
/* 120 */       this.m_value_double = Math.round(this.m_value_double);
/*     */     }
/*     */   }
/*     */   
/*     */   protected void setRandomValue(float a_value) {
/* 125 */     RandomGenerator randomGen = getGPConfiguration().getRandomGenerator();
/* 126 */     this.m_value_float = (float)(randomGen.nextFloat() * (this.m_upperBounds - this.m_lowerBounds) + this.m_lowerBounds);
/*     */ 
/*     */     
/* 129 */     if (this.m_wholeNumbers) {
/* 130 */       this.m_value_float = Math.round(this.m_value_float);
/*     */     }
/*     */   }
/*     */   
/*     */   protected void setRandomValue() {
/* 135 */     Class retType = getReturnType();
/* 136 */     if (retType == CommandGene.FloatClass) {
/* 137 */       setRandomValue(this.m_value_float);
/*     */     }
/* 139 */     else if (retType == CommandGene.IntegerClass) {
/* 140 */       setRandomValue(this.m_value_int);
/*     */     }
/* 142 */     else if (retType == CommandGene.LongClass) {
/* 143 */       setRandomValue(this.m_value_long);
/*     */     }
/* 145 */     else if (retType == CommandGene.DoubleClass) {
/* 146 */       setRandomValue(this.m_value_double);
/*     */     } else {
/*     */       
/* 149 */       throw new RuntimeException("unknown terminal type: " + retType);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void setValue(double a_value) {
/* 154 */     if (this.m_wholeNumbers) {
/* 155 */       this.m_value_double = Math.round(a_value);
/*     */     } else {
/*     */       
/* 158 */       this.m_value_double = a_value;
/*     */     } 
/*     */   }
/*     */   
/*     */   public void setValue(float a_value) {
/* 163 */     if (this.m_wholeNumbers) {
/* 164 */       this.m_value_float = Math.round(a_value);
/*     */     } else {
/*     */       
/* 167 */       this.m_value_float = a_value;
/*     */     } 
/*     */   }
/*     */   
/*     */   public void setValue(int a_value) {
/* 172 */     this.m_value_int = a_value;
/*     */   }
/*     */   
/*     */   public void setValue(long a_value) {
/* 176 */     this.m_value_long = a_value;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CommandGene applyMutation(int index, double a_percentage) throws InvalidConfigurationException {
/* 184 */     if (a_percentage > 0.85D) {
/* 185 */       setRandomValue();
/*     */     } else {
/*     */       
/* 188 */       Class retType = getReturnType();
/* 189 */       if (retType == CommandGene.FloatClass) {
/*     */         
/* 191 */         float newValuef, rangef = ((float)this.m_upperBounds - (float)this.m_lowerBounds) * (float)a_percentage;
/*     */         
/* 193 */         if (this.m_value_float >= (this.m_upperBounds - this.m_lowerBounds) / 2.0D) {
/* 194 */           newValuef = this.m_value_float - getGPConfiguration().getRandomGenerator().nextFloat() * rangef;
/*     */         }
/*     */         else {
/*     */           
/* 198 */           newValuef = this.m_value_float + getGPConfiguration().getRandomGenerator().nextFloat() * rangef;
/*     */         } 
/*     */ 
/*     */ 
/*     */         
/* 203 */         if (Math.abs(newValuef - this.m_lowerBounds) < 1.0E-7D || Math.abs(this.m_upperBounds - newValuef) < 1.0E-7D) {
/*     */           
/* 205 */           setRandomValue(this.m_value_float);
/*     */         } else {
/*     */           
/* 208 */           setValue(newValuef);
/*     */         }
/*     */       
/* 211 */       } else if (retType == CommandGene.DoubleClass) {
/*     */         
/* 213 */         double newValueD, rangeD = (this.m_upperBounds - this.m_lowerBounds) * a_percentage;
/* 214 */         if (this.m_value_double >= (this.m_upperBounds - this.m_lowerBounds) / 2.0D) {
/* 215 */           newValueD = this.m_value_double - getGPConfiguration().getRandomGenerator().nextFloat() * rangeD;
/*     */         }
/*     */         else {
/*     */           
/* 219 */           newValueD = this.m_value_double + getGPConfiguration().getRandomGenerator().nextFloat() * rangeD;
/*     */         } 
/*     */ 
/*     */ 
/*     */         
/* 224 */         if (Math.abs(newValueD - this.m_lowerBounds) < 1.0E-7D || Math.abs(this.m_upperBounds - newValueD) < 1.0E-7D) {
/*     */           
/* 226 */           setRandomValue(this.m_value_double);
/*     */         } else {
/*     */           
/* 229 */           setValue(newValueD);
/*     */         }
/*     */       
/* 232 */       } else if (retType == CommandGene.IntegerClass) {
/*     */         int newValueI;
/* 234 */         double range = (this.m_upperBounds - this.m_lowerBounds) * a_percentage;
/* 235 */         if (this.m_value_int >= (this.m_upperBounds - this.m_lowerBounds) / 2.0D) {
/* 236 */           newValueI = this.m_value_int - (int)Math.round(getGPConfiguration().getRandomGenerator().nextInt() * range);
/*     */         
/*     */         }
/*     */         else {
/*     */           
/* 241 */           newValueI = this.m_value_int + (int)Math.round(getGPConfiguration().getRandomGenerator().nextFloat() * range);
/*     */         } 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 247 */         if (newValueI < this.m_lowerBounds || newValueI > this.m_upperBounds) {
/* 248 */           setRandomValue(this.m_value_int);
/*     */         } else {
/*     */           
/* 251 */           setValue(newValueI);
/*     */         }
/*     */       
/* 254 */       } else if (retType == CommandGene.LongClass) {
/*     */         long newValueL;
/* 256 */         double range = (this.m_upperBounds - this.m_lowerBounds) * a_percentage;
/* 257 */         if (this.m_value_long >= (this.m_upperBounds - this.m_lowerBounds) / 2.0D) {
/* 258 */           newValueL = this.m_value_long - Math.round(getGPConfiguration().getRandomGenerator().nextInt() * range);
/*     */         
/*     */         }
/*     */         else {
/*     */           
/* 263 */           newValueL = this.m_value_long + Math.round(getGPConfiguration().getRandomGenerator().nextFloat() * range);
/*     */         } 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 269 */         if (newValueL < this.m_lowerBounds || newValueL > this.m_upperBounds) {
/* 270 */           setRandomValue(this.m_value_long);
/*     */         } else {
/*     */           
/* 273 */           setValue(newValueL);
/*     */         } 
/*     */       } 
/*     */     } 
/* 277 */     return this;
/*     */   }
/*     */   
/*     */   public String toString() {
/* 281 */     Class retType = getReturnType();
/* 282 */     if (retType == CommandGene.FloatClass) {
/* 283 */       return "" + this.m_value_float;
/*     */     }
/* 285 */     if (retType == CommandGene.IntegerClass) {
/* 286 */       return "" + this.m_value_int;
/*     */     }
/* 288 */     if (retType == CommandGene.LongClass) {
/* 289 */       return "" + this.m_value_long;
/*     */     }
/* 291 */     if (retType == CommandGene.DoubleClass) {
/* 292 */       return "" + this.m_value_double;
/*     */     }
/*     */     
/* 295 */     return "unknown terminal type: " + retType;
/*     */   }
/*     */ 
/*     */   
/*     */   public int execute_int(ProgramChromosome c, int n, Object[] args) {
/* 300 */     return this.m_value_int;
/*     */   }
/*     */   
/*     */   public long execute_long(ProgramChromosome c, int n, Object[] args) {
/* 304 */     return this.m_value_long;
/*     */   }
/*     */   
/*     */   public float execute_float(ProgramChromosome c, int n, Object[] args) {
/* 308 */     return this.m_value_float;
/*     */   }
/*     */   
/*     */   public double execute_double(ProgramChromosome c, int n, Object[] args) {
/* 312 */     return this.m_value_double;
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
/*     */   public Object execute_object(ProgramChromosome c, int n, Object[] args) {
/* 327 */     StringBuffer value = new StringBuffer("(");
/* 328 */     Class retType = getReturnType();
/* 329 */     if (retType == CommandGene.FloatClass) {
/* 330 */       value.append(this.m_value_float).append("f");
/*     */     }
/* 332 */     else if (retType == CommandGene.DoubleClass) {
/* 333 */       value.append(this.m_value_double).append("d");
/*     */     }
/* 335 */     else if (retType == CommandGene.IntegerClass) {
/* 336 */       value.append(this.m_value_int);
/*     */     }
/* 338 */     else if (retType == CommandGene.LongClass) {
/* 339 */       value.append(this.m_value_long).append("l");
/*     */     } 
/* 341 */     value.append(")");
/* 342 */     return value;
/*     */   }
/*     */   
/*     */   public Class getChildType(IGPProgram a_ind, int a_chromNum) {
/* 346 */     return null;
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
/*     */   public Object clone() {
/*     */     try {
/* 359 */       Terminal result = new Terminal(getGPConfiguration(), getReturnType(), this.m_lowerBounds, this.m_upperBounds);
/*     */       
/* 361 */       result.m_value_double = this.m_value_double;
/* 362 */       result.m_value_float = this.m_value_float;
/* 363 */       result.m_value_int = this.m_value_int;
/* 364 */       result.m_value_long = this.m_value_long;
/* 365 */       result.m_wholeNumbers = this.m_wholeNumbers;
/* 366 */       return result;
/* 367 */     } catch (Exception ex) {
/* 368 */       throw new CloneException(ex);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\gp\terminal\Terminal.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */