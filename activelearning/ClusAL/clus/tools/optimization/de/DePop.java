/*     */ package clus.tools.optimization.de;
/*     */ 
/*     */ import clus.main.ClusStatManager;
/*     */ import clus.main.Settings;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Random;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DePop
/*     */ {
/*     */   public ArrayList<DeInd> m_Inds;
/*     */   private DeProbl m_Probl;
/*     */   private Random m_Rand;
/*     */   private ClusStatManager m_StatMgr;
/*     */   
/*     */   public DePop(ClusStatManager stat_mgr, DeProbl probl) {
/*  45 */     this.m_Probl = probl;
/*  46 */     this.m_StatMgr = stat_mgr;
/*  47 */     this.m_Rand = new Random(getSettings().getOptDESeed());
/*  48 */     this.m_Inds = new ArrayList<>(getSettings().getOptDEPopSize());
/*  49 */     for (int i = 0; i < getSettings().getOptDEPopSize(); i++) {
/*  50 */       DeInd ind = new DeInd();
/*  51 */       this.m_Inds.add(ind);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void createFirstPop() {
/*  56 */     for (int i = 0; i < getSettings().getOptDEPopSize(); i++) {
/*  57 */       ((DeInd)this.m_Inds.get(i)).setGenes(this.m_Probl.getRandVector(this.m_Rand));
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int evaluatePop(int num_eval) {
/*  66 */     int result = num_eval;
/*  67 */     for (int i = 0; i < this.m_Inds.size(); i++) {
/*  68 */       result = ((DeInd)this.m_Inds.get(i)).evaluate(this.m_Probl, result);
/*     */     }
/*  70 */     return result;
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
/*     */   public ArrayList<Double> getCandidate(int parent) {
/*     */     int i1, i2, i3;
/*  84 */     ArrayList<Double> result = new ArrayList<>(this.m_Probl.getNumVar());
/*  85 */     for (int k = 0; k < this.m_Probl.getNumVar(); k++) {
/*  86 */       result.add(k, new Double(0.0D));
/*     */     }
/*     */     
/*     */     do {
/*  90 */       i1 = (int)(getSettings().getOptDEPopSize() * this.m_Rand.nextDouble());
/*  91 */     } while (i1 == parent);
/*     */     do {
/*  93 */       i2 = (int)(getSettings().getOptDEPopSize() * this.m_Rand.nextDouble());
/*  94 */     } while (i2 == parent || i2 == i1);
/*     */     do {
/*  96 */       i3 = (int)(getSettings().getOptDEPopSize() * this.m_Rand.nextDouble());
/*  97 */     } while (i3 == parent || i3 == i1 || i3 == i2);
/*     */ 
/*     */     
/* 100 */     int i_rand = (int)(this.m_Probl.getNumVar() * this.m_Rand.nextDouble());
/* 101 */     int i = i_rand;
/*     */ 
/*     */ 
/*     */     
/* 105 */     for (int kVariable = 0; kVariable < this.m_Probl.getNumVar(); kVariable++) {
/*     */       
/* 107 */       result.set(i, ((DeInd)this.m_Inds.get(parent)).getGenes().get(i));
/*     */ 
/*     */       
/* 110 */       if (this.m_Rand.nextDouble() < getSettings().getOptDECrossProb() || i == i_rand) {
/* 111 */         result.set(i, new Double(
/* 112 */               getSettings().getOptDEWeight() * (((Double)((DeInd)this.m_Inds
/* 113 */               .get(i1)).getGenes().get(i)).doubleValue() - ((Double)((DeInd)this.m_Inds
/* 114 */               .get(i2)).getGenes().get(i)).doubleValue()) + ((Double)((DeInd)this.m_Inds
/* 115 */               .get(i3)).getGenes().get(i)).doubleValue()));
/*     */       }
/*     */ 
/*     */ 
/*     */       
/* 120 */       if (this.m_Rand.nextDouble() < getSettings().getOptDEProbMutationZero()) {
/* 121 */         result.set(i, new Double(0.0D));
/*     */       }
/*     */ 
/*     */       
/* 125 */       if (this.m_Rand.nextDouble() < getSettings().getOptDEProbMutationNonZero()) {
/* 126 */         result.set(i, new Double(this.m_Probl.getRandValueInRange(this.m_Rand, i)));
/*     */       }
/*     */       
/* 129 */       i = ++i % this.m_Probl.getNumVar();
/*     */     } 
/*     */     
/* 132 */     return this.m_Probl.getRoundVector(result);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void sortPopRandom() {
/* 141 */     ArrayList<DeInd> inds = new ArrayList<>(getSettings().getOptDEPopSize());
/*     */     
/* 143 */     ArrayList<Integer> indexes = new ArrayList<>(getSettings().getOptDEPopSize());
/*     */     int i;
/* 145 */     for (i = 0; i < getSettings().getOptDEPopSize(); i++) {
/* 146 */       inds.add(new DeInd());
/* 147 */       indexes.add(new Integer(i));
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 153 */     for (i = 0; i < getSettings().getOptDEPopSize(); i++) {
/* 154 */       int n = (int)(indexes.size() * this.m_Rand.nextDouble());
/*     */       
/* 156 */       ((DeInd)inds.get(i)).copy(this.m_Inds
/* 157 */           .get(((Integer)indexes
/* 158 */             .get(n)).intValue()));
/* 159 */       indexes.remove(n);
/*     */     } 
/*     */ 
/*     */     
/* 163 */     for (i = 0; i < getSettings().getOptDEPopSize(); i++) {
/* 164 */       ((DeInd)this.m_Inds.get(i)).copy(inds.get(i));
/*     */     }
/*     */   }
/*     */   
/*     */   public String getPopString() {
/* 169 */     String result = "";
/* 170 */     for (int i = 0; i < this.m_Inds.size(); i++) {
/* 171 */       result = result + ((DeInd)this.m_Inds.get(i)).getIndString();
/*     */     }
/* 173 */     return result;
/*     */   }
/*     */   
/*     */   public Settings getSettings() {
/* 177 */     return this.m_StatMgr.getSettings();
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\tools\optimization\de\DePop.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */