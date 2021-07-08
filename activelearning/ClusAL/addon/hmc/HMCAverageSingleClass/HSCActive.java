/*     */ package addon.hmc.HMCAverageSingleClass;
/*     */ 
/*     */ import clus.Clus;
/*     */ import clus.algo.ClusInductionAlgorithmType;
/*     */ import clus.algo.tdidt.ClusDecisionTree;
/*     */ import clus.data.io.ARFFFile;
/*     */ import clus.data.rows.DataTuple;
/*     */ import clus.data.rows.RowData;
/*     */ import clus.data.rows.TupleIterator;
/*     */ import clus.data.type.ClusAttrType;
/*     */ import clus.data.type.ClusSchema;
/*     */ import clus.data.type.NumericAttrType;
/*     */ import clus.data.type.StringAttrType;
/*     */ import clus.error.ClusError;
/*     */ import clus.error.ClusErrorList;
/*     */ import clus.ext.hierarchical.ClassHierarchy;
/*     */ import clus.ext.hierarchical.ClassTerm;
/*     */ import clus.ext.hierarchical.ClassesTuple;
/*     */ import clus.ext.hierarchical.ClassesValue;
/*     */ import clus.ext.hierarchical.HierClassTresholdPruner;
/*     */ import clus.ext.hierarchical.HierClassWiseAccuracy;
/*     */ import clus.ext.hierarchical.WHTDStatistic;
/*     */ import clus.main.ClusOutput;
/*     */ import clus.main.ClusRun;
/*     */ import clus.main.ClusStatManager;
/*     */ import clus.main.Settings;
/*     */ import clus.model.ClusModel;
/*     */ import clus.model.ClusModelInfo;
/*     */ import clus.model.modelio.ClusModelCollectionIO;
/*     */ import clus.model.processor.ClusModelProcessor;
/*     */ import clus.model.processor.PredictionWriter;
/*     */ import clus.statistic.ClusStatistic;
/*     */ import clus.statistic.RegressionStat;
/*     */ import clus.util.ClusException;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintWriter;
/*     */ import java.util.Arrays;
/*     */ import java.util.Date;
/*     */ import jeans.io.ini.INIFileNominalOrDoubleOrVector;
/*     */ import jeans.util.FileUtil;
/*     */ import jeans.util.array.StringTable;
/*     */ import jeans.util.cmdline.CMDLineArgs;
/*     */ import jeans.util.cmdline.CMDLineArgsProvider;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class HSCActive
/*     */   implements CMDLineArgsProvider
/*     */ {
/*  56 */   private static String[] g_Options = new String[] { "models", "hsc", "stats", "loadPredictions" };
/*  57 */   private static int[] g_OptionArities = new int[] { 1, 0, 0, 1 };
/*     */   
/*     */   private Clus m_Clus;
/*  60 */   protected StringTable m_Table = new StringTable();
/*     */   
/*     */   protected ClusErrorList[][] m_EvalArray;
/*     */   
/*     */   public double[][][] m_PredProb;
/*     */   
/*     */   public double[][] m_Measure;
/*     */   
/*     */   protected int m_NbModels;
/*     */   
/*     */   protected int m_TotSize;
/*     */ 
/*     */   
/*     */   public void run(String[] args) throws IOException, ClusException, ClassNotFoundException {
/*  74 */     setM_Clus(new Clus());
/*  75 */     Settings sett = this.m_Clus.getSettings();
/*  76 */     CMDLineArgs cargs = new CMDLineArgs(this);
/*  77 */     cargs.process(args);
/*  78 */     if (cargs.allOK()) {
/*  79 */       sett.setDate(new Date());
/*  80 */       sett.setAppName(cargs.getMainArg(0));
/*  81 */       this.m_Clus.initSettings(cargs);
/*  82 */       ClusDecisionTree clss = new ClusDecisionTree(this.m_Clus);
/*  83 */       this.m_Clus.initialize(cargs, (ClusInductionAlgorithmType)clss);
/*  84 */       WHTDStatistic wHTDStatistic = createTargetStat();
/*  85 */       wHTDStatistic.calcMean();
/*  86 */       if (cargs.hasOption("stats")) {
/*  87 */         computeStats();
/*  88 */         System.exit(0);
/*     */       } 
/*  90 */       if (cargs.hasOption("models") || cargs.hasOption("hsc")) {
/*  91 */         if (cargs.hasOption("hsc")) {
/*  92 */           this.m_Clus.getSettings().setSuffix(".hsc.combined");
/*     */         } else {
/*     */           
/*  95 */           this.m_Clus.getSettings().setSuffix(".sc.combined");
/*     */         } 
/*  97 */         ClusRun cr = this.m_Clus.partitionData();
/*     */         
/*  99 */         cr.combineTrainAndValidSets();
/*     */         
/* 101 */         ClassHierarchy hier = getStatManager().getHier();
/* 102 */         this.m_PredProb = new double[2][][];
/* 103 */         for (int i = 0; i <= 1; i++) {
/* 104 */           int size = cr.getDataSet(i).getNbRows();
/* 105 */           this.m_PredProb[i] = new double[size][hier.getTotal()];
/* 106 */           for (int k = 0; k < size; k++) {
/* 107 */             Arrays.fill(this.m_PredProb[i][k], Double.MAX_VALUE);
/*     */           }
/*     */         } 
/*     */         
/* 111 */         INIFileNominalOrDoubleOrVector class_thr = getSettings().getClassificationThresholds();
/* 112 */         if (class_thr.isVector()) {
/* 113 */           HierClassTresholdPruner pruner = (HierClassTresholdPruner)getStatManager().getTreePruner(null);
/* 114 */           this.m_EvalArray = new ClusErrorList[2][pruner.getNbResults()];
/* 115 */           for (int j = 0; j < pruner.getNbResults(); j++) {
/* 116 */             for (int k = 0; k <= 1; k++) {
/* 117 */               this.m_EvalArray[k][j] = new ClusErrorList();
/* 118 */               this.m_EvalArray[k][j].addError((ClusError)new HierClassWiseAccuracy(this.m_EvalArray[k][j], hier));
/* 119 */               this.m_EvalArray[k][j].addError(null);
/*     */             } 
/*     */           } 
/*     */         } 
/*     */         
/* 124 */         if (cargs.hasOption("hsc")) {
/*     */           
/* 126 */           HMCAverageNodeWiseModels avg = new HMCAverageNodeWiseModels(this, this.m_PredProb);
/* 127 */           avg.processModels(cr);
/* 128 */           this.m_NbModels = avg.getNbModels();
/* 129 */           this.m_TotSize = avg.getTotalSize();
/* 130 */           if (this.m_EvalArray != null) {
/* 131 */             avg.updateErrorMeasures(cr);
/*     */           }
/*     */         } else {
/* 134 */           loadModelPerModel(cargs.getOptionValue("models"), cr);
/*     */         } 
/*     */         
/* 137 */         ClusOutput output = new ClusOutput(sett.getAppNameWithSuffix() + ".out", this.m_Clus.getSchema(), sett);
/*     */         
/* 139 */         ClusModelInfo def_model = cr.addModelInfo(0);
/* 140 */         def_model.setModel(ClusDecisionTree.induceDefault(cr));
/*     */         
/* 142 */         ClusModelInfo orig_model_inf = cr.addModelInfo(1);
/* 143 */         HMCAverageTreeModel orig_model = new HMCAverageTreeModel((ClusStatistic)wHTDStatistic, this.m_PredProb, this.m_NbModels, this.m_TotSize);
/* 144 */         orig_model_inf.setModel(orig_model);
/*     */         
/* 146 */         cr.copyAllModelsMIs();
/* 147 */         RowData train = (RowData)cr.getTrainingSet();
/* 148 */         train.addIndices();
/* 149 */         orig_model.setDataSet(0);
/* 150 */         this.m_Clus.calcError((TupleIterator)train.getIterator(), 0, cr);
/* 151 */         RowData test = cr.getTestSet();
/* 152 */         if (test != null) {
/* 153 */           test.addIndices();
/* 154 */           orig_model.setDataSet(1);
/* 155 */           this.m_Clus.calcError((TupleIterator)test.getIterator(), 1, cr);
/*     */         } 
/*     */         
/* 158 */         if (class_thr.isVector()) {
/* 159 */           HierClassTresholdPruner pruner = (HierClassTresholdPruner)getStatManager().getTreePruner(null);
/* 160 */           for (int j = 0; j < pruner.getNbResults(); j++) {
/* 161 */             ClusModelInfo pruned_info = cr.addModelInfo(pruner.getPrunedName(j));
/*     */             
/* 163 */             pruned_info.setShouldWritePredictions(false);
/* 164 */             pruned_info.setTrainError(this.m_EvalArray[0][j]);
/* 165 */             pruned_info.setTestError(this.m_EvalArray[1][j]);
/*     */           } 
/*     */         } 
/* 168 */         output.writeHeader();
/* 169 */         output.writeOutput(cr, true, getSettings().isOutTrainError());
/* 170 */         output.close();
/* 171 */       } else if (cargs.hasOption("loadPredictions")) {
/*     */         
/* 173 */         this.m_Clus.getSettings().setSuffix(".evaluatedPredictions");
/* 174 */         ClusRun cr = this.m_Clus.partitionData();
/*     */ 
/*     */ 
/*     */         
/* 178 */         ClassHierarchy hier = getStatManager().getHier();
/* 179 */         int size = cr.getDataSet(1).getNbRows();
/*     */         
/* 181 */         this.m_PredProb = new double[1][size][hier.getTotal()];
/*     */ 
/*     */         
/* 184 */         String file = cargs.getOptionValue("loadPredictions");
/* 185 */         RowData rw = ARFFFile.readArff(file);
/* 186 */         ClusSchema schema = rw.getSchema();
/* 187 */         NumericAttrType[] na = schema.getNumericAttrUse(0);
/*     */ 
/*     */         
/* 190 */         int[] mapping_classes = new int[schema.getNbAttributes()];
/* 191 */         for (int y = 0; y < na.length; y++) {
/* 192 */           String label = na[y].getName();
/*     */           
/* 194 */           boolean found = false;
/* 195 */           for (int a = 0; a < hier.getTotal(); a++) {
/* 196 */             if (hier.getTermAt(a).toStringHuman(hier).equals(label)) {
/* 197 */               mapping_classes[y] = a;
/* 198 */               found = true;
/*     */             } 
/*     */           } 
/* 201 */           if (!found) {
/* 202 */             throw new ClusException("Error: class " + label + " not found.");
/*     */           }
/*     */         } 
/*     */         
/* 206 */         RowData testset = cr.getDataSet(1);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 226 */         System.out.println("Number of rows in predictions-file: " + rw.getNbRows());
/* 227 */         System.out.println("Number of rows in test-file: " + testset.getNbRows());
/*     */ 
/*     */         
/* 230 */         System.out.println("Number of classes: " + hier.getTotal());
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 239 */         for (int x = 0; x < rw.getNbRows(); x++) {
/*     */           
/* 241 */           DataTuple tuple = rw.getTuple(x);
/*     */ 
/*     */           
/* 244 */           DataTuple tuple_test = testset.getTuple(x);
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 249 */           for (int i = 0; i < na.length; i++) {
/* 250 */             int a = mapping_classes[i];
/*     */ 
/*     */             
/* 253 */             this.m_PredProb[0][x][a] = na[i].getNumeric(tuple);
/*     */           } 
/*     */         } 
/*     */ 
/*     */ 
/*     */         
/* 259 */         INIFileNominalOrDoubleOrVector class_thr = getSettings().getClassificationThresholds();
/*     */         
/* 261 */         if (class_thr.isVector()) {
/* 262 */           HierClassTresholdPruner pruner = (HierClassTresholdPruner)getStatManager().getTreePruner(null);
/* 263 */           this.m_EvalArray = new ClusErrorList[2][pruner.getNbResults()];
/* 264 */           for (int i = 0; i < pruner.getNbResults(); i++) {
/*     */             
/* 266 */             this.m_EvalArray[1][i] = new ClusErrorList();
/* 267 */             this.m_EvalArray[1][i].addError((ClusError)new HierClassWiseAccuracy(this.m_EvalArray[1][i], hier));
/* 268 */             this.m_EvalArray[1][i].addError(null);
/*     */           } 
/*     */         } 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 275 */         ClusOutput output = new ClusOutput(sett.getAppNameWithSuffix() + ".out", this.m_Clus.getSchema(), sett);
/*     */         
/* 277 */         ClusModelInfo def_model = cr.addModelInfo(0);
/* 278 */         def_model.setModel(ClusDecisionTree.induceDefault(cr));
/*     */         
/* 280 */         ClusModelInfo orig_model_inf = cr.addModelInfo(1);
/*     */         
/* 282 */         HMCAverageTreeModel orig_model = new HMCAverageTreeModel((ClusStatistic)wHTDStatistic, this.m_PredProb, this.m_NbModels, this.m_TotSize);
/* 283 */         orig_model_inf.setModel(orig_model);
/*     */         
/* 285 */         cr.copyAllModelsMIs();
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 290 */         RowData test = cr.getTestSet();
/* 291 */         if (test != null) {
/* 292 */           test.addIndices();
/*     */           
/* 294 */           this.m_Clus.calcError((TupleIterator)test.getIterator(), 1, cr);
/*     */         } 
/*     */         
/* 297 */         if (class_thr.isVector()) {
/* 298 */           HierClassTresholdPruner pruner = (HierClassTresholdPruner)getStatManager().getTreePruner(null);
/* 299 */           for (int i = 0; i < pruner.getNbResults(); i++) {
/* 300 */             ClusModelInfo pruned_info = cr.addModelInfo(pruner.getPrunedName(i));
/*     */             
/* 302 */             pruned_info.setShouldWritePredictions(false);
/*     */             
/* 304 */             pruned_info.setTestError(this.m_EvalArray[1][i]);
/*     */           } 
/*     */         } 
/*     */         
/* 308 */         output.writeHeader();
/* 309 */         output.writeOutput(cr, true, getSettings().isOutTrainError());
/* 310 */         output.close();
/*     */       } else {
/* 312 */         throw new ClusException("Must specify e.g., -models dirname");
/*     */       } 
/*     */     } 
/*     */   }
/*     */   public final void restartPrinterWriters(ClusRun cr, Settings m_Sett, int activeLearningIteration, String activeLearningAlgorithm, String labelInferingAlgorithm) {
/*     */     String ts_name, tr_name;
/* 318 */     cr.resetAllModelsMI();
/* 319 */     ClusModelInfo allmi = cr.getAllModelsMI();
/*     */     
/* 321 */     if (m_Sett.getPartialLabelling().booleanValue()) {
/* 322 */       ts_name = m_Sett.getAppNameWithSuffix() + "." + activeLearningAlgorithm + "." + labelInferingAlgorithm + ".hsc.pred.partial.test." + activeLearningIteration + ".arff";
/*     */     } else {
/* 324 */       ts_name = m_Sett.getAppNameWithSuffix() + "." + activeLearningAlgorithm + "." + labelInferingAlgorithm + ".hsc.pred.test." + activeLearningIteration + ".arff";
/*     */     } 
/* 326 */     allmi.addModelProcessor(1, (ClusModelProcessor)new PredictionWriter(ts_name, m_Sett, 
/* 327 */           getStatManager().createStatistic(3)));
/*     */ 
/*     */     
/* 330 */     allmi = cr.getAllModelsMI();
/*     */     
/* 332 */     if (m_Sett.getPartialLabelling().booleanValue()) {
/* 333 */       tr_name = m_Sett.getAppNameWithSuffix() + "." + activeLearningAlgorithm + "." + labelInferingAlgorithm + ".hsc.pred.partial.train." + activeLearningIteration + ".arff";
/*     */     } else {
/* 335 */       tr_name = m_Sett.getAppNameWithSuffix() + "." + activeLearningAlgorithm + "." + labelInferingAlgorithm + ".hsc.pred.train." + activeLearningIteration + ".arff";
/*     */     } 
/*     */     
/* 338 */     allmi.addModelProcessor(0, (ClusModelProcessor)new PredictionWriter(tr_name, m_Sett, 
/* 339 */           getStatManager()
/* 340 */           .createStatistic(3)));
/*     */   }
/*     */ 
/*     */   
/*     */   public ClusStatManager getStatManager() {
/* 345 */     return this.m_Clus.getStatManager();
/*     */   }
/*     */   
/*     */   public Settings getSettings() {
/* 349 */     return this.m_Clus.getSettings();
/*     */   }
/*     */   
/*     */   public Clus getClus() {
/* 353 */     return this.m_Clus;
/*     */   }
/*     */   
/*     */   public ClusErrorList getEvalArray(int traintest, int j) {
/* 357 */     return this.m_EvalArray[traintest][j];
/*     */   }
/*     */   
/*     */   public WHTDStatistic createTargetStat() {
/* 361 */     return (WHTDStatistic)this.m_Clus.getStatManager().createStatistic(3);
/*     */   }
/*     */   
/*     */   public String getClassStr(String file) {
/* 365 */     StringBuffer result = new StringBuffer();
/* 366 */     String value = FileUtil.getName(FileUtil.removePath(file));
/* 367 */     String[] cmps = value.split("_");
/* 368 */     String[] elems = cmps[cmps.length - 1].split("-");
/* 369 */     for (int i = 0; i < elems.length; i++) {
/* 370 */       if (i != 0) {
/* 371 */         result.append("/");
/*     */       }
/* 373 */       result.append(elems[i]);
/*     */     } 
/* 375 */     return result.toString();
/*     */   }
/*     */   
/*     */   public int getClassIndex(String file) throws ClusException {
/* 379 */     String class_str = getClassStr(file);
/* 380 */     ClassHierarchy hier = getStatManager().getHier();
/* 381 */     ClassesValue val = new ClassesValue(class_str, hier.getType().getTable());
/* 382 */     return hier.getClassTerm(val).getIndex();
/*     */   }
/*     */   
/*     */   public ClusModel loadModel(String file) throws IOException, ClusException, ClassNotFoundException {
/* 386 */     String class_str = getClassStr(file);
/*     */     
/* 388 */     ClusModelCollectionIO io = ClusModelCollectionIO.load(file);
/* 389 */     ClusModel sub_model = io.getModel("Original");
/* 390 */     if (sub_model == null) {
/* 391 */       throw new ClusException("Error: .model file does not contain model named 'Original'");
/*     */     }
/* 393 */     this.m_NbModels++;
/* 394 */     this.m_TotSize += sub_model.getModelSize();
/* 395 */     return sub_model;
/*     */   }
/*     */   
/*     */   public void loadModelPerModel(String dir, ClusRun cr) throws IOException, ClusException, ClassNotFoundException {
/* 399 */     String[] files = FileUtil.dirList(dir, "model");
/* 400 */     for (int i = 0; i < files.length; i++) {
/* 401 */       ClusModel model = loadModel(FileUtil.cmbPath(dir, files[i]));
/* 402 */       int class_idx = getClassIndex(files[i]);
/* 403 */       for (int j = 0; j <= 1; j++) {
/* 404 */         evaluateModelAndUpdateErrors(j, class_idx, model, cr);
/*     */       }
/*     */     } 
/* 407 */     INIFileNominalOrDoubleOrVector class_thr = getSettings().getClassificationThresholds();
/* 408 */     if (class_thr.isVector()) {
/* 409 */       HierClassTresholdPruner pruner = (HierClassTresholdPruner)getStatManager().getTreePruner(null);
/* 410 */       for (int j = 0; j < pruner.getNbResults(); j++) {
/* 411 */         for (int traintest = 0; traintest <= 1; traintest++) {
/* 412 */           RowData data = cr.getDataSet(traintest);
/* 413 */           ClusErrorList error = getEvalArray(traintest, j);
/* 414 */           error.setNbExamples(data.getNbRows(), data.getNbRows());
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void evaluateModelAndUpdateErrors(int train_or_test, int class_idx, ClusModel model, ClusRun cr) throws ClusException, IOException {
/* 422 */     RowData data = cr.getDataSet(train_or_test);
/* 423 */     this.m_Clus.getSchema().attachModel(model);
/* 424 */     INIFileNominalOrDoubleOrVector class_thr = getSettings().getClassificationThresholds();
/* 425 */     if (class_thr.isVector()) {
/* 426 */       HierClassTresholdPruner pruner = (HierClassTresholdPruner)getStatManager().getTreePruner(null);
/* 427 */       for (int j = 0; j < data.getNbRows(); j++) {
/* 428 */         DataTuple tuple = data.getTuple(j);
/* 429 */         ClusStatistic prediction = model.predictWeighted(tuple);
/* 430 */         double[] predicted_distr = prediction.getNumericPred();
/* 431 */         ClassesTuple tp = (ClassesTuple)tuple.getObjVal(0);
/* 432 */         boolean actually_has_class = tp.hasClass(class_idx);
/* 433 */         for (int k = 0; k < pruner.getNbResults(); k++) {
/*     */           
/* 435 */           boolean predicted_class = (predicted_distr[0] >= pruner.getThreshold(k) / 100.0D);
/* 436 */           HierClassWiseAccuracy acc = (HierClassWiseAccuracy)this.m_EvalArray[train_or_test][k].getError(0);
/* 437 */           acc.nextPrediction(class_idx, predicted_class, actually_has_class);
/*     */         } 
/*     */       } 
/*     */     } 
/* 441 */     for (int i = 0; i < data.getNbRows(); i++) {
/* 442 */       DataTuple tuple = data.getTuple(i);
/* 443 */       ClusStatistic prediction = model.predictWeighted(tuple);
/* 444 */       double[] predicted_distr = prediction.getNumericPred();
/* 445 */       this.m_PredProb[train_or_test][i][class_idx] = predicted_distr[0];
/*     */     } 
/*     */   }
/*     */   
/*     */   public String[] getOptionArgs() {
/* 450 */     return g_Options;
/*     */   }
/*     */   
/*     */   public int[] getOptionArgArities() {
/* 454 */     return g_OptionArities;
/*     */   }
/*     */   
/*     */   public int getNbMainArgs() {
/* 458 */     return 1;
/*     */   }
/*     */ 
/*     */   
/*     */   public void showHelp() {}
/*     */   
/*     */   public void countClasses(RowData data, double[] counts) {
/* 465 */     ClassHierarchy hier = getStatManager().getHier();
/* 466 */     int sidx = hier.getType().getArrayIndex();
/* 467 */     boolean[] arr = new boolean[hier.getTotal()];
/* 468 */     for (int i = 0; i < data.getNbRows(); i++) {
/* 469 */       DataTuple tuple = data.getTuple(i);
/* 470 */       ClassesTuple tp = (ClassesTuple)tuple.getObjVal(sidx);
/*     */       
/* 472 */       Arrays.fill(arr, false);
/* 473 */       tp.fillBoolArrayNodeAndAncestors(arr); int j;
/* 474 */       for (j = 0; j < arr.length; j++) {
/* 475 */         if (arr[j]) {
/* 476 */           counts[0] = counts[0] + 1.0D;
/*     */         }
/*     */       } 
/*     */       
/* 480 */       hier.removeParentNodes(arr);
/* 481 */       for (j = 0; j < arr.length; j++) {
/* 482 */         if (arr[j]) {
/* 483 */           counts[1] = counts[1] + 1.0D;
/*     */         }
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public void computeStats() throws ClusException, IOException {
/* 490 */     ClusRun cr = this.m_Clus.partitionData();
/* 491 */     RegressionStat stat = (RegressionStat)getStatManager().createStatistic(3);
/* 492 */     RowData train = (RowData)cr.getTrainingSet();
/* 493 */     RowData valid = (RowData)cr.getPruneSet();
/* 494 */     RowData test = cr.getTestSet();
/* 495 */     train.calcTotalStat((ClusStatistic)stat);
/* 496 */     if (valid != null) {
/* 497 */       valid.calcTotalStat((ClusStatistic)stat);
/*     */     }
/* 499 */     if (test != null) {
/* 500 */       test.calcTotalStat((ClusStatistic)stat);
/*     */     }
/* 502 */     stat.calcMean();
/* 503 */     ClassHierarchy hier = getStatManager().getHier();
/* 504 */     PrintWriter wrt = getSettings().getFileAbsoluteWriter(getSettings().getAppName() + "-hmcstat.arff");
/* 505 */     ClusSchema schema = new ClusSchema("HMC-Statistics");
/* 506 */     schema.addAttrType((ClusAttrType)new StringAttrType("Class"));
/* 507 */     schema.addAttrType((ClusAttrType)new NumericAttrType("Weight"));
/* 508 */     schema.addAttrType((ClusAttrType)new NumericAttrType("MinDepth"));
/* 509 */     schema.addAttrType((ClusAttrType)new NumericAttrType("MaxDepth"));
/* 510 */     schema.addAttrType((ClusAttrType)new NumericAttrType("Frequency"));
/* 511 */     double total = stat.getTotalWeight();
/* 512 */     double[] classCounts = new double[2];
/* 513 */     countClasses(train, classCounts);
/* 514 */     if (valid != null) {
/* 515 */       countClasses(valid, classCounts);
/*     */     }
/* 517 */     if (test != null) {
/* 518 */       countClasses(test, classCounts);
/*     */     }
/* 520 */     int nbDescriptiveAttrs = this.m_Clus.getSchema().getNbDescriptiveAttributes();
/* 521 */     wrt.println();
/* 522 */     wrt.println("% Number of examples: " + total);
/* 523 */     wrt.println("% Number of descriptive attributes: " + nbDescriptiveAttrs);
/* 524 */     wrt.println("% Number of classes: " + hier.getTotal());
/* 525 */     wrt.println("% Avg number of labels/example: " + (classCounts[0] / total) + " (most specific: " + (classCounts[1] / total) + ")");
/* 526 */     wrt.println("% Hierarchy depth: " + hier.getDepth());
/* 527 */     wrt.println();
/* 528 */     ARFFFile.writeArffHeader(wrt, schema);
/* 529 */     wrt.println("@DATA");
/* 530 */     for (int i = 0; i < hier.getTotal(); i++) {
/* 531 */       ClassTerm term = hier.getTermAt(i);
/* 532 */       int index = term.getIndex();
/* 533 */       wrt.print(term.toStringHuman(hier));
/* 534 */       wrt.print("," + hier.getWeight(index));
/* 535 */       wrt.print("," + term.getMinDepth());
/* 536 */       wrt.print("," + term.getMaxDepth());
/* 537 */       wrt.print("," + stat.getSumValues(index));
/* 538 */       wrt.println();
/*     */     } 
/* 540 */     wrt.close();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void main(String[] args) {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setM_Clus(Clus m_Clus) {
/* 560 */     this.m_Clus = m_Clus;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\addon\hmc\HMCAverageSingleClass\HSCActive.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */