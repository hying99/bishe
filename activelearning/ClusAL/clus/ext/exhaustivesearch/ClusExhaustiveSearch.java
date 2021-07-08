/*     */ package clus.ext.exhaustivesearch;
/*     */ 
/*     */ import clus.Clus;
/*     */ import clus.algo.ClusInductionAlgorithm;
/*     */ import clus.algo.ClusInductionAlgorithmType;
/*     */ import clus.algo.split.CurrentBestTestAndHeuristic;
/*     */ import clus.algo.split.FindBestTest;
/*     */ import clus.algo.tdidt.ClusNode;
/*     */ import clus.algo.tdidt.ConstraintDFInduce;
/*     */ import clus.algo.tdidt.processor.BasicExampleCollector;
/*     */ import clus.data.rows.DataTuple;
/*     */ import clus.data.rows.RowData;
/*     */ import clus.data.type.ClusAttrType;
/*     */ import clus.data.type.ClusSchema;
/*     */ import clus.data.type.NominalAttrType;
/*     */ import clus.data.type.NumericAttrType;
/*     */ import clus.ext.beamsearch.ClusBeam;
/*     */ import clus.ext.beamsearch.ClusBeamHeuristic;
/*     */ import clus.ext.beamsearch.ClusBeamModel;
/*     */ import clus.ext.beamsearch.ClusBeamTreeElem;
/*     */ import clus.ext.constraint.ClusConstraintFile;
/*     */ import clus.heuristic.ClusHeuristic;
/*     */ import clus.main.ClusRun;
/*     */ import clus.main.ClusStatManager;
/*     */ import clus.main.Settings;
/*     */ import clus.model.ClusModel;
/*     */ import clus.model.ClusModelInfo;
/*     */ import clus.model.modelio.ClusModelCollectionIO;
/*     */ import clus.model.processor.ClusModelProcessor;
/*     */ import clus.model.test.NodeTest;
/*     */ import clus.statistic.ClusStatistic;
/*     */ import clus.util.ClusException;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintWriter;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import jeans.io.MyFile;
/*     */ import jeans.math.SingleStat;
/*     */ import jeans.tree.MyNode;
/*     */ import jeans.tree.Node;
/*     */ import jeans.util.MyArray;
/*     */ import jeans.util.StringUtils;
/*     */ import jeans.util.cmdline.CMDLineArgs;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ClusExhaustiveSearch
/*     */   extends ClusInductionAlgorithmType
/*     */ {
/*     */   public static final int HEURISTIC_ERROR = 0;
/*     */   public static final int HEURISTIC_SS = 1;
/*  62 */   protected BasicExampleCollector m_Coll = new BasicExampleCollector();
/*     */   protected ConstraintDFInduce m_Induce;
/*     */   protected ClusExhaustiveInduce m_BeamInduce;
/*     */   protected boolean m_BeamChanged;
/*     */   protected int m_CurrentModel;
/*     */   protected int m_MaxTreeSize;
/*     */   protected double m_MaxError;
/*     */   protected double m_TotalWeight;
/*     */   protected ArrayList m_BeamStats;
/*     */   protected ClusBeam m_Beam;
/*     */   protected boolean m_BeamPostPruning;
/*     */   protected ClusBeamHeuristic m_Heuristic;
/*     */   protected ClusHeuristic m_AttrHeuristic;
/*     */   protected boolean m_Verbose;
/*     */   
/*     */   public ClusExhaustiveSearch(Clus clus) throws ClusException, IOException {
/*  78 */     super(clus);
/*     */   }
/*     */   
/*     */   public void reset() {
/*  82 */     this.m_Beam = null;
/*  83 */     this.m_BeamChanged = false;
/*  84 */     this.m_CurrentModel = -1;
/*  85 */     this.m_TotalWeight = 0.0D;
/*  86 */     this.m_BeamStats = new ArrayList();
/*     */   }
/*     */   
/*     */   public ClusInductionAlgorithm createInduce(ClusSchema schema, Settings sett, CMDLineArgs cargs) throws ClusException, IOException {
/*  90 */     schema.addIndices(0);
/*  91 */     this.m_BeamInduce = new ClusExhaustiveInduce(schema, sett, this);
/*  92 */     this.m_BeamInduce.getStatManager().setBeamSearch(true);
/*  93 */     return this.m_BeamInduce;
/*     */   }
/*     */   
/*     */   public void initializeHeuristic() {
/*  97 */     ClusStatManager smanager = this.m_BeamInduce.getStatManager();
/*  98 */     Settings sett = smanager.getSettings();
/*  99 */     this.m_MaxTreeSize = sett.getMaxSize();
/* 100 */     this.m_MaxError = sett.getMaxErrorConstraint(0);
/* 101 */     System.out.println("ClusExhaustiveSearch : The Max Error is " + this.m_MaxError);
/* 102 */     this.m_BeamPostPruning = sett.isBeamPostPrune();
/* 103 */     this.m_Heuristic = (ClusBeamHeuristic)smanager.getHeuristic();
/* 104 */     int attr_heur = sett.getBeamAttrHeuristic();
/* 105 */     if (attr_heur != 0) {
/* 106 */       this.m_AttrHeuristic = smanager.createHeuristic(attr_heur);
/* 107 */       this.m_Heuristic.setAttrHeuristic(this.m_AttrHeuristic);
/*     */     } 
/*     */   }
/*     */   
/*     */   public final boolean isBeamPostPrune() {
/* 112 */     return this.m_BeamPostPruning;
/*     */   }
/*     */   
/*     */   public double computeLeafAdd(ClusNode leaf) {
/* 116 */     return this.m_Heuristic.computeLeafAdd(leaf);
/*     */   }
/*     */   
/*     */   public double estimateBeamMeasure(ClusNode tree) {
/* 120 */     return this.m_Heuristic.estimateBeamMeasure(tree);
/*     */   }
/*     */   
/*     */   public void initSelector(CurrentBestTestAndHeuristic sel) {
/* 124 */     if (hasAttrHeuristic()) {
/* 125 */       sel.setHeuristic(this.m_AttrHeuristic);
/*     */     }
/*     */   }
/*     */   
/*     */   public final boolean hasAttrHeuristic() {
/* 130 */     return (this.m_AttrHeuristic != null);
/*     */   }
/*     */   
/*     */   public ClusBeam initializeBeamExhaustive(ClusRun run) throws ClusException {
/* 134 */     ClusStatManager smanager = this.m_BeamInduce.getStatManager();
/* 135 */     Settings sett = smanager.getSettings();
/* 136 */     sett.setMinimalWeight(1.0D);
/* 137 */     ClusBeam beam = new ClusBeam(-1, false);
/*     */     
/* 139 */     RowData train = (RowData)run.getTrainingSet();
/* 140 */     ClusStatistic stat = this.m_Induce.createTotalClusteringStat(train);
/* 141 */     stat.calcMean();
/* 142 */     this.m_Induce.initSelectorAndSplit(stat);
/* 143 */     initSelector(this.m_Induce.getBestTest());
/* 144 */     System.out.println("Root statistic: " + stat);
/* 145 */     ClusNode root = null;
/*     */     
/* 147 */     String constr_file = sett.getConstraintFile();
/* 148 */     if (StringUtils.unCaseCompare(constr_file, "None")) {
/* 149 */       root = new ClusNode();
/* 150 */       root.setClusteringStat(stat);
/*     */     } else {
/* 152 */       ClusConstraintFile file = ClusConstraintFile.getInstance();
/* 153 */       root = file.getClone(constr_file);
/* 154 */       root.setClusteringStat(stat);
/* 155 */       this.m_Induce.fillInStatsAndTests(root, train);
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 160 */     double weight = root.getClusteringStat().getTotalWeight();
/* 161 */     setTotalWeight(weight);
/*     */     
/* 163 */     double value = estimateBeamMeasure(root);
/*     */     
/* 165 */     beam.addModel(new ClusBeamModel(value, (ClusModel)root));
/* 166 */     System.out.println("the number of children from the root node is :" + root.getNbChildren());
/* 167 */     return beam;
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
/*     */   public void refineGivenLeafExhaustive(ClusNode leaf, ClusBeamModel root, ClusBeam beam, ClusAttrType[] attrs) {
/* 180 */     MyArray arr = (MyArray)leaf.getVisitor();
/* 181 */     RowData data = new RowData(arr.getObjects(), arr.size());
/* 182 */     if (this.m_Induce.initSelectorAndStopCrit(leaf, data)) {
/*     */       
/* 184 */       System.out.println("stopping criterion reached in refineGivenLeafExhaustive");
/*     */       return;
/*     */     } 
/* 187 */     if (leaf.getClusteringStat().getError() == 0.0D) {
/*     */       return;
/*     */     }
/*     */ 
/*     */     
/* 192 */     CurrentBestTestAndHeuristic sel = this.m_Induce.getBestTest();
/* 193 */     FindBestTest find = this.m_Induce.getFindBestTest();
/* 194 */     double base_value = root.getValue();
/* 195 */     double leaf_add = this.m_Heuristic.computeLeafAdd(leaf);
/* 196 */     this.m_Heuristic.setTreeOffset(base_value - leaf_add);
/*     */     
/* 198 */     for (int i = 0; i < attrs.length; i++) {
/*     */       
/* 200 */       sel.resetBestTest();
/* 201 */       double beam_min_value = Double.NEGATIVE_INFINITY;
/* 202 */       sel.setBestHeur(beam_min_value);
/*     */       
/* 204 */       ClusAttrType at = attrs[i];
/*     */ 
/*     */       
/* 207 */       if (at instanceof NominalAttrType) { find.findNominal((NominalAttrType)at, data); }
/* 208 */       else { find.findNumeric((NumericAttrType)at, data); }
/*     */       
/* 210 */       if (sel.hasBestTest()) {
/* 211 */         ClusNode ref_leaf = (ClusNode)leaf.cloneNode();
/* 212 */         ref_leaf.testToNode(sel);
/*     */         
/* 214 */         if (Settings.VERBOSE > 0) System.out.println("Test: " + ref_leaf.getTestString() + " -> " + sel.m_BestHeur + " (" + ref_leaf.getTest().getPosFreq() + ")");
/*     */         
/* 216 */         ClusStatManager mgr = this.m_Induce.getStatManager();
/* 217 */         int arity = ref_leaf.updateArity();
/* 218 */         NodeTest test = ref_leaf.getTest();
/* 219 */         for (int j = 0; j < arity; j++) {
/* 220 */           ClusNode child = new ClusNode();
/* 221 */           ref_leaf.setChild((Node)child, j);
/* 222 */           RowData subset = data.applyWeighted(test, j);
/* 223 */           child.initClusteringStat(mgr, subset);
/*     */           
/* 225 */           child.initTargetStat(mgr, subset);
/* 226 */           child.getTargetStat().calcMean();
/*     */         } 
/*     */         
/* 229 */         ClusNode root_model = (ClusNode)root.getModel();
/* 230 */         ClusNode ref_tree = (ClusNode)root_model.cloneTree((MyNode)leaf, (MyNode)ref_leaf);
/* 231 */         double new_heur = sanityCheck(sel.m_BestHeur, ref_tree);
/*     */         
/* 233 */         ClusBeamModel new_model = new ClusBeamModel(new_heur, (ClusModel)ref_tree);
/* 234 */         new_model.setParentModelIndex(getCurrentModel());
/* 235 */         beam.addModel(new_model);
/* 236 */         setBeamChanged(true);
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
/*     */   public void refineEachLeaf(ClusNode tree, ClusBeamModel root, ClusBeam beam, ClusAttrType[] attrs) {
/* 250 */     int nb_c = tree.getNbChildren();
/* 251 */     if (nb_c == 0) {
/* 252 */       refineGivenLeafExhaustive(tree, root, beam, attrs);
/*     */     } else {
/* 254 */       for (int i = 0; i < nb_c; i++) {
/* 255 */         ClusNode child = (ClusNode)tree.getChild(i);
/*     */         
/* 257 */         refineEachLeaf(child, root, beam, attrs);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void refineModel(ClusBeamModel model, ClusBeam beam, ClusRun run) throws IOException {
/* 264 */     ClusNode tree = (ClusNode)model.getModel();
/*     */     
/* 266 */     if (this.m_MaxTreeSize >= 0) {
/* 267 */       int size = tree.getNbNodes();
/* 268 */       if (size + 2 > this.m_MaxTreeSize) {
/*     */         return;
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 274 */     if (this.m_MaxError > 0.0D && this.m_MaxTreeSize > 0) {
/* 275 */       int NbpossibleSplit = (this.m_MaxTreeSize - tree.getModelSize()) / 2;
/*     */       
/* 277 */       double[] error = getErrorPerleaf(tree);
/*     */       
/* 279 */       double minerror = 0.0D;
/* 280 */       if (error.length > NbpossibleSplit) {
/* 281 */         for (int j = 0; j < error.length - NbpossibleSplit; j++)
/*     */         {
/* 283 */           minerror += error[j];
/*     */         }
/* 285 */         double minerrorrel = minerror / this.m_TotalWeight;
/*     */         
/* 287 */         if (minerrorrel > this.m_MaxError) {
/* 288 */           tree.printTree();
/* 289 */           System.out.println("PRUNE WITH ERROR");
/*     */           
/*     */           return;
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/* 296 */     RowData train = (RowData)run.getTrainingSet();
/* 297 */     this.m_Coll.initialize((ClusModel)tree, null);
/* 298 */     int nb_rows = train.getNbRows();
/* 299 */     for (int i = 0; i < nb_rows; i++) {
/* 300 */       DataTuple tuple = train.getTuple(i);
/* 301 */       tree.applyModelProcessor(tuple, (ClusModelProcessor)this.m_Coll);
/*     */     } 
/*     */     
/* 304 */     ClusAttrType[] attrs = train.getSchema().getDescriptiveAttributes();
/* 305 */     refineEachLeaf(tree, model, beam, attrs);
/*     */     
/* 307 */     tree.clearVisitors();
/*     */   }
/*     */   
/*     */   public static double[] getErrorPerleaf(ClusNode tree) {
/* 311 */     int nbleaf = tree.getNbLeaf();
/* 312 */     double[] resulterror = new double[nbleaf];
/* 313 */     if (tree.atBottomLevel()) {
/* 314 */       ClusStatistic total = tree.getClusteringStat();
/* 315 */       resulterror[0] = total.getError();
/*     */     }
/*     */     else {
/*     */       
/* 319 */       ClusNode child0 = (ClusNode)tree.getChild(0);
/* 320 */       resulterror = getErrorPerleaf(child0);
/* 321 */       for (int i = 1; i < tree.getNbChildren(); i++) {
/* 322 */         ClusNode childi = (ClusNode)tree.getChild(i);
/* 323 */         double[] errori = getErrorPerleaf(childi);
/* 324 */         resulterror = concatarraysorted(resulterror, errori);
/*     */       } 
/*     */     } 
/* 327 */     return resulterror;
/*     */   }
/*     */   
/*     */   public static double[] concatarraysorted(double[] array1, double[] array2) {
/* 331 */     int size_array = array1.length + array2.length;
/* 332 */     double[] array = new double[size_array]; int i;
/* 333 */     for (i = 0; i < array1.length; i++) {
/* 334 */       array[i] = array1[i];
/*     */     }
/* 336 */     for (i = 0; i < array2.length; i++) {
/* 337 */       array[i + array1.length] = array2[i];
/*     */     }
/* 339 */     Arrays.sort(array);
/* 340 */     return array;
/*     */   }
/*     */ 
/*     */   
/*     */   public void refineBeamExhaustive(ClusBeam beam, ClusRun run) throws IOException {
/* 345 */     setBeamChanged(false);
/* 346 */     ArrayList<ClusBeamModel> models = beam.toArray();
/*     */     
/* 348 */     for (int i = 0; i < models.size(); i++) {
/* 349 */       System.out.print("Refining model: " + i);
/* 350 */       setCurrentModel(i);
/* 351 */       ClusBeamModel model = models.get(i);
/* 352 */       if (!model.isRefined() && !model.isFinished()) {
/* 353 */         if (this.m_Verbose) System.out.print("[*]"); 
/* 354 */         refineModel(model, beam, run);
/* 355 */         model.setRefined(true);
/* 356 */         model.setParentModelIndex(-1);
/*     */       } 
/* 358 */       if (this.m_Verbose) {
/* 359 */         if (model.isRefined()) {
/* 360 */           System.out.println("[R]");
/*     */         }
/* 362 */         if (model.isFinished()) {
/* 363 */           System.out.println("[F]");
/*     */         }
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public Settings getSettings() {
/* 371 */     return this.m_Clus.getSettings();
/*     */   }
/*     */   
/*     */   public void estimateBeamStats(ClusBeam beam) {
/* 375 */     SingleStat stat_heuristic = new SingleStat();
/* 376 */     SingleStat stat_size = new SingleStat();
/* 377 */     SingleStat stat_same_heur = new SingleStat();
/* 378 */     ArrayList<ClusBeamModel> lst = beam.toArray();
/* 379 */     HashSet<NodeTest> tops = new HashSet();
/* 380 */     for (int i = 0; i < lst.size(); i++) {
/* 381 */       ClusBeamModel model = lst.get(i);
/* 382 */       stat_heuristic.addFloat(model.getValue());
/* 383 */       stat_size.addFloat(model.getModel().getModelSize());
/* 384 */       NodeTest top = ((ClusNode)model.getModel()).getTest();
/* 385 */       if (top != null && 
/* 386 */         !tops.contains(top)) {
/* 387 */         tops.add(top);
/*     */       }
/*     */     } 
/*     */     
/* 391 */     Iterator<ClusBeamTreeElem> iter = beam.getIterator();
/* 392 */     while (iter.hasNext()) {
/* 393 */       ClusBeamTreeElem elem = iter.next();
/* 394 */       stat_same_heur.addFloat(elem.getCount());
/*     */     } 
/* 396 */     ArrayList<SingleStat> stat = new ArrayList();
/* 397 */     stat.add(stat_heuristic);
/* 398 */     stat.add(stat_same_heur);
/* 399 */     stat.add(stat_size);
/* 400 */     stat.add(new Integer(tops.size()));
/* 401 */     this.m_BeamStats.add(stat);
/*     */   }
/*     */   
/*     */   public String getLevelStat(int i) {
/* 405 */     ArrayList stat = this.m_BeamStats.get(i);
/* 406 */     StringBuffer buf = new StringBuffer();
/* 407 */     buf.append("Level: " + i);
/* 408 */     for (int j = 0; j < stat.size(); j++) {
/* 409 */       Object elem = stat.get(j);
/* 410 */       buf.append(", ");
/* 411 */       if (elem instanceof SingleStat) {
/* 412 */         SingleStat st = (SingleStat)elem;
/* 413 */         buf.append(st.getMean() + "," + st.getRange());
/*     */       } else {
/* 415 */         buf.append(elem.toString());
/*     */       } 
/*     */     } 
/* 418 */     return buf.toString();
/*     */   }
/*     */   
/*     */   public void printBeamStats(int level) {
/* 422 */     System.out.println(getLevelStat(level));
/*     */   }
/*     */   
/*     */   public void saveBeamStats() {
/* 426 */     MyFile stats = new MyFile(getSettings().getAppName() + ".bmstats");
/* 427 */     for (int i = 0; i < this.m_BeamStats.size(); i++) {
/* 428 */       stats.log(getLevelStat(i));
/*     */     }
/* 430 */     stats.close();
/*     */   }
/*     */   
/*     */   public void writeModel(ClusModelCollectionIO strm) throws IOException {
/* 434 */     saveBeamStats();
/* 435 */     ArrayList<ClusBeamModel> beam = getBeam().toArray();
/* 436 */     for (int i = 0; i < beam.size(); i++) {
/* 437 */       ClusBeamModel m = beam.get(i);
/* 438 */       ClusNode node = (ClusNode)m.getModel();
/* 439 */       node.updateTree();
/* 440 */       node.clearVisitors();
/*     */     } 
/* 442 */     int pos = 1;
/* 443 */     for (int j = beam.size() - 1; j >= 0; j--) {
/* 444 */       ClusBeamModel m = beam.get(j);
/* 445 */       ClusModelInfo info = new ClusModelInfo("B" + pos + ": " + m.getValue());
/* 446 */       info.setScore(m.getValue());
/* 447 */       info.setModel(m.getModel());
/* 448 */       strm.addModel(info);
/* 449 */       pos++;
/*     */     } 
/*     */   }
/*     */   
/*     */   public void setVerbose(boolean verb) {
/* 454 */     this.m_Verbose = verb;
/*     */   }
/*     */   
/*     */   public ClusBeam exhaustiveSearch(ClusRun run) throws ClusException, IOException {
/* 458 */     reset();
/* 459 */     System.out.println("Starting exhaustive search");
/* 460 */     this.m_Induce = new ConstraintDFInduce(this.m_BeamInduce);
/* 461 */     ClusBeam beam = initializeBeamExhaustive(run);
/* 462 */     ClusBeam beamresult = new ClusBeam(-1, false);
/* 463 */     int cpt_tree_evaluation = 0;
/* 464 */     int i = 0;
/* 465 */     setVerbose(true);
/*     */     while (true) {
/* 467 */       System.out.print("Step: ");
/* 468 */       if (i != 0) System.out.print(","); 
/* 469 */       System.out.println(i);
/* 470 */       System.out.flush();
/* 471 */       refineBeamExhaustive(beam, run);
/* 472 */       if (isBeamChanged()) {
/* 473 */         estimateBeamStats(beam);
/*     */ 
/*     */ 
/*     */         
/* 477 */         i++; continue;
/*     */       }  break;
/* 479 */     }  ArrayList<ClusBeamModel> arraybeam = beam.toArray();
/* 480 */     for (int j = 0; j < arraybeam.size(); j++) {
/*     */       
/* 482 */       ClusBeamModel m = arraybeam.get(j);
/*     */       
/* 484 */       ClusNode tree = (ClusNode)m.getModel();
/*     */ 
/*     */ 
/*     */       
/* 488 */       cpt_tree_evaluation++;
/* 489 */       if (this.m_MaxError <= 0.0D || ClusNode.estimateErrorRecursive(tree) / this.m_TotalWeight <= this.m_MaxError) {
/* 490 */         beamresult.addModel(m);
/*     */       }
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 496 */     System.out.println();
/* 497 */     setBeam(beamresult);
/* 498 */     ArrayList arraybeamresult = beamresult.toArray();
/* 499 */     System.out.println(" the model that fulfill the constraints are" + arraybeamresult.size());
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 506 */     System.out.println("The number of tree evaluated is " + cpt_tree_evaluation);
/* 507 */     return beamresult;
/*     */   }
/*     */   
/*     */   public void setBeam(ClusBeam beam) {
/* 511 */     this.m_Beam = beam;
/*     */   }
/*     */   
/*     */   public ClusBeam getBeam() {
/* 515 */     return this.m_Beam;
/*     */   }
/*     */   
/*     */   public boolean isBeamChanged() {
/* 519 */     return this.m_BeamChanged;
/*     */   }
/*     */   
/*     */   public void setBeamChanged(boolean change) {
/* 523 */     this.m_BeamChanged = change;
/*     */   }
/*     */   
/*     */   public int getCurrentModel() {
/* 527 */     return this.m_CurrentModel;
/*     */   }
/*     */   
/*     */   public void setCurrentModel(int model) {
/* 531 */     this.m_CurrentModel = model;
/*     */   }
/*     */   
/*     */   public void setTotalWeight(double weight) {
/* 535 */     this.m_TotalWeight = weight;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double sanityCheck(double value, ClusNode tree) {
/* 543 */     double expected = estimateBeamMeasure(tree);
/* 544 */     if (Math.abs(value - expected) > 1.0E-6D) {
/* 545 */       System.out.println("Bug in heurisitc: " + value + " <> " + expected);
/* 546 */       PrintWriter wrt = new PrintWriter(System.out);
/* 547 */       tree.printModel(wrt);
/* 548 */       wrt.close();
/* 549 */       System.out.flush();
/* 550 */       System.exit(1);
/*     */     } 
/* 552 */     return expected;
/*     */   }
/*     */   
/*     */   public void tryLogBeam(MyFile log, ClusBeam beam, String txt) {
/* 556 */     if (log.isEnabled()) {
/* 557 */       log.log(txt);
/* 558 */       log.log("*********************************************");
/* 559 */       beam.print(log.getWriter(), this.m_Clus.getSettings().getBeamBestN());
/* 560 */       log.log();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void pruneAll(ClusRun cr) throws ClusException, IOException {}
/*     */ 
/*     */   
/*     */   public ClusModel pruneSingle(ClusModel model, ClusRun cr) throws ClusException, IOException {
/* 569 */     return model;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\ext\exhaustivesearch\ClusExhaustiveSearch.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */