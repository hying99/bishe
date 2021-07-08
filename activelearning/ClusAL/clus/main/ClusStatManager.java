/*      */ package clus.main;
/*      */ import clus.algo.rules.ClusRuleHeuristicDispersionAdt;
/*      */ import clus.algo.rules.ClusRuleHeuristicDispersionMlt;
/*      */ import clus.algo.rules.ClusRuleHeuristicError;
/*      */ import clus.algo.rules.ClusRuleHeuristicRDispersionAdt;
/*      */ import clus.algo.rules.ClusRuleHeuristicRDispersionMlt;
/*      */ import clus.algo.rules.ClusRuleHeuristicSSD;
/*      */ import clus.data.ClusData;
/*      */ import clus.data.attweights.ClusAttributeWeights;
/*      */ import clus.data.attweights.ClusNormalizedAttributeWeights;
/*      */ import clus.data.rows.DataPreprocs;
/*      */ import clus.data.rows.RowData;
/*      */ import clus.data.type.ClusAttrType;
/*      */ import clus.data.type.ClusSchema;
/*      */ import clus.data.type.NominalAttrType;
/*      */ import clus.data.type.NumericAttrType;
/*      */ import clus.data.type.TimeSeriesAttrType;
/*      */ import clus.error.AbsoluteError;
/*      */ import clus.error.Accuracy;
/*      */ import clus.error.AvgDistancesError;
/*      */ import clus.error.ClusError;
/*      */ import clus.error.ClusErrorList;
/*      */ import clus.error.ICVPairwiseDistancesError;
/*      */ import clus.error.MSError;
/*      */ import clus.error.MisclassificationError;
/*      */ import clus.error.RMSError;
/*      */ import clus.error.multiscore.MultiScore;
/*      */ import clus.ext.beamsearch.ClusBeamHeuristicError;
/*      */ import clus.ext.beamsearch.ClusBeamHeuristicMEstimate;
/*      */ import clus.ext.beamsearch.ClusBeamHeuristicSS;
/*      */ import clus.ext.beamsearch.ClusBeamSimRegrStat;
/*      */ import clus.ext.hierarchical.ClassHierarchy;
/*      */ import clus.ext.hierarchical.ClassesAttrType;
/*      */ import clus.ext.hierarchical.ClassesTuple;
/*      */ import clus.ext.hierarchical.HierClassTresholdPruner;
/*      */ import clus.ext.hierarchical.HierClassWiseAccuracy;
/*      */ import clus.ext.hierarchical.HierJaccardDistance;
/*      */ import clus.ext.hierarchical.HierRemoveInsigClasses;
/*      */ import clus.ext.hierarchical.HierSingleLabelStat;
/*      */ import clus.ext.hierarchical.HierSumPairwiseDistancesStat;
/*      */ import clus.ext.hierarchical.WHTDStatistic;
/*      */ import clus.ext.ilevelc.ILevelCRandIndex;
/*      */ import clus.ext.ilevelc.ILevelCStatistic;
/*      */ import clus.ext.semisupervised.ModifiedGainHeuristic;
/*      */ import clus.ext.semisupervised.SemiSupMinLabeledWeightStopCrit;
/*      */ import clus.ext.sspd.SSPDMatrix;
/*      */ import clus.ext.timeseries.DTWTimeSeriesDist;
/*      */ import clus.ext.timeseries.QDMTimeSeriesDist;
/*      */ import clus.ext.timeseries.TSCTimeSeriesDist;
/*      */ import clus.ext.timeseries.TimeSeriesStat;
/*      */ import clus.heuristic.ClusHeuristic;
/*      */ import clus.heuristic.ClusStopCriterion;
/*      */ import clus.heuristic.ClusStopCriterionMinNbExamples;
/*      */ import clus.heuristic.ClusStopCriterionMinWeight;
/*      */ import clus.heuristic.GainHeuristic;
/*      */ import clus.heuristic.ReducedErrorHeuristic;
/*      */ import clus.heuristic.VarianceReductionHeuristic;
/*      */ import clus.heuristic.VarianceReductionHeuristicCompatibility;
/*      */ import clus.heuristic.VarianceReductionHeuristicEfficient;
/*      */ import clus.model.ClusModel;
/*      */ import clus.pruning.BottomUpPruningVSB;
/*      */ import clus.pruning.CartPruning;
/*      */ import clus.pruning.M5Pruner;
/*      */ import clus.pruning.PruneTree;
/*      */ import clus.pruning.SequencePruningVSB;
/*      */ import clus.pruning.SizeConstraintPruning;
/*      */ import clus.statistic.ClassificationStat;
/*      */ import clus.statistic.ClusDistance;
/*      */ import clus.statistic.ClusStatistic;
/*      */ import clus.statistic.CombStat;
/*      */ import clus.statistic.RegressionStat;
/*      */ import clus.statistic.SumPairwiseDistancesStat;
/*      */ import clus.util.ClusException;
/*      */ import java.io.IOException;
/*      */ import java.io.Serializable;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import jeans.io.ini.INIFileNominalOrDoubleOrVector;
/*      */ import org.apache.commons.math.MathException;
/*      */ import org.apache.commons.math.distribution.ChiSquaredDistribution;
/*      */ import org.apache.commons.math.distribution.DistributionFactory;
/*      */ 
/*      */ public class ClusStatManager implements Serializable {
/*   84 */   protected static int m_Mode = -1;
/*      */   
/*      */   public static final long serialVersionUID = 1L;
/*      */   
/*      */   public static final int MODE_NONE = -1;
/*      */   
/*      */   public static final int MODE_CLASSIFY = 0;
/*      */   
/*      */   public static final int MODE_REGRESSION = 1;
/*      */   
/*      */   public static final int MODE_HIERARCHICAL = 2;
/*      */   
/*      */   public static final int MODE_SSPD = 3;
/*      */   public static final int MODE_CLASSIFY_AND_REGRESSION = 4;
/*      */   public static final int MODE_TIME_SERIES = 5;
/*      */   public static final int MODE_ILEVELC = 6;
/*      */   public static final int MODE_PHYLO = 7;
/*      */   public static final int MODE_BEAM_SEARCH = 8;
/*      */   protected transient ClusHeuristic m_Heuristic;
/*      */   protected ClusSchema m_Schema;
/*      */   protected boolean m_BeamSearch;
/*      */   protected boolean m_RuleInduceOnly;
/*      */   protected Settings m_Settings;
/*      */   protected ClusStatistic[] m_TrainSetStatAttrUse;
/*      */   protected ClusStatistic[] m_StatisticAttrUse;
/*      */   protected ClusAttributeWeights m_NormalizationWeights;
/*      */   protected ClusAttributeWeights m_ClusteringWeights;
/*      */   protected ClusNormalizedAttributeWeights m_DispersionWeights;
/*      */   protected ClassHierarchy m_Hier;
/*      */   protected SSPDMatrix m_SSPDMtrx;
/*      */   protected double[] m_ChiSquareInvProb;
/*      */   
/*      */   public ClusStatManager(ClusSchema schema, Settings sett) throws ClusException, IOException {
/*  117 */     this(schema, sett, true);
/*      */   }
/*      */   
/*      */   public ClusStatManager(ClusSchema schema, Settings sett, boolean docheck) throws ClusException, IOException {
/*  121 */     this.m_Schema = schema;
/*  122 */     this.m_Settings = sett;
/*  123 */     if (docheck) {
/*  124 */       check();
/*  125 */       initStructure();
/*      */     } 
/*      */   }
/*      */   
/*      */   public Settings getSettings() {
/*  130 */     return this.m_Settings;
/*      */   }
/*      */   
/*      */   public int getCompatibility() {
/*  134 */     return getSettings().getCompatibility();
/*      */   }
/*      */   
/*      */   public final ClusSchema getSchema() {
/*  138 */     return this.m_Schema;
/*      */   }
/*      */   
/*      */   public static final int getMode() {
/*  142 */     return m_Mode;
/*      */   }
/*      */   
/*      */   public boolean isClassificationOrRegression() {
/*  146 */     return (m_Mode == 0 || m_Mode == 1 || m_Mode == 4);
/*      */   }
/*      */ 
/*      */   
/*      */   public final ClassHierarchy getHier() {
/*  151 */     return this.m_Hier;
/*      */   }
/*      */   
/*      */   public void initStatisticAndStatManager() throws ClusException, IOException {
/*  155 */     initWeights();
/*  156 */     initStatistic();
/*  157 */     initHierarchySettings();
/*      */   }
/*      */   
/*      */   public ClusAttributeWeights getClusteringWeights() {
/*  161 */     return this.m_ClusteringWeights;
/*      */   }
/*      */   
/*      */   public ClusNormalizedAttributeWeights getDispersionWeights() {
/*  165 */     return this.m_DispersionWeights;
/*      */   }
/*      */   
/*      */   public ClusAttributeWeights getNormalizationWeights() {
/*  169 */     return this.m_NormalizationWeights;
/*      */   }
/*      */   
/*      */   public static boolean hasBitEqualToOne(boolean[] array) {
/*  173 */     for (int i = 0; i < array.length; i++) {
/*  174 */       if (array[i]) {
/*  175 */         return true;
/*      */       }
/*      */     } 
/*  178 */     return false;
/*      */   }
/*      */   
/*      */   public void initWeights(ClusNormalizedAttributeWeights result, NumericAttrType[] num, NominalAttrType[] nom, INIFileNominalOrDoubleOrVector winfo) throws ClusException {
/*  182 */     result.setAllWeights(0.0D);
/*  183 */     int nbattr = result.getNbAttributes();
/*  184 */     if (winfo.hasArrayIndexNames()) {
/*      */       
/*  186 */       double target_weight = winfo.getDouble(0);
/*  187 */       double non_target_weight = winfo.getDouble(1);
/*  188 */       double num_weight = winfo.getDouble(2);
/*  189 */       double nom_weight = winfo.getDouble(3);
/*  190 */       if (getSettings().getVerbose() >= 2) {
/*  191 */         System.out.println("  Target weight     = " + target_weight);
/*  192 */         System.out.println("  Non target weight = " + non_target_weight);
/*  193 */         System.out.println("  Numeric weight    = " + num_weight);
/*  194 */         System.out.println("  Nominal weight    = " + nom_weight);
/*      */       }  int i;
/*  196 */       for (i = 0; i < num.length; i++) {
/*  197 */         NumericAttrType cr_num = num[i];
/*  198 */         double tw = (cr_num.getStatus() == 1) ? target_weight : non_target_weight;
/*  199 */         result.setWeight((ClusAttrType)cr_num, num_weight * tw);
/*      */       } 
/*  201 */       for (i = 0; i < nom.length; i++) {
/*  202 */         NominalAttrType cr_nom = nom[i];
/*  203 */         double tw = (cr_nom.getStatus() == 1) ? target_weight : non_target_weight;
/*      */         
/*  205 */         result.setWeight((ClusAttrType)cr_nom, nom_weight * tw);
/*      */       } 
/*  207 */     } else if (winfo.isVector()) {
/*      */       
/*  209 */       if (nbattr != winfo.getVectorLength()) {
/*  210 */         throw new ClusException("Number of attributes is " + nbattr + " but weight vector has only " + winfo
/*      */             
/*  212 */             .getVectorLength() + " components");
/*      */       }
/*  214 */       for (int i = 0; i < nbattr; i++) {
/*  215 */         result.setWeight(i, winfo.getDouble(i));
/*      */       }
/*      */     } else {
/*      */       
/*  219 */       result.setAllWeights(winfo.getDouble());
/*      */     } 
/*      */     
/*  222 */     if (isRuleInduceOnly() && isClassificationOrRegression()) {
/*  223 */       double sum = 0.0D; int i;
/*  224 */       for (i = 0; i < num.length; i++) {
/*  225 */         NumericAttrType cr_num = num[i];
/*  226 */         sum += result.getWeight((ClusAttrType)cr_num);
/*      */       } 
/*  228 */       for (i = 0; i < nom.length; i++) {
/*  229 */         NominalAttrType cr_nom = nom[i];
/*  230 */         sum += result.getWeight((ClusAttrType)cr_nom);
/*      */       } 
/*  232 */       if (sum <= 0.0D) {
/*  233 */         throw new ClusException("initWeights(): Sum of clustering/dispersion weights must be > 0!");
/*      */       }
/*  235 */       for (i = 0; i < num.length; i++) {
/*  236 */         NumericAttrType cr_num = num[i];
/*  237 */         result.setWeight((ClusAttrType)cr_num, result.getWeight((ClusAttrType)cr_num) / sum);
/*      */       } 
/*  239 */       for (i = 0; i < nom.length; i++) {
/*  240 */         NominalAttrType cr_nom = nom[i];
/*  241 */         result.setWeight((ClusAttrType)cr_nom, result.getWeight((ClusAttrType)cr_nom) / sum);
/*      */       } 
/*      */     } 
/*      */   }
/*      */   
/*      */   public void initDispersionWeights() throws ClusException {
/*  247 */     NumericAttrType[] num = this.m_Schema.getNumericAttrUse(0);
/*  248 */     NominalAttrType[] nom = this.m_Schema.getNominalAttrUse(0);
/*  249 */     initWeights(this.m_DispersionWeights, num, nom, getSettings().getDispersionWeights());
/*  250 */     if (getSettings().getVerbose() >= 1 && (isRuleInduceOnly() || isTreeToRuleInduce()) && 
/*  251 */       getSettings().computeDispersion()) {
/*  252 */       System.out.println("Dispersion:   " + this.m_DispersionWeights.getName(this.m_Schema.getAllAttrUse(0)));
/*      */     }
/*      */   }
/*      */   
/*      */   public void initClusteringWeights() throws ClusException {
/*  257 */     if (getMode() == 2) {
/*  258 */       int nb_attrs = this.m_Schema.getNbAttributes();
/*  259 */       this.m_ClusteringWeights = new ClusAttributeWeights(nb_attrs + this.m_Hier.getTotal());
/*  260 */       double[] weights = this.m_Hier.getWeights();
/*  261 */       NumericAttrType[] dummy = this.m_Hier.getDummyAttrs();
/*  262 */       for (int i = 0; i < weights.length; i++) {
/*  263 */         this.m_ClusteringWeights.setWeight((ClusAttrType)dummy[i], weights[i]);
/*      */       }
/*      */       return;
/*      */     } 
/*  267 */     NumericAttrType[] num = this.m_Schema.getNumericAttrUse(2);
/*  268 */     NominalAttrType[] nom = this.m_Schema.getNominalAttrUse(2);
/*  269 */     initWeights((ClusNormalizedAttributeWeights)this.m_ClusteringWeights, num, nom, getSettings().getClusteringWeights());
/*  270 */     if (getSettings().getVerbose() >= 1) {
/*  271 */       System.out.println("Clustering: " + this.m_ClusteringWeights.getName(this.m_Schema.getAllAttrUse(2)));
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void initNormalizationWeights(ClusStatistic stat, ClusData data) throws ClusException {
/*  279 */     int nbattr = this.m_Schema.getNbAttributes();
/*  280 */     this.m_NormalizationWeights.setAllWeights(1.0D);
/*  281 */     boolean[] shouldNormalize = new boolean[nbattr];
/*  282 */     INIFileNominalOrDoubleOrVector winfo = getSettings().getNormalizationWeights();
/*  283 */     if (winfo.isVector()) {
/*  284 */       if (nbattr != winfo.getVectorLength()) {
/*  285 */         throw new ClusException("Number of attributes is " + nbattr + " but weight vector has only " + winfo
/*      */             
/*  287 */             .getVectorLength() + " components");
/*      */       }
/*  289 */       for (int i = 0; i < nbattr; i++) {
/*  290 */         if (winfo.isNominal(i)) {
/*  291 */           shouldNormalize[i] = true;
/*      */         } else {
/*  293 */           this.m_NormalizationWeights.setWeight(i, winfo.getDouble(i));
/*      */         } 
/*      */       } 
/*  296 */     } else if (winfo.isNominal() && winfo.getNominal() == 0) {
/*  297 */       Arrays.fill(shouldNormalize, true);
/*      */     } else {
/*  299 */       this.m_NormalizationWeights.setAllWeights(winfo.getDouble());
/*      */     } 
/*  301 */     if (hasBitEqualToOne(shouldNormalize)) {
/*      */       
/*  303 */       data.calcTotalStat(stat);
/*  304 */       CombStat cmb = (CombStat)stat;
/*  305 */       data.calcTotalStat(stat);
/*      */       
/*  307 */       RegressionStat rstat = cmb.getRegressionStat();
/*  308 */       rstat.initNormalizationWeights(this.m_NormalizationWeights, shouldNormalize);
/*      */       
/*  310 */       if (!isRuleInduceOnly()) {
/*  311 */         ClassificationStat cstat = cmb.getClassificationStat();
/*  312 */         cstat.initNormalizationWeights(this.m_NormalizationWeights, shouldNormalize);
/*      */       } 
/*  314 */       if (m_Mode == 5) {
/*  315 */         TimeSeriesStat tstat = (TimeSeriesStat)createStatistic(3);
/*  316 */         ((RowData)data).calcTotalStatBitVector((ClusStatistic)tstat);
/*  317 */         tstat.initNormalizationWeights(this.m_NormalizationWeights, shouldNormalize);
/*      */       } 
/*      */     } 
/*      */   }
/*      */   
/*      */   public void initWeights() {
/*  323 */     int nbattr = this.m_Schema.getNbAttributes();
/*  324 */     this.m_NormalizationWeights = new ClusAttributeWeights(nbattr);
/*  325 */     this.m_NormalizationWeights.setAllWeights(1.0D);
/*  326 */     this.m_ClusteringWeights = (ClusAttributeWeights)new ClusNormalizedAttributeWeights(this.m_NormalizationWeights);
/*  327 */     this.m_DispersionWeights = new ClusNormalizedAttributeWeights(this.m_NormalizationWeights);
/*      */   }
/*      */   
/*      */   public void check() throws ClusException {
/*  331 */     int nb_types = 0;
/*  332 */     int nb_nom = this.m_Schema.getNbNominalAttrUse(2);
/*  333 */     int nb_num = this.m_Schema.getNbNumericAttrUse(2);
/*      */     
/*  335 */     if (nb_nom > 0 && nb_num > 0) {
/*  336 */       m_Mode = 4;
/*  337 */       nb_types++;
/*  338 */     } else if (nb_nom > 0) {
/*  339 */       m_Mode = 0;
/*  340 */       nb_types++;
/*  341 */     } else if (nb_num > 0) {
/*  342 */       m_Mode = 1;
/*  343 */       nb_types++;
/*      */     } 
/*  345 */     if (this.m_Schema.hasAttributeType(3, 2)) {
/*  346 */       m_Mode = 2;
/*  347 */       getSettings().setSectionHierarchicalEnabled(true);
/*  348 */       nb_types++;
/*      */     } 
/*  350 */     int nb_int = 0;
/*  351 */     if (nb_int > 0 || this.m_Settings.checkHeuristic("SSPD")) {
/*  352 */       m_Mode = 3;
/*  353 */       nb_types++;
/*      */     } 
/*  355 */     if (this.m_Settings.checkHeuristic("GeneticDistance")) {
/*  356 */       m_Mode = 7;
/*      */     }
/*  358 */     if (this.m_Settings.isSectionTimeSeriesEnabled()) {
/*  359 */       m_Mode = 5;
/*  360 */       nb_types++;
/*      */     } 
/*  362 */     if (this.m_Settings.isSectionILevelCEnabled()) {
/*  363 */       m_Mode = 6;
/*      */     }
/*  365 */     if (this.m_Settings.isBeamSearchMode() && this.m_Settings.getBeamSimilarity() != 0.0D) {
/*  366 */       m_Mode = 8;
/*      */     }
/*      */     
/*  369 */     if (nb_types == 0) {
/*  370 */       System.err.println("No target value defined");
/*      */     }
/*  372 */     if (nb_types > 1) {
/*  373 */       throw new ClusException("Incompatible combination of clustering attribute types");
/*      */     }
/*      */   }
/*      */   
/*      */   public void initStructure() throws IOException {
/*  378 */     switch (m_Mode) {
/*      */       case 2:
/*  380 */         createHierarchy();
/*      */         break;
/*      */       case 3:
/*  383 */         this.m_SSPDMtrx = SSPDMatrix.read(getSettings().getFileAbsolute(getSettings().getAppName() + ".dist"), getSettings());
/*      */         break;
/*      */     } 
/*      */   }
/*      */   
/*      */   public ClusStatistic createSuitableStat(NumericAttrType[] num, NominalAttrType[] nom) {
/*  389 */     if (num.length == 0) {
/*  390 */       if (m_Mode == 7)
/*      */       {
/*      */         
/*  393 */         return (ClusStatistic)new GeneticDistanceStat(nom);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*  398 */       return (ClusStatistic)new ClassificationStat(nom);
/*  399 */     }  if (nom.length == 0) {
/*  400 */       return (ClusStatistic)new RegressionStat(num);
/*      */     }
/*  402 */     return (ClusStatistic)new CombStat(this, num, nom);
/*      */   }
/*      */   
/*      */   public boolean heuristicNeedsCombStat()
/*      */   {
/*  407 */     if (isRuleInduceOnly())
/*      */     {
/*      */ 
/*      */       
/*  411 */       return (getSettings().getHeuristic() == 0 || 
/*  412 */         getSettings().getHeuristic() == 8 || 
/*  413 */         getSettings().getHeuristic() == 9 || 
/*  414 */         getSettings().getHeuristic() == 10 || 
/*  415 */         getSettings().getHeuristic() == 11);
/*      */     }
/*  417 */     return false; } public void initStatistic() throws ClusException { ClusDistance dist; HierJaccardDistance hierJaccardDistance; ClusAttrType[] target, targets;
/*      */     TimeSeriesAttrType type;
/*      */     int efficiency;
/*      */     DTWTimeSeriesDist dTWTimeSeriesDist;
/*      */     TSCTimeSeriesDist tSCTimeSeriesDist;
/*  422 */     this.m_StatisticAttrUse = new ClusStatistic[5];
/*      */     
/*  424 */     NumericAttrType[] num1 = this.m_Schema.getNumericAttrUse(0);
/*  425 */     NominalAttrType[] nom1 = this.m_Schema.getNominalAttrUse(0);
/*  426 */     this.m_StatisticAttrUse[0] = (ClusStatistic)new CombStat(this, num1, nom1);
/*      */     
/*  428 */     NumericAttrType[] num2 = this.m_Schema.getNumericAttrUse(3);
/*  429 */     NominalAttrType[] nom2 = this.m_Schema.getNominalAttrUse(3);
/*  430 */     this.m_StatisticAttrUse[3] = createSuitableStat(num2, nom2);
/*      */     
/*  432 */     NumericAttrType[] num3 = this.m_Schema.getNumericAttrUse(2);
/*  433 */     NominalAttrType[] nom3 = this.m_Schema.getNominalAttrUse(2);
/*  434 */     if (num3.length != 0 || nom3.length != 0) {
/*  435 */       if (heuristicNeedsCombStat()) {
/*  436 */         this.m_StatisticAttrUse[2] = (ClusStatistic)new CombStat(this, num3, nom3);
/*      */       } else {
/*  438 */         this.m_StatisticAttrUse[2] = createSuitableStat(num3, nom3);
/*      */       } 
/*      */     }
/*  441 */     switch (m_Mode) {
/*      */       case 2:
/*  443 */         if (getSettings().getHierDistance() == 0) {
/*  444 */           if (getSettings().getHierSingleLabel()) {
/*  445 */             setClusteringStatistic((ClusStatistic)new HierSingleLabelStat(this.m_Hier, getCompatibility()));
/*  446 */             setTargetStatistic((ClusStatistic)new HierSingleLabelStat(this.m_Hier, getCompatibility())); break;
/*      */           } 
/*  448 */           setClusteringStatistic((ClusStatistic)new WHTDStatistic(this.m_Hier, getCompatibility()));
/*  449 */           setTargetStatistic((ClusStatistic)new WHTDStatistic(this.m_Hier, getCompatibility()));
/*      */           break;
/*      */         } 
/*  452 */         dist = null;
/*  453 */         if (getSettings().getHierDistance() == 1) {
/*  454 */           hierJaccardDistance = new HierJaccardDistance(this.m_Hier.getType());
/*      */         }
/*  456 */         setClusteringStatistic((ClusStatistic)new HierSumPairwiseDistancesStat(this.m_Hier, (ClusDistance)hierJaccardDistance, getCompatibility()));
/*  457 */         setTargetStatistic((ClusStatistic)new HierSumPairwiseDistancesStat(this.m_Hier, (ClusDistance)hierJaccardDistance, getCompatibility()));
/*      */         break;
/*      */       
/*      */       case 3:
/*  461 */         target = this.m_Schema.getAllAttrUse(3);
/*  462 */         this.m_SSPDMtrx.setTarget(target);
/*  463 */         setClusteringStatistic((ClusStatistic)new SumPairwiseDistancesStat((ClusDistance)this.m_SSPDMtrx, 3));
/*  464 */         setTargetStatistic((ClusStatistic)new SumPairwiseDistancesStat((ClusDistance)this.m_SSPDMtrx, 3));
/*      */         break;
/*      */       case 5:
/*  467 */         targets = this.m_Schema.getAllAttrUse(3);
/*  468 */         type = (TimeSeriesAttrType)targets[0];
/*  469 */         efficiency = (getSettings()).m_TimeSeriesHeuristicSampling.getValue();
/*  470 */         switch (getSettings().getTimeSeriesDistance()) {
/*      */           case 0:
/*  472 */             dTWTimeSeriesDist = new DTWTimeSeriesDist(type);
/*  473 */             setClusteringStatistic((ClusStatistic)new TimeSeriesStat(type, (ClusDistance)dTWTimeSeriesDist, efficiency));
/*  474 */             setTargetStatistic((ClusStatistic)new TimeSeriesStat(type, (ClusDistance)dTWTimeSeriesDist, efficiency));
/*      */             break;
/*      */           case 1:
/*  477 */             if (type.isEqualLength()) {
/*  478 */               QDMTimeSeriesDist qDMTimeSeriesDist = new QDMTimeSeriesDist(type);
/*  479 */               setClusteringStatistic((ClusStatistic)new TimeSeriesStat(type, (ClusDistance)qDMTimeSeriesDist, efficiency));
/*  480 */               setTargetStatistic((ClusStatistic)new TimeSeriesStat(type, (ClusDistance)qDMTimeSeriesDist, efficiency)); break;
/*      */             } 
/*  482 */             throw new ClusException("QDM Distance is not implemented for time series with different length");
/*      */ 
/*      */           
/*      */           case 2:
/*  486 */             tSCTimeSeriesDist = new TSCTimeSeriesDist(type);
/*  487 */             setClusteringStatistic((ClusStatistic)new TimeSeriesStat(type, (ClusDistance)tSCTimeSeriesDist, efficiency));
/*  488 */             setTargetStatistic((ClusStatistic)new TimeSeriesStat(type, (ClusDistance)tSCTimeSeriesDist, efficiency));
/*      */             break;
/*      */         } 
/*      */         break;
/*      */       case 6:
/*  493 */         setTargetStatistic((ClusStatistic)new ILevelCStatistic(num2));
/*  494 */         setClusteringStatistic((ClusStatistic)new ILevelCStatistic(num3));
/*      */         break;
/*      */       case 8:
/*  497 */         if (num3.length != 0 && num2.length != 0) {
/*  498 */           setTargetStatistic((ClusStatistic)new ClusBeamSimRegrStat(num2, null));
/*  499 */           setClusteringStatistic((ClusStatistic)new ClusBeamSimRegrStat(num3, null));
/*      */         } 
/*      */         break;
/*      */     }  }
/*      */ 
/*      */   
/*      */   public ClusHeuristic createHeuristic(int type) {
/*  506 */     switch (type) {
/*      */       case 2:
/*  508 */         return (ClusHeuristic)new GainHeuristic(false);
/*      */     } 
/*  510 */     return null;
/*      */   }
/*      */ 
/*      */   
/*      */   public void initRuleHeuristic() throws ClusException {
/*  515 */     if (m_Mode == 0) {
/*  516 */       switch (getSettings().getHeuristic()) {
/*      */         case 0:
/*  518 */           this.m_Heuristic = (ClusHeuristic)new ClusRuleHeuristicRDispersionMlt(this, getClusteringWeights());
/*  519 */           getSettings().setHeuristic(11);
/*      */           return;
/*      */         case 1:
/*  522 */           this.m_Heuristic = (ClusHeuristic)new ClusRuleHeuristicError(this, getClusteringWeights());
/*      */           return;
/*      */         case 6:
/*  525 */           this.m_Heuristic = (ClusHeuristic)new ClusRuleHeuristicMEstimate(getSettings().getMEstimate());
/*      */           return;
/*      */         case 8:
/*  528 */           this.m_Heuristic = (ClusHeuristic)new ClusRuleHeuristicDispersionAdt(this, getClusteringWeights());
/*      */           return;
/*      */         case 9:
/*  531 */           this.m_Heuristic = (ClusHeuristic)new ClusRuleHeuristicDispersionMlt(this, getClusteringWeights());
/*      */           return;
/*      */         case 10:
/*  534 */           this.m_Heuristic = (ClusHeuristic)new ClusRuleHeuristicRDispersionAdt(this, getClusteringWeights());
/*      */           return;
/*      */         case 11:
/*  537 */           this.m_Heuristic = (ClusHeuristic)new ClusRuleHeuristicRDispersionMlt(this, getClusteringWeights());
/*      */           return;
/*      */       } 
/*  540 */       throw new ClusException("Unsupported heuristic for single target classification rules!");
/*      */     } 
/*  542 */     if (m_Mode == 1 || m_Mode == 4) {
/*  543 */       switch (getSettings().getHeuristic()) {
/*      */         case 0:
/*  545 */           this.m_Heuristic = (ClusHeuristic)new ClusRuleHeuristicRDispersionMlt(this, getClusteringWeights());
/*      */           return;
/*      */         case 1:
/*  548 */           this.m_Heuristic = (ClusHeuristic)new ClusRuleHeuristicError(this, getClusteringWeights());
/*      */           return;
/*      */         case 8:
/*  551 */           this.m_Heuristic = (ClusHeuristic)new ClusRuleHeuristicDispersionAdt(this, getClusteringWeights());
/*      */           return;
/*      */         case 9:
/*  554 */           this.m_Heuristic = (ClusHeuristic)new ClusRuleHeuristicDispersionMlt(this, getClusteringWeights());
/*      */           return;
/*      */         case 10:
/*  557 */           this.m_Heuristic = (ClusHeuristic)new ClusRuleHeuristicRDispersionAdt(this, getClusteringWeights());
/*      */           return;
/*      */         case 11:
/*  560 */           this.m_Heuristic = (ClusHeuristic)new ClusRuleHeuristicRDispersionMlt(this, getClusteringWeights());
/*      */           return;
/*      */       } 
/*  563 */       throw new ClusException("Unsupported heuristic for multiple target or regression rules!");
/*      */     } 
/*  565 */     if (m_Mode == 2) {
/*  566 */       this.m_Heuristic = (ClusHeuristic)new ClusRuleHeuristicHierarchical(this, getClusteringWeights());
/*      */ 
/*      */ 
/*      */       
/*      */       return;
/*      */     } 
/*      */ 
/*      */     
/*  574 */     if (m_Mode == 5) {
/*  575 */       String name = "Time Series Intra-Cluster Variation Heuristic for Rules";
/*  576 */       this.m_Heuristic = (ClusHeuristic)new ClusRuleHeuristicSSD(this, name, createClusteringStat(), getClusteringWeights());
/*  577 */       getSettings().setHeuristic(5); return;
/*      */     } 
/*  579 */     if (m_Mode == 6) {
/*  580 */       String name = "Intra-Cluster Variation Heuristic for Rules";
/*  581 */       this.m_Heuristic = (ClusHeuristic)new ClusRuleHeuristicSSD(this, name, createClusteringStat(), getClusteringWeights());
/*      */     } else {
/*  583 */       throw new ClusException("Unsupported mode for rules!");
/*      */     } 
/*      */   }
/*      */   
/*      */   public void initBeamSearchHeuristic() throws ClusException {
/*  588 */     if (getSettings().getHeuristic() == 1) {
/*  589 */       this.m_Heuristic = (ClusHeuristic)new ClusBeamHeuristicError(createClusteringStat());
/*  590 */     } else if (getSettings().getHeuristic() == 6) {
/*  591 */       this.m_Heuristic = (ClusHeuristic)new ClusBeamHeuristicMEstimate(createClusteringStat(), getSettings().getMEstimate());
/*  592 */     } else if (getSettings().getHeuristic() == 7) {
/*  593 */       this.m_Heuristic = (ClusHeuristic)new ClusBeamHeuristicMorishita(createClusteringStat());
/*      */     } else {
/*  595 */       this.m_Heuristic = (ClusHeuristic)new ClusBeamHeuristicSS(createClusteringStat(), getClusteringWeights());
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public void initHeuristic() throws ClusException {
/*  601 */     if (isRuleInduceOnly() && !isTreeToRuleInduce()) {
/*  602 */       initRuleHeuristic();
/*      */       return;
/*      */     } 
/*  605 */     if (isBeamSearch()) {
/*  606 */       initBeamSearchHeuristic();
/*      */       return;
/*      */     } 
/*  609 */     if (m_Mode == 2) {
/*  610 */       if (getSettings().getCompatibility() <= 1) {
/*  611 */         this.m_Heuristic = (ClusHeuristic)new VarianceReductionHeuristicCompatibility(createClusteringStat(), getClusteringWeights());
/*      */       } else {
/*  613 */         this.m_Heuristic = (ClusHeuristic)new VarianceReductionHeuristicEfficient(getClusteringWeights(), null);
/*      */       } 
/*  615 */       getSettings().setHeuristic(5);
/*      */       return;
/*      */     } 
/*  618 */     if (m_Mode == 3) {
/*  619 */       ClusStatistic clusstat = createClusteringStat();
/*  620 */       this.m_Heuristic = (ClusHeuristic)new VarianceReductionHeuristic(clusstat.getDistanceName(), clusstat, getClusteringWeights());
/*  621 */       getSettings().setHeuristic(4);
/*      */       return;
/*      */     } 
/*  624 */     if (m_Mode == 5) {
/*  625 */       ClusStatistic clusstat = createClusteringStat();
/*  626 */       this.m_Heuristic = (ClusHeuristic)new VarianceReductionHeuristic(clusstat.getDistanceName(), clusstat, getClusteringWeights());
/*  627 */       getSettings().setHeuristic(5);
/*      */       
/*      */       return;
/*      */     } 
/*  631 */     NumericAttrType[] num = this.m_Schema.getNumericAttrUse(2);
/*  632 */     NominalAttrType[] nom = this.m_Schema.getNominalAttrUse(2);
/*  633 */     if (getSettings().getHeuristic() == 14) {
/*  634 */       this.m_Heuristic = (ClusHeuristic)new VarianceReductionHeuristicInclMissingValues(getClusteringWeights(), this.m_Schema.getAllAttrUse(2), createClusteringStat());
/*      */       return;
/*      */     } 
/*  637 */     if (num.length > 0 && nom.length > 0) {
/*  638 */       if (getSettings().getHeuristic() != 0 && getSettings().getHeuristic() != 5) {
/*  639 */         throw new ClusException("Only SS-Reduction heuristic can be used for combined classification/regression trees!");
/*      */       }
/*  641 */       this.m_Heuristic = (ClusHeuristic)new VarianceReductionHeuristicEfficient(getClusteringWeights(), this.m_Schema.getAllAttrUse(2));
/*  642 */       getSettings().setHeuristic(5);
/*  643 */     } else if (num.length > 0) {
/*  644 */       if (getSettings().getHeuristic() != 0 && getSettings().getHeuristic() != 5) {
/*  645 */         throw new ClusException("Only SS-Reduction heuristic can be used for regression trees!");
/*      */       }
/*  647 */       this.m_Heuristic = (ClusHeuristic)new VarianceReductionHeuristicEfficient(getClusteringWeights(), (ClusAttrType[])this.m_Schema.getNumericAttrUse(2));
/*  648 */       getSettings().setHeuristic(5);
/*  649 */     } else if (nom.length > 0) {
/*  650 */       if (getSettings().getHeuristic() == 13) {
/*  651 */         this.m_Heuristic = (ClusHeuristic)new ModifiedGainHeuristic(createClusteringStat());
/*  652 */       } else if (getSettings().getHeuristic() == 1) {
/*  653 */         this.m_Heuristic = (ClusHeuristic)new ReducedErrorHeuristic(createClusteringStat());
/*  654 */       } else if (getSettings().getHeuristic() == 12) {
/*  655 */         this.m_Heuristic = (ClusHeuristic)new GeneticDistanceHeuristicMatrix();
/*  656 */       } else if (getSettings().getHeuristic() == 5) {
/*  657 */         System.out.println("akii");
/*      */         
/*  659 */         this.m_Heuristic = (ClusHeuristic)new VarianceReductionHeuristicEfficient(getClusteringWeights(), (ClusAttrType[])this.m_Schema.getNominalAttrUse(2));
/*  660 */       } else if (getSettings().getHeuristic() == 3) {
/*  661 */         this.m_Heuristic = (ClusHeuristic)new GainHeuristic(true);
/*      */       } else {
/*  663 */         if (getSettings().getHeuristic() != 0 && 
/*  664 */           getSettings().getHeuristic() != 2 && 
/*  665 */           getSettings().getHeuristic() != 12) {
/*  666 */           throw new ClusException("Given heuristic not supported for classification trees!");
/*      */         }
/*  668 */         this.m_Heuristic = (ClusHeuristic)new GainHeuristic(false);
/*  669 */         getSettings().setHeuristic(2);
/*      */       } 
/*      */     } 
/*      */   }
/*      */   
/*      */   public void initStopCriterion() {
/*      */     ClusStopCriterionMinWeight clusStopCriterionMinWeight;
/*  676 */     ClusStopCriterion stop = null;
/*  677 */     int minEx = getSettings().getMinimalNbExamples();
/*  678 */     double knownWeight = getSettings().getMinimalKnownWeight();
/*  679 */     if (minEx > 0) {
/*  680 */       ClusStopCriterionMinNbExamples clusStopCriterionMinNbExamples = new ClusStopCriterionMinNbExamples(minEx);
/*  681 */     } else if (knownWeight > 0.0D) {
/*  682 */       SemiSupMinLabeledWeightStopCrit semiSupMinLabeledWeightStopCrit = new SemiSupMinLabeledWeightStopCrit(knownWeight);
/*      */     } else {
/*  684 */       double minW = getSettings().getMinimalWeight();
/*  685 */       clusStopCriterionMinWeight = new ClusStopCriterionMinWeight(minW);
/*      */     } 
/*      */     
/*  688 */     this.m_Heuristic.setStopCriterion((ClusStopCriterion)clusStopCriterionMinWeight);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void initSignifcanceTestingTable()
/*      */   {
/*  699 */     int max_nom_val = 0;
/*  700 */     int num_nom_atts = this.m_Schema.getNbNominalAttrUse(0);
/*  701 */     for (int i = 0; i < num_nom_atts; i++) {
/*  702 */       if ((this.m_Schema.getNominalAttrUse(0)[i]).m_NbValues > max_nom_val) {
/*  703 */         max_nom_val = (this.m_Schema.getNominalAttrUse(0)[i]).m_NbValues;
/*      */       }
/*      */     } 
/*  706 */     if (max_nom_val == 0) {
/*  707 */       max_nom_val = 1;
/*      */     }
/*  709 */     double[] table = new double[max_nom_val];
/*  710 */     table[0] = 1.0D - getSettings().getRuleSignificanceLevel();
/*      */     
/*  712 */     for (int j = 1; j < table.length; j++) {
/*  713 */       DistributionFactory distributionFactory = DistributionFactory.newInstance();
/*  714 */       ChiSquaredDistribution chiSquaredDistribution = distributionFactory.createChiSquareDistribution(j);
/*      */       try {
/*  716 */         table[j] = chiSquaredDistribution.inverseCumulativeProbability(table[0]);
/*  717 */       } catch (MathException e) {
/*  718 */         e.printStackTrace();
/*      */       } 
/*      */     } 
/*  721 */     this.m_ChiSquareInvProb = table; } public ClusErrorList createErrorMeasure(MultiScore score) { INIFileNominalOrDoubleOrVector class_thr;
/*      */     double[] recalls;
/*      */     boolean wrCurves;
/*      */     NominalAttrType cls;
/*  725 */     ClusErrorList parent = new ClusErrorList();
/*  726 */     NumericAttrType[] num = this.m_Schema.getNumericAttrUse(3);
/*  727 */     NominalAttrType[] nom = this.m_Schema.getNominalAttrUse(3);
/*  728 */     TimeSeriesAttrType[] ts = this.m_Schema.getTimeSeriesAttrUse(3);
/*  729 */     if (nom.length != 0) {
/*  730 */       parent.addError((ClusError)new ContingencyTable(parent, nom));
/*  731 */       parent.addError((ClusError)new MSNominalError(parent, nom, this.m_NormalizationWeights));
/*      */     } 
/*  733 */     if (num.length != 0) {
/*  734 */       parent.addError((ClusError)new AbsoluteError(parent, num));
/*  735 */       parent.addError((ClusError)new MSError(parent, num));
/*  736 */       parent.addError((ClusError)new RMSError(parent, num));
/*  737 */       if (getSettings().hasNonTrivialWeights()) {
/*  738 */         parent.addError((ClusError)new RMSError(parent, num, this.m_NormalizationWeights));
/*      */       }
/*  740 */       parent.addError((ClusError)new PearsonCorrelation(parent, num));
/*      */     } 
/*  742 */     if (ts.length != 0) {
/*  743 */       ClusStatistic stat = createTargetStat();
/*  744 */       parent.addError((ClusError)new AvgDistancesError(parent, stat.getDistance()));
/*      */     } 
/*  746 */     switch (m_Mode) {
/*      */       case 2:
/*  748 */         class_thr = getSettings().getClassificationThresholds();
/*  749 */         if (class_thr.hasVector()) {
/*  750 */           parent.addError((ClusError)new HierClassWiseAccuracy(parent, this.m_Hier));
/*      */         }
/*  752 */         recalls = getSettings().getRecallValues().getDoubleVector();
/*  753 */         wrCurves = getSettings().isWriteCurves();
/*  754 */         if (getSettings().isCalError()) {
/*  755 */           parent.addError((ClusError)new HierErrorMeasures(parent, this.m_Hier, recalls, getSettings().getCompatibility(), -1, wrCurves));
/*      */         }
/*      */         break;
/*      */       case 6:
/*  759 */         cls = (NominalAttrType)getSchema().getLastNonDisabledType();
/*  760 */         parent.addError((ClusError)new ILevelCRandIndex(parent, cls));
/*      */         break;
/*      */     } 
/*  763 */     return parent; }
/*      */ 
/*      */   
/*      */   public ClusErrorList createEvalError() {
/*  767 */     ClusErrorList parent = new ClusErrorList();
/*  768 */     NumericAttrType[] num = this.m_Schema.getNumericAttrUse(3);
/*  769 */     NominalAttrType[] nom = this.m_Schema.getNominalAttrUse(3);
/*  770 */     TimeSeriesAttrType[] ts = this.m_Schema.getTimeSeriesAttrUse(3);
/*  771 */     if (nom.length != 0) {
/*  772 */       parent.addError((ClusError)new Accuracy(parent, nom));
/*      */     }
/*  774 */     if (num.length != 0) {
/*  775 */       parent.addError((ClusError)new RMSError(parent, num));
/*      */     }
/*  777 */     if (ts.length != 0) {
/*  778 */       ClusStatistic stat = createTargetStat();
/*  779 */       parent.addError((ClusError)new AvgDistancesError(parent, stat.getDistance()));
/*      */     } 
/*  781 */     return parent;
/*      */   }
/*      */   
/*      */   public ClusErrorList createDefaultError() {
/*  785 */     ClusErrorList parent = new ClusErrorList();
/*  786 */     NumericAttrType[] num = this.m_Schema.getNumericAttrUse(3);
/*  787 */     NominalAttrType[] nom = this.m_Schema.getNominalAttrUse(3);
/*  788 */     if (nom.length != 0) {
/*  789 */       parent.addError((ClusError)new MisclassificationError(parent, nom));
/*      */     }
/*  791 */     if (num.length != 0) {
/*  792 */       parent.addError((ClusError)new RMSError(parent, num));
/*      */     }
/*  794 */     switch (m_Mode) {
/*      */       case 2:
/*  796 */         parent.addError((ClusError)new HierClassWiseAccuracy(parent, this.m_Hier));
/*      */         break;
/*      */     } 
/*  799 */     return parent;
/*      */   }
/*      */   
/*      */   public ClusErrorList createAdditiveError() {
/*      */     ClusStatistic stat;
/*  804 */     ClusErrorList parent = new ClusErrorList();
/*  805 */     NumericAttrType[] num = this.m_Schema.getNumericAttrUse(3);
/*  806 */     NominalAttrType[] nom = this.m_Schema.getNominalAttrUse(3);
/*  807 */     if (nom.length != 0) {
/*  808 */       parent.addError((ClusError)new MisclassificationError(parent, nom));
/*      */     }
/*  810 */     if (num.length != 0) {
/*  811 */       parent.addError((ClusError)new MSError(parent, num, getClusteringWeights()));
/*      */     }
/*  813 */     switch (m_Mode) {
/*      */       case 2:
/*  815 */         parent.addError((ClusError)new HierClassWiseAccuracy(parent, this.m_Hier));
/*      */         break;
/*      */       case 5:
/*  818 */         stat = createTargetStat();
/*  819 */         parent.addError((ClusError)new AvgDistancesError(parent, stat.getDistance()));
/*      */         break;
/*      */     } 
/*  822 */     parent.setWeights(getClusteringWeights());
/*  823 */     return parent;
/*      */   }
/*      */   
/*      */   public ClusErrorList createExtraError(int train_err) {
/*  827 */     ClusErrorList parent = new ClusErrorList();
/*  828 */     if (m_Mode == 5) {
/*  829 */       ClusStatistic stat = createTargetStat();
/*  830 */       parent.addError((ClusError)new ICVPairwiseDistancesError(parent, stat.getDistance()));
/*  831 */       parent.addError((ClusError)new TimeSeriesSignificantChangeTesterXVAL(parent, (TimeSeriesStat)stat));
/*      */     } 
/*  833 */     return parent;
/*      */   }
/*      */   
/*      */   public PruneTree getTreePrunerNoVSB() throws ClusException {
/*  837 */     Settings sett = getSettings();
/*  838 */     if (isBeamSearch() && sett.isBeamPostPrune()) {
/*  839 */       sett.setPruningMethod(6);
/*  840 */       return (PruneTree)new SizeConstraintPruning(sett.getBeamTreeMaxSize(), getClusteringWeights());
/*      */     } 
/*  842 */     int err_nb = sett.getMaxErrorConstraintNumber();
/*  843 */     int size_nb = sett.getSizeConstraintPruningNumber();
/*  844 */     if (size_nb > 0 || err_nb > 0) {
/*  845 */       int[] sizes = sett.getSizeConstraintPruningVector();
/*  846 */       if (sett.getPruningMethod() == 9) {
/*  847 */         return (PruneTree)new CartPruning(sizes, getClusteringWeights());
/*      */       }
/*  849 */       sett.setPruningMethod(6);
/*  850 */       SizeConstraintPruning sc_prune = new SizeConstraintPruning(sizes, getClusteringWeights());
/*  851 */       if (err_nb > 0) {
/*  852 */         double[] max_err = sett.getMaxErrorConstraintVector();
/*  853 */         sc_prune.setMaxError(max_err);
/*  854 */         sc_prune.setErrorMeasure(createDefaultError());
/*      */       } 
/*  856 */       if (m_Mode == 5) {
/*  857 */         sc_prune.setAdditiveError(createAdditiveError());
/*      */       }
/*  859 */       return (PruneTree)sc_prune;
/*      */     } 
/*      */     
/*  862 */     INIFileNominalOrDoubleOrVector class_thr = sett.getClassificationThresholds();
/*  863 */     if (class_thr.hasVector()) {
/*  864 */       return (PruneTree)new HierClassTresholdPruner(class_thr.getDoubleVector());
/*      */     }
/*  866 */     if (m_Mode == 1) {
/*  867 */       double mult = sett.getM5PruningMult();
/*  868 */       if (sett.getPruningMethod() == 4) {
/*  869 */         return (PruneTree)new M5PrunerMulti(getClusteringWeights(), mult);
/*      */       }
/*  871 */       if (sett.getPruningMethod() == 0 || sett.getPruningMethod() == 3) {
/*  872 */         sett.setPruningMethod(3);
/*  873 */         return (PruneTree)new M5Pruner(getClusteringWeights(), mult);
/*      */       } 
/*  875 */     } else if (m_Mode == 0) {
/*  876 */       if (sett.getPruningMethod() == 0 || sett.getPruningMethod() == 2) {
/*  877 */         sett.setPruningMethod(2);
/*  878 */         return (PruneTree)new C45Pruner();
/*      */       } 
/*  880 */     } else if (m_Mode == 2) {
/*  881 */       if (sett.getPruningMethod() == 3) {
/*  882 */         double mult = sett.getM5PruningMult();
/*  883 */         return (PruneTree)new M5Pruner(this.m_NormalizationWeights, mult);
/*      */       } 
/*  885 */     } else if (m_Mode == 7 && 
/*  886 */       sett.getPruningMethod() == 10) {
/*  887 */       return (PruneTree)new EncodingCostPruning();
/*      */     } 
/*      */     
/*  890 */     sett.setPruningMethod(1);
/*  891 */     return new PruneTree();
/*      */   }
/*      */   
/*      */   public PruneTree getTreePruner(ClusData pruneset) throws ClusException {
/*  895 */     Settings sett = getSettings();
/*  896 */     int pm = sett.getPruningMethod();
/*  897 */     if (pm == 1)
/*      */     {
/*      */       
/*  900 */       return new PruneTree();
/*      */     }
/*  902 */     if (m_Mode == 2 && pruneset != null) {
/*  903 */       PruneTree pruner = getTreePrunerNoVSB();
/*  904 */       boolean bonf = sett.isUseBonferroni();
/*  905 */       HierRemoveInsigClasses hierpruner = new HierRemoveInsigClasses(pruneset, pruner, bonf, this.m_Hier);
/*  906 */       hierpruner.setSignificance(sett.getHierPruneInSig());
/*  907 */       hierpruner.setNoRootPreds(sett.isHierNoRootPreds());
/*  908 */       sett.setPruningMethod(0);
/*  909 */       return (PruneTree)hierpruner;
/*      */     } 
/*  911 */     if (pruneset != null) {
/*  912 */       if (pm == 7 || pm == 8) {
/*      */         
/*  914 */         SequencePruningVSB pruner = new SequencePruningVSB((RowData)pruneset, getClusteringWeights());
/*  915 */         if (pm == 7) {
/*  916 */           int maxsize = sett.getMaxSize();
/*  917 */           pruner.setSequencePruner((PruneTree)new SizeConstraintPruning(maxsize, getClusteringWeights()));
/*      */         } else {
/*  919 */           pruner.setSequencePruner((PruneTree)new CartPruning(getClusteringWeights(), sett.isMSENominal()));
/*      */         } 
/*  921 */         pruner.setOutputFile(sett.getFileAbsolute("prune.dat"));
/*  922 */         pruner.set1SERule(sett.get1SERule());
/*  923 */         pruner.setHasMissing(this.m_Schema.hasMissing());
/*  924 */         return (PruneTree)pruner;
/*  925 */       }  if (pm == 5 || pm == 0) {
/*  926 */         ClusErrorList parent = createEvalError();
/*  927 */         sett.setPruningMethod(5);
/*  928 */         return (PruneTree)new BottomUpPruningVSB(parent, (RowData)pruneset);
/*      */       } 
/*  930 */       return getTreePrunerNoVSB();
/*      */     } 
/*      */     
/*  933 */     return getTreePrunerNoVSB();
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void setTargetStatistic(ClusStatistic stat) {
/*  939 */     this.m_StatisticAttrUse[3] = stat;
/*      */   }
/*      */ 
/*      */   
/*      */   public void setClusteringStatistic(ClusStatistic stat) {
/*  944 */     this.m_StatisticAttrUse[2] = stat;
/*      */   }
/*      */   
/*      */   public boolean hasClusteringStat() {
/*  948 */     return (this.m_StatisticAttrUse[2] != null);
/*      */   }
/*      */   
/*      */   public ClusStatistic createClusteringStat() {
/*  952 */     return this.m_StatisticAttrUse[2].cloneStat();
/*      */   }
/*      */   
/*      */   public ClusStatistic createTargetStat() {
/*  956 */     return this.m_StatisticAttrUse[3].cloneStat();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ClusStatistic createStatistic(int attType) {
/*  964 */     return this.m_StatisticAttrUse[attType].cloneStat();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ClusStatistic getStatistic(int attType) {
/*  973 */     return this.m_StatisticAttrUse[attType];
/*      */   }
/*      */   
/*      */   public ClusStatistic getTrainSetStat() {
/*  977 */     return getTrainSetStat(0);
/*      */   }
/*      */   
/*      */   public ClusStatistic getTrainSetStat(int attType) {
/*  981 */     return this.m_TrainSetStatAttrUse[attType];
/*      */   }
/*      */   
/*      */   public void computeTrainSetStat(RowData trainset, int attType) {
/*  985 */     this.m_TrainSetStatAttrUse[attType] = createStatistic(attType);
/*  986 */     trainset.calcTotalStatBitVector(this.m_TrainSetStatAttrUse[attType]);
/*  987 */     this.m_TrainSetStatAttrUse[attType].calcMean();
/*      */   }
/*      */   
/*      */   public void computeTrainSetStat(RowData trainset) {
/*  991 */     this.m_TrainSetStatAttrUse = new ClusStatistic[5];
/*  992 */     if (getMode() != 2) {
/*  993 */       computeTrainSetStat(trainset, 0);
/*      */     }
/*  995 */     computeTrainSetStat(trainset, 2);
/*  996 */     computeTrainSetStat(trainset, 3);
/*      */   }
/*      */   
/*      */   public ClusHeuristic getHeuristic() {
/* 1000 */     return this.m_Heuristic;
/*      */   }
/*      */   
/*      */   public String getHeuristicName() {
/* 1004 */     return this.m_Heuristic.getName();
/*      */   }
/*      */ 
/*      */   
/*      */   public void getPreprocs(DataPreprocs pps) {}
/*      */   
/*      */   public boolean needsHierarchyProcessors() {
/* 1011 */     if (m_Mode == 3) {
/* 1012 */       return false;
/*      */     }
/* 1014 */     return true;
/*      */   }
/*      */ 
/*      */   
/*      */   public void setRuleInduceOnly(boolean rule) {
/* 1019 */     this.m_RuleInduceOnly = rule;
/*      */   }
/*      */   
/*      */   public boolean isRuleInduceOnly() {
/* 1023 */     return this.m_RuleInduceOnly;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isTreeToRuleInduce() {
/* 1030 */     return (getSettings().getCoveringMethod() == 9);
/*      */   }
/*      */   
/*      */   public void setBeamSearch(boolean beam) {
/* 1034 */     this.m_BeamSearch = beam;
/*      */   }
/*      */   
/*      */   public boolean isBeamSearch() {
/* 1038 */     return this.m_BeamSearch;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public double getChiSquareInvProb(int df) {
/* 1046 */     return this.m_ChiSquareInvProb[df];
/*      */   }
/*      */   
/*      */   public void updateStatistics(ClusModel model) throws ClusException {
/* 1050 */     if (this.m_Hier != null) {
/* 1051 */       ArrayList<WHTDStatistic> stats = new ArrayList();
/* 1052 */       model.retrieveStatistics(stats);
/* 1053 */       for (int i = 0; i < stats.size(); i++) {
/* 1054 */         WHTDStatistic stat = stats.get(i);
/* 1055 */         stat.setHier(this.m_Hier);
/*      */       } 
/*      */     } 
/*      */   }
/*      */   
/*      */   private void createHierarchy() {
/* 1061 */     int idx = 0;
/* 1062 */     for (int i = 0; i < this.m_Schema.getNbAttributes(); i++) {
/* 1063 */       ClusAttrType type = this.m_Schema.getAttrType(i);
/* 1064 */       if (!type.isDisabled() && type instanceof ClassesAttrType) {
/* 1065 */         ClassesAttrType cltype = (ClassesAttrType)type;
/*      */ 
/*      */         
/* 1068 */         this.m_Hier = cltype.getHier();
/* 1069 */         idx++;
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public void initHierarchySettings() throws ClusException, IOException {
/* 1076 */     if (this.m_Hier != null && 
/* 1077 */       getSettings().hasHierEvalClasses()) {
/* 1078 */       ClassesTuple tuple = ClassesTuple.readFromFile(getSettings()
/* 1079 */           .getHierEvalClasses(), this.m_Hier);
/* 1080 */       this.m_Hier.setEvalClasses(tuple);
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
/*      */   public void initRuleSettings() throws ClusException {
/* 1093 */     Settings sett = getSettings();
/* 1094 */     int covering = sett.getCoveringMethod();
/* 1095 */     int prediction = sett.getRulePredictionMethod();
/*      */     
/* 1097 */     if ((sett.getHeuristic() != 8 || sett
/* 1098 */       .getHeuristic() != 9 || sett
/* 1099 */       .getHeuristic() != 10 || sett.getHeuristic() != 11) && sett
/* 1100 */       .isHeurRuleDist()) {
/* 1101 */       sett.setHeurRuleDistPar(0.0D);
/*      */     }
/* 1103 */     if (sett.isRuleSignificanceTesting()) {
/* 1104 */       Settings.IS_RULE_SIG_TESTING = true;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/* 1109 */     if (sett.isRandomRules()) {
/* 1110 */       sett.setCoveringMethod(0);
/*      */       
/* 1112 */       sett.setCoveringWeight(0.0D);
/*      */     }
/* 1114 */     else if (covering == 0) {
/*      */       
/* 1116 */       sett.setCoveringWeight(0.0D);
/*      */     }
/* 1118 */     else if (covering == 8) {
/* 1119 */       if (prediction == 0 || prediction == 7)
/*      */       {
/* 1121 */         sett.setRulePredictionMethod(2);
/*      */       }
/* 1123 */       sett.setCoveringWeight(0.0D);
/* 1124 */       if (getSettings().getHeurRuleDistPar() < 0.0D) {
/* 1125 */         throw new ClusException("Clus heuristic covering: HeurRuleDistPar must be >= 0!");
/*      */       }
/* 1127 */       if (sett.getHeuristic() != 8 || sett
/* 1128 */         .getHeuristic() != 9 || sett
/* 1129 */         .getHeuristic() != 10 || sett
/* 1130 */         .getHeuristic() != 11) {
/* 1131 */         throw new ClusException("Clus heuristic covering: Only dispersion-based heuristics supported!");
/*      */       }
/*      */     }
/* 1134 */     else if (covering == 2 || covering == 1 || covering == 3 || covering == 5 || covering == 6) {
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1139 */       if (prediction == 0 || prediction == 7)
/*      */       {
/* 1141 */         sett.setRulePredictionMethod(2);
/*      */       }
/* 1143 */       if (sett.getCoveringWeight() < 0.0D) {
/* 1144 */         throw new ClusException("Clus weighted covering: Covering weight must be >= 0!");
/*      */       }
/*      */     }
/* 1147 */     else if (covering == 7) {
/* 1148 */       sett.setRulePredictionMethod(6);
/*      */     }
/* 1150 */     else if (covering == 4) {
/* 1151 */       sett.setRulePredictionMethod(7);
/* 1152 */     } else if (covering == 9) {
/* 1153 */       sett.setHeuristic(5);
/* 1154 */       sett.setRuleAddingMethod(0);
/*      */     } 
/*      */   }
/*      */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\main\ClusStatManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */