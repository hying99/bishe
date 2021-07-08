/*     */ package clus.algo.rules;
/*     */ 
/*     */ import clus.data.rows.DataTuple;
/*     */ import clus.data.rows.RowData;
/*     */ import clus.data.type.NumericAttrType;
/*     */ import clus.main.ClusStatManager;
/*     */ import clus.main.Settings;
/*     */ import clus.statistic.ClusStatistic;
/*     */ import clus.statistic.RegressionStat;
/*     */ import clus.statistic.StatisticPrintInfo;
/*     */ import clus.tools.optimization.ImplicitLinearTerms;
/*     */ import java.io.PrintWriter;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ClusRuleLinearTerm
/*     */   extends ClusRule
/*     */ {
/*     */   public static final long serialVersionUID = 1L;
/*  57 */   private static double[] C_maxValues = null;
/*     */   
/*  59 */   private static double[] C_minValues = null;
/*  60 */   private static ClusStatManager C_statManager = null;
/*     */ 
/*     */   
/*  63 */   private static ImplicitLinearTerms C_implicitTerms = null;
/*     */ 
/*     */   
/*     */   public static void initializeClass(RowData data, ClusStatManager statMgr) {
/*  67 */     C_statManager = statMgr;
/*     */ 
/*     */     
/*  70 */     double[][] linearTermsMinAndMaxes = calcMinAndMaxForTheSet(data, C_statManager.getSchema().getNumericAttrUse(1));
/*  71 */     C_minValues = linearTermsMinAndMaxes[0];
/*  72 */     C_maxValues = linearTermsMinAndMaxes[1];
/*     */     
/*  74 */     C_implicitTerms = null;
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
/*     */   private static double getOffSetValue(int iDescAttr) {
/*  96 */     return RuleNormalization.getDescMean(iDescAttr);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static double getDescStdDev(int iDescAttr) {
/* 102 */     return RuleNormalization.getDescStdDev(iDescAttr);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static double getTargStdDev(int iTargAttr) {
/* 108 */     return RuleNormalization.getTargStdDev(iTargAttr);
/*     */   }
/*     */ 
/*     */   
/*     */   private static double getMaxValue(int iDescAttr) {
/* 113 */     return C_maxValues[iDescAttr];
/*     */   }
/*     */ 
/*     */   
/*     */   private static double getMinValue(int iDescAttr) {
/* 118 */     return C_minValues[iDescAttr];
/*     */   }
/*     */ 
/*     */   
/*     */   public static ClusRule createLinTerm(int iDescriptDim, int iTargetDim, double weight) {
/* 123 */     ClusRuleLinearTerm newTerm = new ClusRuleLinearTerm(C_statManager, iDescriptDim, iTargetDim);
/* 124 */     newTerm.setOptWeight(weight);
/* 125 */     return newTerm;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected static ImplicitLinearTerms returnImplicitLinearTermsIfNeeded(RowData data) {
/* 132 */     if (data.getSchema().getSettings().getOptAddLinearTerms() != 2) {
/* 133 */       return null;
/*     */     }
/* 135 */     double[][] values = new double[2][];
/*     */ 
/*     */ 
/*     */     
/* 139 */     values[0] = C_maxValues;
/* 140 */     values[1] = C_minValues;
/* 141 */     C_implicitTerms = new ImplicitLinearTerms(data, C_statManager);
/* 142 */     return C_implicitTerms;
/*     */   }
/*     */ 
/*     */   
/*     */   protected static void DeleteImplicitLinearTerms() {
/* 147 */     C_implicitTerms.DeleteImplicitLinearTerms();
/* 148 */     C_implicitTerms = null;
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
/* 163 */   private int m_descriptiveDimForLinearTerm = 0;
/*     */   
/* 165 */   private int m_targetDimForLinearTerm = 0;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean m_scaleLinearTerm = false;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ClusRuleLinearTerm(ClusStatManager statManager, int iDescriptDim, int iTargetDim) {
/* 181 */     super(statManager);
/*     */     
/* 183 */     this.m_descriptiveDimForLinearTerm = iDescriptDim;
/* 184 */     this.m_targetDimForLinearTerm = iTargetDim;
/* 185 */     this.m_scaleLinearTerm = statManager.getSettings().isOptNormalizeLinearTerms();
/*     */ 
/*     */     
/* 188 */     this.m_TargetStat = statManager.createTargetStat();
/*     */     
/* 190 */     int nbTargets = statManager.getStatistic(3).getNbAttributes();
/* 191 */     if (!(this.m_TargetStat instanceof RegressionStat)) {
/* 192 */       System.err.println("Error: Using linear terms is implemented for regression only.");
/*     */     }
/* 194 */     RegressionStat stat = (RegressionStat)this.m_TargetStat;
/* 195 */     stat.m_Means = new double[nbTargets];
/* 196 */     stat.m_Means[iTargetDim] = 1.0D;
/* 197 */     stat.m_NbAttrs = nbTargets;
/* 198 */     stat.m_SumValues = new double[nbTargets];
/* 199 */     stat.m_SumWeights = new double[nbTargets];
/* 200 */     stat.m_SumValues[iTargetDim] = 1.0D;
/* 201 */     stat.m_SumWeights[iTargetDim] = 1.0D;
/*     */   }
/*     */ 
/*     */   
/*     */   public ClusStatistic predictWeighted(DataTuple tuple) {
/* 206 */     if (!(this.m_TargetStat instanceof RegressionStat)) {
/* 207 */       System.err.println("Error: Using linear terms for optimization is implemented for regression only.");
/*     */     }
/* 209 */     RegressionStat stat = (RegressionStat)this.m_TargetStat;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 214 */     double pred = attributeToLinTermPrediction(getSettings(), tuple, this.m_descriptiveDimForLinearTerm, this.m_targetDimForLinearTerm, stat.m_NbAttrs, this.m_scaleLinearTerm);
/*     */ 
/*     */     
/* 217 */     if (Double.isNaN(pred)) {
/*     */       
/* 219 */       for (int i = 0; i < stat.m_NbAttrs; i++) {
/* 220 */         stat.m_Means[i] = Double.NaN;
/* 221 */         stat.m_SumValues[i] = Double.NaN;
/* 222 */         stat.m_SumWeights[i] = 1.0D;
/*     */       } 
/*     */     } else {
/*     */       
/* 226 */       for (int i = 0; i < stat.m_NbAttrs; i++) {
/* 227 */         stat.m_Means[i] = 0.0D;
/* 228 */         stat.m_SumValues[i] = stat.m_Means[i];
/* 229 */         stat.m_SumWeights[i] = 1.0D;
/*     */       } 
/* 231 */       stat.m_Means[this.m_targetDimForLinearTerm] = pred;
/* 232 */       stat.m_SumValues[this.m_targetDimForLinearTerm] = stat.m_Means[this.m_targetDimForLinearTerm];
/*     */     } 
/*     */     
/* 235 */     return this.m_TargetStat;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static double attributeToLinTermPrediction(Settings sett, DataTuple tuple, int iDescrDim, int iTarDim, int nbOfTargets, boolean scaleLinearTerm) {
/* 244 */     double descrValue = C_statManager.getSchema().getNumericAttrUse(1)[iDescrDim].getNumeric(tuple);
/*     */     
/* 246 */     if (Double.isNaN(descrValue) || Double.isInfinite(descrValue)) {
/* 247 */       if (sett.getOptNormalizeLinearTerms() == 2) {
/* 248 */         descrValue = getOffSetValue(iDescrDim);
/*     */       } else {
/* 250 */         descrValue = Double.NaN;
/*     */       } 
/*     */     }
/*     */     
/* 254 */     if (sett.isOptLinearTermsTruncate() && 
/* 255 */       !Double.isNaN(getMaxValue(iDescrDim)) && 
/* 256 */       !Double.isNaN(getMinValue(iDescrDim))) {
/* 257 */       descrValue = Math.max(Math.min(descrValue, getMaxValue(iDescrDim)), 
/* 258 */           getMinValue(iDescrDim));
/*     */     }
/*     */ 
/*     */     
/* 262 */     if (sett.isOptNormalizeLinearTerms() && scaleLinearTerm) {
/* 263 */       descrValue -= getOffSetValue(iDescrDim);
/* 264 */       descrValue /= 2.0D * getDescStdDev(iDescrDim);
/*     */ 
/*     */       
/* 267 */       descrValue *= 2.0D * getTargStdDev(iTarDim);
/*     */     } 
/*     */ 
/*     */     
/* 271 */     return descrValue;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean covers(DataTuple tuple) {
/* 276 */     if (getSettings().getOptNormalizeLinearTerms() == 2) {
/* 277 */       return true;
/*     */     }
/*     */ 
/*     */     
/* 281 */     double value = C_statManager.getSchema().getNumericAttrUse(1)[this.m_descriptiveDimForLinearTerm].getNumeric(tuple);
/* 282 */     return (!Double.isNaN(value) && !Double.isInfinite(value));
/*     */   }
/*     */   
/*     */   public void printModel(PrintWriter wrt, StatisticPrintInfo info) {
/* 286 */     wrt.println("Linear term for the numerical attribute with index " + this.m_descriptiveDimForLinearTerm + " predicting target index " + this.m_targetDimForLinearTerm);
/*     */ 
/*     */     
/* 289 */     if (getSettings().isOptLinearTermsTruncate()) {
/* 290 */       wrt.println("The prediction is truncated on the interval [" + C_minValues[this.m_descriptiveDimForLinearTerm] + "," + C_maxValues[this.m_descriptiveDimForLinearTerm] + "].");
/*     */     }
/*     */ 
/*     */     
/* 294 */     if (getSettings().getOptNormalizeLinearTerms() == 2) {
/*     */       
/* 296 */       wrt.println("Linear term prediction was scaled and shifted by (x-average)*(standard deviation of target)/(standard deviation of descriptive) during normalization.");
/* 297 */     } else if (getSettings().getOptNormalizeLinearTerms() == 1) {
/*     */       
/* 299 */       wrt.println("Linear term prediction is scaled and shifted by (x-average)*(standard deviation of target)/(standard deviation of descriptive)");
/*     */     } 
/* 301 */     if (getSettings().isOptNormalizeLinearTerms()) {
/* 302 */       wrt.println("      Standard deviation (targ) : " + getTargStdDev(this.m_targetDimForLinearTerm));
/* 303 */       wrt.println("      Standard deviation (descr): " + getDescStdDev(this.m_descriptiveDimForLinearTerm));
/* 304 */       wrt.println("      Average                   : " + getOffSetValue(this.m_descriptiveDimForLinearTerm));
/*     */     } 
/*     */     
/* 307 */     commonPrintForRuleTypes(wrt, info);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isRegularRule() {
/* 312 */     return false;
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
/*     */   private static double[][] calcMinAndMaxForTheSet(RowData data, NumericAttrType[] numTypes) {
/* 341 */     double[] mins = new double[numTypes.length];
/* 342 */     double[] maxs = new double[numTypes.length];
/*     */     
/* 344 */     double[] nbOfValidValues = new double[numTypes.length];
/*     */ 
/*     */     
/* 347 */     for (int i = 0; i < numTypes.length; i++) {
/* 348 */       mins[i] = Double.POSITIVE_INFINITY;
/* 349 */       maxs[i] = Double.NEGATIVE_INFINITY;
/*     */     } 
/*     */ 
/*     */     
/* 353 */     for (int iRow = 0; iRow < data.getNbRows(); iRow++) {
/* 354 */       DataTuple tuple = data.getTuple(iRow);
/*     */       
/* 356 */       for (int jNumAttrib = 0; jNumAttrib < numTypes.length; jNumAttrib++) {
/* 357 */         double value = numTypes[jNumAttrib].getNumeric(tuple);
/* 358 */         if (!Double.isNaN(value) && !Double.isInfinite(value)) {
/* 359 */           if (value > maxs[jNumAttrib])
/* 360 */             maxs[jNumAttrib] = value; 
/* 361 */           if (value < mins[jNumAttrib])
/* 362 */             mins[jNumAttrib] = value; 
/* 363 */           nbOfValidValues[jNumAttrib] = nbOfValidValues[jNumAttrib] + 1.0D;
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/* 368 */     for (int iDim = 0; iDim < numTypes.length; iDim++) {
/*     */ 
/*     */       
/* 371 */       if (mins[iDim] == Double.POSITIVE_INFINITY && maxs[iDim] == Double.NEGATIVE_INFINITY) {
/* 372 */         maxs[iDim] = Double.NaN; mins[iDim] = Double.NaN;
/*     */       } 
/*     */     } 
/*     */     
/* 376 */     double[][] minAndMax = new double[2][];
/* 377 */     minAndMax[0] = mins;
/* 378 */     minAndMax[1] = maxs;
/* 379 */     return minAndMax;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double[] convertToPlainTerm(double[] addToDefaultPred, double defaultWeight) {
/* 493 */     addToDefaultPred[this.m_targetDimForLinearTerm] = addToDefaultPred[this.m_targetDimForLinearTerm] - 
/* 494 */       getOptWeight() * getOffSetValue(this.m_descriptiveDimForLinearTerm) * 2.0D * 
/* 495 */       getTargStdDev(this.m_targetDimForLinearTerm) / defaultWeight * 2.0D * 
/* 496 */       getDescStdDev(this.m_descriptiveDimForLinearTerm);
/*     */ 
/*     */     
/* 499 */     setOptWeight(getOptWeight() * 2.0D * getTargStdDev(this.m_targetDimForLinearTerm) / 2.0D * 
/* 500 */         getDescStdDev(this.m_descriptiveDimForLinearTerm));
/*     */ 
/*     */     
/* 503 */     this.m_scaleLinearTerm = false;
/*     */     
/* 505 */     return addToDefaultPred;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\algo\rules\ClusRuleLinearTerm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */