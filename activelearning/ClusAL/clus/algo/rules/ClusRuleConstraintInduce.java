/*      */ package clus.algo.rules;
/*      */ 
/*      */ import clus.algo.ClusInductionAlgorithm;
/*      */ import clus.algo.split.CurrentBestTestAndHeuristic;
/*      */ import clus.data.attweights.ClusAttributeWeights;
/*      */ import clus.data.attweights.ClusNormalizedAttributeWeights;
/*      */ import clus.data.rows.DataTuple;
/*      */ import clus.data.rows.MemoryTupleIterator;
/*      */ import clus.data.rows.RowData;
/*      */ import clus.data.type.ClusAttrType;
/*      */ import clus.data.type.ClusSchema;
/*      */ import clus.data.type.NominalAttrType;
/*      */ import clus.data.type.NumericAttrType;
/*      */ import clus.ext.beamsearch.ClusBeam;
/*      */ import clus.ext.beamsearch.ClusBeamModel;
/*      */ import clus.ext.ilevelc.DerivedConstraintsComputer;
/*      */ import clus.ext.ilevelc.ILevelCStatistic;
/*      */ import clus.ext.ilevelc.ILevelConstraint;
/*      */ import clus.heuristic.ClusHeuristic;
/*      */ import clus.main.ClusRun;
/*      */ import clus.main.ClusStatManager;
/*      */ import clus.main.Settings;
/*      */ import clus.model.ClusModel;
/*      */ import clus.model.ClusModelInfo;
/*      */ import clus.model.test.ClusRuleConstraintInduceTest;
/*      */ import clus.model.test.NodeTest;
/*      */ import clus.statistic.ClusStatistic;
/*      */ import clus.statistic.RegressionStat;
/*      */ import clus.tools.optimization.GDAlg;
/*      */ import clus.tools.optimization.OptAlg;
/*      */ import clus.tools.optimization.OptProbl;
/*      */ import clus.tools.optimization.de.DeAlg;
/*      */ import clus.util.ClusException;
/*      */ import clus.util.ClusRandom;
/*      */ import java.io.FileOutputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.OutputStreamWriter;
/*      */ import java.io.PrintWriter;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Hashtable;
/*      */ import java.util.Iterator;
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
/*      */ public class ClusRuleConstraintInduce
/*      */   extends ClusInductionAlgorithm
/*      */ {
/*      */   protected boolean m_BeamChanged;
/*      */   protected FindBestTestRules m_FindBestTest;
/*      */   protected ClusHeuristic m_Heuristic;
/*      */   protected ArrayList<ILevelConstraint> m_Constraints;
/*      */   protected int size;
/*      */   protected ClusRuleConstraintInduceTest m_BestTest;
/*      */   protected double m_BestHeur;
/*      */   protected ArrayList<ILevelConstraint> m_BestConstraints;
/*      */   protected ClusNormalizedAttributeWeights m_Scale;
/*      */   protected RowData m_Data;
/*      */   private double m_Global_Var;
/*   68 */   protected double m_Alfa = 0.1D;
/*   69 */   protected double m_Gamma = 0.5D;
/*      */   private int m_MaxNbClasses;
/*      */   
/*      */   public ClusRuleConstraintInduce(ClusSchema schema, Settings sett) throws ClusException, IOException {
/*   73 */     super(schema, sett);
/*   74 */     this.m_FindBestTest = new FindBestTestRules(getStatManager());
/*      */   }
/*      */   
/*      */   void resetAll() {
/*   78 */     this.m_BeamChanged = false;
/*      */   }
/*      */   
/*      */   public void setHeuristic(ClusHeuristic heur) {
/*   82 */     this.m_Heuristic = heur;
/*      */   }
/*      */   
/*      */   public double estimateBeamMeasure(ClusRule rule) {
/*      */     double con;
/*   87 */     ClusStatistic cs = rule.m_ClusteringStat;
/*   88 */     double var = cs.getSVarS((ClusAttributeWeights)this.m_Scale) / this.m_Global_Var;
/*   89 */     double con1 = rule.getNumberOfViolatedConstraintsRCCC();
/*   90 */     double con2 = rule.getConstraints().size();
/*      */     
/*   92 */     if (con2 != 0.0D) {
/*   93 */       con = con1 / con2;
/*      */     } else {
/*   95 */       con = 0.0D;
/*   96 */     }  double cov1 = ((RowData)rule.getVisitor()).toArrayList().size();
/*   97 */     double cov2 = this.m_Data.getNbRows();
/*   98 */     double cov = cov1 / cov2;
/*   99 */     double gamma = this.m_Gamma;
/*  100 */     double alfa = this.m_Alfa;
/*  101 */     double result = (1.0D - (1.0D - gamma) * var + gamma * con) * Math.pow(cov, alfa);
/*  102 */     return result;
/*      */   }
/*      */   
/*      */   public boolean isBeamChanged() {
/*  106 */     return this.m_BeamChanged;
/*      */   }
/*      */   
/*      */   public void setBeamChanged(boolean change) {
/*  110 */     this.m_BeamChanged = change;
/*      */   }
/*      */   
/*      */   ClusBeam initializeBeam(RowData data) {
/*  114 */     Settings sett = getSettings();
/*  115 */     ClusBeam beam = new ClusBeam(sett.getBeamWidth(), sett.getBeamRemoveEqualHeur());
/*  116 */     ClusStatistic stat = createTotalClusteringStat(data);
/*  117 */     ClusRule rule = new ClusRule(getStatManager());
/*  118 */     rule.setClusteringStat(stat);
/*  119 */     rule.setVisitor(data);
/*      */     
/*  121 */     Iterator<ILevelConstraint> i = this.m_Constraints.iterator();
/*  122 */     ArrayList<ILevelConstraint> c = new ArrayList<>();
/*  123 */     ArrayList<DataTuple> ds = data.toArrayList();
/*  124 */     while (i.hasNext()) {
/*  125 */       ILevelConstraint ilc = i.next();
/*  126 */       if (ds.contains(ilc.getT1()) || ds.contains(ilc.getT2()))
/*  127 */         c.add(ilc); 
/*      */     } 
/*  129 */     rule.setConstraints(c);
/*  130 */     double value = estimateBeamMeasure(rule);
/*  131 */     beam.addModel(new ClusBeamModel(value, rule));
/*  132 */     return beam;
/*      */   }
/*      */   
/*      */   public void refineModel(ClusBeamModel model, ClusBeam beam, int model_idx) {
/*  136 */     ClusRule rule = (ClusRule)model.getModel();
/*  137 */     RowData data = (RowData)rule.getVisitor();
/*  138 */     ArrayList<ILevelConstraint> constraints = rule.getConstraints();
/*  139 */     if (this.m_FindBestTest.initSelectorAndStopCrit(rule.getClusteringStat(), data)) {
/*  140 */       model.setFinished(true);
/*      */       
/*      */       return;
/*      */     } 
/*  144 */     if (((RowData)rule.getVisitor()).getNbRows() <= 2) {
/*  145 */       model.setFinished(true);
/*      */       return;
/*      */     } 
/*  148 */     ClusAttrType[] attrs = data.getSchema().getDescriptiveAttributes();
/*  149 */     for (int i = 0; i < attrs.length; i++) {
/*  150 */       double beam_min_value = beam.getMinValue();
/*  151 */       ClusAttrType at = attrs[i];
/*  152 */       if (at instanceof NominalAttrType) {
/*  153 */         findNominal((NominalAttrType)at, data, constraints);
/*      */       } else {
/*      */         try {
/*  156 */           findNumeric((NumericAttrType)at, data, constraints, rule, this.size);
/*  157 */         } catch (ClusException clusException) {}
/*      */       } 
/*      */       
/*  160 */       System.out.println("Best test: " + this.m_BestTest);
/*  161 */       if (this.m_BestTest != null && this.m_BestHeur != Double.NEGATIVE_INFINITY) {
/*  162 */         RowData subset; ClusRuleConstraintInduceTest test = this.m_BestTest; if (Settings.VERBOSE > 0) System.out.println("  Test: " + test.getString() + " -> " + this.m_BestHeur);
/*      */         
/*  164 */         if (test.isSmallerThanTest()) {
/*  165 */           subset = data.applyConstraint(test, 1);
/*      */         } else {
/*  167 */           subset = data.applyConstraint(test, 0);
/*  168 */         }  ClusRule ref_rule = rule.cloneRule();
/*  169 */         ref_rule.addTest((NodeTest)test);
/*  170 */         ref_rule.setVisitor(subset);
/*  171 */         ref_rule.setClusteringStat(createTotalClusteringStat(subset));
/*  172 */         ref_rule.setConstraints(this.m_BestConstraints);
/*  173 */         if (getSettings().isHeurRuleDist()) {
/*  174 */           int[] subset_idx = new int[subset.getNbRows()];
/*  175 */           for (int j = 0; j < subset_idx.length; j++) {
/*  176 */             subset_idx[j] = subset.getTuple(j).getIndex();
/*      */           }
/*  178 */           ((ClusRuleHeuristicDispersion)this.m_Heuristic).setDataIndexes(subset_idx);
/*      */         } 
/*  180 */         double new_heur = sanityCheck(this.m_BestHeur, ref_rule);
/*      */         
/*  182 */         if (new_heur > beam_min_value) {
/*  183 */           ClusBeamModel new_model = new ClusBeamModel(new_heur, ref_rule);
/*  184 */           new_model.setParentModelIndex(model_idx);
/*  185 */           beam.addModel(new_model);
/*  186 */           setBeamChanged(true);
/*      */         } 
/*      */       } 
/*      */     } 
/*      */   }
/*      */   
/*      */   public void refineBeam(ClusBeam beam) {
/*  193 */     setBeamChanged(false);
/*  194 */     ArrayList<ClusBeamModel> models = beam.toArray();
/*  195 */     this.m_BestTest = null;
/*  196 */     this.m_BestHeur = Double.NEGATIVE_INFINITY;
/*  197 */     this.m_BestConstraints = null;
/*  198 */     for (int i = 0; i < models.size(); i++) {
/*  199 */       ClusBeamModel model = models.get(i);
/*  200 */       if (!model.isRefined() && !model.isFinished()) {
/*      */ 
/*      */         
/*  203 */         refineModel(model, beam, i);
/*  204 */         model.setRefined(true);
/*  205 */         model.setParentModelIndex(-1);
/*      */       } 
/*      */     } 
/*      */   }
/*      */   
/*      */   public ClusRule learnOneRule(RowData data) {
/*  211 */     ClusBeam beam = initializeBeam(data);
/*  212 */     int i = 0;
/*      */     while (true) {
/*  214 */       if (Settings.VERBOSE > 0) {
/*  215 */         System.out.println("Step: " + i);
/*      */       } else {
/*  217 */         if (i != 0) {
/*  218 */           System.out.print(",");
/*      */         }
/*  220 */         System.out.print(i);
/*      */       } 
/*  222 */       System.out.flush();
/*  223 */       refineBeam(beam);
/*  224 */       System.out.println();
/*  225 */       if (!isBeamChanged()) {
/*      */         break;
/*      */       }
/*  228 */       i++;
/*      */     } 
/*  230 */     System.out.println();
/*  231 */     double best = beam.getBestModel().getValue();
/*  232 */     double worst = beam.getWorstModel().getValue();
/*  233 */     System.out.println("Worst = " + worst + " Best = " + best);
/*  234 */     ClusRule result = (ClusRule)beam.getBestAndSmallestModel().getModel();
/*      */     
/*  236 */     RowData rule_data = (RowData)result.getVisitor();
/*  237 */     result.setTargetStat(createTotalTargetStat(rule_data));
/*      */     
/*  239 */     return result;
/*      */   }
/*      */   
/*      */   public ClusRule learnEmptyRule(RowData data) {
/*  243 */     ClusRule result = new ClusRule(getStatManager());
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  248 */     return result;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ClusRule[] learnBeamOfRules(RowData data) {
/*  257 */     ClusBeam beam = initializeBeam(data);
/*  258 */     int i = 0;
/*  259 */     System.out.print("Step: ");
/*      */     while (true) {
/*  261 */       if (Settings.VERBOSE > 0) {
/*  262 */         System.out.println("Step: " + i);
/*      */       } else {
/*  264 */         if (i != 0) {
/*  265 */           System.out.print(",");
/*      */         }
/*  267 */         System.out.print(i);
/*      */       } 
/*  269 */       System.out.flush();
/*  270 */       refineBeam(beam);
/*  271 */       if (!isBeamChanged()) {
/*      */         break;
/*      */       }
/*  274 */       i++;
/*      */     } 
/*  276 */     System.out.println();
/*  277 */     double best = beam.getBestModel().getValue();
/*  278 */     double worst = beam.getWorstModel().getValue();
/*  279 */     System.out.println("Worst = " + worst + " Best = " + best);
/*  280 */     ArrayList<ClusBeamModel> beam_models = beam.toArray();
/*  281 */     ClusRule[] result = new ClusRule[beam_models.size()];
/*  282 */     for (int j = 0; j < beam_models.size(); j++) {
/*      */       
/*  284 */       int k = beam_models.size() - j - 1;
/*  285 */       ClusRule rule = (ClusRule)((ClusBeamModel)beam_models.get(k)).getModel();
/*      */       
/*  287 */       RowData rule_data = (RowData)rule.getVisitor();
/*  288 */       rule.setTargetStat(createTotalTargetStat(rule_data));
/*  289 */       rule.setVisitor(null);
/*  290 */       rule.simplify();
/*  291 */       result[j] = rule;
/*      */     } 
/*  293 */     return result;
/*      */   }
/*      */   
/*      */   public void separateAndConquor(ClusRuleSet rset, RowData data) {
/*  297 */     while (data.getNbRows() > 0) {
/*  298 */       ClusRule rule = learnOneRule(data);
/*  299 */       if (rule.isEmpty()) {
/*      */         break;
/*      */       }
/*  302 */       rule.computePrediction();
/*  303 */       rule.printModel();
/*  304 */       System.out.println();
/*  305 */       rset.add(rule);
/*  306 */       data = rule.removeCovered(data);
/*      */     } 
/*      */     
/*  309 */     ClusStatistic left_over = createTotalTargetStat(data);
/*  310 */     left_over.calcMean();
/*  311 */     System.out.println("Left Over: " + left_over);
/*  312 */     rset.setTargetStat(left_over);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void separateAndConquorWeighted(ClusRuleSet rset, RowData data) throws ClusException {
/*  322 */     int max_rules = getSettings().getMaxRulesNb();
/*  323 */     int i = 0;
/*  324 */     RowData data_copy = (RowData)data.deepCloneData();
/*  325 */     ArrayList<boolean[]> bit_vect_array = (ArrayList)new ArrayList<>();
/*  326 */     while (data.getNbRows() > 0 && i < max_rules) {
/*  327 */       ClusRule rule = learnOneRule(data);
/*  328 */       if (rule.isEmpty()) {
/*      */         break;
/*      */       }
/*  331 */       rule.computePrediction();
/*  332 */       rule.printModel();
/*  333 */       System.out.println();
/*  334 */       rset.add(rule);
/*  335 */       data = rule.reweighCovered(data);
/*  336 */       i++;
/*  337 */       if (getSettings().isHeurRuleDist()) {
/*  338 */         boolean[] bit_vect = new boolean[data_copy.getNbRows()];
/*  339 */         for (int j = 0; j < bit_vect.length; j++) {
/*  340 */           if (!bit_vect[j]) {
/*  341 */             for (int k = 0; k < rset.getModelSize(); k++) {
/*  342 */               if (rset.getRule(k).covers(data_copy.getTuple(j))) {
/*  343 */                 bit_vect[j] = true;
/*      */                 break;
/*      */               } 
/*      */             } 
/*      */           }
/*      */         } 
/*  349 */         bit_vect_array.add(bit_vect);
/*  350 */         ((ClusRuleHeuristicDispersion)this.m_Heuristic).setCoveredBitVectArray(bit_vect_array);
/*      */       } 
/*      */     } 
/*      */     
/*  354 */     ClusStatistic left_over = createTotalTargetStat(data);
/*  355 */     left_over.calcMean();
/*  356 */     System.out.println("Left Over: " + left_over);
/*  357 */     rset.setTargetStat(left_over);
/*      */   }
/*      */   
/*      */   public double sanityCheck(double value, ClusRule rule) {
/*  361 */     double expected = estimateBeamMeasure(rule);
/*  362 */     if (Math.abs(value - expected) > 1.0E-6D) {
/*  363 */       System.out.println("Bug in heurisitc: " + value + " <> " + expected);
/*  364 */       PrintWriter wrt = new PrintWriter(System.out);
/*  365 */       rule.printModel(wrt);
/*  366 */       wrt.close();
/*  367 */       System.out.flush();
/*  368 */       System.exit(1);
/*      */     } 
/*  370 */     return expected;
/*      */   }
/*      */   
/*      */   public ClusModel induce(ClusRun run) throws ClusException, IOException {
/*  374 */     int method = getSettings().getCoveringMethod();
/*  375 */     int add_method = getSettings().getRuleAddingMethod();
/*  376 */     RowData data = (RowData)run.getTrainingSet();
/*  377 */     ClusStatistic stat = createTotalClusteringStat(data);
/*  378 */     this.m_FindBestTest.initSelectorAndSplit(stat);
/*  379 */     setHeuristic(this.m_FindBestTest.getBestTest().getHeuristic());
/*  380 */     ClusRuleSet rset = new ClusRuleSet(getStatManager());
/*  381 */     if (method == 0) {
/*  382 */       separateAndConquor(rset, data);
/*      */     } else {
/*  384 */       separateAndConquorWeighted(rset, data);
/*      */     } 
/*  386 */     rset.postProc();
/*      */     
/*  388 */     if (getSettings().getRulePredictionMethod() == 6) {
/*  389 */       rset = optimizeRuleSet(rset, data);
/*      */     }
/*      */     
/*  392 */     if (getSettings().computeDispersion()) {
/*  393 */       rset.addDataToRules(data);
/*  394 */       rset.computeDispersion(0);
/*  395 */       rset.removeDataFromRules();
/*  396 */       if (run.getTestIter() != null) {
/*  397 */         RowData testdata = run.getTestSet();
/*  398 */         rset.addDataToRules(testdata);
/*  399 */         rset.computeDispersion(1);
/*  400 */         rset.removeDataFromRules();
/*      */       } 
/*      */     } 
/*      */     
/*  404 */     rset.numberRules();
/*  405 */     return rset;
/*      */   }
/*      */   public ClusRuleSet optimizeRuleSet(ClusRuleSet rset, RowData data) throws ClusException, IOException {
/*      */     DeAlg deAlg;
/*  409 */     String fname = getSettings().getDataFile();
/*  410 */     PrintWriter wrt_pred = new PrintWriter(new OutputStreamWriter(new FileOutputStream(fname + ".r-pred")));
/*      */     
/*  412 */     OptAlg optAlg = null;
/*      */     
/*  414 */     OptProbl.OptParam param = rset.giveFormForWeightOptimization(wrt_pred, data);
/*      */ 
/*      */     
/*  417 */     if (getSettings().getRulePredictionMethod() == 8) {
/*  418 */       GDAlg gDAlg = new GDAlg(getStatManager(), param, rset);
/*      */     } else {
/*  420 */       deAlg = new DeAlg(getStatManager(), param, rset);
/*      */     } 
/*      */     
/*  423 */     ArrayList<Double> weights = deAlg.optimize();
/*      */ 
/*      */     
/*  426 */     System.out.print("The weights for rules:");
/*  427 */     for (int j = 0; j < rset.getModelSize(); j++) {
/*  428 */       rset.getRule(j).setOptWeight(((Double)weights.get(j)).doubleValue());
/*  429 */       System.out.print(((Double)weights.get(j)).doubleValue() + "; ");
/*      */     } 
/*  431 */     System.out.print("\n");
/*  432 */     rset.removeLowWeightRules();
/*  433 */     RowData data_copy = (RowData)data.cloneData();
/*  434 */     updateDefaultRule(rset, data_copy);
/*      */     
/*  436 */     return rset;
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void updateDefaultRule(ClusRuleSet rset, RowData data) {
/*  530 */     for (int i = 0; i < rset.getModelSize(); i++) {
/*  531 */       data = rset.getRule(i).removeCovered(data);
/*      */     }
/*  533 */     ClusStatistic left_over = createTotalTargetStat(data);
/*  534 */     left_over.calcMean();
/*  535 */     System.out.println("Left Over: " + left_over);
/*  536 */     rset.setTargetStat(left_over);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ClusModel induceRandomly(ClusRun run) throws ClusException, IOException {
/*  545 */     int number = getSettings().nbRandomRules();
/*  546 */     RowData data = (RowData)run.getTrainingSet();
/*  547 */     ClusStatistic stat = createTotalClusteringStat(data);
/*  548 */     this.m_FindBestTest.initSelectorAndSplit(stat);
/*  549 */     setHeuristic(this.m_FindBestTest.getBestTest().getHeuristic());
/*  550 */     ClusRuleSet rset = new ClusRuleSet(getStatManager());
/*  551 */     Random rn = new Random(42L);
/*  552 */     for (int i = 0; i < number; i++) {
/*  553 */       ClusRule rule = generateOneRandomRule(data, rn);
/*  554 */       rule.computePrediction();
/*  555 */       rule.printModel();
/*  556 */       System.out.println();
/*  557 */       if (!rset.addIfUnique(rule)) {
/*  558 */         i--;
/*      */       }
/*      */     } 
/*  561 */     ClusStatistic left_over = createTotalTargetStat(data);
/*  562 */     left_over.calcMean();
/*  563 */     System.out.println("Left Over: " + left_over);
/*  564 */     rset.setTargetStat(left_over);
/*  565 */     rset.postProc();
/*      */     
/*  567 */     if (getSettings().computeDispersion()) {
/*  568 */       rset.addDataToRules(data);
/*  569 */       rset.computeDispersion(0);
/*  570 */       rset.removeDataFromRules();
/*  571 */       if (run.getTestIter() != null) {
/*  572 */         RowData testdata = run.getTestSet();
/*  573 */         rset.addDataToRules(testdata);
/*  574 */         rset.computeDispersion(1);
/*  575 */         rset.removeDataFromRules();
/*      */       } 
/*      */     } 
/*  578 */     return rset;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private ClusRule generateOneRandomRule(RowData data, Random rn) {
/*      */     int nb_tests;
/*  590 */     ClusStatManager mgr = getStatManager();
/*  591 */     ClusRule result = new ClusRule(mgr);
/*  592 */     ClusAttrType[] attrs = data.getSchema().getDescriptiveAttributes();
/*      */     
/*  594 */     RowData orig_data = data;
/*      */ 
/*      */     
/*  597 */     if (attrs.length > 1) {
/*  598 */       nb_tests = rn.nextInt(attrs.length - 1) + 1;
/*      */     } else {
/*  600 */       nb_tests = 1;
/*      */     } 
/*      */     
/*  603 */     int[] test_atts = new int[nb_tests];
/*  604 */     for (int i = 0; i < nb_tests; ) {
/*      */       while (true) {
/*  606 */         int att_idx = rn.nextInt(attrs.length);
/*  607 */         boolean unique = true;
/*  608 */         for (int k = 0; k < i; k++) {
/*  609 */           if (att_idx == test_atts[k]) {
/*  610 */             unique = false;
/*      */           }
/*      */         } 
/*  613 */         if (unique) {
/*  614 */           test_atts[i] = att_idx; break;
/*      */         } 
/*      */       } 
/*      */       i++;
/*      */     } 
/*  619 */     CurrentBestTestAndHeuristic sel = this.m_FindBestTest.getBestTest();
/*  620 */     for (int j = 0; j < test_atts.length; j++) {
/*  621 */       result.setClusteringStat(createTotalClusteringStat(data));
/*  622 */       if (this.m_FindBestTest.initSelectorAndStopCrit(result.getClusteringStat(), data)) {
/*      */         break;
/*      */       }
/*      */       
/*  626 */       sel.resetBestTest();
/*  627 */       sel.setBestHeur(Double.NEGATIVE_INFINITY);
/*  628 */       ClusAttrType at = attrs[test_atts[j]];
/*  629 */       if (at instanceof NominalAttrType) {
/*  630 */         this.m_FindBestTest.findNominalRandom((NominalAttrType)at, data, rn);
/*      */       } else {
/*  632 */         this.m_FindBestTest.findNumericRandom((NumericAttrType)at, data, orig_data, rn);
/*      */       } 
/*  634 */       if (sel.hasBestTest()) {
/*  635 */         NodeTest test = sel.updateTest();
/*  636 */         if (Settings.VERBOSE > 0) System.out.println("  Test: " + test.getString() + " -> " + sel.m_BestHeur); 
/*  637 */         result.addTest(test);
/*      */         
/*  639 */         data = data.apply(test, 0);
/*      */       } 
/*      */     } 
/*      */     
/*  643 */     result.setTargetStat(createTotalTargetStat(data));
/*  644 */     result.setClusteringStat(createTotalClusteringStat(data));
/*  645 */     return result;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public ClusModel induceSingleUnpruned(ClusRun cr) throws ClusException, IOException {
/*  651 */     resetAll();
/*  652 */     if (!getSettings().isRandomRules()) {
/*  653 */       return induce(cr);
/*      */     }
/*  655 */     return induceRandomly(cr);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void induceAll(ClusRun cr) throws ClusException, IOException {
/*  661 */     ClusRandom.reset(4);
/*  662 */     RowData data = (RowData)cr.getTrainingSet();
/*  663 */     int nbRows = data.getNbRows();
/*      */     
/*  665 */     RowData test = cr.getTestSet();
/*  666 */     if (test != null) {
/*  667 */       ArrayList allData = new ArrayList();
/*  668 */       data.addTo(allData);
/*  669 */       test.addTo(allData);
/*  670 */       data = new RowData(allData, data.getSchema());
/*      */     } 
/*  672 */     System.out.println("All data: " + data.getNbRows());
/*  673 */     this.size = data.getNbRows();
/*  674 */     data.addIndices();
/*  675 */     ArrayList points = data.toArrayList();
/*      */     
/*  677 */     if (getSettings().hasILevelCFile()) {
/*  678 */       String fname = getSettings().getILevelCFile();
/*  679 */       this.m_Constraints = ILevelConstraint.loadConstraints(fname, points);
/*  680 */       ClusAttrType type = getSchema().getAttrType(getSchema().getNbAttributes() - 1);
/*  681 */       if (type.getTypeIndex() == 0) {
/*  682 */         NominalAttrType cls = (NominalAttrType)type;
/*  683 */         this.m_MaxNbClasses = cls.getNbValues();
/*      */       } 
/*      */     } else {
/*  686 */       this.m_Constraints = createConstraints(data, nbRows);
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  692 */     System.out.println("All constraints: " + this.m_Constraints.size());
/*      */     
/*  694 */     this.m_Scale = (ClusNormalizedAttributeWeights)getStatManager().getClusteringWeights();
/*  695 */     this.m_Data = data;
/*  696 */     ClusStatistic allStat = getStatManager().createStatistic(2);
/*  697 */     data.calcTotalStat(allStat);
/*  698 */     this.m_Global_Var = allStat.getSVarS((ClusAttributeWeights)this.m_Scale);
/*  699 */     System.out.println("Global Variance: " + this.m_Global_Var);
/*  700 */     ClusModel model = induceSingleUnpruned(cr);
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  705 */     ClusRuleSet crs = (ClusRuleSet)model;
/*      */     
/*  707 */     labelRules(crs);
/*      */ 
/*      */ 
/*      */     
/*  711 */     ClusModelInfo pruned_model = cr.addModelInfo(1);
/*  712 */     pruned_model.setModel(model);
/*  713 */     pruned_model.setName("Original");
/*      */   }
/*      */ 
/*      */   
/*      */   private void createExtraConstraints(ArrayList<ILevelConstraint> constraints, ArrayList points) {
/*  718 */     DerivedConstraintsComputer comp = new DerivedConstraintsComputer(points, this.m_Constraints);
/*  719 */     comp.compute();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void labelRulesSimple(ClusRuleSet crs) {
/*  726 */     for (int i = 0; i < crs.getModelSize(); i++) {
/*  727 */       ClusRule rule = crs.getRule(i);
/*  728 */       ((ILevelCStatistic)rule.getTargetStat()).setClusterID(i);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void labelRulesKMeansInit(ClusRuleSet crs) {
/*  736 */     int iterations = 50;
/*  737 */     ArrayList<int[]> labels = (ArrayList)new ArrayList<>();
/*  738 */     for (int i = 0; i < iterations; i++) {
/*  739 */       int[] l = labelRulesKMeans(crs);
/*  740 */       labels.add(l);
/*      */     } 
/*      */     
/*  743 */     int bestfreq = 0;
/*  744 */     int[] bestLabel = new int[crs.getModelSize()]; int j;
/*  745 */     for (j = 0; j < iterations; j++) {
/*  746 */       int[] l = labels.get(j);
/*  747 */       int freq = 1;
/*  748 */       for (int k = j + 1; k < iterations; k++) {
/*  749 */         int[] c = labels.get(k);
/*  750 */         if (sameLabeling(c, l))
/*  751 */           freq++; 
/*      */       } 
/*  753 */       if (bestfreq < freq) {
/*  754 */         bestfreq = freq;
/*  755 */         bestLabel = l;
/*      */       } 
/*      */     } 
/*  758 */     for (j = 0; j < crs.getModelSize(); j++) {
/*  759 */       ClusRule rule = crs.getRule(j);
/*  760 */       ((ILevelCStatistic)rule.getTargetStat()).setClusterID(bestLabel[j]);
/*      */     } 
/*      */   }
/*      */   
/*      */   private boolean sameLabeling(int[] c, int[] l) {
/*  765 */     for (int i = 0; i < c.length; i++) {
/*  766 */       if (c[i] != l[i])
/*  767 */         return false; 
/*      */     } 
/*  769 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private int[] labelRulesKMeans(ClusRuleSet crs) {
/*  776 */     ArrayList<ArrayList<Double>> averages = new ArrayList<>();
/*  777 */     Hashtable<ClusRule, ArrayList<Double>> hash = new Hashtable<>();
/*  778 */     Hashtable<ArrayList<Double>, int[]> weights = (Hashtable)new Hashtable<>();
/*  779 */     Hashtable<ArrayList<Double>, ArrayList<Double>> assign = new Hashtable<>();
/*  780 */     for (int i = 0; i < crs.getModelSize(); i++) {
/*  781 */       ClusRule rule = crs.getRule(i);
/*  782 */       int[] w = new int[1];
/*  783 */       w[0] = ((RowData)rule.getVisitor()).toArrayList().size();
/*  784 */       ArrayList<Double> av = computeAverage(rule);
/*  785 */       weights.put(av, w);
/*  786 */       averages.add(av);
/*  787 */       hash.put(rule, av);
/*      */     } 
/*  789 */     ArrayList<Double> min = getMinima(averages);
/*  790 */     ArrayList<Double> max = getMaxima(averages);
/*      */     
/*  792 */     ArrayList<ArrayList<Double>> centers = createCenters(min, max, averages, crs);
/*      */ 
/*      */     
/*  795 */     for (int j = 0; j < averages.size(); j++) {
/*  796 */       assign.put(averages.get(j), centers.get(0));
/*      */     }
/*  798 */     boolean changed = true;
/*  799 */     while (changed) {
/*  800 */       changed = false;
/*  801 */       for (int m = 0; m < averages.size(); m++) {
/*  802 */         ArrayList<Double> closest = getClosestCenter(averages.get(m), centers, min, max);
/*  803 */         if (!((ArrayList)assign.get(averages.get(m))).equals(closest)) {
/*  804 */           changed = true;
/*  805 */           assign.remove(averages.get(m));
/*  806 */           assign.put(averages.get(m), closest);
/*      */         } 
/*      */       } 
/*  809 */       if (changed) {
/*  810 */         centers = computeAverage(averages, centers, assign, weights);
/*      */       }
/*      */     } 
/*  813 */     int[] labeling = new int[crs.getModelSize()];
/*  814 */     for (int k = 0; k < crs.getModelSize(); k++) {
/*  815 */       ClusRule rule = crs.getRule(k);
/*  816 */       ArrayList<Double> av = hash.get(rule);
/*      */       
/*  818 */       ArrayList<Double> center = assign.get(av);
/*  819 */       labeling[k] = centers.indexOf(center) + 1;
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  843 */     return labeling;
/*      */   }
/*      */ 
/*      */   
/*      */   private ArrayList<ArrayList<Double>> computeAverage(ArrayList<ArrayList<Double>> averages, ArrayList<ArrayList<Double>> centers, Hashtable<ArrayList<Double>, ArrayList<Double>> assign, Hashtable<ArrayList<Double>, int[]> weights) {
/*  848 */     ArrayList<ArrayList<Double>> result = new ArrayList<>();
/*  849 */     for (int i = 0; i < centers.size(); i++) {
/*  850 */       ArrayList<Double> average = new ArrayList<>();
/*      */       
/*  852 */       int totalWeight = 0;
/*  853 */       ArrayList<Double> center = centers.get(i);
/*  854 */       for (int z = 0; z < center.size(); z++) {
/*  855 */         average.add(Double.valueOf(0.0D));
/*      */       }
/*  857 */       for (int j = 0; j < averages.size(); j++) {
/*  858 */         ArrayList<Double> c = assign.get(averages.get(j));
/*  859 */         if (center.equals(c)) {
/*      */ 
/*      */ 
/*      */           
/*  863 */           totalWeight++;
/*  864 */           for (int x = 0; x < center.size(); x++) {
/*  865 */             double y = ((Double)average.get(x)).doubleValue();
/*  866 */             y += ((Double)((ArrayList<Double>)averages.get(j)).get(x)).doubleValue();
/*  867 */             average.set(x, Double.valueOf(y));
/*      */           } 
/*      */         } 
/*      */       } 
/*  871 */       if (totalWeight > 0) {
/*  872 */         for (int x = 0; x < center.size(); x++) {
/*  873 */           double y = ((Double)average.get(x)).doubleValue();
/*  874 */           y /= totalWeight;
/*  875 */           average.set(x, Double.valueOf(y));
/*      */         } 
/*  877 */         result.add(average);
/*      */       } else {
/*      */         
/*  880 */         result.add(centers.get(i));
/*      */       } 
/*  882 */     }  return result;
/*      */   }
/*      */ 
/*      */   
/*      */   private ArrayList<Double> getClosestCenter(ArrayList<Double> point, ArrayList<ArrayList<Double>> centers, ArrayList<Double> min, ArrayList<Double> max) {
/*  887 */     ArrayList<Double> closest = new ArrayList<>();
/*  888 */     double distance = Double.POSITIVE_INFINITY;
/*  889 */     for (int i = 0; i < centers.size(); i++) {
/*  890 */       double d = calculateDistance(point, centers.get(i), min, max);
/*  891 */       if (d < distance) {
/*  892 */         distance = d;
/*  893 */         closest = centers.get(i);
/*      */       } 
/*      */     } 
/*  896 */     return closest;
/*      */   }
/*      */ 
/*      */   
/*      */   private double calculateDistance(ArrayList<Double> point, ArrayList<Double> center, ArrayList<Double> min, ArrayList<Double> max) {
/*  901 */     double distance = 0.0D;
/*  902 */     for (int i = 0; i < center.size(); i++) {
/*  903 */       double norm = ((Double)max.get(i)).doubleValue() - ((Double)min.get(i)).doubleValue();
/*  904 */       double s = ((Double)point.get(i)).doubleValue() - ((Double)center.get(i)).doubleValue();
/*  905 */       distance += Math.pow(s / norm, 2.0D);
/*      */     } 
/*  907 */     return Math.sqrt(distance);
/*      */   }
/*      */ 
/*      */   
/*      */   private ArrayList<ArrayList<Double>> createCenters(ArrayList<Double> min, ArrayList<Double> max, ArrayList<ArrayList<Double>> averages, ClusRuleSet crs) {
/*  912 */     ArrayList<ArrayList<Double>> centers = new ArrayList<>();
/*  913 */     for (int i = 0; i < this.m_MaxNbClasses; i++) {
/*  914 */       ArrayList<Double> c = new ArrayList<>();
/*  915 */       for (int j = 0; j < min.size(); j++) {
/*  916 */         double p = ClusRandom.nextDouble(4) * (((Double)max.get(j)).doubleValue() - ((Double)min.get(j)).doubleValue()) + ((Double)min.get(j)).doubleValue();
/*  917 */         c.add(Double.valueOf(p));
/*      */       } 
/*  919 */       centers.add(c);
/*      */     } 
/*  921 */     return centers;
/*      */   }
/*      */   
/*      */   private ArrayList<ArrayList<Double>> createCentersFrequencyBased(ArrayList<ArrayList<Double>> averages, ClusRuleSet crs) {
/*  925 */     ArrayList<ArrayList<Double>> centers = new ArrayList<>();
/*  926 */     ArrayList<ArrayList<Double>> clone = (ArrayList<ArrayList<Double>>)averages.clone();
/*  927 */     double m = Double.POSITIVE_INFINITY;
/*  928 */     for (int i = 0; i < this.m_MaxNbClasses; i++) {
/*  929 */       int c = getMostFrequentClone(clone, crs, m);
/*  930 */       ClusRule rule = crs.getRule(c);
/*  931 */       m = ((RowData)rule.getVisitor()).toArrayList().size();
/*  932 */       centers.add(clone.get(c));
/*      */     } 
/*  934 */     return centers;
/*      */   }
/*      */ 
/*      */   
/*      */   private int getMostFrequentClone(ArrayList<ArrayList<Double>> clone, ClusRuleSet crs, double m) {
/*  939 */     int highestFreq = 0;
/*  940 */     int c = -1;
/*  941 */     System.out.println("max" + m);
/*  942 */     for (int i = 0; i < clone.size(); i++) {
/*  943 */       ClusRule rule = crs.getRule(i);
/*  944 */       int freq = ((RowData)rule.getVisitor()).toArrayList().size();
/*  945 */       System.out.println(freq);
/*  946 */       if (freq > highestFreq && freq < m) {
/*  947 */         c = i;
/*  948 */         highestFreq = freq;
/*      */       } 
/*      */     } 
/*  951 */     System.out.println("freq:" + highestFreq);
/*  952 */     System.out.println(c);
/*  953 */     return c;
/*      */   }
/*      */   
/*      */   private ArrayList<Double> getMinima(ArrayList<ArrayList<Double>> averages) {
/*  957 */     ArrayList<Double> min = new ArrayList<>();
/*  958 */     Iterator<ArrayList<Double>> i = averages.iterator();
/*  959 */     ArrayList<Double> l1 = i.next();
/*  960 */     for (int j = 0; j < l1.size(); j++) {
/*  961 */       double a = ((Double)l1.get(j)).doubleValue();
/*  962 */       min.add(Double.valueOf(a));
/*      */     } 
/*  964 */     while (i.hasNext()) {
/*  965 */       ArrayList<Double> l = i.next();
/*  966 */       for (int k = 0; k < l.size(); k++) {
/*  967 */         double a = ((Double)l.get(k)).doubleValue();
/*  968 */         if (a < ((Double)min.get(k)).doubleValue())
/*  969 */           min.set(k, Double.valueOf(a)); 
/*      */       } 
/*      */     } 
/*  972 */     return min;
/*      */   }
/*      */   
/*      */   private ArrayList<Double> getMaxima(ArrayList<ArrayList<Double>> averages) {
/*  976 */     ArrayList<Double> max = new ArrayList<>();
/*  977 */     Iterator<ArrayList<Double>> i = averages.iterator();
/*  978 */     ArrayList<Double> l1 = i.next();
/*  979 */     for (int j = 0; j < l1.size(); j++) {
/*  980 */       double a = ((Double)l1.get(j)).doubleValue();
/*  981 */       max.add(Double.valueOf(a));
/*      */     } 
/*  983 */     while (i.hasNext()) {
/*  984 */       ArrayList<Double> l = i.next();
/*  985 */       for (int k = 0; k < l.size(); k++) {
/*  986 */         double a = ((Double)l.get(k)).doubleValue();
/*  987 */         if (a > ((Double)max.get(k)).doubleValue())
/*  988 */           max.set(k, Double.valueOf(a)); 
/*      */       } 
/*      */     } 
/*  991 */     return max;
/*      */   }
/*      */   
/*      */   private ArrayList<Double> computeAverage(ClusRule rule) {
/*  995 */     RowData data = (RowData)rule.getVisitor();
/*  996 */     ClusAttrType[] attrs = data.getSchema().getDescriptiveAttributes();
/*  997 */     ArrayList<Double> average = new ArrayList<>();
/*  998 */     for (int i = 0; i < attrs.length; i++) {
/*  999 */       average.add(Double.valueOf(0.0D));
/*      */     }
/* 1001 */     ArrayList<DataTuple> tuples = ((RowData)rule.getVisitor()).toArrayList();
/* 1002 */     Iterator<DataTuple> iterator = tuples.iterator();
/* 1003 */     while (iterator.hasNext()) {
/* 1004 */       DataTuple t = iterator.next();
/* 1005 */       for (int k = 0; k < attrs.length; k++) {
/* 1006 */         Double a = average.get(k);
/* 1007 */         a = Double.valueOf(a.doubleValue() + t.getDoubleVal(k));
/* 1008 */         average.set(k, a);
/*      */       } 
/*      */     } 
/* 1011 */     for (int j = 0; j < attrs.length; j++) {
/* 1012 */       Double a = average.get(j);
/* 1013 */       a = Double.valueOf(a.doubleValue() / tuples.size());
/* 1014 */       average.set(j, a);
/*      */     } 
/* 1016 */     return average;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void labelRules(ClusRuleSet crs) {
/* 1023 */     ArrayList<ArrayList<ClusRule>> clusters = new ArrayList<>(); int i;
/* 1024 */     for (i = 0; i < crs.getModelSize(); i++) {
/* 1025 */       ArrayList<ClusRule> cr = new ArrayList<>();
/* 1026 */       ClusRule rule = crs.getRule(i);
/* 1027 */       cr.add(rule);
/* 1028 */       clusters.add(cr);
/*      */     } 
/*      */     
/* 1031 */     while (clusters.size() > this.m_MaxNbClasses) {
/* 1032 */       int best_I = -1;
/* 1033 */       int best_J = -1;
/* 1034 */       double best_score = Double.NEGATIVE_INFINITY;
/* 1035 */       for (int j = 0; j < clusters.size(); j++) {
/* 1036 */         for (int k = 0; k < j; k++) {
/* 1037 */           ClusRule rule1 = getRule(clusters.get(j));
/* 1038 */           ClusRule rule2 = getRule(clusters.get(k));
/* 1039 */           ArrayList<ClusRule> c = new ArrayList<>();
/* 1040 */           c.add(rule1);
/* 1041 */           c.add(rule2);
/* 1042 */           ClusRule combo = getRule(c);
/*      */ 
/*      */           
/* 1045 */           double scoreC = calcNewHeur(combo);
/*      */           
/* 1047 */           if (scoreC > best_score) {
/* 1048 */             best_I = j;
/* 1049 */             best_J = k;
/* 1050 */             best_score = scoreC;
/*      */           } 
/*      */         } 
/*      */       } 
/*      */       
/* 1055 */       ArrayList<ClusRule> r = clusters.get(best_I);
/* 1056 */       int size_i = r.size();
/* 1057 */       int size_j = ((ArrayList)clusters.get(best_J)).size();
/* 1058 */       ((ArrayList<ClusRule>)clusters.get(best_J)).addAll(r);
/* 1059 */       int size = ((ArrayList)clusters.get(best_J)).size();
/* 1060 */       clusters.remove(best_I);
/*      */     } 
/*      */     
/* 1063 */     for (i = 0; i < clusters.size(); i++) {
/* 1064 */       ArrayList<ClusRule> c = clusters.get(i);
/* 1065 */       for (int j = 0; j < c.size(); j++) {
/* 1066 */         ClusRule rule = c.get(j);
/* 1067 */         ((ILevelCStatistic)rule.getTargetStat()).setClusterID(i + 1);
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private ClusRule getRule(ArrayList<ClusRule> c) {
/* 1076 */     ClusRule n = c.get(0);
/* 1077 */     for (int i = 1; i < c.size(); i++) {
/* 1078 */       ClusRule combo = new ClusRule(getStatManager());
/* 1079 */       ClusRule rule = c.get(i);
/* 1080 */       RowData data = new RowData(getSchema());
/* 1081 */       data.addAll((RowData)rule.getVisitor(), (RowData)n.getVisitor());
/* 1082 */       ClusStatistic stat = createTotalClusteringStat(data);
/* 1083 */       combo.setClusteringStat(stat);
/* 1084 */       combo.setVisitor(data);
/* 1085 */       ArrayList<ILevelConstraint> cons = (ArrayList<ILevelConstraint>)n.getConstraints().clone();
/* 1086 */       Iterator<ILevelConstraint> it = rule.getConstraints().iterator();
/* 1087 */       while (it.hasNext()) {
/* 1088 */         ILevelConstraint ilc = it.next();
/* 1089 */         if (!cons.contains(ilc))
/* 1090 */           cons.add(ilc); 
/*      */       } 
/* 1092 */       combo.setConstraints(cons);
/* 1093 */       n = combo;
/*      */     } 
/* 1095 */     return n;
/*      */   }
/*      */   private double calcNewHeur(ClusRule rule) {
/*      */     double con;
/* 1099 */     ClusStatistic cs = rule.m_ClusteringStat;
/* 1100 */     double var = cs.getSVarS((ClusAttributeWeights)this.m_Scale) / this.m_Global_Var;
/* 1101 */     double con1 = rule.getNumberOfViolatedConstraintsRCCC();
/* 1102 */     double con2 = rule.getConstraints().size();
/*      */     
/* 1104 */     if (con2 != 0.0D) {
/* 1105 */       con = con1 / con2;
/*      */     } else {
/* 1107 */       con = 0.0D;
/* 1108 */     }  double cov1 = ((RowData)rule.getVisitor()).toArrayList().size();
/* 1109 */     double cov2 = this.m_Data.getNbRows();
/* 1110 */     double cov = cov1 / cov2;
/* 1111 */     double gamma = this.m_Gamma;
/* 1112 */     double alfa = this.m_Alfa;
/* 1113 */     double result = (1.0D - (1.0D - gamma) * var + gamma * con) * Math.pow(cov, alfa);
/*      */ 
/*      */ 
/*      */     
/* 1117 */     return result;
/*      */   }
/*      */   
/*      */   public ArrayList<ILevelConstraint> createConstraints(RowData data, int nbRows) {
/* 1121 */     ArrayList<ILevelConstraint> constr = new ArrayList<>();
/* 1122 */     ClusAttrType type = getSchema().getAttrType(getSchema().getNbAttributes() - 1);
/* 1123 */     if (type.getTypeIndex() == 0) {
/* 1124 */       NominalAttrType cls = (NominalAttrType)type;
/* 1125 */       this.m_MaxNbClasses = cls.getNbValues();
/* 1126 */       int nbConstraints = getSettings().getILevelCNbRandomConstraints();
/* 1127 */       for (int i = 0; i < nbConstraints; i++) {
/* 1128 */         int t1i = 0;
/* 1129 */         int t2i = 0;
/* 1130 */         while (t1i == t2i) {
/* 1131 */           t1i = ClusRandom.nextInt(4, nbRows);
/* 1132 */           t2i = ClusRandom.nextInt(4, nbRows);
/*      */         } 
/* 1134 */         DataTuple t1 = data.getTuple(t1i);
/* 1135 */         DataTuple t2 = data.getTuple(t2i);
/* 1136 */         if (cls.getNominal(t1) == cls.getNominal(t2)) {
/* 1137 */           constr.add(new ILevelConstraint(t1, t2, 0));
/*      */         } else {
/* 1139 */           constr.add(new ILevelConstraint(t1, t2, 1));
/*      */         } 
/*      */       } 
/*      */     } 
/* 1143 */     return constr;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void findNominal(NominalAttrType type, RowData data, ArrayList<ILevelConstraint> constraints) {}
/*      */ 
/*      */ 
/*      */   
/*      */   public void findNumeric(NumericAttrType at, RowData data, ArrayList<ILevelConstraint> constraints, ClusRule rule, int size) throws ClusException {
/* 1154 */     if (at.isSparse()) {
/*      */ 
/*      */       
/* 1157 */       System.exit(1);
/*      */     } else {
/* 1159 */       data.sort(at);
/*      */     } 
/* 1161 */     if (at.hasMissing()) {
/* 1162 */       throw new ClusException("Does not support attributes with missing values: " + at.getName());
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1168 */     this.m_BestHeur = Double.NEGATIVE_INFINITY;
/* 1169 */     ILevelCStatistic cs = (ILevelCStatistic)rule.getClusteringStat();
/* 1170 */     RegressionStat s = new RegressionStat(cs.m_Attrs);
/* 1171 */     RegressionStat s_I = new RegressionStat(cs.m_Attrs);
/*      */ 
/*      */     
/* 1174 */     for (int i = 0; i < data.getNbRows(); i++) {
/* 1175 */       DataTuple tuple = data.getTuple(i);
/* 1176 */       s.updateWeighted(tuple, tuple.getWeight());
/* 1177 */       s_I.updateWeighted(tuple, tuple.getWeight());
/*      */     } 
/* 1179 */     int idx = at.getArrayIndex();
/* 1180 */     int Cviol = 0;
/*      */     
/* 1182 */     Hashtable<DataTuple, ArrayList<ILevelConstraint>> hash = new Hashtable<>(data.getNbRows());
/* 1183 */     MemoryTupleIterator dataIterator = data.getIterator();
/* 1184 */     for (int a = 0; a < dataIterator.getNbExamples(); a++) {
/* 1185 */       DataTuple dataTuple = dataIterator.readTuple();
/* 1186 */       hash.put(dataTuple, new ArrayList<>());
/*      */     } 
/* 1188 */     Iterator<ILevelConstraint> it = constraints.iterator();
/* 1189 */     ArrayList<ILevelConstraint> ML = new ArrayList<>();
/* 1190 */     ArrayList<ILevelConstraint> CL = new ArrayList<>();
/* 1191 */     ArrayList<DataTuple> d = data.toArrayList();
/* 1192 */     while (it.hasNext()) {
/* 1193 */       ILevelConstraint c = it.next();
/* 1194 */       if (c.getType() == 0) {
/* 1195 */         ML.add(c);
/* 1196 */         DataTuple dataTuple1 = c.getT1();
/* 1197 */         DataTuple dataTuple2 = c.getT2();
/* 1198 */         if (hash.containsKey(dataTuple1)) {
/* 1199 */           ArrayList<ILevelConstraint> cons = hash.get(dataTuple1);
/* 1200 */           cons.add(c);
/* 1201 */           hash.put(dataTuple1, cons);
/*      */         } 
/* 1203 */         if (hash.containsKey(dataTuple2)) {
/* 1204 */           ArrayList<ILevelConstraint> cons = hash.get(dataTuple2);
/* 1205 */           cons.add(c);
/* 1206 */           hash.put(dataTuple2, cons);
/*      */         } 
/* 1208 */         if (!d.contains(dataTuple1) || !d.contains(dataTuple2))
/* 1209 */           Cviol++; 
/* 1210 */         if (dataTuple1.equals(null) && dataTuple2.equals(null))
/* 1211 */           System.out.println("ML should have been removed"); 
/*      */         continue;
/*      */       } 
/* 1214 */       CL.add(c);
/* 1215 */       DataTuple t1 = c.getT1();
/* 1216 */       DataTuple t2 = c.getT2();
/* 1217 */       if (hash.containsKey(t1)) {
/* 1218 */         ArrayList<ILevelConstraint> cons = hash.get(t1);
/* 1219 */         cons.add(c);
/* 1220 */         hash.put(t1, cons);
/*      */       } 
/* 1222 */       if (hash.containsKey(t2)) {
/* 1223 */         ArrayList<ILevelConstraint> cons = hash.get(t2);
/* 1224 */         cons.add(c);
/* 1225 */         hash.put(t2, cons);
/*      */       } 
/* 1227 */       if (d.contains(t1) && d.contains(t2)) {
/* 1228 */         Cviol++; continue;
/* 1229 */       }  if (!d.contains(t1) && !d.contains(t2)) {
/* 1230 */         System.out.println("CL should have been removed");
/*      */       }
/*      */     } 
/* 1233 */     ArrayList<ILevelConstraint> ML_I = (ArrayList<ILevelConstraint>)ML.clone();
/* 1234 */     ArrayList<ILevelConstraint> CL_I = (ArrayList<ILevelConstraint>)CL.clone();
/* 1235 */     Hashtable<DataTuple, ArrayList<ILevelConstraint>> hash_I = (Hashtable<DataTuple, ArrayList<ILevelConstraint>>)hash.clone();
/* 1236 */     int Dtot = size;
/* 1237 */     int Dcov = data.getNbRows();
/* 1238 */     int Ctot = constraints.size();
/* 1239 */     int Dcov_I = Dcov;
/* 1240 */     int Cviol_I = Cviol;
/* 1241 */     int Ctot_I = Ctot;
/* 1242 */     findNumericNormal(at, data, s, idx, Cviol, hash, ML, CL, Dtot, Dcov, Ctot);
/* 1243 */     findNumericInverse(at, data, s_I, idx, Cviol_I, hash_I, ML_I, CL_I, Dtot, Dcov_I, Ctot_I);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void findNumericInverse(NumericAttrType at, RowData data, RegressionStat s, int idx, int Cviol, Hashtable<DataTuple, ArrayList<ILevelConstraint>> hash, ArrayList<ILevelConstraint> ML, ArrayList<ILevelConstraint> CL, int Dtot, int Dcov, int Ctot) {
/* 1252 */     double prev = Double.NaN;
/*      */     
/* 1254 */     for (int i = data.getNbRows() - 1; i >= 0; i--) {
/*      */       
/* 1256 */       DataTuple tuple = data.getTuple(i);
/* 1257 */       double value = tuple.getDoubleVal(idx);
/* 1258 */       if (value != prev && prev > Double.NEGATIVE_INFINITY && Dcov > 1) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1265 */         double heuristic = computeHeuristic(Dtot, Dcov, Ctot, Cviol, s, ML, CL, hash);
/* 1266 */         if (heuristic > this.m_BestHeur) {
/* 1267 */           this.m_BestHeur = heuristic;
/* 1268 */           double splitpoint = (value + prev) / 2.0D;
/* 1269 */           this.m_BestTest = new ClusRuleConstraintInduceTest((ClusAttrType)at, splitpoint, false);
/* 1270 */           this.m_BestConstraints = (ArrayList<ILevelConstraint>)ML.clone();
/* 1271 */           this.m_BestConstraints.addAll((ArrayList)CL.clone());
/* 1272 */           Iterator<ILevelConstraint> iterator = this.m_BestConstraints.iterator();
/*      */         } 
/*      */       } 
/* 1275 */       prev = value;
/*      */       
/* 1277 */       s.updateWeighted(tuple, -1.0D * tuple.getWeight());
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1292 */       Iterator<ILevelConstraint> m = ((ArrayList<ILevelConstraint>)hash.get(tuple)).iterator();
/* 1293 */       while (m.hasNext()) {
/* 1294 */         DataTuple toCheck; ILevelConstraint ilc = m.next();
/*      */         
/* 1296 */         if (tuple.getIndex() == ilc.getT1().getIndex()) {
/* 1297 */           toCheck = ilc.getT2();
/*      */         } else {
/* 1299 */           toCheck = ilc.getT1();
/*      */         } 
/* 1301 */         if (ilc.getType() == 0) {
/* 1302 */           if (hash.containsKey(toCheck)) {
/* 1303 */             Cviol++; continue;
/*      */           } 
/* 1305 */           ML.remove(ilc);
/* 1306 */           Cviol--;
/* 1307 */           Ctot--;
/*      */           continue;
/*      */         } 
/* 1310 */         if (hash.containsKey(toCheck)) {
/* 1311 */           Cviol--; continue;
/*      */         } 
/* 1313 */         CL.remove(ilc);
/* 1314 */         Ctot--;
/*      */       } 
/*      */ 
/*      */       
/* 1318 */       hash.remove(tuple);
/* 1319 */       Dcov--;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void findNumericNormal(NumericAttrType at, RowData data, RegressionStat s, int idx, int Cviol, Hashtable<DataTuple, ArrayList<ILevelConstraint>> hash, ArrayList<ILevelConstraint> ML, ArrayList<ILevelConstraint> CL, int Dtot, int Dcov, int Ctot) {
/* 1329 */     double prev = Double.NaN;
/*      */     
/* 1331 */     for (int i = 0; i < data.getNbRows(); i++) {
/*      */       
/* 1333 */       DataTuple tuple = data.getTuple(i);
/* 1334 */       double value = tuple.getDoubleVal(idx);
/* 1335 */       if (value != prev && prev > Double.NEGATIVE_INFINITY && Dcov > 1) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1342 */         double heuristic = computeHeuristic(Dtot, Dcov, Ctot, Cviol, s, ML, CL, hash);
/* 1343 */         if (heuristic > this.m_BestHeur) {
/* 1344 */           this.m_BestHeur = heuristic;
/* 1345 */           double splitpoint = (value + prev) / 2.0D;
/* 1346 */           this.m_BestTest = new ClusRuleConstraintInduceTest((ClusAttrType)at, splitpoint, true);
/* 1347 */           this.m_BestConstraints = (ArrayList<ILevelConstraint>)ML.clone();
/* 1348 */           this.m_BestConstraints.addAll((ArrayList)CL.clone());
/*      */         } 
/*      */       } 
/* 1351 */       prev = value;
/*      */       
/* 1353 */       s.updateWeighted(tuple, -1.0D * tuple.getWeight());
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1368 */       Iterator<ILevelConstraint> m = ((ArrayList<ILevelConstraint>)hash.get(tuple)).iterator();
/* 1369 */       while (m.hasNext()) {
/* 1370 */         DataTuple toCheck; ILevelConstraint ilc = m.next();
/*      */         
/* 1372 */         if (tuple.getIndex() == ilc.getT1().getIndex()) {
/* 1373 */           toCheck = ilc.getT2();
/*      */         } else {
/* 1375 */           toCheck = ilc.getT1();
/*      */         } 
/* 1377 */         if (ilc.getType() == 0) {
/* 1378 */           if (hash.containsKey(toCheck)) {
/* 1379 */             Cviol++; continue;
/*      */           } 
/* 1381 */           ML.remove(ilc);
/* 1382 */           Cviol--;
/* 1383 */           Ctot--;
/*      */           continue;
/*      */         } 
/* 1386 */         if (hash.containsKey(toCheck)) {
/* 1387 */           Cviol--; continue;
/*      */         } 
/* 1389 */         CL.remove(ilc);
/* 1390 */         Ctot--;
/*      */       } 
/*      */ 
/*      */       
/* 1394 */       hash.remove(tuple);
/* 1395 */       Dcov--;
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private double computeHeuristic(double dtot, double dcov, double ctot, double cviol, RegressionStat s, ArrayList<ILevelConstraint> ml, ArrayList<ILevelConstraint> cl, Hashtable<DataTuple, ArrayList<ILevelConstraint>> hash) {
/* 1401 */     double con, var = s.getSVarS((ClusAttributeWeights)this.m_Scale) / this.m_Global_Var;
/*      */     
/* 1403 */     if (ctot == 0.0D) {
/* 1404 */       con = 0.0D;
/*      */     } else {
/* 1406 */       con = cviol / ctot;
/*      */     } 
/* 1408 */     double data = dcov / dtot;
/* 1409 */     double g = this.m_Gamma;
/* 1410 */     double a = this.m_Alfa;
/* 1411 */     double h = (1.0D - (1.0D - g) * var + g * con) * Math.pow(data, a);
/* 1412 */     return h;
/*      */   }
/*      */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\algo\rules\ClusRuleConstraintInduce.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */