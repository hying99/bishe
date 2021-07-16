/*     */ package clus.algo.tdidt.tune;
/*     */ 
/*     */ import clus.algo.ClusInductionAlgorithmType;
/*     */ import clus.algo.tdidt.ClusDecisionTree;
/*     */ import clus.algo.tdidt.ClusNode;
/*     */ import clus.data.ClusData;
/*     */ import clus.data.attweights.ClusAttributeWeights;
/*     */ import clus.data.rows.DataTuple;
/*     */ import clus.data.rows.MemoryTupleIterator;
/*     */ import clus.data.rows.RowData;
/*     */ import clus.data.type.ClusSchema;
/*     */ import clus.error.ClusError;
/*     */ import clus.error.ClusErrorList;
/*     */ import clus.ext.hierarchical.HierRemoveInsigClasses;
/*     */ import clus.main.ClusRun;
/*     */ import clus.main.ClusStatManager;
/*     */ import clus.main.ClusSummary;
/*     */ import clus.main.Settings;
/*     */ import clus.model.ClusModel;
/*     */ import clus.model.ClusModelInfo;
/*     */ import clus.model.processor.ClusModelProcessor;
/*     */ import clus.pruning.PruneTree;
/*     */ import clus.pruning.SizeConstraintPruning;
/*     */ import clus.pruning.TreeErrorComputer;
/*     */ import clus.selection.ClusSelection;
/*     */ import clus.selection.XValDataSelection;
/*     */ import clus.selection.XValMainSelection;
/*     */ import clus.selection.XValRandomSelection;
/*     */ import clus.selection.XValSelection;
/*     */ import clus.statistic.ClusStatistic;
/*     */ import clus.util.ClusException;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Random;
/*     */ import jeans.io.MyFile;
/*     */ import jeans.math.SingleStatList;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class CDTuneSizeConstrPruning
/*     */   extends ClusDecisionTree
/*     */ {
/*     */   protected ClusInductionAlgorithmType m_Class;
/*     */   protected ClusSchema m_Schema;
/*     */   protected ClusStatistic m_TotalStat;
/*     */   protected boolean m_HasMissing;
/*     */   protected int m_NbExamples;
/*     */   protected int m_OrigSize;
/*  65 */   protected double m_RelErrAcc = 0.01D;
/*     */   
/*     */   protected ArrayList m_Graph;
/*     */   
/*     */   protected int m_Optimal;
/*     */   protected int m_MaxSize;
/*     */   
/*     */   public CDTuneSizeConstrPruning(ClusInductionAlgorithmType clss) {
/*  73 */     super(clss.getClus());
/*  74 */     this.m_Class = clss;
/*     */   }
/*     */   protected ClusAttributeWeights m_TargetWeights; protected boolean m_Relative; protected double m_RelativeScale;
/*     */   public void printInfo() {
/*  78 */     System.out.println("TDIDT (Tuning Size Constraint)");
/*  79 */     System.out.println("Heuristic: " + getStatManager().getHeuristicName());
/*     */   }
/*     */   
/*     */   private final void showFold(int i) {
/*  83 */     if (i != 0) System.out.print(" "); 
/*  84 */     System.out.print(String.valueOf(i + 1));
/*  85 */     System.out.flush();
/*     */   }
/*     */   
/*     */   public void setRelativeMeasure(boolean enable, double value) {
/*  89 */     this.m_Relative = enable;
/*  90 */     this.m_RelativeScale = value;
/*     */   }
/*     */   
/*     */   public void computeTestStatistics(ClusRun[] runs, int model, ClusError error) throws IOException, ClusException {
/*  94 */     TreeErrorComputer comp = new TreeErrorComputer();
/*  95 */     for (int i = 0; i < runs.length; i++) {
/*  96 */       ClusNode tree = (ClusNode)runs[i].getModelInfo(model).getModel();
/*  97 */       TreeErrorComputer.initializeTestErrors(tree, error);
/*  98 */       MemoryTupleIterator test = (MemoryTupleIterator)runs[i].getTestIter();
/*  99 */       test.init();
/* 100 */       DataTuple tuple = test.readTuple();
/* 101 */       while (tuple != null) {
/* 102 */         tree.applyModelProcessor(tuple, (ClusModelProcessor)comp);
/* 103 */         tuple = test.readTuple();
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public void computeErrorStandard(ClusNode tree, int model, ClusRun run) throws ClusException, IOException {
/* 109 */     ClusModelInfo mi = run.getModelInfo(model);
/* 110 */     ClusError err = mi.getTestError().getFirstError();
/* 111 */     MemoryTupleIterator test = (MemoryTupleIterator)run.getTestIter();
/* 112 */     test.init();
/* 113 */     DataTuple tuple = test.readTuple();
/* 114 */     while (tuple != null) {
/* 115 */       ClusStatistic pred = tree.predictWeighted(tuple);
/* 116 */       err.addExample(tuple, pred);
/* 117 */       tuple = test.readTuple();
/*     */     } 
/*     */   }
/*     */   
/*     */   public SingleStatList computeTreeError(ClusRun[] runs, SizeConstraintPruning[] pruners, int model, ClusSummary summ, int size) throws ClusException, IOException {
/* 122 */     ClusModelInfo summ_mi = summ.getModelInfo(model);
/* 123 */     ClusError summ_err = summ_mi.getTestError().getFirstError();
/* 124 */     summ_err.reset();
/* 125 */     SingleStatList res = new SingleStatList(runs.length);
/* 126 */     for (int i = 0; i < runs.length; i++) {
/* 127 */       ClusModelInfo mi = runs[i].getModelInfo(model);
/* 128 */       ClusNode tree = (ClusNode)mi.getModel();
/* 129 */       if (size == 1) {
/* 130 */         tree = tree.cloneNodeWithVisitor();
/*     */       } else {
/* 132 */         int modelsize = tree.getModelSize();
/* 133 */         if (size < modelsize) {
/* 134 */           tree = tree.cloneTreeWithVisitors();
/* 135 */           pruners[i].pruneExecute(tree, size);
/*     */         } 
/*     */       } 
/* 138 */       getStatManager(); if (ClusStatManager.getMode() == 2) {
/* 139 */         PruneTree pruner = new PruneTree();
/* 140 */         boolean bonf = getSettings().isUseBonferroni();
/* 141 */         HierRemoveInsigClasses hierpruner = new HierRemoveInsigClasses(runs[i].getPruneSet(), pruner, bonf, getStatManager().getHier());
/* 142 */         hierpruner.setSignificance(getSettings().getHierPruneInSig());
/* 143 */         hierpruner.prune(tree);
/*     */       } 
/* 145 */       ClusError err = mi.getTestError().getFirstError();
/* 146 */       err.reset();
/* 147 */       if (this.m_HasMissing) {
/* 148 */         computeErrorStandard(tree, model, runs[i]);
/*     */       } else {
/* 150 */         TreeErrorComputer.computeErrorSimple(tree, err);
/*     */       } 
/* 152 */       summ_err.add(err);
/* 153 */       MemoryTupleIterator test = (MemoryTupleIterator)runs[i].getTestIter();
/* 154 */       mi.getTestError().setNbExamples(test.getNbExamples());
/* 155 */       if (this.m_Relative) {
/* 156 */         res.addFloat(err.getModelError() / this.m_RelativeScale);
/*     */       } else {
/* 158 */         res.addFloat(err.getModelError());
/*     */       } 
/*     */     } 
/* 161 */     summ_mi.getTestError().setNbExamples(this.m_NbExamples);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 168 */     if (this.m_Relative) {
/* 169 */       res.setY(summ_err.getModelError() / this.m_RelativeScale);
/*     */     } else {
/* 171 */       res.setY(summ_err.getModelError());
/*     */     } 
/* 173 */     return res;
/*     */   }
/*     */   
/*     */   public SingleStatList addPoint(ArrayList<SingleStatList> points, int size, ClusRun[] runs, SizeConstraintPruning[] pruners, int model, ClusSummary summ) throws ClusException, IOException {
/* 177 */     int pos = 0;
/* 178 */     while (pos < points.size() && ((SingleStatList)points.get(pos)).getX() < size) {
/* 179 */       pos++;
/*     */     }
/* 181 */     if (pos < points.size() && ((SingleStatList)points.get(pos)).getX() == size) {
/* 182 */       return null;
/*     */     }
/* 184 */     SingleStatList point = computeTreeError(runs, pruners, model, summ, size);
/* 185 */     point.setX(size);
/* 186 */     points.add(pos, point);
/* 187 */     return point;
/*     */   }
/*     */   
/*     */   public double getRange(ArrayList<SingleStatList> graph) {
/* 191 */     double min = Double.POSITIVE_INFINITY;
/* 192 */     double max = Double.NEGATIVE_INFINITY;
/* 193 */     for (int i = 0; i < graph.size(); i++) {
/* 194 */       SingleStatList elem = graph.get(i);
/* 195 */       if (elem.getY() < min) min = elem.getY(); 
/* 196 */       if (elem.getY() > max) max = elem.getY(); 
/*     */     } 
/* 198 */     return Math.abs(max - min);
/*     */   }
/*     */   
/*     */   public void refineGraph(ArrayList<SingleStatList> graph, ClusRun[] runs, SizeConstraintPruning[] pruners, int model, ClusSummary summ) throws ClusException, IOException {
/* 202 */     int prevsize = -1;
/*     */     while (true) {
/* 204 */       boolean not_found = true;
/*     */       
/* 206 */       for (int i = 0; i < graph.size() - 2 && not_found; i++) {
/* 207 */         SingleStatList e1 = graph.get(i);
/* 208 */         SingleStatList e2 = graph.get(i + 1);
/* 209 */         if (Math.abs(e1.getY() - e2.getY()) > this.m_RelErrAcc) {
/* 210 */           int s1 = (int)e1.getX();
/* 211 */           int s2 = (int)e2.getX();
/* 212 */           int nmean = ((s1 + s2) / 2 - 1) / 2;
/* 213 */           int smean = 2 * nmean + 1;
/* 214 */           if (smean != s1 && smean != s2 && (this.m_OrigSize == -1 || smean < this.m_OrigSize)) {
/* 215 */             addPoint(graph, smean, runs, pruners, model, summ);
/* 216 */             System.out.print("#");
/* 217 */             System.out.flush();
/*     */             
/* 219 */             not_found = false;
/*     */           } 
/*     */         } 
/*     */       } 
/* 223 */       if (graph.size() == prevsize)
/* 224 */         return;  prevsize = graph.size();
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int findOptimalSize(ArrayList<SingleStatList> graph, boolean shouldBeLow) {
/* 248 */     double best_value = shouldBeLow ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY;
/* 249 */     int best_index = -1;
/* 250 */     for (int i = 0; i < graph.size(); i++) {
/* 251 */       SingleStatList elem = graph.get(i);
/* 252 */       if (shouldBeLow) {
/* 253 */         if (elem.getY() < best_value) {
/* 254 */           best_value = elem.getY();
/* 255 */           best_index = i;
/*     */         }
/*     */       
/* 258 */       } else if (elem.getY() > best_value) {
/* 259 */         best_value = elem.getY();
/* 260 */         best_index = i;
/*     */       } 
/*     */     } 
/*     */     
/* 264 */     if (best_index == -1) {
/* 265 */       return 1;
/*     */     }
/*     */     
/* 268 */     SingleStatList best_elem = graph.get(best_index);
/* 269 */     System.out.print("[" + best_elem.getX() + "," + best_elem.getY() + "]");
/* 270 */     SingleStatList result = best_elem;
/* 271 */     int pos = best_index - 1;
/* 272 */     while (pos >= 0) {
/* 273 */       SingleStatList prev_elem = graph.get(pos);
/* 274 */       if (prev_elem.getX() >= 3.0D && Math.abs(prev_elem.getY() - best_elem.getY()) < this.m_RelErrAcc) {
/* 275 */         result = prev_elem;
/* 276 */         System.out.print(" < " + prev_elem.getX());
/*     */       } 
/* 278 */       pos--;
/*     */     } 
/* 280 */     return (int)result.getX();
/*     */   }
/*     */   
/*     */   public final XValMainSelection getXValSelection(Settings sett, int nbrows) throws IOException, ClusException {
/* 284 */     String value = sett.getTuneFolds();
/* 285 */     if (value.length() > 0 && Character.isDigit(value.charAt(0))) {
/*     */       try {
/* 287 */         int nbfolds = Integer.parseInt(value);
/* 288 */         Random random = new Random(0L);
/* 289 */         return (XValMainSelection)new XValRandomSelection(nbrows, nbfolds, random);
/* 290 */       } catch (NumberFormatException e) {
/* 291 */         throw new ClusException("Illegal number of folds: " + value);
/*     */       } 
/*     */     }
/* 294 */     return (XValMainSelection)XValDataSelection.readFoldsFile(value, nbrows);
/*     */   }
/*     */ 
/*     */   
/*     */   public void findBestSize(ClusData trset) throws ClusException, IOException {
/* 299 */     int prevVerb = Settings.enableVerbose(0);
/* 300 */     ClusStatManager mgr = getStatManager();
/* 301 */     ClusSummary summ = new ClusSummary();
/* 302 */     ClusErrorList errorpar = mgr.createDefaultError();
/* 303 */     errorpar.setWeights(this.m_TargetWeights);
/* 304 */     summ.setTestError(errorpar);
/* 305 */     int model = 1;
/* 306 */     XValMainSelection sel = getXValSelection(getSettings(), trset.getNbRows());
/* 307 */     int nbfolds = sel.getNbFolds();
/* 308 */     ClusRun[] runs = new ClusRun[nbfolds];
/*     */     
/* 310 */     for (int i = 0; i < nbfolds; i++) {
/* 311 */       showFold(i);
/* 312 */       XValSelection msel = new XValSelection(sel, i);
/* 313 */       ClusRun cr = this.m_Clus.partitionDataBasic(trset, (ClusSelection)msel, summ, i + 1);
/* 314 */       ClusModel tree = this.m_Class.induceSingleUnpruned(cr);
/* 315 */       cr.getModelInfo(model).setModel(tree);
/* 316 */       runs[i] = cr;
/*     */     } 
/*     */     
/* 319 */     int maxsize = 0;
/* 320 */     SizeConstraintPruning[] pruners = new SizeConstraintPruning[nbfolds];
/* 321 */     for (int j = 0; j < nbfolds; j++) {
/* 322 */       ClusNode tree = (ClusNode)runs[j].getModelInfo(model).getModel();
/* 323 */       int size = tree.getModelSize();
/* 324 */       if (this.m_OrigSize != -1 && size > this.m_OrigSize) size = this.m_OrigSize; 
/* 325 */       if (size > maxsize) maxsize = size; 
/* 326 */       SizeConstraintPruning pruner = new SizeConstraintPruning(size, mgr.getClusteringWeights());
/* 327 */       pruner.pruneInitialize(tree, size);
/* 328 */       pruners[j] = pruner;
/*     */     } 
/* 330 */     if (maxsize == 1) {
/* 331 */       System.out.println("Optimal size (maxsize = 1) = 1");
/* 332 */       this.m_Class.getSettings().setSizeConstraintPruning(1);
/*     */       
/*     */       return;
/*     */     } 
/* 336 */     ClusError error = summ.getModelInfo(model).getTestError().getFirstError();
/* 337 */     if (!this.m_HasMissing) {
/* 338 */       computeTestStatistics(runs, model, error);
/*     */     }
/*     */     
/* 341 */     ArrayList graph = new ArrayList();
/* 342 */     setRelativeMeasure(false, 0.0D);
/* 343 */     SingleStatList point = computeTreeError(runs, pruners, model, summ, 1);
/* 344 */     setRelativeMeasure(true, point.getY());
/* 345 */     System.out.print(" ");
/* 346 */     System.out.print("<" + point.getY() + ">");
/* 347 */     addPoint(graph, 1, runs, pruners, model, summ);
/* 348 */     addPoint(graph, maxsize, runs, pruners, model, summ);
/*     */     
/* 350 */     int n = 1;
/* 351 */     boolean shouldBeLow = error.shouldBeLow();
/*     */     while (true) {
/* 353 */       int size = (int)(Math.pow(2.0D, n) + 1.0D);
/* 354 */       if (size > maxsize || (
/* 355 */         this.m_OrigSize != -1 && size > this.m_OrigSize))
/* 356 */         break;  SingleStatList new_pt = addPoint(graph, size, runs, pruners, model, summ);
/* 357 */       if (new_pt == null) {
/*     */         break;
/*     */       }
/* 360 */       if (shouldBeLow ? (
/* 361 */         graph.size() > 5 && new_pt.getY() > 1.1D) : (
/*     */ 
/*     */ 
/*     */         
/* 365 */         graph.size() > 5 && new_pt.getY() < 0.9D)) {
/*     */         break;
/*     */       }
/*     */       
/* 369 */       System.out.print("*");
/* 370 */       System.out.flush();
/*     */       
/* 372 */       n++;
/*     */     } 
/* 374 */     refineGraph(graph, runs, pruners, model, summ);
/* 375 */     int optimalSize = findOptimalSize(graph, shouldBeLow);
/* 376 */     System.out.println(" Best = " + optimalSize);
/*     */     
/* 378 */     setFinalResult(graph, optimalSize, maxsize);
/* 379 */     getSettings().setSizeConstraintPruning(optimalSize);
/* 380 */     Settings.enableVerbose(prevVerb);
/*     */   }
/*     */   
/*     */   public void saveInformation(String fname) {
/* 384 */     System.out.println("Saving: " + fname + ".dat");
/* 385 */     MyFile file = new MyFile(fname + ".dat");
/* 386 */     file.log("" + this.m_Optimal + "\t" + this.m_MaxSize);
/* 387 */     for (int i = 0; i < this.m_Graph.size(); i++) {
/* 388 */       SingleStatList elem = this.m_Graph.get(i);
/* 389 */       file.log("" + elem.getX() + "\t" + elem.getY());
/*     */     } 
/* 391 */     file.close();
/*     */   }
/*     */   
/*     */   public void setFinalResult(ArrayList graph, int optimal, int maxsize) {
/* 395 */     this.m_Graph = graph;
/* 396 */     this.m_Optimal = optimal;
/* 397 */     this.m_MaxSize = maxsize;
/*     */   }
/*     */   
/*     */   public ClusModel induceSingle(ClusRun cr) {
/* 401 */     System.out.println(">>> Error: induceSingle/1 not implemented");
/* 402 */     return null;
/*     */   }
/*     */   
/*     */   public ClusStatistic createTotalStat(RowData data) {
/* 406 */     ClusStatistic stat = this.m_Class.getStatManager().createClusteringStat();
/* 407 */     data.calcTotalStatBitVector(stat);
/* 408 */     return stat;
/*     */   }
/*     */   
/*     */   public void induceAll(ClusRun cr) throws ClusException {
/*     */     try {
/* 413 */       long start_time = System.currentTimeMillis();
/* 414 */       this.m_OrigSize = getSettings().getSizeConstraintPruning(0);
/* 415 */       if (getSettings().getSizeConstraintPruningNumber() > 1) {
/* 416 */         throw new ClusException("Only one value is allowed for MaxSize if -tunesize is given");
/*     */       }
/* 418 */       RowData train = (RowData)cr.getTrainingSet();
/* 419 */       this.m_Schema = train.getSchema();
/* 420 */       this.m_HasMissing = this.m_Schema.hasMissing();
/* 421 */       this.m_TotalStat = createTotalStat(train);
/* 422 */       this.m_NbExamples = train.getNbRows();
/*     */       
/* 424 */       System.out.println("Has missing values: " + this.m_HasMissing);
/* 425 */       this.m_TargetWeights = this.m_Class.getStatManager().getClusteringWeights();
/*     */       
/* 427 */       findBestSize((ClusData)train);
/* 428 */       System.out.println();
/*     */       
/* 430 */       this.m_Class.induceAll(cr);
/* 431 */       getSettings().setSizeConstraintPruning(this.m_OrigSize);
/* 432 */       long time = System.currentTimeMillis() - start_time;
/* 433 */       if (Settings.VERBOSE > 0) System.out.println("Time: " + (time / 1000.0D) + " sec"); 
/* 434 */       cr.setInductionTime(time);
/* 435 */     } catch (ClusException e) {
/* 436 */       System.err.println("Error: " + e);
/* 437 */     } catch (IOException e) {
/* 438 */       System.err.println("IO Error: " + e);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\algo\tdidt\tune\CDTuneSizeConstrPruning.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */