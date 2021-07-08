/*     */ package org.jgap.impl;
/*     */ 
/*     */ import gnu.trove.THashMap;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.StringTokenizer;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class MapGene
/*     */   extends BaseGene
/*     */   implements IPersistentRepresentation
/*     */ {
/*     */   private static final String CVS_REVISION = "$Revision: 1.23 $";
/*     */   private THashMap m_geneMap;
/*     */   private Object m_value;
/*     */   static final String ALLELEMAP_BEGIN_DELIMITER = "[";
/*     */   static final String ALLELEMAP_END_DELIMITER = "]";
/*     */   
/*     */   public MapGene() throws InvalidConfigurationException {
/*  66 */     this(Genotype.getStaticConfiguration());
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
/*     */   public MapGene(Configuration a_config) throws InvalidConfigurationException {
/*  78 */     super(a_config);
/*  79 */     this.m_geneMap = new THashMap();
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
/*     */   public MapGene(Configuration a_config, Map a_alleles) throws InvalidConfigurationException {
/*  94 */     super(a_config);
/*  95 */     this.m_geneMap = new THashMap();
/*  96 */     addAlleles(a_alleles);
/*     */   }
/*     */   
/*     */   protected Gene newGeneInternal() {
/*     */     try {
/* 101 */       MapGene result = new MapGene(getConfiguration(), (Map)this.m_geneMap);
/*     */       
/* 103 */       Object value = getAllele();
/* 104 */       result.setAllele(value);
/* 105 */       return (Gene)result;
/*     */     }
/* 107 */     catch (InvalidConfigurationException iex) {
/* 108 */       throw new IllegalStateException(iex.getMessage());
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
/*     */   public void addAllele(Object a_key, Object a_value) {
/* 120 */     this.m_geneMap.put(a_key, a_value);
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
/*     */   public void addAllele(Object a_value) {
/* 132 */     this.m_geneMap.put(a_value, a_value);
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
/*     */   public void addAllele(int a_value) {
/* 144 */     this.m_geneMap.put(new Integer(a_value), new Integer(a_value));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addAlleles(Map a_alleles) {
/* 155 */     if (a_alleles == null) {
/* 156 */       throw new IllegalArgumentException("List of alleles may not be null!");
/*     */     }
/*     */     
/* 159 */     this.m_geneMap.putAll(a_alleles);
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
/*     */   public void removeAlleles(Object a_key) {
/* 171 */     this.m_geneMap.remove(a_key);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Map getAlleles() {
/* 181 */     return (Map)this.m_geneMap;
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
/* 197 */     if (this.m_geneMap.isEmpty()) {
/* 198 */       this.m_value = new Integer(a_numberGenerator.nextInt());
/*     */     } else {
/*     */       
/* 201 */       this.m_value = this.m_geneMap.get(this.m_geneMap.keySet().toArray()[a_numberGenerator.nextInt(this.m_geneMap.size())]);
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
/*     */   public void applyMutation(int a_index, double a_percentage) {
/* 224 */     RandomGenerator rn = getConfiguration().getRandomGenerator();
/* 225 */     setToRandomValue(rn);
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
/* 251 */     if (a_representation != null) {
/* 252 */       StringTokenizer tokenizer = new StringTokenizer(a_representation, ":");
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 257 */       if (tokenizer.countTokens() != 2) {
/* 258 */         throw new UnsupportedRepresentationException("The format of the given persistent representation is not recognized: it must contain two tokens.");
/*     */       }
/*     */ 
/*     */       
/* 262 */       String valueRepresentation = tokenizer.nextToken();
/*     */ 
/*     */       
/* 265 */       if (valueRepresentation.equals("null")) {
/* 266 */         this.m_value = null;
/*     */       } else {
/*     */         
/*     */         try {
/* 270 */           this.m_value = new Integer(Integer.parseInt(valueRepresentation));
/*     */         }
/* 272 */         catch (NumberFormatException e) {
/* 273 */           throw new UnsupportedRepresentationException("The format of the given persistent representation is not recognized: field 1 does not appear to be an integer value.");
/*     */         } 
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 281 */       String s = tokenizer.nextToken();
/* 282 */       tokenizer = new StringTokenizer(s, ",");
/* 283 */       int lastWasOpening = 0;
/* 284 */       String key = null;
/* 285 */       String keyClass = null;
/* 286 */       String valueClass = null;
/* 287 */       while (tokenizer.hasMoreTokens()) {
/* 288 */         String element = tokenizer.nextToken(",");
/* 289 */         if (lastWasOpening == 1) {
/* 290 */           key = element.substring(0);
/* 291 */           lastWasOpening = 2; continue;
/*     */         } 
/* 293 */         if (lastWasOpening == 2) {
/* 294 */           valueClass = element.substring(0);
/* 295 */           lastWasOpening = 3; continue;
/*     */         } 
/* 297 */         if (lastWasOpening == 3) {
/* 298 */           if (element.endsWith(")")) {
/* 299 */             element = element.substring(0, element.length() - 1);
/*     */             try {
/* 301 */               Class<?> keyType = Class.forName(keyClass);
/* 302 */               Constructor<?> keyC = keyType.getConstructor(new Class[] { String.class });
/* 303 */               Object keyObject = keyC.newInstance(new Object[] { key });
/*     */               
/* 305 */               Class<?> valueType = Class.forName(valueClass);
/* 306 */               Constructor<?> valueC = valueType.getConstructor(new Class[] { String.class });
/* 307 */               Object valueObject = valueC.newInstance(new Object[] { element });
/* 308 */               addAllele(keyObject, valueObject);
/* 309 */               lastWasOpening = 0;
/* 310 */             } catch (Exception cex) {
/* 311 */               throw new UnsupportedRepresentationException("Invalid class: " + keyClass);
/*     */             } 
/*     */             
/*     */             continue;
/*     */           } 
/* 316 */           throw new IllegalStateException("Closing bracket missing");
/*     */         } 
/*     */ 
/*     */         
/* 320 */         if (element.startsWith("(")) {
/* 321 */           keyClass = element.substring(1);
/* 322 */           lastWasOpening = 1;
/*     */           continue;
/*     */         } 
/* 325 */         throw new IllegalStateException("Opening bracket missing");
/*     */       } 
/*     */ 
/*     */       
/* 329 */       if (lastWasOpening != 0) {
/* 330 */         throw new IllegalStateException("Elements missing");
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
/*     */ 
/*     */ 
/*     */   
/*     */   public String getPersistentRepresentation() throws UnsupportedOperationException {
/* 357 */     Iterator it = this.m_geneMap.keySet().iterator();
/* 358 */     StringBuffer strbf = new StringBuffer();
/* 359 */     boolean first = true;
/* 360 */     while (it.hasNext()) {
/* 361 */       if (!first) {
/* 362 */         strbf.append(",");
/*     */       }
/* 364 */       Object key = it.next();
/* 365 */       Object value = this.m_geneMap.get(key);
/* 366 */       strbf.append("(" + key.getClass().getName() + "," + key.toString() + "," + value.getClass().getName() + "," + value.toString() + ")");
/*     */       
/* 368 */       first = false;
/*     */     } 
/* 370 */     return this.m_value.toString() + ":" + strbf.toString();
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
/* 386 */     if (a_newValue == null) {
/*     */       return;
/*     */     }
/* 389 */     if (this.m_geneMap.keySet().isEmpty()) {
/* 390 */       this.m_value = a_newValue;
/*     */     }
/* 392 */     else if (this.m_geneMap.keySet().contains(a_newValue)) {
/* 393 */       this.m_value = this.m_geneMap.get(a_newValue);
/*     */     } else {
/*     */       
/* 396 */       throw new IllegalArgumentException("Allele value being set (" + a_newValue + ") is not an element of the set of" + " permitted values.");
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
/*     */   public int compareTo(Object a_other) {
/* 420 */     MapGene otherGene = (MapGene)a_other;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 425 */     if (otherGene == null) {
/* 426 */       return 1;
/*     */     }
/* 428 */     if (otherGene.m_value == null)
/*     */     {
/*     */       
/* 431 */       if (this.m_value != null) {
/* 432 */         return 1;
/*     */       }
/*     */     }
/*     */     try {
/* 436 */       int size1 = this.m_geneMap.size();
/* 437 */       int size2 = otherGene.m_geneMap.size();
/* 438 */       if (size1 != size2) {
/* 439 */         if (size1 < size2) {
/* 440 */           return -1;
/*     */         }
/*     */         
/* 443 */         return 1;
/*     */       } 
/*     */ 
/*     */ 
/*     */       
/* 448 */       Iterator it1 = this.m_geneMap.keySet().iterator();
/*     */       
/* 450 */       while (it1.hasNext()) {
/* 451 */         Object key1 = it1.next();
/* 452 */         if (!otherGene.m_geneMap.keySet().contains(key1)) {
/* 453 */           Object key2 = otherGene.m_geneMap.keySet().iterator().next();
/* 454 */           if (Comparable.class.isAssignableFrom(key1.getClass()) && Comparable.class.isAssignableFrom(key2.getClass()))
/*     */           {
/* 456 */             return ((Comparable<Object>)key1).compareTo(key2);
/*     */           }
/*     */ 
/*     */           
/* 460 */           return -1;
/*     */         } 
/*     */         
/* 463 */         Object value1 = this.m_geneMap.get(key1);
/* 464 */         Object value2 = otherGene.m_geneMap.get(key1);
/* 465 */         if (value1 == null && value2 != null) {
/* 466 */           return -1;
/*     */         }
/* 468 */         if (value1 == null && value2 != null) {
/* 469 */           return -1;
/*     */         }
/* 471 */         if (!value1.equals(value2)) {
/* 472 */           if (value2 == null) {
/* 473 */             return 1;
/*     */           }
/*     */           
/* 476 */           if (Comparable.class.isAssignableFrom(value1.getClass()) && Comparable.class.isAssignableFrom(value2.getClass()))
/*     */           {
/* 478 */             return ((Comparable<Object>)value1).compareTo(value2);
/*     */           }
/*     */ 
/*     */           
/* 482 */           return -1;
/*     */         } 
/*     */       } 
/*     */ 
/*     */ 
/*     */       
/* 488 */       if (this.m_value == null) {
/* 489 */         if (otherGene.m_value != null) {
/* 490 */           return 1;
/*     */         }
/*     */         
/* 493 */         return 0;
/*     */       } 
/*     */       
/* 496 */       Method method = this.m_value.getClass().getMethod("compareTo", new Class[] { otherGene.m_value.getClass() });
/*     */       
/* 498 */       Integer i = (Integer)method.invoke(this.m_value, new Object[] { otherGene.m_value });
/*     */       
/* 500 */       return i.intValue();
/*     */     }
/* 502 */     catch (InvocationTargetException ex) {
/* 503 */       ex.printStackTrace();
/* 504 */       throw new IllegalArgumentException("CompareTo method of the Gene value object cannot be invoked.");
/*     */     
/*     */     }
/* 507 */     catch (IllegalArgumentException ex) {
/* 508 */       ex.printStackTrace();
/* 509 */       throw new IllegalArgumentException("The value object of the Gene does not have a compareTo method.  It cannot be compared.");
/*     */ 
/*     */     
/*     */     }
/* 513 */     catch (IllegalAccessException ex) {
/* 514 */       ex.printStackTrace();
/* 515 */       throw new IllegalArgumentException("The compareTo method of the Gene value object cannot be accessed ");
/*     */     
/*     */     }
/* 518 */     catch (SecurityException ex) {
/* 519 */       ex.printStackTrace();
/* 520 */       throw new IllegalArgumentException("The compareTo method of the Gene value object cannot be accessed.  Insufficient permission levels.");
/*     */ 
/*     */     
/*     */     }
/* 524 */     catch (NoSuchMethodException ex) {
/* 525 */       ex.printStackTrace();
/* 526 */       throw new IllegalArgumentException("The value object of the Gene does not have a compareTo method.  It cannot be compared.");
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Object getInternalValue() {
/* 537 */     return this.m_value;
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
/* 549 */     if (getInternalValue() == null) {
/* 550 */       return -71;
/*     */     }
/*     */     
/* 553 */     return super.hashCode();
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
/*     */   public String toString() {
/* 567 */     String result = "[";
/* 568 */     if (this.m_geneMap.size() < 1) {
/* 569 */       result = result + "null";
/*     */     } else {
/*     */       
/* 572 */       Set keys = this.m_geneMap.keySet();
/* 573 */       Iterator keyIterator = keys.iterator();
/* 574 */       boolean firstTime = true;
/* 575 */       while (keyIterator.hasNext()) {
/* 576 */         String keyString, valueString; if (!firstTime) {
/* 577 */           result = result + ",";
/*     */         } else {
/*     */           
/* 580 */           firstTime = false;
/*     */         } 
/* 582 */         Object key = keyIterator.next();
/*     */         
/* 584 */         if (key == null) {
/* 585 */           keyString = "null";
/*     */         } else {
/*     */           
/* 588 */           keyString = key.toString();
/*     */         } 
/* 590 */         result = result + "(" + keyString + ",";
/* 591 */         Object value = this.m_geneMap.get(key);
/*     */         
/* 593 */         if (value == null) {
/* 594 */           valueString = "null";
/*     */         } else {
/*     */           
/* 597 */           valueString = value.toString();
/*     */         } 
/* 599 */         result = result + valueString + ")";
/*     */       } 
/*     */     } 
/* 602 */     result = result + "]";
/* 603 */     return result;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\impl\MapGene.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */