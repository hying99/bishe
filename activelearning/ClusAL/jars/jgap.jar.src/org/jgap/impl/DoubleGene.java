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
/*     */ public class DoubleGene
/*     */   extends NumberGene
/*     */   implements IPersistentRepresentation
/*     */ {
/*     */   private static final String CVS_REVISION = "$Revision: 1.39 $";
/*     */   private double m_upperBound;
/*     */   private double m_lowerBound;
/*     */   
/*     */   public DoubleGene() throws InvalidConfigurationException {
/*  56 */     this(Genotype.getStaticConfiguration());
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
/*     */   public DoubleGene(Configuration a_config) throws InvalidConfigurationException {
/*  72 */     this(a_config, -8.988465674311579E307D, 8.988465674311579E307D);
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
/*     */   public DoubleGene(Configuration a_config, double a_lowerBound, double a_upperBound) throws InvalidConfigurationException {
/*  93 */     super(a_config);
/*  94 */     this.m_lowerBound = a_lowerBound;
/*  95 */     this.m_upperBound = a_upperBound;
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
/* 110 */       DoubleGene result = new DoubleGene(getConfiguration(), this.m_lowerBound, this.m_upperBound);
/*     */       
/* 112 */       return (Gene)result;
/*     */     }
/* 114 */     catch (InvalidConfigurationException iex) {
/* 115 */       throw new IllegalStateException(iex.getMessage());
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
/* 138 */     if (getInternalValue() == null) {
/* 139 */       s = "null";
/*     */     } else {
/*     */       
/* 142 */       s = getInternalValue().toString();
/*     */     } 
/* 144 */     return s + ":" + this.m_lowerBound + ":" + this.m_upperBound;
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
/* 170 */     if (a_representation != null) {
/* 171 */       StringTokenizer tokenizer = new StringTokenizer(a_representation, ":");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 177 */       if (tokenizer.countTokens() != 3) {
/* 178 */         throw new UnsupportedRepresentationException("The format of the given persistent representation  is not recognized: it does not contain three tokens: " + a_representation);
/*     */       }
/*     */ 
/*     */ 
/*     */       
/* 183 */       String valueRepresentation = tokenizer.nextToken();
/* 184 */       String lowerBoundRepresentation = tokenizer.nextToken();
/* 185 */       String upperBoundRepresentation = tokenizer.nextToken();
/*     */ 
/*     */       
/* 188 */       if (valueRepresentation.equals("null")) {
/* 189 */         setAllele((Object)null);
/*     */       } else {
/*     */         
/*     */         try {
/* 193 */           setAllele(new Double(Double.parseDouble(valueRepresentation)));
/*     */         }
/* 195 */         catch (NumberFormatException e) {
/* 196 */           throw new UnsupportedRepresentationException("The format of the given persistent representation is not recognized: field 1 does not appear to be a double value.");
/*     */         } 
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*     */       try {
/* 205 */         this.m_lowerBound = Double.parseDouble(lowerBoundRepresentation);
/*     */       
/*     */       }
/* 208 */       catch (NumberFormatException e) {
/* 209 */         throw new UnsupportedRepresentationException("The format of the given persistent representation is not recognized: field 2 does not appear to be a double value.");
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*     */       try {
/* 217 */         this.m_upperBound = Double.parseDouble(upperBoundRepresentation);
/*     */       
/*     */       }
/* 220 */       catch (NumberFormatException e) {
/* 221 */         throw new UnsupportedRepresentationException("The format of the given persistent representation is not recognized: field 3 does not appear to be a double value.");
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
/*     */   public double doubleValue() {
/* 237 */     return ((Double)getAllele()).doubleValue();
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
/*     */   public void setToRandomValue(RandomGenerator a_numberGenerator) {
/* 255 */     setAllele(new Double((this.m_upperBound - this.m_lowerBound) * a_numberGenerator.nextDouble() + this.m_lowerBound));
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
/*     */   protected int compareToNative(Object o1, Object o2) {
/* 272 */     return ((Double)o1).compareTo((Double)o2);
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
/* 289 */     if (getAllele() != null) {
/* 290 */       Double d_value = (Double)getAllele();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 297 */       if (d_value.doubleValue() > this.m_upperBound || d_value.doubleValue() < this.m_lowerBound) {
/*     */         RandomGenerator rn;
/*     */         
/* 300 */         if (getConfiguration() != null) {
/* 301 */           rn = getConfiguration().getRandomGenerator();
/*     */         } else {
/*     */           
/* 304 */           rn = new StockRandomGenerator();
/*     */         } 
/* 306 */         setAllele(new Double(rn.nextDouble() * (this.m_upperBound - this.m_lowerBound) + this.m_lowerBound));
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
/*     */   public void applyMutation(int index, double a_percentage) {
/* 322 */     double range = (this.m_upperBound - this.m_lowerBound) * a_percentage;
/* 323 */     double newValue = doubleValue() + range;
/* 324 */     setAllele(new Double(newValue));
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
/* 336 */     if (getInternalValue() == null) {
/* 337 */       return -3;
/*     */     }
/*     */     
/* 340 */     return super.hashCode();
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
/* 352 */     String s = "DoubleGene(" + this.m_lowerBound + "," + this.m_upperBound + ")" + "=";
/*     */     
/* 354 */     if (getInternalValue() == null) {
/* 355 */       s = s + "null";
/*     */     } else {
/*     */       
/* 358 */       s = s + getInternalValue().toString();
/*     */     } 
/* 360 */     return s;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getLowerBound() {
/* 369 */     return this.m_lowerBound;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getUpperBound() {
/* 378 */     return this.m_upperBound;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\impl\DoubleGene.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */