/*     */ package clus.ext.beamsearch;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ClusBeamSearch
/*     */   extends ClusInductionAlgorithmType
/*     */ {
/*     */   public static final int HEURISTIC_ERROR = 0;
/*     */   public static final int HEURISTIC_SS = 1;
/*  60 */   protected BasicExampleCollector m_Coll = new BasicExampleCollector();
/*     */   protected ConstraintDFInduce m_Induce;
/*     */   protected ClusBeamInduce m_BeamInduce;
/*     */   protected boolean m_BeamChanged;
/*     */   protected int m_CurrentModel;
/*     */   protected int m_MaxTreeSize;
/*     */   protected double m_TotalWeight;
/*     */   protected ArrayList m_BeamStats;
/*     */   protected ClusBeam m_Beam;
/*     */   protected boolean m_BeamPostPruning;
/*     */   protected ClusBeamHeuristic m_Heuristic;
/*     */   protected ClusHeuristic m_AttrHeuristic;
/*     */   protected boolean m_Verbose;
/*     */   protected ClusBeamModelDistance m_BeamModelDistance;
/*     */   protected ClusBeamSyntacticConstraint m_BeamSyntConstr;
/*     */   
/*     */   public ClusBeamSearch(Clus clus) throws ClusException, IOException {
/*  77 */     super(clus);
/*     */   }
/*     */   
/*     */   public void reset() {
/*  81 */     this.m_Beam = null;
/*  82 */     this.m_BeamChanged = false;
/*  83 */     this.m_CurrentModel = -1;
/*  84 */     this.m_TotalWeight = 0.0D;
/*  85 */     this.m_BeamStats = new ArrayList();
/*     */   }
/*     */   
/*     */   public ClusInductionAlgorithm createInduce(ClusSchema schema, Settings sett, CMDLineArgs cargs) throws ClusException, IOException {
/*  89 */     schema.addIndices(0);
/*  90 */     this.m_BeamInduce = new ClusBeamInduce(schema, sett, this);
/*  91 */     this.m_BeamInduce.getStatManager().setBeamSearch(true);
/*  92 */     return this.m_BeamInduce;
/*     */   }
/*     */   
/*     */   public void initializeHeuristic() {
/*  96 */     ClusStatManager smanager = this.m_BeamInduce.getStatManager();
/*  97 */     Settings sett = smanager.getSettings();
/*  98 */     this.m_MaxTreeSize = sett.getBeamTreeMaxSize();
/*  99 */     System.out.println("BeamSearch : the maximal size of the trees is " + this.m_MaxTreeSize);
/* 100 */     this.m_BeamPostPruning = sett.isBeamPostPrune();
/* 101 */     this.m_Heuristic = (ClusBeamHeuristic)smanager.getHeuristic();
/* 102 */     int attr_heur = sett.getBeamAttrHeuristic();
/* 103 */     if (attr_heur != 0) {
/* 104 */       this.m_AttrHeuristic = smanager.createHeuristic(attr_heur);
/* 105 */       this.m_Heuristic.setAttrHeuristic(this.m_AttrHeuristic);
/*     */     } 
/*     */   }
/*     */   
/*     */   public final boolean isBeamPostPrune() {
/* 110 */     return this.m_BeamPostPruning;
/*     */   }
/*     */   
/*     */   public double computeLeafAdd(ClusNode leaf) {
/* 114 */     return this.m_Heuristic.computeLeafAdd(leaf);
/*     */   }
/*     */   
/*     */   public double estimateBeamMeasure(ClusNode tree) {
/* 118 */     return this.m_Heuristic.estimateBeamMeasure(tree);
/*     */   }
/*     */   
/*     */   public void initSelector(CurrentBestTestAndHeuristic sel) {
/* 122 */     if (hasAttrHeuristic()) {
/* 123 */       sel.setHeuristic(this.m_AttrHeuristic);
/*     */     }
/*     */   }
/*     */   
/*     */   public final boolean hasAttrHeuristic() {
/* 128 */     return (this.m_AttrHeuristic != null);
/*     */   }
/*     */   
/*     */   public ClusBeam initializeBeam(ClusRun run) throws ClusException, IOException {
/* 132 */     ClusStatManager smanager = this.m_BeamInduce.getStatManager();
/* 133 */     Settings sett = smanager.getSettings();
/* 134 */     ClusBeam beam = new ClusBeam(sett.getBeamWidth(), sett.getBeamRemoveEqualHeur());
/*     */     
/* 136 */     RowData train = (RowData)run.getTrainingSet();
/* 137 */     train.addIndices();
/* 138 */     ClusStatistic stat = this.m_Induce.createTotalClusteringStat(train);
/* 139 */     stat.calcMean();
/* 140 */     this.m_Induce.initSelectorAndSplit(stat);
/* 141 */     initSelector(this.m_Induce.getBestTest());
/* 142 */     System.out.println("Root statistic: " + stat);
/*     */     
/* 144 */     ClusNode root = null;
/* 145 */     String constr_file = sett.getConstraintFile();
/* 146 */     if (StringUtils.unCaseCompare(constr_file, "None")) {
/* 147 */       root = new ClusNode();
/* 148 */       root.setClusteringStat(stat);
/*     */     } else {
/* 150 */       ClusConstraintFile file = ClusConstraintFile.getInstance();
/* 151 */       root = file.getClone(constr_file);
/* 152 */       root.setClusteringStat(stat);
/* 153 */       this.m_Induce.fillInStatsAndTests(root, train);
/*     */     } 
/*     */     
/* 156 */     root.initTargetStat(getStatManager(), train);
/* 157 */     root.getTargetStat().calcMean();
/* 158 */     root.getClusteringStat().setBeam(beam);
/* 159 */     root.getTargetStat().setBeam(beam);
/*     */     
/* 161 */     double weight = root.getClusteringStat().getTotalWeight();
/* 162 */     setTotalWeight(weight);
/*     */     
/* 164 */     double value = estimateBeamMeasure(root);
/*     */     
/* 166 */     beam.addModel(new ClusBeamModel(value, (ClusModel)root));
/*     */     
/* 168 */     this.m_BeamModelDistance = new ClusBeamModelDistance(run, beam);
/*     */     
/* 170 */     if (Settings.BEAM_SYNT_DIST_CONSTR) this.m_BeamSyntConstr = new ClusBeamSyntacticConstraint(run); 
/* 171 */     return beam;
/*     */   }
/*     */   
/*     */   public void refineGivenLeaf(ClusNode leaf, ClusBeamModel root, ClusBeam beam, ClusAttrType[] attrs) {
/* 175 */     MyArray arr = (MyArray)leaf.getVisitor();
/* 176 */     RowData data = new RowData(arr.getObjects(), arr.size());
/* 177 */     if (this.m_Induce.initSelectorAndStopCrit(leaf, data)) {
/*     */       return;
/*     */     }
/*     */ 
/*     */     
/* 182 */     CurrentBestTestAndHeuristic sel = this.m_Induce.getBestTest();
/* 183 */     FindBestTest find = this.m_Induce.getFindBestTest();
/* 184 */     double base_value = root.getValue();
/* 185 */     double leaf_add = this.m_Heuristic.computeLeafAdd(leaf);
/* 186 */     this.m_Heuristic.setTreeOffset(base_value - leaf_add);
/*     */     
/* 188 */     for (int i = 0; i < attrs.length; i++) {
/*     */       
/* 190 */       sel.resetBestTest();
/* 191 */       double beam_min_value = beam.getMinValue();
/* 192 */       sel.setBestHeur(beam_min_value);
/*     */       
/* 194 */       ClusAttrType at = attrs[i];
/*     */       
/* 196 */       if (at instanceof NominalAttrType) { find.findNominal((NominalAttrType)at, data); }
/* 197 */       else { find.findNumeric((NumericAttrType)at, data); }
/*     */       
/* 199 */       if (sel.hasBestTest()) {
/* 200 */         ClusNode ref_leaf = (ClusNode)leaf.cloneNode();
/* 201 */         ref_leaf.testToNode(sel);
/*     */         
/* 203 */         if (Settings.VERBOSE > 0) System.out.println("Test: " + ref_leaf.getTestString() + " -> " + sel.m_BestHeur + " (" + ref_leaf.getTest().getPosFreq() + ")");
/*     */         
/* 205 */         ClusStatManager mgr = this.m_Induce.getStatManager();
/* 206 */         int arity = ref_leaf.updateArity();
/* 207 */         NodeTest test = ref_leaf.getTest();
/* 208 */         for (int j = 0; j < arity; j++) {
/* 209 */           ClusNode child = new ClusNode();
/* 210 */           ref_leaf.setChild((Node)child, j);
/* 211 */           RowData subset = data.applyWeighted(test, j);
/* 212 */           child.initClusteringStat(mgr, subset);
/*     */           
/* 214 */           child.initTargetStat(mgr, subset);
/* 215 */           child.getTargetStat().calcMean();
/*     */         } 
/*     */         
/* 218 */         ClusNode root_model = (ClusNode)root.getModel();
/* 219 */         ClusNode ref_tree = (ClusNode)root_model.cloneTree((MyNode)leaf, (MyNode)ref_leaf);
/* 220 */         double new_heur = sanityCheck(sel.m_BestHeur, ref_tree);
/*     */ 
/*     */         
/* 223 */         ClusBeamModel new_model = new ClusBeamModel(new_heur, (ClusModel)ref_tree);
/* 224 */         new_model.setParentModelIndex(getCurrentModel());
/*     */         
/* 226 */         if (Settings.BEAM_SIMILARITY != 0.0D && !Settings.BEAM_SYNT_DIST_CONSTR) {
/* 227 */           new_model.setModelPredictions(this.m_BeamModelDistance.getPredictions(new_model.getModel()));
/* 228 */           if (!beam.modelAlreadyIn(new_model))
/*     */           {
/* 230 */             this.m_BeamModelDistance.addDistToCandOpt(beam, new_model);
/* 231 */             if (beam.removeMinUpdatedOpt(new_model, this.m_BeamModelDistance) == 1) setBeamChanged(true);
/*     */             
/*     */ 
/*     */           
/*     */           }
/*     */ 
/*     */         
/*     */         }
/*     */         else {
/*     */           
/* 241 */           if (Settings.BEAM_SYNT_DIST_CONSTR) {
/* 242 */             System.out.println("OLD HEUR = " + new_heur);
/* 243 */             new_model.setModelPredictions(this.m_BeamModelDistance.getPredictions(new_model.getModel()));
/* 244 */             new_heur -= Settings.BEAM_SIMILARITY * this.m_BeamModelDistance.getDistToConstraint(new_model, this.m_BeamSyntConstr);
/* 245 */             System.out.println("UPDT HEUR = " + new_heur);
/*     */           } 
/*     */           
/* 248 */           if (new_heur > beam_min_value) {
/*     */             
/* 250 */             beam.addModel(new_model);
/* 251 */             setBeamChanged(true);
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void refineEachLeaf(ClusNode tree, ClusBeamModel root, ClusBeam beam, ClusAttrType[] attrs) {
/* 261 */     int nb_c = tree.getNbChildren();
/* 262 */     if (nb_c == 0) {
/* 263 */       refineGivenLeaf(tree, root, beam, attrs);
/*     */     } else {
/* 265 */       for (int i = 0; i < nb_c; i++) {
/* 266 */         ClusNode child = (ClusNode)tree.getChild(i);
/* 267 */         refineEachLeaf(child, root, beam, attrs);
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public void refineModel(ClusBeamModel model, ClusBeam beam, ClusRun run) throws IOException {
/* 273 */     ClusNode tree = (ClusNode)model.getModel();
/*     */     
/* 275 */     if (this.m_MaxTreeSize >= 0) {
/* 276 */       int size = tree.getNbNodes();
/* 277 */       if (size + 2 > this.m_MaxTreeSize) {
/*     */         return;
/*     */       }
/*     */     } 
/*     */     
/* 282 */     RowData train = (RowData)run.getTrainingSet();
/* 283 */     this.m_Coll.initialize((ClusModel)tree, null);
/* 284 */     int nb_rows = train.getNbRows();
/* 285 */     for (int i = 0; i < nb_rows; i++) {
/* 286 */       DataTuple tuple = train.getTuple(i);
/* 287 */       tree.applyModelProcessor(tuple, (ClusModelProcessor)this.m_Coll);
/*     */     } 
/*     */     
/* 290 */     ClusAttrType[] attrs = train.getSchema().getDescriptiveAttributes();
/* 291 */     refineEachLeaf(tree, model, beam, attrs);
/*     */     
/* 293 */     tree.clearVisitors();
/*     */   }
/*     */   
/*     */   public void refineBeam(ClusBeam beam, ClusRun run) throws IOException {
/* 297 */     setBeamChanged(false);
/* 298 */     ArrayList<ClusBeamModel> models = beam.toArray();
/* 299 */     for (int i = 0; i < models.size(); i++) {
/*     */       
/* 301 */       setCurrentModel(i);
/* 302 */       ClusBeamModel model = models.get(i);
/* 303 */       if (!model.isRefined() && !model.isFinished()) {
/* 304 */         if (this.m_Verbose) System.out.print("[*]"); 
/* 305 */         refineModel(model, beam, run);
/* 306 */         model.setRefined(true);
/* 307 */         model.setParentModelIndex(-1);
/*     */       } 
/* 309 */       if (this.m_Verbose) {
/* 310 */         if (model.isRefined()) {
/* 311 */           System.out.print("[R]");
/*     */         }
/* 313 */         if (model.isFinished()) {
/* 314 */           System.out.print("[F]");
/*     */         }
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public Settings getSettings() {
/* 321 */     return this.m_Clus.getSettings();
/*     */   }
/*     */   
/*     */   public void estimateBeamStats(ClusBeam beam) {
/* 325 */     SingleStat stat_heuristic = new SingleStat();
/* 326 */     SingleStat stat_size = new SingleStat();
/* 327 */     SingleStat stat_same_heur = new SingleStat();
/* 328 */     ArrayList<ClusBeamModel> lst = beam.toArray();
/* 329 */     HashSet<NodeTest> tops = new HashSet();
/* 330 */     for (int i = 0; i < lst.size(); i++) {
/* 331 */       ClusBeamModel model = lst.get(i);
/* 332 */       stat_heuristic.addFloat(model.getValue());
/* 333 */       stat_size.addFloat(model.getModel().getModelSize());
/* 334 */       NodeTest top = ((ClusNode)model.getModel()).getTest();
/* 335 */       if (top != null && 
/* 336 */         !tops.contains(top)) {
/* 337 */         tops.add(top);
/*     */       }
/*     */     } 
/*     */     
/* 341 */     Iterator<ClusBeamTreeElem> iter = beam.getIterator();
/* 342 */     while (iter.hasNext()) {
/* 343 */       ClusBeamTreeElem elem = iter.next();
/* 344 */       stat_same_heur.addFloat(elem.getCount());
/*     */     } 
/* 346 */     ArrayList<SingleStat> stat = new ArrayList();
/* 347 */     stat.add(stat_heuristic);
/* 348 */     stat.add(stat_same_heur);
/* 349 */     stat.add(stat_size);
/* 350 */     stat.add(new Integer(tops.size()));
/* 351 */     this.m_BeamStats.add(stat);
/*     */   }
/*     */   
/*     */   public String getLevelStat(int i) {
/* 355 */     ArrayList stat = this.m_BeamStats.get(i);
/* 356 */     StringBuffer buf = new StringBuffer();
/* 357 */     buf.append("Level: " + i);
/* 358 */     for (int j = 0; j < stat.size(); j++) {
/* 359 */       Object elem = stat.get(j);
/* 360 */       buf.append(", ");
/* 361 */       if (elem instanceof SingleStat) {
/* 362 */         SingleStat st = (SingleStat)elem;
/* 363 */         buf.append(st.getMean() + "," + st.getRange());
/*     */       } else {
/* 365 */         buf.append(elem.toString());
/*     */       } 
/*     */     } 
/* 368 */     return buf.toString();
/*     */   }
/*     */   
/*     */   public void printBeamStats(int level) {
/* 372 */     System.out.println(getLevelStat(level));
/*     */   }
/*     */   
/*     */   public void saveBeamStats() {
/* 376 */     MyFile stats = new MyFile(getSettings().getAppName() + ".bmstats");
/* 377 */     for (int i = 0; i < this.m_BeamStats.size(); i++) {
/* 378 */       stats.log(getLevelStat(i));
/*     */     }
/* 380 */     stats.close();
/*     */   }
/*     */   
/*     */   public void writeModel(ClusModelCollectionIO strm) throws IOException {
/* 384 */     saveBeamStats();
/* 385 */     ArrayList<ClusBeamModel> beam = getBeam().toArray();
/* 386 */     for (int i = 0; i < beam.size(); i++) {
/* 387 */       ClusBeamModel m = beam.get(i);
/* 388 */       ClusNode node = (ClusNode)m.getModel();
/* 389 */       node.updateTree();
/* 390 */       node.clearVisitors();
/*     */     } 
/* 392 */     int pos = 1;
/* 393 */     for (int j = beam.size() - 1; j >= 0; j--) {
/* 394 */       ClusBeamModel m = beam.get(j);
/* 395 */       ClusModelInfo info = new ClusModelInfo("B" + pos + ": " + m.getValue());
/* 396 */       info.setScore(m.getValue());
/* 397 */       info.setModel(m.getModel());
/* 398 */       strm.addModel(info);
/* 399 */       pos++;
/*     */     } 
/*     */   }
/*     */   
/*     */   public void setVerbose(boolean verb) {
/* 404 */     this.m_Verbose = verb;
/*     */   }
/*     */   
/*     */   public ClusNode beamSearch(ClusRun run) throws ClusException, IOException {
/* 408 */     reset();
/* 409 */     System.out.println("Starting beam search");
/* 410 */     this.m_Induce = new ConstraintDFInduce(this.m_BeamInduce);
/* 411 */     ClusBeam beam = initializeBeam(run);
/*     */ 
/*     */     
/* 414 */     int i = 0;
/*     */     while (true) {
/* 416 */       System.out.println("Step: " + i);
/* 417 */       refineBeam(beam, run);
/* 418 */       if (isBeamChanged()) {
/*     */         
/* 420 */         estimateBeamStats(beam);
/*     */ 
/*     */ 
/*     */         
/* 424 */         i++; continue;
/*     */       }  break;
/* 426 */     }  setBeam(beam);
/* 427 */     double best = beam.getBestModel().getValue();
/* 428 */     double worst = beam.getWorstModel().getValue();
/* 429 */     System.out.println("Worst = " + worst + " Best = " + best);
/* 430 */     printBeamStats(i - 1);
/* 431 */     ClusNode result = (ClusNode)beam.getBestAndSmallestModel().getModel();
/*     */     
/* 433 */     return result;
/*     */   }
/*     */   
/*     */   public void setBeam(ClusBeam beam) {
/* 437 */     this.m_Beam = beam;
/*     */   }
/*     */   
/*     */   public ClusBeam getBeam() {
/* 441 */     return this.m_Beam;
/*     */   }
/*     */   
/*     */   public boolean isBeamChanged() {
/* 445 */     return this.m_BeamChanged;
/*     */   }
/*     */   
/*     */   public void setBeamChanged(boolean change) {
/* 449 */     this.m_BeamChanged = change;
/*     */   }
/*     */   
/*     */   public int getCurrentModel() {
/* 453 */     return this.m_CurrentModel;
/*     */   }
/*     */   
/*     */   public void setCurrentModel(int model) {
/* 457 */     this.m_CurrentModel = model;
/*     */   }
/*     */   
/*     */   public void setTotalWeight(double weight) {
/* 461 */     this.m_TotalWeight = weight;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double sanityCheck(double value, ClusNode tree) {
/* 469 */     double expected = estimateBeamMeasure(tree);
/* 470 */     if (Math.abs(value - expected) > 1.0E-6D) {
/* 471 */       System.out.println("Bug in heurisitc: " + value + " <> " + expected);
/* 472 */       PrintWriter wrt = new PrintWriter(System.out);
/* 473 */       tree.printModel(wrt);
/* 474 */       wrt.close();
/* 475 */       System.out.flush();
/* 476 */       System.exit(1);
/*     */     } 
/* 478 */     return expected;
/*     */   }
/*     */   
/*     */   public void tryLogBeam(MyFile log, ClusBeam beam, String txt) {
/* 482 */     if (log.isEnabled()) {
/* 483 */       log.log(txt);
/* 484 */       log.log("*********************************************");
/* 485 */       beam.print(log.getWriter(), this.m_Clus.getSettings().getBeamBestN());
/* 486 */       log.log();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void pruneAll(ClusRun cr) throws ClusException, IOException {}
/*     */   
/*     */   public ClusModel pruneSingle(ClusModel model, ClusRun cr) throws ClusException, IOException {
/* 494 */     return model;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\ext\beamsearch\ClusBeamSearch.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */