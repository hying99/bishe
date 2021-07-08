/*     */ package org.jgap.impl;
/*     */ 
/*     */ import org.jgap.BaseGene;
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
/*     */ public class BooleanGene
/*     */   extends BaseGene
/*     */   implements IPersistentRepresentation
/*     */ {
/*     */   private static final String CVS_REVISION = "$Revision: 1.31 $";
/*  39 */   protected static final Boolean TRUE_BOOLEAN = Boolean.valueOf(true);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  46 */   protected static final Boolean FALSE_BOOLEAN = Boolean.valueOf(false);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Boolean m_value;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BooleanGene() throws InvalidConfigurationException {
/*  64 */     this(Genotype.getStaticConfiguration());
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
/*     */   public BooleanGene(Configuration a_config) throws InvalidConfigurationException {
/*  76 */     super(a_config);
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
/*     */   public BooleanGene(Configuration a_config, boolean a_value) throws InvalidConfigurationException {
/*  89 */     super(a_config);
/*  90 */     this.m_value = new Boolean(a_value);
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
/*     */   public BooleanGene(Configuration a_config, Boolean a_value) throws InvalidConfigurationException {
/* 103 */     super(a_config);
/* 104 */     if (a_value == null) {
/* 105 */       throw new IllegalArgumentException("Allele value may not be null. Use no argument constructor if you need to set allele to null initially.");
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 111 */     this.m_value = a_value;
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
/*     */   protected Gene newGeneInternal() {
/*     */     try {
/* 127 */       return (Gene)new BooleanGene(getConfiguration());
/*     */     }
/* 129 */     catch (InvalidConfigurationException iex) {
/* 130 */       throw new IllegalStateException(iex.getMessage());
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setAllele(Object a_newValue) {
/* 141 */     this.m_value = (Boolean)a_newValue;
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
/*     */   public String getPersistentRepresentation() {
/*     */     String s;
/* 160 */     if (getInternalValue() == null) {
/* 161 */       s = "null";
/*     */     } else {
/*     */       
/* 164 */       s = getInternalValue().toString();
/*     */     } 
/* 166 */     return s;
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
/*     */   public void setValueFromPersistentRepresentation(String a_representation) throws UnsupportedRepresentationException {
/* 191 */     if (a_representation != null) {
/* 192 */       if (a_representation.equals("null")) {
/* 193 */         this.m_value = null;
/*     */       }
/* 195 */       else if (a_representation.equals("true")) {
/* 196 */         this.m_value = TRUE_BOOLEAN;
/*     */       }
/* 198 */       else if (a_representation.equals("false")) {
/* 199 */         this.m_value = FALSE_BOOLEAN;
/*     */       } else {
/*     */         
/* 202 */         throw new UnsupportedRepresentationException("Unknown boolean gene representation: " + a_representation);
/*     */       }
/*     */     
/*     */     }
/*     */     else {
/*     */       
/* 208 */       throw new UnsupportedRepresentationException("The input parameter must not be null!");
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
/*     */   public boolean booleanValue() {
/* 220 */     return this.m_value.booleanValue();
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
/*     */   public void setToRandomValue(RandomGenerator a_numberGenerator) {
/* 240 */     if (a_numberGenerator.nextBoolean() == true) {
/* 241 */       this.m_value = TRUE_BOOLEAN;
/*     */     } else {
/*     */       
/* 244 */       this.m_value = FALSE_BOOLEAN;
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
/*     */   public int compareTo(Object a_other) {
/* 265 */     BooleanGene otherBooleanGene = (BooleanGene)a_other;
/*     */ 
/*     */     
/* 268 */     if (otherBooleanGene == null) {
/* 269 */       return 1;
/*     */     }
/* 271 */     if (otherBooleanGene.m_value == null) {
/*     */ 
/*     */ 
/*     */       
/* 275 */       if (this.m_value != null) {
/* 276 */         return 1;
/*     */       }
/*     */       
/* 279 */       if (isCompareApplicationData()) {
/* 280 */         return compareApplicationData(getApplicationData(), otherBooleanGene.getApplicationData());
/*     */       }
/*     */ 
/*     */       
/* 284 */       return 0;
/*     */     } 
/*     */ 
/*     */     
/* 288 */     if (this.m_value == null) {
/* 289 */       if (otherBooleanGene.m_value == null) {
/* 290 */         if (isCompareApplicationData()) {
/* 291 */           return compareApplicationData(getApplicationData(), otherBooleanGene.getApplicationData());
/*     */         }
/*     */ 
/*     */         
/* 295 */         return 0;
/*     */       } 
/*     */ 
/*     */       
/* 299 */       return -1;
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 305 */     if (!this.m_value.booleanValue()) {
/* 306 */       if (!otherBooleanGene.m_value.booleanValue()) {
/*     */ 
/*     */         
/* 309 */         if (isCompareApplicationData()) {
/* 310 */           return compareApplicationData(getApplicationData(), otherBooleanGene.getApplicationData());
/*     */         }
/*     */ 
/*     */         
/* 314 */         return 0;
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 321 */       return -1;
/*     */     } 
/*     */     
/* 324 */     if (otherBooleanGene.m_value.booleanValue() == true) {
/*     */ 
/*     */       
/* 327 */       if (isCompareApplicationData()) {
/* 328 */         return compareApplicationData(getApplicationData(), otherBooleanGene.getApplicationData());
/*     */       }
/*     */ 
/*     */       
/* 332 */       return 0;
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 339 */     return 1;
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
/*     */   public void applyMutation(int a_index, double a_percentage) {
/* 354 */     if (this.m_value == null) {
/* 355 */       this.m_value = Boolean.valueOf(false);
/*     */     }
/* 357 */     else if (a_percentage > 0.0D) {
/*     */ 
/*     */       
/* 360 */       if (!this.m_value.booleanValue()) {
/* 361 */         this.m_value = Boolean.valueOf(true);
/*     */       }
/*     */     }
/* 364 */     else if (a_percentage < 0.0D) {
/*     */ 
/*     */       
/* 367 */       if (this.m_value.booleanValue()) {
/* 368 */         this.m_value = Boolean.valueOf(false);
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   protected Object getInternalValue() {
/* 374 */     return this.m_value;
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
/* 386 */     if (getInternalValue() == null) {
/* 387 */       return -2;
/*     */     }
/*     */     
/* 390 */     return super.hashCode();
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
/* 402 */     String s = "BooleanGene=";
/*     */     
/* 404 */     if (getInternalValue() == null) {
/* 405 */       s = s + "null";
/*     */     } else {
/*     */       
/* 408 */       s = s + getInternalValue().toString();
/*     */     } 
/* 410 */     return s;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\impl\BooleanGene.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */