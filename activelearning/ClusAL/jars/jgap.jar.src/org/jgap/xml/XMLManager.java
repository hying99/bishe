/*     */ package org.jgap.xml;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import javax.xml.parsers.DocumentBuilder;
/*     */ import javax.xml.parsers.DocumentBuilderFactory;
/*     */ import javax.xml.parsers.ParserConfigurationException;
/*     */ import javax.xml.transform.Transformer;
/*     */ import javax.xml.transform.TransformerConfigurationException;
/*     */ import javax.xml.transform.TransformerException;
/*     */ import javax.xml.transform.TransformerFactory;
/*     */ import javax.xml.transform.dom.DOMSource;
/*     */ import javax.xml.transform.stream.StreamResult;
/*     */ import junitx.util.PrivateAccessor;
/*     */ import org.jgap.Chromosome;
/*     */ import org.jgap.Configuration;
/*     */ import org.jgap.Gene;
/*     */ import org.jgap.Genotype;
/*     */ import org.jgap.IChromosome;
/*     */ import org.jgap.InvalidConfigurationException;
/*     */ import org.jgap.Population;
/*     */ import org.jgap.UnsupportedRepresentationException;
/*     */ import org.w3c.dom.Document;
/*     */ import org.w3c.dom.Element;
/*     */ import org.w3c.dom.NodeList;
/*     */ import org.xml.sax.SAXException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class XMLManager
/*     */ {
/*     */   private static final String CVS_REVISION = "$Revision: 1.20 $";
/*     */   private static final String GENOTYPE_TAG = "genotype";
/*     */   private static final String CHROMOSOME_TAG = "chromosome";
/*     */   private static final String GENES_TAG = "genes";
/*     */   private static final String GENE_TAG = "gene";
/*     */   private static final String ALLELE_TAG = "allele";
/*     */   private static final String SIZE_ATTRIBUTE = "size";
/*     */   private static final String CLASS_ATTRIBUTE = "class";
/*     */   private static final DocumentBuilder m_documentCreator;
/*  82 */   private static final Object m_lock = new Object();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static {
/*     */     try {
/*  90 */       m_documentCreator = DocumentBuilderFactory.newInstance().newDocumentBuilder();
/*     */     
/*     */     }
/*  93 */     catch (ParserConfigurationException parserError) {
/*  94 */       throw new RuntimeException("XMLManager: Unable to setup DocumentBuilder: " + parserError.getMessage());
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
/*     */   public static Document representChromosomeAsDocument(IChromosome a_subject) {
/*     */     Document chromosomeDocument;
/* 124 */     synchronized (m_lock) {
/* 125 */       chromosomeDocument = m_documentCreator.newDocument();
/*     */     } 
/* 127 */     Element chromosomeElement = representChromosomeAsElement(a_subject, chromosomeDocument);
/*     */     
/* 129 */     chromosomeDocument.appendChild(chromosomeElement);
/* 130 */     return chromosomeDocument;
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
/*     */   public static Document representGenotypeAsDocument(Genotype a_subject) {
/*     */     Document genotypeDocument;
/* 149 */     synchronized (m_lock) {
/* 150 */       genotypeDocument = m_documentCreator.newDocument();
/*     */     } 
/* 152 */     Element genotypeElement = representGenotypeAsElement(a_subject, genotypeDocument);
/*     */     
/* 154 */     genotypeDocument.appendChild(genotypeElement);
/* 155 */     return genotypeDocument;
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
/*     */   public static Element representGenesAsElement(Gene[] a_geneValues, Document a_xmlDocument) {
/* 176 */     Element genesElement = a_xmlDocument.createElement("genes");
/*     */ 
/*     */ 
/*     */     
/* 180 */     for (int i = 0; i < a_geneValues.length; i++) {
/*     */ 
/*     */       
/* 183 */       Element geneElement = a_xmlDocument.createElement("gene");
/*     */ 
/*     */ 
/*     */       
/* 187 */       geneElement.setAttribute("class", a_geneValues[i].getClass().getName());
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 192 */       Element alleleRepresentation = representAlleleAsElement(a_geneValues[i], a_xmlDocument);
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 197 */       geneElement.appendChild(alleleRepresentation);
/* 198 */       genesElement.appendChild(geneElement);
/*     */     } 
/* 200 */     return genesElement;
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
/*     */   private static Element representAlleleAsElement(Gene a_gene, Document a_xmlDocument) {
/* 214 */     Element alleleElement = a_xmlDocument.createElement("allele");
/* 215 */     alleleElement.setAttribute("class", a_gene.getClass().getName());
/* 216 */     alleleElement.setAttribute("value", a_gene.getPersistentRepresentation());
/* 217 */     return alleleElement;
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
/*     */   public static Element representChromosomeAsElement(IChromosome a_subject, Document a_xmlDocument) {
/* 242 */     Element chromosomeElement = a_xmlDocument.createElement("chromosome");
/*     */     
/* 244 */     chromosomeElement.setAttribute("size", Integer.toString(a_subject.size()));
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 249 */     Element genesElement = representGenesAsElement(a_subject.getGenes(), a_xmlDocument);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 254 */     chromosomeElement.appendChild(genesElement);
/* 255 */     return chromosomeElement;
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
/*     */   public static Element representGenotypeAsElement(Genotype a_subject, Document a_xmlDocument) {
/* 278 */     Population population = a_subject.getPopulation();
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 283 */     Element genotypeTag = a_xmlDocument.createElement("genotype");
/* 284 */     genotypeTag.setAttribute("size", Integer.toString(population.size()));
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 289 */     for (int i = 0; i < population.size(); i++) {
/* 290 */       Element chromosomeElement = representChromosomeAsElement(population.getChromosome(i), a_xmlDocument);
/*     */ 
/*     */       
/* 293 */       genotypeTag.appendChild(chromosomeElement);
/*     */     } 
/* 295 */     return genotypeTag;
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
/*     */   
/*     */   public static Gene[] getGenesFromElement(Configuration a_activeConfiguration, Element a_xmlElement) throws ImproperXMLException, UnsupportedRepresentationException, GeneCreationException {
/* 325 */     if (a_xmlElement == null || !a_xmlElement.getTagName().equals("genes"))
/*     */     {
/* 327 */       throw new ImproperXMLException("Unable to build Chromosome instance from XML Element: given Element is not a 'genes' element.");
/*     */     }
/*     */ 
/*     */     
/* 331 */     List<?> genes = Collections.synchronizedList(new ArrayList());
/*     */ 
/*     */     
/* 334 */     NodeList geneElements = a_xmlElement.getElementsByTagName("gene");
/* 335 */     if (geneElements == null) {
/* 336 */       throw new ImproperXMLException("Unable to build Gene instances from XML Element: 'gene' sub-elements not found.");
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 346 */     int numberOfGeneNodes = geneElements.getLength();
/* 347 */     for (int i = 0; i < numberOfGeneNodes; i++) {
/* 348 */       Gene gene; Element thisGeneElement = (Element)geneElements.item(i);
/* 349 */       thisGeneElement.normalize();
/*     */ 
/*     */ 
/*     */       
/* 353 */       String geneClassName = thisGeneElement.getAttribute("class");
/*     */ 
/*     */       
/* 356 */       Class<?> geneClass = null;
/*     */       try {
/* 358 */         geneClass = Class.forName(geneClassName);
/*     */         try {
/* 360 */           Constructor<?> constr = geneClass.getConstructor(new Class[] { Configuration.class });
/*     */           
/* 362 */           gene = (Gene)constr.newInstance(new Object[] { a_activeConfiguration });
/*     */         
/*     */         }
/* 365 */         catch (NoSuchMethodException nsme) {
/*     */ 
/*     */           
/* 368 */           Constructor<?> constr = geneClass.getConstructor(new Class[0]);
/* 369 */           gene = (Gene)constr.newInstance(new Object[0]);
/* 370 */           gene = (Gene)PrivateAccessor.invoke(gene, "newGeneInternal", new Class[0], new Object[0]);
/*     */         }
/*     */       
/*     */       }
/* 374 */       catch (Throwable e) {
/* 375 */         throw new GeneCreationException(geneClass, e);
/*     */       } 
/*     */ 
/*     */ 
/*     */       
/* 380 */       NodeList children = thisGeneElement.getChildNodes();
/* 381 */       int childrenSize = children.getLength();
/* 382 */       String alleleRepresentation = null;
/* 383 */       for (int j = 0; j < childrenSize; j++) {
/* 384 */         Element alleleElem = (Element)children.item(j);
/* 385 */         if (alleleElem.getTagName().equals("allele")) {
/* 386 */           alleleRepresentation = alleleElem.getAttribute("value");
/*     */         }
/* 388 */         if (children.item(j).getNodeType() == 3) {
/*     */ 
/*     */           
/* 391 */           alleleRepresentation = children.item(j).getNodeValue();
/*     */           
/*     */           break;
/*     */         } 
/*     */       } 
/*     */       
/* 397 */       if (alleleRepresentation == null) {
/* 398 */         throw new ImproperXMLException("Unable to build Gene instance from XML Element: value (allele) is missing representation.");
/*     */       }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*     */       try {
/* 406 */         gene.setValueFromPersistentRepresentation(alleleRepresentation);
/*     */       
/*     */       }
/* 409 */       catch (UnsupportedOperationException e) {
/* 410 */         throw new GeneCreationException("Unable to build Gene because it does not support the setValueFromPersistentRepresentation() method.");
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 416 */       genes.add(gene);
/*     */     } 
/* 418 */     return genes.<Gene>toArray(new Gene[genes.size()]);
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Chromosome getChromosomeFromElement(Configuration a_activeConfiguration, Element a_xmlElement) throws ImproperXMLException, InvalidConfigurationException, UnsupportedRepresentationException, GeneCreationException {
/* 451 */     if (a_xmlElement == null || !a_xmlElement.getTagName().equals("chromosome"))
/*     */     {
/* 453 */       throw new ImproperXMLException("Unable to build Chromosome instance from XML Element: given Element is not a 'chromosome' element.");
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 459 */     Element genesElement = (Element)a_xmlElement.getElementsByTagName("genes").item(0);
/*     */     
/* 461 */     if (genesElement == null) {
/* 462 */       throw new ImproperXMLException("Unable to build Chromosome instance from XML Element: 'genes' sub-element not found.");
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 468 */     Gene[] geneAlleles = getGenesFromElement(a_activeConfiguration, genesElement);
/*     */ 
/*     */ 
/*     */     
/* 472 */     return new Chromosome(a_activeConfiguration, geneAlleles);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Genotype getGenotypeFromElement(Configuration a_activeConfiguration, Element a_xmlElement) throws ImproperXMLException, InvalidConfigurationException, UnsupportedRepresentationException, GeneCreationException {
/* 508 */     if (a_xmlElement == null || !a_xmlElement.getTagName().equals("genotype"))
/*     */     {
/* 510 */       throw new ImproperXMLException("Unable to build Genotype instance from XML Element: given Element is not a 'genotype' element.");
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 517 */     NodeList chromosomes = a_xmlElement.getElementsByTagName("chromosome");
/*     */     
/* 519 */     int numChromosomes = chromosomes.getLength();
/* 520 */     Population population = new Population(a_activeConfiguration, numChromosomes);
/* 521 */     for (int i = 0; i < numChromosomes; i++) {
/* 522 */       population.addChromosome((IChromosome)getChromosomeFromElement(a_activeConfiguration, (Element)chromosomes.item(i)));
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 527 */     return new Genotype(a_activeConfiguration, population);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Genotype getGenotypeFromDocument(Configuration a_activeConfiguration, Document a_xmlDocument) throws ImproperXMLException, InvalidConfigurationException, UnsupportedRepresentationException, GeneCreationException {
/* 564 */     Element rootElement = a_xmlDocument.getDocumentElement();
/* 565 */     if (rootElement == null || !rootElement.getTagName().equals("genotype"))
/*     */     {
/* 567 */       throw new ImproperXMLException("Unable to build Genotype from XML Document: 'genotype' element must be at root of document.");
/*     */     }
/*     */ 
/*     */     
/* 571 */     return getGenotypeFromElement(a_activeConfiguration, rootElement);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Chromosome getChromosomeFromDocument(Configuration a_activeConfiguration, Document a_xmlDocument) throws ImproperXMLException, InvalidConfigurationException, UnsupportedRepresentationException, GeneCreationException {
/* 608 */     Element rootElement = a_xmlDocument.getDocumentElement();
/* 609 */     if (rootElement == null || !rootElement.getTagName().equals("chromosome"))
/*     */     {
/* 611 */       throw new ImproperXMLException("Unable to build Chromosome instance from XML Document: 'chromosome' element must be at root of Document.");
/*     */     }
/*     */ 
/*     */     
/* 615 */     return getChromosomeFromElement(a_activeConfiguration, rootElement);
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
/*     */   public static Document readFile(File file) throws IOException, SAXException {
/* 631 */     return m_documentCreator.parse(file);
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
/*     */   public static void writeFile(Document doc, File file) throws IOException {
/*     */     Transformer transformer;
/* 647 */     TransformerFactory tFactory = TransformerFactory.newInstance();
/*     */ 
/*     */     
/*     */     try {
/* 651 */       transformer = tFactory.newTransformer();
/*     */     }
/* 653 */     catch (TransformerConfigurationException tex) {
/* 654 */       throw new IOException(tex.getMessage());
/*     */     } 
/* 656 */     DOMSource source = new DOMSource(doc);
/* 657 */     FileOutputStream fos = new FileOutputStream(file);
/* 658 */     StreamResult result = new StreamResult(fos);
/*     */     try {
/* 660 */       transformer.transform(source, result);
/* 661 */       fos.close();
/*     */     }
/* 663 */     catch (TransformerException tex) {
/* 664 */       throw new IOException(tex.getMessage());
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\xml\XMLManager.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */