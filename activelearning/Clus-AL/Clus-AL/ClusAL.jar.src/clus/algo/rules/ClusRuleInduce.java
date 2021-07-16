/*      */ package clus.algo.rules;
/*      */ 
/*      */ import clus.algo.ClusInductionAlgorithm;
/*      */ import clus.algo.split.CurrentBestTestAndHeuristic;
/*      */ import clus.data.rows.RowData;
/*      */ import clus.data.type.ClusAttrType;
/*      */ import clus.data.type.ClusSchema;
/*      */ import clus.data.type.NominalAttrType;
/*      */ import clus.data.type.NumericAttrType;
/*      */ import clus.ext.beamsearch.ClusBeam;
/*      */ import clus.ext.beamsearch.ClusBeamModel;
/*      */ import clus.heuristic.ClusHeuristic;
/*      */ import clus.main.ClusRun;
/*      */ import clus.main.ClusStatManager;
/*      */ import clus.main.Settings;
/*      */ import clus.model.ClusModel;
/*      */ import clus.model.ClusModelInfo;
/*      */ import clus.model.test.NodeTest;
/*      */ import clus.selection.BaggingSelection;
/*      */ import clus.selection.ClusSelection;
/*      */ import clus.statistic.ClusStatistic;
/*      */ import clus.tools.optimization.CallExternGD;
/*      */ import clus.tools.optimization.GDAlg;
/*      */ import clus.tools.optimization.OptAlg;
/*      */ import clus.tools.optimization.OptProbl;
/*      */ import clus.tools.optimization.de.DeAlg;
/*      */ import clus.util.ClusException;
/*      */ import java.io.IOException;
/*      */ import java.io.PrintWriter;
/*      */ import java.util.ArrayList;
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
/*      */ public class ClusRuleInduce
/*      */   extends ClusInductionAlgorithm
/*      */ {
/*      */   protected boolean m_BeamChanged;
/*      */   protected FindBestTestRules m_FindBestTest;
/*      */   protected ClusHeuristic m_Heuristic;
/*      */   
/*      */   public ClusRuleInduce(ClusSchema schema, Settings sett) throws ClusException, IOException {
/*   60 */     super(schema, sett);
/*   61 */     this.m_FindBestTest = new FindBestTestRules(getStatManager());
/*      */   }
/*      */   
/*      */   void resetAll() {
/*   65 */     this.m_BeamChanged = false;
/*      */   }
/*      */   
/*      */   public void setHeuristic(ClusHeuristic heur) {
/*   69 */     this.m_Heuristic = heur;
/*      */   }
/*      */   
/*      */   public double estimateBeamMeasure(ClusRule rule) {
/*   73 */     return this.m_Heuristic.calcHeuristic(null, rule.getClusteringStat(), null);
/*      */   }
/*      */   
/*      */   public boolean isBeamChanged() {
/*   77 */     return this.m_BeamChanged;
/*      */   }
/*      */   
/*      */   public void setBeamChanged(boolean change) {
/*   81 */     this.m_BeamChanged = change;
/*      */   }
/*      */   
/*      */   ClusBeam initializeBeam(RowData data) {
/*   85 */     Settings sett = getSettings();
/*   86 */     ClusBeam beam = new ClusBeam(sett.getBeamWidth(), sett.getBeamRemoveEqualHeur());
/*   87 */     ClusStatistic stat = createTotalClusteringStat(data);
/*   88 */     ClusRule rule = new ClusRule(getStatManager());
/*   89 */     rule.setClusteringStat(stat);
/*   90 */     rule.setVisitor(data);
/*   91 */     double value = estimateBeamMeasure(rule);
/*   92 */     beam.addModel(new ClusBeamModel(value, rule));
/*   93 */     return beam;
/*      */   }
/*      */   
/*      */   public void refineModel(ClusBeamModel model, ClusBeam beam, int model_idx) {
/*   97 */     ClusRule rule = (ClusRule)model.getModel();
/*   98 */     RowData data = (RowData)rule.getVisitor();
/*   99 */     if (this.m_FindBestTest.initSelectorAndStopCrit(rule.getClusteringStat(), data)) {
/*  100 */       model.setFinished(true);
/*      */       return;
/*      */     } 
/*  103 */     CurrentBestTestAndHeuristic sel = this.m_FindBestTest.getBestTest();
/*  104 */     ClusAttrType[] attrs = data.getSchema().getDescriptiveAttributes();
/*  105 */     for (int i = 0; i < attrs.length; i++) {
/*      */       
/*  107 */       sel.resetBestTest();
/*  108 */       double beam_min_value = beam.getMinValue();
/*  109 */       sel.setBestHeur(beam_min_value);
/*  110 */       ClusAttrType at = attrs[i];
/*  111 */       if (at instanceof NominalAttrType) { this.m_FindBestTest.findNominal((NominalAttrType)at, data); }
/*  112 */       else { this.m_FindBestTest.findNumeric((NumericAttrType)at, data); }
/*  113 */        if (sel.hasBestTest()) {
/*  114 */         double new_heur; NodeTest test = sel.updateTest();
/*  115 */         if (Settings.VERBOSE > 0) System.out.println("  Test: " + test.getString() + " -> " + sel.m_BestHeur);
/*      */         
/*  117 */         RowData subset = data.apply(test, 0);
/*  118 */         ClusRule ref_rule = rule.cloneRule();
/*  119 */         ref_rule.addTest(test);
/*  120 */         ref_rule.setVisitor(subset);
/*  121 */         ref_rule.setClusteringStat(createTotalClusteringStat(subset));
/*      */         
/*  123 */         if (getSettings().isHeurRuleDist()) {
/*  124 */           int[] subset_idx = new int[subset.getNbRows()];
/*  125 */           for (int j = 0; j < subset_idx.length; j++) {
/*  126 */             subset_idx[j] = subset.getTuple(j).getIndex();
/*      */           }
/*  128 */           ((ClusRuleHeuristicDispersion)this.m_Heuristic).setDataIndexes(subset_idx);
/*      */         } 
/*      */ 
/*      */         
/*  132 */         if (getSettings().isTimeSeriesProtoComlexityExact()) {
/*  133 */           new_heur = sanityCheck(sel.m_BestHeur, ref_rule);
/*      */         } else {
/*      */           
/*  136 */           new_heur = sel.m_BestHeur;
/*      */         } 
/*      */         
/*  139 */         if (new_heur > beam_min_value) {
/*  140 */           ClusBeamModel new_model = new ClusBeamModel(new_heur, ref_rule);
/*  141 */           new_model.setParentModelIndex(model_idx);
/*  142 */           beam.addModel(new_model);
/*  143 */           setBeamChanged(true);
/*      */         } 
/*      */       } 
/*      */     } 
/*      */   }
/*      */   
/*      */   public void refineBeam(ClusBeam beam) {
/*  150 */     setBeamChanged(false);
/*  151 */     ArrayList<ClusBeamModel> models = beam.toArray();
/*  152 */     for (int i = 0; i < models.size(); i++) {
/*  153 */       ClusBeamModel model = models.get(i);
/*  154 */       if (!model.isRefined() && !model.isFinished()) {
/*      */         
/*  156 */         refineModel(model, beam, i);
/*  157 */         model.setRefined(true);
/*  158 */         model.setParentModelIndex(-1);
/*      */       } 
/*      */     } 
/*      */   }
/*      */   
/*      */   public ClusRule learnOneRule(RowData data) {
/*  164 */     ClusBeam beam = initializeBeam(data);
/*  165 */     int i = 0;
/*  166 */     System.out.print("Step: ");
/*      */     while (true) {
/*  168 */       if (Settings.VERBOSE > 0) {
/*  169 */         System.out.println("Step: " + i);
/*      */       } else {
/*  171 */         if (i != 0) {
/*  172 */           System.out.print(",");
/*      */         }
/*  174 */         System.out.print(i);
/*      */       } 
/*  176 */       System.out.flush();
/*  177 */       refineBeam(beam);
/*  178 */       if (!isBeamChanged()) {
/*      */         break;
/*      */       }
/*  181 */       i++;
/*      */     } 
/*  183 */     System.out.println();
/*  184 */     double best = beam.getBestModel().getValue();
/*  185 */     double worst = beam.getWorstModel().getValue();
/*  186 */     System.out.println("Worst = " + worst + " Best = " + best);
/*  187 */     ClusRule result = (ClusRule)beam.getBestAndSmallestModel().getModel();
/*      */     
/*  189 */     RowData rule_data = (RowData)result.getVisitor();
/*  190 */     result.setTargetStat(createTotalTargetStat(rule_data));
/*  191 */     result.setVisitor(null);
/*  192 */     return result;
/*      */   }
/*      */   
/*      */   public ClusRule learnEmptyRule(RowData data) {
/*  196 */     ClusRule result = new ClusRule(getStatManager());
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  201 */     return result;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ClusRule[] learnBeamOfRules(RowData data) {
/*  210 */     ClusBeam beam = initializeBeam(data);
/*  211 */     int i = 0;
/*  212 */     System.out.print("Step: ");
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
/*  224 */       if (!isBeamChanged()) {
/*      */         break;
/*      */       }
/*  227 */       i++;
/*      */     } 
/*  229 */     System.out.println();
/*  230 */     double best = beam.getBestModel().getValue();
/*  231 */     double worst = beam.getWorstModel().getValue();
/*  232 */     System.out.println("Worst = " + worst + " Best = " + best);
/*  233 */     ArrayList<ClusBeamModel> beam_models = beam.toArray();
/*  234 */     ClusRule[] result = new ClusRule[beam_models.size()];
/*  235 */     for (int j = 0; j < beam_models.size(); j++) {
/*      */       
/*  237 */       int k = beam_models.size() - j - 1;
/*  238 */       ClusRule rule = (ClusRule)((ClusBeamModel)beam_models.get(k)).getModel();
/*      */       
/*  240 */       RowData rule_data = (RowData)rule.getVisitor();
/*  241 */       rule.setTargetStat(createTotalTargetStat(rule_data));
/*  242 */       rule.setVisitor(null);
/*  243 */       rule.simplify();
/*  244 */       result[j] = rule;
/*      */     } 
/*  246 */     return result;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void separateAndConquor(ClusRuleSet rset, RowData data) {
/*      */     ClusStatistic left_over;
/*  254 */     int max_rules = getSettings().getMaxRulesNb();
/*  255 */     int i = 0;
/*      */     
/*  257 */     while (data.getNbRows() > 0 && i < max_rules) {
/*  258 */       ClusRule rule = learnOneRule(data);
/*  259 */       if (rule.isEmpty()) {
/*      */         break;
/*      */       }
/*  262 */       rule.computePrediction();
/*  263 */       rule.printModel();
/*  264 */       System.out.println();
/*  265 */       rset.add(rule);
/*  266 */       data = rule.removeCovered(data);
/*  267 */       i++;
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  273 */     if (data.getNbRows() > 0) {
/*  274 */       left_over = createTotalTargetStat(data);
/*  275 */       left_over.calcMean();
/*      */     } else {
/*  277 */       System.out.println("All training examples covered - default rule on entire training set!");
/*  278 */       rset.m_Comment = new String(" (on entire training set)");
/*  279 */       left_over = getStatManager().getTrainSetStat(3).cloneStat();
/*  280 */       left_over.copy(getStatManager().getTrainSetStat(3));
/*  281 */       left_over.calcMean();
/*      */       
/*  283 */       System.err.println(left_over.toString());
/*      */     } 
/*  285 */     System.out.println("Left Over: " + left_over);
/*  286 */     rset.setTargetStat(left_over);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void separateAndConquorWeighted(ClusRuleSet rset, RowData data) throws ClusException {
/*      */     ClusStatistic left_over;
/*  296 */     int max_rules = getSettings().getMaxRulesNb();
/*  297 */     int i = 0;
/*  298 */     RowData data_copy = (RowData)data.deepCloneData();
/*  299 */     ArrayList<boolean[]> bit_vect_array = new ArrayList();
/*      */     
/*  301 */     while (data.getNbRows() > 0 && i < max_rules) {
/*  302 */       ClusRule rule = learnOneRule(data);
/*  303 */       if (rule.isEmpty()) {
/*      */         break;
/*      */       }
/*  306 */       rule.computePrediction();
/*  307 */       if (Settings.isPrintAllRules())
/*  308 */         rule.printModel(); 
/*  309 */       System.out.println();
/*  310 */       rset.add(rule);
/*  311 */       data = rule.reweighCovered(data);
/*  312 */       i++;
/*  313 */       if (getSettings().isHeurRuleDist()) {
/*  314 */         boolean[] bit_vect = new boolean[data_copy.getNbRows()];
/*  315 */         for (int j = 0; j < bit_vect.length; j++) {
/*  316 */           if (!bit_vect[j]) {
/*  317 */             for (int k = 0; k < rset.getModelSize(); k++) {
/*  318 */               if (rset.getRule(k).covers(data_copy.getTuple(j))) {
/*  319 */                 bit_vect[j] = true;
/*      */                 break;
/*      */               } 
/*      */             } 
/*      */           }
/*      */         } 
/*  325 */         bit_vect_array.add(bit_vect);
/*  326 */         ((ClusRuleHeuristicDispersion)this.m_Heuristic).setCoveredBitVectArray(bit_vect_array);
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  333 */     if (data.getNbRows() > 0) {
/*  334 */       left_over = createTotalTargetStat(data);
/*  335 */       left_over.calcMean();
/*      */     } else {
/*  337 */       System.out.println("All training examples covered - default rule on entire training set!");
/*  338 */       rset.m_Comment = new String(" (on entire training set)");
/*  339 */       left_over = getStatManager().getTrainSetStat(3).cloneStat();
/*  340 */       left_over.copy(getStatManager().getTrainSetStat(3));
/*  341 */       left_over.calcMean();
/*      */       
/*  343 */       System.err.println(left_over.toString());
/*      */     } 
/*  345 */     System.out.println("Left Over: " + left_over);
/*  346 */     rset.setTargetStat(left_over);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void separateAndConquorAddRulesIfBetter(ClusRuleSet rset, RowData data) throws ClusException {
/*  355 */     int max_rules = getSettings().getMaxRulesNb();
/*  356 */     int i = 0;
/*  357 */     RowData data_copy = (RowData)data.deepCloneData();
/*  358 */     ArrayList<boolean[]> bit_vect_array = new ArrayList();
/*  359 */     ClusStatistic left_over = createTotalTargetStat(data_copy);
/*  360 */     left_over.calcMean();
/*  361 */     ClusStatistic new_left_over = left_over;
/*  362 */     rset.setTargetStat(left_over);
/*  363 */     double err_score = rset.computeErrorScore(data_copy);
/*  364 */     while (data.getNbRows() > 0 && i < max_rules) {
/*  365 */       ClusRule rule = learnOneRule(data);
/*  366 */       if (rule.isEmpty()) {
/*      */         break;
/*      */       }
/*  369 */       rule.computePrediction();
/*  370 */       ClusRuleSet new_rset = rset.cloneRuleSet();
/*  371 */       new_rset.add(rule);
/*  372 */       data = rule.reweighCovered(data);
/*  373 */       left_over = new_left_over;
/*  374 */       new_left_over = createTotalTargetStat(data);
/*  375 */       new_left_over.calcMean();
/*  376 */       new_rset.setTargetStat(new_left_over);
/*  377 */       double new_err_score = new_rset.computeErrorScore(data_copy);
/*  378 */       if (err_score - new_err_score > 1.0E-6D) {
/*  379 */         i++;
/*  380 */         rule.printModel();
/*  381 */         System.out.println();
/*  382 */         err_score = new_err_score;
/*  383 */         rset.add(rule);
/*  384 */         if (getSettings().isHeurRuleDist()) {
/*  385 */           boolean[] bit_vect = new boolean[data_copy.getNbRows()];
/*  386 */           for (int j = 0; j < bit_vect.length; j++) {
/*  387 */             if (!bit_vect[j]) {
/*  388 */               for (int k = 0; k < rset.getModelSize(); k++) {
/*  389 */                 if (rset.getRule(k).covers(data_copy.getTuple(j))) {
/*  390 */                   bit_vect[j] = true;
/*      */                   break;
/*      */                 } 
/*      */               } 
/*      */             }
/*      */           } 
/*  396 */           bit_vect_array.add(bit_vect);
/*  397 */           ((ClusRuleHeuristicDispersion)this.m_Heuristic).setCoveredBitVectArray(bit_vect_array);
/*      */         } 
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/*  404 */     System.out.println("Left Over: " + left_over);
/*  405 */     rset.setTargetStat(left_over);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void separateAndConquorAddRulesIfBetterFromBeam(ClusRuleSet rset, RowData data) throws ClusException {
/*  415 */     int max_rules = getSettings().getMaxRulesNb();
/*  416 */     int i = 0;
/*  417 */     RowData data_copy = (RowData)data.deepCloneData();
/*  418 */     ArrayList<boolean[]> bit_vect_array = new ArrayList();
/*  419 */     ClusStatistic left_over = createTotalTargetStat(data);
/*  420 */     ClusStatistic new_left_over = left_over;
/*  421 */     left_over.calcMean();
/*  422 */     rset.setTargetStat(left_over);
/*  423 */     int nb_tar = left_over.getNbAttributes();
/*  424 */     boolean cls_task = false;
/*  425 */     if (left_over instanceof clus.statistic.ClassificationStat) {
/*  426 */       cls_task = true;
/*      */     }
/*  428 */     int[] def_maj_class = new int[nb_tar];
/*  429 */     if (cls_task) {
/*  430 */       for (int t = 0; t < nb_tar; t++) {
/*  431 */         def_maj_class[t] = left_over.getNominalPred()[t];
/*      */       }
/*      */     }
/*  434 */     double err_score = rset.computeErrorScore(data);
/*  435 */     while (data_copy.getNbRows() > 0 && i < max_rules) {
/*  436 */       ClusRule[] rules = learnBeamOfRules(data_copy);
/*  437 */       left_over = new_left_over;
/*  438 */       int rule_added = -1;
/*      */       
/*  440 */       for (int j = 0; j < rules.length; j++) {
/*  441 */         if (!rules[j].isEmpty()) {
/*      */ 
/*      */           
/*  444 */           rules[j].computePrediction();
/*  445 */           ClusRuleSet new_rset = rset.cloneRuleSet();
/*  446 */           new_rset.add(rules[j]);
/*  447 */           RowData data_copy2 = (RowData)data_copy.deepCloneData();
/*  448 */           data_copy2 = rules[j].reweighCovered(data_copy2);
/*  449 */           ClusStatistic new_left_over2 = createTotalTargetStat(data_copy2);
/*  450 */           new_left_over2.calcMean();
/*  451 */           new_rset.setTargetStat(new_left_over2);
/*  452 */           double new_err_score = new_rset.computeErrorScore(data);
/*      */           
/*  454 */           boolean add_anyway = false;
/*  455 */           if (cls_task) {
/*  456 */             for (int t = 0; t < nb_tar; t++) {
/*  457 */               if (def_maj_class[t] == rules[j].getTargetStat().getNominalPred()[t]) {
/*  458 */                 add_anyway = true;
/*      */               }
/*      */             } 
/*      */           }
/*  462 */           if (err_score - new_err_score > 1.0E-6D || add_anyway) {
/*  463 */             err_score = new_err_score;
/*  464 */             rule_added = j;
/*  465 */             data_copy = data_copy2;
/*  466 */             new_left_over = new_left_over2;
/*      */           } 
/*      */         } 
/*      */       } 
/*  470 */       if (rule_added != -1) {
/*  471 */         i++;
/*  472 */         rules[rule_added].printModel();
/*  473 */         System.out.println();
/*  474 */         rset.add(rules[rule_added]);
/*  475 */         if (getSettings().isHeurRuleDist()) {
/*  476 */           boolean[] bit_vect = new boolean[data.getNbRows()];
/*  477 */           for (int k = 0; k < bit_vect.length; k++) {
/*  478 */             if (!bit_vect[k]) {
/*  479 */               for (int m = 0; m < rset.getModelSize(); m++) {
/*  480 */                 if (rset.getRule(m).covers(data.getTuple(k))) {
/*  481 */                   bit_vect[k] = true;
/*      */                   break;
/*      */                 } 
/*      */               } 
/*      */             }
/*      */           } 
/*  487 */           bit_vect_array.add(bit_vect);
/*  488 */           ((ClusRuleHeuristicDispersion)this.m_Heuristic).setCoveredBitVectArray(bit_vect_array);
/*      */         } 
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/*  494 */     System.out.println("Left Over: " + left_over);
/*  495 */     rset.setTargetStat(left_over);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void separateAndConquorBootstraped(ClusRuleSet rset, RowData data) throws ClusException {
/*  505 */     int nb_sets = 10;
/*  506 */     int nb_rows = data.getNbRows();
/*  507 */     int max_rules = getSettings().getMaxRulesNb();
/*  508 */     max_rules /= nb_sets;
/*  509 */     RowData data_not_covered = (RowData)data.cloneData();
/*  510 */     for (int z = 0; z < nb_sets; z++) {
/*      */       
/*  512 */       RowData data_sel = (RowData)data.cloneData();
/*  513 */       BaggingSelection msel = new BaggingSelection(nb_rows, getSettings().getEnsembleBagSize());
/*  514 */       data_sel.update((ClusSelection)msel);
/*      */       
/*  516 */       if (getSettings().isHeurRuleDist()) {
/*  517 */         int[] data_idx = new int[data_sel.getNbRows()];
/*  518 */         for (int j = 0; j < data_sel.getNbRows(); j++) {
/*  519 */           data_sel.getTuple(j).setIndex(j);
/*  520 */           data_idx[j] = j;
/*      */         } 
/*  522 */         ((ClusRuleHeuristicDispersion)this.m_Heuristic).setDataIndexes(data_idx);
/*  523 */         ((ClusRuleHeuristicDispersion)this.m_Heuristic).initCoveredBitVectArray(data_sel.getNbRows());
/*      */       } 
/*      */       
/*  526 */       int i = 0;
/*  527 */       RowData data_sel_copy = (RowData)data_sel.cloneData();
/*  528 */       ArrayList<boolean[]> bit_vect_array = new ArrayList();
/*  529 */       while (data_sel.getNbRows() > 0 && i < max_rules) {
/*  530 */         ClusRule rule = learnOneRule(data_sel);
/*  531 */         if (rule.isEmpty()) {
/*      */           break;
/*      */         }
/*  534 */         rule.computePrediction();
/*  535 */         rule.printModel();
/*  536 */         System.out.println();
/*  537 */         rset.addIfUnique(rule);
/*  538 */         data_sel = rule.removeCovered(data_sel);
/*  539 */         data_not_covered = rule.removeCovered(data_not_covered);
/*  540 */         i++;
/*  541 */         if (getSettings().isHeurRuleDist()) {
/*  542 */           boolean[] bit_vect = new boolean[data_sel_copy.getNbRows()];
/*  543 */           for (int j = 0; j < bit_vect.length; j++) {
/*  544 */             if (!bit_vect[j]) {
/*  545 */               for (int k = 0; k < rset.getModelSize(); k++) {
/*  546 */                 if (rset.getRule(k).covers(data_sel_copy.getTuple(j))) {
/*  547 */                   bit_vect[j] = true;
/*      */                   break;
/*      */                 } 
/*      */               } 
/*      */             }
/*      */           } 
/*  553 */           bit_vect_array.add(bit_vect);
/*  554 */           ((ClusRuleHeuristicDispersion)this.m_Heuristic).setCoveredBitVectArray(bit_vect_array);
/*      */         } 
/*      */       } 
/*      */     } 
/*      */     
/*  559 */     ClusStatistic left_over = createTotalTargetStat(data_not_covered);
/*  560 */     left_over.calcMean();
/*  561 */     System.out.println("Left Over: " + left_over);
/*  562 */     rset.setTargetStat(left_over);
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
/*      */   public void separateAndConquorRandomly(ClusRuleSet rset, RowData data) throws ClusException {
/*  575 */     int nb_rules = 100;
/*  576 */     int max_def_rules = 10;
/*  577 */     ClusRule[] rules = new ClusRule[nb_rules];
/*  578 */     Random rn = new Random(42L);
/*  579 */     for (int k = 0; k < nb_rules; k++) {
/*  580 */       ClusRule rule = generateOneRandomRule(data, rn);
/*  581 */       rule.computePrediction();
/*  582 */       rules[k] = rule;
/*      */     } 
/*  584 */     int max_rules = getSettings().getMaxRulesNb();
/*  585 */     int i = 0;
/*  586 */     RowData data_copy = (RowData)data.deepCloneData();
/*  587 */     ClusStatistic left_over = createTotalTargetStat(data);
/*  588 */     ClusStatistic new_left_over = left_over;
/*  589 */     left_over.calcMean();
/*  590 */     rset.setTargetStat(left_over);
/*  591 */     int nb_tar = left_over.getNbAttributes();
/*  592 */     boolean cls_task = false;
/*  593 */     if (left_over instanceof clus.statistic.ClassificationStat) {
/*  594 */       cls_task = true;
/*      */     }
/*  596 */     int[] def_maj_class = new int[nb_tar];
/*  597 */     if (cls_task) {
/*  598 */       for (int t = 0; t < nb_tar; t++) {
/*  599 */         def_maj_class[t] = left_over.getNominalPred()[t];
/*      */       }
/*      */     }
/*  602 */     double err_score = rset.computeErrorScore(data);
/*  603 */     int nb_def_rules = 0;
/*  604 */     boolean add_anyway = false;
/*  605 */     while (i < max_rules) {
/*  606 */       left_over = new_left_over;
/*  607 */       int rule_added = -1;
/*      */       
/*  609 */       for (int j = 0; j < rules.length; j++) {
/*  610 */         if (rules[j] != null && !rules[j].isEmpty()) {
/*      */ 
/*      */           
/*  613 */           rules[j].computePrediction();
/*  614 */           ClusRuleSet new_rset = rset.cloneRuleSet();
/*  615 */           new_rset.add(rules[j]);
/*  616 */           RowData data_copy2 = (RowData)data_copy.deepCloneData();
/*  617 */           data_copy2 = rules[j].reweighCovered(data_copy2);
/*  618 */           ClusStatistic new_left_over2 = createTotalTargetStat(data_copy2);
/*  619 */           new_left_over2.calcMean();
/*  620 */           new_rset.setTargetStat(new_left_over2);
/*  621 */           double new_err_score = new_rset.computeErrorScore(data);
/*      */           
/*  623 */           add_anyway = false;
/*  624 */           if (cls_task) {
/*  625 */             for (int t = 0; t < nb_tar; t++) {
/*  626 */               if (def_maj_class[t] == rules[j].getTargetStat().getNominalPred()[t]) {
/*  627 */                 add_anyway = true;
/*      */               }
/*      */             } 
/*      */           }
/*  631 */           double err_d = err_score - new_err_score;
/*  632 */           if (err_d > 1.0E-6D || nb_def_rules < max_def_rules) {
/*  633 */             if (add_anyway) {
/*  634 */               nb_def_rules++;
/*      */             }
/*  636 */             err_score = new_err_score;
/*  637 */             rule_added = j;
/*      */             
/*  639 */             data_copy = data_copy2;
/*  640 */             new_left_over = new_left_over2;
/*      */           } 
/*      */         } 
/*      */       } 
/*  644 */       if (rule_added != -1) {
/*  645 */         i++;
/*  646 */         rules[rule_added].printModel();
/*  647 */         System.out.println();
/*  648 */         rset.addIfUnique(rules[rule_added]);
/*  649 */         rules[rule_added] = null;
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/*  654 */     System.out.println("Left Over: " + left_over);
/*  655 */     rset.setTargetStat(left_over);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void separateAndConquorAddRulesIfBetterFromBeam2(ClusRuleSet rset, RowData data) throws ClusException {
/*  665 */     int max_rules = getSettings().getMaxRulesNb();
/*  666 */     int i = 0;
/*  667 */     RowData data_copy = (RowData)data.deepCloneData();
/*  668 */     ClusStatistic left_over = createTotalTargetStat(data);
/*      */     
/*  670 */     left_over.calcMean();
/*  671 */     rset.setTargetStat(left_over);
/*  672 */     ClusRule empty_rule = learnEmptyRule(data_copy);
/*  673 */     empty_rule.setTargetStat(left_over);
/*  674 */     data_copy = empty_rule.reweighCovered(data_copy);
/*  675 */     double err_score = rset.computeErrorScore(data);
/*  676 */     while (data.getNbRows() > 0 && i < max_rules) {
/*  677 */       ClusRule[] rules = learnBeamOfRules(data_copy);
/*      */       
/*  679 */       int rule_added = -1;
/*      */       
/*  681 */       for (int j = 0; j < rules.length; j++) {
/*  682 */         if (!rules[j].isEmpty()) {
/*      */ 
/*      */           
/*  685 */           rules[j].computePrediction();
/*  686 */           ClusRuleSet new_rset = rset.cloneRuleSet();
/*  687 */           new_rset.add(rules[j]);
/*  688 */           RowData data_copy2 = (RowData)data_copy.deepCloneData();
/*  689 */           data_copy2 = rules[j].reweighCovered(data_copy2);
/*      */ 
/*      */ 
/*      */           
/*  693 */           new_rset.setTargetStat(left_over);
/*  694 */           double new_err_score = new_rset.computeErrorScore(data);
/*  695 */           if (err_score - new_err_score > 1.0E-6D) {
/*  696 */             err_score = new_err_score;
/*  697 */             rule_added = j;
/*  698 */             data_copy = data_copy2;
/*      */           } 
/*      */         } 
/*      */       } 
/*      */       
/*  703 */       if (rule_added != -1) {
/*  704 */         i++;
/*  705 */         rules[rule_added].printModel();
/*  706 */         System.out.println();
/*  707 */         rset.add(rules[rule_added]);
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/*  712 */     System.out.println("Left Over: " + left_over);
/*      */   }
/*      */ 
/*      */   
/*      */   public void separateAndConquorWithHeuristic(ClusRuleSet rset, RowData data) {
/*  717 */     int max_rules = getSettings().getMaxRulesNb();
/*  718 */     ArrayList<boolean[]> bit_vect_array = new ArrayList();
/*  719 */     int i = 0;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  734 */     while (i < max_rules) {
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  739 */       ClusRule[] rules = learnBeamOfRules(data);
/*  740 */       ClusRule rule = rules[0];
/*  741 */       for (int l = 0; l < rules.length - 1; l++) {
/*  742 */         rule = rules[l + 1];
/*  743 */         if (rset.unique(rule)) {
/*      */           break;
/*      */         }
/*      */       } 
/*  747 */       if (rule.isEmpty() || !rset.unique(rule)) {
/*      */         break;
/*      */       }
/*  750 */       rule.computePrediction();
/*  751 */       rule.printModel();
/*  752 */       System.out.println();
/*  753 */       rset.add(rule);
/*  754 */       i++;
/*  755 */       boolean[] bit_vect = new boolean[data.getNbRows()];
/*  756 */       for (int j = 0; j < bit_vect.length; j++) {
/*  757 */         if (!bit_vect[j]) {
/*  758 */           for (int k = 0; k < rset.getModelSize(); k++) {
/*  759 */             if (rset.getRule(k).covers(data.getTuple(j))) {
/*  760 */               bit_vect[j] = true;
/*      */               break;
/*      */             } 
/*      */           } 
/*      */         }
/*      */       } 
/*  766 */       bit_vect_array.add(bit_vect);
/*  767 */       ((ClusRuleHeuristicDispersion)this.m_Heuristic).setCoveredBitVectArray(bit_vect_array);
/*      */     } 
/*      */     
/*  770 */     updateDefaultRule(rset, data);
/*      */   }
/*      */   
/*      */   public double sanityCheck(double value, ClusRule rule) {
/*  774 */     double expected = estimateBeamMeasure(rule);
/*  775 */     if (Math.abs(value - expected) > 1.0E-6D) {
/*  776 */       System.out.println("Bug in heurisitc: " + value + " <> " + expected);
/*  777 */       PrintWriter wrt = new PrintWriter(System.out);
/*  778 */       rule.printModel(wrt);
/*  779 */       wrt.close();
/*  780 */       System.out.flush();
/*  781 */       System.exit(1);
/*      */     } 
/*  783 */     return expected;
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
/*      */   public ClusModel induce(ClusRun run) throws ClusException, IOException {
/*  795 */     int method = getSettings().getCoveringMethod();
/*  796 */     int add_method = getSettings().getRuleAddingMethod();
/*  797 */     RowData data = (RowData)run.getTrainingSet();
/*  798 */     ClusStatistic stat = createTotalClusteringStat(data);
/*  799 */     this.m_FindBestTest.initSelectorAndSplit(stat);
/*  800 */     setHeuristic(this.m_FindBestTest.getBestTest().getHeuristic());
/*  801 */     if (getSettings().isHeurRuleDist()) {
/*  802 */       int[] data_idx = new int[data.getNbRows()];
/*  803 */       for (int i = 0; i < data.getNbRows(); i++) {
/*  804 */         data.getTuple(i).setIndex(i);
/*  805 */         data_idx[i] = i;
/*      */       } 
/*  807 */       ((ClusRuleHeuristicDispersion)this.m_Heuristic).setDataIndexes(data_idx);
/*  808 */       ((ClusRuleHeuristicDispersion)this.m_Heuristic).initCoveredBitVectArray(data.getNbRows());
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/*  813 */     ClusRuleSet rset = new ClusRuleSet(getStatManager());
/*  814 */     if (method == 0) {
/*  815 */       separateAndConquor(rset, data);
/*  816 */     } else if (method == 5) {
/*  817 */       separateAndConquorAddRulesIfBetterFromBeam2(rset, data);
/*      */ 
/*      */     
/*      */     }
/*  821 */     else if (method == 6) {
/*  822 */       separateAndConquorRandomly(rset, data);
/*  823 */     } else if (method == 7) {
/*  824 */       separateAndConquorBootstraped(rset, data);
/*  825 */     } else if (method == 8) {
/*  826 */       separateAndConquorWithHeuristic(rset, data);
/*  827 */     } else if (add_method == 1) {
/*  828 */       separateAndConquorAddRulesIfBetter(rset, data);
/*  829 */     } else if (add_method == 2) {
/*  830 */       separateAndConquorAddRulesIfBetterFromBeam(rset, data);
/*      */     } else {
/*  832 */       separateAndConquorWeighted(rset, data);
/*      */     } 
/*      */ 
/*      */     
/*  836 */     rset.postProc();
/*      */ 
/*      */     
/*  839 */     if (getSettings().isRulePredictionOptimized()) {
/*  840 */       rset = optimizeRuleSet(rset, data);
/*      */     }
/*  842 */     rset.setTrainErrorScore();
/*  843 */     rset.addDataToRules(data);
/*      */     
/*  845 */     if (getSettings().computeDispersion()) {
/*  846 */       rset.computeDispersion(0);
/*  847 */       rset.removeDataFromRules();
/*  848 */       if (run.getTestIter() != null) {
/*  849 */         RowData testdata = run.getTestSet();
/*  850 */         rset.addDataToRules(testdata);
/*  851 */         rset.computeDispersion(1);
/*  852 */         rset.removeDataFromRules();
/*      */       } 
/*      */     } 
/*      */     
/*  856 */     rset.numberRules();
/*  857 */     return rset;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ClusRuleSet optimizeRuleSet(ClusRuleSet rset, RowData data) throws ClusException, IOException {
/*      */     DeAlg deAlg;
/*  869 */     String fname = getSettings().getDataFile();
/*      */     
/*  871 */     PrintWriter wrt_pred = null;
/*      */     
/*  873 */     OptAlg optAlg = null;
/*  874 */     OptProbl.OptParam param = rset.giveFormForWeightOptimization(wrt_pred, data);
/*  875 */     ArrayList<Double> weights = null;
/*      */     
/*  877 */     if (Settings.VERBOSE > 0) System.out.println("Preparing for optimization.");
/*      */ 
/*      */     
/*  880 */     if (getSettings().getRulePredictionMethod() == 8) {
/*  881 */       GDAlg gDAlg = new GDAlg(getStatManager(), param, rset);
/*  882 */     } else if (getSettings().getRulePredictionMethod() == 6) {
/*  883 */       deAlg = new DeAlg(getStatManager(), param, rset);
/*  884 */     } else if (getSettings().getRulePredictionMethod() == 9) {
/*  885 */       weights = CallExternGD.main(getStatManager(), param, rset);
/*      */     } 
/*      */     
/*  888 */     if (Settings.VERBOSE > 0 && getSettings().getRulePredictionMethod() != 9) System.out.println("Preparations ended. Starting optimization.");
/*      */     
/*  890 */     if (getSettings().getRulePredictionMethod() != 9)
/*      */     {
/*      */ 
/*      */       
/*  894 */       if (getSettings().getRulePredictionMethod() == 8 && 
/*  895 */         getSettings().getOptGDNbOfTParameterTry() > 1) {
/*      */ 
/*      */         
/*  898 */         GDAlg gdalg = (GDAlg)deAlg;
/*  899 */         double firstTVal = 1.0D;
/*  900 */         double lastTVal = getSettings().getOptGDGradTreshold();
/*      */ 
/*      */         
/*  903 */         double interTVal = (lastTVal - firstTVal) / (getSettings().getOptGDNbOfTParameterTry() - 1);
/*      */         
/*  905 */         double minFitness = Double.POSITIVE_INFINITY;
/*  906 */         for (int iRun = 0; iRun < getSettings().getOptGDNbOfTParameterTry(); iRun++) {
/*      */ 
/*      */           
/*  909 */           if (iRun == getSettings().getOptGDNbOfTParameterTry() - 1) {
/*  910 */             getSettings().setOptGDGradTreshold(lastTVal);
/*      */           } else {
/*  912 */             getSettings().setOptGDGradTreshold(firstTVal + iRun * interTVal);
/*      */           } 
/*      */           
/*  915 */           gdalg.initGDForNewRunWithSamePredictions();
/*      */           
/*  917 */           ArrayList<Double> newWeights = gdalg.optimize();
/*      */           
/*  919 */           if (Settings.VERBOSE > 0) {
/*  920 */             System.err.print("\nThe T value " + (firstTVal + iRun * interTVal) + " has a test fitness: " + gdalg
/*  921 */                 .getBestFitness());
/*      */           }
/*  923 */           if (gdalg.getBestFitness() < minFitness) {
/*      */             
/*  925 */             weights = newWeights;
/*  926 */             minFitness = gdalg.getBestFitness();
/*      */             
/*  928 */             rset.m_optWeightBestTValue = firstTVal + iRun * interTVal;
/*  929 */             rset.m_optWeightBestFitness = minFitness;
/*      */             
/*  931 */             if (Settings.VERBOSE > 0) {
/*  932 */               System.err.print(" - best so far!");
/*      */             }
/*      */           }
/*  935 */           else if (getSettings().getOptGDEarlyTTryStop() && gdalg
/*  936 */             .getBestFitness() > getSettings().getOptGDEarlyStopTreshold() * minFitness) {
/*      */             
/*  938 */             if (Settings.VERBOSE > 0) {
/*  939 */               System.err.print(" - early T value stop reached.");
/*      */             }
/*      */             
/*      */             break;
/*      */           } 
/*      */         } 
/*  945 */         getSettings().setOptGDGradTreshold(lastTVal);
/*      */       } else {
/*      */         
/*  948 */         weights = deAlg.optimize();
/*      */       } 
/*      */     }
/*      */     int j;
/*  952 */     for (j = 0; j < rset.getModelSize(); j++) {
/*  953 */       rset.getRule(j).setOptWeight(((Double)weights.get(j)).doubleValue());
/*      */     }
/*      */ 
/*      */     
/*  957 */     if (getSettings().getRulePredictionMethod() != 9) deAlg.postProcess(rset);
/*      */ 
/*      */     
/*  960 */     if (Settings.VERBOSE > 0) {
/*  961 */       System.out.print("\nThe weights for rules:");
/*  962 */       for (j = 0; j < weights.size(); j++) {
/*  963 */         System.out.print(((Double)weights.get(j)).doubleValue() + "; ");
/*      */       }
/*  965 */       System.out.print("\n");
/*      */     } 
/*  967 */     int indexOfLastHandledWeight = rset.removeLowWeightRules() + 1;
/*      */ 
/*      */     
/*  970 */     if (getSettings().getOptAddLinearTerms() == 2) {
/*  971 */       rset.addImplicitLinearTermsExplicitly(weights, indexOfLastHandledWeight);
/*      */     }
/*      */ 
/*      */     
/*  975 */     if (getSettings().isOptAddLinearTerms() && 
/*  976 */       getSettings().getOptNormalizeLinearTerms() == 2) {
/*  977 */       rset.convertToPlainLinearTerms();
/*      */     }
/*      */     
/*  980 */     RowData data_copy = (RowData)data.cloneData();
/*  981 */     updateDefaultRule(rset, data_copy);
/*  982 */     return rset;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void updateDefaultRule(ClusRuleSet rset, RowData data) {
/*  990 */     for (int i = 0; i < rset.getModelSize(); i++) {
/*  991 */       data = rset.getRule(i).removeCovered(data);
/*      */     }
/*  993 */     ClusStatistic left_over = createTotalTargetStat(data);
/*  994 */     left_over.calcMean();
/*  995 */     System.out.println("Left Over: " + left_over);
/*  996 */     rset.setTargetStat(left_over);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ClusModel induceRandomly(ClusRun run) throws ClusException, IOException {
/* 1007 */     int number = getSettings().nbRandomRules();
/* 1008 */     RowData data = (RowData)run.getTrainingSet();
/* 1009 */     ClusStatistic stat = createTotalClusteringStat(data);
/* 1010 */     this.m_FindBestTest.initSelectorAndSplit(stat);
/* 1011 */     setHeuristic(this.m_FindBestTest.getBestTest().getHeuristic());
/* 1012 */     ClusRuleSet rset = new ClusRuleSet(getStatManager());
/* 1013 */     Random rn = new Random(42L);
/* 1014 */     for (int i = 0; i < number; i++) {
/* 1015 */       ClusRule rule = generateOneRandomRule(data, rn);
/* 1016 */       rule.computePrediction();
/* 1017 */       rule.printModel();
/* 1018 */       System.out.println();
/* 1019 */       if (!rset.addIfUnique(rule)) {
/* 1020 */         i--;
/*      */       }
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1030 */     if (getSettings().isRulePredictionOptimized()) {
/* 1031 */       rset = optimizeRuleSet(rset, data);
/*      */     }
/*      */     
/* 1034 */     ClusStatistic left_over = createTotalTargetStat(data);
/* 1035 */     left_over.calcMean();
/* 1036 */     System.out.println("Left Over: " + left_over);
/* 1037 */     rset.setTargetStat(left_over);
/* 1038 */     rset.postProc();
/* 1039 */     rset.setTrainErrorScore();
/*      */     
/* 1041 */     if (getSettings().computeDispersion()) {
/* 1042 */       rset.addDataToRules(data);
/* 1043 */       rset.computeDispersion(0);
/* 1044 */       rset.removeDataFromRules();
/* 1045 */       if (run.getTestIter() != null) {
/* 1046 */         RowData testdata = run.getTestSet();
/* 1047 */         rset.addDataToRules(testdata);
/* 1048 */         rset.computeDispersion(1);
/* 1049 */         rset.removeDataFromRules();
/*      */       } 
/*      */     } 
/* 1052 */     return rset;
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
/* 1064 */     ClusStatManager mgr = getStatManager();
/* 1065 */     ClusRule result = new ClusRule(mgr);
/* 1066 */     ClusAttrType[] attrs = data.getSchema().getDescriptiveAttributes();
/*      */     
/* 1068 */     RowData orig_data = data;
/*      */ 
/*      */     
/* 1071 */     if (attrs.length > 1) {
/* 1072 */       nb_tests = rn.nextInt(attrs.length - 1) + 1;
/*      */     } else {
/* 1074 */       nb_tests = 1;
/*      */     } 
/*      */     
/* 1077 */     int[] test_atts = new int[nb_tests];
/* 1078 */     for (int i = 0; i < nb_tests; ) {
/*      */       while (true) {
/* 1080 */         int att_idx = rn.nextInt(attrs.length);
/* 1081 */         boolean unique = true;
/* 1082 */         for (int k = 0; k < i; k++) {
/* 1083 */           if (att_idx == test_atts[k]) {
/* 1084 */             unique = false;
/*      */           }
/*      */         } 
/* 1087 */         if (unique) {
/* 1088 */           test_atts[i] = att_idx; break;
/*      */         } 
/*      */       } 
/*      */       i++;
/*      */     } 
/* 1093 */     CurrentBestTestAndHeuristic sel = this.m_FindBestTest.getBestTest();
/* 1094 */     for (int j = 0; j < test_atts.length; j++) {
/* 1095 */       result.setClusteringStat(createTotalClusteringStat(data));
/* 1096 */       if (this.m_FindBestTest.initSelectorAndStopCrit(result.getClusteringStat(), data)) {
/*      */         break;
/*      */       }
/*      */       
/* 1100 */       sel.resetBestTest();
/* 1101 */       sel.setBestHeur(Double.NEGATIVE_INFINITY);
/* 1102 */       ClusAttrType at = attrs[test_atts[j]];
/* 1103 */       if (at instanceof NominalAttrType) {
/* 1104 */         this.m_FindBestTest.findNominalRandom((NominalAttrType)at, data, rn);
/*      */       } else {
/* 1106 */         this.m_FindBestTest.findNumericRandom((NumericAttrType)at, data, orig_data, rn);
/*      */       } 
/* 1108 */       if (sel.hasBestTest()) {
/* 1109 */         NodeTest test = sel.updateTest();
/* 1110 */         if (Settings.VERBOSE > 0) System.out.println("  Test: " + test.getString() + " -> " + sel.m_BestHeur); 
/* 1111 */         result.addTest(test);
/*      */         
/* 1113 */         data = data.apply(test, 0);
/*      */       } 
/*      */     } 
/*      */     
/* 1117 */     result.setTargetStat(createTotalTargetStat(data));
/* 1118 */     result.setClusteringStat(createTotalClusteringStat(data));
/* 1119 */     return result;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public ClusModel induceSingleUnpruned(ClusRun cr) throws ClusException, IOException {
/* 1125 */     resetAll();
/* 1126 */     if (!getSettings().isRandomRules()) {
/* 1127 */       return induce(cr);
/*      */     }
/* 1129 */     return induceRandomly(cr);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void induceAll(ClusRun cr) throws ClusException, IOException {
/* 1135 */     RowData trainData = (RowData)cr.getTrainingSet();
/* 1136 */     getStatManager().getHeuristic().setTrainData(trainData);
/* 1137 */     ClusStatistic trainStat = getStatManager().getTrainSetStat(2);
/* 1138 */     double value = trainStat.getDispersion(getStatManager().getClusteringWeights(), trainData);
/* 1139 */     getStatManager().getHeuristic().setTrainDataHeurValue(value);
/*      */     
/* 1141 */     ClusModel model = induceSingleUnpruned(cr);
/*      */ 
/*      */ 
/*      */     
/* 1145 */     ClusModelInfo pruned_model = cr.addModelInfo(1);
/* 1146 */     pruned_model.setModel(model);
/* 1147 */     pruned_model.setName("Original");
/*      */   }
/*      */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\algo\rules\ClusRuleInduce.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */