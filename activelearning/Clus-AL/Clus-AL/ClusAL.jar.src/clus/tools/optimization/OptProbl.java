/*      */ package clus.tools.optimization;
/*      */ 
/*      */ import clus.algo.rules.ClusRuleSet;
/*      */ import clus.algo.rules.RuleNormalization;
/*      */ import clus.data.rows.DataTuple;
/*      */ import clus.main.ClusStatManager;
/*      */ import clus.main.Settings;
/*      */ import clus.statistic.ClassificationStat;
/*      */ import clus.statistic.ClusStatistic;
/*      */ import clus.util.ClusFormat;
/*      */ import java.io.FileNotFoundException;
/*      */ import java.io.FileOutputStream;
/*      */ import java.io.OutputStreamWriter;
/*      */ import java.io.PrintWriter;
/*      */ import java.text.NumberFormat;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.BitSet;
/*      */ import java.util.Random;
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
/*      */ public class OptProbl
/*      */ {
/*      */   protected static final double INVALID_PREDICTION = -InfinityD;
/*      */   private int m_NumVar;
/*      */   private RulePred[] m_RulePred;
/*      */   private double[][][][] m_BaseFuncPred;
/*      */   
/*      */   public static class OptParam
/*      */   {
/*      */     public OptProbl.RulePred[] m_rulePredictions;
/*      */     public double[][][][] m_baseFuncPredictions;
/*      */     public OptProbl.TrueValues[] m_trueValues;
/*      */     
/*      */     public OptParam(int nbRule, int nbOtherBaseFunc, int nbInst, int nbTarg, ImplicitLinearTerms implicitLinTerms) {
/*      */       int jRul;
/*   69 */       for (this.m_rulePredictions = new OptProbl.RulePred[nbRule], jRul = 0; jRul < nbRule; ) { this.m_rulePredictions[jRul] = new OptProbl.RulePred(nbInst, nbTarg); jRul++; }
/*      */       
/*   71 */       this.m_baseFuncPredictions = new double[nbOtherBaseFunc][nbInst][nbTarg][1]; this.m_trueValues = new OptProbl.TrueValues[nbInst]; this.m_implicitLinearTerms = implicitLinTerms;
/*      */     } public OptParam(OptProbl.RulePred[] rulePredictions, double[][][][] predictions, OptProbl.TrueValues[] trueValues, ImplicitLinearTerms implicitLinTerms) {
/*   73 */       this.m_rulePredictions = rulePredictions; this.m_baseFuncPredictions = predictions; this.m_trueValues = trueValues; this.m_implicitLinearTerms = implicitLinTerms;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*   79 */     public ImplicitLinearTerms m_implicitLinearTerms = null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static class RulePred
/*      */   {
/*      */     public BitSet m_cover;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public double[][] m_prediction;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public RulePred(int nbInst, int nbTarg) {
/*  103 */       this.m_cover = new BitSet(nbInst); this.m_prediction = new double[nbTarg][1]; } public RulePred(BitSet cover, double[][] prediction) {
/*  104 */       this.m_cover = cover; this.m_prediction = prediction;
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
/*  132 */   private ImplicitLinearTerms m_LinTermMemSavePred = null; private TrueValues[] m_TrueVal; private ClusStatManager m_StatMgr;
/*      */   private boolean m_ClssTask;
/*      */   private double[] m_TargetAvg;
/*      */   private double[] m_TargetNormFactor;
/*      */   boolean m_saveMemoryLinears;
/*      */   
/*      */   public static class TrueValues {
/*      */     public DataTuple m_dataExample;
/*      */     public double[] m_targets;
/*      */     
/*  142 */     public TrueValues(int nbTarg) { this.m_targets = new double[nbTarg]; } public TrueValues(int nbTarg, DataTuple instance) {
/*  143 */       this.m_targets = new double[nbTarg]; this.m_dataExample = instance;
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
/*      */   public OptProbl(ClusStatManager stat_mgr, OptParam optInfo) {
/*  174 */     this.m_StatMgr = stat_mgr;
/*      */     
/*  176 */     this.m_saveMemoryLinears = (getSettings().getOptAddLinearTerms() == 2);
/*      */     
/*  178 */     if (this.m_saveMemoryLinears) {
/*  179 */       int nbOfTargetAtts = (this.m_StatMgr.getSchema().getNumericAttrUse(3)).length;
/*  180 */       int nbOfDescrAtts = (this.m_StatMgr.getSchema().getNumericAttrUse(1)).length;
/*  181 */       this.m_NumVar = optInfo.m_baseFuncPredictions.length + optInfo.m_rulePredictions.length + nbOfTargetAtts * nbOfDescrAtts;
/*      */     } else {
/*      */       
/*  184 */       this.m_NumVar = optInfo.m_baseFuncPredictions.length + optInfo.m_rulePredictions.length;
/*      */     } 
/*      */     
/*  187 */     this.m_LinTermMemSavePred = optInfo.m_implicitLinearTerms;
/*  188 */     this.m_BaseFuncPred = optInfo.m_baseFuncPredictions;
/*  189 */     this.m_RulePred = optInfo.m_rulePredictions;
/*  190 */     this.m_TrueVal = optInfo.m_trueValues;
/*      */ 
/*      */     
/*  193 */     if (ClusStatManager.getMode() != 1 && 
/*  194 */       ClusStatManager.getMode() != 0)
/*      */     {
/*  196 */       System.err.println("Weight optimization: Mixed types of targets (reg/clas) not implemented. Assuming regression.\n ");
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  203 */     this.m_ClssTask = (ClusStatManager.getMode() == 0);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  209 */     if (!this.m_ClssTask) {
/*  210 */       this.m_TargetAvg = new double[getNbOfTargets()];
/*      */ 
/*      */       
/*  213 */       if (getSettings().isOptNormalization()) {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  218 */         this.m_TargetNormFactor = initNormFactors(getNbOfTargets(), getSettings());
/*  219 */         if (getSettings().getOptNormalization() != 2) {
/*  220 */           this.m_TargetAvg = initMeans(getNbOfTargets());
/*      */         }
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected static void splitDataIntoValAndTrainSet(ClusStatManager stat_mgr, OptParam origData, OptParam valData, OptParam trainData) {
/*  229 */     Settings set = stat_mgr.getSettings();
/*  230 */     int nbRows = origData.m_trueValues.length;
/*      */     
/*  232 */     int nbDataTest = (int)Math.ceil(nbRows * set.getOptGDEarlyStopAmount());
/*      */ 
/*      */     
/*  235 */     Random randGen = new Random(0L);
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  240 */     boolean[] selectedInstances = new boolean[nbRows];
/*      */     
/*  242 */     for (int iTestSetInstance = 0; iTestSetInstance < nbDataTest; iTestSetInstance++) {
/*      */ 
/*      */ 
/*      */       
/*  246 */       int newIndex = randGen.nextInt(nbRows - iTestSetInstance);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  252 */       int iNewTestInstance = 0;
/*      */ 
/*      */       
/*  255 */       for (int indexOfUnUsedInstance = 0; indexOfUnUsedInstance < newIndex; iNewTestInstance++) {
/*  256 */         if (!selectedInstances[iNewTestInstance])
/*      */         {
/*  258 */           indexOfUnUsedInstance++;
/*      */         }
/*      */       } 
/*  261 */       while (selectedInstances[iNewTestInstance]) {
/*  262 */         iNewTestInstance++;
/*      */       }
/*      */       
/*  265 */       selectedInstances[iNewTestInstance] = true;
/*      */       
/*  267 */       valData.m_trueValues[iTestSetInstance] = origData.m_trueValues[iNewTestInstance];
/*      */       
/*  269 */       origData.m_trueValues[iNewTestInstance] = null;
/*      */ 
/*      */       
/*  272 */       for (int iNonRule = 0; iNonRule < origData.m_baseFuncPredictions.length; iNonRule++) {
/*  273 */         valData.m_baseFuncPredictions[iNonRule][iTestSetInstance] = origData.m_baseFuncPredictions[iNonRule][iNewTestInstance];
/*      */         
/*  275 */         origData.m_baseFuncPredictions[iNonRule][iNewTestInstance] = (double[][])null;
/*      */       } 
/*      */       
/*  278 */       for (int i = 0; i < origData.m_rulePredictions.length; i++) {
/*  279 */         if ((origData.m_rulePredictions[i]).m_cover.get(iNewTestInstance)) {
/*  280 */           (valData.m_rulePredictions[i]).m_cover.set(iTestSetInstance);
/*      */         }
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/*  287 */     int iInstanceRestIndex = 0;
/*  288 */     int nbOfInstances = nbRows;
/*  289 */     for (int iInstance = 0; iInstance < nbOfInstances; iInstance++) {
/*      */       
/*  291 */       if (!selectedInstances[iInstance]) {
/*      */         
/*  293 */         trainData.m_trueValues[iInstanceRestIndex] = origData.m_trueValues[iInstance];
/*      */         int i;
/*  295 */         for (i = 0; i < origData.m_baseFuncPredictions.length; i++) {
/*  296 */           trainData.m_baseFuncPredictions[i][iInstanceRestIndex] = origData.m_baseFuncPredictions[i][iInstance];
/*      */         }
/*      */         
/*  299 */         for (i = 0; i < origData.m_rulePredictions.length; i++) {
/*      */           
/*  301 */           if ((origData.m_rulePredictions[i]).m_cover.get(iInstance)) {
/*  302 */             (trainData.m_rulePredictions[i]).m_cover.set(iInstanceRestIndex);
/*      */           }
/*      */         } 
/*  305 */         iInstanceRestIndex++;
/*      */       } 
/*      */     } 
/*      */     
/*  309 */     for (int iRule = 0; iRule < origData.m_rulePredictions.length; iRule++) {
/*  310 */       (trainData.m_rulePredictions[iRule]).m_prediction = (origData.m_rulePredictions[iRule]).m_prediction;
/*  311 */       (valData.m_rulePredictions[iRule]).m_prediction = (origData.m_rulePredictions[iRule]).m_prediction;
/*      */     } 
/*      */     
/*  314 */     if (iInstanceRestIndex != trainData.m_trueValues.length) {
/*  315 */       System.err.println("GDProbl error. Wrong amount of early stop data added");
/*  316 */       System.exit(1);
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  323 */     set.setOptRegPar(0.0D);
/*  324 */     set.setOptNbZeroesPar(0.0D);
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
/*      */   public final double calcFitness(ArrayList<Double> genes) {
/*  338 */     return calcFitnessForTarget(genes, -1);
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
/*      */   public double calcFitnessForTarget(ArrayList<Double> genes, int iFitnessTarget) {
/*  351 */     ClusStatistic tar_stat = getTargetStat();
/*      */ 
/*      */     
/*  354 */     int nb_rows = getNbOfInstances();
/*  355 */     int nb_covered = 0;
/*  356 */     int nb_targets = tar_stat.getNbAttributes();
/*      */ 
/*      */     
/*  359 */     int indFirstTarget = 0;
/*  360 */     int indLastTarget = tar_stat.getNbAttributes() - 1;
/*  361 */     if (iFitnessTarget != -1) {
/*      */       
/*  363 */       indFirstTarget = iFitnessTarget;
/*  364 */       indLastTarget = iFitnessTarget;
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/*  369 */     int[] nb_values = new int[nb_targets];
/*      */ 
/*      */     
/*  372 */     for (int iTarget = indFirstTarget; iTarget <= indLastTarget; iTarget++) {
/*  373 */       if (isClassifTask()) {
/*      */         
/*  375 */         nb_values[iTarget] = ((ClassificationStat)tar_stat).getAttribute(iTarget).getNbValues();
/*      */       } else {
/*  377 */         nb_values[iTarget] = 1;
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/*  382 */     double[][] pred = new double[nb_rows][nb_targets];
/*      */ 
/*      */     
/*  385 */     for (int iInstance = 0; iInstance < nb_rows; iInstance++) {
/*      */ 
/*      */ 
/*      */       
/*  389 */       double[][] pred_sum = new double[nb_targets][];
/*      */       
/*  391 */       for (int i = indFirstTarget; i <= indLastTarget; i++) {
/*  392 */         pred_sum[i] = new double[nb_values[i]];
/*      */         
/*  394 */         if (isClassifTask()) {
/*      */           
/*  396 */           pred[iInstance][i] = Double.NEGATIVE_INFINITY;
/*  397 */           for (int iValue = 0; iValue < nb_values[i]; iValue++) {
/*  398 */             pred_sum[i][iValue] = Double.NEGATIVE_INFINITY;
/*      */           }
/*      */         }
/*      */         else {
/*      */           
/*  403 */           pred[iInstance][i] = 0.0D;
/*  404 */           pred_sum[i][0] = 0.0D;
/*      */         } 
/*      */       } 
/*      */       
/*  408 */       boolean covered = false;
/*      */ 
/*      */       
/*  411 */       for (int iRule = 0; iRule < getNumVar(); iRule++) {
/*  412 */         if (((Double)genes.get(iRule)).doubleValue() != 0.0D)
/*      */         {
/*  414 */           for (int j = indFirstTarget; j <= indLastTarget; j++) {
/*      */ 
/*      */             
/*  417 */             for (int iClass = 0; iClass < nb_values[j]; iClass++) {
/*  418 */               if (isCovered(iRule, iInstance)) {
/*  419 */                 covered = true;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */                 
/*  425 */                 pred_sum[j][iClass] = pred_sum[j][iClass] + ((Double)genes.get(iRule)).doubleValue() * 
/*      */                   
/*  427 */                   getPredictionsWhenCovered(iRule, iInstance, j, iClass);
/*      */               } 
/*      */             } 
/*      */           } 
/*      */         }
/*      */       } 
/*      */ 
/*      */ 
/*      */       
/*  436 */       if (isClassifTask()) {
/*      */         
/*  438 */         pred[iInstance] = predictClass(pred_sum);
/*      */       } else {
/*      */         
/*  441 */         pred[iInstance] = predictRegression(pred_sum);
/*      */       } 
/*      */       
/*  444 */       if (covered) {
/*  445 */         nb_covered++;
/*      */       }
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/*  451 */     double loss = 0.0D;
/*      */ 
/*      */     
/*  454 */     if (isClassifTask()) {
/*      */       
/*  456 */       if (getSettings().getOptDELossFunction() != 1) {
/*      */         
/*      */         try {
/*  459 */           throw new Exception("DE optimization task is for classification, but the chosen loss is mainly for regression. Use OptDELossFunction = 01Error to correct this.");
/*      */         }
/*  461 */         catch (Exception e) {
/*  462 */           e.printStackTrace();
/*      */           
/*  464 */           loss = loss(pred, iFitnessTarget);
/*      */         } 
/*      */       } else {
/*  467 */         loss = loss(pred, iFitnessTarget) * nb_rows / nb_covered;
/*      */       } 
/*      */     } else {
/*  470 */       loss = loss(pred, iFitnessTarget);
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/*  475 */     double reg_penalty = 0.0D;
/*  476 */     if (getSettings().getOptRegPar() != 0.0D) {
/*  477 */       reg_penalty = getSettings().getOptRegPar() * regularization(genes);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*  482 */     double nbOfZeroes_penalty = 0.0D;
/*  483 */     if (getSettings().getOptNbZeroesPar() != 0.0D) {
/*  484 */       nbOfZeroes_penalty = getSettings().getOptNbZeroesPar() * returnNbNonZeroes(genes);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  491 */     return loss + reg_penalty + nbOfZeroes_penalty;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private int returnNbNonZeroes(ArrayList<Double> genes) {
/*  498 */     int nbNonZeroes = 0;
/*      */     
/*  500 */     for (int j = 0; j < genes.size(); j++) {
/*  501 */       if (((Double)genes.get(j)).doubleValue() != 0.0D) {
/*  502 */         nbNonZeroes++;
/*      */       }
/*      */     } 
/*      */     
/*  506 */     return nbNonZeroes;
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
/*      */   private double[] predictRegression(double[][] predictionSums) {
/*  519 */     int nbOfTargets = predictionSums.length;
/*  520 */     double[] prediction = new double[nbOfTargets];
/*  521 */     for (int iTarget = 0; iTarget < nbOfTargets; iTarget++) {
/*      */       
/*  523 */       if (predictionSums[iTarget] != null)
/*      */       {
/*      */         
/*  526 */         prediction[iTarget] = predictionSums[iTarget][0];
/*      */       }
/*      */     } 
/*      */     
/*  530 */     return prediction;
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
/*      */   protected double[] predictClass(double[][] predictionSums) {
/*  542 */     int nbOfTargets = predictionSums.length;
/*  543 */     double[] prediction = new double[nbOfTargets];
/*      */     
/*  545 */     for (int iTarget = 0; iTarget < nbOfTargets; iTarget++) {
/*      */       
/*  547 */       double max = 0.0D;
/*  548 */       int iMaxClass = 0;
/*      */ 
/*      */       
/*  551 */       for (int iClass = 0; iClass < (predictionSums[iTarget]).length; iClass++) {
/*  552 */         if (predictionSums[iTarget][iClass] > max) {
/*      */ 
/*      */           
/*  555 */           iMaxClass = iClass;
/*  556 */           max = predictionSums[iTarget][iClass];
/*      */         } 
/*      */       } 
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
/*  569 */       prediction[iTarget] = iMaxClass;
/*      */     } 
/*      */     
/*  572 */     return prediction;
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
/*      */   protected double loss(double[][] prediction, int iTarget) {
/*  592 */     double loss = 0.0D;
/*  593 */     switch (getSettings().getOptDELossFunction())
/*      */     { case 1:
/*  595 */         if (iTarget != -1)
/*  596 */           System.err.println("Loss over single target implemented only for squared loss!"); 
/*  597 */         loss = loss01(getTrueValues(), prediction);
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
/*  616 */         return loss;case 2: if (iTarget != -1) System.err.println("Loss over single target implemented only for squared loss!");  loss = lossRRMSE(getTrueValues(), prediction); return loss;case 3: if (iTarget != -1) System.err.println("Loss over single target implemented only for squared loss!");  loss = lossHuber(getTrueValues(), prediction); return loss; }  loss = lossSquared(prediction, iTarget); return loss;
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
/*      */   private double lossSquared(double[][] prediction, int indTarget) {
/*  629 */     double loss = 0.0D;
/*  630 */     int numberOfInstances = prediction.length;
/*      */ 
/*      */     
/*  633 */     if (indTarget != -1) {
/*      */       
/*  635 */       for (int iInstance = 0; iInstance < numberOfInstances; iInstance++)
/*      */       {
/*  637 */         loss += Math.pow(getTrueValue(iInstance, indTarget) - prediction[iInstance][indTarget], 2.0D);
/*      */       }
/*      */     } else {
/*      */       
/*  641 */       int numberOfTargets = (prediction[0]).length;
/*  642 */       for (int jTarget = 0; jTarget < numberOfTargets; jTarget++) {
/*      */         
/*  644 */         double attributeLoss = 0.0D;
/*  645 */         for (int iInstance = 0; iInstance < numberOfInstances; iInstance++)
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*  652 */           attributeLoss += Math.pow(getTrueValue(iInstance, jTarget) - prediction[iInstance][jTarget], 2.0D);
/*      */         }
/*      */ 
/*      */         
/*  656 */         if (getSettings().isOptNormalization()) {
/*  657 */           attributeLoss /= getNormFactor(jTarget);
/*      */         }
/*      */         
/*  660 */         loss += 1.0D / (2 * numberOfTargets) * attributeLoss;
/*      */       } 
/*      */     } 
/*  663 */     return loss / numberOfInstances;
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
/*      */   private double lossRRMSE(TrueValues[] trueValue, double[][] prediction) {
/*  677 */     double loss = 0.0D;
/*  678 */     int numberOfInstances = prediction.length;
/*  679 */     int numberOfTargets = (prediction[0]).length;
/*      */     
/*  681 */     for (int jTarget = 0; jTarget < numberOfTargets; jTarget++) {
/*      */       
/*  683 */       double attributeLoss = 0.0D;
/*  684 */       double attribVariance = 0.0D;
/*  685 */       double attribMean = 0.0D;
/*      */       
/*      */       int iInstance;
/*  688 */       for (iInstance = 0; iInstance < numberOfInstances; iInstance++)
/*      */       {
/*  690 */         attribMean += (trueValue[iInstance]).m_targets[jTarget];
/*      */       }
/*  692 */       attribMean /= numberOfInstances;
/*      */       
/*  694 */       for (iInstance = 0; iInstance < numberOfInstances; iInstance++) {
/*      */         
/*  696 */         attributeLoss += Math.pow(prediction[iInstance][jTarget] - (trueValue[iInstance]).m_targets[jTarget], 2.0D);
/*  697 */         attribVariance += Math.pow(attribMean - (trueValue[iInstance]).m_targets[jTarget], 2.0D);
/*      */       } 
/*      */       
/*  700 */       loss += 1.0D / numberOfTargets * Math.sqrt(attributeLoss / attribVariance);
/*      */     } 
/*      */     
/*  703 */     return loss / numberOfInstances;
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
/*      */   private double lossHuber(TrueValues[] trueValue, double[][] prediction) {
/*  717 */     double loss = 0.0D;
/*  718 */     int numberOfInstances = prediction.length;
/*      */ 
/*      */     
/*  721 */     if (numberOfInstances == 0) {
/*  722 */       return 0.0D;
/*      */     }
/*  724 */     int numberOfTargets = (prediction[0]).length;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  729 */     double[] deltas = computeHuberDeltas(trueValue, prediction);
/*      */     
/*  731 */     for (int jTarget = 0; jTarget < numberOfTargets; jTarget++) {
/*      */       
/*  733 */       double attributeLoss = 0.0D;
/*  734 */       for (int iInstance = 0; iInstance < numberOfInstances; iInstance++) {
/*      */         
/*  736 */         if (Math.abs((trueValue[iInstance]).m_targets[jTarget] - prediction[iInstance][jTarget]) < deltas[jTarget]) {
/*  737 */           attributeLoss += Math.pow((trueValue[iInstance]).m_targets[jTarget] - prediction[iInstance][jTarget], 2.0D);
/*      */         } else {
/*  739 */           attributeLoss += deltas[jTarget] * (Math.abs((trueValue[iInstance]).m_targets[jTarget] - prediction[iInstance][jTarget]) - deltas[jTarget] / 2.0D);
/*      */         } 
/*      */       } 
/*      */ 
/*      */       
/*  744 */       loss += 1.0D / numberOfTargets * attributeLoss;
/*      */     } 
/*      */     
/*  747 */     return loss / numberOfInstances;
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
/*      */   private double[] computeHuberDeltas(TrueValues[] trueValues, double[][] predictions) {
/*  760 */     int numberOfInstances = trueValues.length;
/*  761 */     int numberOfTargets = (trueValues[0]).m_targets.length;
/*      */ 
/*      */     
/*  764 */     double alpha = getSettings().getOptHuberAlpha();
/*  765 */     double[] deltas = new double[numberOfTargets];
/*      */     
/*  767 */     double[] targetDistances = new double[numberOfInstances];
/*      */     
/*  769 */     for (int jTarget = 0; jTarget < numberOfTargets; jTarget++) {
/*  770 */       for (int iInstance = 0; iInstance < numberOfInstances; iInstance++)
/*      */       {
/*  772 */         targetDistances[iInstance] = Math.abs((trueValues[iInstance]).m_targets[jTarget] - predictions[iInstance][jTarget]);
/*      */       }
/*      */ 
/*      */       
/*  776 */       Arrays.sort(targetDistances);
/*      */ 
/*      */       
/*  779 */       deltas[jTarget] = targetDistances[(int)Math.floor(numberOfInstances * alpha)];
/*      */     } 
/*      */     
/*  782 */     return deltas;
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
/*      */   private double loss01(TrueValues[] trueValue, double[][] prediction) {
/*  796 */     int accuracy = 0;
/*  797 */     int numberOfInstances = prediction.length;
/*  798 */     int numberOfTargets = (prediction[0]).length;
/*      */     
/*  800 */     for (int jTarget = 0; jTarget < numberOfTargets; jTarget++) {
/*  801 */       for (int iInstance = 0; iInstance < numberOfInstances; iInstance++) {
/*      */ 
/*      */         
/*  804 */         if ((trueValue[iInstance]).m_targets[jTarget] == prediction[iInstance][jTarget])
/*      */         {
/*  806 */           accuracy++;
/*      */         }
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/*  812 */     return 1.0D - accuracy / (numberOfInstances * numberOfTargets);
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
/*      */   protected double regularization(ArrayList<Double> genes) {
/*  825 */     double reg_penalty = 0.0D;
/*      */     
/*  827 */     for (int j = 0; j < genes.size(); j++)
/*      */     {
/*  829 */       reg_penalty += Math.pow(Math.abs(((Double)genes.get(j)).doubleValue()), 
/*  830 */           getSettings().getOptDERegulPower());
/*      */     }
/*      */     
/*  833 */     return reg_penalty;
/*      */   }
/*      */ 
/*      */   
/*      */   public final int getNumVar() {
/*  838 */     return this.m_NumVar;
/*      */   }
/*      */   
/*      */   protected final Settings getSettings() {
/*  842 */     return this.m_StatMgr.getSettings();
/*      */   }
/*      */   
/*      */   protected final ClusStatistic getTargetStat() {
/*  846 */     return this.m_StatMgr.getStatistic(3);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final boolean isCovered(int iRule, int iInstance) {
/*  854 */     if (iRule >= this.m_RulePred.length) {
/*  855 */       return this.m_saveMemoryLinears ? true : (!Double.isNaN(this.m_BaseFuncPred[iRule - this.m_RulePred.length][iInstance][0][0]));
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  863 */     return (this.m_RulePred[iRule]).m_cover.get(iInstance);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final double getPredictionsWhenCovered(int iRule, int iInstance, int iTarget, int iClass) {
/*  874 */     if (!isRuleTerm(iRule)) {
/*  875 */       if (this.m_saveMemoryLinears) {
/*  876 */         return this.m_LinTermMemSavePred.predict(iRule - this.m_RulePred.length, (this.m_TrueVal[iInstance]).m_dataExample, iTarget, (this.m_RulePred[0]).m_prediction.length);
/*      */       }
/*      */ 
/*      */       
/*  880 */       return this.m_BaseFuncPred[iRule - this.m_RulePred.length][iInstance][iTarget][iClass];
/*      */     } 
/*      */     
/*  883 */     return (this.m_RulePred[iRule]).m_prediction[iTarget][iClass];
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected final boolean isRuleTerm(int index) {
/*  889 */     return (index < this.m_RulePred.length);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final double getPredictionsWhenCovered(int iRule, int iInstance, int iTarget) {
/*  896 */     return getPredictionsWhenCovered(iRule, iInstance, iTarget, 0);
/*      */   }
/*      */ 
/*      */   
/*      */   protected final double getTrueValue(int iInstance, int iTarget) {
/*  901 */     return (this.m_TrueVal[iInstance]).m_targets[iTarget] - getMean(iTarget);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final TrueValues[] getTrueValues() {
/*  911 */     return this.m_TrueVal;
/*      */   }
/*      */   
/*      */   protected final boolean isClassifTask() {
/*  915 */     return this.m_ClssTask;
/*      */   }
/*      */   
/*      */   protected final int getNbOfInstances() {
/*  919 */     return this.m_TrueVal.length;
/*      */   }
/*      */   
/*      */   protected int getNbOfTargets() {
/*  923 */     return getTargetStat().getNbAttributes();
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected final void changeData(OptParam newData) {
/*  929 */     this.m_BaseFuncPred = newData.m_baseFuncPredictions;
/*  930 */     this.m_RulePred = newData.m_rulePredictions;
/*      */     
/*  932 */     this.m_TrueVal = newData.m_trueValues;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected final boolean isValidValue(double pred) {
/*  938 */     return (!Double.isInfinite(pred) && !Double.isNaN(pred));
/*      */   }
/*      */ 
/*      */   
/*      */   protected static double[] initMeans(int nbTargs) {
/*  943 */     double[] means = new double[nbTargs];
/*  944 */     for (int iTarget = 0; iTarget < nbTargs; iTarget++) {
/*  945 */       means[iTarget] = RuleNormalization.getTargMean(iTarget);
/*      */     }
/*  947 */     return means;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected static double[] initNormFactors(int nbTargs, Settings sett) {
/*  954 */     double[] scaleFactor = new double[nbTargs];
/*  955 */     for (int iTarget = 0; iTarget < nbTargs; iTarget++) {
/*  956 */       if (sett.getOptNormalization() == 3) {
/*  957 */         scaleFactor[iTarget] = Math.pow(RuleNormalization.getTargStdDev(iTarget), 4.0D);
/*      */       } else {
/*  959 */         scaleFactor[iTarget] = 4.0D * Math.pow(RuleNormalization.getTargStdDev(iTarget), 2.0D);
/*      */       } 
/*  961 */     }  return scaleFactor;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final double getNormFactor(int iTarget) {
/*  969 */     return this.m_TargetNormFactor[iTarget];
/*      */   }
/*      */   
/*      */   protected final double getMean(int iAttr) {
/*  973 */     return this.m_TargetAvg[iAttr];
/*      */   }
/*      */ 
/*      */   
/*      */   protected final BitSet getRuleCovers(int iRule) {
/*  978 */     return (this.m_RulePred[iRule]).m_cover;
/*      */   }
/*      */ 
/*      */   
/*      */   protected final int getRuleNextCovered(int iRule, int iFromIndex) {
/*  983 */     return (this.m_RulePred[iRule]).m_cover.nextSetBit(iFromIndex);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final int getLinTargetDim(int iLinTerm) {
/*  992 */     return (iLinTerm - this.m_RulePred.length) % getNbOfTargets();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final int getLinDescrDim(int iLinTerm) {
/* 1000 */     return (int)Math.floor((iLinTerm - this.m_RulePred.length) / getNbOfTargets());
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void preparePredictionsForNormalization() {
/* 1006 */     if (!getSettings().isOptNormalization() || 
/* 1007 */       getSettings().getOptNormalization() == 2) {
/*      */       return;
/*      */     }
/*      */ 
/*      */     
/* 1012 */     for (int iTarg = 0; iTarg < getNbOfTargets(); iTarg++) {
/* 1013 */       if (getPredictionsWhenCovered(0, 0, iTarg) != getMean(iTarg)) {
/* 1014 */         System.err.println("Error: Difference in preparePredictionsForNormalization for target nb " + iTarg + ". The values are " + 
/* 1015 */             getPredictionsWhenCovered(0, 0, iTarg) + " and " + 
/* 1016 */             getMean(iTarg));
/* 1017 */         System.exit(1);
/*      */       } 
/* 1019 */       (this.m_RulePred[0]).m_prediction[iTarg][0] = Math.sqrt(getNormFactor(iTarg));
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1025 */     if (GDProbl.m_printGDDebugInformation) {
/* 1026 */       String fname = getSettings().getDataFile();
/*      */       
/* 1028 */       PrintWriter wrt_pred = null;
/* 1029 */       PrintWriter wrt_true = null;
/*      */       try {
/* 1031 */         wrt_pred = new PrintWriter(new OutputStreamWriter(new FileOutputStream(fname + ".gd-pred")));
/* 1032 */         wrt_true = new PrintWriter(new OutputStreamWriter(new FileOutputStream(fname + ".gd-true")));
/* 1033 */       } catch (FileNotFoundException e) {
/* 1034 */         e.printStackTrace();
/* 1035 */         System.exit(1);
/*      */       } 
/*      */       
/* 1038 */       printPredictionsToFile(wrt_pred);
/* 1039 */       wrt_pred.close();
/* 1040 */       printTrueValuesToFile(wrt_true);
/* 1041 */       wrt_true.close();
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   protected void changeRuleSetToUndoNormNormalization(ClusRuleSet rset) {
/* 1047 */     if (!getSettings().isOptNormalization() || 
/* 1048 */       getSettings().getOptNormalization() == 2) {
/*      */       return;
/*      */     }
/* 1051 */     double[] newPred = new double[getNbOfTargets()];
/* 1052 */     for (int iTarg = 0; iTarg < getNbOfTargets(); iTarg++)
/*      */     {
/* 1054 */       newPred[iTarg] = getPredictionsWhenCovered(0, 0, iTarg) * rset.getRule(0).getOptWeight() + 
/* 1055 */         getMean(iTarg);
/*      */     }
/* 1057 */     rset.getRule(0).setNumericPrediction(newPred);
/* 1058 */     rset.getRule(0).setOptWeight(1.0D);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private String printPred(int ruleIndex, int exampleIndex) {
/* 1064 */     NumberFormat fr = ClusFormat.THREE_AFTER_DOT;
/* 1065 */     String print = "[";
/* 1066 */     for (int iTarg = 0; iTarg < getNbOfTargets(); iTarg++) {
/* 1067 */       double pred = getPredictionsWhenCovered(ruleIndex, exampleIndex, iTarg);
/* 1068 */       if (getSettings().isOptNormalization()) {
/* 1069 */         pred /= Math.sqrt(getNormFactor(iTarg));
/*      */       }
/* 1071 */       print = print + "" + fr.format(pred);
/* 1072 */       if (iTarg != getNbOfTargets() - 1)
/* 1073 */         print = print + "; "; 
/*      */     } 
/* 1075 */     print = print + "]";
/* 1076 */     return print;
/*      */   }
/*      */ 
/*      */   
/*      */   protected void printPredictionsToFile(PrintWriter wrt) {
/* 1081 */     if (getSettings().isOptNormalization()) {
/* 1082 */       wrt.print("Norm factors: [");
/* 1083 */       for (int iTarget = 0; iTarget < getNbOfTargets(); iTarget++) {
/* 1084 */         wrt.print(getNormFactor(iTarget));
/* 1085 */         if (iTarget != getNbOfTargets() - 1)
/* 1086 */           wrt.print("; "); 
/*      */       } 
/* 1088 */       wrt.print("]\n");
/*      */     } 
/* 1090 */     for (int iRule = 0; iRule < getNumVar(); iRule++) {
/* 1091 */       if (iRule < this.m_RulePred.length) {
/* 1092 */         wrt.print("Rule nb " + iRule + ": ");
/* 1093 */         wrt.print(printPred(iRule, 0));
/*      */       } else {
/* 1095 */         wrt.print("Term nb " + iRule + ": ");
/*      */         
/* 1097 */         for (int iInstance = 0; iInstance < getNbOfInstances(); iInstance++) {
/* 1098 */           wrt.print(isCovered(iRule, iInstance) ? printPred(iRule, 0) : "[NA]");
/*      */         }
/*      */       } 
/* 1101 */       wrt.print("\n");
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void printTrueValuesToFile(PrintWriter wrt) {
/* 1109 */     NumberFormat fr = ClusFormat.THREE_AFTER_DOT;
/* 1110 */     for (int iTrueVal = 0; iTrueVal < getNbOfInstances(); iTrueVal++) {
/*      */       
/* 1112 */       wrt.print("[");
/* 1113 */       for (int iTarg = 0; iTarg < getNbOfTargets(); iTarg++) {
/* 1114 */         double val = getTrueValue(iTrueVal, iTarg);
/* 1115 */         if (getSettings().isOptNormalization()) {
/* 1116 */           val /= Math.sqrt(getNormFactor(iTarg));
/*      */         }
/* 1118 */         wrt.print(fr.format(val));
/* 1119 */         if (iTarg != getNbOfTargets() - 1)
/* 1120 */           wrt.print("; "); 
/*      */       } 
/* 1122 */       wrt.print("]\n");
/*      */     } 
/*      */     
/* 1125 */     wrt.print("\n");
/*      */   }
/*      */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\tools\optimization\OptProbl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */