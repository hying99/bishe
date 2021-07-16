/*     */ package clus.ext.ensembles;
/*     */ 
/*     */ import clus.Clus;
/*     */ import clus.algo.ClusInductionAlgorithm;
/*     */ import clus.algo.tdidt.ClusDecisionTree;
/*     */ import clus.algo.tdidt.DepthFirstInduce;
/*     */ import clus.algo.tdidt.DepthFirstInduceSparse;
/*     */ import clus.data.rows.MemoryTupleIterator;
/*     */ import clus.data.rows.RowData;
/*     */ import clus.data.rows.TupleIterator;
/*     */ import clus.data.type.ClusAttrType;
/*     */ import clus.data.type.ClusSchema;
/*     */ import clus.error.ClusErrorList;
/*     */ import clus.main.ClusOutput;
/*     */ import clus.main.ClusRun;
/*     */ import clus.main.ClusStatManager;
/*     */ import clus.main.ClusSummary;
/*     */ import clus.main.Settings;
/*     */ import clus.model.ClusModel;
/*     */ import clus.model.ClusModelInfo;
/*     */ import clus.model.modelio.ClusModelCollectionIO;
/*     */ import clus.selection.BaggingSelection;
/*     */ import clus.selection.ClusSelection;
/*     */ import clus.selection.OOBSelection;
/*     */ import clus.util.ClusException;
/*     */ import clus.util.ClusRandom;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import jeans.resource.ResourceInfo;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ClusEnsembleInduce
/*     */   extends ClusInductionAlgorithm
/*     */ {
/*     */   Clus m_BagClus;
/*     */   static ClusAttrType[] m_RandomSubspaces;
/*     */   ClusForest m_OForest;
/*     */   ClusForest m_DForest;
/*     */   static int m_Mode;
/*  54 */   long m_SummTime = 0L;
/*     */ 
/*     */   
/*     */   static boolean m_OptMode;
/*     */ 
/*     */   
/*     */   ClusEnsembleInduceOptimization m_Optimization;
/*     */ 
/*     */   
/*     */   int[] m_OutEnsembleAt;
/*     */ 
/*     */   
/*     */   static int m_NbMaxBags;
/*     */ 
/*     */   
/*     */   ClusOOBErrorEstimate m_OOBEstimation;
/*     */   
/*     */   boolean m_FeatRank;
/*     */   
/*     */   ClusEnsembleFeatureRanking m_FeatureRanking;
/*     */ 
/*     */   
/*     */   public ClusEnsembleInduce(ClusSchema schema, Settings sett, Clus clus) throws ClusException, IOException {
/*  77 */     super(schema, sett);
/*  78 */     initialize(schema, sett, clus);
/*     */   }
/*     */   
/*     */   public ClusEnsembleInduce(ClusInductionAlgorithm other, Clus clus) throws ClusException, IOException {
/*  82 */     super(other);
/*  83 */     initialize(getSchema(), getSettings(), clus);
/*     */   }
/*     */   
/*     */   public void initialize(ClusSchema schema, Settings sett, Clus clus) throws ClusException, IOException {
/*  87 */     this.m_BagClus = clus;
/*  88 */     getStatManager(); m_Mode = ClusStatManager.getMode();
/*     */     
/*  90 */     m_OptMode = (Settings.shouldOptimizeEnsemble() && !Settings.IS_XVAL && (m_Mode == 2 || m_Mode == 1 || m_Mode == 0));
/*  91 */     this.m_OutEnsembleAt = sett.getNbBaggingSets().getIntVectorSorted();
/*  92 */     m_NbMaxBags = this.m_OutEnsembleAt[this.m_OutEnsembleAt.length - 1];
/*  93 */     this.m_FeatRank = (sett.shouldPerformRanking() && !Settings.IS_XVAL);
/*  94 */     if (this.m_FeatRank && !Settings.shouldEstimateOOB()) {
/*  95 */       System.err.println("For Feature Ranking OOB estimate of error should also be performed.");
/*  96 */       System.err.println("OOB Error Estimate is set to true.");
/*  97 */       Settings.m_EnsembleOOBestimate.setValue(true);
/*     */     } 
/*  99 */     if (Settings.shouldEstimateOOB()) {
/* 100 */       this.m_OOBEstimation = new ClusOOBErrorEstimate(m_Mode);
/*     */     }
/* 102 */     if (this.m_FeatRank) {
/* 103 */       this.m_FeatureRanking = new ClusEnsembleFeatureRanking();
/* 104 */       this.m_FeatureRanking.initializeAttributes(schema.getDescriptiveAttributes());
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void induceAll(ClusRun cr) throws ClusException, IOException {
/* 115 */     switch (cr.getStatManager().getSettings().getEnsembleMethod()) {
/*     */       
/*     */       case 0:
/* 118 */         induceBagging(cr);
/*     */         break;
/*     */ 
/*     */       
/*     */       case 1:
/* 123 */         induceBagging(cr);
/*     */         break;
/*     */ 
/*     */       
/*     */       case 2:
/* 128 */         induceSubspaces(cr);
/*     */         break;
/*     */ 
/*     */       
/*     */       case 3:
/* 133 */         induceBaggingSubspaces(cr);
/*     */         break;
/*     */ 
/*     */       
/*     */       case 5:
/* 138 */         induceRForestNoBagging(cr);
/*     */         break;
/*     */     } 
/*     */ 
/*     */     
/* 143 */     if (this.m_FeatRank) {
/* 144 */       this.m_FeatureRanking.sortFeatureRanks();
/* 145 */       this.m_FeatureRanking.printRanking(cr.getStatManager().getSettings().getFileAbsolute(cr.getStatManager().getSettings().getAppName()));
/* 146 */       this.m_FeatureRanking.convertRanksByName();
/*     */     } 
/*     */     
/* 149 */     postProcessForest(cr);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ClusModel induceSingleUnpruned(ClusRun cr) throws ClusException, IOException {
/* 157 */     ClusRun myRun = new ClusRun(cr);
/* 158 */     induceAll(myRun);
/* 159 */     ClusModelInfo info = myRun.getModelInfo(1);
/* 160 */     return info.getModel();
/*     */   }
/*     */ 
/*     */   
/*     */   public void induceRForestNoBagging(ClusRun cr) throws ClusException, IOException {
/*     */     MemoryTupleIterator memoryTupleIterator;
/* 166 */     this.m_OForest = new ClusForest(getStatManager());
/* 167 */     this.m_DForest = new ClusForest(getStatManager());
/* 168 */     long summ_time = 0L;
/* 169 */     TupleIterator train_iterator = null;
/* 170 */     TupleIterator test_iterator = null;
/* 171 */     if (m_OptMode) {
/* 172 */       train_iterator = cr.getTrainIter();
/* 173 */       if (this.m_BagClus.hasTestSet()) {
/* 174 */         memoryTupleIterator = cr.getTestSet().getIterator();
/* 175 */         if (m_Mode == 2 || m_Mode == 1) {
/* 176 */           this.m_Optimization = new ClusEnsembleInduceOptRegHMLC(train_iterator, (TupleIterator)memoryTupleIterator, cr.getTrainingSet().getNbRows() + cr.getTestSet().getNbRows());
/*     */         }
/* 178 */         if (m_Mode == 0) {
/* 179 */           this.m_Optimization = new ClusEnsembleInduceOptClassification(train_iterator, (TupleIterator)memoryTupleIterator, cr.getTrainingSet().getNbRows() + cr.getTestSet().getNbRows());
/*     */         }
/*     */       } else {
/* 182 */         if (m_Mode == 2 || m_Mode == 1) {
/* 183 */           this.m_Optimization = new ClusEnsembleInduceOptRegHMLC(train_iterator, (TupleIterator)memoryTupleIterator, cr.getTrainingSet().getNbRows());
/*     */         }
/* 185 */         if (m_Mode == 0) {
/* 186 */           this.m_Optimization = new ClusEnsembleInduceOptClassification(train_iterator, (TupleIterator)memoryTupleIterator, cr.getTrainingSet().getNbRows());
/*     */         }
/*     */       } 
/* 189 */       this.m_Optimization.initPredictions(this.m_OForest.getStat());
/*     */     } 
/* 191 */     for (int i = 1; i <= m_NbMaxBags; i++) {
/* 192 */       DepthFirstInduce ind; long one_bag_time = ResourceInfo.getTime();
/* 193 */       if (Settings.VERBOSE > 0) {
/* 194 */         System.out.println("Bag: " + i);
/*     */       }
/* 196 */       ClusRun crSingle = new ClusRun(cr.getTrainingSet(), cr.getSummary());
/*     */       
/* 198 */       if (getSchema().isSparse()) {
/* 199 */         DepthFirstInduceSparse depthFirstInduceSparse = new DepthFirstInduceSparse(this);
/*     */       } else {
/* 201 */         ind = new DepthFirstInduce(this);
/*     */       } 
/* 203 */       ind.initialize();
/* 204 */       crSingle.getStatManager().initClusteringWeights();
/* 205 */       ClusModel model = ind.induceSingleUnpruned(crSingle);
/* 206 */       summ_time += ResourceInfo.getTime() - one_bag_time;
/* 207 */       if (m_OptMode) {
/*     */         
/* 209 */         if (i == 1) {
/* 210 */           this.m_Optimization.initModelPredictionForTuples(model, train_iterator, (TupleIterator)memoryTupleIterator);
/*     */         } else {
/* 212 */           this.m_Optimization.addModelPredictionForTuples(model, train_iterator, (TupleIterator)memoryTupleIterator, i);
/*     */         } 
/*     */       } else {
/* 215 */         this.m_OForest.addModelToForest(model);
/*     */       } 
/*     */ 
/*     */ 
/*     */       
/* 220 */       if (m_OptMode && i != m_NbMaxBags && checkToOutEnsemble(i)) {
/* 221 */         crSingle.setInductionTime(summ_time);
/* 222 */         postProcessForest(crSingle);
/* 223 */         crSingle.setTestSet(cr.getTestIter());
/* 224 */         crSingle.setTrainingSet(cr.getTrainingSet());
/* 225 */         outputBetweenForest(crSingle, this.m_BagClus, "_" + i + "_");
/*     */       } 
/* 227 */       crSingle.deleteData();
/* 228 */       crSingle.setModels(new ArrayList());
/*     */     } 
/*     */   }
/*     */   public void induceSubspaces(ClusRun cr) throws ClusException, IOException {
/*     */     MemoryTupleIterator memoryTupleIterator;
/* 233 */     this.m_OForest = new ClusForest(getStatManager());
/* 234 */     this.m_DForest = new ClusForest(getStatManager());
/* 235 */     long summ_time = 0L;
/* 236 */     TupleIterator train_iterator = null;
/* 237 */     TupleIterator test_iterator = null;
/*     */     
/* 239 */     if (m_OptMode) {
/* 240 */       train_iterator = cr.getTrainIter();
/* 241 */       if (this.m_BagClus.hasTestSet()) {
/* 242 */         memoryTupleIterator = cr.getTestSet().getIterator();
/* 243 */         if (m_Mode == 2 || m_Mode == 1) {
/* 244 */           this.m_Optimization = new ClusEnsembleInduceOptRegHMLC(train_iterator, (TupleIterator)memoryTupleIterator, cr.getTrainingSet().getNbRows() + cr.getTestSet().getNbRows());
/*     */         }
/* 246 */         if (m_Mode == 0) {
/* 247 */           this.m_Optimization = new ClusEnsembleInduceOptClassification(train_iterator, (TupleIterator)memoryTupleIterator, cr.getTrainingSet().getNbRows() + cr.getTestSet().getNbRows());
/*     */         }
/*     */       } else {
/*     */         
/* 251 */         if (m_Mode == 2 || m_Mode == 1) {
/* 252 */           this.m_Optimization = new ClusEnsembleInduceOptRegHMLC(train_iterator, (TupleIterator)memoryTupleIterator, cr.getTrainingSet().getNbRows());
/*     */         }
/* 254 */         if (m_Mode == 0) {
/* 255 */           this.m_Optimization = new ClusEnsembleInduceOptClassification(train_iterator, (TupleIterator)memoryTupleIterator, cr.getTrainingSet().getNbRows());
/*     */         }
/*     */       } 
/* 258 */       this.m_Optimization.initPredictions(this.m_OForest.getStat());
/*     */     } 
/* 260 */     for (int i = 1; i <= m_NbMaxBags; i++) {
/* 261 */       DepthFirstInduce ind; long one_bag_time = ResourceInfo.getTime();
/* 262 */       if (Settings.VERBOSE > 0) {
/* 263 */         System.out.println("Bag: " + i);
/*     */       }
/* 265 */       ClusRun crSingle = new ClusRun(cr.getTrainingSet(), cr.getSummary());
/* 266 */       setRandomSubspaces(cr.getStatManager().getSchema().getDescriptiveAttributes(), cr.getStatManager().getSettings().getNbRandomAttrSelected());
/*     */       
/* 268 */       if (getSchema().isSparse()) {
/* 269 */         DepthFirstInduceSparse depthFirstInduceSparse = new DepthFirstInduceSparse(this);
/*     */       } else {
/* 271 */         ind = new DepthFirstInduce(this);
/*     */       } 
/* 273 */       ind.initialize();
/* 274 */       crSingle.getStatManager().initClusteringWeights();
/* 275 */       ClusModel model = ind.induceSingleUnpruned(crSingle);
/* 276 */       summ_time += ResourceInfo.getTime() - one_bag_time;
/* 277 */       if (m_OptMode) {
/*     */         
/* 279 */         if (i == 1) {
/* 280 */           this.m_Optimization.initModelPredictionForTuples(model, train_iterator, (TupleIterator)memoryTupleIterator);
/*     */         } else {
/* 282 */           this.m_Optimization.addModelPredictionForTuples(model, train_iterator, (TupleIterator)memoryTupleIterator, i);
/*     */         } 
/*     */       } else {
/* 285 */         this.m_OForest.addModelToForest(model);
/*     */       } 
/*     */ 
/*     */ 
/*     */       
/* 290 */       if (m_OptMode && i != m_NbMaxBags && checkToOutEnsemble(i)) {
/* 291 */         crSingle.setInductionTime(summ_time);
/* 292 */         postProcessForest(crSingle);
/* 293 */         crSingle.setTestSet(cr.getTestIter());
/* 294 */         crSingle.setTrainingSet(cr.getTrainingSet());
/* 295 */         outputBetweenForest(crSingle, this.m_BagClus, "_" + i + "_");
/*     */       } 
/* 297 */       crSingle.deleteData();
/* 298 */       crSingle.setModels(new ArrayList());
/*     */     } 
/*     */   }
/*     */   public void induceBagging(ClusRun cr) throws ClusException, IOException {
/*     */     MemoryTupleIterator memoryTupleIterator;
/* 303 */     int i, nbrows = cr.getTrainingSet().getNbRows();
/*     */     
/* 305 */     ((RowData)cr.getTrainingSet()).addIndices();
/*     */     
/* 307 */     if (cr.getTestSet() != null)
/*     */     {
/* 309 */       cr.getTestSet().addIndices();
/*     */     }
/*     */     
/* 312 */     this.m_OForest = new ClusForest(getStatManager());
/* 313 */     this.m_DForest = new ClusForest(getStatManager());
/*     */     
/* 315 */     TupleIterator train_iterator = null;
/* 316 */     TupleIterator test_iterator = null;
/* 317 */     OOBSelection oob_total = null;
/* 318 */     OOBSelection oob_sel = null;
/* 319 */     if (m_OptMode) {
/* 320 */       train_iterator = cr.getTrainIter();
/* 321 */       if (this.m_BagClus.hasTestSet()) {
/* 322 */         memoryTupleIterator = cr.getTestSet().getIterator();
/* 323 */         if (m_Mode == 2 || m_Mode == 1) {
/* 324 */           this.m_Optimization = new ClusEnsembleInduceOptRegHMLC(train_iterator, (TupleIterator)memoryTupleIterator, cr.getTrainingSet().getNbRows() + cr.getTestSet().getNbRows());
/*     */         }
/* 326 */         if (m_Mode == 0) {
/* 327 */           this.m_Optimization = new ClusEnsembleInduceOptClassification(train_iterator, (TupleIterator)memoryTupleIterator, cr.getTrainingSet().getNbRows() + cr.getTestSet().getNbRows());
/*     */         }
/*     */       } else {
/* 330 */         if (m_Mode == 2 || m_Mode == 1) {
/* 331 */           this.m_Optimization = new ClusEnsembleInduceOptRegHMLC(train_iterator, (TupleIterator)memoryTupleIterator, cr.getTrainingSet().getNbRows());
/*     */         }
/* 333 */         if (m_Mode == 0) {
/* 334 */           this.m_Optimization = new ClusEnsembleInduceOptClassification(train_iterator, (TupleIterator)memoryTupleIterator, cr.getTrainingSet().getNbRows());
/*     */         }
/*     */       } 
/* 337 */       this.m_Optimization.initPredictions(this.m_OForest.getStat());
/*     */     } 
/*     */     
/* 340 */     int origMaxDepth = -1;
/* 341 */     if (getSettings().isEnsembleRandomDepth())
/*     */     {
/*     */       
/* 344 */       origMaxDepth = getSettings().getTreeMaxDepth();
/*     */     }
/* 346 */     BaggingSelection msel = null;
/* 347 */     int[] bagSelections = getSettings().getBagSelection().getIntVectorSorted();
/*     */     
/* 349 */     switch (bagSelections[0]) {
/*     */       
/*     */       case -1:
/* 352 */         for (i = 1; i <= m_NbMaxBags; i++) {
/* 353 */           msel = new BaggingSelection(nbrows, getSettings().getEnsembleBagSize());
/* 354 */           if (Settings.shouldEstimateOOB()) {
/* 355 */             oob_sel = new OOBSelection(msel);
/* 356 */             if (i == 1) {
/* 357 */               oob_total = new OOBSelection(msel);
/*     */             } else {
/* 359 */               oob_total.addToThis(oob_sel);
/*     */             } 
/*     */           } 
/* 362 */           induceOneBag(cr, i, origMaxDepth, oob_sel, oob_total, train_iterator, (TupleIterator)memoryTupleIterator, msel);
/*     */         } 
/*     */         break;
/*     */       
/*     */       case 0:
/* 367 */         makeForestFromBags(cr, train_iterator, (TupleIterator)memoryTupleIterator);
/*     */         break;
/*     */ 
/*     */       
/*     */       default:
/* 372 */         for (i = 1; i < bagSelections[0]; i++)
/*     */         {
/* 374 */           msel = new BaggingSelection(nbrows, getSettings().getEnsembleBagSize());
/*     */         }
/* 376 */         for (i = bagSelections[0]; i <= bagSelections[1]; i++) {
/* 377 */           msel = new BaggingSelection(nbrows, getSettings().getEnsembleBagSize());
/* 378 */           if (Settings.shouldEstimateOOB()) {
/* 379 */             oob_sel = new OOBSelection(msel);
/*     */           }
/* 381 */           induceOneBag(cr, i, origMaxDepth, oob_sel, oob_total, train_iterator, (TupleIterator)memoryTupleIterator, msel);
/*     */         } 
/*     */         break;
/*     */     } 
/*     */ 
/*     */     
/* 387 */     if (origMaxDepth != -1) {
/* 388 */       getSettings().setTreeMaxDepth(origMaxDepth);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void induceOneBag(ClusRun cr, int i, int origMaxDepth, OOBSelection oob_sel, OOBSelection oob_total, TupleIterator train_iterator, TupleIterator test_iterator, BaggingSelection msel) throws ClusException, IOException {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: invokevirtual getSettings : ()Lclus/main/Settings;
/*     */     //   4: invokevirtual isEnsembleRandomDepth : ()Z
/*     */     //   7: ifeq -> 25
/*     */     //   10: aload_0
/*     */     //   11: invokevirtual getSettings : ()Lclus/main/Settings;
/*     */     //   14: iconst_5
/*     */     //   15: invokestatic nextDouble : (I)D
/*     */     //   18: iload_3
/*     */     //   19: invokestatic randDepthWighExponentialDistribution : (DI)I
/*     */     //   22: invokevirtual setTreeMaxDepth : (I)V
/*     */     //   25: invokestatic getTime : ()J
/*     */     //   28: lstore #9
/*     */     //   30: getstatic clus/main/Settings.VERBOSE : I
/*     */     //   33: ifle -> 61
/*     */     //   36: getstatic java/lang/System.out : Ljava/io/PrintStream;
/*     */     //   39: new java/lang/StringBuilder
/*     */     //   42: dup
/*     */     //   43: invokespecial <init> : ()V
/*     */     //   46: ldc 'Bag: '
/*     */     //   48: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   51: iload_2
/*     */     //   52: invokevirtual append : (I)Ljava/lang/StringBuilder;
/*     */     //   55: invokevirtual toString : ()Ljava/lang/String;
/*     */     //   58: invokevirtual println : (Ljava/lang/String;)V
/*     */     //   61: aload_0
/*     */     //   62: getfield m_BagClus : Lclus/Clus;
/*     */     //   65: aload_1
/*     */     //   66: invokevirtual getTrainingSet : ()Lclus/data/ClusData;
/*     */     //   69: aload #8
/*     */     //   71: aload_1
/*     */     //   72: invokevirtual getSummary : ()Lclus/main/ClusSummary;
/*     */     //   75: iload_2
/*     */     //   76: invokevirtual partitionDataBasic : (Lclus/data/ClusData;Lclus/selection/ClusSelection;Lclus/main/ClusSummary;I)Lclus/main/ClusRun;
/*     */     //   79: astore #11
/*     */     //   81: aload_0
/*     */     //   82: invokevirtual getSchema : ()Lclus/data/type/ClusSchema;
/*     */     //   85: invokevirtual isSparse : ()Z
/*     */     //   88: ifeq -> 104
/*     */     //   91: new clus/algo/tdidt/DepthFirstInduceSparse
/*     */     //   94: dup
/*     */     //   95: aload_0
/*     */     //   96: invokespecial <init> : (Lclus/algo/ClusInductionAlgorithm;)V
/*     */     //   99: astore #12
/*     */     //   101: goto -> 114
/*     */     //   104: new clus/algo/tdidt/DepthFirstInduce
/*     */     //   107: dup
/*     */     //   108: aload_0
/*     */     //   109: invokespecial <init> : (Lclus/algo/ClusInductionAlgorithm;)V
/*     */     //   112: astore #12
/*     */     //   114: aload #12
/*     */     //   116: invokevirtual initialize : ()V
/*     */     //   119: aload #11
/*     */     //   121: invokevirtual getStatManager : ()Lclus/main/ClusStatManager;
/*     */     //   124: invokevirtual initClusteringWeights : ()V
/*     */     //   127: aload #12
/*     */     //   129: aload #11
/*     */     //   131: invokevirtual induceSingleUnpruned : (Lclus/main/ClusRun;)Lclus/model/ClusModel;
/*     */     //   134: astore #13
/*     */     //   136: aload_0
/*     */     //   137: dup
/*     */     //   138: getfield m_SummTime : J
/*     */     //   141: invokestatic getTime : ()J
/*     */     //   144: lload #9
/*     */     //   146: lsub
/*     */     //   147: ladd
/*     */     //   148: putfield m_SummTime : J
/*     */     //   151: invokestatic shouldEstimateOOB : ()Z
/*     */     //   154: ifeq -> 191
/*     */     //   157: aload_0
/*     */     //   158: invokevirtual getSettings : ()Lclus/main/Settings;
/*     */     //   161: invokevirtual getBagSelection : ()Ljeans/io/ini/INIFileNominalOrIntOrVector;
/*     */     //   164: invokevirtual getIntVectorSorted : ()[I
/*     */     //   167: iconst_0
/*     */     //   168: iaload
/*     */     //   169: iconst_m1
/*     */     //   170: if_icmpne -> 191
/*     */     //   173: aload_0
/*     */     //   174: getfield m_OOBEstimation : Lclus/ext/ensembles/ClusOOBErrorEstimate;
/*     */     //   177: aload #4
/*     */     //   179: aload_1
/*     */     //   180: invokevirtual getTrainingSet : ()Lclus/data/ClusData;
/*     */     //   183: checkcast clus/data/rows/RowData
/*     */     //   186: aload #13
/*     */     //   188: invokevirtual updateOOBTuples : (Lclus/selection/OOBSelection;Lclus/data/rows/RowData;Lclus/model/ClusModel;)V
/*     */     //   191: aload_0
/*     */     //   192: getfield m_FeatRank : Z
/*     */     //   195: ifeq -> 413
/*     */     //   198: new java/util/ArrayList
/*     */     //   201: dup
/*     */     //   202: invokespecial <init> : ()V
/*     */     //   205: astore #14
/*     */     //   207: aload_0
/*     */     //   208: getfield m_FeatureRanking : Lclus/ext/ensembles/ClusEnsembleFeatureRanking;
/*     */     //   211: aload #13
/*     */     //   213: checkcast clus/algo/tdidt/ClusNode
/*     */     //   216: aload #14
/*     */     //   218: invokevirtual fillWithAttributesInTree : (Lclus/algo/tdidt/ClusNode;Ljava/util/ArrayList;)V
/*     */     //   221: aload_1
/*     */     //   222: invokevirtual getTrainingSet : ()Lclus/data/ClusData;
/*     */     //   225: checkcast clus/data/rows/RowData
/*     */     //   228: invokevirtual deepCloneData : ()Lclus/data/ClusData;
/*     */     //   231: checkcast clus/data/rows/RowData
/*     */     //   234: astore #15
/*     */     //   236: aload_0
/*     */     //   237: getfield m_FeatureRanking : Lclus/ext/ensembles/ClusEnsembleFeatureRanking;
/*     */     //   240: aload #15
/*     */     //   242: aload #4
/*     */     //   244: invokevirtual selectFrom : (Lclus/selection/ClusSelection;)Lclus/data/ClusData;
/*     */     //   247: checkcast clus/data/rows/RowData
/*     */     //   250: aload #13
/*     */     //   252: invokevirtual calcAverageError : (Lclus/data/rows/RowData;Lclus/model/ClusModel;)D
/*     */     //   255: dstore #16
/*     */     //   257: iconst_0
/*     */     //   258: istore #18
/*     */     //   260: iload #18
/*     */     //   262: aload #14
/*     */     //   264: invokevirtual size : ()I
/*     */     //   267: if_icmpge -> 413
/*     */     //   270: aload #14
/*     */     //   272: iload #18
/*     */     //   274: invokevirtual get : (I)Ljava/lang/Object;
/*     */     //   277: checkcast java/lang/String
/*     */     //   280: astore #19
/*     */     //   282: aload_0
/*     */     //   283: getfield m_FeatureRanking : Lclus/ext/ensembles/ClusEnsembleFeatureRanking;
/*     */     //   286: aload #19
/*     */     //   288: invokevirtual getAttributeInfo : (Ljava/lang/String;)[D
/*     */     //   291: astore #20
/*     */     //   293: aload #20
/*     */     //   295: iconst_0
/*     */     //   296: daload
/*     */     //   297: dstore #21
/*     */     //   299: aload #20
/*     */     //   301: iconst_1
/*     */     //   302: daload
/*     */     //   303: dstore #23
/*     */     //   305: aload_0
/*     */     //   306: getfield m_FeatureRanking : Lclus/ext/ensembles/ClusEnsembleFeatureRanking;
/*     */     //   309: aload #4
/*     */     //   311: aload #15
/*     */     //   313: aload #4
/*     */     //   315: invokevirtual selectFrom : (Lclus/selection/ClusSelection;)Lclus/data/ClusData;
/*     */     //   318: checkcast clus/data/rows/RowData
/*     */     //   321: dload #21
/*     */     //   323: d2i
/*     */     //   324: dload #23
/*     */     //   326: d2i
/*     */     //   327: invokevirtual createRandomizedOOBdata : (Lclus/selection/OOBSelection;Lclus/data/rows/RowData;II)Lclus/data/rows/RowData;
/*     */     //   330: astore #25
/*     */     //   332: invokestatic getMode : ()I
/*     */     //   335: iconst_1
/*     */     //   336: if_icmpne -> 366
/*     */     //   339: aload #20
/*     */     //   341: iconst_2
/*     */     //   342: dup2
/*     */     //   343: daload
/*     */     //   344: aload_0
/*     */     //   345: getfield m_FeatureRanking : Lclus/ext/ensembles/ClusEnsembleFeatureRanking;
/*     */     //   348: aload #25
/*     */     //   350: aload #13
/*     */     //   352: invokevirtual calcAverageError : (Lclus/data/rows/RowData;Lclus/model/ClusModel;)D
/*     */     //   355: dload #16
/*     */     //   357: dsub
/*     */     //   358: dload #16
/*     */     //   360: ddiv
/*     */     //   361: dadd
/*     */     //   362: dastore
/*     */     //   363: goto -> 396
/*     */     //   366: invokestatic getMode : ()I
/*     */     //   369: ifne -> 396
/*     */     //   372: aload #20
/*     */     //   374: iconst_2
/*     */     //   375: dup2
/*     */     //   376: daload
/*     */     //   377: dload #16
/*     */     //   379: aload_0
/*     */     //   380: getfield m_FeatureRanking : Lclus/ext/ensembles/ClusEnsembleFeatureRanking;
/*     */     //   383: aload #25
/*     */     //   385: aload #13
/*     */     //   387: invokevirtual calcAverageError : (Lclus/data/rows/RowData;Lclus/model/ClusModel;)D
/*     */     //   390: dsub
/*     */     //   391: dload #16
/*     */     //   393: ddiv
/*     */     //   394: dadd
/*     */     //   395: dastore
/*     */     //   396: aload_0
/*     */     //   397: getfield m_FeatureRanking : Lclus/ext/ensembles/ClusEnsembleFeatureRanking;
/*     */     //   400: aload #19
/*     */     //   402: aload #20
/*     */     //   404: invokevirtual putAttributeInfo : (Ljava/lang/String;[D)V
/*     */     //   407: iinc #18, 1
/*     */     //   410: goto -> 260
/*     */     //   413: getstatic clus/ext/ensembles/ClusEnsembleInduce.m_OptMode : Z
/*     */     //   416: ifeq -> 457
/*     */     //   419: iload_2
/*     */     //   420: iconst_1
/*     */     //   421: if_icmpne -> 440
/*     */     //   424: aload_0
/*     */     //   425: getfield m_Optimization : Lclus/ext/ensembles/ClusEnsembleInduceOptimization;
/*     */     //   428: aload #13
/*     */     //   430: aload #6
/*     */     //   432: aload #7
/*     */     //   434: invokevirtual initModelPredictionForTuples : (Lclus/model/ClusModel;Lclus/data/rows/TupleIterator;Lclus/data/rows/TupleIterator;)V
/*     */     //   437: goto -> 466
/*     */     //   440: aload_0
/*     */     //   441: getfield m_Optimization : Lclus/ext/ensembles/ClusEnsembleInduceOptimization;
/*     */     //   444: aload #13
/*     */     //   446: aload #6
/*     */     //   448: aload #7
/*     */     //   450: iload_2
/*     */     //   451: invokevirtual addModelPredictionForTuples : (Lclus/model/ClusModel;Lclus/data/rows/TupleIterator;Lclus/data/rows/TupleIterator;I)V
/*     */     //   454: goto -> 466
/*     */     //   457: aload_0
/*     */     //   458: getfield m_OForest : Lclus/ext/ensembles/ClusForest;
/*     */     //   461: aload #13
/*     */     //   463: invokevirtual addModelToForest : (Lclus/model/ClusModel;)V
/*     */     //   466: aload_0
/*     */     //   467: iload_2
/*     */     //   468: invokevirtual checkToOutEnsemble : (I)Z
/*     */     //   471: ifeq -> 632
/*     */     //   474: aload_0
/*     */     //   475: invokevirtual getSettings : ()Lclus/main/Settings;
/*     */     //   478: invokevirtual getBagSelection : ()Ljeans/io/ini/INIFileNominalOrIntOrVector;
/*     */     //   481: invokevirtual getIntVectorSorted : ()[I
/*     */     //   484: iconst_0
/*     */     //   485: iaload
/*     */     //   486: iconst_m1
/*     */     //   487: if_icmpne -> 632
/*     */     //   490: aload #11
/*     */     //   492: aload_0
/*     */     //   493: getfield m_SummTime : J
/*     */     //   496: invokevirtual setInductionTime : (J)V
/*     */     //   499: aload_0
/*     */     //   500: aload #11
/*     */     //   502: invokevirtual postProcessForest : (Lclus/main/ClusRun;)V
/*     */     //   505: invokestatic shouldEstimateOOB : ()Z
/*     */     //   508: ifeq -> 567
/*     */     //   511: aload_0
/*     */     //   512: getfield m_OOBEstimation : Lclus/ext/ensembles/ClusOOBErrorEstimate;
/*     */     //   515: aload #11
/*     */     //   517: aload #5
/*     */     //   519: aload_1
/*     */     //   520: invokevirtual getTrainingSet : ()Lclus/data/ClusData;
/*     */     //   523: checkcast clus/data/rows/RowData
/*     */     //   526: aload_0
/*     */     //   527: getfield m_BagClus : Lclus/Clus;
/*     */     //   530: new java/lang/StringBuilder
/*     */     //   533: dup
/*     */     //   534: invokespecial <init> : ()V
/*     */     //   537: ldc '_'
/*     */     //   539: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   542: iload_2
/*     */     //   543: invokevirtual append : (I)Ljava/lang/StringBuilder;
/*     */     //   546: ldc '_'
/*     */     //   548: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   551: invokevirtual toString : ()Ljava/lang/String;
/*     */     //   554: invokevirtual postProcessForestForOOBEstimate : (Lclus/main/ClusRun;Lclus/selection/OOBSelection;Lclus/data/rows/RowData;Lclus/Clus;Ljava/lang/String;)V
/*     */     //   557: aload_1
/*     */     //   558: aload #11
/*     */     //   560: iconst_1
/*     */     //   561: invokevirtual getModelInfo : (I)Lclus/model/ClusModelInfo;
/*     */     //   564: invokevirtual addModelInfo2 : (Lclus/model/ClusModelInfo;)V
/*     */     //   567: getstatic clus/ext/ensembles/ClusEnsembleInduce.m_OptMode : Z
/*     */     //   570: ifeq -> 632
/*     */     //   573: iload_2
/*     */     //   574: getstatic clus/ext/ensembles/ClusEnsembleInduce.m_NbMaxBags : I
/*     */     //   577: if_icmpeq -> 632
/*     */     //   580: aload #11
/*     */     //   582: aload_1
/*     */     //   583: invokevirtual getTestIter : ()Lclus/data/rows/TupleIterator;
/*     */     //   586: invokevirtual setTestSet : (Lclus/data/rows/TupleIterator;)V
/*     */     //   589: aload #11
/*     */     //   591: aload_1
/*     */     //   592: invokevirtual getTrainingSet : ()Lclus/data/ClusData;
/*     */     //   595: invokevirtual setTrainingSet : (Lclus/data/ClusData;)V
/*     */     //   598: aload_0
/*     */     //   599: aload #11
/*     */     //   601: aload_0
/*     */     //   602: getfield m_BagClus : Lclus/Clus;
/*     */     //   605: new java/lang/StringBuilder
/*     */     //   608: dup
/*     */     //   609: invokespecial <init> : ()V
/*     */     //   612: ldc '_'
/*     */     //   614: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   617: iload_2
/*     */     //   618: invokevirtual append : (I)Ljava/lang/StringBuilder;
/*     */     //   621: ldc '_'
/*     */     //   623: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   626: invokevirtual toString : ()Ljava/lang/String;
/*     */     //   629: invokevirtual outputBetweenForest : (Lclus/main/ClusRun;Lclus/Clus;Ljava/lang/String;)V
/*     */     //   632: aload_0
/*     */     //   633: invokevirtual getSettings : ()Lclus/main/Settings;
/*     */     //   636: invokevirtual getBagSelection : ()Ljeans/io/ini/INIFileNominalOrIntOrVector;
/*     */     //   639: invokevirtual getIntVectorSorted : ()[I
/*     */     //   642: iconst_0
/*     */     //   643: iaload
/*     */     //   644: iconst_m1
/*     */     //   645: if_icmpne -> 659
/*     */     //   648: aload_0
/*     */     //   649: invokevirtual getSettings : ()Lclus/main/Settings;
/*     */     //   652: pop
/*     */     //   653: invokestatic isPrintEnsembleModelFiles : ()Z
/*     */     //   656: ifeq -> 747
/*     */     //   659: new clus/model/modelio/ClusModelCollectionIO
/*     */     //   662: dup
/*     */     //   663: invokespecial <init> : ()V
/*     */     //   666: astore #14
/*     */     //   668: aload #11
/*     */     //   670: ldc 'Original'
/*     */     //   672: invokevirtual addModelInfo : (Ljava/lang/String;)Lclus/model/ClusModelInfo;
/*     */     //   675: astore #15
/*     */     //   677: aload #15
/*     */     //   679: aload #13
/*     */     //   681: invokevirtual setModel : (Lclus/model/ClusModel;)V
/*     */     //   684: aload_0
/*     */     //   685: getfield m_BagClus : Lclus/Clus;
/*     */     //   688: aload #11
/*     */     //   690: aload #14
/*     */     //   692: invokevirtual saveModels : (Lclus/main/ClusRun;Lclus/model/modelio/ClusModelCollectionIO;)V
/*     */     //   695: aload #14
/*     */     //   697: aload_0
/*     */     //   698: getfield m_BagClus : Lclus/Clus;
/*     */     //   701: invokevirtual getSettings : ()Lclus/main/Settings;
/*     */     //   704: new java/lang/StringBuilder
/*     */     //   707: dup
/*     */     //   708: invokespecial <init> : ()V
/*     */     //   711: aload_1
/*     */     //   712: invokevirtual getStatManager : ()Lclus/main/ClusStatManager;
/*     */     //   715: invokevirtual getSettings : ()Lclus/main/Settings;
/*     */     //   718: invokevirtual getAppName : ()Ljava/lang/String;
/*     */     //   721: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   724: ldc '_bag'
/*     */     //   726: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   729: iload_2
/*     */     //   730: invokevirtual append : (I)Ljava/lang/StringBuilder;
/*     */     //   733: ldc '.model'
/*     */     //   735: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   738: invokevirtual toString : ()Ljava/lang/String;
/*     */     //   741: invokevirtual getFileAbsolute : (Ljava/lang/String;)Ljava/lang/String;
/*     */     //   744: invokevirtual save : (Ljava/lang/String;)V
/*     */     //   747: aload #11
/*     */     //   749: invokevirtual deleteData : ()V
/*     */     //   752: aload #11
/*     */     //   754: new java/util/ArrayList
/*     */     //   757: dup
/*     */     //   758: invokespecial <init> : ()V
/*     */     //   761: invokevirtual setModels : (Ljava/util/ArrayList;)V
/*     */     //   764: return
/*     */     // Line number table:
/*     */     //   Java source line number -> byte code offset
/*     */     //   #394	-> 0
/*     */     //   #396	-> 10
/*     */     //   #398	-> 15
/*     */     //   #396	-> 19
/*     */     //   #400	-> 25
/*     */     //   #401	-> 30
/*     */     //   #402	-> 36
/*     */     //   #404	-> 61
/*     */     //   #406	-> 81
/*     */     //   #407	-> 91
/*     */     //   #409	-> 104
/*     */     //   #411	-> 114
/*     */     //   #413	-> 119
/*     */     //   #414	-> 127
/*     */     //   #415	-> 136
/*     */     //   #418	-> 151
/*     */     //   #419	-> 173
/*     */     //   #422	-> 191
/*     */     //   #423	-> 198
/*     */     //   #424	-> 207
/*     */     //   #425	-> 221
/*     */     //   #426	-> 236
/*     */     //   #427	-> 257
/*     */     //   #428	-> 270
/*     */     //   #429	-> 282
/*     */     //   #430	-> 293
/*     */     //   #431	-> 299
/*     */     //   #432	-> 305
/*     */     //   #433	-> 332
/*     */     //   #434	-> 339
/*     */     //   #435	-> 366
/*     */     //   #436	-> 372
/*     */     //   #438	-> 396
/*     */     //   #427	-> 407
/*     */     //   #443	-> 413
/*     */     //   #444	-> 419
/*     */     //   #445	-> 424
/*     */     //   #447	-> 440
/*     */     //   #450	-> 457
/*     */     //   #462	-> 466
/*     */     //   #463	-> 490
/*     */     //   #464	-> 499
/*     */     //   #465	-> 505
/*     */     //   #467	-> 511
/*     */     //   #475	-> 557
/*     */     //   #481	-> 567
/*     */     //   #482	-> 580
/*     */     //   #483	-> 589
/*     */     //   #484	-> 598
/*     */     //   #487	-> 632
/*     */     //   #488	-> 659
/*     */     //   #489	-> 668
/*     */     //   #490	-> 677
/*     */     //   #491	-> 684
/*     */     //   #492	-> 695
/*     */     //   #495	-> 747
/*     */     //   #496	-> 752
/*     */     //   #498	-> 764
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	descriptor
/*     */     //   101	3	12	ind	Lclus/algo/tdidt/DepthFirstInduce;
/*     */     //   282	125	19	current_attribute	Ljava/lang/String;
/*     */     //   293	114	20	info	[D
/*     */     //   299	108	21	type	D
/*     */     //   305	102	23	position	D
/*     */     //   332	75	25	permuted	Lclus/data/rows/RowData;
/*     */     //   260	153	18	z	I
/*     */     //   207	206	14	attests	Ljava/util/ArrayList;
/*     */     //   236	177	15	tdata	Lclus/data/rows/RowData;
/*     */     //   257	156	16	oob_err	D
/*     */     //   668	79	14	io	Lclus/model/modelio/ClusModelCollectionIO;
/*     */     //   677	70	15	orig_info	Lclus/model/ClusModelInfo;
/*     */     //   0	765	0	this	Lclus/ext/ensembles/ClusEnsembleInduce;
/*     */     //   0	765	1	cr	Lclus/main/ClusRun;
/*     */     //   0	765	2	i	I
/*     */     //   0	765	3	origMaxDepth	I
/*     */     //   0	765	4	oob_sel	Lclus/selection/OOBSelection;
/*     */     //   0	765	5	oob_total	Lclus/selection/OOBSelection;
/*     */     //   0	765	6	train_iterator	Lclus/data/rows/TupleIterator;
/*     */     //   0	765	7	test_iterator	Lclus/data/rows/TupleIterator;
/*     */     //   0	765	8	msel	Lclus/selection/BaggingSelection;
/*     */     //   30	735	9	one_bag_time	J
/*     */     //   81	684	11	crSingle	Lclus/main/ClusRun;
/*     */     //   114	651	12	ind	Lclus/algo/tdidt/DepthFirstInduce;
/*     */     //   136	629	13	model	Lclus/model/ClusModel;
/*     */     // Local variable type table:
/*     */     //   start	length	slot	name	signature
/*     */     //   207	206	14	attests	Ljava/util/ArrayList<Ljava/lang/String;>;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void makeForestFromBags(ClusRun cr, TupleIterator train_iterator, TupleIterator test_iterator) throws ClusException, IOException {
/*     */     try {
/* 502 */       this.m_OForest = new ClusForest(getStatManager());
/* 503 */       this.m_DForest = new ClusForest(getStatManager());
/* 504 */       OOBSelection oob_total = null;
/* 505 */       OOBSelection oob_sel = null;
/* 506 */       BaggingSelection msel = null;
/* 507 */       System.out.println("Start loading models");
/*     */       
/* 509 */       for (int i = 1; i <= m_NbMaxBags; i++) {
/* 510 */         System.out.println("Loading model for bag " + i);
/* 511 */         ClusModelCollectionIO io = ClusModelCollectionIO.load(this.m_BagClus.getSettings().getFileAbsolute(getSettings().getAppName() + "_bag" + i + ".model"));
/* 512 */         ClusModel orig_bag_model = io.getModel("Original");
/* 513 */         if (orig_bag_model == null) {
/* 514 */           throw new ClusException(cr.getStatManager().getSettings().getAppName() + "_bag" + i + ".model file does not contain model named 'Original'");
/*     */         }
/* 516 */         if (m_OptMode) {
/*     */           
/* 518 */           if (i == 1) {
/* 519 */             this.m_Optimization.initModelPredictionForTuples(orig_bag_model, train_iterator, test_iterator);
/*     */           } else {
/* 521 */             this.m_Optimization.addModelPredictionForTuples(orig_bag_model, train_iterator, test_iterator, i);
/*     */           } 
/*     */         } else {
/* 524 */           this.m_OForest.addModelToForest(orig_bag_model);
/*     */         } 
/* 526 */         if (Settings.shouldEstimateOOB()) {
/*     */           
/* 528 */           msel = new BaggingSelection(cr.getTrainingSet().getNbRows(), getSettings().getEnsembleBagSize());
/* 529 */           oob_sel = new OOBSelection(msel);
/* 530 */           if (i == 1) {
/* 531 */             oob_total = new OOBSelection(msel);
/*     */           } else {
/* 533 */             oob_total.addToThis(oob_sel);
/*     */           } 
/* 535 */           this.m_OOBEstimation.updateOOBTuples(oob_sel, (RowData)cr.getTrainingSet(), orig_bag_model);
/*     */         } 
/*     */         
/* 538 */         if (checkToOutEnsemble(i)) {
/* 539 */           postProcessForest(cr);
/* 540 */           if (Settings.shouldEstimateOOB()) {
/* 541 */             this.m_OOBEstimation.postProcessForestForOOBEstimate(cr, oob_total, (RowData)cr.getTrainingSet(), this.m_BagClus, "_" + i + "_");
/*     */           }
/* 543 */           if (m_OptMode && i != m_NbMaxBags) {
/* 544 */             outputBetweenForest(cr, this.m_BagClus, "_" + i + "_");
/*     */           }
/*     */         } 
/* 547 */         cr.setModels(new ArrayList());
/*     */ 
/*     */ 
/*     */       
/*     */       }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     }
/* 557 */     catch (ClassNotFoundException e) {
/* 558 */       throw new ClusException("Error: not all of the _bagX.model files were found");
/*     */     } 
/*     */   }
/*     */   public void induceBaggingSubspaces(ClusRun cr) throws ClusException, IOException {
/*     */     MemoryTupleIterator memoryTupleIterator;
/* 563 */     int nbrows = cr.getTrainingSet().getNbRows();
/* 564 */     this.m_OForest = new ClusForest(getStatManager());
/* 565 */     this.m_DForest = new ClusForest(getStatManager());
/* 566 */     long summ_time = 0L;
/* 567 */     TupleIterator train_iterator = null;
/* 568 */     TupleIterator test_iterator = null;
/* 569 */     OOBSelection oob_total = null;
/* 570 */     OOBSelection oob_sel = null;
/*     */     
/* 572 */     if (m_OptMode) {
/* 573 */       train_iterator = cr.getTrainIter();
/* 574 */       if (this.m_BagClus.hasTestSet()) {
/* 575 */         memoryTupleIterator = cr.getTestSet().getIterator();
/* 576 */         if (m_Mode == 2 || m_Mode == 1) {
/* 577 */           this.m_Optimization = new ClusEnsembleInduceOptRegHMLC(train_iterator, (TupleIterator)memoryTupleIterator, cr.getTrainingSet().getNbRows() + cr.getTestSet().getNbRows());
/*     */         }
/* 579 */         if (m_Mode == 0) {
/* 580 */           this.m_Optimization = new ClusEnsembleInduceOptClassification(train_iterator, (TupleIterator)memoryTupleIterator, cr.getTrainingSet().getNbRows() + cr.getTestSet().getNbRows());
/*     */         }
/*     */       } else {
/* 583 */         if (m_Mode == 2 || m_Mode == 1) {
/* 584 */           this.m_Optimization = new ClusEnsembleInduceOptRegHMLC(train_iterator, (TupleIterator)memoryTupleIterator, cr.getTrainingSet().getNbRows());
/*     */         }
/* 586 */         if (m_Mode == 0) {
/* 587 */           this.m_Optimization = new ClusEnsembleInduceOptClassification(train_iterator, (TupleIterator)memoryTupleIterator, cr.getTrainingSet().getNbRows());
/*     */         }
/*     */       } 
/* 590 */       this.m_Optimization.initPredictions(this.m_OForest.getStat());
/*     */     } 
/* 592 */     for (int i = 1; i <= m_NbMaxBags; i++) {
/* 593 */       DepthFirstInduce ind; long one_bag_time = ResourceInfo.getTime();
/* 594 */       if (Settings.VERBOSE > 0) {
/* 595 */         System.out.println("Bag: " + i);
/*     */       }
/* 597 */       BaggingSelection msel = new BaggingSelection(nbrows, getSettings().getEnsembleBagSize());
/* 598 */       ClusRun crSingle = this.m_BagClus.partitionDataBasic(cr.getTrainingSet(), (ClusSelection)msel, cr.getSummary(), i);
/* 599 */       setRandomSubspaces(cr.getStatManager().getSchema().getDescriptiveAttributes(), cr.getStatManager().getSettings().getNbRandomAttrSelected());
/*     */       
/* 601 */       if (getSchema().isSparse()) {
/* 602 */         DepthFirstInduceSparse depthFirstInduceSparse = new DepthFirstInduceSparse(this);
/*     */       } else {
/* 604 */         ind = new DepthFirstInduce(this);
/*     */       } 
/* 606 */       ind.initialize();
/* 607 */       crSingle.getStatManager().initClusteringWeights();
/* 608 */       ind.initializeHeuristic();
/* 609 */       ClusModel model = ind.induceSingleUnpruned(crSingle);
/* 610 */       summ_time += ResourceInfo.getTime() - one_bag_time;
/*     */       
/* 612 */       if (Settings.shouldEstimateOOB()) {
/* 613 */         oob_sel = new OOBSelection(msel);
/* 614 */         if (i == 1) {
/* 615 */           oob_total = new OOBSelection(msel);
/*     */         } else {
/* 617 */           oob_total.addToThis(oob_sel);
/*     */         } 
/* 619 */         this.m_OOBEstimation.updateOOBTuples(oob_sel, (RowData)cr.getTrainingSet(), model);
/*     */       } 
/*     */       
/* 622 */       if (m_OptMode) {
/* 623 */         if (i == 1) {
/* 624 */           this.m_Optimization.initModelPredictionForTuples(model, train_iterator, (TupleIterator)memoryTupleIterator);
/*     */         } else {
/* 626 */           this.m_Optimization.addModelPredictionForTuples(model, train_iterator, (TupleIterator)memoryTupleIterator, i);
/*     */         } 
/*     */       } else {
/* 629 */         this.m_OForest.addModelToForest(model);
/* 630 */         ClusModel defmod = ClusDecisionTree.induceDefault(crSingle);
/* 631 */         this.m_DForest.addModelToForest(defmod);
/*     */       } 
/*     */       
/* 634 */       if (checkToOutEnsemble(i)) {
/* 635 */         crSingle.setInductionTime(summ_time);
/* 636 */         postProcessForest(crSingle);
/* 637 */         if (Settings.shouldEstimateOOB()) {
/* 638 */           this.m_OOBEstimation.postProcessForestForOOBEstimate(crSingle, oob_total, (RowData)cr.getTrainingSet(), this.m_BagClus, "_" + i + "_");
/*     */         }
/* 640 */         if (m_OptMode && i != m_NbMaxBags) {
/* 641 */           crSingle.setTestSet(cr.getTestIter());
/* 642 */           crSingle.setTrainingSet(cr.getTrainingSet());
/* 643 */           outputBetweenForest(crSingle, this.m_BagClus, "_" + i + "_");
/*     */         } 
/*     */       } 
/* 646 */       crSingle.deleteData();
/* 647 */       crSingle.setModels(new ArrayList());
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean checkToOutEnsemble(int idx) {
/* 653 */     for (int i = 0; i < this.m_OutEnsembleAt.length; i++) {
/* 654 */       if (this.m_OutEnsembleAt[i] == idx) {
/* 655 */         return true;
/*     */       }
/*     */     } 
/* 658 */     return false;
/*     */   }
/*     */   
/*     */   public void postProcessForest(ClusRun cr) throws ClusException {
/* 662 */     ClusModelInfo def_info = cr.addModelInfo("Default");
/* 663 */     if (m_OptMode) {
/* 664 */       this.m_DForest = null;
/*     */     }
/* 666 */     def_info.setModel(ClusDecisionTree.induceDefault(cr));
/*     */     
/* 668 */     ClusModelInfo orig_info = cr.addModelInfo("Original");
/* 669 */     orig_info.setModel(this.m_OForest);
/*     */ 
/*     */     
/* 672 */     cr.getStatManager(); if (ClusStatManager.getMode() == 2) {
/* 673 */       double[] thresholds = cr.getStatManager().getSettings().getClassificationThresholds().getDoubleVector();
/*     */       
/* 675 */       this.m_OForest.setPrintModels(Settings.isPrintEnsembleModels());
/* 676 */       if (!m_OptMode) {
/* 677 */         this.m_DForest.setPrintModels(Settings.isPrintEnsembleModels());
/*     */       }
/* 679 */       if (thresholds != null) {
/* 680 */         for (int i = 0; i < thresholds.length; i++) {
/* 681 */           ClusModelInfo pruned_info = cr.addModelInfo("T(" + thresholds[i] + ")");
/* 682 */           ClusForest new_forest = this.m_OForest.cloneForestWithThreshold(thresholds[i]);
/* 683 */           new_forest.setPrintModels(Settings.isPrintEnsembleModels());
/* 684 */           pruned_info.setShouldWritePredictions(false);
/* 685 */           pruned_info.setModel(new_forest);
/*     */         } 
/*     */       }
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 692 */     if (getSettings().rulesFromTree() != 0 && 
/* 693 */       getSettings().getCoveringMethod() != 9) {
/* 694 */       this.m_OForest.convertToRules(cr, false);
/*     */     }
/*     */   }
/*     */   
/*     */   public void outputBetweenForest(ClusRun cr, Clus cl, String addname) throws IOException, ClusException {
/* 699 */     Settings sett = cr.getStatManager().getSettings();
/* 700 */     ClusSchema schema = cr.getStatManager().getSchema();
/* 701 */     ClusOutput output = new ClusOutput(sett.getAppName() + addname + ".out", schema, sett);
/* 702 */     ClusSummary summary = cr.getSummary();
/* 703 */     getStatManager().computeTrainSetStat((RowData)cr.getTrainingSet());
/* 704 */     cl.calcError(cr, null, null);
/* 705 */     if (summary != null) {
/* 706 */       for (int i = 1; i < cr.getNbModels(); i++) {
/* 707 */         ClusModelInfo summ_info = cr.getModelInfo(i);
/* 708 */         ClusErrorList test_err = summ_info.getTestError();
/* 709 */         summ_info.setTestError(test_err);
/*     */       } 
/*     */     }
/* 712 */     System.out.println("TA NA HORA DE PRINTAR SEUS TROXA");
/* 713 */     cl.calcExtraTrainingSetErrors(cr);
/* 714 */     output.writeHeader();
/* 715 */     output.writeOutput(cr, true, getSettings().isOutTrainError());
/* 716 */     output.close();
/* 717 */     cl.getClassifier().saveInformation(sett.getAppName());
/* 718 */     ClusModelCollectionIO io = new ClusModelCollectionIO();
/* 719 */     cl.saveModels(cr, io);
/* 720 */     io.save(cl.getSettings().getFileAbsolute(cr.getStatManager().getSettings().getAppName() + addname + ".model"));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int getMaxNbBags() {
/* 727 */     return m_NbMaxBags;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ClusAttrType[] selectRandomSubspaces(ClusAttrType[] attrs, int select) {
/* 737 */     int origsize = attrs.length;
/* 738 */     int[] samples = new int[origsize];
/*     */     
/* 740 */     boolean randomize = true;
/* 741 */     int i = 0;
/* 742 */     while (randomize) {
/* 743 */       int rnd = ClusRandom.nextInt(1, origsize);
/* 744 */       if (samples[rnd] == 0) {
/* 745 */         samples[rnd] = samples[rnd] + 1;
/* 746 */         i++;
/*     */       } 
/* 748 */       if (i == select) {
/* 749 */         randomize = false;
/*     */       }
/*     */     } 
/* 752 */     ClusAttrType[] result = new ClusAttrType[select];
/* 753 */     int res = 0;
/* 754 */     for (int k = 0; k < origsize; k++) {
/* 755 */       if (samples[k] != 0) {
/* 756 */         result[res] = attrs[k];
/* 757 */         res++;
/*     */       } 
/*     */     } 
/*     */     
/* 761 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   public static ClusAttrType[] getRandomSubspaces() {
/* 766 */     return m_RandomSubspaces;
/*     */   }
/*     */   
/*     */   public static void setRandomSubspaces(ClusAttrType[] attrs, int select) {
/* 770 */     m_RandomSubspaces = selectRandomSubspaces(attrs, select);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isOptimized() {
/* 777 */     return m_OptMode;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\ext\ensembles\ClusEnsembleInduce.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */