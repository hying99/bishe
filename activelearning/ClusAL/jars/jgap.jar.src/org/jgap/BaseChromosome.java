/*     */ package org.jgap;
/*     */ 
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.StringTokenizer;
/*     */ import org.jgap.util.StringKit;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class BaseChromosome
/*     */   implements IChromosome, IInitializer, IPersistentRepresentation, IBusinessKey
/*     */ {
/*     */   private static final String CVS_REVISION = "$Revision: 1.11 $";
/*     */   public static final String GENE_DELIMITER = "#";
/*     */   public static final String GENE_DELIMITER_HEADING = "<";
/*     */   public static final String GENE_DELIMITER_CLOSING = ">";
/*     */   public static final String CHROM_DELIMITER = "#";
/*     */   private Configuration m_configuration;
/*     */   private Gene[] m_genes;
/*     */   private int m_age;
/*     */   private int m_operatedOn;
/*     */   
/*     */   public BaseChromosome(Configuration a_configuration) throws InvalidConfigurationException {
/*  77 */     if (a_configuration == null) {
/*  78 */       throw new InvalidConfigurationException("Configuration to be set must not be null!");
/*     */     }
/*     */ 
/*     */     
/*  82 */     this.m_configuration = a_configuration;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Configuration getConfiguration() {
/*  92 */     return this.m_configuration;
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
/*     */   public abstract Object clone();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void increaseAge() {
/* 114 */     this.m_age++;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void resetAge() {
/* 124 */     this.m_age = 0;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getAge() {
/* 135 */     return this.m_age;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void increaseOperatedOn() {
/* 146 */     this.m_operatedOn++;
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
/*     */   public void resetOperatedOn() {
/* 158 */     this.m_operatedOn = 0;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int operatedOn() {
/* 169 */     return this.m_operatedOn;
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
/*     */   public synchronized Gene[] getGenes() {
/* 183 */     return this.m_genes;
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
/*     */   public void setGenes(Gene[] a_genes) throws InvalidConfigurationException {
/* 198 */     this.m_genes = a_genes;
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
/*     */   public synchronized Gene getGene(int a_desiredLocus) {
/* 213 */     return this.m_genes[a_desiredLocus];
/*     */   }
/*     */   
/*     */   public void setGene(int a_index, Gene a_gene) {
/* 217 */     this.m_genes[a_index] = a_gene;
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
/*     */   public int size() {
/* 232 */     if (this.m_genes == null)
/*     */     {
/* 234 */       return 0;
/*     */     }
/*     */     
/* 237 */     return this.m_genes.length;
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
/*     */   public String getPersistentRepresentation() {
/* 254 */     StringBuffer b = new StringBuffer();
/*     */ 
/*     */     
/* 257 */     b.append(getFitnessValueDirectly());
/* 258 */     b.append("#");
/*     */ 
/*     */     
/* 261 */     b.append(size());
/* 262 */     b.append("#");
/* 263 */     getGenesPersistentRepresentation(b);
/* 264 */     return b.toString();
/*     */   }
/*     */   
/*     */   public StringBuffer getGenesPersistentRepresentation() {
/* 268 */     StringBuffer b = new StringBuffer();
/* 269 */     getGenesPersistentRepresentation(b);
/* 270 */     return b;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getBusinessKey() {
/* 280 */     return getGenesPersistentRepresentation().toString();
/*     */   }
/*     */ 
/*     */   
/*     */   public void getGenesPersistentRepresentation(StringBuffer a_buffer) {
/* 285 */     int size = size();
/* 286 */     for (int i = 0; i < size; i++) {
/* 287 */       Gene gene = getGene(i);
/* 288 */       a_buffer.append("<");
/* 289 */       a_buffer.append(encode(gene.getClass().getName() + "#" + gene.getPersistentRepresentation()));
/*     */ 
/*     */       
/* 292 */       a_buffer.append(">");
/*     */     } 
/*     */   }
/*     */   
/*     */   protected String encode(String a_string) {
/* 297 */     return StringKit.encode(a_string);
/*     */   }
/*     */   
/*     */   protected String decode(String a_string) throws UnsupportedEncodingException {
/* 301 */     return StringKit.decode(a_string);
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
/*     */   public void setValueFromPersistentRepresentation(String a_representation) throws UnsupportedRepresentationException {
/* 317 */     if (a_representation != null) {
/*     */       try {
/* 319 */         List<String> r = split(a_representation);
/*     */ 
/*     */ 
/*     */         
/* 323 */         String g = decode(r.get(0));
/* 324 */         setFitnessValue(Double.parseDouble(g));
/* 325 */         r.remove(0);
/*     */ 
/*     */         
/* 328 */         g = decode(r.get(0));
/* 329 */         int count = Integer.parseInt(g);
/* 330 */         setGenes(new Gene[count]);
/* 331 */         r.remove(0);
/*     */ 
/*     */         
/* 334 */         Iterator<String> iter = r.iterator();
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 339 */         int index = 0;
/* 340 */         while (iter.hasNext()) {
/* 341 */           g = decode(iter.next());
/* 342 */           StringTokenizer st = new StringTokenizer(g, "#");
/* 343 */           if (st.countTokens() != 2) {
/* 344 */             throw new UnsupportedRepresentationException("In " + g + ", " + "expecting two tokens, separated by " + "#");
/*     */           }
/* 346 */           String clas = st.nextToken();
/* 347 */           String representation = st.nextToken();
/* 348 */           Gene gene = createGene(clas, representation);
/* 349 */           setGene(index++, gene);
/*     */         }
/*     */       
/* 352 */       } catch (Exception ex) {
/* 353 */         throw new UnsupportedRepresentationException(ex.toString());
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
/*     */   protected Gene createGene(String a_geneClassName, String a_persistentRepresentation) throws Exception {
/* 375 */     Class<?> geneClass = Class.forName(a_geneClassName);
/* 376 */     Constructor<?> constr = geneClass.getConstructor(new Class[] { Configuration.class });
/* 377 */     Gene gene = (Gene)constr.newInstance(new Object[] { getConfiguration() });
/* 378 */     gene.setValueFromPersistentRepresentation(a_persistentRepresentation);
/* 379 */     return gene;
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
/*     */   protected static final List split(String a_string) throws UnsupportedRepresentationException {
/* 396 */     List<?> a = Collections.synchronizedList(new ArrayList());
/*     */ 
/*     */     
/* 399 */     int index = 0;
/* 400 */     StringTokenizer st0 = new StringTokenizer(a_string, "#", false);
/*     */     
/* 402 */     if (!st0.hasMoreTokens()) {
/* 403 */       throw new UnsupportedRepresentationException("Fitness value expected!");
/*     */     }
/* 405 */     String fitnessS = st0.nextToken();
/* 406 */     a.add(fitnessS);
/* 407 */     index += fitnessS.length();
/* 408 */     if (!st0.hasMoreTokens()) {
/* 409 */       throw new UnsupportedRepresentationException("Number of genes expected!");
/*     */     }
/* 411 */     String numGenes = st0.nextToken();
/* 412 */     a.add(numGenes);
/* 413 */     index += numGenes.length();
/*     */     
/* 415 */     index += 2;
/*     */     
/* 417 */     if (!st0.hasMoreTokens()) {
/* 418 */       throw new UnsupportedRepresentationException("Gene data missing!");
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 423 */     a_string = a_string.substring(index);
/*     */ 
/*     */ 
/*     */     
/* 427 */     StringTokenizer st = new StringTokenizer(a_string, "<>", true);
/*     */     
/* 429 */     while (st.hasMoreTokens()) {
/* 430 */       if (!st.nextToken().equals("<")) {
/* 431 */         throw new UnsupportedRepresentationException(a_string + " no open tag");
/*     */       }
/* 433 */       String n = st.nextToken();
/* 434 */       if (n.equals(">")) {
/* 435 */         a.add("");
/*     */         continue;
/*     */       } 
/* 438 */       a.add(n);
/* 439 */       if (!st.nextToken().equals(">")) {
/* 440 */         throw new UnsupportedRepresentationException(a_string + " no close tag");
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 445 */     return a;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\BaseChromosome.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */