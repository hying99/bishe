/*     */ package org.jgap.impl;
/*     */ 
/*     */ import java.util.StringTokenizer;
/*     */ import org.jgap.Configuration;
/*     */ import org.jgap.Gene;
/*     */ import org.jgap.Genotype;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class IntegerGene
/*     */   extends NumberGene
/*     */   implements IPersistentRepresentation
/*     */ {
/*     */   private static final String CVS_REVISION = "$Revision: 1.45 $";
/*     */   protected static final long INTEGER_RANGE = 4294967295L;
/*     */   private int m_upperBounds;
/*     */   private int m_lowerBounds;
/*     */   
/*     */   public IntegerGene() throws InvalidConfigurationException {
/*  62 */     this(Genotype.getStaticConfiguration());
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
/*     */   public IntegerGene(Configuration a_config) throws InvalidConfigurationException {
/*  78 */     this(a_config, -2147483648, 2147483647);
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
/*     */   public IntegerGene(Configuration a_config, int a_lowerBounds, int a_upperBounds) throws InvalidConfigurationException {
/*  98 */     super(a_config);
/*  99 */     this.m_lowerBounds = a_lowerBounds;
/* 100 */     this.m_upperBounds = a_upperBounds;
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
/*     */   protected Gene newGeneInternal() {
/*     */     try {
/* 115 */       IntegerGene result = new IntegerGene(getConfiguration(), this.m_lowerBounds, this.m_upperBounds);
/*     */       
/* 117 */       return (Gene)result;
/* 118 */     } catch (InvalidConfigurationException iex) {
/* 119 */       throw new IllegalStateException(iex.getMessage());
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
/*     */   public String getPersistentRepresentation() {
/*     */     String s;
/* 142 */     if (getInternalValue() == null) {
/* 143 */       s = "null";
/*     */     } else {
/*     */       
/* 146 */       s = getInternalValue().toString();
/*     */     } 
/* 148 */     return s + ":" + this.m_lowerBounds + ":" + this.m_upperBounds;
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setValueFromPersistentRepresentation(String a_representation) throws UnsupportedRepresentationException {
/* 174 */     if (a_representation != null) {
/* 175 */       StringTokenizer tokenizer = new StringTokenizer(a_representation, ":");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 181 */       if (tokenizer.countTokens() != 3) {
/* 182 */         throw new UnsupportedRepresentationException("The format of the given persistent representation  is not recognized: it does not contain three tokens: " + a_representation);
/*     */       }
/*     */ 
/*     */ 
/*     */       
/* 187 */       String valueRepresentation = tokenizer.nextToken();
/* 188 */       String lowerBoundRepresentation = tokenizer.nextToken();
/* 189 */       String upperBoundRepresentation = tokenizer.nextToken();
/*     */ 
/*     */       
/* 192 */       if (valueRepresentation.equals("null")) {
/* 193 */         setAllele((Object)null);
/*     */       } else {
/*     */         
/*     */         try {
/* 197 */           setAllele(new Integer(Integer.parseInt(valueRepresentation)));
/* 198 */         } catch (NumberFormatException e) {
/* 199 */           throw new UnsupportedRepresentationException("The format of the given persistent representation is not recognized: field 1 does not appear to be an integer value.");
/*     */         } 
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*     */       try {
/* 208 */         this.m_lowerBounds = Integer.parseInt(lowerBoundRepresentation);
/*     */       }
/* 210 */       catch (NumberFormatException e) {
/* 211 */         throw new UnsupportedRepresentationException("The format of the given persistent representation is not recognized: field 2 does not appear to be an integer value.");
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*     */       try {
/* 219 */         this.m_upperBounds = Integer.parseInt(upperBoundRepresentation);
/*     */       }
/* 221 */       catch (NumberFormatException e) {
/* 222 */         throw new UnsupportedRepresentationException("The format of the given persistent representation is not recognized: field 3 does not appear to be an integer value.");
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
/*     */   public int intValue() {
/* 240 */     return ((Integer)getAllele()).intValue();
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
/*     */   public void setToRandomValue(RandomGenerator a_numberGenerator) {
/* 257 */     double randomValue = (this.m_upperBounds - this.m_lowerBounds) * a_numberGenerator.nextDouble() + this.m_lowerBounds;
/*     */ 
/*     */     
/* 260 */     setAllele(new Integer((int)Math.round(randomValue)));
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
/*     */   protected int compareToNative(Object a_o1, Object a_o2) {
/* 276 */     return ((Integer)a_o1).compareTo((Integer)a_o2);
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
/*     */   protected void mapValueToWithinBounds() {
/* 293 */     if (getAllele() != null) {
/* 294 */       Integer i_value = (Integer)getAllele();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 301 */       if (i_value.intValue() > this.m_upperBounds || i_value.intValue() < this.m_lowerBounds) {
/*     */         RandomGenerator rn;
/*     */         
/* 304 */         if (getConfiguration() != null) {
/* 305 */           rn = getConfiguration().getRandomGenerator();
/*     */         } else {
/*     */           
/* 308 */           rn = new StockRandomGenerator();
/*     */         } 
/* 310 */         if (this.m_upperBounds - this.m_lowerBounds == 0) {
/* 311 */           setAllele(new Integer(this.m_lowerBounds));
/*     */         } else {
/*     */           
/* 314 */           setAllele(new Integer(rn.nextInt(this.m_upperBounds - this.m_lowerBounds) + this.m_lowerBounds));
/*     */         } 
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
/*     */   public void applyMutation(int a_index, double a_percentage) {
/* 331 */     double range = (this.m_upperBounds - this.m_lowerBounds) * a_percentage;
/* 332 */     if (getAllele() == null) {
/* 333 */       setAllele(new Integer((int)range + this.m_lowerBounds));
/*     */     } else {
/*     */       
/* 336 */       int newValue = (int)Math.round(intValue() + range);
/* 337 */       setAllele(new Integer(newValue));
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
/*     */   public int hashCode() {
/* 350 */     if (getInternalValue() == null) {
/* 351 */       return -1;
/*     */     }
/*     */     
/* 354 */     return super.hashCode();
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
/*     */   public String toString() {
/* 366 */     String s = "IntegerGene(" + this.m_lowerBounds + "," + this.m_upperBounds + ")" + "=";
/*     */     
/* 368 */     if (getInternalValue() == null) {
/* 369 */       s = s + "null";
/*     */     } else {
/*     */       
/* 372 */       s = s + getInternalValue().toString();
/*     */     } 
/* 374 */     return s;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getLowerBounds() {
/* 384 */     return this.m_lowerBounds;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getUpperBounds() {
/* 394 */     return this.m_upperBounds;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\impl\IntegerGene.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */