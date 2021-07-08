/*     */ package org.jgap.data;
/*     */ 
/*     */ import org.jgap.Gene;
/*     */ import org.jgap.Genotype;
/*     */ import org.jgap.IChromosome;
/*     */ import org.jgap.Population;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DataTreeBuilder
/*     */ {
/*     */   private static final String CVS_REVISION = "$Revision: 1.12 $";
/*     */   private static final String GENOTYPE_TAG = "genotype";
/*     */   private static final String CHROMOSOME_TAG = "chromosome";
/*     */   private static final String GENES_TAG = "genes";
/*     */   private static final String GENE_TAG = "gene";
/*     */   private static final String ALLELE_TAG = "allele";
/*     */   private static final String SIZE_ATTRIBUTE = "size";
/*     */   private static final String CLASS_ATTRIBUTE = "class";
/*     */   private Object m_lock;
/*     */   private static DataTreeBuilder m_instance;
/*     */   
/*     */   public static synchronized DataTreeBuilder getInstance() {
/*  78 */     if (m_instance == null) {
/*  79 */       m_instance = new DataTreeBuilder();
/*  80 */       m_instance.m_lock = new Object();
/*     */     } 
/*  82 */     return m_instance;
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
/*     */   public IDataCreators representGenotypeAsDocument(Genotype a_subject) throws Exception {
/*     */     IDataCreators genotypeDocument;
/* 108 */     synchronized (this.m_lock) {
/* 109 */       genotypeDocument = new DataElementsDocument();
/* 110 */       genotypeDocument.setTree(createTree());
/*     */     } 
/* 112 */     IDataElement genotypeElement = representGenotypeAsElement(a_subject);
/* 113 */     genotypeDocument.appendChild(genotypeElement);
/* 114 */     return genotypeDocument;
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
/*     */   public IDataElement representGenotypeAsElement(Genotype a_subject) throws Exception {
/* 134 */     Population population = a_subject.getPopulation();
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 139 */     IDataElement genotypeTag = new DataElement("genotype");
/* 140 */     genotypeTag.setAttribute("size", Integer.toString(population.size()));
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 145 */     for (int i = 0; i < population.size(); i++) {
/* 146 */       IDataElement chromosomeElement = representChromosomeAsElement(population.getChromosome(i));
/*     */       
/* 148 */       genotypeTag.appendChild(chromosomeElement);
/*     */     } 
/* 150 */     return genotypeTag;
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
/*     */   public IDataCreators representChromosomeAsDocument(IChromosome a_subject) throws Exception {
/*     */     IDataCreators chromosomeDocument;
/* 170 */     synchronized (this.m_lock) {
/*     */       
/* 172 */       chromosomeDocument = new DataElementsDocument();
/* 173 */       chromosomeDocument.setTree(createTree());
/*     */     } 
/* 175 */     IDataElement chromosomeElement = representChromosomeAsElement(a_subject);
/*     */     
/* 177 */     chromosomeDocument.appendChild(chromosomeElement);
/* 178 */     return chromosomeDocument;
/*     */   }
/*     */   
/*     */   protected IDataElementList createTree() {
/* 182 */     return new DataElementList();
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
/*     */   public IDataElement representChromosomeAsElement(IChromosome a_subject) throws Exception {
/* 204 */     IDataElement chromosomeElement = new DataElement("chromosome");
/* 205 */     chromosomeElement.setAttribute("size", Integer.toString(a_subject.size()));
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 210 */     IDataElement genesElement = representGenesAsElement(a_subject.getGenes());
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 215 */     chromosomeElement.appendChild(genesElement);
/* 216 */     return chromosomeElement;
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
/*     */   public IDataElement representGenesAsElement(Gene[] a_geneValues) throws Exception {
/* 233 */     IDataElement genesElement = new DataElement("genes");
/*     */ 
/*     */ 
/*     */     
/* 237 */     for (int i = 0; i < a_geneValues.length; i++) {
/* 238 */       IDataElement geneElement = representGeneAsElement(a_geneValues[i]);
/* 239 */       genesElement.appendChild(geneElement);
/*     */     } 
/* 241 */     return genesElement;
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
/*     */   public IDataElement representGeneAsElement(Gene a_gene) throws Exception {
/* 258 */     IDataElement geneElement = new DataElement("gene");
/*     */ 
/*     */ 
/*     */     
/* 262 */     geneElement.setAttribute("class", a_gene.getClass().getName());
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 267 */     geneElement.appendChild(representAlleleAsElement(a_gene));
/* 268 */     return geneElement;
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
/*     */   private IDataElement representAlleleAsElement(Gene a_gene) throws Exception {
/* 283 */     IDataElement alleleElement = new DataElement("allele");
/* 284 */     alleleElement.setAttribute("value", a_gene.getPersistentRepresentation());
/* 285 */     return alleleElement;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\data\DataTreeBuilder.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */