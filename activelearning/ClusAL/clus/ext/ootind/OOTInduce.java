/*     */ package clus.ext.ootind;
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
/*     */ import clus.ext.optxval.OptXValGroup;
/*     */ import clus.ext.optxval.OptXValNode;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class OOTInduce
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
/*     */   public boolean SHOULD_OPTIMIZE = false;
/*     */   
/*     */   public OOTInduce(ClusSchema schema, Settings sett) throws ClusException, IOException {
/*  65 */     super(schema, sett);
/*     */   }
/*     */ 
/*     */   
/*     */   public final void findNominal(NominalAttrType at, OptXValGroup grp) {
/*  70 */     int nbvalues = at.getNbValues();
/*  71 */     int statsize = nbvalues + at.intHasMissing();
/*  72 */     reset(statsize);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  78 */     RowData data = grp.getData();
/*  79 */     int nb_rows = data.getNbRows();
/*  80 */     for (int i = 0; i < nb_rows; i++) {
/*  81 */       DataTuple tuple = data.getTuple(i);
/*  82 */       int value = at.getNominal(tuple);
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*  87 */       int[] counts = tuple.m_Folds;
/*  88 */       for (int k = 0; k < counts.length; k++) {
/*  89 */         int count = counts[k];
/*  90 */         if (count != 0) this.m_TestStat[k][value].updateWeighted(tuple, count * tuple.getWeight());
/*     */       
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  98 */     int nb = grp.getNbFolds();
/*  99 */     for (int j = 0; j < nb; j++) {
/* 100 */       this.m_Split.findSplit(this.m_Selector[j], at);
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
/* 111 */     RowData data = grp.getData();
/* 112 */     int idx = at.getArrayIndex();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 137 */     copyTotal(grp);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 143 */     int[] folds = grp.getFolds(); int i;
/* 144 */     for (i = 0; i < folds.length; i++) {
/* 145 */       this.m_PrevCl[i] = -1;
/* 146 */       this.m_PrevVl[i] = Double.NaN;
/*     */     } 
/*     */     
/* 149 */     if (Settings.ONE_NOMINAL) {
/* 150 */       for (i = first; i < nb_rows; i++) {
/* 151 */         DataTuple tuple = data.getTuple(i);
/* 152 */         int crcl = tuple.getClassification();
/* 153 */         double value = tuple.getDoubleVal(idx);
/* 154 */         for (int j = 0; j < folds.length; j++) {
/* 155 */           int cr_fold = folds[j];
/* 156 */           int count = tuple.m_Folds[cr_fold];
/* 157 */           if (count != 0) {
/* 158 */             if (this.m_PrevCl[j] == -1 && value != this.m_PrevVl[j] && this.m_PrevVl[j] != Double.NaN)
/*     */             
/*     */             { 
/*     */ 
/*     */               
/* 163 */               this.m_Selector[j].updateNumeric(value, this.m_PosStat[cr_fold], (ClusAttrType)at);
/*     */ 
/*     */ 
/*     */ 
/*     */               
/* 168 */               this.m_PrevCl[j] = crcl; }
/*     */             
/* 170 */             else if (this.m_PrevCl[j] != crcl) { this.m_PrevCl[j] = -1; }
/*     */             
/* 172 */             this.m_PrevVl[j] = value;
/*     */ 
/*     */ 
/*     */ 
/*     */             
/* 177 */             this.m_PosStat[cr_fold].updateWeighted(tuple, tuple.getWeight() * count);
/*     */           }
/*     */         
/*     */         }
/*     */       
/*     */       }
/*     */     
/*     */     } else {
/*     */       
/* 186 */       for (i = first; i < nb_rows; i++) {
/* 187 */         DataTuple tuple = data.getTuple(i);
/* 188 */         double value = tuple.getDoubleVal(idx);
/* 189 */         for (int j = 0; j < folds.length; j++) {
/* 190 */           int cr_fold = folds[j];
/* 191 */           int count = tuple.m_Folds[cr_fold];
/* 192 */           if (count != 0) {
/* 193 */             if (value != this.m_PrevVl[j] && this.m_PrevVl[j] != Double.NaN)
/*     */             {
/*     */ 
/*     */ 
/*     */               
/* 198 */               this.m_Selector[j].updateNumeric(value, this.m_PosStat[cr_fold], (ClusAttrType)at);
/*     */             }
/*     */ 
/*     */ 
/*     */ 
/*     */             
/* 204 */             this.m_PrevVl[j] = value;
/*     */ 
/*     */ 
/*     */ 
/*     */             
/* 209 */             this.m_PosStat[cr_fold].updateWeighted(tuple, tuple.getWeight() * count);
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract OptXValNode xvalInduce(OptXValGroup paramOptXValGroup);
/*     */ 
/*     */ 
/*     */   
/*     */   public ClusData createData() {
/* 224 */     return (ClusData)new RowData(this.m_Schema);
/*     */   }
/*     */   
/*     */   public final void reset(int nb) {
/* 228 */     for (int i = 0; i < this.m_NbFolds; i++) {
/* 229 */       for (int j = 0; j < nb; j++) {
/* 230 */         this.m_TestStat[i][j].reset();
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   public final void copyTotal(OptXValGroup grp) {
/* 236 */     ClusStatistic[] stot = grp.m_TotStat;
/* 237 */     for (int i = 0; i < this.m_NbFolds; i++) {
/* 238 */       this.m_TestStat[i][1].copy(stot[i]);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public final void findBestTest(OptXValGroup mgrp) {
/* 244 */     mgrp.makeNodes();
/*     */     
/* 246 */     RowData data = mgrp.getData();
/* 247 */     ClusSchema schema = data.getSchema();
/* 248 */     ClusAttrType[] attrs = schema.getDescriptiveAttributes();
/* 249 */     int nb_normal = attrs.length;
/* 250 */     for (int i = 0; i < nb_normal; i++) {
/* 251 */       ClusAttrType at = attrs[i];
/* 252 */       if (at instanceof NominalAttrType) { findNominal((NominalAttrType)at, mgrp); }
/* 253 */       else { findNumeric((NumericAttrType)at, mgrp); }
/*     */     
/*     */     } 
/*     */   }
/*     */   public final CurrentBestTestAndHeuristic getSelector(int i) {
/* 258 */     return this.m_Selector[i];
/*     */   }
/*     */   
/*     */   public final void cleanSplit() {
/* 262 */     this.m_Split = null;
/*     */   }
/*     */   
/*     */   public final void createStats() {
/* 266 */     this.m_Heuristic = this.m_StatManager.getHeuristic();
/* 267 */     this.m_PosStat = new ClusStatistic[this.m_NbFolds];
/* 268 */     this.m_TestStat = new ClusStatistic[this.m_NbFolds][this.m_MaxStats];
/* 269 */     this.m_Selector = new CurrentBestTestAndHeuristic[this.m_NbFolds];
/* 270 */     for (int i = 0; i < this.m_NbFolds; i++) {
/* 271 */       for (int j = 0; j < this.m_MaxStats; j++) {
/* 272 */         this.m_TestStat[i][j] = this.m_StatManager.createClusteringStat();
/*     */       }
/* 274 */       this.m_PosStat[i] = this.m_TestStat[i][0];
/*     */       
/* 276 */       CurrentBestTestAndHeuristic currentBestTestAndHeuristic = this.m_Selector[i] = new CurrentBestTestAndHeuristic();
/* 277 */       currentBestTestAndHeuristic.m_Heuristic = this.m_Heuristic;
/*     */     } 
/*     */     
/* 280 */     CurrentBestTestAndHeuristic sel = this.m_DFirst.getBestTest();
/* 281 */     sel.m_Heuristic = this.m_Heuristic;
/* 282 */     sel.m_TestStat = this.m_TestStat[0];
/* 283 */     sel.m_PosStat = this.m_PosStat[0];
/*     */   }
/*     */   
/*     */   public final void initTestSelectors(OptXValGroup grp) {
/* 287 */     int nb = grp.getNbFolds();
/* 288 */     for (int i = 0; i < nb; i++) {
/* 289 */       int fold = grp.getFold(i);
/* 290 */       CurrentBestTestAndHeuristic sel = this.m_Selector[i];
/* 291 */       sel.m_TestStat = this.m_TestStat[fold];
/* 292 */       sel.m_PosStat = this.m_PosStat[fold];
/* 293 */       sel.initTestSelector(grp.getTotStat(fold));
/*     */     } 
/*     */   }
/*     */   
/*     */   public final void setNbFolds(int folds) {
/* 298 */     this.m_NbFolds = folds;
/* 299 */     this.m_PrevCl = new int[folds];
/* 300 */     this.m_PrevVl = new double[folds];
/*     */   }
/*     */ 
/*     */   
/*     */   public final void initialize(int folds) {
/* 305 */     if (getSettings().isBinarySplit()) { this.m_Split = (NominalSplit)new SubsetSplit(); }
/* 306 */     else { this.m_Split = (NominalSplit)new NArySplit(); }
/*     */     
/* 308 */     this.m_DFirst = new DepthFirstInduce(this, this.m_Split);
/*     */     
/* 310 */     setNbFolds(folds);
/*     */     
/* 312 */     if (this.m_Schema.getNbNumericDescriptiveAttributes() > 0) this.m_MaxStats = Math.max(this.m_MaxStats, 3);
/*     */   
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final OptXValNode ootInduce(RowData data) {
/* 321 */     createStats();
/* 322 */     this.m_Split.initialize(this.m_StatManager);
/* 323 */     this.m_Scratch = this.m_StatManager.createClusteringStat();
/*     */     
/* 325 */     OptXValGroup grp = new OptXValGroup(data, this.m_NbFolds);
/* 326 */     grp.initializeFolds();
/* 327 */     grp.create2(this.m_StatManager, this.m_NbFolds);
/* 328 */     grp.calcTotalStats2();
/*     */     
/* 330 */     return xvalInduce(grp);
/*     */   }
/*     */   
/*     */   public ClusNode induce(ClusRun cr, MultiScore score) {
/* 334 */     return null;
/*     */   }
/*     */   
/*     */   public ClusModel induceSingleUnpruned(ClusRun cr) {
/* 338 */     return null;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\ext\ootind\OOTInduce.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */