/*     */ package org.jgap;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.StringTokenizer;
/*     */ import java.util.Vector;
/*     */ import org.jgap.util.ChromosomeFitnessComparator;
/*     */ import org.jgap.util.CloneException;
/*     */ import org.jgap.util.ICloneable;
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
/*     */ public class Population
/*     */   implements Serializable, ICloneable, IPersistentRepresentation
/*     */ {
/*     */   private static final String CVS_REVISION = "$Revision: 1.63 $";
/*     */   private List m_chromosomes;
/*     */   private IChromosome m_fittestChromosome;
/*     */   private boolean m_changed;
/*     */   private boolean m_sorted;
/*     */   private Configuration m_config;
/*     */   public static final String CHROM_DELIMITER = "~";
/*     */   public static final String CHROM_DELIMITER_HEADING = "[";
/*     */   public static final String CHROM_DELIMITER_CLOSING = "]";
/*     */   
/*     */   public Population(Configuration a_config) throws InvalidConfigurationException {
/*  74 */     this(a_config, 100);
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
/*     */   public Population(Configuration a_config, IChromosome[] a_chromosomes) throws InvalidConfigurationException {
/*  89 */     this(a_config, a_chromosomes.length);
/*  90 */     synchronized (this.m_chromosomes) {
/*  91 */       for (int i = 0; i < a_chromosomes.length; i++)
/*     */       {
/*     */         
/*  94 */         this.m_chromosomes.add(a_chromosomes[i]);
/*     */       }
/*     */     } 
/*  97 */     setChanged(true);
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
/*     */   public Population(Configuration a_config, IChromosome a_chromosome) throws InvalidConfigurationException {
/* 111 */     this(a_config, 1);
/* 112 */     if (a_chromosome == null) {
/* 113 */       throw new IllegalArgumentException("Chromosome passed must not be null!");
/*     */     }
/* 115 */     synchronized (this.m_chromosomes) {
/* 116 */       this.m_chromosomes.add(a_chromosome);
/*     */     } 
/* 118 */     setChanged(true);
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
/*     */   public Population(Configuration a_config, int a_size) throws InvalidConfigurationException {
/* 132 */     if (a_config == null) {
/* 133 */       throw new InvalidConfigurationException("Configuration must not be null!");
/*     */     }
/* 135 */     this.m_config = a_config;
/*     */     
/* 137 */     this.m_chromosomes = new Vector(a_size);
/* 138 */     setChanged(true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Population() throws InvalidConfigurationException {
/* 149 */     this(Genotype.getStaticConfiguration());
/*     */   }
/*     */   
/*     */   public Configuration getConfiguration() {
/* 153 */     return this.m_config;
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
/*     */   public void addChromosome(IChromosome a_toAdd) {
/* 165 */     if (a_toAdd != null) {
/* 166 */       synchronized (this.m_chromosomes) {
/* 167 */         this.m_chromosomes.add(a_toAdd);
/*     */       } 
/* 169 */       setChanged(true);
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
/*     */   public void addChromosomes(Population a_population) {
/* 183 */     if (a_population != null) {
/* 184 */       synchronized (this.m_chromosomes) {
/* 185 */         this.m_chromosomes.addAll(a_population.getChromosomes());
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 195 */       setChanged(true);
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
/*     */   public void setChromosomes(List a_chromosomes) {
/* 208 */     synchronized (this.m_chromosomes) {
/* 209 */       this.m_chromosomes = a_chromosomes;
/*     */     } 
/* 211 */     setChanged(true);
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
/*     */   public void setChromosome(int a_index, IChromosome a_chromosome) {
/* 226 */     if (this.m_chromosomes.size() == a_index) {
/* 227 */       addChromosome(a_chromosome);
/*     */     } else {
/*     */       
/* 230 */       synchronized (this.m_chromosomes) {
/* 231 */         this.m_chromosomes.set(a_index, a_chromosome);
/*     */       } 
/* 233 */       setChanged(true);
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
/*     */   public List getChromosomes() {
/* 246 */     return this.m_chromosomes;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public IChromosome getChromosome(int a_index) {
/* 257 */     return this.m_chromosomes.get(a_index);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int size() {
/* 267 */     return this.m_chromosomes.size();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Iterator iterator() {
/* 278 */     return this.m_chromosomes.iterator();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public IChromosome[] toChromosomes() {
/* 288 */     return (IChromosome[])this.m_chromosomes.toArray((Object[])new IChromosome[this.m_chromosomes.size()]);
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
/*     */   public IChromosome determineFittestChromosome() {
/*     */     double bestFitness;
/* 303 */     if (!this.m_changed && this.m_fittestChromosome != null) {
/* 304 */       return this.m_fittestChromosome;
/*     */     }
/* 306 */     Iterator<IChromosome> it = this.m_chromosomes.iterator();
/* 307 */     FitnessEvaluator evaluator = getConfiguration().getFitnessEvaluator();
/*     */     
/* 309 */     if (evaluator.isFitter(2.0D, 1.0D)) {
/* 310 */       bestFitness = -1.0D;
/*     */     } else {
/*     */       
/* 313 */       bestFitness = Double.MAX_VALUE;
/*     */     } 
/*     */     
/* 316 */     while (it.hasNext()) {
/* 317 */       IChromosome chrom = it.next();
/* 318 */       double fitness = chrom.getFitnessValue();
/* 319 */       if (evaluator.isFitter(fitness, bestFitness) || this.m_fittestChromosome == null) {
/*     */         
/* 321 */         this.m_fittestChromosome = chrom;
/* 322 */         bestFitness = fitness;
/*     */       } 
/*     */     } 
/* 325 */     setChanged(false);
/* 326 */     return this.m_fittestChromosome;
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
/*     */   public IChromosome determineFittestChromosome(int a_startIndex, int a_endIndex) {
/* 343 */     double bestFitness = -1.0D;
/* 344 */     FitnessEvaluator evaluator = getConfiguration().getFitnessEvaluator();
/*     */     
/* 346 */     int startIndex = Math.max(0, a_startIndex);
/* 347 */     int endIndex = Math.min(this.m_chromosomes.size() - 1, a_endIndex);
/* 348 */     this.m_fittestChromosome = null;
/* 349 */     for (int i = startIndex; i <= endIndex; i++) {
/* 350 */       IChromosome chrom = this.m_chromosomes.get(i);
/* 351 */       double fitness = chrom.getFitnessValue();
/* 352 */       if (evaluator.isFitter(fitness, bestFitness) || this.m_fittestChromosome == null) {
/*     */         
/* 354 */         this.m_fittestChromosome = chrom;
/* 355 */         bestFitness = fitness;
/*     */       } 
/*     */     } 
/* 358 */     return this.m_fittestChromosome;
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
/*     */   protected void setChanged(boolean a_changed) {
/* 371 */     this.m_changed = a_changed;
/* 372 */     setSorted(false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isChanged() {
/* 382 */     return this.m_changed;
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
/*     */   protected void setSorted(boolean a_sorted) {
/* 394 */     this.m_sorted = a_sorted;
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
/*     */   public boolean contains(IChromosome a_chromosome) {
/* 406 */     return this.m_chromosomes.contains(a_chromosome);
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
/*     */   IChromosome removeChromosome(int a_index) {
/* 421 */     if (a_index < 0 || a_index >= size()) {
/* 422 */       throw new IllegalArgumentException("Index must be within bounds!");
/*     */     }
/* 424 */     setChanged(true);
/* 425 */     return this.m_chromosomes.remove(a_index);
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
/*     */   public void keepPopSizeConstant() throws InvalidConfigurationException {
/* 438 */     int popSize = size();
/*     */ 
/*     */     
/* 441 */     int maxSize = getConfiguration().getPopulationSize();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 449 */     while (popSize > maxSize) {
/*     */ 
/*     */ 
/*     */       
/* 453 */       removeChromosome(0);
/* 454 */       popSize--;
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
/*     */   public List determineFittestChromosomes(int a_numberOfChromosomes) {
/* 471 */     int numberOfChromosomes = Math.min(a_numberOfChromosomes, getChromosomes().size());
/*     */     
/* 473 */     if (numberOfChromosomes <= 0) {
/* 474 */       return null;
/*     */     }
/* 476 */     if (!this.m_changed && this.m_sorted) {
/* 477 */       return getChromosomes().subList(0, numberOfChromosomes);
/*     */     }
/*     */     
/* 480 */     sortByFitness();
/*     */     
/* 482 */     return getChromosomes().subList(0, numberOfChromosomes);
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
/*     */   public void sortByFitness() {
/* 497 */     sort((Comparator)new ChromosomeFitnessComparator(getConfiguration().getFitnessEvaluator()));
/*     */     
/* 499 */     setChanged(false);
/* 500 */     setSorted(true);
/* 501 */     this.m_fittestChromosome = this.m_chromosomes.get(0);
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
/*     */   protected void sort(Comparator<?> a_comparator) {
/* 513 */     Collections.sort(getChromosomes(), a_comparator);
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
/*     */   public List getGenome(boolean a_resolveCompositeGenes) {
/* 528 */     List result = new Vector();
/* 529 */     List<IChromosome> chroms = getChromosomes();
/* 530 */     int len = chroms.size();
/* 531 */     for (int i = 0; i < len; i++) {
/* 532 */       IChromosome chrom = chroms.get(i);
/* 533 */       Gene[] genes = chrom.getGenes();
/* 534 */       int len2 = genes.length;
/* 535 */       for (int j = 0; j < len2; j++) {
/* 536 */         Gene gene = genes[j];
/* 537 */         if (a_resolveCompositeGenes && gene instanceof ICompositeGene) {
/* 538 */           addCompositeGene(result, gene);
/*     */         } else {
/*     */           
/* 541 */           addAtomicGene(result, gene);
/*     */         } 
/*     */       } 
/*     */     } 
/* 545 */     return result;
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
/*     */   private void addCompositeGene(List a_result, Gene a_gene) {
/* 559 */     if (a_gene instanceof ICompositeGene) {
/* 560 */       int len = a_gene.size();
/* 561 */       for (int i = 0; i < len; i++) {
/* 562 */         addCompositeGene(a_result, ((ICompositeGene)a_gene).geneAt(i));
/*     */       }
/*     */     } else {
/*     */       
/* 566 */       addAtomicGene(a_result, a_gene);
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
/*     */   private void addAtomicGene(List<Gene> a_result, Gene a_gene) {
/* 580 */     a_result.add(a_gene);
/*     */   }
/*     */   
/*     */   public boolean isSorted() {
/* 584 */     return this.m_sorted;
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
/*     */   public boolean equals(Object a_pop) {
/*     */     try {
/* 598 */       return (compareTo(a_pop) == 0);
/* 599 */     } catch (ClassCastException e) {
/*     */ 
/*     */ 
/*     */       
/* 603 */       return false;
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
/*     */   public int compareTo(Object a_pop) {
/* 621 */     Population other = (Population)a_pop;
/* 622 */     if (a_pop == null) {
/* 623 */       return 1;
/*     */     }
/* 625 */     int size1 = size();
/* 626 */     int size2 = other.size();
/* 627 */     if (size1 != size2) {
/* 628 */       if (size1 < size2) {
/* 629 */         return -1;
/*     */       }
/*     */       
/* 632 */       return 1;
/*     */     } 
/*     */     
/* 635 */     List chroms2 = other.getChromosomes();
/* 636 */     for (int i = 0; i < size1; i++) {
/* 637 */       if (!chroms2.contains(this.m_chromosomes.get(i))) {
/* 638 */         return 1;
/*     */       }
/*     */     } 
/* 641 */     return 0;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object clone() {
/*     */     try {
/* 652 */       Population result = new Population(this.m_config);
/*     */ 
/*     */       
/* 655 */       result.m_changed = true;
/* 656 */       result.m_sorted = false;
/* 657 */       result.m_fittestChromosome = this.m_fittestChromosome;
/* 658 */       int size = this.m_chromosomes.size();
/* 659 */       for (int i = 0; i < size; i++) {
/* 660 */         IChromosome chrom = this.m_chromosomes.get(i);
/* 661 */         result.addChromosome((IChromosome)chrom.clone());
/*     */       } 
/* 663 */       return result;
/* 664 */     } catch (Exception ex) {
/* 665 */       throw new CloneException(ex);
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
/*     */   public void clear() {
/* 678 */     this.m_chromosomes.clear();
/* 679 */     this.m_changed = true;
/* 680 */     this.m_sorted = true;
/* 681 */     this.m_fittestChromosome = null;
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
/*     */   public String getPersistentRepresentation() {
/* 697 */     StringBuffer b = new StringBuffer();
/*     */ 
/*     */ 
/*     */     
/* 701 */     for (int i = 0; i < this.m_chromosomes.size(); i++) {
/* 702 */       IChromosome chrom = this.m_chromosomes.get(i);
/* 703 */       if (!(chrom instanceof IPersistentRepresentation)) {
/* 704 */         throw new RuntimeException("Population contains a chromosome of type " + chrom.getClass().getName() + " which does not implement" + " IPersistentRepresentation!");
/*     */       }
/*     */ 
/*     */ 
/*     */       
/* 709 */       b.append("[");
/* 710 */       b.append(StringKit.encode(chrom.getClass().getName() + "~" + ((IPersistentRepresentation)chrom).getPersistentRepresentation()));
/*     */ 
/*     */ 
/*     */       
/* 714 */       b.append("]");
/*     */     } 
/* 716 */     return b.toString();
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
/* 732 */     if (a_representation != null) {
/*     */       try {
/* 734 */         List r = split(a_representation);
/*     */         
/* 736 */         this.m_chromosomes = new Vector();
/*     */ 
/*     */         
/* 739 */         Iterator<String> iter = r.iterator();
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 744 */         while (iter.hasNext()) {
/* 745 */           String g = StringKit.decode(iter.next());
/* 746 */           StringTokenizer st = new StringTokenizer(g, "~");
/* 747 */           if (st.countTokens() != 2) {
/* 748 */             throw new UnsupportedRepresentationException("In " + g + ", " + "expecting two tokens, separated by " + "~");
/*     */           }
/* 750 */           String clas = st.nextToken();
/* 751 */           String representation = st.nextToken();
/* 752 */           IChromosome chrom = createChromosome(clas, representation);
/* 753 */           this.m_chromosomes.add(chrom);
/*     */         } 
/* 755 */         setChanged(true);
/* 756 */       } catch (Exception ex) {
/* 757 */         throw new UnsupportedRepresentationException(ex.toString());
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
/*     */   protected IChromosome createChromosome(String a_chromClassName, String a_persistentRepresentation) throws Exception {
/* 779 */     Class<?> chromClass = Class.forName(a_chromClassName);
/* 780 */     Constructor<?> constr = chromClass.getConstructor(new Class[] { Configuration.class });
/* 781 */     IChromosome chrom = (IChromosome)constr.newInstance(new Object[] { getConfiguration() });
/*     */     
/* 783 */     ((IPersistentRepresentation)chrom).setValueFromPersistentRepresentation(a_persistentRepresentation);
/*     */     
/* 785 */     return chrom;
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
/* 802 */     List<?> a = Collections.synchronizedList(new ArrayList());
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 808 */     StringTokenizer st = new StringTokenizer(a_string, "[]", true);
/*     */     
/* 810 */     while (st.hasMoreTokens()) {
/* 811 */       if (!st.nextToken().equals("[")) {
/* 812 */         throw new UnsupportedRepresentationException(a_string + " no open tag");
/*     */       }
/* 814 */       String n = st.nextToken();
/* 815 */       if (n.equals("]")) {
/* 816 */         a.add("");
/*     */         continue;
/*     */       } 
/* 819 */       a.add(n);
/* 820 */       if (!st.nextToken().equals("]")) {
/* 821 */         throw new UnsupportedRepresentationException(a_string + " no close tag");
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 826 */     return a;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\Population.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */