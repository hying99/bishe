/*     */ package clus.algo.split;
/*     */ 
/*     */ import clus.data.attweights.ClusAttributeWeights;
/*     */ import clus.data.rows.RowData;
/*     */ import clus.data.type.ClusAttrType;
/*     */ import clus.heuristic.ClusHeuristic;
/*     */ import clus.main.ClusStatManager;
/*     */ import clus.main.Settings;
/*     */ import clus.model.test.InverseNumericTest;
/*     */ import clus.model.test.NodeTest;
/*     */ import clus.model.test.NumericTest;
/*     */ import clus.statistic.ClusStatistic;
/*     */ import clus.util.ClusException;
/*     */ import java.util.ArrayList;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class CurrentBestTestAndHeuristic
/*     */ {
/*     */   public static final int TYPE_NONE = -1;
/*     */   public static final int TYPE_NUMERIC = 0;
/*     */   public static final int TYPE_TEST = 1;
/*     */   public static final int TYPE_INVERSE_NUMERIC = 2;
/*     */   public ClusStatistic m_TotStat;
/*     */   public ClusStatistic m_TotCorrStat;
/*     */   public ClusStatistic m_MissingStat;
/*     */   public ClusStatistic m_PosStat;
/*     */   public ClusStatistic[] m_TestStat;
/*     */   public ClusHeuristic m_Heuristic;
/*     */   public ClusAttributeWeights m_ClusteringWeights;
/*     */   public NodeTest m_BestTest;
/*     */   public int m_TestType;
/*     */   public double m_BestHeur;
/*     */   public double m_UnknownFreq;
/*     */   public ClusAttrType m_SplitAttr;
/*  60 */   public ArrayList m_AlternativeBest = new ArrayList();
/*     */ 
/*     */   
/*     */   public boolean m_IsAcceptable = true;
/*     */ 
/*     */   
/*     */   public double m_BestSplit;
/*     */ 
/*     */   
/*     */   public double m_PosFreq;
/*     */ 
/*     */   
/*     */   public RowData m_Subset;
/*     */ 
/*     */   
/*     */   public String toString() {
/*  76 */     return this.m_PosStat.getString2();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public final boolean hasBestTest() {
/*  82 */     return (this.m_IsAcceptable == true && this.m_TestType != -1);
/*     */   }
/*     */   
/*     */   public final String getTestString() {
/*  86 */     return this.m_BestTest.getString();
/*     */   }
/*     */   
/*     */   public final NodeTest updateTest() {
/*  90 */     if (this.m_TestType == 0) {
/*  91 */       this.m_TestType = 1;
/*  92 */       this.m_BestTest = (NodeTest)new NumericTest(this.m_SplitAttr.getType(), this.m_BestSplit, this.m_PosFreq);
/*     */     }
/*  94 */     else if (this.m_TestType == 2) {
/*  95 */       this.m_TestType = 1;
/*  96 */       this.m_BestTest = (NodeTest)new InverseNumericTest(this.m_SplitAttr.getType(), this.m_BestSplit, this.m_PosFreq);
/*     */     } 
/*  98 */     if (this.m_BestTest == null) {
/*  99 */       System.out.println("Best test is null");
/*     */     }
/* 101 */     this.m_BestTest.preprocess(1);
/* 102 */     this.m_BestTest.setUnknownFreq(this.m_UnknownFreq);
/* 103 */     this.m_BestTest.setHeuristicValue(this.m_BestHeur);
/* 104 */     return this.m_BestTest;
/*     */   }
/*     */   
/*     */   public void setInitialData(ClusStatistic totstat, RowData data) throws ClusException {
/* 108 */     this.m_Heuristic.setInitialData(totstat, data);
/*     */   }
/*     */   
/*     */   public final void initTestSelector(ClusStatistic totstat, RowData subset) {
/* 112 */     initTestSelector(totstat);
/*     */     
/* 114 */     for (int i = 0; i < this.m_TestStat.length; i++) {
/* 115 */       this.m_TestStat[i].setSDataSize(subset.getNbRows());
/*     */     }
/* 117 */     this.m_Heuristic.setData(subset);
/* 118 */     this.m_Subset = subset;
/*     */   }
/*     */ 
/*     */   
/*     */   public final void initTestSelector(ClusStatistic totstat) {
/* 123 */     this.m_TotStat = totstat;
/* 124 */     resetBestTest();
/*     */   }
/*     */   
/*     */   public final void resetBestTest() {
/* 128 */     this.m_BestTest = null;
/* 129 */     this.m_TestType = -1;
/* 130 */     this.m_BestHeur = Double.NEGATIVE_INFINITY;
/* 131 */     this.m_UnknownFreq = 0.0D;
/* 132 */     this.m_SplitAttr = null;
/* 133 */     resetAlternativeBest();
/*     */   }
/*     */   
/*     */   public final void resetAlternativeBest() {
/* 137 */     this.m_AlternativeBest.clear();
/*     */   }
/*     */   
/*     */   public final void addAlternativeBest(NodeTest nt) {
/* 141 */     this.m_AlternativeBest.add(nt);
/*     */   }
/*     */   
/*     */   public final void setBestHeur(double value) {
/* 145 */     this.m_BestHeur = value;
/*     */   }
/*     */   
/*     */   public final void reset(int nb) {
/* 149 */     for (int i = 0; i < nb; i++) {
/* 150 */       this.m_TestStat[i].reset();
/*     */     }
/* 152 */     this.m_MissingStat = this.m_TestStat[nb - 1];
/*     */   }
/*     */   
/*     */   public final void reset() {
/* 156 */     this.m_PosStat.reset();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final void create(ClusStatManager smanager, int nbstat) throws ClusException {
/* 165 */     this.m_TotStat = null;
/* 166 */     this.m_Heuristic = smanager.getHeuristic();
/* 167 */     this.m_TestStat = new ClusStatistic[nbstat];
/* 168 */     for (int i = 0; i < nbstat; i++) {
/* 169 */       this.m_TestStat[i] = smanager.createClusteringStat();
/*     */     }
/* 171 */     this.m_ClusteringWeights = smanager.getClusteringWeights();
/* 172 */     this.m_TotCorrStat = smanager.createClusteringStat();
/* 173 */     this.m_PosStat = this.m_TestStat[0];
/*     */   }
/*     */   
/*     */   public final void setHeuristic(ClusHeuristic heur) {
/* 177 */     this.m_Heuristic = heur;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final double getPosWeight() {
/* 186 */     return this.m_PosStat.m_SumWeight;
/*     */   }
/*     */   
/*     */   public final double getTotWeight() {
/* 190 */     return this.m_TotStat.m_SumWeight;
/*     */   }
/*     */   
/*     */   public final double getTotNoUnkW() {
/* 194 */     return this.m_TotCorrStat.m_SumWeight;
/*     */   }
/*     */ 
/*     */   
/*     */   public final void subtractMissing() {
/* 199 */     this.m_TotCorrStat.subtractFromThis(this.m_MissingStat);
/*     */   }
/*     */   
/*     */   public final void copyTotal() {
/* 203 */     this.m_TotCorrStat.copy(this.m_TotStat);
/*     */   }
/*     */   
/*     */   public final void calcPosFreq() {
/* 207 */     this.m_PosFreq = this.m_PosStat.m_SumWeight / this.m_TotStat.m_SumWeight;
/*     */   }
/*     */   
/*     */   public final ClusStatistic getStat(int i) {
/* 211 */     return this.m_TestStat[i];
/*     */   }
/*     */   
/*     */   public final ClusStatistic getPosStat() {
/* 215 */     return this.m_PosStat;
/*     */   }
/*     */   
/*     */   public final ClusStatistic getMissStat() {
/* 219 */     return this.m_MissingStat;
/*     */   }
/*     */   
/*     */   public final ClusStatistic getTotStat() {
/* 223 */     return this.m_TotStat;
/*     */   }
/*     */   
/*     */   public final ArrayList getAlternativeBest() {
/* 227 */     return this.m_AlternativeBest;
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
/*     */   public final boolean stopCrit() {
/* 239 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final void updateNumeric(double val, ClusStatistic pos, ClusAttrType at) {
/* 249 */     double heur = this.m_Heuristic.calcHeuristic(this.m_TotCorrStat, pos, this.m_MissingStat);
/* 250 */     if (heur > this.m_BestHeur + 1.0E-6D) {
/* 251 */       double tot_w = getTotWeight();
/* 252 */       double tot_no_unk = getTotNoUnkW();
/* 253 */       this.m_UnknownFreq = (tot_w - tot_no_unk) / tot_w;
/* 254 */       this.m_TestType = 0;
/* 255 */       this.m_PosFreq = pos.m_SumWeight / tot_no_unk;
/* 256 */       this.m_BestSplit = val;
/* 257 */       this.m_BestHeur = heur;
/* 258 */       this.m_SplitAttr = at;
/*     */     } 
/*     */   }
/*     */   
/*     */   public final void updateNumeric(double val, ClusAttrType at) {
/* 263 */     double heur = this.m_Heuristic.calcHeuristic(this.m_TotCorrStat, this.m_PosStat, this.m_MissingStat);
/* 264 */     if (Settings.VERBOSE >= 2) {
/* 265 */       System.err.println("Heur: " + heur + " nb: " + this.m_PosStat.m_SumWeight);
/*     */     }
/*     */     
/* 268 */     if (heur > this.m_BestHeur + 1.0E-6D) {
/* 269 */       if (Settings.VERBOSE >= 2) {
/* 270 */         System.err.println("Better.");
/*     */       }
/* 272 */       double tot_w = getTotWeight();
/* 273 */       double tot_no_unk = getTotNoUnkW();
/* 274 */       if (Settings.VERBOSE >= 2) {
/* 275 */         System.err.println(" tot_w: " + tot_w + " tot_no_unk: " + tot_no_unk);
/*     */       }
/* 277 */       this.m_UnknownFreq = (tot_w - tot_no_unk) / tot_w;
/* 278 */       this.m_TestType = 0;
/* 279 */       this.m_PosFreq = getPosWeight() / tot_no_unk;
/* 280 */       this.m_BestSplit = val;
/* 281 */       this.m_BestHeur = heur;
/* 282 */       this.m_SplitAttr = at;
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
/*     */   public final void updateInverseNumeric(double val, ClusAttrType at) {
/* 295 */     double heur = this.m_Heuristic.calcHeuristic(this.m_TotCorrStat, this.m_PosStat, this.m_MissingStat);
/* 296 */     if (Settings.VERBOSE >= 2) {
/* 297 */       System.err.println("Heur: " + heur + " nb: " + this.m_PosStat.m_SumWeight);
/*     */     }
/* 299 */     if (heur > this.m_BestHeur + 1.0E-6D) {
/* 300 */       if (Settings.VERBOSE >= 2) {
/* 301 */         System.err.println("Better.");
/*     */       }
/* 303 */       double tot_w = getTotWeight();
/* 304 */       double tot_no_unk = getTotNoUnkW();
/* 305 */       this.m_UnknownFreq = (tot_w - tot_no_unk) / tot_w;
/* 306 */       this.m_TestType = 2;
/* 307 */       this.m_PosFreq = getPosWeight() / tot_no_unk;
/* 308 */       this.m_BestSplit = val;
/* 309 */       this.m_BestHeur = heur;
/* 310 */       this.m_SplitAttr = at;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final double calcHeuristic(ClusStatistic stat) {
/* 320 */     return this.m_Heuristic.calcHeuristic(this.m_TotStat, stat, this.m_MissingStat);
/*     */   }
/*     */   
/*     */   public final double calcHeuristic(ClusStatistic tot, ClusStatistic pos) {
/* 324 */     return this.m_Heuristic.calcHeuristic(tot, pos, this.m_MissingStat);
/*     */   }
/*     */   
/*     */   public final double calcHeuristic(ClusStatistic tot, ClusStatistic[] set, int arity) {
/* 328 */     return this.m_Heuristic.calcHeuristic(tot, set, arity);
/*     */   }
/*     */   
/*     */   public final ClusHeuristic getHeuristic() {
/* 332 */     return this.m_Heuristic;
/*     */   }
/*     */   
/*     */   public final double getHeuristicValue() {
/* 336 */     return this.m_BestHeur;
/*     */   }
/*     */   
/*     */   public void checkAcceptable(ClusStatistic tot, ClusStatistic pos) {
/* 340 */     this.m_IsAcceptable = this.m_Heuristic.isAcceptable(tot, pos, this.m_MissingStat);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final void setRootStatistic(ClusStatistic stat) {
/* 349 */     this.m_Heuristic.setRootStatistic(stat);
/* 350 */     for (int i = 0; i < this.m_TestStat.length; i++) {
/* 351 */       this.m_TestStat[i].setTrainingStat(stat);
/*     */     }
/* 353 */     this.m_TotCorrStat.setTrainingStat(stat);
/*     */   }
/*     */   
/*     */   public final void statOnData(RowData data) {
/* 357 */     setSDataSize(data.getNbRows());
/* 358 */     this.m_Heuristic.setData(data);
/*     */   }
/*     */   
/*     */   private final void setSDataSize(int nbex) {
/* 362 */     this.m_TotStat.setSDataSize(nbex);
/* 363 */     int nbstat = this.m_TestStat.length;
/* 364 */     for (int i = 0; i < nbstat; i++)
/* 365 */       this.m_TestStat[i].setSDataSize(nbex); 
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\algo\split\CurrentBestTestAndHeuristic.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */