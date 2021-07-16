/*      */ package clus.main;
/*      */ 
/*      */ import clus.data.type.ClusAttrType;
/*      */ import clus.data.type.ClusSchema;
/*      */ import clus.data.type.IntegerAttrType;
/*      */ import clus.ext.hierarchical.ClassesValue;
/*      */ import clus.heuristic.FTest;
/*      */ import clus.statistic.StatisticPrintInfo;
/*      */ import clus.util.ClusException;
/*      */ import java.io.File;
/*      */ import java.io.FileNotFoundException;
/*      */ import java.io.FileOutputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.OutputStreamWriter;
/*      */ import java.io.PrintWriter;
/*      */ import java.io.Serializable;
/*      */ import java.util.Arrays;
/*      */ import java.util.Date;
/*      */ import jeans.io.ini.INIFile;
/*      */ import jeans.io.ini.INIFileBool;
/*      */ import jeans.io.ini.INIFileDouble;
/*      */ import jeans.io.ini.INIFileInt;
/*      */ import jeans.io.ini.INIFileNode;
/*      */ import jeans.io.ini.INIFileNominal;
/*      */ import jeans.io.ini.INIFileNominalOrDoubleOrVector;
/*      */ import jeans.io.ini.INIFileNominalOrIntOrVector;
/*      */ import jeans.io.ini.INIFileSection;
/*      */ import jeans.io.ini.INIFileString;
/*      */ import jeans.io.ini.INIFileStringOrDouble;
/*      */ import jeans.io.ini.INIFileStringOrInt;
/*      */ import jeans.io.range.IntRangeCheck;
/*      */ import jeans.io.range.ValueCheck;
/*      */ import jeans.resource.ResourceInfo;
/*      */ import jeans.util.FileUtil;
/*      */ import jeans.util.StringUtils;
/*      */ import jeans.util.cmdline.CMDLineArgs;
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
/*      */ public class Settings
/*      */   implements Serializable
/*      */ {
/*      */   public static final long SERIAL_VERSION_ID = 1L;
/*      */   public static final long serialVersionUID = 1L;
/*   58 */   protected INIFile m_Ini = new INIFile();
/*      */ 
/*      */   
/*      */   public static final String DEFAULT = "Default";
/*      */ 
/*      */   
/*      */   public static final String NONE = "None";
/*      */ 
/*      */   
/*   67 */   public static final String[] NONELIST = new String[] { "None" };
/*   68 */   public static final String[] EMPTY = new String[0];
/*   69 */   public static final String[] INFINITY = new String[] { "Infinity" };
/*      */   public static final String INFINITY_STRING = "Infinity";
/*      */   public static final int INFINITY_VALUE = 0;
/*   72 */   public static final double[] FOUR_ONES = new double[] { 1.0D, 1.0D, 1.0D, 1.0D };
/*      */ 
/*      */   
/*      */   protected Date m_Date;
/*      */ 
/*      */   
/*      */   protected String m_AppName;
/*      */   
/*      */   protected String m_DirName;
/*      */   
/*   82 */   protected String m_Suffix = "";
/*      */   
/*   84 */   public static int VERBOSE = 1; public static boolean EXACT_TIME = false; protected INIFileInt m_Verbose; protected INIFileNominal m_Compatibility; protected INIFileString m_RandomSeed;
/*      */   protected INIFileNominal m_ResourceInfoLoaded;
/*      */   
/*      */   public Date getDate() {
/*   88 */     return this.m_Date;
/*      */   }
/*      */   
/*      */   public void setDate(Date date) {
/*   92 */     this.m_Date = date;
/*      */   }
/*      */   
/*      */   public String getAppName() {
/*   96 */     return this.m_AppName;
/*      */   }
/*      */   
/*      */   public String getAppNameWithSuffix() {
/*  100 */     return this.m_AppName + this.m_Suffix;
/*      */   }
/*      */   
/*      */   public void setSuffix(String suffix) {
/*  104 */     this.m_Suffix = suffix;
/*      */   }
/*      */   
/*      */   public void setAppName(String file) {
/*  108 */     file = StringUtils.removeSuffix(file, ".gz");
/*  109 */     file = StringUtils.removeSuffix(file, ".arff");
/*  110 */     file = StringUtils.removeSuffix(file, ".s");
/*  111 */     file = StringUtils.removeSuffix(file, ".");
/*  112 */     this.m_AppName = FileUtil.removePath(file);
/*  113 */     this.m_DirName = FileUtil.getPath(file);
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
/*      */   public static int enableVerbose(int talk) {
/*  127 */     int prev = VERBOSE;
/*  128 */     VERBOSE = talk;
/*  129 */     return prev;
/*      */   }
/*      */   
/*      */   public int getCompatibility() {
/*  133 */     return this.m_Compatibility.getValue();
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean hasRandomSeed() {
/*  138 */     return !StringUtils.unCaseCompare(this.m_RandomSeed.getValue(), "None");
/*      */   }
/*      */   
/*      */   public int getRandomSeed() {
/*  142 */     return Integer.parseInt(this.m_RandomSeed.getValue());
/*      */   }
/*      */   
/*      */   public int getResourceInfoLoaded() {
/*  146 */     return this.m_ResourceInfoLoaded.getValue();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  154 */   public static final String[] COMPATIBILITY = new String[] { "CMB05", "MLJ08", "Latest" };
/*      */ 
/*      */   
/*      */   public static final int COMPATIBILITY_CMB05 = 0;
/*      */ 
/*      */   
/*      */   public static final int COMPATIBILITY_MLJ08 = 1;
/*      */ 
/*      */   
/*      */   public static final int COMPATIBILITY_LATEST = 2;
/*      */   
/*  165 */   public static final String[] RESOURCE_INFO_LOAD = new String[] { "Yes", "No", "Test" };
/*      */ 
/*      */   
/*      */   public static final int RESOURCE_INFO_LOAD_YES = 0;
/*      */ 
/*      */   
/*      */   public static final int RESOURCE_INFO_LOAD_NO = 1;
/*      */   
/*      */   public static final int RESOURCE_INFO_LOAD_TEST = 2;
/*      */   
/*  175 */   public static final String[] ACTIVE_ALGORITHM = new String[] { "Random", "UncertaintySampling", "HCAL", "QueryByCommittee", "QueryByCommitteeHierarchy", "SSMAL" };
/*      */   
/*      */   protected INIFileString m_ActiveData;
/*      */   
/*      */   protected INIFileDouble m_BudgetPerIteration;
/*      */   
/*      */   protected INIFileString m_ActiveAlgorithm;
/*      */   
/*      */   protected INIFileInt m_BatchSize;
/*      */   
/*      */   protected INIFileBool m_PartialLabelling;
/*      */   
/*      */   protected INIFileBool m_SemiPartialLabelling;
/*      */   
/*      */   protected INIFileBool m_InferNegativeLabels;
/*      */   
/*      */   protected INIFileBool m_InferPositiveLabels;
/*      */   
/*      */   protected INIFileBool m_WriteOOBError;
/*      */   
/*      */   protected INIFileString m_LabelInferingAlgorithm;
/*      */   
/*      */   protected INIFileString m_LabelPairFindingAlgorithm;
/*      */   
/*      */   protected INIFileInt m_Iteration;
/*      */   
/*      */   protected INIFileInt m_LabelInferingBatchSize;
/*      */   
/*      */   protected INIFileNominalOrDoubleOrVector m_LabelCost;
/*      */   protected INIFileBool m_WriteActiveTrainPredictions;
/*      */   protected INIFileBool m_WriteActiveTestPredictions;
/*      */   protected INIFileBool m_WriteActiveTrainErrors;
/*      */   protected INIFileBool m_WriteActiveTestErrors;
/*      */   protected INIFileBool m_WriteActiveOOBErrors;
/*      */   protected INIFileBool m_WriteQueriedInstances;
/*      */   protected INIFileBool m_WriteBudgetInformation;
/*      */   protected INIFileDouble m_Alpha;
/*      */   protected INIFileDouble m_Beta;
/*      */   protected INIFileDouble m_Sigma;
/*      */   protected INIFileInt m_MaxIterations;
/*      */   protected INIFileInt m_OptimizingIterations;
/*      */   protected INIFileInt m_PopulationSize;
/*      */   protected INIFileString m_DataFile;
/*      */   protected INIFileStringOrDouble m_TestSet;
/*      */   protected INIFileStringOrDouble m_PruneSet;
/*      */   protected INIFileStringOrInt m_PruneSetMax;
/*      */   protected INIFileStringOrInt m_XValFolds;
/*      */   protected INIFileBool m_RemoveMissingTarget;
/*      */   
/*      */   public boolean getWriteOOBError() {
/*  225 */     return this.m_WriteOOBError.getValue();
/*      */   }
/*      */   
/*      */   public void setWriteOOBError(boolean writeOOBError) {
/*  229 */     this.m_WriteOOBError.setValue(writeOOBError);
/*      */   }
/*      */   
/*      */   public int getPopulationSize() {
/*  233 */     return this.m_PopulationSize.getValue();
/*      */   }
/*      */   
/*      */   public void setPopulationSize(int populationSize) {
/*  237 */     this.m_PopulationSize.setValue(populationSize);
/*      */   }
/*      */   
/*      */   public int getOptimizingIterations() {
/*  241 */     return this.m_OptimizingIterations.getValue();
/*      */   }
/*      */   
/*      */   public void setOptimizingIterations(int iterations) {
/*  245 */     this.m_OptimizingIterations.setValue(iterations);
/*      */   }
/*      */   
/*      */   public int getMaxIterations() {
/*  249 */     return this.m_MaxIterations.getValue();
/*      */   }
/*      */   
/*      */   public void setMaxIterations(int iterations) {
/*  253 */     this.m_MaxIterations.setValue(iterations);
/*      */   }
/*      */   
/*      */   public double getAlpha() {
/*  257 */     return this.m_Alpha.getValue();
/*      */   }
/*      */   
/*      */   public void setAlpha(double alpha) {
/*  261 */     this.m_Alpha.setValue(alpha);
/*      */   }
/*      */   
/*      */   public double getBeta() {
/*  265 */     return this.m_Beta.getValue();
/*      */   }
/*      */   
/*      */   public void setBeta(double beta) {
/*  269 */     this.m_Beta.setValue(beta);
/*      */   }
/*      */   
/*      */   public double getSigma() {
/*  273 */     return this.m_Sigma.getValue();
/*      */   }
/*      */   
/*      */   public void setSigma(double sigma) {
/*  277 */     this.m_Sigma.setValue(sigma);
/*      */   }
/*      */   
/*      */   public int getIteration() {
/*  281 */     return this.m_Iteration.getValue();
/*      */   }
/*      */   
/*      */   public void setIteration(int iteration) {
/*  285 */     this.m_Iteration.setValue(iteration);
/*      */   }
/*      */   
/*      */   public String getLabelPairFindingAlgorithm() {
/*  289 */     return this.m_LabelPairFindingAlgorithm.getValue();
/*      */   }
/*      */   
/*      */   public void setLabelPairFindingAlgorithm(String labelPairFindingAlgorithm) {
/*  293 */     this.m_ActiveData.setValue(labelPairFindingAlgorithm);
/*      */   }
/*      */   
/*      */   public Boolean getPartialLabelling() {
/*  297 */     return Boolean.valueOf(this.m_PartialLabelling.getValue());
/*      */   }
/*      */   
/*      */   public void setPartialLabelling(boolean newValue) {
/*  301 */     this.m_PartialLabelling.setValue(newValue);
/*      */   }
/*      */   
/*      */   public Boolean getNegativeInfering() {
/*  305 */     return Boolean.valueOf(this.m_InferNegativeLabels.getValue());
/*      */   }
/*      */   
/*      */   public void setNegativeInfering(boolean value) {
/*  309 */     this.m_InferNegativeLabels.setValue(value);
/*      */   }
/*      */   
/*      */   public Boolean getPositiveInfering() {
/*  313 */     return Boolean.valueOf(this.m_InferPositiveLabels.getValue());
/*      */   }
/*      */   
/*      */   public void setPositiveInfering(boolean value) {
/*  317 */     this.m_InferPositiveLabels.setValue(value);
/*      */   }
/*      */ 
/*      */   
/*      */   public INIFileNominalOrDoubleOrVector getLabelCost() {
/*  322 */     return this.m_LabelCost;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getActiveDataFile() {
/*  330 */     return this.m_ActiveData.getValue();
/*      */   }
/*      */   
/*      */   public void setActiveDataFile(String datafile) {
/*  334 */     this.m_ActiveData.setValue(datafile);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public double getBudgetPerIteration() {
/*  345 */     return this.m_BudgetPerIteration.getValue();
/*      */   }
/*      */   
/*      */   public void setBudgetPerIteration(int newValue) {
/*  349 */     this.m_BudgetPerIteration.setValue(newValue);
/*      */   }
/*      */   
/*      */   public int getLabelInferingBatchSize() {
/*  353 */     return this.m_LabelInferingBatchSize.getValue();
/*      */   }
/*      */   
/*      */   public void setLabelInferingBatchSize(int newValue) {
/*  357 */     this.m_LabelInferingBatchSize.setValue(newValue);
/*      */   }
/*      */   
/*      */   public int getBatchSize() {
/*  361 */     return this.m_BatchSize.getValue();
/*      */   }
/*      */   
/*      */   public void setBatchSize(int newValue) {
/*  365 */     this.m_BatchSize.setValue(newValue);
/*      */   }
/*      */   
/*      */   public boolean getWriteActiveOOBError() {
/*  369 */     return this.m_WriteActiveOOBErrors.getValue();
/*      */   }
/*      */   
/*      */   public void setWriteActiveOOB(boolean newValue) {
/*  373 */     this.m_WriteActiveOOBErrors.setValue(newValue);
/*      */   }
/*      */   
/*      */   public boolean getWriteActiveTrainPredictions() {
/*  377 */     return this.m_WriteActiveTrainPredictions.getValue();
/*      */   }
/*      */   
/*      */   public void setWriteActiveTrainPredictions(boolean newValue) {
/*  381 */     this.m_WriteActiveTrainPredictions.setValue(newValue);
/*      */   }
/*      */   
/*      */   public boolean getWriteActiveTestPredictions() {
/*  385 */     return this.m_WriteActiveTestPredictions.getValue();
/*      */   }
/*      */   
/*      */   public void getWriteActiveTestPredictions(boolean newValue) {
/*  389 */     this.m_WriteActiveTestPredictions.setValue(newValue);
/*      */   }
/*      */   
/*      */   public boolean getWriteQueriedInstances() {
/*  393 */     return this.m_WriteQueriedInstances.getValue();
/*      */   }
/*      */   
/*      */   public void getWriteQueriedInstances(boolean newValue) {
/*  397 */     this.m_WriteQueriedInstances.setValue(newValue);
/*      */   }
/*      */   
/*      */   public boolean getWriteBudgetInformation() {
/*  401 */     return this.m_WriteBudgetInformation.getValue();
/*      */   }
/*      */   
/*      */   public boolean getWriteActiveTestErrors() {
/*  405 */     return this.m_WriteActiveTestErrors.getValue();
/*      */   }
/*      */   
/*      */   public boolean getWriteActiveTrainErrors() {
/*  409 */     return this.m_WriteActiveTrainErrors.getValue();
/*      */   }
/*      */   
/*      */   public boolean isActiveParametersOK() throws ClusException {
/*  413 */     boolean foundActiveLearningAlgorithm = false;
/*  414 */     if (!getPartialLabelling().booleanValue()) {
/*  415 */       throw new ClusException("PartialLabelling must be true");
/*      */     }
/*  417 */     if (getBudgetPerIteration() <= 0.0D) {
/*  418 */       throw new ClusException("Budget or BudgetPerIteration parameters require a positive integer value, got value = " + getBudgetPerIteration() + ". Try setting one of then to a positive value");
/*      */     }
/*      */     
/*  421 */     if (getBatchSize() <= 0) {
/*  422 */       throw new ClusException("Batchsize Variable requires a positive integer value, got value = " + getBatchSize());
/*      */     }
/*  424 */     for (String ACTIVE_ALGORITHM1 : ACTIVE_ALGORITHM) {
/*  425 */       if (ACTIVE_ALGORITHM1.equals(getActiveAlgorithm())) {
/*  426 */         foundActiveLearningAlgorithm = true;
/*      */         
/*      */         break;
/*      */       } 
/*      */     } 
/*  431 */     if (!foundActiveLearningAlgorithm) {
/*  432 */       throw new ClusException("Valid Active Learning Algorithms are " + Arrays.toString(ACTIVE_ALGORITHM) + ", got " + getActiveAlgorithm());
/*      */     }
/*  434 */     return true;
/*      */   }
/*      */   
/*      */   public String getLabelInferingAlgorithm() {
/*  438 */     return this.m_LabelInferingAlgorithm.getValue();
/*      */   }
/*      */   
/*      */   public void setLabelInferingAlgorithm(String algorithm) {
/*  442 */     this.m_LabelInferingAlgorithm.setValue(algorithm);
/*      */   }
/*      */   
/*      */   public String getActiveAlgorithm() {
/*  446 */     return this.m_ActiveAlgorithm.getValue();
/*      */   }
/*      */   
/*      */   public void setActiveAlgorithm(String algorithm) {
/*  450 */     this.m_ActiveData.setValue(algorithm);
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
/*  473 */   public static final String[] NORMALIZE_DATA_VALUES = new String[] { "None", "Numeric" }; public static final int NORMALIZE_DATA_NONE = 0; public static final int NORMALIZE_DATA_NUMERIC = 1; protected INIFileNominal m_NormalizeData; protected INIFileString m_ActiveTarget; protected INIFileString m_ActiveClustering; protected INIFileString m_ActiveDescriptive; protected INIFileString m_ActiveKey; protected INIFileString m_ActiveDisabled;
/*      */   protected INIFileNominalOrDoubleOrVector m_ActiveWeights;
/*      */   protected INIFileNominalOrDoubleOrVector m_ActiveClusteringWeights;
/*      */   protected INIFileBool m_ActiveReduceMemoryNominal;
/*      */   protected INIFileString m_Target;
/*      */   protected INIFileString m_Clustering;
/*      */   protected INIFileString m_Descriptive;
/*      */   protected INIFileString m_Key;
/*      */   protected INIFileString m_Disabled;
/*      */   protected INIFileNominalOrDoubleOrVector m_Weights;
/*      */   protected INIFileNominalOrDoubleOrVector m_ClusteringWeights;
/*      */   protected INIFileBool m_ReduceMemoryNominal;
/*      */   protected INIFileSection m_SectionSIT;
/*      */   protected INIFileString m_MainTarget;
/*      */   protected INIFileString m_Search;
/*      */   protected INIFileString m_Learner;
/*      */   protected INIFileBool m_Recursive;
/*      */   protected INIFileString m_Error;
/*      */   
/*      */   public String getDataFile() {
/*  493 */     return this.m_DataFile.getValue();
/*      */   }
/*      */   
/*      */   public boolean isNullFile() {
/*  497 */     return StringUtils.unCaseCompare(this.m_DataFile.getValue(), "None");
/*      */   }
/*      */   
/*      */   public void updateDataFile(String fname) {
/*  501 */     if (isNullFile()) {
/*  502 */       this.m_DataFile.setValue(fname);
/*      */     }
/*      */   }
/*      */   
/*      */   public String getTestFile() {
/*  507 */     return this.m_TestSet.getValue();
/*      */   }
/*      */   
/*      */   public boolean isNullTestFile() {
/*  511 */     return this.m_TestSet.isDoubleOrNull("None");
/*      */   }
/*      */   
/*      */   public String getPruneFile() {
/*  515 */     return this.m_PruneSet.getValue();
/*      */   }
/*      */   
/*      */   public boolean isNullPruneFile() {
/*  519 */     return this.m_PruneSet.isDoubleOrNull("None");
/*      */   }
/*      */   
/*      */   public double getTestProportion() {
/*  523 */     if (!this.m_TestSet.isDouble()) {
/*  524 */       return 0.0D;
/*      */     }
/*  526 */     return this.m_TestSet.getDoubleValue();
/*      */   }
/*      */   
/*      */   public double getPruneProportion() {
/*  530 */     if (!this.m_PruneSet.isDouble()) {
/*  531 */       return 0.0D;
/*      */     }
/*  533 */     return this.m_PruneSet.getDoubleValue();
/*      */   }
/*      */   
/*      */   public int getPruneSetMax() {
/*  537 */     if (this.m_PruneSetMax.isString("Infinity")) {
/*  538 */       return Integer.MAX_VALUE;
/*      */     }
/*  540 */     return this.m_PruneSetMax.getIntValue();
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean isNullXValFile() {
/*  545 */     return this.m_XValFolds.isIntOrNull("None");
/*      */   }
/*      */   
/*      */   public boolean isLOOXVal() {
/*  549 */     return this.m_XValFolds.isString("LOO");
/*      */   }
/*      */   
/*      */   public String getXValFile() {
/*  553 */     return this.m_XValFolds.getValue();
/*      */   }
/*      */   
/*      */   public int getXValFolds() {
/*  557 */     return this.m_XValFolds.getIntValue();
/*      */   }
/*      */   
/*      */   public void setXValFolds(int folds) {
/*  561 */     this.m_XValFolds.setIntValue(folds);
/*      */   }
/*      */   
/*      */   public boolean isRemoveMissingTarget() {
/*  565 */     return this.m_RemoveMissingTarget.getValue();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getNormalizeData() {
/*  572 */     return this.m_NormalizeData.getValue();
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
/*      */   public String getActiveTarget() {
/*  590 */     return this.m_ActiveTarget.getValue();
/*      */   }
/*      */   
/*      */   public void setActiveTarget(String str) {
/*  594 */     this.m_ActiveTarget.setValue(str);
/*      */   }
/*      */   
/*      */   public boolean isDefaultActiveTarget() {
/*  598 */     return StringUtils.unCaseCompare(this.m_ActiveTarget.getValue(), "Default");
/*      */   }
/*      */   
/*      */   public String getActiveClustering() {
/*  602 */     return this.m_ActiveClustering.getValue();
/*      */   }
/*      */   
/*      */   public void setActiveClustering(String str) {
/*  606 */     this.m_ActiveClustering.setValue(str);
/*      */   }
/*      */   
/*      */   public String getActiveDescriptive() {
/*  610 */     return this.m_Descriptive.getValue();
/*      */   }
/*      */   
/*      */   public void setActiveDescriptive(String str) {
/*  614 */     this.m_ActiveDescriptive.setValue(str);
/*      */   }
/*      */   
/*      */   public String getActiveDisabled() {
/*  618 */     return this.m_ActiveDisabled.getValue();
/*      */   }
/*      */   
/*      */   public void setActiveDisabled(String str) {
/*  622 */     this.m_ActiveDisabled.setValue(str);
/*      */   }
/*      */   
/*      */   public String getActiveKey() {
/*  626 */     return this.m_ActiveKey.getValue();
/*      */   }
/*      */   
/*      */   public void setActiveKey(String str) {
/*  630 */     this.m_ActiveKey.setValue(str);
/*      */   }
/*      */   
/*      */   public INIFileNominalOrDoubleOrVector getActiveClusteringWeights() {
/*  634 */     return this.m_ActiveClusteringWeights;
/*      */   }
/*      */   
/*      */   public boolean getActiveReduceMemoryNominalAttrs() {
/*  638 */     return this.m_ActiveReduceMemoryNominal.getValue();
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
/*      */   public String getTarget() {
/*  655 */     return this.m_Target.getValue();
/*      */   }
/*      */   
/*      */   public void setTarget(String str) {
/*  659 */     this.m_Target.setValue(str);
/*      */   }
/*      */   
/*      */   public boolean isNullTarget() {
/*  663 */     return StringUtils.unCaseCompare(this.m_Target.getValue(), "None");
/*      */   }
/*      */   
/*      */   public boolean isDefaultTarget() {
/*  667 */     return StringUtils.unCaseCompare(this.m_Target.getValue(), "Default");
/*      */   }
/*      */   
/*      */   public String getClustering() {
/*  671 */     return this.m_Clustering.getValue();
/*      */   }
/*      */   
/*      */   public void setClustering(String str) {
/*  675 */     this.m_Clustering.setValue(str);
/*      */   }
/*      */   
/*      */   public String getDescriptive() {
/*  679 */     return this.m_Descriptive.getValue();
/*      */   }
/*      */   
/*      */   public void setDescriptive(String str) {
/*  683 */     this.m_Descriptive.setValue(str);
/*      */   }
/*      */   
/*      */   public String getKey() {
/*  687 */     return this.m_Key.getValue();
/*      */   }
/*      */   
/*      */   public String getDisabled() {
/*  691 */     return this.m_Disabled.getValue();
/*      */   }
/*      */   
/*      */   public void setDisabled(String str) {
/*  695 */     this.m_Disabled.setValue(str);
/*      */   }
/*      */   
/*      */   public INIFileNominalOrDoubleOrVector getNormalizationWeights() {
/*  699 */     return this.m_Weights;
/*      */   }
/*      */   
/*      */   public boolean hasNonTrivialWeights() {
/*  703 */     for (int i = 0; i < this.m_Weights.getVectorLength(); i++) {
/*  704 */       if (this.m_Weights.isNominal(i))
/*  705 */         return true; 
/*  706 */       if (this.m_Weights.getDouble(i) != 1.0D) {
/*  707 */         return true;
/*      */       }
/*      */     } 
/*  710 */     return false;
/*      */   }
/*      */   
/*      */   public INIFileNominalOrDoubleOrVector getClusteringWeights() {
/*  714 */     return this.m_ClusteringWeights;
/*      */   }
/*      */   
/*      */   public boolean getReduceMemoryNominalAttrs() {
/*  718 */     return this.m_ReduceMemoryNominal.getValue();
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
/*      */   public String getError() {
/*  734 */     return this.m_Error.getValue();
/*      */   }
/*      */   
/*      */   public String getLearnerName() {
/*  738 */     return this.m_Learner.getValue();
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean getRecursive() {
/*  743 */     return this.m_Recursive.getValue();
/*      */   }
/*      */   
/*      */   public String getMainTarget() {
/*  747 */     return this.m_MainTarget.getValue();
/*      */   }
/*      */   
/*      */   public String getSearchName() {
/*  751 */     return this.m_Search.getValue();
/*      */   }
/*      */   
/*      */   public void setSearch(String b) {
/*  755 */     this.m_Search.setValue(b);
/*      */   }
/*      */   
/*      */   public void setMainTarget(String str) {
/*  759 */     this.m_MainTarget.setValue(str);
/*      */   }
/*      */   
/*      */   public void setSectionSITEnabled(boolean enable) {
/*  763 */     this.m_SectionSIT.setEnabled(enable);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  771 */   public static final String[] NORMALIZATIONS = new String[] { "Normalize" };
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int NORMALIZATION_DEFAULT = 0;
/*      */ 
/*      */ 
/*      */   
/*  779 */   public static final String[] NUM_NOM_TAR_NTAR_WEIGHTS = new String[] { "TargetWeight", "NonTargetWeight", "NumericWeight", "NominalWeight" }; public static final int TARGET_WEIGHT = 0; public static final int NON_TARGET_WEIGHT = 1; public static final int NUMERIC_WEIGHT = 2; public static final int NOMINAL_WEIGHT = 3; protected INIFileDouble m_MEstimate; protected INIFileDouble m_MinW; protected INIFileDouble m_MinKnownW; protected INIFileInt m_MinNbEx; protected INIFileString m_TuneFolds; protected INIFileNominalOrDoubleOrVector m_ClassWeight; protected INIFileBool m_NominalSubsetTests; protected INIFileString m_SyntacticConstrFile; protected INIFileNominalOrIntOrVector m_MaxSizeConstr; protected INIFileNominalOrDoubleOrVector m_MaxErrorConstr; protected INIFileInt m_SetsData; protected INIFileBool m_OutFoldErr; protected INIFileBool m_OutFoldData; protected INIFileBool m_OutFoldModels; protected INIFileBool m_OutTrainErr; protected INIFileBool m_OutValidErr;
/*      */   protected INIFileBool m_OutTestErr;
/*      */   protected INIFileBool m_ShowForest;
/*      */   protected INIFileBool m_ShowBrFreq;
/*      */   protected INIFileBool m_ShowUnknown;
/*      */   protected INIFileNominal m_ShowInfo;
/*      */   protected INIFileNominal m_ShowModels;
/*      */   protected INIFileBool m_PrintModelAndExamples;
/*      */   protected INIFileNominal m_WritePredictions;
/*      */   protected INIFileBool m_WriteErrorFile;
/*      */   protected INIFileBool m_ModelIDFiles;
/*      */   protected INIFileBool m_OutputPythonModel;
/*      */   protected INIFileBool m_OutputDatabaseQueries;
/*      */   protected INIFileBool m_WriteCurves;
/*      */   
/*      */   public double getMEstimate() {
/*  795 */     return this.m_MEstimate.getValue();
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
/*      */   public double getMinimalWeight() {
/*  811 */     return this.m_MinW.getValue();
/*      */   }
/*      */   
/*      */   public double getMinimalKnownWeight() {
/*  815 */     return this.m_MinKnownW.getValue();
/*      */   }
/*      */   
/*      */   public int getMinimalNbExamples() {
/*  819 */     return this.m_MinNbEx.getValue();
/*      */   }
/*      */   
/*      */   public void setMinimalWeight(double val) {
/*  823 */     this.m_MinW.setValue(val);
/*      */   }
/*      */   
/*      */   public String getTuneFolds() {
/*  827 */     return this.m_TuneFolds.getValue();
/*      */   }
/*      */   
/*      */   public double[] getClassWeight() {
/*  831 */     return this.m_ClassWeight.getDoubleVector();
/*      */   }
/*      */   
/*      */   public boolean isNominalSubsetTests() {
/*  835 */     return this.m_NominalSubsetTests.getValue();
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
/*      */   public boolean hasConstraintFile() {
/*  848 */     return !StringUtils.unCaseCompare(this.m_SyntacticConstrFile.getValue(), "None");
/*      */   }
/*      */   
/*      */   public String getConstraintFile() {
/*  852 */     return this.m_SyntacticConstrFile.getValue();
/*      */   }
/*      */   
/*      */   public int getMaxSize() {
/*  856 */     return getSizeConstraintPruning(0);
/*      */   }
/*      */   
/*      */   public int getSizeConstraintPruning(int idx) {
/*  860 */     if (this.m_MaxSizeConstr.isNominal(idx)) {
/*  861 */       return -1;
/*      */     }
/*  863 */     return this.m_MaxSizeConstr.getInt(idx);
/*      */   }
/*      */ 
/*      */   
/*      */   public int getSizeConstraintPruningNumber() {
/*  868 */     int len = this.m_MaxSizeConstr.getVectorLength();
/*  869 */     if (len == 1 && this.m_MaxSizeConstr.getNominal() == 0) {
/*  870 */       return 0;
/*      */     }
/*  872 */     return len;
/*      */   }
/*      */ 
/*      */   
/*      */   public int[] getSizeConstraintPruningVector() {
/*  877 */     int size_nb = getSizeConstraintPruningNumber();
/*  878 */     int[] sizes = new int[size_nb];
/*  879 */     for (int i = 0; i < size_nb; i++) {
/*  880 */       sizes[i] = getSizeConstraintPruning(i);
/*      */     }
/*  882 */     return sizes;
/*      */   }
/*      */   
/*      */   public void setSizeConstraintPruning(int size) {
/*  886 */     this.m_MaxSizeConstr.setInt(size);
/*      */   }
/*      */   
/*      */   public double getMaxErrorConstraint(int idx) {
/*  890 */     if (this.m_MaxErrorConstr.isNominal(idx)) {
/*  891 */       return Double.POSITIVE_INFINITY;
/*      */     }
/*  893 */     return this.m_MaxErrorConstr.getDouble(idx);
/*      */   }
/*      */ 
/*      */   
/*      */   public int getMaxErrorConstraintNumber() {
/*  898 */     int len = this.m_MaxErrorConstr.getVectorLength();
/*  899 */     if (len == 1 && this.m_MaxErrorConstr.getDouble(0) == 0.0D) {
/*  900 */       return 0;
/*      */     }
/*  902 */     return len;
/*      */   }
/*      */ 
/*      */   
/*      */   public double[] getMaxErrorConstraintVector() {
/*  907 */     int error_nb = getMaxErrorConstraintNumber();
/*  908 */     double[] max_error = new double[error_nb];
/*  909 */     for (int i = 0; i < error_nb; i++) {
/*  910 */       max_error[i] = getMaxErrorConstraint(i);
/*      */     }
/*  912 */     return max_error;
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
/*      */   
/*      */   public boolean isOutTrainError() {
/*  948 */     return this.m_OutTrainErr.getValue();
/*      */   }
/*      */   
/*      */   public boolean isOutValidError() {
/*  952 */     return this.m_OutValidErr.getValue();
/*      */   }
/*      */   
/*      */   public boolean isOutTestError() {
/*  956 */     return this.m_OutTestErr.getValue();
/*      */   }
/*      */   
/*      */   public boolean isCalError() {
/*  960 */     return this.m_CalErr.getValue();
/*      */   }
/*      */   
/*      */   public boolean isShowBranchFreq() {
/*  964 */     return this.m_ShowBrFreq.getValue();
/*      */   }
/*      */   
/*      */   public boolean isShowUnknown() {
/*  968 */     return this.m_ShowUnknown.getValue();
/*      */   }
/*      */   
/*      */   public boolean isPrintModelAndExamples() {
/*  972 */     return this.m_PrintModelAndExamples.getValue();
/*      */   }
/*      */   
/*      */   public boolean isOutFoldError() {
/*  976 */     return this.m_OutFoldErr.getValue();
/*      */   }
/*      */   
/*      */   public boolean isOutFoldData() {
/*  980 */     return this.m_OutFoldData.getValue();
/*      */   }
/*      */   
/*      */   public boolean isOutputFoldModels() {
/*  984 */     return this.m_OutFoldModels.getValue();
/*      */   }
/*      */   
/*      */   public boolean isWriteTestSetPredictions() {
/*  988 */     return this.m_WritePredictions.contains(1);
/*      */   }
/*      */   
/*      */   public boolean isWriteTrainSetPredictions() {
/*  992 */     return this.m_WritePredictions.contains(2);
/*      */   }
/*      */   
/*      */   public boolean isWriteErrorFile() {
/*  996 */     return this.m_WriteErrorFile.getValue();
/*      */   }
/*      */   
/*      */   public boolean isWriteModelIDPredictions() {
/* 1000 */     return this.m_ModelIDFiles.getValue();
/*      */   }
/*      */   
/*      */   public boolean isOutputPythonModel() {
/* 1004 */     return this.m_OutputPythonModel.getValue();
/*      */   }
/*      */   
/*      */   public boolean isOutputDatabaseQueries() {
/* 1008 */     return this.m_OutputDatabaseQueries.getValue();
/*      */   }
/*      */   
/*      */   public boolean isShowXValForest() {
/* 1012 */     return this.m_ShowForest.getValue();
/*      */   }
/*      */   
/*      */   public boolean isWriteCurves() {
/* 1016 */     return this.m_WriteCurves.getValue();
/*      */   }
/*      */   
/*      */   public boolean getShowModel(int i) {
/* 1020 */     return this.m_ShowModels.contains(i);
/*      */   }
/*      */   
/*      */   public boolean shouldShowModel(int model) {
/* 1024 */     boolean others = getShowModel(3);
/* 1025 */     if (model == 0 && getShowModel(0))
/* 1026 */       return true; 
/* 1027 */     if (model == 1 && getShowModel(1))
/* 1028 */       return true; 
/* 1029 */     if (model == 2 && (getShowModel(2) || others))
/* 1030 */       return true; 
/* 1031 */     if (others) {
/* 1032 */       return true;
/*      */     }
/* 1034 */     return false;
/*      */   }
/*      */   
/*      */   public StatisticPrintInfo getStatisticPrintInfo() {
/* 1038 */     StatisticPrintInfo info = new StatisticPrintInfo();
/* 1039 */     info.SHOW_EXAMPLE_COUNT = this.m_ShowInfo.contains(0);
/* 1040 */     info.SHOW_EXAMPLE_COUNT_BYTARGET = this.m_ShowInfo.contains(1);
/* 1041 */     info.SHOW_DISTRIBUTION = this.m_ShowInfo.contains(2);
/* 1042 */     info.SHOW_INDEX = this.m_ShowInfo.contains(3);
/* 1043 */     info.INTERNAL_DISTR = this.m_ShowInfo.contains(4);
/* 1044 */     return info;
/*      */   }
/*      */   
/*      */   public int getBaggingSets() {
/* 1048 */     return this.m_SetsData.getValue();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 1056 */   public static final String[] SHOW_MODELS = new String[] { "Default", "Original", "Pruned", "Others" };
/*      */   
/* 1058 */   public static final int[] SHOW_MODELS_VALUES = new int[] { 0, 2, 3 };
/*      */   
/*      */   public static final int SHOW_MODELS_DEFAULT = 0;
/*      */   public static final int SHOW_MODELS_ORIGINAL = 1;
/*      */   public static final int SHOW_MODELS_PRUNED = 2;
/*      */   public static final int SHOW_MODELS_OTHERS = 3;
/* 1064 */   public static final String[] SHOW_INFO = new String[] { "Count", "CountByTarget", "Distribution", "Index", "NodePrototypes" };
/*      */   
/* 1066 */   public static final int[] SHOW_INFO_VALUES = new int[] { 0 };
/*      */   
/* 1068 */   public static final String[] CONVERT_RULES = new String[] { "No", "Leaves", "AllNodes" };
/*      */ 
/*      */   
/*      */   public static final int CONVERT_RULES_NONE = 0;
/*      */ 
/*      */   
/*      */   public static final int CONVERT_RULES_LEAVES = 1;
/*      */   
/*      */   public static final int CONVERT_RULES_ALLNODES = 2;
/*      */   
/*      */   public static boolean SHOW_UNKNOWN_FREQ;
/*      */   
/*      */   public static boolean SHOW_BRANCH_FREQ;
/*      */   
/* 1082 */   public static final String[] WRITE_PRED = new String[] { "None", "Test", "Train" };
/*      */   
/* 1084 */   public static final int[] WRITE_PRED_VALUES = new int[] { 0 };
/*      */ 
/*      */   
/*      */   public static final int WRITE_PRED_NONE = 0;
/*      */ 
/*      */   
/*      */   public static final int WRITE_PRED_TEST = 1;
/*      */   
/*      */   public static final int WRITE_PRED_TRAIN = 2;
/*      */   
/* 1094 */   public static final String[] TREE_OPTIMIZE_VALUES = new String[] { "NoClusteringStats", "NoInodeStats" };
/* 1095 */   public static final int[] TREE_OPTIMIZE_NONE = new int[0];
/*      */   
/*      */   public static final int TREE_OPTIMIZE_NO_CLUSTERING_STATS = 0;
/*      */   
/*      */   public static final int TREE_OPTIMIZE_NO_INODE_STATS = 1;
/* 1100 */   public static final String[] INDUCTION_ORDER = new String[] { "DepthFirst", "BestFirst" };
/*      */   
/*      */   public static final int DEPTH_FIRST = 0;
/*      */   
/*      */   public static final int BEST_FIRST = 1;
/*      */   
/*      */   protected INIFileNominal m_InductionOrder;
/*      */   
/*      */   protected INIFileSection m_SectionTree;
/*      */   
/*      */   protected INIFileNominal m_Heuristic;
/*      */   
/*      */   protected INIFileInt m_TreeMaxDepth;
/*      */   
/*      */   protected INIFileBool m_BinarySplit;
/*      */   
/*      */   protected INIFileBool m_AlternativeSplits;
/*      */   
/*      */   protected INIFileNominalOrDoubleOrVector m_FTest;
/*      */   
/*      */   protected INIFileNominal m_PruningMethod;
/*      */   protected INIFileBool m_1SERule;
/*      */   protected INIFileBool m_MSENominal;
/*      */   protected INIFileDouble m_M5PruningMult;
/*      */   protected INIFileNominal m_RulesFromTree;
/*      */   protected INIFileNominal m_TreeOptimize;
/*      */   protected INIFileInt m_TreeSplitSampling;
/*      */   
/*      */   public void setSectionTreeEnabled(boolean enable) {
/* 1129 */     this.m_SectionTree.setEnabled(enable);
/*      */   }
/*      */   
/*      */   public int getHeuristic() {
/* 1133 */     return this.m_Heuristic.getValue();
/*      */   }
/*      */   
/*      */   public void setHeuristic(int value) {
/* 1137 */     this.m_Heuristic.setSingleValue(value);
/*      */   }
/*      */   
/*      */   public boolean checkHeuristic(String value) {
/* 1141 */     return this.m_Heuristic.getStringSingle().equals(value);
/*      */   }
/*      */ 
/*      */   
/*      */   public int getInductionOrder() {
/* 1146 */     return this.m_InductionOrder.getValue();
/*      */   }
/*      */   
/*      */   public void setInductionOrder(int value) {
/* 1150 */     this.m_InductionOrder.setSingleValue(value);
/*      */   }
/*      */   
/*      */   public boolean checkInductionOrder(String value) {
/* 1154 */     return this.m_InductionOrder.getStringSingle().equals(value);
/*      */   }
/*      */ 
/*      */   
/*      */   public int getTreeMaxDepth() {
/* 1159 */     return this.m_TreeMaxDepth.getValue();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getTreeSplitSampling() {
/* 1169 */     return this.m_TreeSplitSampling.getValue();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setTreeSplitSampling(int value) {
/* 1179 */     this.m_TreeSplitSampling.setValue(value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setTreeMaxDepth(int value) {
/* 1187 */     this.m_TreeMaxDepth.setValue(value);
/*      */   }
/*      */   
/*      */   public boolean isBinarySplit() {
/* 1191 */     return this.m_BinarySplit.getValue();
/*      */   }
/*      */   
/*      */   public boolean showAlternativeSplits() {
/* 1195 */     return this.m_AlternativeSplits.getValue();
/*      */   }
/*      */   
/*      */   public INIFileNominalOrDoubleOrVector getFTestArray() {
/* 1199 */     return this.m_FTest;
/*      */   }
/*      */   
/*      */   public double getFTest() {
/* 1203 */     return this.m_FTest.getDouble();
/*      */   }
/*      */   
/*      */   public void setFTest(double ftest) {
/* 1207 */     FTEST_VALUE = ftest;
/* 1208 */     FTEST_LEVEL = FTest.getLevelAndComputeArray(ftest);
/* 1209 */     this.m_FTest.setDouble(ftest);
/*      */   }
/*      */   
/*      */   public int getPruningMethod() {
/* 1213 */     return this.m_PruningMethod.getValue();
/*      */   }
/*      */   
/*      */   public void setPruningMethod(int method) {
/* 1217 */     this.m_PruningMethod.setSingleValue(method);
/*      */   }
/*      */   
/*      */   public String getPruningMethodName() {
/* 1221 */     return this.m_PruningMethod.getStringValue();
/*      */   }
/*      */   
/*      */   public boolean get1SERule() {
/* 1225 */     return this.m_1SERule.getValue();
/*      */   }
/*      */   
/*      */   public boolean isMSENominal() {
/* 1229 */     return this.m_MSENominal.getValue();
/*      */   }
/*      */   
/*      */   public double getM5PruningMult() {
/* 1233 */     return this.m_M5PruningMult.getValue();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int rulesFromTree() {
/* 1240 */     return this.m_RulesFromTree.getValue();
/*      */   }
/*      */   
/*      */   public boolean hasTreeOptimize(int value) {
/* 1244 */     return this.m_TreeOptimize.contains(value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 1252 */   public static final String[] HEURISTICS = new String[] { "Default", "ReducedError", "Gain", "GainRatio", "SSPD", "VarianceReduction", "MEstimate", "Morishita", "DispersionAdt", "DispersionMlt", "RDispersionAdt", "RDispersionMlt", "GeneticDistance", "SemiSupervised", "VarianceReductionMissing" };
/*      */   
/*      */   public static final int HEURISTIC_DEFAULT = 0;
/*      */   
/*      */   public static final int HEURISTIC_REDUCED_ERROR = 1;
/*      */   
/*      */   public static final int HEURISTIC_GAIN = 2;
/*      */   
/*      */   public static final int HEURISTIC_GAIN_RATIO = 3;
/*      */   
/*      */   public static final int HEURISTIC_SSPD = 4;
/*      */   
/*      */   public static final int HEURISTIC_VARIANCE_REDUCTION = 5;
/*      */   
/*      */   public static final int HEURISTIC_MESTIMATE = 6;
/*      */   
/*      */   public static final int HEURISTIC_MORISHITA = 7;
/*      */   
/*      */   public static final int HEURISTIC_DISPERSION_ADT = 8;
/*      */   
/*      */   public static final int HEURISTIC_DISPERSION_MLT = 9;
/*      */   
/*      */   public static final int HEURISTIC_R_DISPERSION_ADT = 10;
/*      */   
/*      */   public static final int HEURISTIC_R_DISPERSION_MLT = 11;
/*      */   
/*      */   public static final int HEURISTIC_GENETIC_DISTANCE = 12;
/*      */   
/*      */   public static final int HEURISTIC_SEMI_SUPERVISED = 13;
/*      */   
/*      */   public static final int HEURISTIC_SS_REDUCTION_MISSING = 14;
/*      */   
/*      */   public static int FTEST_LEVEL;
/*      */   public static double FTEST_VALUE;
/*      */   public static double MINIMAL_WEIGHT;
/*      */   public static boolean ONE_NOMINAL = true;
/* 1288 */   public static final String[] PRUNING_METHODS = new String[] { "Default", "None", "C4.5", "M5", "M5Multi", "ReducedErrorVSB", "Garofalakis", "GarofalakisVSB", "CartVSB", "CartMaxSize", "EncodingCost" };
/*      */   
/*      */   public static final int PRUNING_METHOD_DEFAULT = 0;
/*      */   
/*      */   public static final int PRUNING_METHOD_NONE = 1;
/*      */   
/*      */   public static final int PRUNING_METHOD_C45 = 2;
/*      */   
/*      */   public static final int PRUNING_METHOD_M5 = 3;
/*      */   
/*      */   public static final int PRUNING_METHOD_M5_MULTI = 4;
/*      */   
/*      */   public static final int PRUNING_METHOD_REDERR_VSB = 5;
/*      */   
/*      */   public static final int PRUNING_METHOD_GAROFALAKIS = 6;
/*      */   
/*      */   public static final int PRUNING_METHOD_GAROFALAKIS_VSB = 7;
/*      */   
/*      */   public static final int PRUNING_METHOD_CART_VSB = 8;
/*      */   public static final int PRUNING_METHOD_CART_MAXSIZE = 9;
/*      */   public static final int PRUNING_METHOD_ENCODING_COST = 10;
/*      */   protected INIFileSection m_SectionRules;
/*      */   public static INIFileBool m_PrintAllRules;
/*      */   
/*      */   public void setSectionRulesEnabled(boolean enable) {
/* 1313 */     this.m_SectionRules.setEnabled(enable);
/*      */   }
/*      */   
/*      */   public static boolean isPrintAllRules() {
/* 1317 */     return m_PrintAllRules.getValue();
/*      */   }
/*      */   
/* 1320 */   public static final String[] COVERING_METHODS = new String[] { "Standard", "WeightedMultiplicative", "WeightedAdditive", "WeightedError", "Union", "BeamRuleDefSet", "RandomRuleSet", "StandardBootstrap", "HeurOnly", "RulesFromTree" };
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int COVERING_METHOD_STANDARD = 0;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int COVERING_METHOD_WEIGHTED_MULTIPLICATIVE = 1;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int COVERING_METHOD_WEIGHTED_ADDITIVE = 2;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int COVERING_METHOD_WEIGHTED_ERROR = 3;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int COVERING_METHOD_UNION = 4;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int COVERING_METHOD_BEAM_RULE_DEF_SET = 5;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int COVERING_METHOD_RANDOM_RULE_SET = 6;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int COVERING_METHOD_STANDARD_BOOTSTRAP = 7;
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int COVERING_METHOD_HEURISTIC_ONLY = 8;
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int COVERING_METHOD_RULES_FROM_TREE = 9;
/*      */ 
/*      */ 
/*      */   
/* 1372 */   public static final String[] RULE_PREDICTION_METHODS = new String[] { "DecisionList", "TotCoverageWeighted", "CoverageWeighted", "AccuracyWeighted", "AccCovWeighted", "EquallyWeighted", "Optimized", "Union", "GDOptimized", "GDOptimizedBinary" };
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int RULE_PREDICTION_METHOD_DECISION_LIST = 0;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int RULE_PREDICTION_METHOD_TOT_COVERAGE_WEIGHTED = 1;
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int RULE_PREDICTION_METHOD_COVERAGE_WEIGHTED = 2;
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int RULE_PREDICTION_METHOD_ACCURACY_WEIGHTED = 3;
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int RULE_PREDICTION_METHOD_ACC_COV_WEIGHTED = 4;
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int RULE_PREDICTION_METHOD_EQUALLY_WEIGHTED = 5;
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int RULE_PREDICTION_METHOD_OPTIMIZED = 6;
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int RULE_PREDICTION_METHOD_UNION = 7;
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int RULE_PREDICTION_METHOD_GD_OPTIMIZED = 8;
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int RULE_PREDICTION_METHOD_GD_OPTIMIZED_BINARY = 9;
/*      */ 
/*      */ 
/*      */   
/* 1418 */   public static final String[] RULE_ADDING_METHODS = new String[] { "Always", "IfBetter", "IfBetterBeam" };
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int RULE_ADDING_METHOD_ALWAYS = 0;
/*      */ 
/*      */   
/*      */   public static final int RULE_ADDING_METHOD_IF_BETTER = 1;
/*      */ 
/*      */   
/*      */   public static final int RULE_ADDING_METHOD_IF_BETTER_BEAM = 2;
/*      */ 
/*      */   
/*      */   public static boolean IS_RULE_SIG_TESTING = false;
/*      */ 
/*      */   
/* 1434 */   public static final String[] OPT_LOSS_FUNCTIONS = new String[] { "Squared", "01Error", "RRMSE", "Huber" };
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int OPT_LOSS_FUNCTIONS_SQUARED = 0;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int OPT_LOSS_FUNCTIONS_01ERROR = 1;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int OPT_LOSS_FUNCTIONS_RRMSE = 2;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int OPT_LOSS_FUNCTIONS_HUBER = 3;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 1460 */   public static final String[] OPT_GD_MT_COMBINE_GRADIENTS = new String[] { "Avg", "Max", "MaxLoss", "MaxLossFast" };
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int OPT_GD_MT_GRADIENT_AVG = 0;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int OPT_GD_MT_GRADIENT_MAX_GRADIENT = 1;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int OPT_GD_MT_GRADIENT_MAX_LOSS_VALUE = 2;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int OPT_GD_MT_GRADIENT_MAX_LOSS_VALUE_FAST = 3;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 1485 */   public static final String[] GD_EXTERNAL_METHOD_VALUES = new String[] { "update", "brute" };
/*      */   
/*      */   public static final int GD_EXTERNAL_METHOD_GD = 0;
/*      */   public static final int GD_EXTERNAL_METHOD_BRUTE = 1;
/* 1489 */   public static final String[] OPT_LINEAR_TERM_NORM_VALUES = new String[] { "No", "Yes", "YesAndConvert" };
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int OPT_LIN_TERM_NORM_NO = 0;
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int OPT_LIN_TERM_NORM_YES = 1;
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int OPT_LIN_TERM_NORM_CONVERT = 2;
/*      */ 
/*      */ 
/*      */   
/* 1506 */   public static final String[] OPT_GD_ADD_LINEAR_TERMS = new String[] { "No", "Yes", "YesSaveMemory" };
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int OPT_GD_ADD_LIN_NO = 0;
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int OPT_GD_ADD_LIN_YES = 1;
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int OPT_GD_ADD_LIN_YES_SAVE_MEMORY = 2;
/*      */ 
/*      */ 
/*      */   
/* 1522 */   public static final String[] OPT_NORMALIZATION = new String[] { "No", "Yes", "OnlyScaling", "YesVariance" };
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int OPT_NORMALIZATION_NO = 0;
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int OPT_NORMALIZATION_YES = 1;
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int OPT_NORMALIZATION_ONLY_SCALING = 2;
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int OPT_NORMALIZATION_YES_VARIANCE = 3;
/*      */ 
/*      */ 
/*      */   
/*      */   protected INIFileNominal m_CoveringMethod;
/*      */ 
/*      */ 
/*      */   
/*      */   protected INIFileNominal m_PredictionMethod;
/*      */ 
/*      */ 
/*      */   
/*      */   protected INIFileNominal m_RuleAddingMethod;
/*      */ 
/*      */ 
/*      */   
/*      */   protected INIFileDouble m_CoveringWeight;
/*      */ 
/*      */ 
/*      */   
/*      */   protected INIFileDouble m_InstCoveringWeightThreshold;
/*      */ 
/*      */ 
/*      */   
/*      */   protected INIFileInt m_MaxRulesNb;
/*      */ 
/*      */ 
/*      */   
/*      */   protected INIFileDouble m_HeurDispOffset;
/*      */ 
/*      */ 
/*      */   
/*      */   protected INIFileDouble m_HeurCoveragePar;
/*      */ 
/*      */ 
/*      */   
/*      */   protected INIFileDouble m_HeurRuleDistPar;
/*      */ 
/*      */ 
/*      */   
/*      */   protected INIFileDouble m_HeurPrototypeDistPar;
/*      */ 
/*      */ 
/*      */   
/*      */   protected INIFileDouble m_RuleSignificanceLevel;
/*      */ 
/*      */ 
/*      */   
/*      */   protected INIFileInt m_RuleNbSigAtts;
/*      */ 
/*      */ 
/*      */   
/*      */   protected INIFileBool m_ComputeDispersion;
/*      */ 
/*      */ 
/*      */   
/*      */   protected INIFileDouble m_VarBasedDispNormWeight;
/*      */ 
/*      */ 
/*      */   
/*      */   protected INIFileNominalOrDoubleOrVector m_DispersionWeights;
/*      */ 
/*      */ 
/*      */   
/*      */   protected INIFileInt m_RandomRules;
/*      */ 
/*      */ 
/*      */   
/*      */   protected INIFileBool m_RuleWiseErrors;
/*      */ 
/*      */ 
/*      */   
/*      */   protected INIFileBool m_constrainedToFirstAttVal;
/*      */ 
/*      */ 
/*      */   
/*      */   protected INIFileInt m_OptDEPopSize;
/*      */ 
/*      */ 
/*      */   
/*      */   protected INIFileInt m_OptDENumEval;
/*      */ 
/*      */ 
/*      */   
/*      */   protected INIFileDouble m_OptDECrossProb;
/*      */ 
/*      */ 
/*      */   
/*      */   protected INIFileDouble m_OptDEWeight;
/*      */ 
/*      */ 
/*      */   
/*      */   protected INIFileInt m_OptDESeed;
/*      */ 
/*      */ 
/*      */   
/*      */   protected INIFileDouble m_OptDERegulPower;
/*      */ 
/*      */ 
/*      */   
/*      */   protected INIFileDouble m_OptDEProbMutationZero;
/*      */ 
/*      */ 
/*      */   
/*      */   protected INIFileDouble m_OptDEProbMutationNonZero;
/*      */ 
/*      */ 
/*      */   
/*      */   protected INIFileDouble m_OptRegPar;
/*      */ 
/*      */ 
/*      */   
/*      */   protected INIFileDouble m_OptNbZeroesPar;
/*      */ 
/*      */ 
/*      */   
/*      */   protected INIFileDouble m_OptRuleWeightThreshold;
/*      */ 
/*      */ 
/*      */   
/*      */   protected INIFileNominal m_OptLossFunction;
/*      */ 
/*      */ 
/*      */   
/*      */   protected INIFileDouble m_OptHuberAlpha;
/*      */ 
/*      */ 
/*      */   
/*      */   protected INIFileBool m_OptDefaultShiftPred;
/*      */ 
/*      */ 
/*      */   
/*      */   protected INIFileNominal m_OptAddLinearTerms;
/*      */ 
/*      */ 
/*      */   
/*      */   protected INIFileNominal m_OptNormalizeLinearTerms;
/*      */ 
/*      */ 
/*      */   
/*      */   protected INIFileBool m_OptLinearTermsTruncate;
/*      */ 
/*      */ 
/*      */   
/*      */   protected INIFileBool m_OptOmitRulePredictions;
/*      */ 
/*      */ 
/*      */   
/*      */   protected INIFileBool m_OptWeightGenerality;
/*      */ 
/*      */ 
/*      */   
/*      */   protected INIFileNominal m_OptNormalization;
/*      */ 
/*      */ 
/*      */   
/*      */   protected INIFileInt m_OptGDMaxIter;
/*      */ 
/*      */   
/*      */   protected INIFileDouble m_OptGDGradTreshold;
/*      */ 
/*      */   
/*      */   protected INIFileDouble m_OptGDStepSize;
/*      */ 
/*      */   
/*      */   protected INIFileBool m_OptGDIsDynStepsize;
/*      */ 
/*      */   
/*      */   protected INIFileInt m_OptGDMaxNbWeights;
/*      */ 
/*      */   
/*      */   protected INIFileDouble m_OptGDEarlyStopAmount;
/*      */ 
/*      */   
/*      */   protected INIFileDouble m_OptGDEarlyStopTreshold;
/*      */ 
/*      */   
/*      */   protected INIFileStringOrInt m_OptGDNbOfStepSizeReduce;
/*      */ 
/*      */   
/*      */   protected INIFileNominal m_OptGDExternalMethod;
/*      */ 
/*      */   
/*      */   protected INIFileNominal m_OptGDMTGradientCombine;
/*      */ 
/*      */   
/*      */   protected INIFileInt m_OptGDNbOfTParameterTry;
/*      */ 
/*      */   
/*      */   protected INIFileBool m_OptGDEarlyTTryStop;
/*      */ 
/*      */ 
/*      */   
/*      */   public INIFileNominalOrDoubleOrVector getDispersionWeights() {
/* 1732 */     return this.m_DispersionWeights;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isRandomRules() {
/* 1740 */     return (this.m_RandomRules.getValue() > 0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int nbRandomRules() {
/* 1747 */     return this.m_RandomRules.getValue();
/*      */   }
/*      */   
/*      */   public boolean isRuleWiseErrors() {
/* 1751 */     return this.m_RuleWiseErrors.getValue();
/*      */   }
/*      */   
/*      */   public int getCoveringMethod() {
/* 1755 */     return this.m_CoveringMethod.getValue();
/*      */   }
/*      */   
/*      */   public void setCoveringMethod(int method) {
/* 1759 */     this.m_CoveringMethod.setSingleValue(method);
/*      */   }
/*      */   
/*      */   public int getRulePredictionMethod() {
/* 1763 */     return this.m_PredictionMethod.getValue();
/*      */   }
/*      */   
/*      */   public boolean isRulePredictionOptimized() {
/* 1767 */     return (getRulePredictionMethod() == 6 || 
/* 1768 */       getRulePredictionMethod() == 8 || 
/* 1769 */       getRulePredictionMethod() == 9);
/*      */   }
/*      */   
/*      */   public void setRulePredictionMethod(int method) {
/* 1773 */     this.m_PredictionMethod.setSingleValue(method);
/*      */   }
/*      */   
/*      */   public int getRuleAddingMethod() {
/* 1777 */     return this.m_RuleAddingMethod.getValue();
/*      */   }
/*      */   
/*      */   public void setRuleAddingMethod(int method) {
/* 1781 */     this.m_RuleAddingMethod.setSingleValue(method);
/*      */   }
/*      */   
/*      */   public double getCoveringWeight() {
/* 1785 */     return this.m_CoveringWeight.getValue();
/*      */   }
/*      */   
/*      */   public void setCoveringWeight(double weight) {
/* 1789 */     this.m_CoveringWeight.setValue(weight);
/*      */   }
/*      */   
/*      */   public double getInstCoveringWeightThreshold() {
/* 1793 */     return this.m_InstCoveringWeightThreshold.getValue();
/*      */   }
/*      */   
/*      */   public void setInstCoveringWeightThreshold(double thresh) {
/* 1797 */     this.m_InstCoveringWeightThreshold.setValue(thresh);
/*      */   }
/*      */   
/*      */   public int getMaxRulesNb() {
/* 1801 */     return this.m_MaxRulesNb.getValue();
/*      */   }
/*      */   
/*      */   public void setMaxRulesNb(int nb) {
/* 1805 */     this.m_MaxRulesNb.setValue(nb);
/*      */   }
/*      */   
/*      */   public double getRuleSignificanceLevel() {
/* 1809 */     return this.m_RuleSignificanceLevel.getValue();
/*      */   }
/*      */   
/*      */   public int getRuleNbSigAtt() {
/* 1813 */     return this.m_RuleNbSigAtts.getValue();
/*      */   }
/*      */   
/*      */   public boolean isRuleSignificanceTesting() {
/* 1817 */     return (this.m_RuleNbSigAtts.getValue() != 0);
/*      */   }
/*      */   
/*      */   public double getHeurDispOffset() {
/* 1821 */     return this.m_HeurDispOffset.getValue();
/*      */   }
/*      */   
/*      */   public double getHeurCoveragePar() {
/* 1825 */     return this.m_HeurCoveragePar.getValue();
/*      */   }
/*      */   
/*      */   public double getHeurRuleDistPar() {
/* 1829 */     return this.m_HeurRuleDistPar.getValue();
/*      */   }
/*      */   
/*      */   public void setHeurRuleDistPar(double value) {
/* 1833 */     this.m_HeurRuleDistPar.setValue(value);
/*      */   }
/*      */   
/*      */   public boolean isHeurRuleDist() {
/* 1837 */     return (this.m_HeurRuleDistPar.getValue() > 0.0D);
/*      */   }
/*      */   
/*      */   public boolean isWeightedCovering() {
/* 1841 */     if (this.m_CoveringMethod.getValue() == 2 || this.m_CoveringMethod
/* 1842 */       .getValue() == 1 || this.m_CoveringMethod
/* 1843 */       .getValue() == 3) {
/* 1844 */       return true;
/*      */     }
/* 1846 */     return false;
/*      */   }
/*      */ 
/*      */   
/*      */   public double getHeurPrototypeDistPar() {
/* 1851 */     return this.m_HeurPrototypeDistPar.getValue();
/*      */   }
/*      */   
/*      */   public void setHeurPrototypeDistPar(double value) {
/* 1855 */     this.m_HeurPrototypeDistPar.setValue(value);
/*      */   }
/*      */   
/*      */   public boolean isHeurPrototypeDistPar() {
/* 1859 */     return (this.m_HeurPrototypeDistPar.getValue() > 0.0D);
/*      */   }
/*      */   
/*      */   private boolean m_ruleInduceParamsDisabled = false;
/* 1863 */   private double m_origHeurRuleDistPar = 0.0D;
/* 1864 */   private int m_origRulePredictionMethod = 0;
/* 1865 */   private int m_origCoveringMethod = 0;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void disableRuleInduceParams() {
/* 1872 */     if (!this.m_ruleInduceParamsDisabled) {
/* 1873 */       this.m_origHeurRuleDistPar = getHeurRuleDistPar();
/* 1874 */       this.m_origRulePredictionMethod = getRulePredictionMethod();
/* 1875 */       this.m_origCoveringMethod = getCoveringMethod();
/*      */       
/* 1877 */       setHeurRuleDistPar(0.0D);
/* 1878 */       setRulePredictionMethod(0);
/* 1879 */       setCoveringMethod(9);
/* 1880 */       this.m_ruleInduceParamsDisabled = true;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void returnRuleInduceParams() {
/* 1889 */     if (this.m_ruleInduceParamsDisabled) {
/* 1890 */       setHeurRuleDistPar(this.m_origHeurRuleDistPar);
/* 1891 */       setRulePredictionMethod(this.m_origRulePredictionMethod);
/* 1892 */       setCoveringMethod(this.m_origCoveringMethod);
/* 1893 */       this.m_ruleInduceParamsDisabled = false;
/*      */     } 
/*      */   }
/*      */   
/*      */   public boolean computeDispersion() {
/* 1898 */     return this.m_ComputeDispersion.getValue();
/*      */   }
/*      */   
/*      */   public double getVarBasedDispNormWeight() {
/* 1902 */     return this.m_VarBasedDispNormWeight.getValue();
/*      */   }
/*      */   
/*      */   public boolean isConstrainedToFirstAttVal() {
/* 1906 */     return this.m_constrainedToFirstAttVal.getValue();
/*      */   }
/*      */   
/*      */   public double getOptDECrossProb() {
/* 1910 */     return this.m_OptDECrossProb.getValue();
/*      */   }
/*      */   
/*      */   public int getOptDENumEval() {
/* 1914 */     return this.m_OptDENumEval.getValue();
/*      */   }
/*      */   
/*      */   public int getOptDEPopSize() {
/* 1918 */     return this.m_OptDEPopSize.getValue();
/*      */   }
/*      */   
/*      */   public int getOptDESeed() {
/* 1922 */     return this.m_OptDESeed.getValue();
/*      */   }
/*      */   
/*      */   public double getOptDEWeight() {
/* 1926 */     return this.m_OptDEWeight.getValue();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public double getOptRegPar() {
/* 1933 */     return this.m_OptRegPar.getValue();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setOptRegPar(double newValue) {
/* 1940 */     this.m_OptRegPar.setValue(newValue);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public double getOptNbZeroesPar() {
/* 1947 */     return this.m_OptNbZeroesPar.getValue();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setOptNbZeroesPar(double newValue) {
/* 1954 */     this.m_OptNbZeroesPar.setValue(newValue);
/*      */   }
/*      */   
/*      */   public double getOptRuleWeightThreshold() {
/* 1958 */     return this.m_OptRuleWeightThreshold.getValue();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isOptDefaultShiftPred() {
/* 1966 */     return this.m_OptDefaultShiftPred.getValue();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isOptAddLinearTerms() {
/* 1973 */     return (this.m_OptAddLinearTerms.getValue() != 0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getOptAddLinearTerms() {
/* 1980 */     return this.m_OptAddLinearTerms.getValue();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isOptNormalizeLinearTerms() {
/* 1987 */     return (this.m_OptNormalizeLinearTerms.getValue() != 0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getOptNormalizeLinearTerms() {
/* 1994 */     return this.m_OptNormalizeLinearTerms.getValue();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isOptLinearTermsTruncate() {
/* 2002 */     return this.m_OptLinearTermsTruncate.getValue();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isOptOmitRulePredictions() {
/* 2009 */     return this.m_OptOmitRulePredictions.getValue();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isOptWeightGenerality() {
/* 2017 */     return this.m_OptWeightGenerality.getValue();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isOptNormalization() {
/* 2025 */     return (this.m_OptNormalization.getValue() != 0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getOptNormalization() {
/* 2033 */     return this.m_OptNormalization.getValue();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getOptDELossFunction() {
/* 2040 */     return this.m_OptLossFunction.getValue();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public double getOptDERegulPower() {
/* 2047 */     return this.m_OptDERegulPower.getValue();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public double getOptDEProbMutationZero() {
/* 2055 */     return this.m_OptDEProbMutationZero.getValue();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public double getOptDEProbMutationNonZero() {
/* 2063 */     return this.m_OptDEProbMutationNonZero.getValue();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public double getOptHuberAlpha() {
/* 2070 */     return this.m_OptHuberAlpha.getValue();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getOptGDMaxIter() {
/* 2077 */     return this.m_OptGDMaxIter.getValue();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getOptGDLossFunction() {
/* 2084 */     return this.m_OptLossFunction.getValue();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public double getOptGDGradTreshold() {
/* 2093 */     return this.m_OptGDGradTreshold.getValue();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setOptGDGradTreshold(double newVal) {
/* 2102 */     this.m_OptGDGradTreshold.setValue(newVal);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public double getOptGDStepSize() {
/* 2109 */     return this.m_OptGDStepSize.getValue();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isOptGDIsDynStepsize() {
/* 2116 */     return this.m_OptGDIsDynStepsize.getValue();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public double getOptGDEarlyStopAmount() {
/* 2123 */     return this.m_OptGDEarlyStopAmount.getValue();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public double getOptGDEarlyStopTreshold() {
/* 2130 */     return this.m_OptGDEarlyStopTreshold.getValue();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getOptGDMaxNbWeights() {
/* 2138 */     return this.m_OptGDMaxNbWeights.getValue();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setOptGDMaxNbWeights(int nbWeights) {
/* 2146 */     this.m_OptGDMaxNbWeights.setValue(nbWeights);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getOptGDNbOfStepSizeReduce() {
/* 2155 */     if (this.m_OptGDNbOfStepSizeReduce.isString("Infinity")) {
/* 2156 */       return Integer.MAX_VALUE;
/*      */     }
/* 2158 */     return this.m_OptGDNbOfStepSizeReduce.getIntValue();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getOptGDExternalMethod() {
/* 2166 */     return this.m_OptGDExternalMethod.getValue();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getOptGDMTGradientCombine() {
/* 2174 */     return this.m_OptGDMTGradientCombine.getValue();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getOptGDNbOfTParameterTry() {
/* 2182 */     return this.m_OptGDNbOfTParameterTry.getValue();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getOptGDEarlyTTryStop() {
/* 2191 */     return this.m_OptGDEarlyTTryStop.getValue();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 2201 */   public static final String[] HIERTYPES = new String[] { "Tree", "DAG" };
/*      */   
/*      */   public static final int HIERTYPE_TREE = 0;
/*      */   
/*      */   public static final int HIERTYPE_DAG = 1;
/* 2206 */   public static final String[] HIERWEIGHT = new String[] { "ExpSumParentWeight", "ExpAvgParentWeight", "ExpMinParentWeight", "ExpMaxParentWeight", "NoWeight" };
/*      */   
/*      */   public static final int HIERWEIGHT_EXP_SUM_PARENT_WEIGHT = 0;
/*      */   
/*      */   public static final int HIERWEIGHT_EXP_AVG_PARENT_WEIGHT = 1;
/*      */   public static final int HIERWEIGHT_EXP_MIN_PARENT_WEIGHT = 2;
/*      */   public static final int HIERWEIGHT_EXP_MAX_PARENT_WEIGHT = 3;
/*      */   public static final int HIERWEIGHT_NO_WEIGHT = 4;
/* 2214 */   public static final String[] HIERDIST = new String[] { "WeightedEuclidean", "Jaccard" };
/*      */   
/*      */   public static final int HIERDIST_WEIGHTED_EUCLIDEAN = 0;
/*      */   
/*      */   public static final int HIERDIST_JACCARD = 1;
/* 2219 */   public static final String[] HIERMEASURES = new String[] { "AverageAUROC", "AverageAUPRC", "WeightedAverageAUPRC", "PooledAUPRC" };
/*      */   public static final int HIERMEASURE_AUROC = 0;
/*      */   public static final int HIERMEASURE_AUPRC = 1;
/*      */   public static final int HIERMEASURE_WEIGHTED_AUPRC = 2;
/*      */   public static final int HIERMEASURE_POOLED_AUPRC = 3;
/*      */   INIFileSection m_SectionHierarchical;
/*      */   protected INIFileNominal m_HierType;
/*      */   protected INIFileNominal m_HierWType;
/*      */   protected INIFileNominal m_HierDistance;
/*      */   protected INIFileDouble m_HierWParam;
/*      */   protected INIFileString m_HierSep;
/*      */   protected INIFileString m_HierEmptySetIndicator;
/*      */   protected INIFileNominal m_HierOptimizeErrorMeasure;
/*      */   protected INIFileString m_DefinitionFile;
/*      */   protected INIFileBool m_HierNoRootPreds;
/*      */   protected INIFileBool m_HierSingleLabel;
/*      */   protected INIFileBool m_CalErr;
/*      */   protected INIFileDouble m_HierPruneInSig;
/*      */   protected INIFileBool m_HierUseBonferroni;
/*      */   protected INIFileNominalOrDoubleOrVector m_HierClassThreshold;
/*      */   protected INIFileNominalOrDoubleOrVector m_RecallValues;
/*      */   protected INIFileString m_HierEvalClasses;
/*      */   protected static INIFileBool m_HierUseMEstimate;
/*      */   protected INIFileSection m_SectionILevelC;
/*      */   protected INIFileString m_ILevelCFile;
/*      */   
/*      */   public void setSectionHierarchicalEnabled(boolean enable) {
/* 2246 */     this.m_SectionHierarchical.setEnabled(enable);
/*      */   }
/*      */   protected INIFileDouble m_ILevelCAlpha; protected INIFileInt m_ILevelNbRandomConstr; protected INIFileBool m_ILevelCCOPKMeans; protected INIFileBool m_ILevelCMPCKMeans; public static int BEAM_WIDTH; public static double SIZE_PENALTY; public static double BEAM_SIMILARITY; public static boolean BEAM_SYNT_DIST_CONSTR; protected INIFileSection m_SectionBeam; protected INIFileDouble m_SizePenalty; protected INIFileInt m_BeamWidth; protected INIFileInt m_BeamBestN; protected INIFileInt m_TreeMaxSize; protected INIFileNominal m_BeamAttrHeuristic; protected INIFileBool m_FastBS; protected INIFileBool m_BeamPostPrune; protected INIFileBool m_BMRemoveEqualHeur; protected INIFileDouble m_BeamSimilarity; protected INIFileBool m_BSortTrainParameter; protected INIFileBool m_BeamToForest; protected INIFileString m_BeamSyntacticConstrFile; protected INIFileSection m_SectionExhaustive; protected INIFileBool m_Exhaustive; protected INIFileInt m_StartTreeCpt; protected INIFileInt m_StartItemCpt;
/*      */   public boolean getHierSingleLabel() {
/* 2250 */     return this.m_HierSingleLabel.getValue();
/*      */   }
/*      */   
/*      */   public int getHierType() {
/* 2254 */     return this.m_HierType.getValue();
/*      */   }
/*      */   
/*      */   public int getHierDistance() {
/* 2258 */     return this.m_HierDistance.getValue();
/*      */   }
/*      */   
/*      */   public int getHierWType() {
/* 2262 */     return this.m_HierWType.getValue();
/*      */   }
/*      */   
/*      */   public double getHierWParam() {
/* 2266 */     return this.m_HierWParam.getValue();
/*      */   }
/*      */   
/*      */   public INIFileNominalOrDoubleOrVector getClassificationThresholds() {
/* 2270 */     return this.m_HierClassThreshold;
/*      */   }
/*      */   
/*      */   public INIFileNominalOrDoubleOrVector getRecallValues() {
/* 2274 */     return this.m_RecallValues;
/*      */   }
/*      */   
/*      */   public boolean isHierNoRootPreds() {
/* 2278 */     return this.m_HierNoRootPreds.getValue();
/*      */   }
/*      */   
/*      */   public boolean isUseBonferroni() {
/* 2282 */     return this.m_HierUseBonferroni.getValue();
/*      */   }
/*      */   
/*      */   public double getHierPruneInSig() {
/* 2286 */     return this.m_HierPruneInSig.getValue();
/*      */   }
/*      */   
/*      */   public boolean hasHierEvalClasses() {
/* 2290 */     return !StringUtils.unCaseCompare(this.m_HierEvalClasses.getValue(), "None");
/*      */   }
/*      */   
/*      */   public String getHierEvalClasses() {
/* 2294 */     return this.m_HierEvalClasses.getValue();
/*      */   }
/*      */   
/*      */   public int getHierOptimizeErrorMeasure() {
/* 2298 */     return this.m_HierOptimizeErrorMeasure.getValue();
/*      */   }
/*      */   
/*      */   public boolean hasDefinitionFile() {
/* 2302 */     return !StringUtils.unCaseCompare(this.m_DefinitionFile.getValue(), "None");
/*      */   }
/*      */   
/*      */   public String getDefinitionFile() {
/* 2306 */     return this.m_DefinitionFile.getValue();
/*      */   }
/*      */   
/*      */   public void initHierarchical() {
/* 2310 */     ClassesValue.setHSeparator(this.m_HierSep.getValue());
/* 2311 */     ClassesValue.setEmptySetIndicator(this.m_HierEmptySetIndicator.getValue());
/*      */   }
/*      */   
/*      */   public static boolean useMEstimate() {
/* 2315 */     return m_HierUseMEstimate.getValue();
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
/*      */   public boolean isSectionILevelCEnabled() {
/* 2331 */     return this.m_SectionILevelC.isEnabled();
/*      */   }
/*      */   
/*      */   public boolean hasILevelCFile() {
/* 2335 */     return !StringUtils.unCaseCompare(this.m_ILevelCFile.getValue(), "None");
/*      */   }
/*      */   
/*      */   public String getILevelCFile() {
/* 2339 */     return this.m_ILevelCFile.getValue();
/*      */   }
/*      */   
/*      */   public double getILevelCAlpha() {
/* 2343 */     return this.m_ILevelCAlpha.getValue();
/*      */   }
/*      */   
/*      */   public int getILevelCNbRandomConstraints() {
/* 2347 */     return this.m_ILevelNbRandomConstr.getValue();
/*      */   }
/*      */   
/*      */   public boolean isILevelCCOPKMeans() {
/* 2351 */     return this.m_ILevelCCOPKMeans.getValue();
/*      */   }
/*      */   
/*      */   public boolean isILevelCMPCKMeans() {
/* 2355 */     return this.m_ILevelCMPCKMeans.getValue();
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
/*      */   public void setSectionBeamEnabled(boolean enable) {
/* 2383 */     this.m_SectionBeam.setEnabled(enable);
/*      */   }
/*      */   
/*      */   public int getBeamWidth() {
/* 2387 */     return this.m_BeamWidth.getValue();
/*      */   }
/*      */   
/*      */   public double getSizePenalty() {
/* 2391 */     return this.m_SizePenalty.getValue();
/*      */   }
/*      */   
/*      */   public boolean isBeamSearchMode() {
/* 2395 */     return this.m_SectionBeam.isEnabled();
/*      */   }
/*      */   
/*      */   public int getBeamBestN() {
/* 2399 */     return this.m_BeamBestN.getValue();
/*      */   }
/*      */   
/*      */   public int getBeamTreeMaxSize() {
/* 2403 */     return this.m_TreeMaxSize.getValue();
/*      */   }
/*      */   
/*      */   public boolean getBeamRemoveEqualHeur() {
/* 2407 */     return this.m_BMRemoveEqualHeur.getValue();
/*      */   }
/*      */   
/*      */   public boolean getBeamSortOnTrainParameter() {
/* 2411 */     return this.m_BSortTrainParameter.getValue();
/*      */   }
/*      */   
/*      */   public double getBeamSimilarity() {
/* 2415 */     return this.m_BeamSimilarity.getValue();
/*      */   }
/*      */   
/*      */   public boolean isBeamPostPrune() {
/* 2419 */     return this.m_BeamPostPrune.getValue();
/*      */   }
/*      */   
/*      */   public int getBeamAttrHeuristic() {
/* 2423 */     return this.m_BeamAttrHeuristic.getValue();
/*      */   }
/*      */   
/*      */   public boolean hasBeamConstraintFile() {
/* 2427 */     return !StringUtils.unCaseCompare(this.m_BeamSyntacticConstrFile.getValue(), "None");
/*      */   }
/*      */   
/*      */   public String getBeamConstraintFile() {
/* 2431 */     return this.m_BeamSyntacticConstrFile.getValue();
/*      */   }
/*      */   
/*      */   public boolean isBeamToForest() {
/* 2435 */     return this.m_BeamToForest.getValue();
/*      */   }
/*      */   
/*      */   public boolean isFastBS() {
/* 2439 */     return this.m_FastBS.getValue();
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
/*      */   public void setSectionExhaustiveEnabled(boolean enable) {
/* 2453 */     this.m_SectionExhaustive.setEnabled(enable);
/*      */   }
/*      */   
/*      */   public boolean isExhaustiveSearch() {
/* 2457 */     return this.m_Exhaustive.getValue();
/*      */   }
/*      */   
/*      */   public int getStartTreeCpt() {
/* 2461 */     return this.m_StartTreeCpt.getValue();
/*      */   }
/*      */   
/*      */   public int getStartItemCpt() {
/* 2465 */     return this.m_StartItemCpt.getValue();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 2473 */   public static final String[] TIME_SERIES_DISTANCE_MEASURE = new String[] { "DTW", "QDM", "TSC" };
/*      */   
/*      */   public static final int TIME_SERIES_DISTANCE_MEASURE_DTW = 0;
/*      */   
/*      */   public static final int TIME_SERIES_DISTANCE_MEASURE_QDM = 1;
/*      */   public static final int TIME_SERIES_DISTANCE_MEASURE_TSC = 2;
/* 2479 */   public static final String[] TIME_SERIES_PROTOTYPE_COMPLEXITY = new String[] { "N2", "LOG", "LINEAR", "NPAIRS", "TEST" };
/*      */   
/*      */   INIFileSection m_SectionTimeSeries;
/*      */   public INIFileNominal m_TimeSeriesDistance;
/*      */   public INIFileNominal m_TimeSeriesHeuristicSampling;
/*      */   
/*      */   public boolean isSectionTimeSeriesEnabled() {
/* 2486 */     return this.m_SectionTimeSeries.isEnabled();
/*      */   }
/*      */   
/*      */   public void setSectionTimeSeriesEnabled(boolean enable) {
/* 2490 */     this.m_SectionTimeSeries.setEnabled(enable);
/*      */   }
/*      */   
/*      */   public boolean isTimeSeriesProtoComlexityExact() {
/* 2494 */     if (this.m_TimeSeriesHeuristicSampling.getValue() == 0) {
/* 2495 */       return true;
/*      */     }
/* 2497 */     return false;
/*      */   }
/*      */ 
/*      */   
/*      */   public int getTimeSeriesDistance() {
/* 2502 */     return this.m_TimeSeriesDistance.getValue();
/*      */   }
/*      */   
/*      */   public int getTimeSeriesHeuristicSampling() {
/* 2506 */     return this.m_TimeSeriesHeuristicSampling.getValue();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 2514 */   public static final String[] PHYLOGENY_DISTANCE_MEASURE = new String[] { "PDist", "Edit", "JC", "Kimura", "AminoKimura" };
/*      */   
/*      */   public static final int PHYLOGENY_DISTANCE_MEASURE_PDIST = 0;
/*      */   
/*      */   public static final int PHYLOGENY_DISTANCE_MEASURE_EDIT = 1;
/*      */   public static final int PHYLOGENY_DISTANCE_MEASURE_JC = 2;
/*      */   public static final int PHYLOGENY_DISTANCE_MEASURE_KIMURA = 3;
/*      */   public static final int PHYLOGENY_DISTANCE_MEASURE_AMINOKIMURA = 4;
/* 2522 */   public static final String[] PHYLOGENY_SEQUENCE = new String[] { "DNA", "Protein", "Any" };
/*      */   
/*      */   public static final int PHYLOGENY_SEQUENCE_DNA = 0;
/*      */   
/*      */   public static final int PHYLOGENY_SEQUENCE_AMINO = 1;
/*      */   public static final int PHYLOGENY_SEQUENCE_ANY = 2;
/* 2528 */   public static final String[] PHYLOGENY_CRITERION = new String[] { "MinTotBranchLength", "MaxAvgPWDistance", "MaxMinPWDistance" };
/*      */   
/*      */   public static final int PHYLOGENY_CRITERION_BRANCHLENGTHS = 0;
/*      */   
/*      */   public static final int PHYLOGENY_CRITERION_MAXAVGPWDIST = 1;
/*      */   public static final int PHYLOGENY_CRITERION_MAXMINPWDIST = 2;
/*      */   INIFileSection m_SectionPhylogeny;
/*      */   public static INIFileNominal m_PhylogenyDM;
/*      */   public static INIFileNominal m_PhylogenyCriterion;
/*      */   public static INIFileNominal m_PhylogenySequence;
/*      */   public static INIFileString m_PhylogenyDistanceMatrix;
/*      */   public static INIFileDouble m_PhylogenyEntropyVsRootStop;
/*      */   public static INIFileDouble m_PhylogenyDistancesVsRootStop;
/*      */   public static INIFileDouble m_PhylogenyEntropyVsParentStop;
/*      */   public static INIFileDouble m_PhylogenyDistancesVsParentStop;
/*      */   
/*      */   public String getPhylogenyDistanceMatrix() {
/* 2545 */     return m_PhylogenyDistanceMatrix.getValue();
/*      */   }
/*      */   
/*      */   public boolean isSectionPhylogenyEnabled() {
/* 2549 */     return this.m_SectionPhylogeny.isEnabled();
/*      */   }
/*      */   
/*      */   public void setSectionPhylogenyEnabled(boolean enable) {
/* 2553 */     this.m_SectionPhylogeny.setEnabled(enable);
/*      */   }
/*      */   
/*      */   public double getPhylogenyEntropyVsRootStop() {
/* 2557 */     return m_PhylogenyEntropyVsRootStop.getValue();
/*      */   }
/*      */   
/*      */   public double getPhylogenyDistancesVsRootStop() {
/* 2561 */     return m_PhylogenyDistancesVsRootStop.getValue();
/*      */   }
/*      */   
/*      */   public double getPhylogenyEntropyVsParentStop() {
/* 2565 */     return m_PhylogenyEntropyVsParentStop.getValue();
/*      */   }
/*      */   
/*      */   public double getPhylogenyDistancesVsParentStop() {
/* 2569 */     return m_PhylogenyDistancesVsParentStop.getValue();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 2577 */   public static final String[] ENSEMBLE_TYPE = new String[] { "Bagging", "RForest", "RSubspaces", "BagSubspaces", "Boosting", "RFeatSelection", "Pert" };
/*      */   
/*      */   public static final int ENSEMBLE_BAGGING = 0;
/*      */   
/*      */   public static final int ENSEMBLE_RFOREST = 1;
/*      */   
/*      */   public static final int ENSEMBLE_RSUBSPACES = 2;
/*      */   
/*      */   public static final int ENSEMBLE_BAGSUBSPACES = 3;
/*      */   
/*      */   public static final int ENSEMBLE_BOOSTING = 4;
/*      */   
/*      */   public static final int ENSEMBLE_NOBAGRFOREST = 5;
/*      */   
/*      */   public static final int ENSEMBLE_PERT = 6;
/*      */   
/* 2593 */   public static final String[] VOTING_TYPE = new String[] { "Majority", "ProbabilityDistribution" };
/*      */   
/*      */   public static final int VOTING_TYPE_MAJORITY = 0;
/*      */   
/*      */   public static final int VOTING_TYPE_PROBAB_DISTR = 1;
/*      */   
/*      */   INIFileSection m_SectionEnsembles;
/*      */   
/*      */   protected INIFileNominalOrIntOrVector m_NbBags;
/*      */   
/*      */   public static INIFileNominal m_EnsembleMethod;
/*      */   
/*      */   public static INIFileNominal m_ClassificationVoteType;
/*      */   
/*      */   protected INIFileInt m_RandomAttrSelected;
/*      */   
/*      */   public static INIFileBool m_PrintAllModels;
/*      */   
/*      */   public static INIFileBool m_PrintAllModelFiles;
/*      */   
/*      */   public static boolean m_EnsembleMode = false;
/*      */   
/*      */   public static INIFileBool m_EnsembleShouldOpt;
/*      */   
/*      */   public static INIFileBool m_EnsembleOOBestimate;
/*      */   
/*      */   protected INIFileBool m_FeatureRanking;
/*      */   
/*      */   protected INIFileBool m_WriteEnsemblePredictions;
/*      */   protected INIFileNominalOrIntOrVector m_BagSelection;
/*      */   protected INIFileBool m_EnsembleRandomDepth;
/*      */   protected INIFileInt m_EnsembleBagSize;
/*      */   INIFileSection m_SectionKNN;
/*      */   public static INIFileInt kNN_k;
/*      */   public static INIFileString kNN_vectDist;
/*      */   public static INIFileBool kNN_distWeighted;
/*      */   public static INIFileBool kNN_normalized;
/*      */   public static INIFileBool kNN_attrWeighted;
/*      */   INIFileSection m_SectionKNNT;
/*      */   public static INIFileInt kNNT_k;
/*      */   public static INIFileString kNNT_vectDist;
/*      */   public static INIFileBool kNNT_distWeighted;
/*      */   public static INIFileBool kNNT_normalized;
/*      */   public static INIFileBool kNNT_attrWeighted;
/*      */   public static boolean SHOW_XVAL_FOREST;
/*      */   
/*      */   public boolean isEnsembleMode() {
/* 2640 */     return m_EnsembleMode;
/*      */   }
/*      */   
/*      */   public void setEnsembleMode(boolean value) {
/* 2644 */     m_EnsembleMode = value;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isSectionEnsembleEnabled() {
/* 2651 */     return this.m_SectionEnsembles.isEnabled();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setSectionEnsembleEnabled(boolean value) {
/* 2658 */     this.m_SectionEnsembles.setEnabled(value);
/*      */   }
/*      */   
/*      */   public int getEnsembleMethod() {
/* 2662 */     return m_EnsembleMethod.getValue();
/*      */   }
/*      */   
/*      */   public void setEnsembleMethod(String value) {
/* 2666 */     m_EnsembleMethod.setValue(value);
/*      */   }
/*      */   
/*      */   public void setEnsembleMethod(int value) {
/* 2670 */     m_EnsembleMethod.setSingleValue(value);
/*      */   }
/*      */   
/*      */   public boolean shouldPerformRanking() {
/* 2674 */     return this.m_FeatureRanking.getValue();
/*      */   }
/*      */   
/*      */   public void setFeatureRanking(boolean value) {
/* 2678 */     this.m_FeatureRanking.setValue(value);
/*      */   }
/*      */   
/*      */   public INIFileNominalOrIntOrVector getNbBaggingSets() {
/* 2682 */     if (!this.m_NbBags.isVector() && this.m_NbBags.getInt() == 0) {
/* 2683 */       this.m_NbBags.setInt(10);
/*      */     }
/* 2685 */     return this.m_NbBags;
/*      */   }
/*      */   
/*      */   public void setNbBags(int value) {
/* 2689 */     this.m_NbBags.setInt(value);
/*      */   }
/*      */   
/*      */   public int getNbRandomAttrSelected() {
/* 2693 */     return this.m_RandomAttrSelected.getValue();
/*      */   }
/*      */   
/*      */   public INIFileNominalOrIntOrVector getBagSelection() {
/* 2697 */     return this.m_BagSelection;
/*      */   }
/*      */   
/*      */   public void updateNbRandomAttrSelected(ClusSchema schema) {
/*      */     int fsize;
/* 2702 */     if (getNbRandomAttrSelected() == 0) {
/* 2703 */       fsize = (int)(Math.log(schema.getNbDescriptiveAttributes()) / Math.log(2.0D) + 1.0D);
/*      */     } else {
/* 2705 */       fsize = getNbRandomAttrSelected();
/*      */     } 
/* 2707 */     setNbRandomAttrSelected(fsize);
/*      */   }
/*      */   
/*      */   public void setNbRandomAttrSelected(int value) {
/* 2711 */     this.m_RandomAttrSelected.setValue(value);
/*      */   }
/*      */   
/*      */   public void setBagSelection(int value) {
/* 2715 */     this.m_BagSelection.setInt(value);
/*      */   }
/*      */   
/*      */   public static boolean isPrintEnsembleModels() {
/* 2719 */     return m_PrintAllModels.getValue();
/*      */   }
/*      */   
/*      */   public static boolean isPrintEnsembleModelFiles() {
/* 2723 */     return m_PrintAllModelFiles.getValue();
/*      */   }
/*      */   
/*      */   public static boolean shouldOptimizeEnsemble() {
/* 2727 */     return m_EnsembleShouldOpt.getValue();
/*      */   }
/*      */   
/*      */   public boolean shouldWritePredictionsFromEnsemble() {
/* 2731 */     return this.m_WriteEnsemblePredictions.getValue();
/*      */   }
/*      */   
/*      */   public static boolean shouldEstimateOOB() {
/* 2735 */     return m_EnsembleOOBestimate.getValue();
/*      */   }
/*      */   
/*      */   public void setOOBestimate(boolean value) {
/* 2739 */     m_EnsembleOOBestimate.setValue(value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isEnsembleRandomDepth() {
/* 2748 */     return this.m_EnsembleRandomDepth.getValue();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getEnsembleBagSize() {
/* 2757 */     return this.m_EnsembleBagSize.getValue();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setEnsembleBagSize(int value) {
/* 2766 */     this.m_EnsembleBagSize.setValue(value);
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
/*      */   public void setSectionKNNEnabled(boolean enable) {
/* 2782 */     this.m_SectionKNN.setEnabled(enable);
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
/*      */   public void setSectionKNNTEnabled(boolean enable) {
/* 2798 */     this.m_SectionKNNT.setEnabled(enable);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean XVAL_OVERLAP = true;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean IS_XVAL = false;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void create() {
/* 2816 */     INIFileSection settings = new INIFileSection("General");
/* 2817 */     settings.addNode((INIFileNode)(this.m_Verbose = new INIFileInt("Verbose", 1)));
/* 2818 */     settings.addNode((INIFileNode)(this.m_Compatibility = new INIFileNominal("Compatibility", COMPATIBILITY, 2)));
/* 2819 */     settings.addNode((INIFileNode)(this.m_RandomSeed = new INIFileString("RandomSeed", "0")));
/* 2820 */     settings.addNode((INIFileNode)(this.m_ResourceInfoLoaded = new INIFileNominal("ResourceInfoLoaded", RESOURCE_INFO_LOAD, 1)));
/*      */     
/* 2822 */     INIFileSection data = new INIFileSection("Data");
/* 2823 */     data.addNode((INIFileNode)(this.m_DataFile = new INIFileString("File", "None")));
/* 2824 */     data.addNode((INIFileNode)(this.m_TestSet = new INIFileStringOrDouble("TestSet", "None")));
/*      */ 
/*      */     
/* 2827 */     data.addNode((INIFileNode)(this.m_PruneSet = new INIFileStringOrDouble("PruneSet", "None")));
/* 2828 */     data.addNode((INIFileNode)(this.m_PruneSetMax = new INIFileStringOrInt("PruneSetMax", "Infinity")));
/* 2829 */     data.addNode((INIFileNode)(this.m_XValFolds = new INIFileStringOrInt("XVal")));
/* 2830 */     this.m_XValFolds.setIntValue(10);
/* 2831 */     data.addNode((INIFileNode)(this.m_RemoveMissingTarget = new INIFileBool("RemoveMissingTarget", false)));
/* 2832 */     data.addNode((INIFileNode)(this.m_NormalizeData = new INIFileNominal("NormalizeData", NORMALIZE_DATA_VALUES, 0)));
/*      */     
/* 2834 */     INIFileSection active = new INIFileSection("Active");
/*      */     
/* 2836 */     active.addNode((INIFileNode)(this.m_ActiveData = new INIFileString("ActiveDataset", "None")));
/*      */     
/* 2838 */     active.addNode((INIFileNode)(this.m_BudgetPerIteration = new INIFileDouble("BudgetPerIteration", 0.0D)));
/* 2839 */     active.addNode((INIFileNode)(this.m_ActiveAlgorithm = new INIFileString("ActiveAlgorithm", "None")));
/* 2840 */     active.addNode((INIFileNode)(this.m_BatchSize = new INIFileInt("BatchSize", 0)));
/* 2841 */     active.addNode((INIFileNode)(this.m_LabelInferingBatchSize = new INIFileInt("LabelInferingBatchSize", 0)));
/*      */     
/* 2843 */     active.addNode((INIFileNode)(this.m_PartialLabelling = new INIFileBool("PartialLabelling", true)));
/* 2844 */     setPartialLabelling(true);
/* 2845 */     active.addNode((INIFileNode)(this.m_InferNegativeLabels = new INIFileBool("InferNegativeLabels", false)));
/* 2846 */     active.addNode((INIFileNode)(this.m_InferPositiveLabels = new INIFileBool("InferPositiveLabels", true)));
/*      */     
/* 2848 */     active.addNode((INIFileNode)(this.m_LabelCost = new INIFileNominalOrDoubleOrVector("LabelCost", NONELIST)));
/* 2849 */     active.addNode((INIFileNode)(this.m_WriteOOBError = new INIFileBool("WriteOOBError", false)));
/*      */     
/* 2851 */     active.addNode((INIFileNode)(this.m_WriteActiveOOBErrors = new INIFileBool("WriteActiveOOBError", false)));
/*      */     
/* 2853 */     active.addNode((INIFileNode)(this.m_WriteActiveTestPredictions = new INIFileBool("WriteActiveTestPredictions", false)));
/* 2854 */     active.addNode((INIFileNode)(this.m_WriteActiveTrainPredictions = new INIFileBool("WriteActiveTrainPredictions", false)));
/* 2855 */     active.addNode((INIFileNode)(this.m_WriteQueriedInstances = new INIFileBool("WriteActiveQueriedInstances", false)));
/* 2856 */     active.addNode((INIFileNode)(this.m_WriteBudgetInformation = new INIFileBool("WriteActiveBudgetInformation", false)));
/* 2857 */     active.addNode((INIFileNode)(this.m_WriteActiveTestErrors = new INIFileBool("WriteActiveTestError", true)));
/* 2858 */     active.addNode((INIFileNode)(this.m_WriteActiveTrainErrors = new INIFileBool("WriteActiveTrainError", false)));
/* 2859 */     active.addNode((INIFileNode)(this.m_Iteration = new INIFileInt("Iteration", -1)));
/* 2860 */     active.addNode((INIFileNode)(this.m_OptimizingIterations = new INIFileInt("OptimizingIterations", -1)));
/* 2861 */     active.addNode((INIFileNode)(this.m_PopulationSize = new INIFileInt("PopulationSize", -1)));
/* 2862 */     active.addNode((INIFileNode)(this.m_Alpha = new INIFileDouble("Alpha", Double.NaN)));
/* 2863 */     active.addNode((INIFileNode)(this.m_Beta = new INIFileDouble("Beta", Double.NaN)));
/*      */     
/* 2865 */     active.addNode((INIFileNode)(this.m_Sigma = new INIFileDouble("Sigma", 1.0D)));
/*      */     
/* 2867 */     active.addNode((INIFileNode)(this.m_MaxIterations = new INIFileInt("MaxIterations", 50)));
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 2872 */     INIFileSection activeAttrs = new INIFileSection("ActiveAttributes");
/* 2873 */     activeAttrs.addNode((INIFileNode)(this.m_ActiveTarget = new INIFileString("ActiveTarget", "Default")));
/* 2874 */     activeAttrs.addNode((INIFileNode)(this.m_ActiveClustering = new INIFileString("ActiveClustering", "Default")));
/* 2875 */     activeAttrs.addNode((INIFileNode)(this.m_ActiveDescriptive = new INIFileString("ActiveDescriptive", "Default")));
/* 2876 */     activeAttrs.addNode((INIFileNode)(this.m_ActiveKey = new INIFileString("ActiveKey", "None")));
/* 2877 */     activeAttrs.addNode((INIFileNode)(this.m_ActiveDisabled = new INIFileString("ActiveDisable", "None")));
/* 2878 */     activeAttrs.addNode((INIFileNode)(this.m_ActiveWeights = new INIFileNominalOrDoubleOrVector("ActiveWeights", NORMALIZATIONS)));
/* 2879 */     this.m_ActiveWeights.setNominal(0);
/* 2880 */     activeAttrs.addNode((INIFileNode)(this.m_ActiveClusteringWeights = new INIFileNominalOrDoubleOrVector("ClusteringWeights", EMPTY)));
/* 2881 */     this.m_ActiveClusteringWeights.setDouble(1.0D);
/* 2882 */     this.m_ActiveClusteringWeights.setArrayIndexNames(NUM_NOM_TAR_NTAR_WEIGHTS);
/* 2883 */     activeAttrs.addNode((INIFileNode)(this.m_ActiveReduceMemoryNominal = new INIFileBool("ActiveReduceMemoryNominalAttrs", false)));
/* 2884 */     INIFileSection attrs = new INIFileSection("Attributes");
/* 2885 */     attrs.addNode((INIFileNode)(this.m_Target = new INIFileString("Target", "Default")));
/* 2886 */     attrs.addNode((INIFileNode)(this.m_Clustering = new INIFileString("Clustering", "Default")));
/* 2887 */     attrs.addNode((INIFileNode)(this.m_Descriptive = new INIFileString("Descriptive", "Default")));
/* 2888 */     attrs.addNode((INIFileNode)(this.m_Key = new INIFileString("Key", "None")));
/* 2889 */     attrs.addNode((INIFileNode)(this.m_Disabled = new INIFileString("Disable", "None")));
/* 2890 */     attrs.addNode((INIFileNode)(this.m_Weights = new INIFileNominalOrDoubleOrVector("Weights", NORMALIZATIONS)));
/* 2891 */     this.m_Weights.setNominal(0);
/* 2892 */     attrs.addNode((INIFileNode)(this.m_ClusteringWeights = new INIFileNominalOrDoubleOrVector("ClusteringWeights", EMPTY)));
/* 2893 */     this.m_ClusteringWeights.setDouble(1.0D);
/* 2894 */     this.m_ClusteringWeights.setArrayIndexNames(NUM_NOM_TAR_NTAR_WEIGHTS);
/* 2895 */     attrs.addNode((INIFileNode)(this.m_ReduceMemoryNominal = new INIFileBool("ReduceMemoryNominalAttrs", false)));
/*      */     
/* 2897 */     this.m_SectionSIT = new INIFileSection("SIT");
/* 2898 */     this.m_SectionSIT.addNode((INIFileNode)(this.m_MainTarget = new INIFileString("Main_target", "Default")));
/* 2899 */     this.m_SectionSIT.addNode((INIFileNode)(this.m_Recursive = new INIFileBool("Recursive", false)));
/* 2900 */     this.m_SectionSIT.addNode((INIFileNode)(this.m_Search = new INIFileString("Search", "OneTarget")));
/* 2901 */     this.m_SectionSIT.addNode((INIFileNode)(this.m_Learner = new INIFileString("Learner", "ClusLearner")));
/* 2902 */     this.m_SectionSIT.addNode((INIFileNode)(this.m_Error = new INIFileString("Error", "MSE")));
/* 2903 */     this.m_SectionSIT.setEnabled(false);
/*      */     
/* 2905 */     INIFileSection constr = new INIFileSection("Constraints");
/* 2906 */     constr.addNode((INIFileNode)(this.m_SyntacticConstrFile = new INIFileString("Syntactic", "None")));
/* 2907 */     constr.addNode((INIFileNode)(this.m_MaxSizeConstr = new INIFileNominalOrIntOrVector("MaxSize", INFINITY)));
/* 2908 */     constr.addNode((INIFileNode)(this.m_MaxErrorConstr = new INIFileNominalOrDoubleOrVector("MaxError", INFINITY)));
/* 2909 */     constr.addNode((INIFileNode)(this.m_TreeMaxDepth = new INIFileInt("MaxDepth", -1)));
/* 2910 */     this.m_MaxSizeConstr.setNominal(0);
/* 2911 */     this.m_MaxErrorConstr.setDouble(0.0D);
/*      */     
/* 2913 */     INIFileSection output = new INIFileSection("Output");
/* 2914 */     output.addNode((INIFileNode)(this.m_ShowModels = new INIFileNominal("ShowModels", SHOW_MODELS, SHOW_MODELS_VALUES)));
/* 2915 */     output.addNode((INIFileNode)(this.m_OutTrainErr = new INIFileBool("TrainErrors", true)));
/* 2916 */     output.addNode((INIFileNode)(this.m_OutValidErr = new INIFileBool("ValidErrors", true)));
/* 2917 */     output.addNode((INIFileNode)(this.m_OutTestErr = new INIFileBool("TestErrors", true)));
/* 2918 */     output.addNode((INIFileNode)(this.m_OutFoldModels = new INIFileBool("AllFoldModels", true)));
/* 2919 */     output.addNode((INIFileNode)(this.m_OutFoldErr = new INIFileBool("AllFoldErrors", false)));
/* 2920 */     output.addNode((INIFileNode)(this.m_OutFoldData = new INIFileBool("AllFoldDatasets", false)));
/* 2921 */     output.addNode((INIFileNode)(this.m_ShowUnknown = new INIFileBool("UnknownFrequency", false)));
/* 2922 */     output.addNode((INIFileNode)(this.m_ShowBrFreq = new INIFileBool("BranchFrequency", false)));
/* 2923 */     output.addNode((INIFileNode)(this.m_ShowInfo = new INIFileNominal("ShowInfo", SHOW_INFO, SHOW_INFO_VALUES)));
/* 2924 */     output.addNode((INIFileNode)(this.m_PrintModelAndExamples = new INIFileBool("PrintModelAndExamples", false)));
/* 2925 */     output.addNode((INIFileNode)(this.m_WriteErrorFile = new INIFileBool("WriteErrorFile", false)));
/* 2926 */     output.addNode((INIFileNode)(this.m_WritePredictions = new INIFileNominal("WritePredictions", WRITE_PRED, WRITE_PRED_VALUES)));
/*      */ 
/*      */     
/* 2929 */     output.addNode((INIFileNode)(this.m_ModelIDFiles = new INIFileBool("ModelIDFiles", false)));
/* 2930 */     output.addNode((INIFileNode)(this.m_WriteCurves = new INIFileBool("WriteCurves", false)));
/* 2931 */     output.addNode((INIFileNode)(this.m_OutputPythonModel = new INIFileBool("OutputPythonModel", false)));
/* 2932 */     output.addNode((INIFileNode)(this.m_OutputDatabaseQueries = new INIFileBool("OutputDatabaseQueries", false)));
/*      */     
/* 2934 */     INIFileSection nominal = new INIFileSection("Nominal");
/* 2935 */     nominal.addNode((INIFileNode)(this.m_MEstimate = new INIFileDouble("MEstimate", 1.0D)));
/*      */     
/* 2937 */     INIFileSection model = new INIFileSection("Model");
/* 2938 */     model.addNode((INIFileNode)(this.m_MinW = new INIFileDouble("MinimalWeight", 2.0D)));
/* 2939 */     model.addNode((INIFileNode)(this.m_MinNbEx = new INIFileInt("MinimalNumberExamples", 0)));
/* 2940 */     model.addNode((INIFileNode)(this.m_MinKnownW = new INIFileDouble("MinimalKnownWeight", 0.0D)));
/* 2941 */     model.addNode((INIFileNode)(this.m_TuneFolds = new INIFileString("ParamTuneNumberFolds", "10")));
/* 2942 */     model.addNode((INIFileNode)(this.m_ClassWeight = new INIFileNominalOrDoubleOrVector("ClassWeights", EMPTY)));
/* 2943 */     model.addNode((INIFileNode)(this.m_NominalSubsetTests = new INIFileBool("NominalSubsetTests", true)));
/*      */     
/* 2945 */     this.m_SectionTree = new INIFileSection("Tree");
/* 2946 */     this.m_SectionTree.addNode((INIFileNode)(this.m_Heuristic = new INIFileNominal("Heuristic", HEURISTICS, 0)));
/* 2947 */     this.m_SectionTree.addNode((INIFileNode)(this.m_PruningMethod = new INIFileNominal("PruningMethod", PRUNING_METHODS, 0)));
/* 2948 */     this.m_SectionTree.addNode((INIFileNode)(this.m_M5PruningMult = new INIFileDouble("M5PruningMult", 2.0D)));
/* 2949 */     this.m_SectionTree.addNode((INIFileNode)(this.m_1SERule = new INIFileBool("1-SE-Rule", false)));
/* 2950 */     this.m_SectionTree.addNode((INIFileNode)(this.m_FTest = new INIFileNominalOrDoubleOrVector("FTest", NONELIST)));
/* 2951 */     this.m_FTest.setDouble(1.0D);
/* 2952 */     this.m_SectionTree.addNode((INIFileNode)(this.m_BinarySplit = new INIFileBool("BinarySplit", true)));
/* 2953 */     this.m_SectionTree.addNode((INIFileNode)(this.m_RulesFromTree = new INIFileNominal("ConvertToRules", CONVERT_RULES, 0)));
/* 2954 */     this.m_SectionTree.addNode((INIFileNode)(this.m_AlternativeSplits = new INIFileBool("AlternativeSplits", false)));
/* 2955 */     this.m_SectionTree.addNode((INIFileNode)(this.m_TreeOptimize = new INIFileNominal("Optimize", TREE_OPTIMIZE_VALUES, TREE_OPTIMIZE_NONE)));
/* 2956 */     this.m_SectionTree.addNode((INIFileNode)(this.m_MSENominal = new INIFileBool("MSENominal", false)));
/* 2957 */     this.m_SectionTree.addNode((INIFileNode)(this.m_TreeSplitSampling = new INIFileInt("SplitSampling", 0)));
/* 2958 */     this.m_TreeSplitSampling.setValueCheck((ValueCheck)new IntRangeCheck(0, 2147483647));
/*      */ 
/*      */     
/* 2961 */     this.m_SectionTree.addNode((INIFileNode)(this.m_InductionOrder = new INIFileNominal("InductionOrder", INDUCTION_ORDER, 0)));
/*      */     
/* 2963 */     this.m_SectionRules = new INIFileSection("Rules");
/* 2964 */     this.m_SectionRules.addNode((INIFileNode)(this.m_CoveringMethod = new INIFileNominal("CoveringMethod", COVERING_METHODS, 0)));
/* 2965 */     this.m_SectionRules.addNode((INIFileNode)(this.m_PredictionMethod = new INIFileNominal("PredictionMethod", RULE_PREDICTION_METHODS, 0)));
/* 2966 */     this.m_SectionRules.addNode((INIFileNode)(this.m_RuleAddingMethod = new INIFileNominal("RuleAddingMethod", RULE_ADDING_METHODS, 0)));
/* 2967 */     this.m_SectionRules.addNode((INIFileNode)(this.m_CoveringWeight = new INIFileDouble("CoveringWeight", 0.1D)));
/* 2968 */     this.m_SectionRules.addNode((INIFileNode)(this.m_InstCoveringWeightThreshold = new INIFileDouble("InstCoveringWeightThreshold", 0.1D)));
/* 2969 */     this.m_SectionRules.addNode((INIFileNode)(this.m_MaxRulesNb = new INIFileInt("MaxRulesNb", 1000)));
/* 2970 */     this.m_SectionRules.addNode((INIFileNode)(this.m_HeurDispOffset = new INIFileDouble("HeurDispOffset", 0.0D)));
/* 2971 */     this.m_SectionRules.addNode((INIFileNode)(this.m_HeurCoveragePar = new INIFileDouble("HeurCoveragePar", 1.0D)));
/* 2972 */     this.m_SectionRules.addNode((INIFileNode)(this.m_HeurRuleDistPar = new INIFileDouble("HeurRuleDistPar", 0.0D)));
/* 2973 */     this.m_SectionRules.addNode((INIFileNode)(this.m_HeurPrototypeDistPar = new INIFileDouble("HeurPrototypeDistPar", 0.0D)));
/* 2974 */     this.m_SectionRules.addNode((INIFileNode)(this.m_RuleSignificanceLevel = new INIFileDouble("RuleSignificanceLevel", 0.05D)));
/* 2975 */     this.m_SectionRules.addNode((INIFileNode)(this.m_RuleNbSigAtts = new INIFileInt("RuleNbSigAtts", 0)));
/* 2976 */     this.m_SectionRules.addNode((INIFileNode)(this.m_ComputeDispersion = new INIFileBool("ComputeDispersion", false)));
/* 2977 */     this.m_SectionRules.addNode((INIFileNode)(this.m_VarBasedDispNormWeight = new INIFileDouble("VarBasedDispNormWeight", 4.0D)));
/* 2978 */     this.m_SectionRules.addNode((INIFileNode)(this.m_DispersionWeights = new INIFileNominalOrDoubleOrVector("DispersionWeights", EMPTY)));
/* 2979 */     this.m_DispersionWeights.setArrayIndexNames(NUM_NOM_TAR_NTAR_WEIGHTS);
/* 2980 */     this.m_DispersionWeights.setDoubleArray(FOUR_ONES);
/* 2981 */     this.m_DispersionWeights.setArrayIndexNames(true);
/* 2982 */     this.m_SectionRules.addNode((INIFileNode)(this.m_RandomRules = new INIFileInt("RandomRules", 0)));
/* 2983 */     this.m_SectionRules.addNode((INIFileNode)(this.m_RuleWiseErrors = new INIFileBool("PrintRuleWiseErrors", false)));
/* 2984 */     this.m_SectionRules.addNode((INIFileNode)(m_PrintAllRules = new INIFileBool("PrintAllRules", true)));
/* 2985 */     this.m_SectionRules.addNode((INIFileNode)(this.m_constrainedToFirstAttVal = new INIFileBool("ConstrainedToFirstAttVal", false)));
/* 2986 */     this.m_SectionRules.addNode((INIFileNode)(this.m_OptDEPopSize = new INIFileInt("OptDEPopSize", 500)));
/* 2987 */     this.m_SectionRules.addNode((INIFileNode)(this.m_OptDENumEval = new INIFileInt("OptDENumEval", 10000)));
/* 2988 */     this.m_SectionRules.addNode((INIFileNode)(this.m_OptDECrossProb = new INIFileDouble("OptDECrossProb", 0.3D)));
/* 2989 */     this.m_SectionRules.addNode((INIFileNode)(this.m_OptDEWeight = new INIFileDouble("OptDEWeight", 0.5D)));
/* 2990 */     this.m_SectionRules.addNode((INIFileNode)(this.m_OptDESeed = new INIFileInt("OptDESeed", 0)));
/* 2991 */     this.m_SectionRules.addNode((INIFileNode)(this.m_OptDERegulPower = new INIFileDouble("OptDERegulPower", 1.0D)));
/* 2992 */     this.m_SectionRules.addNode((INIFileNode)(this.m_OptDEProbMutationZero = new INIFileDouble("OptDEProbMutationZero", 0.0D)));
/* 2993 */     this.m_SectionRules.addNode((INIFileNode)(this.m_OptDEProbMutationNonZero = new INIFileDouble("OptDEProbMutationNonZero", 0.0D)));
/* 2994 */     this.m_SectionRules.addNode((INIFileNode)(this.m_OptRegPar = new INIFileDouble("OptRegPar", 0.0D)));
/* 2995 */     this.m_SectionRules.addNode((INIFileNode)(this.m_OptNbZeroesPar = new INIFileDouble("OptNbZeroesPar", 0.0D)));
/* 2996 */     this.m_SectionRules.addNode((INIFileNode)(this.m_OptRuleWeightThreshold = new INIFileDouble("OptRuleWeightThreshold", 0.1D)));
/* 2997 */     this.m_SectionRules.addNode((INIFileNode)(this.m_OptLossFunction = new INIFileNominal("OptDELossFunction", OPT_LOSS_FUNCTIONS, 0)));
/* 2998 */     this.m_SectionRules.addNode((INIFileNode)(this.m_OptDefaultShiftPred = new INIFileBool("OptDefaultShiftPred", true)));
/* 2999 */     this.m_SectionRules.addNode((INIFileNode)(this.m_OptAddLinearTerms = new INIFileNominal("OptAddLinearTerms", OPT_GD_ADD_LINEAR_TERMS, 0)));
/* 3000 */     this.m_SectionRules.addNode((INIFileNode)(this.m_OptNormalizeLinearTerms = new INIFileNominal("OptNormalizeLinearTerms", OPT_LINEAR_TERM_NORM_VALUES, 1)));
/* 3001 */     this.m_SectionRules.addNode((INIFileNode)(this.m_OptLinearTermsTruncate = new INIFileBool("OptLinearTermsTruncate", true)));
/* 3002 */     this.m_SectionRules.addNode((INIFileNode)(this.m_OptOmitRulePredictions = new INIFileBool("OptOmitRulePredictions", true)));
/* 3003 */     this.m_SectionRules.addNode((INIFileNode)(this.m_OptWeightGenerality = new INIFileBool("OptWeightGenerality", false)));
/*      */     
/* 3005 */     this.m_SectionRules.addNode((INIFileNode)(this.m_OptNormalization = new INIFileNominal("OptNormalization", OPT_NORMALIZATION, 1)));
/* 3006 */     this.m_SectionRules.addNode((INIFileNode)(this.m_OptHuberAlpha = new INIFileDouble("OptHuberAlpha", 0.9D)));
/* 3007 */     this.m_SectionRules.addNode((INIFileNode)(this.m_OptGDMaxIter = new INIFileInt("OptGDMaxIter", 1000)));
/*      */     
/* 3009 */     this.m_SectionRules.addNode((INIFileNode)(this.m_OptGDGradTreshold = new INIFileDouble("OptGDGradTreshold", 1.0D)));
/* 3010 */     this.m_SectionRules.addNode((INIFileNode)(this.m_OptGDStepSize = new INIFileDouble("OptGDStepSize", 0.1D)));
/* 3011 */     this.m_SectionRules.addNode((INIFileNode)(this.m_OptGDIsDynStepsize = new INIFileBool("OptGDIsDynStepsize", true)));
/* 3012 */     this.m_SectionRules.addNode((INIFileNode)(this.m_OptGDMaxNbWeights = new INIFileInt("OptGDMaxNbWeights", 0)));
/* 3013 */     this.m_SectionRules.addNode((INIFileNode)(this.m_OptGDEarlyStopAmount = new INIFileDouble("OptGDEarlyStopAmount", 0.0D)));
/* 3014 */     this.m_SectionRules.addNode((INIFileNode)(this.m_OptGDEarlyStopTreshold = new INIFileDouble("OptGDEarlyStopTreshold", 1.1D)));
/* 3015 */     this.m_SectionRules.addNode((INIFileNode)(this.m_OptGDNbOfStepSizeReduce = new INIFileStringOrInt("OptGDNbOfStepSizeReduce", "Infinity")));
/* 3016 */     this.m_SectionRules.addNode((INIFileNode)(this.m_OptGDExternalMethod = new INIFileNominal("OptGDExternalMethod", GD_EXTERNAL_METHOD_VALUES, 0)));
/* 3017 */     this.m_SectionRules.addNode((INIFileNode)(this.m_OptGDMTGradientCombine = new INIFileNominal("OptGDMTGradientCombine", OPT_GD_MT_COMBINE_GRADIENTS, 0)));
/* 3018 */     this.m_SectionRules.addNode((INIFileNode)(this.m_OptGDNbOfTParameterTry = new INIFileInt("OptGDNbOfTParameterTry", 1)));
/* 3019 */     this.m_SectionRules.addNode((INIFileNode)(this.m_OptGDEarlyTTryStop = new INIFileBool("OptGDEarlyTTryStop", true)));
/* 3020 */     this.m_SectionRules.setEnabled(false);
/*      */     
/* 3022 */     this.m_SectionHierarchical = new INIFileSection("Hierarchical");
/* 3023 */     this.m_SectionHierarchical.addNode((INIFileNode)(this.m_HierType = new INIFileNominal("Type", HIERTYPES, 0)));
/* 3024 */     this.m_SectionHierarchical.addNode((INIFileNode)(this.m_HierDistance = new INIFileNominal("Distance", HIERDIST, 0)));
/* 3025 */     this.m_SectionHierarchical.addNode((INIFileNode)(this.m_HierWType = new INIFileNominal("WType", HIERWEIGHT, 0)));
/* 3026 */     this.m_SectionHierarchical.addNode((INIFileNode)(this.m_HierWParam = new INIFileDouble("WParam", 0.75D)));
/* 3027 */     this.m_SectionHierarchical.addNode((INIFileNode)(this.m_HierSep = new INIFileString("HSeparator", ".")));
/* 3028 */     this.m_SectionHierarchical.addNode((INIFileNode)(this.m_HierEmptySetIndicator = new INIFileString("EmptySetIndicator", "n")));
/* 3029 */     this.m_SectionHierarchical.addNode((INIFileNode)(this.m_HierOptimizeErrorMeasure = new INIFileNominal("OptimizeErrorMeasure", HIERMEASURES, 3)));
/* 3030 */     this.m_SectionHierarchical.addNode((INIFileNode)(this.m_DefinitionFile = new INIFileString("DefinitionFile", "None")));
/* 3031 */     this.m_SectionHierarchical.addNode((INIFileNode)(this.m_HierNoRootPreds = new INIFileBool("NoRootPredictions", false)));
/* 3032 */     this.m_SectionHierarchical.addNode((INIFileNode)(this.m_HierPruneInSig = new INIFileDouble("PruneInSig", 0.0D)));
/* 3033 */     this.m_SectionHierarchical.addNode((INIFileNode)(this.m_HierUseBonferroni = new INIFileBool("Bonferroni", false)));
/* 3034 */     this.m_SectionHierarchical.addNode((INIFileNode)(this.m_HierSingleLabel = new INIFileBool("SingleLabel", false)));
/* 3035 */     this.m_SectionHierarchical.addNode((INIFileNode)(this.m_CalErr = new INIFileBool("CalculateErrors", true)));
/* 3036 */     this.m_SectionHierarchical.addNode((INIFileNode)(this.m_HierClassThreshold = new INIFileNominalOrDoubleOrVector("ClassificationThreshold", NONELIST)));
/* 3037 */     this.m_HierClassThreshold.setNominal(0);
/* 3038 */     this.m_SectionHierarchical.addNode((INIFileNode)(this.m_RecallValues = new INIFileNominalOrDoubleOrVector("RecallValues", NONELIST)));
/* 3039 */     this.m_RecallValues.setNominal(0);
/* 3040 */     this.m_SectionHierarchical.addNode((INIFileNode)(this.m_HierEvalClasses = new INIFileString("EvalClasses", "None")));
/* 3041 */     this.m_SectionHierarchical.addNode((INIFileNode)(m_HierUseMEstimate = new INIFileBool("MEstimate", false)));
/* 3042 */     this.m_SectionHierarchical.setEnabled(false);
/*      */     
/* 3044 */     this.m_SectionILevelC = new INIFileSection("ILevelC");
/* 3045 */     this.m_SectionILevelC.addNode((INIFileNode)(this.m_ILevelCAlpha = new INIFileDouble("Alpha", 0.5D)));
/* 3046 */     this.m_SectionILevelC.addNode((INIFileNode)(this.m_ILevelCFile = new INIFileString("File", "None")));
/* 3047 */     this.m_SectionILevelC.addNode((INIFileNode)(this.m_ILevelNbRandomConstr = new INIFileInt("NbRandomConstraints", 0)));
/* 3048 */     this.m_SectionILevelC.addNode((INIFileNode)(this.m_ILevelCCOPKMeans = new INIFileBool("RunCOPKMeans", false)));
/* 3049 */     this.m_SectionILevelC.addNode((INIFileNode)(this.m_ILevelCMPCKMeans = new INIFileBool("RunMPCKMeans", false)));
/* 3050 */     this.m_SectionILevelC.setEnabled(false);
/*      */     
/* 3052 */     this.m_SectionBeam = new INIFileSection("Beam");
/* 3053 */     this.m_SectionBeam.addNode((INIFileNode)(this.m_SizePenalty = new INIFileDouble("SizePenalty", 0.1D)));
/* 3054 */     this.m_SectionBeam.addNode((INIFileNode)(this.m_BeamWidth = new INIFileInt("BeamWidth", 10)));
/* 3055 */     this.m_SectionBeam.addNode((INIFileNode)(this.m_BeamBestN = new INIFileInt("BeamBestN", 5)));
/* 3056 */     this.m_SectionBeam.addNode((INIFileNode)(this.m_TreeMaxSize = new INIFileInt("MaxSize", -1)));
/* 3057 */     this.m_SectionBeam.addNode((INIFileNode)(this.m_BeamAttrHeuristic = new INIFileNominal("AttributeHeuristic", HEURISTICS, 0)));
/* 3058 */     this.m_SectionBeam.addNode((INIFileNode)(this.m_FastBS = new INIFileBool("FastSearch", true)));
/* 3059 */     this.m_SectionBeam.addNode((INIFileNode)(this.m_BeamPostPrune = new INIFileBool("PostPrune", false)));
/* 3060 */     this.m_SectionBeam.addNode((INIFileNode)(this.m_BMRemoveEqualHeur = new INIFileBool("RemoveEqualHeur", false)));
/* 3061 */     this.m_SectionBeam.addNode((INIFileNode)(this.m_BeamSimilarity = new INIFileDouble("BeamSimilarity", 0.0D)));
/* 3062 */     this.m_SectionBeam.addNode((INIFileNode)(this.m_BSortTrainParameter = new INIFileBool("BeamSortOnTrainParameteres", false)));
/* 3063 */     this.m_SectionBeam.addNode((INIFileNode)(this.m_BeamSyntacticConstrFile = new INIFileString("DistSyntacticConstr", "None")));
/* 3064 */     this.m_SectionBeam.addNode((INIFileNode)(this.m_BeamToForest = new INIFileBool("BeamToForest", false)));
/* 3065 */     this.m_SectionBeam.setEnabled(false);
/*      */ 
/*      */     
/* 3068 */     this.m_SectionExhaustive = new INIFileSection("Exhaustive");
/* 3069 */     this.m_SectionExhaustive.addNode((INIFileNode)(this.m_Exhaustive = new INIFileBool("Exhaustive", true)));
/* 3070 */     this.m_SectionExhaustive.addNode((INIFileNode)(this.m_StartTreeCpt = new INIFileInt("StartTreeCpt", 0)));
/* 3071 */     this.m_SectionExhaustive.addNode((INIFileNode)(this.m_StartItemCpt = new INIFileInt("StartItemCpt", 0)));
/* 3072 */     this.m_SectionExhaustive.setEnabled(false);
/*      */     
/* 3074 */     this.m_SectionTimeSeries = new INIFileSection("TimeSeries");
/* 3075 */     this.m_SectionTimeSeries.addNode((INIFileNode)(this.m_TimeSeriesDistance = new INIFileNominal("DistanceMeasure", TIME_SERIES_DISTANCE_MEASURE, 0)));
/* 3076 */     this.m_SectionTimeSeries.addNode((INIFileNode)(this.m_TimeSeriesHeuristicSampling = new INIFileNominal("PrototypeComlexity", TIME_SERIES_PROTOTYPE_COMPLEXITY, 0)));
/* 3077 */     this.m_SectionTimeSeries.setEnabled(false);
/*      */     
/* 3079 */     this.m_SectionPhylogeny = new INIFileSection("Phylogeny");
/* 3080 */     this.m_SectionPhylogeny.addNode((INIFileNode)(m_PhylogenyDM = new INIFileNominal("DistanceMeasure", PHYLOGENY_DISTANCE_MEASURE, 0)));
/* 3081 */     this.m_SectionPhylogeny.addNode((INIFileNode)(m_PhylogenyCriterion = new INIFileNominal("OptimizationCriterion", PHYLOGENY_CRITERION, 0)));
/* 3082 */     this.m_SectionPhylogeny.addNode((INIFileNode)(m_PhylogenySequence = new INIFileNominal("Sequence", PHYLOGENY_SEQUENCE, 0)));
/* 3083 */     this.m_SectionPhylogeny.addNode((INIFileNode)(m_PhylogenyDistanceMatrix = new INIFileString("DistanceMatrix", "dist")));
/* 3084 */     this.m_SectionPhylogeny.addNode((INIFileNode)(m_PhylogenyEntropyVsRootStop = new INIFileDouble("EntropyVsRootStopCriterion", 0.0D)));
/* 3085 */     this.m_SectionPhylogeny.addNode((INIFileNode)(m_PhylogenyDistancesVsRootStop = new INIFileDouble("SumPWDistancesVsRootStopCriterion", 0.0D)));
/* 3086 */     this.m_SectionPhylogeny.addNode((INIFileNode)(m_PhylogenyEntropyVsParentStop = new INIFileDouble("EntropyVsParentStopCriterion", 0.0D)));
/* 3087 */     this.m_SectionPhylogeny.addNode((INIFileNode)(m_PhylogenyDistancesVsParentStop = new INIFileDouble("SumPWDistancesVsParentStopCriterion", 0.0D)));
/* 3088 */     this.m_SectionPhylogeny.setEnabled(false);
/*      */     
/* 3090 */     this.m_SectionEnsembles = new INIFileSection("Ensemble");
/* 3091 */     this.m_SectionEnsembles.addNode((INIFileNode)(this.m_NbBags = new INIFileNominalOrIntOrVector("Iterations", NONELIST)));
/* 3092 */     this.m_SectionEnsembles.addNode((INIFileNode)(m_EnsembleMethod = new INIFileNominal("EnsembleMethod", ENSEMBLE_TYPE, 0)));
/* 3093 */     this.m_SectionEnsembles.addNode((INIFileNode)(m_ClassificationVoteType = new INIFileNominal("VotingType", VOTING_TYPE, 0)));
/* 3094 */     this.m_SectionEnsembles.addNode((INIFileNode)(this.m_RandomAttrSelected = new INIFileInt("SelectRandomSubspaces", 0)));
/* 3095 */     this.m_SectionEnsembles.addNode((INIFileNode)(m_PrintAllModels = new INIFileBool("PrintAllModels", false)));
/* 3096 */     this.m_SectionEnsembles.addNode((INIFileNode)(m_PrintAllModelFiles = new INIFileBool("PrintAllModelFiles", false)));
/* 3097 */     this.m_SectionEnsembles.addNode((INIFileNode)(m_EnsembleShouldOpt = new INIFileBool("Optimize", false)));
/* 3098 */     this.m_SectionEnsembles.addNode((INIFileNode)(m_EnsembleOOBestimate = new INIFileBool("OOBestimate", false)));
/* 3099 */     this.m_SectionEnsembles.addNode((INIFileNode)(this.m_FeatureRanking = new INIFileBool("FeatureRanking", false)));
/* 3100 */     this.m_SectionEnsembles.addNode((INIFileNode)(this.m_WriteEnsemblePredictions = new INIFileBool("WriteEnsemblePredictions", false)));
/* 3101 */     this.m_SectionEnsembles.addNode((INIFileNode)(this.m_EnsembleRandomDepth = new INIFileBool("EnsembleRandomDepth", false)));
/* 3102 */     this.m_SectionEnsembles.addNode((INIFileNode)(this.m_BagSelection = new INIFileNominalOrIntOrVector("BagSelection", NONELIST)));
/* 3103 */     this.m_BagSelection.setInt(-1);
/* 3104 */     this.m_SectionEnsembles.addNode((INIFileNode)(this.m_EnsembleBagSize = new INIFileInt("BagSize", 0)));
/* 3105 */     this.m_EnsembleBagSize.setValueCheck((ValueCheck)new IntRangeCheck(0, 2147483647));
/* 3106 */     this.m_SectionEnsembles.setEnabled(false);
/*      */     
/* 3108 */     this.m_SectionKNN = new INIFileSection("kNN");
/* 3109 */     this.m_SectionKNN.addNode((INIFileNode)(kNN_k = new INIFileInt("k", 3)));
/* 3110 */     this.m_SectionKNN.addNode((INIFileNode)(kNN_vectDist = new INIFileString("VectorDistance", "Euclidian")));
/* 3111 */     this.m_SectionKNN.addNode((INIFileNode)(kNN_distWeighted = new INIFileBool("DistanceWeighted", false)));
/* 3112 */     this.m_SectionKNN.addNode((INIFileNode)(kNN_normalized = new INIFileBool("Normalizing", true)));
/* 3113 */     this.m_SectionKNN.addNode((INIFileNode)(kNN_attrWeighted = new INIFileBool("AttributeWeighted", false)));
/* 3114 */     this.m_SectionKNN.setEnabled(false);
/*      */     
/* 3116 */     this.m_SectionKNNT = new INIFileSection("kNNTree");
/* 3117 */     this.m_SectionKNNT.addNode((INIFileNode)(kNNT_k = new INIFileInt("k", 3)));
/* 3118 */     this.m_SectionKNNT.addNode((INIFileNode)(kNNT_vectDist = new INIFileString("VectorDistance", "Euclidian")));
/* 3119 */     this.m_SectionKNNT.addNode((INIFileNode)(kNNT_distWeighted = new INIFileBool("DistanceWeighted", false)));
/* 3120 */     this.m_SectionKNNT.addNode((INIFileNode)(kNNT_normalized = new INIFileBool("Normalizing", true)));
/* 3121 */     this.m_SectionKNNT.addNode((INIFileNode)(kNNT_attrWeighted = new INIFileBool("AttributeWeighted", false)));
/* 3122 */     this.m_SectionKNNT.setEnabled(false);
/*      */     
/* 3124 */     INIFileSection exper = new INIFileSection("Experimental");
/* 3125 */     exper.addNode((INIFileNode)(this.m_SetsData = new INIFileInt("NumberBags", 25)));
/* 3126 */     exper.addNode((INIFileNode)(this.m_ShowForest = new INIFileBool("XValForest", false)));
/* 3127 */     exper.setEnabled(false);
/*      */     
/* 3129 */     this.m_Ini.addNode((INIFileNode)settings);
/* 3130 */     this.m_Ini.addNode((INIFileNode)data);
/* 3131 */     this.m_Ini.addNode((INIFileNode)attrs);
/* 3132 */     this.m_Ini.addNode((INIFileNode)active);
/*      */     
/* 3134 */     this.m_Ini.addNode((INIFileNode)constr);
/* 3135 */     this.m_Ini.addNode((INIFileNode)output);
/* 3136 */     this.m_Ini.addNode((INIFileNode)nominal);
/* 3137 */     this.m_Ini.addNode((INIFileNode)model);
/* 3138 */     this.m_Ini.addNode((INIFileNode)this.m_SectionTree);
/* 3139 */     this.m_Ini.addNode((INIFileNode)this.m_SectionRules);
/* 3140 */     this.m_Ini.addNode((INIFileNode)this.m_SectionHierarchical);
/* 3141 */     this.m_Ini.addNode((INIFileNode)this.m_SectionILevelC);
/* 3142 */     this.m_Ini.addNode((INIFileNode)this.m_SectionBeam);
/* 3143 */     this.m_Ini.addNode((INIFileNode)this.m_SectionExhaustive);
/* 3144 */     this.m_Ini.addNode((INIFileNode)this.m_SectionTimeSeries);
/* 3145 */     this.m_Ini.addNode((INIFileNode)this.m_SectionPhylogeny);
/* 3146 */     this.m_Ini.addNode((INIFileNode)this.m_SectionEnsembles);
/* 3147 */     this.m_Ini.addNode((INIFileNode)this.m_SectionKNN);
/* 3148 */     this.m_Ini.addNode((INIFileNode)this.m_SectionKNNT);
/* 3149 */     this.m_Ini.addNode((INIFileNode)exper);
/* 3150 */     this.m_Ini.addNode((INIFileNode)this.m_SectionSIT);
/*      */   }
/*      */   
/*      */   public void initNamedValues() {
/* 3154 */     this.m_TreeMaxDepth.setNamedValue(-1, "Infinity");
/* 3155 */     this.m_TreeMaxSize.setNamedValue(-1, "Infinity");
/* 3156 */     this.m_TreeSplitSampling.setNamedValue(0, "None");
/*      */   }
/*      */   
/*      */   public void updateTarget(ClusSchema schema) {
/* 3160 */     if (checkHeuristic("SSPD")) {
/* 3161 */       schema.addAttrType((ClusAttrType)new IntegerAttrType("SSPD"));
/* 3162 */       int nb = schema.getNbAttributes();
/* 3163 */       this.m_Target.setValue(String.valueOf(nb));
/*      */     } 
/*      */   }
/*      */   
/*      */   public void updateActiveTarget(ClusSchema schema) {
/* 3168 */     if (checkHeuristic("SSPD")) {
/* 3169 */       schema.addAttrType((ClusAttrType)new IntegerAttrType("SSPD"));
/* 3170 */       int nb = schema.getNbAttributes();
/* 3171 */       this.m_ActiveTarget.setValue(String.valueOf(nb));
/*      */     } 
/*      */   }
/*      */   
/*      */   public void initialize(CMDLineArgs cargs, boolean loads) throws IOException {
/* 3176 */     create();
/* 3177 */     initNamedValues();
/*      */     
/* 3179 */     if (cargs != null) {
/* 3180 */       preprocess(cargs);
/*      */     }
/* 3182 */     if (loads) {
/*      */       try {
/* 3184 */         String fname = getFileAbsolute(getAppName() + ".s");
/* 3185 */         this.m_Ini.load(fname, '%');
/*      */       }
/* 3187 */       catch (FileNotFoundException e) {
/* 3188 */         System.out.println("No settings file found");
/*      */       } 
/*      */     }
/* 3191 */     if (cargs != null) {
/* 3192 */       process(cargs);
/*      */     }
/* 3194 */     updateDataFile(getAppName() + ".arff");
/* 3195 */     initHierarchical();
/*      */   }
/*      */ 
/*      */   
/*      */   public void preprocess(CMDLineArgs cargs) {}
/*      */   
/*      */   public void process(CMDLineArgs cargs) {
/* 3202 */     if (cargs.hasOption("target")) {
/* 3203 */       this.m_Target.setValue(cargs.getOptionValue("target"));
/*      */     }
/* 3205 */     if (cargs.hasOption("disable")) {
/* 3206 */       String disarg = cargs.getOptionValue("disable");
/* 3207 */       String orig = this.m_Disabled.getValue();
/* 3208 */       if (StringUtils.unCaseCompare(orig, "None")) {
/* 3209 */         this.m_Disabled.setValue(disarg);
/*      */       } else {
/* 3211 */         this.m_Disabled.setValue(orig + "," + disarg);
/*      */       } 
/*      */     } 
/* 3214 */     if (cargs.hasOption("silent")) {
/* 3215 */       VERBOSE = 0;
/*      */     }
/*      */   }
/*      */   
/*      */   public void update(ClusSchema schema) {
/* 3220 */     setFTest(getFTest());
/* 3221 */     MINIMAL_WEIGHT = getMinimalWeight();
/* 3222 */     SHOW_UNKNOWN_FREQ = isShowUnknown();
/* 3223 */     SHOW_XVAL_FOREST = isShowXValForest();
/* 3224 */     SHOW_BRANCH_FREQ = isShowBranchFreq();
/* 3225 */     ONE_NOMINAL = (schema.getNbNominalTargetAttributes() == 1 && schema.getNbNumericTargetAttributes() == 0);
/* 3226 */     SIZE_PENALTY = getSizePenalty();
/* 3227 */     BEAM_WIDTH = this.m_BeamWidth.getValue();
/* 3228 */     BEAM_SIMILARITY = getBeamSimilarity();
/* 3229 */     BEAM_SYNT_DIST_CONSTR = hasBeamConstraintFile();
/* 3230 */     VERBOSE = this.m_Verbose.getValue();
/* 3231 */     if (isEnsembleMode()) {
/* 3232 */       updateNbRandomAttrSelected(schema);
/*      */     }
/*      */   }
/*      */   
/*      */   public int getVerbose() {
/* 3237 */     return this.m_Verbose.getValue();
/*      */   }
/*      */   
/*      */   public void updateDisabledSettings() {
/* 3241 */     int pruning = getPruningMethod();
/* 3242 */     this.m_M5PruningMult.setEnabled((pruning == 3 || pruning == 4));
/* 3243 */     this.m_PruneSetMax.setEnabled(!this.m_PruneSet.isString("None"));
/* 3244 */     this.m_1SERule.setEnabled((pruning == 7));
/* 3245 */     int heur = getHeuristic();
/* 3246 */     this.m_FTest.setEnabled((heur == 4 || heur == 5));
/* 3247 */     if (ResourceInfo.isLibLoaded()) {
/* 3248 */       this.m_ResourceInfoLoaded.setSingleValue(0);
/*      */     } else {
/* 3250 */       this.m_ResourceInfoLoaded.setSingleValue(1);
/*      */     } 
/*      */   }
/*      */   
/*      */   public void show(PrintWriter where) throws IOException {
/* 3255 */     updateDisabledSettings();
/*      */     
/* 3257 */     boolean tempInduceParamNeeded = this.m_ruleInduceParamsDisabled;
/* 3258 */     if (getCoveringMethod() == 9 && tempInduceParamNeeded) {
/* 3259 */       returnRuleInduceParams();
/*      */     }
/* 3261 */     this.m_Ini.save(where);
/* 3262 */     if (getCoveringMethod() == 9 && tempInduceParamNeeded) {
/* 3263 */       disableRuleInduceParams();
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public String getFileAbsolute(String fname) {
/* 3270 */     if (this.m_DirName == null)
/* 3271 */       return fname; 
/* 3272 */     if (FileUtil.isAbsolutePath(fname)) {
/* 3273 */       return fname;
/*      */     }
/* 3275 */     return this.m_DirName + File.separator + fname;
/*      */   }
/*      */ 
/*      */   
/*      */   public PrintWriter getFileAbsoluteWriter(String fname) throws FileNotFoundException {
/* 3280 */     String path = getFileAbsolute(fname);
/*      */     
/* 3282 */     return new PrintWriter(new OutputStreamWriter(new FileOutputStream(path)));
/*      */   }
/*      */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\main\Settings.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */