/*     */ package clus.tools.optimization;
/*     */ 
/*     */ import clus.algo.rules.ClusRuleSet;
/*     */ import clus.main.ClusStatManager;
/*     */ import clus.main.Settings;
/*     */ import java.util.ArrayList;
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
/*     */ public class CallExternGD
/*     */ {
/*     */   static {
/*  33 */     System.loadLibrary("GDInterface");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ArrayList<Double> main(ClusStatManager clusStatManager, OptProbl.OptParam optInfo, ClusRuleSet rset) {
/*  43 */     int nbOfWeights = optInfo.m_rulePredictions.length;
/*  44 */     int nbOfRules = nbOfWeights;
/*     */ 
/*     */ 
/*     */     
/*  48 */     Settings set = clusStatManager.getSettings();
/*  49 */     int nbTargs = clusStatManager.getStatistic(3).getNbAttributes();
/*     */     
/*  51 */     int nbDescrForDataMatrix = 0;
/*  52 */     int nbRows = optInfo.m_trueValues.length;
/*     */ 
/*     */     
/*  55 */     if (set.getOptAddLinearTerms() == 2) {
/*  56 */       nbDescrForDataMatrix = (clusStatManager.getSchema().getNumericAttrUse(1)).length;
/*  57 */       nbOfWeights += nbDescrForDataMatrix * nbTargs;
/*     */     } 
/*     */ 
/*     */     
/*  61 */     double[] normFactors = OptProbl.initNormFactors(nbTargs, set);
/*  62 */     double[] targetAvg = OptProbl.initMeans(nbTargs);
/*     */ 
/*     */ 
/*     */     
/*  66 */     for (int iTarg = 0; iTarg < nbTargs; iTarg++) {
/*  67 */       if ((optInfo.m_rulePredictions[0]).m_prediction[iTarg][0] != targetAvg[iTarg]) {
/*  68 */         System.err.println("Error: Difference in main for target nb " + iTarg + ". The values are " + (optInfo.m_rulePredictions[0]).m_prediction[iTarg][0] + " and " + targetAvg[iTarg]);
/*     */ 
/*     */         
/*  71 */         System.exit(1);
/*     */       } 
/*  73 */       (optInfo.m_rulePredictions[0]).m_prediction[iTarg][0] = Math.sqrt(normFactors[iTarg]);
/*     */     } 
/*     */     
/*  76 */     OptProbl.OptParam trainingSet = optInfo;
/*  77 */     OptProbl.OptParam validationSet = null;
/*     */     
/*  79 */     if (set.getOptGDEarlyStopAmount() > 0.0D) {
/*     */       
/*  81 */       int nbDataTest = (int)Math.ceil(nbRows * set.getOptGDEarlyStopAmount());
/*     */ 
/*     */       
/*  84 */       validationSet = new OptProbl.OptParam(optInfo.m_rulePredictions.length, optInfo.m_baseFuncPredictions.length, nbDataTest, nbTargs, optInfo.m_implicitLinearTerms);
/*     */ 
/*     */ 
/*     */       
/*  88 */       trainingSet = new OptProbl.OptParam(optInfo.m_rulePredictions.length, optInfo.m_baseFuncPredictions.length, nbRows - nbDataTest, nbTargs, optInfo.m_implicitLinearTerms);
/*     */ 
/*     */ 
/*     */       
/*  92 */       OptProbl.splitDataIntoValAndTrainSet(clusStatManager, optInfo, validationSet, trainingSet);
/*     */     } 
/*     */     
/*  95 */     double[] weights = new double[nbOfWeights];
/*     */ 
/*     */     
/*  98 */     double[] rulePreds = new double[nbOfRules * nbTargs];
/*  99 */     for (int iRule = 0; iRule < nbOfRules; iRule++) {
/* 100 */       for (int k = 0; k < nbTargs; k++)
/*     */       {
/* 102 */         rulePreds[iRule * nbTargs + k] = (trainingSet.m_rulePredictions[iRule]).m_prediction[k][0] / Math.sqrt(normFactors[k]);
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 107 */     boolean[] ruleCovers = new boolean[nbOfRules * nbRows];
/*     */     
/* 109 */     int nbInstTrain = (trainingSet.m_rulePredictions[0]).m_cover.length();
/* 110 */     int nbInstVal = (validationSet.m_rulePredictions[0]).m_cover.length();
/* 111 */     for (int j = 0; j < nbOfRules; j++) {
/*     */       
/* 113 */       int k = 0;
/*     */       
/* 115 */       int m = 0;
/* 116 */       int n = nbInstTrain;
/* 117 */       OptProbl.OptParam optParam = trainingSet;
/*     */ 
/*     */ 
/*     */       
/* 121 */       for (; m < n; m++, k++) {
/* 122 */         ruleCovers[j * nbRows + k] = (optParam.m_rulePredictions[j]).m_cover.get(m);
/* 123 */         if (m == nbInstTrain - 1) {
/* 124 */           m = -1;
/* 125 */           optParam = validationSet;
/* 126 */           n = nbInstVal;
/*     */         } 
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 132 */     double[] binData = new double[nbRows * (nbTargs + nbDescrForDataMatrix)];
/*     */ 
/*     */ 
/*     */     
/* 136 */     int iIndex = 0;
/*     */     
/* 138 */     int iInst = 0;
/* 139 */     int iMaxInst = nbInstTrain;
/* 140 */     OptProbl.OptParam targetData = trainingSet;
/*     */     
/* 142 */     for (; iInst < iMaxInst; iInst++, iIndex++) {
/*     */ 
/*     */       
/* 145 */       for (int iDescrDim = 0; iDescrDim < nbDescrForDataMatrix; iDescrDim++) {
/* 146 */         binData[iIndex * (nbTargs + nbDescrForDataMatrix) + iDescrDim] = targetData.m_implicitLinearTerms
/*     */           
/* 148 */           .predict(iDescrDim * nbTargs, (targetData.m_trueValues[iInst]).m_dataExample, 0, nbTargs) / 
/*     */           
/* 150 */           Math.sqrt(normFactors[0]);
/*     */       }
/*     */       
/* 153 */       for (int iTarDim = 0; iTarDim < nbTargs; iTarDim++) {
/* 154 */         binData[iIndex * (nbTargs + nbDescrForDataMatrix) + nbDescrForDataMatrix + iTarDim] = ((targetData.m_trueValues[iInst]).m_targets[iTarDim] - targetAvg[iTarDim]) / 
/*     */           
/* 156 */           Math.sqrt(normFactors[iTarDim]);
/*     */       }
/*     */ 
/*     */       
/* 160 */       if (iInst == nbInstTrain - 1) {
/* 161 */         iInst = -1;
/* 162 */         targetData = validationSet;
/* 163 */         iMaxInst = nbInstVal;
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 170 */     CallExternGD mappedFile = new CallExternGD();
/*     */     
/* 172 */     String settings = "";
/* 173 */     if (set.getOptAddLinearTerms() == 2)
/* 174 */       settings = settings + "linTermsUsed 1\n"; 
/* 175 */     settings = settings + "nbOfTargs " + nbTargs + "\nnbTrainData " + nbInstTrain + "\nnbValData " + nbInstVal + "\nnbOfRules " + nbOfRules + "\nnbOfDescrAttr " + nbDescrForDataMatrix;
/*     */     
/* 177 */     settings = settings + "\nnbOfIterations " + set.getOptGDMaxIter() + "\nminTVal " + set.getOptGDGradTreshold();
/* 178 */     settings = settings + "\nnbOfDiffTVal " + set.getOptGDNbOfTParameterTry() + "\nnbNonZeroWeights " + set.getOptGDMaxNbWeights() + "\n";
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 183 */     weights = mappedFile.externalOptim(settings, binData, rulePreds, ruleCovers);
/*     */ 
/*     */     
/* 186 */     weights[0] = undoNormalization(weights[0], rulePreds, nbTargs, targetAvg, rset, normFactors);
/*     */     
/* 188 */     ArrayList<Double> result = new ArrayList<>(nbOfWeights);
/* 189 */     for (int i = 0; i < nbOfWeights; i++) {
/* 190 */       result.add(Double.valueOf(weights[i]));
/*     */     }
/* 192 */     return result;
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
/*     */   private static final double undoNormalization(double defaultRuleWeights, double[] defaultRulePreds, int nbTargs, double[] targMeans, ClusRuleSet rset, double[] normFactors) {
/* 219 */     double[] newDefault = new double[nbTargs];
/* 220 */     for (int iTarg = 0; iTarg < nbTargs; iTarg++)
/*     */     {
/* 222 */       newDefault[iTarg] = defaultRulePreds[iTarg] * Math.sqrt(normFactors[iTarg]) * defaultRuleWeights + targMeans[iTarg];
/*     */     }
/*     */ 
/*     */     
/* 226 */     rset.getRule(0).setNumericPrediction(newDefault);
/* 227 */     return 1.0D;
/*     */   }
/*     */   
/*     */   native double[] externalOptim(String paramString, double[] paramArrayOfdouble1, double[] paramArrayOfdouble2, boolean[] paramArrayOfboolean);
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\tools\optimization\CallExternGD.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */