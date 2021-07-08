/*     */ package org.jgap.gp.impl;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import org.jgap.InvalidConfigurationException;
/*     */ import org.jgap.RandomGenerator;
/*     */ import org.jgap.gp.CommandGene;
/*     */ import org.jgap.gp.CrossMethod;
/*     */ import org.jgap.gp.IGPProgram;
/*     */ import org.jgap.gp.IMutateable;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class BranchTypingCross
/*     */   extends CrossMethod
/*     */   implements Serializable, Comparable, Cloneable
/*     */ {
/*     */   private static final String CVS_REVISION = "$Revision: 1.15 $";
/*     */   
/*     */   public BranchTypingCross(GPConfiguration a_config) {
/*  28 */     super(a_config);
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
/*     */   public IGPProgram[] operate(IGPProgram i1, IGPProgram i2) {
/*     */     try {
/*  49 */       int[] sizes = new int[i1.size()];
/*  50 */       int totalSize = 0;
/*  51 */       for (int i = 0; i < i1.size(); i++) {
/*     */ 
/*     */         
/*  54 */         sizes[i] = i1.getChromosome(i).getSize(0);
/*  55 */         totalSize += sizes[i];
/*     */       } 
/*     */       
/*  58 */       int nodeNum = getConfiguration().getRandomGenerator().nextInt(totalSize);
/*     */ 
/*     */       
/*     */       int chromosomeNum;
/*     */       
/*  63 */       for (chromosomeNum = 0; chromosomeNum < i1.size(); chromosomeNum++) {
/*  64 */         nodeNum -= sizes[chromosomeNum];
/*  65 */         if (nodeNum < 0) {
/*     */           break;
/*     */         }
/*     */       } 
/*     */       
/*  70 */       ProgramChromosome[] newChromosomes = doCross(i1.getChromosome(chromosomeNum), i2.getChromosome(chromosomeNum));
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*  80 */       IGPProgram[] newIndividuals = { (IGPProgram)new GPProgram(i1), (IGPProgram)new GPProgram(i1) };
/*     */ 
/*     */       
/*  83 */       for (int j = 0; j < i1.size(); j++) {
/*  84 */         if (j != chromosomeNum) {
/*  85 */           newIndividuals[0].setChromosome(j, i1.getChromosome(j));
/*  86 */           newIndividuals[1].setChromosome(j, i2.getChromosome(j));
/*     */         } else {
/*     */           
/*  89 */           newIndividuals[0].setChromosome(j, newChromosomes[0]);
/*  90 */           newIndividuals[1].setChromosome(j, newChromosomes[1]);
/*     */         } 
/*  92 */       }  return newIndividuals;
/*  93 */     } catch (InvalidConfigurationException iex) {
/*  94 */       return null;
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
/*     */   protected ProgramChromosome[] doCross(ProgramChromosome c0, ProgramChromosome c1) throws InvalidConfigurationException {
/*     */     int p0, p1;
/* 122 */     ProgramChromosome[] c = { c0, c1 };
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 127 */     RandomGenerator random = getConfiguration().getRandomGenerator();
/* 128 */     if (random.nextFloat() < getConfiguration().getFunctionProb()) {
/*     */       
/* 130 */       int nf = c0.numFunctions();
/* 131 */       if (nf == 0)
/*     */       {
/* 133 */         return c;
/*     */       }
/* 135 */       int fctIndex = random.nextInt(nf);
/* 136 */       p0 = c0.getFunction(fctIndex);
/*     */     
/*     */     }
/*     */     else {
/*     */       
/* 141 */       p0 = c0.getTerminal(random.nextInt(c0.numTerminals()));
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 146 */     CommandGene command = c0.getNode(p0);
/* 147 */     if (IMutateable.class.isInstance(command)) {
/* 148 */       IMutateable term = (IMutateable)command;
/* 149 */       command = term.applyMutation(0, 0.5D);
/* 150 */       if (command != null)
/*     */       {
/*     */         
/* 153 */         if (c0.getCommandOfClass(0, command.getClass()) >= 0) {
/* 154 */           c0.setGene(p0, command);
/*     */         }
/*     */       }
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 162 */     CommandGene nodeP0 = c0.getNode(p0);
/* 163 */     Class type_ = nodeP0.getReturnType();
/* 164 */     int subType = nodeP0.getSubReturnType();
/* 165 */     if (random.nextFloat() < getConfiguration().getFunctionProb()) {
/*     */       
/* 167 */       int nf = c1.numFunctions(type_, subType);
/* 168 */       if (nf == 0)
/*     */       {
/*     */         
/* 171 */         return c;
/*     */       }
/* 173 */       p1 = c1.getFunction(random.nextInt(nf), type_, subType);
/*     */     
/*     */     }
/*     */     else {
/*     */       
/* 178 */       int nt = c1.numTerminals(type_, subType);
/* 179 */       if (nt == 0)
/*     */       {
/*     */         
/* 182 */         return c;
/*     */       }
/* 184 */       p1 = c1.getTerminal(random.nextInt(c1.numTerminals(type_, subType)), type_, subType);
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 190 */     command = c1.getNode(p1);
/* 191 */     if (IMutateable.class.isInstance(command)) {
/* 192 */       IMutateable term = (IMutateable)command;
/* 193 */       command = term.applyMutation(0, 0.5D);
/* 194 */       if (command != null)
/*     */       {
/*     */         
/* 197 */         if (c0.getCommandOfClass(0, command.getClass()) >= 0) {
/* 198 */           c1.setGene(p1, command);
/*     */         }
/*     */       }
/*     */     } 
/*     */     
/* 203 */     int s0 = c0.getSize(p0);
/* 204 */     int s1 = c1.getSize(p1);
/* 205 */     int d0 = c0.getDepth(p0);
/* 206 */     int d1 = c1.getDepth(p1);
/* 207 */     int c0s = c0.getSize(0);
/* 208 */     int c1s = c1.getSize(0);
/*     */ 
/*     */ 
/*     */     
/* 212 */     if (d0 - 1 + s1 > getConfiguration().getMaxCrossoverDepth() || c0s - p0 - s0 < 0 || p0 + s1 + c0s - p0 - s0 >= (c0.getFunctions()).length) {
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 217 */       c[0] = c1;
/*     */     } else {
/*     */       
/* 220 */       c[0] = new ProgramChromosome(getConfiguration(), (c0.getFunctions()).length, c[0].getFunctionSet(), c[0].getArgTypes(), c0.getIndividual());
/*     */ 
/*     */ 
/*     */       
/* 224 */       System.arraycopy(c0.getFunctions(), 0, c[0].getFunctions(), 0, p0);
/* 225 */       System.arraycopy(c1.getFunctions(), p1, c[0].getFunctions(), p0, s1);
/* 226 */       System.arraycopy(c0.getFunctions(), p0 + s0, c[0].getFunctions(), p0 + s1, c0s - p0 - s0);
/*     */       
/* 228 */       c[0].redepth();
/*     */     } 
/*     */ 
/*     */     
/* 232 */     if (d1 - 1 + s0 > getConfiguration().getMaxCrossoverDepth() || c1s - p1 - s1 < 0 || p1 + s0 + c1s - p1 - s1 >= (c1.getFunctions()).length) {
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 237 */       c[1] = c0;
/*     */     } else {
/*     */       
/* 240 */       c[1] = new ProgramChromosome(getConfiguration(), (c1.getFunctions()).length, c[1].getFunctionSet(), c[1].getArgTypes(), c1.getIndividual());
/*     */ 
/*     */       
/* 243 */       System.arraycopy(c1.getFunctions(), 0, c[1].getFunctions(), 0, p1);
/* 244 */       System.arraycopy(c0.getFunctions(), p0, c[1].getFunctions(), p1, s0);
/* 245 */       System.arraycopy(c1.getFunctions(), p1 + s1, c[1].getFunctions(), p1 + s0, c1s - p1 - s1);
/*     */       
/* 247 */       c[1].redepth();
/*     */     } 
/* 249 */     return c;
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
/*     */   public int compareTo(Object a_other) {
/* 262 */     BranchTypingCross other = (BranchTypingCross)a_other;
/* 263 */     if (other == null) {
/* 264 */       return 1;
/*     */     }
/* 266 */     return 0;
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
/*     */   public boolean equals(Object a_other) {
/*     */     try {
/* 281 */       BranchTypingCross other = (BranchTypingCross)a_other;
/* 282 */       if (other == null) {
/* 283 */         return false;
/*     */       }
/*     */       
/* 286 */       return true;
/*     */     }
/* 288 */     catch (ClassCastException cex) {
/* 289 */       return false;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object clone() {
/* 300 */     BranchTypingCross result = new BranchTypingCross(getConfiguration());
/* 301 */     return result;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\gp\impl\BranchTypingCross.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */