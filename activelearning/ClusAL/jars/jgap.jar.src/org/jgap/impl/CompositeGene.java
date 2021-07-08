/*     */ package org.jgap.impl;
/*     */ 
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.StringTokenizer;
/*     */ import java.util.Vector;
/*     */ import org.jgap.BaseGene;
/*     */ import org.jgap.Configuration;
/*     */ import org.jgap.Gene;
/*     */ import org.jgap.Genotype;
/*     */ import org.jgap.IBusinessKey;
/*     */ import org.jgap.ICompositeGene;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class CompositeGene
/*     */   extends BaseGene
/*     */   implements ICompositeGene, IPersistentRepresentation
/*     */ {
/*     */   private static final String CVS_REVISION = "$Revision: 1.60 $";
/*     */   public static final String GENE_DELIMITER = "#";
/*     */   public static final String GENE_DELIMITER_HEADING = "<";
/*     */   public static final String GENE_DELIMITER_CLOSING = ">";
/*     */   private Gene m_geneTypeAllowed;
/*     */   private List<Gene> m_genes;
/*     */   
/*     */   public CompositeGene() throws InvalidConfigurationException {
/*  84 */     this(Genotype.getStaticConfiguration());
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
/*     */   public CompositeGene(Configuration a_config) throws InvalidConfigurationException {
/*  96 */     this(a_config, (Gene)null);
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
/*     */   public CompositeGene(Configuration a_config, Gene a_geneTypeAllowed) throws InvalidConfigurationException {
/* 115 */     super(a_config);
/* 116 */     this.m_genes = new Vector<Gene>();
/* 117 */     if (a_geneTypeAllowed != null) {
/* 118 */       this.m_geneTypeAllowed = a_geneTypeAllowed;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addGene(Gene a_gene) {
/* 128 */     addGene(a_gene, false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Gene getGeneTypeAllowed() {
/* 138 */     return this.m_geneTypeAllowed;
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
/*     */   public void addGene(Gene a_gene, boolean a_strict) {
/*     */     boolean containsGene;
/* 155 */     if (a_gene == null) {
/* 156 */       throw new IllegalArgumentException("Gene instance must not be null!");
/*     */     }
/* 158 */     if (this.m_geneTypeAllowed != null && 
/* 159 */       !a_gene.getClass().getName().equals(this.m_geneTypeAllowed.getClass().getName()))
/*     */     {
/* 161 */       throw new IllegalArgumentException("Adding a " + a_gene.getClass().getName() + " has been forbidden!");
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 169 */     if (!a_strict) {
/* 170 */       containsGene = containsGeneByIdentity(a_gene);
/*     */     } else {
/*     */       
/* 173 */       containsGene = this.m_genes.contains(a_gene);
/*     */     } 
/* 175 */     if (containsGene) {
/* 176 */       throw new IllegalArgumentException("The gene is already contained in the CompositeGene!");
/*     */     }
/*     */     
/* 179 */     this.m_genes.add(a_gene);
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
/*     */   public boolean removeGeneByIdentity(Gene a_gene) {
/* 194 */     int size = size();
/* 195 */     if (size < 1) {
/* 196 */       return false;
/*     */     }
/*     */     
/* 199 */     for (int i = 0; i < size; i++) {
/* 200 */       if (geneAt(i) == a_gene) {
/* 201 */         this.m_genes.remove(i);
/* 202 */         return true;
/*     */       } 
/*     */     } 
/*     */     
/* 206 */     return false;
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
/*     */   public boolean removeGene(Gene a_gene) {
/* 221 */     return this.m_genes.remove(a_gene);
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
/*     */   public void cleanup() {
/* 233 */     int size = this.m_genes.size();
/* 234 */     for (int i = 0; i < size; i++) {
/* 235 */       Gene gene = this.m_genes.get(i);
/* 236 */       gene.cleanup();
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
/*     */   public void setToRandomValue(RandomGenerator a_numberGenerator) {
/* 252 */     if (a_numberGenerator == null) {
/* 253 */       throw new IllegalArgumentException("Random generatoe must not be null!");
/*     */     }
/*     */     
/* 256 */     int size = this.m_genes.size();
/* 257 */     for (int i = 0; i < size; i++) {
/* 258 */       Gene gene = this.m_genes.get(i);
/* 259 */       gene.setToRandomValue(a_numberGenerator);
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
/*     */   public void setValueFromPersistentRepresentation(String a_representation) throws UnsupportedRepresentationException {
/* 277 */     if (a_representation != null) {
/*     */       
/*     */       try {
/*     */         
/* 281 */         this.m_genes.clear();
/* 282 */         List r = split(a_representation);
/* 283 */         Iterator<String> iter = r.iterator();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 289 */         while (iter.hasNext()) {
/* 290 */           String g = decode(iter.next());
/* 291 */           StringTokenizer st = new StringTokenizer(g, "#");
/* 292 */           if (st.countTokens() != 2) {
/* 293 */             throw new UnsupportedRepresentationException("In " + g + ", " + "expecting two tokens, separated by " + "#");
/*     */           }
/* 295 */           String clas = st.nextToken();
/* 296 */           String representation = st.nextToken();
/* 297 */           Gene gene = createGene(clas, representation);
/* 298 */           addGene(gene);
/*     */         }
/*     */       
/* 301 */       } catch (Exception ex) {
/* 302 */         throw new UnsupportedRepresentationException(ex.toString());
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
/*     */   protected Gene createGene(String a_geneClassName, String a_persistentRepresentation) throws Exception {
/* 322 */     Class<?> geneClass = Class.forName(a_geneClassName);
/* 323 */     Constructor<?> constr = geneClass.getConstructor(new Class[] { Configuration.class });
/* 324 */     Gene gene = (Gene)constr.newInstance(new Object[] { getConfiguration() });
/* 325 */     gene.setValueFromPersistentRepresentation(a_persistentRepresentation);
/* 326 */     return gene;
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
/*     */   public String getPersistentRepresentation() throws UnsupportedOperationException {
/* 341 */     StringBuffer b = new StringBuffer();
/* 342 */     Iterator<Gene> iter = this.m_genes.iterator();
/*     */     
/* 344 */     while (iter.hasNext()) {
/* 345 */       Gene gene = iter.next();
/* 346 */       b.append("<");
/* 347 */       b.append(encode(gene.getClass().getName() + "#" + gene.getPersistentRepresentation()));
/*     */ 
/*     */ 
/*     */       
/* 351 */       b.append(">");
/*     */     } 
/* 353 */     return b.toString();
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
/*     */   public Object getAllele() {
/* 368 */     List<Object> alleles = new Vector();
/*     */     
/* 370 */     int size = this.m_genes.size();
/* 371 */     for (int i = 0; i < size; i++) {
/* 372 */       Gene gene = this.m_genes.get(i);
/* 373 */       alleles.add(gene.getAllele());
/*     */     } 
/* 375 */     return alleles;
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
/*     */   public void setAllele(Object a_newValue) {
/* 390 */     if (!(a_newValue instanceof List)) {
/* 391 */       throw new IllegalArgumentException("The expected type of the allele is a List descendent.");
/*     */     }
/*     */ 
/*     */     
/* 395 */     if (getConstraintChecker() != null && 
/* 396 */       !getConstraintChecker().verify((Gene)this, a_newValue, null, -1)) {
/*     */       return;
/*     */     }
/*     */     
/* 400 */     List alleles = (List)a_newValue;
/*     */     
/* 402 */     for (int i = 0; i < alleles.size(); i++) {
/* 403 */       Gene gene = this.m_genes.get(i);
/* 404 */       gene.setAllele(alleles.get(i));
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
/* 420 */       CompositeGene compositeGene = new CompositeGene(getConfiguration());
/* 421 */       compositeGene.setConstraintChecker(getConstraintChecker());
/*     */       
/* 423 */       int geneSize = this.m_genes.size();
/* 424 */       for (int i = 0; i < geneSize; i++) {
/* 425 */         Gene gene = this.m_genes.get(i);
/* 426 */         compositeGene.addGene(gene.newGene(), false);
/*     */       } 
/* 428 */       return (Gene)compositeGene;
/*     */     }
/* 430 */     catch (InvalidConfigurationException iex) {
/* 431 */       throw new IllegalStateException(iex.getMessage());
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
/* 455 */     if (a_other == null) {
/* 456 */       return 1;
/*     */     }
/* 458 */     if (!(a_other instanceof CompositeGene)) {
/* 459 */       return getClass().getName().compareTo(a_other.getClass().getName());
/*     */     }
/* 461 */     CompositeGene otherCompositeGene = (CompositeGene)a_other;
/* 462 */     if (otherCompositeGene.isEmpty()) {
/*     */ 
/*     */ 
/*     */       
/* 466 */       if (isEmpty()) {
/* 467 */         return 0;
/*     */       }
/*     */       
/* 470 */       return 1;
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 476 */     int numberGenes = Math.min(size(), otherCompositeGene.size());
/*     */ 
/*     */     
/* 479 */     for (int i = 0; i < numberGenes; i++) {
/* 480 */       Gene gene1 = geneAt(i);
/* 481 */       Gene gene2 = otherCompositeGene.geneAt(i);
/* 482 */       int result = gene1.compareTo(gene2);
/* 483 */       if (result != 0) {
/* 484 */         return result;
/*     */       }
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 490 */     if (size() == otherCompositeGene.size()) {
/* 491 */       if (isCompareApplicationData()) {
/* 492 */         return compareApplicationData(getApplicationData(), otherCompositeGene.getApplicationData());
/*     */       }
/*     */ 
/*     */       
/* 496 */       return 0;
/*     */     } 
/*     */ 
/*     */     
/* 500 */     if (size() > otherCompositeGene.size()) {
/* 501 */       return 1;
/*     */     }
/*     */     
/* 504 */     return -1;
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
/*     */   public String toString() {
/* 523 */     if (this.m_genes.isEmpty()) {
/* 524 */       return "CompositeGene=null";
/*     */     }
/*     */     
/* 527 */     String result = "CompositeGene=(";
/*     */     
/* 529 */     for (int i = 0; i < this.m_genes.size(); i++) {
/* 530 */       Gene gene = this.m_genes.get(i);
/* 531 */       result = result + gene;
/* 532 */       if (i < this.m_genes.size() - 1) {
/* 533 */         result = result + "#";
/*     */       }
/*     */     } 
/* 536 */     return result + ")";
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isEmpty() {
/* 547 */     return this.m_genes.isEmpty();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Gene geneAt(int a_index) {
/* 558 */     return this.m_genes.get(a_index);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<Gene> getGenes() {
/* 569 */     return this.m_genes;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int size() {
/* 579 */     return this.m_genes.size();
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
/*     */   public boolean containsGeneByIdentity(Gene gene) {
/*     */     boolean result;
/* 594 */     int size = size();
/* 595 */     if (size < 1) {
/* 596 */       result = false;
/*     */     } else {
/*     */       
/* 599 */       result = false;
/* 600 */       for (int i = 0; i < size; i++) {
/*     */ 
/*     */         
/* 603 */         if (geneAt(i) == gene) {
/* 604 */           result = true;
/*     */           break;
/*     */         } 
/*     */       } 
/*     */     } 
/* 609 */     return result;
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
/*     */   public void applyMutation(int a_index, double a_percentage) {
/* 628 */     throw new RuntimeException("applyMutation may not be called for a CompositeGene. Call this method for each gene contained in the CompositeGene.");
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
/*     */   protected static final List split(String a_string) throws UnsupportedRepresentationException {
/* 646 */     List<?> a = Collections.synchronizedList(new ArrayList());
/* 647 */     StringTokenizer st = new StringTokenizer(a_string, "<>", true);
/*     */     
/* 649 */     while (st.hasMoreTokens()) {
/* 650 */       if (!st.nextToken().equals("<")) {
/* 651 */         throw new UnsupportedRepresentationException(a_string + " no opening tag");
/*     */       }
/* 653 */       String n = st.nextToken();
/* 654 */       if (n.equals(">")) {
/*     */         
/* 656 */         a.add("");
/*     */         continue;
/*     */       } 
/* 659 */       a.add(n);
/* 660 */       if (!st.nextToken().equals(">")) {
/* 661 */         throw new UnsupportedRepresentationException(a_string + " no closing tag");
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 666 */     return a;
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
/* 678 */     int hashCode = 1;
/*     */     
/* 680 */     for (int i = 0; i < size(); i++) {
/* 681 */       int geneHashcode = geneAt(i).hashCode();
/* 682 */       hashCode = 31 * hashCode + geneHashcode;
/*     */     } 
/* 684 */     return hashCode;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Object getInternalValue() {
/* 693 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getBusinessKey() {
/* 698 */     Iterator<Gene> iter = this.m_genes.iterator();
/*     */     
/* 700 */     StringBuffer b = new StringBuffer();
/* 701 */     while (iter.hasNext()) {
/* 702 */       Gene gene = iter.next();
/* 703 */       b.append("<");
/* 704 */       if (IBusinessKey.class.isAssignableFrom(gene.getClass())) {
/* 705 */         b.append(((IBusinessKey)gene).getBusinessKey());
/*     */       
/*     */       }
/*     */       else {
/*     */         
/* 710 */         b.append(gene.getPersistentRepresentation());
/*     */       } 
/* 712 */       b.append(">");
/*     */     } 
/* 714 */     return b.toString();
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\impl\CompositeGene.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */