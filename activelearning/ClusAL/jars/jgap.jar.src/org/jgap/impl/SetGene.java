/*     */ package org.jgap.impl;
/*     */ 
/*     */ import gnu.trove.THashSet;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.StringTokenizer;
/*     */ import org.jgap.BaseGene;
/*     */ import org.jgap.Configuration;
/*     */ import org.jgap.Gene;
/*     */ import org.jgap.Genotype;
/*     */ import org.jgap.ICompareToHandler;
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
/*     */ public class SetGene
/*     */   extends BaseGene
/*     */   implements IPersistentRepresentation
/*     */ {
/*     */   private static final String CVS_REVISION = "$Revision: 1.18 $";
/*  33 */   private THashSet m_geneSet = new THashSet();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Object m_value;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SetGene() throws InvalidConfigurationException {
/*  46 */     this(Genotype.getStaticConfiguration());
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
/*     */   public SetGene(Configuration a_conf) throws InvalidConfigurationException {
/*  59 */     super(a_conf);
/*     */   }
/*     */   
/*     */   protected Gene newGeneInternal() {
/*     */     try {
/*  64 */       return (Gene)new SetGene(getConfiguration());
/*     */     }
/*  66 */     catch (InvalidConfigurationException iex) {
/*  67 */       throw new IllegalStateException(iex.getMessage());
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addAllele(Object a_value) {
/*  77 */     this.m_geneSet.add(a_value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addAlleles(Collection a_alleles) {
/*  86 */     this.m_geneSet.addAll(a_alleles);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void removeAlleles(Object a_key) {
/*  95 */     this.m_geneSet.remove(a_key);
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
/*     */   public void setToRandomValue(RandomGenerator a_numberGenerator) {
/* 107 */     this.m_value = this.m_geneSet.toArray()[a_numberGenerator.nextInt(this.m_geneSet.size())];
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
/*     */   public void applyMutation(int a_index, double a_percentage) {
/*     */     RandomGenerator rn;
/* 129 */     if (getConfiguration() != null) {
/* 130 */       rn = getConfiguration().getRandomGenerator();
/*     */     } else {
/*     */       
/* 133 */       rn = getConfiguration().getJGAPFactory().createRandomGenerator();
/*     */     } 
/* 135 */     setToRandomValue(rn);
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
/*     */   public void setValueFromPersistentRepresentation(String a_representation) throws UnsupportedRepresentationException {
/* 156 */     if (a_representation != null) {
/* 157 */       StringTokenizer tokenizer = new StringTokenizer(a_representation, ":");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 163 */       if (tokenizer.countTokens() < 3) {
/* 164 */         throw new UnsupportedRepresentationException("The format of the given persistent representation is not recognized: it must contain at least three tokens.");
/*     */       }
/*     */ 
/*     */       
/* 168 */       String valueRepresentation = tokenizer.nextToken();
/*     */ 
/*     */ 
/*     */       
/* 172 */       if (valueRepresentation.equals("null")) {
/* 173 */         this.m_value = null;
/*     */       } else {
/*     */         
/*     */         try {
/* 177 */           this.m_value = new Integer(Integer.parseInt(valueRepresentation));
/*     */         
/*     */         }
/* 180 */         catch (NumberFormatException e) {
/* 181 */           throw new UnsupportedRepresentationException("The format of the given persistent representation is not recognized: field 1 does not appear to be an integer value.");
/*     */         } 
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 190 */       while (tokenizer.hasMoreTokens()) {
/*     */         try {
/* 192 */           Integer allele = new Integer(Integer.parseInt(tokenizer.nextToken()));
/* 193 */           this.m_geneSet.add(allele);
/*     */         }
/* 195 */         catch (NumberFormatException e) {
/* 196 */           throw new UnsupportedRepresentationException("The format of the given persistent representation is not recognized: a member of the list of eligible values does not appear to be an integer value.");
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
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
/* 228 */     Iterator<E> it = this.m_geneSet.iterator();
/* 229 */     StringBuffer strbf = new StringBuffer();
/* 230 */     while (it.hasNext()) {
/* 231 */       strbf.append(":");
/* 232 */       strbf.append(it.next().toString());
/*     */     } 
/* 234 */     return this.m_value.toString() + strbf.toString();
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
/*     */   public void setAllele(Object a_newValue) {
/* 246 */     if (this.m_geneSet.contains(a_newValue)) {
/* 247 */       this.m_value = a_newValue;
/*     */     } else {
/*     */       
/* 250 */       throw new IllegalArgumentException("Allele value being set is not an element of the set of permitted values.");
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
/*     */   public int compareTo(Object other) {
/* 273 */     SetGene otherGene = (SetGene)other;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 278 */     if (otherGene == null) {
/* 279 */       return 1;
/*     */     }
/* 281 */     if (otherGene.m_value == null)
/*     */     {
/*     */ 
/*     */       
/* 285 */       return (this.m_value == null) ? 0 : 1;
/*     */     }
/*     */     
/* 288 */     ICompareToHandler handler = getConfiguration().getJGAPFactory().getCompareToHandlerFor(this.m_value, this.m_value.getClass());
/*     */     
/* 290 */     if (handler != null) {
/*     */       try {
/* 292 */         return ((Integer)handler.perform(this.m_value, null, otherGene.m_value)).intValue();
/*     */       
/*     */       }
/* 295 */       catch (Exception ex) {
/* 296 */         throw new Error(ex);
/*     */       } 
/*     */     }
/*     */     
/* 300 */     return 0;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Object getInternalValue() {
/* 310 */     return this.m_value;
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
/* 322 */     if (getInternalValue() == null) {
/* 323 */       return -67;
/*     */     }
/*     */     
/* 326 */     return super.hashCode();
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\impl\SetGene.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */