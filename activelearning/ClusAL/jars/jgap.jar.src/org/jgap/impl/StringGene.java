/*     */ package org.jgap.impl;
/*     */ 
/*     */ import java.util.StringTokenizer;
/*     */ import org.jgap.BaseGene;
/*     */ import org.jgap.Configuration;
/*     */ import org.jgap.Gene;
/*     */ import org.jgap.Genotype;
/*     */ import org.jgap.IBusinessKey;
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
/*     */ public class StringGene
/*     */   extends BaseGene
/*     */   implements IPersistentRepresentation, IBusinessKey
/*     */ {
/*     */   public static final String ALPHABET_CHARACTERS_UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
/*     */   public static final String ALPHABET_CHARACTERS_LOWER = "abcdefghijklmnopqrstuvwxyz";
/*     */   public static final String ALPHABET_CHARACTERS_DIGITS = "0123456789";
/*     */   public static final String ALPHABET_CHARACTERS_SPECIAL = "+.*/\\,;@";
/*     */   private static final String CVS_REVISION = "$Revision: 1.58 $";
/*     */   private int m_minLength;
/*     */   private int m_maxLength;
/*     */   private String m_alphabet;
/*     */   private String m_value;
/*     */   
/*     */   public StringGene() throws InvalidConfigurationException {
/*  68 */     this(Genotype.getStaticConfiguration());
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
/*     */   public StringGene(Configuration a_config) throws InvalidConfigurationException {
/*  85 */     this(a_config, 0, 0);
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
/*     */   public StringGene(Configuration a_config, int a_minLength, int a_maxLength) throws InvalidConfigurationException {
/* 105 */     this(a_config, a_minLength, a_maxLength, (String)null);
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
/*     */   public StringGene(Configuration a_config, int a_minLength, int a_maxLength, String a_alphabet) throws InvalidConfigurationException {
/* 125 */     super(a_config);
/* 126 */     if (a_minLength < 0) {
/* 127 */       throw new IllegalArgumentException("minimum length must be greater than zero!");
/*     */     }
/*     */ 
/*     */     
/* 131 */     if (a_maxLength < a_minLength) {
/* 132 */       throw new IllegalArgumentException("minimum length must be smaller than or equal to maximum length!");
/*     */     }
/*     */ 
/*     */     
/* 136 */     this.m_minLength = a_minLength;
/* 137 */     this.m_maxLength = a_maxLength;
/* 138 */     setAlphabet(a_alphabet);
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
/*     */   public void setToRandomValue(RandomGenerator a_numberGenerator) {
/* 154 */     if (this.m_alphabet == null || this.m_alphabet.length() < 1) {
/* 155 */       throw new IllegalStateException("The valid alphabet is empty!");
/*     */     }
/* 157 */     if (this.m_maxLength < this.m_minLength || this.m_maxLength < 1) {
/* 158 */       throw new IllegalStateException("Illegal valid maximum and/or minimum length of alphabet!");
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 167 */     int length = this.m_maxLength - this.m_minLength + 1;
/* 168 */     int i = a_numberGenerator.nextInt() % length;
/* 169 */     if (i < 0) {
/* 170 */       i = -i;
/*     */     }
/* 172 */     length = this.m_minLength + i;
/*     */ 
/*     */ 
/*     */     
/* 176 */     String newAllele = "";
/* 177 */     int alphabetLength = this.m_alphabet.length();
/* 178 */     for (int j = 0; j < length; j++) {
/* 179 */       int index = a_numberGenerator.nextInt(alphabetLength);
/* 180 */       char value = this.m_alphabet.charAt(index);
/* 181 */       newAllele = newAllele + value;
/*     */     } 
/*     */ 
/*     */     
/* 185 */     setAllele(newAllele);
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
/*     */   public void setValueFromPersistentRepresentation(String a_representation) throws UnsupportedRepresentationException {
/* 208 */     if (a_representation != null) {
/* 209 */       String tempValue; StringTokenizer tokenizer = new StringTokenizer(a_representation, ":");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 215 */       if (tokenizer.countTokens() != 4) {
/* 216 */         throw new UnsupportedRepresentationException("The format of the given persistent representation '" + a_representation + "'" + " is not recognized: it does not contain four tokens.");
/*     */       }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 225 */       String valueRepresentation = decode(tokenizer.nextToken());
/* 226 */       String minLengthRepresentation = tokenizer.nextToken();
/* 227 */       String maxLengthRepresentation = tokenizer.nextToken();
/* 228 */       String alphabetRepresentation = decode(tokenizer.nextToken());
/*     */ 
/*     */       
/*     */       try {
/* 232 */         this.m_minLength = Integer.parseInt(minLengthRepresentation);
/* 233 */       } catch (NumberFormatException e) {
/* 234 */         throw new UnsupportedRepresentationException("The format of the given persistent representation is not recognized: field 2 does not appear to be an integer value.");
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*     */       try {
/* 242 */         this.m_maxLength = Integer.parseInt(maxLengthRepresentation);
/* 243 */       } catch (NumberFormatException e) {
/* 244 */         throw new UnsupportedRepresentationException("The format of the given persistent representation is not recognized: field 3 does not appear to be an integer value.");
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 252 */       if (valueRepresentation.equals("null")) {
/* 253 */         tempValue = null;
/*     */       
/*     */       }
/* 256 */       else if (valueRepresentation.equals("\"\"")) {
/* 257 */         tempValue = "";
/*     */       } else {
/*     */         
/* 260 */         tempValue = valueRepresentation;
/*     */       } 
/*     */ 
/*     */ 
/*     */       
/* 265 */       if (tempValue != null) {
/* 266 */         if (this.m_minLength > tempValue.length()) {
/* 267 */           throw new UnsupportedRepresentationException("The value given is shorter than the allowed maximum length.");
/*     */         }
/*     */ 
/*     */         
/* 271 */         if (this.m_maxLength < tempValue.length()) {
/* 272 */           throw new UnsupportedRepresentationException("The value given is longer than the allowed maximum length.");
/*     */         }
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 279 */       if (!isValidAlphabet(tempValue, alphabetRepresentation)) {
/* 280 */         throw new UnsupportedRepresentationException("The value given contains invalid characters.");
/*     */       }
/*     */       
/* 283 */       this.m_value = tempValue;
/*     */ 
/*     */       
/* 286 */       this.m_alphabet = alphabetRepresentation;
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
/*     */ 
/*     */   
/*     */   public String getPersistentRepresentation() throws UnsupportedOperationException {
/*     */     String s;
/* 312 */     if (this.m_value == null) {
/* 313 */       s = "null";
/*     */     
/*     */     }
/* 316 */     else if (this.m_value.equals("")) {
/* 317 */       s = "\"\"";
/*     */     } else {
/*     */       
/* 320 */       s = this.m_value;
/*     */     } 
/*     */     
/* 323 */     return encode("" + s) + ":" + this.m_minLength + ":" + this.m_maxLength + ":" + encode("" + this.m_alphabet);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getBusinessKey() {
/* 332 */     return this.m_value + ":" + this.m_minLength + ":" + this.m_maxLength;
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
/*     */   public void setAllele(Object a_newValue) {
/* 348 */     if (a_newValue != null) {
/* 349 */       String temp = (String)a_newValue;
/* 350 */       if (temp.length() < this.m_minLength || temp.length() > this.m_maxLength)
/*     */       {
/* 352 */         throw new IllegalArgumentException("The given value is too short or too long!");
/*     */       }
/*     */ 
/*     */ 
/*     */       
/* 357 */       if (!isValidAlphabet(temp, this.m_alphabet)) {
/* 358 */         throw new IllegalArgumentException("The given value contains at least one invalid character.");
/*     */       }
/*     */       
/* 361 */       if (getConstraintChecker() != null && 
/* 362 */         !getConstraintChecker().verify((Gene)this, a_newValue, null, -1)) {
/*     */         return;
/*     */       }
/*     */       
/* 366 */       this.m_value = temp;
/*     */     } else {
/*     */       
/* 369 */       this.m_value = null;
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
/*     */   protected Gene newGeneInternal() {
/*     */     try {
/* 385 */       StringGene result = new StringGene(getConfiguration(), this.m_minLength, this.m_maxLength, this.m_alphabet);
/*     */       
/* 387 */       result.setConstraintChecker(getConstraintChecker());
/* 388 */       return (Gene)result;
/* 389 */     } catch (InvalidConfigurationException iex) {
/* 390 */       throw new IllegalStateException(iex.getMessage());
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
/*     */   public int compareTo(Object a_other) {
/* 410 */     StringGene otherStringGene = (StringGene)a_other;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 415 */     if (otherStringGene == null) {
/* 416 */       return 1;
/*     */     }
/* 418 */     if (otherStringGene.m_value == null) {
/*     */ 
/*     */ 
/*     */       
/* 422 */       if (this.m_value == null) {
/* 423 */         if (isCompareApplicationData()) {
/* 424 */           return compareApplicationData(getApplicationData(), otherStringGene.getApplicationData());
/*     */         }
/*     */ 
/*     */         
/* 428 */         return 0;
/*     */       } 
/*     */ 
/*     */       
/* 432 */       return 1;
/*     */     } 
/*     */ 
/*     */     
/* 436 */     int res = this.m_value.compareTo(otherStringGene.m_value);
/* 437 */     if (res == 0) {
/* 438 */       if (isCompareApplicationData()) {
/* 439 */         return compareApplicationData(getApplicationData(), otherStringGene.getApplicationData());
/*     */       }
/*     */ 
/*     */       
/* 443 */       return 0;
/*     */     } 
/*     */ 
/*     */     
/* 447 */     return res;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int size() {
/* 453 */     return this.m_value.length();
/*     */   }
/*     */   
/*     */   public int getMaxLength() {
/* 457 */     return this.m_maxLength;
/*     */   }
/*     */   
/*     */   public int getMinLength() {
/* 461 */     return this.m_minLength;
/*     */   }
/*     */   
/*     */   public void setMinLength(int m_minLength) {
/* 465 */     this.m_minLength = m_minLength;
/*     */   }
/*     */   
/*     */   public void setMaxLength(int m_maxLength) {
/* 469 */     this.m_maxLength = m_maxLength;
/*     */   }
/*     */   
/*     */   public String getAlphabet() {
/* 473 */     return this.m_alphabet;
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
/*     */   public void setAlphabet(String a_alphabet) {
/* 486 */     this.m_alphabet = a_alphabet;
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
/*     */   public String toString() {
/* 499 */     String s = "StringGene=";
/* 500 */     if (this.m_value == null) {
/* 501 */       s = s + "null";
/*     */     
/*     */     }
/* 504 */     else if (this.m_value.equals("")) {
/* 505 */       s = s + "\"\"";
/*     */     } else {
/*     */       
/* 508 */       s = s + this.m_value;
/*     */     } 
/*     */     
/* 511 */     return s;
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
/*     */   public String stringValue() {
/* 523 */     return this.m_value;
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
/*     */   private boolean isValidAlphabet(String a_value, String a_alphabet) {
/* 536 */     if (a_value == null || a_value.length() < 1) {
/* 537 */       return true;
/*     */     }
/* 539 */     if (a_alphabet == null) {
/* 540 */       return true;
/*     */     }
/* 542 */     if (a_alphabet.length() < 1) {
/* 543 */       return false;
/*     */     }
/*     */ 
/*     */     
/* 547 */     int length = a_value.length();
/*     */     
/* 549 */     for (int i = 0; i < length; i++) {
/* 550 */       char c = a_value.charAt(i);
/* 551 */       if (a_alphabet.indexOf(c) < 0) {
/* 552 */         return false;
/*     */       }
/*     */     } 
/* 555 */     return true;
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
/*     */   public void applyMutation(int index, double a_percentage) {
/*     */     boolean randomize;
/*     */     char newValue;
/* 569 */     String s = stringValue();
/* 570 */     int index2 = -1;
/*     */     
/* 572 */     int len = 0;
/* 573 */     if (this.m_alphabet != null) {
/* 574 */       len = this.m_alphabet.length();
/* 575 */       if (len < 1) {
/*     */ 
/*     */         
/* 578 */         randomize = true;
/*     */       } else {
/*     */         
/* 581 */         randomize = false;
/*     */       } 
/*     */     } else {
/*     */       
/* 585 */       randomize = true;
/*     */     } 
/*     */     
/* 588 */     RandomGenerator rn = getConfiguration().getRandomGenerator();
/* 589 */     if (!randomize) {
/* 590 */       int indexC = this.m_alphabet.indexOf(s.charAt(index));
/* 591 */       index2 = indexC + (int)Math.round(len * a_percentage);
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 596 */       if (index2 < 0 || index2 >= len) {
/* 597 */         index2 = rn.nextInt(len);
/*     */       }
/* 599 */       newValue = this.m_alphabet.charAt(index2);
/*     */     } else {
/*     */       
/* 602 */       index2 = rn.nextInt(256);
/* 603 */       newValue = (char)index2;
/*     */     } 
/*     */ 
/*     */     
/* 607 */     if (s == null) {
/* 608 */       s = "" + newValue;
/*     */     } else {
/*     */       
/* 611 */       s = s.substring(0, index) + newValue + s.substring(index + 1);
/*     */     } 
/* 613 */     setAllele(s);
/*     */   }
/*     */   
/*     */   protected Object getInternalValue() {
/* 617 */     return this.m_value;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\impl\StringGene.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */