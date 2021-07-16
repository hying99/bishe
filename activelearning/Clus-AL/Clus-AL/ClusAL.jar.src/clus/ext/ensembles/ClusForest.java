/*     */ package clus.ext.ensembles;
/*     */ 
/*     */ import clus.algo.rules.ClusRuleSet;
/*     */ import clus.algo.rules.ClusRulesFromTree;
/*     */ import clus.algo.tdidt.ClusNode;
/*     */ import clus.data.rows.DataTuple;
/*     */ import clus.data.rows.RowData;
/*     */ import clus.data.type.ClusAttrType;
/*     */ import clus.data.type.ClusSchema;
/*     */ import clus.ext.hierarchical.HierClassTresholdPruner;
/*     */ import clus.ext.hierarchical.HierSingleLabelStat;
/*     */ import clus.ext.hierarchical.WHTDStatistic;
/*     */ import clus.main.ClusRun;
/*     */ import clus.main.ClusStatManager;
/*     */ import clus.main.Settings;
/*     */ import clus.model.ClusModel;
/*     */ import clus.model.ClusModelInfo;
/*     */ import clus.model.processor.ClusEnsemblePredictionWriter;
/*     */ import clus.statistic.ClassificationStat;
/*     */ import clus.statistic.ClusStatistic;
/*     */ import clus.statistic.GeneticDistanceStat;
/*     */ import clus.statistic.RegressionStat;
/*     */ import clus.statistic.RegressionStatBase;
/*     */ import clus.statistic.StatisticPrintInfo;
/*     */ import clus.util.ClusException;
/*     */ import java.io.File;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintWriter;
/*     */ import java.io.Serializable;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import jeans.util.MyArray;
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
/*     */ public class ClusForest
/*     */   implements ClusModel, Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   ArrayList m_Forest;
/*     */   ClusStatistic m_Stat;
/*     */   boolean m_PrintModels;
/*     */   String m_AttributeList;
/*     */   String m_AppName;
/*     */   
/*     */   public ClusForest() {
/*  63 */     this.m_Forest = new ArrayList();
/*     */   }
/*     */ 
/*     */   
/*     */   public ClusForest(ClusStatManager statmgr) {
/*  68 */     this.m_Forest = new ArrayList();
/*  69 */     if (ClusStatManager.getMode() == 0) {
/*  70 */       this.m_Stat = (ClusStatistic)new ClassificationStat(statmgr.getSchema().getNominalAttrUse(3));
/*  71 */     } else if (ClusStatManager.getMode() == 1) {
/*  72 */       this.m_Stat = (ClusStatistic)new RegressionStat(statmgr.getSchema().getNumericAttrUse(3));
/*  73 */     } else if (ClusStatManager.getMode() == 2) {
/*  74 */       if (statmgr.getSettings().getHierSingleLabel()) {
/*  75 */         this.m_Stat = (ClusStatistic)new HierSingleLabelStat(statmgr.getHier(), statmgr.getCompatibility());
/*     */       } else {
/*  77 */         this.m_Stat = (ClusStatistic)new WHTDStatistic(statmgr.getHier(), statmgr.getCompatibility());
/*     */       } 
/*  79 */     } else if (ClusStatManager.getMode() == 7) {
/*  80 */       this.m_Stat = (ClusStatistic)new GeneticDistanceStat(statmgr.getSchema().getNominalAttrUse(3));
/*     */     } else {
/*  82 */       System.err.println(getClass().getName() + " initForest(): Error initializing the statistic " + ClusStatManager.getMode());
/*     */     } 
/*  84 */     this.m_AppName = statmgr.getSettings().getFileAbsolute(statmgr.getSettings().getAppName());
/*  85 */     this.m_AttributeList = "";
/*  86 */     ClusAttrType[] cat = ClusSchema.vectorToAttrArray(statmgr.getSchema().collectAttributes(1, -1));
/*  87 */     if (statmgr.getSettings().isOutputPythonModel()) {
/*  88 */       for (int ii = 0; ii < cat.length - 1; ii++) {
/*  89 */         this.m_AttributeList = this.m_AttributeList.concat(cat[ii].getName() + ", ");
/*     */       }
/*  91 */       this.m_AttributeList = this.m_AttributeList.concat(cat[cat.length - 1].getName());
/*     */     } 
/*     */   }
/*     */   
/*     */   public void addModelToForest(ClusModel model) {
/*  96 */     this.m_Forest.add(model);
/*     */   }
/*     */ 
/*     */   
/*     */   public void applyModelProcessors(DataTuple tuple, MyArray mproc) throws IOException {
/* 101 */     for (int i = 0; i < this.m_Forest.size(); i++) {
/* 102 */       ClusModel model = this.m_Forest.get(i);
/* 103 */       model.applyModelProcessors(tuple, mproc);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void attachModel(HashMap table) throws ClusException {
/* 109 */     for (int i = 0; i < this.m_Forest.size(); i++) {
/* 110 */       ClusModel model = this.m_Forest.get(i);
/* 111 */       model.attachModel(table);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public int getID() {
/* 117 */     return 0;
/*     */   }
/*     */   
/*     */   public String getModelInfo() {
/* 121 */     int sumOfLeaves = 0;
/* 122 */     for (int i = 0; i < getNbModels(); i++) {
/* 123 */       sumOfLeaves += ((ClusNode)getModel(i)).getNbLeaves();
/*     */     }
/*     */     
/* 126 */     int sumOfNodes = 0;
/* 127 */     for (int j = 0; j < getNbModels(); j++) {
/* 128 */       sumOfNodes += ((ClusNode)getModel(j)).getNbNodes();
/*     */     }
/*     */     
/* 131 */     String result = "FOREST with " + getNbModels() + " models (Total nodes: " + sumOfNodes + " and leaves: " + sumOfLeaves + ")\n";
/* 132 */     for (int k = 0; k < getNbModels(); k++) {
/* 133 */       result = result + "\t Model " + (k + 1) + ": " + getModel(k).getModelInfo() + "\n";
/*     */     }
/* 135 */     return result;
/*     */   }
/*     */   
/*     */   public int getModelSize() {
/* 139 */     int nbNodes = 0;
/* 140 */     for (int i = 0; i < this.m_Forest.size(); i++) {
/* 141 */       ClusNode node = this.m_Forest.get(i);
/* 142 */       nbNodes += node.getNbNodes();
/*     */     } 
/* 144 */     return nbNodes;
/*     */   }
/*     */ 
/*     */   
/*     */   public ClusModel getModelByIndex(int treeIndex) {
/* 149 */     return this.m_Forest.get(treeIndex);
/*     */   }
/*     */ 
/*     */   
/*     */   public ClusStatistic predictWeighted(DataTuple tuple) {
/* 154 */     if (ClusOOBErrorEstimate.isOOBCalculation()) {
/* 155 */       return predictWeightedOOB(tuple);
/*     */     }
/* 157 */     if (!ClusEnsembleInduce.m_OptMode) {
/* 158 */       return predictWeightedStandard(tuple);
/*     */     }
/*     */     
/* 161 */     return predictWeightedOpt(tuple);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public double[] getVarianceAcrossForest(DataTuple tuple) {
/* 167 */     double[][] predictionsPerModel = new double[this.m_Forest.size()][];
/* 168 */     for (int i = 0; i < this.m_Forest.size(); i++) {
/* 169 */       ClusModel model = this.m_Forest.get(i);
/* 170 */       WHTDStatistic a = (WHTDStatistic)model.predictWeighted(tuple);
/* 171 */       double[] arrayOfDouble = a.getNumericPred();
/* 172 */       predictionsPerModel[i] = arrayOfDouble;
/*     */     } 
/* 174 */     double[][] transposedMatrix = transposeMatrix(predictionsPerModel);
/* 175 */     double[] variance = getLabelsVariance(transposedMatrix);
/* 176 */     return variance;
/*     */   }
/*     */ 
/*     */   
/*     */   public double getLabelVariance(double[] predictionsProbabilities) {
/* 181 */     double mean = getLabelsMean(predictionsProbabilities);
/* 182 */     double rowSum = 0.0D;
/* 183 */     for (int j = 0; j < predictionsProbabilities.length; j++) {
/* 184 */       rowSum += Math.pow(predictionsProbabilities[j] - mean, 2.0D);
/*     */     }
/* 186 */     rowSum = Math.sqrt(rowSum) / predictionsProbabilities.length;
/*     */     
/* 188 */     double variance = rowSum;
/* 189 */     return variance;
/*     */   }
/*     */   
/*     */   public double[][] transposeMatrix(double[][] matrix) {
/* 193 */     double[][] transposed = new double[(matrix[0]).length][matrix.length];
/* 194 */     for (int i = 0; i < matrix.length; i++) {
/* 195 */       for (int j = 0; j < (matrix[i]).length; j++) {
/* 196 */         transposed[j][i] = matrix[i][j];
/*     */       }
/*     */     } 
/* 199 */     return transposed;
/*     */   }
/*     */   
/*     */   public double[] getLabelsVariance(double[][] predictionsProbabilities) {
/* 203 */     double[] variance = new double[predictionsProbabilities.length];
/*     */     
/* 205 */     for (int i = 0; i < predictionsProbabilities.length; i++) {
/* 206 */       double mean = getLabelsMean(predictionsProbabilities[i]);
/*     */       
/* 208 */       double rowSum = 0.0D;
/* 209 */       for (int j = 0; j < (predictionsProbabilities[i]).length; j++)
/*     */       {
/*     */         
/* 212 */         rowSum += Math.pow(predictionsProbabilities[i][j] - mean, 2.0D);
/*     */       }
/*     */       
/* 215 */       rowSum = Math.sqrt(rowSum) / (predictionsProbabilities[0]).length;
/*     */       
/* 217 */       variance[i] = rowSum;
/*     */     } 
/*     */     
/* 220 */     return variance;
/*     */   }
/*     */   
/*     */   public double getLabelsMean(double[] labels) {
/* 224 */     double sum = 0.0D;
/*     */     
/* 226 */     for (int i = 0; i < labels.length; ) { sum += labels[i]; i++; }
/*     */ 
/*     */     
/* 229 */     return sum / labels.length;
/*     */   }
/*     */ 
/*     */   
/*     */   public double getVarianceAcrossForestHSC(DataTuple tuple) {
/* 234 */     double[] predictionsPerModel = new double[this.m_Forest.size()];
/* 235 */     for (int i = 0; i < this.m_Forest.size(); i++) {
/* 236 */       ClusModel model = this.m_Forest.get(i);
/* 237 */       WHTDStatistic a = (WHTDStatistic)model.predictWeighted(tuple);
/* 238 */       double[] arrayOfDouble = a.getNumericPred();
/* 239 */       predictionsPerModel[i] = arrayOfDouble[0];
/*     */     } 
/* 241 */     double variance = getLabelVariance(predictionsPerModel);
/* 242 */     return variance;
/*     */   }
/*     */ 
/*     */   
/*     */   public ClusStatistic predictWeightedStandard(DataTuple tuple) {
/* 247 */     ArrayList<ClusStatistic> votes = new ArrayList();
/* 248 */     for (int i = 0; i < this.m_Forest.size(); i++) {
/* 249 */       ClusModel model = this.m_Forest.get(i);
/* 250 */       votes.add(model.predictWeighted(tuple));
/*     */     } 
/*     */     
/* 253 */     this.m_Stat.vote(votes);
/* 254 */     ClusEnsemblePredictionWriter.setVotes(votes);
/* 255 */     return this.m_Stat;
/*     */   }
/*     */ 
/*     */   
/*     */   public ClusStatistic predictWeightedOOB(DataTuple tuple) {
/* 260 */     if (ClusEnsembleInduce.m_Mode == 2 || ClusEnsembleInduce.m_Mode == 1) {
/* 261 */       return predictWeightedOOBRegressionHMC(tuple);
/*     */     }
/* 263 */     if (ClusEnsembleInduce.m_Mode == 0) {
/* 264 */       return predictWeightedOOBClassification(tuple);
/*     */     }
/*     */     
/* 267 */     System.err.println(getClass().getName() + ".predictWeightedOOB(DataTuple) - Error in Setting the Mode");
/* 268 */     return null;
/*     */   }
/*     */   
/*     */   public ClusStatistic predictWeightedOOBRegressionHMC(DataTuple tuple) {
/* 272 */     double[] predictions = null;
/* 273 */     if (ClusOOBErrorEstimate.containsPredictionForTuple(tuple)) {
/* 274 */       predictions = ClusOOBErrorEstimate.getPredictionForRegressionHMCTuple(tuple);
/*     */     } else {
/* 276 */       System.err.println(getClass().getName() + ".predictWeightedOOBRegressionHMC(DataTuple) - Missing Prediction For Tuple");
/* 277 */       System.err.println("Tuple Hash = " + tuple.hashCode());
/*     */     } 
/* 279 */     this.m_Stat.reset();
/* 280 */     ((RegressionStatBase)this.m_Stat).m_Means = new double[predictions.length];
/* 281 */     for (int j = 0; j < predictions.length; j++) {
/* 282 */       ((RegressionStatBase)this.m_Stat).m_Means[j] = predictions[j];
/*     */     }
/* 284 */     if (ClusEnsembleInduce.m_Mode == 2) {
/* 285 */       this.m_Stat = this.m_Stat;
/*     */     } else {
/* 287 */       this.m_Stat = this.m_Stat;
/*     */     } 
/* 289 */     this.m_Stat.computePrediction();
/* 290 */     return this.m_Stat;
/*     */   }
/*     */   
/*     */   public ClusStatistic predictWeightedOOBClassification(DataTuple tuple) {
/* 294 */     double[][] predictions = (double[][])null;
/* 295 */     if (ClusOOBErrorEstimate.containsPredictionForTuple(tuple)) {
/* 296 */       predictions = ClusOOBErrorEstimate.getPredictionForClassificationTuple(tuple);
/*     */     } else {
/* 298 */       System.err.println(getClass().getName() + ".predictWeightedOOBClassification(DataTuple) - Missing Prediction For Tuple");
/* 299 */       System.err.println("Tuple Hash = " + tuple.hashCode());
/*     */     } 
/* 301 */     this.m_Stat.reset();
/*     */     
/* 303 */     ((ClassificationStat)this.m_Stat).m_ClassCounts = new double[predictions.length][];
/* 304 */     for (int m = 0; m < predictions.length; m++) {
/* 305 */       ((ClassificationStat)this.m_Stat).m_ClassCounts[m] = new double[(predictions[m]).length];
/* 306 */       for (int n = 0; n < (predictions[m]).length; n++) {
/* 307 */         ((ClassificationStat)this.m_Stat).m_ClassCounts[m][n] = predictions[m][n];
/*     */       }
/*     */     } 
/* 310 */     this.m_Stat.computePrediction();
/* 311 */     return this.m_Stat;
/*     */   }
/*     */   
/*     */   public ClusStatistic predictWeightedOpt(DataTuple tuple) {
/* 315 */     int position = ClusEnsembleInduceOptimization.locateTuple(tuple);
/* 316 */     int predlength = ClusEnsembleInduceOptimization.getPredictionLength(position);
/* 317 */     this.m_Stat.reset();
/* 318 */     if (ClusStatManager.getMode() == 1 || ClusStatManager.getMode() == 2) {
/* 319 */       ((RegressionStatBase)this.m_Stat).m_Means = new double[predlength];
/* 320 */       for (int i = 0; i < predlength; i++) {
/* 321 */         ((RegressionStatBase)this.m_Stat).m_Means[i] = ClusEnsembleInduceOptimization.getPredictionValue(position, i);
/*     */       }
/* 323 */       this.m_Stat.computePrediction();
/* 324 */       return this.m_Stat;
/*     */     } 
/* 326 */     if (ClusStatManager.getMode() == 0) {
/* 327 */       ((ClassificationStat)this.m_Stat).m_ClassCounts = new double[predlength][];
/* 328 */       for (int j = 0; j < predlength; j++) {
/* 329 */         ((ClassificationStat)this.m_Stat).m_ClassCounts[j] = ClusEnsembleInduceOptimization.getPredictionValueClassification(position, j);
/*     */       }
/* 331 */       this.m_Stat.computePrediction();
/* 332 */       for (int k = 0; k < this.m_Stat.getNbAttributes(); k++) {
/* 333 */         ((ClassificationStat)this.m_Stat).m_SumWeights[k] = 1.0D;
/*     */       }
/*     */       
/* 336 */       return this.m_Stat;
/*     */     } 
/* 338 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public void printModel(PrintWriter wrt) {
/* 343 */     if (Settings.isPrintEnsembleModels()) {
/*     */       
/* 345 */       for (int i = 0; i < this.m_Forest.size(); i++) {
/* 346 */         ClusModel model = this.m_Forest.get(i);
/* 347 */         if (this.m_PrintModels) {
/* 348 */           thresholdToModel(i, getThreshold());
/*     */         }
/* 350 */         wrt.write("Model " + (i + 1) + ": \n");
/* 351 */         wrt.write("\n");
/* 352 */         model.printModel(wrt);
/* 353 */         wrt.write("\n");
/*     */       } 
/*     */     } else {
/* 356 */       wrt.write("Forest with " + getNbModels() + " models\n");
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void printModel(PrintWriter wrt, StatisticPrintInfo info) {
/* 363 */     if (Settings.isPrintEnsembleModels()) {
/*     */       
/* 365 */       for (int i = 0; i < this.m_Forest.size(); i++) {
/* 366 */         ClusModel model = this.m_Forest.get(i);
/* 367 */         if (this.m_PrintModels) {
/* 368 */           thresholdToModel(i, getThreshold());
/*     */         }
/* 370 */         wrt.write("Model " + (i + 1) + ": \n");
/* 371 */         wrt.write("\n");
/* 372 */         model.printModel(wrt);
/* 373 */         wrt.write("\n");
/*     */       } 
/*     */     } else {
/* 376 */       wrt.write("Forest with " + getNbModels() + " models\n");
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void printModelAndExamples(PrintWriter wrt, StatisticPrintInfo info, RowData examples) {
/* 383 */     for (int i = 0; i < this.m_Forest.size(); i++) {
/* 384 */       ClusModel model = this.m_Forest.get(i);
/* 385 */       model.printModelAndExamples(wrt, info, examples);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void printModelToPythonScript(PrintWriter wrt) {
/* 390 */     printForestToPython();
/*     */   }
/*     */ 
/*     */   
/*     */   public void printForestToPython() {
/*     */     try {
/* 396 */       File pyscript = new File(this.m_AppName + "_models.py");
/* 397 */       PrintWriter wrtr = new PrintWriter(new FileOutputStream(pyscript));
/* 398 */       wrtr.println("# Python code of the trees in the ensemble");
/* 399 */       wrtr.println();
/* 400 */       for (int i = 0; i < this.m_Forest.size(); i++) {
/* 401 */         ClusModel model = this.m_Forest.get(i);
/* 402 */         wrtr.println("#Model " + (i + 1));
/* 403 */         wrtr.println("def clus_tree_" + (i + 1) + "( " + this.m_AttributeList + " ):");
/* 404 */         model.printModelToPythonScript(wrtr);
/* 405 */         wrtr.println();
/*     */       } 
/* 407 */       wrtr.flush();
/* 408 */       wrtr.close();
/* 409 */       System.out.println("Model to Python Code written to: " + pyscript.getName());
/* 410 */     } catch (IOException e) {
/* 411 */       System.err.println(getClass().getName() + ".printForestToPython(): Error while writing models to python script");
/* 412 */       e.printStackTrace();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void printModelToQuery(PrintWriter wrt, ClusRun cr, int starttree, int startitem, boolean ex) {
/* 419 */     for (int i = 0; i < this.m_Forest.size(); i++) {
/* 420 */       ClusModel model = this.m_Forest.get(i);
/* 421 */       model.printModelToQuery(wrt, cr, starttree, startitem, ex);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public ClusModel prune(int prunetype) {
/* 427 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void retrieveStatistics(ArrayList list) {}
/*     */ 
/*     */   
/*     */   public void showForest() {
/* 436 */     for (int i = 0; i < this.m_Forest.size(); i++) {
/* 437 */       System.out.println("***************************");
/* 438 */       ClusModel model = this.m_Forest.get(i);
/* 439 */       ((ClusNode)model).printTree();
/* 440 */       System.out.println("***************************");
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ClusModel getModel(int idx) {
/* 450 */     return this.m_Forest.get(idx);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getNbModels() {
/* 458 */     return this.m_Forest.size();
/*     */   }
/*     */   
/*     */   public ClusStatistic getStat() {
/* 462 */     return this.m_Stat;
/*     */   }
/*     */   
/*     */   public void setStat(ClusStatistic stat) {
/* 466 */     this.m_Stat = stat;
/*     */   }
/*     */   
/*     */   public void thresholdToModel(int model_nb, double threshold) {
/*     */     try {
/* 471 */       HierClassTresholdPruner pruner = new HierClassTresholdPruner(null);
/* 472 */       pruner.pruneRecursive((ClusNode)getModel(model_nb), threshold);
/* 473 */     } catch (ClusException e) {
/* 474 */       System.err.println(getClass().getName() + " thresholdToModel(): Error while applying threshold " + threshold + " to model " + model_nb);
/* 475 */       e.printStackTrace();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ArrayList getModels() {
/* 483 */     return this.m_Forest;
/*     */   }
/*     */   
/*     */   public void setModels(ArrayList models) {
/* 487 */     this.m_Forest = models;
/*     */   }
/*     */ 
/*     */   
/*     */   public ClusForest cloneForestWithThreshold(double threshold) {
/* 492 */     ClusForest clone = new ClusForest();
/* 493 */     clone.setModels(getModels());
/* 494 */     WHTDStatistic stat = (WHTDStatistic)getStat().cloneStat();
/* 495 */     stat.copyAll(getStat());
/* 496 */     stat.setThreshold(threshold);
/* 497 */     clone.setStat((ClusStatistic)stat);
/* 498 */     return clone;
/*     */   }
/*     */   
/*     */   public void setPrintModels(boolean print) {
/* 502 */     this.m_PrintModels = print;
/*     */   }
/*     */   
/*     */   public boolean isPrintModels() {
/* 506 */     return this.m_PrintModels;
/*     */   }
/*     */   
/*     */   public double getThreshold() {
/* 510 */     return ((WHTDStatistic)getStat()).getThreshold();
/*     */   }
/*     */   
/*     */   public void removeModels() {
/* 514 */     this.m_Forest.clear();
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
/*     */   public void convertToRules(ClusRun cr, boolean addOnlyUnique) {
/* 533 */     ClusRulesFromTree treeTransform = new ClusRulesFromTree(true, cr.getStatManager().getSettings().rulesFromTree());
/* 534 */     ClusRuleSet ruleSet = new ClusRuleSet(cr.getStatManager());
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 539 */     for (int iTree = 0; iTree < getNbModels(); iTree++) {
/*     */       
/* 541 */       ClusNode treeRootNode = (ClusNode)getModel(iTree);
/*     */ 
/*     */ 
/*     */       
/* 545 */       ruleSet.addRuleSet(treeTransform.constructRules(treeRootNode, cr
/* 546 */             .getStatManager()), addOnlyUnique);
/*     */     } 
/*     */     
/* 549 */     ruleSet.addDataToRules((RowData)cr.getTrainingSet());
/*     */     
/* 551 */     ClusModelInfo rules_info = cr.addModelInfo("Rules-" + cr
/* 552 */         .getModelInfo(1).getName());
/* 553 */     rules_info.setModel((ClusModel)ruleSet);
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\ext\ensembles\ClusForest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */