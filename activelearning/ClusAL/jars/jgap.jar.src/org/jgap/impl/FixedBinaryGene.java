/*     */ package org.jgap.impl;
/*     */ 
/*     */ import java.util.StringTokenizer;
/*     */ import org.jgap.BaseGene;
/*     */ import org.jgap.Configuration;
/*     */ import org.jgap.Gene;
/*     */ import org.jgap.IPersistentRepresentation;
/*     */ import org.jgap.InvalidConfigurationException;
/*     */ import org.jgap.RandomGenerator;
/*     */ import org.jgap.UnsupportedRepresentationException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class FixedBinaryGene
/*     */   extends BaseGene
/*     */   implements IPersistentRepresentation
/*     */ {
/*     */   private static final String CVS_REVISION = "$Revision: 1.39 $";
/*     */   private int m_length;
/*     */   private int[] m_value;
/*     */   private static final int WORD_LEN_BITS = 32;
/*     */   
/*     */   public FixedBinaryGene(Configuration a_config, int a_length) throws InvalidConfigurationException {
/*  54 */     super(a_config);
/*  55 */     if (a_length < 1) {
/*  56 */       throw new IllegalArgumentException("Length must be greater than zero!");
/*     */     }
/*  58 */     this.m_length = a_length;
/*  59 */     int bufSize = this.m_length / 32;
/*  60 */     if (0 != this.m_length % 32) {
/*  61 */       bufSize++;
/*     */     }
/*  63 */     this.m_value = new int[bufSize];
/*  64 */     for (int i = 0; i < bufSize; i++) {
/*  65 */       this.m_value[i] = 0;
/*     */     }
/*     */   }
/*     */   
/*     */   protected Gene newGeneInternal() {
/*     */     try {
/*  71 */       FixedBinaryGene result = new FixedBinaryGene(getConfiguration(), this.m_length);
/*  72 */       return (Gene)result;
/*     */     }
/*  74 */     catch (InvalidConfigurationException iex) {
/*  75 */       throw new IllegalStateException(iex.getMessage());
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public FixedBinaryGene(Configuration a_config, FixedBinaryGene a_toCopy) throws InvalidConfigurationException {
/*  82 */     super(a_config);
/*  83 */     this.m_length = a_toCopy.getLength();
/*  84 */     int bufSize = this.m_length / 32;
/*  85 */     if (0 != this.m_length % 32) {
/*  86 */       bufSize++;
/*     */     }
/*  88 */     this.m_value = new int[bufSize];
/*  89 */     System.arraycopy(a_toCopy.getValue(), 0, this.m_value, 0, this.m_value.length);
/*     */   }
/*     */   
/*     */   protected int[] getValue() {
/*  93 */     return this.m_value;
/*     */   }
/*     */   
/*     */   public int getLength() {
/*  97 */     return this.m_length;
/*     */   }
/*     */   
/*     */   public Object clone() {
/*     */     try {
/* 102 */       return new FixedBinaryGene(getConfiguration(), this);
/*     */     }
/* 104 */     catch (InvalidConfigurationException iex) {
/* 105 */       throw new IllegalStateException(iex.getMessage());
/*     */     } 
/*     */   }
/*     */   
/*     */   public void setAllele(Object a_newValue) {
/* 110 */     if (a_newValue == null) {
/* 111 */       throw new IllegalArgumentException("Allele must not be null!");
/*     */     }
/* 113 */     if (((int[])a_newValue).length != getLength()) {
/* 114 */       throw new IllegalArgumentException("Length of allele must be equal to fixed length set (" + getLength() + ")");
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 119 */     if (getConstraintChecker() != null && 
/* 120 */       !getConstraintChecker().verify((Gene)this, a_newValue, null, -1)) {
/*     */       return;
/*     */     }
/*     */     
/* 124 */     int[] bits = (int[])a_newValue;
/* 125 */     for (int i = 0; i < bits.length; i++) {
/* 126 */       setBit(i, bits[i]);
/*     */     }
/*     */   }
/*     */   
/*     */   public Object getAllele() {
/* 131 */     int[] bits = new int[getLength()];
/* 132 */     for (int i = 0; i < getLength(); i++) {
/* 133 */       if (getBit(i)) {
/* 134 */         bits[i] = 1;
/*     */       } else {
/*     */         
/* 137 */         bits[i] = 0;
/*     */       } 
/*     */     } 
/* 140 */     return bits;
/*     */   }
/*     */   
/*     */   public int[] getIntValues() {
/* 144 */     return this.m_value;
/*     */   }
/*     */   
/*     */   public boolean getBit(int a_index) {
/* 148 */     checkIndex(a_index);
/* 149 */     return getUnchecked(a_index);
/*     */   }
/*     */   
/*     */   public void setBit(int a_index, boolean a_value) {
/* 153 */     checkIndex(a_index);
/* 154 */     setUnchecked(a_index, a_value);
/*     */   }
/*     */   
/*     */   public void setBit(int a_index, int a_value) {
/* 158 */     if (a_value > 0) {
/* 159 */       if (a_value != 1) {
/* 160 */         throw new IllegalArgumentException("Only values 0 and 1 are valid!");
/*     */       }
/* 162 */       setBit(a_index, true);
/*     */     } else {
/*     */       
/* 165 */       if (a_value != 0) {
/* 166 */         throw new IllegalArgumentException("Only values 0 and 1 are valid!");
/*     */       }
/* 168 */       setBit(a_index, false);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void setBit(int a_from, int a_to, boolean a_value) {
/* 173 */     checkSubLength(a_from, a_to);
/* 174 */     for (int i = a_from; i < a_to; i++) {
/* 175 */       setUnchecked(i, a_value);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void setBit(int a_from, int a_to, FixedBinaryGene a_values) {
/* 181 */     if (a_values.getLength() == 0) {
/* 182 */       throw new IllegalArgumentException("Length of values must be > 0");
/*     */     }
/* 184 */     checkSubLength(a_from, a_to);
/* 185 */     int iV = 0;
/* 186 */     for (int i = a_from; i <= a_to; i++, iV++) {
/* 187 */       if (iV >= a_values.getLength()) {
/* 188 */         iV = 0;
/*     */       }
/* 190 */       setUnchecked(i, a_values.getUnchecked(iV));
/*     */     } 
/*     */   }
/*     */   
/*     */   public FixedBinaryGene substring(int a_from, int a_to) {
/*     */     try {
/* 196 */       int len = checkSubLength(a_from, a_to);
/* 197 */       FixedBinaryGene substring = new FixedBinaryGene(getConfiguration(), len);
/* 198 */       for (int i = a_from; i <= a_to; i++) {
/* 199 */         substring.setUnchecked(i - a_from, getUnchecked(i));
/*     */       }
/* 201 */       return substring;
/*     */     }
/* 203 */     catch (InvalidConfigurationException iex) {
/* 204 */       throw new IllegalStateException(iex.getMessage());
/*     */     } 
/*     */   }
/*     */   
/*     */   public void flip(int a_index) {
/* 209 */     checkIndex(a_index);
/* 210 */     int segment = a_index / 32;
/* 211 */     int offset = a_index % 32;
/* 212 */     int mask = 1 << 32 - offset - 1;
/* 213 */     this.m_value[segment] = this.m_value[segment] ^ mask;
/*     */   }
/*     */   
/*     */   protected int checkSubLength(int a_from, int a_to) {
/* 217 */     checkIndex(a_from);
/* 218 */     checkIndex(a_to);
/* 219 */     int sublen = a_to - a_from + 1;
/* 220 */     if (0 >= sublen) {
/* 221 */       throw new IllegalArgumentException("must have 'from' <= 'to', but has " + a_from + " > " + a_to);
/*     */     }
/*     */     
/* 224 */     return sublen;
/*     */   }
/*     */   
/*     */   protected void checkIndex(int a_index) {
/* 228 */     if (a_index < 0 || a_index >= getLength()) {
/* 229 */       throw new IndexOutOfBoundsException("index is " + a_index + ", but must be in [0, " + (getLength() - 1) + "]");
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean getUnchecked(int a_index) {
/* 236 */     int segment = a_index / 32;
/* 237 */     int offset = a_index % 32;
/* 238 */     int mask = 1 << 32 - offset - 1;
/* 239 */     return (0 != (this.m_value[segment] & mask));
/*     */   }
/*     */   
/*     */   public void setUnchecked(int a_index, boolean a_value) {
/* 243 */     int segment = a_index / 32;
/* 244 */     int offset = a_index % 32;
/* 245 */     int mask = 1 << 32 - offset - 1;
/* 246 */     if (a_value) {
/* 247 */       this.m_value[segment] = this.m_value[segment] | mask;
/*     */     } else {
/*     */       
/* 250 */       this.m_value[segment] = this.m_value[segment] & (mask ^ 0xFFFFFFFF);
/*     */     } 
/*     */   }
/*     */   
/*     */   public String getPersistentRepresentation() {
/* 255 */     return toString();
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
/*     */   public void setValueFromPersistentRepresentation(String a_representation) throws UnsupportedRepresentationException {
/* 277 */     if (a_representation != null) {
/* 278 */       if (isValidRepresentation(a_representation)) {
/* 279 */         a_representation = a_representation.substring(1, a_representation.length() - 1);
/*     */         
/* 281 */         StringTokenizer st = new StringTokenizer(a_representation, ",");
/* 282 */         int index = 0;
/* 283 */         while (st.hasMoreTokens()) {
/* 284 */           int i = Integer.parseInt(st.nextToken());
/* 285 */           setBit(index++, i);
/*     */         } 
/* 287 */         if (index < getLength()) {
/* 288 */           throw new UnsupportedRepresentationException("Invalid gene representation: " + a_representation);
/*     */         }
/*     */       }
/*     */       else {
/*     */         
/* 293 */         throw new UnsupportedRepresentationException("Invalid gene representation: " + a_representation);
/*     */       }
/*     */     
/*     */     } else {
/*     */       
/* 298 */       throw new UnsupportedRepresentationException("The input parameter must not be null!");
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
/*     */   private boolean isValidRepresentation(String a_representation) {
/* 312 */     if (!a_representation.startsWith("[") || !a_representation.endsWith("]")) {
/* 313 */       return false;
/*     */     }
/* 315 */     return true;
/*     */   }
/*     */   
/*     */   public void setToRandomValue(RandomGenerator a_numberGenerator) {
/* 319 */     if (a_numberGenerator == null) {
/* 320 */       throw new IllegalArgumentException("Random Generator must not be null!");
/*     */     }
/* 322 */     int len = getLength();
/* 323 */     for (int i = 0; i < len; i++) {
/* 324 */       setBit(i, a_numberGenerator.nextBoolean());
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 335 */     int len = getLength();
/* 336 */     String s = "FixedBinaryGene[";
/*     */     
/* 338 */     for (int i = 0; i < len; i++) {
/* 339 */       int value; if (getBit(i)) {
/* 340 */         value = 1;
/*     */       } else {
/*     */         
/* 343 */         value = 0;
/*     */       } 
/* 345 */       if (i == 0) {
/* 346 */         s = s + value;
/*     */       } else {
/*     */         
/* 349 */         s = s + "," + value;
/*     */       } 
/*     */     } 
/* 352 */     return s + "]";
/*     */   }
/*     */ 
/*     */   
/*     */   public String getBusinessKey() {
/* 357 */     return toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int size() {
/* 367 */     return this.m_value.length;
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
/*     */   public void applyMutation(int a_index, double a_percentage) {
/* 381 */     if (a_index < 0 || a_index >= getLength()) {
/* 382 */       throw new IllegalArgumentException("Index must be between 0 and getLength() - 1");
/*     */     }
/*     */     
/* 385 */     if (a_percentage > 0.0D) {
/*     */ 
/*     */       
/* 388 */       if (!getBit(a_index)) {
/* 389 */         setBit(a_index, true);
/*     */       }
/*     */     }
/* 392 */     else if (a_percentage < 0.0D) {
/*     */ 
/*     */       
/* 395 */       if (getBit(a_index)) {
/* 396 */         setBit(a_index, false);
/*     */       }
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
/*     */ 
/*     */   
/*     */   public int compareTo(Object a_other) {
/* 420 */     FixedBinaryGene otherGene = (FixedBinaryGene)a_other;
/*     */ 
/*     */     
/* 423 */     if (otherGene == null) {
/* 424 */       return 1;
/*     */     }
/* 426 */     int thisLen = getLength();
/* 427 */     int otherLen = otherGene.getLength();
/* 428 */     if (thisLen != otherLen) {
/* 429 */       if (thisLen > otherLen) {
/* 430 */         return 1;
/*     */       }
/*     */       
/* 433 */       return -1;
/*     */     } 
/*     */ 
/*     */     
/* 437 */     for (int i = 0; i < thisLen; i++) {
/* 438 */       boolean b1 = getBit(i);
/* 439 */       boolean b2 = otherGene.getBit(i);
/* 440 */       if (b1) {
/* 441 */         if (!b2) {
/* 442 */           return 1;
/*     */         
/*     */         }
/*     */       }
/* 446 */       else if (b2) {
/* 447 */         return -1;
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 453 */     if (isCompareApplicationData()) {
/* 454 */       return compareApplicationData(getApplicationData(), otherGene.getApplicationData());
/*     */     }
/*     */ 
/*     */     
/* 458 */     return 0;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Object getInternalValue() {
/* 468 */     return this.m_value;
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
/*     */   public int hashCode() {
/* 482 */     int result = 0;
/* 483 */     for (int i = 0; i < this.m_value.length; i++) {
/* 484 */       if (this.m_value[i] == 0) {
/* 485 */         result += 31 * result + 47;
/*     */       } else {
/*     */         
/* 488 */         result += 31 * result + 91;
/*     */       } 
/*     */     } 
/* 491 */     return result;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\impl\FixedBinaryGene.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */