/*     */ package clus.ext.exhaustivesearch;
/*     */ 
/*     */ import clus.Clus;
/*     */ import clus.algo.split.CurrentBestTestAndHeuristic;
/*     */ import clus.algo.split.FindBestTest;
/*     */ import clus.algo.tdidt.ClusNode;
/*     */ import clus.algo.tdidt.ConstraintDFInduce;
/*     */ import clus.data.rows.DataTuple;
/*     */ import clus.data.rows.RowData;
/*     */ import clus.data.type.ClusAttrType;
/*     */ import clus.data.type.NominalAttrType;
/*     */ import clus.data.type.NumericAttrType;
/*     */ import clus.ext.beamsearch.ClusBeam;
/*     */ import clus.ext.beamsearch.ClusBeamModel;
/*     */ import clus.ext.constraint.ClusConstraintFile;
/*     */ import clus.main.ClusRun;
/*     */ import clus.main.ClusStatManager;
/*     */ import clus.main.Settings;
/*     */ import clus.model.ClusModel;
/*     */ import clus.model.processor.ClusModelProcessor;
/*     */ import clus.model.test.NodeTest;
/*     */ import clus.statistic.ClusStatistic;
/*     */ import clus.util.ClusException;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import jeans.math.MDouble;
/*     */ import jeans.tree.MyNode;
/*     */ import jeans.tree.Node;
/*     */ import jeans.util.MyArray;
/*     */ import jeans.util.StringUtils;
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
/*     */ public class ClusExhaustiveDFSearch
/*     */   extends ClusExhaustiveSearch
/*     */ {
/*     */   protected int m_cTree;
/*     */   
/*     */   public ClusExhaustiveDFSearch(Clus clus) throws ClusException, IOException {
/*  54 */     super(clus);
/*     */   }
/*     */   
/*     */   public int getCTree() {
/*  58 */     return this.m_cTree;
/*     */   }
/*     */   
/*     */   public void setCTree(int i) {
/*  62 */     this.m_cTree = i;
/*     */   }
/*     */   
/*     */   public void incCTree() {
/*  66 */     this.m_cTree++;
/*     */   }
/*     */   
/*     */   public ClusBeamModel getRootNode(ClusRun run) throws ClusException {
/*  70 */     ClusStatManager smanager = this.m_BeamInduce.getStatManager();
/*  71 */     Settings sett = smanager.getSettings();
/*  72 */     sett.setMinimalWeight(1.0D);
/*     */     
/*  74 */     RowData train = (RowData)run.getTrainingSet();
/*  75 */     ClusStatistic stat = this.m_Induce.createTotalClusteringStat(train);
/*  76 */     stat.calcMean();
/*  77 */     this.m_Induce.initSelectorAndSplit(stat);
/*  78 */     initSelector(this.m_Induce.getBestTest());
/*  79 */     System.out.println("Root statistic: " + stat);
/*  80 */     ClusNode root = null;
/*     */     
/*  82 */     String constr_file = sett.getConstraintFile();
/*  83 */     if (StringUtils.unCaseCompare(constr_file, "None")) {
/*  84 */       root = new ClusNode();
/*  85 */       root.setClusteringStat(stat);
/*     */     } else {
/*  87 */       ClusConstraintFile file = ClusConstraintFile.getInstance();
/*  88 */       root = file.getClone(constr_file);
/*  89 */       root.setClusteringStat(stat);
/*  90 */       this.m_Induce.fillInStatsAndTests(root, train);
/*     */     } 
/*     */ 
/*     */     
/*  94 */     double weight = root.getClusteringStat().getTotalWeight();
/*  95 */     setTotalWeight(weight);
/*     */     
/*  97 */     double value = estimateBeamMeasure(root);
/*     */     
/*  99 */     return new ClusBeamModel(value, (ClusModel)root);
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
/*     */   public void refineGivenLeafExhaustiveDF(ClusNode leaf, ClusBeamModel root, ClusBeam beam, ClusAttrType[] attrs, ClusRun run) throws IOException {
/* 112 */     MyArray arr = (MyArray)leaf.getVisitor();
/* 113 */     RowData data = new RowData(arr.getObjects(), arr.size());
/* 114 */     if (this.m_Induce.initSelectorAndStopCrit(leaf, data)) {
/*     */       
/* 116 */       System.out.println("stopping criterion reached in refineGivenLeafExhaustive");
/*     */       return;
/*     */     } 
/* 119 */     if (leaf.getClusteringStat().getError() == 0.0D) {
/*     */       return;
/*     */     }
/*     */ 
/*     */     
/* 124 */     CurrentBestTestAndHeuristic sel = this.m_Induce.getBestTest();
/* 125 */     FindBestTest find = this.m_Induce.getFindBestTest();
/* 126 */     double base_value = root.getValue();
/* 127 */     double leaf_add = this.m_Heuristic.computeLeafAdd(leaf);
/* 128 */     this.m_Heuristic.setTreeOffset(base_value - leaf_add);
/* 129 */     int nbNewLeaves = 0;
/* 130 */     ClusNode[] newLeaves = new ClusNode[attrs.length];
/*     */     
/* 132 */     for (int i = 0; i < attrs.length; i++) {
/*     */       
/* 134 */       sel.resetBestTest();
/*     */       
/* 136 */       ClusAttrType at = attrs[i];
/*     */ 
/*     */       
/* 139 */       if (at instanceof NominalAttrType) { find.findNominal((NominalAttrType)at, data); }
/* 140 */       else { find.findNumeric((NumericAttrType)at, data); }
/*     */       
/* 142 */       if (sel.hasBestTest()) {
/* 143 */         ClusNode ref_leaf = (ClusNode)leaf.cloneNode();
/* 144 */         ref_leaf.testToNode(sel);
/*     */         
/* 146 */         if (Settings.VERBOSE > 0) System.out.println("Test: " + ref_leaf.getTestString() + " -> " + sel.m_BestHeur + " (" + ref_leaf.getTest().getPosFreq() + ")"); 
/* 147 */         newLeaves[nbNewLeaves++] = ref_leaf;
/*     */       } 
/*     */     } 
/* 150 */     ClusStatManager mgr = this.m_Induce.getStatManager();
/* 151 */     for (int j = 0; j < nbNewLeaves; j++) {
/* 152 */       ClusNode ref_leaf = newLeaves[j];
/* 153 */       int arity = ref_leaf.updateArity();
/* 154 */       NodeTest test = ref_leaf.getTest();
/* 155 */       for (int k = 0; k < arity; k++) {
/* 156 */         ClusNode child = new ClusNode();
/* 157 */         ref_leaf.setChild((Node)child, k);
/* 158 */         RowData subset = data.applyWeighted(test, k);
/* 159 */         child.initClusteringStat(mgr, subset);
/*     */         
/* 161 */         child.initTargetStat(mgr, subset);
/* 162 */         child.getTargetStat().calcMean();
/*     */       } 
/*     */       
/* 165 */       ClusNode root_model = (ClusNode)root.getModel();
/* 166 */       ClusNode ref_tree = (ClusNode)root_model.cloneTree((MyNode)leaf, (MyNode)ref_leaf);
/* 167 */       double new_heur = estimateBeamMeasure(ref_tree);
/*     */       
/* 169 */       ClusBeamModel new_model = new ClusBeamModel(new_heur, (ClusModel)ref_tree);
/* 170 */       new_model.setParentModelIndex(getCurrentModel());
/* 171 */       refineModel(new_model, beam, run);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void refineEachLeafDF(ClusNode tree, ClusBeamModel root, ClusBeam beam, ClusAttrType[] attrs, ClusRun run) throws IOException {
/* 180 */     int nb_c = tree.getNbChildren();
/*     */ 
/*     */     
/* 183 */     if (nb_c == 0) {
/* 184 */       refineGivenLeafExhaustiveDF(tree, root, beam, attrs, run);
/*     */     } else {
/* 186 */       for (int i = nb_c - 1; i >= 0; i--) {
/* 187 */         ClusNode child = (ClusNode)tree.getChild(i);
/* 188 */         refineEachLeafDF(child, root, beam, attrs, run);
/* 189 */         if (child.getNbChildren() > 0) {
/*     */           break;
/*     */         }
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public static ArrayList getErrorPerleaf(ClusNode tree, MDouble sumLeftLeaves) {
/* 197 */     ArrayList<Comparable> listRightLeaves = new ArrayList();
/*     */ 
/*     */     
/* 200 */     getErrorPerleaf(tree, sumLeftLeaves, listRightLeaves);
/* 201 */     Collections.sort(listRightLeaves);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 209 */     return listRightLeaves;
/*     */   }
/*     */   
/*     */   public static void getErrorPerleaf(ClusNode tree, MDouble sumLeftLeaves, ArrayList<Double> listRightLeaves) {
/* 213 */     int nb_c = tree.getNbChildren();
/* 214 */     if (nb_c == 0) {
/* 215 */       ClusStatistic total = tree.getClusteringStat();
/* 216 */       listRightLeaves.add(new Double(total.getError()));
/*     */     } else {
/* 218 */       boolean foundRightMost = false;
/* 219 */       for (int i = nb_c - 1; i >= 0; i--) {
/* 220 */         ClusNode child = (ClusNode)tree.getChild(i);
/* 221 */         if (foundRightMost) {
/* 222 */           getSumLeftLeavesError(child, sumLeftLeaves);
/*     */         } else {
/* 224 */           getErrorPerleaf(child, sumLeftLeaves, listRightLeaves);
/*     */         } 
/* 226 */         if (child.getNbChildren() > 0) {
/* 227 */           foundRightMost = true;
/*     */         }
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public static void getSumLeftLeavesError(ClusNode tree, MDouble sumLeftLeaves) {
/* 234 */     int nb_c = tree.getNbChildren();
/* 235 */     if (nb_c == 0) {
/* 236 */       ClusStatistic total = tree.getClusteringStat();
/* 237 */       sumLeftLeaves.addDouble(total.getError());
/*     */     } else {
/* 239 */       for (int i = 0; i < nb_c; i++) {
/* 240 */         ClusNode child = (ClusNode)tree.getChild(i);
/* 241 */         getSumLeftLeavesError(child, sumLeftLeaves);
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public void refineModel(ClusBeamModel model, ClusBeam beam, ClusRun run) throws IOException {
/* 247 */     ClusNode tree = (ClusNode)model.getModel();
/* 248 */     int size = tree.getNbNodes();
/*     */ 
/*     */     
/* 251 */     incCTree();
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 256 */     boolean tree_ok = true;
/* 257 */     if (this.m_MaxTreeSize > 0 && size > this.m_MaxTreeSize) tree_ok = false; 
/* 258 */     if (size > 1 && this.m_MaxError > 0.0D && ClusNode.estimateErrorRecursive(tree) / this.m_TotalWeight > this.m_MaxError) tree_ok = false; 
/* 259 */     if (size == 1 && this.m_MaxError > 0.0D && tree.m_ClusteringStat.getErrorRel() > this.m_MaxError) tree_ok = false; 
/* 260 */     if (tree_ok)
/*     */     {
/* 262 */       beam.addModel(model);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 267 */     if (this.m_MaxTreeSize >= 0 && 
/* 268 */       size + 2 > this.m_MaxTreeSize) {
/*     */       return;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 275 */     if (this.m_MaxError > 0.0D && this.m_MaxTreeSize > 0) {
/* 276 */       int NbpossibleSplit = (this.m_MaxTreeSize - tree.getModelSize()) / 2;
/*     */ 
/*     */       
/* 279 */       MDouble sumLeftLeaves = new MDouble();
/* 280 */       ArrayList<Double> error = getErrorPerleaf(tree, sumLeftLeaves);
/*     */       
/* 282 */       if (error.size() > NbpossibleSplit) {
/* 283 */         double minerror = sumLeftLeaves.getDouble();
/* 284 */         for (int j = 0; j < error.size() - NbpossibleSplit; j++)
/*     */         {
/* 286 */           minerror += ((Double)error.get(j)).doubleValue();
/*     */         }
/* 288 */         double minerrorrel = minerror / this.m_TotalWeight;
/*     */         
/* 290 */         if (minerrorrel > this.m_MaxError) {
/*     */           return;
/*     */         }
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 300 */     RowData train = (RowData)run.getTrainingSet();
/* 301 */     this.m_Coll.initialize((ClusModel)tree, null);
/* 302 */     int nb_rows = train.getNbRows();
/* 303 */     for (int i = 0; i < nb_rows; i++) {
/* 304 */       DataTuple tuple = train.getTuple(i);
/* 305 */       tree.applyModelProcessor(tuple, (ClusModelProcessor)this.m_Coll);
/*     */     } 
/*     */     
/* 308 */     ClusAttrType[] attrs = train.getSchema().getDescriptiveAttributes();
/* 309 */     refineEachLeafDF(tree, model, beam, attrs, run);
/*     */     
/* 311 */     tree.clearVisitors();
/*     */   }
/*     */ 
/*     */   
/*     */   public ClusBeam exhaustiveSearch(ClusRun run) throws ClusException, IOException {
/* 316 */     reset();
/* 317 */     System.out.println("Starting exhaustive depth first search :");
/* 318 */     this.m_Induce = new ConstraintDFInduce(this.m_BeamInduce);
/* 319 */     ClusBeam beam = new ClusBeam(-1, false);
/* 320 */     ClusBeamModel current = getRootNode(run);
/* 321 */     refineModel(current, beam, run);
/* 322 */     setBeam(beam);
/* 323 */     ArrayList arraybeamresult = beam.toArray();
/* 324 */     System.out.println("The number of resulting model is " + arraybeamresult.size());
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 331 */     System.out.println("The number of tree evaluated during search is " + getCTree());
/* 332 */     return beam;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\ext\exhaustivesearch\ClusExhaustiveDFSearch.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */