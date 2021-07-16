/*      */ package clus;
/*      */ import addon.hmc.HMCNodeWiseModels.hmcnwmodels.HSC;
/*      */ import clus.activelearning.algo.ClusActiveLearningAlgorithm;
/*      */ import clus.activelearning.algo.ClusActiveLearningAlgorithmHMC;
/*      */ import clus.activelearning.algo.ClusLabelInferingAlgorithm;
/*      */ import clus.activelearning.algo.ClusLabelPairFindingAlgorithm;
/*      */ import clus.activelearning.algoHMC.QueryByCommitteeHMC;
/*      */ import clus.activelearning.algoHMC.RandomSamplingHMC;
/*      */ import clus.activelearning.algoHMC.UncertaintySamplingHMC;
/*      */ import clus.activelearning.algoHMC.UncertaintyVarianceCostHMC;
/*      */ import clus.activelearning.labelcorrelation.UncertaintyInfering;
/*      */ import clus.activelearning.labelcorrelation.VarianceInfering;
/*      */ import clus.activelearning.labelinfering.KNN;
/*      */ import clus.activelearning.labelinfering.RankingProbability;
/*      */ import clus.activelearning.labelinfering.SSMAL;
/*      */ import clus.activelearning.labelinfering.VSSMAL;
/*      */ import clus.algo.ClusInductionAlgorithm;
/*      */ import clus.algo.ClusInductionAlgorithmType;
/*      */ import clus.algo.kNN.KNNTreeClassifier;
/*      */ import clus.algo.rules.ClusRuleClassifier;
/*      */ import clus.algo.rules.ClusRuleSet;
/*      */ import clus.algo.tdidt.ClusDecisionTree;
/*      */ import clus.algo.tdidt.ClusNode;
/*      */ import clus.algo.tdidt.ClusSITDecisionTree;
/*      */ import clus.algo.tdidt.ConstraintDFInduce;
/*      */ import clus.algo.tdidt.tune.CDTuneSizeConstrPruning;
/*      */ import clus.data.ClusData;
/*      */ import clus.data.io.ARFFFile;
/*      */ import clus.data.io.ClusReader;
/*      */ import clus.data.io.ClusView;
/*      */ import clus.data.rows.DataPreprocs;
/*      */ import clus.data.rows.DataTuple;
/*      */ import clus.data.rows.DiskTupleIterator;
/*      */ import clus.data.rows.RowData;
/*      */ import clus.data.rows.TupleIterator;
/*      */ import clus.data.type.ClusAttrType;
/*      */ import clus.data.type.ClusSchema;
/*      */ import clus.data.type.NominalAttrType;
/*      */ import clus.data.type.NumericAttrType;
/*      */ import clus.data.type.TimeSeriesAttrType;
/*      */ import clus.error.ClusError;
/*      */ import clus.error.ClusErrorList;
/*      */ import clus.error.ClusErrorOutput;
/*      */ import clus.error.CorrelationMatrixComputer;
/*      */ import clus.error.multiscore.MultiScore;
/*      */ import clus.ext.constraint.ClusConstraintFile;
/*      */ import clus.ext.ensembles.ClusEnsembleClassifier;
/*      */ import clus.ext.ensembles.ClusEnsembleInduce;
/*      */ import clus.ext.hierarchical.ClassHierarchy;
/*      */ import clus.gui.TreeFrame;
/*      */ import clus.main.ClusOutput;
/*      */ import clus.main.ClusRun;
/*      */ import clus.main.ClusStat;
/*      */ import clus.main.ClusStatManager;
/*      */ import clus.main.ClusSummary;
/*      */ import clus.main.Settings;
/*      */ import clus.model.ClusModel;
/*      */ import clus.model.ClusModelInfo;
/*      */ import clus.model.modelio.ClusModelCollectionIO;
/*      */ import clus.model.modelio.ClusTreeReader;
/*      */ import clus.model.processor.ClusEnsemblePredictionWriter;
/*      */ import clus.model.processor.ClusModelProcessor;
/*      */ import clus.model.processor.ModelProcessorCollection;
/*      */ import clus.model.processor.PredictionWriter;
/*      */ import clus.pruning.PruneTree;
/*      */ import clus.selection.BaggingSelection;
/*      */ import clus.selection.ClusSelection;
/*      */ import clus.selection.CriterionBasedSelection;
/*      */ import clus.selection.OverSample;
/*      */ import clus.selection.RandomSelection;
/*      */ import clus.selection.XValMainSelection;
/*      */ import clus.selection.XValSelection;
/*      */ import clus.statistic.ClusStatistic;
/*      */ import clus.statistic.CombStat;
/*      */ import clus.statistic.RegressionStat;
/*      */ import clus.util.ClusException;
/*      */ import clus.util.ClusFormat;
/*      */ import clus.util.ClusRandom;
/*      */ import java.io.FileNotFoundException;
/*      */ import java.io.IOException;
/*      */ import java.io.LineNumberReader;
/*      */ import java.io.PrintWriter;
/*      */ import java.text.NumberFormat;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Date;
/*      */ import jeans.io.MyFile;
/*      */ import jeans.io.ObjectLoadStream;
/*      */ import jeans.io.ObjectSaveStream;
/*      */ import jeans.resource.ResourceInfo;
/*      */ import jeans.util.FileUtil;
/*      */ import jeans.util.IntervalCollection;
/*      */ import jeans.util.StringUtils;
/*      */ import jeans.util.cmdline.CMDLineArgs;
/*      */ 
/*      */ public class Clus implements CMDLineArgsProvider {
/*   96 */   public static final String[] OPTION_ARGS = new String[] { "exhaustive", "xval", "oxval", "target", "disable", "silent", "lwise", "c45", "info", "sample", "debug", "tuneftest", "load", "soxval", "bag", "obag", "show", "knn", "knnTree", "beam", "gui", "fillin", "rules", "weka", "corrmatrix", "tunesize", "out2model", "test", "normalize", "tseries", "writetargets", "fold", "forest", "copying", "sit", "tc", "active", "hsc" };
/*      */ 
/*      */   
/*      */   public static final boolean m_UseHier = true;
/*      */   
/*      */   public static final String VERSION = "2.12";
/*      */   
/*  103 */   public static final int[] OPTION_ARITIES = new int[] { 0, 0, 0, 1, 1, 0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0 };
/*      */ 
/*      */ 
/*      */   
/*  107 */   protected Settings m_Sett = new Settings();
/*  108 */   private ClusSummary m_Summary = new ClusSummary();
/*      */   
/*      */   private ClusSchema m_Schema;
/*      */   
/*      */   protected MultiScore m_Score;
/*      */   
/*      */   protected ClusInductionAlgorithmType m_Classifier;
/*      */   
/*      */   protected ClusInductionAlgorithm m_Induce;
/*      */   protected RowData m_Data;
/*      */   private RowData m_ActiveData;
/*      */   private RowData m_QueriedData;
/*      */   protected ClusSchema m_ActiveSchema;
/*  121 */   protected Date m_StartDate = new Date();
/*      */   
/*      */   protected boolean isxval = false;
/*      */   protected CMDLineArgs m_CmdLine;
/*      */   
/*      */   public final void initializeActive(CMDLineArgs cargs, ClusInductionAlgorithmType clss) throws IOException, ClusException {
/*  127 */     this.m_CmdLine = cargs;
/*  128 */     this.m_Classifier = clss;
/*      */     
/*  130 */     boolean test = (this.m_Sett.getResourceInfoLoaded() == 2);
/*  131 */     ResourceInfo.loadLibrary(test);
/*      */     
/*  133 */     ARFFFile arff = null;
/*  134 */     if (this.m_Sett.getVerbose() > 0) {
/*  135 */       System.out.println("Loading '" + this.m_Sett.getAppName() + "'");
/*      */     }
/*      */     
/*  138 */     ClusRandom.initialize(this.m_Sett);
/*  139 */     ClusReader reader = new ClusReader(this.m_Sett.getDataFile(), this.m_Sett);
/*  140 */     if (this.m_Sett.getVerbose() > 0) {
/*  141 */       System.out.println();
/*      */     }
/*  143 */     if (cargs.hasOption("c45")) {
/*  144 */       if (this.m_Sett.getVerbose() > 0) {
/*  145 */         System.out.println("Reading C45 .names/.data");
/*      */       }
/*      */     } else {
/*  148 */       if (this.m_Sett.getVerbose() > 0) {
/*  149 */         System.out.println("Reading ARFF Header");
/*      */       }
/*  151 */       arff = new ARFFFile(reader);
/*  152 */       setM_Schema(arff.read(this.m_Sett));
/*      */     } 
/*      */     
/*  155 */     if (this.m_Sett.getVerbose() > 0) {
/*  156 */       System.out.println();
/*      */     }
/*  158 */     if (this.m_Sett.getVerbose() > 0) {
/*  159 */       System.out.println("Reading CSV Data");
/*      */     }
/*      */ 
/*      */     
/*  163 */     this.m_Sett.updateTarget(getM_Schema());
/*      */     
/*  165 */     getM_Schema().initializeSettings(this.m_Sett);
/*      */     
/*  167 */     this.m_Sett.setTarget(getM_Schema().getTarget().toString());
/*  168 */     this.m_Sett.setDisabled(getM_Schema().getDisabled().toString());
/*  169 */     this.m_Sett.setClustering(getM_Schema().getClustering().toString());
/*  170 */     this.m_Sett.setDescriptive(getM_Schema().getDescriptive().toString());
/*      */ 
/*      */     
/*  173 */     if (ResourceInfo.isLibLoaded()) {
/*  174 */       ClusStat.m_InitialMemory = ResourceInfo.getMemory();
/*      */     }
/*  176 */     ClusView view = getM_Schema().createNormalView();
/*      */     
/*  178 */     this.m_Data = view.readData(reader, getM_Schema());
/*      */     
/*  180 */     reader.close();
/*      */ 
/*      */ 
/*      */     
/*  184 */     if (!this.m_Sett.isDefaultActiveTarget()) {
/*  185 */       throw new ClusException("Couldn't find Active data file");
/*      */     }
/*  187 */     ClusReader ActiveReader = new ClusReader(this.m_Sett.getActiveDataFile(), this.m_Sett);
/*  188 */     ARFFFile ActiveArff = new ARFFFile(ActiveReader);
/*  189 */     this.m_ActiveSchema = ActiveArff.read(this.m_Sett);
/*      */     
/*  191 */     this.m_Sett.updateActiveTarget(this.m_ActiveSchema);
/*      */     
/*  193 */     this.m_ActiveSchema.initializeSettings(this.m_Sett);
/*      */ 
/*      */     
/*  196 */     this.m_Sett.setActiveTarget(this.m_ActiveSchema.getTarget().toString());
/*      */     
/*  198 */     this.m_Sett.setActiveDisabled(this.m_ActiveSchema.getDisabled().toString());
/*  199 */     this.m_Sett.setActiveClustering(this.m_ActiveSchema.getClustering().toString());
/*  200 */     this.m_Sett.setActiveDescriptive(this.m_ActiveSchema.getDescriptive().toString());
/*  201 */     ClusView ActiveView = this.m_ActiveSchema.createNormalView();
/*  202 */     setM_ActiveData(ActiveView.readData(ActiveReader, this.m_ActiveSchema));
/*      */ 
/*      */     
/*  205 */     ActiveReader.close();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  215 */     if (this.m_Sett.isActiveParametersOK()) {
/*  216 */       if (this.m_Sett.getVerbose() > 0) {
/*  217 */         System.out.println("Found " + getM_ActiveData().getNbRows() + " rows on Active Learning Dataset");
/*      */       }
/*  219 */       if (getSettings().getNormalizeData() != 0) {
/*  220 */         if (this.m_Sett.getVerbose() > 0) {
/*  221 */           System.out.println("Normalizing numerical data");
/*      */         }
/*      */         
/*  224 */         setM_ActiveData(returnNormalizedData(getM_ActiveData()));
/*      */       } 
/*  226 */       if (getSettings().isRemoveMissingTarget()) {
/*  227 */         setM_ActiveData(CriterionBasedSelection.removeMissingTarget(getM_ActiveData()));
/*  228 */         CriterionBasedSelection.clearMissingFlagTargetAttrs(this.m_ActiveSchema);
/*      */       } 
/*  230 */       if (this.m_Sett.getVerbose() > 0) {
/*  231 */         System.out.println("Found " + this.m_Data.getNbRows() + " rows");
/*      */       }
/*      */     } 
/*      */     
/*  235 */     this.m_Induce = clss.createInduce(getM_Schema(), this.m_Sett, cargs);
/*      */ 
/*      */ 
/*      */     
/*  239 */     this.m_Sett.update(getM_Schema());
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  246 */     if (!this.m_Induce.getStatManager().isRuleInduceOnly()) {
/*  247 */       this.m_Sett.disableRuleInduceParams();
/*      */     }
/*      */     
/*  250 */     if (this.isxval) {
/*  251 */       Settings.IS_XVAL = true;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*  256 */     preprocessActive();
/*      */ 
/*      */ 
/*      */     
/*  260 */     this.m_Induce.initialize();
/*      */     
/*  262 */     initializeAttributeWeights((ClusData)this.m_Data);
/*  263 */     initializeAttributeWeights((ClusData)getM_ActiveData());
/*      */     
/*  265 */     this.m_Induce.initializeHeuristic();
/*  266 */     loadConstraintFile();
/*  267 */     initializeSummary(clss);
/*  268 */     if (this.m_Sett.getVerbose() > 0) {
/*  269 */       System.out.println();
/*      */     }
/*      */     
/*  272 */     if (cargs.hasOption("sample")) {
/*  273 */       String svalue = cargs.getOptionValue("sample");
/*  274 */       sample(svalue);
/*      */     } 
/*  276 */     if (this.m_Sett.getVerbose() > 0) {
/*  277 */       System.out.println("Has missing values: " + getM_Schema().hasMissing());
/*      */     }
/*  279 */     if (ResourceInfo.isLibLoaded()) {
/*  280 */       System.out.println("Memory usage: loading data took " + (ClusStat.m_LoadedMemory - ClusStat.m_InitialMemory) + " kB");
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final void initialize(CMDLineArgs cargs, ClusInductionAlgorithmType clss) throws IOException, ClusException {
/*  289 */     this.m_CmdLine = cargs;
/*  290 */     this.m_Classifier = clss;
/*      */     
/*  292 */     boolean test = (this.m_Sett.getResourceInfoLoaded() == 2);
/*  293 */     ResourceInfo.loadLibrary(test);
/*      */     
/*  295 */     ARFFFile arff = null;
/*  296 */     if (this.m_Sett.getVerbose() > 0) {
/*  297 */       System.out.println("Loading '" + this.m_Sett.getAppName() + "'");
/*      */     }
/*  299 */     ClusRandom.initialize(this.m_Sett);
/*  300 */     ClusReader reader = new ClusReader(this.m_Sett.getDataFile(), this.m_Sett);
/*  301 */     if (this.m_Sett.getVerbose() > 0) {
/*  302 */       System.out.println();
/*      */     }
/*  304 */     if (cargs.hasOption("c45")) {
/*  305 */       if (this.m_Sett.getVerbose() > 0) {
/*  306 */         System.out.println("Reading C45 .names/.data");
/*      */       }
/*      */     } else {
/*  309 */       if (this.m_Sett.getVerbose() > 0) {
/*  310 */         System.out.println("Reading ARFF Header");
/*      */       }
/*  312 */       arff = new ARFFFile(reader);
/*  313 */       setM_Schema(arff.read(this.m_Sett));
/*      */     } 
/*      */     
/*  316 */     if (this.m_Sett.getVerbose() > 0) {
/*  317 */       System.out.println();
/*      */     }
/*  319 */     if (this.m_Sett.getVerbose() > 0) {
/*  320 */       System.out.println("Reading CSV Data");
/*      */     }
/*      */     
/*  323 */     this.m_Sett.updateTarget(getM_Schema());
/*  324 */     getM_Schema().initializeSettings(this.m_Sett);
/*  325 */     this.m_Sett.setTarget(getM_Schema().getTarget().toString());
/*  326 */     this.m_Sett.setDisabled(getM_Schema().getDisabled().toString());
/*  327 */     this.m_Sett.setClustering(getM_Schema().getClustering().toString());
/*  328 */     this.m_Sett.setDescriptive(getM_Schema().getDescriptive().toString());
/*      */ 
/*      */     
/*  331 */     if (ResourceInfo.isLibLoaded()) {
/*  332 */       ClusStat.m_InitialMemory = ResourceInfo.getMemory();
/*      */     }
/*  334 */     ClusView view = getM_Schema().createNormalView();
/*  335 */     this.m_Data = view.readData(reader, getM_Schema());
/*  336 */     reader.close();
/*  337 */     if (this.m_Sett.getVerbose() > 0) {
/*  338 */       System.out.println("Found " + this.m_Data.getNbRows() + " rows");
/*      */     }
/*      */     
/*  341 */     if (getSettings().getNormalizeData() != 0) {
/*  342 */       if (this.m_Sett.getVerbose() > 0) {
/*  343 */         System.out.println("Normalizing numerical data");
/*      */       }
/*  345 */       this.m_Data = returnNormalizedData(this.m_Data);
/*      */     } 
/*      */     
/*  348 */     getM_Schema().printInfo();
/*  349 */     if (ResourceInfo.isLibLoaded()) {
/*  350 */       ClusStat.m_LoadedMemory = ResourceInfo.getMemory();
/*      */     }
/*  352 */     if (getSettings().isRemoveMissingTarget()) {
/*  353 */       this.m_Data = CriterionBasedSelection.removeMissingTarget(this.m_Data);
/*  354 */       CriterionBasedSelection.clearMissingFlagTargetAttrs(getM_Schema());
/*      */     } 
/*      */ 
/*      */     
/*  358 */     this.m_Induce = clss.createInduce(getM_Schema(), this.m_Sett, cargs);
/*      */ 
/*      */     
/*  361 */     this.m_Sett.update(getM_Schema());
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  368 */     if (!this.m_Induce.getStatManager().isRuleInduceOnly()) {
/*  369 */       this.m_Sett.disableRuleInduceParams();
/*      */     }
/*      */     
/*  372 */     if (this.isxval) {
/*  373 */       Settings.IS_XVAL = true;
/*      */     }
/*      */     
/*  376 */     preprocess();
/*      */ 
/*      */     
/*  379 */     this.m_Induce.initialize();
/*  380 */     initializeAttributeWeights((ClusData)this.m_Data);
/*  381 */     this.m_Induce.initializeHeuristic();
/*  382 */     loadConstraintFile();
/*  383 */     if (this.m_Sett.getVerbose() > 0) {
/*  384 */       System.out.println();
/*      */     }
/*      */     
/*  387 */     if (cargs.hasOption("sample")) {
/*  388 */       String svalue = cargs.getOptionValue("sample");
/*  389 */       sample(svalue);
/*      */     } 
/*  391 */     if (this.m_Sett.getVerbose() > 0) {
/*  392 */       System.out.println("Has missing values: " + getM_Schema().hasMissing());
/*      */     }
/*  394 */     if (ResourceInfo.isLibLoaded()) {
/*  395 */       System.out.println("Memory usage: loading data took " + (ClusStat.m_LoadedMemory - ClusStat.m_InitialMemory) + " kB");
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void initialize(RowData data, ClusSchema schema, Settings sett, ClusInductionAlgorithmType clss) throws IOException, ClusException {
/*  403 */     this.m_Data = data;
/*  404 */     this.m_Sett = sett;
/*  405 */     this.m_Classifier = clss;
/*  406 */     setM_Schema(schema);
/*  407 */     this.m_CmdLine = new CMDLineArgs(this);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void recreateInduce(CMDLineArgs cargs, ClusInductionAlgorithmType clss, ClusSchema schema, RowData data) throws ClusException, IOException {
/*  418 */     setM_Summary(new ClusSummary());
/*  419 */     setM_Schema(schema);
/*  420 */     this.m_Induce = clss.createInduce(schema, this.m_Sett, cargs);
/*  421 */     this.m_Data = data;
/*  422 */     this.m_Classifier = clss;
/*  423 */     data.setSchema(schema);
/*  424 */     this.m_Induce.initialize();
/*  425 */     initializeAttributeWeights((ClusData)data);
/*  426 */     this.m_Induce.initializeHeuristic();
/*  427 */     initializeSummary(clss);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void initializeAddOn(String appname) throws ClusException, IOException {
/*  438 */     Settings sett = getSettings();
/*  439 */     sett.setDate(new Date());
/*  440 */     sett.setAppName(appname);
/*  441 */     initSettings(null);
/*  442 */     ClusDecisionTree clss = new ClusDecisionTree(this);
/*  443 */     initialize(new CMDLineArgs(this), (ClusInductionAlgorithmType)clss);
/*      */   }
/*      */   
/*      */   public final void loadConstraintFile() throws IOException {
/*  447 */     if (this.m_Sett.hasConstraintFile()) {
/*  448 */       ClusConstraintFile constr = ClusConstraintFile.getInstance();
/*  449 */       constr.load(this.m_Sett.getConstraintFile(), getM_Schema());
/*      */     } 
/*      */   }
/*      */   
/*      */   public final void initSettings(CMDLineArgs cargs) throws IOException {
/*  454 */     this.m_Sett.initialize(cargs, true);
/*      */   }
/*      */   
/*      */   public final void initializeSummary(ClusInductionAlgorithmType clss) {
/*  458 */     ClusStatManager mgr = this.m_Induce.getStatManager();
/*  459 */     ClusErrorList error = mgr.createErrorMeasure(this.m_Score);
/*  460 */     getM_Summary().resetAll();
/*  461 */     getM_Summary().setStatManager(mgr);
/*  462 */     if (this.m_Sett.isOutTrainError()) {
/*  463 */       getM_Summary().setTrainError(error);
/*      */     }
/*  465 */     if (hasTestSet() && this.m_Sett.isOutTestError()) {
/*  466 */       getM_Summary().setTestError(error);
/*      */     }
/*  468 */     if (hasPruneSet() && this.m_Sett.isOutValidError()) {
/*  469 */       getM_Summary().setValidationError(error);
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   public final void initializeClassWeights() {
/*  475 */     double[] we = this.m_Sett.getClassWeight();
/*      */ 
/*      */     
/*  478 */     System.out.println(we);
/*      */     
/*  480 */     ClusAttrType[] classes = getM_Schema().getAllAttrUse(3);
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  485 */     ClusAttrType targetclass = classes[0];
/*  486 */     RowData data = this.m_Data;
/*  487 */     int nbrows = this.m_Data.getNbRows();
/*  488 */     for (int i = 0; i < nbrows; i++) {
/*  489 */       DataTuple tuple = data.getTuple(i);
/*  490 */       if (targetclass.getString(tuple).equals("[pos]")) {
/*      */ 
/*      */         
/*  493 */         DataTuple newTuple = tuple.changeWeight(we[0]);
/*  494 */         data.setTuple(newTuple, i);
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public final void sample(String svalue) {
/*      */     RandomSelection randomSelection;
/*  503 */     int nb_rows = this.m_Data.getNbRows();
/*  504 */     int ps_perc = svalue.indexOf('%');
/*  505 */     if (ps_perc != -1) {
/*      */       
/*  507 */       double val = Double.parseDouble(svalue.substring(0, ps_perc + 1)) / 100.0D;
/*  508 */       if (val < 1.0D) {
/*  509 */         randomSelection = new RandomSelection(nb_rows, val);
/*      */       } else {
/*  511 */         OverSample overSample = new OverSample(nb_rows, val);
/*      */       } 
/*      */     } else {
/*  514 */       randomSelection = new RandomSelection(nb_rows, Integer.parseInt(svalue));
/*      */     } 
/*  516 */     this.m_Data = (RowData)this.m_Data.selectFrom((ClusSelection)randomSelection);
/*  517 */     int nb_sel = this.m_Data.getNbRows();
/*  518 */     System.out.println("Sample (" + svalue + ") " + nb_rows + " -> " + nb_sel);
/*      */     
/*  520 */     System.out.println();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final void induce(ClusRun cr, ClusInductionAlgorithmType clss) throws ClusException, IOException {
/*  533 */     if (Settings.VERBOSE > 0) {
/*  534 */       System.out.println("Run: " + cr.getIndexString());
/*  535 */       clss.printInfo();
/*  536 */       System.out.println();
/*      */     } 
/*  538 */     clss.induceAll(cr);
/*      */     
/*  540 */     if (Settings.VERBOSE > 0) {
/*  541 */       System.out.println();
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   public final void showTree(String fname) throws ClusException, IOException, ClassNotFoundException {
/*  547 */     TreeFrame.showTree(getSettings().getFileAbsolute(fname));
/*      */   }
/*      */ 
/*      */   
/*      */   public final void gui(String lok) throws ClusException, IOException, ClassNotFoundException {
/*  552 */     ClusSchema schema = new ClusSchema("Clus");
/*  553 */     ClusStatManager mgr = new ClusStatManager(schema, this.m_Sett, false);
/*  554 */     TreeFrame.start(mgr, lok);
/*      */   }
/*      */ 
/*      */   
/*      */   public final void postprocModel(ClusModel model, TupleIterator iter, ModelProcessorCollection coll) throws IOException, ClusException {
/*  559 */     iter.init();
/*  560 */     ClusSchema mschema = iter.getSchema();
/*  561 */     if (iter.shouldAttach()) {
/*  562 */       System.out
/*  563 */         .println("Effect of should_attach not implemented in postprocModel");
/*      */     }
/*  565 */     coll.initialize(model, mschema);
/*  566 */     DataTuple tuple = iter.readTuple();
/*  567 */     while (tuple != null) {
/*  568 */       model.applyModelProcessors(tuple, (MyArray)coll);
/*  569 */       coll.modelDone();
/*  570 */       tuple = iter.readTuple();
/*      */     } 
/*  572 */     iter.close();
/*  573 */     coll.terminate(model);
/*      */   }
/*      */   
/*      */   public final int getNbRows() {
/*  577 */     return this.m_Data.getNbRows();
/*      */   }
/*      */   
/*      */   public final RowData getData() {
/*  581 */     return this.m_Data;
/*      */   }
/*      */   
/*      */   public final RowData getRowDataClone() {
/*  585 */     return (RowData)this.m_Data.cloneData();
/*      */   }
/*      */   
/*      */   public final MultiScore getMultiScore() {
/*  589 */     return this.m_Score;
/*      */   }
/*      */   
/*      */   public final ClusInductionAlgorithm getInduce() {
/*  593 */     return this.m_Induce;
/*      */   }
/*      */   
/*      */   public final ClusInductionAlgorithmType getClassifier() {
/*  597 */     return this.m_Classifier;
/*      */   }
/*      */   
/*      */   public final ClusStatManager getStatManager() {
/*  601 */     return this.m_Induce.getStatManager();
/*      */   }
/*      */   
/*      */   public final MultiScore getScore() {
/*  605 */     return this.m_Score;
/*      */   }
/*      */   
/*      */   public final ClusSchema getSchema() {
/*  609 */     return getM_Schema();
/*      */   }
/*      */   
/*      */   public final Settings getSettings() {
/*  613 */     return this.m_Sett;
/*      */   }
/*      */   
/*      */   public final ClusSummary getSummary() {
/*  617 */     return getM_Summary();
/*      */   }
/*      */   
/*      */   public final DataPreprocs getPreprocs(boolean single) {
/*  621 */     DataPreprocs pps = new DataPreprocs();
/*  622 */     getM_Schema().getPreprocs(pps, single);
/*  623 */     this.m_Induce.getPreprocs(pps);
/*  624 */     return pps;
/*      */   }
/*      */ 
/*      */   
/*      */   public final void initializeAttributeWeights(ClusData data) throws IOException, ClusException {
/*  629 */     ClusStatManager mgr = getInduce().getStatManager();
/*  630 */     ClusStatistic allStat = mgr.createStatistic(0);
/*  631 */     ClusStatistic[] stats = new ClusStatistic[1];
/*  632 */     stats[0] = allStat;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  640 */     mgr.initNormalizationWeights(allStat, data);
/*  641 */     mgr.initClusteringWeights();
/*  642 */     mgr.initDispersionWeights();
/*  643 */     mgr.initHeuristic();
/*  644 */     mgr.initStopCriterion();
/*  645 */     mgr.initSignifcanceTestingTable();
/*      */   }
/*      */   
/*      */   public final void preprocess(ClusData data) throws ClusException {
/*  649 */     DataPreprocs pps = getPreprocs(false);
/*  650 */     int nb = pps.getNbPasses();
/*  651 */     for (int i = 0; i < nb; i++) {
/*  652 */       data.preprocess(i, pps);
/*  653 */       pps.done(i);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final void preprocSingle(RowData data) throws ClusException {
/*  662 */     DataPreprocs pps = getPreprocs(true);
/*  663 */     for (int i = 0; i < data.getNbRows(); i++) {
/*  664 */       DataTuple tuple = data.getTuple(i);
/*  665 */       pps.preprocSingle(tuple);
/*      */     } 
/*      */   }
/*      */   
/*      */   public final void preprocess() throws ClusException {
/*  670 */     preprocess((ClusData)this.m_Data);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final void preprocessActive() throws ClusException {
/*  680 */     preprocess((ClusData)this.m_Data);
/*  681 */     preprocess((ClusData)getM_ActiveData());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final boolean hasTestSet() {
/*  691 */     if (!this.m_Sett.isNullTestFile()) {
/*  692 */       return true;
/*      */     }
/*  694 */     if (this.m_Sett.getTestProportion() != 0.0D) {
/*  695 */       return true;
/*      */     }
/*  697 */     if (this.isxval) {
/*  698 */       return true;
/*      */     }
/*  700 */     return false;
/*      */   }
/*      */   
/*      */   public final boolean hasPruneSet() {
/*  704 */     if (!this.m_Sett.isNullPruneFile()) {
/*  705 */       return true;
/*      */     }
/*  707 */     if (this.m_Sett.getPruneProportion() != 0.0D) {
/*  708 */       return true;
/*      */     }
/*  710 */     return false;
/*      */   }
/*      */ 
/*      */   
/*      */   public final RowData loadDataFile(String fname) throws IOException, ClusException {
/*  715 */     ClusReader reader = new ClusReader(fname, this.m_Sett);
/*  716 */     if (Settings.VERBOSE > 0) {
/*  717 */       System.out.println("Reading: " + fname);
/*      */     }
/*  719 */     ARFFFile arff = new ARFFFile(reader);
/*      */     
/*  721 */     arff.read(this.m_Sett);
/*      */     
/*  723 */     ClusView view = getM_Schema().createNormalView();
/*  724 */     RowData data = view.readData(reader, getM_Schema());
/*  725 */     reader.close();
/*  726 */     if (Settings.VERBOSE > 0) {
/*  727 */       System.out.println("Found " + data.getNbRows() + " rows");
/*      */     }
/*  729 */     preprocSingle(data);
/*  730 */     return data;
/*      */   }
/*      */   public final ClusRun partitionData() throws IOException, ClusException {
/*      */     RandomSelection randomSelection;
/*  734 */     boolean testfile = false;
/*  735 */     boolean writetest = false;
/*  736 */     ClusSelection sel = null;
/*  737 */     if (!this.m_Sett.isNullTestFile()) {
/*  738 */       testfile = true;
/*  739 */       writetest = true;
/*      */     } else {
/*  741 */       double test = this.m_Sett.getTestProportion();
/*  742 */       if (test != 0.0D) {
/*  743 */         int nbtot = this.m_Data.getNbRows();
/*  744 */         randomSelection = new RandomSelection(nbtot, test);
/*  745 */         writetest = true;
/*      */       } 
/*      */     } 
/*  748 */     return partitionData((ClusData)this.m_Data, (ClusSelection)randomSelection, testfile, writetest, getM_Summary(), 1);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final ClusRun partitionData(ClusSelection sel, int idx) throws IOException, ClusException {
/*  755 */     return partitionData((ClusData)this.m_Data, sel, false, false, getM_Summary(), idx);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final ClusRun partitionData(ClusData data, ClusSelection sel, boolean testfile, boolean writetest, ClusSummary summary, int idx) throws IOException, ClusException {
/*  762 */     String test_fname = this.m_Sett.getAppName();
/*  763 */     ClusRun cr = partitionDataBasic(data, sel, summary, idx);
/*  764 */     boolean hasMissing = getM_Schema().hasMissing();
/*  765 */     if (testfile) {
/*  766 */       test_fname = this.m_Sett.getTestFile();
/*  767 */       MyClusInitializer init = new MyClusInitializer();
/*      */       
/*  769 */       DiskTupleIterator diskTupleIterator = new DiskTupleIterator(test_fname, init, getPreprocs(true), this.m_Sett);
/*  770 */       diskTupleIterator.setShouldAttach(true);
/*  771 */       cr.setTestSet((TupleIterator)diskTupleIterator);
/*      */     } 
/*      */ 
/*      */     
/*  775 */     if (writetest) {
/*  776 */       if (this.m_Sett.isWriteModelIDPredictions()) {
/*  777 */         ClusModelInfo mi = cr.addModelInfo(1);
/*  778 */         String ts_name = this.m_Sett.getAppNameWithSuffix() + ".test.id";
/*  779 */         mi.addModelProcessor(1, (ClusModelProcessor)new NodeIDWriter(ts_name, hasMissing, this.m_Sett));
/*      */       } 
/*      */       
/*  782 */       if (this.m_Sett.isWriteTestSetPredictions()) {
/*  783 */         ClusModelInfo allmi = cr.getAllModelsMI();
/*  784 */         String ts_name = this.m_Sett.getAppNameWithSuffix() + ".test.pred.txt";
/*      */         
/*  786 */         allmi
/*  787 */           .addModelProcessor(1, (ClusModelProcessor)new PredictionWriter(ts_name, this.m_Sett, 
/*      */               
/*  789 */               getStatManager().createStatistic(3)));
/*      */       } 
/*      */     } 
/*      */     
/*  793 */     if (this.m_Sett.isWriteTrainSetPredictions()) {
/*  794 */       ClusModelInfo allmi = cr.getAllModelsMI();
/*  795 */       String tr_name = this.m_Sett.getAppNameWithSuffix() + ".train." + idx + ".pred.txt";
/*      */       
/*  797 */       allmi.addModelProcessor(0, (ClusModelProcessor)new PredictionWriter(tr_name, this.m_Sett, 
/*  798 */             getStatManager()
/*  799 */             .createStatistic(3)));
/*      */     } 
/*  801 */     if (this.m_Sett.isWriteModelIDPredictions()) {
/*  802 */       ClusModelInfo mi = cr.addModelInfo(1);
/*  803 */       String id_tr_name = this.m_Sett.getAppNameWithSuffix() + ".train." + idx + ".id";
/*      */       
/*  805 */       mi.addModelProcessor(0, (ClusModelProcessor)new NodeExampleCollector(id_tr_name, hasMissing, this.m_Sett));
/*      */     } 
/*      */     
/*  808 */     return cr;
/*      */   }
/*      */ 
/*      */   
/*      */   public final ClusRun partitionDataBasic(RowData train) throws IOException, ClusException {
/*  813 */     ClusSummary summary = new ClusSummary();
/*  814 */     return partitionDataBasic((ClusData)train, null, null, summary, 1);
/*      */   }
/*      */ 
/*      */   
/*      */   public final ClusRun partitionDataBasic(ClusData data, ClusSelection sel, ClusSummary summary, int idx) throws IOException, ClusException {
/*  819 */     return partitionDataBasic(data, sel, null, summary, idx);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final ClusRun partitionDataBasic(ClusData data, ClusSelection sel, ClusData prunefile, ClusSummary summary, int idx) throws IOException, ClusException {
/*  826 */     ClusRun cr = new ClusRun(data.cloneData(), summary);
/*  827 */     if (sel != null) {
/*  828 */       if (sel.changesDistribution()) {
/*  829 */         ((RowData)cr.getTrainingSet()).update(sel);
/*      */       } else {
/*  831 */         ClusData val = cr.getTrainingSet().select(sel);
/*  832 */         cr.setTestSet((TupleIterator)((RowData)val).getIterator());
/*      */       } 
/*      */     }
/*  835 */     int pruning_max = this.m_Sett.getPruneSetMax();
/*  836 */     double vsb = this.m_Sett.getPruneProportion();
/*  837 */     if (vsb != 0.0D) {
/*  838 */       ClusData train = cr.getTrainingSet();
/*  839 */       int nbtot = train.getNbRows();
/*  840 */       int nbsel = (int)Math.round(vsb * nbtot);
/*  841 */       if (nbsel > pruning_max) {
/*  842 */         nbsel = pruning_max;
/*      */       }
/*  844 */       RandomSelection prunesel = new RandomSelection(nbtot, nbsel);
/*  845 */       cr.setPruneSet(train.select((ClusSelection)prunesel), (ClusSelection)prunesel);
/*  846 */       if (Settings.VERBOSE > 0) {
/*  847 */         System.out.println("Selecting pruning set: " + nbsel);
/*      */       }
/*      */     } 
/*  850 */     if (!this.m_Sett.isNullPruneFile()) {
/*  851 */       String prset = this.m_Sett.getPruneFile();
/*  852 */       if (prunefile != null) {
/*  853 */         cr.setPruneSet(prunefile, null);
/*      */       } else {
/*  855 */         RowData rowData = loadDataFile(prset);
/*  856 */         cr.setPruneSet((ClusData)rowData, null);
/*  857 */         if (Settings.VERBOSE > 0) {
/*  858 */           System.out.println("Selecting pruning set: " + prset);
/*      */         }
/*      */       } 
/*      */     } 
/*  862 */     cr.setIndex(idx);
/*  863 */     cr.copyTrainingData();
/*  864 */     return cr;
/*      */   }
/*      */ 
/*      */   
/*      */   public final void attachModels(ClusSchema schema, ClusRun cr) throws ClusException {
/*  869 */     for (int i = 0; i < cr.getNbModels(); i++) {
/*  870 */       ClusModel model = cr.getModel(i);
/*  871 */       if (model != null) {
/*  872 */         schema.attachModel(model);
/*      */       }
/*      */     } 
/*      */   }
/*      */   
/*      */   public static final double calcModelError(ClusStatManager mgr, RowData data, ClusModel model) throws ClusException, IOException {
/*  878 */     ClusSchema schema = data.getSchema();
/*      */     
/*  880 */     ClusErrorList error = new ClusErrorList();
/*  881 */     NumericAttrType[] num = schema.getNumericAttrUse(3);
/*  882 */     NominalAttrType[] nom = schema.getNominalAttrUse(3);
/*  883 */     TimeSeriesAttrType[] ts = schema.getTimeSeriesAttrUse(3);
/*  884 */     if (nom.length != 0) {
/*  885 */       error.addError((ClusError)new Accuracy(error, nom));
/*  886 */     } else if (num.length != 0) {
/*  887 */       error.addError((ClusError)new PearsonCorrelation(error, num));
/*  888 */     } else if (ts.length != 0) {
/*  889 */       error.addError((ClusError)new PearsonCorrelation(error, num));
/*      */     } 
/*      */     
/*  892 */     schema.attachModel(model);
/*      */     
/*  894 */     for (int i = 0; i < data.getNbRows(); i++) {
/*  895 */       DataTuple tuple = data.getTuple(i);
/*  896 */       ClusStatistic pred = model.predictWeighted(tuple);
/*  897 */       error.addExample(tuple, pred);
/*      */     } 
/*      */     
/*  900 */     double err = error.getFirstError().getModelError();
/*      */     
/*  902 */     return err;
/*      */   }
/*      */   
/*      */   public final void calcError(TupleIterator iter, int type, ClusRun cr) throws IOException, ClusException {
/*  906 */     calcError(iter, type, cr, null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final void calcError(TupleIterator iter, int type, ClusRun cr, ClusEnsemblePredictionWriter ens_pred) throws IOException, ClusException {
/*      */     // Byte code:
/*      */     //   0: aload_1
/*      */     //   1: invokevirtual init : ()V
/*      */     //   4: aload_1
/*      */     //   5: invokevirtual getSchema : ()Lclus/data/type/ClusSchema;
/*      */     //   8: astore #5
/*      */     //   10: aload_1
/*      */     //   11: invokevirtual shouldAttach : ()Z
/*      */     //   14: ifeq -> 24
/*      */     //   17: aload_0
/*      */     //   18: aload #5
/*      */     //   20: aload_3
/*      */     //   21: invokevirtual attachModels : (Lclus/data/type/ClusSchema;Lclus/main/ClusRun;)V
/*      */     //   24: aload_3
/*      */     //   25: iload_2
/*      */     //   26: aload #5
/*      */     //   28: invokevirtual initModelProcessors : (ILclus/data/type/ClusSchema;)V
/*      */     //   31: aload_0
/*      */     //   32: invokevirtual getSettings : ()Lclus/main/Settings;
/*      */     //   35: pop
/*      */     //   36: getstatic clus/main/Settings.IS_XVAL : Z
/*      */     //   39: ifeq -> 60
/*      */     //   42: aload_0
/*      */     //   43: invokevirtual getSettings : ()Lclus/main/Settings;
/*      */     //   46: pop
/*      */     //   47: getstatic clus/main/Settings.IS_XVAL : Z
/*      */     //   50: ifeq -> 64
/*      */     //   53: aload_3
/*      */     //   54: invokevirtual getTestSet : ()Lclus/data/rows/RowData;
/*      */     //   57: ifnonnull -> 64
/*      */     //   60: iconst_1
/*      */     //   61: goto -> 65
/*      */     //   64: iconst_0
/*      */     //   65: istore #6
/*      */     //   67: iload #6
/*      */     //   69: ifeq -> 90
/*      */     //   72: iload_2
/*      */     //   73: ifne -> 90
/*      */     //   76: aload_0
/*      */     //   77: invokevirtual getSettings : ()Lclus/main/Settings;
/*      */     //   80: invokevirtual shouldWritePredictionsFromEnsemble : ()Z
/*      */     //   83: ifeq -> 90
/*      */     //   86: iconst_1
/*      */     //   87: goto -> 91
/*      */     //   90: iconst_0
/*      */     //   91: istore #6
/*      */     //   93: aload_0
/*      */     //   94: invokevirtual getSettings : ()Lclus/main/Settings;
/*      */     //   97: pop
/*      */     //   98: getstatic clus/main/Settings.IS_XVAL : Z
/*      */     //   101: ifne -> 115
/*      */     //   104: aload_3
/*      */     //   105: invokevirtual getTestSet : ()Lclus/data/rows/RowData;
/*      */     //   108: ifnull -> 115
/*      */     //   111: iconst_1
/*      */     //   112: goto -> 116
/*      */     //   115: iconst_0
/*      */     //   116: istore #7
/*      */     //   118: iload #7
/*      */     //   120: ifeq -> 142
/*      */     //   123: iload_2
/*      */     //   124: iconst_1
/*      */     //   125: if_icmpne -> 142
/*      */     //   128: aload_0
/*      */     //   129: invokevirtual getSettings : ()Lclus/main/Settings;
/*      */     //   132: invokevirtual shouldWritePredictionsFromEnsemble : ()Z
/*      */     //   135: ifeq -> 142
/*      */     //   138: iconst_1
/*      */     //   139: goto -> 143
/*      */     //   142: iconst_0
/*      */     //   143: istore #7
/*      */     //   145: iload #6
/*      */     //   147: ifne -> 155
/*      */     //   150: iload #7
/*      */     //   152: ifeq -> 160
/*      */     //   155: aload_3
/*      */     //   156: iload_2
/*      */     //   157: invokevirtual initEnsemblePredictionsWriter : (I)V
/*      */     //   160: aload_3
/*      */     //   161: invokevirtual getAllModelsMI : ()Lclus/model/ClusModelInfo;
/*      */     //   164: iload_2
/*      */     //   165: invokevirtual getAddModelProcessors : (I)Lclus/model/processor/ModelProcessorCollection;
/*      */     //   168: astore #8
/*      */     //   170: aload_1
/*      */     //   171: invokevirtual readTuple : ()Lclus/data/rows/DataTuple;
/*      */     //   174: astore #9
/*      */     //   176: aload #9
/*      */     //   178: ifnull -> 381
/*      */     //   181: aload #8
/*      */     //   183: aload #9
/*      */     //   185: invokevirtual exampleUpdate : (Lclus/data/rows/DataTuple;)V
/*      */     //   188: iconst_0
/*      */     //   189: istore #10
/*      */     //   191: iload #10
/*      */     //   193: aload_3
/*      */     //   194: invokevirtual getNbModels : ()I
/*      */     //   197: if_icmpge -> 367
/*      */     //   200: aload_3
/*      */     //   201: iload #10
/*      */     //   203: invokevirtual getModelInfo : (I)Lclus/model/ClusModelInfo;
/*      */     //   206: astore #11
/*      */     //   208: aload #11
/*      */     //   210: ifnull -> 361
/*      */     //   213: aload #11
/*      */     //   215: invokevirtual getModel : ()Lclus/model/ClusModel;
/*      */     //   218: ifnull -> 361
/*      */     //   221: aload #11
/*      */     //   223: invokevirtual getModel : ()Lclus/model/ClusModel;
/*      */     //   226: astore #12
/*      */     //   228: aload #12
/*      */     //   230: aload #9
/*      */     //   232: invokeinterface predictWeighted : (Lclus/data/rows/DataTuple;)Lclus/statistic/ClusStatistic;
/*      */     //   237: astore #13
/*      */     //   239: aload #11
/*      */     //   241: iload_2
/*      */     //   242: invokevirtual getError : (I)Lclus/error/ClusErrorList;
/*      */     //   245: astore #14
/*      */     //   247: aload #14
/*      */     //   249: ifnull -> 261
/*      */     //   252: aload #14
/*      */     //   254: aload #9
/*      */     //   256: aload #13
/*      */     //   258: invokevirtual addExample : (Lclus/data/rows/DataTuple;Lclus/statistic/ClusStatistic;)V
/*      */     //   261: aload #11
/*      */     //   263: iload_2
/*      */     //   264: invokevirtual getModelProcessors : (I)Lclus/model/processor/ModelProcessorCollection;
/*      */     //   267: astore #15
/*      */     //   269: aload #15
/*      */     //   271: ifnull -> 307
/*      */     //   274: aload #15
/*      */     //   276: invokevirtual needsModelUpdate : ()Z
/*      */     //   279: ifeq -> 298
/*      */     //   282: aload #12
/*      */     //   284: aload #9
/*      */     //   286: aload #15
/*      */     //   288: invokeinterface applyModelProcessors : (Lclus/data/rows/DataTuple;Ljeans/util/MyArray;)V
/*      */     //   293: aload #15
/*      */     //   295: invokevirtual modelDone : ()V
/*      */     //   298: aload #15
/*      */     //   300: aload #9
/*      */     //   302: aload #13
/*      */     //   304: invokevirtual exampleUpdate : (Lclus/data/rows/DataTuple;Lclus/statistic/ClusStatistic;)V
/*      */     //   307: iload #6
/*      */     //   309: ifne -> 317
/*      */     //   312: iload #7
/*      */     //   314: ifeq -> 336
/*      */     //   317: iload #10
/*      */     //   319: iconst_1
/*      */     //   320: if_icmpne -> 336
/*      */     //   323: aload #11
/*      */     //   325: iload_2
/*      */     //   326: invokevirtual getEnsemblePredictionWriter : (I)Lclus/model/processor/ClusEnsemblePredictionWriter;
/*      */     //   329: aload #9
/*      */     //   331: aload #13
/*      */     //   333: invokevirtual writePredictionsForTuple : (Lclus/data/rows/DataTuple;Lclus/statistic/ClusStatistic;)V
/*      */     //   336: aload #4
/*      */     //   338: ifnull -> 361
/*      */     //   341: iload #10
/*      */     //   343: iconst_1
/*      */     //   344: if_icmpne -> 361
/*      */     //   347: iload_2
/*      */     //   348: iconst_1
/*      */     //   349: if_icmpne -> 361
/*      */     //   352: aload #4
/*      */     //   354: aload #9
/*      */     //   356: aload #13
/*      */     //   358: invokevirtual writePredictionsForTuple : (Lclus/data/rows/DataTuple;Lclus/statistic/ClusStatistic;)V
/*      */     //   361: iinc #10, 1
/*      */     //   364: goto -> 191
/*      */     //   367: aload #8
/*      */     //   369: invokevirtual exampleDone : ()V
/*      */     //   372: aload_1
/*      */     //   373: invokevirtual readTuple : ()Lclus/data/rows/DataTuple;
/*      */     //   376: astore #9
/*      */     //   378: goto -> 176
/*      */     //   381: aload_1
/*      */     //   382: invokevirtual close : ()V
/*      */     //   385: aload_3
/*      */     //   386: iload_2
/*      */     //   387: invokevirtual termModelProcessors : (I)V
/*      */     //   390: iload #6
/*      */     //   392: ifne -> 400
/*      */     //   395: iload #7
/*      */     //   397: ifeq -> 405
/*      */     //   400: aload_3
/*      */     //   401: iload_2
/*      */     //   402: invokevirtual termEnsemblePredictionsWriter : (I)V
/*      */     //   405: return
/*      */     // Line number table:
/*      */     //   Java source line number -> byte code offset
/*      */     //   #913	-> 0
/*      */     //   #914	-> 4
/*      */     //   #915	-> 10
/*      */     //   #916	-> 17
/*      */     //   #919	-> 24
/*      */     //   #921	-> 31
/*      */     //   #922	-> 67
/*      */     //   #923	-> 93
/*      */     //   #924	-> 118
/*      */     //   #926	-> 145
/*      */     //   #927	-> 155
/*      */     //   #929	-> 160
/*      */     //   #931	-> 170
/*      */     //   #932	-> 176
/*      */     //   #933	-> 181
/*      */     //   #934	-> 188
/*      */     //   #935	-> 200
/*      */     //   #936	-> 208
/*      */     //   #937	-> 221
/*      */     //   #938	-> 228
/*      */     //   #939	-> 239
/*      */     //   #940	-> 247
/*      */     //   #941	-> 252
/*      */     //   #943	-> 261
/*      */     //   #944	-> 269
/*      */     //   #945	-> 274
/*      */     //   #946	-> 282
/*      */     //   #947	-> 293
/*      */     //   #949	-> 298
/*      */     //   #952	-> 307
/*      */     //   #953	-> 323
/*      */     //   #955	-> 336
/*      */     //   #956	-> 352
/*      */     //   #934	-> 361
/*      */     //   #960	-> 367
/*      */     //   #961	-> 372
/*      */     //   #963	-> 381
/*      */     //   #964	-> 385
/*      */     //   #965	-> 390
/*      */     //   #966	-> 400
/*      */     //   #968	-> 405
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	descriptor
/*      */     //   228	133	12	model	Lclus/model/ClusModel;
/*      */     //   239	122	13	pred	Lclus/statistic/ClusStatistic;
/*      */     //   247	114	14	err	Lclus/error/ClusErrorList;
/*      */     //   269	92	15	coll	Lclus/model/processor/ModelProcessorCollection;
/*      */     //   208	153	11	mi	Lclus/model/ClusModelInfo;
/*      */     //   191	176	10	i	I
/*      */     //   0	406	0	this	Lclus/Clus;
/*      */     //   0	406	1	iter	Lclus/data/rows/TupleIterator;
/*      */     //   0	406	2	type	I
/*      */     //   0	406	3	cr	Lclus/main/ClusRun;
/*      */     //   0	406	4	ens_pred	Lclus/model/processor/ClusEnsemblePredictionWriter;
/*      */     //   10	396	5	mschema	Lclus/data/type/ClusSchema;
/*      */     //   67	339	6	wr_ens_tr_preds	Z
/*      */     //   118	288	7	wr_ens_te_preds	Z
/*      */     //   170	236	8	allcoll	Lclus/model/processor/ModelProcessorCollection;
/*      */     //   176	230	9	tuple	Lclus/data/rows/DataTuple;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final void calcErrorWithoutPrinting(TupleIterator iter, int type, ClusRun cr, ClusEnsemblePredictionWriter ens_pred) throws IOException, ClusException {
/*      */     // Byte code:
/*      */     //   0: aload_1
/*      */     //   1: invokevirtual init : ()V
/*      */     //   4: aload_1
/*      */     //   5: invokevirtual getSchema : ()Lclus/data/type/ClusSchema;
/*      */     //   8: astore #5
/*      */     //   10: aload_1
/*      */     //   11: invokevirtual shouldAttach : ()Z
/*      */     //   14: ifeq -> 24
/*      */     //   17: aload_0
/*      */     //   18: aload #5
/*      */     //   20: aload_3
/*      */     //   21: invokevirtual attachModels : (Lclus/data/type/ClusSchema;Lclus/main/ClusRun;)V
/*      */     //   24: aload_0
/*      */     //   25: invokevirtual getSettings : ()Lclus/main/Settings;
/*      */     //   28: pop
/*      */     //   29: getstatic clus/main/Settings.IS_XVAL : Z
/*      */     //   32: ifeq -> 53
/*      */     //   35: aload_0
/*      */     //   36: invokevirtual getSettings : ()Lclus/main/Settings;
/*      */     //   39: pop
/*      */     //   40: getstatic clus/main/Settings.IS_XVAL : Z
/*      */     //   43: ifeq -> 57
/*      */     //   46: aload_3
/*      */     //   47: invokevirtual getTestSet : ()Lclus/data/rows/RowData;
/*      */     //   50: ifnonnull -> 57
/*      */     //   53: iconst_1
/*      */     //   54: goto -> 58
/*      */     //   57: iconst_0
/*      */     //   58: istore #6
/*      */     //   60: iload #6
/*      */     //   62: ifeq -> 83
/*      */     //   65: iload_2
/*      */     //   66: ifne -> 83
/*      */     //   69: aload_0
/*      */     //   70: invokevirtual getSettings : ()Lclus/main/Settings;
/*      */     //   73: invokevirtual shouldWritePredictionsFromEnsemble : ()Z
/*      */     //   76: ifeq -> 83
/*      */     //   79: iconst_1
/*      */     //   80: goto -> 84
/*      */     //   83: iconst_0
/*      */     //   84: istore #6
/*      */     //   86: aload_0
/*      */     //   87: invokevirtual getSettings : ()Lclus/main/Settings;
/*      */     //   90: pop
/*      */     //   91: getstatic clus/main/Settings.IS_XVAL : Z
/*      */     //   94: ifne -> 108
/*      */     //   97: aload_3
/*      */     //   98: invokevirtual getTestSet : ()Lclus/data/rows/RowData;
/*      */     //   101: ifnull -> 108
/*      */     //   104: iconst_1
/*      */     //   105: goto -> 109
/*      */     //   108: iconst_0
/*      */     //   109: istore #7
/*      */     //   111: iload #7
/*      */     //   113: ifeq -> 135
/*      */     //   116: iload_2
/*      */     //   117: iconst_1
/*      */     //   118: if_icmpne -> 135
/*      */     //   121: aload_0
/*      */     //   122: invokevirtual getSettings : ()Lclus/main/Settings;
/*      */     //   125: invokevirtual shouldWritePredictionsFromEnsemble : ()Z
/*      */     //   128: ifeq -> 135
/*      */     //   131: iconst_1
/*      */     //   132: goto -> 136
/*      */     //   135: iconst_0
/*      */     //   136: istore #7
/*      */     //   138: aload_1
/*      */     //   139: invokevirtual readTuple : ()Lclus/data/rows/DataTuple;
/*      */     //   142: astore #8
/*      */     //   144: aload #8
/*      */     //   146: ifnull -> 274
/*      */     //   149: iconst_0
/*      */     //   150: istore #9
/*      */     //   152: iload #9
/*      */     //   154: aload_3
/*      */     //   155: invokevirtual getNbModels : ()I
/*      */     //   158: if_icmpge -> 265
/*      */     //   161: aload_3
/*      */     //   162: iload #9
/*      */     //   164: invokevirtual getModelInfo : (I)Lclus/model/ClusModelInfo;
/*      */     //   167: astore #10
/*      */     //   169: aload #10
/*      */     //   171: ifnull -> 259
/*      */     //   174: aload #10
/*      */     //   176: invokevirtual getModel : ()Lclus/model/ClusModel;
/*      */     //   179: ifnull -> 259
/*      */     //   182: aload #10
/*      */     //   184: invokevirtual getModel : ()Lclus/model/ClusModel;
/*      */     //   187: astore #11
/*      */     //   189: aload #11
/*      */     //   191: aload #8
/*      */     //   193: invokeinterface predictWeighted : (Lclus/data/rows/DataTuple;)Lclus/statistic/ClusStatistic;
/*      */     //   198: astore #12
/*      */     //   200: aload #10
/*      */     //   202: iload_2
/*      */     //   203: invokevirtual getError : (I)Lclus/error/ClusErrorList;
/*      */     //   206: astore #13
/*      */     //   208: aload #13
/*      */     //   210: ifnull -> 222
/*      */     //   213: aload #13
/*      */     //   215: aload #8
/*      */     //   217: aload #12
/*      */     //   219: invokevirtual addExample : (Lclus/data/rows/DataTuple;Lclus/statistic/ClusStatistic;)V
/*      */     //   222: aload #10
/*      */     //   224: iload_2
/*      */     //   225: invokevirtual getModelProcessors : (I)Lclus/model/processor/ModelProcessorCollection;
/*      */     //   228: astore #14
/*      */     //   230: aload #14
/*      */     //   232: ifnull -> 259
/*      */     //   235: aload #14
/*      */     //   237: invokevirtual needsModelUpdate : ()Z
/*      */     //   240: ifeq -> 259
/*      */     //   243: aload #11
/*      */     //   245: aload #8
/*      */     //   247: aload #14
/*      */     //   249: invokeinterface applyModelProcessors : (Lclus/data/rows/DataTuple;Ljeans/util/MyArray;)V
/*      */     //   254: aload #14
/*      */     //   256: invokevirtual modelDone : ()V
/*      */     //   259: iinc #9, 1
/*      */     //   262: goto -> 152
/*      */     //   265: aload_1
/*      */     //   266: invokevirtual readTuple : ()Lclus/data/rows/DataTuple;
/*      */     //   269: astore #8
/*      */     //   271: goto -> 144
/*      */     //   274: aload_1
/*      */     //   275: invokevirtual close : ()V
/*      */     //   278: return
/*      */     // Line number table:
/*      */     //   Java source line number -> byte code offset
/*      */     //   #971	-> 0
/*      */     //   #972	-> 4
/*      */     //   #973	-> 10
/*      */     //   #974	-> 17
/*      */     //   #976	-> 24
/*      */     //   #977	-> 60
/*      */     //   #978	-> 86
/*      */     //   #979	-> 111
/*      */     //   #983	-> 138
/*      */     //   #984	-> 144
/*      */     //   #986	-> 149
/*      */     //   #987	-> 161
/*      */     //   #988	-> 169
/*      */     //   #989	-> 182
/*      */     //   #990	-> 189
/*      */     //   #991	-> 200
/*      */     //   #993	-> 208
/*      */     //   #994	-> 213
/*      */     //   #996	-> 222
/*      */     //   #997	-> 230
/*      */     //   #998	-> 235
/*      */     //   #999	-> 243
/*      */     //   #1000	-> 254
/*      */     //   #986	-> 259
/*      */     //   #1005	-> 265
/*      */     //   #1007	-> 274
/*      */     //   #1008	-> 278
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	descriptor
/*      */     //   189	70	11	model	Lclus/model/ClusModel;
/*      */     //   200	59	12	pred	Lclus/statistic/ClusStatistic;
/*      */     //   208	51	13	err	Lclus/error/ClusErrorList;
/*      */     //   230	29	14	coll	Lclus/model/processor/ModelProcessorCollection;
/*      */     //   169	90	10	mi	Lclus/model/ClusModelInfo;
/*      */     //   152	113	9	i	I
/*      */     //   0	279	0	this	Lclus/Clus;
/*      */     //   0	279	1	iter	Lclus/data/rows/TupleIterator;
/*      */     //   0	279	2	type	I
/*      */     //   0	279	3	cr	Lclus/main/ClusRun;
/*      */     //   0	279	4	ens_pred	Lclus/model/processor/ClusEnsemblePredictionWriter;
/*      */     //   10	269	5	mschema	Lclus/data/type/ClusSchema;
/*      */     //   60	219	6	wr_ens_tr_preds	Z
/*      */     //   111	168	7	wr_ens_te_preds	Z
/*      */     //   144	135	8	tuple	Lclus/data/rows/DataTuple;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void addModelErrorMeasures(ClusRun cr) {
/* 1011 */     for (int i = 0; i < cr.getNbModels(); i++) {
/* 1012 */       ClusModelInfo info = cr.getModelInfo(i);
/*      */       
/* 1014 */       if (info != null && info.getModel() instanceof ClusRuleSet && this.m_Sett
/* 1015 */         .isRuleWiseErrors()) {
/* 1016 */         ClusRuleSet ruleset = (ClusRuleSet)info.getModel();
/* 1017 */         ruleset.setError(info.getTrainingError(), 0);
/* 1018 */         ruleset.setError(info.getTestError(), 1);
/* 1019 */         info.addModelProcessor(0, (ClusModelProcessor)new ClusCalcRuleErrorProc(0, info.getTrainingError()));
/* 1020 */         info.addModelProcessor(1, (ClusModelProcessor)new ClusCalcRuleErrorProc(1, info.getTestError()));
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public final void calcExtraTrainingSetErrors(ClusRun cr) throws ClusException {
/* 1027 */     ClusErrorList parent = getStatManager().createExtraError(0);
/*      */     
/* 1029 */     for (int i = 0; i < cr.getNbModels(); i++) {
/* 1030 */       ClusModelInfo info = cr.getModelInfo(i);
/* 1031 */       if (info != null) {
/* 1032 */         ClusErrorList parent_cl = parent.getErrorClone();
/* 1033 */         parent_cl.compute((RowData)cr.getTrainingSet(), info);
/* 1034 */         info.setExtraError(0, parent_cl);
/* 1035 */         if (info.getModel() instanceof ClusRuleSet && this.m_Sett
/* 1036 */           .isRuleWiseErrors()) {
/* 1037 */           ClusRuleSet ruleset = (ClusRuleSet)info.getModel();
/* 1038 */           for (int j = 0; j < ruleset.getModelSize(); j++) {
/* 1039 */             ClusErrorList rule_error = parent.getErrorClone();
/* 1040 */             rule_error.compute((RowData)cr.getTrainingSet(), (ClusModel)ruleset
/* 1041 */                 .getRule(j));
/* 1042 */             ruleset.getRule(j).addError(rule_error, 0);
/*      */           } 
/*      */         } 
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public final void calcError(ClusRun cr, ClusSummary summary) throws IOException, ClusException {
/* 1051 */     calcError(cr, summary);
/*      */   }
/*      */   
/*      */   public final void calcError(ClusRun cr, ClusSummary summary, ClusEnsemblePredictionWriter ens_pred) throws IOException, ClusException {
/* 1055 */     cr.copyAllModelsMIs();
/*      */     
/* 1057 */     for (int i = 0; i < cr.getNbModels(); i++) {
/* 1058 */       if (cr.getModelInfo(i) != null && !this.m_Sett.shouldShowModel(i)) {
/*      */         
/* 1060 */         ClusModelInfo inf = cr.getModelInfo(i);
/* 1061 */         if (inf.getTrainingError() != null) {
/* 1062 */           inf.getTrainingError().clear();
/*      */         }
/* 1064 */         if (inf.getTestError() != null) {
/* 1065 */           inf.getTestError().clear();
/*      */         }
/* 1067 */         if (inf.getValidationError() != null) {
/* 1068 */           inf.getValidationError().clear();
/*      */         }
/*      */       } 
/*      */     } 
/* 1072 */     if (this.m_Sett.isOutTrainError()) {
/* 1073 */       if (Settings.VERBOSE > 0) {
/* 1074 */         System.out.println("Computing training error");
/*      */       }
/* 1076 */       calcError(cr.getTrainIter(), 0, cr, ens_pred);
/*      */     } 
/* 1078 */     TupleIterator tsiter = cr.getTestIter();
/* 1079 */     if (this.m_Sett.isOutTestError() && tsiter != null) {
/* 1080 */       if (Settings.VERBOSE > 0) {
/* 1081 */         System.out.println("Computing testing error");
/*      */       }
/* 1083 */       calcError(tsiter, 1, cr, ens_pred);
/*      */     } 
/* 1085 */     if (this.m_Sett.isOutValidError() && cr.getPruneSet() != null) {
/* 1086 */       if (Settings.VERBOSE > 0) {
/* 1087 */         System.out.println("Computing validation error");
/*      */       }
/* 1089 */       calcError(cr.getPruneIter(), 2, cr, ens_pred);
/*      */     } 
/* 1091 */     if (summary != null) {
/* 1092 */       summary.addSummary(cr);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void out2model(String fname) throws IOException, ClusException {
/* 1097 */     String model_name = FileUtil.getName(fname) + ".model";
/* 1098 */     ClusTreeReader rdr = new ClusTreeReader();
/* 1099 */     ClusNode node = rdr.loadOutTree(fname, getM_Schema(), "Original Model");
/* 1100 */     if (node == null)
/*      */     {
/* 1102 */       node = rdr.loadOutTree(fname, getM_Schema(), "Pruned Model");
/*      */     }
/* 1104 */     if (node == null) {
/* 1105 */       throw new ClusException("Unable to find original tree in .out file");
/*      */     }
/* 1107 */     ClusRun cr = partitionData();
/* 1108 */     ConstraintDFInduce induce = new ConstraintDFInduce(this.m_Induce);
/* 1109 */     ClusNode orig = induce.fillInInduce(cr, node, getScore());
/* 1110 */     orig.numberTree();
/* 1111 */     PruneTree pruner = induce.getStatManager().getTreePruner(cr
/* 1112 */         .getPruneSet());
/* 1113 */     pruner.setTrainingData((RowData)cr.getTrainingSet());
/* 1114 */     ClusNode pruned = (ClusNode)orig.cloneTree();
/* 1115 */     pruner.prune(pruned);
/* 1116 */     pruned.numberTree();
/* 1117 */     System.out.println();
/* 1118 */     System.out.println("Tree read from .out:");
/* 1119 */     orig.printTree();
/* 1120 */     System.out.println();
/* 1121 */     if (rdr.getLineAfterTree() != null) {
/* 1122 */       System.out.println("First line after tree: '" + rdr
/* 1123 */           .getLineAfterTree() + "'");
/* 1124 */       System.out.println();
/*      */     } 
/* 1126 */     ClusModelCollectionIO io = new ClusModelCollectionIO();
/* 1127 */     ClusModelInfo pruned_info = new ClusModelInfo("Pruned");
/* 1128 */     pruned_info.setModel((ClusModel)pruned);
/* 1129 */     io.addModel(pruned_info);
/* 1130 */     ClusModelInfo orig_info = new ClusModelInfo("Original");
/* 1131 */     orig_info.setModel((ClusModel)orig);
/* 1132 */     io.addModel(orig_info);
/* 1133 */     io.save(model_name);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static double[][] calcStdDevsForTheSet(RowData data, NumericAttrType[] numTypes) {
/* 1147 */     int[] nbOfValidValues = new int[numTypes.length];
/*      */ 
/*      */     
/* 1150 */     double[] means = new double[numTypes.length];
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1157 */     boolean[] varIsNonZero = new boolean[numTypes.length];
/*      */ 
/*      */     
/* 1160 */     double[] prevAcceptedValue = new double[numTypes.length];
/*      */     
/* 1162 */     for (int i = 0; i < prevAcceptedValue.length; i++) {
/* 1163 */       prevAcceptedValue[i] = Double.NaN;
/*      */     }
/*      */ 
/*      */     
/* 1167 */     for (int iRow = 0; iRow < data.getNbRows(); iRow++) {
/* 1168 */       DataTuple tuple = data.getTuple(iRow);
/*      */       
/* 1170 */       for (int m = 0; m < numTypes.length; m++) {
/* 1171 */         double value = numTypes[m].getNumeric(tuple);
/* 1172 */         if (!Double.isNaN(value) && !Double.isInfinite(value)) {
/*      */ 
/*      */           
/* 1175 */           if (!Double.isNaN(prevAcceptedValue[m]) && prevAcceptedValue[m] != value)
/*      */           {
/* 1177 */             varIsNonZero[m] = true;
/*      */           }
/*      */           
/* 1180 */           prevAcceptedValue[m] = value;
/* 1181 */           means[m] = means[m] + value;
/* 1182 */           nbOfValidValues[m] = nbOfValidValues[m] + 1;
/*      */         } 
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/* 1188 */     for (int jNumAttrib = 0; jNumAttrib < numTypes.length; jNumAttrib++) {
/* 1189 */       if (nbOfValidValues[jNumAttrib] == 0) {
/* 1190 */         nbOfValidValues[jNumAttrib] = 1;
/*      */       }
/* 1192 */       if (!varIsNonZero[jNumAttrib]) {
/*      */         
/* 1194 */         means[jNumAttrib] = prevAcceptedValue[jNumAttrib];
/*      */       } else {
/* 1196 */         means[jNumAttrib] = means[jNumAttrib] / nbOfValidValues[jNumAttrib];
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1203 */     double[] variance = new double[numTypes.length];
/*      */ 
/*      */     
/* 1206 */     for (int j = 0; j < data.getNbRows(); j++) {
/* 1207 */       DataTuple tuple = data.getTuple(j);
/*      */       
/* 1209 */       for (int m = 0; m < numTypes.length; m++) {
/* 1210 */         double value = numTypes[m].getNumeric(tuple);
/* 1211 */         if (!Double.isNaN(value) && !Double.isInfinite(value))
/*      */         {
/* 1213 */           variance[m] = variance[m] + Math.pow(value - means[m], 2.0D);
/*      */         }
/*      */       } 
/*      */     } 
/*      */     
/* 1218 */     double[] stdDevs = new double[numTypes.length];
/*      */ 
/*      */     
/* 1221 */     for (int k = 0; k < numTypes.length; k++) {
/*      */       
/* 1223 */       if (!varIsNonZero[k]) {
/*      */ 
/*      */         
/* 1226 */         variance[k] = 0.25D;
/*      */         
/* 1228 */         System.out.println("Warning: Variance of attribute " + k + " is zero.");
/*      */       } else {
/* 1230 */         variance[k] = variance[k] / nbOfValidValues[k];
/*      */       } 
/*      */       
/* 1233 */       stdDevs[k] = Math.sqrt(variance[k]);
/*      */     } 
/*      */     
/* 1236 */     double[][] meanAndStdDev = new double[2][];
/* 1237 */     meanAndStdDev[0] = means;
/* 1238 */     meanAndStdDev[1] = stdDevs;
/* 1239 */     return meanAndStdDev;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final RowData returnNormalizedData(RowData data) {
/* 1249 */     NumericAttrType[] numTypes = data.getSchema().getNumericAttrUse(0);
/* 1250 */     double[][] meanAndStdDev = calcStdDevsForTheSet(data, numTypes);
/*      */ 
/*      */     
/* 1253 */     double[] stdDevs = meanAndStdDev[1];
/* 1254 */     double[] mean = meanAndStdDev[0];
/*      */     
/* 1256 */     ArrayList<DataTuple> normalized = new ArrayList<>();
/*      */     
/* 1258 */     for (int jNumAttrib = 0; jNumAttrib < numTypes.length; jNumAttrib++) {
/* 1259 */       numTypes[jNumAttrib].setSparse(false);
/*      */     }
/*      */     
/* 1262 */     for (int iRow = 0; iRow < data.getNbRows(); iRow++) {
/* 1263 */       DataTuple tuple = data.getTuple(iRow).deepCloneTuple();
/*      */       
/* 1265 */       for (int i = 0; i < tuple.m_Doubles.length; i++) {
/* 1266 */         NumericAttrType type = numTypes[i];
/*      */         
/* 1268 */         double value = type.getNumeric(tuple);
/* 1269 */         if (!Double.isNaN(value) && !Double.isInfinite(value)) {
/* 1270 */           value -= mean[i];
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 1275 */           value /= 2.0D * stdDevs[i];
/*      */         } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1283 */         type.setNumeric(tuple, value);
/*      */       } 
/*      */ 
/*      */ 
/*      */       
/* 1288 */       normalized.add(tuple);
/*      */     } 
/*      */     
/* 1291 */     RowData normalized_data = new RowData(normalized, data.getSchema());
/* 1292 */     System.out.println("Normalized number of examples: " + normalized_data
/* 1293 */         .getNbRows());
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1352 */     return normalized_data;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final void normalizeDataAndWriteToFile() throws IOException, ClusException {
/* 1362 */     RowData data = this.m_Data;
/* 1363 */     CombStat cmb = (CombStat)getStatManager().getTrainSetStat(0);
/*      */     
/* 1365 */     RegressionStat rstat = cmb.getRegressionStat();
/* 1366 */     NumericAttrType[] numtypes = getSchema().getNumericAttrUse(0);
/*      */     
/* 1368 */     int tcnt = 0;
/* 1369 */     for (int j = 0; j < numtypes.length; j++) {
/* 1370 */       NumericAttrType type = numtypes[j];
/* 1371 */       if (type.isTarget()) {
/* 1372 */         tcnt++;
/* 1373 */         NumberFormat format = ClusFormat.THREE_AFTER_DOT;
/* 1374 */         System.out.print(StringUtils.printStr("T" + tcnt + " ", 5));
/* 1375 */         System.out
/* 1376 */           .print(StringUtils.printStr(type.getName() + " ", 30));
/* 1377 */         System.out.print(StringUtils.printStr(format.format(rstat
/* 1378 */                 .getMean(j)), 10));
/* 1379 */         System.out.print(StringUtils.printStr(format.format(
/* 1380 */                 Math.sqrt(rstat.getVariance(j))), 10));
/* 1381 */         System.out.println();
/*      */       } 
/*      */     } 
/* 1384 */     ArrayList<DataTuple> normalized = new ArrayList();
/* 1385 */     for (int i = 0; i < data.getNbRows(); i++) {
/* 1386 */       DataTuple tuple = data.getTuple(i).deepCloneTuple();
/* 1387 */       for (int k = 0; k < numtypes.length; k++) {
/* 1388 */         NumericAttrType type = numtypes[k];
/* 1389 */         if (type.isTarget()) {
/* 1390 */           double value = type.getNumeric(tuple);
/* 1391 */           value -= rstat.getMean(k);
/* 1392 */           value /= Math.sqrt(rstat.getVariance(k));
/* 1393 */           type.setNumeric(tuple, value);
/*      */         } 
/*      */       } 
/* 1396 */       normalized.add(tuple);
/*      */     } 
/* 1398 */     RowData normalized_data = new RowData(normalized, getSchema());
/* 1399 */     System.out.println("Size: " + normalized_data.getNbRows());
/*      */     
/* 1401 */     String fname = this.m_Sett.getFileAbsolute(FileUtil.getName(this.m_Sett
/* 1402 */           .getDataFile()) + "_norm.txt");
/*      */     
/* 1404 */     ARFFFile.writeArff(fname, normalized_data);
/*      */   }
/*      */ 
/*      */   
/*      */   public final void testModel(String fname) throws IOException, ClusException, ClassNotFoundException {
/* 1409 */     ClusModelCollectionIO io = ClusModelCollectionIO.load(fname);
/* 1410 */     ClusNode res = (ClusNode)io.getModel("Original");
/* 1411 */     String test_name = this.m_Sett.getAppName() + ".test";
/* 1412 */     ClusOutput out = new ClusOutput(test_name, getM_Schema(), this.m_Sett);
/* 1413 */     ClusRun cr = partitionData();
/* 1414 */     getStatManager().updateStatistics((ClusModel)res);
/* 1415 */     getSchema().attachModel((ClusModel)res);
/* 1416 */     getClassifier().pruneAll(cr);
/* 1417 */     getClassifier().postProcess(cr);
/* 1418 */     calcError(cr, (ClusSummary)null, (ClusEnsemblePredictionWriter)null);
/* 1419 */     out.writeHeader();
/* 1420 */     out.writeOutput(cr, true, this.m_Sett.isOutTrainError());
/* 1421 */     out.close();
/*      */   }
/*      */ 
/*      */   
/*      */   public final void showModel(String fname) throws IOException, ClusException, ClassNotFoundException {
/* 1426 */     ClusModelCollectionIO io = ClusModelCollectionIO.load(fname);
/* 1427 */     ClusNode res = (ClusNode)io.getModel("Pruned");
/* 1428 */     System.out.println("Tree read from .model:");
/* 1429 */     System.out.println();
/* 1430 */     res.inverseTests();
/* 1431 */     res.printTree();
/*      */   }
/*      */ 
/*      */   
/*      */   public final void saveModels(ClusRun models, ClusModelCollectionIO io) throws IOException {
/* 1436 */     if (getInduce().isModelWriter()) {
/* 1437 */       getInduce().writeModel(io);
/*      */     }
/* 1439 */     int pos = 0;
/* 1440 */     for (int i = models.getNbModels() - 1; i >= 0; i--) {
/* 1441 */       ClusModelInfo info = models.getModelInfo(i);
/* 1442 */       if (info != null && info.shouldSave()) {
/* 1443 */         io.insertModel(pos++, info);
/*      */       }
/*      */     } 
/*      */   }
/*      */   
/*      */   public ClusRun train(RowData train) throws ClusException, IOException {
/* 1449 */     this.m_Induce = getClassifier().createInduce(train.getSchema(), this.m_Sett, this.m_CmdLine);
/* 1450 */     ClusRun cr = partitionDataBasic(train);
/* 1451 */     this.m_Induce.initialize();
/* 1452 */     initializeAttributeWeights((ClusData)this.m_Data);
/* 1453 */     this.m_Induce.initializeHeuristic();
/* 1454 */     getStatManager().computeTrainSetStat((RowData)cr.getTrainingSet());
/* 1455 */     induce(cr, getClassifier());
/* 1456 */     return cr;
/*      */   }
/*      */   
/*      */   public final void restartPrinterWriters(ClusRun cr, int activeLearningIteration, String activeLearningAlgorithm, String labelInferingAlgorithm) { String ts_name, tr_name;
/* 1460 */     cr.resetAllModelsMI();
/* 1461 */     ClusModelInfo allmi = cr.getAllModelsMI();
/*      */     
/* 1463 */     if (this.m_Sett.getPartialLabelling().booleanValue()) {
/* 1464 */       ts_name = this.m_Sett.getAppNameWithSuffix() + "." + activeLearningAlgorithm + "." + labelInferingAlgorithm + ".pred.partial.test." + activeLearningIteration + ".arff";
/*      */     } else {
/* 1466 */       ts_name = this.m_Sett.getAppNameWithSuffix() + "." + activeLearningAlgorithm + "." + labelInferingAlgorithm + ".pred.test." + activeLearningIteration + ".arff";
/*      */     } 
/* 1468 */     allmi.addModelProcessor(1, (ClusModelProcessor)new PredictionWriter(ts_name, this.m_Sett, 
/* 1469 */           getStatManager().createStatistic(3)));
/*      */ 
/*      */     
/* 1472 */     allmi = cr.getAllModelsMI();
/*      */     
/* 1474 */     if (this.m_Sett.getPartialLabelling().booleanValue()) {
/* 1475 */       tr_name = this.m_Sett.getAppNameWithSuffix() + "." + activeLearningAlgorithm + "." + labelInferingAlgorithm + ".pred.partial.train." + activeLearningIteration + ".arff";
/*      */     } else {
/* 1477 */       tr_name = this.m_Sett.getAppNameWithSuffix() + "." + activeLearningAlgorithm + "." + labelInferingAlgorithm + ".pred.train." + activeLearningIteration + ".arff";
/*      */     } 
/*      */     
/* 1480 */     allmi.addModelProcessor(0, (ClusModelProcessor)new PredictionWriter(tr_name, this.m_Sett, 
/* 1481 */           getStatManager()
/* 1482 */           .createStatistic(3))); } public final void activeRun(ClusInductionAlgorithmType clss, Clus clus) throws IOException, ClusException, Exception { ClusDecisionTree clusDecisionTree; RandomSamplingHMC randomSamplingHMC; UncertaintySamplingHMC uncertaintySamplingHMC; UncertaintyVarianceCostHMC uncertaintyVarianceCostHMC;
/*      */     RankingProbability rankingProbability;
/*      */     KNN kNN;
/*      */     SSMAL sSMAL;
/*      */     VSSMAL vSSMAL;
/*      */     UncertaintyInfering uncertaintyInfering;
/* 1488 */     ClusActiveLearningAlgorithmHMC al = null;
/* 1489 */     ClusRun cr = null;
/* 1490 */     ClusLabelInferingAlgorithm lpa = null;
/* 1491 */     ClusLabelPairFindingAlgorithm lca = null;
/*      */     
/* 1493 */     ClassHierarchy h = getStatManager().getHier();
/* 1494 */     cr = partitionData();
/*      */     
/* 1496 */     getStatManager().computeTrainSetStat((RowData)cr.getTrainingSet());
/* 1497 */     if (this.m_Sett.isEnsembleMode()) {
/* 1498 */       ClusEnsembleClassifier clusEnsembleClassifier = new ClusEnsembleClassifier(this);
/*      */     } else {
/* 1500 */       clusDecisionTree = new ClusDecisionTree(this);
/*      */     } 
/* 1502 */     induce(cr, (ClusInductionAlgorithmType)clusDecisionTree);
/*      */     
/* 1504 */     switch (this.m_Sett.getActiveAlgorithm()) {
/*      */       case "Random":
/* 1506 */         randomSamplingHMC = new RandomSamplingHMC(this, this.m_Sett.getActiveAlgorithm());
/*      */         break;
/*      */       case "UncertaintySampling":
/* 1509 */         uncertaintySamplingHMC = new UncertaintySamplingHMC(this, this.m_Sett.getActiveAlgorithm());
/*      */         break;
/*      */       case "QueryByCommittee":
/* 1512 */         if (this.m_Sett.isEnsembleMode()) {
/* 1513 */           QueryByCommitteeHMC queryByCommitteeHMC = new QueryByCommitteeHMC(this, this.m_Sett.getActiveAlgorithm()); break;
/*      */         } 
/* 1515 */         throw new ClusException("QueryByCommittee requires the Ensemble Mode, try setting -forest on the command line");
/*      */ 
/*      */       
/*      */       case "UncertaintyVariance":
/* 1519 */         if (this.m_Sett.isEnsembleMode()) {
/* 1520 */           UncertaintyVarianceHMC uncertaintyVarianceHMC = new UncertaintyVarianceHMC(this, this.m_Sett.getActiveAlgorithm()); break;
/*      */         } 
/* 1522 */         throw new ClusException("UncertaintyVariance requires the Ensemble Mode, try setting -forest on the command line");
/*      */ 
/*      */ 
/*      */       
/*      */       case "UncertaintyVarianceCost":
/* 1527 */         if (this.m_Sett.isEnsembleMode() && this.m_Sett.getPartialLabelling().booleanValue()) {
/* 1528 */           uncertaintyVarianceCostHMC = new UncertaintyVarianceCostHMC(this, this.m_Sett.getActiveAlgorithm()); break;
/*      */         } 
/* 1530 */         throw new ClusException("UncertaintyVarianceCost requires the Ensemble Mode(try setting -forest on the command line) and PartialLabelling = True on the settings file");
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1537 */     switch (this.m_Sett.getLabelPairFindingAlgorithm()) {
/*      */       case "Variance":
/* 1539 */         if (this.m_Sett.isEnsembleMode()) {
/* 1540 */           VarianceInfering varianceInfering = new VarianceInfering((ClusActiveLearningAlgorithm)uncertaintyVarianceCostHMC, getSettings().getLabelInferingBatchSize()); break;
/*      */         } 
/* 1542 */         throw new ClusException("Variance Label Infering Algorithm requires the Ensemble Mode, try setting -forest on the command line");
/*      */ 
/*      */       
/*      */       case "Uncertainty":
/* 1546 */         uncertaintyInfering = new UncertaintyInfering((ClusActiveLearningAlgorithm)uncertaintyVarianceCostHMC, getSettings().getLabelInferingBatchSize());
/*      */         break;
/*      */     } 
/* 1549 */     switch (getSettings().getLabelInferingAlgorithm()) {
/*      */       case "RankingProbability":
/* 1551 */         rankingProbability = new RankingProbability((ClusLabelPairFindingAlgorithm)uncertaintyInfering, (ClusActiveLearningAlgorithm)uncertaintyVarianceCostHMC, getSettings().getPositiveInfering().booleanValue(), getSettings().getNegativeInfering().booleanValue());
/*      */         break;
/*      */       case "KNN":
/* 1554 */         kNN = new KNN(this, (ClusLabelPairFindingAlgorithm)uncertaintyInfering, (ClusActiveLearningAlgorithm)uncertaintyVarianceCostHMC, getSettings().getPositiveInfering().booleanValue(), getSettings().getNegativeInfering().booleanValue());
/*      */         break;
/*      */       case "SSMAL":
/* 1557 */         sSMAL = new SSMAL(this, (ClusLabelPairFindingAlgorithm)uncertaintyInfering, (ClusActiveLearningAlgorithm)uncertaintyVarianceCostHMC, getSettings().getPositiveInfering().booleanValue(), getSettings().getNegativeInfering().booleanValue());
/*      */         break;
/*      */       case "VSSMAL":
/* 1560 */         vSSMAL = new VSSMAL(this, (ClusLabelPairFindingAlgorithm)uncertaintyInfering, (ClusActiveLearningAlgorithm)uncertaintyVarianceCostHMC);
/*      */         break;
/*      */     } 
/*      */     
/* 1564 */     createOutput(cr, (ClusActiveLearningAlgorithm)uncertaintyVarianceCostHMC, (ClusLabelInferingAlgorithm)vSSMAL);
/* 1565 */     boolean isDone = false;
/* 1566 */     while (!isDone) {
/* 1567 */       getNewActiveDataset((ClusActiveLearningAlgorithmHMC)uncertaintyVarianceCostHMC, cr);
/* 1568 */       cr = partitionData();
/* 1569 */       getStatManager().computeTrainSetStat((RowData)cr.getTrainingSet());
/* 1570 */       if (this.m_Sett.isEnsembleMode()) {
/* 1571 */         ClusEnsembleClassifier clusEnsembleClassifier = new ClusEnsembleClassifier(this);
/*      */       } else {
/* 1573 */         clusDecisionTree = new ClusDecisionTree(this);
/*      */       } 
/* 1575 */       induce(cr, (ClusInductionAlgorithmType)clusDecisionTree);
/* 1576 */       if (!this.m_Sett.getLabelInferingAlgorithm().equals("None")) {
/* 1577 */         getNewInferedDataset((ClusLabelInferingAlgorithm)vSSMAL, cr);
/* 1578 */         cr = partitionData();
/* 1579 */         getStatManager().computeTrainSetStat((RowData)cr.getTrainingSet());
/* 1580 */         if (this.m_Sett.isEnsembleMode()) {
/* 1581 */           ClusEnsembleClassifier clusEnsembleClassifier = new ClusEnsembleClassifier(this);
/*      */         } else {
/* 1583 */           clusDecisionTree = new ClusDecisionTree(this);
/*      */         } 
/* 1585 */         induce(cr, (ClusInductionAlgorithmType)clusDecisionTree);
/*      */       } 
/*      */       
/* 1588 */       createOutput(cr, (ClusActiveLearningAlgorithm)uncertaintyVarianceCostHMC, (ClusLabelInferingAlgorithm)vSSMAL);
/* 1589 */       isDone = updateDone((ClusActiveLearningAlgorithm)uncertaintyVarianceCostHMC, (ClusLabelInferingAlgorithm)vSSMAL);
/*      */     } 
/*      */     
/* 1592 */     createOutput(cr, (ClusActiveLearningAlgorithm)uncertaintyVarianceCostHMC, (ClusLabelInferingAlgorithm)vSSMAL); }
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean updateDone(ClusActiveLearningAlgorithm al, ClusLabelInferingAlgorithm lpa) {
/* 1597 */     int maxIteration = this.m_Sett.getIteration();
/* 1598 */     boolean queriedDone = (al.isDone() || al.getIterationCounter() == maxIteration);
/* 1599 */     if (lpa != null) {
/* 1600 */       return (queriedDone || lpa.isDone());
/*      */     }
/* 1602 */     return queriedDone;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private void createOutput(ClusRun cr, ClusActiveLearningAlgorithm al, ClusLabelInferingAlgorithm lpa) throws IOException, ClusException {
/* 1608 */     restartPrinterWriters(cr, al.getIterationCounter(), this.m_Sett.getActiveAlgorithm(), this.m_Sett.getLabelInferingAlgorithm());
/* 1609 */     cr.copyAllModelsMIs();
/*      */     
/* 1611 */     cr.initTestSet();
/* 1612 */     if (this.m_Sett.getWriteActiveTestPredictions())
/*      */     {
/*      */       
/* 1615 */       calcError(cr.getTestIter(), 1, cr, null);
/*      */     }
/* 1617 */     if (this.m_Sett.getWriteActiveTestErrors()) {
/*      */       String tr_name;
/*      */       
/* 1620 */       if (!this.m_Sett.getWriteActiveTestPredictions()) {
/* 1621 */         calcErrorWithoutPrinting(cr.getTestIter(), 1, cr, null);
/*      */       }
/*      */ 
/*      */       
/* 1625 */       if (this.m_Sett.getPartialLabelling().booleanValue()) {
/* 1626 */         tr_name = this.m_Sett.getAppNameWithSuffix() + ".errors." + this.m_Sett.getActiveAlgorithm() + "." + this.m_Sett.getLabelInferingAlgorithm() + ".partial.test." + al.getIterationCounter() + ".arff";
/*      */       } else {
/* 1628 */         tr_name = this.m_Sett.getAppNameWithSuffix() + ".errors." + this.m_Sett.getActiveAlgorithm() + "." + this.m_Sett.getLabelInferingAlgorithm() + ".test." + al.getIterationCounter() + ".arff";
/*      */       } 
/* 1630 */       ClusOutput output = new ClusOutput(tr_name, getM_Schema(), this.m_Sett);
/* 1631 */       output.writeActiveOutput(cr, true, al, lpa, 0, -1.0D);
/* 1632 */       output.close();
/*      */     } 
/* 1634 */     if (this.m_Sett.getWriteActiveTrainPredictions())
/*      */     {
/* 1636 */       calcError(cr.getTrainIter(), 0, cr, null);
/*      */     }
/* 1638 */     if (this.m_Sett.getWriteActiveTrainErrors()) {
/*      */       String tr_name;
/*      */       
/* 1641 */       if (!this.m_Sett.getWriteActiveTrainPredictions()) {
/* 1642 */         calcErrorWithoutPrinting(cr.getTrainIter(), 0, cr, null);
/*      */       }
/*      */       
/* 1645 */       if (this.m_Sett.getPartialLabelling().booleanValue()) {
/* 1646 */         tr_name = this.m_Sett.getAppNameWithSuffix() + ".errors." + this.m_Sett.getActiveAlgorithm() + "." + this.m_Sett.getLabelInferingAlgorithm() + ".partial.train." + al.getIterationCounter() + ".arff";
/*      */       } else {
/* 1648 */         tr_name = this.m_Sett.getAppNameWithSuffix() + ".errors." + this.m_Sett.getActiveAlgorithm() + "." + this.m_Sett.getLabelInferingAlgorithm() + ".train." + al.getIterationCounter() + ".arff";
/*      */       } 
/* 1650 */       ClusOutput output = new ClusOutput(tr_name, getM_Schema(), this.m_Sett);
/* 1651 */       calcExtraTrainingSetErrors(cr);
/* 1652 */       output.writeActiveOutput(cr, true, al, lpa, 1, -1.0D);
/* 1653 */       output.close();
/*      */     } 
/* 1655 */     if (this.m_Sett.getWriteQueriedInstances() && 
/* 1656 */       al.getIterationCounter() > 0) {
/*      */       String tr_name;
/* 1658 */       if (this.m_Sett.getPartialLabelling().booleanValue()) {
/* 1659 */         tr_name = this.m_Sett.getAppNameWithSuffix() + ".queriedInstances." + this.m_Sett.getActiveAlgorithm() + "." + this.m_Sett.getLabelInferingAlgorithm() + ".partial." + al.getIterationCounter() + ".arff";
/*      */       } else {
/* 1661 */         tr_name = this.m_Sett.getAppNameWithSuffix() + ".queriedInstances." + this.m_Sett.getActiveAlgorithm() + "." + this.m_Sett.getLabelInferingAlgorithm() + "." + al.getIterationCounter() + ".arff";
/*      */       } 
/* 1663 */       ARFFFile.writeActiveArff(this.m_Sett.getFileAbsoluteWriter(tr_name), getM_QueriedData());
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void getNewActiveDataset(ClusActiveLearningAlgorithmHMC al, ClusRun cr) throws ClusException, IOException {
/* 1680 */     if (this.m_Sett.getVerbose() > 0) {
/* 1681 */       System.out.println("Starting to sample iteration: " + (al.getIterationCounter() + 1));
/*      */     }
/* 1683 */     setM_QueriedData(al.sample(cr));
/*      */     
/* 1685 */     if (this.m_Sett.getPartialLabelling().booleanValue()) {
/* 1686 */       this.m_Data.addOrReplace(getM_QueriedData());
/*      */     } else {
/* 1688 */       this.m_Data.addDataTuples(getM_QueriedData());
/*      */     } 
/* 1690 */     if (this.m_Sett.getVerbose() > 0) {
/* 1691 */       System.out.println("Finished sampling iteration: " + (al.getIterationCounter() + 1));
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1697 */     getM_QueriedData().getSchema().setRelationName(this.m_Sett.getActiveAlgorithm() + "_iteration_" + al.getIterationCounter());
/*      */   }
/*      */ 
/*      */   
/*      */   private void getNewInferedDataset(ClusLabelInferingAlgorithm lpa, ClusRun cr) throws ClusException, IOException {
/*      */     ClusDecisionTree clusDecisionTree;
/* 1703 */     RowData infered = lpa.inferHMC(cr);
/* 1704 */     this.m_Data.addOrReplace(infered);
/* 1705 */     cr = partitionData();
/* 1706 */     getStatManager().computeTrainSetStat((RowData)cr.getTrainingSet());
/* 1707 */     getM_QueriedData().getSchema().setRelationName(this.m_Sett.getActiveAlgorithm() + "_iteration_" + lpa.getActiveLearning().getIterationCounter());
/*      */     
/* 1709 */     if (this.m_Sett.isEnsembleMode()) {
/* 1710 */       ClusEnsembleClassifier clusEnsembleClassifier = new ClusEnsembleClassifier(this);
/*      */     } else {
/* 1712 */       clusDecisionTree = new ClusDecisionTree(this);
/*      */     } 
/* 1714 */     induce(cr, (ClusInductionAlgorithmType)clusDecisionTree);
/*      */   }
/*      */ 
/*      */   
/*      */   public final void singleRun(ClusInductionAlgorithmType clss) throws IOException, ClusException {
/* 1719 */     ClusModelCollectionIO io = new ClusModelCollectionIO();
/* 1720 */     getM_Summary().setTotalRuns(1);
/* 1721 */     ClusRun run = singleRunMain(clss, null);
/* 1722 */     saveModels(run, io);
/*      */ 
/*      */     
/* 1725 */     if (ClusEnsembleInduce.isOptimized() && this.m_Sett
/* 1726 */       .getNbBaggingSets().getVectorLength() > 1 && this.m_Sett.getBagSelection().getIntVectorSorted()[0] < 1) {
/* 1727 */       io.save(getSettings().getFileAbsolute(this.m_Sett
/* 1728 */             .getAppName() + "_" + 
/* 1729 */             ClusEnsembleInduce.getMaxNbBags() + "_.model"));
/*      */     } else {
/* 1731 */       io.save(getSettings().getFileAbsolute(this.m_Sett
/* 1732 */             .getAppName() + ".model"));
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final ClusRun singleRunMain(ClusInductionAlgorithmType clss, ClusSummary summ) throws IOException, ClusException {
/*      */     ClusOutput output;
/* 1746 */     if (ClusEnsembleInduce.isOptimized() && this.m_Sett
/* 1747 */       .getNbBaggingSets().getVectorLength() > 1 && this.m_Sett.getBagSelection().getIntVectorSorted()[0] < 1) {
/*      */       
/* 1749 */       output = new ClusOutput(this.m_Sett.getAppName() + "_" + ClusEnsembleInduce.getMaxNbBags() + "_.out", getM_Schema(), this.m_Sett);
/*      */     } else {
/*      */       
/* 1752 */       output = new ClusOutput(this.m_Sett.getAppName() + ".out", getM_Schema(), this.m_Sett);
/*      */     } 
/*      */     
/* 1755 */     ClusRun cr = partitionData();
/*      */ 
/*      */     
/* 1758 */     getStatManager().computeTrainSetStat((RowData)cr.getTrainingSet());
/*      */     
/* 1760 */     induce(cr, clss);
/* 1761 */     if (summ == null)
/*      */     {
/* 1763 */       addModelErrorMeasures(cr);
/*      */     }
/*      */     
/* 1766 */     calcError(cr, (ClusSummary)null, (ClusEnsemblePredictionWriter)null);
/* 1767 */     if (summ != null) {
/* 1768 */       for (int i = 0; i < cr.getNbModels(); i++) {
/* 1769 */         ClusModelInfo info = cr.getModelInfo(i);
/* 1770 */         ClusModelInfo summ_info = summ.getModelInfo(i);
/* 1771 */         ClusErrorList test_err = summ_info.getTestError();
/* 1772 */         info.setTestError(test_err);
/*      */       } 
/*      */     }
/* 1775 */     calcExtraTrainingSetErrors(cr);
/* 1776 */     output.writeHeader();
/* 1777 */     output.writeOutput(cr, true, this.m_Sett.isOutTrainError());
/* 1778 */     output.close();
/* 1779 */     clss.saveInformation(this.m_Sett.getAppName());
/* 1780 */     return cr;
/*      */   }
/*      */ 
/*      */   
/*      */   public final XValMainSelection getXValSelection() throws IOException, ClusException {
/* 1785 */     if (this.m_Sett.isLOOXVal())
/* 1786 */       return (XValMainSelection)new XValRandomSelection(this.m_Data.getNbRows(), this.m_Data
/* 1787 */           .getNbRows()); 
/* 1788 */     if (this.m_Sett.isNullXValFile()) {
/* 1789 */       return getM_Schema().getXValSelection((ClusData)this.m_Data);
/*      */     }
/* 1791 */     return (XValMainSelection)XValDataSelection.readFoldsFile(this.m_Sett.getXValFile(), this.m_Data
/* 1792 */         .getNbRows());
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public final void combineAllFoldRuns(ClusInductionAlgorithmType clss) throws IOException, ClusException {
/* 1798 */     ClusOutput output = new ClusOutput(this.m_Sett.getAppName() + ".xval", getM_Schema(), this.m_Sett);
/* 1799 */     output.writeHeader();
/* 1800 */     XValMainSelection sel = getXValSelection();
/* 1801 */     getM_Summary().setTotalRuns(sel.getNbFolds());
/* 1802 */     for (int fold = 0; fold < sel.getNbFolds(); fold++) {
/* 1803 */       String dat_fname = "folds/" + this.m_Sett.getAppName() + ".fold." + fold;
/* 1804 */       System.out.println("Reading: " + dat_fname);
/* 1805 */       ObjectLoadStream strm = new ObjectLoadStream(new FileInputStream(dat_fname));
/*      */       
/*      */       try {
/* 1808 */         getM_Summary().addSummary((ClusRun)strm.readObject());
/* 1809 */         output.print((String)strm.readObject());
/* 1810 */       } catch (ClassNotFoundException classNotFoundException) {}
/*      */       
/* 1812 */       strm.close();
/*      */     } 
/*      */     
/* 1815 */     PrintWriter wrt = new PrintWriter(new OutputStreamWriter(new FileOutputStream(this.m_Sett.getAppName() + ".test.pred")));
/* 1816 */     for (int i = 0; i < sel.getNbFolds(); i++) {
/* 1817 */       String pw_fname = "folds/" + this.m_Sett.getAppName() + ".test.pred." + i;
/*      */       
/* 1819 */       System.out.println("Combining: " + pw_fname);
/* 1820 */       LineNumberReader rdr = new LineNumberReader(new InputStreamReader(new FileInputStream(pw_fname)));
/*      */       
/* 1822 */       String line = rdr.readLine();
/* 1823 */       if (i != 0) {
/*      */         
/* 1825 */         while (line != null && !line.equals("@DATA")) {
/* 1826 */           line = rdr.readLine();
/*      */         }
/* 1828 */         line = rdr.readLine();
/*      */       } 
/* 1830 */       while (line != null) {
/* 1831 */         wrt.println(line);
/* 1832 */         line = rdr.readLine();
/*      */       } 
/* 1834 */       rdr.close();
/*      */     } 
/* 1836 */     wrt.close();
/* 1837 */     output.writeSummary(getM_Summary());
/* 1838 */     output.close();
/*      */     
/* 1840 */     ClusRandom.initialize(this.m_Sett);
/* 1841 */     singleRunMain(clss, getM_Summary());
/*      */   }
/*      */ 
/*      */   
/*      */   public final void oneFoldRun(ClusInductionAlgorithmType clss, int fold) throws IOException, ClusException {
/* 1846 */     if (fold == 0) {
/* 1847 */       combineAllFoldRuns(clss);
/*      */     } else {
/* 1849 */       fold--;
/* 1850 */       FileUtil.mkdir("folds");
/* 1851 */       ClusOutput output = new ClusOutput(getM_Schema(), this.m_Sett);
/* 1852 */       ClusStatistic target = getStatManager().createStatistic(3);
/*      */       
/* 1854 */       String pw_fname = "folds/" + this.m_Sett.getAppName() + ".test.pred." + fold;
/*      */       
/* 1856 */       PredictionWriter wrt = new PredictionWriter(pw_fname, this.m_Sett, target);
/*      */       
/* 1858 */       wrt.globalInitialize(getM_Schema());
/* 1859 */       XValMainSelection sel = getXValSelection();
/* 1860 */       ClusModelCollectionIO io = new ClusModelCollectionIO();
/* 1861 */       getM_Summary().setTotalRuns(sel.getNbFolds());
/* 1862 */       ClusRun cr = doOneFold(fold, clss, sel, io, wrt, output, null, null);
/* 1863 */       wrt.close();
/* 1864 */       output.close();
/*      */ 
/*      */       
/* 1867 */       cr.deleteData();
/* 1868 */       String dat_fname = "folds/" + this.m_Sett.getAppName() + ".fold." + fold;
/* 1869 */       ObjectSaveStream strm = new ObjectSaveStream(new FileOutputStream(dat_fname));
/*      */       
/* 1871 */       strm.writeObject(cr);
/* 1872 */       strm.writeObject(output.getString());
/* 1873 */       strm.close();
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final ClusRun doOneFold(int fold, ClusInductionAlgorithmType clss, XValMainSelection sel, ClusModelCollectionIO io, PredictionWriter wrt, ClusOutput output, ClusErrorOutput errOutput, ClusEnsemblePredictionWriter ens_pred) throws IOException, ClusException {
/* 1881 */     XValSelection msel = new XValSelection(sel, fold);
/* 1882 */     ClusRun cr = partitionData((ClusSelection)msel, fold + 1);
/*      */     
/* 1884 */     getStatManager().computeTrainSetStat((RowData)cr.getTrainingSet());
/* 1885 */     if (wrt != null) {
/* 1886 */       wrt.println("! Fold = " + fold);
/* 1887 */       cr.getAllModelsMI().addModelProcessor(1, (ClusModelProcessor)wrt);
/* 1888 */       if (this.m_Sett.isOutFoldData()) {
/* 1889 */         ARFFFile.writeArff(this.m_Sett.getAppName() + "-test-" + fold + ".txt", cr.getTestSet());
/* 1890 */         ARFFFile.writeArff(this.m_Sett.getAppName() + "-train-" + fold + ".txt", (RowData)cr.getTrainingSet());
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1910 */     induce(cr, clss);
/* 1911 */     if (this.m_Sett.isRuleWiseErrors()) {
/* 1912 */       addModelErrorMeasures(cr);
/*      */     }
/*      */     
/* 1915 */     calcError(cr, getM_Summary(), ens_pred);
/* 1916 */     if (errOutput != null)
/*      */     {
/* 1918 */       errOutput.writeOutput(cr, false, false, 
/* 1919 */           (getStatManager().getClusteringWeights()).m_Weights);
/*      */     }
/* 1921 */     if (this.m_Sett.isOutputFoldModels()) {
/*      */       
/* 1923 */       output.writeOutput(cr, false);
/* 1924 */       if (!Settings.m_EnsembleMode) {
/* 1925 */         ClusModelInfo mi = cr.getModelInfo(2);
/* 1926 */         if (mi != null) {
/* 1927 */           io.addModel(mi);
/*      */         }
/*      */       } 
/*      */     } else {
/* 1931 */       output.writeBrief(cr);
/*      */     } 
/* 1933 */     return cr;
/*      */   }
/*      */ 
/*      */   
/*      */   public final void xvalRun(ClusInductionAlgorithmType clss) throws IOException, ClusException {
/* 1938 */     ClusErrorOutput errFileOutput = null;
/* 1939 */     if (getSettings().isWriteErrorFile()) {
/* 1940 */       errFileOutput = new ClusErrorOutput(this.m_Sett.getAppName() + ".err", getM_Schema(), this.m_Sett);
/* 1941 */       errFileOutput.writeHeader();
/*      */     } 
/* 1943 */     PredictionWriter testPredWriter = null;
/* 1944 */     if (getSettings().isWriteTestSetPredictions()) {
/* 1945 */       ClusStatistic target = getStatManager().createStatistic(3);
/*      */       
/* 1947 */       testPredWriter = new PredictionWriter(this.m_Sett.getAppName() + ".test.pred.txt", this.m_Sett, target);
/*      */       
/* 1949 */       testPredWriter.globalInitialize(getM_Schema());
/*      */     } 
/*      */     
/* 1952 */     ClusOutput output = new ClusOutput(this.m_Sett.getAppName() + ".xval", getM_Schema(), this.m_Sett);
/* 1953 */     output.writeHeader();
/* 1954 */     XValMainSelection sel = getXValSelection();
/* 1955 */     ClusModelCollectionIO io = new ClusModelCollectionIO();
/* 1956 */     ClusEnsemblePredictionWriter ens_pred = null;
/* 1957 */     if (getSettings().shouldWritePredictionsFromEnsemble()) {
/* 1958 */       ens_pred = new ClusEnsemblePredictionWriter(getStatManager().getSettings().getAppName() + ".ens.xval.preds", getStatManager().getSchema(), getStatManager().getSettings());
/*      */     }
/*      */     
/* 1961 */     for (int fold = 0; fold < sel.getNbFolds(); fold++) {
/* 1962 */       doOneFold(fold, clss, sel, io, testPredWriter, output, errFileOutput, ens_pred);
/*      */     }
/*      */     
/* 1965 */     if (getSettings().shouldWritePredictionsFromEnsemble()) {
/* 1966 */       ens_pred.closeWriter();
/*      */     }
/* 1968 */     output.writeSummary(getM_Summary());
/* 1969 */     output.close();
/* 1970 */     if (testPredWriter != null) {
/* 1971 */       testPredWriter.close();
/*      */     }
/* 1973 */     if (errFileOutput != null) {
/* 1974 */       errFileOutput.close();
/*      */     }
/*      */     
/* 1977 */     ClusRandom.initialize(this.m_Sett);
/* 1978 */     ClusRun run = singleRunMain(clss, getM_Summary());
/* 1979 */     saveModels(run, io);
/* 1980 */     io.save(getSettings().getFileAbsolute(this.m_Sett.getAppName() + ".model"));
/*      */   }
/*      */ 
/*      */   
/*      */   public final void baggingRun(ClusInductionAlgorithmType clss) throws IOException, ClusException {
/* 1985 */     ClusOutput output = new ClusOutput(this.m_Sett.getAppName() + ".bag", getM_Schema(), this.m_Sett);
/* 1986 */     output.writeHeader();
/* 1987 */     ClusStatistic target = getStatManager().createStatistic(3);
/*      */     
/* 1989 */     PredictionWriter wrt = new PredictionWriter(this.m_Sett.getAppName() + ".test.pred.txt", this.m_Sett, target);
/*      */     
/* 1991 */     wrt.globalInitialize(getM_Schema());
/* 1992 */     int nbsets = this.m_Sett.getBaggingSets();
/* 1993 */     int nbrows = this.m_Data.getNbRows();
/* 1994 */     for (int i = 0; i < nbsets; i++) {
/* 1995 */       BaggingSelection msel = new BaggingSelection(nbrows, getSettings().getEnsembleBagSize());
/* 1996 */       ClusRun cr = partitionData((ClusSelection)msel, i + 1);
/* 1997 */       ClusModelInfo mi = cr.getModelInfo(2);
/* 1998 */       mi.addModelProcessor(1, (ClusModelProcessor)wrt);
/* 1999 */       induce(cr, clss);
/* 2000 */       calcError(cr, getM_Summary(), (ClusEnsemblePredictionWriter)null);
/* 2001 */       if (this.m_Sett.isOutputFoldModels()) {
/* 2002 */         output.writeOutput(cr, false);
/*      */       }
/*      */     } 
/* 2005 */     wrt.close();
/* 2006 */     output.writeSummary(getM_Summary());
/* 2007 */     output.close();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final void exhaustiveRun(ClusInductionAlgorithmType clss) throws IOException, ClusException {
/* 2018 */     ClusOutput output = new ClusOutput(this.m_Sett.getAppName() + ".all", getM_Schema(), this.m_Sett);
/* 2019 */     output.writeHeader();
/* 2020 */     ClusRun cr = partitionData();
/* 2021 */     induce(cr, clss);
/* 2022 */     output.writeOutput(cr, false);
/* 2023 */     output.writeSummary(getM_Summary());
/* 2024 */     output.close();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public RowData getM_ActiveData() {
/* 2032 */     return this.m_ActiveData;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setM_ActiveData(RowData m_ActiveData) {
/* 2039 */     this.m_ActiveData = m_ActiveData;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public RowData getM_QueriedData() {
/* 2046 */     return this.m_QueriedData;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setM_QueriedData(RowData m_QueriedData) {
/* 2053 */     this.m_QueriedData = m_QueriedData;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ClusSummary getM_Summary() {
/* 2060 */     return this.m_Summary;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setM_Summary(ClusSummary m_Summary) {
/* 2067 */     this.m_Summary = m_Summary;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ClusSchema getM_Schema() {
/* 2074 */     return this.m_Schema;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setM_Schema(ClusSchema m_Schema) {
/* 2081 */     this.m_Schema = m_Schema;
/*      */   }
/*      */   
/*      */   private class MyClusInitializer implements ClusSchemaInitializer {
/*      */     private MyClusInitializer() {}
/*      */     
/*      */     public void initSchema(ClusSchema schema) throws ClusException, IOException {
/* 2088 */       if (Clus.this.getSettings().getHeuristic() == 4) {
/* 2089 */         schema.addAttrType((ClusAttrType)new IntegerAttrType("SSPD"));
/*      */       }
/* 2091 */       schema.setTarget(new IntervalCollection(Clus.this.m_Sett.getTarget()));
/* 2092 */       schema.setDisabled(new IntervalCollection(Clus.this.m_Sett.getDisabled()));
/* 2093 */       schema.setClustering(new IntervalCollection(Clus.this.m_Sett.getClustering()));
/* 2094 */       schema.setDescriptive(new IntervalCollection(Clus.this.m_Sett.getDescriptive()));
/* 2095 */       schema.setKey(new IntervalCollection(Clus.this.m_Sett.getKey()));
/*      */       
/* 2097 */       schema.updateAttributeUse();
/*      */       
/* 2099 */       schema.initializeFrom(Clus.this.getM_Schema());
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   public void writeTargets() throws ClusException, IOException {
/* 2105 */     ClassHierarchy hier = getStatManager().getHier();
/* 2106 */     if (hier != null) {
/* 2107 */       hier.writeTargets(this.m_Data, getM_Schema(), this.m_Sett.getAppName());
/*      */     }
/*      */   }
/*      */   
/*      */   public void showInfo() throws ClusException, IOException {
/* 2112 */     RowData data = this.m_Data;
/* 2113 */     System.out.println("Name            #Rows      #Missing  #Nominal #Numeric #Target  #Classes");
/* 2114 */     System.out.print(StringUtils.printStr(this.m_Sett.getAppName(), 16));
/* 2115 */     System.out.print(StringUtils.printInt(data.getNbRows(), 11));
/*      */ 
/*      */ 
/*      */     
/* 2119 */     double perc = getM_Schema().getTotalInputNbMissing() / data.getNbRows() / getM_Schema().getNbDescriptiveAttributes() * 100.0D;
/*      */     
/* 2121 */     System.out.print(StringUtils.printStr(ClusFormat.TWO_AFTER_DOT
/* 2122 */           .format(perc) + "%", 10));
/* 2123 */     System.out.print(StringUtils.printInt(getM_Schema().getNbNominalDescriptiveAttributes(), 9));
/* 2124 */     System.out.print(StringUtils.printInt(getM_Schema().getNbNumericDescriptiveAttributes(), 9));
/* 2125 */     System.out.print(StringUtils.printInt(getM_Schema().getNbAllAttrUse(3), 9));
/* 2126 */     NominalAttrType[] tarnom = getM_Schema().getNominalAttrUse(3);
/* 2127 */     if (tarnom != null && tarnom.length >= 1) {
/* 2128 */       if (tarnom.length == 1) {
/* 2129 */         System.out.println(tarnom[0].getNbValues());
/*      */       } else {
/* 2131 */         System.out.println("M:" + tarnom.length);
/*      */       } 
/*      */     } else {
/* 2134 */       System.out.println("(num)");
/*      */     } 
/* 2136 */     System.out.println();
/* 2137 */     getM_Schema().showDebug();
/* 2138 */     if (getStatManager().hasClusteringStat()) {
/* 2139 */       ClusStatistic[] stats = new ClusStatistic[2];
/* 2140 */       stats[0] = getStatManager().createClusteringStat();
/* 2141 */       stats[1] = getStatManager().createStatistic(0);
/* 2142 */       this.m_Data.calcTotalStats(stats);
/* 2143 */       if (!this.m_Sett.isNullTestFile()) {
/* 2144 */         System.out.println("Loading: " + this.m_Sett.getTestFile());
/* 2145 */         updateStatistic(this.m_Sett.getTestFile(), stats);
/*      */       } 
/* 2147 */       if (!this.m_Sett.isNullPruneFile()) {
/* 2148 */         System.out.println("Loading: " + this.m_Sett.getPruneFile());
/* 2149 */         updateStatistic(this.m_Sett.getPruneFile(), stats);
/*      */       } 
/* 2151 */       ClusStatistic.calcMeans(stats);
/* 2152 */       MyFile statf = new MyFile(getSettings().getAppName() + ".distr");
/* 2153 */       statf.log("** Target:");
/* 2154 */       stats[0].printDistribution(statf.getWriter());
/* 2155 */       statf.log("** All:");
/* 2156 */       stats[1].printDistribution(statf.getWriter());
/* 2157 */       statf.close();
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public void updateStatistic(String fname, ClusStatistic[] stats) throws ClusException, IOException {
/* 2163 */     MyClusInitializer init = new MyClusInitializer();
/*      */     
/* 2165 */     DiskTupleIterator diskTupleIterator = new DiskTupleIterator(fname, init, getPreprocs(true), this.m_Sett);
/* 2166 */     diskTupleIterator.init();
/* 2167 */     DataTuple tuple = diskTupleIterator.readTuple();
/* 2168 */     while (tuple != null) {
/* 2169 */       for (int i = 0; i < stats.length; i++) {
/* 2170 */         stats[i].updateWeighted(tuple, 1.0D);
/*      */       }
/* 2172 */       tuple = diskTupleIterator.readTuple();
/*      */     } 
/* 2174 */     diskTupleIterator.close();
/*      */   }
/*      */   
/*      */   public void setFolds(int folds) {
/* 2178 */     this.m_Sett.setXValFolds(folds);
/*      */   }
/*      */   
/*      */   public void showDebug() {
/* 2182 */     getM_Schema().showDebug();
/*      */   }
/*      */   
/*      */   public void showHelp() {
/* 2186 */     ClusOutput.showHelp();
/*      */   }
/*      */   
/*      */   public String[] getOptionArgs() {
/* 2190 */     return OPTION_ARGS;
/*      */   }
/*      */   
/*      */   public int[] getOptionArgArities() {
/* 2194 */     return OPTION_ARITIES;
/*      */   }
/*      */   
/*      */   public int getNbMainArgs() {
/* 2198 */     return 1;
/*      */   }
/*      */   
/*      */   public String getAppName() {
/* 2202 */     return this.m_Sett.getAppName();
/*      */   }
/*      */   
/*      */   public static void main(String[] args) throws Exception, Exception {
/*      */     try {
/* 2207 */       ClusOutput.printHeader();
/* 2208 */       Clus clus = new Clus();
/* 2209 */       Settings sett = clus.getSettings();
/* 2210 */       CMDLineArgs cargs = new CMDLineArgs(clus);
/* 2211 */       cargs.process(args);
/* 2212 */       if (cargs.hasOption("copying")) {
/* 2213 */         ClusOutput.printGPL();
/* 2214 */         System.exit(0);
/* 2215 */       } else if (cargs.getNbMainArgs() == 0) {
/* 2216 */         clus.showHelp();
/* 2217 */         System.out.println();
/* 2218 */         System.out.println("Expected main argument");
/* 2219 */         System.exit(0);
/*      */       } 
/* 2221 */       if (cargs.allOK()) {
/* 2222 */         ClusDecisionTree clusDecisionTree; sett.setDate(new Date());
/* 2223 */         sett.setAppName(cargs.getMainArg(0));
/* 2224 */         clus.initSettings(cargs);
/* 2225 */         ClusInductionAlgorithmType clss = null;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 2239 */         if (cargs.hasOption("knn")) {
/* 2240 */           clus.getSettings().setSectionKNNEnabled(true);
/* 2241 */           clus.getSettings().setSectionTreeEnabled(false);
/* 2242 */           KNNClassifier kNNClassifier = new KNNClassifier(clus);
/* 2243 */         } else if (cargs.hasOption("knnTree")) {
/* 2244 */           clus.getSettings().setSectionKNNTEnabled(true);
/* 2245 */           KNNTreeClassifier kNNTreeClassifier = new KNNTreeClassifier(clus);
/* 2246 */         } else if (cargs.hasOption("rules")) {
/* 2247 */           clus.getSettings().setSectionBeamEnabled(true);
/* 2248 */           clus.getSettings().setSectionRulesEnabled(true);
/* 2249 */           ClusRuleClassifier clusRuleClassifier = new ClusRuleClassifier(clus);
/* 2250 */         } else if (!cargs.hasOption("weka")) {
/*      */ 
/*      */           
/* 2253 */           if (cargs.hasOption("tuneftest")) {
/* 2254 */             ClusDecisionTree clusDecisionTree1 = new ClusDecisionTree(clus);
/* 2255 */             CDTTuneFTest cDTTuneFTest = new CDTTuneFTest((ClusInductionAlgorithmType)clusDecisionTree1);
/* 2256 */           } else if (cargs.hasOption("tunesize")) {
/* 2257 */             ClusDecisionTree clusDecisionTree1 = new ClusDecisionTree(clus);
/* 2258 */             CDTuneSizeConstrPruning cDTuneSizeConstrPruning = new CDTuneSizeConstrPruning((ClusInductionAlgorithmType)clusDecisionTree1);
/* 2259 */           } else if (cargs.hasOption("beam")) {
/* 2260 */             clus.getSettings().setSectionBeamEnabled(true);
/* 2261 */             clss = sett.isFastBS() ? (ClusInductionAlgorithmType)new ClusFastBeamSearch(clus) : (ClusInductionAlgorithmType)new ClusBeamSearch(clus);
/*      */           }
/* 2263 */           else if (cargs.hasOption("exhaustive")) {
/*      */             
/* 2265 */             clus.getSettings().setSectionExhaustiveEnabled(true);
/* 2266 */             ClusExhaustiveDFSearch clusExhaustiveDFSearch = new ClusExhaustiveDFSearch(clus);
/* 2267 */           } else if (cargs.hasOption("sit")) {
/*      */             
/* 2269 */             clus.getSettings().setSectionSITEnabled(true);
/* 2270 */             ClusDecisionTree clusDecisionTree1 = new ClusDecisionTree(clus);
/* 2271 */             ClusSITDecisionTree clusSITDecisionTree = new ClusSITDecisionTree((ClusInductionAlgorithmType)clusDecisionTree1);
/* 2272 */           } else if (cargs.hasOption("forest")) {
/* 2273 */             sett.setEnsembleMode(true);
/* 2274 */             ClusEnsembleClassifier clusEnsembleClassifier = new ClusEnsembleClassifier(clus);
/*      */           } else {
/*      */             
/* 2277 */             clusDecisionTree = new ClusDecisionTree(clus);
/*      */           } 
/*      */         } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 2291 */         if (cargs.hasOption("corrmatrix")) {
/* 2292 */           clus.initialize(cargs, (ClusInductionAlgorithmType)clusDecisionTree);
/* 2293 */           CorrelationMatrixComputer cmp = new CorrelationMatrixComputer();
/* 2294 */           cmp.compute(clus.m_Data);
/* 2295 */           cmp.printMatrixTeX();
/* 2296 */         } else if (cargs.hasOption("info")) {
/* 2297 */           clus.initialize(cargs, (ClusInductionAlgorithmType)clusDecisionTree);
/* 2298 */           clus.showInfo();
/* 2299 */         } else if (cargs.hasOption("writetargets")) {
/* 2300 */           clus.initialize(cargs, (ClusInductionAlgorithmType)clusDecisionTree);
/* 2301 */           clus.writeTargets();
/* 2302 */         } else if (cargs.hasOption("out2model")) {
/* 2303 */           clus.initialize(cargs, (ClusInductionAlgorithmType)clusDecisionTree);
/* 2304 */           clus.out2model(cargs.getOptionValue("out2model"));
/* 2305 */         } else if (cargs.hasOption("test")) {
/* 2306 */           clus.initialize(cargs, (ClusInductionAlgorithmType)clusDecisionTree);
/* 2307 */           clus.testModel(cargs.getOptionValue("test"));
/* 2308 */         } else if (cargs.hasOption("normalize")) {
/* 2309 */           clus.initialize(cargs, (ClusInductionAlgorithmType)clusDecisionTree);
/* 2310 */           clus.normalizeDataAndWriteToFile();
/* 2311 */         } else if (cargs.hasOption("debug")) {
/* 2312 */           clus.initialize(cargs, (ClusInductionAlgorithmType)clusDecisionTree);
/* 2313 */           clus.showDebug();
/* 2314 */         } else if (cargs.hasOption("xval")) {
/* 2315 */           clus.isxval = true;
/* 2316 */           clus.initialize(cargs, (ClusInductionAlgorithmType)clusDecisionTree);
/* 2317 */           clus.xvalRun((ClusInductionAlgorithmType)clusDecisionTree);
/* 2318 */         } else if (cargs.hasOption("fold")) {
/* 2319 */           clus.isxval = true;
/* 2320 */           clus.initialize(cargs, (ClusInductionAlgorithmType)clusDecisionTree);
/* 2321 */           clus.oneFoldRun((ClusInductionAlgorithmType)clusDecisionTree, cargs.getOptionInteger("fold"));
/* 2322 */         } else if (cargs.hasOption("bag")) {
/* 2323 */           clus.isxval = true;
/* 2324 */           clus.initialize(cargs, (ClusInductionAlgorithmType)clusDecisionTree);
/* 2325 */           clus.baggingRun((ClusInductionAlgorithmType)clusDecisionTree);
/* 2326 */         } else if (cargs.hasOption("show")) {
/*      */           
/* 2328 */           clus.showTree(clus.getAppName());
/* 2329 */         } else if (cargs.hasOption("gui")) {
/* 2330 */           clus.gui(cargs.getMainArg(0));
/* 2331 */         } else if (cargs.hasOption("tseries")) {
/* 2332 */           clus.getSettings().setSectionTimeSeriesEnabled(true);
/* 2333 */           clus.initialize(cargs, (ClusInductionAlgorithmType)clusDecisionTree);
/* 2334 */           clus.singleRun((ClusInductionAlgorithmType)clusDecisionTree);
/* 2335 */         } else if (cargs.hasOption("active")) {
/* 2336 */           if (cargs.hasOption("hsc")) {
/* 2337 */             HSC m = new HSC();
/* 2338 */             m.run(args);
/*      */           } else {
/* 2340 */             clus.initializeActive(cargs, (ClusInductionAlgorithmType)clusDecisionTree);
/* 2341 */             clus.activeRun((ClusInductionAlgorithmType)clusDecisionTree, clus);
/*      */           } 
/*      */         } else {
/* 2344 */           clus.initialize(cargs, (ClusInductionAlgorithmType)clusDecisionTree);
/* 2345 */           clus.singleRun((ClusInductionAlgorithmType)clusDecisionTree);
/*      */         } 
/*      */       } 
/*      */ 
/*      */ 
/*      */       
/* 2351 */       DebugFile.close();
/* 2352 */     } catch (ClusException e) {
/* 2353 */       System.err.println();
/* 2354 */       System.err.println("Error: " + e);
/* 2355 */     } catch (IllegalArgumentException e) {
/* 2356 */       System.err.println();
/* 2357 */       System.err.println("Error: " + e.getMessage());
/* 2358 */     } catch (FileNotFoundException e) {
/* 2359 */       System.err.println();
/* 2360 */       System.err.println("File not found: " + e);
/* 2361 */     } catch (IOException e) {
/* 2362 */       System.err.println();
/* 2363 */       System.err.println("IO Error: " + e);
/* 2364 */     } catch (ClassNotFoundException e) {
/* 2365 */       System.err.println();
/* 2366 */       System.err.println("Class not found" + e);
/*      */     } 
/*      */   }
/*      */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\Clus.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */