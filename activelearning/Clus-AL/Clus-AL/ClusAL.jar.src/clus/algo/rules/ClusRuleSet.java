/*      */ package clus.algo.rules;
/*      */ 
/*      */ import clus.data.rows.DataTuple;
/*      */ import clus.data.rows.RowData;
/*      */ import clus.data.type.ClusAttrType;
/*      */ import clus.data.type.ClusSchema;
/*      */ import clus.data.type.NominalAttrType;
/*      */ import clus.data.type.NumericAttrType;
/*      */ import clus.error.ClusErrorList;
/*      */ import clus.ext.hierarchical.WHTDStatistic;
/*      */ import clus.main.ClusRun;
/*      */ import clus.main.ClusStatManager;
/*      */ import clus.main.Settings;
/*      */ import clus.model.ClusModel;
/*      */ import clus.model.processor.ClusModelProcessor;
/*      */ import clus.statistic.ClassificationStat;
/*      */ import clus.statistic.ClusStatistic;
/*      */ import clus.statistic.RegressionStat;
/*      */ import clus.statistic.StatisticPrintInfo;
/*      */ import clus.tools.optimization.OptProbl;
/*      */ import clus.util.ClusException;
/*      */ import clus.util.ClusFormat;
/*      */ import java.io.IOException;
/*      */ import java.io.PrintWriter;
/*      */ import java.io.Serializable;
/*      */ import java.text.DecimalFormat;
/*      */ import java.text.NumberFormat;
/*      */ import java.util.ArrayList;
/*      */ import java.util.HashMap;
/*      */ import jeans.util.MyArray;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class ClusRuleSet
/*      */   implements ClusModel, Serializable
/*      */ {
/*      */   public static final long serialVersionUID = 1L;
/*      */   protected ClusStatistic m_TargetStat;
/*      */   protected boolean m_allCoveringRuleExists = false;
/*   64 */   protected ArrayList<ClusRule> m_Rules = new ArrayList<>();
/*      */   
/*   66 */   protected ArrayList m_DefaultData = new ArrayList();
/*      */   
/*      */   protected ClusStatManager m_StatManager;
/*      */   
/*      */   protected boolean m_HasRuleErrors;
/*      */   protected String m_Comment;
/*   72 */   protected double m_optWeightBestTValue = 0.0D;
/*      */   
/*   74 */   protected double m_optWeightBestFitness = 0.0D;
/*      */   
/*      */   public ClusRuleSet(ClusStatManager statmanager) {
/*   77 */     this.m_StatManager = statmanager;
/*      */   }
/*      */ 
/*      */   
/*      */   static final double EQUAL_MAX_DIFFER = 1.0E-6D;
/*      */ 
/*      */   
/*      */   public ClusRuleSet cloneRuleSet() {
/*   85 */     ClusRuleSet new_ruleset = new ClusRuleSet(this.m_StatManager);
/*   86 */     for (int i = 0; i < getModelSize(); i++) {
/*   87 */       new_ruleset.add(getRule(i));
/*      */     }
/*   89 */     return new_ruleset;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ClusRuleSet cloneRuleSetWithThreshold(double threshold) {
/*   98 */     ClusRuleSet new_ruleset = new ClusRuleSet(this.m_StatManager);
/*   99 */     for (int i = 0; i < getModelSize(); i++) {
/*  100 */       ClusRule newRule = getRule(i).cloneRule();
/*  101 */       WHTDStatistic stat = (WHTDStatistic)getRule(i).getTargetStat();
/*  102 */       WHTDStatistic new_stat = (WHTDStatistic)stat.cloneStat();
/*  103 */       new_stat.copyAll((ClusStatistic)stat);
/*  104 */       new_stat.setThreshold(threshold);
/*  105 */       new_stat.calcMean();
/*  106 */       newRule.setTargetStat((ClusStatistic)new_stat);
/*  107 */       new_ruleset.add(newRule);
/*      */     } 
/*  109 */     return new_ruleset;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void add(ClusRule rule) {
/*  116 */     if (getSettings().isWeightedCovering()) {
/*      */       
/*  118 */       if (unique(rule)) {
/*  119 */         this.m_Rules.add(rule);
/*      */       }
/*      */     } else {
/*  122 */       this.m_Rules.add(rule);
/*      */     } 
/*      */   }
/*      */   
/*      */   public void removeLastRule() {
/*  127 */     this.m_Rules.remove(getModelSize() - 1);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean addIfUnique(ClusRule rule) {
/*  135 */     if (unique(rule)) {
/*  136 */       this.m_Rules.add(rule);
/*  137 */       return true;
/*      */     } 
/*  139 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean unique(ClusRule rule) {
/*  148 */     boolean res = true;
/*  149 */     for (int i = 0; i < this.m_Rules.size(); i++) {
/*  150 */       if (((ClusRule)this.m_Rules.get(i)).equals(rule)) {
/*  151 */         res = false;
/*      */       }
/*      */     } 
/*  154 */     return res;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean addIfUniqueDeeply(ClusRule rule) {
/*  164 */     if (uniqueDeeply(rule)) {
/*  165 */       this.m_Rules.add(rule);
/*  166 */       return true;
/*      */     } 
/*  168 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean uniqueDeeply(ClusRule rule) {
/*  179 */     boolean isUnique = true;
/*  180 */     for (int i = 0; i < this.m_Rules.size() && isUnique; i++) {
/*  181 */       if (((ClusRule)this.m_Rules.get(i)).equalsDeeply(rule)) {
/*  182 */         isUnique = false;
/*      */       }
/*      */     } 
/*  185 */     return isUnique;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ClusStatistic predictWeighted(DataTuple tuple) {
/*  192 */     boolean covered = false;
/*  193 */     int pred_method = getSettings().getRulePredictionMethod();
/*  194 */     if (pred_method == 0) {
/*  195 */       for (int j = 0; j < getModelSize(); j++) {
/*  196 */         ClusRule rule = getRule(j);
/*  197 */         if (rule.covers(tuple)) {
/*  198 */           return rule.getTargetStat();
/*      */         }
/*      */       } 
/*  201 */       return this.m_TargetStat;
/*  202 */     }  if (pred_method == 7) {
/*      */ 
/*      */ 
/*      */       
/*  206 */       ClusStatistic clusStatistic = this.m_TargetStat.cloneSimple();
/*  207 */       clusStatistic.unionInit();
/*  208 */       for (int j = 0; j < getModelSize(); j++) {
/*  209 */         ClusRule rule = getRule(j);
/*  210 */         if (rule.covers(tuple)) {
/*  211 */           clusStatistic.union(rule.getTargetStat());
/*  212 */           covered = true;
/*      */         } 
/*      */       } 
/*  215 */       clusStatistic.unionDone();
/*      */ 
/*      */       
/*  218 */       return covered ? clusStatistic : this.m_TargetStat;
/*      */     } 
/*  220 */     if (getSettings().isRulePredictionOptimized()) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  226 */       ClusRule firstRule = getRule(0);
/*      */       
/*  228 */       ClusStatistic prediction = firstRule.predictWeighted(tuple).copyNormalizedWeighted(getAppropriateWeight(firstRule));
/*      */       
/*  230 */       for (int iBaseRule = 1; iBaseRule < getModelSize(); iBaseRule++) {
/*  231 */         ClusRule rule = getRule(iBaseRule);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  237 */         if (rule.covers(tuple)) {
/*  238 */           ClusStatistic rulestat = rule.predictWeighted(tuple);
/*      */ 
/*      */ 
/*      */           
/*  242 */           ClusStatistic norm_rulestat = rulestat.normalizedCopy();
/*  243 */           prediction.addPrediction(norm_rulestat, getAppropriateWeight(rule));
/*      */         } 
/*      */       } 
/*      */ 
/*      */       
/*  248 */       return prediction;
/*      */     } 
/*      */     
/*  251 */     ClusStatistic stat = this.m_TargetStat.cloneSimple();
/*  252 */     double weight_sum = 0.0D;
/*      */     
/*      */     int i;
/*  255 */     for (i = 0; i < getModelSize(); i++) {
/*  256 */       ClusRule rule = getRule(i);
/*  257 */       if (rule.covers(tuple)) {
/*  258 */         weight_sum += getAppropriateWeight(rule);
/*      */       }
/*      */     } 
/*      */ 
/*      */     
/*  263 */     for (i = 0; i < getModelSize(); i++) {
/*  264 */       ClusRule rule = getRule(i);
/*  265 */       if (rule.covers(tuple)) {
/*  266 */         ClusStatistic rulestat = rule.predictWeighted(tuple);
/*  267 */         double weight = getAppropriateWeight(rule) / weight_sum;
/*  268 */         ClusStatistic norm_rulestat = rulestat.normalizedCopy();
/*  269 */         stat.addPrediction(norm_rulestat, weight);
/*  270 */         covered = true;
/*      */       } 
/*      */     } 
/*  273 */     if (covered) {
/*  274 */       stat.computePrediction();
/*  275 */       return stat;
/*      */     } 
/*      */     
/*  278 */     return this.m_TargetStat;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public double getAppropriateWeight(ClusRule rule) {
/*  284 */     switch (getSettings().getRulePredictionMethod()) {
/*      */       case 2:
/*  286 */         return rule.m_TargetStat.m_SumWeight;
/*      */       case 1:
/*  288 */         return rule.getCoverage()[0];
/*      */       case 4:
/*  290 */         return rule.m_TargetStat.m_SumWeight * (1.0D - rule.getTrainErrorScore());
/*      */       case 3:
/*  292 */         return 1.0D - rule.getTrainErrorScore();
/*      */       case 6:
/*      */       case 8:
/*      */       case 9:
/*  296 */         return rule.getOptWeight();
/*      */     } 
/*  298 */     System.err.println("getAppropriateWeight(): Unknown weighted prediction method!");
/*  299 */     return Double.NEGATIVE_INFINITY;
/*      */   }
/*      */ 
/*      */   
/*      */   public void removeEmptyRules() {
/*  304 */     for (int i = getModelSize() - 1; i >= 0; i--) {
/*  305 */       if (getRule(i).isEmpty()) {
/*  306 */         this.m_Rules.remove(i);
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int removeLowWeightRules() {
/*  316 */     double threshold = getSettings().getOptRuleWeightThreshold();
/*  317 */     int nb_rules = getModelSize();
/*  318 */     for (int i = nb_rules - 1; i >= 0; i--) {
/*      */ 
/*      */ 
/*      */       
/*  322 */       if (i != 0 || !this.m_allCoveringRuleExists)
/*      */       {
/*      */         
/*  325 */         if (Math.abs(getRule(i).getOptWeight()) < threshold || getRule(i).getOptWeight() == 0.0D)
/*      */         {
/*  327 */           this.m_Rules.remove(i); } 
/*      */       }
/*      */     } 
/*  330 */     if (Settings.VERBOSE > 0) System.out.println("Rules left: " + getModelSize() + " out of " + nb_rules); 
/*  331 */     return nb_rules - 1;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void convertToPlainLinearTerms() {
/*  340 */     RegressionStat firstRuleStat = (RegressionStat)(getRule(0)).m_TargetStat;
/*      */ 
/*      */     
/*  343 */     if (getRule(0).getOptWeight() == 0.0D) {
/*      */       
/*  345 */       for (int i = 0; i < firstRuleStat.m_NbAttrs; i++) {
/*  346 */         firstRuleStat.m_Means[i] = 0.0D;
/*      */       }
/*  348 */       getRule(0).setOptWeight(1.0D);
/*      */     } 
/*      */     
/*  351 */     double defaultWeight = getRule(0).getOptWeight();
/*  352 */     ClusStatistic tar_stat = this.m_StatManager.getStatistic(3);
/*      */     
/*  354 */     double[] addToDefaultPred = new double[tar_stat.getNbAttributes()];
/*      */     
/*  356 */     for (int iRule = 0; iRule < getModelSize(); iRule++) {
/*      */       
/*  358 */       ClusRule cursor = getRule(iRule);
/*  359 */       if (!cursor.isRegularRule()) {
/*  360 */         addToDefaultPred = ((ClusRuleLinearTerm)cursor).convertToPlainTerm(addToDefaultPred, defaultWeight);
/*      */       }
/*      */     } 
/*      */ 
/*      */     
/*  365 */     for (int iTarget = 0; iTarget < firstRuleStat.m_NbAttrs; iTarget++) {
/*  366 */       firstRuleStat.m_Means[iTarget] = firstRuleStat.m_Means[iTarget] + addToDefaultPred[iTarget];
/*  367 */       firstRuleStat.m_SumValues[iTarget] = firstRuleStat.m_Means[iTarget];
/*  368 */       firstRuleStat.m_SumWeights[iTarget] = 1.0D;
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public void simplifyRules() {
/*  374 */     for (int i = getModelSize() - 1; i >= 0; i--) {
/*  375 */       getRule(i).simplify();
/*      */     }
/*      */   }
/*      */   
/*      */   public void attachModel(HashMap table) throws ClusException {
/*  380 */     for (int i = 0; i < this.m_Rules.size(); i++) {
/*  381 */       ClusRule rule = this.m_Rules.get(i);
/*  382 */       rule.attachModel(table);
/*      */     } 
/*      */   }
/*      */   
/*      */   public void printModel(PrintWriter wrt) {
/*  387 */     printModel(wrt, StatisticPrintInfo.getInstance());
/*      */   }
/*      */   
/*      */   public void printModel(PrintWriter wrt, StatisticPrintInfo info) {
/*  391 */     NumberFormat fr = ClusFormat.SIX_AFTER_DOT;
/*      */     
/*  393 */     boolean headers = true;
/*      */     
/*  395 */     double[][] avg_dispersion = new double[2][3];
/*  396 */     double[] avg_coverage = new double[2];
/*  397 */     double[][] avg_prod = new double[2][3];
/*      */     
/*  399 */     if (!Settings.isPrintAllRules()) {
/*  400 */       wrt.println("Set of " + this.m_Rules.size() + " rules.\n");
/*      */     }
/*  402 */     if (getSettings().getRulePredictionMethod() == 8 && 
/*  403 */       getSettings().getOptGDNbOfTParameterTry() > 1)
/*      */     {
/*  405 */       wrt.println("Gradient descent optimization: Smallest test fitness of " + fr
/*  406 */           .format(this.m_optWeightBestFitness) + " with T value: " + fr.format(this.m_optWeightBestTValue) + "\n");
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*  411 */     for (int i = 0; i < this.m_Rules.size(); i++) {
/*  412 */       ClusRule rule = this.m_Rules.get(i);
/*  413 */       if (headers) {
/*      */         
/*  415 */         if (Settings.isPrintAllRules()) {
/*  416 */           String head = new String("Rule " + (i + 1) + ":");
/*  417 */           char[] underline = new char[head.length()];
/*  418 */           for (int j = 0; j < head.length(); j++) {
/*  419 */             underline[j] = '=';
/*      */           }
/*      */           
/*  422 */           wrt.println(head);
/*  423 */           wrt.println(new String(underline));
/*      */         } 
/*      */         
/*  426 */         if (getSettings().computeDispersion()) {
/*  427 */           avg_dispersion[0][0] = avg_dispersion[0][0] + rule.m_CombStat[0].dispersionCalc();
/*  428 */           avg_dispersion[0][1] = avg_dispersion[0][1] + rule.m_CombStat[0].dispersionNum(1);
/*  429 */           avg_dispersion[0][2] = avg_dispersion[0][2] + rule.m_CombStat[0].dispersionNom(1);
/*  430 */           avg_coverage[0] = avg_coverage[0] + rule.m_Coverage[0];
/*  431 */           avg_prod[0][0] = avg_prod[0][0] + rule.m_CombStat[0].dispersionCalc() * rule.m_Coverage[0];
/*  432 */           avg_prod[0][1] = avg_prod[0][1] + rule.m_CombStat[0].dispersionNum(1) * rule.m_Coverage[0];
/*  433 */           avg_prod[0][2] = avg_prod[0][2] + rule.m_CombStat[0].dispersionNom(1) * rule.m_Coverage[0];
/*  434 */           if (rule.m_CombStat[1] != null) {
/*  435 */             avg_dispersion[1][0] = avg_dispersion[1][0] + rule.m_CombStat[1].dispersionCalc();
/*  436 */             avg_dispersion[1][1] = avg_dispersion[1][1] + rule.m_CombStat[1].dispersionNum(1);
/*  437 */             avg_dispersion[1][2] = avg_dispersion[1][2] + rule.m_CombStat[1].dispersionNom(1);
/*  438 */             avg_coverage[1] = avg_coverage[1] + rule.m_Coverage[1];
/*  439 */             avg_prod[1][0] = avg_prod[1][0] + rule.m_CombStat[1].dispersionCalc() * rule.m_Coverage[1];
/*  440 */             avg_prod[1][1] = avg_prod[1][1] + rule.m_CombStat[1].dispersionNum(1) * rule.m_Coverage[1];
/*  441 */             avg_prod[1][2] = avg_prod[1][2] + rule.m_CombStat[1].dispersionNom(1) * rule.m_Coverage[1];
/*      */           } 
/*      */         } 
/*      */       } 
/*  445 */       if (Settings.isPrintAllRules()) {
/*      */         
/*  447 */         rule.printModel(wrt, info);
/*  448 */         wrt.println();
/*      */       } 
/*      */     } 
/*      */     
/*  452 */     if (this.m_TargetStat != null && this.m_TargetStat.isValidPrediction()) {
/*  453 */       if (headers) {
/*  454 */         if (this.m_Comment == null) {
/*  455 */           wrt.println("Default rule:");
/*  456 */           wrt.println("=============");
/*      */         } else {
/*  458 */           wrt.println("Default rule" + this.m_Comment + ":");
/*  459 */           wrt.println("=============");
/*      */         } 
/*      */       }
/*  462 */       wrt.println("Default = " + ((this.m_TargetStat == null) ? "N/A" : this.m_TargetStat.getString()));
/*      */     } 
/*  464 */     if (headers && getSettings().computeDispersion()) {
/*  465 */       wrt.println("\n\nRule set dispersion:");
/*  466 */       wrt.println("=====================");
/*  467 */       avg_dispersion[0][0] = (avg_dispersion[0][0] == 0.0D) ? 0.0D : (avg_dispersion[0][0] / this.m_Rules.size());
/*  468 */       avg_dispersion[0][1] = (avg_dispersion[0][1] == 0.0D) ? 0.0D : (avg_dispersion[0][1] / this.m_Rules.size());
/*  469 */       avg_dispersion[0][2] = (avg_dispersion[0][2] == 0.0D) ? 0.0D : (avg_dispersion[0][2] / this.m_Rules.size());
/*  470 */       avg_coverage[0] = (avg_coverage[0] == 0.0D) ? 0.0D : (avg_coverage[0] / this.m_Rules.size());
/*  471 */       avg_prod[0][0] = (avg_prod[0][0] == 0.0D) ? 0.0D : (avg_prod[0][0] / this.m_Rules.size());
/*  472 */       avg_prod[0][1] = (avg_prod[0][1] == 0.0D) ? 0.0D : (avg_prod[0][1] / this.m_Rules.size());
/*  473 */       avg_prod[0][2] = (avg_prod[0][2] == 0.0D) ? 0.0D : (avg_prod[0][2] / this.m_Rules.size());
/*  474 */       avg_dispersion[1][0] = (avg_dispersion[1][0] == 0.0D) ? 0.0D : (avg_dispersion[1][0] / this.m_Rules.size());
/*  475 */       avg_dispersion[1][1] = (avg_dispersion[1][1] == 0.0D) ? 0.0D : (avg_dispersion[1][1] / this.m_Rules.size());
/*  476 */       avg_dispersion[1][2] = (avg_dispersion[1][2] == 0.0D) ? 0.0D : (avg_dispersion[1][2] / this.m_Rules.size());
/*  477 */       avg_coverage[1] = (avg_coverage[1] == 0.0D) ? 0.0D : (avg_coverage[1] / this.m_Rules.size());
/*  478 */       avg_prod[1][0] = (avg_prod[1][0] == 0.0D) ? 0.0D : (avg_prod[1][0] / this.m_Rules.size());
/*  479 */       avg_prod[1][1] = (avg_prod[1][1] == 0.0D) ? 0.0D : (avg_prod[1][1] / this.m_Rules.size());
/*  480 */       avg_prod[1][2] = (avg_prod[1][2] == 0.0D) ? 0.0D : (avg_prod[1][2] / this.m_Rules.size());
/*  481 */       wrt.println("   Avg_Dispersion  (train): " + fr.format(avg_dispersion[0][0]) + " = " + fr.format(avg_dispersion[0][1]) + " + " + fr.format(avg_dispersion[0][2]));
/*  482 */       wrt.println("   Avg_Coverage    (train): " + fr.format(avg_coverage[0]));
/*  483 */       wrt.println("   Avg_Cover*Disp  (train): " + fr.format(avg_prod[0][0]) + " = " + fr.format(avg_prod[0][1]) + " + " + fr.format(avg_prod[0][2]));
/*  484 */       wrt.println("   Avg_Dispersion  (test):  " + fr.format(avg_dispersion[1][0]) + " = " + fr.format(avg_dispersion[1][1]) + " + " + fr.format(avg_dispersion[1][2]));
/*  485 */       wrt.println("   Avg_Coverage    (test):  " + fr.format(avg_coverage[1]));
/*  486 */       wrt.println("   Avg_Cover*Disp  (test):  " + fr.format(avg_prod[1][0]) + " = " + fr.format(avg_prod[1][1]) + " + " + fr.format(avg_prod[1][2]));
/*      */     } 
/*      */   }
/*      */   
/*      */   public void printModelAndExamples(PrintWriter wrt, StatisticPrintInfo info, ClusSchema schema) {
/*  491 */     for (int i = 0; i < this.m_Rules.size(); i++) {
/*  492 */       ClusRule rule = this.m_Rules.get(i);
/*  493 */       rule.printModel(wrt, info);
/*  494 */       wrt.println();
/*  495 */       wrt.println("Covered examples:");
/*  496 */       ArrayList<DataTuple> data = rule.getData();
/*  497 */       ClusAttrType[] attrs = schema.getAllAttrUse(3);
/*  498 */       ClusAttrType[] key = schema.getAllAttrUse(4);
/*  499 */       for (int k = 0; k < data.size(); k++) {
/*  500 */         DataTuple tuple = data.get(k);
/*  501 */         wrt.print(String.valueOf(k + 1) + ": ");
/*  502 */         boolean hasval = false; int j;
/*  503 */         for (j = 0; j < key.length; j++) {
/*  504 */           if (hasval) wrt.print(","); 
/*  505 */           wrt.print(key[j].getString(tuple));
/*  506 */           hasval = true;
/*      */         } 
/*  508 */         for (j = 0; j < attrs.length; j++) {
/*  509 */           if (hasval) wrt.print(","); 
/*  510 */           wrt.print(attrs[j].getString(tuple));
/*  511 */           hasval = true;
/*      */         } 
/*  513 */         wrt.println();
/*      */       } 
/*  515 */       wrt.println();
/*      */     } 
/*  517 */     wrt.println("Default = " + ((this.m_TargetStat == null) ? "None" : this.m_TargetStat.getString()));
/*      */   }
/*      */   
/*      */   public void printModelAndExamples(PrintWriter wrt, StatisticPrintInfo info, RowData examples) {
/*  521 */     addDataToRules(examples);
/*      */     
/*  523 */     printModelAndExamples(wrt, info, examples.getSchema());
/*  524 */     removeDataFromRules();
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void printModelToPythonScript(PrintWriter wrt) {}
/*      */ 
/*      */ 
/*      */   
/*      */   public void printModelToQuery(PrintWriter wrt, ClusRun cr, int starttree, int startitem, boolean ex) {}
/*      */ 
/*      */   
/*      */   public int getModelSize() {
/*  537 */     return this.m_Rules.size();
/*      */   }
/*      */   
/*      */   public Settings getSettings() {
/*  541 */     return this.m_StatManager.getSettings();
/*      */   }
/*      */   
/*      */   public ClusRule getRule(int i) {
/*  545 */     return this.m_Rules.get(i);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getNbLiterals() {
/*  553 */     int count = 0;
/*  554 */     for (int i = 0; i < this.m_Rules.size(); i++) {
/*  555 */       ClusRule rule = this.m_Rules.get(i);
/*  556 */       if (rule.isRegularRule())
/*  557 */         count += rule.getModelSize(); 
/*      */     } 
/*  559 */     return count;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private int getNbLinearTerms() {
/*  566 */     int count = 0;
/*  567 */     for (int i = 0; i < this.m_Rules.size(); i++) {
/*  568 */       ClusRule rule = this.m_Rules.get(i);
/*  569 */       if (!rule.isRegularRule()) {
/*  570 */         count++;
/*      */       }
/*      */     } 
/*  573 */     return count;
/*      */   }
/*      */   
/*      */   public void setTargetStat(ClusStatistic def) {
/*  577 */     this.m_TargetStat = def;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void postProc() {
/*  585 */     this.m_TargetStat.calcMean();
/*  586 */     for (int i = 0; i < this.m_Rules.size(); i++) {
/*  587 */       ClusRule rule = this.m_Rules.get(i);
/*  588 */       rule.postProc();
/*      */     } 
/*      */   }
/*      */   
/*      */   public String getModelInfo() {
/*  593 */     return "Rules = " + getModelSize() + " (Tests: " + getNbLiterals() + " and linear terms: " + 
/*  594 */       getNbLinearTerms() + ")";
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void computeDispersion(int mode) {
/*  602 */     for (int i = 0; i < this.m_Rules.size(); i++) {
/*  603 */       ClusRule rule = this.m_Rules.get(i);
/*  604 */       rule.computeDispersion(mode);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public double computeErrorScore(RowData data) {
/*  614 */     ClusStatistic tar_stat = this.m_StatManager.getStatistic(3);
/*      */     
/*  616 */     if (tar_stat instanceof ClassificationStat) {
/*  617 */       double result = 0.0D;
/*  618 */       int nb_rows = data.getNbRows();
/*  619 */       int nb_tar = tar_stat.getNbNominalAttributes();
/*  620 */       int[] nb_right = new int[nb_tar];
/*  621 */       for (int i = 0; i < nb_rows; i++) {
/*  622 */         DataTuple tuple = data.getTuple(i);
/*  623 */         int[] predictions = predictWeighted(tuple).getNominalPred();
/*      */         
/*  625 */         NominalAttrType[] targetAttrs = data.getSchema().getNominalAttrUse(3);
/*  626 */         for (int k = 0; k < nb_tar; k++) {
/*  627 */           int true_value = targetAttrs[k].getNominal(tuple);
/*  628 */           if (predictions[k] == true_value) {
/*  629 */             nb_right[k] = nb_right[k] + 1;
/*      */           }
/*      */         } 
/*      */       } 
/*  633 */       for (int j = 0; j < nb_tar; j++) {
/*  634 */         result += (1.0D * nb_rows - nb_right[j]) / nb_rows;
/*      */       }
/*  636 */       result /= nb_tar;
/*  637 */       return result;
/*      */     } 
/*  639 */     if (tar_stat instanceof RegressionStat) {
/*  640 */       double result = 0.0D;
/*  641 */       int nb_rows = data.getNbRows();
/*  642 */       int nb_tar = tar_stat.getNbNumericAttributes();
/*  643 */       double[] sum_sqr_err = new double[nb_tar];
/*  644 */       NumericAttrType[] targetAttrs = data.getSchema().getNumericAttrUse(3);
/*  645 */       for (int i = 0; i < nb_rows; i++) {
/*  646 */         DataTuple tuple = data.getTuple(i);
/*  647 */         double[] predictions = ((RegressionStat)predictWeighted(tuple)).getNumericPred();
/*  648 */         for (int k = 0; k < nb_tar; k++) {
/*  649 */           double diff = predictions[k] - targetAttrs[k].getNumeric(tuple);
/*  650 */           sum_sqr_err[k] = sum_sqr_err[k] + diff * diff;
/*      */         } 
/*      */       } 
/*  653 */       for (int j = 0; j < nb_tar; j++) {
/*  654 */         result += Math.sqrt(sum_sqr_err[j] / nb_rows);
/*      */       }
/*  656 */       result /= nb_tar;
/*  657 */       return result;
/*      */     } 
/*  659 */     return -1.0D;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setTrainErrorScore() {
/*  669 */     for (int i = 0; i < this.m_Rules.size(); i++) {
/*  670 */       ((ClusRule)this.m_Rules.get(i)).setTrainErrorScore();
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean addDataToRules(DataTuple tuple) {
/*  679 */     boolean covered = false;
/*  680 */     for (int i = 0; i < this.m_Rules.size(); i++) {
/*  681 */       ClusRule rule = this.m_Rules.get(i);
/*  682 */       if (rule.covers(tuple)) {
/*  683 */         rule.addDataTuple(tuple);
/*  684 */         covered = true;
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/*  689 */     return covered;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void addDataToRules(RowData data) {
/*  697 */     for (int i = 0; i < data.getNbRows(); i++) {
/*  698 */       DataTuple tuple = data.getTuple(i);
/*  699 */       if (!addDataToRules(tuple)) {
/*  700 */         this.m_DefaultData.add(tuple);
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void removeDataFromRules() {
/*  709 */     for (int i = 0; i < this.m_Rules.size(); i++) {
/*  710 */       ((ClusRule)this.m_Rules.get(i)).removeDataTuples();
/*      */     }
/*  712 */     this.m_DefaultData.clear();
/*      */   }
/*      */   
/*      */   public ArrayList getDefaultData() {
/*  716 */     return this.m_DefaultData;
/*      */   }
/*      */   
/*      */   public void applyModelProcessors(DataTuple tuple, MyArray mproc) throws IOException {
/*  720 */     for (int i = 0; i < getModelSize(); i++) {
/*  721 */       ClusRule rule = getRule(i);
/*  722 */       if (rule.covers(tuple)) {
/*  723 */         for (int j = 0; j < mproc.size(); j++) {
/*  724 */           ClusModelProcessor proc = (ClusModelProcessor)mproc.elementAt(j);
/*  725 */           proc.modelUpdate(tuple, rule);
/*      */         } 
/*      */       }
/*      */     } 
/*      */   }
/*      */   
/*      */   public final void applyModelProcessor(DataTuple tuple, ClusModelProcessor proc) throws IOException {
/*  732 */     for (int i = 0; i < getModelSize(); i++) {
/*  733 */       ClusRule rule = getRule(i);
/*  734 */       if (rule.covers(tuple)) proc.modelUpdate(tuple, rule); 
/*      */     } 
/*      */   }
/*      */   
/*      */   public void setError(ClusErrorList error, int subset) {
/*  739 */     this.m_HasRuleErrors = true;
/*  740 */     for (int i = 0; i < this.m_Rules.size(); i++) {
/*  741 */       ClusRule rule = getRule(i);
/*  742 */       if (error != null) { rule.setError(error.getErrorClone(), subset); }
/*  743 */       else { rule.setError(null, subset); }
/*      */     
/*      */     } 
/*      */   }
/*      */   public boolean hasRuleErrors() {
/*  748 */     return this.m_HasRuleErrors;
/*      */   }
/*      */   
/*      */   public int getID() {
/*  752 */     return 0;
/*      */   }
/*      */   
/*      */   public void numberRules() {
/*  756 */     for (int i = 0; i < this.m_Rules.size(); i++) {
/*  757 */       ClusRule rule = getRule(i);
/*  758 */       rule.setID(i + 1);
/*      */     } 
/*      */   }
/*      */   
/*      */   public ClusModel prune(int prunetype) {
/*  763 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void retrieveStatistics(ArrayList list) {}
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public OptProbl.OptParam giveFormForWeightOptimization(PrintWriter outLogFile, RowData data) {
/*  780 */     ClusSchema schema = data.getSchema();
/*      */ 
/*      */     
/*  783 */     double[] defaultPred = null;
/*      */ 
/*      */ 
/*      */     
/*  787 */     defaultPred = addDefaultRuleToRuleSet();
/*      */     
/*  789 */     if (getSettings().isOptDefaultShiftPred())
/*      */     {
/*  791 */       shiftRulePredictions(defaultPred);
/*      */     }
/*      */     
/*  794 */     if (getSettings().isOptOmitRulePredictions())
/*      */     {
/*  796 */       omitRulePredictions();
/*      */     }
/*      */ 
/*      */     
/*  800 */     if (getSettings().isOptWeightGenerality())
/*      */     {
/*      */       
/*  803 */       weightGeneralityForPredictions(data.getNbRows());
/*      */     }
/*      */ 
/*      */     
/*  807 */     if (getSettings().isOptAddLinearTerms()) {
/*      */ 
/*      */       
/*  810 */       ClusRuleLinearTerm.initializeClass(data, this.m_StatManager);
/*      */       
/*  812 */       if (getSettings().getOptAddLinearTerms() == 1)
/*      */       {
/*      */         
/*  815 */         addLinearTermsToRuleSet();
/*      */       }
/*      */     } 
/*      */ 
/*      */     
/*  820 */     ClusStatistic tar_stat = this.m_StatManager.getStatistic(3);
/*  821 */     int nb_target = tar_stat.getNbAttributes();
/*  822 */     int nb_baseFunctions = getModelSize();
/*  823 */     int nb_rows = data.getNbRows();
/*  824 */     boolean isClassification = false;
/*  825 */     if (this.m_TargetStat instanceof ClassificationStat) {
/*  826 */       isClassification = true;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*  831 */     ClusAttrType[] trueValuesTemp = new ClusAttrType[nb_target];
/*  832 */     if (isClassification) {
/*      */       
/*  834 */       trueValuesTemp = (ClusAttrType[])schema.getNominalAttrUse(3);
/*      */     } else {
/*      */       
/*  837 */       trueValuesTemp = (ClusAttrType[])schema.getNumericAttrUse(3);
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  844 */     OptProbl.TrueValues[] trueValues = new OptProbl.TrueValues[nb_rows];
/*      */     
/*  846 */     for (int iRows = 0; iRows < nb_rows; iRows++) {
/*  847 */       DataTuple tuple = data.getTuple(iRows);
/*      */       
/*  849 */       OptProbl.TrueValues newTrueTargets = null;
/*      */       
/*  851 */       if (getSettings().getOptAddLinearTerms() == 2) {
/*  852 */         newTrueTargets = new OptProbl.TrueValues(nb_target, tuple);
/*      */       } else {
/*  854 */         newTrueTargets = new OptProbl.TrueValues(nb_target, null);
/*      */       } 
/*      */       
/*  857 */       trueValues[iRows] = newTrueTargets;
/*      */ 
/*      */       
/*  860 */       for (int kTargets = 0; kTargets < nb_target; kTargets++) {
/*      */         
/*  862 */         if (isClassification) {
/*  863 */           (trueValues[iRows]).m_targets[kTargets] = ((NominalAttrType)trueValuesTemp[kTargets]).getNominal(tuple);
/*      */         } else {
/*  865 */           (trueValues[iRows]).m_targets[kTargets] = ((NumericAttrType)trueValuesTemp[kTargets]).getNumeric(tuple);
/*      */         } 
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  876 */     boolean allRulesScrolled = false;
/*  877 */     int nbOfRegularRules = 0;
/*  878 */     for (int jRules = 0; jRules < nb_baseFunctions; jRules++) {
/*  879 */       ClusRule rule = getRule(jRules);
/*  880 */       if (rule.isRegularRule()) {
/*  881 */         nbOfRegularRules++;
/*  882 */         if (allRulesScrolled) {
/*      */           
/*  884 */           System.err.println("Error: Order of rule set is wrong. Rules have to be before linear terms etc.");
/*  885 */           System.exit(1);
/*      */         } 
/*      */       } else {
/*  888 */         allRulesScrolled = true;
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  895 */     int[] nb_values = new int[nb_target];
/*      */     
/*  897 */     for (int iTarget = 0; iTarget < nb_target; iTarget++) {
/*  898 */       if (isClassification) {
/*  899 */         nb_values[iTarget] = ((ClassificationStat)this.m_TargetStat).getAttribute(0).getNbValues();
/*      */       } else {
/*  901 */         nb_values[iTarget] = 1;
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/*  907 */     DecimalFormat mf = new DecimalFormat("#00.000");
/*  908 */     mf.setPositivePrefix("+");
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  913 */     double[][][][] nonrule_pred = new double[nb_baseFunctions - nbOfRegularRules][nb_rows][nb_target][];
/*      */ 
/*      */     
/*  916 */     OptProbl.RulePred[] rule_pred = new OptProbl.RulePred[nbOfRegularRules];
/*  917 */     for (int j = 0; j < nbOfRegularRules; j++) {
/*  918 */       rule_pred[j] = new OptProbl.RulePred(nb_rows, nb_target);
/*      */       
/*  920 */       ClusRule rule = getRule(j);
/*      */ 
/*      */       
/*  923 */       double[] targets = ((RegressionStat)rule.predictWeighted(null)).normalizedCopy().getNumericPred();
/*      */       
/*  925 */       for (int k = 0; k < nb_target; k++) {
/*  926 */         (rule_pred[j]).m_prediction[k][0] = targets[k];
/*      */       }
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/*  932 */     if (outLogFile != null) {
/*      */ 
/*      */       
/*  935 */       DecimalFormat format6 = new DecimalFormat("000000");
/*  936 */       for (int k = 0; k < nb_baseFunctions; k++) {
/*  937 */         if (getRule(k).isRegularRule()) {
/*  938 */           outLogFile.print("R  " + format6.format((k + 1)));
/*      */         }
/*      */         else {
/*      */           
/*  942 */           outLogFile.print("L  " + format6.format((k + 1)));
/*      */         } 
/*  944 */         for (int m = 1; m < nb_target; m++) {
/*  945 */           outLogFile.print("         ");
/*      */         }
/*      */       } 
/*  948 */       outLogFile.println();
/*      */     } 
/*      */     
/*  951 */     for (int i = 0; i < nb_rows; i++) {
/*      */       
/*  953 */       DataTuple tuple = data.getTuple(i);
/*      */       
/*  955 */       for (int k = 0; k < nb_baseFunctions; k++) {
/*  956 */         ClusRule rule = getRule(k);
/*      */         
/*  958 */         if (rule.isRegularRule()) {
/*      */ 
/*      */           
/*  961 */           if (rule.covers(tuple)) {
/*  962 */             (rule_pred[k]).m_cover.set(i);
/*      */           }
/*  964 */           printRuleToFile(outLogFile, mf, (rule_pred[k]).m_prediction, (rule_pred[k]).m_cover
/*  965 */               .get(i));
/*      */         }
/*      */         else {
/*      */           
/*  969 */           int nonRegIndex = k - nbOfRegularRules;
/*      */ 
/*      */           
/*  972 */           for (int m = 0; m < nb_target; m++) {
/*  973 */             nonrule_pred[nonRegIndex][i][m] = new double[nb_values[m]];
/*      */           }
/*      */           
/*  976 */           if (rule.covers(tuple)) {
/*      */ 
/*      */             
/*  979 */             if (isClassification) {
/*  980 */               nonrule_pred[nonRegIndex][i] = ((ClassificationStat)rule
/*  981 */                 .predictWeighted(tuple)).normalizedCopy().getClassCounts();
/*      */             }
/*      */             else {
/*      */               
/*  985 */               double[] targets = ((RegressionStat)rule.predictWeighted(tuple)).normalizedCopy().getNumericPred();
/*  986 */               for (int kTargets = 0; kTargets < nb_target; kTargets++) {
/*  987 */                 nonrule_pred[nonRegIndex][i][kTargets][0] = targets[kTargets];
/*      */               }
/*      */             } 
/*      */           } else {
/*  991 */             for (int kTargets = 0; kTargets < nb_target; kTargets++) {
/*  992 */               for (int lValues = 0; lValues < nb_values[kTargets]; lValues++) {
/*  993 */                 nonrule_pred[nonRegIndex][i][kTargets][lValues] = Double.NaN;
/*      */               }
/*      */             } 
/*      */           } 
/*      */           
/*  998 */           printPredictionsToFile(outLogFile, mf, nonrule_pred[nonRegIndex][i]);
/*      */         } 
/*      */       } 
/*      */ 
/*      */ 
/*      */       
/* 1004 */       if (outLogFile != null) {
/*      */ 
/*      */         
/* 1007 */         outLogFile.print(" :: [");
/* 1008 */         for (int kTargets = 0; kTargets < nb_target; kTargets++) {
/* 1009 */           outLogFile.print(mf.format((trueValues[i]).m_targets[kTargets]));
/* 1010 */           if (kTargets < nb_target - 1) outLogFile.print("; "); 
/*      */         } 
/* 1012 */         outLogFile.print("]\n");
/*      */       } 
/*      */     } 
/*      */     
/* 1016 */     if (outLogFile != null) {
/* 1017 */       outLogFile.flush();
/*      */     }
/*      */ 
/*      */     
/* 1021 */     OptProbl.OptParam param = new OptProbl.OptParam(rule_pred, nonrule_pred, trueValues, ClusRuleLinearTerm.returnImplicitLinearTermsIfNeeded(data));
/* 1022 */     return param;
/*      */   }
/*      */ 
/*      */   
/*      */   private void printRuleToFile(PrintWriter outLogFile, DecimalFormat mf, double[][] prediction, boolean covers) {
/* 1027 */     if (outLogFile == null)
/* 1028 */       return;  if (covers) {
/* 1029 */       printPredictionsToFile(outLogFile, mf, prediction);
/*      */     } else {
/*      */       
/* 1032 */       double[][] tempPreds = new double[prediction.length][(prediction[0]).length];
/* 1033 */       printPredictionsToFile(outLogFile, mf, tempPreds);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void printPredictionsToFile(PrintWriter outLogFile, DecimalFormat mf, double[][] targets) {
/* 1042 */     if (outLogFile == null)
/*      */       return; 
/* 1044 */     boolean isClassification = ((targets[0]).length > 1);
/*      */ 
/*      */     
/* 1047 */     outLogFile.print("[");
/* 1048 */     for (int kTargets = 0; kTargets < targets.length; kTargets++) {
/*      */       
/* 1050 */       if (isClassification) outLogFile.print("{"); 
/* 1051 */       for (int lValues = 0; lValues < (targets[kTargets]).length; lValues++) {
/* 1052 */         outLogFile.print(mf.format(targets[kTargets][lValues]));
/* 1053 */         if (lValues < (targets[kTargets]).length - 1) outLogFile.print("; ");
/*      */       
/*      */       } 
/* 1056 */       if (isClassification) outLogFile.print("}"); 
/* 1057 */       if (kTargets < targets.length - 1) outLogFile.print("; "); 
/*      */     } 
/* 1059 */     outLogFile.print("]");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void shiftRulePredictions(double[] defaultPred) {
/* 1068 */     if (Settings.VERBOSE > 0)
/* 1069 */       System.out.println("Shifting rule predictions according to the default prediction."); 
/* 1070 */     for (int iRule = 1; iRule < getModelSize(); iRule++) {
/*      */       
/* 1072 */       ClusRule rule = getRule(iRule);
/* 1073 */       if (rule.isRegularRule()) {
/* 1074 */         if (!(rule.m_TargetStat instanceof RegressionStat)) {
/* 1075 */           System.err.println("Error: GD optimization is implemented regression only.");
/*      */         }
/* 1077 */         RegressionStat stat = (RegressionStat)rule.m_TargetStat;
/*      */         
/* 1079 */         for (int iTarget = 0; iTarget < stat.m_NbAttrs; iTarget++) {
/*      */           
/* 1081 */           stat.m_Means[iTarget] = stat.m_Means[iTarget] - defaultPred[iTarget];
/* 1082 */           stat.m_SumValues[iTarget] = stat.m_Means[iTarget];
/* 1083 */           stat.m_SumWeights[iTarget] = 1.0D;
/*      */         } 
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void omitRulePredictions() {
/* 1094 */     if (Settings.VERBOSE > 0) {
/* 1095 */       System.out.println("Omitting rule predictions for optimization.");
/*      */     }
/*      */ 
/*      */     
/* 1099 */     int iRule = 0;
/* 1100 */     if (getSettings().isOptNormalization() && 
/* 1101 */       getSettings().getOptNormalization() != 2) {
/* 1102 */       iRule = 1;
/*      */     }
/* 1104 */     for (; iRule < getModelSize(); iRule++) {
/*      */       
/* 1106 */       ClusRule rule = getRule(iRule);
/* 1107 */       if (rule.isRegularRule()) {
/* 1108 */         if (!(rule.m_TargetStat instanceof RegressionStat)) {
/* 1109 */           System.err.println("Error: GD optimization is implemented regression only.");
/*      */         }
/* 1111 */         RegressionStat stat = (RegressionStat)rule.m_TargetStat;
/*      */ 
/*      */         
/* 1114 */         if (stat.m_NbAttrs > 1) {
/*      */ 
/*      */           
/* 1117 */           double scalingValue = 0.0D;
/*      */ 
/*      */           
/* 1120 */           double maxCompareValue = 0.0D; int iTarget;
/* 1121 */           for (iTarget = 0; iTarget < stat.m_NbAttrs; iTarget++) {
/*      */ 
/*      */             
/* 1124 */             if (Math.abs(stat.m_Means[iTarget] / 2.0D * getTargStd(iTarget)) > 
/* 1125 */               Math.abs(maxCompareValue)) {
/*      */               
/* 1127 */               maxCompareValue = stat.m_Means[iTarget] / 2.0D * getTargStd(iTarget);
/*      */               
/* 1129 */               scalingValue = stat.m_Means[iTarget];
/*      */               
/* 1131 */               if (getSettings().isOptNormalization())
/*      */               {
/* 1133 */                 scalingValue /= 2.0D * getTargStd(iTarget);
/*      */               }
/*      */             } 
/*      */           } 
/*      */           
/* 1138 */           if (scalingValue == 0.0D) {
/* 1139 */             scalingValue = 1.0D;
/*      */           }
/* 1141 */           for (iTarget = 0; iTarget < stat.m_NbAttrs; iTarget++)
/*      */           {
/*      */ 
/*      */ 
/*      */             
/* 1146 */             stat.m_Means[iTarget] = stat.m_Means[iTarget] / scalingValue;
/*      */             
/* 1148 */             stat.m_SumValues[iTarget] = stat.m_Means[iTarget];
/* 1149 */             stat.m_SumWeights[iTarget] = 1.0D;
/*      */           
/*      */           }
/*      */         
/*      */         }
/*      */         else {
/*      */           
/* 1156 */           if (getSettings().isOptNormalization()) {
/* 1157 */             stat.m_Means[0] = 2.0D * getTargStd(0);
/*      */           } else {
/* 1159 */             stat.m_Means[0] = 1.0D;
/* 1160 */           }  stat.m_SumValues[0] = stat.m_Means[0];
/* 1161 */           stat.m_SumWeights[0] = 1.0D;
/*      */         } 
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private double getTargStd(int iTarg) {
/* 1169 */     return RuleNormalization.getTargStdDev(iTarg);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void weightGeneralityForPredictions(int nbOfExamples) {
/* 1176 */     if (Settings.VERBOSE > 0)
/* 1177 */       System.out.println("Scaling the rule predictions for generalization weighting."); 
/* 1178 */     for (int iRule = 0; iRule < getModelSize(); iRule++) {
/*      */       
/* 1180 */       ClusRule rule = getRule(iRule);
/* 1181 */       if (rule.isRegularRule()) {
/* 1182 */         if (!(rule.m_TargetStat instanceof RegressionStat)) {
/* 1183 */           System.err.println("Error: GD optimization is implemented regression only.");
/*      */         }
/* 1185 */         RegressionStat stat = (RegressionStat)rule.m_TargetStat;
/* 1186 */         double scalingFactor = stat.m_SumWeight / nbOfExamples;
/* 1187 */         for (int iTarget = 0; iTarget < stat.m_NbAttrs; iTarget++) {
/* 1188 */           stat.m_Means[iTarget] = stat.m_Means[iTarget] * scalingFactor;
/* 1189 */           stat.m_SumValues[iTarget] = stat.m_Means[iTarget];
/* 1190 */           stat.m_SumWeights[iTarget] = 1.0D;
/*      */         } 
/*      */       } 
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
/*      */   public int addRuleSet(ClusRuleSet newRules) {
/* 1205 */     return addRuleSet(newRules, true);
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
/*      */   public int addRuleSet(ClusRuleSet newRules, boolean addOnlyUnique) {
/* 1217 */     int numberAdded = 0;
/* 1218 */     for (int iRule = 0; iRule < newRules.getModelSize(); iRule++) {
/*      */ 
/*      */       
/* 1221 */       if (addOnlyUnique) {
/*      */ 
/*      */         
/* 1224 */         if (getSettings().isRulePredictionOptimized() && getSettings().isOptOmitRulePredictions()) {
/* 1225 */           if (addIfUnique(newRules.getRule(iRule))) {
/* 1226 */             numberAdded++;
/*      */           }
/* 1228 */         } else if (addIfUniqueDeeply(newRules.getRule(iRule))) {
/* 1229 */           numberAdded++;
/*      */         } 
/*      */       } else {
/* 1232 */         this.m_Rules.add(newRules.getRule(iRule));
/* 1233 */         numberAdded++;
/*      */       } 
/*      */     } 
/* 1236 */     return numberAdded;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public double[] addDefaultRuleToRuleSet() {
/* 1244 */     if (Settings.VERBOSE > 0)
/* 1245 */       System.out.println("Adding default rule explicitly to rule set."); 
/* 1246 */     ClusRule defaultRuleForEnsembles = new ClusRule(this.m_StatManager);
/*      */     
/* 1248 */     defaultRuleForEnsembles.m_TargetStat = this.m_TargetStat;
/*      */ 
/*      */ 
/*      */     
/* 1252 */     int nbOfTargetAtts = this.m_StatManager.getStatistic(3).getNbAttributes();
/* 1253 */     double[] newPred = new double[nbOfTargetAtts];
/* 1254 */     for (int i = 0; i < nbOfTargetAtts; i++) {
/* 1255 */       newPred[i] = RuleNormalization.getTargMean(i);
/*      */     }
/* 1257 */     defaultRuleForEnsembles.setNumericPrediction(newPred);
/*      */     
/* 1259 */     this.m_Rules.add(0, defaultRuleForEnsembles);
/* 1260 */     this.m_TargetStat = null;
/* 1261 */     this.m_allCoveringRuleExists = true;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1272 */     return newPred;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void addLinearTermsToRuleSet() {
/* 1280 */     if (Settings.VERBOSE > 0) {
/* 1281 */       System.out.println("Adding linear terms as rules.");
/*      */     }
/* 1283 */     int nbTargets = this.m_StatManager.getStatistic(3).getNbAttributes();
/* 1284 */     int nbDescrAttr = (this.m_StatManager.getSchema().getNumericAttrUse(1)).length;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1290 */     for (int iDescriptDim = 0; iDescriptDim < nbDescrAttr; iDescriptDim++) {
/* 1291 */       for (int iTargetDim = 0; iTargetDim < nbTargets; iTargetDim++) {
/*      */ 
/*      */ 
/*      */         
/* 1295 */         ClusRuleLinearTerm newTerm = new ClusRuleLinearTerm(this.m_StatManager, iDescriptDim, iTargetDim);
/* 1296 */         this.m_Rules.add(newTerm);
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/* 1301 */     if (Settings.VERBOSE > 0) {
/* 1302 */       System.out.println("\tAdded " + nbDescrAttr + " linear terms for each target, total " + (nbDescrAttr * nbTargets) + " terms.");
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   private void addSingleLinearTerm(int iDescriptDim, int iTargetDim, double weight) {
/* 1308 */     ClusRuleLinearTerm newTerm = new ClusRuleLinearTerm(this.m_StatManager, iDescriptDim, iTargetDim);
/* 1309 */     newTerm.setOptWeight(weight);
/* 1310 */     this.m_Rules.add(newTerm);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void addImplicitLinearTermsExplicitly(ArrayList<Double> weights, int indFirstLinTerm) {
/* 1318 */     double threshold = getSettings().getOptRuleWeightThreshold();
/* 1319 */     int nbOfTargetAtts = this.m_StatManager.getStatistic(3).getNbAttributes();
/*      */     
/* 1321 */     int addedTerms = 0;
/*      */     
/* 1323 */     for (int iLinTerm = indFirstLinTerm; iLinTerm < weights.size(); iLinTerm++) {
/* 1324 */       if (Math.abs(((Double)weights.get(iLinTerm)).doubleValue()) >= threshold && ((Double)weights.get(iLinTerm)).doubleValue() != 0.0D) {
/* 1325 */         addSingleLinearTerm((int)Math.floor((iLinTerm - indFirstLinTerm) / nbOfTargetAtts), (iLinTerm - indFirstLinTerm) % nbOfTargetAtts, ((Double)weights
/* 1326 */             .get(iLinTerm)).doubleValue());
/* 1327 */         addedTerms++;
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/* 1332 */     if (Settings.VERBOSE > 0) {
/* 1333 */       System.out.println("\tAdded " + addedTerms + " linear terms explicitly to the set.");
/*      */     }
/* 1335 */     ClusRuleLinearTerm.DeleteImplicitLinearTerms();
/*      */   }
/*      */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\algo\rules\ClusRuleSet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */