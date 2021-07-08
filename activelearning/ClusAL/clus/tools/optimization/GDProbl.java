/*     */ package clus.tools.optimization;
/*     */ 
/*     */ import clus.main.ClusStatManager;
/*     */ import clus.util.ClusFormat;
/*     */ import java.io.PrintWriter;
/*     */ import java.text.NumberFormat;
/*     */ import java.util.ArrayList;
/*     */ import java.util.BitSet;
/*     */ import java.util.LinkedList;
/*     */ import java.util.ListIterator;
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
/*     */ public class GDProbl
/*     */   extends OptProbl
/*     */ {
/*     */   protected static boolean m_printGDDebugInformation = false;
/*     */   protected double[][] m_covariances;
/*     */   protected boolean[] m_isCovComputed;
/*     */   protected boolean[] m_isWeightNonZero;
/*     */   protected int m_nbOfNonZeroRules;
/*     */   protected double[] m_predCovWithTrue;
/*     */   protected double[] m_gradients;
/*     */   protected int[] m_bannedWeights;
/*     */   protected double m_stepSize;
/*     */   protected OptProbl m_earlyStopProbl;
/*     */   double m_dynStepLowerBound;
/*     */   protected double m_minFitness;
/*     */   protected ArrayList<Double> m_minFitWeights;
/*     */   
/*     */   public GDProbl(ClusStatManager stat_mgr, OptProbl.OptParam optInfo) {
/* 124 */     super(stat_mgr, optInfo);
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
/* 175 */     this.m_dynStepLowerBound = 0.0D; preparePredictionsForNormalization(); if (getSettings().getOptGDEarlyStopAmount() > 0.0D) {
/*     */       int nbDataTest = (int)Math.ceil(getNbOfInstances() * getSettings().getOptGDEarlyStopAmount()); OptProbl.OptParam dataEarlyStop = new OptProbl.OptParam(optInfo.m_rulePredictions.length, optInfo.m_baseFuncPredictions.length, nbDataTest, getNbOfTargets(), optInfo.m_implicitLinearTerms); OptProbl.OptParam trainingSet = new OptProbl.OptParam(optInfo.m_rulePredictions.length, optInfo.m_baseFuncPredictions.length, getNbOfInstances() - nbDataTest, getNbOfTargets(), optInfo.m_implicitLinearTerms); splitDataIntoValAndTrainSet(stat_mgr, optInfo, dataEarlyStop, trainingSet); changeData(trainingSet); this.m_earlyStopProbl = new OptProbl(stat_mgr, dataEarlyStop);
/*     */     }  int nbWeights = getNumVar(); this.m_covariances = new double[nbWeights][nbWeights]; for (int i = 0; i < nbWeights; i++) {
/*     */       for (int j = 0; j < nbWeights; j++)
/*     */         this.m_covariances[i][j] = Double.NaN; 
/*     */     }  this.m_isCovComputed = new boolean[nbWeights]; initPredictorVsTrueValuesCovariances();
/*     */     if (getSettings().isOptGDIsDynStepsize())
/* 182 */       computeDynStepSize();  } public void initGDForNewRunWithSamePredictions() { int nbWeights = getNumVar();
/*     */     
/* 184 */     if (getSettings().getOptGDEarlyStopAmount() > 0.0D) {
/* 185 */       this.m_minFitness = Double.POSITIVE_INFINITY;
/* 186 */       this.m_minFitWeights = new ArrayList<>(getNumVar());
/*     */       
/* 188 */       for (int iWeight = 0; iWeight < getNumVar(); iWeight++)
/*     */       {
/* 190 */         this.m_minFitWeights.add(new Double(0.0D));
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 195 */     this.m_isWeightNonZero = new boolean[nbWeights];
/*     */     
/* 197 */     if (getSettings().getOptGDMTGradientCombine() == 2) {
/* 198 */       this.m_bannedWeights = new int[nbWeights];
/*     */     } else {
/* 200 */       this.m_bannedWeights = null;
/*     */     } 
/* 202 */     this.m_gradients = new double[nbWeights];
/*     */     
/* 204 */     this.m_nbOfNonZeroRules = 0;
/* 205 */     this.m_stepSize = getSettings().getOptGDStepSize();
/*     */ 
/*     */     
/* 208 */     if (getSettings().isOptGDIsDynStepsize()) {
/* 209 */       this.m_stepSize = this.m_dynStepLowerBound;
/*     */     } }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void computeDynStepSize() {
/* 217 */     for (int dimension = 0; dimension < getNumVar(); dimension++) {
/* 218 */       this.m_covariances[dimension][dimension] = computeCovFor2Preds(dimension, dimension);
/*     */     }
/*     */ 
/*     */     
/* 222 */     double sum = 0.0D;
/* 223 */     for (int i = 0; i < getNumVar(); i++) {
/* 224 */       sum += getWeightCov(i, i);
/*     */     }
/* 226 */     this.m_dynStepLowerBound = 1.0D / sum;
/* 227 */     if (m_printGDDebugInformation) System.out.println("DEBUG: DynStepSize lower bound is " + this.m_dynStepLowerBound);
/*     */   
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
/*     */   protected ArrayList<Double> getInitialWeightVector() {
/* 241 */     ArrayList<Double> result = new ArrayList<>(getNumVar());
/* 242 */     for (int i = 0; i < getNumVar(); i++) {
/* 243 */       result.add(new Double(0.0D));
/*     */     }
/* 245 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   protected final double getCovForPrediction(int iPred) {
/* 250 */     return this.m_predCovWithTrue[iPred];
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void initPredictorVsTrueValuesCovariances() {
/* 257 */     this.m_predCovWithTrue = new double[getNumVar()];
/*     */ 
/*     */     
/* 260 */     for (int iPred = 0; iPred < getNumVar(); iPred++) {
/* 261 */       this.m_predCovWithTrue[iPred] = computePredVsTrueValueCov(iPred);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private double computePredVsTrueValueCov(int iPred) {
/* 269 */     double[] covs = new double[getNbOfTargets()];
/* 270 */     int nbOfTargets = getNbOfTargets();
/* 271 */     for (int iTarget = 0; iTarget < nbOfTargets; iTarget++) {
/* 272 */       for (int iInstance = 0; iInstance < getNbOfInstances(); iInstance++) {
/* 273 */         double trueVal = getTrueValue(iInstance, iTarget);
/* 274 */         if (isValidValue(trueVal))
/*     */         {
/* 276 */           covs[iTarget] = covs[iTarget] + trueVal * 
/* 277 */             predictWithRule(iPred, iInstance, iTarget);
/*     */         }
/*     */       } 
/* 280 */       covs[iTarget] = covs[iTarget] / getNbOfInstances();
/* 281 */       if (getSettings().isOptNormalization()) {
/* 282 */         covs[iTarget] = covs[iTarget] / getNormFactor(iTarget);
/*     */       }
/*     */     } 
/*     */     
/* 286 */     double avgCov = 0.0D;
/* 287 */     for (int i = 0; i < nbOfTargets; i++) {
/* 288 */       avgCov += covs[i] / nbOfTargets;
/*     */     }
/* 290 */     return avgCov;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final double getWeightCov(int iFirst, int iSecond) {
/* 298 */     int min = Math.min(iFirst, iSecond);
/* 299 */     int max = Math.max(iFirst, iSecond);
/*     */     
/* 301 */     if (Double.isNaN(this.m_covariances[min][max]))
/* 302 */       throw new Error("Asked covariance not yet computed. Something wrong in the covariances in GDProbl."); 
/* 303 */     return this.m_covariances[min][max];
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
/*     */   private void computeWeightCov(int dimension) {
/* 317 */     for (int iMin = 0; iMin < dimension; iMin++) {
/*     */ 
/*     */       
/* 320 */       if (!this.m_isCovComputed[iMin])
/*     */       {
/*     */         
/* 323 */         this.m_covariances[iMin][dimension] = computeCovFor2Preds(iMin, dimension);
/*     */       }
/*     */     } 
/*     */     
/* 327 */     this.m_covariances[dimension][dimension] = computeCovFor2Preds(dimension, dimension);
/* 328 */     for (int iMax = dimension + 1; iMax < getNumVar(); iMax++) {
/* 329 */       if (!this.m_isCovComputed[iMax])
/*     */       {
/*     */         
/* 332 */         this.m_covariances[dimension][iMax] = computeCovFor2Preds(dimension, iMax);
/*     */       }
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
/*     */   private double computeCovFor2Preds(int iPrevious, int iLatter) {
/* 349 */     if (isRuleTerm(iLatter))
/*     */     {
/* 351 */       return computeCovFor2Rules(iPrevious, iLatter); } 
/* 352 */     if (isRuleTerm(iPrevious))
/*     */     {
/* 354 */       return computeCovForRuleAndLin(iPrevious, iLatter);
/*     */     }
/*     */     
/* 357 */     return computeCovFor2Lin(iPrevious, iLatter);
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
/*     */   private double computeCovFor2Lin(int iPrevious, int iLatter) {
/* 407 */     int nbOfInstances = getNbOfInstances();
/* 408 */     int nbOfTargets = getNbOfTargets();
/*     */ 
/*     */     
/* 411 */     int iTarget = getLinTargetDim(iPrevious);
/*     */     
/* 413 */     if (iTarget != getLinTargetDim(iLatter)) return 0.0D;
/*     */ 
/*     */     
/* 416 */     double avgCov = 0.0D;
/*     */     
/* 418 */     for (int iInstance = 0; iInstance < nbOfInstances; iInstance++) {
/* 419 */       avgCov += predictWithRule(iPrevious, iInstance, iTarget) * 
/* 420 */         predictWithRule(iLatter, iInstance, iTarget);
/*     */     }
/* 422 */     avgCov /= (nbOfTargets * getNbOfInstances());
/*     */     
/* 424 */     if (getSettings().isOptNormalization()) {
/* 425 */       avgCov /= getNormFactor(iTarget);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 431 */     return avgCov;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private double computeCovForRuleAndLin(int iRule, int iLinear) {
/* 440 */     int nbOfTargets = getNbOfTargets();
/*     */ 
/*     */     
/* 443 */     int iTarget = getLinTargetDim(iLinear);
/*     */     
/* 445 */     double avgCov = 0.0D;
/*     */ 
/*     */     
/* 448 */     int iInstance = getRuleNextCovered(iRule, 0);
/* 449 */     for (; iInstance >= 0; iInstance = getRuleNextCovered(iRule, iInstance + 1)) {
/* 450 */       avgCov += getPredictionsWhenCovered(iLinear, iInstance, iTarget);
/*     */     }
/*     */     
/* 453 */     avgCov *= getPredictionsWhenCovered(iRule, 0, iTarget);
/*     */     
/* 455 */     avgCov /= (nbOfTargets * getNbOfInstances());
/*     */     
/* 457 */     if (getSettings().isOptNormalization()) {
/* 458 */       avgCov /= getNormFactor(iTarget);
/*     */     }
/* 460 */     return avgCov;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private double computeCovFor2Rules(int iPrevious, int iLatter) {
/* 471 */     BitSet prev = (BitSet)getRuleCovers(iPrevious).clone();
/* 472 */     BitSet latter = getRuleCovers(iLatter);
/* 473 */     prev.and(latter);
/*     */     
/* 475 */     int nbOfTargets = getNbOfTargets();
/* 476 */     double avgCov = 0.0D;
/*     */     
/* 478 */     for (int iTarget = 0; iTarget < nbOfTargets; iTarget++) {
/* 479 */       double cov = 0.0D;
/* 480 */       cov += getPredictionsWhenCovered(iPrevious, 0, iTarget) * 
/* 481 */         getPredictionsWhenCovered(iLatter, 0, iTarget);
/*     */       
/* 483 */       if (getSettings().isOptNormalization()) {
/* 484 */         cov /= getNormFactor(iTarget);
/*     */       }
/* 486 */       avgCov += cov / nbOfTargets;
/*     */     } 
/* 488 */     avgCov *= prev.cardinality() / getNbOfInstances();
/*     */     
/* 490 */     return avgCov;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final double predictWithRule(int iRule, int iInstance, int iTarget) {
/* 498 */     return isCovered(iRule, iInstance) ? getPredictionsWhenCovered(iRule, iInstance, iTarget) : 0.0D;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void fullGradientComputation(ArrayList<Double> weights) {
/* 504 */     for (int iWeight = 0; iWeight < weights.size(); iWeight++) {
/* 505 */       this.m_gradients[iWeight] = getGradient(iWeight, weights);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected double getGradient(int iWeightDim, ArrayList<Double> weights) {
/* 513 */     double gradient = 0.0D;
/* 514 */     switch (getSettings().getOptGDLossFunction()) {
/*     */       
/*     */       case 1:
/*     */       case 2:
/*     */       case 3:
/*     */         
/*     */         try {
/*     */ 
/*     */           
/* 523 */           throw new Exception("0/1 or Huber loss function not yet implemented for Gradient descent.\nUsing squared loss.\n");
/*     */         }
/* 525 */         catch (Exception s) {
/* 526 */           s.printStackTrace();
/*     */           break;
/*     */         } 
/*     */     } 
/*     */     
/* 531 */     gradient = gradientSquared(iWeightDim, weights);
/*     */ 
/*     */ 
/*     */     
/* 535 */     return gradient;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private double gradientSquared(int iGradWeightDim, ArrayList<Double> weights) {
/* 546 */     double gradient = getCovForPrediction(iGradWeightDim);
/*     */     
/* 548 */     for (int iWeight = 0; iWeight < getNumVar(); iWeight++) {
/* 549 */       if (this.m_isWeightNonZero[iWeight]) {
/* 550 */         gradient -= ((Double)weights.get(iWeight)).doubleValue() * 
/* 551 */           getWeightCov(iWeight, iGradWeightDim);
/*     */       }
/*     */     } 
/*     */     
/* 555 */     return gradient;
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
/*     */   protected final void modifyGradients(int[] changedWeightIndex, ArrayList<Double> weights) {
/* 574 */     modifyGradientSquared(changedWeightIndex);
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
/*     */   public void modifyGradientSquared(int[] iChangedWeights) {
/* 589 */     double[] oldGradsOfChanged = new double[iChangedWeights.length];
/*     */     
/* 591 */     for (int iCopy = 0; iCopy < iChangedWeights.length; iCopy++) {
/* 592 */       oldGradsOfChanged[iCopy] = this.m_gradients[iChangedWeights[iCopy]];
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 600 */     boolean firstLinearTermReached = false;
/* 601 */     int nbOfTargs = getNbOfTargets();
/* 602 */     int nbOfChanged = iChangedWeights.length;
/* 603 */     int nbOfGrads = this.m_gradients.length;
/*     */ 
/*     */     
/* 606 */     for (int iiAffecting = 0; iiAffecting < nbOfChanged; iiAffecting++) {
/* 607 */       if (!firstLinearTermReached && 
/* 608 */         !isRuleTerm(iChangedWeights[iiAffecting])) {
/* 609 */         firstLinearTermReached = true;
/*     */       }
/* 611 */       boolean secondLinearTermReached = false;
/* 612 */       double stepAmount = this.m_stepSize * oldGradsOfChanged[iiAffecting];
/*     */       
/* 614 */       for (int iWeightChange = 0; iWeightChange < nbOfGrads; iWeightChange++) {
/* 615 */         this.m_gradients[iWeightChange] = this.m_gradients[iWeightChange] - getWeightCov(iChangedWeights[iiAffecting], iWeightChange) * stepAmount;
/*     */         
/* 617 */         if (firstLinearTermReached) {
/* 618 */           if (secondLinearTermReached) {
/*     */             
/* 620 */             iWeightChange += nbOfTargs - 1;
/* 621 */           } else if (!isRuleTerm(iWeightChange)) {
/*     */ 
/*     */ 
/*     */             
/* 625 */             iWeightChange += (getLinTargetDim(iChangedWeights[iiAffecting]) + nbOfTargs - 1) % nbOfTargs;
/*     */             
/* 627 */             secondLinearTermReached = true;
/*     */           } 
/*     */         }
/*     */       } 
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
/*     */   public int[] getMaxGradients(int nbOfIterations) {
/* 643 */     int maxElements = getSettings().getOptGDMaxNbWeights();
/* 644 */     boolean maxNbOfWeightReached = false;
/* 645 */     if (maxElements > 0 && this.m_nbOfNonZeroRules >= maxElements)
/*     */     {
/*     */ 
/*     */       
/* 649 */       maxNbOfWeightReached = true;
/*     */     }
/*     */     
/* 652 */     double maxGrad = 0.0D;
/* 653 */     for (int iGrad = 0; iGrad < this.m_gradients.length; iGrad++) {
/* 654 */       if (this.m_bannedWeights == null || this.m_bannedWeights[iGrad] <= nbOfIterations)
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 660 */         if (Math.abs(this.m_gradients[iGrad]) > maxGrad && (!maxNbOfWeightReached || this.m_isWeightNonZero[iGrad] || iGrad == 0))
/*     */         {
/* 662 */           maxGrad = Math.abs(this.m_gradients[iGrad]); } 
/*     */       }
/*     */     } 
/* 665 */     ArrayList<Integer> iMaxGradients = new ArrayList<>();
/*     */ 
/*     */     
/* 668 */     double minAllowed = getSettings().getOptGDGradTreshold() * maxGrad;
/*     */ 
/*     */     
/* 671 */     for (int iCopy = 0; iCopy < this.m_gradients.length; iCopy++) {
/* 672 */       if (this.m_bannedWeights == null || this.m_bannedWeights[iCopy] <= nbOfIterations)
/*     */       {
/*     */ 
/*     */         
/* 676 */         if ((Math.abs(this.m_gradients[iCopy]) >= minAllowed && (!maxNbOfWeightReached || this.m_isWeightNonZero[iCopy])) || iCopy == 0) {
/*     */           
/* 678 */           iMaxGradients.add(Integer.valueOf(iCopy));
/*     */ 
/*     */           
/* 681 */           if (getSettings().getOptGDGradTreshold() == 1.0D && iCopy != 0) {
/*     */             break;
/*     */           }
/*     */         } 
/*     */       }
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 691 */     if (maxElements > 0 && !maxNbOfWeightReached && getSettings().getOptGDGradTreshold() < 1.0D) {
/*     */ 
/*     */ 
/*     */       
/* 695 */       int nbOfOldGrads = 0;
/*     */ 
/*     */       
/* 698 */       for (int j = 0; j < iMaxGradients.size(); j++) {
/* 699 */         if (this.m_isWeightNonZero[((Integer)iMaxGradients.get(j)).intValue()] || ((Integer)iMaxGradients.get(j)).intValue() == 0) {
/* 700 */           nbOfOldGrads++;
/*     */         }
/*     */       } 
/*     */ 
/*     */ 
/*     */       
/* 706 */       int nbOfAllowedNewGradients = maxElements - this.m_nbOfNonZeroRules;
/* 707 */       if (nbOfAllowedNewGradients < iMaxGradients.size() - nbOfOldGrads) {
/*     */ 
/*     */ 
/*     */         
/* 711 */         LinkedList<Integer> iAllowedNewMaxGradients = new LinkedList<>();
/*     */         
/* 713 */         for (int k = 0; k < iMaxGradients.size(); k++) {
/*     */ 
/*     */           
/* 716 */           if (!this.m_isWeightNonZero[((Integer)iMaxGradients.get(k)).intValue()] && ((Integer)iMaxGradients.get(k)).intValue() != 0) {
/*     */ 
/*     */             
/* 719 */             ListIterator<Integer> iAllowed = iAllowedNewMaxGradients.listIterator();
/* 720 */             while (iAllowed.hasNext()) {
/*     */               
/* 722 */               if (Math.abs(this.m_gradients[((Integer)iAllowed.next()).intValue()]) < 
/* 723 */                 Math.abs(this.m_gradients[((Integer)iMaxGradients.get(k)).intValue()])) {
/* 724 */                 iAllowed.previous();
/*     */                 
/*     */                 break;
/*     */               } 
/*     */             } 
/*     */             
/* 730 */             iAllowed.add(iMaxGradients.get(k));
/* 731 */             iMaxGradients.remove(k);
/* 732 */             k--;
/*     */ 
/*     */             
/* 735 */             if (iAllowedNewMaxGradients.size() > nbOfAllowedNewGradients) {
/* 736 */               iAllowedNewMaxGradients.removeLast();
/*     */             }
/*     */           } 
/*     */         } 
/*     */ 
/*     */ 
/*     */         
/* 743 */         ListIterator<Integer> iList = iAllowedNewMaxGradients.listIterator();
/*     */         
/* 745 */         int addedElements = 0;
/* 746 */         for (; addedElements < nbOfAllowedNewGradients; addedElements++) {
/* 747 */           iMaxGradients.add(iList.next());
/*     */         }
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 756 */     int[] iMaxGradientsArray = new int[iMaxGradients.size()];
/* 757 */     for (int i = 0; i < iMaxGradients.size(); i++) {
/* 758 */       iMaxGradientsArray[i] = ((Integer)iMaxGradients.get(i)).intValue();
/*     */     }
/* 760 */     return iMaxGradientsArray;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final double howMuchWeightChanges(int iTargetWeight) {
/* 769 */     return this.m_stepSize * this.m_gradients[iTargetWeight];
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void computeCovariancesIfNeeded(int iWeight) {
/* 775 */     if (!this.m_isCovComputed[iWeight]) {
/* 776 */       computeWeightCov(iWeight);
/* 777 */       this.m_isCovComputed[iWeight] = true;
/*     */     } 
/*     */     
/* 780 */     if (!this.m_isWeightNonZero[iWeight]) {
/* 781 */       this.m_isWeightNonZero[iWeight] = true;
/*     */ 
/*     */       
/* 784 */       if (iWeight != 0) {
/* 785 */         this.m_nbOfNonZeroRules++;
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public final void dropStepSize(double amount) {
/* 792 */     if (amount >= 1.0D) {
/* 793 */       System.err.println("Something wrong with dropStepSize. Argument >= 1.");
/*     */     }
/*     */     
/* 796 */     this.m_stepSize *= amount;
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
/*     */   public double getBestFitness() {
/* 810 */     return this.m_minFitness;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isEarlyStop(ArrayList<Double> weights) {
/* 817 */     double newFitness = this.m_earlyStopProbl.calcFitness(weights);
/*     */     
/* 819 */     if (newFitness < this.m_minFitness) {
/* 820 */       this.m_minFitness = newFitness;
/*     */       
/* 822 */       for (int iWeight = 0; iWeight < weights.size(); iWeight++)
/*     */       {
/* 824 */         this.m_minFitWeights.set(iWeight, Double.valueOf(((Double)weights.get(iWeight)).doubleValue()));
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 829 */     boolean stop = false;
/*     */     
/* 831 */     if (newFitness > getSettings().getOptGDEarlyStopTreshold() * this.m_minFitness) {
/* 832 */       stop = true;
/* 833 */       if (m_printGDDebugInformation) {
/* 834 */         System.out.println("\nGD: Independent test set error increase detected - overfitting.\n");
/*     */       }
/*     */     } 
/* 837 */     return stop;
/*     */   }
/*     */ 
/*     */   
/*     */   public void restoreBestWeight(ArrayList<Double> targetWeights) {
/* 842 */     for (int iWeight = 0; iWeight < targetWeights.size(); iWeight++)
/*     */     {
/* 844 */       targetWeights.set(iWeight, Double.valueOf(((Double)this.m_minFitWeights.get(iWeight)).doubleValue()));
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
/*     */   public static int randDepthWighExponentialDistribution(double unifRand, int avgDepth) {
/* 864 */     int maxDepths = 0;
/*     */     
/* 866 */     if (unifRand == 0.0D) {
/*     */       
/* 868 */       maxDepths = -1;
/*     */     } else {
/* 870 */       int avgNbLeaves = (int)Math.pow(2.0D, avgDepth);
/*     */       
/* 872 */       double terminalNodes = 2.0D + (2 - avgNbLeaves) / Math.log((avgNbLeaves - 2)) * Math.log(unifRand);
/* 873 */       maxDepths = (int)Math.ceil(Math.log(terminalNodes) / Math.log(2.0D));
/*     */     } 
/*     */     
/* 876 */     return maxDepths;
/*     */   }
/*     */ 
/*     */   
/*     */   public void printGradientsToFile(int iterNro, PrintWriter wrt) {
/* 881 */     if (!m_printGDDebugInformation) {
/*     */       return;
/*     */     }
/* 884 */     NumberFormat fr = ClusFormat.SIX_AFTER_DOT;
/* 885 */     wrt.print("Iteration " + iterNro + ":");
/* 886 */     for (int i = 0; i < this.m_gradients.length; i++) {
/* 887 */       wrt.print(fr.format(this.m_gradients[i]) + "\t");
/*     */     }
/* 889 */     wrt.print("\n");
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\tools\optimization\GDProbl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */