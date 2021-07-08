/*     */ package org.jgap.supergenes;
/*     */ 
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import java.util.StringTokenizer;
/*     */ import java.util.TreeSet;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class AbstractSupergene
/*     */   extends BaseGene
/*     */   implements Supergene, SupergeneValidator, IPersistentRepresentation
/*     */ {
/*     */   private static final String CVS_REVISION = "$Revision: 1.23 $";
/*     */   public static final String GENE_DELIMITER = "#";
/*     */   public static final String GENE_DELIMITER_HEADING = "<";
/*     */   public static final String GENE_DELIMITER_CLOSING = ">";
/*     */   public static final int MAX_RETRIES = 1;
/*     */   public static final int MAX_IMMUTABLE_GENES = 100000;
/*     */   private Gene[] m_genes;
/*  71 */   private static Set[] m_immutable = new Set[1];
/*     */ 
/*     */   
/*     */   protected SupergeneValidator m_validator;
/*     */ 
/*     */   
/*     */   public Gene[] getGenes() {
/*  78 */     return this.m_genes;
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
/*     */   public final Gene geneAt(int a_index) {
/*  94 */     return this.m_genes[a_index];
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
/*     */   public AbstractSupergene() throws InvalidConfigurationException {
/* 107 */     this(Genotype.getStaticConfiguration(), new Gene[0]);
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
/*     */   public AbstractSupergene(Configuration a_config) throws InvalidConfigurationException {
/* 121 */     this(a_config, new Gene[0]);
/*     */   }
/*     */   public boolean isValid() { if (this.m_validator == null) return true;  return this.m_validator.isValid(this.m_genes, this); }
/*     */   public boolean isValid(Gene[] a_case, Supergene a_forSupergene) { throw new Error("For " + getClass().getName() + ", override " + " isValid (Gene[], Supergene) or set an" + " external validator."); }
/*     */   protected Gene newGeneInternal() { Gene[] g = new Gene[this.m_genes.length]; for (int i = 0; i < this.m_genes.length; i++) g[i] = this.m_genes[i].newGene();  try { Constructor<?> constr = getClass().getConstructor(new Class[] { Configuration.class, Gene[].class }); AbstractSupergene age = (AbstractSupergene)constr.newInstance(new Object[] { getConfiguration(), getGenes() }); if (this.m_validator != this) age.setValidator(this.m_validator);  age.m_genes = g; return age; } catch (Exception ex) { ex.printStackTrace(); throw new Error("This should not happen. Is the constructor with parameters {org.jgap.Configuration, org,jgap,Gene[]} provided for " + getClass().getName() + "?"); }  }
/*     */   public void applyMutation(int a_index, double a_percentage) { if (a_index < m_immutable.length && m_immutable[a_index] != null) synchronized (m_immutable) { if (m_immutable[a_index].contains(this)) return;  }   Object backup = this.m_genes[a_index].getAllele(); for (int i = 0; i < 1; i++) { this.m_genes[a_index].applyMutation(0, a_percentage); if (isValid()) return;  }  this.m_genes[a_index].setAllele(backup); markImmutable(a_index); }
/*     */   private void markImmutable(int a_index) { synchronized (m_immutable) { if (m_immutable.length <= a_index) { Set[] r = new Set[2 * m_immutable.length]; System.arraycopy(m_immutable, 0, r, 0, m_immutable.length); m_immutable = r; }  if (m_immutable[a_index] == null) m_immutable[a_index] = new TreeSet();  if (m_immutable[a_index].size() < 100000) m_immutable[a_index].add(this);  }  }
/*     */   public static void reset() { m_immutable = new Set[1]; }
/*     */   public void setToRandomValue(RandomGenerator a_numberGenerator) { int i; for (i = 0; i < this.m_genes.length; i++) this.m_genes[i].setToRandomValue(a_numberGenerator);  if (isValid()) return;  for (i = 0; i < 1; i++) { for (int j = 0; j < this.m_genes.length; j++) { this.m_genes[j].setToRandomValue(a_numberGenerator); if (isValid()) return;  }  }  }
/*     */   public void setAllele(Object a_superAllele) { if (this.m_genes.length < 1) return;  Object[] a = (Object[])a_superAllele; if (a.length != this.m_genes.length)
/*     */       throw new IllegalArgumentException("Record length, " + a.length + " not equal to " + this.m_genes.length);  for (int i = 0; i < this.m_genes.length; i++)
/*     */       this.m_genes[i].setAllele(a[i]);  } public Object getAllele() { Object[] o = new Object[this.m_genes.length]; for (int i = 0; i < this.m_genes.length; i++)
/* 133 */       o[i] = this.m_genes[i].getAllele();  return o; } public AbstractSupergene(Configuration a_conf, Gene[] a_genes) throws InvalidConfigurationException { super(a_conf);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 629 */     this.m_validator = this; if (a_genes == null)
/*     */       throw new RuntimeException("null value for genes not allowed!");  this.m_genes = a_genes; }
/*     */   public String getPersistentRepresentation() throws UnsupportedOperationException { StringBuffer b = new StringBuffer(); String validator = null; String v_representation = ""; SupergeneValidator v = getValidator(); if (v == null) { validator = "null"; } else if (v == this) { validator = "this"; } else { validator = v.getClass().getName(); v_representation = v.getPersistent(); }  b.append("<"); b.append(encode(validator + "#" + v_representation)); b.append(">"); for (int i = 0; i < this.m_genes.length; i++) { Gene gene = this.m_genes[i]; b.append("<"); b.append(encode(gene.getClass().getName() + "#" + gene.getPersistentRepresentation())); b.append(">"); }  return b.toString(); }
/*     */   public void setValueFromPersistentRepresentation(String a_representation) throws UnsupportedRepresentationException { if (a_representation != null) { try { List r = split(a_representation); Iterator<String> iter = r.iterator(); this.m_genes = new Gene[r.size() - 1]; String validator = iter.next(); setValidator(createValidator(decode(validator))); for (int i = 0; i < this.m_genes.length; i++) { String g = decode(iter.next()); StringTokenizer st = new StringTokenizer(g, "#"); if (st.countTokens() != 2)
/*     */             throw new UnsupportedRepresentationException("In " + g + ", " + "expecting two tokens, separated by " + "#");  String clas = st.nextToken(); String representation = st.nextToken(); Gene gene = createGene(clas, representation); this.m_genes[i] = gene; }  } catch (Exception ex) { ex.printStackTrace(); throw new UnsupportedRepresentationException(ex.getCause().getMessage()); }  } else { throw new UnsupportedRepresentationException("null value not allowed"); }
/* 634 */      } public String getPersistent() { return ""; } protected SupergeneValidator createValidator(String a_rep) { try { SupergeneValidator sv; StringTokenizer vo = new StringTokenizer(a_rep, "#", true); if (vo.countTokens() != 2)
/*     */         throw new Error("In " + a_rep + ", expecting two tokens, separated by " + "#");  String clas = vo.nextToken(); if (clas.equals("this")) { sv = this; } else if (clas.equals("null")) { sv = null; } else { Class<?> svClass = Class.forName(clas); Constructor<?> constr = svClass.getConstructor(new Class[] { Configuration.class }); sv = (SupergeneValidator)constr.newInstance(new Object[] { getConfiguration() }); }  if (sv != null)
/*     */         sv.setFromPersistent(decode(vo.nextToken()));  return sv; } catch (Exception ex) { throw new Error("Unable to create validator from '" + a_rep + "' for " + getClass().getName(), ex); }  }
/*     */   protected Gene createGene(String a_geneClassName, String a_persistentRepresentation) throws Exception { Class<?> geneClass = Class.forName(a_geneClassName); Constructor<?> constr = geneClass.getConstructor(new Class[] { Configuration.class }); Gene gene = (Gene)constr.newInstance(new Object[] { getConfiguration() }); gene.setValueFromPersistentRepresentation(a_persistentRepresentation); return gene; }
/*     */   public void cleanup() { for (int i = 0; i < this.m_genes.length; i++)
/*     */       this.m_genes[i].cleanup();  }
/*     */   public String toString() { StringBuffer b = new StringBuffer(); b.append("Supergene " + getClass().getName() + " {"); for (int i = 0; i < this.m_genes.length; i++) { b.append("|"); b.append(this.m_genes[i].toString()); b.append("|"); }  if (this.m_validator == null) { b.append(" non validating"); } else { b.append(" validator: " + this.m_validator.getClass().getName()); }  b.append("}"); return b.toString(); }
/*     */   public int size() { return this.m_genes.length; }
/*     */   public int compareTo(Object o) { AbstractSupergene q = (AbstractSupergene)o; int c = this.m_genes.length - q.m_genes.length; if (c != 0)
/*     */       return c;  for (int i = 0; i < this.m_genes.length; i++) { c = this.m_genes[i].compareTo(q.m_genes[i]); if (c != 0)
/*     */         return c;  }
/*     */      if (getClass().equals(o.getClass()))
/*     */       return 0;  return getClass().getName().compareTo(o.getClass().getName()); }
/* 647 */   public Object getInternalValue() { throw new RuntimeException("getInternalValue() called unexpectedly!"); }
/*     */ 
/*     */   
/*     */   public boolean equals(Object a_gene) {
/*     */     if (a_gene == null || !a_gene.getClass().equals(getClass()))
/*     */       return false; 
/*     */     AbstractSupergene age = (AbstractSupergene)a_gene;
/*     */     if (this.m_validator != age.m_validator && this.m_validator != null)
/*     */       if (m_immutable != null && !this.m_validator.getClass().equals(age.m_validator.getClass()))
/*     */         return false;  
/*     */     return Arrays.equals((Object[])this.m_genes, (Object[])age.m_genes);
/*     */   }
/*     */   
/*     */   public int hashCode() {
/*     */     int s = 0;
/*     */     for (int i = this.m_genes.length - 1; i >= 0; i--)
/*     */       s += this.m_genes[i].hashCode(); 
/*     */     return s;
/*     */   }
/*     */   
/*     */   protected static final List split(String a_string) throws UnsupportedRepresentationException {
/*     */     List<?> a = Collections.synchronizedList(new ArrayList());
/*     */     StringTokenizer st = new StringTokenizer(a_string, "<>", true);
/*     */     while (st.hasMoreTokens()) {
/*     */       if (!st.nextToken().equals("<"))
/*     */         throw new UnsupportedRepresentationException(a_string + " no open tag"); 
/*     */       String n = st.nextToken();
/*     */       if (n.equals(">")) {
/*     */         a.add("");
/*     */         continue;
/*     */       } 
/*     */       a.add(n);
/*     */       if (!st.nextToken().equals(">"))
/*     */         throw new UnsupportedRepresentationException(a_string + " no close tag"); 
/*     */     } 
/*     */     return a;
/*     */   }
/*     */   
/*     */   public void addGene(Gene a_gene) {
/*     */     Gene[] genes = new Gene[this.m_genes.length + 1];
/*     */     System.arraycopy(this.m_genes, 0, genes, 0, this.m_genes.length);
/*     */     genes[this.m_genes.length] = a_gene;
/*     */     this.m_genes = genes;
/*     */   }
/*     */   
/*     */   public void setValidator(SupergeneValidator a_validator) {
/*     */     this.m_validator = a_validator;
/*     */   }
/*     */   
/*     */   public SupergeneValidator getValidator() {
/*     */     return this.m_validator;
/*     */   }
/*     */   
/*     */   public void setFromPersistent(String a_from) {}
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\supergenes\AbstractSupergene.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */