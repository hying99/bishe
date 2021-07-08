/*     */ package clus.algo.split;
/*     */ 
/*     */ import clus.algo.rules.ClusRuleHeuristicDispersion;
/*     */ import clus.data.type.NominalAttrType;
/*     */ import clus.main.ClusStatManager;
/*     */ import clus.main.Settings;
/*     */ import clus.model.test.NodeTest;
/*     */ import clus.model.test.SubsetTest;
/*     */ import clus.statistic.ClusStatistic;
/*     */ import clus.statistic.CombStat;
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
/*     */ public class SubsetSplit
/*     */   extends NominalSplit
/*     */ {
/*     */   ClusStatistic m_PStat;
/*     */   ClusStatistic m_CStat;
/*     */   ClusStatistic m_MStat;
/*     */   ClusStatManager m_StatManager;
/*     */   
/*     */   public void initialize(ClusStatManager manager) {
/*  39 */     this.m_PStat = manager.createClusteringStat();
/*  40 */     this.m_CStat = this.m_PStat.cloneStat();
/*  41 */     this.m_MStat = this.m_PStat.cloneStat();
/*  42 */     this.m_StatManager = manager;
/*     */   }
/*     */   
/*     */   public void setSDataSize(int size) {
/*  46 */     this.m_PStat.setSDataSize(size);
/*  47 */     this.m_CStat.setSDataSize(size);
/*  48 */     this.m_MStat.setSDataSize(size);
/*     */   }
/*     */   
/*     */   public ClusStatManager getStatManager() {
/*  52 */     return this.m_StatManager;
/*     */   }
/*     */   
/*     */   public void showTest(NominalAttrType type, boolean[] isin, int add, double mheur, ClusStatistic tot, ClusStatistic pos) {
/*  56 */     int count = 0;
/*  57 */     System.out.print(type.getName() + " in {");
/*  58 */     for (int i = 0; i < type.getNbValues(); i++) {
/*  59 */       if (isin[i] || i == add) {
/*  60 */         if (count != 0) {
/*  61 */           System.out.print(",");
/*     */         }
/*  63 */         System.out.print(type.getValue(i));
/*  64 */         count++;
/*     */       } 
/*     */     } 
/*  67 */     tot.calcMean();
/*  68 */     pos.calcMean();
/*     */     
/*  70 */     System.out.println("}: " + mheur);
/*     */   }
/*     */ 
/*     */   
/*     */   public void findSplit(CurrentBestTestAndHeuristic node, NominalAttrType type) {
/*  75 */     double unk_freq = 0.0D;
/*  76 */     int nbvalues = type.getNbValues();
/*     */ 
/*     */ 
/*     */     
/*  80 */     boolean[] isin = new boolean[nbvalues];
/*  81 */     boolean acceptable = true;
/*     */     
/*  83 */     if (type.hasMissing()) {
/*  84 */       ClusStatistic unknown = node.m_TestStat[nbvalues];
/*  85 */       this.m_MStat.copy(node.m_TotStat);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*  93 */       this.m_MStat.subtractFromThis(unknown);
/*  94 */       unk_freq = unknown.m_SumWeight / node.m_TotStat.m_SumWeight;
/*     */     } else {
/*  96 */       this.m_MStat.copy(node.m_TotStat);
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 105 */     int card = 0;
/* 106 */     double pos_freq = 0.0D;
/* 107 */     double bheur = Double.NEGATIVE_INFINITY;
/*     */     
/* 109 */     if (nbvalues == 2 && (!getStatManager().isRuleInduceOnly() || 
/* 110 */       getStatManager().getSettings().isConstrainedToFirstAttVal()))
/*     */     
/* 112 */     { card = 1;
/* 113 */       isin[0] = true;
/* 114 */       ClusStatistic CStat = node.m_TestStat[0];
/* 115 */       bheur = node.calcHeuristic(this.m_MStat, CStat);
/*     */       
/* 117 */       pos_freq = CStat.m_SumWeight / this.m_MStat.m_SumWeight; }
/* 118 */     else { getStatManager(); if (ClusStatManager.getMode() == 7 && Settings.m_PhylogenySequence.getValue() == 0) {
/*     */ 
/*     */ 
/*     */         
/* 122 */         boolean[] valid = new boolean[nbvalues];
/*     */         
/*     */         int j;
/* 125 */         for (j = 0; j < nbvalues; j++) {
/* 126 */           this.m_PStat.reset();
/* 127 */           this.m_PStat.add(node.m_TestStat[j]);
/* 128 */           double mheur = node.calcHeuristic(this.m_MStat, this.m_PStat);
/* 129 */           if (mheur > bheur) {
/* 130 */             bheur = mheur;
/* 131 */             isin = new boolean[nbvalues];
/* 132 */             isin[j] = true;
/* 133 */             card = 1;
/*     */             
/* 135 */             pos_freq = this.m_PStat.m_SumWeight / this.m_MStat.m_SumWeight;
/*     */           } 
/* 137 */           if (mheur > Double.NEGATIVE_INFINITY) {
/* 138 */             valid[j] = true;
/*     */           }
/*     */         } 
/*     */         
/* 142 */         for (j = 0; j < nbvalues; j++) {
/* 143 */           if (valid[j]) {
/* 144 */             for (int k = j + 1; k < nbvalues; k++) {
/* 145 */               if (valid[k]) {
/* 146 */                 this.m_PStat.reset();
/* 147 */                 this.m_CStat.copy(this.m_PStat);
/* 148 */                 this.m_CStat.add(node.m_TestStat[j]);
/* 149 */                 this.m_PStat.add(node.m_TestStat[j]);
/* 150 */                 this.m_PStat.add(node.m_TestStat[k]);
/*     */                 
/* 152 */                 ClusStatistic[] csarray = new ClusStatistic[2];
/* 153 */                 csarray[0] = this.m_PStat;
/* 154 */                 csarray[1] = this.m_CStat;
/* 155 */                 double mheur = node.calcHeuristic(this.m_MStat, csarray, 2);
/*     */                 
/* 157 */                 if (mheur > bheur) {
/* 158 */                   bheur = mheur;
/* 159 */                   isin = new boolean[nbvalues];
/* 160 */                   isin[j] = true;
/* 161 */                   isin[k] = true;
/* 162 */                   card = 2;
/*     */                   
/* 164 */                   pos_freq = this.m_PStat.m_SumWeight / this.m_MStat.m_SumWeight;
/*     */                 }
/*     */               
/*     */               } 
/*     */             } 
/*     */           }
/*     */         } 
/*     */       } else {
/*     */         
/* 173 */         this.m_PStat.reset();
/* 174 */         int bvalue = 0;
/* 175 */         if (this.m_PStat instanceof CombStat && ((CombStat)this.m_PStat)
/* 176 */           .getSettings().isHeurRuleDist()) {
/* 177 */           ((ClusRuleHeuristicDispersion)node.m_Heuristic).setDataIndexes(new int[0]);
/*     */         }
/* 179 */         boolean allowSubsetSplits = getStatManager().getSettings().isNominalSubsetTests();
/* 180 */         while (bvalue != -1 && card + 1 < nbvalues) {
/* 181 */           bvalue = -1;
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 186 */           for (int j = 0; j < nbvalues; j++) {
/* 187 */             if (!isin[j]) {
/*     */               
/* 189 */               this.m_CStat.copy(this.m_PStat);
/* 190 */               this.m_CStat.add(node.m_TestStat[j]);
/* 191 */               if (this.m_PStat instanceof CombStat && ((CombStat)this.m_PStat)
/* 192 */                 .getSettings().isHeurRuleDist()) {
/* 193 */                 boolean[] arrayOfBoolean = new boolean[nbvalues];
/* 194 */                 for (int i = 0; i < nbvalues; i++) {
/* 195 */                   arrayOfBoolean[i] = isin[i];
/*     */                 }
/* 197 */                 arrayOfBoolean[j] = true;
/* 198 */                 ((ClusRuleHeuristicDispersion)node.m_Heuristic).setDataIndexes(arrayOfBoolean);
/*     */               } 
/*     */ 
/*     */               
/* 202 */               boolean[] isin_current = new boolean[nbvalues];
/* 203 */               for (int k = 0; k < nbvalues; k++) {
/* 204 */                 isin_current[k] = isin[k];
/*     */               }
/* 206 */               isin_current[j] = true;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */               
/* 212 */               double mheur = node.calcHeuristic(this.m_MStat, this.m_CStat);
/*     */ 
/*     */               
/* 215 */               if (mheur > bheur) {
/* 216 */                 bheur = mheur;
/* 217 */                 bvalue = j;
/*     */                 
/* 219 */                 pos_freq = this.m_CStat.m_SumWeight / this.m_MStat.m_SumWeight;
/* 220 */                 node.checkAcceptable(this.m_MStat, this.m_CStat);
/*     */               } 
/*     */             } 
/*     */           } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 229 */           if (bvalue != -1) {
/*     */ 
/*     */             
/* 232 */             card++;
/* 233 */             isin[bvalue] = true;
/* 234 */             this.m_PStat.add(node.m_TestStat[bvalue]);
/*     */           } 
/*     */           
/* 237 */           if (!allowSubsetSplits) {
/*     */             break;
/*     */           }
/*     */         } 
/*     */       }  }
/*     */ 
/*     */ 
/*     */     
/* 245 */     if (bheur > node.m_BestHeur + 1.0E-6D) {
/*     */ 
/*     */       
/* 248 */       node.m_UnknownFreq = unk_freq;
/* 249 */       node.m_BestHeur = bheur;
/* 250 */       node.m_TestType = 1;
/* 251 */       node.m_BestTest = (NodeTest)new SubsetTest(type, card, isin, pos_freq);
/* 252 */       node.resetAlternativeBest();
/*     */     }
/* 254 */     else if (getStatManager().getSettings().showAlternativeSplits() && ((bheur > node.m_BestHeur - 1.0E-6D && bheur < node.m_BestHeur + 1.0E-6D) || bheur == Double.POSITIVE_INFINITY)) {
/*     */ 
/*     */ 
/*     */       
/* 258 */       node.addAlternativeBest((NodeTest)new SubsetTest(type, card, isin, pos_freq));
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void findRandomSplit(CurrentBestTestAndHeuristic node, NominalAttrType type, Random rn) {
/* 264 */     double unk_freq = 0.0D;
/* 265 */     int nbvalues = type.getNbValues();
/* 266 */     boolean[] isin = new boolean[nbvalues];
/*     */     
/* 268 */     if (type.hasMissing()) {
/* 269 */       ClusStatistic unknown = node.m_TestStat[nbvalues];
/* 270 */       this.m_MStat.copy(node.m_TotStat);
/* 271 */       this.m_MStat.subtractFromThis(unknown);
/* 272 */       unk_freq = unknown.m_SumWeight / node.m_TotStat.m_SumWeight;
/*     */     } else {
/* 274 */       this.m_MStat.copy(node.m_TotStat);
/*     */     } 
/* 276 */     int card = 0;
/* 277 */     double pos_freq = 0.0D;
/*     */     
/*     */     while (true) {
/* 280 */       for (int i = 0; i < isin.length; i++) {
/* 281 */         isin[i] = rn.nextBoolean();
/*     */       }
/* 283 */       int sum = 0;
/* 284 */       for (int j = 0; j < isin.length; j++) {
/* 285 */         if (isin[j]) {
/* 286 */           sum++;
/*     */         }
/*     */       } 
/* 289 */       if (sum != 0 && sum != nbvalues) {
/* 290 */         card = sum;
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 295 */         this.m_PStat.reset();
/* 296 */         for (int k = 0; k < nbvalues; k++) {
/* 297 */           if (isin[k]) {
/* 298 */             this.m_PStat.add(node.m_TestStat[k]);
/*     */           }
/*     */         } 
/* 301 */         pos_freq = this.m_PStat.m_SumWeight / this.m_MStat.m_SumWeight;
/* 302 */         node.m_UnknownFreq = unk_freq;
/* 303 */         node.m_BestHeur = node.calcHeuristic(this.m_MStat, this.m_PStat);
/* 304 */         node.m_TestType = 1;
/* 305 */         node.m_BestTest = (NodeTest)new SubsetTest(type, card, isin, pos_freq);
/*     */         return;
/*     */       } 
/*     */     } 
/*     */   }
/*     */   public void findRandomPertSplit(CurrentBestTestAndHeuristic node, NominalAttrType type, Random rn, int valueincl, int valueexcl) {
/* 311 */     int nbvalues = type.getNbValues();
/* 312 */     boolean[] isin = new boolean[nbvalues];
/*     */ 
/*     */     
/* 315 */     int card = 0;
/* 316 */     double pos_freq = 0.0D;
/*     */     while (true) {
/* 318 */       for (int i = 0; i < isin.length; i++) {
/* 319 */         isin[i] = rn.nextBoolean();
/*     */       }
/* 321 */       isin[valueincl] = true;
/* 322 */       isin[valueexcl] = false;
/* 323 */       int sum = 0;
/* 324 */       for (int j = 0; j < isin.length; j++) {
/* 325 */         if (isin[j]) {
/* 326 */           sum++;
/*     */         }
/*     */       } 
/* 329 */       if (sum != 0 && sum != nbvalues) {
/* 330 */         card = sum;
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 335 */         this.m_PStat.reset();
/* 336 */         for (int k = 0; k < nbvalues; k++) {
/* 337 */           if (isin[k]) {
/* 338 */             this.m_PStat.add(node.m_TestStat[k]);
/*     */           }
/*     */         } 
/* 341 */         node.m_TestType = 1;
/* 342 */         node.m_BestTest = (NodeTest)new SubsetTest(type, card, isin, pos_freq);
/*     */         return;
/*     */       } 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\algo\split\SubsetSplit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */