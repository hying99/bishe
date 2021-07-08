/*     */ package org.jgap.impl;
/*     */ 
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import java.util.TreeSet;
/*     */ import org.jgap.BaseGeneticOperator;
/*     */ import org.jgap.Configuration;
/*     */ import org.jgap.Gene;
/*     */ import org.jgap.Genotype;
/*     */ import org.jgap.IChromosome;
/*     */ import org.jgap.InvalidConfigurationException;
/*     */ import org.jgap.Population;
/*     */ import org.jgap.RandomGenerator;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class GreedyCrossover
/*     */   extends BaseGeneticOperator
/*     */ {
/*     */   private static final String CVS_REVISION = "$Revision: 1.27 $";
/*     */   boolean ASSERTIONS = true;
/*  64 */   private int m_startOffset = 1;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public GreedyCrossover() throws InvalidConfigurationException {
/*  79 */     super(Genotype.getStaticConfiguration());
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
/*     */   public GreedyCrossover(Configuration a_configuration) throws InvalidConfigurationException {
/*  93 */     super(a_configuration);
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
/*     */   public double distance(Object a_from, Object a_to) {
/* 107 */     IntegerGene from = (IntegerGene)a_from;
/* 108 */     IntegerGene to = (IntegerGene)a_to;
/* 109 */     return Math.abs(to.intValue() - from.intValue());
/*     */   }
/*     */ 
/*     */   
/*     */   public void operate(Population a_population, List<IChromosome> a_candidateChromosomes) {
/* 114 */     int size = Math.min(getConfiguration().getPopulationSize(), a_population.size());
/*     */     
/* 116 */     int numCrossovers = size / 2;
/* 117 */     RandomGenerator generator = getConfiguration().getRandomGenerator();
/*     */ 
/*     */ 
/*     */     
/* 121 */     for (int i = 0; i < numCrossovers; i++) {
/* 122 */       IChromosome firstMate = (IChromosome)a_population.getChromosome(generator.nextInt(size)).clone();
/*     */ 
/*     */       
/* 125 */       IChromosome secondMate = (IChromosome)a_population.getChromosome(generator.nextInt(size)).clone();
/*     */ 
/*     */       
/* 128 */       operate(firstMate, secondMate);
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 133 */       a_candidateChromosomes.add(firstMate);
/* 134 */       a_candidateChromosomes.add(secondMate);
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
/*     */   public void operate(IChromosome a_firstMate, IChromosome a_secondMate) {
/* 150 */     Gene[] g1 = a_firstMate.getGenes();
/* 151 */     Gene[] g2 = a_secondMate.getGenes();
/*     */     
/*     */     try {
/* 154 */       Gene[] c1 = operate(g1, g2);
/* 155 */       Gene[] c2 = operate(g2, g1);
/* 156 */       a_firstMate.setGenes(c1);
/* 157 */       a_secondMate.setGenes(c2);
/*     */     }
/* 159 */     catch (InvalidConfigurationException cex) {
/* 160 */       throw new Error("Error occured while operating on:" + a_firstMate + " and " + a_secondMate + ". First " + this.m_startOffset + " genes were excluded " + "from crossover. Error message: " + cex.getMessage());
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Gene[] operate(Gene[] a_g1, Gene[] a_g2) {
/* 170 */     int n = a_g1.length;
/* 171 */     LinkedList<Gene> out = new LinkedList();
/* 172 */     TreeSet<Gene> not_picked = new TreeSet();
/* 173 */     out.add(a_g1[this.m_startOffset]); int j;
/* 174 */     for (j = this.m_startOffset + 1; j < n; j++) {
/* 175 */       if (this.ASSERTIONS && not_picked.contains(a_g1[j])) {
/* 176 */         throw new Error("All genes must be different for " + getClass().getName() + ". The gene " + a_g1[j] + "[" + j + "] occurs more " + "than once in one of the chromosomes. ");
/*     */       }
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 182 */       not_picked.add(a_g1[j]);
/*     */     } 
/* 184 */     if (this.ASSERTIONS) {
/* 185 */       if (a_g1.length != a_g2.length) {
/* 186 */         throw new Error("Chromosome sizes must be equal");
/*     */       }
/* 188 */       for (j = this.m_startOffset; j < n; j++) {
/* 189 */         if (!not_picked.contains(a_g2[j]) && 
/* 190 */           !a_g1[this.m_startOffset].equals(a_g2[j])) {
/* 191 */           throw new Error("Chromosome gene sets must be identical. First gene set: " + a_g1 + ", second gene set: " + a_g2);
/*     */         }
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 198 */     while (not_picked.size() > 1) {
/* 199 */       Gene picked, other; boolean pick1; Gene last = out.getLast();
/* 200 */       Gene n1 = findNext(a_g1, last);
/* 201 */       Gene n2 = findNext(a_g2, last);
/*     */ 
/*     */       
/* 204 */       if (n1 == null) {
/* 205 */         pick1 = false;
/*     */       }
/* 207 */       else if (n2 == null) {
/* 208 */         pick1 = true;
/*     */       } else {
/*     */         
/* 211 */         pick1 = (distance(last, n1) < distance(last, n2));
/*     */       } 
/* 213 */       if (pick1) {
/* 214 */         picked = n1;
/* 215 */         other = n2;
/*     */       } else {
/*     */         
/* 218 */         picked = n2;
/* 219 */         other = n1;
/*     */       } 
/* 221 */       if (out.contains(picked)) {
/* 222 */         picked = other;
/*     */       }
/* 224 */       if (picked == null || out.contains(picked))
/*     */       {
/* 226 */         picked = not_picked.first();
/*     */       }
/* 228 */       out.add(picked);
/* 229 */       not_picked.remove(picked);
/*     */     } 
/* 231 */     if (this.ASSERTIONS && not_picked.size() != 1) {
/* 232 */       throw new Error("Given Gene not correctly created (must have length > 1)");
/*     */     }
/*     */     
/* 235 */     out.add(not_picked.last());
/* 236 */     Gene[] g = new Gene[n];
/* 237 */     Iterator<Gene> gi = out.iterator(); int i;
/* 238 */     for (i = 0; i < this.m_startOffset; i++) {
/* 239 */       g[i] = a_g1[i];
/*     */     }
/* 241 */     if (this.ASSERTIONS && 
/* 242 */       out.size() != g.length - this.m_startOffset) {
/* 243 */       throw new Error("Unexpected internal error. These two must be equal: " + out.size() + " and " + (g.length - this.m_startOffset) + ", g.length " + g.length + ", start offset " + this.m_startOffset);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 249 */     for (i = this.m_startOffset; i < g.length; i++) {
/* 250 */       g[i] = gi.next();
/*     */     }
/* 252 */     return g;
/*     */   }
/*     */   
/*     */   protected Gene findNext(Gene[] a_g, Gene a_x) {
/* 256 */     for (int i = this.m_startOffset; i < a_g.length - 1; i++) {
/* 257 */       if (a_g[i].equals(a_x)) {
/* 258 */         return a_g[i + 1];
/*     */       }
/*     */     } 
/* 261 */     return null;
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
/*     */   public void setStartOffset(int a_offset) {
/* 273 */     this.m_startOffset = a_offset;
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
/*     */   public int getStartOffset() {
/* 285 */     return this.m_startOffset;
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
/*     */   public int compareTo(Object a_other) {
/* 300 */     if (a_other == null) {
/* 301 */       return 1;
/*     */     }
/* 303 */     GreedyCrossover op = (GreedyCrossover)a_other;
/* 304 */     if (getStartOffset() < op.getStartOffset())
/*     */     {
/* 306 */       return 1;
/*     */     }
/* 308 */     if (getStartOffset() > op.getStartOffset()) {
/* 309 */       return -1;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 314 */     return 0;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\impl\GreedyCrossover.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */