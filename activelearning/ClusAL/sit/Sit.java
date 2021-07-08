/*     */ package sit;
/*     */ 
/*     */ import clus.data.io.ARFFFile;
/*     */ import clus.data.io.ClusReader;
/*     */ import clus.data.io.ClusView;
/*     */ import clus.data.rows.DataTuple;
/*     */ import clus.data.rows.RowData;
/*     */ import clus.data.type.ClusAttrType;
/*     */ import clus.data.type.ClusSchema;
/*     */ import clus.data.type.NumericAttrType;
/*     */ import clus.main.ClusStat;
/*     */ import clus.main.Settings;
/*     */ import clus.selection.ClusSelection;
/*     */ import clus.selection.XValMainSelection;
/*     */ import clus.selection.XValRandomSelection;
/*     */ import clus.selection.XValSelection;
/*     */ import clus.util.ClusException;
/*     */ import clus.util.ClusRandom;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Date;
/*     */ import jeans.resource.ResourceInfo;
/*     */ import jeans.util.IntervalCollection;
/*     */ import jeans.util.cmdline.CMDLineArgs;
/*     */ import jeans.util.cmdline.CMDLineArgsProvider;
/*     */ import sit.mtLearner.ClusLearner;
/*     */ import sit.mtLearner.KNNLearner;
/*     */ import sit.mtLearner.MTLearner;
/*     */ import sit.searchAlgorithm.AllTargets;
/*     */ import sit.searchAlgorithm.GeneticSearch;
/*     */ import sit.searchAlgorithm.GreedySIT;
/*     */ import sit.searchAlgorithm.NoStopSearch;
/*     */ import sit.searchAlgorithm.OneTarget;
/*     */ import sit.searchAlgorithm.SearchAlgorithm;
/*     */ import sit.searchAlgorithm.TC;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Sit
/*     */   implements CMDLineArgsProvider
/*     */ {
/*  45 */   protected Settings m_Sett = new Settings();
/*     */   
/*     */   protected ClusSchema m_Schema;
/*     */   
/*     */   protected RowData m_Data;
/*     */   
/*     */   protected MTLearner m_Learner;
/*     */   protected SearchAlgorithm m_Search;
/*     */   protected int m_SearchSelection;
/*  54 */   private static Sit singleton = null;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Sit getInstance() {
/*  61 */     if (singleton == null) {
/*  62 */       singleton = new Sit();
/*     */     }
/*  64 */     return singleton;
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
/*     */   public void initialize() throws IOException, ClusException {
/*  77 */     ARFFFile arff = null;
/*  78 */     System.out.println("Loading '" + this.m_Sett.getAppName() + "'");
/*  79 */     ClusRandom.initialize(this.m_Sett);
/*  80 */     ClusReader reader = new ClusReader(this.m_Sett.getDataFile(), this.m_Sett);
/*  81 */     System.out.println();
/*  82 */     System.out.println("Reading ARFF Header");
/*  83 */     arff = new ARFFFile(reader);
/*  84 */     this.m_Schema = arff.read(this.m_Sett);
/*     */     
/*  86 */     System.out.println();
/*  87 */     System.out.println("Reading CSV Data");
/*     */     
/*  89 */     this.m_Sett.updateTarget(this.m_Schema);
/*  90 */     this.m_Schema.initializeSettings(this.m_Sett);
/*  91 */     this.m_Sett.setTarget(this.m_Schema.getTarget().toString());
/*  92 */     this.m_Sett.setDisabled(this.m_Schema.getDisabled().toString());
/*  93 */     this.m_Sett.setClustering(this.m_Schema.getClustering().toString());
/*  94 */     this.m_Sett.setDescriptive(this.m_Schema.getDescriptive().toString());
/*     */     
/*  96 */     if (ResourceInfo.isLibLoaded()) {
/*  97 */       ClusStat.m_InitialMemory = ResourceInfo.getMemory();
/*     */     }
/*  99 */     ClusView view = this.m_Schema.createNormalView();
/* 100 */     this.m_Data = view.readData(reader, this.m_Schema);
/* 101 */     reader.close();
/*     */     
/* 103 */     this.m_Sett.update(this.m_Schema);
/*     */     
/* 105 */     Settings.IS_XVAL = true;
/* 106 */     System.out.println("Has missing values: " + this.m_Schema.hasMissing());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final Settings getSettings() {
/* 114 */     return this.m_Sett;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final void initSettings(CMDLineArgs cargs) throws IOException {
/* 123 */     this.m_Sett.initialize(cargs, true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void InitLearner() {
/* 131 */     if (this.m_Sett.getLearnerName().equals("KNN")) {
/* 132 */       System.out.println("Using KNN Learner");
/* 133 */       this.m_Learner = (MTLearner)new KNNLearner();
/*     */     } else {
/* 135 */       System.out.println("Using Clus Learner");
/* 136 */       this.m_Learner = (MTLearner)new ClusLearner();
/*     */     } 
/*     */ 
/*     */     
/* 140 */     this.m_Learner.init(this.m_Data, this.m_Sett);
/* 141 */     int mt = (new Integer(this.m_Sett.getMainTarget())).intValue() - 1;
/* 142 */     ClusAttrType mainTarget = this.m_Schema.getAttrType(mt);
/* 143 */     this.m_Learner.setMainTarget(mainTarget);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void InitLearner(RowData data) {
/* 151 */     if (this.m_Sett.getLearnerName().equals("KNN")) {
/* 152 */       System.out.println("Using KNN Learner");
/* 153 */       this.m_Learner = (MTLearner)new KNNLearner();
/*     */     } else {
/* 155 */       System.out.println("Using Clus Learner");
/* 156 */       this.m_Learner = (MTLearner)new ClusLearner();
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 161 */     this.m_Learner.init(data, this.m_Sett);
/* 162 */     int mt = (new Integer(this.m_Sett.getMainTarget())).intValue() - 1;
/* 163 */     ClusAttrType mainTarget = this.m_Schema.getAttrType(mt);
/* 164 */     this.m_Learner.setMainTarget(mainTarget);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void InitSearchAlgorithm() {
/* 171 */     String search = this.m_Sett.getSearchName();
/* 172 */     if (search.equals("OneTarget")) {
/* 173 */       this.m_Search = (SearchAlgorithm)new OneTarget();
/* 174 */       System.out.println("Search = single target");
/*     */     }
/* 176 */     else if (search.equals("AllTargets")) {
/* 177 */       this.m_Search = (SearchAlgorithm)new AllTargets();
/* 178 */       System.out.println("Search = full multi target");
/*     */     }
/* 180 */     else if (search.equals("GeneticSearch")) {
/* 181 */       this.m_Search = (SearchAlgorithm)new GeneticSearch();
/* 182 */       System.out.println("Search = Genetic search strategy");
/*     */     }
/* 184 */     else if (search.equals("SIT")) {
/* 185 */       this.m_Search = (SearchAlgorithm)new GreedySIT();
/* 186 */       System.out.println("Search = SIT, with stop criterion");
/*     */     }
/* 188 */     else if (search.equals("NoStop")) {
/* 189 */       this.m_Search = (SearchAlgorithm)new NoStopSearch();
/* 190 */       System.out.println("Search = SIT, no stop criterion");
/*     */     }
/* 192 */     else if (search.equals("TC")) {
/* 193 */       this.m_Search = (SearchAlgorithm)new TC();
/* 194 */       System.out.println("Search = TC");
/*     */     } else {
/*     */       
/* 197 */       System.err.println("Search strategy unknown!");
/*     */     } 
/*     */ 
/*     */     
/* 201 */     this.m_Search.setMTLearner(this.m_Learner);
/* 202 */     this.m_Search.setSettings(this.m_Sett);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TargetSet search() {
/* 212 */     int mt = (new Integer(this.m_Sett.getMainTarget())).intValue() - 1;
/* 213 */     ClusAttrType mainTarget = this.m_Schema.getAttrType(mt);
/* 214 */     IntervalCollection candidates = new IntervalCollection(this.m_Sett.getTarget());
/* 215 */     TargetSet candidateSet = new TargetSet(this.m_Schema, candidates);
/* 216 */     return this.m_Search.search(mainTarget, candidateSet);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 223 */   public static final String[] OPTION_ARGS = new String[] { "xval" };
/* 224 */   public static final int[] OPTION_ARITIES = new int[] { 0 };
/*     */   
/*     */   public int getNbMainArgs() {
/* 227 */     return 1;
/*     */   }
/*     */   
/*     */   public int[] getOptionArgArities() {
/* 231 */     return OPTION_ARITIES;
/*     */   }
/*     */   
/*     */   public String[] getOptionArgs() {
/* 235 */     return OPTION_ARGS;
/*     */   }
/*     */   
/*     */   public void showHelp() {}
/*     */   
/*     */   public void singleRun() {
/* 241 */     System.out.println("Starting single run");
/*     */     
/* 243 */     InitLearner();
/*     */     
/* 245 */     InitSearchAlgorithm();
/*     */ 
/*     */     
/* 248 */     ErrorOutput errOut = new ErrorOutput(this.m_Sett);
/*     */     try {
/* 250 */       errOut.writeHeader();
/* 251 */     } catch (IOException e) {
/*     */       
/* 253 */       e.printStackTrace();
/*     */     } 
/*     */     
/* 256 */     TargetSet trgset = search();
/*     */ 
/*     */     
/* 259 */     int mt = (new Integer(this.m_Sett.getMainTarget())).intValue() - 1;
/* 260 */     ClusAttrType mainTarget = this.m_Schema.getAttrType(mt);
/* 261 */     int errorIdx = mainTarget.getArrayIndex();
/*     */     
/* 263 */     int nbFolds = 20;
/* 264 */     this.m_Learner.initXVal(nbFolds);
/*     */     
/* 266 */     ArrayList<RowData[]> folds = (ArrayList)new ArrayList<>();
/* 267 */     for (int f = 0; f < nbFolds; f++) {
/* 268 */       folds.add(this.m_Learner.LearnModel(trgset, f));
/*     */     }
/*     */     
/* 271 */     double finalerror = Evaluator.getPearsonCorrelation(folds, errorIdx);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void XValRun() throws Exception {
/* 280 */     ErrorOutput errOut = new ErrorOutput(this.m_Sett);
/* 281 */     errOut.writeHeader();
/* 282 */     System.out.println("Starting XVal run");
/*     */ 
/*     */     
/* 285 */     XValRandomSelection m_XValSel = null;
/* 286 */     int nrFolds = 26;
/*     */     
/*     */     try {
/* 289 */       m_XValSel = new XValRandomSelection(this.m_Data.getNbRows(), nrFolds);
/* 290 */     } catch (ClusException e) {
/* 291 */       e.printStackTrace();
/*     */     } 
/*     */     
/* 294 */     int mt = (new Integer(this.m_Sett.getMainTarget())).intValue() - 1;
/* 295 */     ClusAttrType mainTarget = this.m_Schema.getAttrType(mt);
/* 296 */     int errorIdx = mainTarget.getArrayIndex();
/*     */     
/* 298 */     for (int i = 0; i < nrFolds; i++) {
/* 299 */       System.out.println("Outer XVAL fold " + (i + 1));
/* 300 */       XValSelection msel = new XValSelection((XValMainSelection)m_XValSel, i);
/* 301 */       RowData train = (RowData)this.m_Data.cloneData();
/* 302 */       RowData test = (RowData)train.select((ClusSelection)msel);
/*     */       
/* 304 */       System.out.println(test.getNbRows());
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 309 */       InitLearner(train);
/*     */       
/* 311 */       InitSearchAlgorithm();
/*     */ 
/*     */       
/* 314 */       Long d = Long.valueOf((new Date()).getTime());
/* 315 */       TargetSet searchResult = search();
/*     */ 
/*     */       
/* 318 */       this.m_Learner.setTestData(test);
/*     */ 
/*     */       
/* 321 */       RowData[] predictions = this.m_Learner.LearnModel(searchResult);
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
/* 334 */       Long new_d = Long.valueOf((new Date()).getTime());
/* 335 */       Long dif = Long.valueOf(new_d.longValue() - d.longValue());
/*     */       
/* 337 */       double error = 0.0D;
/* 338 */       String errorName = this.m_Sett.getError();
/* 339 */       if (errorName.equals("MSE")) {
/* 340 */         error = Evaluator.getMSE(predictions, errorIdx);
/*     */       }
/* 342 */       else if (errorName.equals("MisclassificationError")) {
/* 343 */         error = Evaluator.getMisclassificationError(predictions, errorIdx);
/*     */       } else {
/*     */         
/* 346 */         error = Evaluator.getPearsonCorrelation(predictions, errorIdx);
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 353 */       errOut.addFold(0, i, this.m_Learner.getName(), this.m_Search.getName(), Integer.toString(mt + 1), error, "\"" + searchResult.toString() + " \"", dif);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void YATSXValRun() throws Exception {
/* 363 */     ErrorOutput errOut = new ErrorOutput(this.m_Sett);
/* 364 */     errOut.writeHeader();
/* 365 */     System.out.println("Starting XVal run");
/*     */ 
/*     */     
/* 368 */     XValRandomSelection m_XValSel = null;
/* 369 */     int nrFolds = 500;
/*     */     
/*     */     try {
/* 372 */       m_XValSel = new XValRandomSelection(this.m_Data.getNbRows(), nrFolds);
/* 373 */     } catch (ClusException e) {
/* 374 */       e.printStackTrace();
/*     */     } 
/*     */     
/* 377 */     int mt = (new Integer(this.m_Sett.getMainTarget())).intValue() - 1;
/* 378 */     ClusAttrType mainTarget = this.m_Schema.getAttrType(mt);
/* 379 */     int errorIdx = mainTarget.getArrayIndex();
/*     */     
/* 381 */     for (int i = 0; i < nrFolds; i++) {
/* 382 */       System.out.println("Outer XVAL fold " + (i + 1));
/* 383 */       XValSelection msel = new XValSelection((XValMainSelection)m_XValSel, i);
/* 384 */       RowData train = (RowData)this.m_Data.cloneData();
/* 385 */       RowData test = (RowData)train.select((ClusSelection)msel);
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 390 */       InitLearner(train);
/*     */       
/* 392 */       InitSearchAlgorithm();
/*     */       
/* 394 */       Long d = Long.valueOf((new Date()).getTime());
/* 395 */       TargetSet searchResult = search();
/*     */ 
/*     */       
/* 398 */       this.m_Learner.setTestData(test);
/*     */       
/* 400 */       RowData[] predictions = this.m_Learner.LearnModel(searchResult);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 408 */       RowData pred = predictions[1];
/* 409 */       RowData xtr_train = (RowData)test.deepCloneData();
/* 410 */       for (int t = 0; t < pred.getNbRows(); t++) {
/* 411 */         DataTuple tp = pred.getTuple(t);
/* 412 */         double dp = mainTarget.getNumeric(tp);
/* 413 */         DataTuple clone = xtr_train.getTuple(t);
/* 414 */         ((NumericAttrType)mainTarget).setNumeric(clone, dp);
/*     */       } 
/*     */ 
/*     */ 
/*     */       
/* 419 */       RowData new_train = new RowData(train.getSchema(), train.getNbRows() + xtr_train.getNbRows());
/*     */       int j;
/* 421 */       for (j = 0; j < train.getNbRows(); j++) {
/* 422 */         new_train.setTuple(train.getTuple(j), j);
/*     */       }
/* 424 */       for (j = train.getNbRows(); j < train.getNbRows() + xtr_train.getNbRows(); j++) {
/* 425 */         new_train.setTuple(xtr_train.getTuple(j - train.getNbRows()), j);
/*     */       }
/*     */       
/* 428 */       InitLearner(xtr_train);
/* 429 */       this.m_Learner.setTestData(test);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 436 */       Long new_d = Long.valueOf((new Date()).getTime());
/* 437 */       Long dif = Long.valueOf(new_d.longValue() - d.longValue());
/*     */       
/* 439 */       double error = 0.0D;
/* 440 */       String errorName = this.m_Sett.getError();
/*     */       
/* 442 */       if (errorName.equals("RME")) {
/* 443 */         error = Evaluator.getRelativeError(predictions, errorIdx);
/* 444 */         System.out.println(error);
/*     */       }
/* 446 */       else if (errorName.equals("MSE")) {
/* 447 */         error = Evaluator.getMSE(predictions, errorIdx);
/*     */       }
/* 449 */       else if (errorName.equals("MisclassificationError")) {
/* 450 */         error = Evaluator.getMisclassificationError(predictions, errorIdx);
/*     */       } else {
/*     */         
/* 453 */         error = Evaluator.getPearsonCorrelation(predictions, errorIdx);
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 460 */       errOut.addFold(0, i, this.m_Learner.getName(), this.m_Search.getName(), Integer.toString(mt + 1), error, "\"" + searchResult.toString() + " \"", dif);
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
/*     */   public static void main(String[] args) throws Exception {
/* 478 */     Sit sit = getInstance();
/* 479 */     Settings sett = sit.getSettings();
/* 480 */     CMDLineArgs cargs = new CMDLineArgs(sit);
/* 481 */     cargs.process(args);
/* 482 */     if (cargs.getNbMainArgs() == 0) {
/* 483 */       sit.showHelp();
/* 484 */       System.out.println();
/* 485 */       System.out.println("Expected main argument");
/* 486 */       System.exit(0);
/*     */     } 
/* 488 */     if (cargs.allOK()) {
/* 489 */       sett.setDate(new Date());
/* 490 */       sett.setAppName(cargs.getMainArg(0));
/* 491 */       sit.initSettings(cargs);
/*     */     } else {
/*     */       
/* 494 */       System.err.println("Arguments not ok?!");
/*     */     } 
/* 496 */     sit.initialize();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 502 */     sit.m_SearchSelection = 1;
/* 503 */     sit.XValRun();
/*     */ 
/*     */ 
/*     */     
/* 507 */     System.out.println("Finished");
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\sit\Sit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */