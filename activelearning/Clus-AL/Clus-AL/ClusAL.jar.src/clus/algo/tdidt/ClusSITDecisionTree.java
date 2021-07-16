/*     */ package clus.algo.tdidt;
/*     */ 
/*     */ import clus.algo.ClusInductionAlgorithmType;
/*     */ import clus.data.ClusData;
/*     */ import clus.data.type.NominalAttrType;
/*     */ import clus.data.type.NumericAttrType;
/*     */ import clus.error.Accuracy;
/*     */ import clus.error.ClusError;
/*     */ import clus.error.ClusErrorList;
/*     */ import clus.error.ClusErrorOutput;
/*     */ import clus.error.PearsonCorrelation;
/*     */ import clus.ext.hierarchical.HierClassWiseAccuracy;
/*     */ import clus.main.ClusRun;
/*     */ import clus.main.ClusStatManager;
/*     */ import clus.main.ClusSummary;
/*     */ import clus.main.Settings;
/*     */ import clus.model.ClusModel;
/*     */ import clus.model.ClusModelInfo;
/*     */ import clus.selection.ClusSelection;
/*     */ import clus.selection.XValMainSelection;
/*     */ import clus.selection.XValRandomSelection;
/*     */ import clus.selection.XValSelection;
/*     */ import clus.util.ClusException;
/*     */ import java.io.IOException;
/*     */ import java.util.Random;
/*     */ import jeans.resource.ResourceInfo;
/*     */ import jeans.util.IntervalCollection;
/*     */ 
/*     */ public class ClusSITDecisionTree extends ClusDecisionTree {
/*     */   public ClusSITDecisionTree(ClusInductionAlgorithmType clss) {
/*  31 */     super(clss.getClus());
/*  32 */     this.m_Class = clss;
/*     */   }
/*     */   protected ClusInductionAlgorithmType m_Class;
/*     */   public void printInfo() {
/*  36 */     System.out.println("---------SIT---------");
/*  37 */     System.out.println("Heuristic: " + getStatManager().getHeuristicName());
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public ClusErrorList createTuneError(ClusStatManager mgr) {
/*  43 */     ClusErrorList parent = new ClusErrorList();
/*  44 */     if (ClusStatManager.getMode() == 2) {
/*  45 */       parent.addError((ClusError)new HierClassWiseAccuracy(parent, mgr.getHier()));
/*  46 */       return parent;
/*     */     } 
/*  48 */     NumericAttrType[] num = mgr.getSchema().getNumericAttrUse(3);
/*  49 */     NominalAttrType[] nom = mgr.getSchema().getNominalAttrUse(3);
/*  50 */     if (nom.length != 0) {
/*  51 */       parent.addError((ClusError)new Accuracy(parent, nom));
/*     */     }
/*  53 */     if (num.length != 0) {
/*  54 */       parent.addError((ClusError)new PearsonCorrelation(parent, num));
/*     */     }
/*     */     
/*  57 */     return parent;
/*     */   }
/*     */   private final void showFold(int i, XValMainSelection sel) {
/*  60 */     if (i != 0) System.out.print(" "); 
/*  61 */     System.out.print(String.valueOf(i + 1));
/*     */     
/*  63 */     System.out.flush();
/*     */   }
/*     */   
/*     */   public ClusError doParamXVal(ClusData trset, ClusData pruneset) throws ClusException, IOException {
/*  67 */     int prevVerb = Settings.enableVerbose(0);
/*  68 */     ClusStatManager mgr = getStatManager();
/*  69 */     ClusSummary summ = new ClusSummary();
/*  70 */     summ.setTestError(createTuneError(mgr));
/*     */ 
/*     */     
/*  73 */     Random random = new Random(0L);
/*     */     
/*  75 */     int nbfolds = 10;
/*  76 */     XValRandomSelection xValRandomSelection = new XValRandomSelection(trset.getNbRows(), nbfolds, random);
/*     */     
/*  78 */     for (int i = 0; i < nbfolds; i++) {
/*  79 */       showFold(i, (XValMainSelection)xValRandomSelection);
/*  80 */       XValSelection msel = new XValSelection((XValMainSelection)xValRandomSelection, i);
/*  81 */       ClusRun cr = this.m_Clus.partitionDataBasic(trset, (ClusSelection)msel, pruneset, summ, i + 1);
/*  82 */       ClusModel pruned = this.m_Class.induceSingle(cr);
/*  83 */       cr.addModelInfo(2).setModel(pruned);
/*  84 */       this.m_Clus.calcError(cr, summ, null);
/*     */     } 
/*     */     
/*  87 */     ClusModelInfo mi = summ.getModelInfo(2);
/*  88 */     Settings.enableVerbose(prevVerb);
/*     */     
/*  90 */     ClusErrorList err_list = mi.getTestError();
/*  91 */     ClusError err = err_list.getFirstError();
/*  92 */     System.out.println();
/*     */     
/*  94 */     return err;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void resetWeights(int main_target) {
/* 101 */     resetWeights();
/* 102 */     (getStatManager().getClusteringWeights()).m_Weights[main_target] = 1.0D;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void resetWeights() {
/* 108 */     ClusStatManager mgr = getStatManager();
/* 109 */     double[] weights = (mgr.getClusteringWeights()).m_Weights;
/*     */     
/* 111 */     for (int i = 0; i < weights.length; i++) {
/* 112 */       weights[i] = 0.0D;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private double addBestSupportTasks(double[] weights, int emc, int[] support_range, ClusData trset, ClusData pruneset) throws ClusException, IOException {
/* 120 */     ClusStatManager mgr = getStatManager();
/*     */     
/* 122 */     double[] best_weights = (double[])weights.clone();
/*     */     
/* 124 */     ClusError err = doParamXVal(trset, pruneset);
/* 125 */     double best_err = err.getModelErrorComponent(emc);
/* 126 */     System.out.print("Current best Target error: " + best_err + " for targets ");
/* 127 */     for (int k = 0; k < weights.length; k++) {
/* 128 */       if (best_weights[k] == 1.0D) {
/* 129 */         System.out.print((k + 1) + " ");
/*     */       }
/*     */     } 
/* 132 */     System.out.println();
/*     */ 
/*     */     
/* 135 */     for (int i = support_range[0]; i <= support_range[1]; i++) {
/* 136 */       (mgr.getClusteringWeights()).m_Weights = (double[])weights.clone();
/*     */       
/* 138 */       if ((mgr.getClusteringWeights()).m_Weights[i] != 1.0D) {
/* 139 */         (mgr.getClusteringWeights()).m_Weights[i] = 1.0D;
/*     */ 
/*     */ 
/*     */         
/* 143 */         System.out.print("Testing targets: ");
/* 144 */         for (int m = 0; m < weights.length; m++) {
/* 145 */           if ((mgr.getClusteringWeights()).m_Weights[m] == 1.0D) {
/* 146 */             System.out.print((m + 1) + " ");
/*     */           }
/*     */         } 
/* 149 */         System.out.println();
/* 150 */         err = doParamXVal(trset, pruneset);
/* 151 */         System.out.println("Correlation: " + err.getModelErrorComponent(emc));
/* 152 */         if (err.getModelErrorComponent(emc) > best_err) {
/* 153 */           best_err = err.getModelErrorComponent(emc);
/* 154 */           best_weights = (double[])(mgr.getClusteringWeights()).m_Weights.clone();
/*     */         } 
/* 156 */         System.out.println();
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 161 */     System.out.println("Best error: " + best_err);
/* 162 */     System.out.print("Best targets:");
/* 163 */     for (int j = 0; j < weights.length; j++) {
/* 164 */       if (best_weights[j] == 1.0D) {
/* 165 */         System.out.print((j + 1) + " ");
/*     */       }
/*     */     } 
/* 168 */     System.out.println();
/*     */     
/* 170 */     (mgr.getClusteringWeights()).m_Weights = best_weights;
/*     */     
/* 172 */     return best_err;
/*     */   }
/*     */   
/*     */   private double substractBestSupportTasks(double[] weights, int emc, int[] support_range, ClusData trset, ClusData pruneset) throws ClusException, IOException {
/* 176 */     ClusStatManager mgr = getStatManager();
/*     */     
/* 178 */     double[] best_weights = (double[])weights.clone();
/*     */     
/* 180 */     ClusError err = doParamXVal(trset, pruneset);
/* 181 */     double best_err = err.getModelErrorComponent(emc);
/* 182 */     System.out.print("Current best Target error: " + best_err + " for targets ");
/* 183 */     for (int k = 0; k < weights.length; k++) {
/* 184 */       if (best_weights[k] == 1.0D) {
/* 185 */         System.out.print((k + 1) + " ");
/*     */       }
/*     */     } 
/* 188 */     System.out.println();
/*     */ 
/*     */ 
/*     */     
/* 192 */     for (int i = support_range[0]; i <= support_range[1]; i++) {
/* 193 */       (mgr.getClusteringWeights()).m_Weights = (double[])weights.clone();
/*     */       
/* 195 */       if ((mgr.getClusteringWeights()).m_Weights[i] != 0.0D) {
/* 196 */         (mgr.getClusteringWeights()).m_Weights[i] = 0.0D;
/*     */ 
/*     */ 
/*     */         
/* 200 */         System.out.print("Testing targets: ");
/* 201 */         for (int m = 0; m < weights.length; m++) {
/* 202 */           if ((mgr.getClusteringWeights()).m_Weights[m] == 1.0D) {
/* 203 */             System.out.print((m + 1) + " ");
/*     */           }
/*     */         } 
/* 206 */         System.out.println();
/* 207 */         err = doParamXVal(trset, pruneset);
/* 208 */         System.out.println("Correlation: " + err.getModelErrorComponent(emc));
/* 209 */         if (err.getModelErrorComponent(emc) > best_err) {
/* 210 */           best_err = err.getModelErrorComponent(emc);
/* 211 */           best_weights = (double[])(mgr.getClusteringWeights()).m_Weights.clone();
/*     */         } 
/* 213 */         System.out.println();
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 218 */     System.out.println("Best error: " + best_err);
/* 219 */     System.out.print("Best targets:");
/* 220 */     for (int j = 0; j < weights.length; j++) {
/* 221 */       if (best_weights[j] == 1.0D) {
/* 222 */         System.out.print((j + 1) + " ");
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 227 */     (mgr.getClusteringWeights()).m_Weights = best_weights;
/*     */     
/* 229 */     return best_err;
/*     */   }
/*     */   
/*     */   public void findBestSupportTasks(ClusData trset, ClusData pruneset) throws ClusException, IOException {
/* 233 */     ClusStatManager mgr = getStatManager();
/* 234 */     Settings settings = mgr.getSettings();
/* 235 */     int main_target = (new Integer(settings.getMainTarget())).intValue() - 1;
/*     */ 
/*     */     
/* 238 */     settings.getTarget();
/* 239 */     IntervalCollection targets = new IntervalCollection(settings.getTarget());
/*     */     
/* 241 */     int[] support_range = { targets.getMinIndex() - 1, targets.getMaxIndex() - 1 };
/* 242 */     int emc = main_target - support_range[0];
/* 243 */     boolean recursive = settings.getRecursive();
/*     */ 
/*     */ 
/*     */     
/* 247 */     resetWeights(main_target);
/* 248 */     double[] weights = (mgr.getClusteringWeights()).m_Weights;
/* 249 */     double best_err = addBestSupportTasks((double[])weights.clone(), emc, support_range, trset, pruneset);
/*     */     
/* 251 */     if (recursive) {
/* 252 */       System.out.println("\n---recursive sit---");
/* 253 */       weights = (mgr.getClusteringWeights()).m_Weights;
/* 254 */       double new_err = addBestSupportTasks((double[])weights.clone(), emc, support_range, trset, pruneset);
/* 255 */       while (new_err > best_err) {
/* 256 */         best_err = new_err;
/* 257 */         weights = (mgr.getClusteringWeights()).m_Weights;
/* 258 */         new_err = addBestSupportTasks((double[])weights.clone(), emc, support_range, trset, pruneset);
/*     */       } 
/*     */     } 
/*     */     
/* 262 */     System.out.println();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void twoSidedSit(ClusData trset, ClusData pruneset) throws ClusException, IOException {
/* 268 */     ClusStatManager mgr = getStatManager();
/* 269 */     Settings settings = mgr.getSettings();
/* 270 */     int main_target = (new Integer(settings.getMainTarget())).intValue() - 1;
/*     */ 
/*     */     
/* 273 */     settings.getTarget();
/* 274 */     IntervalCollection targets = new IntervalCollection(settings.getTarget());
/*     */     
/* 276 */     int[] support_range = { targets.getMinIndex() - 1, targets.getMaxIndex() - 1 };
/* 277 */     int emc = main_target - support_range[0];
/* 278 */     boolean recursive = settings.getRecursive();
/*     */ 
/*     */ 
/*     */     
/* 282 */     resetWeights(main_target);
/* 283 */     double[] weights = (mgr.getClusteringWeights()).m_Weights;
/*     */     
/* 285 */     ClusError err = doParamXVal(trset, pruneset);
/* 286 */     double ST_err = err.getModelErrorComponent(emc);
/* 287 */     double best_err = ST_err;
/*     */     
/* 289 */     System.out.println("Estimated ST error: " + ST_err);
/*     */ 
/*     */ 
/*     */     
/* 293 */     for (int i = support_range[0]; i <= support_range[1]; i++) {
/* 294 */       (mgr.getClusteringWeights()).m_Weights[i] = 1.0D;
/*     */     }
/* 296 */     err = doParamXVal(trset, pruneset);
/* 297 */     double MT_err = err.getModelErrorComponent(emc);
/* 298 */     System.out.println("Estimated MT error: " + MT_err);
/*     */ 
/*     */ 
/*     */     
/* 302 */     if (MT_err > ST_err) {
/* 303 */       best_err = MT_err;
/* 304 */       System.out.println("\n---recursive sub sit---");
/* 305 */       weights = (mgr.getClusteringWeights()).m_Weights;
/* 306 */       double new_err = substractBestSupportTasks((double[])weights.clone(), emc, support_range, trset, pruneset);
/* 307 */       while (new_err > best_err) {
/* 308 */         best_err = new_err;
/* 309 */         weights = (mgr.getClusteringWeights()).m_Weights;
/* 310 */         new_err = substractBestSupportTasks((double[])weights.clone(), emc, support_range, trset, pruneset);
/*     */       } 
/*     */     } else {
/* 313 */       System.out.println("\n---recursive add sit---");
/* 314 */       resetWeights(main_target);
/* 315 */       weights = (mgr.getClusteringWeights()).m_Weights;
/* 316 */       double new_err = addBestSupportTasks((double[])weights.clone(), emc, support_range, trset, pruneset);
/* 317 */       while (new_err > best_err) {
/* 318 */         best_err = new_err;
/* 319 */         weights = (mgr.getClusteringWeights()).m_Weights;
/* 320 */         new_err = addBestSupportTasks((double[])weights.clone(), emc, support_range, trset, pruneset);
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 328 */     System.out.println();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void superSit(ClusData trset, ClusData pruneset) throws ClusException, IOException {
/* 334 */     ClusStatManager mgr = getStatManager();
/* 335 */     Settings settings = mgr.getSettings();
/* 336 */     int main_target = (new Integer(settings.getMainTarget())).intValue() - 1;
/*     */ 
/*     */     
/* 339 */     settings.getTarget();
/* 340 */     IntervalCollection targets = new IntervalCollection(settings.getTarget());
/*     */     
/* 342 */     int[] support_range = { targets.getMinIndex() - 1, targets.getMaxIndex() - 1 };
/* 343 */     int emc = main_target - support_range[0];
/* 344 */     boolean recursive = settings.getRecursive();
/*     */ 
/*     */ 
/*     */     
/* 348 */     resetWeights(main_target);
/* 349 */     double[] weights = (mgr.getClusteringWeights()).m_Weights;
/*     */     
/* 351 */     ClusError err = doParamXVal(trset, pruneset);
/* 352 */     double ST_err = err.getModelErrorComponent(emc);
/* 353 */     double best_err = ST_err;
/*     */     
/* 355 */     System.out.println("Estimated ST error: " + ST_err);
/*     */ 
/*     */     
/* 358 */     double[] starting_weights = (double[])weights.clone();
/*     */ 
/*     */     
/* 361 */     for (int i = support_range[0]; i <= support_range[1]; i++) {
/* 362 */       (mgr.getClusteringWeights()).m_Weights[i] = 1.0D;
/* 363 */       err = doParamXVal(trset, pruneset);
/* 364 */       double MT_err = err.getModelErrorComponent(emc);
/* 365 */       if (MT_err > ST_err) {
/* 366 */         starting_weights[i] = 1.0D;
/* 367 */         System.out.println("Adding target " + (i + 1) + " to starting set");
/*     */       } 
/*     */     } 
/* 370 */     (mgr.getClusteringWeights()).m_Weights = starting_weights;
/*     */     
/* 372 */     System.out.print("Starting from targets ");
/* 373 */     for (int j = 0; j < weights.length; j++) {
/* 374 */       if (starting_weights[j] == 1.0D) {
/* 375 */         System.out.print((j + 1) + " ");
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 380 */     System.out.println("\n---recursive sit---");
/*     */     
/* 382 */     weights = starting_weights;
/* 383 */     double new_err = addBestSupportTasks((double[])weights.clone(), emc, support_range, trset, pruneset);
/* 384 */     new_err = substractBestSupportTasks((double[])weights.clone(), emc, support_range, trset, pruneset);
/* 385 */     while (new_err > best_err) {
/* 386 */       best_err = new_err;
/*     */       
/* 388 */       weights = (mgr.getClusteringWeights()).m_Weights;
/* 389 */       new_err = addBestSupportTasks((double[])weights.clone(), emc, support_range, trset, pruneset);
/* 390 */       new_err = substractBestSupportTasks((double[])weights.clone(), emc, support_range, trset, pruneset);
/*     */     } 
/* 392 */     System.out.println();
/*     */   }
/*     */   
/*     */   public void sweepSit(ClusData trset, ClusData pruneset) throws ClusException, IOException {
/* 396 */     ClusStatManager mgr = getStatManager();
/* 397 */     Settings settings = mgr.getSettings();
/* 398 */     int main_target = (new Integer(settings.getMainTarget())).intValue() - 1;
/*     */ 
/*     */     
/* 401 */     settings.getTarget();
/* 402 */     IntervalCollection targets = new IntervalCollection(settings.getTarget());
/*     */     
/* 404 */     int[] support_range = { targets.getMinIndex() - 1, targets.getMaxIndex() - 1 };
/* 405 */     int emc = main_target - support_range[0];
/* 406 */     boolean recursive = settings.getRecursive();
/*     */ 
/*     */ 
/*     */     
/* 410 */     resetWeights(main_target);
/* 411 */     double[] weights = (mgr.getClusteringWeights()).m_Weights;
/*     */     
/* 413 */     ClusError err = doParamXVal(trset, pruneset);
/* 414 */     double best_err = err.getModelErrorComponent(emc);
/*     */     
/* 416 */     double[] selected_weights = (double[])weights.clone();
/* 417 */     boolean improved = true;
/*     */     
/* 419 */     System.out.print("Set before sweeping: "); int j;
/* 420 */     for (j = 0; j < weights.length; j++) {
/* 421 */       if (selected_weights[j] == 1.0D) {
/* 422 */         System.out.print((j + 1) + " ");
/*     */       }
/*     */     } 
/* 425 */     System.out.println();
/* 426 */     while (improved) {
/* 427 */       improved = false;
/*     */       
/* 429 */       for (int i = support_range[0]; i <= support_range[1]; i++) {
/* 430 */         if ((mgr.getClusteringWeights()).m_Weights[i] == 0.0D) {
/* 431 */           (mgr.getClusteringWeights()).m_Weights[i] = 1.0D;
/* 432 */           err = doParamXVal(trset, pruneset);
/* 433 */           double MT_err = err.getModelErrorComponent(emc);
/* 434 */           if (MT_err > best_err) {
/* 435 */             best_err = MT_err;
/* 436 */             selected_weights[i] = 1.0D;
/*     */             
/* 438 */             System.out.print("Adding target " + (i + 1) + " to selected set: ");
/* 439 */             for (int k = 0; k < weights.length; ) { if (selected_weights[k] == 1.0D) System.out.print((k + 1) + " ");  k++; }  System.out.println();
/*     */             
/* 441 */             (mgr.getClusteringWeights()).m_Weights = (double[])selected_weights.clone();
/* 442 */             improved = true;
/*     */           } else {
/* 444 */             (mgr.getClusteringWeights()).m_Weights = (double[])selected_weights.clone();
/*     */           } 
/*     */         } else {
/* 447 */           (mgr.getClusteringWeights()).m_Weights[i] = 0.0D;
/* 448 */           err = doParamXVal(trset, pruneset);
/* 449 */           double MT_err = err.getModelErrorComponent(emc);
/* 450 */           if (MT_err > best_err) {
/* 451 */             best_err = MT_err;
/* 452 */             selected_weights[i] = 0.0D;
/*     */             
/* 454 */             System.out.print("Removing target " + (i + 1) + " from selected set: ");
/* 455 */             for (int k = 0; k < weights.length; ) { if (selected_weights[k] == 1.0D) System.out.print((k + 1) + " ");  k++; }  System.out.println();
/*     */             
/* 457 */             (mgr.getClusteringWeights()).m_Weights = (double[])selected_weights.clone();
/* 458 */             improved = true;
/*     */           } else {
/* 460 */             (mgr.getClusteringWeights()).m_Weights = (double[])selected_weights.clone();
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/* 466 */     System.out.print("Final targets ");
/* 467 */     for (j = 0; j < weights.length; j++) {
/* 468 */       if (selected_weights[j] == 1.0D) {
/* 469 */         System.out.print((j + 1) + " ");
/*     */       }
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 477 */     System.out.println();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void exhaustiveSearch(ClusRun cr) throws ClusException, IOException {
/* 483 */     ClusStatManager mgr = getStatManager();
/* 484 */     Settings settings = mgr.getSettings();
/*     */ 
/*     */ 
/*     */     
/* 488 */     settings.getTarget();
/* 489 */     IntervalCollection targets = new IntervalCollection(settings.getTarget());
/*     */     
/* 491 */     int[] support_range = { targets.getMinIndex() - 1, targets.getMaxIndex() - 1 };
/*     */     
/* 493 */     boolean recursive = settings.getRecursive();
/*     */ 
/*     */     
/* 496 */     resetWeights();
/* 497 */     double[] weights = (mgr.getClusteringWeights()).m_Weights;
/*     */     
/* 499 */     ClusErrorOutput errOutput = new ClusErrorOutput(settings.getAppName() + ".err", settings);
/*     */ 
/*     */     
/* 502 */     int n = support_range[1] - support_range[0] + 1;
/* 503 */     for (int B = 0; B < 1 << n; B++) {
/* 504 */       for (int b = 0; b < n; b++) {
/* 505 */         if ((B & 1 << b) > 0) {
/* 506 */           weights[b + support_range[0]] = 1.0D;
/*     */         } else {
/* 508 */           weights[b + support_range[0]] = 0.0D;
/*     */         } 
/*     */       } 
/* 511 */       System.out.println();
/* 512 */       for (int j = 0; j < weights.length; j++) {
/* 513 */         if (weights[j] == 1.0D) {
/* 514 */           System.out.print((j + 1) + " ");
/*     */         }
/*     */       } 
/*     */       
/* 518 */       errOutput.writeOutput(cr, false, false, weights);
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 523 */     ClusError err = doParamXVal(cr.getTrainingSet(), cr.getPruneSet());
/* 524 */     double best_err = err.getModelErrorComponent(0);
/*     */   }
/*     */   
/*     */   public void induceAll(ClusRun cr) throws ClusException, IOException {
/* 528 */     ClusStatManager mgr = getStatManager();
/* 529 */     Settings settings = mgr.getSettings();
/*     */     
/* 531 */     long start_time = ResourceInfo.getTime();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 541 */     long done_time = ResourceInfo.getTime();
/*     */ 
/*     */ 
/*     */     
/* 545 */     System.out.println("----------Building final model------------");
/* 546 */     this.m_Class.induceAll(cr);
/*     */     
/* 548 */     cr.setInductionTime(done_time - start_time);
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\algo\tdidt\ClusSITDecisionTree.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */