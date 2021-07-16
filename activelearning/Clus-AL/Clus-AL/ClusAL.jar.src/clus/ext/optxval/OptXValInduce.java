/*     */ package clus.ext.optxval;
/*     */ 
/*     */ import clus.algo.ClusInductionAlgorithm;
/*     */ import clus.algo.split.CurrentBestTestAndHeuristic;
/*     */ import clus.algo.split.NArySplit;
/*     */ import clus.algo.split.NominalSplit;
/*     */ import clus.algo.split.SubsetSplit;
/*     */ import clus.algo.tdidt.ClusNode;
/*     */ import clus.algo.tdidt.DepthFirstInduce;
/*     */ import clus.data.ClusData;
/*     */ import clus.data.rows.DataTuple;
/*     */ import clus.data.rows.RowData;
/*     */ import clus.data.type.ClusAttrType;
/*     */ import clus.data.type.ClusSchema;
/*     */ import clus.data.type.NominalAttrType;
/*     */ import clus.data.type.NumericAttrType;
/*     */ import clus.error.multiscore.MultiScore;
/*     */ import clus.heuristic.ClusHeuristic;
/*     */ import clus.main.ClusRun;
/*     */ import clus.main.Settings;
/*     */ import clus.model.ClusModel;
/*     */ import clus.statistic.ClusStatistic;
/*     */ import clus.util.ClusException;
/*     */ import java.io.IOException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class OptXValInduce
/*     */   extends ClusInductionAlgorithm
/*     */ {
/*     */   protected ClusHeuristic m_Heuristic;
/*     */   protected DepthFirstInduce m_DFirst;
/*     */   protected NominalSplit m_Split;
/*     */   protected ClusStatistic[] m_PosStat;
/*     */   protected ClusStatistic[][] m_TestStat;
/*     */   protected ClusStatistic m_Scratch;
/*     */   protected int m_NbFolds;
/*     */   protected int[] m_PrevCl;
/*     */   protected double[] m_PrevVl;
/*     */   protected CurrentBestTestAndHeuristic[] m_Selector;
/*     */   protected int m_MaxStats;
/*     */   
/*     */   public OptXValInduce(ClusSchema schema, Settings sett) throws ClusException, IOException {
/*  56 */     super(schema, sett);
/*     */   }
/*     */ 
/*     */   
/*     */   public final void findNominal(NominalAttrType at, OptXValGroup grp) {
/*  61 */     int nbvalues = at.getNbValues();
/*  62 */     int statsize = nbvalues + at.intHasMissing();
/*  63 */     reset(statsize);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  69 */     RowData data = grp.getData();
/*  70 */     int nb_rows = data.getNbRows();
/*  71 */     for (int i = 0; i < nb_rows; i++) {
/*  72 */       DataTuple tuple = data.getTuple(i);
/*  73 */       int value = at.getNominal(tuple);
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*  78 */       this.m_TestStat[tuple.m_Index][value].updateWeighted(tuple, i);
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  84 */     sumStats(statsize);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  90 */     int nb = grp.getNbFolds();
/*  91 */     for (int j = 0; j < nb; j++) {
/*  92 */       int foldnr = grp.getFold(j);
/*  93 */       if (foldnr != 0) {
/*  94 */         ClusStatistic[] zero_stat = this.m_TestStat[0];
/*  95 */         ClusStatistic[] cr_stat = this.m_TestStat[foldnr];
/*  96 */         for (int k = 0; k < statsize; k++) {
/*  97 */           cr_stat[k].subtractFromOther(zero_stat[k]);
/*     */         }
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 104 */       this.m_Split.findSplit(this.m_Selector[j], at);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final void findNumeric(NumericAttrType at, OptXValGroup grp) {
/* 115 */     RowData data = grp.getData();
/* 116 */     int idx = at.getArrayIndex();
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 121 */     data.sort(at);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 126 */     reset(2);
/*     */     
/* 128 */     int first = 0;
/* 129 */     int nb_rows = data.getNbRows();
/* 130 */     if (at.hasMissing()) {
/* 131 */       DataTuple tuple; while (first < nb_rows && (tuple = data.getTuple(first)).hasNumMissing(idx)) {
/* 132 */         this.m_TestStat[tuple.m_Index][1].updateWeighted(tuple, first);
/* 133 */         first++;
/*     */       } 
/* 135 */       subtractMissing(grp);
/*     */     } else {
/* 137 */       copyTotal(grp);
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 143 */     int[] folds = grp.getFolds();
/*     */     
/* 145 */     for (int i = 0; i < folds.length; i++) {
/* 146 */       this.m_PrevCl[i] = -1;
/* 147 */       this.m_PrevVl[i] = Double.NaN;
/*     */     } 
/* 149 */     ClusStatistic sum = this.m_PosStat[0];
/* 150 */     if (Settings.ONE_NOMINAL) {
/* 151 */       for (int j = first; j < nb_rows; j++) {
/* 152 */         DataTuple tuple = data.getTuple(j);
/* 153 */         boolean no_sum_calc = true;
/* 154 */         int foldnr = tuple.getIndex();
/* 155 */         int crcl = tuple.getClassification();
/* 156 */         double value = tuple.getDoubleVal(idx);
/* 157 */         for (int k = 0; k < folds.length; k++) {
/* 158 */           int cr_fold = folds[k];
/* 159 */           if (foldnr != cr_fold) {
/* 160 */             if (this.m_PrevCl[k] == -1 && value != this.m_PrevVl[k] && this.m_PrevVl[k] != Double.NaN)
/* 161 */             { if (no_sum_calc) {
/*     */ 
/*     */ 
/*     */ 
/*     */                 
/* 166 */                 sum.reset();
/* 167 */                 for (int m = 1; m <= this.m_NbFolds; ) { sum.add(this.m_PosStat[m]); m++; }
/* 168 */                  no_sum_calc = false;
/*     */               } 
/*     */ 
/*     */ 
/*     */ 
/*     */               
/* 174 */               if (cr_fold != 0) {
/*     */ 
/*     */ 
/*     */ 
/*     */                 
/* 179 */                 this.m_Scratch.copy(sum);
/* 180 */                 this.m_Scratch.subtractFromThis(this.m_PosStat[cr_fold]);
/*     */ 
/*     */ 
/*     */ 
/*     */                 
/* 185 */                 this.m_Selector[k].updateNumeric(value, this.m_Scratch, (ClusAttrType)at);
/*     */ 
/*     */ 
/*     */               
/*     */               }
/*     */               else {
/*     */ 
/*     */ 
/*     */ 
/*     */                 
/* 195 */                 this.m_Selector[k].updateNumeric(value, sum, (ClusAttrType)at);
/*     */               } 
/*     */ 
/*     */ 
/*     */ 
/*     */               
/* 201 */               this.m_PrevCl[k] = crcl; }
/*     */             
/* 203 */             else if (this.m_PrevCl[k] != crcl) { this.m_PrevCl[k] = -1; }
/*     */             
/* 205 */             this.m_PrevVl[k] = value;
/*     */           } 
/*     */         } 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 212 */         this.m_PosStat[foldnr].updateWeighted(tuple, j);
/*     */       
/*     */       }
/*     */     
/*     */     }
/*     */     else {
/*     */       
/* 219 */       for (int j = first; j < nb_rows; j++) {
/* 220 */         DataTuple tuple = data.getTuple(j);
/* 221 */         boolean no_sum_calc = true;
/* 222 */         int foldnr = tuple.getIndex();
/* 223 */         double value = tuple.getDoubleVal(idx);
/* 224 */         for (int k = 0; k < folds.length; k++) {
/* 225 */           int cr_fold = folds[k];
/* 226 */           if (foldnr != cr_fold) {
/* 227 */             if (value != this.m_PrevVl[k] && this.m_PrevVl[k] != Double.NaN) {
/* 228 */               if (no_sum_calc) {
/*     */ 
/*     */ 
/*     */ 
/*     */                 
/* 233 */                 sum.reset();
/* 234 */                 for (int m = 1; m <= this.m_NbFolds; ) { sum.add(this.m_PosStat[m]); m++; }
/* 235 */                  no_sum_calc = false;
/*     */               } 
/*     */ 
/*     */ 
/*     */ 
/*     */               
/* 241 */               if (cr_fold != 0) {
/*     */ 
/*     */ 
/*     */ 
/*     */                 
/* 246 */                 this.m_Scratch.copy(sum);
/* 247 */                 this.m_Scratch.subtractFromThis(this.m_PosStat[cr_fold]);
/*     */ 
/*     */ 
/*     */ 
/*     */                 
/* 252 */                 this.m_Selector[k].updateNumeric(value, this.m_Scratch, (ClusAttrType)at);
/*     */ 
/*     */ 
/*     */               
/*     */               }
/*     */               else {
/*     */ 
/*     */ 
/*     */ 
/*     */                 
/* 262 */                 this.m_Selector[k].updateNumeric(value, sum, (ClusAttrType)at);
/*     */               } 
/*     */             } 
/*     */ 
/*     */ 
/*     */ 
/*     */             
/* 269 */             this.m_PrevVl[k] = value;
/*     */           } 
/*     */         } 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 276 */         this.m_PosStat[foldnr].updateWeighted(tuple, j);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract OptXValNode xvalInduce(OptXValGroup paramOptXValGroup);
/*     */ 
/*     */ 
/*     */   
/*     */   public ClusData createData() {
/* 288 */     return (ClusData)new RowData(this.m_Schema);
/*     */   }
/*     */   
/*     */   public final void reset(int nb) {
/* 292 */     for (int i = 0; i <= this.m_NbFolds; i++) {
/* 293 */       for (int j = 0; j < nb; j++) {
/* 294 */         this.m_TestStat[i][j].reset();
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public final void sumStats(int nb) {
/* 301 */     for (int j = 0; j < nb; j++) {
/*     */       
/* 303 */       ClusStatistic sum = this.m_TestStat[0][j];
/* 304 */       for (int i = 1; i <= this.m_NbFolds; i++) {
/* 305 */         sum.add(this.m_TestStat[i][j]);
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public final void subtractMissing(OptXValGroup grp) {
/* 312 */     ClusStatistic sum = this.m_TestStat[0][1];
/* 313 */     for (int i = 1; i <= this.m_NbFolds; i++) {
/* 314 */       sum.add(this.m_TestStat[i][1]);
/*     */     }
/*     */     
/* 317 */     ClusStatistic[] stot = grp.m_TotStat;
/* 318 */     for (int j = 0; j <= this.m_NbFolds; j++) {
/* 319 */       this.m_TestStat[j][1].subtractFromOther(stot[j]);
/*     */     }
/*     */   }
/*     */   
/*     */   public final void copyTotal(OptXValGroup grp) {
/* 324 */     ClusStatistic[] stot = grp.m_TotStat;
/* 325 */     for (int i = 0; i <= this.m_NbFolds; i++) {
/* 326 */       this.m_TestStat[i][1].copy(stot[i]);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public final void findBestTest(OptXValGroup mgrp) {
/* 332 */     mgrp.makeNodes();
/*     */     
/* 334 */     RowData data = mgrp.getData();
/* 335 */     ClusSchema schema = data.getSchema();
/* 336 */     ClusAttrType[] attrs = schema.getDescriptiveAttributes();
/* 337 */     int nb_normal = attrs.length;
/* 338 */     for (int i = 0; i < nb_normal; i++) {
/* 339 */       ClusAttrType at = attrs[i];
/* 340 */       if (at instanceof NominalAttrType) { findNominal((NominalAttrType)at, mgrp); }
/* 341 */       else { findNumeric((NumericAttrType)at, mgrp); }
/*     */     
/*     */     } 
/*     */   }
/*     */   public final CurrentBestTestAndHeuristic getSelector(int i) {
/* 346 */     return this.m_Selector[i];
/*     */   }
/*     */   
/*     */   public final void cleanSplit() {
/* 350 */     this.m_Split = null;
/*     */   }
/*     */   
/*     */   public final void createStats() {
/* 354 */     int mfolds = this.m_NbFolds + 1;
/* 355 */     this.m_Heuristic = this.m_StatManager.getHeuristic();
/* 356 */     this.m_PosStat = new ClusStatistic[mfolds];
/* 357 */     this.m_TestStat = new ClusStatistic[mfolds][this.m_MaxStats];
/* 358 */     this.m_Selector = new CurrentBestTestAndHeuristic[mfolds];
/* 359 */     for (int i = 0; i < mfolds; i++) {
/* 360 */       for (int j = 0; j < this.m_MaxStats; j++) {
/* 361 */         this.m_TestStat[i][j] = this.m_StatManager.createClusteringStat();
/*     */       }
/* 363 */       this.m_PosStat[i] = this.m_TestStat[i][0];
/*     */       
/* 365 */       CurrentBestTestAndHeuristic currentBestTestAndHeuristic = this.m_Selector[i] = new CurrentBestTestAndHeuristic();
/* 366 */       currentBestTestAndHeuristic.m_Heuristic = this.m_Heuristic;
/*     */     } 
/*     */     
/* 369 */     CurrentBestTestAndHeuristic sel = this.m_DFirst.getBestTest();
/* 370 */     sel.m_Heuristic = this.m_Heuristic;
/* 371 */     sel.m_TestStat = this.m_TestStat[0];
/* 372 */     sel.m_PosStat = this.m_PosStat[0];
/*     */   }
/*     */   
/*     */   public final void initTestSelectors(OptXValGroup grp) {
/* 376 */     int nb = grp.getNbFolds();
/* 377 */     for (int i = 0; i < nb; i++) {
/* 378 */       int fold = grp.getFold(i);
/* 379 */       CurrentBestTestAndHeuristic sel = this.m_Selector[i];
/* 380 */       sel.m_TestStat = this.m_TestStat[fold];
/* 381 */       sel.m_PosStat = this.m_PosStat[fold];
/* 382 */       sel.initTestSelector(grp.getTotStat(fold));
/*     */     } 
/*     */   }
/*     */   
/*     */   public final void setNbFolds(int folds) {
/* 387 */     this.m_NbFolds = folds;
/* 388 */     this.m_PrevCl = new int[folds + 1];
/* 389 */     this.m_PrevVl = new double[folds + 1];
/*     */   }
/*     */ 
/*     */   
/*     */   public final void initialize(int folds) {
/* 394 */     if (getSettings().isBinarySplit()) { this.m_Split = (NominalSplit)new SubsetSplit(); }
/* 395 */     else { this.m_Split = (NominalSplit)new NArySplit(); }
/*     */     
/* 397 */     this.m_DFirst = new DepthFirstInduce(this, this.m_Split);
/*     */     
/* 399 */     setNbFolds(folds);
/*     */     
/* 401 */     if (this.m_Schema.getNbNumericDescriptiveAttributes() > 0) this.m_MaxStats = Math.max(this.m_MaxStats, 3);
/*     */   
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final OptXValNode optXVal(RowData data) {
/* 410 */     createStats();
/* 411 */     this.m_Split.initialize(this.m_StatManager);
/* 412 */     this.m_Scratch = this.m_StatManager.createClusteringStat();
/*     */     
/* 414 */     OptXValGroup grp = new OptXValGroup(data, this.m_NbFolds + 1);
/* 415 */     grp.initializeFolds();
/* 416 */     grp.create(this.m_StatManager, this.m_NbFolds);
/* 417 */     grp.calcTotalStats();
/*     */     
/* 419 */     return xvalInduce(grp);
/*     */   }
/*     */   
/*     */   public ClusNode induce(ClusRun cr, MultiScore score) {
/* 423 */     return null;
/*     */   }
/*     */   
/*     */   public ClusModel induceSingleUnpruned(ClusRun cr) {
/* 427 */     return null;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\ext\optxval\OptXValInduce.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */