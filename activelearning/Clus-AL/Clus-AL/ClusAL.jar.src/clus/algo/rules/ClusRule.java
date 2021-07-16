/*     */ package clus.algo.rules;
/*     */ 
/*     */ import clus.data.attweights.ClusAttributeWeights;
/*     */ import clus.data.rows.DataTuple;
/*     */ import clus.data.rows.RowData;
/*     */ import clus.data.type.NominalAttrType;
/*     */ import clus.data.type.NumericAttrType;
/*     */ import clus.error.ClusErrorList;
/*     */ import clus.ext.ilevelc.ILevelConstraint;
/*     */ import clus.main.ClusRun;
/*     */ import clus.main.ClusStatManager;
/*     */ import clus.main.Settings;
/*     */ import clus.model.ClusModel;
/*     */ import clus.model.test.NodeTest;
/*     */ import clus.statistic.ClusStatistic;
/*     */ import clus.statistic.CombStat;
/*     */ import clus.statistic.RegressionStat;
/*     */ import clus.statistic.StatisticPrintInfo;
/*     */ import clus.util.ClusException;
/*     */ import clus.util.ClusFormat;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintWriter;
/*     */ import java.io.Serializable;
/*     */ import java.text.NumberFormat;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
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
/*     */ 
/*     */ public class ClusRule
/*     */   implements ClusModel, Serializable
/*     */ {
/*     */   public static final long serialVersionUID = 1L;
/*     */   protected int m_ID;
/*     */   protected Object m_Visitor;
/*     */   protected ArrayList<ILevelConstraint> m_Constraint;
/*     */   protected ClusStatistic m_TargetStat;
/*     */   protected ClusStatistic m_ClusteringStat;
/*  57 */   protected ArrayList m_Tests = new ArrayList();
/*     */   
/*     */   protected ClusStatManager m_StatManager;
/*     */   
/*     */   protected ClusErrorList[] m_Errors;
/*  62 */   protected ArrayList m_Data = new ArrayList();
/*     */ 
/*     */   
/*  65 */   protected CombStat[] m_CombStat = new CombStat[2];
/*     */ 
/*     */ 
/*     */   
/*  69 */   protected double[] m_Coverage = new double[2];
/*     */   
/*     */   protected double m_TrainErrorScore;
/*     */   
/*     */   protected double m_OptWeight;
/*     */   
/*     */   private static final double EQUAL_MAX_DIFFER = 1.0E-6D;
/*     */   
/*     */   public ClusRule(ClusStatManager statManager) {
/*  78 */     this.m_StatManager = statManager;
/*  79 */     this.m_TrainErrorScore = -1.0D;
/*  80 */     this.m_OptWeight = -1.0D;
/*     */   }
/*     */   
/*     */   public int getID() {
/*  84 */     return this.m_ID;
/*     */   }
/*     */   
/*     */   public void setID(int id) {
/*  88 */     this.m_ID = id;
/*     */   }
/*     */ 
/*     */   
/*     */   public ClusStatistic predictWeighted(DataTuple tuple) {
/*  93 */     return this.m_TargetStat;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void computePrediction() {
/* 100 */     this.m_TargetStat.calcMean();
/* 101 */     this.m_ClusteringStat.calcMean();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public ClusRule cloneRule() {
/* 107 */     ClusRule new_rule = new ClusRule(this.m_StatManager);
/* 108 */     for (int i = 0; i < getModelSize(); i++) {
/* 109 */       new_rule.addTest(getTest(i));
/*     */     }
/* 111 */     return new_rule;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(Object other) {
/* 119 */     ClusRule o = (ClusRule)other;
/* 120 */     if (o.getModelSize() != getModelSize()) return false; 
/* 121 */     for (int i = 0; i < getModelSize(); i++) {
/* 122 */       boolean has_test = false;
/* 123 */       for (int j = 0; j < getModelSize() && !has_test; j++) {
/* 124 */         if (getTest(i).equals(o.getTest(j))) has_test = true; 
/*     */       } 
/* 126 */       if (!has_test) return false; 
/*     */     } 
/* 128 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equalsDeeply(Object other) {
/* 136 */     ClusRule o = (ClusRule)other;
/* 137 */     if (o.getModelSize() != getModelSize()) return false; 
/* 138 */     for (int i = 0; i < getModelSize(); i++) {
/* 139 */       boolean has_test = false;
/* 140 */       for (int j = 0; j < getModelSize() && !has_test; j++) {
/* 141 */         if (getTest(i).equals(o.getTest(j))) has_test = true; 
/*     */       } 
/* 143 */       if (!has_test) return false;
/*     */     
/*     */     } 
/*     */     
/* 147 */     double[] ruleTarget = ((RegressionStat)o.m_TargetStat).m_Means;
/*     */ 
/*     */     
/* 150 */     boolean targetsAreSimilar = true;
/* 151 */     for (int iTarget = 0; iTarget < ruleTarget.length && targetsAreSimilar; iTarget++) {
/*     */       
/* 153 */       if (Math.abs(ruleTarget[iTarget] - ((RegressionStat)this.m_TargetStat).m_Means[iTarget]) >= 1.0E-6D) {
/* 154 */         targetsAreSimilar = false;
/*     */       }
/*     */     } 
/* 157 */     return targetsAreSimilar;
/*     */   }
/*     */   
/*     */   public int hashCode() {
/* 161 */     int hashCode = 1234;
/* 162 */     for (int i = 0; i < getModelSize(); i++) {
/* 163 */       hashCode += getTest(i).hashCode();
/*     */     }
/* 165 */     return hashCode;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean covers(DataTuple tuple) {
/* 170 */     for (int i = 0; i < getModelSize(); i++) {
/* 171 */       NodeTest test = getTest(i);
/* 172 */       int res = test.predictWeighted(tuple);
/* 173 */       if (res != 0) return false; 
/*     */     } 
/* 175 */     return true;
/*     */   }
/*     */   
/*     */   public void simplify() {
/* 179 */     for (int i = getModelSize() - 1; i >= 0; i--) {
/* 180 */       boolean found = false;
/* 181 */       NodeTest test_i = getTest(i);
/* 182 */       for (int j = 0; j < i && !found; j++) {
/* 183 */         NodeTest test_j = getTest(j);
/* 184 */         NodeTest simplify = test_j.simplifyConjunction(test_i);
/* 185 */         if (simplify != null) {
/* 186 */           setTest(j, simplify);
/* 187 */           found = true;
/*     */         } 
/*     */       } 
/* 190 */       if (found) removeTest(i); 
/*     */     } 
/*     */   }
/*     */   
/*     */   public RowData removeCovered(RowData data) {
/* 195 */     int covered = 0;
/* 196 */     for (int i = 0; i < data.getNbRows(); i++) {
/* 197 */       DataTuple tuple = data.getTuple(i);
/* 198 */       if (covers(tuple)) covered++; 
/*     */     } 
/* 200 */     int idx = 0;
/* 201 */     RowData res = new RowData(data.getSchema(), data.getNbRows() - covered);
/* 202 */     for (int j = 0; j < data.getNbRows(); j++) {
/* 203 */       DataTuple tuple = data.getTuple(j);
/*     */       
/* 205 */       if (!covers(tuple)) {
/* 206 */         res.setTuple(tuple, idx++);
/*     */       }
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 216 */     return res;
/*     */   }
/*     */   
/*     */   public RowData computeCovered(RowData data) {
/* 220 */     int covered = 0;
/* 221 */     for (int i = 0; i < data.getNbRows(); i++) {
/* 222 */       DataTuple tuple = data.getTuple(i);
/* 223 */       if (covers(tuple)) covered++; 
/*     */     } 
/* 225 */     int idx = 0;
/* 226 */     RowData res = new RowData(data.getSchema(), covered);
/* 227 */     for (int j = 0; j < data.getNbRows(); j++) {
/* 228 */       DataTuple tuple = data.getTuple(j);
/* 229 */       if (covers(tuple)) res.setTuple(tuple, idx++); 
/*     */     } 
/* 231 */     return res;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public RowData removeCoveredEnough(RowData data) {
/* 239 */     double threshold = getSettings().getInstCoveringWeightThreshold();
/*     */ 
/*     */     
/* 242 */     double covered = 0.0D;
/* 243 */     for (int i = 0; i < data.getNbRows(); i++) {
/* 244 */       DataTuple tuple = data.getTuple(i);
/* 245 */       if (tuple.m_Weight < threshold) {
/* 246 */         covered++;
/*     */       }
/*     */     } 
/* 249 */     int idx = 0;
/*     */     
/* 251 */     RowData res = new RowData(data.getSchema(), (int)(data.getNbRows() - covered));
/* 252 */     for (int j = 0; j < data.getNbRows(); j++) {
/* 253 */       DataTuple tuple = data.getTuple(j);
/* 254 */       if (tuple.m_Weight >= threshold) {
/* 255 */         res.setTuple(tuple, idx++);
/*     */       }
/*     */     } 
/* 258 */     return res;
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
/*     */   public RowData reweighCovered(RowData data) throws ClusException {
/* 275 */     int cov_method = getSettings().getCoveringMethod();
/* 276 */     double cov_w_par = getSettings().getCoveringWeight();
/* 277 */     int nb_rows = data.getNbRows();
/* 278 */     RowData result = new RowData(data.getSchema(), nb_rows);
/*     */     
/* 280 */     if (cov_w_par >= 1.0D || cov_w_par < 0.0D) {
/* 281 */       throw new ClusException("Weighted covering: covering weight should be between 0 and 1!");
/*     */     }
/* 283 */     for (int i = 0; i < data.getNbRows(); i++) {
/* 284 */       double new_weight; DataTuple tuple = data.getTuple(i);
/* 285 */       double old_weight = tuple.getWeight();
/* 286 */       if (cov_method == 2) {
/* 287 */         new_weight = cov_w_par * old_weight / (old_weight + 1.0D);
/* 288 */       } else if (cov_method == 1) {
/* 289 */         if (cov_w_par == 1.0D) {
/* 290 */           throw new ClusException("Multiplicative weighted covering: covering weight should not be 1!");
/*     */         }
/* 292 */         new_weight = old_weight * cov_w_par;
/*     */ 
/*     */ 
/*     */       
/*     */       }
/* 297 */       else if (this.m_TargetStat instanceof clus.statistic.ClassificationStat) {
/* 298 */         int[] predictions = predictWeighted(tuple).getNominalPred();
/* 299 */         NominalAttrType[] targetAttrs = data.getSchema().getNominalAttrUse(3);
/* 300 */         if (predictions.length > 1) {
/* 301 */           double prop_true = 0.0D;
/* 302 */           for (int j = 0; j < predictions.length; j++) {
/* 303 */             int true_value = targetAttrs[j].getNominal(tuple);
/* 304 */             if (predictions[j] == true_value) {
/* 305 */               prop_true++;
/*     */             }
/*     */           } 
/* 308 */           prop_true = (prop_true != 0.0D) ? (prop_true / predictions.length) : 0.0D;
/* 309 */           new_weight = old_weight * (1.0D + prop_true * (cov_w_par - 1.0D));
/*     */         } else {
/* 311 */           int prediction = predictions[0];
/* 312 */           int true_value = targetAttrs[0].getNominal(tuple);
/* 313 */           if (prediction == true_value) {
/* 314 */             new_weight = old_weight * cov_w_par;
/*     */           } else {
/* 316 */             new_weight = old_weight;
/*     */           } 
/*     */         } 
/* 319 */       } else if (ClusStatManager.getMode() == 2) {
/* 320 */         ClusStatistic prediction = predictWeighted(tuple);
/* 321 */         ClusAttributeWeights weights = this.m_StatManager.getClusteringWeights();
/* 322 */         double coef = cov_w_par * prediction.getAbsoluteDistance(tuple, weights);
/* 323 */         if (coef > 1.0D) {
/* 324 */           coef = 1.0D;
/*     */         }
/* 326 */         new_weight = old_weight * coef;
/*     */       }
/* 328 */       else if (this.m_TargetStat instanceof RegressionStat) {
/* 329 */         double[] predictions = predictWeighted(tuple).getNumericPred();
/* 330 */         NumericAttrType[] targetAttrs = data.getSchema().getNumericAttrUse(3);
/* 331 */         if (predictions.length > 1) {
/* 332 */           double[] true_values = new double[predictions.length];
/* 333 */           ClusStatistic stat = this.m_StatManager.getTrainSetStat();
/* 334 */           double[] variance = new double[predictions.length];
/* 335 */           double[] coef = new double[predictions.length];
/* 336 */           for (int j = 0; j < true_values.length; j++) {
/* 337 */             true_values[j] = targetAttrs[j].getNumeric(tuple);
/* 338 */             variance[j] = ((CombStat)stat).getRegressionStat().getVariance(j);
/* 339 */             coef[j] = cov_w_par * Math.abs(predictions[j] - true_values[j]) / Math.sqrt(variance[j]);
/*     */           } 
/* 341 */           double mean_coef = 0.0D;
/* 342 */           for (int k = 0; k < true_values.length; k++) {
/* 343 */             mean_coef += coef[k];
/*     */           }
/* 345 */           mean_coef /= coef.length;
/* 346 */           if (mean_coef > 1.0D) {
/* 347 */             mean_coef = 1.0D;
/*     */           }
/* 349 */           new_weight = old_weight * mean_coef;
/*     */         } else {
/* 351 */           double prediction = predictions[0];
/* 352 */           double true_value = targetAttrs[0].getNumeric(tuple);
/* 353 */           ClusStatistic stat = this.m_StatManager.getTrainSetStat();
/* 354 */           double variance = ((CombStat)stat).getRegressionStat().getVariance(0);
/* 355 */           double coef = cov_w_par * Math.abs(prediction - true_value) / Math.sqrt(variance);
/* 356 */           if (coef > 1.0D) {
/* 357 */             coef = 1.0D;
/*     */           }
/* 359 */           new_weight = old_weight * coef;
/*     */         } 
/* 361 */       } else if (ClusStatManager.getMode() == 5) {
/* 362 */         ClusStatistic prediction = predictWeighted(tuple);
/* 363 */         ClusAttributeWeights weights = this.m_StatManager.getClusteringWeights();
/* 364 */         double coef = cov_w_par * prediction.getAbsoluteDistance(tuple, weights);
/* 365 */         if (coef > 1.0D) {
/* 366 */           coef = 1.0D;
/*     */         }
/* 368 */         new_weight = old_weight * coef;
/*     */       } else {
/* 370 */         throw new ClusException("reweighCovered(): Unsupported mode!");
/*     */       } 
/*     */       
/* 373 */       if (covers(tuple)) {
/* 374 */         result.setTuple(tuple.changeWeight(new_weight), i);
/*     */       } else {
/* 376 */         result.setTuple(tuple, i);
/*     */       } 
/*     */     } 
/* 379 */     return removeCoveredEnough(result);
/*     */   }
/*     */   
/*     */   public void setVisitor(Object visitor) {
/* 383 */     this.m_Visitor = visitor;
/*     */   }
/*     */   
/*     */   public Object getVisitor() {
/* 387 */     return this.m_Visitor;
/*     */   }
/*     */   
/*     */   public void setConstraints(ArrayList<ILevelConstraint> constraints) {
/* 391 */     this.m_Constraint = constraints;
/*     */   }
/*     */   
/*     */   public ArrayList<ILevelConstraint> getConstraints() {
/* 395 */     return this.m_Constraint;
/*     */   }
/*     */   
/*     */   public int getNumberOfViolatedConstraints() {
/* 399 */     int count = 0;
/* 400 */     Iterator<ILevelConstraint> i = this.m_Constraint.iterator();
/* 401 */     while (i.hasNext()) {
/* 402 */       ILevelConstraint ilc = i.next();
/* 403 */       DataTuple t1 = ilc.getT1();
/* 404 */       DataTuple t2 = ilc.getT2();
/* 405 */       if (ilc.getType() == 0) {
/*     */         
/* 407 */         if (!this.m_Data.contains(t1) || !this.m_Data.contains(t2))
/* 408 */           count++;  continue;
/*     */       } 
/* 410 */       if (this.m_Data.contains(t1) && this.m_Data.contains(t2)) {
/* 411 */         count++;
/*     */       }
/*     */     } 
/* 414 */     return count;
/*     */   }
/*     */   
/*     */   public int getNumberOfViolatedConstraintsRCCC() {
/* 418 */     int count = 0;
/* 419 */     Iterator<ILevelConstraint> i = this.m_Constraint.iterator();
/* 420 */     ArrayList<DataTuple> data = ((RowData)getVisitor()).toArrayList();
/* 421 */     while (i.hasNext()) {
/* 422 */       ILevelConstraint ilc = i.next();
/* 423 */       DataTuple t1 = ilc.getT1();
/* 424 */       DataTuple t2 = ilc.getT2();
/* 425 */       if (ilc.getType() == 0) {
/*     */         
/* 427 */         if (!data.contains(t1) || !data.contains(t2))
/* 428 */           count++; 
/*     */         continue;
/*     */       } 
/* 431 */       if (data.contains(t1) && data.contains(t2)) {
/* 432 */         count++;
/*     */       }
/*     */     } 
/* 435 */     return count;
/*     */   }
/*     */ 
/*     */   
/*     */   public ArrayList<ILevelConstraint> getViolatedConstraintsRCCC() {
/* 440 */     Iterator<ILevelConstraint> i = this.m_Constraint.iterator();
/* 441 */     ArrayList<DataTuple> data = ((RowData)getVisitor()).toArrayList();
/* 442 */     ArrayList<ILevelConstraint> c = new ArrayList<>();
/* 443 */     while (i.hasNext()) {
/* 444 */       ILevelConstraint ilc = i.next();
/* 445 */       DataTuple t1 = ilc.getT1();
/* 446 */       DataTuple t2 = ilc.getT2();
/* 447 */       if (ilc.getType() == 0) {
/*     */         
/* 449 */         if (!data.contains(t1) || !data.contains(t2))
/* 450 */           c.add(ilc); 
/*     */         continue;
/*     */       } 
/* 453 */       if (data.contains(t1) && data.contains(t2)) {
/* 454 */         c.add(ilc);
/*     */       }
/*     */     } 
/* 457 */     return c;
/*     */   }
/*     */ 
/*     */   
/*     */   public void applyModelProcessors(DataTuple tuple, MyArray mproc) throws IOException {}
/*     */   
/*     */   public void attachModel(HashMap table) throws ClusException {
/* 464 */     for (int i = 0; i < this.m_Tests.size(); i++) {
/* 465 */       NodeTest test = this.m_Tests.get(i);
/* 466 */       test.attachModel(table);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void printModel() {
/* 471 */     PrintWriter wrt = new PrintWriter(System.out);
/* 472 */     printModel(wrt);
/* 473 */     wrt.flush();
/*     */   }
/*     */   
/*     */   public void printModel(PrintWriter wrt) {
/* 477 */     printModel(wrt, StatisticPrintInfo.getInstance());
/*     */   }
/*     */   
/*     */   public void printModel(PrintWriter wrt, StatisticPrintInfo info) {
/* 481 */     wrt.print("IF ");
/* 482 */     if (this.m_Tests.size() == 0) {
/* 483 */       wrt.print("true");
/*     */     } else {
/* 485 */       for (int i = 0; i < this.m_Tests.size(); i++) {
/* 486 */         NodeTest test = this.m_Tests.get(i);
/* 487 */         if (i != 0) {
/* 488 */           wrt.println(" AND");
/* 489 */           wrt.print("   ");
/*     */         } 
/* 491 */         wrt.print(test.getString());
/*     */       } 
/*     */     } 
/* 494 */     wrt.println();
/*     */     
/* 496 */     wrt.print("THEN " + this.m_TargetStat.getString(info));
/*     */     
/* 498 */     if (getID() != 0 && info.SHOW_INDEX) { wrt.println(" (" + getID() + ")"); }
/* 499 */     else { wrt.println(); }
/* 500 */      String extra = this.m_TargetStat.getExtraInfo();
/* 501 */     if (extra != null) {
/*     */       
/* 503 */       wrt.println();
/* 504 */       wrt.print(extra);
/*     */     } 
/*     */     
/* 507 */     commonPrintForRuleTypes(wrt, info);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void commonPrintForRuleTypes(PrintWriter wrt, StatisticPrintInfo info) {
/* 512 */     NumberFormat fr = ClusFormat.SIX_AFTER_DOT;
/* 513 */     if (getSettings().isRulePredictionOptimized()) {
/* 514 */       wrt.println("\n   Rule weight        : " + fr.format(getOptWeight()));
/*     */     }
/* 516 */     if (getSettings().computeDispersion() && this.m_CombStat[0] != null) {
/*     */       
/* 518 */       wrt.println("   Dispersion (train): " + this.m_CombStat[0].getDispersionString());
/* 519 */       wrt.println("   Coverage   (train): " + fr.format(this.m_Coverage[0]));
/* 520 */       wrt.println("   Cover*Disp (train): " + fr.format(this.m_CombStat[0].dispersionCalc() * this.m_Coverage[0]));
/* 521 */       if (this.m_CombStat[1] != null) {
/* 522 */         wrt.println("   Dispersion (test):  " + this.m_CombStat[1].getDispersionString());
/* 523 */         wrt.println("   Coverage   (test):  " + fr.format(this.m_Coverage[1]));
/* 524 */         wrt.println("   Cover*Disp (test):  " + fr.format(this.m_CombStat[1].dispersionCalc() * this.m_Coverage[1]));
/*     */       } 
/*     */     } 
/* 527 */     if (hasErrors()) {
/*     */       
/* 529 */       ClusErrorList train_err = getError(0);
/* 530 */       if (train_err != null) {
/* 531 */         wrt.println();
/* 532 */         wrt.println("Training error");
/* 533 */         train_err.showError(wrt);
/*     */       } 
/* 535 */       ClusErrorList test_err = getError(1);
/* 536 */       if (test_err != null) {
/* 537 */         wrt.println();
/* 538 */         wrt.println("Testing error");
/* 539 */         test_err.showError(wrt);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void printModelToPythonScript(PrintWriter wrt) {}
/*     */ 
/*     */   
/*     */   public void printModelToQuery(PrintWriter wrt, ClusRun cr, int starttree, int startitem, boolean ex) {}
/*     */ 
/*     */   
/*     */   public void printModelAndExamples(PrintWriter wrt, StatisticPrintInfo info, RowData examples) {}
/*     */   
/*     */   public Settings getSettings() {
/* 554 */     return this.m_StatManager.getSettings();
/*     */   }
/*     */   
/*     */   public boolean isEmpty() {
/* 558 */     return (getModelSize() == 0);
/*     */   }
/*     */   
/*     */   public int getModelSize() {
/* 562 */     return this.m_Tests.size();
/*     */   }
/*     */   
/*     */   public NodeTest getTest(int i) {
/* 566 */     return this.m_Tests.get(i);
/*     */   }
/*     */   
/*     */   public void setTest(int i, NodeTest test) {
/* 570 */     this.m_Tests.set(i, test);
/*     */   }
/*     */   
/*     */   public void addTest(NodeTest test) {
/* 574 */     this.m_Tests.add(test);
/*     */   }
/*     */   
/*     */   public void removeTest(int i) {
/* 578 */     this.m_Tests.remove(i);
/*     */   }
/*     */   
/*     */   public double[] getCoverage() {
/* 582 */     return this.m_Coverage;
/*     */   }
/*     */   
/*     */   public void setCoverage(double[] coverage) {
/* 586 */     this.m_Coverage = coverage;
/*     */   }
/*     */   
/*     */   public ClusStatistic getTargetStat() {
/* 590 */     return this.m_TargetStat;
/*     */   }
/*     */   
/*     */   public ClusStatistic getClusteringStat() {
/* 594 */     return this.m_ClusteringStat;
/*     */   }
/*     */   
/*     */   public void setTargetStat(ClusStatistic stat) {
/* 598 */     this.m_TargetStat = stat;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setNumericPrediction(double[] newPred) {
/* 603 */     RegressionStat stat = (RegressionStat)getTargetStat();
/*     */     
/* 605 */     for (int iTarget = 0; iTarget < stat.m_NbAttrs; iTarget++) {
/* 606 */       stat.m_Means[iTarget] = newPred[iTarget];
/* 607 */       stat.m_SumValues[iTarget] = stat.m_Means[iTarget];
/* 608 */       stat.m_SumWeights[iTarget] = 1.0D;
/*     */     } 
/* 610 */     setTargetStat((ClusStatistic)stat);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setClusteringStat(ClusStatistic stat) {
/* 615 */     this.m_ClusteringStat = stat;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void postProc() {
/* 624 */     this.m_TargetStat.calcMean();
/*     */   }
/*     */   
/*     */   public String getModelInfo() {
/* 628 */     return "Tests = " + getModelSize();
/*     */   }
/*     */   
/*     */   public double getTrainErrorScore() {
/* 632 */     if (this.m_TrainErrorScore != -1.0D) {
/* 633 */       return this.m_TrainErrorScore;
/*     */     }
/* 635 */     System.err.println("getTrainErrorScore(): Error score not initialized!");
/* 636 */     return Double.POSITIVE_INFINITY;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setTrainErrorScore() {
/* 646 */     int nb_rows = this.m_Data.size();
/* 647 */     int nb_tar = this.m_TargetStat.getNbAttributes();
/* 648 */     if (this.m_TargetStat instanceof clus.statistic.ClassificationStat) {
/* 649 */       int[] true_counts = new int[nb_tar];
/* 650 */       NominalAttrType[] targetAttrs = this.m_StatManager.getSchema().getNominalAttrUse(3);
/* 651 */       for (int i = 0; i < nb_rows; i++) {
/* 652 */         DataTuple tuple = this.m_Data.get(i);
/* 653 */         int[] prediction = predictWeighted(tuple).getNominalPred();
/* 654 */         for (int k = 0; k < nb_tar; k++) {
/* 655 */           if (prediction[k] == targetAttrs[k].getNominal(tuple)) {
/* 656 */             true_counts[k] = true_counts[k] + 1;
/*     */           }
/*     */         } 
/*     */       } 
/* 660 */       double sum_err = 0.0D;
/* 661 */       for (int j = 0; j < nb_tar; j++) {
/* 662 */         sum_err += (nb_rows - true_counts[j]) / nb_rows;
/*     */       }
/* 664 */       this.m_TrainErrorScore = sum_err / nb_tar;
/* 665 */     } else if (ClusStatManager.getMode() == 2) {
/*     */       
/* 667 */       double sum_diff = 0.0D;
/* 668 */       ClusAttributeWeights weight = this.m_StatManager.getClusteringWeights();
/* 669 */       for (int i = 0; i < nb_rows; i++) {
/* 670 */         DataTuple tuple = this.m_Data.get(i);
/* 671 */         ClusStatistic prediction = predictWeighted(tuple);
/* 672 */         sum_diff = prediction.getAbsoluteDistance(tuple, weight);
/*     */       } 
/* 674 */       this.m_TrainErrorScore = sum_diff / nb_tar;
/* 675 */       if (this.m_TrainErrorScore > 1.0D) {
/* 676 */         this.m_TrainErrorScore = 1.0D;
/*     */       }
/* 678 */     } else if (this.m_TargetStat instanceof RegressionStat) {
/* 679 */       double norm = getSettings().getVarBasedDispNormWeight();
/* 680 */       ClusStatistic stat = this.m_StatManager.getTrainSetStat();
/* 681 */       NumericAttrType[] targetAttrs = this.m_StatManager.getSchema().getNumericAttrUse(3);
/* 682 */       int[] target_idx = new int[nb_tar];
/* 683 */       double[] variance = new double[nb_tar];
/* 684 */       double[] diff = new double[nb_tar];
/* 685 */       for (int j = 0; j < nb_tar; j++) {
/* 686 */         target_idx[j] = targetAttrs[j].getArrayIndex();
/* 687 */         variance[j] = ((CombStat)stat).getRegressionStat().getVariance(target_idx[j]);
/*     */       } 
/* 689 */       for (int i = 0; i < nb_rows; i++) {
/* 690 */         DataTuple tuple = this.m_Data.get(i);
/* 691 */         double[] prediction = predictWeighted(tuple).getNumericPred();
/* 692 */         for (int m = 0; m < nb_tar; m++) {
/* 693 */           diff[m] = diff[m] + Math.abs(prediction[m] - targetAttrs[m].getNumeric(tuple));
/*     */         }
/*     */       } 
/* 696 */       double sum_diff = 0.0D;
/* 697 */       for (int k = 0; k < nb_tar; k++) {
/* 698 */         sum_diff += diff[k] / nb_rows / Math.sqrt(variance[k]) / norm * norm;
/*     */       }
/* 700 */       this.m_TrainErrorScore = sum_diff / nb_tar;
/* 701 */       if (this.m_TrainErrorScore > 1.0D) {
/* 702 */         this.m_TrainErrorScore = 1.0D;
/*     */       }
/* 704 */     } else if (ClusStatManager.getMode() == 5) {
/* 705 */       double sum_diff = 0.0D;
/* 706 */       ClusAttributeWeights weight = this.m_StatManager.getClusteringWeights();
/*     */       
/* 708 */       for (int i = 0; i < nb_rows; i++) {
/* 709 */         DataTuple tuple = this.m_Data.get(i);
/* 710 */         ClusStatistic prediction = predictWeighted(tuple);
/* 711 */         sum_diff = prediction.getAbsoluteDistance(tuple, weight);
/*     */       } 
/* 713 */       this.m_TrainErrorScore = sum_diff / nb_tar;
/* 714 */       if (this.m_TrainErrorScore > 1.0D) {
/* 715 */         this.m_TrainErrorScore = 1.0D;
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   public double getOptWeight() {
/* 721 */     if (this.m_OptWeight != -1.0D) {
/* 722 */       return this.m_OptWeight;
/*     */     }
/* 724 */     System.err.println("Warning: Optimal rule weight not initialized!");
/* 725 */     return -1.0D;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setOptWeight(double weight) {
/* 730 */     this.m_OptWeight = weight;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void computeDispersion(int mode) {
/* 738 */     CombStat combStat = (CombStat)this.m_StatManager.createStatistic(0);
/* 739 */     for (int i = 0; i < this.m_Data.size(); i++) {
/* 740 */       combStat.updateWeighted(this.m_Data.get(i), 0);
/*     */     }
/* 742 */     combStat.calcMean();
/* 743 */     this.m_CombStat[mode] = combStat;
/*     */     
/* 745 */     this.m_Coverage[mode] = this.m_Data.size();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addDataTuple(DataTuple tuple) {
/* 753 */     this.m_Data.add(tuple);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void removeDataTuples() {
/* 760 */     this.m_Data.clear();
/*     */   }
/*     */   
/*     */   public ArrayList getData() {
/* 764 */     return this.m_Data;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setError(ClusErrorList error, int subset) {
/* 771 */     if (this.m_Errors == null) {
/* 772 */       this.m_Errors = new ClusErrorList[2];
/*     */     }
/* 774 */     this.m_Errors[subset] = error;
/*     */   }
/*     */   
/*     */   public void addError(ClusErrorList error, int subset) {
/* 778 */     if (this.m_Errors == null) {
/* 779 */       setError(error, subset);
/*     */     } else {
/* 781 */       this.m_Errors[subset].addErrors(error);
/*     */     } 
/*     */   }
/*     */   
/*     */   public ClusErrorList getError(int subset) {
/* 786 */     if (this.m_Errors == null) return null; 
/* 787 */     return this.m_Errors[subset];
/*     */   }
/*     */   
/*     */   public boolean hasErrors() {
/* 791 */     return (this.m_Errors != null);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean hasPrediction() {
/* 796 */     return this.m_TargetStat.isValidPrediction();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isRegularRule() {
/* 805 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public void computeCoverStat(RowData data, ClusStatistic stat) {
/* 810 */     int nb = data.getNbRows();
/* 811 */     stat.setSDataSize(nb);
/* 812 */     for (int i = 0; i < nb; i++) {
/* 813 */       DataTuple tuple = data.getTuple(i);
/* 814 */       if (covers(tuple)) {
/* 815 */         stat.updateWeighted(tuple, i);
/*     */       }
/*     */     } 
/* 818 */     stat.optimizePreCalc(data);
/*     */   }
/*     */   
/*     */   public ClusModel prune(int prunetype) {
/* 822 */     return this;
/*     */   }
/*     */   
/*     */   public void retrieveStatistics(ArrayList list) {}
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\algo\rules\ClusRule.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */