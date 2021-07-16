/*     */ package addon.hmc.HMCNodeWiseModels.hmcnwmodels;
/*     */ 
/*     */ import addon.hmc.HMCAverageSingleClass.HMCAverageTreeModel;
/*     */ import addon.hmc.HMCAverageSingleClass.HSCPrediction;
/*     */ import clus.Clus;
/*     */ import clus.activelearning.algo.ClusActiveLearningAlgorithm;
/*     */ import clus.activelearning.algo.ClusActiveLearningAlgorithmHSC;
/*     */ import clus.activelearning.algo.ClusLabelInferingAlgorithm;
/*     */ import clus.activelearning.algo.ClusLabelPairFindingAlgorithm;
/*     */ import clus.activelearning.algoHSC.QueryByCommitteeHSC;
/*     */ import clus.activelearning.algoHSC.QueryByCommitteeHierarchyNoLevelHSC;
/*     */ import clus.activelearning.algoHSC.RandomSamplingHSC;
/*     */ import clus.activelearning.algoHSC.SSMALHSC;
/*     */ import clus.activelearning.algoHSC.UncertaintySamplingHCALHSC;
/*     */ import clus.activelearning.algoHSC.UncertaintySamplingHSC;
/*     */ import clus.algo.ClusInductionAlgorithmType;
/*     */ import clus.algo.tdidt.ClusDecisionTree;
/*     */ import clus.data.io.ARFFFile;
/*     */ import clus.data.rows.DataTuple;
/*     */ import clus.data.rows.RowData;
/*     */ import clus.data.rows.SparseDataTuple;
/*     */ import clus.data.rows.TupleIterator;
/*     */ import clus.data.type.ClusAttrType;
/*     */ import clus.data.type.ClusSchema;
/*     */ import clus.error.BinaryPredictionList;
/*     */ import clus.error.ROCAndPRCurve;
/*     */ import clus.ext.ensembles.ClusEnsembleClassifier;
/*     */ import clus.ext.hierarchical.ClassHierarchy;
/*     */ import clus.ext.hierarchical.ClassTerm;
/*     */ import clus.ext.hierarchical.ClassesAttrType;
/*     */ import clus.ext.hierarchical.ClassesTuple;
/*     */ import clus.ext.hierarchical.ClassesValue;
/*     */ import clus.ext.hierarchical.HierErrorMeasures;
/*     */ import clus.ext.hierarchical.WHTDStatistic;
/*     */ import clus.main.ClusOutput;
/*     */ import clus.main.ClusRun;
/*     */ import clus.main.ClusStatManager;
/*     */ import clus.main.ClusSummary;
/*     */ import clus.main.Settings;
/*     */ import clus.model.ClusModel;
/*     */ import clus.model.ClusModelInfo;
/*     */ import clus.model.processor.ClusModelProcessor;
/*     */ import clus.model.processor.PredictionWriter;
/*     */ import clus.statistic.ClusStatistic;
/*     */ import clus.util.ClusException;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Date;
/*     */ import java.util.HashSet;
/*     */ import jeans.util.cmdline.CMDLineArgs;
/*     */ import jeans.util.cmdline.CMDLineArgsProvider;
/*     */ 
/*     */ public class HSC implements CMDLineArgsProvider {
/*  79 */   private static String[] g_Options = new String[] { "forest", "active" };
/*     */   
/*  80 */   private static int[] g_OptionArities = new int[] { 0, 0 };
/*     */   
/*     */   protected Clus m_Clus;
/*     */   
/*     */   protected CMDLineArgs m_Cargs;
/*     */   
/*     */   protected double[] m_FTests;
/*     */   
/*     */   protected ClusSchema m_Schema;
/*     */   
/*     */   protected ClusSummary m_Summary;
/*     */   
/*     */   protected ClusStatManager m_StatManager;
/*     */   
/*     */   protected Settings m_Settings;
/*     */   
/*     */   private double[][][] m_PredProb;
/*     */   
/*     */   private ClusModel[] m_Models;
/*     */   
/*     */   private ROCAndPRCurve m_PooledCurve;
/*     */   
/*     */   private BinaryPredictionList m_Pooled;
/*     */   
/*     */   public void run(String[] args) throws IOException, ClusException, ClassNotFoundException, Exception {
/*  95 */     String[] arg = new String[args.length - 1];
/*  96 */     for (int i = 0, j = 0; i < args.length; i++) {
/*  97 */       if (!args[i].equals("-hsc")) {
/*  98 */         arg[j] = args[i];
/*  99 */         j++;
/*     */       } 
/*     */     } 
/* 102 */     this.m_Clus = new Clus();
/* 103 */     Settings sett = this.m_Clus.getSettings();
/* 104 */     this.m_Cargs = new CMDLineArgs(this);
/* 105 */     this.m_Cargs.process(arg);
/* 106 */     if (this.m_Cargs.allOK()) {
/*     */       ClusDecisionTree clusDecisionTree;
/* 107 */       sett.setDate(new Date());
/* 108 */       sett.setAppName(this.m_Cargs.getMainArg(0));
/* 110 */       this.m_Clus.initSettings(this.m_Cargs);
/* 112 */       if (this.m_Clus.getSettings().isEnsembleMode()) {
/* 113 */         ClusEnsembleClassifier clusEnsembleClassifier = new ClusEnsembleClassifier(this.m_Clus);
/* 114 */         this.m_Clus.getSettings().setEnsembleMode(true);
/*     */       } else {
/* 116 */         clusDecisionTree = new ClusDecisionTree(this.m_Clus);
/*     */       } 
/* 119 */       this.m_Clus.initializeActive(this.m_Cargs, (ClusInductionAlgorithmType)clusDecisionTree);
/* 120 */       activeRun();
/*     */     } else {
/* 122 */       System.out.println("m_Cargs nok");
/*     */     } 
/*     */   }
/*     */   
/*     */   private void activeRun() throws IOException, ClusException, ClassNotFoundException, Exception {
/*     */     RandomSamplingHSC randomSamplingHSC;
/*     */     UncertaintySamplingHSC uncertaintySamplingHSC;
/*     */     UncertaintySamplingHCALHSC uncertaintySamplingHCALHSC;
/*     */     SSMALHSC sSMALHSC;
/* 127 */     ClusActiveLearningAlgorithmHSC al = null;
/* 131 */     ClusLabelInferingAlgorithm lpa = null;
/* 133 */     Settings settings = this.m_Clus.getSettings();
/* 134 */     ClassHierarchy h = this.m_Clus.getStatManager().getHier();
/* 135 */     this.m_Models = new ClusModel[h.getTotal()];
/* 136 */     ClusRun cr = this.m_Clus.partitionData();
/* 137 */     this.m_Clus.getStatManager().computeTrainSetStat((RowData)cr.getTrainingSet());
/* 138 */     HashSet<String> newLabels = new HashSet<>();
/* 139 */     ClusLabelPairFindingAlgorithm lca = null;
/* 140 */     switch (settings.getActiveAlgorithm()) {
/*     */       case "Random":
/* 142 */         randomSamplingHSC = new RandomSamplingHSC(this.m_Clus, settings.getActiveAlgorithm());
/*     */         break;
/*     */       case "UncertaintySampling":
/* 145 */         uncertaintySamplingHSC = new UncertaintySamplingHSC(this.m_Clus, settings.getActiveAlgorithm());
/*     */         break;
/*     */       case "HCAL":
/* 148 */         if (this.m_Clus.getSettings().getOptimizingIterations() <= 0 || this.m_Clus.getSettings().getPopulationSize() <= 0)
/* 149 */           throw new ClusException("The algorithms UncertaintySamplingHCAL requires the parameters OptimizingIterations and PopulationSize to have higher value than 1"); 
/* 151 */         uncertaintySamplingHCALHSC = new UncertaintySamplingHCALHSC(this.m_Clus, settings.getActiveAlgorithm(), this.m_Clus.getSettings().getOptimizingIterations(), this.m_Clus.getSettings().getPopulationSize());
/*     */         break;
/*     */       case "QueryByCommittee":
/* 154 */         if (settings.isEnsembleMode()) {
/* 155 */           QueryByCommitteeHSC queryByCommitteeHSC = new QueryByCommitteeHSC(this.m_Clus, settings.getActiveAlgorithm());
/*     */           break;
/*     */         } 
/* 157 */         throw new ClusException("QueryByCommittee requires the Ensemble Mode, try setting -forest on the command line");
/*     */       case "QueryByCommitteeHierarchy":
/* 161 */         if (settings.isEnsembleMode()) {
/* 162 */           QueryByCommitteeHierarchyNoLevelHSC queryByCommitteeHierarchyNoLevelHSC = new QueryByCommitteeHierarchyNoLevelHSC(this.m_Clus, settings.getActiveAlgorithm());
/*     */           break;
/*     */         } 
/* 164 */         throw new ClusException("QueryByCommitteeHierarchy requires the Ensemble Mode, try setting -forest on the command line");
/*     */       case "SSMAL":
/* 169 */         if (settings.isEnsembleMode()) {
/* 171 */           if (Double.isNaN(settings.getAlpha()))
/* 172 */             throw new ClusException("SSMAL requires a valid value for alpha, got: " + settings.getAlpha()); 
/* 175 */           sSMALHSC = new SSMALHSC(this.m_Clus, settings.getActiveAlgorithm(), settings.getAlpha());
/*     */           break;
/*     */         } 
/* 178 */         throw new ClusException("SSMAL requires the Ensemble Mode, try setting -forest on the command line");
/*     */     } 
/* 184 */     this.m_Pooled = new BinaryPredictionList();
/* 185 */     this.m_PooledCurve = new ROCAndPRCurve(this.m_Pooled);
/* 187 */     induce((ClusActiveLearningAlgorithm)sSMALHSC, cr, newLabels);
/* 188 */     createOutput(cr, (ClusActiveLearningAlgorithm)sSMALHSC, lpa);
/* 189 */     boolean isDone = false;
/* 190 */     while (!isDone) {
/* 191 */       System.out.println("Iteration:" + sSMALHSC.getIterationCounter());
/* 195 */       getNewActiveDataset((ClusActiveLearningAlgorithmHSC)sSMALHSC, cr);
/* 196 */       newLabels = getQueriedLabels((ClusActiveLearningAlgorithm)sSMALHSC);
/* 197 */       cr = this.m_Clus.partitionData();
/* 198 */       this.m_Clus.getStatManager().computeTrainSetStat((RowData)cr.getTrainingSet());
/* 199 */       induce((ClusActiveLearningAlgorithm)sSMALHSC, cr, newLabels);
/* 200 */       cr.copyAllModelsMIs();
/* 201 */       createOutput(cr, (ClusActiveLearningAlgorithm)sSMALHSC, lpa);
/* 202 */       isDone = this.m_Clus.updateDone((ClusActiveLearningAlgorithm)sSMALHSC, lpa);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void getInferedDataset(ClusLabelInferingAlgorithm lpa, ClusRun cr) throws IOException, ClusException, ClassNotFoundException {
/* 208 */     RowData infered = lpa.inferHSC(this.m_Models, cr);
/* 209 */     this.m_Clus.getData().addOrReplace(infered);
/*     */   }
/*     */   
/*     */   private void resetProbs(ClusRun cr) throws ClusException, IOException {
/* 213 */     ClassHierarchy h = cr.getStatManager().getHier();
/* 214 */     this.m_PredProb = new double[2][][];
/* 215 */     for (int i = 0; i <= 1; i++) {
/* 217 */       int size = cr.getDataSet(i).getNbRows();
/* 218 */       this.m_PredProb[i] = new double[size][h.getTotal()];
/* 219 */       for (int k = 0; k < size; k++)
/* 220 */         Arrays.fill(this.m_PredProb[i][k], Double.MAX_VALUE); 
/*     */     } 
/*     */   }
/*     */   
/*     */   private void createOutput(ClusRun cr, ClusActiveLearningAlgorithm al, ClusLabelInferingAlgorithm lpa) throws IOException, ClusException, ClassNotFoundException {
/* 226 */     resetProbs(cr);
/* 227 */     HSCPrediction avgNodewise = new HSCPrediction(this.m_Clus, this.m_PredProb);
/* 228 */     avgNodewise.processModels(this.m_Clus, this.m_Models);
/* 230 */     WHTDStatistic wHTDStatistic = createTargetStat();
/* 231 */     restartPrinterWriters(cr, this.m_Clus.getSettings(), al.getIterationCounter(), this.m_Clus.getSettings().getActiveAlgorithm());
/* 233 */     wHTDStatistic.calcMean();
/* 234 */     Settings settings = this.m_Clus.getSettings();
/* 238 */     this.m_Pooled.sort();
/* 239 */     this.m_PooledCurve.computeCurves();
/* 240 */     this.m_PooledCurve.clear();
/* 242 */     ClusModelInfo def_model = cr.addModelInfo(0);
/* 243 */     def_model.setModel(ClusDecisionTree.induceDefault(cr));
/* 245 */     ClusModelInfo orig_model_inf = cr.addModelInfo(1);
/* 246 */     HMCAverageTreeModel orig_model = new HMCAverageTreeModel((ClusStatistic)wHTDStatistic, this.m_PredProb, avgNodewise.getNbModels(), avgNodewise.getTotalSize());
/* 248 */     cr.copyAllModelsMIs();
/* 249 */     orig_model_inf.setModel((ClusModel)orig_model);
/* 250 */     RowData train = (RowData)cr.getTrainingSet();
/* 251 */     train.addIndices();
/* 252 */     RowData test = cr.getTestSet();
/* 253 */     test.addIndices();
/* 255 */     if (settings.getWriteActiveTestErrors()) {
/* 256 */       orig_model.setDataSet(1);
/* 258 */       if (!settings.getWriteActiveTestPredictions())
/* 259 */         this.m_Clus.calcErrorWithoutPrinting((TupleIterator)cr.getTestSet().getIterator(), 1, cr, null); 
/* 262 */       String tr_name = settings.getAppNameWithSuffix() + ".iteration" + al.getIterationCounter() + ".test.hsc.errors.out";
/* 264 */       ClusOutput output = new ClusOutput(tr_name, this.m_Clus.getSchema(), settings);
/* 265 */       output.writeActiveOutput(cr, true, al, lpa, 0, -1.0D);
/* 266 */       output.close();
/*     */     } 
/* 268 */     if (settings.getWriteActiveTrainPredictions()) {
/* 269 */       orig_model.setDataSet(0);
/* 271 */       this.m_Clus.calcError((TupleIterator)((RowData)cr.getTrainingSet()).getIterator(), 0, cr);
/*     */     } 
/* 273 */     if (settings.getWriteActiveTrainErrors()) {
/* 274 */       orig_model.setDataSet(0);
/* 276 */       if (!settings.getWriteActiveTrainPredictions())
/* 277 */         this.m_Clus.calcErrorWithoutPrinting((TupleIterator)((RowData)cr.getTrainingSet()).getIterator(), 0, cr, null); 
/* 279 */       String tr_name = settings.getAppNameWithSuffix() + ".iteration" + al.getIterationCounter() + ".train.hsc.errors.out";
/* 281 */       ClusOutput output = new ClusOutput(tr_name, this.m_Clus.getSchema(), settings);
/* 282 */       this.m_Clus.calcExtraTrainingSetErrors(cr);
/* 283 */       output.writeActiveOutput(cr, true, al, lpa, 1, this.m_PooledCurve.getAreaPR());
/* 284 */       output.close();
/*     */     } 
/* 286 */     if (settings.getWriteQueriedInstances()) {
/* 287 */       String tr_name = settings.getAppNameWithSuffix() + ".iteration" + al.getIterationCounter() + ".queriedInstances.hsc.errors.out";
/* 289 */       if (al.getIterationCounter() > 0)
/* 290 */         ARFFFile.writeActiveArff(settings.getFileAbsoluteWriter(tr_name), this.m_Clus.getM_QueriedData()); 
/*     */     } 
/*     */   }
/*     */   
/*     */   private void getNewActiveDataset(ClusActiveLearningAlgorithmHSC al, ClusRun cr) throws ClusException, IOException, ClassNotFoundException {
/* 296 */     if (this.m_Clus.getSettings().getVerbose() > 0)
/* 297 */       System.out.println("Starting to sample iteration: " + al.getIterationCounter()); 
/* 299 */     this.m_Clus.setM_QueriedData(al.sample(this.m_Models));
/* 300 */     if (this.m_Clus.getSettings().getVerbose() > 0)
/* 301 */       System.out.println("Finished sampling iteration: " + al.getIterationCounter()); 
/* 303 */     if (this.m_Clus.getSettings().getPartialLabelling().booleanValue()) {
/* 304 */       this.m_Clus.getData().addOrReplace(this.m_Clus.getM_QueriedData());
/*     */     } else {
/* 306 */       this.m_Clus.getData().addDataTuples(this.m_Clus.getM_QueriedData());
/*     */     } 
/* 308 */     this.m_Clus.getM_QueriedData().getSchema().setRelationName(this.m_Clus.getSettings().getActiveAlgorithm() + "_iteration_" + al.getIterationCounter());
/*     */   }
/*     */   
/*     */   private HashSet<String> getQueriedLabels(ClusActiveLearningAlgorithm al) {
/* 312 */     HashSet<String> newLabels = al.getIteration().getNewLabels();
/* 313 */     return newLabels;
/*     */   }
/*     */   
/*     */   private HashSet<String> getInferedLabels(ClusLabelInferingAlgorithm lpa) {
/* 317 */     HashSet<String> inferedLabels = lpa.getIteration().getNewLabels();
/* 318 */     return inferedLabels;
/*     */   }
/*     */   
/*     */   public RowData getNodeData(RowData train, int nodeid, String nodeLabel) {
/* 322 */     ArrayList<DataTuple> selected = new ArrayList();
/* 323 */     for (int i = 0; i < train.getNbRows(); i++) {
/*     */       DataTuple tuple;
/* 325 */       if (this.m_Clus.getSchema().isSparse()) {
/* 326 */         SparseDataTuple sparseDataTuple = (SparseDataTuple)train.getTuple(i);
/*     */       } else {
/* 328 */         tuple = train.getTuple(i);
/*     */       } 
/* 330 */       ClassesTuple target = (ClassesTuple)tuple.getObjVal(0);
/* 331 */       if (nodeid == -1 || target.hasClass(nodeid) || (tuple.getOracleAnswer() != null && tuple.getOracleAnswer().queriedBefore(nodeLabel)))
/* 332 */         selected.add(tuple); 
/*     */     } 
/* 338 */     return new RowData(selected, train.getSchema());
/*     */   }
/*     */   
/*     */   public RowData createChildData(RowData nodeData, ClassesAttrType ctype, int childid, String parentLabel, String childLabel) throws ClusException {
/* 343 */     ClassHierarchy chier = ctype.getHier();
/* 344 */     ClassesValue one = new ClassesValue("1", ctype.getTable());
/* 345 */     chier.addClass(one);
/* 346 */     chier.initialize();
/* 347 */     one.addHierarchyIndices(chier);
/* 348 */     RowData childData = new RowData(ctype.getSchema(), nodeData.getNbRows());
/* 349 */     ArrayList<DataTuple> data = new ArrayList<>();
/* 350 */     for (int j = 0; j < nodeData.getNbRows(); j++) {
/*     */       DataTuple tuple;
/* 353 */       if (this.m_Clus.getSchema().isSparse()) {
/* 354 */         SparseDataTuple sparseDataTuple = (SparseDataTuple)nodeData.getTuple(j);
/*     */       } else {
/* 356 */         tuple = nodeData.getTuple(j);
/*     */       } 
/* 358 */       ClassesTuple target = (ClassesTuple)tuple.getObjVal(0);
/* 359 */       if (target.hasClass(childid)) {
/*     */         SparseDataTuple sparseDataTuple;
/* 360 */         ClassesTuple clss = new ClassesTuple(1);
/* 361 */         clss.addItem(new ClassesValue(one.getTerm()));
/* 362 */         DataTuple new_tuple = tuple.deepActiveCloneTuple();
/* 363 */         if (this.m_Clus.getSchema().isSparse())
/* 364 */           sparseDataTuple = (SparseDataTuple)new_tuple; 
/* 366 */         sparseDataTuple.setSchema(ctype.getSchema());
/* 367 */         sparseDataTuple.setObjectVal(clss, 0);
/* 368 */         childData.setTuple((DataTuple)sparseDataTuple, j);
/* 369 */         data.add(sparseDataTuple);
/* 373 */       } else if (tuple.getOracleAnswer() == null || (parentLabel.equals("R") && tuple.getOracleAnswer().queriedNegatively(childLabel)) || (tuple.getOracleAnswer().queriedPositively(parentLabel) && tuple.getOracleAnswer().queriedNegatively(childLabel))) {
/* 374 */         ClassesTuple clss = new ClassesTuple(0);
/* 375 */         DataTuple new_tuple = tuple.deepActiveCloneTuple();
/* 376 */         new_tuple.setSchema(ctype.getSchema());
/* 377 */         new_tuple.setObjectVal(clss, 0);
/* 378 */         childData.setTuple(new_tuple, j);
/* 379 */         data.add(new_tuple);
/*     */       } 
/*     */     } 
/* 385 */     return new RowData(data, ctype.getSchema());
/*     */   }
/*     */   
/*     */   public ClusSchema createChildSchema(ClusSchema oschema, ClassesAttrType ctype, String name) throws ClusException, IOException {
/* 389 */     ClusSchema cschema = new ClusSchema(name);
/* 390 */     for (int j = 0; j < oschema.getNbAttributes(); j++) {
/* 391 */       ClusAttrType atype = oschema.getAttrType(j);
/* 392 */       if (!(atype instanceof ClassesAttrType)) {
/* 393 */         ClusAttrType copy_atype = atype.cloneType();
/* 394 */         cschema.addAttrType(copy_atype);
/*     */       } 
/*     */     } 
/* 397 */     cschema.addAttrType((ClusAttrType)ctype);
/* 399 */     cschema.initializeSettings(this.m_Clus.getSettings());
/* 401 */     if (oschema.isSparse())
/* 402 */       cschema.setSparse(); 
/* 404 */     return cschema;
/*     */   }
/*     */   
/*     */   public void doOneNode(ClusActiveLearningAlgorithm al, ClassTerm node, ClassHierarchy hier, RowData train, HashSet<String> newLabels) throws ClusException, IOException {
/* 421 */     RowData nodeData = getNodeData(train, node.getIndex(), node.toPathString());
/* 422 */     String nodeName = node.toPathString("=");
/* 426 */     Clus nodeClus = new Clus();
/* 427 */     Settings sett = nodeClus.getSettings();
/* 428 */     for (int i = 0; i < node.getNbChildren(); i++) {
/* 429 */       ClassTerm child = (ClassTerm)node.getChild(i);
/* 430 */       if (newLabels.isEmpty() || newLabels.contains(child.toPathString())) {
/* 432 */         String childName = child.toPathString("=");
/* 433 */         ClassesAttrType ctype = new ClassesAttrType(nodeName + "-" + childName);
/* 434 */         ClusSchema cschema = createChildSchema(train.getSchema(), ctype, "REL-" + nodeName + "-" + childName);
/* 435 */         RowData childData = createChildData(nodeData, ctype, child.getIndex(), node.toPathString(), child.toPathString());
/* 438 */         sett.setDate(new Date());
/* 439 */         sett.setAppName(this.m_Cargs.getMainArg(0));
/* 440 */         nodeClus.initSettings(this.m_Cargs);
/* 441 */         nodeClus.getSettings().setEnsembleMode(true);
/* 442 */         ClusEnsembleClassifier clusEnsembleClassifier = new ClusEnsembleClassifier(nodeClus);
/* 443 */         nodeClus.recreateInduce(this.m_Cargs, (ClusInductionAlgorithmType)clusEnsembleClassifier, cschema, childData);
/* 444 */         ClusRun cr = new ClusRun(childData.cloneData(), nodeClus.getSummary());
/* 445 */         cr.copyTrainingData();
/* 446 */         nodeClus.initializeSummary((ClusInductionAlgorithmType)clusEnsembleClassifier);
/* 447 */         nodeClus.getStatManager().computeTrainSetStat((RowData)cr.getTrainingSet());
/* 449 */         nodeClus.induce(cr, (ClusInductionAlgorithmType)clusEnsembleClassifier);
/* 450 */         if (Settings.shouldEstimateOOB()) {
/* 451 */           HierErrorMeasures h = (HierErrorMeasures)cr.getModelInfo(0).getError(0).getError(0);
/* 453 */           this.m_Pooled.add(h.m_HSCPrediction);
/* 457 */           this.m_Models[child.getIndex()] = cr.getModel(2);
/*     */         } else {
/* 460 */           this.m_Models[child.getIndex()] = cr.getModel(1);
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public void computeRecursive(ClusActiveLearningAlgorithm al, ClassTerm node, ClassHierarchy hier, RowData train, boolean[] computed, HashSet<String> newLabels) throws ClusException, IOException {
/* 472 */     if (!computed[node.getIndex()]) {
/* 476 */       computed[node.getIndex()] = true;
/* 477 */       doOneNode(al, node, hier, train, newLabels);
/* 479 */       for (int i = 0; i < node.getNbChildren(); i++) {
/* 480 */         ClassTerm child = (ClassTerm)node.getChild(i);
/* 481 */         computeRecursive(al, child, hier, train, computed, newLabels);
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public void computeRecursiveRoot(ClusActiveLearningAlgorithm al, ClassTerm node, ClassHierarchy hier, RowData train, boolean[] computed, HashSet<String> newLabels) throws ClusException, IOException {
/* 487 */     doOneNode(al, node, hier, train, newLabels);
/* 488 */     for (int i = 0; i < node.getNbChildren(); i++) {
/* 489 */       ClassTerm child = (ClassTerm)node.getChild(i);
/* 490 */       computeRecursive(al, child, hier, train, computed, newLabels);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void induce(ClusActiveLearningAlgorithm al, ClusRun cr, HashSet<String> newLabels) throws IOException, ClusException, ClassNotFoundException {
/* 495 */     RowData train = (RowData)cr.getTrainingSet();
/* 496 */     ClusStatManager mgr = this.m_Clus.getStatManager();
/* 497 */     ClassHierarchy hier = mgr.getHier();
/* 498 */     ClassTerm root = hier.getRoot();
/* 499 */     boolean[] computed = new boolean[hier.getTotal()];
/* 500 */     computeRecursiveRoot(al, root, hier, train, computed, newLabels);
/*     */   }
/*     */   
/*     */   public WHTDStatistic createTargetStat() {
/* 504 */     return (WHTDStatistic)this.m_Clus.getStatManager().createStatistic(3);
/*     */   }
/*     */   
/*     */   public String[] getOptionArgs() {
/* 509 */     return g_Options;
/*     */   }
/*     */   
/*     */   public int[] getOptionArgArities() {
/* 514 */     return g_OptionArities;
/*     */   }
/*     */   
/*     */   public int getNbMainArgs() {
/* 519 */     return this.m_Clus.getNbMainArgs();
/*     */   }
/*     */   
/*     */   public void showHelp() {}
/*     */   
/*     */   public final void restartPrinterWriters(ClusRun cr, Settings m_Sett, int activeLearningIteration, String activeLearningAlgorithm) {
/* 527 */     cr.resetAllModelsMI();
/* 528 */     ClusModelInfo allmi = cr.getAllModelsMI();
/* 530 */     String ts_name = m_Sett.getAppNameWithSuffix() + ".iteration" + activeLearningIteration + ".hsc.pred.partial.test.out";
/* 532 */     allmi.addModelProcessor(1, (ClusModelProcessor)new PredictionWriter(ts_name, m_Sett, this.m_Clus
/* 533 */           .getStatManager().createStatistic(3)));
/* 536 */     allmi = cr.getAllModelsMI();
/* 538 */     String tr_name = m_Sett.getAppNameWithSuffix() + ".iteration" + activeLearningIteration + ".hsc.pred.partial.train.out";
/* 540 */     allmi.addModelProcessor(0, (ClusModelProcessor)new PredictionWriter(tr_name, m_Sett, this.m_Clus
/* 541 */           .getStatManager()
/* 542 */           .createStatistic(3)));
/*     */   }
/*     */   
/*     */   public static void main(String[] args) throws Exception {
/*     */     try {
/* 548 */       HSC m = new HSC();
/* 549 */       m.run(args);
/* 551 */     } catch (IOException io) {
/* 552 */       System.out.println("IO Error: " + io.getMessage());
/* 553 */     } catch (ClusException|ClassNotFoundException cl) {
/* 554 */       System.out.println("Error: " + cl.getMessage());
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\addon\hmc\HMCNodeWiseModels\hmcnwmodels\HSC.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */